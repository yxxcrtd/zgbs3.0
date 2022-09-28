/*
 * 文件名：RskValTableImportService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年1月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.risk.valtblimport.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.ValtblImportDateTraceMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.RskValtblSubject;
import com.joyintech.tams.common.base.entity.ValtblImportDateTrace;
import com.joyintech.tams.component.business.common.dto.BaseFinProductBaseInfoDto;
import com.joyintech.tams.component.business.common.dto.InfChinamutualfunddescriptionDto;
import com.joyintech.tams.component.business.common.dto.InterfPayhsrcsecinfoDto;
import com.joyintech.tams.component.business.common.dto.InterfStockInfoDto;
import com.joyintech.tams.component.business.common.service.CommonAutoCouponService;
import com.joyintech.tams.component.business.riskrds.constant.PageFieldToInterfFactory;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskValtblPrtBotasset.dto.RskValtblPrtBotassetMapDto;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskValtblPrtBotasset.service.RskValtblPrtBotassetService;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskvalassetcode.dto.RskValtblAssetSubMapDtlDto;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskvalassetcode.service.RskValAssetCode001Service;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskvalassetcode.service.RskValAssetCode003Service;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskvalrelateimport.service.RskValRelateImport002Service;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskvalstructure.dto.RskValStructureDto;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskvalstructure.service.RskValStructureService;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskvalsumname.dto.RskValSumnameDto;
import com.joyintech.tams.invest.risk.penetrationmanagement.rskvalsumname.service.RskValSumname001Service;
import com.joyintech.tams.invest.risk.valtblimport.dto.*;
import com.joyintech.tams.invest.risk.valtblimport.filtertools.filter.Filter;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *  估值表导入
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年1月2日
 * @see RskValTableImportService
 * @since 1.0
 */
@Service
public class RskValTableImportService extends CommonExportService<RskValtblAssetSubDto>{
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(RskValTableImportService.class);

    /**
     * 指标结构映射
     */
    @Autowired
    private RskValStructureService rskValStructureService;
    
    /**
     * 资产科目映射
     */
    @Autowired
    private RskValAssetCode001Service rskValAssetCode001Service;
    
    /**
     * 汇总科目映射
     */
    @Autowired
    private RskValSumname001Service rskValSumname001Service;
    
    @Autowired
    private  RskValAssetCode003Service rskValAssetCode003Service;
    
    @Autowired
    private CommonAutoCouponService commonAutoCouponService;
    @Autowired
    private RskValTableImportCommonService rskValTableImportCommonService;
    /**
     * 底层资产映射
     */
    @Autowired
    private RskValtblPrtBotassetService rskValtblPrtBotassetService;
    @Autowired
    private ValtblImportDateTraceMapper valtblImportDateTraceMapper;
    @Autowired
    private RskValRelateImport002Service rskValRelateImport002Service;
    /**
     * 风险服务
     */
    @Autowired
    private InterfRiskRdsService interfRiskRdsService;
	
    /**
     * 债券
     */
    public static final String KEY_ZQ_STR = "1103";
    /**
     * 基金
     */
    public static final String KEY_JJ_STR = "1105";
    /**
     * 股票
     */
    public static final String KEY_GP_STR = "1102";
    /**
     * 非标
     */
    public static final String KEY_FB_STR = "1701";
    /**
     * 项目类
     */
    public static final String KEY_LLXFB_STR = "1901";
    /**
     * 存款类
     */
    public static final String KEY_CKL_STR = "1003";
    /**
     * 逆回购
     */
    public static final String KEY_NHGB_STR = "1202";
    /**
     * 逆回购利息
     */
    public static final String KEY_NHGI_STR = "120410";
    /**
     * 正回购
     */
    public static final String KEY_ZHGB_STR = "2202";
    /**
     * 正回购利息
     */
    public static final String KEY_ZHGI_STR = "2231";
    /**
     * 期权
     */
    public static final String KEY_QQ_STR = "3103";
    /**
     * 期货
     */
    public static final String KEY_QH_STR = "3102";



    /**
     * 利息
     */
    public static final String KEY_INT_STR = "1204";
    public static final String KEY_INT_GPJJ_STR = "1203";
    public List<String> insert(RskValtblAssetSubDto dto) throws ParseException {
    	
    	//存放加券过程中，中债要素check不通过的资产信息--徽商代码移植0602
    	List<String> errorInfoList=new ArrayList<String>();
    	
        RskValtblPrtBotassetMapDto rskValtblPrtBotassetMapDto = new RskValtblPrtBotassetMapDto();
        InterfPayhsrcsecinfoDto interfPayhsrcsecinfoDto = new InterfPayhsrcsecinfoDto();
        InfChinamutualfunddescriptionDto infChinamutualfunddescriptionDto = new InfChinamutualfunddescriptionDto();        
        //股票--徽商代码移植0602
        InterfStockInfoDto interfStockInfoDto=new InterfStockInfoDto();
        
        //是否自动加券
        boolean isAutoCoupon = false;
        List<String> isAutoCouponBondSecidList = new ArrayList<String>();
        List<String> isAutoCouponFundfInfoWindcodeList = new ArrayList<String>();        
        //要加券的股票--徽商代码移植0602
        List<String> isAutoCouponStockfInfoWindcodeList = new ArrayList<String>();
                
        //查找底层资产映射表
        List<RskValtblPrtBotassetMapDto> botAssetMapList = rskValtblPrtBotassetService.selectBotAssetMapByPtlId(dto.getPtlId());
        //查找标的金融产品信息
        List<FinProduct> finProductList = selectAllAsset();
        List<String> subNameList = new ArrayList<String>();
        Map<String,Object> paramMap = new HashMap<String,Object>();
        dto.getSelectedItem().forEach(item->{
            if (StringUtil.isEmpty(item.getSubName())) {
                item.setSubName(StringUtil.EMPTY);
            }
            subNameList.add(item.getSubName());
        });
        paramMap.put("windNameList",subNameList);
        //查找wind债券信息
        List<InterfPayhsrcsecinfoDto>  interfPayhsrcsecinfoList  = commonAutoCouponService.selectWindBondAsset(paramMap);
        //查找wind基金信息
        List<InfChinamutualfunddescriptionDto> infChinamutualfunddescriptionList = commonAutoCouponService.selectWindFundAsset(paramMap);
        //查找wind股票信息--徽商代码移植0602
        List<InterfStockInfoDto> interfStockInfoList=commonAutoCouponService.selectWindStockAsset(paramMap);
        

        Map<String,String> paraMap =new HashMap<String,String>();
        for(RskValtblAssetSubDto rskValtblAssetSubDtoTemp: dto.getSelectedItem()){
            if(rskValtblAssetSubDtoTemp.getSubCode().length() ==8){
                if(paraMap.get(rskValtblAssetSubDtoTemp.getSubCode()) ==null){
                    paraMap.put(rskValtblAssetSubDtoTemp.getSubCode(),rskValtblAssetSubDtoTemp.getSubName());
                }else{
                    continue;
                }
            }
        }

        for(RskValtblAssetSubDto entity:dto.getSelectedItem()){
            String actMarketCode = null;
            String subCode=entity.getSubCode();
            String subName=entity.getSubName();
            if(StringUtil.equals(entity.getValType().getDesc(), ValType.ASSET.getDesc())
                   && StringUtil.isNotEmpty(entity.getIsMatch()) && StringUtil.equals(entity.getIsMatch(),DictCodeListExt1.是否_否.getValue())){
               continue;
            }
            boolean flag = true;
            //新增底层资产
            boolean isAddSubBotMap = true;
            //更新底层资产
            boolean isUpdateSubBotMap = false;
            //第一步 匹配底层资产映射表
            if(null!=botAssetMapList&&!botAssetMapList.isEmpty()){
                for(RskValtblPrtBotassetMapDto entityRskValtblPrtBotassetMap:botAssetMapList){
                    if(StringUtil.equals(subCode, entityRskValtblPrtBotassetMap.getSubCode())){
                        isAddSubBotMap= false;
                        if(StringUtil.isNotEmpty(entityRskValtblPrtBotassetMap.getFinprodId())){
                            flag = false;
                            entity.setFinprodId(entityRskValtblPrtBotassetMap.getFinprodId());
                            break;
                        }else{
                            isUpdateSubBotMap = true;
                        }
                    }
                }
            }
            //底层资产映射没有匹配上  该资产写入映射表
            if(isAddSubBotMap){
                entity.setWritebotAssetMap(true);
            }
            //底层资产映射匹配上  写入映射表
            if(isUpdateSubBotMap){
                entity.setUpdatebotAssetMap(true);
            }
            // add by QuJianZhong 区分债券交易场所 20200501 start　
            if (subCode.contains("SH") ||
                    subCode.contains("SZ") ||
                    subCode.contains("CY")
                    || subCode.contains("OF")//场外基金
                    || subCode.contains("OTC")//大部分行场外基金后缀都是OTC

            ) {
                if(subCode.contains("OTC")){
                    actMarketCode = subCode.substring(subCode.length() - 3);
                }else{
                    actMarketCode = subCode.substring(subCode.length() - 2);
                }
                actMarketCode = actMarketCode.replace("CY", "IB");
                actMarketCode = actMarketCode.replace("OTC", "OF");
            } else  if(subName.contains("上交所")||
                    subName.contains("深交所")||
                    subName.contains("银行间")){
                if (subName.contains("上交所")) {
                    actMarketCode = "SH";
                }
                if (subName.contains("深交所")) {
                    actMarketCode = "SZ";
                }
                if (subName.contains("银行间")) {
                    actMarketCode = "IB";
                }
            }else {
                //根据估值表底层资产映射获取债券的交易场所，适用于四级科目无法区分
                if(subCode.length() >8){
                    String subNamesub = paraMap.get(subCode.substring(0,8));
                    if (subNamesub.contains("上交所")) {
                        actMarketCode = "SH";
                    }
                    if (subNamesub.contains("深交所")) {
                        actMarketCode = "SZ";
                    }
                    if (subNamesub.contains("银行间")) {
                        actMarketCode = "IB";
                    }
                }
            }

            if (StringUtil.isNotEmpty(actMarketCode)) {
                entity.setMarketId(actMarketCode);
            }
            // add by QuJianZhong 区分债券交易场所 20200501 end
            //第二部  匹配标的金融产品信息
            if(flag&&null!=finProductList&&!finProductList.isEmpty()) {
                for (FinProduct entityFinProduct : finProductList) {
                    //匹配标的金融产品信息的交易场所add by lilan  20200528
                    String tradeMarket=null;
                    if(entityFinProduct.getFinprodId().length()>2){
                        tradeMarket = entityFinProduct.getFinprodId().substring(entityFinProduct.getFinprodId().length() - 2);
                    }

                    if ((StringUtil.equals(subName, entityFinProduct.getFinprodName())
                            || StringUtil.equals(subName, entityFinProduct.getFinprodAbbr()))
                            && (StringUtil.equals(actMarketCode, tradeMarket) ||
                            // add by jiangpeng on 20201221 非债券资产 //add by hxy on 20210803 债券可能是QD债券，去掉非债券资产的判断
                            (StringUtil.isEmpty(actMarketCode) && StringUtil.isNotEmpty(entityFinProduct.getFinprodType2())))
                            && subCode.length() > 8) {/*非四级科目对应行，都不能有资产代码*/
                        flag = false;
                        entity.setAssetSource(DictCodeListExt1.估值资产来源_系统内.getValue());
                        entity.setFinprodId(entityFinProduct.getFinprodId());
                        break;
                    }
                }
            }
            //第三步  匹配wind资产信息
            if(flag){
                if(StringUtil.equals(entity.getValType().getDesc(), ValType.ASSET.getDesc())){
                    //添加利息匹配1204
                    if(StringUtil.equals(subCode.substring(0, 4),KEY_ZQ_STR)
                       ||StringUtil.equals(subCode.substring(0, 4),KEY_INT_STR)){
                        if(null!=interfPayhsrcsecinfoList&&!interfPayhsrcsecinfoList.isEmpty()){
                            for(InterfPayhsrcsecinfoDto entityInterfPayhsrcsecinfo:interfPayhsrcsecinfoList){
                                if(StringUtil.equals(subName, entityInterfPayhsrcsecinfo.getSecname())
                                    ||StringUtil.equals(subName, entityInterfPayhsrcsecinfo.getSecfullname())){
                                	
                                	// add by QuJianZhong 区分债券交易场所 20200501 start
                                	String currentMarketCode = entityInterfPayhsrcsecinfo.getSecid().substring(entityInterfPayhsrcsecinfo.getSecid().indexOf(".") + 1);
                                	if (!StringUtil.equals(actMarketCode, currentMarketCode)) {
                                		// 当前接口表债券市场代码与估值表不同，则继续寻找
                                		continue;
                                	}
                                	// add by QuJianZhong 区分债券交易场所 20200501 end
                                	
                                    flag = false;
                                    //避免重复加券
                                    if(StringUtil.equals(subCode.substring(0, 4),KEY_ZQ_STR)){
                                    	isAutoCoupon = true;
                                    	isAutoCouponBondSecidList.add(entityInterfPayhsrcsecinfo.getSecid());
                                    }
                                    entity.setAssetSource(DictCodeListExt1.估值资产来源_WIND债券.getValue());
                                    entity.setFinprodId(entityInterfPayhsrcsecinfo.getFinprodId());
                                    entity.setFinprodMarketId(entityInterfPayhsrcsecinfo.getSecid());
                                    break;
                                }
                            }
                        }
                    }else if(StringUtil.equals(subCode.substring(0, 4),KEY_JJ_STR)
                            ||StringUtil.equals(subCode.substring(0, 4),KEY_INT_GPJJ_STR)){
                        if(null!=infChinamutualfunddescriptionList&&!infChinamutualfunddescriptionList.isEmpty()){
                            for(InfChinamutualfunddescriptionDto entityinfChinamutualfunddescription:infChinamutualfunddescriptionList){
                                if(StringUtil.equals(subName, entityinfChinamutualfunddescription.getfInfoName())
                                   ||StringUtil.equals(subName, entityinfChinamutualfunddescription.getfInfoFullname())){
                                    flag = false;
                                    //避免重复加券
                                    if(StringUtil.equals(subCode.substring(0, 4),KEY_JJ_STR)){
                                        isAutoCoupon = true;
                                        isAutoCouponFundfInfoWindcodeList.add(entityinfChinamutualfunddescription.getfInfoWindcode());
                                    }
                                    entity.setAssetSource(DictCodeListExt1.估值资产来源_WIND基金.getValue());
                                    //徽商代码移植-股票加券、股票债券基金加券数据的有效性check处理合并  201210602  start
                                    entity.setFinprodId(entityinfChinamutualfunddescription.getFinprodId());
                                    //徽商代码移植-股票加券、股票债券基金加券数据的有效性check处理合并  201210602  end
                                    entity.setFinprodMarketId(entityinfChinamutualfunddescription.getfInfoWindcode());
                                    break;
                                }
                            }
                        }
                      //徽商代码移植-股票加券、股票债券基金加券数据的有效性check处理合并  201210602  start
                    }else if(StringUtil.equals(subCode.substring(0, 4),KEY_GP_STR)
                            ||StringUtil.equals(subCode.substring(0, 4),KEY_INT_GPJJ_STR)){
                    	if(null!=interfStockInfoList&&!interfStockInfoList.isEmpty()){
                            for(InterfStockInfoDto entityInterfStockInfoDto:interfStockInfoList){
                                if(StringUtil.equals(subName, entityInterfStockInfoDto.getAssetname())){
                                	flag = false;
                                    //避免重复加券
                                    if(StringUtil.equals(subCode.substring(0, 4),KEY_GP_STR)){
                                        isAutoCoupon = true;
                                        isAutoCouponStockfInfoWindcodeList.add(entityInterfStockInfoDto.getAssetcode());
                                    }
                                    entity.setAssetSource(DictCodeListExt1.估值资产来源_WIND股票.getValue());
                                    entity.setFinprodId(entityInterfStockInfoDto.getFinprodId());
                                    entity.setFinprodMarketId(entityInterfStockInfoDto.getAssetcode());
                                    break;
                                }
                            }
                        }
                    }
                   //徽商代码移植-股票加券、股票债券基金加券数据的有效性check处理合并  201210602  end
                }
            }
            
        }
        botAssetMapList.clear();
        
        isAutoCouponBondSecidList = isAutoCouponBondSecidList.stream().distinct().collect(Collectors.toList());
        isAutoCouponFundfInfoWindcodeList = isAutoCouponFundfInfoWindcodeList.stream().distinct().collect(Collectors.toList());
        isAutoCouponStockfInfoWindcodeList = isAutoCouponStockfInfoWindcodeList.stream().distinct().collect(Collectors.toList());
        
        interfPayhsrcsecinfoDto.setSecidList(isAutoCouponBondSecidList);
        infChinamutualfunddescriptionDto.setfInfoWindcodeList(isAutoCouponFundfInfoWindcodeList);
        interfStockInfoDto.setStockCodeList(isAutoCouponStockfInfoWindcodeList);
        
        //自动加券
        if(isAutoCoupon){
        	 //调用债券自动加券方法
            interfPayhsrcsecinfoDto = commonAutoCouponService.autoBondCoupon(interfPayhsrcsecinfoDto);
            if(null != interfPayhsrcsecinfoDto  && null != interfPayhsrcsecinfoDto.getErrorBondList()) {
            	errorInfoList.addAll(interfPayhsrcsecinfoDto.getErrorBondList());
            }
            
            //调用基金自动加券方法
            infChinamutualfunddescriptionDto = commonAutoCouponService.autoFundCoupon(infChinamutualfunddescriptionDto);
            if(null != infChinamutualfunddescriptionDto  && null != infChinamutualfunddescriptionDto.getErrorFundList()) {
            	errorInfoList.addAll(infChinamutualfunddescriptionDto.getErrorFundList());
            }
            
            //调用股票自动加券方法
            interfStockInfoDto=commonAutoCouponService.autoStockCoupon(interfStockInfoDto);
            if(null != interfStockInfoDto  && null != interfStockInfoDto.getErrorStockList()) {
            	errorInfoList.addAll(interfStockInfoDto.getErrorStockList());
            }
        }
        for(RskValtblAssetSubDto entity2:dto.getSelectedItem()){
            if(StringUtil.equals(entity2.getValType().getDesc(), ValType.ASSET.getDesc())
                    && StringUtil.isNotEmpty(entity2.getIsMatch()) && StringUtil.equals(entity2.getIsMatch(),DictCodeListExt1.是否_否.getValue())){
                continue;
            }
            //导入方式
            entity2.setImpMode(dto.getImpMode());
            if(entity2.isWritebotAssetMap()||entity2.isUpdatebotAssetMap()){
                if(StringUtil.equals(entity2.getValType().getDesc(), ValType.ASSET.getDesc())){
                    RskValtblPrtBotassetMapDto rskValtblPrtBotassetMapDto2 = new RskValtblPrtBotassetMapDto();
                    if(StringUtil.isNotEmpty(entity2.getSubCode())&&entity2.getSubCode().length()>8){
                        if(StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_ZQ_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_债券.getValue());
                        }else if(StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_JJ_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_基金.getValue());
                        }else if(StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_GP_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_股票.getValue());
                        }else if(StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_FB_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_非标.getValue());
                        }else if(StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_LLXFB_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_项目类.getValue());
                        }else if(StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_CKL_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_存款类.getValue());
                        }else if (StringUtil.equals(entity2.getSubCode().substring(0, 4), KEY_NHGB_STR)) {
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_逆回购.getValue());
                            String code=StringUtil.substring(entity2.getSubCode(),0,8);
                            String finprodType= null;
                            if(StringUtil.equals(code,"12020101") || StringUtil.equals(code,"12020201") || StringUtil.equals(code,"12020301")){
                                finprodType=DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue();
                            }else if(StringUtil.equals(code,"12020102") || StringUtil.equals(code,"12020202") || StringUtil.equals(code,"12020302")){
                                finprodType=DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue();
                            }
                            String termType = rskValTableImportCommonService.getTermTypeFromStr(entity2.getSubCode(), entity2.getSubName());
                            if(termType != null) {
                                entity2.setFinprodId(finprodType.concat(GlobalConstant.UNDER_LINE).concat(termType));
                            } else {
                                entity2.setFinprodId(finprodType.concat(GlobalConstant.UNDER_LINE).concat(entity2.getSubCode().substring(8, entity2.getSubCode().length())));
                            }
                            //在fin_trade_product、fin_product_type生成相关数据，风险需要用到
                            createRepoInfo(entity2);
                        } else if (StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_ZHGB_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_正回购.getValue());
                            String code=entity2.getSubCode().substring(0, 8);
                            String finprodType= null;
                            if(StringUtil.equals(code,"22020101") || StringUtil.equals(code,"22020201") || StringUtil.equals(code,"22020301")){
                                finprodType=DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue();
                            }else if(StringUtil.equals(code,"22020102") || StringUtil.equals(code,"22020202") || StringUtil.equals(code,"22020302")){
                                finprodType=DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue();
                            }
                            String termType = rskValTableImportCommonService.getTermTypeFromStr(entity2.getSubCode(), entity2.getSubName());
                            if(termType != null) {
                                entity2.setFinprodId(finprodType.concat(GlobalConstant.UNDER_LINE).concat(termType));
                            } else {
                                entity2.setFinprodId(finprodType.concat(GlobalConstant.UNDER_LINE).concat(entity2.getSubCode().substring(8, entity2.getSubCode().length())));
                            }
                            //在fin_trade_product、fin_product_type生成相关数据，风险需要用到
                            createRepoInfo(entity2);
                        } else if (StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_ZHGI_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_正回购.getValue());
                        } else if (StringUtil.equals(entity2.getSubCode().substring(0, 6),KEY_NHGI_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_逆回购.getValue());
                        } else if (StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_QQ_STR) || StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_QH_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_衍生品.getValue());
                        }else{
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_其他.getValue());
                        }
                        if(StringUtil.equals(entity2.getAssetSource(), DictCodeListExt1.估值资产来源_WIND债券.getValue())){
                            if(null!=interfPayhsrcsecinfoDto.getBaseFinProductBaseInfoList()
                            &&!interfPayhsrcsecinfoDto.getBaseFinProductBaseInfoList().isEmpty()){
                                for(BaseFinProductBaseInfoDto baseFinProductBaseInfoDto:interfPayhsrcsecinfoDto.getBaseFinProductBaseInfoList()){
                                    if(StringUtil.equals(entity2.getFinprodMarketId(), baseFinProductBaseInfoDto.getFinProduct().getFinprodMarketId())){
                                        rskValtblPrtBotassetMapDto2.setFinprodId(baseFinProductBaseInfoDto.getFinProduct().getFinprodId());
                                        entity2.setFinprodId(baseFinProductBaseInfoDto.getFinProduct().getFinprodId());
                                        entity2.setAssetSource(DictCodeListExt1.估值资产来源_系统内.getValue());
                                    }
                                }
                            }
                        }
                        if(StringUtil.equals(entity2.getAssetSource(), DictCodeListExt1.估值资产来源_WIND基金.getValue())){
                            if(null!=infChinamutualfunddescriptionDto.getBaseFinProductBaseInfoList()
                            &&!infChinamutualfunddescriptionDto.getBaseFinProductBaseInfoList().isEmpty()){
                                for(BaseFinProductBaseInfoDto baseFinProductBaseInfoDto:infChinamutualfunddescriptionDto.getBaseFinProductBaseInfoList()){
                                    if(StringUtil.equals(entity2.getFinprodMarketId(), baseFinProductBaseInfoDto.getFinProduct().getFinprodMarketId())){
                                        rskValtblPrtBotassetMapDto2.setFinprodId(baseFinProductBaseInfoDto.getFinProduct().getFinprodId());
                                        entity2.setFinprodId(baseFinProductBaseInfoDto.getFinProduct().getFinprodId());
                                        entity2.setAssetSource(DictCodeListExt1.估值资产来源_系统内.getValue());
                                    }
                                }
                            }
                        }
                        if(StringUtil.equals(entity2.getAssetSource(), DictCodeListExt1.估值资产来源_WIND股票.getValue())){                     	
                        	if(null!=interfStockInfoDto.getBaseFinProductBaseInfoList()
                               &&!interfStockInfoDto.getBaseFinProductBaseInfoList().isEmpty()){
                        		for(BaseFinProductBaseInfoDto baseFinProductBaseInfoDto:interfStockInfoDto.getBaseFinProductBaseInfoList()){
                                    if(StringUtil.equals(entity2.getFinprodMarketId(), baseFinProductBaseInfoDto.getFinProduct().getFinprodMarketId())){
		                        		rskValtblPrtBotassetMapDto2.setFinprodId(baseFinProductBaseInfoDto.getFinProduct().getFinprodId());
		                                entity2.setFinprodId(baseFinProductBaseInfoDto.getFinProduct().getFinprodId());
		                                entity2.setAssetSource(DictCodeListExt1.估值资产来源_系统内.getValue());
                                    }
                        		}
                        	}
                        }
                        rskValtblPrtBotassetMapDto2.setSubCode(entity2.getSubCode());
                        rskValtblPrtBotassetMapDto2.setSubName(entity2.getSubName());
                        rskValtblPrtBotassetMapDto2.setPtlId(dto.getPtlId());
                        rskValtblPrtBotassetMapDto2.setFinprodId(entity2.getFinprodId());
                        rskValtblPrtBotassetMapDto2.setAssetSource(entity2.getAssetSource());
                        rskValtblPrtBotassetMapDto2.setTblMarketId(entity2.getMarketId());
                        if(entity2.isWritebotAssetMap()){
                            rskValtblPrtBotassetMapDto2.setIsAdd(DictCodeList.是否_是.getValue());
                        }
                        botAssetMapList.add(rskValtblPrtBotassetMapDto2);
                    }
                }
            }
        }
        rskValtblPrtBotassetMapDto.setSelectedItem(botAssetMapList);
        //write映射表
        writebotAssetMap(rskValtblPrtBotassetMapDto);
        //保存估值表

        saveValTbl(dto);
		
        //上农商个性化_估值表导入预占实占变更  add by zhangpei 20201224 start
        preOccupationChange(dto);
        //上农商个性化_估值表导入预占实占变更  add by zhangpei 20201224 end
		
        //处理成功的文件入库估值表导入日期记录表
        if(null!=dto.getSelectedItem()&&!dto.getSelectedItem().isEmpty()){
            ValtblImportDateTrace dateDto = new ValtblImportDateTrace();
            dateDto.setUuid(IDGenerator.generate());
            dateDto.setImpdate(DateUtil.parse(DateUtil.getSysDate()));
            dateDto.setPortfolioId(dto.getPtlId());
            dateDto.setValdate(dto.getValDate());
            dateDto.setCreateDept((LoginUserUtil.getInstCode()!= null && LoginUserUtil.getInstCode().size()>0) ? LoginUserUtil.getInstCode().get(0):null);
            valtblImportDateTraceMapper.insert(dateDto);
        }
        return errorInfoList;
    }
    
    /**
     * 写入底层映射表
     * <功能详细描述>
     * @param dto
     * @see
     * @since 1.0
     */
    private void writebotAssetMap(RskValtblPrtBotassetMapDto dto){
        restTemplate.postForObject(
            ServiceApplication.RISK_SERVICE_NAME +"/RskValtblPrtBotasset/writebotAssetMap",
            dto,Integer.class);
    }
    /**
     * 保存估值表信息
     * <功能详细描述>
     * @param dto
     * @see
     * @since 1.0
     */
    private void saveValTbl(RskValtblAssetSubDto dto){
        restTemplate.postForObject(
            ServiceApplication.RISK_SERVICE_NAME +"/RskValTableImport/saveValTbl",
            dto,Integer.class);
    }
    /**
     * 获取所有资产信息
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public List<FinProduct> selectAllAsset(){
         List<FinProduct> list=restTemplate.getForList(
            ServiceApplication.ASSET_SERVICE_NAME + "/AssetCommon/selectAllAsset",
            FinProduct.class);
         //资产维护界面维护的资产
        List<FinProduct> list1=restTemplate.getForList(
                ServiceApplication.RISK_SERVICE_NAME + "/RskValRelateImport/selectAllAsset",
                FinProduct.class);
        list.addAll(list1);
        return list;
    }
    /**
     * 估值表预览
     * <功能详细描述>
     * @param dto
     * @return
     * @throws Exception 
     * @see
     * @since 1.0
     */
   public List<RskValtblAssetSubDto> viewValTable(RskValTableImportParamDto dto) throws ClassNotFoundException, FileNotFoundException, InstantiationException, IllegalAccessException {
       Map<String, Object> paramMap = new HashMap<String, Object>();
       List<RskValtblAssetSubDto> valList = new LinkedList<RskValtblAssetSubDto>();
       List<RskValtblAssetSubDto> valHand_oneList = new LinkedList<RskValtblAssetSubDto>();
       //指标结构映射
       List<RskValStructureDto> rskValStructureList = rskValStructureService.selectForList(dto.getStructMapId());
       //资产科目映射
       List<RskValtblAssetSubMapDtlDto> rskValAssetCodeList= rskValAssetCode001Service.selectDtlList(dto.getAssetSubMapId());
       //汇总科目映射
       List<RskValSumnameDto> rskValSumnameList =  rskValSumname001Service.selectForList(dto.getSumsubMapId());
       //目标资产科目
       List<RskValtblSubject> rskValtblSubjectList= rskValAssetCode003Service.selecctRskValtblSubjectCodeList("01");
       //目标汇总科目
       List<RskValtblSubject> rskValtblSubjectList2= rskValAssetCode003Service.selecctRskValtblSubjectCodeList("02");
       Map<String,ColumnMapEntry> vtstructureMap = new HashMap<String, ColumnMapEntry>();
       Map<String,SubCodeEntry> vtaccountMap = new HashMap<String, SubCodeEntry>();
       Map<String,SumNameCodeEntry> vtsumaccountMap = new HashMap<String, SumNameCodeEntry>();
       //构造目标科目代码汇总科目Map
       Map<String,SumNameCodeEntry> vtsumCodeMap = new HashMap<String, SumNameCodeEntry>();
       //构造结构映射map
       for(RskValStructureDto rskValStructureDto:rskValStructureList){
           ColumnMapEntry vtstructEntry = new ColumnMapEntry();
           vtstructEntry.setColIndex(Integer.parseInt(rskValStructureDto.getSrcColIndex()));
           vtstructEntry.setDataType(rskValStructureDto.getSrcColDataType());
           vtstructEntry.setValIndex(rskValStructureDto.getTgtIndex());
           vtstructureMap.put(rskValStructureDto.getSrcColIndex(), vtstructEntry);
       }
       //构造资产科目映射map
       for(RskValtblAssetSubMapDtlDto rskValtblAssetSubMapDtlDto:rskValAssetCodeList){
           SubCodeEntry subentry = new SubCodeEntry();
           subentry.setSrc_code(rskValtblAssetSubMapDtlDto.getSrcCode());
           subentry.setTgt_code(rskValtblAssetSubMapDtlDto.getTgtCode());
           subentry.setTgt_name(rskValtblAssetSubMapDtlDto.getTgtName());
           subentry.setAllowSum(rskValtblAssetSubMapDtlDto.getAllowSum());
           vtaccountMap.put(rskValtblAssetSubMapDtlDto.getSrcCode(), subentry);
       }
       //构造汇总资产科目映射map
       for(RskValSumnameDto vtsumaccountDto:rskValSumnameList){
           SumNameCodeEntry entry = new SumNameCodeEntry();
           entry.setSumName(vtsumaccountDto.getSumName().trim());
           entry.setSumCode(vtsumaccountDto.getSumCode().trim());
           entry.setNextColType(vtsumaccountDto.getNextIndexType());
           for(RskValtblSubject rskValtblSubject:rskValtblSubjectList2){
               if(StringUtil.equals(rskValtblSubject.getSubCode(), vtsumaccountDto.getSumCode())){
                   entry.setTgt_sumName(rskValtblSubject.getSubName());
               }
           }
           vtsumaccountMap.put(vtsumaccountDto.getSumName(), entry);
           vtsumCodeMap.put(entry.getSumCode(),entry);
       }
       paramMap.put("start_row", dto.getStartRow());
       paramMap.put("subjectCodeCol", dto.getSubjectCodeCol());
       paramMap.put("trimChars",dto.getTrimChars());
       paramMap.put("vtstructureMap", vtstructureMap);
       paramMap.put("vtaccountMap", vtaccountMap);
       paramMap.put("vtsumaccountMap", vtsumaccountMap);
       // 处理没有币种列的情况，全部设置为CNY
       boolean isExistCcyCol = vtstructureMap.values().stream().noneMatch((e) -> e.getValIndex().equals(ValIndex.CCY));
       paramMap.put("isExistCcyCol", isExistCcyCol);
       //处理excel数据 
       if (StringUtil.isNotEmpty(dto.getValTableFileName())) {
           /*List<List<String>> dataList = ExcelUtil.build(FileNameUtil.getUploadFilePath(dto.getValTableFileName()),
               null).readToList(0,Integer.parseInt(dto.getStartRow())-1, Integer.parseInt(dto.getStartRow())-2);*/
           //框架遇到空行 不读取  估值表比较特殊 资产与汇总科目中间有空格 需要重写
           List<List<String>> dataList = readExcelUtil(dto);
           if (StringUtil.isEmptyList(dataList)) {
               return null;
           }
           for(List<String> list : dataList){
               //处理excel------估值表
               RskValtblAssetSubDto val = handExcelListForVal(list,paramMap);
               if(val != null) {
                   val.setValDate(dto.getValDate());
                   val.setPtlId(dto.getPtlId());
                   valList.add(val);
               }
           }
           //处理valList---取出映射表配置的科目代码及底层资产
           List<RskValtblAssetSubDto> handval_List = handValList(valList, vtaccountMap);
           //处理handval_List---为null的数值赋0
           dealHandValList(handval_List);
           //底层---一级汇总
           valHand_oneList = sum_handValList(handval_List, vtaccountMap,rskValtblSubjectList, vtsumCodeMap);
           // 一二三级科目的币种,汇率设为空
           for (RskValtblAssetSubDto handledDto : valHand_oneList) {
               if (handledDto.getSubCode().length() <= 8) {
                   handledDto.setCcy(null);
                   handledDto.setExchangeRate(null);
               }
           }
           //多条科目需汇总（不同投资目的可生成多条信息）
           Map<String,Integer> indexMap = new HashMap<>();
           int i=0;
           while(i<valHand_oneList.size()) {
        	   RskValtblAssetSubDto processDto = valHand_oneList.get(i);
        	   if(indexMap.containsKey(processDto.getSubCode())) {
        		   RskValtblAssetSubDto sumDto = valHand_oneList.get(indexMap.get(processDto.getSubCode()));
        		   //1.汇总计算
        		   summary(sumDto,processDto);
        		   //2.移除
        		   valHand_oneList.remove(i);
        	   }
        	   else {
        		   indexMap.put(processDto.getSubCode(), i++);
        	   }
           }
           //针对已匹配资产科目，若系统中不存在，预览时不展示
           Iterator<RskValtblAssetSubDto> itList = valHand_oneList.iterator();
           List<String> subList= rskValAssetCode003Service.selecctRskValtblSubCodeList("01");
           while (itList.hasNext()){
               RskValtblAssetSubDto dtoit = itList.next();
               if(StringUtil.equals(dtoit.getValType().getDesc(), ValType.ASSET.getDesc())
                       && StringUtil.equals(dtoit.getIsMatch(), DictCodeListExt1.是否_是.getValue())
                       && StringUtil.isNotEmpty(dtoit.getSubCode()) ){
                   String subCode=dtoit.getSubCode();
                   if(dtoit.getSubCode().length()>8){
                       subCode=dtoit.getSubCode().substring(0, 8);
                   }
                   if(!subList.contains(subCode)){
                       itList.remove();
                   }
               }
           }
       }

       valHand_oneList.sort(Comparator.comparing(RskValtblAssetSubDto::getIsMatch,Comparator.nullsLast(String::compareTo)).thenComparing(RskValtblAssetSubDto::getValType).thenComparing(RskValtblAssetSubDto::getSubCode));

       return valHand_oneList;
   }
    /**
     * 为数值型字段赋初始值
     *
     * @param handval_list
     * @return
     */
    private void dealHandValList(List<RskValtblAssetSubDto> handval_list) {
        BigDecimal security_cost = BigDecimal.ZERO;
        BigDecimal ctnwr = BigDecimal.ZERO;
        BigDecimal smv = BigDecimal.ZERO;
        BigDecimal mcr = BigDecimal.ZERO;
        BigDecimal securityCostOri = BigDecimal.ZERO;
        BigDecimal smvOri = BigDecimal.ZERO;
        BigDecimal valueAddedOri = BigDecimal.ZERO;
        BigDecimal valueAdded = BigDecimal.ZERO;
        for (RskValtblAssetSubDto dto : handval_list) {
            if (ValType.SUM.equals(dto.getValType())) {
                continue;
            }
            if (Objects.isNull(dto.getSecurityCost())) {
                dto.setSecurityCost(BigDecimal.ZERO);
            }
            if (Objects.isNull(dto.getCtnwr())) {
                dto.setCtnwr(BigDecimal.ZERO);
            }
            if (Objects.isNull(dto.getSmv())) {
                dto.setSmv(BigDecimal.ZERO);
            }
            if (Objects.isNull(dto.getMcr())) {
                dto.setMcr(BigDecimal.ZERO);
            }
            if (Objects.isNull(dto.getSecurityCostOri())) {
                dto.setSecurityCostOri(BigDecimal.ZERO);
            }
            if (Objects.isNull(dto.getSmvOri())) {
                dto.setSmvOri(BigDecimal.ZERO);
            }
            if (Objects.isNull(dto.getValueAdded())) {
                dto.setValueAdded(BigDecimal.ZERO);
            }
            if (Objects.isNull(dto.getValueAddedOri())) {
                dto.setValueAddedOri(BigDecimal.ZERO);
            }
        }
    }
   private void summary(RskValtblAssetSubDto sumDto,RskValtblAssetSubDto processDto) {
	   //证券数量 securityQuantity 
	   sumDto.setSecurityQuantity(processAdd(sumDto.getSecurityQuantity(),processDto.getSecurityQuantity()));
	   //单位成本 unitCost 汇总处理待确认
	   //证券成本 securityCost
	   sumDto.setSecurityCost(processAdd(sumDto.getSecurityCost(),processDto.getSecurityCost()));
	   //成本占净值比 ctnwr
	   sumDto.setCtnwr(processAdd(sumDto.getCtnwr(),processDto.getCtnwr()));
	   //行情收市价 mcp 汇总处理待确认
	   //证券市值  smv
	   sumDto.setSmv(processAdd(sumDto.getSmv(),processDto.getSmv()));
	   //市值占净值比 mcr
	   sumDto.setMcr(processAdd(sumDto.getMcr(),processDto.getMcr()));
	   //估值增值 valueAdded
	   sumDto.setValueAdded(processAdd(sumDto.getValueAdded(),processDto.getValueAdded()));
	   //停牌信息 equityInfo
	   //权益信息 valType
       //成本（原币）
       sumDto.setSecurityCostOri(processAdd(sumDto.getSecurityCostOri(), processDto.getSecurityCostOri()));
       //市值（原币）
       sumDto.setSmvOri(processAdd(sumDto.getSmvOri(), processDto.getSmvOri()));
       //估值增值（原币）
       sumDto.setValueAddedOri(processAdd(sumDto.getValueAddedOri(), processDto.getValueAddedOri()));
       //汇率
   }
   
   private BigDecimal processAdd(BigDecimal num1,BigDecimal num2) {
	   if(num1==null&&num2==null) {
		   return null;
	   }
	   return ArithmeticUtil.doAdd(num1,num2);
   }
   
   /**
    * 
    * 底层向上汇总--共通
    * <功能详细描述>
    * @param valList
    * @param vtcountSubList
    * @param level 03(三级) 02(二级) 01(一级)
    * @return
    * @see
    * @since 1.0
    */
   private List<RskValtblAssetSubDto> commonHandleSum(List<RskValtblAssetSubDto> valList,List<RskValtblSubject>  vtcountSubList,String level,Map<String,SumNameCodeEntry> vtsumCodeMap){
       List<RskValtblAssetSubDto> resultList = new LinkedList<RskValtblAssetSubDto>();
       Map<String,String> tempmap = new HashMap<String,String>();
       for(RskValtblAssetSubDto por:valList){
           //过滤估值表汇总科目映射
           if(vtsumCodeMap.containsKey(por.getSubCode())){
               continue;
           }
           String subSubjcode = StringUtil.EMPTY;
           String ccy = por.getCcy();
           BigDecimal exchangeRate = por.getExchangeRate();
           RskValtblAssetSubDto tempporDto = new RskValtblAssetSubDto();
               BigDecimal security_cost=BigDecimal.ZERO;
               BigDecimal ctnwr=BigDecimal.ZERO;
               BigDecimal smv=BigDecimal.ZERO;
               BigDecimal mcr=BigDecimal.ZERO;
               BigDecimal securityCostOri = BigDecimal.ZERO;
               BigDecimal smvOri = BigDecimal.ZERO;
               BigDecimal valueAddedOri = BigDecimal.ZERO;
               BigDecimal valueAdded = BigDecimal.ZERO;
               boolean flag=false;
               if(StringUtil.equals("03", level)){
                   if(por.getSubCode().length()>=8){
                       subSubjcode=por.getSubCode().substring(0, 8);
                   }
               }else if(StringUtil.equals("02", level)){
                   if(por.getSubCode().length()<=8&&por.getSubCode().length()>=6){
                       subSubjcode=por.getSubCode().substring(0, 6);
                   }
               }else if(StringUtil.equals("01", level)){
                   if(por.getSubCode().length()<=6&&por.getSubCode().length()>=4){
                       subSubjcode=por.getSubCode().substring(0, 4);
                       
               }
               }
               if(tempmap.containsKey(subSubjcode)){
                   continue;
               }
               if(StringUtil.isNotEmpty(subSubjcode)){
                   tempmap.put(subSubjcode, subSubjcode);
                   for(RskValtblAssetSubDto por_all:valList){
                       if(StringUtil.isEmpty(por_all.getAllowSum())){
                           por_all.setAllowSum("Y");
                       }
                       if(StringUtil.equals(por_all.getAllowSum(), "N")){
                           continue;
                       }else{
                           if(!flag){
                               for(RskValtblSubject dto:vtcountSubList){
                                   if(dto.getSubCode().equals(subSubjcode)){
                                       flag=true;
                                       tempporDto.setSubName(dto.getSubName());
                                   }
                               }
                           }
                           if(por_all.getSubCode().startsWith(subSubjcode)){
                               if((StringUtil.equals("03", level)&&(por_all.getSubCode().length()>=8))
                                   ||(StringUtil.equals("02", level)&&(por_all.getSubCode().length()==8||por_all.getSubCode().length()==6))
                                   ||(StringUtil.equals("01", level)&&((por_all.getSubCode().length()==6||por_all.getSubCode().length()==4)))){
                                   if (por_all.getSecurityCostOri() != null) {
                                       securityCostOri = ArithmeticUtil.doAdd(securityCostOri, por_all.getSecurityCostOri());
                                   }
                                   if (por_all.getSmvOri() != null) {
                                       smvOri = ArithmeticUtil.doAdd(smvOri, por_all.getSmvOri());
                                   }
                                   if (por_all.getValueAddedOri() != null) {
                                       valueAddedOri = ArithmeticUtil.doAdd(valueAddedOri, por_all.getValueAddedOri());
                                   }
                                   if (por_all.getValueAdded() != null) {
                                       valueAdded = ArithmeticUtil.doAdd(valueAdded, por_all.getValueAdded());
                                   }
                                   if(por_all.getSecurityCost()!=null){
                                       security_cost = ArithmeticUtil.doAdd(security_cost,por_all.getSecurityCost());
                                   }
                                   if(por_all.getCtnwr()!=null){
                                       ctnwr = ArithmeticUtil.doAdd(ctnwr,por_all.getCtnwr());
                                   }
                                   if(por_all.getSmv()!=null){
                                       smv = ArithmeticUtil.doAdd(smv,por_all.getSmv());
                                   }
                                   if(por_all.getMcr()!=null){
                                       mcr = ArithmeticUtil.doAdd(mcr,por_all.getMcr());
                                   }
                               }
                           }
                       }
                   }
                   if(!flag){
                       tempporDto.setSubName(por.getSubName());
                   }
                   tempporDto.setValType(ValType.ASSET);
                   tempporDto.setSubCode(subSubjcode);
                   tempporDto.setSecurityCost(security_cost.setScale(8, BigDecimal.ROUND_HALF_UP));
                   tempporDto.setCtnwr(ctnwr.setScale(8, BigDecimal.ROUND_HALF_UP));
                   tempporDto.setSmv(smv.setScale(8, BigDecimal.ROUND_HALF_UP));
                   tempporDto.setMcr(mcr.setScale(8, BigDecimal.ROUND_HALF_UP));
                   tempporDto.setIsMatch(por.getIsMatch());
                   tempporDto.setSecurityCostOri(securityCostOri.setScale(8, BigDecimal.ROUND_HALF_UP));
                   tempporDto.setSmvOri(smvOri.setScale(8, BigDecimal.ROUND_HALF_UP));
                   tempporDto.setValueAddedOri(valueAddedOri.setScale(8, BigDecimal.ROUND_HALF_UP));
                   tempporDto.setValueAdded(valueAdded.setScale(8, BigDecimal.ROUND_HALF_UP));
                   tempporDto.setExchangeRate(exchangeRate);
                   tempporDto.setCcy(ccy);
                   resultList.add(tempporDto);
               }
       }
       //移除重复的科目代码数据
       for(RskValtblAssetSubDto por:valList){
           if(!tempmap.containsKey(por.getSubCode())){
               resultList.add(por);
           }
       }
       return resultList;
   }
   /**
    * 底层---一级汇总
    * <功能详细描述>
    * @param valList
    * @param vtaccountMap
    * @param vtcountSubList
    * @return
    * @see
    * @since 1.0
    */
   private List<RskValtblAssetSubDto> sum_handValList(List<RskValtblAssetSubDto> valList,Map<String,SubCodeEntry> vtaccountMap,List<RskValtblSubject>  vtcountSubList,Map<String,SumNameCodeEntry> vtsumCodeMap){
       try{
           List<RskValtblAssetSubDto> valHand_threeList = commonHandleSum(valList,vtcountSubList,"03",vtsumCodeMap);
           List<RskValtblAssetSubDto> valHand_twoList = commonHandleSum(valHand_threeList,vtcountSubList,"02",vtsumCodeMap);
           List<RskValtblAssetSubDto> valHand_oneList = commonHandleSum(valHand_twoList,vtcountSubList,"01",vtsumCodeMap);
           Collections.sort(valHand_oneList);
           return valHand_oneList;
       }catch(Exception e){
           logger.error(e.getMessage(),e);
       }
       return null;
   }
   
   /**
    * 处理资产科目
    * <功能详细描述>
    * @param valList
    * @param vtaccountMap
    * @return
    * @see
    * @since 1.0
    */
   private List<RskValtblAssetSubDto> handValList(List<RskValtblAssetSubDto> valList,Map<String,SubCodeEntry> vtaccountMap){
       //处理valList---取出映射表配置的科目代码及底层资产
       List<RskValtblAssetSubDto> one_handval_List = new LinkedList<RskValtblAssetSubDto>();
       List<RskValtblAssetSubDto> two_handval_List = new LinkedList<RskValtblAssetSubDto>();
       List<RskValtblAssetSubDto> sum_val_List = new LinkedList<RskValtblAssetSubDto>();
       List<RskValtblAssetSubDto> unMatchList = new LinkedList<RskValtblAssetSubDto>();//统计未匹配的在画面红字展示
       String TAX_SECTION_BEGIN = CacheUtil.getSysParam("TAX_SECTION_BEGIN");
       List<String> fplist=StringUtil.splitToList(",", CacheUtil.getSysParam("GP_VALUATION_APPRECIATION_SUBJECTS"));
       for(RskValtblAssetSubDto handval:valList){
           handval.setIsMatch(DictCodeListExt1.是否_是.getValue());
           if(handval.getValType().equals(ValType.SUM)){
               sum_val_List.add(handval);
           }
           String subjCode = handval.getSubCode(); 
           if(vtaccountMap.containsKey(subjCode)){
               if(vtaccountMap.get(subjCode)!=null){
                   //允许汇总 Y N
                   handval.setAllowSum(vtaccountMap.get(subjCode).getAllowSum());
                   one_handval_List.add(handval);
               }
           }else{
               boolean isMatch = false;//add by lpf 统计未匹配的
               for(int i=subjCode.length();i>0;i--){
                   String beforSub = subjCode.substring(0, i);
                   if(vtaccountMap.containsKey(beforSub)){
                       one_handval_List.add(handval);
                       isMatch = true;
                       break;
                   }
               }
               //统计未匹配的红字展示
               //“1” “3”开头或者 ”2“开头回购融入
               // GLLCUAT-400:增加 “估增” 判断 modify by fuyongsheng 20220917 start
               if(!(handval.getSubName().contains("估值增值") || handval.getSubName().contains("估增")) && !(fplist.contains(handval.getSubCode()))){
               // GLLCUAT-400:增加 “估增” 判断 modify by fuyongsheng 20220917 end
                       if (!isMatch && handval.getValType() == ValType.ASSET  && (!(handval.getSubCode().substring(0, 1).equals(TAX_SECTION_BEGIN)))) {
                           handval.setIsMatch(DictCodeListExt1.是否_否.getValue());
                           unMatchList.add(handval);
                       }else if(!isMatch && handval.getValType() == ValType.ASSET && (handval.getSubCode().substring(0, 1).equals(TAX_SECTION_BEGIN))
                               && ((handval.getSubCode().length() >= 4 && (handval.getSubCode().substring(0, 4).equals("2202") || handval.getSubCode().substring(0, 4).equals("2701")))
                               ||(handval.getSubCode().length() >= 6 && handval.getSubCode().substring(0, 6).equals("223101")))){
                           handval.setIsMatch(DictCodeListExt1.是否_否.getValue());
                           unMatchList.add(handval);
                       }
               }
               //统计未匹配的红字展示
           }
       }
       //保留叶子节点
       for(RskValtblAssetSubDto one_handval:one_handval_List){
           int i=0;
           for(RskValtblAssetSubDto two_handval:one_handval_List){
               if(two_handval.getSubCode().startsWith(one_handval.getSubCode())
                       &&two_handval.getSubCode().length()>one_handval.getSubCode().length()){
                   i++;
                   break;
               }
           }
           if(i==0){
               two_handval_List.add(one_handval);
           }
       }
       // 去除未映射List 子节点已映射的
       for(RskValtblAssetSubDto dto :two_handval_List){
           Iterator<RskValtblAssetSubDto> it = unMatchList.iterator();
           while (it.hasNext()){
               RskValtblAssetSubDto dtoit = it.next();
               if(dto.getSubCode().startsWith(dtoit.getSubCode())){
                   it.remove();
               }
           }
       }
       //资产科目代码替换成标准科目代码
       for(RskValtblAssetSubDto two_handval:two_handval_List){
           String subjCode = two_handval.getSubCode();
           if(vtaccountMap.containsKey(subjCode)){
               if(vtaccountMap.get(subjCode)!=null){
                   SubCodeEntry entity = (SubCodeEntry)vtaccountMap.get(subjCode);
                   //允许汇总 Y N
                   two_handval.setAllowSum(entity.getAllowSum());
                   two_handval.setSubCode(entity.getTgt_code());
                   two_handval.setSubName(entity.getTgt_name());
               }
           }else{
               for(int i=subjCode.length();i>0;i--){
                   String beforSub = subjCode.substring(0, i);
                   String afterSub = subjCode.substring(i,subjCode.length());
                   if(vtaccountMap.containsKey(beforSub)){
                       SubCodeEntry tempEntity = (SubCodeEntry)vtaccountMap.get(beforSub);
                       two_handval.setSubCode(tempEntity.getTgt_code()+afterSub);
                       break;
                   }
               }
           }  
       }
       // 特殊处理100201科目 & 120401科目
       for(RskValtblAssetSubDto two_handval:two_handval_List) {
           String subjCode = two_handval.getSubCode();
           if (subjCode.startsWith("100201") && !"100201".equals(subjCode)) {
               subjCode = "100201" + SysDataUtil.getNameFromDataDict(DictCode.估值币种映射, two_handval.getCcy());
           } else if (subjCode.startsWith("120401") && !"120401".equals(subjCode)) {
               subjCode = "120401" + SysDataUtil.getNameFromDataDict(DictCode.估值币种映射, two_handval.getCcy());
           }
           two_handval.setSubCode(subjCode);
       }
       valList.clear();
       valList.addAll(sum_val_List);
       valList.addAll(two_handval_List);
       valList.addAll(unMatchList);//统计未匹配的
       return valList;
   }
   
   /**
    * 处理excel------估值表
    * <功能详细描述>
    * @param list
    * @param paramMap
    * @return
    * @see
    * @since 1.0
    */
   private RskValtblAssetSubDto handExcelListForVal(List<String> list,Map<String, Object> paramMap){
       RskValtblAssetSubDto val = new RskValtblAssetSubDto();
       int subjCodeColIdx=Integer.valueOf(paramMap.get("subjectCodeCol").toString())-1;
       String subjCode = StringUtil.EMPTY;
       if(list.size()>subjCodeColIdx+1){
           subjCode = list.get(subjCodeColIdx);
       }
       if(StringUtil.isEmpty(subjCode)){return null;}else{
           subjCode.replaceAll(Pattern.quote(" "), "");
           //add by 张年  2019/07/30
           //科目代码统一去点、去横杠
           subjCode = subjCode.replaceAll(Pattern.quote(" "), "").replace(".","").replace("-","");}
          //add by 张年  2019/07/30
       //根据科目代码列 判断是资产科目还是汇总科目
       if(subjCode.length() >= 4) {
           try {
               Integer.valueOf(subjCode.substring(0, 4));
               val.setValType(ValType.ASSET);
           } catch(NumberFormatException e) {
               val.setValType(ValType.SUM);
           }
       } else {
           val.setValType(ValType.SUM);
       }
       val = handleSubjCodeCol(list,val,paramMap,subjCodeColIdx,subjCode);
       return val;
   }
   
   /**
    * 处理科目代码列
    * <功能详细描述>
    * @return
    * @see
    * @since 1.0
    */
   @SuppressWarnings("unchecked")
   private RskValtblAssetSubDto handleSubjCodeCol(List<String> list,RskValtblAssetSubDto val,Map<String, Object> paramMap,int subjCodeColIdx,String subjCode){
       SumNameCodeEntry sumNameCodeEntry = null;String subjName = StringUtil.EMPTY;
       switch(val.getValType()) {
           case ASSET:
               val.setSubCode(subjCode);
               val.setSubName(subjName);
               break;
           case SUM:
               Map<String,SumNameCodeEntry> vtsumaccountMap = (Map<String, SumNameCodeEntry>) paramMap.get("vtsumaccountMap");
               if(paramMap.get("trimChars")!=null){
                   if(StringUtil.isNotEmpty(paramMap.get("trimChars").toString())){
                   String regex = "["
                           + paramMap.get("trimChars").toString()
                               .replaceAll("\\\\", "\\\\\\\\")
                               .replaceAll("\\[", "\\\\[")
                               .replaceAll("\\]", "\\\\]")
                               .replaceAll("\\^", "\\\\^")

                           +"]";
                   subjCode = subjCode.replaceAll(regex, "");
               }
               }
               if(vtsumaccountMap.containsKey(subjCode)){
                   sumNameCodeEntry = vtsumaccountMap.get(subjCode);
               }
               if(sumNameCodeEntry == null) return null;
               String sumName="";
               if(paramMap.get("trim_chars")!=null){
               if(StringUtil.isNotEmpty(paramMap.get("trim_chars").toString())){
                   String regex = "["
                           + paramMap.get("trim_chars").toString()
                           .replaceAll("\\\\", "\\\\\\\\")
                           .replaceAll("\\[", "\\\\[")
                           .replaceAll("\\]", "\\\\]")
                           .replaceAll("\\^", "\\\\^")
                       +"]";
                   sumName = sumNameCodeEntry.getTgt_sumName().replaceAll(regex, "");
               }else{
                   sumName = sumNameCodeEntry.getTgt_sumName();
               }
               }else{
                   sumName = sumNameCodeEntry.getTgt_sumName();
               }
               val.setSubName(sumName);
               val.setSubCode(sumNameCodeEntry.getSumCode());
               break;
       }
       //下一列
       subjName = list.get(subjCodeColIdx+1);
       if(val!=null){
           switch(val.getValType()) {
           case ASSET:
               val.setSubName(subjName);
               break;
           case SUM:
               if(ValDataType.NULL == (sumNameCodeEntry==null?null:sumNameCodeEntry.getNextColType())) break;
               Double cellNum = null;
               if(StringUtil.isNotEmpty(subjName) && subjName.trim().length()!=0){
                   subjName = subjName.replaceAll(",", "");
                   if(sumNameCodeEntry != null && ValDataType.STRING == sumNameCodeEntry.getNextColType()){
                       cellNum = null;
                   }else{
                       if(subjName.contains("%")){
                           subjName = subjName.replaceAll("%", "");
                           cellNum = (BigDecimal.valueOf(Double.valueOf(subjName)/100)).setScale(8,   BigDecimal.ROUND_HALF_UP).doubleValue();
                       }else{
                           cellNum = (BigDecimal.valueOf(Double.valueOf(subjName))).setScale(8,   BigDecimal.ROUND_HALF_UP).doubleValue();
                       }
                   }
               }else{
                   cellNum = null;
               }
               if(cellNum != null && sumNameCodeEntry != null) {
                   if(ValDataType.PERCENT == sumNameCodeEntry.getNextColType()) {
                       cellNum = (BigDecimal.valueOf(cellNum/100)).setScale(8,   BigDecimal.ROUND_HALF_UP).doubleValue();
                   }
                   val.setSecurityCost(BigDecimal.valueOf(cellNum));
               }
               break;
       }
       //处理其他列
       handleOtherCol(list,val,paramMap);
       }
       return val;
   }
   
   @SuppressWarnings("unchecked")
   private void handleOtherCol(List<String> list,RskValtblAssetSubDto val,Map<String, Object> paramMap){
       String cellVlaue = StringUtil.EMPTY;
       BigDecimal cellNum = null;
       String cellStr = StringUtil.EMPTY;
       Map<String,ColumnMapEntry> vtstructureMap = new HashMap<String, ColumnMapEntry>();
       vtstructureMap = (Map<String, ColumnMapEntry>) paramMap.get("vtstructureMap");
       for(int i=0;i<list.size();i++){
           cellVlaue = list.get(i);
           cellStr = cellVlaue.replaceAll(" ", "");
           try{
               ColumnMapEntry colMapEntry = vtstructureMap.get(Integer.toString(i+1));
               if(colMapEntry == null) {
                   continue;
               }
               if(StringUtil.isNotEmpty(cellStr)){
                   cellStr = cellStr.replaceAll(",", "");
                   if(ValDataType.STRING == colMapEntry.getDataType()){
                       cellNum = null;
                   }else{
                       if(cellVlaue.contains("%")){
                           cellStr = cellStr.replaceAll("%", "");
                           cellNum = (BigDecimal.valueOf(Double.valueOf(cellStr)/100)).setScale(8,   BigDecimal.ROUND_HALF_UP);
                       }else{
                           cellNum = (BigDecimal.valueOf(Double.valueOf(cellStr))).setScale(8,   BigDecimal.ROUND_HALF_UP);
                       }
                   }
               }else{
                   cellNum = null;
               }
               if(ValDataType.PERCENT == colMapEntry.getDataType()) {
                   cellNum = cellNum == null ? null : (cellNum.divide(new BigDecimal(100))).setScale(8,   BigDecimal.ROUND_HALF_UP);
               }
               switch(colMapEntry.getValIndex()) {
                   case QUANTITY:
                       if(cellNum != null) val.setSecurityQuantity(cellNum);
                       break;
                   case UNIT_COST:
                       if(cellNum != null) val.setUnitCost(cellNum);
                       break;
                   case COST:
                       if(cellNum != null) {
                           if(val.getSecurityCost()==null){
                               val.setSecurityCost(cellNum);
                           }
                       }
                       break;
                   case CTNWR:
                       if(cellNum != null) val.setCtnwr(cellNum);
                       break;
                   case MCP:
                       if(cellNum != null) val.setMcp(cellNum);
                       break;
                   case SMV:
                       if(cellNum != null) val.setSmv(cellNum);
                       break;
                   case MCR:
                       if(cellNum != null) val.setMcr(cellNum);
                       break;
                   case VAL_INC:
                       if(cellNum != null) val.setValueAdded(cellNum);
                       break;
                   case SUSPENSION_INFO:
                       val.setSuspensionInfo(cellStr);
                       break;
                   case EQUITY_INFO:
                       val.setEquityInfo(cellStr);
                       break;
                   case COST_ORI:
                       if (cellNum != null) {
                           if (val.getSecurityCostOri() == null) {
                               val.setSecurityCostOri(cellNum);
                           }
                       }
                       break;
                   case SMV_ORI:
                       if (cellNum != null) {
                           val.setSmvOri(cellNum);
                       }
                       break;
                   case VAL_INC_ORI:
                       if (cellNum != null) {
                           val.setValueAddedOri(cellNum);
                       }
                       break;
                   case CCY:
                       val.setCcy(cellStr);
                       break;
                   case EXCHANGE_RATE:
                       if (cellNum != null) {
                           val.setExchangeRate(cellNum);
                       }
                       break;
                   default:
                       break;
               }
           }catch(Exception e){
               logger.error(e.getMessage(),e);
           }
       }
       if ((Boolean) paramMap.get("isExistCcyCol")) {
           val.setCcy(DictCodeList.币种_人民币_CNY.getValue());
       }
   }
   
   /**
    *  重读excel
    * <功能详细描述>
    * @param dto
    * @return
 * @throws Exception 
    * @see
    * @since 1.0
    */
	private List<List<String>> readExcelUtil(RskValTableImportParamDto dto) throws FileNotFoundException, ClassNotFoundException, IllegalAccessException, InstantiationException {
		List<List<String>> resultList = new ArrayList<List<String>>();
		Workbook wb = ExcelUtil.loadWorkbook(dto.getValTableFileName(), null, null);
        if (Objects.isNull(wb)) {
            return null;
        }
        // ---modify by 裴家俊 20190514---start---------
		Filter filter = null;
		if (StringUtil.isNotEmpty(dto.getFilterId())) {
			if (dto.getFilterId().startsWith(BussinessConstant.VALUATION_FILTER_MERGECOL)) {
				filter = (Filter) Class
						.forName(BussinessConstant.VALUATION_FILTER_URL + BussinessConstant.VALUATION_FILTER_MERGECOL)
						.newInstance();
			} else {
				filter = (Filter) Class.forName(BussinessConstant.VALUATION_FILTER_URL + dto.getFilterId())
						.newInstance();
			}
		}
		if (filter != null) {
			if (dto.getFilterId().startsWith(BussinessConstant.VALUATION_FILTER_MERGECOL)) {
				wb = filter.doFilter(wb, 0,
						dto.getFilterId().substring(BussinessConstant.VALUATION_FILTER_MERGECOL.length() + 1));
			} else {
				wb = filter.doFilter(wb, 0);
			}
			resultList = readToList(0, Integer.parseInt(dto.getStartRow()) - 1, Integer.parseInt(dto.getStartRow()) - 1,
					wb);
		} else {
			resultList = readToList(0, Integer.parseInt(dto.getStartRow()) - 1, Integer.parseInt(dto.getStartRow()) - 2,
					wb);
		}
		// ---modify by 裴家俊 20190514---end---------
		return resultList;
	}
   
   /**
    * 解析excel
    * <功能详细描述>
    * @param sheetIndex
    * @param skipRows
    * @param titleRow
    * @return
    * @throws Exception
    * @see
    * @since 1.0
    */
   private List<List<String>> readToList(int sheetIndex, int skipRows, int titleRow,Workbook wb)
        {
       List<List<String>> ls = new ArrayList<List<String>>();
       if (null != wb) {
           Sheet sh = wb.getSheetAt(sheetIndex);
           int rows = sh.getPhysicalNumberOfRows();
           int cells = sh.getRow(titleRow).getPhysicalNumberOfCells();
           //这步check多余   双表头无法读出数据
           /*Row tRow = sh.getRow(titleRow);*/
           // 列标题为空的话结束
           /*for (int c = 0; c < cells; c++ ) {
               if (tRow.getCell(c) == null
                   || StringUtil.isEmpty(getStringCellValue(tRow.getCell(c)))) {
                   cells = c;
                   break;
               }
           }*/

           for (int i = skipRows; i < rows; i++ ) {
               Row row = sh.getRow(i);
               if (row == null) {
                   break;
               }
               List<String> r = new ArrayList<String>(cells);
               boolean dataFlag = true;
               for (int c = 0; c < cells; c++ ) {
                   if (row.getCell(c) == null
                       || row.getCell(c).getCellTypeEnum() == CellType.BLANK) {
                       r.add("");
                   }
                   else {
                       dataFlag = false;
                       r.add(getStringCellValue(row.getCell(c)));
                   }
               }
               // 遇到一行全为空继续
               if (dataFlag) {
                   continue;
               }
               ls.add(r);
           }
       }
       return ls;
   }
   
   
   
   
   private static String getStringCellValue(Cell cell) {
       String value = null;
       switch (cell.getCellTypeEnum()) {
           case NUMERIC: // 数字
               if (HSSFDateUtil.isCellDateFormatted(cell)) {
                   value = DateUtil.format(cell.getDateCellValue(),
                       DateUtil.DEFAULT_ANOTHER_FORMAT);
                   return value;
               }
               value = ExcelUtil.point.format(cell.getNumericCellValue());
               break;
           case STRING: // 字符串
               value = cell.getStringCellValue();
               break;
           case FORMULA: // 公式
               cell.setCellType(CellType.STRING);
               value = cell.getStringCellValue();
               break;
           case BLANK: // 空值
               break;
           case BOOLEAN: // Boolean
               break;
           case ERROR: // 故障
               break;
           default:
               if (HSSFDateUtil.isCellDateFormatted(cell)) {
                   value = DateUtil.format(cell.getDateCellValue());
               }
               break;
       }

       return value;
   }
   
   /**
    * 估值表科目数量
    * <功能详细描述>
    * @param paramMap
    * @return
    * @see
    * @since 1.0
    */
   public Integer selectCount(Map<String,Object> paramMap){
       return restTemplate.postForObject(
           ServiceApplication.RISK_SERVICE_NAME + "/RskValTableImport/selectCount",paramMap,
           Integer.class);
   }
   
   /**
    * 组合下拉框(分页)
    * <功能详细描述>
    * @param param
    * @return
    * @throws Exception
    * @see
    * @since 1.0
    */
   public Page<SelectOptionDto> getGroupIdInfo(Map<String, Object> param)
         {
       param.put("loginUserName", SecurityUtil.getLoginUser().getUserName());
       param.put("portfolioType", DictCodeListExt1.组合类型_委外组合.getValue());
       return restTemplate.postForPage(
           ServiceApplication.COMMON_SERVICE_NAME + "/CommonSelect/getGroupIdInfo", param,
           SelectOptionDto.class);
   }

    /**
     * 根据估值表中回购数据生成对应的回购信息，存入表fin_trade_product、fin_product_type
     * @param entity2
     */
    private void createRepoInfo(RskValtblAssetSubDto entity2){
        restTemplate.postForObject(
                ServiceApplication.RISK_SERVICE_NAME +"/RskValTableImport/createRepoInfo",
                entity2,Integer.class);
    }
   
   /**
    * 上农商个性化_估值表导入 
    * 预占实占变更 
    * add by zhangpei
    * 20201222
 * @throws Exception 
    */
   private AjaxResult preOccupationChange(RskValtblAssetSubDto dto){
	   AjaxResult result=new AjaxResult();

	   //1.估值表文件中各资产的分配明细
	   List<PreOccupationChangeDto> detailList=restTemplate.postForList(
	            ServiceApplication.RISK_SERVICE_NAME +"/RskValTableImport/preOccupationDetail",
	            dto,PreOccupationChangeDto.class);
	   //2.预占实占变更
	   //将指令预占（分配组合+底层资产代码+组合分配金额）的风险指标和授信额度释放
	   List<PreOccupationChangeDto> needFreeList=restTemplate.postForList(
	            ServiceApplication.RISK_SERVICE_NAME +"/RskValTableImport/queryNeedFreedInfo",
	            dto,PreOccupationChangeDto.class);
	   if(!StringUtil.isEmptyList(needFreeList)){
		   //释放授信占用额度
		   List<CreditLinesInputDto> creditFreeList=getPreListForCreditLinesFreed(needFreeList,DictCodeListExt1.交易状态_撤销.getValue());
		   AllTradeCommonUtil.handleBeforeTradeComplete(creditFreeList,DictCodeListRsk.授信占用类别_预占.getValue());
	   }
	   //释放今日估值表该委外组合已经实占的数据
	   List<ValTableImpRiskDto> riskActList=restTemplate.postForList(
	            ServiceApplication.RISK_SERVICE_NAME +"/RskValTableImport/queryImpRiskInfo",
	            dto,ValTableImpRiskDto.class);
	   if(!StringUtil.isEmptyList(riskActList)){
		   //释放授信占用额度
		   List<CreditLinesInputDto> creditFreeList=getListForCreditLinesFreed(riskActList,DictCodeListExt1.交易状态_撤销.getValue(),"C");
		   AllTradeCommonUtil.handleBeforeTradeComplete(creditFreeList,DictCodeListRsk.授信占用类别_实占.getValue());
		   //删除实际占用授信的留痕数据
    	   restTemplate.postForObject(
    	           ServiceApplication.RISK_SERVICE_NAME +"/RskValTableImport/deleteByPrimaryKeyRisk",
    	           riskActList,Integer.class);
	   }
	   

		// 调用授信接口，按照估值表文件逐条实占
	    List<Map<String, Object>> actOccpListAll=new ArrayList<>();
		if (!StringUtil.isEmptyList(detailList)) {
			for (PreOccupationChangeDto actualDto : detailList) {
				List<Map<String, Object>> actOccpList = coverActListOccupForMapList(actualDto.getDetailDtos(),
						actualDto.getPortfolioId(),dto.getValDate());
				actOccpListAll.addAll(actOccpList);
			}
		}
		//留痕操作
		if(!StringUtil.isEmptyList(actOccpListAll)){
			List<ValTableImpRiskDto> listForLh=new ArrayList<>();
			for(Map<String, Object> map:actOccpListAll){
				ValTableImpRiskDto valTableImpRiskDto=new ValTableImpRiskDto();
				valTableImpRiskDto.setTradeId(coverStrForGetMapValue(map.get(PageFieldToInterfFactory.INF_EVENTID)));
				valTableImpRiskDto.setWwPortfolioid(dto.getPtlId());
				valTableImpRiskDto.setPortfolioId(coverStrForGetMapValue(map.get(PageFieldToInterfFactory.INF_PORTFOLIOCODE)));
				valTableImpRiskDto.setFinprodId(coverStrForGetMapValue(map.get(PageFieldToInterfFactory.INF_ASSETCODE)));
				valTableImpRiskDto.setPs(coverStrForGetMapValue(map.get(PageFieldToInterfFactory.INF_PS)));
				try {
					valTableImpRiskDto.setEventDate(DateUtil.parseDate((String)map.get(PageFieldToInterfFactory.INF_EVENTDATE), DateUtil.DEFAULT_DATE_FORMAT_EN));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				valTableImpRiskDto.setAssetType(coverStrForGetMapValue(map.get(PageFieldToInterfFactory.INF_ASSETTYPE)));
				valTableImpRiskDto.setAmount((BigDecimal)map.get(PageFieldToInterfFactory.INF_AMOUNT));
				valTableImpRiskDto.setCreditAmount((BigDecimal)map.get("CreditAmount"));
				listForLh.add(valTableImpRiskDto);
			}
			//插入留痕数据表
			restTemplate.postForObject(
      	           ServiceApplication.RISK_SERVICE_NAME +"/RskValTableImport/insertValImpRisk",
      	         listForLh,Integer.class);
			//实占授信额度
			List<CreditLinesInputDto> creditOccpList=getListForCreditLinesFreed(listForLh,DictCodeListExt1.交易状态_初始.getValue(),"C");
			AllTradeCommonUtil.handleBeforeTradeComplete(creditOccpList,DictCodeListRsk.授信占用类别_实占.getValue());
		}
		
	   //3.指令状态变更
	   if(!StringUtil.isEmptyList(needFreeList)){
		   updateEntrustStatus(needFreeList);
	   }
	   return result;
   }
   
   /**
	 * 上农商个性化_估值表导入
	 * 根据委托代码，更新指令状态为‘已完成’
	 * add by zhangpei
	 * 20201224
	 * @param list
	 * @return
	 */
   private void updateEntrustStatus(List<PreOccupationChangeDto> list){
       restTemplate.postForObject(
           ServiceApplication.RISK_SERVICE_NAME +"/RskValTableImport/updateEntrustStatus",
           list,Integer.class);
   }
   /**
    * 上农商个性化_估值表导入
    * 转换风险实占数据为List<Map<String, Object>>
    * @param riskActList
    * @return
    */
   @SuppressWarnings("unchecked")
   private List<Map<String, Object>> coverActListOccupForMapList(List<PreOccupActualDetailDto> detailDtos,String portfolioCode,Date valDate){
	   List<Map<String, Object>> result = new ArrayList<>();
	   if(!StringUtil.isEmptyList(detailDtos)){
		   for(PreOccupActualDetailDto dto:detailDtos){
			   Map<String, Object> finDto = new HashMap<>();
			   //设置tradeId
	           String code = IDGenerator.generate();
               finDto.put(PageFieldToInterfFactory.INF_EVENTID, code);
               finDto.put(PageFieldToInterfFactory.INF_PS, BussinessDictCodeList.交易方向_买入.getValue());
               finDto.put(PageFieldToInterfFactory.INF_EVENTDATE, DateUtil.format(
            		   valDate, DateUtil.DEFAULT_DATE_FORMAT_EN));
               finDto.put(PageFieldToInterfFactory.INF_REVOKEFLG, BussinessDictCodeListRds.是否_否.getValue());
               finDto.put(PageFieldToInterfFactory.INF_ISINNERTRD, BussinessDictCodeListRds.是否_否.getValue());
               interfRiskRdsService.changeEventId(finDto);
               finDto.put(PageFieldToInterfFactory.INF_PORTFOLIOCODE, portfolioCode);
               finDto.put(PageFieldToInterfFactory.INF_ASSETCODE, dto.getAssetFinprodId());
               finDto.put(PageFieldToInterfFactory.INF_ASSETTYPE, dto.getFinprodType2());
               finDto.put(PageFieldToInterfFactory.INF_AMOUNT, dto.getAllotAmout());
               finDto.put(PageFieldToInterfFactory.INF_MARKETCODE, dto.getAssetFinprodId());
               finDto.put(PageFieldToInterfFactory.INF_ISWARNNEEDCONFIRM, DictCodeList.是否_否.getValue());
               finDto.put(PageFieldToInterfFactory.INF_ISAPPRNEEDCONFIRM, DictCodeList.是否_否.getValue());
               finDto.put(PageFieldToInterfFactory.INF_EVENTTYPE, BussinessDictCodeListRds.事件类型_投资交易.getValue());
               //辅助字段：--传入Redis无用，为Credit
               finDto.put("CreditAmount", dto.getAllotAmoutCredit());
			   result.add(finDto);
		   }
	   }
	   return result;
   }
   /**
    * 上农商个性化_估值表导入
    * 格式转换
    * @param obj
    * @return
    */
   private String coverStrForGetMapValue(Object obj){
	   if(StringUtil.isNotEmpty(obj)){
		   if(obj instanceof String){
			   return obj.toString();
		   }
	   }
	   return null;
   }
   /**
    * 上农商个性化_估值表导入
    * 获取授信额度组装的List
    * add by zhangpei
    * 20201225
    * @param riskActList
    * @return
    */
   private List<CreditLinesInputDto> getListForCreditLinesFreed(List<ValTableImpRiskDto> riskActList,String status,String flag){
	   List<CreditLinesInputDto> inputDtoList = new ArrayList<>();
	   for(ValTableImpRiskDto dto:riskActList){
		   CreditLinesInputDto creditLinesInputDto = new CreditLinesInputDto();
		   creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
           creditLinesInputDto.setTradeId(dto.getTradeId());
           if(StringUtil.equals(DictCodeListExt1.交易状态_撤销.getValue(), status)){
        	   creditLinesInputDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
           }else{
        	   creditLinesInputDto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
           }
           
           if(StringUtil.isNotEmpty(dto.getEventDate())){
        	   creditLinesInputDto.setTradeDate(dto.getEventDate());
           }else{
        	   creditLinesInputDto.setTradeDate(DateUtil.platDateFromCache());
           }
           creditLinesInputDto.setFinprodId(dto.getFinprodId());
           creditLinesInputDto.setFinprodType2(dto.getAssetType());
           creditLinesInputDto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
           if(StringUtil.equals("R", flag)){
        	   creditLinesInputDto.setTradeAmt(dto.getAmount());
               creditLinesInputDto.setShareAmt(dto.getAmount()); 
           }else if(StringUtil.equals("C", flag)){
        	   creditLinesInputDto.setTradeAmt(dto.getCreditAmount());
               creditLinesInputDto.setShareAmt(dto.getCreditAmount()); 
           }else{
        	   creditLinesInputDto.setTradeAmt(dto.getAmount());
               creditLinesInputDto.setShareAmt(dto.getAmount());  
           }
           creditLinesInputDto.setPs(BussinessDictCodeList.交易方向_买入.getValue());
           creditLinesInputDto.setIsBottom("Y");
           String id = restTemplate.postForObject(ServiceApplication.RISK_SERVICE_NAME +"/RskValTableImport/selectId",
                   dto.getWwPortfolioid(),String.class);
           creditLinesInputDto.setpFinprodId(id);
		   inputDtoList.add(creditLinesInputDto);
	   }
	   return inputDtoList;
   }
   
   /**
    * 上农商个性化_估值表导入
    * 获取预占授信额度组装的List
    * add by zhangpei
    * 20201225
    * @param riskActList
    * @return
    */
   private List<CreditLinesInputDto> getPreListForCreditLinesFreed(List<PreOccupationChangeDto> needFreeList,String status){
	   List<CreditLinesInputDto> inputDtoList = new ArrayList<>();
	   for(PreOccupationChangeDto dto:needFreeList){
		   CreditLinesInputDto creditLinesInputDto = new CreditLinesInputDto();
		   creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
           creditLinesInputDto.setTradeId(dto.getEntrustId());
           if(StringUtil.equals(DictCodeListExt1.交易状态_撤销.getValue(), status)){
        	   creditLinesInputDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
           }else{
        	   creditLinesInputDto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
           }
           
           creditLinesInputDto.setTradeDate(DateUtil.platDateFromCache());
           creditLinesInputDto.setFinprodId(dto.getBottomAssetId());
           creditLinesInputDto.setFinprodType2(dto.getFinprodType2());
           creditLinesInputDto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
           creditLinesInputDto.setTradeAmt(dto.getAmount());
           creditLinesInputDto.setShareAmt(dto.getAmount());
           creditLinesInputDto.setPs(BussinessDictCodeList.交易方向_买入.getValue());
           creditLinesInputDto.setIsBottom("Y");
		   inputDtoList.add(creditLinesInputDto);
	   }
	   return inputDtoList;
   }

    public int selectGoalCount(Map<String, Object> paramMap) {
        return restTemplate.postForObject(
                ServiceApplication.RISK_SERVICE_NAME + "/RskValTableImport/selectGoalCount",paramMap,
                Integer.class);
    }

	@Override
	protected void buildExportParam(ExportParam exportParam) {
		 exportParam.config("/com/joyintech/tams/invest/risk/valtblimport/conf/export_conf002.xml")
         .dataSource(DataSourceType.MASERT);
	}

	@Override
	public Page<RskValtblAssetSubDto> selectPageData(Map<String, Object> selectParam) {
		RskValTableImportParamDto dto=new RskValTableImportParamDto();
		dto.setAssetSubMapId(selectParam.get("structMapId")==null?null:selectParam.get("structMapId").toString());
		dto.setPtlId(selectParam.get("ptlId")==null?null:selectParam.get("ptlId").toString());
		dto.setValTableFileName(selectParam.get("valTableFileName")==null?null:selectParam.get("valTableFileName").toString());
	    dto.setValDate(selectParam.get("valDate")==null?null:DateUtil.parse(selectParam.get("valDate").toString()));
		dto.setSumsubMapId(selectParam.get("sumsubMapId")==null?null:selectParam.get("sumsubMapId").toString());
		dto.setAssetSubMapId(selectParam.get("assetSubMapId")==null?null:selectParam.get("assetSubMapId").toString());
		dto.setSubjectCodeCol(selectParam.get("subjectCodeCol")==null?null:selectParam.get("subjectCodeCol").toString());
		dto.setStartRow(selectParam.get("startRow")==null?null:selectParam.get("startRow").toString());
		dto.setTrimChars(selectParam.get("trimChars")==null?null:selectParam.get("trimChars").toString());
		dto.setFilterId(selectParam.get("filterId")==null?null:selectParam.get("filterId").toString());
		dto.setStructMapId(selectParam.get("structMapId")==null?null:selectParam.get("structMapId").toString());
		
		List<RskValtblAssetSubDto> list=new LinkedList<RskValtblAssetSubDto>();
		try {
			list = viewValTable(dto);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Page<RskValtblAssetSubDto> page=new Page<RskValtblAssetSubDto>();
		page.setRoot(list);
		return page;
	}
	
	/**
     * 设置预警与违规格式
     * {@inheritDoc}
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
    	 DecimalFormat df=new DecimalFormat("###,###.##"); //保留二位小数
    	if(("securityQuantity".equals(propName)) && value != null){
             cell.setCellValue(df.format(new BigDecimal(value.toString())));
         }
    	 if(("securityCost".equals(propName)) && value != null){
             cell.setCellValue(df.format(new BigDecimal(value.toString())));
         }
    	 if(("smv".equals(propName)) && value != null){
             cell.setCellValue(df.format(new BigDecimal(value.toString())));
         }
    }
	
}
