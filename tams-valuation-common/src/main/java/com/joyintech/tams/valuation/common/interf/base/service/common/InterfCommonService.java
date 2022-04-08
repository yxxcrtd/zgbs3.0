/*
 * 文件名：ValuationCommonInterfService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年7月2日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.common;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import com.google.common.collect.Maps;

import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.systemtools.service.SystemToolService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.InvAimDto;
import com.joyintech.tams.valuation.common.batch.finproduct.service.FinProductCommonBatchService;
import com.joyintech.tams.valuation.common.batch.tradecommon.dto.ValTrdProductDealInfoDto;
import com.joyintech.tams.valuation.common.batch.tradecommon.service.ValTradeCommonService;
import com.joyintech.tams.valuation.common.constants.ValDictCode;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValAcctLayeringInfoBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCommonBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductAcctBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.constant.DictCodeInterfList;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.constant.TempConstant;
import com.joyintech.tams.valuation.common.interf.base.dao.InterfCommonMapper;
import com.joyintech.tams.valuation.common.interf.base.dao.ValInterfCommonMapper;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.dealcommon.DealCommonService;
import com.joyintech.tams.valuation.common.interf.base.util.InterfHelper;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValDlTarget;
import com.joyintech.tams.valuation.common.interf.val.dao.BokTradeBuildMapper;
import com.joyintech.tams.valuation.common.interf.val.service.ValSysParaService;
import com.joyintech.tams.valuation.common.service.BookAccountCommonService;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 接口共通
 * 
 * @author weipeng
 * @version 1.0 2018年7月2日
 * @see InterfCommonService
 * @since 1.0
 */
@Service
public class InterfCommonService {

    /**
     * 账套信息Mapper
     */
    @Autowired
    private BokBooksetMapper valBokBooksetMapper;
    
    /**
     * 账套日期Mapper
     */
    @Autowired
    private BokBooksetDateMapper valBokBooksetDateMapper;

    /**
     * 接口共通Mapper
     */
    @Autowired
    private ValInterfCommonMapper valInterfCommonMapper;

    /**
     * 账套清算账户Mapper
     */
    @Autowired
    private BokBooksetCapacctInfoMapper valBooksetCapacctInfoMapper;

    @Autowired
    private InterfCommonMapper mapper;

    @Autowired
    private FinProductCommonBatchService finProductCommonBatchService;

    @Autowired
    private ValAcctLayeringInfoBatchMapper valAcctLayeringInfoBatchMapper;

    @Autowired
    private BookAccountCommonService bookAccountCommonService;
    
    @Autowired
    private BokValSchemeMapper bokValSchemeMapper;
    /**
     * 通道共同
     */
    @Autowired
    private ChannelCommonService channelCommonService;
    
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(InterfCommonService.class);

    @Autowired
    private ValCommonBatchMapper valCommonBatchMapper;
    
    @Autowired
    private ValSysParaService valSysParaService;
    
    @Autowired
    private BokTradeBuildMapper bokTradeBuildMapper;

    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;

    @Autowired
    private DealCommonService dealCommonService;
    
    @Autowired
    private ValFinProductAcctBatchMapper valFinProductAcctBatchMapper;
    
    
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    @Autowired
    private FinProductAddMapper finProductAddMapper;
    /**
     * 系统工具类
     */
    @Autowired
    private SystemToolService systemToolService;
    /**
     * 交易信息查询
     */
    @Autowired
    ValTradeCommonService valTradeCommonService;
    /**
     * 账套信息初始化
     * 
     * @param dto 标的类金融产品信息 modify by wujie on 20181013 remark:增加参数tmp_booksetId
     * modify by wujie on 20181220 remark:增加参数otherParams,包括原tmp_booksetId字段设置
     */
    @SuppressWarnings("unchecked")
    public BooksetInitResult booksetInit(FinProduct dto, ValFinProductExt dtoAdd,
                                         OtherParamsDto otherParams) {
        BooksetInitResult result = new BooksetInitResult();
        String[] queryTypes = new String[] {InterfCommonConstant.账套信息查询_账套信息,
            InterfCommonConstant.账套信息查询_账套日期, InterfCommonConstant.账套信息查询_账套配置};
        Map<String, Object> booksetInfoMap = getBooksetInfoById(otherParams.getTmp_booksetId(), queryTypes);
        // 账套信息Dto
        BokBookset valBokBookset = (BokBookset)booksetInfoMap.get(
            InterfCommonConstant.账套信息查询_账套信息);
        // 账套配置
        //判断账套是否已经有历史账套参数配置，若有，则不取账套模板中的，取自账套本身　modify by hxy 20210827
        List<BokBooksetExt> listValBokBooksetExt = mapper.selectBooksetExtByBooksetId(dto.getFinprodId());
        boolean ishaveBooksetExt=true;
        if(StringUtil.isEmptyList(listValBokBooksetExt)){
            listValBokBooksetExt= (List<BokBooksetExt>)booksetInfoMap.get(
                    InterfCommonConstant.账套信息查询_账套配置);
            ishaveBooksetExt=false;
        }
        /*List<BokBooksetExt> listValBokBooksetExt = (List<BokBooksetExt>)booksetInfoMap.get(
            InterfCommonConstant.账套信息查询_账套配置);*/
        
        // 2.2.1 账套表(BOK_BOOKSET)
        BokBookset resValBokBookset = new BokBookset();
        // 变量.模板账套信息.模板账套代码 = 变量.模板账套信息.账套代码
        resValBokBookset.setBooksetIdTmpl(valBokBookset.getBooksetId());
        // 变量.模板账套信息.账套代码 = prodinfo.金融产品代码
        resValBokBookset.setBooksetId(dto.getFinprodId());
        // 变量.模板账套信息.账套名称 = prodinfo.金融产品代码+"账套"
        String booksetName = "";
        if (dto.getFinprodAbbr() == null || "".equals(dto.getFinprodAbbr())) {
            booksetName = dto.getFinprodId() + "账套";
        }
        else {
            booksetName = dto.getFinprodAbbr();
        }
        resValBokBookset.setBooksetName(booksetName);
        resValBokBookset.setBooksetFullname(dto.getFinprodName());

        // 变量.模板账套信息.账套类别 = 固定值：02 理财账套
        resValBokBookset.setBooksetType(DictCodeList.账套类别_理财账套.getValue());
        // 变量.模板账套信息.核算方式 = prodinfo.收益类型
        resValBokBookset.setAcctMode(dto.getProfitType());
        // 变量.模板账套信息.核算主体代码 = prodinfo.金融产品代码
        resValBokBookset.setAcctSubjectId(dto.getFinprodId());
        // 变量.模板账套信息.核算主体类别 = 固定值：F16 理财产品
        resValBokBookset.setAcctSubjectType(dto.getFinprodType());
        // 变量.模板账套信息.本位币 = prodinfo.币种
        resValBokBookset.setStandardCcy(dto.getCcy());
        // 会计准则
        resValBokBookset.setAcctStandard(valBokBookset.getAcctStandard());
        // 变量.模板账套信息.账套开始日 = prodinfo.起息日
        resValBokBookset.setStartDate(dto.getVdate());
        
        /**modify by wujie on 20190114 start
        // 变量.模板账套信息.账套结束日 = prodinfo.到期日
        resValBokBookset.setEndDate(dto.getMdate());
        **/
        // 变量.模板账套信息.账套结束日 = prodinfo.实际到期日
        resValBokBookset.setEndDate(dto.getActmdate() != null ? dto.getActmdate() : dto.getMdate());
        /**modify by wujie on 20190114 end**/
        
        
        // 变量.模板账套信息.特殊核算模型 = 账套信息Dto.特殊核算模型
        resValBokBookset.setBokSpecMode(valBokBookset.getBokSpecMode());// add by dongjun on
        // 变量.模板账套信息 = prodinfo.所属机构   add by tanxuan on 20210310 多级法人权限控制改造
        resValBokBookset.setBelongInstitutionCode(dto.getBelongInstitutionCode());
        result.setValBokBookset(resValBokBookset);

        // 2.2.2 账套日期表(BOK_BOOKSET_DATE)
        BokBooksetDate resValBokBooksetDate = new BokBooksetDate();
        String[] types = new String[] {InterfCommonConstant.账套信息查询_账套日期};
        Map<String, Object> infoMap = getBooksetInfoById(dto.getFinprodId(), types);
        BokBooksetDate valBokBooksetDate = (BokBooksetDate)infoMap.get(
            InterfCommonConstant.账套信息查询_账套日期);
        if (valBokBooksetDate == null
            || InterfCommonConstant.接口_默认用户.equals(valBokBooksetDate.getUpdateUser())) {
            resValBokBooksetDate.setBooksetId(dto.getFinprodId());
            resValBokBooksetDate.setBooksetDate(dto.getVdate());
        }
        else {
            resValBokBooksetDate = valBokBooksetDate;
        }
        result.setValBokBooksetDate(resValBokBooksetDate);

        // 2.2.3 账套配置表(BOK_BOOKSET_EXT)

        String profitType = dto.getProfitType();
        Map<String, Boolean> isAreadyMap = new HashMap<String, Boolean>();
        isAreadyMap.put(ValDictCodeList.账套配置表参数项_活期存款是否计提.getValue(), false);
        isAreadyMap.put(ValDictCodeList.账套配置表参数项_本期利润损益结转频率.getValue(), false);
        isAreadyMap.put(ValDictCodeList.账套配置表参数项_损益平准金结转频率.getValue(), false);
        isAreadyMap.put(ValDictCodeList.账套配置表参数项_损益结转频率.getValue(), false);
        isAreadyMap.put(DictCode.收益类型.getValue(), false);
        isAreadyMap.put(ValDictCodeList.账套配置表参数项_估值表模板.getValue(), false);

        //获取系统参数：产品类型对应的结转频率值 add by hpp 2021-01-11 start
        //损益科目结转频率-收益型
        String CARRYOVER_CYCLE_SYX= CacheUtil.getSysParam("CARRYOVER_CYCLE_SYX");
        //损益科目结转频率-净值型
        String CARRYOVER_CYCLE_JZX= CacheUtil.getSysParam("CARRYOVER_CYCLE_JZX");
        //损益科目结转频率-货币型
        String CARRYOVER_CYCLE_HBX= CacheUtil.getSysParam("CARRYOVER_CYCLE_HBX");

        //损益平准金结转频率-收益型
        String PAL_CARRYOVER_CYCLE_SYX= CacheUtil.getSysParam("PAL_CARRYOVER_CYCLE_SYX");
        //损益平准金结转频率-净值型
        String PAL_CARRYOVER_CYCLE_JZX= CacheUtil.getSysParam("PAL_CARRYOVER_CYCLE_JZX");
        //损益平准金结转频率-货币型
        String PAL_CARRYOVER_CYCLE_HBX= CacheUtil.getSysParam("PAL_CARRYOVER_CYCLE_HBX");

        //本期利润损益结转频率-收益型
        String UP_CARRYOVER_CYCLE_SYX= CacheUtil.getSysParam("UP_CARRYOVER_CYCLE_SYX");
        //本期利润损益结转频率-净值型
        String UP_CARRYOVER_CYCLE_JZX= CacheUtil.getSysParam("UP_CARRYOVER_CYCLE_JZX");
        //本期利润损益结转频率-货币型
        String UP_CARRYOVER_CYCLE_HBX= CacheUtil.getSysParam("UP_CARRYOVER_CYCLE_HBX");
        //获取系统参数：产品类型对应的结转频率值 add by hpp 2021-01-11 end

        List<BokBooksetExt> resValBokBooksetExt = new ArrayList<>();
        for (BokBooksetExt valBokBooksetExt : listValBokBooksetExt) {
            BokBooksetExt ext = new BokBooksetExt();
            BeanUtils.copyProperties(valBokBooksetExt, ext);
            ext.setBooksetId(dto.getFinprodId());
            // 1.活期存款
            if (ValDictCodeList.账套配置表参数项_活期存款是否计提.getValue().equals(ext.getParamCode())) {
                isAreadyMap.put(ValDictCodeList.账套配置表参数项_活期存款是否计提.getValue(), true);
                if (dtoAdd != null && dtoAdd.getIsCalc() != null) {
                    if(!ishaveBooksetExt){
                        ext.setParamValue(dtoAdd.getIsCalc());
                    }
                }
            }
            // 2. 货币型产品
            if (DictCodeList.收益类型_货币型.getValue().equals(profitType)) {
                if (ValDictCodeList.账套配置表参数项_本期利润损益结转频率.getValue().equals(ext.getParamCode())) {
                    isAreadyMap.put(ValDictCodeList.账套配置表参数项_本期利润损益结转频率.getValue(), true);
                    if(!ishaveBooksetExt){
                        ext.setParamValue(UP_CARRYOVER_CYCLE_HBX);
                    }
                }
                if (ValDictCodeList.账套配置表参数项_损益平准金结转频率.getValue().equals(ext.getParamCode())) {
                    isAreadyMap.put(ValDictCodeList.账套配置表参数项_损益平准金结转频率.getValue(), true);
                    if(!ishaveBooksetExt){
                        ext.setParamValue(PAL_CARRYOVER_CYCLE_HBX);
                    }
                }
                if (ValDictCodeList.账套配置表参数项_损益结转频率.getValue().equals(ext.getParamCode())) {
                    isAreadyMap.put(ValDictCodeList.账套配置表参数项_损益结转频率.getValue(), true);
                    if(!ishaveBooksetExt){
                        ext.setParamValue(CARRYOVER_CYCLE_HBX);
                    }
                }
            }
            // 3. 净值型产品
            if (DictCodeList.收益类型_净值型.getValue().equals(profitType)) {
                if (ValDictCodeList.账套配置表参数项_损益结转频率.getValue().equals(ext.getParamCode())) {
                    isAreadyMap.put(ValDictCodeList.账套配置表参数项_损益结转频率.getValue(), true);
                    if(!ishaveBooksetExt){
                        ext.setParamValue(CARRYOVER_CYCLE_JZX);
                    }
                }
                if (ValDictCodeList.账套配置表参数项_本期利润损益结转频率.getValue().equals(ext.getParamCode())) {
                    isAreadyMap.put(ValDictCodeList.账套配置表参数项_本期利润损益结转频率.getValue(), true);
                    if(!ishaveBooksetExt){
                        ext.setParamValue(UP_CARRYOVER_CYCLE_JZX);
                    }
                }
                if (ValDictCodeList.账套配置表参数项_损益平准金结转频率.getValue().equals(ext.getParamCode())) {
                    isAreadyMap.put(ValDictCodeList.账套配置表参数项_损益平准金结转频率.getValue(), true);
                    if(!ishaveBooksetExt){
                        ext.setParamValue(PAL_CARRYOVER_CYCLE_JZX);
                    }
                }
            }
            // 4.收益类型
            if (DictCode.收益类型.getValue().equals(ext.getParamCode())) {
                isAreadyMap.put(DictCode.收益类型.getValue(), true);
                if(!ishaveBooksetExt){
                    ext.setParamValue(profitType);
                }
            }
            // 5. 预期收益产品 add by wujie on 20181008
            if (DictCodeList.收益类型_收益型.getValue().equals(profitType)) {
                if (ValDictCodeList.账套配置表参数项_本期利润损益结转频率.getValue().equals(ext.getParamCode())) {
                    isAreadyMap.put(ValDictCodeList.账套配置表参数项_本期利润损益结转频率.getValue(), true);
                    if(!ishaveBooksetExt){
                        ext.setParamValue(UP_CARRYOVER_CYCLE_SYX);
                    }
                }
                if (ValDictCodeList.账套配置表参数项_损益平准金结转频率.getValue().equals(ext.getParamCode())) {
                    isAreadyMap.put(ValDictCodeList.账套配置表参数项_损益平准金结转频率.getValue(), true);
                    if(!ishaveBooksetExt){
                        ext.setParamValue(PAL_CARRYOVER_CYCLE_SYX);
                    }
                }
                if (ValDictCodeList.账套配置表参数项_损益结转频率.getValue().equals(ext.getParamCode())) {
                    isAreadyMap.put(ValDictCodeList.账套配置表参数项_损益结转频率.getValue(), true);
                    if(!ishaveBooksetExt){
                        ext.setParamValue(CARRYOVER_CYCLE_SYX);
                    }
                }
            }
      
            //6.估值表模板 add by wujie on 20181220
            if (ValDictCodeList.账套配置表参数项_估值表模板.getValue().equals(ext.getParamCode())) {
                isAreadyMap.put(ValDictCodeList.账套配置表参数项_估值表模板.getValue(), true);
                if(!ishaveBooksetExt){
                    ext.setParamValue(valSysParaService.findParamValue("SCHEME_MODE"));
                }
                if (otherParams != null && otherParams.getValuationTemplate() != null) {
                    if(!ishaveBooksetExt){
                        ext.setParamValue(otherParams.getValuationTemplate());
                    }
                }
//                if (otherParams != null && otherParams.getValuationTemplate() != null) {
//                    ext.setParamValue(otherParams.getValuationTemplate());
//                }
                // 20210623 weipeng :修改取值逻辑，原逻辑有误，otherParams并不会塞值，现改为只有模板没有对应值时取系统参数 end
            }
            resValBokBooksetExt.add(ext);
        }
        for (String key : isAreadyMap.keySet()) {
            boolean isAlready = isAreadyMap.get(key);
            if (isAlready) {
                continue;
            }
            if (ValDictCodeList.账套配置表参数项_活期存款是否计提.getValue().equals(key)) {
                if (dtoAdd != null && dtoAdd.getIsCalc() != null) {
                    BokBooksetExt bokBooksetExt = new BokBooksetExt();
                    bokBooksetExt.setBooksetId(resValBokBookset.getBooksetId());
                    bokBooksetExt.setParamCode(ValDictCodeList.账套配置表参数项_活期存款是否计提.getValue());
                    bokBooksetExt.setParamValue(dtoAdd.getIsCalc());
                    bokBooksetExt.setParamName(ValDictCodeList.账套配置表参数项_活期存款是否计提.getCodeName());
                    bokBooksetExt.setRemark("数据字典：是否");
                    /**add by wujie on 20210303 start**/
                    bokBooksetExt.setParamType(DictCodeListExt3.账套参数类型_资产类.getValue());
                    /**add by wujie on 20210303 end**/
                    resValBokBooksetExt.add(bokBooksetExt);
                }
            }
            if (ValDictCodeList.账套配置表参数项_本期利润损益结转频率.getValue().equals(key)) {
                BokBooksetExt bokBooksetExt = new BokBooksetExt();
                bokBooksetExt.setBooksetId(resValBokBookset.getBooksetId());
                bokBooksetExt.setParamCode(ValDictCodeList.账套配置表参数项_本期利润损益结转频率.getValue());
                bokBooksetExt.setParamName(ValDictCodeList.账套配置表参数项_本期利润损益结转频率.getCodeName());
                bokBooksetExt.setRemark("数据字典：损益结转频率");
                /**add by wujie on 20210303 start**/
                bokBooksetExt.setParamType(DictCodeListExt3.账套参数类型_产品类.getValue());
                /**add by wujie on 20210303 end**/
                // 1. 货币型产品
                if (DictCodeList.收益类型_货币型.getValue().equals(profitType)) {
                    bokBooksetExt.setParamValue(UP_CARRYOVER_CYCLE_HBX);
                    resValBokBooksetExt.add(bokBooksetExt);
                }
                // 2. 净值型产品
                if (DictCodeList.收益类型_净值型.getValue().equals(profitType)) {
                    bokBooksetExt.setParamValue(UP_CARRYOVER_CYCLE_JZX);
                    resValBokBooksetExt.add(bokBooksetExt);
                }
                // 3. 预期收益型产品 add by wujie on 20181008
                if (DictCodeList.收益类型_收益型.getValue().equals(profitType)) {
                    bokBooksetExt.setParamValue(UP_CARRYOVER_CYCLE_SYX);
                    resValBokBooksetExt.add(bokBooksetExt);
                }
            }
            if (ValDictCodeList.账套配置表参数项_损益平准金结转频率.getValue().equals(key)) {
                BokBooksetExt bokBooksetExt = new BokBooksetExt();
                bokBooksetExt.setBooksetId(resValBokBookset.getBooksetId());
                bokBooksetExt.setParamCode(ValDictCodeList.账套配置表参数项_损益平准金结转频率.getValue());
                bokBooksetExt.setParamName(ValDictCodeList.账套配置表参数项_损益平准金结转频率.getCodeName());
                bokBooksetExt.setRemark("数据字典：损益结转频率");
                /**add by wujie on 20210303 start**/
                bokBooksetExt.setParamType(DictCodeListExt3.账套参数类型_产品类.getValue());
                /**add by wujie on 20210303 end**/
                // 1. 货币型产品
                if (DictCodeList.收益类型_货币型.getValue().equals(profitType)) {
                    bokBooksetExt.setParamValue(PAL_CARRYOVER_CYCLE_HBX);
                    resValBokBooksetExt.add(bokBooksetExt);
                }
                // 2. 净值型产品
                if (DictCodeList.收益类型_净值型.getValue().equals(profitType)) {
                    bokBooksetExt.setParamValue(PAL_CARRYOVER_CYCLE_JZX);
                    resValBokBooksetExt.add(bokBooksetExt);
                }
                // 3. 预期收益型产品 add by wujie on 20181008
                if (DictCodeList.收益类型_收益型.getValue().equals(profitType)) {
                    bokBooksetExt.setParamValue(PAL_CARRYOVER_CYCLE_SYX);
                    resValBokBooksetExt.add(bokBooksetExt);
                }
            }
            if (ValDictCodeList.账套配置表参数项_损益结转频率.getValue().equals(key)) {
                BokBooksetExt bokBooksetExt = new BokBooksetExt();
                bokBooksetExt.setBooksetId(resValBokBookset.getBooksetId());
                bokBooksetExt.setParamCode(ValDictCodeList.账套配置表参数项_损益结转频率.getValue());
                bokBooksetExt.setParamName(ValDictCodeList.账套配置表参数项_损益结转频率.getCodeName());
                bokBooksetExt.setRemark("数据字典：损益结转频率");
                /**add by wujie on 20210303 start**/
                bokBooksetExt.setParamType(DictCodeListExt3.账套参数类型_产品类.getValue());
                /**add by wujie on 20210303 end**/
                // 2. 净值型产品
                if (DictCodeList.收益类型_净值型.getValue().equals(profitType)) {
                    bokBooksetExt.setParamValue(CARRYOVER_CYCLE_JZX);
                    resValBokBooksetExt.add(bokBooksetExt);
                }
                // 1. 货币型产品
                if (DictCodeList.收益类型_货币型.getValue().equals(profitType)) {
                    bokBooksetExt.setParamValue(CARRYOVER_CYCLE_HBX);
                    resValBokBooksetExt.add(bokBooksetExt);
                }
                // 3. 预期收益型产品 add by wujie on 20181008
                if (DictCodeList.收益类型_收益型.getValue().equals(profitType)) {
                    bokBooksetExt.setParamValue(CARRYOVER_CYCLE_SYX);
                    resValBokBooksetExt.add(bokBooksetExt);
                }
            }
            if (DictCode.收益类型.getValue().equals(key)) {
                BokBooksetExt bokBooksetExt = new BokBooksetExt();
                bokBooksetExt.setBooksetId(resValBokBookset.getBooksetId());
                bokBooksetExt.setParamCode(DictCode.收益类型.getValue());
                bokBooksetExt.setParamValue(profitType);
                bokBooksetExt.setParamName("收益类型");
                bokBooksetExt.setRemark("数据字典：收益类型");
                /**add by wujie on 20210303 start**/
                bokBooksetExt.setParamType(DictCodeListExt3.账套参数类型_其他类.getValue());
                /**add by wujie on 20210303 end**/
                resValBokBooksetExt.add(bokBooksetExt);
            }
            if (ValDictCodeList.账套配置表参数项_估值表模板.getValue().equals(key)) {
                // 20210623 weipeng :修改取值逻辑，此处为模板没有对应值，直接取系统参数 start
            	String paramValue = valSysParaService.findParamValue("SCHEME_MODE");
//                if (otherParams != null && otherParams.getValuationTemplate() != null) {
//                	paramValue = otherParams.getValuationTemplate();
//                }
                // 20210623 weipeng :修改取值逻辑，此处为模板没有对应值，直接取系统参数 end
                BokBooksetExt bokBooksetExt = new BokBooksetExt();
                bokBooksetExt.setBooksetId(resValBokBookset.getBooksetId());
                bokBooksetExt.setParamCode(ValDictCodeList.账套配置表参数项_估值表模板.getValue());
                bokBooksetExt.setParamValue(paramValue);
                bokBooksetExt.setParamName(ValDictCodeList.账套配置表参数项_估值表模板.getCodeName());
                bokBooksetExt.setRemark("数据字典：估值表模板");
                
                /**add by wujie on 20210303 start**/
                bokBooksetExt.setParamType(DictCodeListExt3.账套参数类型_估值表.getValue());
                /**add by wujie on 20210303 end**/
                
                resValBokBooksetExt.add(bokBooksetExt);
            }
        }

        /**modify by dongjun on 20190428 账套初始化账套配置变更 start**/
        Map<String, BokBooksetExt> bokBooksetExtMaps = new HashMap<>();
        resValBokBooksetExt.forEach(i -> bokBooksetExtMaps.put(i.getParamCode(), i));
        Map<String, String> sysParams = otherParams.getSysParams(); 
        for(BokBooksetExt bok : resValBokBooksetExt){
        	if(StringUtil.isNotEmpty(sysParams.get(bok.getParamCode()))){
        		bok.setParamValue(sysParams.get(bok.getParamCode()));
        	}
        }
        for(String key : sysParams.keySet()){
        	if(bokBooksetExtMaps.get(key) == null && StringUtil.isNotEmpty(sysParams.get(key))){
        		BokBooksetExt bok = new BokBooksetExt();
        		bok.setBooksetId(dto.getFinprodId());
        		bok.setParamCode(key);
        		bok.setParamValue(sysParams.get(key));
        		bok.setParamName(ValDictCodeList.from(ValDictCode.账套配置表参数项, key) == null ? StringUtil.EMPTY :
        			ValDictCodeList.from(ValDictCode.账套配置表参数项, key).getCodeName());
        		 /**add by wujie on 20210303 start**/
        		bok.setParamType(DictCodeListExt3.账套参数类型_其他类.getValue());
                /**add by wujie on 20210303 end**/
        		resValBokBooksetExt.add(bok);
        	}
        }
        /**modify by dongjun on 20190428 账套初始化账套配置变更end**/
        
        result.setListValBokBooksetExt(resValBokBooksetExt);
        
        BokAcctSubjectInfo resValBokAcctSubjectInfo = new BokAcctSubjectInfo();
        // 定义 变量.核算主体名称 = prodinfo.金融产品简称
        String acctSubjectName = dto.getFinprodAbbr();
        if (dto.getFinprodAbbr() == null || "".equals(dto.getFinprodAbbr())) {
            acctSubjectName = dto.getFinprodName();
        }
        // 核算主体代码 = 输入.prodinfo.金融产品代码
        resValBokAcctSubjectInfo.setAcctSubjectId(dto.getFinprodId());
        // 核算主体类别 = 输入.prodinfo.金融产品类型
        resValBokAcctSubjectInfo.setAcctSubjectType(dto.getFinprodType());
        // 核算主体名称 = 变量.核算主体名称
        resValBokAcctSubjectInfo.setAcctSubjectName(acctSubjectName);
        result.setAcctSubjectInfo(resValBokAcctSubjectInfo);
        
        
        //核算分层信息初始化
        BokAcctLayeringInfo bokAcctLayeringInfo = new BokAcctLayeringInfo();
        // 定义分支序号
        Long branch = new Long(0);
        if (dto.getFinprodType().equals(DictCodeListExt1.金融产品类型_理财子产品.getValue())) {
            branch = Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(dto.getVdate()));
        }
        bokAcctLayeringInfo.setBooksetId(dto.getFinprodId());
        /**modify by wujie on 20190103 start
        bokAcctLayeringInfo.setLayeringId(dto.getFinprodId());
        **/
        bokAcctLayeringInfo.setLayeringId(dto.getFinprodId()+ FinalString.LAYERING_SPLIT+dto.getFinprodType());
        /**modify by wujie on 20190103 end**/
        bokAcctLayeringInfo.setLayeringName(dto.getFinprodName());
        bokAcctLayeringInfo.setfLayeringId(null);
        // MD5("1"+输入.prodinfo.金融产品代码+"2"+输入.prodinfo.金融产品类型+"3"+变量.分支序号)
        BokDetailId bokDetailId = new BokDetailId();
        // 业务明细代码=输入.金融产品代码
        bokDetailId.setBusiId(dto.getFinprodId());
        // 业务明细类型=输入.金融产品类型
        bokDetailId.setBusiDetailType(dto.getFinprodType());
        // 分支序号
        bokDetailId.setBranch(branch);
        bokAcctLayeringInfo.setBokDetailId(genMd5BokDetailId(bokDetailId));
        bokAcctLayeringInfo.setBusiId(dto.getFinprodId());
        bokAcctLayeringInfo.setBusiDetailType(dto.getFinprodType());
        bokAcctLayeringInfo.setBranch(branch);

        // 起息日 = 输入.prodinfo.起息日
        bokAcctLayeringInfo.setVdate(dto.getVdate());
        
        /**modify by wujie on 20190114 start
        // 到期日 = 输入.prodinfo.到期日
        bokAcctLayeringInfo.setMdate(dto.getMdate());
        **/
        // 到期日 = 输入.prodinfo.实际到期日
        bokAcctLayeringInfo.setMdate(dto.getActmdate() != null ? dto.getActmdate() : dto.getMdate());
        /**modify by wujie on 20190114 end**/
        
        // 交易日历 = 输入.prodinfo.交易日历
        bokAcctLayeringInfo.setCalendarId(dto.getCalendarId());
        
        //产品估值方案初始化
        BokBooksetPlan bokBooksetPlan = new BokBooksetPlan();
        bokBooksetPlan = this.initProductValuationScheme(resValBokBookset.getBooksetId());
        //add by dongxiangyang on 20190123
        //变量方案代码
        String prdValSche=otherParams.getPrdValSche();
        //变量方案代码为空时
        if(StringUtil.isNotEmpty(prdValSche)){
        	//查询该方案代码在估值系统是否存在
        	BokValScheme bokValSchemeBok = bokValSchemeMapper.selectByPrimaryKey(prdValSche);
            if(bokValSchemeBok!=null){
            	bokBooksetPlan.setSchemeId(prdValSche);
            }
        }
        if(bokBooksetPlan!=null){
            result.setBokBooksetPlan(bokBooksetPlan);
        }
        result.setAcctLayeringInfo(bokAcctLayeringInfo);
        
        return result;
    }

    /**
     * 账套清算信息初始化
     * 
     * @param dto 金融产品账户信息
     */
    public List<BokBooksetCapacctInfo> booksetCapacctInfoInit(StlFinProductAcct dto,
                                                              List<ValBooksetInfoDto> boksetList) {
        /** add by chendijun 2018/11/12 start **/
        // 初始化返回结果
        List<BokBooksetCapacctInfo> resultList = new ArrayList<>();
        for (ValBooksetInfoDto valBooksetInfoDto : boksetList) {
            // 账套清算账户信息
            BokBooksetCapacctInfo valBooksetCapacctInfo = new BokBooksetCapacctInfo();
            // 账套ID
            valBooksetCapacctInfo.setBooksetId(valBooksetInfoDto.getBooksetId());
            // 账号
            valBooksetCapacctInfo.setAcctNo(dto.getAcctNo());
            // 币种
            valBooksetCapacctInfo.setCcy(dto.getCcy());
            // 资金账户类型
            valBooksetCapacctInfo.setAcctType(dto.getAcctType());
            valBooksetCapacctInfo.setCreateTime(new Date());
            // 更新时间
            valBooksetCapacctInfo.setUpdateTime(new Date());

            resultList.add(valBooksetCapacctInfo);
        }

        return resultList;
        /** add by chendijun 2018/11/12 end **/
    }

    /**
     * 交易入账
     * 
     * @param deal 金融产品交易
     * @param prodId 产品ID
     */
    public InterfProductDealBookCommonDto dealAccounting(TrdProductDeal deal, String prodId) {

        InterfProductDealBookCommonDto interfProductDealBookCommonDto = new InterfProductDealBookCommonDto();

        // 定义 变量.账套信息Dto
        ValBooksetInfoDto valBooksetInfoDto = new ValBooksetInfoDto();
        // 定义 变量.入账信息Dto
        BokDealAccounting valDealAccounting = new BokDealAccounting();
        // 定义 变量.入账账务明细Dto
        BokDealAccountingDetail valDealAccountingDetail = new BokDealAccountingDetail();
        // 定义 变量.账务明细代码表Dto
        BokDetailId valBokDetailId = new BokDetailId();
        // 定义 变量.实际发生日
        Date sjfsDate = deal.getTradeDate();
        
        /**add by wujie on 20190525 start**/
        //交易入账日期类型为交割日
        if("02".equals(this.getDealDateType(deal))){
        	sjfsDate = deal.getSettleDate();
        }
        /**add by wujie on 20190525 end**/
        
        // 定义 变量.账务明细代码
        String bookNo = "";
        // 定义变量 投资目的
        String invAim = "";

        /* 1 查询理财产品对应的账套信息 */
        /** modify by dongjun on 20190313 remark:通道变更 start**/
        // 定义变量：核算主体代码
        String acctSubjectId = prodId;
        if(StringUtil.isNotEmpty(deal.getChlId())){
        	acctSubjectId = acctSubjectId + FinalString.LAYERING_SPLIT + deal.getChlId();
        }
        valBooksetInfoDto = getBooksetInfoByProdId(acctSubjectId);
        /** modify by dongjun on 20190313 remark:通道变更 end**/
        
        /**
         * add by wujie on 20190104 start remark:核算主体类型
         */
        
        interfProductDealBookCommonDto.setAcctSubjectType(valBooksetInfoDto.getAcctSubjectType());
        
        /**add by wujie on 20190104 end**/

        /**add by wujie on 20190625 start remark:返回账套名称**/
        interfProductDealBookCommonDto.setBooksetName(valBooksetInfoDto.getBooksetName());
        /**add by wujie on 20190625 end remark:返回账套名称**/

        /* 2 金融产品交易入账信息初始化 */
        /* 2.1入账信息表(BOK_DEAL_ACCOUNTING) */
        String invAimMethod = null;
        if (DictCodeListExt1.金融产品类型_债券.getValue().equals(deal.getFinprodType())
            && DictCodeList.业务类型_银行间债券交易.getValue().equals(deal.getBusiType())) {
            BokBooksetExt bokBooksetExt = queryBokBooksetExtbyId(DictCode.投资目的获取方式.getValue(),
                valBooksetInfoDto.getBooksetId());
            // added by wangzhiang 增加入账信息投资目的 20181208 start
            invAimMethod = bokBooksetExt != null ? bokBooksetExt.getParamValue() : DictCodeListExt1.投资目的获取方式_上游提供.getValue();
            // added by wangzhiang 增加入账信息投资目的 20181208 start
            if (bokBooksetExt != null
                && DictCodeListExt1.投资目的获取方式_上游提供.getValue().equals(bokBooksetExt.getParamValue())) {
                invAim = deal.getInvAim();
            }
            else {
                InvAimDto invAimDto = finProductCommonBatchService.searchInvAim(sjfsDate,
                    valBooksetInfoDto.getBooksetId(), DictCodeListExt1.证券品种_债券.getValue(),
                    deal.getFinprodId(), invAim);
                if (invAimDto.getSchemeCnt() > 1) {
                    // 抛出异常
                    try {
                        throw new BusinessException("变量.账套信息Dto.账套代码—输入.金融产品交易.金融产品代码：出现重复估值方案");
                    }
                    catch (Exception e) {
                        logger.error("", e);
                    }
                }
                else {
                    invAim = StringUtil.isNotEmpty(
                        invAimDto.getInvAim()) ? invAimDto.getInvAim() : deal.getInvAim();
                }
            }
        }
        else {
            invAim = deal.getInvAim();
        }
        /**add by wujie on 20201116 start remark:投资目的为999999时，设置为空**/
        if("999999".equals(invAim)){
        	invAim = null;
        }
        /**add by wujie on 20201116 end remark:投资目的为999999时，设置为空**/
        
        /** modify by dongjun on 20190313 remark:通道变更 start**/
        // 定义变量：业务明细代码
        String bokDetailId = deal.getFinprodId();
        // 定义变量：业务明细类型
        String busiDetailType = deal.getFinprodType();
        // 定义 变量.币种 = NULL
        String ccy = null;
        // IF 输入.金融产品交易.业务类型 eq 数据字典：业务类型.活期存款交易
        if (DictCodeList.业务类型_活期存款交易.getValue().equals(deal.getBusiType())) {
            // 变量.币种 = 输入.金融产品交易.币种
            ccy = deal.getCcy();
        }
        if(StringUtil.equals(DictCodeList.业务类型_产品费用交易.getValue(), deal.getBusiType())
        		|| StringUtil.equals(DictCodeList.业务类型_增值税交易.getValue(), deal.getBusiType())){
        	if(StringUtil.equals(DictCodeListExt1.金融产品类型_通道.getValue(), valBooksetInfoDto.getAcctSubjectType())){
        		bokDetailId = prodId + FinalString.LAYERING_SPLIT + deal.getChlId();
        	} else{
        		bokDetailId = prodId;
        	}
        	busiDetailType = valBooksetInfoDto.getAcctSubjectType();
        }       
        // 金融产品交易.业务类型 变更为 金融产品交易.金融产品类型
        // 变量.账务明细代码 =输入.金融产品交易.金融产品代码 +输入.金融产品交易.金融产品类型 + 输入.金融产品交易.投资目的
        // 变量.账务明细代码 =输入.金融产品交易.金融产品代码 +输入.金融产品交易.金融产品类型 + 变量.投资目的
        bookNo = getBokDetailId(bokDetailId, null, busiDetailType, invAim,
            null, ccy).getBokDetailId();
        /** modify by dongjun on 20190313 remark:通道变更 end**/
        // 账套代码 BOOKSET_ID 变量.账套信息Dto.账套代码
        valDealAccounting.setBooksetId(valBooksetInfoDto.getBooksetId());
        // 业务编号 BUSI_NO 输入.金融产品交易.交易编号
        valDealAccounting.setBusiNo(deal.getTradeId());
        // 业务表 BUSI_TABLE 02 金融产品表
        valDealAccounting.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        // 录入日 BOOKSET_DATE 系统时间
        valDealAccounting.setBooksetDate(sjfsDate);
        // 入账日 BOOK_DATE 变量.账套信息Dto.当前日期
        valDealAccounting.setBookDate(sjfsDate);
        // 实际发生日 HAPPEN_DATE 变量.实际发生日
        valDealAccounting.setHappenDate(sjfsDate);
        // 是否已入账 IS_ACCOUNTED N 否
        valDealAccounting.setIsAccounted(DictCodeList.是否_否.getValue());
        // 入账凭证编号 BOOK_ID NULL
        // 创建时间 = 系统时间
        valDealAccounting.setCreateTime(deal.getCreateTime());
        
        // added by wangzhiang 增加入账信息投资目的 20181208 start
        // 投资目的
        valDealAccounting.setInvAim(invAim);
        valDealAccounting.setInvAimMethod(invAimMethod);
        // added by wangzhiang 增加入账信息投资目的 20181208 end

        /* 2.2 入账账务明细表(BOK_DEAL_ACCOUNTING_DETAIL) */
        // 账套代码 BOOKSET_ID 变量.账套信息.账套代码
        valDealAccountingDetail.setBooksetId(valBooksetInfoDto.getBooksetId());
        // 业务编号 BUSI_NO 输入.金融产品交易.交易编号
        valDealAccountingDetail.setBusiNo(deal.getTradeId());
        // 业务表 BUSI_TABLE 02 金融产品表
        valDealAccountingDetail.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        // 业务类型 BUSI_TYPE 输入.金融产品交易.业务类型
        valDealAccountingDetail.setBusiType(deal.getBusiType());
        // 账务明细代码 BOK_DETAIL_ID 变量.账务明细代码
        valDealAccountingDetail.setBokDetailId(bookNo);
        // 创建时间 = 系统时间
        valDealAccountingDetail.setCreateTime(deal.getCreateTime());

        // 更新时间 系统时间
        valDealAccountingDetail.setUpdateTime(new Date());

        /* 2.3 账务明细代码表(VAL_BOK_DETAIL_ID) */
        // 账套代码 BOOKSET_ID 变量.账套信息.账套代码
        valBokDetailId.setBooksetId(valBooksetInfoDto.getBooksetId());
        // 账务明细代码 BOK_DETAIL_ID 变量.账务明细代码
        valBokDetailId.setBokDetailId(bookNo);
        /** modify by dongjun on 20190313 remark:通道变更 start**/
        // 业务明细代码 BUSI_ID 变量.业务明细代码
        valBokDetailId.setBusiId(bokDetailId);
        // 业务明细类型 BUSI_DETAIL_TYPE 变量.业务明细类型
        valBokDetailId.setBusiDetailType(busiDetailType);
        /** modify by dongjun on 20190313 remark:通道变更 end**/
        // added by wangzhiang 修改账务明细代码投资目的 20181208 start
        // 投资目的 INV_AIM 输入.金融产品交易.投资目的
        valBokDetailId.setInvAim(invAim);
        // added by wangzhiang 修改账务明细代码投资目的 20181208 end
        // 交易编号 TRADE_ID 输入.金融产品交易.交易编号
        valBokDetailId.setTradeId(deal.getTradeId());

        // 创建时间 = 系统时间
        valBokDetailId.setCreateTime(deal.getCreateTime());
        //调用共通设计->【详细设计-接口共通方法设计.xlsx】->四级科目号设置SHEET
        BokDetailId bokDetailIdTail = this.settingDetailSubjectNoTaiL(valBokDetailId.getBusiId(),valBokDetailId.getBusiDetailType(),0L,null,valBokDetailId.getBooksetId());
        valBokDetailId.setDetailSubjectNoTail(bokDetailIdTail.getDetailSubjectNoTail());
        //T3.账务明细代码.四级科目尾号名称 = dto.四级科目尾号名称
        valBokDetailId.setDetailSubnameTail(bokDetailIdTail.getDetailSubnameTail());
        // 赋值公共入账信息类
        interfProductDealBookCommonDto.setBokDealAccounting(valDealAccounting);
        interfProductDealBookCommonDto.setBokDealAccountingDetail(valDealAccountingDetail);
        interfProductDealBookCommonDto.setBokDetailId(valBokDetailId);

        return interfProductDealBookCommonDto;
    }

    /**
     * 账套信息查询(根据账套代码)
     * 
     * @param booksetId 账套ID
     * @param  queryTypes 查询类型
     * @return
     */
    public Map<String, Object> getBooksetInfoById(String booksetId, String... queryTypes) {// modify
                                                                                           // by
                                                                                           // dongjun
                                                                                           // on
                                                                                           // 20181029
        Map<String, Object> resultMap = new HashMap<>();
        /** modify by dongjun on 20181029 start **/
        if (queryTypes == null || queryTypes.length == 0) {
            queryTypes = new String[] {null};
        }
        for (String queryType : queryTypes) {
            // IF 输入.查询类型 is null or 输入.查询类型 eq "BOOKSET"
            if (queryType == null || queryType.equals(InterfCommonConstant.账套信息查询_账套信息)) {
                BokBookset valBokBookset = valBokBooksetMapper.selectByPrimaryKey(booksetId);
                resultMap.put(InterfCommonConstant.账套信息查询_账套信息, valBokBookset);
            }

            if (queryType == null || queryType.equals(InterfCommonConstant.账套信息查询_账套日期)) {
                BokBooksetDate valBokBooksetDate = valBokBooksetDateMapper.selectByPrimaryKey(
                    booksetId);
                resultMap.put(InterfCommonConstant.账套信息查询_账套日期, valBokBooksetDate);
            }

            if (queryType == null || queryType.equals(InterfCommonConstant.账套信息查询_账套配置)) {
                List<BokBooksetExt> list = mapper.selectBooksetExtByBooksetId(booksetId);
                resultMap.put(InterfCommonConstant.账套信息查询_账套配置, list);
            }

            if (queryType == null || queryType.equals(InterfCommonConstant.账套信息查询_账套科目信息)) {
                List<BokSubject> list = mapper.selectValBokSubjectInfoByBooksetId(booksetId);
                resultMap.put(InterfCommonConstant.账套信息查询_账套科目信息, list);
            }

            if (queryType == null || queryType.equals(InterfCommonConstant.账套信息查询_会计场景)) {
                List<BokTablelist> list = mapper.selectValBokTablelistByBooksetId(booksetId);
                resultMap.put(InterfCommonConstant.账套信息查询_会计场景, list);
            }

            if (queryType == null || queryType.equals(InterfCommonConstant.账套信息查询_会计元素)) {
                List<BokTabledict> list = mapper.selectValBokTabledictByBooksetId(booksetId);
                resultMap.put(InterfCommonConstant.账套信息查询_会计元素, list);
            }
            // BOK_SCENESUBJECT
            if (queryType == null || queryType.equals(InterfCommonConstant.账套信息查询_场景科目关系)) {
                List<BokScenesubject> list = mapper.selectValBokScenesubjectByBooksetId(booksetId);
                resultMap.put(InterfCommonConstant.账套信息查询_场景科目关系, list);
            }
            // BOK_SCENECONFIG
            if (queryType == null || queryType.equals(InterfCommonConstant.账套信息查询_场景分支配置)) {
                List<BokSceneconfig> list = mapper.selectValBokSceneconfigByBooksetId(booksetId);
                resultMap.put(InterfCommonConstant.账套信息查询_场景分支配置, list);
            }
            if (queryType == null || queryType.equals(InterfCommonConstant.账套信息查询_账套估值方案)) {
                List<BokBooksetPlan> list = mapper.selectValBooksetValPlanByBooksetId(booksetId);
                resultMap.put(InterfCommonConstant.账套信息查询_账套估值方案, list);
            }
        }
        /** modify by dongjun on 20181029 end **/

        return resultMap;
    }

    /**
     * 账套信息(根据产品代码)
     * 
     * @param prodId 产品代码
     * @return
     */
    public ValBooksetInfoDto getBooksetInfoByProdId(String prodId) {
        List<ValBooksetInfoDto> list = mapper.getBooksetInfoByProdId(prodId);
        if (!StringUtil.isEmptyList(list)) {
            return list.get(0);
        }
        return null;
    }
    /**
     * 账套科目信息(根据账套代码和科目号)
     * 
     * @param valBokSubject 账套代码 科目号
     * @return
     */
    public BokSubject getValBokSubject(BokSubject valBokSubject) {
        return mapper.getValBokSubject(valBokSubject);
    }
    /**
     * 生成UUID
     * 
     * @param
     */
    public String getUUid() {
        UUID uuid = UUID.randomUUID();
        String id = (uuid.toString()).replace("-", "").substring(0, 32);
        return id;
    }

    /**
     * 查询标的信息
     *ValRateSwapValuationInfo002
     * @param finprodId
     * @return 标的信息
     */
    public TargetFinProduct getTargetFinProduct(String finprodId) {
        TargetFinProduct targetFinProduct = mapper.getTargetFinProduct(finprodId);
        return targetFinProduct;
    }

    /**
     * 查询标的信息
     * 
     * @param finprodId 金融产品代码
     * @return 标的信息
     */
    public FinTradeProduct queryFinTradeProduct(String finprodId) {
        FinTradeProduct finTradeProduct = mapper.queryFinTradeProduct(finprodId);
        return finTradeProduct;
    }

    /**
     * 查询金融产品账户名称
     * 
     * @param finprodId 金融产品代码
     * @param finprodType 金融产品类型
     * @param acctType 账户类型
     * @param acctNo 账户编号
     * @return
     * @author WUJIE
     * @serialData 20180726
     */
	public StlFinProductAcct queryValFinProductAcct(String finprodId, String finprodType, String acctType,
                                                    String acctNo, String ccy) {
		StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(finprodId, finprodType, acctType, acctNo,
				ccy);
		return valFinProductAcct1;
	}

    /**
     * 查询账套清算款账户
     * 
     * @param booksetId 账套ID BOK_BOOKSET_CAPACCT_INFO
     * @param acctNo 账号
     * @return
     * @author WUJIE
     * @serialData 20181210
     */
    public BokBooksetCapacctInfo queryBokBooksetCapacctInfo(String booksetId, String acctNo, String ccy, String acctType) {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("booksetId", booksetId);
        paramsMap.put("acctNo", acctNo);
        paramsMap.put("ccy", ccy);
        paramsMap.put("acctType", acctType);
        return mapper.queryBokBooksetCapacctInfo(paramsMap);
    }

//    /**
//     * 结算指令生成共通
//     * 
//     * @param productDealDto
//     * @param insType
//     * @return add by wujie on 20181017
//     */
//    public Map<String, Object> settlementInstructionMap(ProductDealDto productDealDto,
//                                                        String insType, String isBook,String layeringFlag) {
//        // 定义 变量.结算指令Dto
//        StlOrders valOrders = new StlOrders();
//        // 定义 变量.结算指令扩展Dto
//        StlOrdersExt valOrdersExt = new StlOrdersExt();
//        // 定义 变量.结算日期 =输入.交易标的Dto.交易日
//        Date dealDate = productDealDto.getTradeDate();
//        // 定义 变量.本方资金账户
//        String myAcctNo = "";
//        // 定义 变量.收付方向
//        String ps = "";
//        // 定义 变量.对方资金账户
//        String counterAcctNo = "";
//        // 本方账户类型
//        String myAcctType = "";
//
//        // 初始化结果集
//        Map<String, Object> resultMap = new HashMap<>();
//        // 2.1 净值型产品-认购申购、货币型产品-认购申购、增值税缴纳_退税日_退税、期存款利息_结息、活期存款利息_卖出
//        // 、预期收益型产品-认购申购、应收款项转仓转出
//        if (DictCodeListExt1.指令类型_净值型产品_产品认购申购_收取.getValue().equals(insType)
//            || DictCodeListExt1.指令类型_增值税缴纳_退税日_退税.getValue().equals(insType)
//            || DictCodeListExt1.指令类型_货币型产品_产品认购申购_收取.getValue().equals(insType)
//            || DictCodeListExt1.指令类型_活期存款利息_结息.getValue().equals(insType)
//            || DictCodeListExt1.指令类型_活期存款利息_卖出_转仓.getValue().equals(insType)
//            || DictCodeListExt1.指令类型_预期收益型产品_认购申购.getValue().equals(insType)
//            || DictCodeListExt1.指令类型_应收款项转出.getValue().equals(insType)) {
//        	/**
//        	 * modify by wujie on 20190107 start
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_产品托管户.getValue(), null);
//            myAcctNo = valFinProductAcct1 == null ? null : valFinProductAcct1.getAcctNo();
//            **/
//        	myAcctNo = this.selectValFinProductAcct(productDealDto.getFinprodId(), 
//        			productDealDto.getFinprodType(), DictCodeListExt1.资金账户类型_产品托管户.getValue(), productDealDto.getCcy(),
//        			productDealDto.getBookNo(), layeringFlag);
//            /**modify by wujie on 20190107 end**/
//            counterAcctNo = null;
//            ps = DictCodeList.收付方向_收入.getValue();
//            myAcctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
//        }
//        // 2.2 净值型产品-产品现金分红-支付、增值税缴纳_缴纳日_增值税缴纳
//        // 、应收款项转仓转入
//        else if (DictCodeListExt1.指令类型_净值型产品_产品现金分红_支付.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_增值税缴纳_缴纳日_增值税缴纳.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_货币型产品_产品分红.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_货币型产品_产品到期分红.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_活期存款利息_买入_转仓.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_应收款项转入.getValue().equals(insType)) {
//        	/**
//        	 * modify by wujie on 20190107 start
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_产品托管户.getValue(), null);
//            myAcctNo = valFinProductAcct1 == null ? null : valFinProductAcct1.getAcctNo();
//            **/
//        	myAcctNo = this.selectValFinProductAcct(productDealDto.getFinprodId(), 
//        			productDealDto.getFinprodType(), DictCodeListExt1.资金账户类型_产品托管户.getValue(), productDealDto.getCcy(),
//        			productDealDto.getBookNo(), layeringFlag);
//            /**modify by wujie on 20190107 end**/
//            counterAcctNo = null;
//            ps = DictCodeListExt1.收付方向_支出.getValue();
//            myAcctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
//        }
//        // 2.3 净值型产品-产品赎回到期、货币型产品_产品赎回到期_本金确认、预期收益型产品-赎回到期、预期收益型产品-兑付客户收益
//        // 、拆放同业-拆入到期
//        else if (DictCodeListExt1.指令类型_净值型产品_产品赎回到期.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_货币型产品_产品赎回到期_本金确认.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_预期收益型产品_赎回到期.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_预期收益型产品_兑付客户收益.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_拆放同业_拆入_到期.getValue().equals(insType)) {
//        	/**
//        	 * modify by wujie on 20190107 start
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_待清算款.getValue(), null);
//            myAcctNo = valFinProductAcct1 == null ? null : valFinProductAcct1.getAcctNo();
//            **/
//        	myAcctNo = this.selectValFinProductAcct(productDealDto.getFinprodId(), 
//        			productDealDto.getFinprodType(), DictCodeListExt1.资金账户类型_待清算款.getValue(), productDealDto.getCcy(),
//        			productDealDto.getBookNo(), layeringFlag);
//            /**modify by wujie on 20190107 end**/
//            counterAcctNo = null;
//            ps = DictCodeListExt1.收付方向_支出.getValue();
//            myAcctType = DictCodeListExt1.资金账户类型_待清算款.getValue();
//        }
//        // 2.4 净值型产品-产品赎回到期-支付 、货币型产品-产品赎回到期-本金支付
//        // 、资金划款-收益型产品赎回到期、资金划款-收益型产品支付客户收益
//        // 、资金划款-拆入到期
//        else if (DictCodeListExt1.指令类型_净值型产品_产品赎回到期_支付.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_资金划款_货币型产品赎回到期.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_预期收益型产品_产品赎回到期_产品本金交易_支付.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_预期收益型产品_支付客户收益.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_资金划款_拆入到期.getValue().equals(insType)) {
//        	/**
//        	 * modify by wujie on 20190107 start
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_产品托管户.getValue(), null);
//            myAcctNo = valFinProductAcct1 == null ? null : valFinProductAcct1.getAcctNo();
//            
//             StlFinProductAcct valFinProductAcct2 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_待清算款.getValue(), null);
//            counterAcctNo = valFinProductAcct2.getAcctNo();
//            
//            **/
//        	myAcctNo = this.selectValFinProductAcct(productDealDto.getFinprodId(), 
//        			productDealDto.getFinprodType(), DictCodeListExt1.资金账户类型_产品托管户.getValue(), productDealDto.getCcy(),
//        			productDealDto.getBookNo(), layeringFlag);
//            
//        	counterAcctNo = this.selectValFinProductAcct(productDealDto.getFinprodId(), 
//        			productDealDto.getFinprodType(), DictCodeListExt1.资金账户类型_待清算款.getValue(), productDealDto.getCcy(),
//        			productDealDto.getBookNo(), layeringFlag);
//        	/**modify by wujie on 20190107 end**/
//          
//            ps = DictCodeListExt1.收付方向_支出.getValue();
//            myAcctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
//            dealDate = productDealDto.getEffDate();
//        }
//        // 2.5 产品其他费用-支付 、投资类费用_交易费用_支付
//        else if (DictCodeListExt1.指令类型_产品其他费用_支付.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_投资类费用_交易费用_支付.getValue().equals(insType)) {
//            /**
//        	 * modify by wujie on 20190107 start
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_产品托管户.getValue(), null);
//            myAcctNo = valFinProductAcct1 == null ? null : valFinProductAcct1.getAcctNo();
//            **/
//        	myAcctNo = this.selectValFinProductAcct(productDealDto.getFinprodId(), 
//        			productDealDto.getFinprodType(), DictCodeListExt1.资金账户类型_产品托管户.getValue(), productDealDto.getCcy()
//        			, productDealDto.getBookNo(), layeringFlag);
//            /**modify by wujie on 20190107 end**/
//            
//            counterAcctNo = null;
//            ps = DictCodeListExt1.收付方向_支出.getValue();
//            myAcctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
//        }
//        // 2.6 资金交收-债券类资产买入（资金交收日）、资金交收-计划类资产买入（资金交收日）
//        // 、资金交收-质押式逆回购首期（资金交收）、资金交收-质押式正回购到期（资金交收）
//        // 、资金交收-买断式逆回购首期（资金交收）、资金交收-买断式正回购到期（资金交收）
//        else if (DictCodeListExt1.指令类型_资金交收_债券类资产买入_资金交收日.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_资金交收_计划类资产买入_资金交收日.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_质押式逆回购首期_资金交收.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_质押式正回购到期_资金交收.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_资金交收_买断式逆回购首期_资金到账.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_资金交收_买断式正回购到期_资金到账.getValue().equals(insType)) {
//            ps = DictCodeListExt1.收付方向_支出.getValue();
//
//            /** add by chendijun 2018/11/20 start **/
//            // 变量.账户类型 = 数据字典：资金账户类型.托管户
//            String acctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
//            // 定义 变量.资金账户类型AcctType
//            String zjAcctType = this.getBondBackInstype(productDealDto, insType);
//            if (zjAcctType != null) {
//                acctType = zjAcctType;
//            }
//            /** add by chendijun 2018/11/20 end **/
//
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(), acctType, null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//
//            StlFinProductAcct valFinProductAcct2 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_待清算款.getValue(), null, productDealDto.getCcy());
//            counterAcctNo = valFinProductAcct2.getAcctNo();
//            dealDate = productDealDto.getEffDate();
//            myAcctType = acctType;
//        }
//        // 2.7 公允价值损益类债券资产买入、摊余成本类债券资产买入、综合收益类债券资产买入、摊余成本类计划资产资产买入
//        // 、质押式逆回购-起息、质押式正回购-到期、买断式逆回购-起息、买断式正回购-到期
//        else if (DictCodeListExt1.指令类型_公允价值损益类债券资产买入.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_摊余成本类债券资产买入.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_综合收益类债券资产买入.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_质押式逆回购_起息.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_质押式正回购_到期.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_买断式逆回购_起息.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_买断式正回购_到期.getValue().equals(insType)) {
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_待清算款.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//            counterAcctNo = null;
//            ps = DictCodeListExt1.收付方向_支出.getValue();
//            myAcctType = DictCodeListExt1.资金账户类型_待清算款.getValue();
//        }
//        // 2.8 摊余成本类计划资产资产买入
//        else if (DictCodeListExt1.指令类型_摊余成本类计划资产资产买入.getValue().equals(insType)) {
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_待清算款.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//            counterAcctNo = null;
//            myAcctType = DictCodeListExt1.资金账户类型_待清算款.getValue();
//            ps = DictCodeListExt1.收付方向_支出.getValue();
//        }
//        // 2.9 公允价值损益类债券资产-收息到账、摊余成本类债券资产-收息到账、摊余成本类计划资产资产-收息到账
//        //、综合收益类债券资产-收息到账
//        else if (DictCodeListExt1.指令类型_公允价值损益类债券资产_收息到账.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_摊余成本类债券资产_收息到账.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_摊余成本类计划资产资产_收息到账.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_综合收益类债券资产_收息到账.getValue().equals(insType)
//                 ) {
//            /** add by chendijun 2018/11/20 start **/
//            // 变量.账户类型 = 数据字典：资金账户类型.托管户
//            String acctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
//            // 定义 变量.资金账户类型AcctType
//            String zjAcctType = this.getBondBackInstype(productDealDto, insType);
//            if (zjAcctType != null) {
//                acctType = zjAcctType;
//            }
//            /** add by chendijun 2018/11/20 end **/
//
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(), acctType, null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//            counterAcctNo = null;
//            ps = DictCodeList.收付方向_收入.getValue();
//            myAcctType = acctType;
//        }
//        // 2.10 公允价值损益类债券资产卖出、摊余成本类债券资产卖出、综合收益类债券资产卖出、摊余成本类计划资产资产卖出
//        // 、质押式逆回购-到期、质押式正回购-起息、买断式逆回购派息-资金到账、买断式逆回购-到期、买断式正回购-起息
//        // 存放同业（一般款项）收息、存放同业-还本/到期、存放同业_一般款项_到期卖出
//        // 拆分同业-中间收息、拆放同业-转仓卖出、拆放同业-到期、拆入-起息
//        /**add by wujie on 20190228 start**/
//        //、摊余成本类债券-行权回售、摊余成本类债券-行权赎回
//		//、公允价值损益类债券-行权回售、公允价值损益类债券-行权赎回
//		//、综合收益类债券-行权回售、综合收益类债券-行权赎回
//        /**add by wujie on 20190228 end**/
//        else if (DictCodeListExt1.指令类型_公允价值损益类债券资产卖出.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_摊余成本类债券资产卖出.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_综合收益类债券资产卖出.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_质押式逆回购_到期.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_质押式正回购_起息.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_买断式逆回购_派息.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_买断式逆回购_到期.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_买断式正回购_起息.getValue().equals(insType)
//
//                 || DictCodeListExt1.指令类型_存放同业_一般款项_收息.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_存放同业_一般款项_到期卖出.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_存放同业_还本_到期.getValue().equals(insType)
//
//                 || DictCodeListExt1.指令类型_拆放同业_同业借款_中间收息.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_拆放同业_同业借款_卖出.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_拆放同业_同业借款_到期.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_拆放同业_拆入_买入.getValue().equals(insType)
//                 
//                 /**add by wujie on 20190228 start**/
//                 
//                 || DictCodeListExt1.指令类型_摊余成本类债券_行权回售.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_摊余成本类债券_行权赎回.getValue().equals(insType)
//                 
//                 || DictCodeListExt1.指令类型_公允价值损益类债券_行权回售.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_公允价值损益类债券_行权赎回.getValue().equals(insType)
//                 
//                 || DictCodeListExt1.指令类型_综合收益类债券_行权回售.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_综合收益类债券_行权赎回.getValue().equals(insType)
//                 
//                 /**add by wujie on 20190228 end**/
//
//        ) {
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_待清算款.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//            counterAcctNo = null;
//            myAcctType = DictCodeListExt1.资金账户类型_待清算款.getValue();
//            ps = DictCodeList.收付方向_收入.getValue();
//        }
//        // 2.11 摊余成本类计划资产资产卖出
//        else if (DictCodeListExt1.指令类型_摊余成本类计划资产资产卖出.getValue().equals(insType)) {
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_待清算款.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//            counterAcctNo = null;
//            myAcctType = DictCodeListExt1.资金账户类型_待清算款.getValue();
//            ps = DictCodeListExt1.收付方向_支出.getValue();
//        }
//        // 2.12资金交收-债券类资产卖出-资金到账
//        // 、资金交收-质押式逆回购到期-资金到账、资金交收-质押式正回购首期-资金到账
//        // 、买断式逆回购-派息、资金交收-买断式逆回购到期-资金到账、资金交收-买断式正回购首期-资金到账
//        else if (DictCodeListExt1.指令类型_资金交收_债券类资产卖出_资金到账.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_质押式逆回购到期_资金到账.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_质押式正回购首期_资金到账.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_资金交收_买断式逆回购派息_资金到账.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_资金交收_买断式逆回购到期_资金到账.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_资金交收_买断式正回购首期_资金到账.getValue().equals(insType)) {
//
//            ps = DictCodeList.收付方向_收入.getValue();
//
//            /** add by chendijun 2018/11/20 start **/
//            // 变量.账户类型 = 数据字典：资金账户类型.托管户
//            String acctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
//            // 定义 变量.资金账户类型AcctType
//            String zjAcctType = this.getBondBackInstype(productDealDto, insType);
//            if (zjAcctType != null) {
//                acctType = zjAcctType;
//            }
//            /** add by chendijun 2018/11/20 end **/
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(), acctType, null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//
//            StlFinProductAcct valFinProductAcct2 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_待清算款.getValue(), null, productDealDto.getCcy());
//            counterAcctNo = valFinProductAcct2.getAcctNo();
//            myAcctType = acctType;
//            dealDate = productDealDto.getEffDate();
//        }
//        // 2.13 资金交收-计划类资产卖出-资金到账
//        // 资金收款-存放同业收息、资金收款-存放同业还本/到期
//        // 、资金收款-同业借款中间收息、资金收款-同业借款卖出、资金收款-同业借款还本到期、资金收款-拆入起息
//        else if (DictCodeListExt1.指令类型_资金交收_计划类资产卖出_资金到账.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_资金收款_存放同业_收息.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_资金收款_存放同业_还本到期.getValue().equals(insType)
//
//                 || DictCodeListExt1.指令类型_资金收款_同业借款中间收息.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_资金收款_同业借款卖出.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_资金收款_同业借款还本到期.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_资金收款_拆入起息.getValue().equals(insType)) {
//            ps = DictCodeList.收付方向_收入.getValue();
//
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_产品托管户.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//
//            StlFinProductAcct valFinProductAcct2 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_待清算款.getValue(), null, productDealDto.getCcy());
//            counterAcctNo = valFinProductAcct2.getAcctNo();
//            myAcctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
//            dealDate = productDealDto.getEffDate();
//        }
//        // 2.14 公允价值损益类债券资产-还本到账、摊余成本类债券资产-还本到账、综合收益类债券资产-还本到账、摊余成本类计划资产资产-还本到账
//        else if (DictCodeListExt1.指令类型_公允价值损益类债券资产_还本到账.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_摊余成本类债券资产_还本到账.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_综合收益类债券资产_还本到账.getValue().equals(insType)) {
//
//            /** add by chendijun 2018/11/20 start **/
//            // 变量.账户类型 = 数据字典：资金账户类型.托管户
//            String acctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
//            // 定义 变量.资金账户类型AcctType
//            String zjAcctType = this.getBondBackInstype(productDealDto, insType);
//            if (zjAcctType != null) {
//                acctType = zjAcctType;
//            }
//            /** add by chendijun 2018/11/20 end **/
//
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(), acctType, null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//            counterAcctNo = null;
//            ps = DictCodeList.收付方向_收入.getValue();
//            myAcctType = acctType;
//            dealDate = productDealDto.getEffDate();
//        }
//        // 2.15 摊余成本类计划资产资产-还本到账
//        else if (DictCodeListExt1.指令类型_摊余成本类计划资产资产_还本到账.getValue().equals(insType)) {
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_产品托管户.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//            counterAcctNo = null;
//            ps = DictCodeList.收付方向_收入.getValue();
//            myAcctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
//            dealDate = productDealDto.getEffDate();
//        }
//        // 2.16 公允价值损益类-货币基金-认购确认、公允价值损益类-货币基金-申购确认、非货币基金（交易性）-认购确认、非货币基金（交易性）-申购（确认）
//        else if (DictCodeListExt1.指令类型_公允价值损益类_货币基金_申购确认.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_非货币基金_交易性_申购_确认.getValue().equals(insType)) {
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_待清算款.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//            counterAcctNo = null;
//            ps = DictCodeListExt1.收付方向_支出.getValue();
//            myAcctType = DictCodeListExt1.资金账户类型_待清算款.getValue();
//        }
//        // 2.17 公允价值损益类-货币基金-申购、非货币基金-申购划款、计划类净值型资产-申购划款
//        else if (DictCodeListExt1.指令类型_公允价值损益类_货币基金_申购.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_非货币基金_交易性_申购_划款.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_公允价值损益类_计划类_净值型_申购_划款.getValue().equals(insType)) {
//            ps = DictCodeListExt1.收付方向_支出.getValue();
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_产品托管户.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//
//            StlFinProductAcct valFinProductAcct2 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_待清算款.getValue(), null, productDealDto.getCcy());
//            counterAcctNo = valFinProductAcct2.getAcctNo();
//            myAcctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
//            dealDate = productDealDto.getEffDate();
//        }
//        // 2.18 货币基金及货币型资产-转仓买入、非货币基金及净值型资产-转仓买入
//        else if (DictCodeListExt1.指令类型_公允价值损益类_货币基金_买入.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_非货币基金_交易性_买入.getValue().equals(insType)) {
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getProdId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_产品托管户.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//            counterAcctNo = null;
//            ps = DictCodeListExt1.收付方向_支出.getValue();
//            myAcctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
//        }
//        // 2.19 公允价值损益类-货币基金-赎回-确认、非货币基金及净值型资产-赎回确认
//        // 存放同业（一般款项）到期卖出
//        else if (DictCodeListExt1.指令类型_公允价值损益类_货币基金_赎回_确认.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_非货币基金_交易性_赎回.getValue().equals(insType)
//
//                 || DictCodeListExt1.指令类型_存放同业_一般款项_到期卖出.getValue().equals(insType)) {
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_待清算款.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//            counterAcctNo = null;
//            ps = DictCodeList.收付方向_收入.getValue();
//            myAcctType = DictCodeListExt1.资金账户类型_待清算款.getValue();
//        }
//        // 2.20 公允价值损益类-货币基金-赎回-收款、非货币基金-赎回到账、计划类净值型资产-赎回到账
//        // 、资金收款-存放同业卖出
//        else if (DictCodeListExt1.指令类型_公允价值损益类_货币基金_赎回_收款.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_非货币基金_交易性_赎回_收款.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_资金收款_存放同业卖出.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_公允价值损益类_计划类_净值型_赎回到账日.getValue().equals(insType)) {
//
//            ps = DictCodeList.收付方向_收入.getValue();
//
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_产品托管户.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//
//            StlFinProductAcct valFinProductAcct2 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_待清算款.getValue(), null, productDealDto.getCcy());
//            counterAcctNo = valFinProductAcct2.getAcctNo();
//            myAcctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
//            dealDate = productDealDto.getEffDate();
//        }
//        // 2.21 非货币基金及净值型资产-现金分红、公允价值损益类-货币基金-现金分红
//        else if (DictCodeListExt1.指令类型_非货币基金_交易性_现金分红.getValue().equals(insType)
//        		|| DictCodeListExt1.指令类型_公允价值损益类_货币基金_现金分红.getValue().equals(insType)) {
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_产品托管户.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//            counterAcctNo = null;
//            ps = DictCodeList.收付方向_收入.getValue();
//            myAcctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
//        }
//        // 2.22 货币基金及货币型资产-转仓卖出、非货币基金及净值型资产-转仓卖出
//        else if (DictCodeListExt1.指令类型_公允价值损益类_货币基金_卖出.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_非货币基金及净值型资产_转仓卖出.getValue().equals(insType)) {
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_产品托管户.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//            counterAcctNo = null;
//            ps = DictCodeList.收付方向_收入.getValue();
//            myAcctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
//        }
//        // 2.23 资金划款-存放同业起息、资金划款-同业借款买入
//        else if (DictCodeListExt1.指令类型_资金划款_存放同业起息.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_资金收款_同业借款买入.getValue().equals(insType)) {
//            ps = DictCodeListExt1.收付方向_支出.getValue();
//
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_产品托管户.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//            myAcctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
//
//            StlFinProductAcct valFinProductAcct2 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_待清算款.getValue(), null, productDealDto.getCcy());
//            counterAcctNo = valFinProductAcct2.getAcctNo();
//        }
//        // 2.24 存放同业（一般款项）买入、拆放同业（同业借款）买入
//        else if (DictCodeListExt1.指令类型_存放同业_一般款项_买入.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_拆放同业_同业借款_买入.getValue().equals(insType)) {
//            ps = DictCodeListExt1.收付方向_支出.getValue();
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_待清算款.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//            myAcctType = DictCodeListExt1.资金账户类型_待清算款.getValue();
//            dealDate = productDealDto.getEffDate();
//        }
//
//        // 2.25 债券借贷-融出到期、债券借贷-融入期间标的债券收息、债券借贷-融入期间标的债券还本
//        else if (DictCodeListExt1.指令类型_债券借贷_融出到期.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_债券借贷_融入期间标的债券收息.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_债券借贷_融入期间标的债券还本.getValue().equals(insType)) {
//            ps = DictCodeList.收付方向_收入.getValue();
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_产品托管户.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//            myAcctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
//            dealDate = productDealDto.getEffDate();
//        }
//        // 2.26 债券借贷-融入到期、债券借贷-融入期间标的债券付息、债券借贷-融入债券卖出后收息
//        // 、债券借贷-融入债券卖出后还本、债券借贷-融入期间标的券本金支付
//        else if (DictCodeListExt1.指令类型_债券借贷_融入到期.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_债券借贷_融入期间标的债券付息.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_债券借贷_融入债券卖出后收息.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_债券借贷_融入债券卖出后还本.getValue().equals(insType)
//                 || DictCodeListExt1.指令类型_债券借贷_融入期间标的券本金支付.getValue().equals(insType)) {
//            ps = DictCodeListExt1.收付方向_支出.getValue();
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_产品托管户.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//            myAcctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
//            dealDate = productDealDto.getEffDate();
//        }
//        // 2.27 债券借贷-融入债券卖出后购回
//        else if (DictCodeListExt1.指令类型_债券借贷_融入债券卖出后购回.getValue().equals(insType)) {
//            ps = DictCodeListExt1.收付方向_支出.getValue();
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_待清算款.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//            myAcctType = DictCodeListExt1.资金账户类型_待清算款.getValue();
//            dealDate = productDealDto.getEffDate();
//        }
//        // 2.28 债券借贷-融入债券卖出
//        else if (DictCodeListExt1.指令类型_债券借贷_融入债券卖出.getValue().equals(insType)) {
//            ps = DictCodeList.收付方向_收入.getValue();
//            StlFinProductAcct valFinProductAcct1 = mapper.selectValFinProductAcct(
//                productDealDto.getFinprodId(), productDealDto.getFinprodType(),
//                DictCodeListExt1.资金账户类型_待清算款.getValue(), null, productDealDto.getCcy());
//            myAcctNo = valFinProductAcct1.getAcctNo();
//            myAcctType = DictCodeListExt1.资金账户类型_待清算款.getValue();
//            dealDate = productDealDto.getEffDate();
//        }
//        /** 撤销指令交易对应的原交易编号查询 add by chendijun 2018/11/14 start **/
//        String pOrderId = "";
//        if (DictCodeListExt1.交易状态_撤销.getValue().equals(productDealDto.getTradeStatus())) {
//            StlOrders valOrdersCX = new StlOrders();
//            // 变量.结算指令Dto.交易编号 = 输入.交易标的Dto.原交易编号
//            valOrdersCX.setTradeId(productDealDto.getpTradeId());
//            // 变量.结算指令Dto.指令类型 = 输入.指令类型
//            valOrdersCX.setOrderType(insType);
//            // 变量.结算指令Dto.业务类型 = 输入.交易标的Dto.业务类型
//            valOrdersCX.setBusiType(productDealDto.getBusiType());
//            pOrderId = this.queryPOrderId(valOrdersCX);
//        }
//        /** 撤销指令交易对应的原交易编号查询 add by chendijun 2018/11/14 end **/
//        // 定义 变量.指令编号 = 自动生成
//        String fingerNo = getTradeid(insType, dealDate, "B");
//        // 入库
//        // 结算指令表(STL_ORDERS)
//        valOrders.setOrderId(fingerNo);
//        valOrders.setBusiType(productDealDto.getBusiType());
//        valOrders.setOrderType(insType);
//        valOrders.setOrderNum(null);
//        valOrders.setSettleDate(dealDate);
//        valOrders.setTradeId(productDealDto.getTradeId());
//        valOrders.setMyAcctNo(myAcctNo);
//        valOrders.setPs(ps);
//        valOrders.setCounterAcctNo(counterAcctNo);
//        valOrders.setSettleAmt(productDealDto.getTardeAmt());
//        valOrders.setCcy(productDealDto.getCcy());
//        /** add by chendijun 2018/11/14 start **/
//        valOrders.setSettleStatus(DictCodeList.成功失败标志_成功.getValue());
//        valOrders.setOrderStatus(productDealDto.getTradeStatus());
//        valOrders.setpOrderId(pOrderId);
//        /** add by chendijun 2018/11/14 start **/
//        valOrders.setCreateDept(null);
//        valOrders.setCreateTime(productDealDto.getCreateTime());
//        valOrders.setUpdateTime(new Date());
//        resultMap.put("ORDERS", valOrders);
//
//        // 3.2 结算指令扩展表(VAL_ORDERS_EXT)
//        valOrdersExt.setOrderId(fingerNo);
//        valOrdersExt.setIsTrade(isBook);
//        valOrdersExt.setCreateDept(null);
//        valOrdersExt.setCreateTime(productDealDto.getCreateTime());
//        valOrdersExt.setUpdateTime(new Date());
//        resultMap.put("ORDERS_EXT", valOrdersExt);
//
//        resultMap.put("myAcctNo", myAcctNo);// 本方账户
//        resultMap.put("myAcctType", myAcctType);// 本方账户类型
//
//        return resultMap;
//    }
//
//    
//    /**
//     * 结算指令生成
//     * 
//     * @param productDealDto 交易标的
//     * @param insType 指令类型
//     * @return Map<String,Object>
//     * @see
//     * @since 1.0
//     */
//    public Map<String, Object> settlementInstructionGeneration(ProductDealDto productDealDto,
//                                                               String insType, String isBook) {
//        Map<String, Object> resultMap = this.settlementInstructionMap(productDealDto, insType,
//            isBook,DictCodeList.是否_否.getValue());
//        // 结算指令
//        StlOrders valOrders = (StlOrders)resultMap.get("ORDERS");
//        String fingerNo = valOrders.getOrderId();
//        Date dealDate = valOrders.getSettleDate();
//        String myAcctNo = (String)resultMap.get("myAcctNo");
//        String myAcctType = (String)resultMap.get("myAcctType");
//
//        // 入账信息表，入账账务明细表， 账务明细代码表 isBook 是 N\
//        if (DictCodeList.是否_否.getValue().equals(isBook)) {
//            // 定义 变量.入账信息Dto
//            BokDealAccounting valDealAccounting = new BokDealAccounting();
//
//            // 账套代码 BOOKSET_ID 输入.交易标的Dto.账套代码
//            valDealAccounting.setBooksetId(productDealDto.getBookNo());
//            // 业务编号 BUSI_NO 变量.指令编号
//            valDealAccounting.setBusiNo(fingerNo);
//            // 业务表 BUSI_TABLE 固定值：01 结算指令表
//            valDealAccounting.setBusiTable(DictCodeList.业务表_结算指令表.getValue());
//            // 录入日 BOOKSET_DATE 变量.结算日期
//            valDealAccounting.setBooksetDate(dealDate);
//            // 入账日 BOOK_DATE 变量.结算日期
//            valDealAccounting.setBookDate(dealDate);
//            // 实际发生日 HAPPEN_DATE 变量.结算日期
//            valDealAccounting.setHappenDate(dealDate);
//            // 是否已入账 IS_ACCOUNTED N 否
//            valDealAccounting.setIsAccounted(DictCodeList.是否_否.getValue());
//            // 入账凭证编号 BOOK_ID NULL
//            valDealAccounting.setBookId(null);
//
//            valDealAccounting.setCreateTime(productDealDto.getCreateTime());
//
//            // 定义 变量.入账账务明细Dto
//            BokDealAccountingDetail valDealAccountingDetail = new BokDealAccountingDetail();
//            // 账套代码 BOOKSET_ID 输入.交易标的Dto.账套代码
//            valDealAccountingDetail.setBooksetId(productDealDto.getBookNo());
//            // 业务编号 BUSI_NO 变量.指令编号
//            valDealAccountingDetail.setBusiNo(fingerNo);
//            // 业务表 BUSI_TABLE 固定值：01 结算指令表
//            valDealAccountingDetail.setBusiTable(DictCodeList.业务表_结算指令表.getValue());
//            // 业务类型 BUSI_TYPE 输入.金融产品交易.业务类型
//            valDealAccountingDetail.setBusiType(productDealDto.getBusiType());
//
//            valDealAccountingDetail.setCreateTime(productDealDto.getCreateTime());
//
//            // 定义 变量.账务明细代码表Dto
//            BokDetailId valBokDetailId = new BokDetailId();
//            // 账套代码 BOOKSET_ID 输入.交易标的Dto.账套代码
//            valBokDetailId.setBooksetId(productDealDto.getBookNo());
//            // 业务明细代码 BUSI_ID 变量.本方资金账户
//            valBokDetailId.setBusiId(myAcctNo);
//            // 业务明细类型 BUSI_DETAIL_TYPE 变量.本方账户类型
//            valBokDetailId.setBusiDetailType(myAcctType);
//            // 投资目的 INV_AIM 输入.金融产品交易.投资目的
//            valBokDetailId.setInvAim(null);
//            // 交易编号 TRADE_ID 输入.交易标的Dto.交易编号
//            valBokDetailId.setTradeId(productDealDto.getTradeId());
//
//            String detailId = this.genMd5BokDetailId(valBokDetailId);
//            valBokDetailId.setBokDetailId(detailId);
//            valDealAccountingDetail.setBokDetailId(detailId);
//
//            resultMap.put("ACCOUNTING", valDealAccounting);
//            resultMap.put("ACCOUNTING_DETAI", valDealAccountingDetail);
//            resultMap.put("BOK_DETAIL_ID", valBokDetailId);
//
//        }
//
//        return resultMap;
//    }

    /**
     * 编号生成
     * 
     * @param str:交易方向或者指令类型
     * @param date:日期
     * @param flag: A:交易编号 B:指令编号
     */
    public String getTradeid(String str, Date date, String flag) {
        int sequences;
        // 查询数据库sequences
        if ("A".equals(flag)) {
            // dBUtil.getSequenceIndex(sequenceType);
            sequences = mapper.queryPdSequences();
        }
        else {
            sequences = mapper.queryOrderSequences();
        }
        // 组装8位序列号 不足补0
        String sequence = StringUtils.leftPad(String.valueOf(sequences), 8, '0');
        // 日期格式化yyymmdd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String formatDate = sdf.format(date);

        String tradeid = str + formatDate + sequence;
        return tradeid;
    }

    /**
     * 组装账务明细(资金帐户/现金类)
     * 
     * @param busiId
     * @param bokDetailId
     * @param busiDetailType
     * @param invAim
     * @param branch 分支序号 add by wujie on 20181029 remark:账务明细代码规则调整
     * @return
     */
    public BokDetailId getBokDetailId(String busiId, String bokDetailId, String busiDetailType,
                                      String invAim, Long branch, String ccy) {
        BokDetailId valBokDetailId = new BokDetailId();
        valBokDetailId.setBusiId(busiId);
        valBokDetailId.setBusiDetailType(busiDetailType);
        valBokDetailId.setInvAim(invAim);
        valBokDetailId.setBranch(branch);// add by wujie on 20181029
        valBokDetailId.setCcy(ccy);
        if (bokDetailId == null) {
            valBokDetailId.setBokDetailId(genMd5BokDetailId(valBokDetailId));
        }
        else {
            valBokDetailId.setBokDetailId(bokDetailId);
        }
        return valBokDetailId;
    }

    /**
     * 生成MD5账务明细代码
     *
     * @param valBokDetailId 账务明细代码生成参数
     * @return String 账务明细代码
     * @see
     * @since 1.0
     */
    public String genMd5BokDetailId(BokDetailId valBokDetailId) {
        /**
         * String tempBokDetailId = null; // 判断分支: 除了债券，均为业务明细代码 + 业务明细类型 if
         * (DictCodeListExt1.金融产品类型_债券.getValue().equals(valBokDetailId.getBusiDetailType())) { // 债券
         * // 账务明细代码 = 业务明细代码 + 业务明细类型 + 投资目的 + 证券管理户代码 tempBokDetailId = FinalString.I1 +
         * ValStringUtil.getNotNullStr(valBokDetailId.getBusiId()) + FinalString.I2 +
         * ValStringUtil.getNotNullStr(valBokDetailId.getBusiDetailType()) + FinalString.I3 +
         * ValStringUtil.getNotNullStr(valBokDetailId.getInvAim()) + FinalString.I4 +
         * ValStringUtil.getNotNullStr(valBokDetailId.getSecManageAcctId()); if
         * (valBokDetailId.getBranch() != null && valBokDetailId.getBranch() != 0) {
         * tempBokDetailId += FinalString.I5 + valBokDetailId.getBranch(); } } else { // 账务明细代码 =
         * 业务明细代码 + 业务明细类型 tempBokDetailId = FinalString.I1 +
         * ValStringUtil.getNotNullStr(valBokDetailId.getBusiId()) + FinalString.I2 +
         * ValStringUtil.getNotNullStr(valBokDetailId.getBusiDetailType()); if
         * (valBokDetailId.getBranch() != null && valBokDetailId.getBranch() != 0) {
         * tempBokDetailId += FinalString.I5 + valBokDetailId.getBranch(); } } String
         * md5BokDetailId = MD5Util.string2MD5(tempBokDetailId);
         * valBokDetailId.setBokDetailId(md5BokDetailId);
         **/
        return bookAccountCommonService.genMd5BokDetailId(valBokDetailId);
    }

    /**
     * 查询核算分类
     * 
     * @return 核算分类
     */
    public String getFinanceProductClass(FinProductClass finProductClass) {
        String valclas = mapper.getFinanceProductClass(finProductClass);
        return valclas;
    }

    /**
     * 查询税务分类
     * 
     * @return 税务分类
     */
    public String getFinanceProductTaxClass(FinProductClass finProductClass) {
        String valclas = mapper.getFinanceProductTaxClass(finProductClass);
        return valclas;
    }

    /**
     * 当前计划对应分支序号查询
     * 
     * @param finprodId 标的代码
     * @param finprodType 金融产品类型
     * @param branchType 分支类型
     * @param mdate 到期日
     * @return
     * @see
     * @since 1.0
     */
    public Long queryValFinProductDealBranchNow(String finprodId, String finprodType,
                                                String branchType, Date mdate) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("finprodId", finprodId);
        map.put("finprodType", finprodType);
        map.put("branchType", branchType);
        map.put("mdate", mdate);
        return mapper.queryValFinProductDealBranchNow(map);
    }

//    /**
//     * 最大分支序号查询
//     * 
//     * @param finprodId 标的代码
//     * @param finprodType 金融产品类型
//     * @return
//     * @see
//     * @since 1.0
//     */
//    public Long queryValFinProductDealMaxBranch(String finprodId, String finprodType) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("finprodId", finprodId);
//        map.put("finprodType", finprodType);
//        return mapper.queryValFinProductDealMaxBranch(map);
//    }

    /**
     * 现金流查询
     * 
     * @param map
     * @return
     */
    public FinCashInfo queryCashInfo(Map<String, Object> map) {
        return mapper.queryCashInfo(map);
    }
    /**
     * 现金流查询
     * 
     * @param map
     * @return
     */
    public List<FinCashInfo> queryCashInfoList(Map<String, Object> map) {
        return mapper.queryCashInfoList(map);
    }
    /**
     * 增值税初始化
     * 
     * @param finProduct 标的类金融产品
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getValueAddedTax(FinProduct finProduct) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        FinCashInfo finCashInfo = new FinCashInfo();
        BokDetailId bokDetailId = new BokDetailId();

        String cashId = InterfHelper.getMd5CashId_(finProduct.getFinprodId(),
            finProduct.getFinprodType(), DictCodeListExt1.现金流二级类型_税额.getValue());

        /** 1. 现金流信息表 **/
        // 现金流代码
        finCashInfo.setCashId(cashId);
        finCashInfo.setCashType1(DictCodeListExt1.现金流一级类型_税额.getValue());
        finCashInfo.setCashType2(DictCodeListExt1.现金流二级类型_税额.getValue());
        // 金融产品代码
        finCashInfo.setFinprodId(finProduct.getFinprodId());
        // 金融产品类型
        finCashInfo.setFinprodType(finProduct.getFinprodType());
        finCashInfo.setBranch(InterfCommonConstant.分支序号_默认);

        /** 2. 账务明细代码表 **/
        // 2.1 查询账套信息
        ValBooksetInfoDto valBooksetInfoDto = getBooksetInfoByProdId(finProduct.getFinprodId());
        String booksetId = valBooksetInfoDto == null ? finProduct.getFinprodId() : valBooksetInfoDto.getBooksetId();
        // 账套代码
        bokDetailId.setBooksetId(booksetId);
        // 业务明细代码
        bokDetailId.setBusiId(finCashInfo.getCashId());
        // 业务明细类型
        bokDetailId.setBusiDetailType(finCashInfo.getCashType2());

        // 账务明细代码
        bokDetailId.setBokDetailId(this.genMd5BokDetailId(bokDetailId));

        // 2.3 给帐套配置默认税务方案
        BokBooksetPlan plan = null;
        String[] types = new String[] {InterfCommonConstant.账套信息查询_账套估值方案};
        // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息查询(根据账套代码)SHEET
        Map<String, Object> booksetInfoMap = getBooksetInfoById(booksetId, types);
        List<BokBooksetPlan> list = (List<BokBooksetPlan>)booksetInfoMap.get(
            InterfCommonConstant.账套信息查询_账套估值方案);
        for (BokBooksetPlan booksetPlan : list) {
            if (DictCodeListExt1.配置方案类型_税务方案.getValue().equals(booksetPlan.getSchemeType())) {
                plan = booksetPlan;
                break;
            }
        }

        if (plan == null || InterfCommonConstant.接口_默认用户.equals(plan.getUpdateUser())) {
            plan = this.queryDefaultPlan(booksetId);
            plan.setBooksetId(booksetId);
        }

        // /** 3. 增值税应税方案初始化 **/
        // Map<String, Object> param = new HashMap<String, Object>();
        // param.put("BOOKSETID", booksetId);
        // param.put("SCHEMETYPE", DictCodeListExt1.配置方案类型_税务方案.getValue());
        // param.put("ISDEFAULT", DictCodeList.是否_是.getValue());
        // BokBooksetPlan plan = mapper.buildPlan(param);
        // plan.setBooksetId(booksetId);

        resultMap.put("FIN_CASH_INFO", finCashInfo);
        resultMap.put("BOK_DETAIL_ID", bokDetailId);
        resultMap.put("BOK_BOOKSET_PLAN", plan);

        return resultMap;
    }

    /**
     * 增值税默认税务方案查询
     * 
     * @param booksetId
     */
    public BokBooksetPlan queryDefaultPlan(String booksetId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("BOOKSETID", booksetId);
        param.put("SCHEMETYPE", DictCodeListExt1.配置方案类型_税务方案.getValue());
        param.put("ISDEFAULT", DictCodeList.是否_是.getValue());
        BokBooksetPlan plan = mapper.buildPlan(param);
        if (plan != null) {
            plan.setBooksetId(booksetId);
        }
        return plan;
    }

    /**
     * 查询账套信息（根据账套代码）
     * 
     * @param booksetId 账套代码
     * @param queryType 查询类型
     * @return 账套信息Map结果集
     * @see
     * @since 1.0
     */
    public Map<String, Object> getBooksetByBooksetId(String booksetId, String queryType) {
        // 定义 查询结果resultMap（Map）
        Map<String, Object> resultMap = new HashMap<>();
        // 2.1.1 查询账套信息
        if (queryType == null || queryType.equals("BOOKSET")) {
            BokBookset valBokBookset = valBokBooksetMapper.selectByPrimaryKey(booksetId);
            resultMap.put("BOOKSET", valBokBookset);
        }

        // 2.2 查询账套日期
        // IF 输入.查询类型 is null or 输入.查询类型 eq "BOOKSET_DATE"
        if (queryType == null || queryType.equals("BOOKSET_EXT")) {
            BokBooksetDate valBokBooksetDate = valBokBooksetDateMapper.selectByPrimaryKey(
                booksetId);
            resultMap.put("BOOKSET", valBokBooksetDate);
        }

        // 2.3 查询账套配置
        if (queryType == null || queryType.equals("BOOKSET_EXT")) {
            List<BokBooksetExt> listValBokBooksetExt = valInterfCommonMapper.queryBooksetExtByBooksetId(
                booksetId);
            resultMap.put("BOOKSET_EXT", listValBokBooksetExt);
        }

        // 2.4 查询账套科目
        if (queryType == null || queryType.equals("SUBJECT")) {
            List<BokSubject> listValBokSubject = valInterfCommonMapper.querySubjectByBooksetId(
                booksetId);
            resultMap.put("SUBJECT", listValBokSubject);
        }

        // 2.5 查询会计场景
        if (queryType == null || queryType.equals("TABLELIST")) {
            List<BokTablelist> listValBokSubject = valInterfCommonMapper.queryTablelistByBooksetId(
                booksetId);
            resultMap.put("TABLELIST", listValBokSubject);
        }

        // 2.6 查询会计元素
        if (queryType == null || queryType.equals("TABLEDICT")) {
            List<BokTabledict> listValBokTablelist = valInterfCommonMapper.queryTabledictByBooksetId(
                booksetId);
            resultMap.put("TABLEDICT", listValBokTablelist);
        }

        // 2.7 查询场景科目关系
        if (queryType == null || queryType.equals("SCENESUBJECT")) {
            List<BokScenesubject> listValBokScenesubject = valInterfCommonMapper.queryScenesubjectByBooksetId(
                booksetId);
            resultMap.put("SCENESUBJECT", listValBokScenesubject);
        }

        // 2.8 查询场景分支配置
        if (queryType == null || queryType.equals("SCENECONFIG")) {
            List<BokSceneconfig> listValBokSceneconfig = valInterfCommonMapper.querySceneconfigByBooksetId(
                booksetId);
            resultMap.put("SCENECONFIG", listValBokSceneconfig);
        }

        // 2.9 查询账套估值方案
        if (queryType == null || queryType.equals("BOOKSET_VAL_PLAN")) {
            List<BokBooksetPlan> listValBooksetValPlan = valInterfCommonMapper.queryValPlanByBooksetId(
                booksetId);
            resultMap.put("BOOKSET_VAL_PLAN", listValBooksetValPlan);
        }
        return resultMap;
    }

    /**
     * 查询账套信息(根据产品代码)
     * 
     * @param prodId 产品代码
     * @return 账套信息结果集
     * @see
     * @since 1.0
     */
    public BokBookset getBooksetByProdId(String prodId) {
        return valInterfCommonMapper.queryBooksetByProdId(prodId);
    }

    /**
     * 账套清算账户初始化
     * 
     * @param productAcct 金融产品账户信息（必输项） 只包括金融产品类型为：F16 理财产品
     * @return 账套清算账户初始化入库
     * @see
     * @since 1.0
     */
    public int initBooksetCapacctInfo(StlFinProductAcct productAcct) {
        // 判空
        if (productAcct == null) {
            return 0;
        }
        // 1.2 定义变量
        // 定义 账套信息Dto
        BokBookset valBokBookset = new BokBookset();
        // 定义 账套清算账户
        BokBooksetCapacctInfo valBooksetCapacctInfo = new BokBooksetCapacctInfo();
        // 2.处理逻辑
        // 2.1 查询账套信息Dto
        // 调用共通设计->账套信息(根据产品代码)
        valBokBookset = this.getBooksetByProdId(productAcct.getFinprodId());
        // 2.2 账套清算账户表(BOK_BOOKSET_CAPACCT_INFO)
        // 初始化结果保存至 变量.账套清算账户
        // 账套代码 = 变量.账套信息Dto.账套代码
        valBooksetCapacctInfo.setBooksetId(valBokBookset.getBooksetId());
        // 账号 = productAcct.账号
        valBooksetCapacctInfo.setAcctNo(productAcct.getAcctNo());
        // 币种 = productAcct.币种
        valBooksetCapacctInfo.setCcy(productAcct.getCcy());
        // 资金账户类型 = productAcct.账户类型
        valBooksetCapacctInfo.setAcctType(productAcct.getAcctType());
        // 2.3 初始化完成的账套信息入数据库
        return valBooksetCapacctInfoMapper.insert(valBooksetCapacctInfo);
    }

    /**
     * 将接口日志更新为失败
     *
     * @param detail
     * @return
     * @see
     * @since 1.0
     */
    public int updateInterfActionLogToFailure(String detail) {
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> statusList = new ArrayList<>();
        // 将未执行和执行中的更新为执行成功
        statusList.add(DictCodeInterfList.接口执行状态_未执行.getValue());
        statusList.add(DictCodeInterfList.接口执行状态_执行中.getValue());
        params.put("statusList", statusList);
        params.put("detailAction", detail);
        params.put("status", DictCodeInterfList.接口执行状态_执行失败.getValue());
        return valInterfCommonMapper.updateInterfActionLogStatus(params);
    }

    /**
     * 初始化拆借-拆出:现金流付息规则、还本规则、付息计划、还本计划
     * 
     * @param dto
     * @return add by wujie on 20180829
     */
    public Map<String, Object> getFinTradProduct(ValDlTarget dto) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<FinCashPayRule> listFinCashPayRule = new ArrayList<FinCashPayRule>();
        List<FinCashPlan> listFinCashPlan = new ArrayList<FinCashPlan>();

        /** 利息-支付规则 **/
        FinCashPayRule payrule = new FinCashPayRule();
        String cashId = InterfHelper.getMd5CashId_(dto.getDlId(), dto.getBusiType(),
            DictCodeListExt1.现金流二级类型_利息.getValue());
        // 现金流代码
        payrule.setCashId(cashId);
        // 起息日 = 接口.起息日
        payrule.setEffDate(dto.getVdate());
        // 频率
        payrule.setPayCycle(DictCodeList.频率_到期一次.getValue());
        // 营业日准则
//        payrule.setTdayRule(DictCodeList.营业日准则_不调整.getValue());
        // 推算方式
        payrule.setCalcRule(DictCodeList.推算方式_起息日向后.getValue());
        // 现金流单位
        payrule.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        // 金融产品代码= 接口.拆借标的代码
        payrule.setFinprodId(dto.getDlId());
        // 金融产品类型 = 接口.业务类型
        payrule.setFinprodType(dto.getBusiType());
        // 支付方式
        payrule.setPayType(DictCodeList.支付方式_现金.getValue());
        // 分支序号
        payrule.setBranch(InterfCommonConstant.分支序号_默认);
        listFinCashPayRule.add(payrule);

        /** 利息-现金流计划 **/
        FinCashPlan plan = new FinCashPlan();
        // 现金流代码
        plan.setCashId(cashId);
        // 现金流序号
        plan.setCashNum(BigDecimal.ZERO.longValue());
        // 未调整的计算开始日 = 接口.起息日
        plan.setVdateUnadjust(dto.getVdate());
        // 未调整的计算结束日 = 接口.到期日
        plan.setMdateUnadjust(dto.getMdate());
        // 调整后的计算开始日=接口.起息日
        plan.setVdate(dto.getVdate());
        // 调整后的计算结束日 = 接口.到期日
        plan.setMdate(dto.getMdate());
        // 调整后的计划支付日 = 接口.到期日
        plan.setPayDate(dto.getMdate());
        // 计息本金 = 接口.名义本金
        plan.setIntPrinAmt(dto.getCapital());
        // 计划支付利息 = 接口.到期利息
        plan.setIntAmt(dto.getmIntAmt());
        // 计划支付现金流 = 接口.到期类型
        plan.setCashAmt(dto.getmIntAmt());
        // 现金流单位
        plan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        // 金融产品代码 = 接口.拆借标的代码
        plan.setFinprodId(dto.getDlId());
        // 金融产品类型 = 接口.业务类型
        plan.setFinprodType(dto.getBusiType());
        // 支付方式
        plan.setPayType(DictCodeList.支付方式_现金.getValue());
        // 分支序号
        plan.setBranch(InterfCommonConstant.分支序号_默认);
        listFinCashPlan.add(plan);

        /** 本金-支付规则 **/
        payrule = new FinCashPayRule();
        cashId = InterfHelper.getMd5CashId_(dto.getDlId(), dto.getBusiType(),
                DictCodeListExt1.现金流二级类型_本金.getValue());
        // 现金流代码
        payrule.setCashId(cashId);
        // 起息日 = 接口.起息日
        payrule.setEffDate(dto.getVdate());
        // 频率
        payrule.setPayCycle(DictCodeList.频率_到期一次.getValue());
        // 营业日准则
//        payrule.setTdayRule(DictCodeList.营业日准则_不调整.getValue());
        // 推算方式
        payrule.setCalcRule(DictCodeList.推算方式_起息日向后.getValue());
        // 现金流单位
        payrule.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        // 金融产品代码= 接口.拆借标的代码
        payrule.setFinprodId(dto.getDlId());
        // 金融产品类型 = 接口.业务类型
        payrule.setFinprodType(dto.getBusiType());
        // 支付方式
        payrule.setPayType(DictCodeList.支付方式_现金.getValue());
        // 分支序号
        payrule.setBranch(InterfCommonConstant.分支序号_默认);
        listFinCashPayRule.add(payrule);

        /** 本金-现金流计划 **/
        plan = new FinCashPlan();
        // 现金流代码
        plan.setCashId(cashId);
        // 现金流序号
        plan.setCashNum(BigDecimal.ZERO.longValue());
        // 未调整的计算开始日 = 接口.起息日
        plan.setVdateUnadjust(dto.getVdate());
        // 未调整的计算结束日= 接口.到期日
        plan.setMdateUnadjust(dto.getMdate());
        // 调整后的计算开始日 = 接口.起息日
        plan.setVdate(dto.getVdate());
        // 调整后的计算结束日 = 接口.到期日
        plan.setMdate(dto.getMdate());
        // 调整后的计划支付日 = 接口.到期日
        plan.setPayDate(dto.getMdate());
        // 计划支付本金 = 接口.名义本金
        plan.setPrinAmt(dto.getCapital());
        // 是否支付利息
        plan.setIsPayInt(DictCodeList.是否_否.getValue());
        // 计划支付现金流 = = 接口.到期本金
        plan.setCashAmt(dto.getmPrinAmt());
        // 现金流单位
        plan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        // 金融产品代码 = 接口.拆借标的代码
        plan.setFinprodId(dto.getDlId());
        // 金融产品类型 = 接口.业务类型
        plan.setFinprodType(dto.getBusiType());
        // 支付方式
        plan.setPayType(DictCodeList.支付方式_现金.getValue());
        // 分支序号
        plan.setBranch(InterfCommonConstant.分支序号_默认);
        listFinCashPlan.add(plan);

        resultMap.put("FIN_CASH_PAY_RULE", listFinCashPayRule);
        resultMap.put("FIN_CASH_PLAN", listFinCashPlan);
        return resultMap;
    }

    /**
     * 交易日历映射关系
     * 
     * @param tradeMarket
     * @return
     */
    public String getCalendarId(String tradeMarket) {
        String calendarId = null;
        if (DictCodeListExt1.估值交易市场_银行间.getValue().equals(tradeMarket)) {
            calendarId = "03";
        }
        else if (DictCodeListExt1.估值交易市场_上交所.getValue().equals(tradeMarket)) {
            calendarId = "04";
        }
        else if (DictCodeListExt1.估值交易市场_深交所.getValue().equals(tradeMarket)) {
            calendarId = "05";
        }
        else {
            calendarId = "03";
        }
        return calendarId;
    }

    /**
     * 资金账户类型查询
     *
     * @param acctNo
     * @param prodId
     * @param ccy
     * @return
     * @see
     * @since 1.0
     */
    public List<BokBooksetCapacctInfo> selectCapAcctType(String acctNo, String prodId, String ccy) {
        Map<String, Object> params = new HashMap<String, Object>();

        // 产品代码、资金账户号查询对应的资金账户类型 查询
        params.put("prodId", prodId);
        params.put("acctNo", acctNo);
        params.put("ccy", ccy);
        return valInterfCommonMapper.selectCapAcctType(params);
    }

    /**
     * 分支序号生成-最新 mdate 计算结束日 branchType 分支类型
     */
    public Long getBranch(Date mdate, String branchType) {
        Long breanch = null;
        String breanchStr = new SimpleDateFormat("yyyyMMdd").format(mdate);
        if (DictCodeList.分支类型_应收本金.getValue().equals(branchType)) {
            breanchStr += "01";
        }
        else if (DictCodeList.分支类型_应收利息.getValue().equals(branchType)) {
            breanchStr += "02";
        }
        /** add by yanya on 20210309 增加行权的分支序号生成**/
        else if (DictCodeListExt3.分支类型_行权.getValue().equals(branchType)) {
            breanchStr += "03";
        }
        breanch = Long.valueOf(breanchStr);
        return breanch;
    }

    /**
     * 20181012 leipeng 撤销交易对应的原交易编号查询
     * 
     * @param trdProductDeal 金融产品交易Dto
     * @return tradeId 交易编号
     */
    public String getFTradeId(TrdProductDeal trdProductDeal) {
    	trdProductDeal.setBranch((trdProductDeal.getBranch() == null) ? 0 : trdProductDeal.getBranch());
        String tradeId = valInterfCommonMapper.getFTradeId(trdProductDeal);
        return tradeId;
    }
    /**
     * 认购确认交易对应的认购申请交易
     * @param trdProductDeal
     * @return
     */
    public String getBtradeId(TrdProductDeal trdProductDeal, String applyTradeId){
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("fTradeId",applyTradeId);
    	map.put("ps", DictCodeListExt2.交易方向_认购.getValue());
    	map.put("finprodId",trdProductDeal.getFinprodId());
    	map.put("finprodType",trdProductDeal.getFinprodType());
    	map.put("busiType",trdProductDeal.getBusiType());
    	String tradeId = valInterfCommonMapper.getBTradeId(map);
        return tradeId;
    }

    /***
     * 浮动计算固定利率 add by wujie on 20180910
     */
    public FinCashCalcRule getFinCashCalcRule(FinRateInfo finCashIntInfo) {
        // 现金流计算规则
        FinCashCalcRule valFinCashCalcRule = new FinCashCalcRule();
        // 现金流代码 =MD5(A.资金账户账号+数据字典：金融产品类型.资金账户+数据字典：现金流二级类型.利息)
        valFinCashCalcRule.setCashId(finCashIntInfo.getCashId());
        // 生效日期 =A.生效日期
        valFinCashCalcRule.setEffDate(finCashIntInfo.getEffDate());
        // 计算类型
        // 1.A.利率类型 eq 数据字典:利率类型.固定利率
        // 计算类型 = 数据字典：计算类型.固定利率
        // 2.A.利率类型 eq 数据字典:利率类型.浮动利率
        // 计算类型 = 数据字典：计算类型.浮动利率
        if (DictCodeList.利率类型_固定利率.getValue().equals(finCashIntInfo.getIntType())) {
            valFinCashCalcRule.setCalcType(DictCodeListExt1.计算类型_固定利率.getValue());
            // 生效利率 = A.初始利率
            valFinCashCalcRule.setYield(finCashIntInfo.getRate());

        }
        if (DictCodeList.利率类型_浮动利率.getValue().equals(finCashIntInfo.getIntType())) {
            valFinCashCalcRule.setCalcType(DictCodeListExt1.计算类型_浮动利率.getValue());
            // 基准类型 eq 利率
            if (DictCodeListExt1.基准类型_利率.getValue().equals(finCashIntInfo.getBenchmarkType())) {
                if (DictCodeListExt1.重置类型_随调.getValue().equals(finCashIntInfo.getResetType())) {
                    if (BigDecimal.ZERO.compareTo(finCashIntInfo.getObserveBefDay()) == 0) {
                        // 利率*系数+利差
                        BigDecimal yield = finCashIntInfo.getRate() == null ? BigDecimal.ZERO : finCashIntInfo.getRate();
                        yield = yield.multiply(
                            finCashIntInfo.getCoefficient() == null ? BigDecimal.ONE : finCashIntInfo.getCoefficient());
                        yield = yield.add(
                            finCashIntInfo.getSpreadRate() == null ? BigDecimal.ZERO : finCashIntInfo.getSpreadRate());
                        if (finCashIntInfo.getHighestRate() != null) {
                            if (yield.compareTo(finCashIntInfo.getHighestRate()) > 0) {
                                yield = finCashIntInfo.getHighestRate();
                            }
                        }
                        if (finCashIntInfo.getLowestRate() != null) {
                            if (yield.compareTo(finCashIntInfo.getLowestRate()) < 0) {
                                yield = finCashIntInfo.getLowestRate();
                            }
                        }
                        valFinCashCalcRule.setYield(yield);
                    }
                    else {
                        return null;
                    }
                }
                else {
                    return null;
                }
            }
            else {
                return null;
            }
        }
        
        //【一体化修改】：begin========================================================================= yangjian 2018/1/4
        //【一体化修改】：针对估值一体化变更【48：现金流计算规则表浮动利率编号删除】
          // 浮动利率编号 = A.基准利率编号
          //valFinCashCalcRule.setIntRateId(finCashIntInfo.getBenchmarkId());

        //【一体化修改】：end========================================================================= yangjian 2018/1/4
          
          // 计算基础= A.计息基础
          valFinCashCalcRule.setBasis(finCashIntInfo.getBasis());
          // //基数类型 =数据字典：基数类型.实收资本
          // valFinCashCalcRule.setBaseType(DictCodeList.基数类型_实收资本.getValue());
          // //基数日期类型=数据字典：基数日期类型.当日
          // valFinCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
          // 金融产品代码 = A.资金账户账号
          valFinCashCalcRule.setFinprodId(finCashIntInfo.getFinprodId());
          // 金融产品类型 = 数据字典：金融产品类型.资金账户
          valFinCashCalcRule.setFinprodType(finCashIntInfo.getFinprodType());
          // 分支序号 = 设置全局变量(默认为0）
          valFinCashCalcRule.setBranch(finCashIntInfo.getBranch());
          return valFinCashCalcRule;
      }

    /** 活期存款资金账户下挂利率行情更新 **/
    public List<FinCashCalcRule> getFinCashCalcRule(List<MstIntRateInfo> listMstIntRateInfo,
                                                    Date interfDate) {
        if (listMstIntRateInfo == null || listMstIntRateInfo.size() == 0) {
            return null;
        }
        /** add by wujie on 20181106 start remark:将list对象中非今日生效的数据过滤 **/
        List<MstIntRateInfo> list = new ArrayList<MstIntRateInfo>();
        for (MstIntRateInfo obj : listMstIntRateInfo) {
            if (DateUtil.format(interfDate).equals(DateUtil.format(obj.getEffDate()))) {
                list.add(obj);
            }
        }
        if (list.size() == 0) {
            return null;
        }
        /** add by wujie on 20181106 end remark:将list对象中非今日生效的数据过滤 **/
        /**
         * 查询现金流利率信息表
         */
        Map<String, Object> paramMap = new HashMap<String, Object>();
        // paramMap.put("benchmarkType", DictCodeListExt1.基准类型_利率.getValue());
        // paramMap.put("resetType", DictCodeListExt1.重置类型_随调.getValue());
        // paramMap.put("preObsNum", BigDecimal.ZERO);
        paramMap.put("finprodType", DictCodeListExt1.金融产品类型_资金账户.getValue());
        paramMap.put("branch", InterfCommonConstant.分支序号_默认);
        paramMap.put("interfDate", interfDate);

        List<FinRateInfo> listFinCashIntInfo = valInterfCommonMapper.queryFinCashIntInfo(
            paramMap);
        if (listFinCashIntInfo == null || listFinCashIntInfo.size() == 0) {
            return null;
        }

        /** 取生效日最新的基准利率数据 **/
        Map<String, FinRateInfo> qmap = new HashMap<String, FinRateInfo>();
        for (FinRateInfo obj : listFinCashIntInfo) {
            if (DictCodeList.利率类型_固定利率.getValue().equals(obj.getIntType())) {
                continue;
            }
            /** add by wujie on 20181105 start remark：最新生效日必须小于等于接口跑批日 **/
            if (obj.getEffDate().getTime() > interfDate.getTime()) {
                continue;
            }
            /** add by wujie on 20181105 end **/
            FinRateInfo q = qmap.get(obj.getCashId());
            if (q == null) {
                q = obj;
            }
            else {
                if (q.getEffDate().getTime() < obj.getEffDate().getTime()) {
                    q = obj;
                }
            }
            qmap.put(obj.getCashId(), q);
        }

        listFinCashIntInfo = new ArrayList<FinRateInfo>();
        for (String key : qmap.keySet()) {
            listFinCashIntInfo.add(qmap.get(key));
        }

        /** 现金流利率信息表本次需要更新至现金流计算规则表中的数据 **/
        Map<String, List<FinRateInfo>> map = new HashMap<String, List<FinRateInfo>>();
        for (FinRateInfo obj : listFinCashIntInfo) {
            List<FinRateInfo> l = map.get(obj.getBenchmarkId());
            if (l == null) {
                l = new ArrayList<FinRateInfo>();
            }
            l.add(obj);
            map.put(obj.getBenchmarkId(), l);
        }
        List<FinCashCalcRule> rstList = new ArrayList<FinCashCalcRule>();
        for (MstIntRateInfo obj : list) {
            List<FinRateInfo> l = map.get(obj.getIntRateId());
            if (l == null || l.size() == 0) {
                continue;
            }
            else {
                for (FinRateInfo o : l) {
                    o.setEffDate(obj.getEffDate());
                    o.setRate(obj.getIntRate());
                    FinCashCalcRule finCashCalcRule = this.getFinCashCalcRule(o);
                    if (finCashCalcRule != null) {
                        rstList.add(finCashCalcRule);
                    }
                }
                map.put(obj.getIntRateId(), l);
            }
        }
        return rstList;
    }

    /**
     * 按规则生成回购期限品种
     */
    public static String getRepTermType(String finprodType, long termDay) {
        String termType = null;
        if (DictCodeListExt1.金融产品类型_债券质押式正回购.getValue().equals(finprodType)
            || DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue().equals(finprodType)
            || DictCodeListExt1.金融产品类型_债券买断式正回购.getValue().equals(finprodType)
            || DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(finprodType)) {
            termType = DictCodeList.期限品种_回购_未知.getValue();
            if (termDay == 1) {
                termType = DictCodeList.期限品种_回购_1天.getValue();
            }
            else if (termDay > 1 && termDay <= 7) {
                termType = DictCodeList.期限品种_回购_7天.getValue();
            }
            else if (termDay > 7 && termDay <= 14) {
                termType = DictCodeList.期限品种_回购_14天.getValue();
            }
            else if (termDay > 14 && termDay <= 21) {
                termType = DictCodeList.期限品种_回购_21天.getValue();
            }
            else if (termDay > 21 && termDay <= 30) {
                termType = DictCodeList.期限品种_回购_1个月.getValue();
            }
            else if (termDay > 30 && termDay <= 60) {
                termType = DictCodeList.期限品种_回购_2个月.getValue();
            }
            else if (termDay > 60 && termDay <= 91) {
                termType = DictCodeList.期限品种_回购_3个月.getValue();
            }
            else if (termDay > 91 && termDay <= 121) {
                termType = DictCodeList.期限品种_回购_4个月.getValue();
            }
            else if (termDay > 121 && termDay <= 182) {
                termType = DictCodeList.期限品种_回购_6个月.getValue();
            }
            else if (termDay > 182 && termDay <= 273) {
                termType = DictCodeList.期限品种_回购_9个月.getValue();
            }
            else if (termDay > 273 && termDay <= 365) {
                termType = DictCodeList.期限品种_回购_1年.getValue();
            }
            else {
                termType = DictCodeList.期限品种_回购_未知.getValue();
            }
        }
        else if (DictCodeListExt1.金融产品类型_拆入.getValue().equals(finprodType)
                 || DictCodeListExt1.金融产品类型_拆出.getValue().equals(finprodType)
                 || DictCodeListExt1.金融产品类型_同业借款.getValue().equals(finprodType)) {
            if (termDay == 1) {
                termType = DictCodeList.期限品种_内部拆借_隔夜.getValue();
            }
            else if (termDay > 1 && termDay <= 7) {
                termType = DictCodeList.期限品种_内部拆借_7天.getValue();
            }
            else if (termDay > 7 && termDay <= 14) {
                termType = DictCodeList.期限品种_内部拆借_14天.getValue();
            }
            else if (termDay > 14 && termDay <= 21) {
                termType = DictCodeList.期限品种_内部拆借_21天.getValue();
            }
            else if (termDay > 21 && termDay <= 30) {
                termType = DictCodeList.期限品种_内部拆借_1个月.getValue();
            }
            else if (termDay > 30 && termDay <= 60) {
                termType = DictCodeList.期限品种_内部拆借_2个月.getValue();
            }
            else if (termDay > 60 && termDay <= 91) {
                termType = DictCodeList.期限品种_内部拆借_3个月.getValue();
            }
            else if (termDay > 91 && termDay <= 182) {
                termType = DictCodeList.期限品种_内部拆借_6个月.getValue();
            }
            else if (termDay > 182 && termDay <= 273) {
                termType = DictCodeList.期限品种_内部拆借_9个月.getValue();
            }
            else if (termDay > 273 && termDay <= 365) {
                termType = DictCodeList.期限品种_内部拆借_12个月.getValue();
            }
            else {
                termType = DictCodeList.期限品种_内部拆借_1年以上.getValue();
            }
        }

        return termType;
    }

    /**
     * 债券行权现金流信息生成 listFinExercisePlan List<行权计划dto>
     */
    public List<FinCashInfo> secVestFinCashInfoInit(List<FinExercisePlan> listFinExercisePlan) {
        if (listFinExercisePlan == null || listFinExercisePlan.isEmpty()) {
            return null;
        }

        List<FinCashInfo> listFinCashInfo = new ArrayList<>();
        Map<String, String> secMap = new HashMap<>();
        listFinExercisePlan.forEach(i -> secMap.put(i.getFinprodId(), i.getFinprodId()));
        for (String key : secMap.keySet()) {
            FinCashInfo info = new FinCashInfo();
            /**modify by wujie on 20190121 start
            info.setCashId(
                InterfHelper.getMd5CashId_(secMap.get(key) + DictCodeListExt1.金融产品类型_债券.getValue()
                                           + DictCodeListExt1.现金流二级类型_行权.getValue()));
                                           **/
            info.setCashId(
                    InterfHelper.getMd5CashId_(secMap.get(key), DictCodeListExt1.金融产品类型_债券.getValue()
                                               , DictCodeListExt1.现金流二级类型_本金.getValue()));
            /**modify by wujie on 20190121 end**/
            
            /**modify by wujie on 20190121 start
            info.setCashType1(DictCodeListExt1.现金流一级类型_行权.getValue());
            info.setCashType2(DictCodeListExt1.现金流二级类型_行权.getValue());
            **/
            info.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
            info.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
            /**modify by wujie on 20190121 end**/
            
            info.setFinprodId(secMap.get(key));
            info.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
            info.setBranch(InterfCommonConstant.分支序号_默认);
            listFinCashInfo.add(info);
        }
        return listFinCashInfo;
    }

    /**
     * 已有产品信息查询
     * 
     * @return 分层产品Dto
     */
    public Map<String, LayerPrdInfo> getExistPrdInfo() {
        Map<String, LayerPrdInfo> mapLayerPrdInfo = new HashMap<>();
        List<LayerPrdInfo> listLayerPrdInfo = valInterfCommonMapper.queryLayerPrdInfo();
        if (listLayerPrdInfo != null && !listLayerPrdInfo.isEmpty()) {
            /**modify by wujie on 20190103 start
             * listLayerPrdInfo.forEach(i -> mapLayerPrdInfo.put(i.getFinprodId(), i));**/
            for(LayerPrdInfo lay :listLayerPrdInfo){
            	mapLayerPrdInfo.put(lay.getFinprodId()+
            			FinalString.LAYERING_SPLIT+
            			lay.getFinprodType(), lay);
            }
            /**modify by wujie on 20190103 end**/
        }
        return mapLayerPrdInfo;
    }

    /**
     * 分层结构产品账套信息初始化
     * 
     * @param finProductExt 标的类金融产品扩展信息表Dto finProduct 标的类金融产品信息Dto
     * modify by wujie on 20181220 remark:增加参数otherParams
     */
    public BooksetInitResult getParStrucProduct(FinProduct finProduct,
                                                ValFinProductExt finProductExt, OtherParamsDto otherParams) {
        // 核算主体信息dto
        BokAcctSubjectInfo acctSubjectInfo = new BokAcctSubjectInfo();

        // 定义 变量.核算主体名称 = prodinfo.金融产品简称
        String acctSubjectName = finProduct.getFinprodAbbr();
        if (finProduct.getFinprodAbbr() == null || "".equals(finProduct.getFinprodAbbr())) {
            acctSubjectName = finProduct.getFinprodName();
        }
        // 账套信息map
        BooksetInitResult booksetInitResult = new BooksetInitResult();
        // 账套模板代码
        String bokTemplateId = null;
        // 账务明细代码表
        BokDetailId bokDetailId = new BokDetailId();
        // 初始化核算主体dto
        // 核算主体代码==金融产品代码
        acctSubjectInfo.setAcctSubjectId(finProduct.getFinprodId());
        // 核算主体类别==金融产品类型
        acctSubjectInfo.setAcctSubjectType(finProduct.getFinprodType());
        // 核算主体名称=变量.核算主体名称
        acctSubjectInfo.setAcctSubjectName(acctSubjectName);
        String finprodId = finProduct.getFinprodId();
        if (!finProduct.getFinprodId().startsWith("F16_")) {
            finprodId = "F16_".concat(finprodId);
        }
        FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(finprodId);
        /**
         * 1.初始化账套信息
         */
        // add by weipeng on 20220408：会计引擎升级改造，若按照内部科目编码分录，则需要对应新会计引擎模板 start
        // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录
        String accountEntryType =  CacheUtil.getSysParam("ACCOUNT_ENTRY_TYPE");
        //调用service方法进行逻辑逻辑
        if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
            if (finProductExt.getIsLayerAccount().equals(DictCodeList.是否_否.getValue())) {
                bokTemplateId = InterfCommonConstant.BOOKSETID_NEW;
            }
            else {
                if (DictCodeList.收益类型_收益型.getValue().equals(finProduct.getProfitType())) {
                    bokTemplateId = InterfCommonConstant.BOOKSETID_NEW;
                    if(finProductAdd!=null && DictCodeList.是否_是.getValue().equals(finProductAdd.getIsLay())){
                        bokTemplateId = InterfCommonConstant.BOOKSETID_LAYERING_NOR_NEW;
                    }
                }
                else {
                    bokTemplateId = InterfCommonConstant.BOOKSETID_LAYERING_RI_NEW;
                }
                //        	bokTemplateId = InterfCommonConstant.BOOKSETID_LAYERING_NOR;
            }
        }
        else{
            if (finProductExt.getIsLayerAccount().equals(DictCodeList.是否_否.getValue())) {
                bokTemplateId = InterfCommonConstant.BOOKSETID;
            }
            else {
                if (DictCodeList.收益类型_收益型.getValue().equals(finProduct.getProfitType())) {
                    bokTemplateId = InterfCommonConstant.BOOKSETID;
                    if(finProductAdd!=null && DictCodeList.是否_是.getValue().equals(finProductAdd.getIsLay())){
                        bokTemplateId = InterfCommonConstant.BOOKSETID_LAYERING_NOR;
                    }
                }
                else {
                    bokTemplateId = InterfCommonConstant.BOOKSETID_LAYERING_RI;
                }
                //        	bokTemplateId = InterfCommonConstant.BOOKSETID_LAYERING_NOR;
            }
        }
        // add by weipeng on 20220408：会计引擎升级改造，若按照内部科目编码分录，则需要对应新会计引擎模板 end

        otherParams.setTmp_booksetId(bokTemplateId);
        booksetInitResult = booksetInit(finProduct, finProductExt, otherParams);
        /**
         * 2.初始化账务明细代码表
         */
        // 账套代码==变量
        bokDetailId.setBooksetId(booksetInitResult.getValBokBookset().getBooksetId());
        // 业务明细代码==金融产品代码
        bokDetailId.setBusiId(finProduct.getFinprodId());
        // 业务明细类型
        bokDetailId.setBusiDetailType(finProduct.getFinprodType());

        Long branch = null;
        String layeringId = finProduct.getFinprodId();
        String layeringName = finProduct.getFinprodName();
        String detailSubjectNoTail = finProduct.getFinprodId();
        if (DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(finProduct.getFinprodType())) {
            branch = Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(finProduct.getVdate()));
            layeringId += branch;
            layeringName += branch;
            detailSubjectNoTail = String.valueOf(branch);
        }
        bokDetailId.setBranch(branch);

        // 账务明细代码==MD5("1"+输入.prodinfo.金融产品代码+"2"+数据字典：金融产品类型.理财产品+"3"+分支序号)
        bokDetailId.setBokDetailId(genMd5BokDetailId(bokDetailId));
        // 四级科目尾号==金融产品代码
        /**modify by wujie on 20190103 start
        bokDetailId.setDetailSubjectNoTail(detailSubjectNoTail);
        **/
        bokDetailId.setDetailSubjectNoTail(detailSubjectNoTail+
        		FinalString.LAYERING_SPLIT+
        		finProduct.getFinprodType());
        /**modify by wujie on 20190103 end**/
        // 四级科目尾号名称==金融产品简称
        bokDetailId.setDetailSubnameTail(finProduct.getFinprodAbbr());

        // if (DictCodeList.是否_是.getValue().equals(finProductExt.getIsLayerAccount())) {
        BokAcctLayeringInfo acctLayeringInfo = new BokAcctLayeringInfo();
        // 账套=变量.分层信息.账套代码
        acctLayeringInfo.setBooksetId(bokDetailId.getBooksetId());
        // 账务明细代码
        acctLayeringInfo.setBokDetailId(bokDetailId.getBokDetailId());
        // 业务明细代码
        acctLayeringInfo.setBusiId(finProduct.getFinprodId());
        // 业务明细类型
        acctLayeringInfo.setBusiDetailType(finProduct.getFinprodType());
        // 分层代码
        
        /** modify by wujie on 20190103 start
        acctLayeringInfo.setLayeringId(layeringId);
        **/
        acctLayeringInfo.setLayeringId(layeringId+ FinalString.LAYERING_SPLIT+finProduct.getFinprodType());
        /**modify by wujie on 20190103 end**/
        
        // 分层名称
        acctLayeringInfo.setLayeringName(layeringName);
        // 上层代码
        acctLayeringInfo.setfLayeringId(null);
        // 分支序号
        acctLayeringInfo.setBranch(branch);
        // 起息日 = 输入.prodinfo.起息日
        acctLayeringInfo.setVdate(finProduct.getVdate());
        
        /**modify by wujie on 20190114 start
        // 到期日 = 输入.prodinfo.到期日
        acctLayeringInfo.setMdate(finProduct.getMdate());
        **/
        // 到期日 = 输入.prodinfo.实际到期日
        acctLayeringInfo.setMdate(finProduct.getActmdate() != null ? finProduct.getActmdate() : finProduct.getMdate());
        /**modify by wujie on 20190114 end**/
        
        // 交易日历 = 输入.prodinfo.交易日历
        acctLayeringInfo.setCalendarId(finProduct.getCalendarId());

        booksetInitResult.setAcctLayeringInfo(acctLayeringInfo);
        /**
         * 返回参数设置
         */
        booksetInitResult.setAcctSubjectInfo(acctSubjectInfo);
        booksetInitResult.setBokDetailId(bokDetailId);

        return booksetInitResult;
    }

    /**
     * 查询子产品的父产品集合
     * 
     * @param prodId
     * @return
     * @see
     * @since 1.0
     */
    public List<LayerPrdInfo> querryPfather(String prodId, String finprodType, Map<String, LayerPrdInfo> prdInfoMap) {
        // 父产品集合List
        List<LayerPrdInfo> layerPrdInfoList = new ArrayList<>();
        LayerPrdInfo prdLayerInfoDto = new LayerPrdInfo();
        /**modify by wujie on 20190103 start
        prdLayerInfoDto = prdInfoMap.get(prodId);
        **/
        prdLayerInfoDto = prdInfoMap.get(prodId+
        		FinalString.LAYERING_SPLIT+
        		finprodType);
        /**modify by wujie on 20190103 end**/
        
        if (StringUtil.isNotEmpty(prdLayerInfoDto.getpFinprodId())) {
            while (StringUtil.isNotEmpty(prdLayerInfoDto.getpFinprodId())) {
            	/**modify by wujie on 20190103 start
                prdLayerInfoDto = prdInfoMap.get(prdLayerInfoDto.getpFinprodId());
                **/
            	prdLayerInfoDto = prdInfoMap.get(prdLayerInfoDto.getpFinprodId()+
            			FinalString.LAYERING_SPLIT+
            			prdLayerInfoDto.getfLayeringType());
            	/**modify by wujie on 20190103 end**/
                layerPrdInfoList.add(prdLayerInfoDto);
            }
        }
        return layerPrdInfoList;
    }

    /**
     * 分层结构子产品分层信息初始化
     * 
     * @param prdLayerInfoList 本次新增分层结构产品
     * @param prdInfoMap 全量产品信息（投资组合、理财产品、理财子产品）
     * @param listBokBookset 本次新增账套信息
     * @param acctalyeringInfos 本次新增核算主体分层信息
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> getStrucSubProInit(List<LayerPrdInfo> prdLayerInfoList,
                                                  Map<String, LayerPrdInfo> prdInfoMap,
                                                  List<BokBookset> listBokBookset,
                                                  List<BokAcctLayeringInfo> acctalyeringInfos) {
        // 初始化结果集map
        Map<String, Object> resultMap = new HashMap<String, Object>();
        // 账务明细代码list
        List<BokDetailId> bokDetailIdList = new ArrayList<BokDetailId>();
        // 核算分层信息list
        List<BokAcctLayeringInfo> acctLayeringInfolist = new ArrayList<BokAcctLayeringInfo>();
        for (LayerPrdInfo prdLayerInfo : prdLayerInfoList) {
        	/**add by wujie on 20190103 start**/
        	//金融产品类型：非投资组合  
        	//父产品代码：不为空
        	if(!DictCodeListExt1.金融产品类型_投资组合.getValue().equals(prdLayerInfo.getFinprodType())
            		&& StringUtil.isNotEmpty(prdLayerInfo.getpFinprodId())){
        		String key = prdLayerInfo.getpFinprodId()+
        				FinalString.LAYERING_SPLIT+
        				DictCodeListExt1.金融产品类型_理财产品.getValue();
            	String fLayeringType = null;
            	if(prdInfoMap.get(key)!=null){
            		fLayeringType = prdInfoMap.get(key).getFinprodType();
            	}
            	if(fLayeringType==null){
            		key = prdLayerInfo.getpFinprodId()+
            				FinalString.LAYERING_SPLIT+
            				DictCodeListExt1.金融产品类型_理财子产品.getValue();
            		if(prdInfoMap.get(key)!=null){
                		fLayeringType = prdInfoMap.get(key).getFinprodType();
                	}
            	}
            	prdLayerInfo.setfLayeringType(fLayeringType);
            }
        	/**add by wujie on 20190103 end**/
            // 父产品集合
            List<LayerPrdInfo> layerPrdInfoList = new ArrayList<>();
            /**
             * 1.查询子产品的父产品集合
             */
            // 父产品集合
            String prodId = prdLayerInfo.getFinprodId();
            List<LayerPrdInfo> fatcherProductList = querryPfather(prodId,prdLayerInfo.getFinprodType(),prdInfoMap);
            if (fatcherProductList != null && fatcherProductList.size() > 0) {
                layerPrdInfoList.addAll(fatcherProductList);
            }
            /**
             * 2.查询账套信息
             */
            Map<String, BokBookset> bokBooksetMap = new HashMap<String, BokBookset>();

            Map<String, Object> map = new HashMap<>();
            /**add by wujie on 20190103 start**/
//            map.put("prodId", prdLayerInfo.getFinprodId());
            map.put("layeringId", prdLayerInfo.getLayeringId());
            /**add by wujie on 20190103 end**/
            
            if (StringUtils.isNotEmpty(prdLayerInfo.getPortfolioId())) {
                LayerPrdInfo tzzh = new LayerPrdInfo();
                tzzh.setFinprodId(prdLayerInfo.getPortfolioId());
                /**add by wujie on 20190103 start**/
                tzzh.setLayeringId(prdLayerInfo.getPortfolioId()+
                		FinalString.LAYERING_SPLIT+
                		DictCodeListExt1.金融产品类型_投资组合.getValue());
                tzzh.setFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                /**add by wujie on 20190103 end**/
                layerPrdInfoList.add(tzzh);
            }
            map.put("layerPrdInfoList", layerPrdInfoList);
            if (CollectionUtils.isNotEmpty(layerPrdInfoList)) {
                List<BokBookset> bokBooksetList = valInterfCommonMapper.querryBokinfo(map);
                if (CollectionUtils.isNotEmpty(bokBooksetList)) {
                    for (BokBookset obj : bokBooksetList) {
                        bokBooksetMap.put(obj.getBooksetId(), obj);
                    }
                }
            }
            // 本次新增的账套
            if (CollectionUtils.isNotEmpty(listBokBookset)) {
                for (BokBookset obj : listBokBookset) {
                    if (obj.getAcctSubjectId().equals(prodId) 
                    		&& obj.getAcctSubjectType().equals(prdLayerInfo.getFinprodType())) {
                        break;
                    }
                    for (LayerPrdInfo layer : layerPrdInfoList) {
                        if (layer.getFinprodId().equals(obj.getAcctSubjectId())) {
                            bokBooksetMap.put(obj.getBooksetId(), obj);
                            break;
                        }
                    }
                }
            }
            // 初始化核算分层信息、账务明细代码表
            for (String key : bokBooksetMap.keySet()) {
                // 1.核算分层信息
                // 定义 变量.账套Dto
                BokBookset bokBookset = bokBooksetMap.get(key);

                if (prdLayerInfo.getFinprodId().equals(bokBookset.getAcctSubjectId()) 
                		&& prdLayerInfo.getFinprodType().equals(bokBookset.getAcctSubjectType())) {
                    continue;
                }
                // 业务明细代码
                String busiId = prdLayerInfo.getFinprodId();
                // 业务明细类型
                String busiDetailType = prdLayerInfo.getFinprodType();
                // 四级科目尾号
                String detailSubjectNoTail = prdLayerInfo.getFinprodId();
                // 四级科目尾号名称
                String detailSubnameTail = prdLayerInfo.getFinprodName();
                // 上层代码
                /**modify by wujie on 20190103 start
                String fLayeringId = prdLayerInfo.getpFinprodId();
                **/
                String fLayeringId = prdLayerInfo.getpFinprodId()+
                		FinalString.LAYERING_SPLIT+
                		prdLayerInfo.getfLayeringType();
                /**modify by wujie on 20190103 end**/
                // 分层代码
                /** modify by wujie on 20190103 start
                String layeringId = prdLayerInfo.getFinprodId();
                **/
                String layeringId = prdLayerInfo.getLayeringId();
                /**modify by wujie on 20190103 end**/
                // 分支序号
                Long branch = InterfCommonConstant.分支序号_默认;
                // 分层名称 = 金融产品名称
                String layeringName = prdLayerInfo.getFinprodName();

                if (DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(prdLayerInfo.getFinprodType())) {
                	/**modify by dongjun on 20190404 分支序号 改造 start**/
                    // 分支序号
                	if(prdLayerInfo.getBranch() != null && prdLayerInfo.getBranch() != 0L) {
                		branch = prdLayerInfo.getBranch();
                	} else{
                        branch = Long.valueOf(
                                new SimpleDateFormat("yyyyMMdd").format(prdLayerInfo.getVdate()));
                	}
                	/**modify by dongjun on 20190404 分支序号 改造 end**/;
                    detailSubjectNoTail = prdLayerInfo.getpFinprodId() + branch;
                    busiId = prdLayerInfo.getpFinprodId();
                    layeringName += branch;
                }
                else {
                    if (!StringUtil.isNotEmpty(prdLayerInfo.getpFinprodId())) {
                        if (StringUtil.isNotEmpty(prdLayerInfo.getPortfolioId())) {
                            // 上层代码=输入.prdLayerInfo.投资组合代码
                            fLayeringId = prdLayerInfo.getPortfolioId();
                            /**add by wujie on 20190103 start**/
                            fLayeringId = fLayeringId+
                            		FinalString.LAYERING_SPLIT+
                            		DictCodeListExt1.金融产品类型_投资组合.getValue();
                            /**add by wujie on 20190103 end**/
                        }
                    }
                    else {
                        // 上层代码=输入.prdLayerInfo.母金融产品代码
                        fLayeringId = prdLayerInfo.getpFinprodId();
                        /**add by wujie on 20190103 start**/
                        fLayeringId = fLayeringId+
                        		FinalString.LAYERING_SPLIT+
                        		prdLayerInfo.getfLayeringType();
                        /**add by wujie on 20190103 end**/
                    }
                }
                /** 账务明细表初始化 START **/
                BokDetailId bokDetailId = new BokDetailId();
                // 账套代码=变量DTO.帐套代码
                bokDetailId.setBooksetId(bokBookset.getBooksetId());
                // 业务明细代码=输入.prdLayerInfo.金融产品代码
                bokDetailId.setBusiId(busiId);
                // 业务明细类型=输入.prdLayerInfo.金融产品类型
                bokDetailId.setBusiDetailType(busiDetailType);
                // 分支序号
                bokDetailId.setBranch(branch);

                // 账务明细代码=MD5("1"+输入.prdLayerInfo.金融产品代码+"2"+输入.prdLayerInfo.金融产品类型+“3”+分支序号)
                bokDetailId.setBokDetailId(genMd5BokDetailId(bokDetailId));
                // 四级科目尾号=输入.prdLayerInfo.金融产品代码
                
                /**
                 * modify by wujie on 20190103 start
                bokDetailId.setDetailSubjectNoTail(detailSubjectNoTail);
                **/
                bokDetailId.setDetailSubjectNoTail(detailSubjectNoTail+
                		FinalString.LAYERING_SPLIT+
                		prdLayerInfo.getFinprodType());
                /**modify by wujie on 20190103 end**/
                
                // 四级科目尾号名称=输入.prdLayerInfo.金融产品名称
                bokDetailId.setDetailSubnameTail(detailSubnameTail);
                // 初始化结果
                bokDetailIdList.add(bokDetailId);
                /** 账务明细表初始化 END **/

                /** 核算分层信息初始化 START **/
                BokAcctLayeringInfo acctLayeringInfo = new BokAcctLayeringInfo();
                // 账套=变量.分层信息.账套代码
                acctLayeringInfo.setBooksetId(bokBookset.getBooksetId());
                // 账务明细代码
                acctLayeringInfo.setBokDetailId(bokDetailId.getBokDetailId());
                // 业务明细代码
                acctLayeringInfo.setBusiId(busiId);
                // 业务明细类型
                acctLayeringInfo.setBusiDetailType(busiDetailType);
                // 分层代码
                acctLayeringInfo.setLayeringId(layeringId);
                // 分层名称
                acctLayeringInfo.setLayeringName(layeringName);
                // 上层代码
                acctLayeringInfo.setfLayeringId(fLayeringId);
                // 分支序号
                acctLayeringInfo.setBranch(branch);
                // 起息日 = 输入.prodinfo.起息日
                acctLayeringInfo.setVdate(prdLayerInfo.getVdate());
                
                /**modify by wujie on 20190114 start
                // 到期日 = 输入.prodinfo.到期日
                acctLayeringInfo.setMdate(prdLayerInfo.getMdate());
                **/
                // 到期日 = 输入.prodinfo.到期日
                acctLayeringInfo.setMdate(prdLayerInfo.getActmdate() != null ? prdLayerInfo.getActmdate() : prdLayerInfo.getMdate());
                /**modify by wujie on 20190114 end**/
                
                // 交易日历 = 输入.prodinfo.交易日历
                acctLayeringInfo.setCalendarId(prdLayerInfo.getCalendarId());

                // 初始化结果
                acctLayeringInfolist.add(acctLayeringInfo);

                /** 核算分层信息初始化 END **/
            }
        }
        // 返回 变量.初始化结果集Map
        if (CollectionUtils.isNotEmpty(acctalyeringInfos)) {
            acctLayeringInfolist.addAll(acctalyeringInfos);
        }
        resultMap.put("ACCT_LAYERING_INFO", initBokAcctLayeringInfo(acctLayeringInfolist));
        resultMap.put("BOK_DETAIL_ID", bokDetailIdList);
        return resultMap;
    }

    public List<BokAcctLayeringInfo> initBokAcctLayeringInfo(List<BokAcctLayeringInfo> list) {
        List<BokAcctLayeringInfo> rstList = new ArrayList<BokAcctLayeringInfo>();
        if (CollectionUtils.isEmpty(list)) {
            return rstList;
        }
        Map<String, List<BokAcctLayeringInfo>> map = new HashMap<String, List<BokAcctLayeringInfo>>();
        for (BokAcctLayeringInfo o : list) {
            List<BokAcctLayeringInfo> olist = map.get(o.getBooksetId());
            if (olist == null) {
                olist = new ArrayList<BokAcctLayeringInfo>();
            }
            olist.add(o);
            map.put(o.getBooksetId(), olist);
        }
        for (String key : map.keySet()) {
            List<BokAcctLayeringInfo> nlist = map.get(key);
            // 根据账套查询已存在的分层信息
            List<BokAcctLayeringInfo> klist = valInterfCommonMapper.queryAcctLayeringInfoByBoksetId(
                key);
            if (CollectionUtils.isNotEmpty(klist)) {

                Map<String, BokAcctLayeringInfo> mapN = new HashMap<String, BokAcctLayeringInfo>();
                for (BokAcctLayeringInfo n : nlist) {
                    mapN.put(n.getLayeringId(), n);
                }
                for (BokAcctLayeringInfo k : klist) {
                    if (mapN.get(k.getLayeringId()) == null) {
                        nlist.add(k);
                    }
                }
            }
            map.put(key, setAcctLayerTree(nlist));
        }
        for (String key : map.keySet()) {
            List<BokAcctLayeringInfo> klist = map.get(key);
            if (CollectionUtils.isNotEmpty(klist)) {
                rstList.addAll(klist);
            }
        }
        return rstList;
    }

    /**
     * 初始化级别序号、当前级别、下级最新序号值
     * 
     * @param list
     */
    @SuppressWarnings("unchecked")
    public List<BokAcctLayeringInfo> setAcctLayerTree(List<BokAcctLayeringInfo> list) {
        List<BokAcctLayeringInfo> klist = null;
        if (CollectionUtils.isEmpty(list)) {
            return klist;
        }
        klist = new ArrayList<BokAcctLayeringInfo>();
        Map<String, BokAcctLayeringInfo> map = new HashMap<String, BokAcctLayeringInfo>();

        List<BokAcctLayeringInfo> roots = new ArrayList<BokAcctLayeringInfo>();
        for (BokAcctLayeringInfo o : list) {
            boolean isRoot = true;
            if (StringUtil.isNotEmpty(o.getfLayeringId())) {
                for (BokAcctLayeringInfo c : list) {
                    if (o.getfLayeringId().equals(c.getLayeringId())) {
                        isRoot = false;
                        break;
                    }
                }
            }
            if (isRoot) {
                roots.add(o);
                map.put(o.getBooksetId() + o.getLayeringId(), o);
            }
        }
        if (roots.size() == 0) {
            return klist;
        }
        for (BokAcctLayeringInfo o : roots) {
            String key = o.getBooksetId() + o.getLayeringId();
            BokAcctLayeringInfo keymap = map.get(key);
            keymap.setCurLev(1L);
            keymap.setLev("1");
            keymap.setsBranch(0L);
            map.put(key, keymap);
        }

        List<BokAcctLayeringInfo> notRoots = (List<BokAcctLayeringInfo>) CollectionUtils.subtract(
            list, roots);
        while (notRoots != null && notRoots.size() > 0) {
            List<BokAcctLayeringInfo> sz = new ArrayList<BokAcctLayeringInfo>();
            for (BokAcctLayeringInfo o : notRoots) {
                String key = o.getBooksetId() + o.getLayeringId();
                String fkey = o.getBooksetId() + o.getfLayeringId();
                BokAcctLayeringInfo fp = map.get(fkey);
                if (fp == null) {
                    sz.add(o);
                    continue;
                }
                fp.setsBranch(fp.getsBranch() == null ? 0L : fp.getsBranch() + 1);

                o.setCurLev(fp.getCurLev() + 1);
                o.setLev(fp.getLev() + "-" + fp.getsBranch());
                o.setsBranch(o.getsBranch() == null ? 0 : o.getsBranch());

                map.put(key, o);
                map.put(fkey, fp);
            }
            notRoots = sz;
        }
        for (String key : map.keySet()) {
            klist.add(map.get(key));
        }
        return klist;
    }

    /**
     * 账套信息(根据产品代码) _分层产品
     * 
     * @param prodid
     * @return List<BokBookset>
     */
    public List<ValBooksetInfoDto> getBokBooksetByProdId_Layering(String prodId) {
        // 账套信息List
        List<ValBooksetInfoDto> list = new ArrayList<ValBooksetInfoDto>();
        list = mapper.getBooksetInfoLayering(prodId);
        return list;
    }

    /**
     * 交易入账_分层产品
     * 
     * @param trdProductDeal 理财产品代码prodId
     * @return Map<String,Object>
     */
    public InterfProductDealBookCommonDto dealAccounting_Layering(TrdProductDeal trdProductDeal,
                                                                  String prodId, Date prodVdate,
                                                                  String prodFinprodType) {
    	Long breach = null;
    	if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(prodFinprodType)){
        	breach = Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(prodVdate));
        }
    	return dealAccounting_Layering_Branch(trdProductDeal,prodId,breach,prodFinprodType,null, null);
    }

    /**
     * 交易入账_银行账
     * @param trdProductDeal 金融产品交易
     * @param prodId 理财产品代码
     * @param prodFinprodType 金融产品类型
     * @param branch 分支序号 非必输项
     * @return
     */
    public InterfProductDealBookCommonDto dealAccountingBank(TrdProductDeal trdProductDeal,
                                                             String prodId, String prodFinprodType,
                                                             Long branch) {
        // 1.2 定义变量
        // 定义 变量.银行账套代码 = BANK0001
        String bankBooksetId = InterfCommonConstant.BOOKSETID_BANK;
        // 定义 变量.入账信息List
        List<BokDealAccounting> listBokDealAccounting = new ArrayList<>();
        // 定义 变量.入账账务明细List
        List<BokDealAccountingDetail> listBokDealAccountingDetail = new ArrayList<>();
        /**modify by wujie on 20190416
        // 定义 变量.账套日期信息Dto
        BokBooksetDate bokBooksetDateDto = null;
        **/
        // 定义 变量.银行账场景配置List                                                                 
        List<BokBankSceConfig> listBokBankSceConfig = new ArrayList<>();
        
        
        // 2.返回结果集
        // 初始化结果集resultObject（InterfProductDealBookCommonDto）：
        InterfProductDealBookCommonDto interfProductDealBookCommonDto = new InterfProductDealBookCommonDto();

        // 2.1 判断是否需要生成银行账
        Map<String, Object> params = Maps.newHashMap();
        params.put("accureType", "02");
        params.put("dayendType", "Y");
        params.put("busiType", trdProductDeal.getBusiType());
        params.put("ps", trdProductDeal.getPs());
        params.put("finprodType", trdProductDeal.getFinprodType());
        // 查询结果保存至 变量.银行账场景配置List                                               
        listBokBankSceConfig = mapper.getBokBankSceConfig(params);
        
        // IF 变量.银行账场景配置List IS NULL OR 变量.银行账场景配置List.SIZE()==0
        if(CollectionUtils.isEmpty(listBokBankSceConfig)) {
            return null;
        }
       
        /**modify by wujie on 20190416
        // 2.2 查询银行账套信息
        // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息查询(根据账套代码)SHEET 链接
        Map<String, Object> booksetInfoMap = getBooksetInfoById(bankBooksetId, "BOOKSET_DATE");
        // 变量.账套日期信息Dto = DTO.get("BOOKSET_DATE")
        bokBooksetDateDto = (BokBooksetDate) booksetInfoMap.get("BOOKSET_DATE");
        **/
                                                                                                                                                                    
        // 2.3 金融产品交易入账信息初始化
        // 2.3.1 入账信息表(BOK_DEAL_ACCOUNTING) 
        BokDealAccounting valDealAccounting = new BokDealAccounting();
        // 账套代码 BOOKSET_ID 变量.银行账套代码
        valDealAccounting.setBooksetId(bankBooksetId);
        // 业务编号 BUSI_NO 输入.金融产品交易.交易编号
        valDealAccounting.setBusiNo(trdProductDeal.getTradeId());
        // 业务表 BUSI_TABLE 数据字典：业务表.金融产品表
        valDealAccounting.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        /**modify by wujie on 20190416 start
        // 录入日 BOOKSET_DATE 系统时间
        valDealAccounting.setBooksetDate(trdProductDeal.getCreateTime());
        // 入账日 BOOK_DATE 变量.账套日期信息Dto.当前日期
        valDealAccounting.setBookDate(bokBooksetDateDto.getBooksetDate());
        **/
        valDealAccounting.setBooksetDate(trdProductDeal.getTradeDate());
        valDealAccounting.setBookDate(trdProductDeal.getTradeDate());
        /**modify by wujie on 20190416 end**/
        
        // 实际发生日 HAPPEN_DATE 输入.金融产品交易.交易日
        valDealAccounting.setHappenDate(trdProductDeal.getTradeDate());
        // 是否已入账 IS_ACCOUNTED N 否
        valDealAccounting.setIsAccounted(DictCodeList.是否_否.getValue());
        // 入账凭证编号 BOOK_ID NULL
        valDealAccounting.setBookId(null);
        
        listBokDealAccounting.add(valDealAccounting);
        
        // 2.3.2 账务明细代码生成
        // 定义 变量.账务明细代码
        BokDetailId valBokDetailId = new BokDetailId();
        // 定义 变量.业务明细代码 = 输入.金融产品交易.金融产品代码
        String bokDetailId = trdProductDeal.getFinprodId();
        // 定义 变量.业务明细类型 = 输入.金融产品交易.金融产品类型
        String busiDetailType = trdProductDeal.getFinprodType();
        // IF 数据字典：业务类型.费用交易 eq 输入.金融产品交易.业务类型
        // OR 数据字典：业务类型.增值税交易 eq 输入.金融产品交易.业务类型
        if (DictCodeList.业务类型_产品费用交易.getValue().equals(trdProductDeal.getBusiType())
            || DictCodeList.业务类型_增值税交易.getValue().equals(trdProductDeal.getBusiType())) {
            // 变量.业务明细代码 = 输入.理财产品代码
            bokDetailId = prodId;
            // 变量.业务明细类型 = 输入.理财产品类型
            busiDetailType = prodFinprodType;
        }
        
        // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->组装账务明细(资金帐户-现金类)SHEET
        // 变量.账务明细代码 = DTO.账务明细代码
        valBokDetailId = this.getBokDetailId(bokDetailId, null, busiDetailType, null, branch, null);
        
        // 2.3.3 入账账务明细表(BOK_DEAL_ACCOUNTING_DETAIL)
        BokDealAccountingDetail valBokDealAccountingDetail = new BokDealAccountingDetail();
        // 账套代码 BOOKSET_ID L.账套代码
        valBokDealAccountingDetail.setBooksetId(bankBooksetId);
        // 业务编号 BUSI_NO 输入.金融产品交易.交易编号
        valBokDealAccountingDetail.setBusiNo(trdProductDeal.getTradeId());
        // 业务表 BUSI_TABLE 数据字典：业务表.金融产品表
        valBokDealAccountingDetail.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        // 业务类型 BUSI_TYPE 输入.金融产品交易.业务类型
        valBokDealAccountingDetail.setBusiType(trdProductDeal.getBusiType());
        // 账务明细代码 BOK_DETAIL_ID 变量.账务明细代码
        valBokDealAccountingDetail.setBokDetailId(valBokDetailId.getBokDetailId());
        // 变量.入账账务明细List.add(初始化结果) 
        listBokDealAccountingDetail.add(valBokDealAccountingDetail);
        
        // 2.4 初始化完成的交易入账信息保存至resultMap对象中
        // resultObject.setBokDealAccountingList(变量.入账信息List)
        interfProductDealBookCommonDto.setBokDealAccountingList(listBokDealAccounting);
        // resultObject.setBokDealAccountingDetailList(变量.入账账务明细List)
        interfProductDealBookCommonDto.setBokDealAccountingDetailList(listBokDealAccountingDetail);
        
        return interfProductDealBookCommonDto;
    }
    
//    /**
//     * 结算指令生成
//     * 
//     * @param productDealDto 交易标的
//     * @param insType 指令类型
//     * @return Map<String,Object>
//     * @see
//     * @since 1.0
//     */
//    public Map<String, Object> settlementInstructionGeneration_Layering(ProductDealDto productDealDto,
//                                                                        String insType,
//                                                                        String isBook) {
//    	/**
//    	 * add by wujie on 20190107 start
//    	 */
//    	String booksetId = productDealDto.getFinprodType();
//    	List<ValBooksetInfoDto> valBooksetInfoDtoList = productDealDto.getValBooksetInfoDtoList();
//        if (CollectionUtils.isNotEmpty(valBooksetInfoDtoList)) {
//        	booksetId =  valBooksetInfoDtoList.get(0).getBooksetId();
//        }
//        productDealDto.setBookNo(booksetId);
//        /**add by wujie on 20190107 end**/
//    	
//        Map<String, Object> resultMap = this.settlementInstructionMap(productDealDto, insType,
//            isBook,DictCodeList.是否_是.getValue());
//        // 结算指令
//        StlOrders valOrders = (StlOrders)resultMap.get("ORDERS");
//        String fingerNo = valOrders.getOrderId();
//        Date dealDate = valOrders.getSettleDate();
//        String myAcctNo = (String)resultMap.get("myAcctNo");
//        String myAcctType = (String)resultMap.get("myAcctType");
//
//        List<BokDealAccounting> bokDealAccountingList = new ArrayList<BokDealAccounting>();
//        List<BokDealAccountingDetail> bokDealAccountingDetailList = new ArrayList<BokDealAccountingDetail>();
//        List<BokDetailId> bokDetailIdList = new ArrayList<BokDetailId>();
//
//        // 入账信息表，入账账务明细表， 账务明细代码表 isBook 是 N\
//        if (DictCodeList.是否_否.getValue().equals(isBook)) {
//            if (CollectionUtils.isNotEmpty(valBooksetInfoDtoList)) {
//                for (ValBooksetInfoDto bokset : valBooksetInfoDtoList) {
//                    /**
//                     * 1.入账信息初始化
//                     */
//                    // 定义 变量.入账信息Dto
//                    BokDealAccounting valDealAccounting = new BokDealAccounting();
//                    // 账套代码 BOOKSET_ID
//                    valDealAccounting.setBooksetId(bokset.getBooksetId());
//                    // 业务编号 BUSI_NO 变量.指令编号
//                    valDealAccounting.setBusiNo(fingerNo);
//                    // 业务表 BUSI_TABLE 固定值：01 结算指令表
//                    valDealAccounting.setBusiTable(DictCodeList.业务表_结算指令表.getValue());
//                    // 录入日 BOOKSET_DATE 变量.结算日期
//                    valDealAccounting.setBooksetDate(dealDate);
//                    // 入账日 BOOK_DATE 变量.结算日期
//                    valDealAccounting.setBookDate(dealDate);
//                    // 实际发生日 HAPPEN_DATE 变量.结算日期
//                    valDealAccounting.setHappenDate(dealDate);
//                    // 是否已入账 IS_ACCOUNTED N 否
//                    valDealAccounting.setIsAccounted(DictCodeList.是否_否.getValue());
//                    // 入账凭证编号 BOOK_ID NULL
//                    valDealAccounting.setBookId(null);
//                    bokDealAccountingList.add(valDealAccounting);
//
//                    /**
//                     * 2.入账账务明细初始化
//                     */
//                    // 定义 变量.入账账务明细Dto
//                    BokDealAccountingDetail valDealAccountingDetail = new BokDealAccountingDetail();
//                    // 账套代码 BOOKSET_ID
//                    valDealAccountingDetail.setBooksetId(bokset.getBooksetId());
//                    // 业务编号 BUSI_NO 变量.指令编号
//                    valDealAccountingDetail.setBusiNo(fingerNo);
//                    // 业务表 BUSI_TABLE 固定值：01 结算指令表
//                    valDealAccountingDetail.setBusiTable(DictCodeList.业务表_结算指令表.getValue());
//                    // 业务类型 BUSI_TYPE 输入.金融产品交易.业务类型
//                    valDealAccountingDetail.setBusiType(productDealDto.getBusiType());
//
//                    bokDealAccountingDetailList.add(valDealAccountingDetail);
//
//                    /**
//                     * 3.入账账务明细初始化
//                     */
//                    // 定义 变量.账务明细代码表Dto
//                    BokDetailId valBokDetailId = new BokDetailId();
//                    // 账套代码 BOOKSET_ID 输入.交易标的Dto.账套代码
//                    valBokDetailId.setBooksetId(bokset.getBooksetId());
//                    // 业务明细代码 BUSI_ID 变量.本方资金账户
//                    valBokDetailId.setBusiId(myAcctNo);
//                    // 业务明细类型 BUSI_DETAIL_TYPE 变量.本方账户类型
//                    valBokDetailId.setBusiDetailType(myAcctType);
//                    // 投资目的 INV_AIM 输入.金融产品交易.投资目的
//                    valBokDetailId.setInvAim(null);
//                    // 交易编号 TRADE_ID 输入.交易标的Dto.交易编号
//                    valBokDetailId.setTradeId(productDealDto.getTradeId());
//                    String detailId = this.genMd5BokDetailId(valBokDetailId);
//                    valBokDetailId.setBokDetailId(detailId);
//                    valDealAccountingDetail.setBokDetailId(detailId);
//                    bokDetailIdList.add(valBokDetailId);
//
//                }
//            }
//        }
//        resultMap.put("ACCOUNTING_LIST", bokDealAccountingList);
//        resultMap.put("ACCOUNTING_DETAI_LIST", bokDealAccountingDetailList);
//        resultMap.put("BOK_DETAIL_ID_LIST", bokDetailIdList);
//        return resultMap;
//    }

    /**
     * 账务明细代码对应的四级科目尾号、四级科目尾号名称设置
     * 
     * @param bokDetailIdList 账务明细代码
     * @param detailSubjectNoTail 四级科目尾号
     * @param detailSubnameTail 四级科目尾号名称 add by wujie on 20181017
     */
    public void setDetailSubjectNoTail(List<BokDetailId> bokDetailIdList,
                                       String detailSubjectNoTail, String detailSubnameTail) {
        if (CollectionUtils.isNotEmpty(bokDetailIdList)) {
            for (BokDetailId bokDetailId : bokDetailIdList) {
                bokDetailId.setDetailSubjectNoTail(detailSubjectNoTail);
                bokDetailId.setDetailSubnameTail(detailSubnameTail);
            }
        }
    }

    /**
     * 查询投资的产品对应的金融产品类型
     * 
     * @param finprodId
     * @return
     */
    public String queryProductType(String finprodId) {
        String finprodType = DictCodeListExt1.金融产品类型_理财产品.getValue();
        String rst = mapper.queryProductType(finprodId);
        if (StringUtil.isNotEmpty(rst)) {
            finprodType = rst;
        }
        return finprodType;
    }

    /**
     * 账套参数查询
     * 
     * @param code 账套代码BooksetId
     * @return
     */
    public BokBooksetExt queryBokBooksetExtbyId(String code, String booksetId) {
        BokBooksetExt bokBooksetExt = new BokBooksetExt();
        bokBooksetExt = mapper.queryBokBooksetExtbyId(code, booksetId);
        return bokBooksetExt;
    }

    /**
     * 分层产品增值税初始化
     * 
     * @param listBokAcctLayeringInfo
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> initValueTax(List<BokAcctLayeringInfo> listBokAcctLayeringInfo) {

        Map<String, Object> resultMap = new HashMap<String, Object>();
        // 核算分层代码
        List<String> list = new ArrayList<String>();
        // 核算分层信息list
        List<BokAcctLayeringInfo> resultList = new ArrayList<BokAcctLayeringInfo>();
        Map<String, Set<String>> layMaps = new HashMap<String, Set<String>>();
        for (BokAcctLayeringInfo info : listBokAcctLayeringInfo) {
            list.add(info.getLayeringId());
        }
        if (CollectionUtils.isNotEmpty(list)) {
            resultList = valAcctLayeringInfoBatchMapper.selectByList(list);
            for (BokAcctLayeringInfo info : resultList) {

                if (layMaps.get(info.getLayeringId()) == null) {
                    Set<String> set = new HashSet<String>();
                    set.add(info.getBooksetId());
                    layMaps.put(info.getLayeringId(), set);
                }
                else {
                    layMaps.get(info.getLayeringId()).add(info.getBooksetId());
                }

            }
        }
        // 定义 变量.账务明细代码表
        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
        // 现金流信息
        List<FinCashInfo> listFinCashInfo = new ArrayList<FinCashInfo>();
        // 税务方案信息
        List<BokBooksetPlan> listBokBooksetPlan = new ArrayList<BokBooksetPlan>();
        
        /**add by wujie on 20190214 start**/
        Map<String, FinCashInfo> mapFinCashInfo = new HashMap<String, FinCashInfo>();
        /**add by wujie on 20190214 end**/

        for (BokAcctLayeringInfo layInfo : listBokAcctLayeringInfo) {
            // 初始化现金流信息
            FinCashInfo finCashInfo = new FinCashInfo();
            // 变量 分支序号
            Long branch = null;
            // 变量金融产品代码
            if (layInfo.getBusiDetailType().equals(DictCodeListExt1.金融产品类型_理财子产品.getValue())) {
                branch = layInfo.getBranch();
             // 初始化现金流信息dto
                finCashInfo.setCashId(InterfHelper.getMd5CashId_(layInfo.getBusiId()+branch,
                    layInfo.getBusiDetailType(), DictCodeListExt1.现金流二级类型_税额.getValue()));
            }
            else {
                // 金融产品代码=分层代码
                branch = InterfCommonConstant.分支序号_默认;
                // 初始化现金流信息dto
                finCashInfo.setCashId(InterfHelper.getMd5CashId_(layInfo.getBusiId(),
                    layInfo.getBusiDetailType(), DictCodeListExt1.现金流二级类型_税额.getValue()));
            }
            // 现金流一级类型
            finCashInfo.setCashType1(DictCodeListExt1.现金流一级类型_税额.getValue());
            // 现金流二级类型
            finCashInfo.setCashType2(DictCodeListExt1.现金流二级类型_税额.getValue());
            // 金融产品代码=变量金融产品代码
            finCashInfo.setFinprodId(layInfo.getBusiId());
            // 金融产品类型=layInfo.业务明细类型
            finCashInfo.setFinprodType(layInfo.getBusiDetailType());
            // 分支序号
            finCashInfo.setBranch(branch);
            /**modify by wujie on 20190214 start
            listFinCashInfo.add(finCashInfo);
            **/
            mapFinCashInfo.put(finCashInfo.getCashId(), finCashInfo);
            /**modify by wujie on 20190214 end**/

            Set<String> fLaySet = new HashSet<String>();
            fLaySet.add(layInfo.getBooksetId());
            if (layMaps.get(layInfo.getLayeringId()) != null) {
                fLaySet.addAll(layMaps.get(layInfo.getLayeringId()));
            }

            // loop 上层代码 fLaySet
            for (String s : fLaySet) {
                // 初始化账务明细代码
                BokDetailId valBokDetailId = new BokDetailId();
                // 业务明细代码
                valBokDetailId.setBusiId(finCashInfo.getCashId());
                // 账套代码
                valBokDetailId.setBooksetId(s);
                // 业务明细类型
                valBokDetailId.setBusiDetailType(finCashInfo.getCashType2());
                // 账务明细代码
                valBokDetailId.setBokDetailId(genMd5BokDetailId(valBokDetailId));

                listValBokDetailId.add(valBokDetailId);

                // 定义 变量.税务方案表Dto
                BokBooksetPlan plan = null;
                String[] types = new String[] {InterfCommonConstant.账套信息查询_账套估值方案};
                // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息查询(根据账套代码)SHEET
                Map<String, Object> booksetInfoMap = getBooksetInfoById(s, types);
                List<BokBooksetPlan> bokBooksetPlanlist = (List<BokBooksetPlan>)booksetInfoMap.get(
                    InterfCommonConstant.账套信息查询_账套估值方案);
                for (BokBooksetPlan bokBooksetPlan : bokBooksetPlanlist) {
                    if (DictCodeListExt1.配置方案类型_税务方案.getValue().equals(bokBooksetPlan.getSchemeType())) {
                        plan = bokBooksetPlan;
                        break;
                    }
                }
                /**modify by wujie on 20190617 start
                 if (plan == null || InterfCommonConstant.接口_默认用户.equals(plan)) {
                 **/
                if (plan == null || InterfCommonConstant.接口_默认用户.equals(plan.getUpdateUser())) {
                    /**modify by wujie on 20190617 end**/
                    plan = this.queryDefaultPlan(s);
                    plan.setBooksetId(s);
                }
                listBokBooksetPlan.add(plan);
            }
        }
        
        /**add by wujie on 20190214 start**/
        for(String key :mapFinCashInfo.keySet()){
        	listFinCashInfo.add(mapFinCashInfo.get(key));
        }
        /**add by wujie on 20190214 end**/

        resultMap.put("FIN_CASH_INFO", listFinCashInfo);
        resultMap.put("BOK_DETAIL_ID_LIST", listValBokDetailId);
        resultMap.put("BOK_BOOKSET_PLAN", listBokBooksetPlan);

        return resultMap;
    }

    /**
     * 2018/11/14 chendijun 撤销指令交易对应的原交易编号查询
     * 
     * @param stlOrders 结算指令Dto
     * @return orderId 指令编号
     * @see
     * @since 1.0
     */
    public String queryPOrderId(StlOrders stlOrders) {
        String orderId = valInterfCommonMapper.queryPOrderId(stlOrders);
        return orderId;
    }

    /**
     * 账务明细代码去重
     * 
     * @param listBokDetailId
     * @return add by wujie on 20181117
     */
    public List<BokDetailId> toRepeatBokDetailId(List<BokDetailId> listBokDetailId) {
        List<BokDetailId> list = new ArrayList<BokDetailId>();
        if (listBokDetailId == null || listBokDetailId.size() == 0) {
            return list;
        }
        Map<String, BokDetailId> map = new HashMap<String, BokDetailId>();
        for (BokDetailId obj : listBokDetailId) {
            String key = obj.getBokDetailId() + obj.getBooksetId();
            if (map.get(key) == null) {
                map.put(key, obj);
            }
        }
        for (String key : map.keySet()) {
            // BokDetailId rst = bokDetailIdMapper.selectByPrimaryKey(map.get(key));
            // if(rst==null){
            list.add(map.get(key));
            // }
        }
        return list;
    }

    /***
     * 批量删除交易
     */
    public void deletebatchFinprodDeal(Map<String, Object> paramMap, String interfDate) {
        paramMap.put("createTime", interfDate);
        paramMap.put("createUser", InterfCommonConstant.接口_默认用户);
        // 1.估值核算交易扩展表
        valCommonBatchMapper.deleteProductDealExt(paramMap);
        // 2.交易费用明细表
        valCommonBatchMapper.deleteTradeFeeDetail(paramMap);
        // 3.入账账务明细表
        valCommonBatchMapper.deleteDealAccountingDetail(paramMap);
        // 4.入账信息表
        valCommonBatchMapper.deleteDealAccounting(paramMap);
        // 5.结算指令扩展表
        valCommonBatchMapper.deleteOrdersExt(paramMap);
        // 6.结算指令表
        valCommonBatchMapper.deleteOrders(paramMap);
        // 7.金融产品交易
        valCommonBatchMapper.deleteProductDeal(paramMap);
    }

    /**
     * 获取未执行完的序号
     * 
     * @return
     * @see
     * @since 1.0
     */
    public List<String> getSeqnoUnEXEC() {
        Map<String, Object> params = new HashMap<>();
        List<String> statusList = new ArrayList<>();
        statusList.add(DictCodeInterfList.接口执行状态_未执行.getValue());
        statusList.add(DictCodeInterfList.接口执行状态_执行中.getValue());
        params.put("statusList", statusList);
        return mapper.getSeqnoUnEXEC(params);
    }

    /**
     * 债券、回购结算指令(结算备付金)
     * 
     * @param productDealDto productDealDto String insType return 资金账户类型 acctType
     */
    public String getBondBackInstype(ProductDealDto productDealDto, String insType) {

        // 变量 金融产品账户
        StlFinProductAcct finAccount = null;
        // 变量 资金账户类型
        String acctType = null;
        // 变量 指令类型Map
        Map<String, String> insMap = new HashMap<String, String>();

        insMap.put(DictCodeListExt1.指令类型_资金交收_债券类资产买入_资金交收日.getValue(),
            DictCodeListExt1.指令类型_资金交收_债券类资产买入_资金交收日.getValue());
        insMap.put(DictCodeListExt1.指令类型_资金交收_债券类资产卖出_资金到账.getValue(),
            DictCodeListExt1.指令类型_资金交收_债券类资产卖出_资金到账.getValue());
        insMap.put(DictCodeListExt1.指令类型_摊余成本类债券资产_还本到账.getValue(),
            DictCodeListExt1.指令类型_摊余成本类债券资产_还本到账.getValue());
        insMap.put(DictCodeListExt1.指令类型_摊余成本类债券资产_收息到账.getValue(),
            DictCodeListExt1.指令类型_摊余成本类债券资产_收息到账.getValue());
        insMap.put(DictCodeListExt1.指令类型_公允价值损益类债券资产_还本到账.getValue(),
            DictCodeListExt1.指令类型_公允价值损益类债券资产_还本到账.getValue());
        insMap.put(DictCodeListExt1.指令类型_公允价值损益类债券资产_收息到账.getValue(),
            DictCodeListExt1.指令类型_公允价值损益类债券资产_收息到账.getValue());
        insMap.put(DictCodeListExt1.指令类型_综合收益类债券资产_还本到账.getValue(),
            DictCodeListExt1.指令类型_综合收益类债券资产_还本到账.getValue());
        insMap.put(DictCodeListExt1.指令类型_综合收益类债券资产_收息到账.getValue(),
            DictCodeListExt1.指令类型_综合收益类债券资产_收息到账.getValue());
        insMap.put(DictCodeListExt1.指令类型_质押式正回购首期_资金到账.getValue(),
            DictCodeListExt1.指令类型_质押式正回购首期_资金到账.getValue());
        insMap.put(DictCodeListExt1.指令类型_质押式正回购到期_资金交收.getValue(),
            DictCodeListExt1.指令类型_质押式正回购到期_资金交收.getValue());
        insMap.put(DictCodeListExt1.指令类型_质押式逆回购首期_资金交收.getValue(),
            DictCodeListExt1.指令类型_质押式逆回购首期_资金交收.getValue());
        insMap.put(DictCodeListExt1.指令类型_质押式逆回购到期_资金到账.getValue(),
            DictCodeListExt1.指令类型_质押式逆回购到期_资金到账.getValue());
        insMap.put(DictCodeListExt1.指令类型_资金交收_买断式正回购首期_资金到账.getValue(),
            DictCodeListExt1.指令类型_资金交收_买断式正回购首期_资金到账.getValue());
        insMap.put(DictCodeListExt1.指令类型_资金交收_买断式正回购到期_资金到账.getValue(),
            DictCodeListExt1.指令类型_资金交收_买断式正回购到期_资金到账.getValue());
        insMap.put(DictCodeListExt1.指令类型_资金交收_买断式逆回购首期_资金到账.getValue(),
            DictCodeListExt1.指令类型_资金交收_买断式逆回购首期_资金到账.getValue());
        insMap.put(DictCodeListExt1.指令类型_资金交收_买断式逆回购到期_资金到账.getValue(),
            DictCodeListExt1.指令类型_资金交收_买断式逆回购到期_资金到账.getValue());
        insMap.put(DictCodeListExt1.指令类型_资金交收_买断式逆回购派息_资金到账.getValue(),
            DictCodeListExt1.指令类型_资金交收_买断式逆回购派息_资金到账.getValue());

        if (insMap.get(insType) == null) {
            return acctType;
        }
        // 判断交易是否为交易所债券、回购二级市场交易、标的券交易
        if ((DictCodeList.业务类型_银行间债券交易.getValue().equals(productDealDto.getBusiType())
                || DictCodeList.业务类型_债券质押式回购交易.getValue().equals(productDealDto.getBusiType())
                || DictCodeList.业务类型_债券买断式回购交易.getValue().equals(productDealDto.getBusiType())
                || DictCodeList.业务类型_债券借贷标的债券交易.getValue().equals(productDealDto.getBusiType()))
                && (DictCodeList.交易流通场所_上海证券交易所.getValue().equals(productDealDto.getTradeMarket())
                || DictCodeList.交易流通场所_深圳证券交易所.getValue().equals(productDealDto.getTradeMarket()))
                && DictCodeListExt1.交易市场_二级市场.getValue().equals(productDealDto.getTradeMarketMode())) {

        	/**MODIFY BY WUJIE ON 20190923 START
            String settlementModel = this.getSysParamValue(SysParamCodeConstant.系统参数_交易所结算模式);
            if(!StringUtil.isNotEmpty(settlementModel)){
                settlementModel = DictCodeListExt1.资金账户类型_结算备付金账户.getValue();
            }**/
        	String settlementModel =  DictCodeListExt1.资金账户类型_券商资金账户.getValue();
        	/**MODIFY BY WUJIE ON 20190923 END**/
            
            if(productDealDto != null && CollectionUtils.isNotEmpty(productDealDto.getValBooksetInfoDtoList())){
                /**ADD BY WUJIE ON 20190923 START**/
            	BokBooksetExt bokBooksetExt = this.queryBokBooksetExtbyId(ValDictCodeList.账套配置表参数项_交易所结算模式.getValue(),productDealDto.getValBooksetInfoDtoList().get(0).getBooksetId());
            	if(bokBooksetExt!=null){
            		settlementModel = bokBooksetExt.getParamValue();
            	}
            	/**ADD BY WUJIE ON 20190923 END**/
            	BokBooksetCapacctInfo bokBooksetCapacctInfo = queryBokBooksetCapacctInfo(productDealDto.getValBooksetInfoDtoList().get(0).getBooksetId(),
                        null, productDealDto.getCcy(), settlementModel);
                if(bokBooksetCapacctInfo == null){
                    return acctType;
                }
            }
            else {
                // 变量.金融产品账户
                finAccount = mapper.selectValFinProductAcct(productDealDto.getFinprodId(),
                        productDealDto.getFinprodType(), settlementModel, null,productDealDto.getCcy());
                if (finAccount == null) {
                    return acctType;
                }
            }
            // 变量.资金账户类型AcctType =数据字典：资金账户类型.结算备付金
            acctType = settlementModel;
        }
        return acctType;
    }
    
    /**
     * 
     * 产品估值方案初始化
     * @param booksetId 账套Id  估值方案代码
     * @return 账套估值方案
     * @see
     * @since 1.0
     */
    public BokBooksetPlan initProductValuationScheme(String booksetId) {
        Map<String, Object> map = new HashMap<>();
        //是否默认
        map.put("isdefault", DictCodeList.是否_是.getValue());
        BokBooksetPlan bokBooksetPlan = new BokBooksetPlan();
        bokBooksetPlan = valInterfCommonMapper.initProductValuationScheme(map);
        if (bokBooksetPlan != null) {
            bokBooksetPlan.setBooksetId(booksetId);
            bokBooksetPlan.setSchemeType(DictCodeListExt1.配置方案类型_估值方案.getValue());
        }
        return bokBooksetPlan;
    }
    
    /**
     * 文件解析业务check
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public TemplateCheckReturnResult resolveBusiCheck(String tmpEName, TemplateCheckReturnResult returnResult){
    	TempCheckInfo tempCheckInfo = InterfHelper.checkFinprodDealMap().get(tmpEName);
    	if(tempCheckInfo == null){
    		return returnResult;
    	}
    	
    	List list = returnResult.getT();
    	if(list == null || list.size()==0){
			return returnResult;
		}
    	
		Map<String, ValInterflog> valInterflogMap = InterfHelper.getMapValInterflog(returnResult.getValInterflog());
    	List rstlist = new ArrayList<>();
    	
    	
    	for(Object object :list){
    		boolean ischeck = true;
    		StringBuffer checkError = new StringBuffer();
    		
    		//金融产品代码
    		String finprodId = null;
    		
    		//金融产品类型
    		String finprodType = null;
    		
    		//分支序号
    		Long branch = InterfCommonConstant.分支序号_默认;
    		
    		//交易币种
    		String ccy = null;
    		
    		//日志key
    		String logkey = InterfHelper.getValInterflogKey(object);
    		ValInterflog checkLog = valInterflogMap.get(logkey);
    		
    		finprodId = InterfHelper.getObjectFieldValue(object, tempCheckInfo.getColprdName());
    		/** modify by dongjun on 20190313 remark:通道变更 start **/
    		// 产品代码
    		String prodId = finprodId;
    		// 通道代码
    		String chlId = null;
    		if(StringUtil.isNotEmpty(tempCheckInfo.getColChlId())){
    			chlId = InterfHelper.getObjectFieldValue(object, tempCheckInfo.getColChlId());
    		}
			ccy = InterfHelper.getObjectFieldValue(object, tempCheckInfo.getColCcyName());
			
			Map<String,Object> params = new HashMap<String,Object>();
			
			if(StringUtil.isNotEmpty(chlId)){
				params.put("finprodId", finprodId + FinalString.LAYERING_SPLIT + chlId);
			} else{
	    		params.put("finprodId", finprodId);
			}
    		
    		//产品端交易-产品check
    		if(tempCheckInfo.getColprdName()!=null 
    				&& "PRD".equals(tempCheckInfo.getInvestType())){
    			
    			if(StringUtil.isNotEmpty(chlId)){
    				finprodType = DictCodeListExt1.金融产品类型_通道.getValue();
    			} else{
        			finprodType = DictCodeListExt1.金融产品类型_理财产品.getValue();
    				if(tempCheckInfo.getColprdTypeName() != null){
    					finprodType = InterfHelper.getObjectFieldValue(object, tempCheckInfo.getColprdTypeName());
    				}
    				if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(finprodType)){
    					String prdVdateStr = InterfHelper.getObjectFieldValue(object, tempCheckInfo.getColprdVdateName());
    					if(prdVdateStr == null){
    						ischeck = false;
    	        			checkError.append("prodId"+"：理财子产品["+finprodId+"]，产品起息日为空，需要联系管理员进行交易数据的确认;");
    					}else{
    						branch = Long.valueOf(prdVdateStr);
    					}
    				}
    			}
				    		
        		params.put("finprodType", finprodType);
        		params.put("branch", branch);
        		List<Prdbokstl> prdlist = valInterfCommonMapper.queryPrdbokstl(params);
        		
        		if(prdlist==null || prdlist.size()==0){
        			ischeck = false;
        			checkError.append("prodId"+"：产品["+prodId+"]不存在，需要联系管理员进行产品信息接入确认;");
        		}else{
        			params.put("ccy", ccy);
            		prdlist = valInterfCommonMapper.queryBokAcctSubjectInfo(params);
            		if(prdlist==null || prdlist.size()==0){
            			ischeck = false;
            			checkError.append("prodId"+"：产品["+prodId+"]账套信息不存在，需要联系管理员进行产品信息接入确认;");
            		}else{
            			if(prdlist.get(0).getAcctNo()==null){
                			ischeck = false;
                			checkError.append("prodId"+"：[核算主体:"+prodId+"、币种："+ccy+"]对应的理财托管户不存在，需要联系管理员进行标的账户接入确认;");
                		}
            		}
        		}

        		if(TempConstant.净值型产品费用交易表.equals(tmpEName)
        				|| TempConstant.货币型产品费用交易表.equals(tmpEName)
        				|| TempConstant.预期型产品费用交易表.equals(tmpEName)){

        			String cashId = null;
        			String feeId = InterfHelper.getObjectFieldValue(object, "feeId");
        			if(!DictCodeListExt1.现金流二级类型_浮动管理费.getValue().equals(feeId)) {
                        if (DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(finprodType)) {
                            cashId = InterfHelper.getMd5CashId_(finprodId + branch, feeId);
                        } else {
                            cashId = InterfHelper.getMd5CashId_(finprodId, feeId);
                        }
                        Map<String, Object> paramMap = new HashMap<String, Object>();
                        paramMap.put("cashId", cashId);
                        FinCashInfo finCashInfo = this.queryCashInfo(paramMap);
                        if (finCashInfo == null) {
                            ischeck = false;
                            checkError.append("prodId" + "：产品[" + finprodId + "]没有对应的费用[" + feeId + "]现金流信息，需要联系管理员进行产品费用基础信息接入确认;");
                        }
                    }
        		}

        	}
    		//投资端交易-产品check
    		else if(tempCheckInfo.getColprdName()!=null 
    				&& "INVEST".equals(tempCheckInfo.getInvestType())){
    			/**add by wujie on 20190321 start**/
    			String booksetId = null;
    			if(StringUtil.isNotEmpty(chlId)){
    				//判断产品+通道子账套信息是否已存在
    				String acctSubjectId = finprodId + FinalString.LAYERING_SPLIT + chlId;
    				List<BokBooksetSonDto>  booksetList = this.queryBokBooksetSonInfo(acctSubjectId, null, null);
    				if(booksetList==null || booksetList.size()==0){
            			ischeck = false;
            			checkError.append("交易入账核算主体"+"：["+acctSubjectId+"]不存在，需要联系管理员进行理财产品借通道投资交易接入确认;");
            		}
    				else{
    					booksetId = booksetList.get(0).getBooksetIdSon();
    					/**
            			 * 结算指令交易：不判断投资主体是否有对应托管户
            			 */
            			if(!TempConstant.结算指令.equals(tmpEName)){
            				Map<String,Object> paramsMap = new HashMap<String,Object>();
            				paramsMap.put("booksetId", booksetId);
            				paramsMap.put("acctType", DictCodeListExt1.资金账户类型_产品托管户.getValue());
            				paramsMap.put("ccy", ccy);
            	            BokBooksetCapacctInfo capacctInfo = mapper.queryBokBooksetCapacctInfo(paramsMap);
            				if(capacctInfo==null || capacctInfo.getAcctNo()==null){
                    			ischeck = false;
                    			checkError.append("交易入账核算主体"+"：[核算主体:"+acctSubjectId+"、币种："+ccy+"]对应的托管户不存在，需要联系管理员进行标的账户接入确认;");
                    		}
            			}
    				}
    			}
    			else{
    			/**add by wujie on 20190321 end**/	
    				params.put("branch", branch);
            		List<Prdbokstl> prdlist = valInterfCommonMapper.queryPrdbokstl(params);
            		
            		if(prdlist==null || prdlist.size()==0){
            			ischeck = false;
            			checkError.append("prodId"+"：产品["+prodId+"]不存在，需要联系管理员进行产品信息接入确认;");
            		}else{
            			for(Prdbokstl prd : prdlist){
            				if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(prd.getFinprodType())){
            					continue;
            				}
            				else if(DictCodeListExt1.金融产品类型_理财产品.getValue().equals(prd.getFinprodType())){
            					finprodType = prd.getFinprodType();
            				}
            				else if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(prd.getFinprodType())){
            					finprodType = prd.getFinprodType();
            				}
            				else if(DictCodeListExt1.金融产品类型_通道.getValue().equals(prd.getFinprodType())){
            					finprodType = prd.getFinprodType();
            				}
            			}
            			if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(finprodType)){
            				ischeck = false;
                			checkError.append("prodId"+"：理财子产品["+finprodId+"]不支持做投资交易，需要联系管理员进行产品信息接入确认;");
            			}else{
            				params.put("finprodType", finprodType);
                			params.put("ccy", ccy);
                    		prdlist = valInterfCommonMapper.queryBokAcctSubjectInfo(params);
                    		if(prdlist==null || prdlist.size()==0){
                    			ischeck = false;
                    			checkError.append("prodId"+"：产品["+prodId+"]账套信息不存在，需要联系管理员进行产品信息接入确认;");
                    		}else{
                    			/**
                    			 * 结算指令交易：不判断投资主体是否有对应托管户
                    			 */
                    			if(!TempConstant.结算指令.equals(tmpEName)){
                    				if(prdlist.get(0).getAcctNo()==null){
                            			ischeck = false;
                            			checkError.append("prodId"+"：[核算主体:"+prodId+"、币种："+ccy+"]对应的理财托管户不存在，需要联系管理员进行标的账户接入确认;");
                            		}
                    			}
                    			booksetId = prdlist.get(0).getBooksetId();

                                /** add by dongjun on 20190402 start **/
                                if(TempConstant.结算指令.equals(tmpEName)){
                                    String acctNo = InterfHelper.getObjectFieldValue(object, tempCheckInfo.getAcctNo());
                                    String booksetId2 = prdlist.get(0).getBooksetId();
                                    BokBooksetCapacctInfoKey key = new BokBooksetCapacctInfoKey();
                                    key.setAcctNo(acctNo);
                                    key.setCcy(ccy);
                                    key.setBooksetId(booksetId2);
                                    BokBooksetCapacctInfo bokBooksetCapacctInfo = valBooksetCapacctInfoMapper.selectByPrimaryKey(key);

                                    if(bokBooksetCapacctInfo==null){
                                        ischeck = false;
                                        checkError.append("结算指令"+"：[产品"+finprodId+"、币种："+ccy+"、账号："+acctNo+
                                                "]没有对应的资金账户信息，需要联系管理员进行标的账户接入确认;");
                                    }
                                }
                                /** add by dongjun on 20190402 end **/

                    		}
            			}
            		}
                /**add by wujie on 20190321 start**/
    			}
    			/**add by wujie on 20190321 end**/
    			
    			//投资目的check
        		if(TempConstant.债券买卖交易.equals(tmpEName)
        				|| TempConstant.债券存续期交易.equals(tmpEName)){
        			String assetId_prodName = "assetId";
        			String tradeDate_prodName = "dealDate";
        			
        			if(TempConstant.债券存续期交易.equals(tmpEName)){
        				tradeDate_prodName = "tradeDate";
        			}
        			//资产代码
        			String assetId = InterfHelper.getObjectFieldValue(object, assetId_prodName);
        			//交易日
        			Date tradeDate = DateUtil.parse(InterfHelper.getObjectFieldValue(object, tradeDate_prodName));
        			
        			BokBooksetExt bokBooksetExt = queryBokBooksetExtbyId(DictCode.投资目的获取方式.getValue(),booksetId);
        			if (bokBooksetExt != null 
        					 && DictCodeListExt1.投资目的获取方式_估值方案生成.getValue().equals(bokBooksetExt.getParamValue())) {
        				 InvAimDto invAimDto = finProductCommonBatchService.searchInvAim(tradeDate,
        						 booksetId, DictCodeListExt1.证券品种_债券.getValue(),
        						 assetId, null);
        				if (invAimDto ==null){
        					ischeck = false;
                 			checkError.append("资产["+assetId+"]：在账套["+booksetId+"]无对应估值方案，需要联系管理员进行估值方案配置确认;");
        				}
        				else if(invAimDto.getSchemeCnt() > 1){
        					ischeck = false;
                  			checkError.append("资产["+assetId+"]：在账套["+booksetId+"]出现重复估值方案，需要联系管理员进行估值方案配置确认;"); 
        				}
        			}
        		}
    			
    		}
    		
    		if(StringUtil.equals(TempConstant.通道基础信息, tempCheckInfo.getDtoEname())){
    			String scheId = InterfHelper.getObjectFieldValue(object, tempCheckInfo.getScheId());
    			if(StringUtil.isNotEmpty(scheId)){
    				int shce = valInterfCommonMapper.queryBokSchemeInfo(DictCodeListExt1.配置方案类型_税务方案.getValue(),
    						scheId);
    				if(shce == 0){
    					ischeck = false;
    					checkError.append("[" + shce + "]税务方案信息在系统中不存在，需要联系管理员进行信息接入确认;"); 
    				}
    			}
    		}
    		
    		if(StringUtil.equals(TempConstant.理财产品借通道投资交易, tempCheckInfo.getDtoEname())
    				|| StringUtil.equals(TempConstant.通道产品费用交易, tempCheckInfo.getDtoEname())){
    			//  判断产品基本信息是否存在
    			Map<String,Object> params1 = new HashMap<>();
    			params1.put("finprodId", finprodId);
        		List<Prdbokstl> prdlist = valInterfCommonMapper.queryPrdbokstl(params1);
        		if(StringUtil.isEmptyList(prdlist)){
        			ischeck = false;
					checkError.append("prodId" + ": 产品[" + finprodId + "]基础信息不存在，需要联系管理员进行信息接入确认;"); 
        		}
    			
        		//  判断通道基本信息是否已存在
        		params1.clear();
        		params1.put("finprodId", chlId);
        		params1.put("finprodType", DictCodeListExt1.金融产品类型_通道.getValue());
        		List<Prdbokstl> prdlist1 = valInterfCommonMapper.queryPrdbokstl(params1);
        		if(StringUtil.isEmptyList(prdlist1)){
        			ischeck = false;
					checkError.append("channelId : 通道[" + chlId + "]基础信息不存在，需要联系管理员进行通道信息接入确认;"); 
        		}
        		/** modify by wujie on 20190320 start remark:判断通道是否已挂通道账户
        		//  通道托管户是否已存在
        		String booksetIdSon = finprodId + FinalString.LAYERING_SPLIT + chlId;
        		BokBooksetCapacctInfo bokBooksetCapacctInfo = queryBokBooksetCapacctInfo(booksetIdSon, null, ccy,DictCodeListExt1.资金账户类型_产品托管户.getValue());
        		if(bokBooksetCapacctInfo == null){
        			ischeck = false;
					checkError.append("核算主体" + "：[核算主体:" +booksetIdSon + "、币种：" + ccy + "]对应的理财托管户不存在，需要联系管理员进行标的账户接入确认;"); 
        		}
        		**/
        		Map<String,Object> paramsMap = new HashMap<String,Object>();
        		paramsMap.put("finprodType", DictCodeListExt1.金融产品类型_通道.getValue());
        		paramsMap.put("finprodId",chlId);
                /**modify by wujie on 20190613 start
                 paramsMap.put("acctType",DictCodeList.资金账户类型_通道账户.getValue());
                 **/
                paramsMap.put("acctType", DictCodeListExt1.资金账户类型_产品托管户.getValue());
                /**modify by wujie on 20190613 end**/
        		List<StlFinProductAcct> stlFinProductAcctList = this.queryStlFinProductAcctInfo(paramsMap);
        		if(stlFinProductAcctList==null || stlFinProductAcctList.size()==0){
        			ischeck = false;
                    checkError.append("通道" + "：[" +chlId + "]对应的托管账户不存在，需要联系管理员进行标的账户接入确认;");
                }
        		/**modify by wujie on 20190320 end**/
        		
    		}
    		/** modify by dongjun on 20190313 remark:通道变更 end **/
    		if(TempConstant.产品回补交易.equals(tmpEName)){
    			String cashId = null;
    			String feeId = InterfHelper.getObjectFieldValue(object, "feeId");
    			if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(finprodType)){
    				cashId = InterfHelper.getMd5CashId_(finprodId + branch,feeId);
    			}else{
    				cashId = InterfHelper.getMd5CashId_(finprodId,feeId);
    			}
    			Map<String,Object> paramMap = new HashMap<String,Object>();
    	        paramMap.put("cashId", cashId);
    	        FinCashInfo finCashInfo = this.queryCashInfo(paramMap);
    	        if(finCashInfo==null){
    	        	ischeck = false;
        			checkError.append("prodId"+"：产品["+finprodId+"]没有对应的回补费用["+feeId+"]现金流信息，需要联系管理员进行产品费用基础信息接入确认;");
    	        }
    		}

    		/**add by yinya on 2022-01-08 结算指令自动映射指令类型的检查项 start**/
            if(TempConstant.结算指令.equals(tmpEName)){
                String orderId = InterfHelper.getObjectFieldValue(object, "orderId");
                String finprodType1 = InterfHelper.getObjectFieldValue(object, "finprodType");
                String busiType = InterfHelper.getObjectFieldValue(object, "busiType");
                String tradePs = InterfHelper.getObjectFieldValue(object, "tradePs");
                String invAim = InterfHelper.getObjectFieldValue(object, "invAim");
                //定义 变量.交易对应指令集合 = 调用共通设计->【详细设计-接口共通方法设计.xlsx】->查询交易需要生成的指令SHEET
                ProductDealDto deal = new ProductDealDto();
                deal.setFinprodType(finprodType1);
                deal.setBusiType(busiType);
                deal.setPs(tradePs);
                deal.setInvAim(invAim);
                //02为接口默认使用的映射类型
                deal.setMappingType("02");
                //匹配IS_BOOK为N的指令映射
                deal.setIsTrade(DictCodeList.是否_否.getValue());
                List<StlOrdersMapping> somlist = this.queryStlOrdersMapping(deal);
                List<StlOrdersMapping> stlList =new ArrayList<StlOrdersMapping>();
                if(somlist==null || somlist.size()==0){
                    ischeck = false;
                    checkError.append("指令编号："+orderId
                       +" 金融产品类型："+finprodType1
                       +" 业务类型："+busiType
                       +" 交易方向："+tradePs
                       +" 投资目的："+invAim
                       +"未匹配指令类型,请检查结算指令与交易映射关系表");
                }
                //去重处理
                somlist.stream(). collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(f -> f.getOrderType()+f.getMappingType()))), ArrayList::new))
                        .forEach(f->stlList.add(f));

                if(stlList.size()>1){
                    ischeck = false;
                    checkError.append("指令编号："+orderId
                            +" 金融产品类型："+finprodType1
                            +" 业务类型："+busiType
                            +" 交易方向："+tradePs
                            +" 投资目的："+invAim
                            +" 匹配多个指令类型，请检查结算指令与交易映射关系表");
                }

            }
            /**add by yinya on 2022-01-08 结算指令自动映射指令类型的检查项 end**/

    		if(ischeck){
    			rstlist.add(object);
    		}else{
    			checkLog.setIsCheck(DictCodeList.是否_否.getValue());
    			checkLog.setRemark(checkError.toString());
    			valInterflogMap.put(logkey, checkLog);
    		}
    	}
    	returnResult.setT(rstlist);
    	returnResult.setValInterflog(InterfHelper.getListValInterflog(valInterflogMap));
    	return returnResult;
    }
    
    /**
     * 结算指令生成_查询资金账户
     * @param finprodId
     * @param finprodType
     * @param acctType
     * @param booksetId
     * @param layeringFlag
     * @return
     */
    public String selectValFinProductAcct(String finprodId,String finprodType,String acctType,String ccy,String booksetId,String layeringFlag){
    	String myacctNo = null;
    	if(DictCodeList.是否_是.getValue().equals(layeringFlag)){
    		Map<String,Object> map = new HashMap<String,Object>();
        	map.put("booksetId", booksetId);
        	map.put("acctType", acctType);
        	map.put("ccy", ccy);
        	BokBooksetCapacctInfo result=  mapper.queryBokBooksetCapacctInfo(map);
        	if(result != null){
        		myacctNo = result.getAcctNo();
        	}
    	}else{
    		StlFinProductAcct valFinProductAcct1 = this.queryValFinProductAcct(finprodId, finprodType, acctType, null,ccy);
    		if(valFinProductAcct1 != null){
    			myacctNo = valFinProductAcct1.getAcctNo();
    		}
    	}
    	return myacctNo;
    }
    
    
    /**
     * 通过交易生成指令及指令入账
     * @param tradeDeal
     * @param finprodId
     * @param finprodType
     * @param prodVdate
     * @return
     */
    public Map<String,Object>  instructionGenerate(TrdProductDeal tradeDeal,
                                                   String finprodId, String finprodType, Date prodVdate){
    	Long branch = null;
        if(finprodType.equals(DictCodeListExt1.金融产品类型_理财子产品.getValue())){
        	branch = Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(prodVdate));
        }
    	return instructionGenerate_Branch(tradeDeal,finprodId,finprodType,branch,null);
    }
    
    /**
     * 通过交易生成指令及指令入账(分支序号)
     * @param tradeDeal
     * @param finprodId
     * @param finprodType
     * @param cbranch
     * @return
     */
    public Map<String,Object>  instructionGenerate_Branch(TrdProductDeal tradeDeal, String finprodId, String finprodType, Long cbranch,
                                                          List<PtlTrade> ptlTradeList){
    	Map<String,Object>  resultMap = new HashMap<String,Object>();
    	// 结算指令
        List<StlOrders> listStlOrders = new ArrayList<StlOrders>();
        // 结算指令扩展表
        List<StlOrdersExt> listStlOrdersExt = new ArrayList<StlOrdersExt>();
        // 入账信息表
        List<BokDealAccounting> listBokDealAccounting = new ArrayList<BokDealAccounting>();
        // 账务明细表
        List<BokDealAccountingDetail> listBokDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        // 账务明细代码表
        List<BokDetailId> listBokDetailId = new ArrayList<BokDetailId>();
        // 理财产品代码
        String prodId = finprodId;
        if(StringUtil.isNotEmpty(tradeDeal.getChlId())){
        	finprodId =  prodId + FinalString.LAYERING_SPLIT + tradeDeal.getChlId();
        	finprodType = DictCodeListExt1.金融产品类型_通道.getValue();
        }
        //获取产品类型
        if(finprodType==null){
        	Map<String,Object> params = new HashMap<String,Object>();
    		params.put("finprodId", finprodId);
        	
    		List<Prdbokstl> prdlist = valInterfCommonMapper.queryPrdbokstl(params);
    		if(prdlist==null || prdlist.size()==0){
    			return resultMap;
    		}
    		for(Prdbokstl prd : prdlist){
				if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(prd.getFinprodType())){
					continue;
				}
				else if(DictCodeListExt1.金融产品类型_理财产品.getValue().equals(prd.getFinprodType())){
					finprodType = prd.getFinprodType();
				}
				else if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(prd.getFinprodType())){
					finprodType = prd.getFinprodType();
				} 
				else if(DictCodeListExt1.金融产品类型_通道.getValue().equals(prd.getFinprodType())){
					finprodType = prd.getFinprodType();
				}
			}
        }
        if(finprodType==null){
        	return resultMap;
        }
        Long branch = null;
        if(finprodType.equals(DictCodeListExt1.金融产品类型_理财子产品.getValue())){
        	branch = cbranch;
        }
        
        //根据核算主体查询账套信息
        List<ValBooksetInfoDto> boksetList = this.getBokBooksetByProdId_Layering(finprodId+(branch==null?"":branch)+ FinalString.LAYERING_SPLIT+finprodType);
        if(boksetList==null || boksetList.size()==0){
        	return resultMap;
        }

        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(finprodType)
                /** modify by yanya on 20191122 防止chnlI为空字符串**/
                //&& tradeDeal.getChlId() == null
                && StringUtil.isEmpty(tradeDeal.getChlId())
        		&& !DictCodeList.业务类型_净值型TA交易.getValue().equals(tradeDeal.getBusiType())
        		&& !DictCodeList.业务类型_货币型TA交易.getValue().equals(tradeDeal.getBusiType())
        		&& !DictCodeList.业务类型_预期收益型TA交易.getValue().equals(tradeDeal.getBusiType())
        		&& !DictCodeList.业务类型_产品费用交易.getValue().equals(tradeDeal.getBusiType())
        		){
            List<ValBooksetInfoDto>  valBooksetInfoDtoList= getSonPtlPortfolo(finprodId,ptlTradeList);
            if (CollectionUtils.isNotEmpty(valBooksetInfoDtoList)) {
            	 boksetList.addAll(valBooksetInfoDtoList);
            }
        }
        /**ADD BY WUJIE ON 20191118 START**/
        Map<String, ValBooksetInfoDto> qcMap = new HashMap<String, ValBooksetInfoDto>();
        List<ValBooksetInfoDto> bokSetList = new ArrayList<ValBooksetInfoDto>();
        for(ValBooksetInfoDto dto:boksetList){
        	if(qcMap.get(dto.getBooksetId())==null){
        		bokSetList.add(dto);
        	}
        	qcMap.put(dto.getBooksetId(), dto);
        }
        boksetList.clear();
        boksetList.addAll(bokSetList);
        /**ADD BY WUJIE ON 20191118 END**/
        

    	//1.交易标的DTO初始化
    	// 结算指令
        ProductDealDto dto = new ProductDealDto();
        // 交易编号
        dto.setTradeId(tradeDeal.getTradeId());
        //增值税交易
        //费用交易
        //活期存款交易
        if(DictCodeList.业务类型_增值税交易.getValue().equals(tradeDeal.getBusiType())
        		|| DictCodeList.业务类型_产品费用交易.getValue().equals(tradeDeal.getBusiType())
        		|| DictCodeList.业务类型_活期存款交易.getValue().equals(tradeDeal.getBusiType())){
        	// 金融产品代码
        	dto.setFinprodId(finprodId);
        	//金融产品类型
        	dto.setFinprodType(finprodType);
        }else{
        	// 金融产品代码
            dto.setFinprodId(tradeDeal.getFinprodId());
            //金融产品类型
            dto.setFinprodType(tradeDeal.getFinprodType());
        }
    	// 业务类型
        dto.setBusiType(tradeDeal.getBusiType());
    	// 交易方向
        dto.setPs(tradeDeal.getPs());
    	// 交易日
        dto.setTradeDate(tradeDeal.getTradeDate());
    	// 交割日
        dto.setEffDate(tradeDeal.getSettleDate());
    	// 交易金额
        dto.setTardeAmt(tradeDeal.getTradeAmt());
    	// 币种
        dto.setCcy(tradeDeal.getCcy());
    	//投资目的
    	dto.setInvAim(tradeDeal.getInvAim());
    	// 交易状态
        dto.setTradeStatus(tradeDeal.getTradeStatus());
    	//交易市场
    	dto.setTradeMarketMode(tradeDeal.getTradeMarketMode());
    	//交易场所
    	dto.setTradeMarket(tradeDeal.getTradeMarket());
    	//原交易编号
        dto.setpTradeId(tradeDeal.getpTradeId());
        // 理财产品代码
        dto.setProdId(finprodId);
        dto.setValBooksetInfoDtoList(boksetList);
        //映射类型 01
        dto.setMappingType("01");
        //全价总额
        dto.setFpriceAmt(tradeDeal.getFpriceAmt());
        /**add by yinya 通过接口中是否生成结算指令的字段，控制接入交易的时候，只自动生成随交易支付(IS_BOOK为Y)的结算指令**/
        if(StringUtil.equals(tradeDeal.getIsOrders(), DictCodeList.是否_否.getValue())){
            dto.setIsTrade(DictCodeList.是否_是.getValue());
        }
        Map<String,String> paramMap = new HashMap<String,String>();
        if(dto.getTradeMarket() != null){
        	paramMap.put("TRADE_MARKET", dto.getTradeMarket());
        }
        /**ADD BY WUJIE ON 20200205 START**/
        if(DictCodeListExt3.金融产品类型_期权.getValue().equals(tradeDeal.getFinprodType())){
        	FinTradeProductKey fkey  = new FinTradeProductKey();
        	fkey.setFinprodId(tradeDeal.getFinprodId());
        	fkey.setBranch(InterfCommonConstant.分支序号_默认);
        	FinTradeProduct finTradeProduct = finTradeProductMapper.selectByPrimaryKey(fkey);
        	paramMap.put("EXC_PS", finTradeProduct==null?null:finTradeProduct.getExcPs());
        	/** add by yanya on 20200212 增加期权的收付方向当作参数条件**/
        	//变量.交易标的DTO.参数条件Map.put("PS_PAYMENT",输入.交易信息.收付方向)
            paramMap.put("PS_PAYMENT", tradeDeal.getPsPayment());
        }
        /**ADD BY WUJIE ON 20200205 END**/

        /** add by yanya on 20201222 其他债券跟计划类指令一样，这里增加是否其他债券参数区分 start**/
        //判定为计划类资产 且 业务类型为资产类交易
        if(StringUtil.equals(DictCodeListExt1.金融产品类型_计划类资产.getValue(), tradeDeal.getFinprodType())
            && StringUtil.equals(DictCodeList.业务类型_计划类资产交易_类债券资产交易.getValue(), tradeDeal.getBusiType())) {
            //定义 是否其他债券 默认为否
            String 是否其他债券 = DictCodeList.是否_否.getValue();
            //modify by sunshuaishuai 项目类也走计划类场景，去除项目类记账场景 20220401 start
            //定义 变量.标的类金融产品附属DTO = 查询标的类金融产品附属表
//            FinProductAdd finProductAddDto = finProductAddMapper.selectByPrimaryKey(tradeDeal.getFinprodId());
//            //IF 变量.标的类金融产品附属DTO IS NOT NULL AND 变量.标的类金融产品附属DTO.是否其他债权 IS NOT NULL
//            if(finProductAddDto != null && StringUtil.isNotEmpty(finProductAddDto.getIsQtzq())) {
//                //变量.是否其他债权 = 变量.标的类金融产品附属DTO.是否其他债权
//                是否其他债券 = finProductAddDto.getIsQtzq();
//            }
            //modify by sunshuaishuai 项目类也走计划类场景，去除项目类记账场景 20220401 end
            //变量.交易标的DTO.参数条件Map.put("IS_QTZQ",变量.是否其他债权)
            paramMap.put("IS_QTZQ", 是否其他债券);
        }
        /** add by yanya on 20201222 其他债券跟计划类指令一样，这里增加是否其他债券参数区分 end**/
        
        dto.setParamMap(paramMap);
        
        //3.查询交易需要生成的指令
        List<StlOrdersMapping> somlist = this.queryStlOrdersMapping(dto);
        if(somlist==null || somlist.size()==0){
        	return resultMap;
        }
       
        
        //4.生成结算指令
        for(StlOrdersMapping stl :somlist){
        	// 定义 变量.结算指令Dto
            StlOrders valOrders = new StlOrders();
            // 定义 变量.结算指令扩展Dto
            StlOrdersExt valOrdersExt = new StlOrdersExt();
            
            //结算金额
            BigDecimal settleAmt = dto.getTardeAmt();
            if(stl.getSettlamtFieldcode() != null && stl.getSettlamtFieldcode().length()>0){
            	settleAmt = this.getSettlementAmount(dto, stl.getSettlamtFieldcode());
            }
            
            // 定义 变量.结算日期 
            Date dealDate = dto.getTradeDate();
            if("02".equals(stl.getDateType())){
            	dealDate = dto.getEffDate();
            }
            // 本方账户类型
            String myAcctType = "";
            //对手方账户类型
            String counterAcctType = stl.getCounterAcctType();
            
            // 定义 变量.收付方向
            String ps = stl.getPs();
            
            // 定义 变量.本方资金账户
            String myAcctNo = "";
            
            // 定义 变量.对方资金账户
            String counterAcctNo = "";
            if(DictCodeList.业务类型_活期存款交易.getValue().equals(tradeDeal.getBusiType())){
            	//变量.本方资金账户 = 输入.交易信息.金融产品代码
            	myAcctNo = tradeDeal.getFinprodId();
            	//变量.本方账户类型 = 输入.交易信息.金融产品类型
            	myAcctType = tradeDeal.getFinprodType();
            }else{
	            myAcctType = this.getBondBackInstype(dto, stl.getOrderType());
	            if(myAcctType==null){
	            	myAcctType = stl.getMyAcctType();
	            }
	            
	            //本方资金账户：根据账套Id、资金账户类型、交易币种查询 资金账户号
	            Map<String,Object> params = new HashMap<String,Object>();
	            params.put("booksetId", boksetList.get(0).getBooksetId());
	            params.put("acctType", myAcctType);
	            params.put("ccy", dto.getCcy());
	            BokBooksetCapacctInfo capacctInfo = mapper.queryBokBooksetCapacctInfo(params);
	            if(capacctInfo != null){
	            	myAcctNo = capacctInfo.getAcctNo();
	            }
            }
            
            //对方资金账户：根据账套Id、资金账户类型、交易币种查询 资金账户号
            if(counterAcctType != null){
            	Map<String,Object> params = new HashMap<String,Object>();
                params.put("booksetId", boksetList.get(0).getBooksetId());
                params.put("ccy", dto.getCcy());
                
            	params.put("acctType", counterAcctType);
            	BokBooksetCapacctInfo capacctInfo = mapper.queryBokBooksetCapacctInfo(params);
                if(capacctInfo != null){
                	counterAcctNo = capacctInfo.getAcctNo();
                }
            }
        	
            String pOrderId = "";
            if (DictCodeListExt1.交易状态_撤销.getValue().equals(dto.getTradeStatus())) {
                StlOrders valOrdersCX = new StlOrders();
                // 变量.结算指令Dto.交易编号 = 输入.交易标的Dto.原交易编号
                valOrdersCX.setTradeId(dto.getpTradeId());
                // 变量.结算指令Dto.指令类型 = 输入.指令类型
                valOrdersCX.setOrderType(stl.getOrderType());
                // 变量.结算指令Dto.业务类型 = 输入.交易标的Dto.业务类型
                valOrdersCX.setBusiType(dto.getBusiType());
                pOrderId = this.queryPOrderId(valOrdersCX);
            }
            
            // 2020年2月12日 魏鹏：结算日期为空时，不生成指令，直接返回
            if(!StringUtil.isNotEmpty(dealDate)){
                continue;
            }
            
            // 定义 变量.指令编号 = 自动生成
            String fingerNo = getTradeid(stl.getOrderType(), dealDate, "B");
            // 结算指令表(STL_ORDERS)
            valOrders.setOrderId(fingerNo);
            valOrders.setBusiType(dto.getBusiType());
            valOrders.setOrderType(stl.getOrderType());
            valOrders.setSettleDate(dealDate);
            valOrders.setTradeId(dto.getTradeId());
            valOrders.setMyAcctNo(myAcctNo);
            valOrders.setPs(ps);
            valOrders.setCounterAcctNo(counterAcctNo);
            valOrders.setSettleAmt(settleAmt);
            valOrders.setCcy(dto.getCcy());
            valOrders.setSettleStatus(DictCodeList.成功失败标志_成功.getValue());
            valOrders.setOrderStatus(dto.getTradeStatus());
            valOrders.setpOrderId(pOrderId);
            listStlOrders.add(valOrders);
            
            //结算指令扩展表(VAL_ORDERS_EXT)
            valOrdersExt.setOrderId(fingerNo);
            valOrdersExt.setIsTrade(stl.getIsBook());
            listStlOrdersExt.add(valOrdersExt);
            
            if(DictCodeList.是否_是.getValue().equals(stl.getIsBook())){
            	continue;
            }
            /** add by lby on 20201225 四级科目尾号集合 start**/
            //定义 变量.四级科目尾号集合
            Map<String, BokDetailId> detailSubjectNoTailMap= new HashMap<>();
            /** add by lby on 20201225 四级科目尾号集合 end**/
            for (ValBooksetInfoDto bokset : boksetList) {
            	/**
                 * 入账信息初始化
                 */
                // 定义 变量.入账信息Dto
                BokDealAccounting valDealAccounting = new BokDealAccounting();
                // 账套代码 BOOKSET_ID
                valDealAccounting.setBooksetId(bokset.getBooksetId());
                // 业务编号 BUSI_NO 变量.指令编号
                valDealAccounting.setBusiNo(fingerNo);
                // 业务表 BUSI_TABLE 固定值：01 结算指令表
                valDealAccounting.setBusiTable(DictCodeList.业务表_结算指令表.getValue());
                // 录入日 BOOKSET_DATE 变量.结算日期
                valDealAccounting.setBooksetDate(dealDate);
                // 入账日 BOOK_DATE 变量.结算日期
                valDealAccounting.setBookDate(dealDate);
                // 实际发生日 HAPPEN_DATE 变量.结算日期
                valDealAccounting.setHappenDate(dealDate);
                // 是否已入账 IS_ACCOUNTED N 否
                valDealAccounting.setIsAccounted(DictCodeList.是否_否.getValue());
                listBokDealAccounting.add(valDealAccounting);
            	
                /**
                 * 入账账务明细初始化
                 */
                // 定义 变量.入账账务明细Dto
                BokDealAccountingDetail valDealAccountingDetail = new BokDealAccountingDetail();
                // 账套代码 BOOKSET_ID
                valDealAccountingDetail.setBooksetId(bokset.getBooksetId());
                // 业务编号 BUSI_NO 变量.指令编号
                valDealAccountingDetail.setBusiNo(fingerNo);
                // 业务表 BUSI_TABLE 固定值：01 结算指令表
                valDealAccountingDetail.setBusiTable(DictCodeList.业务表_结算指令表.getValue());
                // 业务类型 BUSI_TYPE 输入.金融产品交易.业务类型
                valDealAccountingDetail.setBusiType(dto.getBusiType());
                listBokDealAccountingDetail.add(valDealAccountingDetail);
                
                
                /**
                 * 3.入账账务明细初始化
                 */
                // 定义 变量.账务明细代码表Dto
                BokDetailId valBokDetailId = new BokDetailId();
                // 账套代码 BOOKSET_ID 输入.交易标的Dto.账套代码
                valBokDetailId.setBooksetId(bokset.getBooksetId());
                // 业务明细代码 BUSI_ID 变量.本方资金账户
                valBokDetailId.setBusiId(myAcctNo);
                // 业务明细类型 BUSI_DETAIL_TYPE 变量.本方账户类型
                valBokDetailId.setBusiDetailType(myAcctType);
                // 投资目的 INV_AIM 输入.金融产品交易.投资目的
                valBokDetailId.setInvAim(null);
                // 交易编号 TRADE_ID 输入.交易标的Dto.交易编号
                valBokDetailId.setTradeId(dto.getTradeId());
                //资金账户初始化需要带币种
                valBokDetailId.setCcy(dto.getCcy());
                String detailId = this.genMd5BokDetailId(valBokDetailId);
                valBokDetailId.setBokDetailId(detailId);
                valDealAccountingDetail.setBokDetailId(detailId);
                // 币种 变量.交易标的DTO.币种
                valBokDetailId.setCcy(dto.getCcy());
                /** add by lby on 20201225 四级科目尾号集合 start**/
                //定义 变量.bokDetailId = 变量.四级科目尾号集合.get(BOK.账套代码+变量.账务明细代码)
                BokDetailId bokDetailId = detailSubjectNoTailMap.get(bokset.getBooksetId() + valBokDetailId.getBokDetailId());
                //IF 变量.bokDetailId IS NULL
                if (!StringUtil.isNotEmpty(bokDetailId)){
                    //调用共通设计->【详细设计-接口共通方法设计.xlsx】->四级科目号设置SHEET
                    //变量.bokDetailId = 返回值
                    bokDetailId = this.settingDetailSubjectNoTaiL(myAcctNo, myAcctType, null, null, bokset.getBooksetId());
                }
                //变量.四级科目尾号集合.put(BOK.账套代码+变量.账务明细代码,变量.bokDetailId)
                detailSubjectNoTailMap.put(bokset.getBooksetId() + valBokDetailId.getBokDetailId(),bokDetailId);
                //变量.账务明细代码表Dto.四级科目尾号 = 变量.bokDetailId.四级科目尾号
                valBokDetailId.setDetailSubjectNoTail(bokDetailId.getDetailSubjectNoTail());
                //变量.账务明细代码表Dto.四级科目尾号名称 = 变量.bokDetailId.四级科目尾号名称
                valBokDetailId.setDetailSubnameTail(bokDetailId.getDetailSubnameTail());
                /** add by lby on 20201225 四级科目尾号集合 end**/
                listBokDetailId.add(valBokDetailId);
                
                /**ADD BY WUJIE ON 20190929 START**/
                if(DictCodeListExt1.资金账户类型_待清算款.getValue().equals(counterAcctType)){
                	 BokDetailId valBokDetailId_dsf = new BokDetailId();
                	 valBokDetailId_dsf.setBooksetId(bokset.getBooksetId());
                	 valBokDetailId_dsf.setBusiId(dto.getFinprodId());
                	 valBokDetailId_dsf.setBusiDetailType(dto.getFinprodType());
                	 valBokDetailId_dsf.setInvAim(tradeDeal.getInvAim());
                	 valBokDetailId_dsf.setTradeId(tradeDeal.getTradeId());
                	 valBokDetailId_dsf.setCcy(tradeDeal.getCcy());
                	 valBokDetailId_dsf.setBokDetailId(genMd5BokDetailId(valBokDetailId_dsf));

                	 /**四级科目尾号处理 START modify by lby 20201228
                	 BokDetailId dsf = this.settingDetailSubjectNoTaiL(dto.getFinprodId(), dto.getFinprodType(), branch, dto.getBusiType(),bokset.getBooksetId());
            		 valBokDetailId_dsf.setDetailSubnameTail(dsf.getDetailSubnameTail());
            		 valBokDetailId_dsf.setDetailSubjectNoTail(dsf.getDetailSubjectNoTail());
                	 四级科目尾号处理 END modify by lby 20201228**/
                    //定义 变量.bokDetailIdTail = 变量.四级科目尾号集合.get(BOK.账套代码+变量.对手方_账务明细代码)
                    BokDetailId bokDetailIdTail = detailSubjectNoTailMap.get(bokset.getBooksetId() + valBokDetailId_dsf.getBokDetailId());
                    if (!StringUtil.isNotEmpty(bokDetailIdTail)){
                        //调用共通设计->【详细设计-接口共通方法设计.xlsx】->四级科目号设置SHEET
                        //变量.bokDetailId = 返回值
                        bokDetailIdTail = this.settingDetailSubjectNoTaiL(dto.getFinprodId(), dto.getFinprodType(), 0L, tradeDeal.getBusiType(), bokset.getBooksetId());
                    }
                    //变量.四级科目尾号集合.put(BOK.账套代码+变量.对手方_账务明细代码,变量.bokDetailId)
                    detailSubjectNoTailMap.put(bokset.getBooksetId() + valBokDetailId_dsf.getBokDetailId(),bokDetailIdTail);
                    //变量.对手方_账务明细代码表DTO.四级科目尾号 = 变量.bokDetailId.四级科目尾号
                    valBokDetailId_dsf.setDetailSubnameTail(bokDetailIdTail.getDetailSubnameTail());
                    //变量.对手方_账务明细代码表DTO.四级科目尾号名称 = 变量.bokDetailId.四级科目尾号名称
                    valBokDetailId_dsf.setDetailSubjectNoTail(bokDetailIdTail.getDetailSubjectNoTail());
                     listBokDetailId.add(valBokDetailId_dsf);
                }
                /**ADD BY WUJIE ON 20190929 END IF**/
            }
            
        }
        
        resultMap.put("ORDERS", listStlOrders);
        resultMap.put("ORDERS_EXT", listStlOrdersExt);
        resultMap.put("ACCOUNTING", listBokDealAccounting);
        resultMap.put("ACCOUNTING_DETAI", listBokDealAccountingDetail);
        resultMap.put("BOK_DETAIL_ID", listBokDetailId);
    	
    	return resultMap;
    }
    
    /**
     * 通过前台指令生成结算指令入账
     * <功能详细描述>
     * @param tradeDeal 交易信息                                                                                                       
     * @param frontStlOrdersDto 指令信息
     * @param finprodId 产品代码
     * @param finprodType 产品类型
     * @param cbranch 分支序号：理财子产品的分支序号
     * @return
     * @see
     * @since 1.0
     */
    public Map<String,Object>  instructionGenerateByFront(TrdProductDeal tradeDeal, StlOrders frontStlOrdersDto, String finprodId,
                                                          String finprodType, Long cbranch, List<PtlTrade> ptlTradeList){
        //1.获取参数
        //1.1 输入参数            
        Map<String,Object>  resultMap = new HashMap<String,Object>();
        //1.2 定义变量            
        // 结算指令
        List<StlOrders> listStlOrders = new ArrayList<StlOrders>();
        // 结算指令扩展表
        List<StlOrdersExt> listStlOrdersExt = new ArrayList<StlOrdersExt>();
        // 入账信息表
        List<BokDealAccounting> listBokDealAccounting = new ArrayList<BokDealAccounting>();
        // 入账信息明细表
        List<BokDealAccountingDetail> listBokDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        // 账务明细代码表
        List<BokDetailId> listBokDetailId = new ArrayList<BokDetailId>();
        // 理财产品代码   = 输入.产品代码   
        String prodId = finprodId;
        
        //2.处理详情
        //IF 输入.交易信息.通道代码 IS NOT NULL
        if(StringUtil.isNotEmpty(tradeDeal.getChlId())){
            //输入.产品代码 += "_" + 输入.交易信息.通道代码
            finprodId =  prodId + FinalString.LAYERING_SPLIT + tradeDeal.getChlId();
            finprodType = DictCodeListExt1.金融产品类型_通道.getValue();
        }
        //IF 输入.产品类型 IS NULL 获取产品类型
        if(finprodType == null){
            //定义 变量.params
            Map<String,Object> params = new HashMap<String,Object>();
            //变量.params.put("finprodId",输入.产品代码)
            params.put("finprodId", finprodId);
            //调用共通设计->【详细设计-接口共通方法设计.xlsx】->产品、子产品、投资组合信息查询SHEET
            List<Prdbokstl> prdlist = valInterfCommonMapper.queryPrdbokstl(params);
            if(prdlist == null || prdlist.size() == 0){
                return resultMap;
            }
            //LOOP DTO L
            for(Prdbokstl prd : prdlist){
                //IF 数据字典：金融产品类型.理财子产品 eq L.金融产品类型
                if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(prd.getFinprodType())){
                    continue;
                }
                //ELSE IF 数据字典：金融产品类型.理财产品 eq L.金融产品类型
                else if(DictCodeListExt1.金融产品类型_理财产品.getValue().equals(prd.getFinprodType())){
                    finprodType = prd.getFinprodType();
                }
                //ELSE IF 数据字典：金融产品类型.投资组合 eq L.金融产品类型
                else if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(prd.getFinprodType())){
                    finprodType = prd.getFinprodType();
                }
                //ELSE IF 数据字典：金融产类型.通道 eq L.金融产品类型
                else if(DictCodeListExt1.金融产品类型_通道.getValue().equals(prd.getFinprodType())){
                    finprodType = prd.getFinprodType();
                }
            }
        }
        //IF 输入.产品类型 IS NULL
        if(finprodType == null){
            return resultMap;
        }
        //定义 变量.分支序号 = NULL
        Long branch = null;
        //IF 数据字典：金融产品类型.理财子产品 eq 输入.产品类型
        if(finprodType.equals(DictCodeListExt1.金融产品类型_理财子产品.getValue())){
            //变量.分支序号 = 输入.分支序号
            branch = cbranch;
        }
        //定义 变量.分层代码 = 输入.产品代码 + (变量.分支序号 ==NULL?"":变量.分支序号) + "_" + 输入.产品类型
        String prodIdLayering = finprodId + (branch == null ? "" : branch) + FinalString.LAYERING_SPLIT + finprodType;
        //定义 变量.核算主体查询账套信息 = 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息(根据产品代码) _分层产品SHEET
        List<ValBooksetInfoDto> boksetList = this.getBokBooksetByProdId_Layering(prodIdLayering);
        //IF 变量.核算主体查询账套信息 IS NULL OR 变量.核算主体查询账套信息.SIZE()==0
        if(boksetList == null || boksetList.size() == 0){
            return resultMap;
        }
        
        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(finprodType)
                /** modify by yanya on 20191122 防止chnlI为空字符串**/
        		//&& tradeDeal.getChlId() == null
                && StringUtil.isEmpty(tradeDeal.getChlId())
        		&& !DictCodeList.业务类型_净值型TA交易.getValue().equals(tradeDeal.getBusiType())
        		&& !DictCodeList.业务类型_货币型TA交易.getValue().equals(tradeDeal.getBusiType())
        		&& !DictCodeList.业务类型_预期收益型TA交易.getValue().equals(tradeDeal.getBusiType())
        		&& !DictCodeList.业务类型_产品费用交易.getValue().equals(tradeDeal.getBusiType())){
        	
            List<ValBooksetInfoDto>  valBooksetInfoDtos= getSonPtlPortfolo(finprodId,ptlTradeList);
            if (CollectionUtils.isNotEmpty(valBooksetInfoDtos)) {
            	boksetList.addAll(valBooksetInfoDtos);
            }
        }
        /**ADD BY WUJIE ON 20191118 START**/
        Map<String, ValBooksetInfoDto> qcMap = new HashMap<String, ValBooksetInfoDto>();
        List<ValBooksetInfoDto> bokSetList = new ArrayList<ValBooksetInfoDto>();
        for(ValBooksetInfoDto dto:boksetList){
        	if(qcMap.get(dto.getBooksetId())==null){
        		bokSetList.add(dto);
        	}
        	qcMap.put(dto.getBooksetId(), dto);
        }
        boksetList.clear();
        boksetList.addAll(bokSetList);
        /**ADD BY WUJIE ON 20191118 END**/
        
        
        //交易标的DTO初始化
        ProductDealDto dto = new ProductDealDto();
        //IF 数据字典：业务类型.增值税交易 eq 输入.交易信息.业务类型     or 数据字典：业务类型.费用交易 eq 输入.交易信息.业务类型  or 数据字典：业务类型.活期存款交易 eq 输入.交易信息.业务类型        
        if(DictCodeList.业务类型_增值税交易.getValue().equals(tradeDeal.getBusiType())
            || DictCodeList.业务类型_产品费用交易.getValue().equals(tradeDeal.getBusiType())
            || DictCodeList.业务类型_活期存款交易.getValue().equals(tradeDeal.getBusiType())){
            // 金融产品代码
            dto.setFinprodId(finprodId);
            //金融产品类型
            dto.setFinprodType(finprodType);
        }
        else {
            // 金融产品代码
            dto.setFinprodId(tradeDeal.getFinprodId());
            //金融产品类型
            dto.setFinprodType(tradeDeal.getFinprodType());
        }
        // 交易编号
        dto.setTradeId(tradeDeal.getTradeId());
        // 业务类型
        dto.setBusiType(tradeDeal.getBusiType());
        // 交易方向
        dto.setPs(tradeDeal.getPs());
        // 交易日
        dto.setTradeDate(tradeDeal.getTradeDate());
        // 交割日
        dto.setEffDate(tradeDeal.getSettleDate());
        // 交易金额
        dto.setTardeAmt(tradeDeal.getTradeAmt());
        // 币种
        dto.setCcy(tradeDeal.getCcy());
        //投资目的
        dto.setInvAim(tradeDeal.getInvAim());
        // 交易状态
        dto.setTradeStatus(tradeDeal.getTradeStatus());
        //交易市场
        dto.setTradeMarketMode(tradeDeal.getTradeMarketMode());
        //交易场所
        dto.setTradeMarket(tradeDeal.getTradeMarket());
        //原交易编号
        dto.setpTradeId(tradeDeal.getpTradeId());
        // 理财产品代码
        dto.setProdId(finprodId);
        //账套信息List
        dto.setValBooksetInfoDtoList(boksetList);
        //映射类型 02
        dto.setMappingType("02");
        //全价总额
        dto.setFpriceAmt(tradeDeal.getFpriceAmt());
        /**add by yinya 通过接口中是否生成结算指令的字段，控制接入交易的时候，只自动生成随交易支付(IS_BOOK为Y)的结算指令**/
        if(StringUtil.equals(tradeDeal.getIsOrders(), DictCodeList.是否_否.getValue())){
            dto.setIsTrade(DictCodeList.是否_是.getValue());
        }
        //定义 变量.参数条件Map = null
        Map<String,String> paramMap = new HashMap<String,String>();
        
        if(tradeDeal.getTradeMarket()!=null && tradeDeal.getTradeMarket().length()>0){
        	paramMap.put("TRADE_MARKET", tradeDeal.getTradeMarket());
        }
        /**ADD BY WUJIE ON 20200205 START**/
        if(DictCodeListExt3.金融产品类型_期权.getValue().equals(tradeDeal.getFinprodType())){
        	FinTradeProductKey fkey  = new FinTradeProductKey();
        	fkey.setFinprodId(tradeDeal.getFinprodId());
        	fkey.setBranch(InterfCommonConstant.分支序号_默认);
        	FinTradeProduct finTradeProduct = finTradeProductMapper.selectByPrimaryKey(fkey);
        	paramMap.put("EXC_PS", finTradeProduct==null?null:finTradeProduct.getExcPs());
            /** add by yanya on 20200212 增加期权的收付方向当作参数条件**/
            //变量.交易标的DTO.参数条件Map.put("PS_PAYMENT",输入.交易信息.收付方向)
            paramMap.put("PS_PAYMENT", tradeDeal.getPsPayment());
        }
        /**ADD BY WUJIE ON 20200205 END**/

        /** add by yanya on 20201222 其他债券跟计划类指令一样，这里增加是否其他债券参数区分 start**/
        //判定为计划类资产 且 业务类型为资产类交易
        if(StringUtil.equals(DictCodeListExt1.金融产品类型_计划类资产.getValue(), tradeDeal.getFinprodType())
                && StringUtil.equals(DictCodeList.业务类型_计划类资产交易_类债券资产交易.getValue(), tradeDeal.getBusiType())) {
            //定义 是否其他债券 默认为否
            String 是否其他债券 = DictCodeList.是否_否.getValue();
            //定义 变量.标的类金融产品附属DTO = 查询标的类金融产品附属表
            FinProductAdd finProductAddDto = finProductAddMapper.selectByPrimaryKey(tradeDeal.getFinprodId());
            //IF 变量.标的类金融产品附属DTO IS NOT NULL AND 变量.标的类金融产品附属DTO.是否其他债权 IS NOT NULL
            if(finProductAddDto != null && StringUtil.isNotEmpty(finProductAddDto.getIsQtzq())) {
                //变量.是否其他债权 = 变量.标的类金融产品附属DTO.是否其他债权
                是否其他债券 = finProductAddDto.getIsQtzq();
            }
            //变量.交易标的DTO.参数条件Map.put("IS_QTZQ",变量.是否其他债权)
            paramMap.put("IS_QTZQ", 是否其他债券);
        }
        /** add by yanya on 20201222 其他债券跟计划类指令一样，这里增加是否其他债券参数区分 end**/
        
        //变量.交易标的DTO.参数条件Map  = 变量.参数条件Map
        dto.setParamMap(paramMap);

        //查询交易需要生成的指令
        //定义 变量.交易对应指令集合 = 调用共通设计->【详细设计-接口共通方法设计.xlsx】->查询交易需要生成的指令SHEET
        List<StlOrdersMapping> somlist = this.queryStlOrdersMapping(dto);
        //IF 变量.交易对应指令集合 IS NULL OR 变量.交易对应指令集合.SIZE()==0
        if(somlist==null || somlist.size()==0){
            return resultMap;
        }
        //LOOP 变量.交易对应指令集合 L
        for(StlOrdersMapping stl :somlist){
        	//定义 变量.结算金额字段值 = 0
            BigDecimal settleAmt = this.getSettlementAmount(dto, stl.getSettlamtFieldcode());
            // 定义 变量.结算指令Dto
            StlOrders valOrders = new StlOrders();
            // 定义 变量.结算指令扩展Dto
            StlOrdersExt valOrdersExt = new StlOrdersExt();
            // 定义 变量.结算日期 
            Date dealDate = null;
            // 本方账户类型
			String myAcctType = stl.getMyAcctType();
            //对手方账户类型
            String counterAcctType = null;
            // 定义 变量.收付方向
            String ps =null;
            //定义 变量.本方资金账户
            String myAcctNo = null;
            //定义 变量.对方资金账户 
            String counterAcctNo = null;
            
            if(DictCodeList.是否_是.getValue().equals(stl.getIsBook())){
//            	settleAmt = this.getSettlementAmount(dto, stl.getSettlamtFieldcode());
            	dealDate = dto.getTradeDate();
            	if("02".equals(stl.getDateType())){
            		dealDate = dto.getEffDate();
            	}
            }else{
//            	if(frontStlOrdersDto.getSettleAmt()==null){
//            		settleAmt = frontStlOrdersDto.getSettleAmt();
//            	}
//            	else{
//            		settleAmt = this.getSettlementAmount(dto, stl.getSettlamtFieldcode());
//            	}
            	dealDate = frontStlOrdersDto.getSettleDate();
            }
            
            myAcctType = stl.getMyAcctType();
            
            counterAcctType = stl.getCounterAcctType();
            
            ps = stl.getPs();
            
            //本方账户、本方账户类型
            if("PAY".equals(myAcctType) || "INCOME".equals(myAcctType)){
//            	if(frontStlOrdersDto.getSettleAmt()==null){
//            		settleAmt = frontStlOrdersDto.getSettleAmt();
//            	}
//            	else{
//            		settleAmt = this.getSettlementAmount(dto, stl.getSettlamtFieldcode());
//            	}
            	dealDate = frontStlOrdersDto.getSettleDate();
            	
            	myAcctNo = "PAY".equals(myAcctType)?frontStlOrdersDto.getMyAcctNo():frontStlOrdersDto.getCounterAcctNo();
            	
            	//根据账套代码、资金账户、币种查询 资金账户类型
                Map<String,Object> myAcctNoParamMap = new HashMap<String,Object>();
                //变量.params.put("booksetId",变量.核算主体查询账套信息.get(0).账套代码)
                myAcctNoParamMap.put("booksetId", boksetList.get(0).getBooksetId());
                //变量.params.put("ccy",变量.交易标的DTO.币种)
                myAcctNoParamMap.put("ccy", dto.getCcy());
                //变量.params.put("acctNo,变量.本方资金账户)
                myAcctNoParamMap.put("acctNo", myAcctNo);
                //调用共通设计->【详细设计-接口共通方法设计.xlsx】->查询账套清算款账户SHEET
                BokBooksetCapacctInfo capacctInfo = mapper.queryBokBooksetCapacctInfo(myAcctNoParamMap);
                if(capacctInfo != null){
                    //变量.本方账户类型 = DTO.账户类型
                    myAcctType = capacctInfo.getAcctType();
                }
            }
            else{
            	//根据账套代码、资金账户类型、币种查询 资金账户
                Map<String,Object> myAcctNoParamMap = new HashMap<String,Object>();
                //变量.params.put("booksetId",变量.核算主体查询账套信息.get(0).账套代码)
                myAcctNoParamMap.put("booksetId", boksetList.get(0).getBooksetId());
                //变量.params.put("ccy",变量.交易标的DTO.币种)
                myAcctNoParamMap.put("ccy", dto.getCcy());
                //变量.params.put("acctNo,变量.本方资金账户)
                myAcctNoParamMap.put("acctType", myAcctType);
                //调用共通设计->【详细设计-接口共通方法设计.xlsx】->查询账套清算款账户SHEET
                BokBooksetCapacctInfo capacctInfo = mapper.queryBokBooksetCapacctInfo(myAcctNoParamMap);
                if(capacctInfo != null){
                	myAcctNo = capacctInfo.getAcctNo();
                }
            }
            
            //对手方资金账户、资金账户类型
            if(StringUtil.isNotEmpty(counterAcctType)){
            	Map<String,Object> counterAcctNoParamMap = new HashMap<String,Object>();
                //变量.params.put("booksetId",变量.核算主体查询账套信息.get(0).账套代码)
                counterAcctNoParamMap.put("booksetId", boksetList.get(0).getBooksetId());
                //变量.params.put("ccy",变量.交易标的DTO.币种)
                counterAcctNoParamMap.put("ccy", dto.getCcy());
                //变量.params.put("acctNo,变量.对方资金账户)
                counterAcctNoParamMap.put("acctType", counterAcctType);
                //调用共通设计->【详细设计-接口共通方法设计.xlsx】->查询账套清算款账户SHEET
                BokBooksetCapacctInfo counterAcctNoInfo = mapper.queryBokBooksetCapacctInfo(counterAcctNoParamMap);
                if(counterAcctNoInfo != null){
                    //变量.本方账户类型 = DTO.账户
                	counterAcctNo = counterAcctNoInfo.getAcctNo();
                }
            }
            if(DictCodeListExt3.指令类型_债券类资产_申购划款.getValue().equals(stl.getOrderType())
            		|| DictCodeListExt3.指令类型_债券类资产_申购退款.getValue().equals(stl.getOrderType())
            		/**add by wujie on 20210528 start**/
            		|| DictCodeListExt3.指令类型_债券类资产_申购确认.getValue().equals(stl.getOrderType())
            		/**add by wujie on 20210528 end**/
            		){
            	counterAcctType = DictCodeListExt1.资金账户类型_交易保证金账户.getValue();
            	counterAcctNo = frontStlOrdersDto.getCounterAcctNo();
            }
            // 2020年2月12日 魏鹏：结算日期为空时，不生成指令，直接返回
            if(!StringUtil.isNotEmpty(dealDate)){
                continue;
            }
            // 定义 变量.指令编号 = 自动生成
            String fingerNo = getTradeid(stl.getOrderType(), dealDate, "B");
            //变量.结算指令Dto
            valOrders.setOrderId(fingerNo);   
            //业务类型  =  变量.交易标的DTO.业务类型
            valOrders.setBusiType(dto.getBusiType());
            //指令类型   =     L.指令类型
            valOrders.setOrderType(stl.getOrderType());
            //结算日期   =     变量.结算日期                            
            valOrders.setSettleDate(dealDate);
            //交易编号    =     变量.交易标的DTO.交易编号 
            valOrders.setTradeId(dto.getTradeId());
            //本方资金账户  =         变量.本方资金账户 
            valOrders.setMyAcctNo(myAcctNo);
            //收付方向     =       变量.收付方向                       
            valOrders.setPs(ps);
            //对方资金账户  =         变量.对方资金账户                   
            valOrders.setCounterAcctNo(counterAcctNo);
            //结算金额       =         变量.交易标的DTO.交易金额         
            valOrders.setSettleAmt(settleAmt);
            //币种          =      变量.交易标的DTO.币种                        
            valOrders.setCcy(dto.getCcy());
            //结算状态     =            数据字典：结算状态.结算成功  
            valOrders.setSettleStatus(DictCodeList.成功失败标志_成功.getValue());
            //指令状态         =   变量.交易标的DTO.交易状态         
            valOrders.setOrderStatus(dto.getTradeStatus());
            //原指令编号    =             变量.原指令编号                            
            valOrders.setpOrderId(dto.getpTradeId());
            //变量.结算指令.add(变量.结算指令Dto)
            listStlOrders.add(valOrders);
            //变量.结算指令扩展Dto
            //指令编号 = 变量.指令编号
            valOrdersExt.setOrderId(fingerNo);
            //是否随交易出账 = L.是否随交易出账
            valOrdersExt.setIsTrade(stl.getIsBook());
            //变量.结算指令扩展表.add(变量.结算指令扩展Dto)
            listStlOrdersExt.add(valOrdersExt);
            //IF 数据字典：是否.是 eq  L.是否随交易出账
            if(DictCodeList.是否_是.getValue().equals(stl.getIsBook())){
                continue;
            }
            /** add by lby on 20201225 四级科目尾号集合 start**/
            //定义 变量.四级科目尾号集合
            Map<String, BokDetailId> detailSubjectNoTailMap= new HashMap<>();
            /** add by lby on 20201225 四级科目尾号集合 end**/
            //LOOP 变量.核算主体查询账套信息 BOK
            for (ValBooksetInfoDto bokset : boksetList) {
                //定义 变量.入账信息Dto
                BokDealAccounting valDealAccounting = new BokDealAccounting();
                // 账套代码 BOOKSET_ID
                valDealAccounting.setBooksetId(bokset.getBooksetId());
                // 业务编号 BUSI_NO 变量.指令编号
                valDealAccounting.setBusiNo(fingerNo);
                // 业务表 BUSI_TABLE 固定值：01 结算指令表
                valDealAccounting.setBusiTable(DictCodeList.业务表_结算指令表.getValue());
                // 录入日 BOOKSET_DATE 变量.结算日期
                valDealAccounting.setBooksetDate(dealDate);
                // 入账日 BOOK_DATE 变量.结算日期
                valDealAccounting.setBookDate(dealDate);
                // 实际发生日 HAPPEN_DATE 变量.结算日期
                valDealAccounting.setHappenDate(dealDate);
                // 是否已入账 IS_ACCOUNTED N 否
                valDealAccounting.setIsAccounted(DictCodeList.是否_否.getValue());
                listBokDealAccounting.add(valDealAccounting);
                
                //定义 变量.账务明细代码表Dto
                BokDetailId valBokDetailId = new BokDetailId();
                // 账套代码 BOOKSET_ID
                valBokDetailId.setBooksetId(bokset.getBooksetId());
                //业务明细代码 = 变量.本方资金账户
                valBokDetailId.setBusiId(myAcctNo);
                //业务明细类型 = 变量.本方资金类型
                valBokDetailId.setBusiDetailType(myAcctType);
                //交易编号 = 变量.交易标的DTO.交易编号
                valBokDetailId.setTradeId(dto.getTradeId());
                //分支序号 = 0
                valBokDetailId.setBranch(0L);
                //币种 = 变量.交易标的DTO.币种
                valBokDetailId.setCcy(dto.getCcy());
                //定义 变量.账务明细代码 = 调用共通设计->【详细设计-接口共通方法设计.xlsx】->生成MD5账务明细代码SHEET
                String bokDetailId = this.genMd5BokDetailId(valBokDetailId);
                valBokDetailId.setBokDetailId(bokDetailId);
                /** add by lby on 20201225 四级科目尾号集合 start**/
                //定义 变量.bokDetailId = 变量.四级科目尾号集合.get(BOK.账套代码 +变量.账务明细代码)
                BokDetailId bokDetailIdTaiL = detailSubjectNoTailMap.get(bokset.getBooksetId() + valBokDetailId.getBokDetailId());
                //IF 变量.bokDetailId IS NULL
                if (!StringUtil.isNotEmpty(bokDetailIdTaiL)){
                    //调用共通设计->【详细设计-接口共通方法设计.xlsx】->四级科目号设置SHEET
                    //变量.bokDetailId = 返回值
                    bokDetailIdTaiL = this.settingDetailSubjectNoTaiL(myAcctNo, myAcctType, null, null, bokset.getBooksetId());
                }
                //变量.四级科目尾号集合.put(BOK.账套代码 +变量.账务明细代码,变量.bokDetailId)
                detailSubjectNoTailMap.put(bokset.getBooksetId() + valBokDetailId.getBokDetailId(),bokDetailIdTaiL);
                //变量.账务明细代码表Dto.四级科目尾号 = 变量.bokDetailId.四级科目尾号
                valBokDetailId.setDetailSubjectNoTail(bokDetailIdTaiL.getDetailSubjectNoTail());
                //变量.账务明细代码表Dto.四级科目尾号名称 = 变量.bokDetailId.四级科目尾号名称
                valBokDetailId.setDetailSubnameTail(bokDetailIdTaiL.getDetailSubnameTail());
                /** add by lby on 20201225 四级科目尾号集合 end**/
                listBokDetailId.add(valBokDetailId);
                
                // 定义 变量.入账账务明细Dto
                BokDealAccountingDetail valDealAccountingDetail = new BokDealAccountingDetail();
                // 账套代码 BOOKSET_ID
                valDealAccountingDetail.setBooksetId(bokset.getBooksetId());
                // 业务编号 BUSI_NO = 变量.指令编号
                valDealAccountingDetail.setBusiNo(fingerNo);
                // 业务表 BUSI_TABLE = 固定值：01 结算指令表
                valDealAccountingDetail.setBusiTable(DictCodeList.业务表_结算指令表.getValue());
                // 业务类型 BUSI_TYPE = 变量.交易标的DTO.业务类型
                valDealAccountingDetail.setBusiType(dto.getBusiType());
                //账务明细代码
                valDealAccountingDetail.setBokDetailId(bokDetailId);
                listBokDealAccountingDetail.add(valDealAccountingDetail);

//                //ADD JINMAODONG 债转股交易需要生成股票的账务明细代码 20191214
//                if(DictCodeListExt1.交易方向_债转股.getValue().equals(tradeDeal.getPs())){
//                    List<TrdFinProductBarter> barters = trdFinProductBarterAccountMapper.queryTrdFinProductBarterAccount(tradeDeal.getTradeId());
//                    if(CollectionUtils.isNotEmpty(barters)){
//                        TrdFinProductBarter barter = barters.get(0);
//                        //定义 变量.账务明细代码表Dto
//                        BokDetailId valBokDetailId2 = new BokDetailId();
//                        // 账套代码 BOOKSET_ID
//                        valBokDetailId2.setBooksetId(bokset.getBooksetId());
//                        //业务明细代码 = 变量.本方资金账户
//                        valBokDetailId2.setBusiId(barter.getFinprodId());
//                        //业务明细类型 = 变量.本方资金类型
//                        valBokDetailId2.setBusiDetailType(barter.getFinprodType());
//                        //交易编号 = 变量.交易标的DTO.交易编号
//                        valBokDetailId2.setTradeId(dto.getTradeId());
//                        //分支序号 = 0
//                        valBokDetailId2.setBranch(barter.getBranch());
//                        //定义 变量.账务明细代码 = 调用共通设计->【详细设计-接口共通方法设计.xlsx】->生成MD5账务明细代码SHEET
//                        String bokDetailId2 = this.genMd5BokDetailId(valBokDetailId2);
//                        valBokDetailId2.setBokDetailId(bokDetailId2);
//                        listBokDetailId.add(valBokDetailId2);
//                    }
//                }
//                //ADD JINMAODONG 债转股交易需要生成股票的账务明细代码 20191214

                /**ADD BY WUJIE ON 20191026 START**/
                if(DictCodeListExt1.资金账户类型_待清算款.getValue().equals(counterAcctType)){
                	 BokDetailId valBokDetailId_dsf = new BokDetailId();
                	 valBokDetailId_dsf.setBooksetId(bokset.getBooksetId());
                	 valBokDetailId_dsf.setBusiId(dto.getFinprodId());
                	 valBokDetailId_dsf.setBusiDetailType(dto.getFinprodType());
                	 valBokDetailId_dsf.setInvAim(tradeDeal.getInvAim());
                	 valBokDetailId_dsf.setTradeId(tradeDeal.getTradeId());
                	 valBokDetailId_dsf.setCcy(tradeDeal.getCcy());
                	 valBokDetailId_dsf.setBokDetailId(genMd5BokDetailId(valBokDetailId_dsf));
                	 /*BokDetailId dsf = this.settingDetailSubjectNoTaiL(dto.getFinprodId(), dto.getFinprodType(), branch, dto.getBusiType(),bokset.getBooksetId());
            		 valBokDetailId_dsf.setDetailSubnameTail(dsf.getDetailSubnameTail());
            		 valBokDetailId_dsf.setDetailSubjectNoTail(dsf.getDetailSubjectNoTail());*/
                	 //定义 变量.bokDetailId = 变量.四级科目尾号集合.get(BOK.账套代码 + 变量.对手方_账务明细代码)
                     BokDetailId bokDetailIdTmp = detailSubjectNoTailMap.get(bokset.getBooksetId() + valBokDetailId_dsf.getBokDetailId());
                    if (!StringUtil.isNotEmpty(bokDetailIdTmp)){
                        //调用共通设计->【详细设计-接口共通方法设计.xlsx】->四级科目号设置SHEET
                        BokDetailId bokDetailId3 = this.settingDetailSubjectNoTaiL(dto.getFinprodId(), dto.getFinprodType(), 0L, tradeDeal.getBusiType(), bokset.getBooksetId());
                        //变量.bokDetailId = 返回值
                        bokDetailIdTmp = bokDetailId3;
                    }
                    //变量.四级科目尾号集合.put(BOK.账套代码+变量.账务明细代码,变量.bokDetailId)
                    detailSubjectNoTailMap.put(bokset.getBooksetId() + valBokDetailId_dsf.getBokDetailId(),bokDetailIdTmp);
                    //变量.对手方_账务明细代码表DTO.四级科目尾号 = 变量.bokDetailId.四级科目尾号
                    valBokDetailId_dsf.setDetailSubjectNoTail(bokDetailIdTmp.getDetailSubjectNoTail());
                    //变量.对手方_账务明细代码表DTO.四级科目尾号名称 = 变量.bokDetailId.四级科目尾号名称
                    valBokDetailId_dsf.setDetailSubnameTail(bokDetailIdTmp.getDetailSubnameTail());
                     listBokDetailId.add(valBokDetailId_dsf);
                }
                /**ADD BY WUJIE ON 20191026 END IF**/
                
                /**ADD BY WUJIE ON 20200110 START**/
                if(DictCodeListExt1.资金账户类型_交易保证金账户.getValue().equals(counterAcctType)){
                	 BokDetailId valBokDetailId_dsf = new BokDetailId();
                	 valBokDetailId_dsf.setBooksetId(bokset.getBooksetId());
                	 valBokDetailId_dsf.setBusiId(counterAcctNo);
                	 valBokDetailId_dsf.setBusiDetailType(counterAcctType);
                	 valBokDetailId_dsf.setTradeId(tradeDeal.getTradeId());
                	 valBokDetailId_dsf.setCcy(tradeDeal.getCcy());
                	 valBokDetailId_dsf.setBokDetailId(genMd5BokDetailId(valBokDetailId_dsf));
                    /** add by lby on 20201225 四级科目尾号集合 start**/
                    //定义 变量.bokDetailId = 变量.四级科目尾号集合.get(BOK.账套代码 + 变量.对手方_账务明细代码)
                    BokDetailId bokDetailIdTaiL2 = detailSubjectNoTailMap.get(bokset.getBooksetId() + valBokDetailId_dsf.getBokDetailId());
                    //IF 变量.bokDetailId IS NULL
                    if (!StringUtil.isNotEmpty(bokDetailIdTaiL2)){
                        //调用共通设计->【详细设计-接口共通方法设计.xlsx】->四级科目号设置SHEET
                        //变量.bokDetailId = 返回值
                        bokDetailIdTaiL2 = this.settingDetailSubjectNoTaiL(counterAcctNo, counterAcctType, null, null, bokset.getBooksetId());
                    }
                    //变量.四级科目尾号集合.put(BOK.账套代码 + 变量.对手方_账务明细代码,变量.bokDetailId)
                    detailSubjectNoTailMap.put(bokset.getBooksetId() + valBokDetailId_dsf.getBokDetailId(),bokDetailIdTaiL2);
                    //变量.交易保证金账户_账务明细代码表DTO.四级科目尾号 =	变量.bokDetailId.四级科目尾号
                    valBokDetailId_dsf.setDetailSubjectNoTail(bokDetailIdTaiL2.getDetailSubjectNoTail());
                    //变量.交易保证金账户_账务明细代码表DTO.四级科目尾号名称 =	变量.bokDetailId.四级科目尾号名称
                    valBokDetailId_dsf.setDetailSubnameTail(bokDetailIdTaiL2.getDetailSubnameTail());
                    /** add by lby on 20201225 四级科目尾号集合 end**/
                     listBokDetailId.add(valBokDetailId_dsf);
                }
                /**ADD BY WUJIE ON 20200110 END IF**/
            }
        }
        
        //3. 初始化完成的账套信息保存至resultMap对象中,并返回
        //变量.resultMap.put("ORDERS",变量.结算指令)
        resultMap.put("ORDERS", listStlOrders);
        //变量.resultMap.put("ORDERS_EXT",变量.结算指令扩展表)
        resultMap.put("ORDERS_EXT", listStlOrdersExt);
        //变量.resultMap.put("ACCOUNTING",变量.入账信息表)
        resultMap.put("ACCOUNTING", listBokDealAccounting);
        //变量.resultMap.put("ACCOUNTING_DETAI",变量.入账信息明细表)
        resultMap.put("ACCOUNTING_DETAI", listBokDealAccountingDetail);
        //变量.resultMap.put("BOK_DETAIL_ID",变量.账务明细代码表)
        resultMap.put("BOK_DETAIL_ID", listBokDetailId);
        return resultMap;
    }
    
    /**
     * 查询交易需要生成的指令
     * <功能详细描述>
     * @param productDealDto
     * @return
     * @see
     * @since 1.0
     */
    public List<StlOrdersMapping> queryStlOrdersMapping(ProductDealDto productDealDto){
    	List<StlOrdersMapping> rstlist = new ArrayList<StlOrdersMapping>();
    	String channelAsset = DictCodeList.是否_否.getValue();
    	if(StringUtil.equals(productDealDto.getFinprodType(), DictCodeListExt1.金融产品类型_计划类资产.getValue())
    			&& StringUtil.equals(productDealDto.getBusiType(), DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue())){
    		TargetFinProduct targetFinProduct = getTargetFinProduct(productDealDto.getFinprodId());
    		if(targetFinProduct != null && StringUtil.equals(DictCodeList.是否_是.getValue(), targetFinProduct.getIsChl())){
    			channelAsset = DictCodeList.是否_是.getValue();
    		}
    	}
    	productDealDto.setChannelAsset(channelAsset);
    	List<StlOrdersMapping> list = mapper.queryStlOrdersMapping(productDealDto);
    	if(list==null || list.size()==0){
    		return list;
    	}
    	for(StlOrdersMapping obj :list){
    		/**modify by wujie on 20191107 start
    		if(obj.getParaCode()==null){
    			rstlist.add(obj);
    		}else{
    			String paraValue = valSysParaService.findParamValue(obj.getParaCode());
    			if(obj.getParamValue().equals(paraValue)){
    				rstlist.add(obj);
    			}
    		}
    		**/
    		boolean isFlag = true;
    		if(obj.getParaCode()!=null){
    			String paraValue = valSysParaService.findParamValue(obj.getParaCode());
    			if(!obj.getParamValue().equals(paraValue)){
    				isFlag = false;
    			}
    		}
    		Map<String,String> paramMap = productDealDto.getParamMap()==null?new HashMap<String,String>():productDealDto.getParamMap();
    		if(obj.getParams()!=null && obj.getParams().length()>0){
    			Map<String,String> rstMap = this.parameterConditionAnalysis(obj.getParams());
    			for(String key :rstMap.keySet()){
    				if(rstMap.get(key)==null || !rstMap.get(key).equals(paramMap.get(key))){
    					isFlag = false;
    				}
    			}
    		}
    		if(isFlag){
    			rstlist.add(obj);
    		}
    		/**add by wujie on 20191107 end**/
    	}
    	return rstlist;
    }

    /**
     * 批量删除交易
     * @param paramMap
     * @param interfDate
     */
    @SuppressWarnings("unchecked")
    public void dealBatchTrdFinprodDeal(Map<String,Object> paramMap,String interfDate){
        String finprodType = null;
        if(paramMap.get("finprodType") != null){
            finprodType = (String)paramMap.get("finprodType");
        }
        String tmpEName = paramMap.get("TMP_E_NAME")==null?null:(String)paramMap.get("TMP_E_NAME");
        String isChl = null;
        if(paramMap.get("IS_CHL") != null){
            isChl = (String)paramMap.get("IS_CHL");
        }
        Map<String,Object> parmas = new HashMap<String,Object>();
        parmas.put("createTime", interfDate);
        parmas.put("createUser", InterfCommonConstant.接口_默认用户);
        parmas.put("busiType", paramMap.get("busiType"));
        parmas.put("busiTypelist", paramMap.get("busiTypelist"));
        parmas.put("pslist", paramMap.get("pslist"));
        parmas.put("pslistNot", paramMap.get("pslistNot"));
        parmas.put("ps", paramMap.get("ps"));
        parmas.put("profitType", paramMap.get("profitType"));
        parmas.put("finprodType", finprodType);
        parmas.put("assetProfitType", paramMap.get("assetProfitType"));
        parmas.put(InterfCommonConstant.接口_运行接口账套ID, paramMap.get(InterfCommonConstant.接口_运行接口账套ID));
        parmas.put("IS_CHL", isChl);
        parmas.put("TMP_E_NAME", tmpEName);

        List<TrdProductDeal> unDelDealList = this.queryUnDelDeal(parmas);

        if(DictCodeListExt1.金融产品类型_基金.getValue().equals(finprodType)){
            parmas = new HashMap<String,Object>();
            parmas.put("createTime", interfDate);
            parmas.put("createUser", InterfCommonConstant.接口_默认用户);
            parmas.put("busiType", DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
            parmas.put("pslist", paramMap.get("pslist"));
            parmas.put("finprodType", DictCodeListExt1.金融产品类型_计划类资产.getValue());
            parmas.put(InterfCommonConstant.接口_运行接口账套ID, paramMap.get(InterfCommonConstant.接口_运行接口账套ID));
            parmas.put("IS_CHL", isChl);
            parmas.put("TMP_E_NAME", tmpEName);

            List<TrdProductDeal> mngDelDealList = this.queryUnDelDeal(parmas);
            if(CollectionUtils.isNotEmpty(mngDelDealList)){
                if(unDelDealList==null){
                    unDelDealList = new ArrayList<TrdProductDeal>();
                }
                unDelDealList.addAll(mngDelDealList);
            }
        }

        /**modify by wujie on 20190705 start
         if(unDelDealList==null || unDelDealList.size()==0){
         return;
         }**/
        if(unDelDealList ==null){
            unDelDealList = new ArrayList<TrdProductDeal>();
        }
        /**modify by wujie on 20190705 end**/

        List<String> fpsList = null;
        if(paramMap.get("fpsList")!=null){
            fpsList = (List<String>)paramMap.get("fpsList");
        }
        if(fpsList==null || fpsList.size()==0){
            List<TrdProductDeal> totalList = this.queryDelChlFeeDeal(unDelDealList);
            if(totalList != null && totalList.size()>0){
                unDelDealList.addAll(totalList);
            }
            this.deleteFinprodDealByTradeId(unDelDealList);
            return;
        }
        if(TempConstant.拆借交易.equals(tmpEName)){
            //1.查询还本到账交易
            parmas = new HashMap<String,Object>();
            parmas.put("createTime", interfDate);
            parmas.put("createUser", InterfCommonConstant.接口_默认用户);
            parmas.put("busiType", paramMap.get("busiType"));
            parmas.put("ps", DictCodeList.交易方向_还本到账.getValue());
            parmas.put("IS_CHL", isChl);
            parmas.put("TMP_E_NAME", tmpEName);
            List<TrdProductDeal> dealList_hb = this.queryUnDelDeal(parmas);
            if(dealList_hb == null || dealList_hb.size()==0){
                List<TrdProductDeal> totalList = this.queryDelChlFeeDeal(unDelDealList);
                if(totalList != null && totalList.size()>0){
                    unDelDealList.addAll(totalList);
                }
                this.deleteFinprodDealByTradeId(unDelDealList);
                return;
            }
            unDelDealList.addAll(dealList_hb);

            //查询还本到账拆分生成的收息到账交易
            parmas = new HashMap<String,Object>();
            parmas.put("createTime", interfDate);
            parmas.put("createUser", InterfCommonConstant.接口_默认用户);
            parmas.put("ftradeList", dealList_hb);
            parmas.put("ps", DictCodeList.交易方向_收息到账.getValue());
            parmas.put("IS_CHL", isChl);
            parmas.put("TMP_E_NAME", tmpEName);
            List<TrdProductDeal> dealList_fx = this.queryUnDelDeal(parmas);
            if(dealList_fx == null || dealList_fx.size()==0){
                List<TrdProductDeal> totalList = this.queryDelChlFeeDeal(unDelDealList);
                if(totalList != null && totalList.size()>0){
                    unDelDealList.addAll(totalList);
                }
                this.deleteFinprodDealByTradeId(unDelDealList);
                return;
            }

            Map<String, TrdProductDeal> hbMap = new HashMap<String, TrdProductDeal>();
            for(TrdProductDeal d :dealList_hb){
                hbMap.put(d.getfTradeId(), d);
            }

            List<TrdProductDeal> fxList = new ArrayList<TrdProductDeal>();
            for(TrdProductDeal deal :dealList_fx){
                if(hbMap.get(deal.getfTradeId())!=null){
                    fxList.add(deal);
                }
            }
            if(fxList!=null && fxList.size()>0){
                unDelDealList.addAll(fxList);
            }
            List<TrdProductDeal> totalList = this.queryDelChlFeeDeal(unDelDealList);
            if(totalList != null && totalList.size()>0){
                unDelDealList.addAll(totalList);
            }
            this.deleteFinprodDealByTradeId(unDelDealList);
            return;
        }

        if(TempConstant.拆借收息.equals(tmpEName)){
            parmas = new HashMap<String,Object>();
            parmas.put("createTime", interfDate);
            parmas.put("createUser", InterfCommonConstant.接口_默认用户);
            parmas.put("ftradeList", unDelDealList);
            parmas.put("fpsList", paramMap.get("fpsList"));
            parmas.put("IS_CHL", isChl);
            parmas.put("TMP_E_NAME", tmpEName);
            List<TrdProductDeal> dealhb = this.queryUnDelDeal(parmas);
            if(dealhb==null || dealhb.size()==0){
                List<TrdProductDeal> totalList = this.queryDelChlFeeDeal(unDelDealList);
                if(totalList != null && totalList.size()>0){
                    unDelDealList.addAll(totalList);
                }
                this.deleteFinprodDealByTradeId(unDelDealList);
                return;
            }
            Map<String, TrdProductDeal> hbMap = new HashMap<String, TrdProductDeal>();
            for(TrdProductDeal d :dealhb){
                hbMap.put(d.getfTradeId(), d);
            }
            List<TrdProductDeal> delList = new ArrayList<TrdProductDeal>();
            for(TrdProductDeal deal :unDelDealList){
                if(hbMap.get(deal.getfTradeId())==null){
                    delList.add(deal);
                }
            }
            if(delList!=null && delList.size()>0){
                List<TrdProductDeal> totalList = this.queryDelChlFeeDeal(delList);
                if(totalList != null && totalList.size()>0){
                    delList.addAll(totalList);
                }
                this.deleteFinprodDealByTradeId(delList);
                return;
            }
            List<TrdProductDeal> totalList = this.queryDelChlFeeDeal(unDelDealList);
            if(totalList != null && totalList.size()>0){
                unDelDealList.addAll(totalList);
            }
            this.deleteFinprodDealByTradeId(unDelDealList);
            return;
        }

        // modify by weipeng : 增加原交易编号判空处理，若取不到原交易，则不进行删除衍生交易处理
        if(!CollectionUtils.isEmpty(unDelDealList)){
            parmas = new HashMap<String,Object>();
            parmas.put("createTime", interfDate);
            parmas.put("createUser", InterfCommonConstant.接口_默认用户);
            parmas.put("ftradeList", unDelDealList);
            parmas.put("fpsList", paramMap.get("fpsList"));
            parmas.put("IS_CHL", isChl);
            parmas.put("TMP_E_NAME", tmpEName);
            List<TrdProductDeal> unDelDealList_cf = this.queryUnDelDeal(parmas);
            if(unDelDealList_cf==null || unDelDealList_cf.size()==0){
                List<TrdProductDeal> totalList = this.queryDelChlFeeDeal(unDelDealList);
                if(totalList != null && totalList.size()>0){
                    unDelDealList.addAll(totalList);
                }
                this.deleteFinprodDealByTradeId(unDelDealList);
                return;
            }
            unDelDealList.addAll(unDelDealList_cf);
        }
        List<TrdProductDeal> totalList = this.queryDelChlFeeDeal(unDelDealList);
        if(totalList != null && totalList.size()>0){
            unDelDealList.addAll(totalList);
        }
        this.deleteFinprodDealByTradeId(unDelDealList);
    }
    
    /**
     * 批量删除交易(交易编号)
     * @param list
     */
    public void deleteFinprodDealByTradeId(List<TrdProductDeal> list){
    	/** remark: 交易删除拆分处理 start**/
    	List<List<TrdProductDeal>> listTrdProductDeal = new ArrayList<>();
    	if(CollectionUtils.isNotEmpty(list)){
    		listTrdProductDeal = DataHandleUtil.splitList(list,500);
    	}

    	if(CollectionUtils.isNotEmpty(listTrdProductDeal)){
    		for(List<TrdProductDeal> listDeal : listTrdProductDeal){
    			// 1.估值核算交易扩展表
    	        valCommonBatchMapper.delProductDealExtByTradeId(listDeal);
    	        // 2.交易费用明细表
    	        valCommonBatchMapper.delTradeFeeDetailByTradeId(listDeal);
    	        // 3.入账账务明细表
    	        valCommonBatchMapper.delDealAccountingDetailByTradeId(listDeal);
    	        // 4.入账信息表
    	        valCommonBatchMapper.delDealAccountingByTradeId(listDeal);
    	        // 5.结算指令扩展表
    	        valCommonBatchMapper.delOrdersExtByTradeId(listDeal);
    	        // 6.结算指令表
    	        valCommonBatchMapper.delOrdersByTradeId(listDeal);
    	        // 7.金融产品交易
    	        valCommonBatchMapper.delProductDealByTradeId(listDeal);
                // 8.金融产品实物交易
                valCommonBatchMapper.delTrdFinProductBarterByTradeId(listDeal);
            }
    	}
    	/** remark: 交易删除拆分处理 end**/

    }
    
    
    /**
     * 查询待删除交易
     * add by wujie on 20190121
     */
    private List<TrdProductDeal> queryUnDelDeal(Map<String,Object> params){
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("busiType", params.get("busiType"));
        paramMap.put("busiTypelist", params.get("busiTypelist"));
        paramMap.put("pslist", params.get("pslist"));
        paramMap.put("pslistNot", params.get("pslistNot"));
        paramMap.put("ps", params.get("ps"));
        paramMap.put("createTime", params.get("createTime"));
        paramMap.put("createUser", params.get("createUser"));
        paramMap.put("profitType", params.get("profitType"));
        paramMap.put("fpsList", params.get("fpsList"));
        paramMap.put("ftradeList", params.get("ftradeList"));
        paramMap.put("finprodType", params.get("finprodType"));
        paramMap.put("assetProfitType", params.get("assetProfitType"));

        if(params.get("TMP_E_NAME") != null){
            String tmpEName = (String)params.get("TMP_E_NAME");
            if(TempConstant.通道产品费用交易.equals(tmpEName)
                    || TempConstant.费用交易表.equals(tmpEName)
                    || TempConstant.理财产品借通道投资交易.equals(tmpEName)){
                paramMap.put("TMP_E_NAME", tmpEName);
            }
        }
        paramMap.put("IS_CHL", params.get("IS_CHL"));
        paramMap.put(InterfCommonConstant.接口_运行接口账套ID, params.get(InterfCommonConstant.接口_运行接口账套ID));

        return valCommonBatchMapper.queryUnDelDeal(paramMap);
    }
    
    
    /**
     * 根据结算指令交易删除数据
     */
    public void dealCleanOrderDeal(Map<String,Object> params,String interfDate){
        Map<String,Object> paramMap = new HashMap<String,Object>();
    	paramMap.put("busiType", params.get("busiType"));
    	paramMap.put("createTime", interfDate);
        paramMap.put("createUser", InterfCommonConstant.接口_默认用户);
        paramMap.put("booksetIds", params.get("booksetIds"));
        List<StlOrders> unDelDealList = valCommonBatchMapper.queryUnDelStlOrders(paramMap);
        if(unDelDealList==null || unDelDealList.size()==0){
        	return;
        }
        // 1.入账账务明细表
        valCommonBatchMapper.delDealAccountingDetailByOrderId(unDelDealList);
        // 2.入账信息表
        valCommonBatchMapper.delDealAccountingByOrderId(unDelDealList);
        // 3.结算指令扩展表
        valCommonBatchMapper.delOrdersExtByOrderId(unDelDealList);
        // 4.结算指令表
        valCommonBatchMapper.delOrdersByOrderId(unDelDealList);
    }
    
    /**
     * 产品回补现金流信息初始化
     * 
     * @param listPrdClawbackCalcRule List<业绩回补规则接口entity>
     * @return List<FinCashInfo>
     */
    public List<FinCashInfo> initPrdClawbackCashInfo(List<PrdClawbackCalcRule> listPrdClawbackCalcRule){
    	List<FinCashInfo> listFinCashInfo = new ArrayList<>();
    	if(StringUtil.isEmptyList(listPrdClawbackCalcRule)){
    		return listFinCashInfo;
    	}
    	
    	Map<String, FinCashInfo> mapFinCashInfo = new HashMap<>();
    	
    	for( PrdClawbackCalcRule dto : listPrdClawbackCalcRule){
    		String cashId = null;
    		Long branch = InterfCommonConstant.分支序号_默认;;
            if (StringUtil.equals(dto.getFinprodType(), DictCodeListExt1.金融产品类型_理财子产品.getValue())) {
            	cashId = InterfHelper.getMd5CashId_(dto.getProdId() + new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate())
            	    , DictCodeListExt1.现金流二级类型_回补.getValue());
            	branch = Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate()));
            }
            else {
            	cashId = InterfHelper.getMd5CashId_(dto.getProdId(), DictCodeListExt1.现金流二级类型_回补.getValue());
            }
            
            FinCashInfo finCashInfo = mapFinCashInfo.get(cashId);
            if(finCashInfo == null){
            	finCashInfo = new FinCashInfo();
            	finCashInfo.setCashId(cashId);
            	finCashInfo.setCashType1(DictCodeListExt1.现金流一级类型_回补.getValue());
            	finCashInfo.setCashType2(DictCodeListExt1.现金流二级类型_回补.getValue());
            	finCashInfo.setFinprodId(dto.getProdId());
            	finCashInfo.setFinprodType(dto.getFinprodType());
            	finCashInfo.setBranch(branch);
            	listFinCashInfo.add(finCashInfo);
            }
            mapFinCashInfo.put(cashId, finCashInfo);
    	}
    		
    	return listFinCashInfo;
    }
    
    /**
     * 对账方案资产映射表初始化
     * @param assetId 资产代码
     * @param trustAssetCode 托管资产代码
     * @return
     */
    public List<BokSchemeAssetConfig> initBokSchemeAssetConfig(String assetId, String trustAssetCode){
    	//定义 变量.对账方案资产映射List
    	List<BokSchemeAssetConfig> listBokSchemeAssetConfig = new ArrayList<>();
    	//定义 变量.方案信息List
    	List<BokSchemeAssetConfig> list = new ArrayList<>();
    	
    	if(!StringUtil.isNotEmpty(trustAssetCode)){
    		return listBokSchemeAssetConfig;
    	}
    	
    	//查询对账方案资产映射表
    	list = mapper.getBokSchemeAssetConfigList();
    	
    	for (BokSchemeAssetConfig dto : list) {
    		//定义 变量.对账方案资产映射Dto
    		BokSchemeAssetConfig bokSchemeAssetConfig = new BokSchemeAssetConfig();
    		bokSchemeAssetConfig.setSchemeId(dto.getSchemeId());
    		bokSchemeAssetConfig.setMyAssetId(assetId);
    		bokSchemeAssetConfig.setOthersideAssetId(trustAssetCode);
    		bokSchemeAssetConfig.setInputType(DictCodeListExt1.录入方式_接口导入.getValue());
    		
    		//定义 变量.对账方案资产映射入库情况
    		BokSchemeAssetConfig bokSchemeAssetConfigDto = new BokSchemeAssetConfig();
    		Map<String,Object> params = new HashMap<>();
    		params.put("schemeId", dto.getSchemeId());
    		params.put("myAssetId", assetId);
    		bokSchemeAssetConfigDto = mapper.getBokSchemeAssetConfigDto(params);
    		
    		if(bokSchemeAssetConfigDto != null && DictCodeListExt1.录入方式_接口导入.getValue().equals(bokSchemeAssetConfigDto.getInputType())){
    			//变量.对账方案资产映射Dto.对方系统资产代码 = 变量.对账方案资产映射入库情况.对方系统资产代码
    			bokSchemeAssetConfig.setOthersideAssetId(bokSchemeAssetConfigDto.getOthersideAssetId());
    		}
    		
    		//变量.对账方案资产映射List.add(变量.对账方案资产映射Dto)
    		listBokSchemeAssetConfig.add(bokSchemeAssetConfig);
		}
    	
    	//定义 变量.默认对账方案资产映射Dto
    	BokSchemeAssetConfig defaultBokSchemeAssetConfig = new BokSchemeAssetConfig();
    	defaultBokSchemeAssetConfig.setSchemeId("SCHEME_ASSET_CONFIG_DEFAULT");
    	defaultBokSchemeAssetConfig.setMyAssetId(assetId);
    	defaultBokSchemeAssetConfig.setOthersideAssetId(trustAssetCode);
    	defaultBokSchemeAssetConfig.setInputType(DictCodeListExt1.录入方式_接口导入.getValue());
    	
    	//变量.对账方案资产映射List.add(变量.默认对账方案资产映射Dto)
    	listBokSchemeAssetConfig.add(defaultBokSchemeAssetConfig);
    	
    	return listBokSchemeAssetConfig;
    	
    }
    
    

	/**
     * 接口日志过滤
     * 
     * @param valInterflog 接口日志
     * @param bookSetIds 账套代码
     * @param valModel 接口模式
     * @return
     */
    public List<ValInterflog> filterValInterflogList(List<ValInterflog> valInterflog, List<String> bookSetIds, String valModel){
    	List<ValInterflog> valInterflogList = new ArrayList<>();
    	List<String> booksetLists = new ArrayList<>();
    	bookSetIds.forEach(i -> booksetLists.add("prodId" + "-" + i));
        if(!StringUtil.isEmptyList(valInterflog)){
        	if(StringUtil.equals(valModel, InterfCommonConstant.接口模式_指定账套)){
        		for(ValInterflog log : valInterflog){
        			if(log.getDetail() == null || StringUtil.equals(log.getDetail(), StringUtil.EMPTY)){
        				valInterflogList.add(log);
        			}else{
        				List<String> arrs = Arrays.asList(log.getDetail().split(";"));
        				for(String str : arrs){
        					if(booksetLists.contains(str)){
        						valInterflogList.add(log);
        					}
        				}
        			}
        		}
        	} else {
        		valInterflogList.addAll(valInterflog);
        	}
        }
    	return valInterflogList;
    }
    
    /**
     * 初始化_通道核算信息
     * @param trdProductDealList 通道产品交易集：List<理财产品借通道投资交易>
     * @param interfDate 接口日期
     * @return resultMap Map<String,Object>
     */
    public Map<String,Object> initChanneAaccounting(List<ValChannelProdDealDto> trdProductDealList , Date interfDate){
    	//定义 变量.paramsMap 
    	Map<String,Object> paramsMap=new HashMap<String,Object>();
    	//定义 变量.待初始化核算信息Map
    	Map<String, ValChannelProdDealDto> initDealMap=new HashMap<String, ValChannelProdDealDto>();
    	//定义 变量.待删除核算信息Map
    	Map<String, ValChannelProdDealDto> delDealMap=new HashMap<String, ValChannelProdDealDto>();
    	//定义 变量.待初始化通道产品信息List
    	List<ChannelAcctSubjectDto> initChannelList=new ArrayList<ChannelAcctSubjectDto>();
    	//定义 变量.待删除通道核算信息List
    	List<ValChannelProdDealDto> delChannelList=new ArrayList<ValChannelProdDealDto>();
    	//通道交易处理（区分待初始、待删除）
    	if(!StringUtil.isEmptyList(trdProductDealList)){
    		trdProductDealList.forEach(x ->{
    			//定义 变量.key =L.产品代码+"_"+L.通道代码
    			/**modify by wujie on 20190320 start
    			String key=x.getFinprodId()+"_"+x.getChlId();**/
    			String key=x.getProdId()+"_"+x.getChlId();
    			/**modify by wujie on 20190320 end**/
    			if(x.getTradeStatus().equals(DictCodeListExt1.交易状态_初始.getValue())){
    				//变量.理财产品借通道投资交易Dto = 变量.待初始化核算信息Map.get(变量.key)
    				ValChannelProdDealDto dealDto=initDealMap.get(key);
    				
    				/**modify by wujie on 20190320 start
    				if(StringUtil.isNotEmpty(dealDto)){
    					//变量.待初始化核算信息Map.put(变量.key,L)
    					initDealMap.put(key,x);
    				}else{
    					if(x.getTradeDate().compareTo(dealDto.getTradeDate())<0){
    						//变量.待初始化核算信息Map.put(变量.key,L)
        					initDealMap.put(key,x);
    					}
    				}**/
    				if(dealDto ==null){
    					initDealMap.put(key,x);
    				}else{
    					if(x.getTradeDate().compareTo(dealDto.getTradeDate())<0){
    						//变量.待初始化核算信息Map.put(变量.key,L)
        					initDealMap.put(key,x);
    					}
    				}
    				/**modify by wujie on 20190320 end**/
    				
    			}else if(x.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
    				delDealMap.put(key, x);
    			}
    		});
    		//初始化通道核算信息
    		for(String key:initDealMap.keySet()){
    			paramsMap.put("acctSubjectId", key);
    			List<BokBooksetSonDto> list=channelCommonService.queryBokBooksetSonInfo(paramsMap);
    			if(StringUtil.isEmptyList(list)){
    				ChannelAcctSubjectDto dto=this.channeAaccountingInit(initDealMap.get(key));
    				if("00".equals(dto.getResultCode())){
    					initChannelList.add(dto);
    				}
    					
    			}
    		}
    		//待删除交易处理
    		//去除本次有新增的通道产品
    		for(String key:initDealMap.keySet()){
    			if(delDealMap.get(key)!=null){
    				delDealMap.remove(key);
    			}
    		}
    		paramsMap.clear();
    		//判断处理本次删除的交易外，是否还存在有效的交易
    		for(String key:delDealMap.keySet()){
    			paramsMap.put("busiType", DictCodeList.业务类型_货币型TA交易.getValue());
    			paramsMap.put("finprodId", key);
    			paramsMap.put("finprodType", DictCodeListExt1.金融产品类型_通道.getValue());
    			//查询通道交易
    			ValChannelProdDealDto dto=channelCommonService.queryChannelProdDeal(paramsMap);
    			if(StringUtil.isNotEmpty(dto)){
    				ValChannelProdDealDto deal=delDealMap.get(key);
    				deal.setFinprodId(deal.getFinprodId()+"_"+deal.getChlId());
    				delChannelList.add(deal);
    			}
    		}
    	}/**modify by wujie on 20190320 
    	  else{**/
    		if(initChannelList.size()>0){
    			this.channelAcctSubjectAdd(initChannelList);
    			
    			//add by zhangquan 20200107
    			//定义 变量.几许产品信息List
    			List<JJPortfolio> jJPortfolio = new ArrayList<JJPortfolio>();
    			//定义 变量.几许产品资金账户信息List
    			List<JJBkCashAcct> jJBkCashAcct = new ArrayList<JJBkCashAcct>();
    			initChannelList.forEach(x ->{
    				//变量.几许产品信息List.add(L.几许产品信息)
    				jJPortfolio.add(x.getjJPortfolio());
    				if(!StringUtil.isEmptyList(x.getjJBkCashAcctList()) && x.getjJBkCashAcctList().size()>0) {
    					//变量.几许产品资金账户信息List.addAll(L.几许产品资金账户信息List)
    					jJBkCashAcct.addAll(x.getjJBkCashAcctList());
    				}
    			});
    			//变量.resultMap.put("JJPortfolio_List",变量.几许产品信息List)
    			paramsMap.put("JJPortfolio_List", jJPortfolio);
    			//变量.resultMap.put("JJBkCashAcct_List",变量.几许产品资金账户信息List)
    			paramsMap.put("JJBkCashAcct_List", jJBkCashAcct);
    		}
    		if(delChannelList.size()>0){
    			this.channelAcctSubjectDelete(delChannelList);
    		}
    		/**modify by wujie on 20190320
    	}**/
    	return paramsMap;
    }
    
    /**
     * 通道核算信息_初始化
     * @param channelProdDeal 通道产品交易集：List<理财产品借通道投资交易>
     * @return resultMap Map<String,Object>
     */
    @SuppressWarnings("unchecked")
    public ChannelAcctSubjectDto channeAaccountingInit(ValChannelProdDealDto channelProdDeal){
    	ChannelAcctSubjectDto channelAcctSubjectDto=new ChannelAcctSubjectDto();
    	//resultObject.处理结果 = 00;
    	channelAcctSubjectDto.setResultCode("00");
    	Map<String,Object> map=new HashMap<String,Object>();
    	//1.3 定义变量
    	//变量.产品账套基本信息
    	ValBooksetInfoDto valBooksetInfoDto=new ValBooksetInfoDto();
    	//通道基本信息
    	TargetFinProduct targetFinProduct=new TargetFinProduct();
    	//变量.交易日历 
    	String calendarId ="";
    	//变量.标的金融产品_资产
    	FinProduct finProduct=new FinProduct();
    	//变量.标的金融产品扩展_资产
    	FinProductAdd finProductExt=new FinProductAdd();
    	//变量.标的类金融产品类型_资产
    	FinProductType finProductType=new FinProductType();
    	//标的.金融产品估值参数Dto
    	FinProductValuationPara finProductValuationPara = new FinProductValuationPara();
    	//变量.账套信息Dto
    	BokBookset bokBookset = new BokBookset();
    	//变量.母子账套关系Dto
    	BokBooksetRe bokBooksetRe=new BokBooksetRe();
    	//变量.账套日期Dto
    	BokBooksetDate bokBooksetDate=new BokBooksetDate();
    	//标量。账套配置List
    	List<BokBooksetExt> listBokBooksetExt = new ArrayList<>();// modify by dongjun on 20190320 通道变更
    	//变量.核算主体信息Dto
    	BokAcctSubjectInfo bokAcctSubjectInfo=new BokAcctSubjectInfo();
    	//变量.核算分层信息Dto
    	BokAcctLayeringInfo bokAcctLayeringInfo=new BokAcctLayeringInfo();
    	//变量.账套估值方案List
    	List<BokBooksetPlan> bokBooksetPlanList=new ArrayList<BokBooksetPlan>();
    	//变量.账务明细代码List
    	List<BokDetailId> bokDetailIdList=new ArrayList<BokDetailId>();
    	//变量.子账套通道现金流信息List
    	List<FinCashInfo> finCashInfoList=new ArrayList<FinCashInfo>();
    	//变量.子账套通道现金流计算规则List
    	List<FinCashCalcRule> finCashCalcRuleList=new ArrayList<FinCashCalcRule>();
    	//变量.金融产品账户List
    	List<StlFinProductAcct> stlFinProductAcctList=new ArrayList<StlFinProductAcct>();
    	//变量.账套清算账户表List
    	List<BokBooksetCapacctInfo> bokBooksetCapacctInfoList=new ArrayList<BokBooksetCapacctInfo>();
    	//变量.子账套代码
    	String booksetIdSon=channelProdDeal.getProdId()+"_"+channelProdDeal.getChlId();
    	//变量.金融产品代码
    	String finprodId=booksetIdSon;
//add by yangwenjun 20201201 ZGBS-42 start
        String objProdId=channelProdDeal.getProdId();
    	if(channelProdDeal.getProdId().contains("_")){
    	     objProdId=channelProdDeal.getProdId().substring(channelProdDeal.getProdId().indexOf("_")+1);
        }
//add by yangwenjun 20201201 ZGBS-42 end
    	//定义 变量.是否子组合 = false;
    	boolean isSubarray = false;
    	
    	//定义 变量.几许产品信息 = null;   add by zhangquan 20200103
    	JJPortfolio jJPortfolio = null;
    	//定义 变量.几许产品资金账户信息list<JJBkCashAcct>
    	List<JJBkCashAcct> jJBkCashAcctList = new ArrayList<JJBkCashAcct>();

    	//2.处理详情
    	//2.1 产品账套基本信息查询
    	valBooksetInfoDto=this.getBooksetInfoByProdId(channelProdDeal.getProdId());
    	
    	if(!StringUtil.isNotEmpty(valBooksetInfoDto)){
    		channelAcctSubjectDto.setResultCode("01");
    		//变量.resultObject.结果描述 = "产品【"+输入.理财产品借通道投资交易.产品代码+"】对应账套信息不存在；"
    		channelAcctSubjectDto.setResultMsg("产品【"+channelProdDeal.getProdId()+"】对应的账套信息不存在!");
    		return channelAcctSubjectDto;
    	}
    	//通道基本信息
    	targetFinProduct=this.getTargetFinProduct(channelProdDeal.getChlId());
    	if(!StringUtil.isNotEmpty(targetFinProduct)){
    		channelAcctSubjectDto.setResultCode("01");
    		//变量.resultObject.结果描述 = "产品【"+输入.理财产品借通道投资交易.产品代码+"】对应账套信息不存在；"
    		channelAcctSubjectDto.setResultMsg("产品【"+channelProdDeal.getChlId()+"】对应的基本信息不存在!");
    		return channelAcctSubjectDto;
    	}
    	//交易日历查询
    	calendarId=this.getCalendarId(channelProdDeal.getTradeMarket());
    	
    	//初始化金融产品标的（计划类货币型资产）
    	//金融产品代码
    	finProduct.setFinprodId(finprodId);
    	//金融产品类型.计划类资产
    	finProduct.setFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue());
    	//金融产品简称
    	finProduct.setFinprodAbbr(targetFinProduct.getFinprodAbbr());
    	//金融产品名称
    	finProduct.setFinprodName(targetFinProduct.getFinprodName());
    	//收益类型
    	finProduct.setProfitType(DictCodeList.收益类型_货币型.getValue());
    	//通道代码
    	finProduct.setChlFinprodId(channelProdDeal.getChlId());
    	//市场代码
    	finProduct.setFinprodMarketId(finprodId);
    	//币种
    	finProduct.setCcy(channelProdDeal.getCcy());
    	//境内外
    	finProduct.setBlnArea(DictCodeList.境内外_境内.getValue());
    	//交易场所
    	finProduct.setTradeMarket(channelProdDeal.getTradeMarket());
    	//交易日历
    	finProduct.setCalendarId(calendarId);
    	//发行日
    	finProduct.setIdate(valBooksetInfoDto.getStartDate());
    	//起息日
    	finProduct.setVdate(valBooksetInfoDto.getStartDate());
    	//到期日
    	finProduct.setMdate(valBooksetInfoDto.getEndDate());
    	//实际到期日
    	finProduct.setActmdate(valBooksetInfoDto.getEndDate());
    	//是否通道
    	finProduct.setIsChl(DictCodeList.是否_是.getValue());
    	//原金融产品代码
    	finProduct.setoFinprodId(finprodId);
    	//本金收益特征
    	finProduct.setCapiIncomeFeature(targetFinProduct.getCapiIncomeFeature());
    	
    	//初始化标的类金融产品扩展表
    	//金融产品代码
    	finProductExt.setFinprodId(finprodId);

    	//申购确认速度
    	finProductExt.setPurSpeed(0L);
    	//赎回确认速度
    	finProductExt.setRedSpeed(0L);
    	//收益标识
    	finProductExt.setProfitFlag(DictCodeListExt1.收益标识_非保本浮动收益型.getValue());
    	
    	//初始化标的类金融产品类型表
    	//金融产品代码
    	finProductType.setFinprodId(finprodId);
    	//分类1
    	finProductType.setType1(DictCodeListExt1.计划类资产类型_其他资产管理计划.getValue());
    	//分类2
    	finProductType.setType2(DictCodeListExt2.计划类资产核算分类_境内计划类.getValue());
    	finProductType.setBranch(InterfCommonConstant.分支序号_默认);
    	
    	//初始化金融产品估值参数表
    	//金融产品代码 = 变量.金融产品代码
    	finProductValuationPara.setFinprodId(finprodId);
    	//分支序号 = 默认为0
    	finProductValuationPara.setBranch(0L);
    	//净值数据延迟天数 = 默认为0
    	finProductValuationPara.setIntAccrualMode("0");
        //2.5初始化通道子账套信息

        //刚蓝色背景标记部分
        //核算主体类型为投资组合且为子组合时，不计税，不计提
        //IF 数据字典：金融产品类型.投资组合 eq  变量.产品账套基本信息.核算主体类型
        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(valBooksetInfoDto.getAcctSubjectType())){
            //调用共通设计->【详细设计-接口共通方法设计.xlsx】->查询组合信息SHEET
            PtlPortfolio bokDetailId = getPtlPortfolo(valBooksetInfoDto.getBooksetId());
            if(bokDetailId != null && StringUtil.equals(bokDetailId.getPortfolioType(), DictCodeListExt1.组合类型_子组合.getValue())){
                //变量.是否子组合 = true;
                isSubarray = true;
            }
        }


    	//初始化母子账套关系表
    	bokBooksetRe.setBooksetIdSon(booksetIdSon);
    	//子账套名称
    	bokBooksetRe.setBooksetNameSon(targetFinProduct.getFinprodAbbr());
    	//通道代码
    	bokBooksetRe.setChannelId(channelProdDeal.getChlId());
    	//核算方式
    	bokBooksetRe.setAcctMode(DictCodeList.收益类型_货币型.getValue());
    	//核算主体代码
    	bokBooksetRe.setAcctSubjectId(finprodId);
    	//核算主体类别
    	bokBooksetRe.setAcctSubjectType(DictCodeListExt1.金融产品类型_通道.getValue());
    	//本位币
    	bokBooksetRe.setStandardCcy(channelProdDeal.getCcy());
    	//账套代码
    	bokBooksetRe.setBooksetId(valBooksetInfoDto.getBooksetId());
    	
    	//初始化核算主体信息表
    	//核算主体代码
    	bokAcctSubjectInfo.setAcctSubjectId(finprodId);
    	//核算主体类别
    	bokAcctSubjectInfo.setAcctSubjectType(DictCodeListExt1.金融产品类型_通道.getValue());
    	//核算主体名称
    	bokAcctSubjectInfo.setAcctSubjectName(targetFinProduct.getFinprodAbbr());
    	
    	//初始化核算分层信息表
    	//变量.分层代码 =变量.金融产品代码+"_"+数据字典：金融产品类型.通道
    	String layeringId=finprodId+"_"+ DictCodeListExt1.金融产品类型_通道.getValue();
    	//变量.账务明细代码
    	String bokDetailId="";
    	BokDetailId bokDetailDto=this.getBokDetailId(finprodId, null, DictCodeListExt1.金融产品类型_通道.getValue(), null, null, null);
    	if(StringUtil.isNotEmpty(bokDetailDto)){
    		bokDetailId=bokDetailDto.getBokDetailId();
    	}
    	//账套代码
    	bokAcctLayeringInfo.setBooksetId(booksetIdSon);
    	//分层代码
    	bokAcctLayeringInfo.setLayeringId(layeringId);
    	//账务明细代码
    	bokAcctLayeringInfo.setBokDetailId(bokDetailId);
    	//业务明细代码
    	bokAcctLayeringInfo.setBusiId(finprodId);
    	//业务明细类型
    	bokAcctLayeringInfo.setBusiDetailType(DictCodeListExt1.金融产品类型_通道.getValue());
    	//起息日
    	bokAcctLayeringInfo.setVdate(valBooksetInfoDto.getStartDate());
    	//到期日
    	bokAcctLayeringInfo.setMdate(valBooksetInfoDto.getEndDate());
    	//交易日历
    	bokAcctLayeringInfo.setCalendarId(calendarId);
    	//分支序号
    	bokAcctLayeringInfo.setBranch(0L);
    	//级别序号
    	bokAcctLayeringInfo.setLev("1");
    	//当前级别
    	bokAcctLayeringInfo.setCurLev(1L);
    	//下级最新序号
    	bokAcctLayeringInfo.setsBranch(0L);
    	//分层名称
    	bokAcctLayeringInfo.setLayeringName(targetFinProduct.getFinprodAbbr());
    	
    	//初始化账套日期表
    	bokBooksetDate.setBooksetId(booksetIdSon);
    	//当前日期
    	bokBooksetDate.setBooksetDate(valBooksetInfoDto.getBooksetDate());
    	
    	// 初始化账套配置表
    	//listBokBooksetExt
    	Map<String, Object> booksetInfoMap = getBooksetInfoById(valBooksetInfoDto.getBooksetId(), InterfCommonConstant.账套信息查询_账套配置);
    	if(booksetInfoMap != null && booksetInfoMap.get(InterfCommonConstant.账套信息查询_账套配置) != null){
    		listBokBooksetExt = (List<BokBooksetExt>)booksetInfoMap.get(InterfCommonConstant.账套信息查询_账套配置);
    	}
    	
    	Map<String, BokBooksetExt> bokBooksetExtMap = new HashMap<>();
    	
    	if(CollectionUtils.isNotEmpty(listBokBooksetExt)){
    		listBokBooksetExt.forEach(i -> {
    			i.setBooksetId(booksetIdSon);
    			bokBooksetExtMap.put(i.getParamCode(), i);
    		});
    	}
    	
    	//定义 变量.活期存款计提配置 = 变量.账套配置Map.get(数据字典：账套配置表参数项.活期存款是否计提)
    	BokBooksetExt depositAccout = bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_活期存款是否计提.getValue());
    	if(depositAccout == null){
    		depositAccout = new BokBooksetExt();
    		depositAccout.setBooksetId(booksetIdSon);
    		depositAccout.setParamCode(ValDictCodeList.账套配置表参数项_活期存款是否计提.getValue());
    		depositAccout.setParamValue(targetFinProduct.getIsCalc());
    		if(isSubarray){
    			depositAccout.setParamValue(DictCodeList.是否_是.getValue());
    		}
    		depositAccout.setParamName(ValDictCodeList.账套配置表参数项_活期存款是否计提.getCodeName());
    		depositAccout.setRemark("数据字典：是否");
    	} else{
    		depositAccout.setParamValue(targetFinProduct.getIsCalc());
    		if(isSubarray){
    			depositAccout.setParamValue(DictCodeList.是否_是.getValue());
    		}
    	}
    	bokBooksetExtMap.put(ValDictCodeList.账套配置表参数项_活期存款是否计提.getValue(), depositAccout);
    	
    	//定义 变量.本期利润损益结转频率配置 = 变量.账套配置Map.get(数据字典：账套配置表参数项.本期利润损益结转频率)
    	BokBooksetExt upCarryoverCycle = bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_本期利润损益结转频率.getValue());
    	if(upCarryoverCycle == null){
    		upCarryoverCycle = new BokBooksetExt();
    		upCarryoverCycle.setBooksetId(booksetIdSon);
    		upCarryoverCycle.setParamCode(ValDictCodeList.账套配置表参数项_本期利润损益结转频率.getValue());
    		upCarryoverCycle.setParamValue(DictCodeListExt1.损益结转频率_日.getValue());
    		upCarryoverCycle.setParamName(ValDictCodeList.账套配置表参数项_本期利润损益结转频率.getCodeName());
    		upCarryoverCycle.setRemark("数据字典：损益结转频率");
    	} else{
    		upCarryoverCycle.setParamValue(DictCodeListExt1.损益结转频率_日.getValue());
    	}
    	bokBooksetExtMap.put(ValDictCodeList.账套配置表参数项_本期利润损益结转频率.getValue(), upCarryoverCycle);
    	
    	// 定义 变量.损益平准金结转频率配置 = 变量.账套配置Map.get(数据字典：账套配置表参数项.损益平准金结转频率)
    	BokBooksetExt palcaCryoverCycle = bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_损益平准金结转频率.getValue());
    	if(palcaCryoverCycle == null){
    		palcaCryoverCycle = new BokBooksetExt();
    		palcaCryoverCycle.setBooksetId(booksetIdSon);
    		palcaCryoverCycle.setParamCode(ValDictCodeList.账套配置表参数项_损益平准金结转频率.getValue());
    		palcaCryoverCycle.setParamValue(DictCodeListExt1.损益结转频率_日.getValue());
    		palcaCryoverCycle.setParamName(ValDictCodeList.账套配置表参数项_损益平准金结转频率.getCodeName());
    		palcaCryoverCycle.setRemark("数据字典：损益结转频率");
    	} else{
    		palcaCryoverCycle.setParamValue(DictCodeListExt1.损益结转频率_日.getValue());
    	}
    	bokBooksetExtMap.put(ValDictCodeList.账套配置表参数项_损益平准金结转频率.getValue(), palcaCryoverCycle);
    	
    	// 定义 变量.收益类型配置 = 变量.账套配置Map.get(DictCode.收益类型.getValue())
     	BokBooksetExt profitType = bokBooksetExtMap.get(DictCode.收益类型.getValue());
    	if(profitType == null){
    		profitType = new BokBooksetExt();
    		profitType.setBooksetId(booksetIdSon);
    		profitType.setParamCode(DictCode.收益类型.getValue());
    		profitType.setParamValue(DictCodeList.收益类型_货币型.getValue());
    		profitType.setParamName("收益类型");
    		profitType.setRemark("数据字典：收益类型");
    	} else{
    		profitType.setParamValue(DictCodeList.收益类型_货币型.getValue());
    	}
    	bokBooksetExtMap.put(DictCode.收益类型.getValue(), profitType);

    	if(bokBooksetExtMap.get(DictCode.收益类型.getValue()).equals(DictCodeList.收益类型_货币型.getValue())){//如果账套信息为货币型账套,账套设置按货币型产品设置配置 add by zxp
            BokBooksetExt bjjzpl = bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_本期利润损益结转频率.getValue());
            bjjzpl.setParamValue(DictCodeListExt1.损益结转频率_日.getValue());
            bokBooksetExtMap.put(ValDictCodeList.账套配置表参数项_本期利润损益结转频率.getValue(),bjjzpl );

            BokBooksetExt syjzpl = bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_损益结转频率.getValue());
            syjzpl.setParamValue(DictCodeListExt1.损益结转频率_日.getValue());
            bokBooksetExtMap.put(ValDictCodeList.账套配置表参数项_损益结转频率.getValue(),syjzpl );

            BokBooksetExt sypzjjzpl = bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_损益平准金结转频率.getValue());
            sypzjjzpl.setParamValue(DictCodeListExt1.损益结转频率_日.getValue());
            bokBooksetExtMap.put(ValDictCodeList.账套配置表参数项_损益平准金结转频率.getValue(),sypzjjzpl );

        }//fixed JSUAT-495 BUG  add by zxp 如果账套信息为货币型账套,账套设置按货币型产品设置配置
    	
    	// 定义 变量.估值表模板配置 = 变量.账套配置Map.get(数据字典：账套配置表参数项.估值表模板)
     	BokBooksetExt valuationTemplate = bokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_估值表模板.getValue());
    	if(valuationTemplate == null){
    		valuationTemplate = new BokBooksetExt();
    		valuationTemplate.setBooksetId(booksetIdSon);
    		valuationTemplate.setParamCode(ValDictCodeList.账套配置表参数项_估值表模板.getValue());
    		valuationTemplate.setParamValue(targetFinProduct.getValuationTemplate());
    		valuationTemplate.setParamName(ValDictCodeList.账套配置表参数项_估值表模板.getCodeName());
    		valuationTemplate.setRemark("数据字典：收益类型");
    	} else{
    		valuationTemplate.setParamValue(targetFinProduct.getValuationTemplate());
    	}
    	if(valuationTemplate.getParamValue() == null){
//    		valuationTemplate.setParamValue(CacheUtil.getSysParam("SCHEME_MODE"));
    		valuationTemplate.setParamValue(valSysParaService.findParamValue("SCHEME_MODE"));
    	}
    	bokBooksetExtMap.put(ValDictCodeList.账套配置表参数项_估值表模板.getValue(), valuationTemplate);
    	
    	listBokBooksetExt = new ArrayList<>();
    	for(String str : bokBooksetExtMap.keySet()){
    		listBokBooksetExt.add(bokBooksetExtMap.get(str));
    	}
    	
    	//初始化账套信息表
    	//账套代码
    	bokBookset.setBooksetId(booksetIdSon);
    	//账套名称
    	bokBookset.setBooksetName(targetFinProduct.getFinprodAbbr()+"("+objProdId+")");
    	//账套类别
    	bokBookset.setBooksetType(valBooksetInfoDto.getBooksetType());
    	//核算方式
    	bokBookset.setAcctMode(DictCodeList.收益类型_货币型.getValue());
    	//核算主体代码
    	bokBookset.setAcctSubjectId(finprodId);
    	//核算主体类别
    	bokBookset.setAcctSubjectType(DictCodeListExt1.金融产品类型_通道.getValue());
    	//本位币
    	bokBookset.setStandardCcy(channelProdDeal.getCcy());
    	//模板账套代码
    	bokBookset.setBooksetIdTmpl(valBooksetInfoDto.getBooksetIdTmpl());
    	//会计准则
    	bokBookset.setAcctStandard(valBooksetInfoDto.getAcctStandard());
    	//账套开始日
    	bokBookset.setStartDate(valBooksetInfoDto.getStartDate());
    	//账套结束日
    	bokBookset.setEndDate(valBooksetInfoDto.getEndDate());
    	//特殊核算模型
    	bokBookset.setBokSpecMode(valBooksetInfoDto.getBokSpecMode());
    	//账套全称
    	bokBookset.setBooksetFullname(targetFinProduct.getFinprodName()+"("+objProdId+")");
    	
    	//几许产品信息初始化   add by zhangquan
    	jJPortfolio = new JJPortfolio();
    	//套账代码
    	jJPortfolio.setBookSet_id(booksetIdSon);
    	//货币
    	jJPortfolio.setStandard_ccy(valBooksetInfoDto.getStandardCcy());
    	//套账模板
    	jJPortfolio.setBookset_id_tmpl(valBooksetInfoDto.getBooksetIdTmpl());
    	//套账创建日历
    	jJPortfolio.setStart_date(valBooksetInfoDto.getStartDate());
    	//套账全名
    	jJPortfolio.setBookset_fullname(valBooksetInfoDto.getBooksetFullname());
    	//套账名
    	jJPortfolio.setBookset_name(valBooksetInfoDto.getBooksetName());
    	//套账节假日
    	jJPortfolio.setCalendar_id(calendarId);
    	
        //2.6 初始化估值方案、税务方案
    	//初始化默认估值方案 
    	BokBooksetPlan bokBooksetPlan=new BokBooksetPlan();
    	//获取默认方案
    	bokBooksetPlan=channelCommonService.queryBokBooksetPlan();
    	//账套代码
    	bokBooksetPlan.setBooksetId(booksetIdSon);
    	//方案类型
    	bokBooksetPlan.setSchemeType(DictCodeListExt1.配置方案类型_估值方案.getValue());
    	//账套估值方案List.add(变量.估值方案Dto)
    	bokBooksetPlanList.add(bokBooksetPlan);
    	
    	if(!isSubarray){
	    	//初始化税务方案
	    	//(1) 税务方案信息-前台推送
	    	bokBooksetPlan=channelCommonService.queryFinProductValuationPara(channelProdDeal.getChlId());
	    	//(2) 税务方案信息-默认方案
	    	if(!StringUtil.isNotEmpty(bokBooksetPlan) || bokBooksetPlan.getSchemeId() ==null){
	    		map.clear();
                // add by weipeng on 20220408：会计引擎升级改造，若按照内部科目编码分录，则需要对应新会计引擎模板 start
                // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录
                String accountEntryType =  CacheUtil.getSysParam("ACCOUNT_ENTRY_TYPE");
                //调用service方法进行逻辑逻辑
                if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
                    map.put("BOOKSETID", InterfCommonConstant.BOOKSETID_NEW);
                }
                else{
                    map.put("BOOKSETID", InterfCommonConstant.BOOKSETID);
                }
                // add by weipeng on 20220408：会计引擎升级改造，若按照内部科目编码分录，则需要对应新会计引擎模板 end

	    		map.put("ISDEFAULT", DictCodeList.是否_是.getValue());
	    		map.put("SCHEMETYPE", DictCodeListExt1.配置方案类型_税务方案.getValue());
	    		bokBooksetPlan=mapper.buildPlan(map);
	    	}
	    	//账套代码
	    	bokBooksetPlan.setBooksetId(booksetIdSon);
	    	//方案类型
	    	bokBooksetPlan.setSchemeType(DictCodeListExt1.配置方案类型_税务方案.getValue());
	    	//账套估值方案List.add(变量.估值方案Dto)
	    	bokBooksetPlanList.add(bokBooksetPlan);
	    	
	    	//初始化增值税现金流信息
	    	FinCashInfo finCashInfoDto=new FinCashInfo();
	    	String cashId= InterfHelper.getMd5CashId_(finprodId, DictCodeListExt1.金融产品类型_通道.getValue(), DictCodeListExt1.现金流二级类型_税额.getValue());
	    	finCashInfoDto.setCashId(cashId);
	    	finCashInfoDto.setCashType1(DictCodeListExt1.现金流一级类型_税额.getValue());
	    	finCashInfoDto.setCashType2(DictCodeListExt1.现金流二级类型_税额.getValue());
	    	finCashInfoDto.setFinprodId(finprodId);
	    	finCashInfoDto.setFinprodType(DictCodeListExt1.金融产品类型_通道.getValue());
	    	finCashInfoDto.setBranch(0L);
	    	finCashInfoList.add(finCashInfoDto);
	    	
	    	//初始化增值税账务明细代码信息
	    	//变量.账务明细代码
	    	bokDetailDto=this.getBokDetailId(cashId, null, finCashInfoDto.getCashType2(), null, null, null);
	    	if(StringUtil.isNotEmpty(bokDetailDto)){
	    		bokDetailId=bokDetailDto.getBokDetailId();
	    	}
	    	//变量.账务明细代码Dto
	    	bokDetailDto= new BokDetailId();
	    	bokDetailDto.setBooksetId(booksetIdSon);
	    	bokDetailDto.setBokDetailId(bokDetailId);
	    	bokDetailDto.setBusiId(cashId);
	    	bokDetailDto.setBusiDetailType(finCashInfoDto.getCashType2());
	    	bokDetailDto.setBranch(0L);
	    	bokDetailIdList.add(bokDetailDto);
    	}
    	//2.7 初始化通道费用现金流信息
    	//初始化子账套的通道费用现金流信息
    	//查询现金流信息
    	map.clear();
    	map.put("finprodId", channelProdDeal.getChlId());
    	map.put("finprodType", DictCodeListExt1.金融产品类型_通道.getValue());
    	map.put("cashType1", DictCodeListExt1.现金流一级类型_费用.getValue());
    	List<FinCashInfo> newfinCashInfoList=this.queryCashInfoList(map);
    	if(!CollectionUtils.isEmpty(newfinCashInfoList)){
    		newfinCashInfoList.forEach(x ->{
    	    	FinCashInfo finCashInfo=new FinCashInfo();
    	    	finCashInfo.setCashId(InterfHelper.getMd5CashId_(x.getCashId(),channelProdDeal.getProdId()));
    	    	finCashInfo.setCashType1(x.getCashType1());
    	    	finCashInfo.setCashType2(x.getCashType2());
    	    	finCashInfo.setFinprodId(finprodId);
    	    	finCashInfo.setFinprodType(DictCodeListExt1.金融产品类型_通道.getValue());
    	    	finCashInfo.setBranch(0L);
    	    	finCashInfoList.add(finCashInfo);
    		});
    	}
    	//初始化子账套的通道费用现金流计算规则信息
    	List<FinCashCalcRule> newfinCashCalcRuleList=this.queryFinCashCalcRuleList(map);
    	if(!CollectionUtils.isEmpty(newfinCashCalcRuleList)){
    		newfinCashCalcRuleList.forEach(x ->{
    			FinCashCalcRule finCashCalcRule=new FinCashCalcRule();
    			finCashCalcRule.setCashId(InterfHelper.getMd5CashId_(x.getCashId(),channelProdDeal.getProdId()));
    			finCashCalcRule.setEffDate(x.getEffDate());
    			finCashCalcRule.setCalcType(x.getCalcType());
    			finCashCalcRule.setBasis(x.getBasis());
    			finCashCalcRule.setBaseType(x.getBaseType());
    			finCashCalcRule.setBaseDateType(x.getBaseDateType());
    			finCashCalcRule.setYield(x.getYield());
    			finCashCalcRule.setBranch(0L);
    			finCashCalcRule.setFinprodId(finprodId);
    			finCashCalcRule.setFinprodType(DictCodeListExt1.金融产品类型_通道.getValue());
    			
    			
    			finCashCalcRuleList.add(finCashCalcRule);
    		});
    	}
    	
        //2.8 初始化金融产品资金账户信息
    	//变量.待初始化资金账户信息List
    	List<StlFinProductAcct> newstlFinProductAcctList=this.queryStlFinProductAcctInfo(map);
    	//初始化通道资金账户信息
    	if(!CollectionUtils.isEmpty(newstlFinProductAcctList)){
    	    //定义 变量.四级科目尾号集合 Map<String,BokDetailId> 20201225 start
            Map<String, BokDetailId> bokDetailIdMap = new HashMap<>();
            // 20201225 end
            newstlFinProductAcctList.forEach(x ->{
                String acctType = x.getAcctType();
                if(DictCodeListExt1.资金账户类型_产品托管户.getValue().equals(x.getAcctType())){
    				// 初始化待清算账户_金融产品账户表
        			StlFinProductAcct slFinProductAcct=new StlFinProductAcct();
        			slFinProductAcct.setFinprodId(finprodId);
        			slFinProductAcct.setFinprodType(DictCodeListExt1.金融产品类型_通道.getValue());
        			slFinProductAcct.setAcctNo(InterfCommonConstant.清算款账户号+x.getCcy());
        			slFinProductAcct.setCcy(x.getCcy());
        			slFinProductAcct.setAcctType(DictCodeListExt1.资金账户类型_待清算款.getValue());
        			stlFinProductAcctList.add(slFinProductAcct);
        			
        			// 初始化待清算账户_账套清算账户表
        			BokBooksetCapacctInfo bokBooksetCapacctInfo=new BokBooksetCapacctInfo();
        			bokBooksetCapacctInfo.setBooksetId(booksetIdSon);
        			bokBooksetCapacctInfo.setAcctNo(InterfCommonConstant.清算款账户号+x.getCcy());
        			bokBooksetCapacctInfo.setCcy(x.getCcy());
        			bokBooksetCapacctInfo.setAcctType(DictCodeListExt1.资金账户类型_待清算款.getValue());
        			
        			bokBooksetCapacctInfoList.add(bokBooksetCapacctInfo);
        			
        			acctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
                }

                // 初始化金融产品账户表
    			StlFinProductAcct slFinProductAcct=new StlFinProductAcct();
    			slFinProductAcct.setFinprodId(finprodId);
    			slFinProductAcct.setFinprodType(DictCodeListExt1.金融产品类型_通道.getValue());
    			slFinProductAcct.setAcctNo(x.getAcctNo());
    			slFinProductAcct.setCcy(x.getCcy());
    			slFinProductAcct.setAcctType(acctType);
    			
    			stlFinProductAcctList.add(slFinProductAcct);
    			// 初始化账套清算账户表
    			BokBooksetCapacctInfo bokBooksetCapacctInfo=new BokBooksetCapacctInfo();
    			bokBooksetCapacctInfo.setBooksetId(booksetIdSon);
    			bokBooksetCapacctInfo.setAcctNo(x.getAcctNo());
    			bokBooksetCapacctInfo.setCcy(x.getCcy());
    			bokBooksetCapacctInfo.setAcctType(acctType);
    			
    			bokBooksetCapacctInfoList.add(bokBooksetCapacctInfo);
    			
    			//初始化活期存款计提账务明细代码
    			if(!DictCodeListExt1.资金账户类型_待清算款.getValue().equals(x.getAcctType())){
    				//变量.账务明细代码 
    				BokDetailId bokDetail=this.getBokDetailId(x.getAcctNo(), null, acctType, null, null, x.getCcy());
    				String bokDetailId_1="";
    		    	if(StringUtil.isNotEmpty(bokDetail)){
    		    		 bokDetailId_1=bokDetail.getBokDetailId();
    		    	}
    		    	bokDetail= new BokDetailId();
    		    	bokDetail.setBooksetId(booksetIdSon);
    		    	bokDetail.setBokDetailId(bokDetailId_1);
    		    	bokDetail.setBusiId(x.getAcctNo());
    		    	bokDetail.setBusiDetailType(acctType);
    		    	bokDetail.setBranch(0L);
                    // 币种 L.币种
    		    	bokDetail.setCcy(x.getCcy());
                    //定义 变量.bokDetailId = 变量.四级科目尾号集合.get(变量.子账套代码+变量.账务明细代码)
                    BokDetailId bokDetailIdTmp = bokDetailIdMap.get(booksetIdSon + bokDetail.getBokDetailId());
                    if (!StringUtil.isNotEmpty(bokDetailIdTmp)){
                        //调用共通设计->【详细设计-接口共通方法设计.xlsx】->四级科目号设置SHEET
                        //变量.bokDetailId = 返回值
                        bokDetailIdTmp = this.settingDetailSubjectNoTaiL(x.getAcctNo(), acctType, null, null, booksetIdSon);
                    }
                    //变量.四级科目尾号集合.put(变量.子账套代码+变量.账务明细代码,变量.bokDetailId)
                    bokDetailIdMap.put(booksetIdSon + bokDetail.getBokDetailId(),bokDetailIdTmp);
                    //变量.账务明细代码Dto.四级科目尾号 = 变量.bokDetailId.四级科目尾号
                    bokDetail.setDetailSubjectNoTail(bokDetailIdTmp.getDetailSubjectNoTail());
                    //变量.账务明细代码Dto.四级科目尾号名称 =	变量.bokDetailId.四级科目尾号名称
                    bokDetail.setDetailSubnameTail(bokDetailIdTmp.getDetailSubnameTail());
                    bokDetailIdList.add(bokDetail);
    			}
    			
    		});
    	}
    	
    	//几许资金账户初始化  add by zhangquan
    	//账套清算账户表List
    	bokBooksetCapacctInfoList.forEach(x ->{
    		//定义 变量.几许产品资金账户Dto
    		JJBkCashAcct jJBkCashAcct = new JJBkCashAcct();
    		//定义 变量.资金账户信息
    		StlCapacctInfo stlCapacctInfo = valFinProductAcctBatchMapper.getStlCapacctInfo(x.getAcctNo(),x.getCcy());
    		jJBkCashAcct.setAcct_no(x.getAcctNo());
    		jJBkCashAcct.setAcct_name(stlCapacctInfo==null?null:stlCapacctInfo.getAcctName());
    		jJBkCashAcct.setBookset_id(x.getBooksetId());
    		jJBkCashAcct.setBank_dep(null);
    		jJBkCashAcct.setCcy(x.getCcy());
    		jJBkCashAcctList.add(jJBkCashAcct);
    		});
    	channelAcctSubjectDto.setFinProduct(finProduct);
    	channelAcctSubjectDto.setFinProductExt(finProductExt);
    	channelAcctSubjectDto.setFinProductType(finProductType);
    	channelAcctSubjectDto.setFinProductValuationPara(finProductValuationPara);
    	channelAcctSubjectDto.setBokBookset(bokBookset);
    	channelAcctSubjectDto.setBokBooksetRe(bokBooksetRe);
    	channelAcctSubjectDto.setBokBooksetDate(bokBooksetDate);
    	channelAcctSubjectDto.setListBokBooksetExt(listBokBooksetExt);
    	channelAcctSubjectDto.setBokAcctSubjectInfo(bokAcctSubjectInfo);
    	channelAcctSubjectDto.setBokAcctLayeringInfo(bokAcctLayeringInfo);
    	channelAcctSubjectDto.setBokBooksetPlanList(bokBooksetPlanList);
    	channelAcctSubjectDto.setBokDetailIdList(bokDetailIdList);
    	channelAcctSubjectDto.setFinCashInfoList(finCashInfoList);
    	channelAcctSubjectDto.setFinCashCalcRuleList(finCashCalcRuleList);
    	channelAcctSubjectDto.setStlFinProductAcctList(stlFinProductAcctList);
    	channelAcctSubjectDto.setBokBooksetCapacctInfoList(bokBooksetCapacctInfoList);
    	channelAcctSubjectDto.setjJPortfolio(jJPortfolio);
    	channelAcctSubjectDto.setjJBkCashAcctList(jJBkCashAcctList);
    	return channelAcctSubjectDto;
    	
    }
	/**
     * 现金流计算规则信息
     */
    private List<FinCashCalcRule> queryFinCashCalcRuleList(Map<String, Object> map) {
		return mapper.queryFinCashCalcRuleList(map);
	}

	/**
     * 查询子账套信息
     * @param acctSubjectId 核算主体代码
     * @param channelId 通道代码
     * @param channelList 通道代码集合
     * @return List<SubBokBooksetInfoDto> 子账套信息Dto
     */
    public List<BokBooksetSonDto> queryBokBooksetSonInfo(String acctSubjectId, String channelId, List<String> channelList){
    	Map<String,Object> map=new HashMap<String,Object>();
    	map.put("acctSubjectId", acctSubjectId);
    	map.put("channelId", channelId);
    	map.put("channelList", channelList);
    	return channelCommonService.queryBokBooksetSonInfo(map);
    }
    /**
     * 通道核算信息_新增
     * @param dtoList 待入库通道核算信息List：List<通道核算信息DTO>
     * @return ChannelAcctSubjectDto 通道核算信息DTO
     */
    
    public ChannelAcctSubjectDto channelAcctSubjectAdd(List<ChannelAcctSubjectDto> dtoList){
    	//定义 变量.resultObject
    	ChannelAcctSubjectDto resultObject=new ChannelAcctSubjectDto();
    	//变量.resultObject.处理结果 = 00
    	resultObject.setResultCode("00");
    	
    	//定义 变量.标的金融产品List
    	List<FinProduct> finProductList=new ArrayList<FinProduct>();
    	
    	// 变量.标的金融产品扩展List
    	List<FinProductAdd> finProductAddList=new ArrayList<FinProductAdd>();
    	
    	// 变量.标的类金融产品类型List
    	List<FinProductType> finProductTypeList=new ArrayList<FinProductType>();
    	
    	/**add by wujie on 20190325 start**/
    	//变量.金融产品估值参数List
    	List<FinProductValuationPara> finProductValuationParaList = new ArrayList<FinProductValuationPara>();
    	/**add by wujie on 20190325 end**/
    	
    	/**add by wujie on 20190322 start**/
    	//变量.账套信息List
    	List<BokBookset> bokBooksetList = new ArrayList<BokBookset>();
    	/**add by wujie on 20190322 end**/
    	// 变量.母子账套关系List
    	List<BokBooksetRe> bokBooksetReList=new ArrayList<BokBooksetRe>();
    	
    	// 变量.账套日期List
    	List<BokBooksetDate> bokBooksetDateList=new ArrayList<BokBooksetDate>();
    	
    	// 账套配置List
    	List<BokBooksetExt> listBokBooksetExt = new ArrayList<BokBooksetExt>();
    	
    	// 变量.核算主体信息List
    	List<BokAcctSubjectInfo> bokAcctSubjectInfoList=new ArrayList<BokAcctSubjectInfo>();
    	
    	// 变量.核算分层信息List
    	List<BokAcctLayeringInfo> bokAcctLayeringInfoList=new ArrayList<BokAcctLayeringInfo>();
    	
    	// 变量.账套估值方案List
    	List<BokBooksetPlan> bokBooksetPlanList=new ArrayList<BokBooksetPlan>();
    	
    	// 变量.账务明细代码List
    	List<BokDetailId> bokDetailIdList=new ArrayList<BokDetailId>();
    	
    	// 变量.子账套通道现金流信息List
    	List<FinCashInfo> finCashInfoList=new ArrayList<FinCashInfo>();
    	
    	// 变量.子账套通道现金流计算规则List
    	List<FinCashCalcRule> finCashCalcRuleList=new ArrayList<FinCashCalcRule>();
    	
    	// 变量.金融产品账户List
    	List<StlFinProductAcct> stlFinProductAcctList=new ArrayList<StlFinProductAcct>();
    	
    	// 变量.账套清算账户表List
    	List<BokBooksetCapacctInfo> bokBooksetCapacctInfoList=new ArrayList<BokBooksetCapacctInfo>();
    	
    	//2. 处理详情
    	dtoList.forEach(x -> {
    		//变量.标的金融产品List.add(L.标的金融产品）
    		finProductList.add(x.getFinProduct());
    		//变量.标的金融产品扩展List.add(L.标的金融产品扩展)
    		finProductAddList.add(x.getFinProductAdd());
    		//变量.标的类金融产品类型List.add(L.标的类金融产品类型)
    		finProductTypeList.add(x.getFinProductType());
    		
    		/**add by wujie on 20190325 start**/
    		//变量.金融产品估值参数List.add(L.金融产品估值参数)
    		finProductValuationParaList.add(x.getFinProductValuationPara());
    		/**add by wujie on 20190325 end**/
    		
    		/**add by wujie on 20190321 start**/
    		//变量.账套信息List.add(L.账套信息)
    		bokBooksetList.add(x.getBokBookset());
    		/**add by wujie on 20190321 end**/
    		//变量.母子账套关系List.add(L.母子账套关系)
    		bokBooksetReList.add(x.getBokBooksetRe());
    		//变量.账套日期List.add(L.账套日期)
    		bokBooksetDateList.add(x.getBokBooksetDate());
    		// 变量.账套配置List.addAll(L.账套配置List)
    		if(x.getListBokBooksetExt()!=null && x.getListBokBooksetExt().size()>0){
    			listBokBooksetExt.addAll(x.getListBokBooksetExt());
    		}
    		//变量.核算主体信息List.add(L.核算主体信息)
    		bokAcctSubjectInfoList.add(x.getBokAcctSubjectInfo());
    		//变量.核算分层信息List.add(L.核算分层信息）
    		bokAcctLayeringInfoList.add(x.getBokAcctLayeringInfo());
    		//变量.账套估值方案List.addAll(L.账套估值方案List)
    		if(x.getBokBooksetPlanList()!=null && x.getBokBooksetPlanList().size()>0){
    			bokBooksetPlanList.addAll(x.getBokBooksetPlanList());
    		}
    		//变量.账务明细代码List.addAll(L.账务明细代码List)
    		if(x.getBokDetailIdList()!=null && x.getBokDetailIdList().size()>0){
    			bokDetailIdList.addAll(x.getBokDetailIdList());
    		}
    		//变量.子账套通道现金流信息List.addAll(L.子账套通道现金流信息List)
    		if(x.getFinCashInfoList()!=null && x.getFinCashInfoList().size()>0){
    			finCashInfoList.addAll(x.getFinCashInfoList());
    		}
    		//变量.子账套通道现金流计算规则List.addAll(L.子账套通道现金流计算规则List)
    		if(x.getFinCashCalcRuleList()!=null && x.getFinCashCalcRuleList().size()>0){
    			finCashCalcRuleList.addAll(x.getFinCashCalcRuleList());
    		}
    		//变量.金融产品账户表List.addAll(L.金融产品账户表List)
    		if(x.getStlFinProductAcctList()!=null && x.getStlFinProductAcctList().size()>0){
    			stlFinProductAcctList.addAll(x.getStlFinProductAcctList());
    		}
    		//变量.账套清算账户表List.addAll(L.账套清算账户表List)
    		if(x.getBokBooksetCapacctInfoList()!=null && x.getBokBooksetCapacctInfoList().size()>0){
    			bokBooksetCapacctInfoList.addAll(x.getBokBooksetCapacctInfoList());
    		}
    	});
    	
    	
    	//标的金融产品
    	channelCommonService.insertValFinProductBatch(finProductList);
    	
    	//标的金融产品扩展
    	channelCommonService.insertFinProductExtBatch(finProductAddList);
    	
    	//标的类金融产品类型
    	channelCommonService.insertValFinProductTypeBatch(finProductTypeList);
    	
    	/**add by wujie on 20190325 start**/
    	//金融产品估值参数表
    	channelCommonService.insertBatchFinProductValuationPara(finProductValuationParaList);
    	/**add by wujie on 20190325 end**/
    	
    	/**add by wujie on 20190322 start**/
    	//账套信息
    	channelCommonService.insertBatchBokBookset(bokBooksetList);
    	/**add by wujie on 20190322 end**/
    	
    	//母子账套关系
    	channelCommonService.insertBokBooksetReBatch(bokBooksetReList);
    	
    	//账套日期
    	channelCommonService.insertBokBooksetDateBatch(bokBooksetDateList);
    	
    	//变量.账套配置List
    	channelCommonService.insertBokBooksetExtBatch(listBokBooksetExt);
    	
    	//核算主体信息
    	channelCommonService.insertBokAcctSubjectInfoBatch(bokAcctSubjectInfoList);
    	
    	//核算分层信息
    	channelCommonService.insertBokAcctLayeringInfoBatch(bokAcctLayeringInfoList);
    	 
    	//账套估值方案
    	channelCommonService.insertBokBooksetPlanBatch(bokBooksetPlanList);
    	
    	//账务明细代码
    	channelCommonService.insertBokDetailIdBatch(bokDetailIdList);
    	
    	//子账套通道现金流信息
    	channelCommonService.insertFinCashInfoBatch(finCashInfoList);
    	
    	//子账套通道现金流计算规则
    	channelCommonService.insertFinCashCalcRuleBatch(finCashCalcRuleList);
    	
    	//金融产品账户表
    	channelCommonService.insertStlFinProductAcctBatch(stlFinProductAcctList);
    	
    	//账套清算账户表
    	channelCommonService.insertBokBooksetCapacctInfoBatch(bokBooksetCapacctInfoList);
    	
    	return resultObject;
    }
    /**
     * 通道核算信息_删除
     * @param dtoList 待入库通道核算信息List：List<通道核算信息DTO>
     * @return ChannelAcctSubjectDto 通道核算信息DTO
     */
    
    public ChannelAcctSubjectDto channelAcctSubjectDelete(List<ValChannelProdDealDto> dtoList){
    	//定义 变量.resultObject
    	ChannelAcctSubjectDto resultObject=new ChannelAcctSubjectDto();
    	//变量.resultObject.处理结果 = 00
    	resultObject.setResultCode("00");
    	if(CollectionUtils.isNotEmpty(dtoList)){
        	//初始化金融产品标的（计划类货币型资产）
    		channelCommonService.delValFinProductBatch(dtoList);
        	//初始化通道子账套信息
    		channelCommonService.delBokBooksetReBatch(dtoList);
    		// 删除账套配置表
    		channelCommonService.delBokBooksetExtBatch(dtoList);
        	//初始化估值方案、税务方案
    		channelCommonService.delBokBooksetPlanBatch(dtoList);
        	//初始化通道现金流信息
    		channelCommonService.delFinCashInfoBatch(dtoList);
    		//初始化资金账户信息
    		channelCommonService.delStlFinProductAcctBatch(dtoList);
    	}

    	return resultObject;
    }
    /**
     * 子账套信息更新(根据账套信息)
     * @param BokBookset 账套信息
     * @return 账套日期：BokBooksetDate
     */
    
    public Map<String,Object> updateBokBooksetSon(BokBookset bokBookset, BokBooksetDate bokBooksetDate){
    	//变量.母子账套关系List
    	List<BokBooksetSonDto> bokBooksetReList=new ArrayList<BokBooksetSonDto>();
    	//返回resultMap
    	Map<String,Object> resultMap=new HashMap<String,Object>();
    	resultMap.put("bokBookset", bokBookset);
    	resultMap.put("bokBooksetDate", bokBooksetDate);
    	
    	//查询账套下的子账套信息
    	bokBooksetReList=channelCommonService.queryBokBooksetReInfo(resultMap);

    	if(bokBooksetReList.size()!=0){
    		resultMap.put("bokBooksetReList", bokBooksetReList);
    		//更新子账套的账套日期信息
    		channelCommonService.updateBokBooksetReInfo(resultMap);
    		//更新子账套核算分层信息
    		channelCommonService.updateBokAcctSubjectInfo(resultMap);
    		
    	}
    	resultMap.clear();
    	resultMap.put("RESULT_CODE", "00");
    	return resultMap;
    }
    /**
     *  金融产品账户信息查询
     */
    public List<StlFinProductAcct> queryStlFinProductAcctInfo (Map<String,Object> map){
    	return mapper.queryStlFinProductAcctInfo(map);
    }
    /**
     * 通道资金账户信息更新
     */
   public Map<String,Object> updateStlFinProductAcct(List<StlFinProductAcct> list){
	   
	   //1.2定义变量
	   //变量.resultMap
	   Map<String,Object> resultMap=new HashMap<String,Object>();
	   //变量.待更新通道Map
	   Map<String,Object> initMap=new HashMap<String,Object>();
	   //变量.金融产品账户表List
	   List<StlFinProductAcct> stlFinProductAcctList=new ArrayList<StlFinProductAcct>();
	   //变量.账套清算账户表List
	   List<BokBooksetCapacctInfo> bokBooksetCapacctInfoList=new ArrayList<BokBooksetCapacctInfo>();
	   //变量.账务明细代码List
	   List<BokDetailId> bokDetailIdList=new ArrayList<BokDetailId>();
	   //2.处理详情
	   if(CollectionUtils.isEmpty(list)){
		   return resultMap;
	   }
	   list.forEach(x ->{
		   if(x.getFinprodType().equals(DictCodeListExt1.金融产品类型_通道.getValue())){
			   String channelId=x.getFinprodId();
			   if(initMap.get(channelId)==null){
				   initMap.put(channelId, channelId);
			   }
		   }
	   });
	   //变量.待更新子账套List
	   List<BokBooksetSonDto> bokBooksetSonDtoList=new ArrayList<BokBooksetSonDto>();
	   //变量.待初始资金账户List
	   List<StlFinProductAcct> initStlFinProductAcctList=new ArrayList<StlFinProductAcct>();
	   for(String key:initMap.keySet()){
		   resultMap.put("channelId", key);
		   bokBooksetSonDtoList=channelCommonService.queryBokBooksetSonInfo(resultMap);
		   if(CollectionUtils.isEmpty(bokBooksetSonDtoList)){
			   continue;
		   }
		   resultMap.clear();
		   resultMap.put("finprodType", DictCodeListExt1.金融产品类型_通道.getValue());
		   resultMap.put("finprodId", key);
		   initStlFinProductAcctList=this.queryStlFinProductAcctInfo(resultMap);
		   if(CollectionUtils.isEmpty(initStlFinProductAcctList)){
			   continue;
		   }
		   //定义 变量.四级科目尾号集合 Map<String,BokDetailId>
           Map<String, BokDetailId> bokDetailIdMap = new HashMap<>();
           for(BokBooksetSonDto b:bokBooksetSonDtoList){
               for(StlFinProductAcct s:initStlFinProductAcctList){
                   String acctType = s.getAcctType();
                   /**modify by wujie on 20190613 start
                    if(StringUtil.equals(s.getAcctType(), DictCodeList.资金账户类型_通道账户.getValue())){
                    **/
                   if(StringUtil.equals(s.getAcctType(), DictCodeListExt1.资金账户类型_产品托管户.getValue())){
                       /**modify by wujie on 20190613 end**/
					   StlFinProductAcct stlFinProductAcct = new StlFinProductAcct();
					   stlFinProductAcct.setFinprodId(b.getAcctSubjectId());
					   stlFinProductAcct.setFinprodType(b.getAcctSubjectType());
					   stlFinProductAcct.setAcctNo(InterfCommonConstant.清算款账户号+s.getCcy());
					   stlFinProductAcct.setCcy(s.getCcy());
					   stlFinProductAcct.setAcctType(DictCodeListExt1.资金账户类型_待清算款.getValue());
					   stlFinProductAcctList.add(stlFinProductAcct);
					   
					   BokBooksetCapacctInfo bokBooksetCapacctInfo = new BokBooksetCapacctInfo();
					   bokBooksetCapacctInfo.setBooksetId(b.getBooksetIdSon());
					   bokBooksetCapacctInfo.setAcctNo(InterfCommonConstant.清算款账户号+s.getCcy());
					   bokBooksetCapacctInfo.setCcy(s.getCcy());
					   bokBooksetCapacctInfo.setAcctType(DictCodeListExt1.资金账户类型_待清算款.getValue());
					   bokBooksetCapacctInfoList.add(bokBooksetCapacctInfo);
					   
					   acctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
                   }
                   StlFinProductAcct dto=new StlFinProductAcct();
				   dto.setFinprodId(b.getAcctSubjectId());
				   dto.setFinprodType(b.getAcctSubjectType());
				   dto.setCcy(s.getCcy());
				   dto.setAcctNo(s.getAcctNo());
				   dto.setAcctType(acctType);//modify by dongjun on 20190321 通道变更
				   stlFinProductAcctList.add(dto);
				// 初始化账套清算账户表
	    			BokBooksetCapacctInfo bokBooksetCapacctInfo=new BokBooksetCapacctInfo();
	    			bokBooksetCapacctInfo.setBooksetId(b.getBooksetIdSon());
	    			bokBooksetCapacctInfo.setAcctNo(s.getAcctNo());
	    			bokBooksetCapacctInfo.setCcy(s.getCcy());
	    			bokBooksetCapacctInfo.setAcctType(acctType);//modify by dongjun on 20190321 通道变更
	    			
	    			bokBooksetCapacctInfoList.add(bokBooksetCapacctInfo);
	    			//初始化活期存款计提账务明细代码
	    				//变量.账务明细代码 
	    			BokDetailId bokDetail=this.getBokDetailId(s.getAcctNo(), null, acctType, null, null, s.getCcy());//modify by dongjun on 20190321 通道变更
                    String bokDetailId="";
                    if(StringUtil.isNotEmpty(bokDetail)){
                         bokDetailId=bokDetail.getBokDetailId();
                    }

                    bokDetail= new BokDetailId();
                    bokDetail.setBooksetId(b.getBooksetIdSon());
                    bokDetail.setBokDetailId(bokDetailId);
                    bokDetail.setBusiId(s.getAcctNo());
                    bokDetail.setBusiDetailType(acctType);//modify by dongjun on 20190321 通道变更
                    bokDetail.setBranch(0L);
                    // 币种 S.币种
                    bokDetail.setCcy(s.getCcy());
                   /** add by lby on 20201225 start**/
                   //定义 变量.bokDetailId =	变量.四级科目尾号集合.get(B.子账套代码+变量.账务明细代码)
                   BokDetailId bokDetailIdTmp = bokDetailIdMap.get(b.getSubBokBooksetId() + bokDetail.getBokDetailId());
                   if (!StringUtil.isNotEmpty(bokDetailIdTmp)){
                       //调用共通设计->【详细设计-接口共通方法设计.xlsx】->四级科目号设置SHEET
                       //变量.bokDetailId = 返回值
                       bokDetailIdTmp = this.settingDetailSubjectNoTaiL(s.getAcctNo(), acctType, null, null, b.getSubBokBooksetId());
                   }
                   //变量.四级科目尾号集合.put(B.子账套代码+变量.账务明细代码,变量.bokDetailId)
                   bokDetailIdMap.put(b.getSubBokBooksetId() + bokDetail.getBokDetailId(),bokDetailIdTmp);
                   //变量.账务明细代码Dto.四级科目尾号 = 变量.bokDetailId.四级科目尾号
                   bokDetail.setDetailSubjectNoTail(bokDetailIdTmp.getDetailSubjectNoTail());
                   //变量.账务明细代码Dto.四级科目尾号名称 = 变量.bokDetailId.四级科目尾号名称
                   bokDetail.setDetailSubnameTail(bokDetailIdTmp.getDetailSubnameTail());
                   /** add by lby on 20201225 end**/
                   bokDetailIdList.add(bokDetail);

			   }
			   
		   };
	   }
	   resultMap.put("StlFinProductAcct_List", stlFinProductAcctList);
	   resultMap.put("BokBooksetCapacctInfo_List", bokBooksetCapacctInfoList);
	   resultMap.put("BokDetailId_List", bokDetailIdList);
	   return resultMap;
   }
   /**
    * 通道费用现金流计算规则表更新
    */
   public List<FinCashCalcRule> updateChannelFinCashCalcRule(Date interfDate, List<String> channelList){
	   /*-----------------------------1.定义变量------------------------------*/
	   //变量.子账套信息List
	   	List<BokBooksetSonDto> bokBooksetSonDtoList=new ArrayList<BokBooksetSonDto>();

	   //变量.待更新通道费率List
	   List<FinCashCalcRule> finCashCalcRuleList=new ArrayList<FinCashCalcRule>();

	   // 变量.resultList
	   List<FinCashCalcRule> resultList=new ArrayList<FinCashCalcRule>();
	   
	   /*-----------------------------2.详情处理------------------------------*/
	   //2.1 子账套信息
	   Map<String,Object> paramsMap=new HashMap<String,Object>();
	   paramsMap.put("channelList",channelList);
	   bokBooksetSonDtoList=channelCommonService.queryBokBooksetSonInfo(paramsMap);
	   if(CollectionUtils.isEmpty(bokBooksetSonDtoList)){
		   return resultList;
	   }
	   
	   //2.2 通道现金流计算规则表数据查询
	   paramsMap = new HashMap<String,Object>();
	   paramsMap.put("finprodIds",channelList);
	   paramsMap.put("finprodType", DictCodeListExt1.金融产品类型_通道.getValue());
	   paramsMap.put("cashType1", DictCodeListExt1.现金流一级类型_费用.getValue());
	   finCashCalcRuleList = this.queryFinCashCalcRuleList(paramsMap);
	   if(CollectionUtils.isEmpty(finCashCalcRuleList)){
		   return resultList;
	   }
	   //2.3 初始化产品+通道的费率信息
	   for(FinCashCalcRule L:finCashCalcRuleList){
		   for(BokBooksetSonDto F:bokBooksetSonDtoList){
		       if(!L.getFinprodId().equals(F.getChannelId())) {
		           continue;
		       }
				FinCashCalcRule finCashCalcRule=new FinCashCalcRule();
				finCashCalcRule.setCashId(InterfHelper.getMd5CashId_(L.getCashId(),F.getFinprodId()));
				finCashCalcRule.setEffDate(L.getEffDate());
				finCashCalcRule.setCalcType(L.getCalcType());
				finCashCalcRule.setBasis(L.getBasis());
				finCashCalcRule.setBaseType(L.getBaseType());
				finCashCalcRule.setBaseDateType(L.getBaseDateType());
				finCashCalcRule.setYield(L.getYield());
				finCashCalcRule.setBranch(0L);
				finCashCalcRule.setFinprodId(F.getFinprodId()+"_"+L.getFinprodId());
				finCashCalcRule.setCreateTime(L.getCreateTime());
				finCashCalcRule.setFinprodType(L.getFinprodType());
				resultList.add(finCashCalcRule);
		   }
	   }
	   return resultList;
   }
    
    /**
     * 投资端通道费拆出
     * 
     * @param trdProductDeal 金融产品交易
     * @param channelFeeInfoDeal 通道费用信息Dto
     * @return
     */
    @SuppressWarnings({"unchecked"})
    public Map<String,Object> assetChlFeeLend(TrdProductDeal trdProductDeal, ChannelFeeInfoDeal channelFeeInfoDeal){
    	Map<String,Object> resultMap = new HashMap<>();
    	// 1.3 定义变量
    	// 定义 变量.金融产品交易Dto
    	TrdProductDeal rtnTrdProductDeal = new TrdProductDeal();
    	// 定义 变量.金融产品附属信息Dto
    	TrdProductDealAdd trdProductDealExt = new TrdProductDealAdd();
    	// 定义 变量.交易费用明细Dto
    	TrdTradeFeeDetail TgTradeFeeDetail = new TrdTradeFeeDetail();
    	// 定义 变量.入账信息表List 
    	List<BokDealAccounting> listBokDealAccounting = new ArrayList<>();
    	// 定义 变量.入账账务明细表List
    	List<BokDealAccountingDetail> listBokDealAccountingDetail =  new ArrayList<>();
    	// 定义 变量.账务明细代码表List
    	List<BokDetailId> listBokDetailId =  new ArrayList<>();
    	// 定义 变量.结算指令
    	List<StlOrders> listStlOrders =  new ArrayList<>();
    	// 定义 变量.结算指令扩展表
    	List<StlOrdersExt> listStlOrdersExt = new ArrayList<>();
    	// 定义 变量.核算主体类型
    	String acctSubjectType = null;

        // IF 输入.通道费用信息DTO IS NULL OR 数据字典：是否.否 eq 输入.通道费用信息DTO.通道费是否随交易支付
        if (channelFeeInfoDeal == null || DictCodeList.是否_否.getValue().equals(channelFeeInfoDeal.getIsPayWithTrade())) {
            return resultMap;
        }

        // 2.1 通道费交易初始化
    	String tradeId = getTradeid(DictCodeList.交易方向_付费.getValue(),trdProductDeal.getTradeDate(),"A");
    	rtnTrdProductDeal.setTradeId(tradeId);
    	rtnTrdProductDeal.setBusiType(DictCodeList.业务类型_产品费用交易.getValue());
    	rtnTrdProductDeal.setFinprodId(channelFeeInfoDeal.getAcctSubjectId());
    	/**modify by wujie on 20190329 start
    	rtnTrdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_交易费用.getValue());**/
    	rtnTrdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_通道费.getValue());
    	/**modify by wujie on 20190329 end**/
    	rtnTrdProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
    	rtnTrdProductDeal.setTradeDate(trdProductDeal.getTradeDate());
    	rtnTrdProductDeal.setSettleDate(trdProductDeal.getSettleDate());
    	rtnTrdProductDeal.setCcy(trdProductDeal.getCcy());
    	/**modify wujie on 20190326 start
    	rtnTrdProductDeal.setTradeAmt(trdProductDeal.getTradeAmt());
    	**/
    	rtnTrdProductDeal.setTradeAmt(channelFeeInfoDeal.getChlFeeAmt());
    	/**modify by wujie on 20190326 end**/
    	
    	rtnTrdProductDeal.setPs(DictCodeList.交易方向_付费.getValue());
    	rtnTrdProductDeal.setTradeStatus(trdProductDeal.getTradeStatus());
    	rtnTrdProductDeal.setTotleTradeId(trdProductDeal.getTradeId());
    	rtnTrdProductDeal.setChlId(channelFeeInfoDeal.getChannelId());
    	rtnTrdProductDeal.setRegistOrg(trdProductDeal.getRegistOrg());
    	
    	rtnTrdProductDeal.setCreateTime(trdProductDeal.getCreateTime());
    	
    	trdProductDealExt.setTradeId(tradeId);
//    	trdProductDealExt.setProdId(channelFeeInfoDeal.getProdId());//一体化无此字段
    	trdProductDealExt.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());
    	trdProductDealExt.setCreateTime(trdProductDeal.getCreateTime());
    	
    	TgTradeFeeDetail.setTradeId(tradeId);
    	TgTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
    	TgTradeFeeDetail.setIsPayWithTrade(channelFeeInfoDeal.getIsPayWithTrade());
    	TgTradeFeeDetail.setFeeAmt(channelFeeInfoDeal.getChlFeeAmt());
    	TgTradeFeeDetail.setFeeId(DictCodeListExt1.一次性费用类型_通道费.getValue());
    	rtnTrdProductDeal.setCreateTime(trdProductDeal.getCreateTime());
    	
    	// 2.2 交易入账
    	InterfProductDealBookCommonDto interfProductDealBookCommonDto = dealAccounting(rtnTrdProductDeal, channelFeeInfoDeal.getProdId());
    	listBokDealAccounting.add(interfProductDealBookCommonDto.getBokDealAccounting());
    	listBokDealAccountingDetail.add(interfProductDealBookCommonDto.getBokDealAccountingDetail());
    	listBokDetailId.add(interfProductDealBookCommonDto.getBokDetailId());
    	acctSubjectType = interfProductDealBookCommonDto.getAcctSubjectType();
    	
    	// 2.3  结算指令生成及入账
    	Map<String,Object> rtnMap = instructionGenerate(rtnTrdProductDeal, channelFeeInfoDeal.getProdId(), acctSubjectType, null);
    	if((List<StlOrders>)rtnMap.get("ORDERS") != null && ((List<StlOrders>)rtnMap.get("ORDERS")).size() > 0){
    		listStlOrders.addAll((List<StlOrders>)rtnMap.get("ORDERS"));
    	}
    	if((List<StlOrdersExt>)rtnMap.get("ORDERS_EXT") != null && ((List<StlOrdersExt>)rtnMap.get("ORDERS_EXT")).size() > 0){
    		listStlOrdersExt.addAll((List<StlOrdersExt>)rtnMap.get("ORDERS_EXT"));
    	}
    	if((List<BokDealAccounting>)rtnMap.get("ACCOUNTING") != null && ((List<BokDealAccounting>)rtnMap.get("ACCOUNTING")).size() > 0){
    		listBokDealAccounting.addAll((List<BokDealAccounting>)rtnMap.get("ACCOUNTING"));
    	}
    	if((List<BokDealAccountingDetail>)rtnMap.get("ACCOUNTING_DETAI") != null && ((List<BokDealAccountingDetail>)rtnMap.get("ACCOUNTING_DETAI")).size() > 0){
    		listBokDealAccountingDetail.addAll((List<BokDealAccountingDetail>)rtnMap.get("ACCOUNTING_DETAI"));
    	}
    	if((List<BokDetailId>)rtnMap.get("BOK_DETAIL_ID") != null && ((List<BokDetailId>)rtnMap.get("BOK_DETAIL_ID")).size() > 0){
    		listBokDetailId.addAll((List<BokDetailId>)rtnMap.get("BOK_DETAIL_ID"));
    	}
    	
    	// 2.4  结果赋值
    	resultMap.put("TrdProductDeal",rtnTrdProductDeal);	
		
    	resultMap.put("TrdProductDealExt",trdProductDealExt);			
    			
    	resultMap.put("TgTradeFeeDetail",TgTradeFeeDetail);		
    			
    	resultMap.put("BokDealAccounting_List",listBokDealAccounting);	
    			
    	resultMap.put("BokDealAccountingDetail_List",listBokDealAccountingDetail);	
    			
    	resultMap.put("BokDetailId_list",listBokDetailId);		
    			
    	resultMap.put("StlOrders_List",listStlOrders);	
    			
    	resultMap.put("StlOrdersExt_List",listStlOrdersExt);		

    	return resultMap;
    }

    /**
     * 查询待删除通道费用交易
     * 
     * @param listTrdProductDeal 待删除交易信息
     * @return 待删除通道费用交易
     */
    public List<TrdProductDeal> queryDelChlFeeDeal(List<TrdProductDeal> listTrdProductDeal){
    	List<TrdProductDeal> delListTrdProductDeal  = new ArrayList<>();
    	if(CollectionUtils.isNotEmpty(listTrdProductDeal)){
    		delListTrdProductDeal = mapper.queryDelChlFeeDeal(listTrdProductDeal);
    	}
    	return delListTrdProductDeal;
    }
    /**
     * 通道费用现金流信息表更新
     * @param tdList
     * @return
     */
    public List<FinCashInfo> updateFinCashInfoTd(Date interfDate, List<String> tdList){
    	//1.1 定义变量
    	//变量.待更新现金流信息
    	List<FinCashInfo> unFinCashInfo  = new ArrayList<FinCashInfo>();
	   	
    	//变量.子账套信息List
	   	List<BokBooksetSonDto> bokBooksetSonDtoList=new ArrayList<BokBooksetSonDto>();
	   	
	    //变量.待更新子账套现金流信息
    	List<FinCashInfo> resultList = new ArrayList<FinCashInfo>();
    	
        //2.1 子账套信息
 	    Map<String,Object> paramsMap=new HashMap<String,Object>();
 	    paramsMap.put("channelList",tdList);
 	    bokBooksetSonDtoList=channelCommonService.queryBokBooksetSonInfo(paramsMap);
 	    if(CollectionUtils.isEmpty(bokBooksetSonDtoList)){
 		    return resultList;
 	    }
 	    
 	    //2.2 查询需要更新的费用现金流信息
 	    paramsMap = new HashMap<String,Object>();
 	    paramsMap.put("finprodType", DictCodeListExt1.金融产品类型_通道.getValue());
 	    paramsMap.put("cashType1", DictCodeListExt1.现金流一级类型_费用.getValue());
 	    paramsMap.put("finprodIds", tdList);
 	    unFinCashInfo = this.queryCashInfoList(paramsMap);
 	    if(CollectionUtils.isEmpty(unFinCashInfo)){
		     return resultList;
	    }
    	//2.3 更新子账套对应的费用现金流信息
 	    for(FinCashInfo cash :unFinCashInfo){
 	    	for(BokBooksetSonDto bok :bokBooksetSonDtoList){
 	            if(!cash.getFinprodId().equals(bok.getChannelId())) {
                    continue;
                }
 	    		FinCashInfo finCashInfo = new FinCashInfo();
 	    		finCashInfo.setCashId(InterfHelper.getMd5CashId_(cash.getCashId(),bok.getFinprodId()));
 	    		finCashInfo.setCashType1(cash.getCashType1());
 	    		finCashInfo.setCashType2(cash.getCashType2());
 	    		finCashInfo.setFinprodId(bok.getBooksetIdSon());
 	    		finCashInfo.setFinprodType(DictCodeListExt1.金融产品类型_通道.getValue());
 	    		finCashInfo.setBranch(0L);
 	    		finCashInfo.setCreateTime(interfDate);
 	    		resultList.add(finCashInfo);
 	    	}
 	    }
    	return resultList;
    }
    
    /**
     * 交易入账_分层产品(分支序号)
     * 
     * @param trdProductDeal 理财产品代码prodId
     * @param trdFinProductBarter
     * @return Map<String,Object>
     */
    public InterfProductDealBookCommonDto dealAccounting_Layering_Branch(TrdProductDeal trdProductDeal,
                                                                         String prodId, Long branch,
                                                                         String prodFinprodType,
                                                                         List<PtlTrade> ptlTradeList, TrdFinProductBarter trdFinProductBarter) {
    	// 返回结果集
        InterfProductDealBookCommonDto interfProductDealBookCommonDto = new InterfProductDealBookCommonDto();
        // 账套信息List
        List<ValBooksetInfoDto> valBooksetInfoDtoList = new ArrayList<>();
        // 入账信息list
        List<BokDealAccounting> bokDealAccountingList = new ArrayList<>();
        // 入账账务明细list
        List<BokDealAccountingDetail> bokDealAccountingDetailList = new ArrayList<>();
        // 账务明细代码表list
        List<BokDetailId> bokDetailIdList = new ArrayList<>();
        // 变量实际发生日
        Date sjfsDate = trdProductDeal.getTradeDate();
        /**add by wujie on 20190525 start**/
        //交易入账日期类型为交割日
        if("02".equals(this.getDealDateType(trdProductDeal))){
        	sjfsDate = trdProductDeal.getSettleDate();
        }
        /**add by wujie on 20190525 end**/
        
        // 变量 账务明细代码
        String bookNo = null;
        // 变量 投资目的
        String invAim = "";
        Long breach = null;
        
        String layeringId = prodId;
        if(StringUtil.isNotEmpty(trdProductDeal.getChlId())){
        	layeringId = layeringId + FinalString.LAYERING_SPLIT + trdProductDeal.getChlId() +
        			FinalString.LAYERING_SPLIT + DictCodeListExt1.金融产品类型_通道.getValue();
        } else {
            if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(prodFinprodType)){
            	breach = branch; 
            	layeringId =  layeringId+ breach;
            }
            layeringId = layeringId+ FinalString.LAYERING_SPLIT+prodFinprodType;
        }
        valBooksetInfoDtoList = getBokBooksetByProdId_Layering(layeringId);

        //IF 数据字典：金融产品类型.投资组合 eq 输入.金融产品类型 and 输入.金融产品交易.通道代码 IS NULL
        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(prodFinprodType)
                /** modify by yanya on 20191122 防止chnlI为空字符串**/
                //&& trdProductDeal.getChlId() == null
                && StringUtil.isEmpty(trdProductDeal.getChlId())
        		&& !DictCodeList.业务类型_净值型TA交易.getValue().equals(trdProductDeal.getBusiType())
        		&& !DictCodeList.业务类型_货币型TA交易.getValue().equals(trdProductDeal.getBusiType())
        		&& !DictCodeList.业务类型_预期收益型TA交易.getValue().equals(trdProductDeal.getBusiType())
        		&& !DictCodeList.业务类型_产品费用交易.getValue().equals(trdProductDeal.getBusiType())){
            List<ValBooksetInfoDto>  valBooksetInfoDtos= getSonPtlPortfolo(prodId,ptlTradeList);
            if (CollectionUtils.isNotEmpty(valBooksetInfoDtos)) {
                valBooksetInfoDtoList.addAll(valBooksetInfoDtos);
            }
        }
        /**ADD BY WUJIE ON 20191118 START**/
        Map<String, ValBooksetInfoDto> qcMap = new HashMap<String, ValBooksetInfoDto>();
        List<ValBooksetInfoDto> bokSetList = new ArrayList<ValBooksetInfoDto>();
        for(ValBooksetInfoDto dto:valBooksetInfoDtoList){
        	if(qcMap.get(dto.getBooksetId())==null){
        		bokSetList.add(dto);
        	}
        	qcMap.put(dto.getBooksetId(), dto);
        }
        /**ADD BY WUJIE ON 20191118 END**/
        for (ValBooksetInfoDto bokBookset : bokSetList) {
        	String invAimMethod = null;
            if (DictCodeListExt1.金融产品类型_债券.getValue().equals(trdProductDeal.getFinprodType())
                && DictCodeList.业务类型_银行间债券交易.getValue().equals(trdProductDeal.getBusiType())) {
                BokBooksetExt bokBooksetExt = queryBokBooksetExtbyId(DictCode.投资目的获取方式.getValue(),
                		bokBookset.getBooksetId());
                invAimMethod = bokBooksetExt != null ? bokBooksetExt.getParamValue() : DictCodeListExt1.投资目的获取方式_上游提供.getValue();
                if (bokBooksetExt != null
                    && DictCodeListExt1.投资目的获取方式_上游提供.getValue().equals(bokBooksetExt.getParamValue())) {
                    invAim = trdProductDeal.getInvAim();
                }
                else {
                    InvAimDto invAimDto = finProductCommonBatchService.searchInvAim(sjfsDate,
                    		bokBookset.getBooksetId(), DictCodeListExt1.证券品种_债券.getValue(),
                    		trdProductDeal.getFinprodId(), invAim);
                    if (invAimDto.getSchemeCnt() > 1) {
                        // 抛出异常
                        try {
                            throw new BusinessException("变量.账套信息Dto.账套代码—输入.金融产品交易.金融产品代码：出现重复估值方案");
                        }
                        catch (Exception e) {
                            logger.error("", e);
                        }
                    }
                    else {
                        invAim = StringUtil.isNotEmpty(
                            invAimDto.getInvAim()) ? invAimDto.getInvAim() : trdProductDeal.getInvAim();
                    }
                }
            }
            else {
                invAim = trdProductDeal.getInvAim();
            }
            
            /**add by wujie on 20201116 start remark:投资目的为999999时，设置为空**/
            if("999999".equals(invAim)){
            	invAim = null;
            }
            /**add by wujie on 20201116 end remark:投资目的为999999时，设置为空**/
            
            String busiId = trdProductDeal.getFinprodId();
            String busiDetailType = trdProductDeal.getFinprodType();
            // 定义 变量.币种 = NULL
            String ccy = null;
            // IF 输入.金融产品交易.业务类型 eq 数据字典：业务类型.活期存款交易
            if (DictCodeList.业务类型_活期存款交易.getValue().equals(trdProductDeal.getBusiType())) {
                // 变量.币种 = 输入.金融产品交易.币种
                ccy = trdProductDeal.getCcy();
            }
            if(StringUtil.equals(DictCodeList.业务类型_产品费用交易.getValue(),trdProductDeal.getBusiType())
            		|| StringUtil.equals(DictCodeList.业务类型_增值税交易.getValue(),trdProductDeal.getBusiType())){
            	if(StringUtil.equals(DictCodeListExt1.金融产品类型_通道.getValue(), bokBookset.getAcctSubjectType())){
            		busiId = prodId + FinalString.LAYERING_SPLIT + trdProductDeal.getChlId();
            	} else{
            		busiId = prodId;
            	}
            	Map<String, Object> busiMap = new HashMap<>();
            	busiMap.put("booksetId", bokBookset.getBooksetId());
            	busiMap.put("busiId", busiId);
            	busiDetailType = mapper.getFinprodType(busiMap);
            	if (StringUtils.isEmpty(busiDetailType)) {
            		busiDetailType = bokBookset.getAcctSubjectType();
            	}
            }
            
            //预提费用支付
            if(StringUtil.equals(trdProductDeal.getFeeType(), DictCodeListExt3.投资端费用品种类型_账户维护费.getValue())){
            	// 变量.业务明细代码 = 输入.证券账户+产品代码+计提开始日
            	busiId = trdProductDeal.getSecManageAcctId()+prodId+ DateUtil.format(trdProductDeal.getVdate(), "yyyyMMdd");
                // 变量.业务明细类型 = 数据字典.金融产品类型.预提费用
                busiDetailType = DictCodeListExt1.金融产品类型_预提费用.getValue();
                invAim = null;
                breach = null;
                ccy = null;
            }
            bookNo = getBokDetailId(busiId, null, busiDetailType, invAim,
                    breach, ccy).getBokDetailId();
            
            BokDealAccounting valDealAccounting = new BokDealAccounting();
            BokDealAccountingDetail valDealAccountingDetail = new BokDealAccountingDetail();
            BokDetailId valBokDetailId = new BokDetailId();
            
            // 账套代码 BOOKSET_ID 变量.账套信息Dto.账套代码
            valDealAccounting.setBooksetId(bokBookset.getBooksetId());
            // 业务编号 BUSI_NO 输入.金融产品交易.交易编号
            valDealAccounting.setBusiNo(trdProductDeal.getTradeId());
            // 业务表 BUSI_TABLE 02 金融产品表
            valDealAccounting.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            // 录入日 BOOKSET_DATE 系统时间
            valDealAccounting.setBooksetDate(sjfsDate);
            // 入账日 BOOK_DATE 变量.账套信息Dto.当前日期
            valDealAccounting.setBookDate(sjfsDate);
            // 实际发生日 HAPPEN_DATE 变量.实际发生日
            valDealAccounting.setHappenDate(sjfsDate);
            // 是否已入账 IS_ACCOUNTED N 否
            valDealAccounting.setIsAccounted(DictCodeList.是否_否.getValue());
            // 投资目的
            valDealAccounting.setInvAim(invAim);
            valDealAccounting.setInvAimMethod(invAimMethod);
            
            bokDealAccountingList.add(valDealAccounting);
            
            
            /* 2.2 入账账务明细表(BOK_DEAL_ACCOUNTING_DETAIL) */
            // 账套代码 BOOKSET_ID 变量.账套信息.账套代码
            valDealAccountingDetail.setBooksetId(bokBookset.getBooksetId());
            // 业务编号 BUSI_NO 输入.金融产品交易.交易编号
            valDealAccountingDetail.setBusiNo(trdProductDeal.getTradeId());
            // 业务表 BUSI_TABLE 02 金融产品表
            valDealAccountingDetail.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            // 业务类型 BUSI_TYPE 输入.金融产品交易.业务类型
            valDealAccountingDetail.setBusiType(trdProductDeal.getBusiType());
            // 账务明细代码 BOK_DETAIL_ID 变量.账务明细代码
            valDealAccountingDetail.setBokDetailId(bookNo);
            
            bokDealAccountingDetailList.add(valDealAccountingDetail);
            
            
            /* 2.3 账务明细代码表(VAL_BOK_DETAIL_ID) */
            // 账套代码 BOOKSET_ID 变量.账套信息.账套代码
            valBokDetailId.setBooksetId(bokBookset.getBooksetId());
            // 账务明细代码 BOK_DETAIL_ID 变量.账务明细代码
            valBokDetailId.setBokDetailId(bookNo);
            
            // 业务明细代码 BUSI_ID 输入.金融产品交易.金融产品代码
            valBokDetailId.setBusiId(busiId);
            // 业务明细类型 BUSI_DETAIL_TYPE 输入.金融产品交易.金融产品类型
            valBokDetailId.setBusiDetailType(busiDetailType);
            
            // 投资目的 INV_AIM 输入.金融产品交易.投资目的
            valBokDetailId.setInvAim(invAim);
            // 交易编号 TRADE_ID 输入.金融产品交易.交易编号
            valBokDetailId.setTradeId(trdProductDeal.getTradeId());
            //分支序号
            valBokDetailId.setBranch(breach==null? InterfCommonConstant.分支序号_默认:breach);

            /** modify by yanya on 20200113 金融产品实物交易对应账务明细代码表数据初始化优化 start**/
            //ADD JINMAODONG 债转股交易需要生成股票的账务明细代码 20191214
            //if(DictCodeListExt1.交易方向_债转股.getValue().equals(trdProductDeal.getPs())){
            //IF 输入.金融产品实物交易DTO is not null
            if(null != trdFinProductBarter) {
                //定义 变量.标的信息DTO = InterfCommonMapper.queryTargetFinProduct(输入.金融产品实物交易DTO.金融产品代码)
                TargetFinProduct targetFinProduct = mapper.queryTargetFinProduct(trdFinProductBarter.getFinprodId());
                //定义 变量.业务明细代码类型 = 变量.标的信息DTO IS NULL?NULL:变量.标的信息DTO.金融产品类型
                //变量.业务明细代码类型 IS NULL?输入.金融产品实物交易DTO.金融产品类型 : 变量.业务明细代码类型
                String targetBusiDetailType = targetFinProduct == null ? trdFinProductBarter.getFinprodType() : targetFinProduct.getFinprodType();
                //定义 变量.账务明细代码表Dto
                BokDetailId valBokDetailId2 = new BokDetailId();
                // 账套代码 BOOKSET_ID
                valBokDetailId2.setBooksetId(bokBookset.getBooksetId());
                //业务明细代码 = 变量.本方资金账户
                valBokDetailId2.setBusiId(trdFinProductBarter.getFinprodId());
                //业务明细类型 = 变量.本方资金类型
                //变量.业务明细代码类型 IS NULL?输入.金融产品实物交易DTO.金融产品类型 : 变量.业务明细代码类型
                valBokDetailId2.setBusiDetailType(targetBusiDetailType);
                //交易编号 = 变量.交易标的DTO.交易编号
                valBokDetailId2.setTradeId(trdProductDeal.getTradeId());
                //分支序号 = 0
                valBokDetailId2.setBranch(trdFinProductBarter.getBranch());
                //定义 变量.账务明细代码 = 调用共通设计->【详细设计-接口共通方法设计.xlsx】->生成MD5账务明细代码SHEET
                String bokDetailId2 = this.genMd5BokDetailId(valBokDetailId2);
                valBokDetailId2.setBokDetailId(bokDetailId2);
                bokDetailIdList.add(valBokDetailId2);
            }
            //}
            //ADD JINMAODONG 债转股交易需要生成股票的账务明细代码 20191214
            /** modify by yanya on 20200113 金融产品实物交易对应账务明细代码表数据初始化优化 end**/

            bokDetailIdList.add(valBokDetailId);
        }

        // 2.3 银行账交易入账
        // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->交易入账_银行账SHEET
        InterfProductDealBookCommonDto bankDto = this.dealAccountingBank(trdProductDeal, prodId, prodFinprodType, breach);
        if(bankDto != null){
        	// IF DTO IS NOT NULL
            if (CollectionUtils.isNotEmpty(bankDto.getBokDealAccountingList())) {
             // 变量.入账信息List.addAll(DTO.getBokDealAccountingList())
                bokDealAccountingList.addAll(bankDto.getBokDealAccountingList());
            }
            if (CollectionUtils.isNotEmpty(bankDto.getBokDealAccountingDetailList())) {
             // 变量.入账账务明细List.addAll(DTO.getBokDealAccountingDetailList())                                                                                  
                bokDealAccountingDetailList.addAll(bankDto.getBokDealAccountingDetailList());
            }
        }
        interfProductDealBookCommonDto.setBokDealAccountingList(bokDealAccountingList);
        interfProductDealBookCommonDto.setBokDealAccountingDetailList(bokDealAccountingDetailList);
        interfProductDealBookCommonDto.setBokDetailIdList(bokDetailIdList);
        interfProductDealBookCommonDto.setValBooksetInfoDtoList(bokSetList);

        return interfProductDealBookCommonDto;
    }
    
    
    /**
     * 获取交易入账的日期类型
     * 01:交易日  02：交割日
     * add by wujie on 20190525
     * @param deal
     * @return
     */
    public String getDealDateType(TrdProductDeal deal){
    	String dateType = "01";//默认为交易日
    	//金融产品类型：质押式逆回购(F03)
    	//交易方向：买入(T01)
    	//业务类型：债券质押式回购交易(04)
    	if(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue().equals(deal.getFinprodType())
    			&& DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
    			&& DictCodeList.业务类型_债券质押式回购交易.getValue().equals(deal.getBusiType())){
    		dateType = "02";
    	}
    	// add by jiangpeng on 20210528 start TISHKBS-1921 货币型产品收益需要在收益支付日记账
    	// 货币型产品 赎回收益
        if(DictCodeListExt1.金融产品类型_理财产品.getValue().equals(deal.getFinprodType())
                && DictCodeList.交易方向_赎回收益确认.getValue().equals(deal.getPs())
                && DictCodeList.业务类型_货币型TA交易.getValue().equals(deal.getBusiType())){
            dateType = "02";
        }
        // add by jiangpeng on 20210528 end TISHKBS-1921
    	return  dateType;
    }

    /**
     *
     * 并集查询：标的类金融产品表和交易类金融产品表
     * 通过金融产品代码查询标的信息
     * @param finprodId
     * @return TargetFinProduct
     * @see
     * @since 1.0
     */
    public TargetFinProduct queryTargetFinProduct(String finprodId) {
        return mapper.queryTargetFinProduct(finprodId);
    }

    public TargetFinProduct getTargetFinProductByCondition(String finprodId, String finprodType){
        return mapper.getTargetFinProductByCondition(finprodId,finprodType);
    }

    /**
     * 获取系统参数
     * @param paramCode
     * @return
     */
    public String getSysParamValue(String paramCode){
        return CacheUtil.getSysParam(paramCode);
    }
    
    /**
     * 查询附加税税则中的附加税类型
     * @param finprodId 金融产品代码
     * @param finprodType 金融产品类型
     * @param queryDate 查询日期
     * @return 缴纳附加税相关附加税类型
     * @see
     * @since 1.0
     */
    public List<String> getAddTaxType(List<String> listFinprodId, String finprodType, Date queryDate,Date queryDateBegin,Date queryDateEnd){
        Map<String, Object> params = new HashMap<>();
        params.put("listFinprodId", listFinprodId);
        params.put("finprodType", finprodType);
        params.put("finprodType_F16", DictCodeListExt1.金融产品类型_理财产品.getValue());
        params.put("queryDate", queryDate);
        //OPEX-650【产品缴纳】缴纳查询界面，选择纳税日期查询条件进行查询，报数据接口异常 guyunfei 20191227 start
        params.put("queryDateBegin", queryDateBegin);
        params.put("queryDateEnd", queryDateEnd);
        //OPEX-650【产品缴纳】缴纳查询界面，选择纳税日期查询条件进行查询，报数据接口异常 guyunfei 20191227 end
        params.put("schemeType", DictCodeListExt1.配置方案类型_税务方案.getValue());
        return mapper.getAddTaxType(params);
    }

    /**
     * 判断交易是否入账
     * @param finprodId
     * @param finprodType
     * @param busiType
     * @param ps
     * @param purchaseType
     * @return isAccountEntry
     */
    public boolean checkDealIsAccountEntry(String finprodId, String finprodType,String busiType,String ps,String purchaseType){
        // 定义 变量.是否入账 = true;
        boolean isAccountEntry = true;
        if((busiType.equals(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue())
                || busiType.equals(DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue()))
        && ps.equals(DictCodeList.交易方向_申购.getValue())
                || ps.equals(DictCodeList.交易方向_赎回到账.getValue())){
                    isAccountEntry = false;
        } else if (DictCodeList.业务类型_指令交易.getValue().equals(busiType)
                && (DictCodeListExt1.交易方向_保证金划款.getValue().equals(ps) || DictCodeListExt1.交易方向_保证金回款.getValue().equals(ps))){
                    isAccountEntry = false;
        }
        return isAccountEntry;
    }
    /**
     * 参数条件解析
     * @param parms
     * @param
     * @return isAccountEntry
     */
    public  Map<String,String> parameterConditionAnalysis(String parms){
        Map<String,String> resultMap = new HashMap<String,String>();
        if(parms == null || parms.isEmpty()){
            return  resultMap;
        }
        String[] parm = parms.split("\\|");
        if(parm == null || parm.length<1){
            return  resultMap;
        }
        for (String parmStr : parm){
            parmStr = parmStr.replaceAll("\\[", "").replaceAll("\\]", "");
           String[] parmList = parmStr.split(",");
           if(parmList == null || parmList.length != 2){
               continue;
           }
            resultMap.put(parmList[0],parmList[1]);
        }
        return resultMap;
    }
    /**
     * 获取结算金额值
     * @param dto
     * @param amt
     * @return isAccountEntry
     */
    public BigDecimal getSettlementAmount(ProductDealDto dto, String amt){
        // 定义 变量.结算金额字段值
        BigDecimal settleAmt = BigDecimal.ZERO;
        try{
	        Method method = dto.getClass().getMethod("get"+amt);
	        Object obj = method.invoke(dto);
	        settleAmt = obj == null ? BigDecimal.ZERO :(BigDecimal) obj ;
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
        return settleAmt;
    }
    
    /**
     * 四级科目号设置
     *
     * @param finprodId 金融产品代码
     * @param finprodType 金融产品类型
     * @param branch
     * @param busiType 业务类型
     * @return
     */
    public BokDetailId settingDetailSubjectNoTaiL(String finprodId, String finprodType, Long branch, String busiType, String booksetId) {

        // 定义 变量.标的信息Dto
        TargetFinProduct targetFinProduct = null;
        // 定义 变量.resultObject BokDetailId
        BokDetailId bokDetailId = new BokDetailId();
        // 定义 变量.处理后金融产品类型 = 输入.金融产品类型
        String lastFinprodType = finprodType;
        // 定义 变量.分支序号 = NULL
        Long branch1 = null;

        // 2.1 查询标的类信息
        // IF 数据字典:金融产品类型.理财子产品 eq 输入.金融产品类型
        if (DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(finprodType)) {
            // 变量.处理后金融产品类型 = 数据字典：金融产品类型.理财产品
            lastFinprodType = DictCodeListExt1.金融产品类型_理财产品.getValue();
            branch1 = branch;
        }
        /** add by lby on 20201225 start **/
        else {
            //ELSE 输入.金融产品类型 IN (数据字典：资金账户类型)
            if (systemToolService.getDataDictItem(DictCode.资金账户类型.getValue(), finprodType) != null){
                //变量.处理后金融产品类型 = 数据字典：金融产品类型.资金账户 F20
                lastFinprodType = DictCodeListExt1.金融产品类型_资金账户.getValue();
            }
        }
        /** add by lby on 20201225 end **/
        // 查询结果保存至 变量.标的信息Dto
        targetFinProduct = bokTradeBuildMapper.getTargetFinProductByCondition(finprodId, lastFinprodType);

        // 2.2 设置四级科目尾号
        // 定义 变量.四级科目尾号 = 输入.金融产品代码
        String detailSubjectNoTaiL = finprodId;
        // 定义 变量.四级科目尾号名称 = 变量.标的信息Dto.金融产品名称
        String detailSubjectNameTaiL = null;
        if(targetFinProduct != null){
            detailSubjectNameTaiL = targetFinProduct.getFinprodName();
        }
        // IF 数据字典:金融产品类型.理财子产品 eq 输入.金融产品类型
        if (DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(finprodType)) {
            // 变量.四级科目尾号 += 变量.分支序号
            detailSubjectNoTaiL += branch1;
            // 变量.四级科目尾号名称 += 变量.分支序号
            detailSubjectNameTaiL += branch1;
        }

        // IF 数据字典：金融产品类型.理财产品 eq 输入.金融产品类型
        // or 数据字典：金融产品类型.理财子产品 eq 输入.金融产品类型
        // or 数据字典：金融产品类型.理财投资组合 eq 输入.金融产品类型
        if (DictCodeListExt1.金融产品类型_理财产品.getValue().equals(finprodType) || DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(finprodType)
                || DictCodeListExt1.金融产品类型_投资组合.getValue().equals(finprodType)) {
            // 变量.四级科目尾号 += FinalString.LAYERING_SPLIT + 输入.金融产品类型
            detailSubjectNoTaiL += FinalString.LAYERING_SPLIT + finprodType;
        }
        
        // IF 数据字典：业务类型.拆借交易 eq 输入.业务类型
        if (targetFinProduct!=null && (DictCodeList.业务类型_拆借交易.getValue().equals(busiType))) {
        	boolean IS_FORSUBJECT = true;//期限品种作为四级科目号
        	if(DictCodeListExt1.金融产品类型_同业借款.getValue().equals(targetFinProduct.getFinprodType()) && booksetId != null){
        		BokBooksetExt bokBooksetExt = this.queryBokBooksetExtbyId(ValDictCodeList.账套配置表参数项_同业借款四级科目类型.getValue(),booksetId);
        		if(bokBooksetExt==null || "01".equals(bokBooksetExt.getParamValue())){
        			IS_FORSUBJECT = false;
        		}
        	}
        	if(IS_FORSUBJECT){
        		// 变量.四级科目尾号 = 变量.标的信息Dto.期限品种
                detailSubjectNoTaiL = targetFinProduct.getTermType();
                // 变量.四级科目尾号名称 =
                // InterfHelper.getDictColdeList_ItemName(DictCode.期限品种_内部拆借.getValue(),变量.标的信息Dto.期限品种)
                detailSubjectNameTaiL = InterfHelper.getDictColdeList_ItemName(DictCode.期限品种_内部拆借.getValue(), targetFinProduct.getTermType());
        	}
        }

        // IF 数据字典：业务类型.债券买断式回购交易 eq 输入.业务类型
        // OR 数据字典：业务类型.债券质押式回购交易 eq 输入.业务类型
        if (targetFinProduct!=null && (DictCodeList.业务类型_债券买断式回购交易.getValue().equals(busiType) || DictCodeList.业务类型_债券质押式回购交易.getValue().equals(busiType))) {
            // 变量.四级科目尾号 = 变量.标的信息Dto.期限品种
            detailSubjectNoTaiL = targetFinProduct.getTermType();
            // 变量.四级科目尾号名称 =
            // InterfHelper.getDictColdeList_ItemName(DictCode.期限品种_回购.getValue(),变量.标的信息Dto.期限品种)
            detailSubjectNameTaiL = InterfHelper.getDictColdeList_ItemName(DictCode.回购期限品种.getValue(), targetFinProduct.getTermType());
        }

        // 变量.resultObject.四级科目尾号 = 变量.四级科目尾号
        bokDetailId.setDetailSubjectNoTail(detailSubjectNoTaiL);
        // 变量.resultObject.四级科目尾号名称 = 变量.四级科目尾号名称
        //变量.resultObject.四级科目尾号名称 = NVL(变量.四级科目尾号名称,变量.四级科目尾号) start add lby on 20201225
        if (StringUtil.isNotEmpty(detailSubjectNameTaiL)){
            bokDetailId.setDetailSubnameTail(detailSubjectNameTaiL);
        }else {
            bokDetailId.setDetailSubnameTail(detailSubjectNoTaiL);
        }
        //变量.resultObject.四级科目尾号名称 = NVL(变量.四级科目尾号名称,变量.四级科目尾号) end add lby on 20201225
        return bokDetailId;
    }

    /**
     * 查询组合信息
     * @param portfolioId
     * @return PtlPortfolo
     * @throws Exception
     */
    public PtlPortfolio getPtlPortfolo(String portfolioId){
        // 定义 变量.组合信息Dto
        PtlPortfolio ptlPortfolio = ptlPortfolioMapper.selectByPrimaryKey(portfolioId);
        return ptlPortfolio;
    }

    /**
     * 帐套信息（子组合）查询
     * @param portfolioId
     * @return
     * @throws Exception
     */
    public List<ValBooksetInfoDto>  getSonPtlPortfolo(String portfolioId, List<PtlTrade> ptlTradeList){
        // 定义 变量.账套信息List
        List<ValBooksetInfoDto> valBooksetInfoDtoList = new ArrayList<ValBooksetInfoDto>();
        // 定义 变量.组合信息Dto
        PtlPortfolio ptlPortfolio = null;
        // 查询组合信息
        // 调用共通设计->【详细设计-投管接口共通方法设计.xlsx】->查询组合信息SHEET
        ptlPortfolio = getPtlPortfolo(portfolioId);
        if(ptlPortfolio == null || !StringUtil.equals(ptlPortfolio.getPortfolioType(), DictCodeListExt1.组合类型_子组合.getValue())){
            return valBooksetInfoDtoList;
        }

        //2.2 查询子组合对应投资组合或理财产品账套
        if(CollectionUtils.isEmpty(ptlTradeList)){
        	return valBooksetInfoDtoList;
        }
        
        for(PtlTrade ptlTrade :ptlTradeList){
        	if(portfolioId.equals(ptlTrade.getPortfolioId())){
        		continue;
        	}
        	String  hesuanId = null;
        	FinProduct finProduct = dealCommonService.queryFinProductByPortfolidId(ptlTrade.getPortfolioId());
        	if(finProduct != null && finProduct.getFinprodId() != null){
                hesuanId = finProduct.getFinprodId();
            }
        	else{
                hesuanId = ptlTrade.getPortfolioId();
        	}
        	// 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息(根据产品代码) _分层产品SHEET
            List<ValBooksetInfoDto> list = getBokBooksetByProdId_Layering(hesuanId);
            if(CollectionUtils.isNotEmpty(ptlTradeList)){
            	valBooksetInfoDtoList.addAll(list);
            }
        }
        
        Map<String, ValBooksetInfoDto> qcMap = new HashMap<String, ValBooksetInfoDto>();
        List<ValBooksetInfoDto> bokSetList = new ArrayList<ValBooksetInfoDto>();
        for(ValBooksetInfoDto dto:valBooksetInfoDtoList){
        	if(qcMap.get(dto.getBooksetId())==null){
        		bokSetList.add(dto);
        	}
        	qcMap.put(dto.getBooksetId(), dto);
        }
        valBooksetInfoDtoList.clear();
        valBooksetInfoDtoList.addAll(bokSetList);
        
        
        return valBooksetInfoDtoList;
    }
    
    /**
     * 单估值版本_接口接交易数据调用共通处理
     * @param tradeDeal
     * @param frontStlOrdersDto
     * @param finprodId
     * @param finprodType
     * @param cbranch
     * @return
     */
    public Map<String,Object>  instructionGenerate_Interf(TrdProductDeal tradeDeal, StlOrders frontStlOrdersDto, String finprodId,
                                                          String finprodType, Long cbranch){
    	Map<String,Object>  resultMap;
    	if(frontStlOrdersDto==null || frontStlOrdersDto.getMyAcctNo() == null){
    		resultMap = this.instructionGenerate(tradeDeal,finprodId, finprodType,null);
        }else{
        	resultMap = this.instructionGenerateByFront(tradeDeal,frontStlOrdersDto, 
        			finprodId, finprodType,null,null);
        }
    	return resultMap;
    }

    /**
     * 查询母子账套关系
     * @param channelId
     * @param booksetId
     * @return
     */
    public List<BokBooksetSonDto> queryBokBooksetRelationship(String channelId, String booksetId){
        Map<String, Object> map = new HashMap<>();
        map.put("channelId", channelId);//通道代码
        map.put("booksetId", booksetId);//账套代码
        return channelCommonService.queryBokBooksetRelationship(map);
    }

    /**
     * 行权回售申请交易更新
     * add by yanya on 20210309
     * @param list 行权申请交易
     */
    public void execApplyDealUpdate(List<SecExecApplyDto> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            Date updateTime = list.get(0).getUpdateTime();//更新时间;
            String updateUser = list.get(0).getUpdateUser();//更新人
            Map<Date, List<SecExecApplyDto>> groupBy = list.stream().collect(Collectors.groupingBy(SecExecApplyDto::getTradeDate));
            for (Map.Entry<Date, List<SecExecApplyDto>> entry : groupBy.entrySet()) {
                //待更新交易List
                List<String> tradeIdList = new ArrayList<>();
                for(SecExecApplyDto dto : entry.getValue()) {
                    //定义查询参数
                    Map<String, Object> 入账查询参数 = new HashMap<>();
                    //账套代码					行权回售申请ENITY.账套代码
                    入账查询参数.put("booksetId", dto.getBooksetId());
                    //金融产品代码					行权回售申请ENITY.金融产品代码
                    入账查询参数.put("finprodId", dto.getFinprodId());
                    //金融产品类型					行权回售申请ENITY.金融产品类型
                    入账查询参数.put("finprodType", dto.getFinprodType());
                    //投资目的					行权回售申请ENITY.投资目的
                    入账查询参数.put("invAim", dto.getInvAim());
                    //业务类型					行权回售申请ENITY.业务类型
                    入账查询参数.put("busiType", dto.getBusiType());
                    //交易方向					行权回售申请ENITY.交易方向
                    入账查询参数.put("ps", dto.getPs());
                    //分支序号					行权回售申请ENITY.分支序号
                    入账查询参数.put("branch", dto.getBranch());
                    //入账日				行权回售申请ENITY.入账日
                    入账查询参数.put("bookDate", dto.getBookDate());
                    //调用【交易信息查询共通方法设计-入账交易信息查询】
                    List<ValTrdProductDealInfoDto> tradeList = valTradeCommonService.findEntryTradeInfo(入账查询参数);
                    if (CollectionUtils.isNotEmpty(tradeList)) {
                        //loop 变量.交易信息List 交易信息Entity
                        tradeList.stream().forEach(item -> {
                            //IF 交易信息Entity.行权状态 IS NULL OR 数据字典：行权状态.不行权 ！= 交易信息Entity.行权状态
                            if(StringUtil.isEmpty(item.getExerciseStatus())
                                    || StringUtil.equals(item.getExerciseStatus(), DictCodeListExt3.行权状态_放弃行权.getValue())) {
                                //变量.待更新交易List.add（交易信息Entity.交易编号)
                                tradeIdList.add(item.getTradeId());
                            }
                        });
                    }
                }
                if (CollectionUtils.isNotEmpty(tradeIdList)) {
                    Date cancelExerciseDate = entry.getKey();
                    //更新数据 行权状态
                    mapper.updateExecApplyDealBatch(tradeIdList, DictCodeListExt3.行权状态_放弃行权.getValue(),
                            cancelExerciseDate, updateTime, updateUser);
                    //删除入账信息表
                    mapper.deleteBokDealAccountingBatch(tradeIdList);
                    //删除入账信息明细表
                    mapper.deleteBokDealAccountingDetailBatch(tradeIdList);

                }
            }
        }
    }

    /**
     * 查询证券品种
     * @return 证券品种
     */
    public String getFinanceProductSecType(FinProductClass finProductClass) {
        String valclas = mapper.getFinanceProductSecType(finProductClass);
        return valclas;
    }

}
