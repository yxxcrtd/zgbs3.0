package com.joyintech.tams.svreport.pbc.assetposition.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.svreport.pbc.assetdebt.dao.*;
import com.joyintech.tams.svreport.pbc.assetdebt.dto.*;
import com.joyintech.tams.svreport.pbc.reportdetail.dto.PbcMonthReportDetailAsset;
import com.joyintech.tams.svreport.pbc.reportdetail.service.PbcMonthReportDetailAssetUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.DictCodeList;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.system.base.entity.SysInstitution;
import com.joyintech.tams.common.svreport.base.service.ChbTypeMappingService;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.ChbCcyConvertUtil;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.common.util.DeadlineUtil;
import com.joyintech.tams.report.interf.common.constant.DateUtil;
import com.joyintech.tams.report.system.instregbankcode.dao.SysInstRegBankCode001Mapper;
import com.joyintech.tams.report.system.instregbankcode.dto.SysInstRegBankCode001Dto;
import com.joyintech.tams.svreport.interf.chb.cprdbalance.dao.ChbCprodBalanceDtoMapper;
import com.joyintech.tams.svreport.pbc.assetposition.dao.PbcMonthAssetPositionMapper;
import com.joyintech.tams.svreport.pbc.assetposition.dao.PbcMonthAssetReportMapper;
import com.joyintech.tams.svreport.pbc.assetposition.dto.PbcMonthAssetPositionDto;
import com.joyintech.tams.svreport.pbc.common.constants.PbcMonthReportConstants;
import com.joyintech.tams.svreport.pbc.common.constants.PbcReportProperycode;
import com.joyintech.tams.svreport.pbc.common.constants.PbcReportType;
import com.joyintech.tams.svreport.pbc.prod.dao.PbcMonthPrdPostionMapper;
import com.joyintech.tams.svreport.pbc.prod.dto.PbcMonthPrdPostionDto;
import com.joyintech.tams.svreport.pbc.report.dao.PbcMonthReportMapper;
import com.joyintech.tams.svreport.pbc.report.dao.PbcReportTemplateMapper;
import com.joyintech.tams.svreport.pbc.report.dto.PbcMonthReport;
import com.joyintech.tams.svreport.pbc.report.dto.PbcReportTemplateDto;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.tools.collectors.JoyinCollectors;

/**
 * 功能:人行报表-资产头寸service
 * @since 1.6.0
 * @version 1.6.0
 * @作者：weiyuqiang
 * @创建时间：2019年3月11日
 * @修改记录：generateMonthReportDebtterm
 */
@SuppressWarnings("rawtypes")
@Service
@Transactional(rollbackFor = Exception.class)
public class PbcMonthAssetReportService  extends BaseService{

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 资产头寸mapper
     */
    @Autowired
    private PbcMonthAssetReportMapper pbcMonthAssetReportMapper;

    /**
     * 产品头寸mapper
     */
    @Autowired
    private PbcMonthPrdPostionMapper pbcMonthPrdPostionMapper;

    /**
     * 2-1人行报表-资产负债端
     */
    @Autowired
    private PbcReportAssetDebtDetailMapper pbcReportAssetDebtDetailMapper;

    /**
     * 资产头寸mapper
     */
    @Autowired
    private PbcReportTemplateMapper pbcReportTemplateMapper;

    /**
     * 主表mapper
     */
    @Autowired
    private PbcMonthReportMapper pbcMonthReportMapper;

    /**
     * 映射service
     */
    @Autowired
    private ChbTypeMappingService chbTypeMappingService;

//    @Autowired
//    private  AssetInfoDtoMapper assetInfoDtoMapper;

    @Autowired
    private  PbcReportAssetIncomeDetailMapper pbcReportAssetIncomeDetailMapper;

    @Autowired
    private  PbcReportCompanyDetailMapper pbcReportCompanyDetailMapper;

    @Autowired
    private  PbcReportAreaDetailMapper pbcReportAreaDetailMapper;

    @Autowired
    private  PbcReportDebtTermDetailMapper pbcReportDebtTermDetailMapper;

    @Autowired
    private PbcReportAssetConfigurationMapper pbcReportAssetConfigurationMapper;

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private SqlSessionTemplate template;

    @Autowired
    private ChbCprodBalanceDtoMapper chbCprodBalanceDtoMapper;

    @Autowired
    private ChbLoanDetailInfoMapper chbLoanDetailInfoMapper;

    @Autowired
    private  PbcMonthAssetPositionMapper pbcMonthAssetPositionMapper;//add by jyl 19.11.25

    @Autowired
    private PbcLoanDetailInfoSubMapper pbcLoanDetailInfoSubMapper;

    @Autowired
    private PbcCapitalTrustDetailSubMapper pbcCapitalTrustDetailSubMapper;

    @Autowired
    private ChbLoanDetailContractDtoMapper chbLoanDetailContractDtoMapper;
    @Autowired
    private SysInstRegBankCode001Mapper sysInstRegBankCode001Mapper;

    @Autowired
    private PbcMonthReportDetailAssetUtil pbcMonthReportDetailAssetUtil;

    private List<PbcMonthReportDetailAsset> assetDetailList;


    /*
     * 默认股权种类
     */
    private static final String DEFAULT_STOCK_TYPE = "A7220";

    // 企业规模映射map
    private static final Map<String, String> ENTERPRISE_SCALE_MAP = new HashMap<String, String>(1 << 4);
    static {
        // CS01-大型 统计到【大型企业】项中
        ENTERPRISE_SCALE_MAP.put("CS01", "02");
        // CS02-中型 统计到【中型企业】项中
        ENTERPRISE_SCALE_MAP.put("CS02", "01");
        // CS03-小型 统计到【小型企业】项中
        ENTERPRISE_SCALE_MAP.put("CS03", "03");
        // CS04-微型 统计到【微型企业】项中
        ENTERPRISE_SCALE_MAP.put("CS04", "04");
        // CS05-其他 统计到【中型企业】项中
        ENTERPRISE_SCALE_MAP.put("CS05", "01");
    }
    /**
     * 功能:生成资产负债端报表
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年3月12日
     * @param
     * @param
     * @param isEdit 是否是修改的数据
     * void
     */
    public void generateMonthAssetReport(Date generateDate,List<String> prodList,boolean isEdit){
        assetDetailList = new ArrayList<>();
        logger.debug("资产报表生成(2_1\2_4\2_5\3_3\4_1):----开始------" +  df.format(new Date()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(generateDate);
//        //每月1日生成上月的数据
//        if ( calendar.get(Calendar.DATE)!=1 ) {
//            return;
//        }

        //联社模式获取
        String modelType = null;
        if(CommonUtil.isBAmodel()){
            modelType = DictCodeList.权限模式_联社模式.getValue();
        }

        //根据模式编码是否为空来确认是否需要获取机构编码
        List<PbcMonthAssetPositionDto> posAssetList =
                pbcMonthAssetReportMapper.selectByDateAndProdid(prodList,DateUtil.getLastDayOfLayMonth(generateDate),modelType);
        // 权益类从产品头寸取值
        String bankCode=SvrDataUtil.getSysParam(SVReportGlobalConstant.CUSTOMER_BANK_CODE);
        List<PbcMonthPrdPostionDto> posProdList;

        //根据模式编码是否为空来确认是否需要获取机构编码
        if(StringUtil.equals(bankCode, "001")){
            posProdList =
                    pbcMonthPrdPostionMapper.selectByDatePsbc(DateUtil.getLastDayOfLayMonth(generateDate),modelType,prodList);
        }else{
            posProdList =
                    pbcMonthPrdPostionMapper.selectByDate(DateUtil.getLastDayOfLayMonth(generateDate),modelType,prodList);
        }

//        if (!CollectionUtils.isEmpty(posAssetList)
//                || !CollectionUtils.isEmpty(posProdList)){
        // 表2-1 资管产品资产负债统计



        Map<String,BigDecimal> qtfz = generateMonthAssetDebtReport(posAssetList,posProdList,generateDate,
                isEdit,PbcReportType.PBC_REPORT_2_1,false,null);
//        // 表3-3 资管产品资产负债剩余期限
        generateMonthReportDebtterm(posAssetList,posProdList,generateDate,qtfz,isEdit);
//        }
//        if (!CollectionUtils.isEmpty(posAssetList)){
        List<ChbAssetInfo> assetList = queryAssetInfoBatch();
        Map<String,ChbAssetInfo> assetMap = assetList.stream().collect(Collectors.toMap(ChbAssetInfo::getAssetCode, a->a,(k1,k2)->k1));

        // 资产收益权贷款明细基本信息
        List<ChbLoanDetailInfo> chbLoanDetailInfoList = chbLoanDetailInfoMapper.queryChbLoanDetailBatch();
        Map<String, ChbLoanDetailInfo> chbLoandMap = chbLoanDetailInfoList.stream().collect(Collectors.toMap(ChbLoanDetailInfo::getAssetCode, a->a,(k1,k2)->k1));
        // 2-2 除回购和拆借外贷款分行业、企业规模统计
//        generateMonthReportCompanyWithoutDebt(posAssetList,generateDate,assetMap,isEdit);
        generateMonthReportCompanyWithoutDebtEx(posAssetList,generateDate,chbLoandMap,isEdit);
        // 2-3 除回购和拆借外贷款分地区统计统计
//        generateMonthReportAreaWithoutDebt(posAssetList,generateDate,assetMap,isEdit);
        generateMonthReportAreaWithoutDebtEx(posAssetList,generateDate,chbLoandMap,isEdit);
        // 表2-4 资产收益权按基础资产投向分类统计表
//        generateMonthAssetReportInCome(posAssetList,generateDate,assetMap,isEdit);
        generateMonthAssetReportInComeEx(posAssetList,generateDate,chbLoandMap,isEdit);
        //为明细信息赋值对应的贷款属性
        PbcMonthReportDetailAssetUtil.updateLoan(assetDetailList,chbLoandMap);


        // 表2-5 企业债券分行业、企业规模统计
        generateMonthReportCompanyBond(posAssetList,generateDate,assetMap,isEdit);
//        }

        //表4-1 资管产品资产配置情况
        generateMonthReportAssetConfiguration(posAssetList,posProdList,generateDate, isEdit,PbcReportType.PBC_REPORT_4_1);

        pbcMonthReportDetailAssetUtil.insertBatch(assetDetailList,generateDate);

        logger.debug("资产报表生成(2_1\2_4\2_5\3_3\4_1):----结束------" +  df.format(new Date()));
    }

    /**
     * 功能:表4-1 资管产品资产配置情况
     * @since 1.0.0
     * @version 1.0.0
     * @author：zhengxiaolian 2022年01月11日
     * @param
     */
    public void generateMonthReportAssetConfiguration(List<PbcMonthAssetPositionDto> posAssetList,List<PbcMonthPrdPostionDto> posProdList,
                                                      Date generateDate,boolean isEdit,String reportType){
        // 生成月度报表主表数据
        PbcMonthReport pbcReport = genMonthReportByReporttype(PbcReportType.PBC_REPORT_4_1,generateDate,isEdit,false);

        Map<String, PbcReportAssetConfigurationDto> result = genAssetMonthReportemplate(generateDate,  reportType, 57,pbcReport.getReportuuid());

        Map<String,Map<String,PbcMonthAssetPositionDto>> deptMap = new HashMap();

        // 汇率取值开关
        String source = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_EXCHANGE_SOURCE_PBC);

        // 汇率map,可重复使用
        Map<String, BigDecimal> rateMap = new HashMap<>();

        Map<String,Date> massageMap = new HashMap<>();

        massageMap.put("endDate", pbcReport.getEnddate());

        for(PbcMonthAssetPositionDto pos :posAssetList){
            if(null!=pos.getInstitutionCode()&&null==deptMap.get(pos.getInstitutionCode())){
                Map<String,PbcMonthAssetPositionDto> singDeptMap = genAssetMonthReportemplate(generateDate,  reportType, 57,pbcReport.getReportuuid());
                singDeptMap.putAll(singDeptMap);
                deptMap.put(pos.getInstitutionCode(), singDeptMap);
            }
            if(PbcMonthReportConstants.PBC_ASSETTYPE2_S03.equals(pos.getAssetType2())){
                //政策性金融债券 统计人行资产二级分类为“政策性金融债券”的资产
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H11000_ZCXJRZQ, reportType, rateMap, source, null, massageMap);

//                if(DictCodeListExt2.资产评级_AAAPLUS.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_AAA.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_AAADAWN.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_AAPLUS.getValue().equals(pos.getRating())
//                ){
//                    //h11100	    AA+（含）以上	统计“政策性金融债券”中评级结果为AAA+、AAA-、AAA、AA+的债券
//                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H11100_AAJYS, reportType, rateMap, source, null, massageMap);
//                }else if(DictCodeListExt2.资产评级_AA.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_AADAWN.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_APLUS.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_A.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_ADAWN.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_ADAWN1.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_ADAWN2.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_ADAWN3.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_BBBPLUS.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_BBB.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_BBBDAWN.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_BBPLUS.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_BB.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_BBDAWN.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_BPLUS.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_B.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_BDAWN.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_CCCPLUS.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_CCC.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_CCCDAWN.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_CCPLUS.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_CC.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_CCDAWN.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_CPLUS.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_C.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_CDAWN.getValue().equals(pos.getRating())
//                        ||DictCodeListExt2.资产评级_D.getValue().equals(pos.getRating())
//                ){
//                    //h11200	    AA+以下	统计“政策性金融债券”中评级结果为AA、AA-、A+、A、A-、BBB+、BBB、BBB-、BB+、BB、BB-、B+、B、B-、CCC+、CCC、CCC-、CC+、CC、CC-、C+、C、C-、D的债券
//                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H11200_AAJYX, reportType, rateMap, source, null, massageMap);
//                }else{
                //h11300	    无评级	统计“政策性金融债券”中没有评级的债券
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H11300_WPJ, reportType, rateMap, source, null, massageMap);
//                }
            }

            if(PbcMonthReportConstants.PBC_ASSETTYPE2_S04.equals(pos.getAssetType2())){
                //h12000	  其他金融债券	统计人行资产二级分类为“其他金融债券”的资产
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H12000_QTJRZQ, reportType, rateMap, source, null, massageMap);

                if(DictCodeListExt2.资产评级_AAAPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_AAA.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_AAADAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_AAPLUS.getValue().equals(pos.getRating())
                ){
                    //h11100	    AA+（含）以上	统计“其他金融债券”中评级结果为AAA+、AAA-、AAA、AA+的债券
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H12100_AAJYS, reportType, rateMap, source, null, massageMap);
                }else if(DictCodeListExt2.资产评级_AA.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_AADAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_APLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_A.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_ADAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_ADAWN1.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_ADAWN2.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_ADAWN3.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BBBPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BBB.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BBBDAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BBPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BB.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BBDAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_B.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BDAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CCCPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CCC.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CCCDAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CCPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CC.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CCDAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_C.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CDAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_D.getValue().equals(pos.getRating())
                ){
                    //h11200	    AA+以下	统计“其他金融债券”中评级结果为AA、AA-、A+、A、A-、BBB+、BBB、BBB-、BB+、BB、BB-、B+、B、B-、CCC+、CCC、CCC-、CC+、CC、CC-、C+、C、C-、D的债券
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H12200_AAJYX, reportType, rateMap, source, null, massageMap);
                }else{
                    //h11300	    无评级	统计“其他金融债券”中没有评级的债券
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H12300_WPJ, reportType, rateMap, source, null, massageMap);
                }
            }
            if(PbcMonthReportConstants.PBC_ASSETTYPE2_S05.equals(pos.getAssetType2())){
                //h13000	  企业债券	统计人行资产二级分类为=“企业债券”的资产
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H13000_QYZQ, reportType, rateMap, source, null, massageMap);

                if(DictCodeListExt2.资产评级_AAAPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_AAA.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_AAADAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_AAPLUS.getValue().equals(pos.getRating())
                ){
                    //h13100	    AA+（含）以上	统计“企业债券”中评级结果为AAA+、AAA-、AAA、AA+的债券
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H13100_AAJYS, reportType, rateMap, source, null, massageMap);
                }else if(DictCodeListExt2.资产评级_AA.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_AADAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_APLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_A.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_ADAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_ADAWN1.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_ADAWN2.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_ADAWN3.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BBBPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BBB.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BBBDAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BBPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BB.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BBDAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_B.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BDAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CCCPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CCC.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CCCDAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CCPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CC.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CCDAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_C.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CDAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_D.getValue().equals(pos.getRating())
                ){
                    //h13200	    AA+以下	统计“企业债券”中评级结果为AA、AA-、A+、A、A-、BBB+、BBB、BBB-、BB+、BB、BB-、B+、B、B-、CCC+、CCC、CCC-、CC+、CC、CC-、C+、C、C-、D的债券
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H13200_AAJYX, reportType, rateMap, source, null, massageMap);
                }else{
                    //h13300	    无评级	统计“企业债券”中没有评级的债券
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H13300_WPJ, reportType, rateMap, source, null, massageMap);
                }
            }
            if(PbcMonthReportConstants.PBC_ASSETTYPE2_S07.equals(pos.getAssetType2())){
                //h14000	  资产支持证券	统计人行资产二级分类为“资产支持证券”的资产
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H14000_ZCZCZQ, reportType, rateMap, source, null, massageMap);

                if(DictCodeListExt2.资产评级_AAAPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_AAA.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_AAADAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_AAPLUS.getValue().equals(pos.getRating())
                ){
                    //h14100	    AA+（含）以上	统计“资产支持证券”中评级结果为AAA+、AAA-、AAA、AA+的债券
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H14100_AAJYS, reportType, rateMap, source, null, massageMap);
                }else if(DictCodeListExt2.资产评级_AA.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_AADAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_APLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_A.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_ADAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_ADAWN1.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_ADAWN2.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_ADAWN3.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BBBPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BBB.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BBBDAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BBPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BB.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BBDAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_B.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_BDAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CCCPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CCC.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CCCDAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CCPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CC.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CCDAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CPLUS.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_C.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_CDAWN.getValue().equals(pos.getRating())
                        ||DictCodeListExt2.资产评级_D.getValue().equals(pos.getRating())
                ){
                    //h14200	    AA+以下	统计“资产支持证券”中评级结果为AA、AA-、A+、A、A-、BBB+、BBB、BBB-、BB+、BB、BB-、B+、B、B-、CCC+、CCC、CCC-、CC+、CC、CC-、C+、C、C-、D的债券
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H14200_AAJYX, reportType, rateMap, source, null, massageMap);
                }else{
                    //h14300	    无评级	统计“资产支持证券”中没有评级的债券
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H14300_WPJ, reportType, rateMap, source, null, massageMap);
                }
            }

            if(PbcMonthReportConstants.PBC_ASSETTYPE3_Y01.equals(pos.getAssetType3())){
                //h21000	  其中：企业债	统计人行资产三级分类为“企业债”的资产
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H21000_QZQYZ, reportType, rateMap, source, null, massageMap);
            }

            if(PbcMonthReportConstants.PBC_ASSETTYPE3_Y02.equals(pos.getAssetType3())){
                //h22000	       公司债	统计人行人行资产三级分类为“公司债”的资产
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H22000_GSZ, reportType, rateMap, source, null, massageMap);
            }

            if(PbcMonthReportConstants.PBC_ASSETTYPE3_Y03.equals(pos.getAssetType3())){
                //h23000	       非金融企业债务融资工具	统计人行资产三级分类为“企业债券融资工具”的资产
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H23000_FJRQYZW, reportType, rateMap, source, null, massageMap);
            }

            if(!PbcMonthReportConstants.PBC_ASSETTYPE1_P14.equals(pos.getAssetType1())){
                //3和4过滤减值
                //要过滤应收账款和应付账款
            if(!(PbcMonthReportConstants.PBC_ASSETTYPE1_P11.equals(pos.getAssetType1())||PbcMonthReportConstants.PBC_ASSETTYPE1_P12.equals(pos.getAssetType1()))) {
                //h30000	三、银行资本补充工具合计	=“h31000优先股+h32000永续债（债性）+h33000永续债（股性）+h34000二级资本债”
                if (DictCodeList.是否_是.getValue().equals(pos.getBankSecPrefer())) {
                    //h31000	  其中：优先股	统计资产要素信息中 是否商业银行优先股=“是”的资产
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H31000_QZYXG, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H30000_YHZBBCGJ, reportType, rateMap, source, null, massageMap);
                }

                if (DictCodeList.是否_是.getValue().equals(pos.getBankSecPerpetual())) {
                    if (DictCodeListExt3.债券性质_债性.getValue().equals(pos.getIsPerpetual())) {
                        //h32000	        永续债（债性）	统计资产要素信息中 是否商业银行永续债=“是”，且债券性质=“债性”的资产
                        setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H32000_YXZZX, reportType, rateMap, source, null, massageMap);
                        setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H30000_YHZBBCGJ, reportType, rateMap, source, null, massageMap);
                    }
                    if (DictCodeListExt3.债券性质_股性.getValue().equals(pos.getIsPerpetual())) {
                        //h33000 永续债（股性）	统计资产要素信息中 是否商业银行永续债=“是”，且债券性质=“股性”的资产
                        setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H33000_YXZGX, reportType, rateMap, source, null, massageMap);
                        setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H30000_YHZBBCGJ, reportType, rateMap, source, null, massageMap);
                    }
                }

                if (DictCodeList.是否_是.getValue().equals(pos.getBankSecCapital())) {
                    //h34000	   二级资本债	统计资产要素信息中 是否商业银行二级资本债=“是”的资产
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H34000_EJZBZ, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H30000_YHZBBCGJ, reportType, rateMap, source, null, massageMap);
                }
            }
            //h40000	四、债券逆回购和同业拆借 	=“h41000住户+h42000广义政府+h43000非金融企业+h44000银行业存款类机构
            // +h45000银行业非存款类金融机构+h46000非银行业金融机构+h47000特定目的载体+h48000境外”
            if(PbcMonthReportConstants.PBC_ASSETTYPE2_R01.equals(pos.getAssetType2())) {
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L01.equals(pos.getAssetType3())) {
                    //h41000	  住户	统计人行资产二级分类为“拆借和回购”，且人行资产三级分类为“住户”　
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H41000_ZH, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H40000_ZQNHGHTYCJ, reportType, rateMap, source, null, massageMap);
                }

                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L02.equals(pos.getAssetType3())) {
                    //h42000	　广义政府	统计人行资产二级分类为“拆借和回购”，且人行资产三级分类为“广义政府”　
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H42000_GYZF, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H40000_ZQNHGHTYCJ, reportType, rateMap, source, null, massageMap);
                }

                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L03.equals(pos.getAssetType3())) {
                    //h43000	　非金融企业  	统计人行资产二级分类为“拆借和回购”，且人行资产三级分类为“非金融企业”　　
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H43000_FJRQY, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H40000_ZQNHGHTYCJ, reportType, rateMap, source, null, massageMap);
                }

                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L04.equals(pos.getAssetType3())) {
                    //h44000	  银行业存款类金融机构	统计人行资产二级分类为“拆借和回购”，且人行资产三级分类为“银行业存款类金融机构”　
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H44000_YHYCKLJRJG, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H40000_ZQNHGHTYCJ, reportType, rateMap, source, null, massageMap);
                }

                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L05.equals(pos.getAssetType3())) {
                    //h45000	　银行业非存款类金融机构	统计人行资产二级分类为“拆借和回购”，且人行资产三级分类为“银行业非存款类金融机构”　
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H45000_YHYFCKLJRJG, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H40000_ZQNHGHTYCJ, reportType, rateMap, source, null, massageMap);
                }

                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L06.equals(pos.getAssetType3())) {
                    //h46000	　非银行业金融机构  	统计人行资产二级分类为“拆借和回购”，且人行资产三级分类为“非银行业金融机构”　　
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H46000_FYHYJRJG, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H40000_ZQNHGHTYCJ, reportType, rateMap, source, null, massageMap);
                }
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L07.equals(pos.getAssetType3())) {
                    //h46100	 其中：中央交易对手方	统计人行资产二级分类为“拆借和回购”，且人行资产三级分类为“非银行业金融机构-中央交易对手”
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H46100_QZZYJYDSF, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H46000_FYHYJRJG, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H40000_ZQNHGHTYCJ, reportType, rateMap, source, null, massageMap);
                }
                //h47000	特定目的载体	=“h47100银行非保本理财+h47200信托公司资管产品+h47300证券公司及其子公司资管产品+h47400基金管理公司及其子公司专户
                // + h47500期货公司及其子公司资管产品+h47600保险资管产品+h47700金融资产投资公司资管产品+h47800公募基金+
                // h47900私募机构私募基金+h47a00其他特定目的载体”
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L08.equals(pos.getAssetType3())) {
                    //h47100	银行非保本理财	统计人行资产二级分类为“拆借和回购”，且人行资产三级分类为“银行非保本理财”
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47100_YHFBBLC, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47000_TDMDZT, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H40000_ZQNHGHTYCJ, reportType, rateMap, source, null, massageMap);
                }
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L09.equals(pos.getAssetType3())) {
                    //h47200	信托公司资管产品	统计人行资产二级分类为“拆借和回购”，且人行资产三级分类为“信托公司资管产品”
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47200_XTGSZGCP, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47000_TDMDZT, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H40000_ZQNHGHTYCJ, reportType, rateMap, source, null, massageMap);
                }
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L10.equals(pos.getAssetType3())) {
                    //h47300	证券公司及其子公司资管产品	统计人行资产二级分类为“拆借和回购”，且人行资产三级分类为“证券公司及其子公司资管产品”
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47300_ZQGSJQZGS, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47000_TDMDZT, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H40000_ZQNHGHTYCJ, reportType, rateMap, source, null, massageMap);
                }
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L11.equals(pos.getAssetType3())) {
                    //h47400	基金管理公司及其子公司专户	统计人行资产二级分类为“拆借和回购”，且人行资产三级分类为“基金管理公司及其子公司专户”
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47400_JJGLGSJQZGS, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47000_TDMDZT, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H40000_ZQNHGHTYCJ, reportType, rateMap, source, null, massageMap);
                }
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L12.equals(pos.getAssetType3())) {
                    //h47500	期货公司及其子公司资管产品	统计人行资产二级分类为“拆借和回购”，且人行资产三级分类为“期货公司及其子公司资管产品”
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47500_QHGLGSJQZGS, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47000_TDMDZT, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H40000_ZQNHGHTYCJ, reportType, rateMap, source, null, massageMap);
                }
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L13.equals(pos.getAssetType3())) {
                    //h47600	保险资管产品	统计人行资产二级分类为“拆借和回购”，且人行资产三级分类为“保险资管产品”
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47600_BXZGCP, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47000_TDMDZT, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H40000_ZQNHGHTYCJ, reportType, rateMap, source, null, massageMap);
                }
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L14.equals(pos.getAssetType3())) {
                    //h47700	金融资产投资公司资管产品	统计人行资产二级分类为“拆借和回购”，且人行资产三级分类为“金融资产投资公司资管产品”
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47700_JRZCTZGSJQZGS, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47000_TDMDZT, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H40000_ZQNHGHTYCJ, reportType, rateMap, source, null, massageMap);
                }
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L15.equals(pos.getAssetType3())) {
                    //h47800	公募基金	统计人行资产二级分类为“拆借和回购”，且人行资产三级分类为“公募基金”
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47800_GMJJ, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47000_TDMDZT, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H40000_ZQNHGHTYCJ, reportType, rateMap, source, null, massageMap);
                }
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L16.equals(pos.getAssetType3())) {
                    //h47900	私募机构私募基金	统计人行资产二级分类为“拆借和回购”，且人行资产三级分类为“私募机构私募基金”
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47900_SMJGSMJJ, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47000_TDMDZT, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H40000_ZQNHGHTYCJ, reportType, rateMap, source, null, massageMap);
                }
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L99.equals(pos.getAssetType3())) {
                    //h47a00	其他特定目的载体	统计人行资产二级分类为“拆借和回购”，且人行资产三级分类为“其他特定目的载体”
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47A00_QTTDMDZT, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H47000_TDMDZT, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H40000_ZQNHGHTYCJ, reportType, rateMap, source, null, massageMap);
                }
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L98.equals(pos.getAssetType3())) {
                    //h48000	　境外	统计人行资产二级分类为“拆借和回购”，且人行资产三级分类为“境外”
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H48000_JW, reportType, rateMap, source, null, massageMap);
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H40000_ZQNHGHTYCJ, reportType, rateMap, source, null, massageMap);
                }
            }
            }
            //“存款”（h51000）+“可转让存单”（h52000）+“债务证券”（h53000）+“除回购和拆借外贷款”（h54000）+“回购和拆借”（h55000）
            // +“资产收益权”（h56000）+“股权及特定目的载体份额”（h57000）+“金融衍生品”（h58000）+“应收账款”（h59000）+“其他”（h5a000）
            if(PbcMonthReportConstants.PBC_ASSETTYPE3_Z01.equals(pos.getAssetType3())){
                //h51000	  存款	取上游系统“130209存放同业减值准备”科目余额非逾期资产
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H51000_CK, reportType, rateMap, source, null, massageMap);
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H50000_ZCJZZB, reportType, rateMap, source, null, massageMap);
            }
            //h52000	  可转让存单	置空
            if(PbcMonthReportConstants.PBC_ASSETTYPE3_Z03.equals(pos.getAssetType3())){
                //h53000	  债务证券	取上游系统二级科目为“130201摊余成本类债券减值准备”科目，
                // 对应的所以三级科目且三级对应四级科目对应明细资产的人行资产一级分类为“债务证券”非逾期资产
                //头寸的时候做过映射处理，这里直接取
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H53000_ZWZQ, reportType, rateMap, source, null, massageMap);
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H50000_ZCJZZB, reportType, rateMap, source, null, massageMap);
            }
            if(PbcMonthReportConstants.PBC_ASSETTYPE3_Z04.equals(pos.getAssetType3())){
                //h54000	  除回购和拆借外贷款	取上游系统二级科目为“130201摊余成本类债券减值准备”或“130203 摊余成本类计划类资产减值准备”科目，
                // 对应的所以三级科目且三级对应且四级科目对应明细资产的人行资产二级分类为“除回购和拆借外贷款” 非逾期资产
                //头寸的时候做过映射处理，这里直接取
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H54000_CHGHCJWDK, reportType, rateMap, source, null, massageMap);
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H50000_ZCJZZB, reportType, rateMap, source, null, massageMap);
            }

            if(PbcMonthReportConstants.PBC_ASSETTYPE3_Z05.equals(pos.getAssetType3())){
                //h55000	  回购和拆借	取上游系统“130207买入返售资产减值准备”+“130210拆放同业减值准备”科目余额非逾期资产
                //头寸的时候做过映射处理，这里直接取
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H55000_HGHCJ, reportType, rateMap, source, null, massageMap);
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H50000_ZCJZZB, reportType, rateMap, source, null, massageMap);
            }

            if(PbcMonthReportConstants.PBC_ASSETTYPE3_Z06.equals(pos.getAssetType3())){
                //h56000	  资产收益权	取上游系统二级科目为 “130203 摊余成本类计划类资产减值准备”科目，
                // 对应的所以三级科目且三级对应且四级科目对应明细资产的人行资产一级分类为“资产收益权”非逾期资产
                //头寸的时候做过映射处理，这里直接取
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H56000_ZCSYQ, reportType, rateMap, source, null, massageMap);
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H50000_ZCJZZB, reportType, rateMap, source, null, massageMap);
            }

            if(PbcMonthReportConstants.PBC_ASSETTYPE3_Z07.equals(pos.getAssetType3())){
                //h57000	  股权及特定目的载体份额	取上游系统二级科目为 “130203 摊余成本类计划类资产减值准备”科目，对应的所以三级科目且三级对应且四级科目对应明细资产的人行资产一级分类为“股权及特定目的载体份额”的非逾期资产
                //头寸的时候做过映射处理，这里直接取
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H57000_GQJTDMDDZTFE, reportType, rateMap, source, null, massageMap);
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H50000_ZCJZZB, reportType, rateMap, source, null, massageMap);
            }
            //h58000	  金融衍生品	置空
            if(PbcMonthReportConstants.PBC_ASSETTYPE3_Z09.equals(pos.getAssetType3())){
                //h59000	  应收账款	取上游系统二级科目为 “130203 摊余成本类计划类资产减值准备”、“130201 摊余成本类债券减值准备”科目、“130207 买入返售资产减值准备”、“130209 存放同业减值准备”、“130210 拆放同业减值准备”科目，对应的所以三级科目且三级对应且四级科目对应明细资产的已逾期
                //头寸的时候做过映射处理，这里直接取
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H59000_YSZK, reportType, rateMap, source, null, massageMap);
                setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_H50000_ZCJZZB, reportType, rateMap, source, null, massageMap);
            }
        }

        //修改数据组装及入库公共方法
        buildSumValAndInster(result, null, pbcReport, !CollectionUtils.isEmpty(posAssetList),false,null,null,null);
        //循环分支机构的数据组装及入库
        for (Map.Entry<String, Map<String, PbcMonthAssetPositionDto>> dtoMap : deptMap.entrySet()) {
            buildSumValAndInster(dtoMap.getValue(), dtoMap.getKey(), pbcReport, !CollectionUtils.isEmpty(posAssetList),false,null,null,null);
        }
    }




    /**
     *  模板组装 根据模板对应数据以及表路径获取对应数据
     * @param generateDate
     * @param
     * @param reportType
     * @param
     * @param size
     * @param uuid
     * @return
     */
    private Map genAssetMonthReportemplate(Date generateDate,String reportType,int size,String uuid){
        Map result = new HashMap();
        // 查询产品月报需要统计的项
        List<PbcReportTemplateDto> listKey = pbcReportTemplateMapper.selectByReporttype(reportType,
                DictCodeListExt1.维护数据状态_有效.getValue());
        if ( listKey.size() != size || listKey.size() == 0)  {
            logger.error("月报模板配置错误，请检查");
            return null;
        }

        for ( PbcReportTemplateDto k:listKey ) {

            //2-1模板使用类型
            if(PbcReportType.PBC_REPORT_2_1.equals(reportType)||PbcReportType.PBC_REPORT_ONEPRD_ASSETDEBT.equals(reportType)){
                PbcReportAssetDebtDetailDto detail = new PbcReportAssetDebtDetailDto();
                detail.setDetailuuid(IDGenerator.generate(36));
                detail.setReporttype(reportType);
                detail.setPropertycode(k.getPropertycode());
                detail.setReportuuid(uuid);
                detail.setCny(BigDecimal.ZERO);
                detail.setEurToCny(BigDecimal.ZERO);
                detail.setUsdToCny(BigDecimal.ZERO);
                detail.setGbpToCny(BigDecimal.ZERO);
                detail.setTotal(BigDecimal.ZERO);
                detail.setJpyToCny(BigDecimal.ZERO);
                detail.setOthToCny(BigDecimal.ZERO);
                result.put(k.getPropertycode(),detail);
            }
            //3-3模板使用类型
            if(PbcReportType.PBC_REPORT_3_3.equals(reportType)){
                PbcReportDebtTermDetail detail = new PbcReportDebtTermDetail();
                detail.setDetailuuid(IDGenerator.generate(36));
                detail.setReporttype(PbcReportType.PBC_REPORT_3_3);
                detail.setPropertycode(k.getPropertycode());
                detail.setGrouptype(k.getGrouptype());
                detail.setReportuuid(uuid);
                detail.setDebtInstrument(BigDecimal.ZERO);
                detail.setStockRight(BigDecimal.ZERO);
                detail.setSpecialShare(BigDecimal.ZERO);
                detail.setActualAmt(BigDecimal.ZERO);
                result.put(k.getPropertycode()+k.getGrouptype(),detail);
            }
            //2-2模板使用类型
            if(PbcReportType.PBC_REPORT_2_2.equals(reportType)){
                PbcReportCompanyDetailDto detail = new PbcReportCompanyDetailDto();
                detail.setDetailuuid(IDGenerator.generate(36));
                detail.setReporttype(PbcReportType.PBC_REPORT_2_2);
                detail.setPropertycode(k.getPropertycode());
                detail.setReportuuid(uuid);
                detail.setLargeCompanyAmt(BigDecimal.ZERO);
                detail.setMediumCompanyAmt(BigDecimal.ZERO);
                detail.setMicroCompanyAmt(BigDecimal.ZERO);
                detail.setSmallCompanyAmt(BigDecimal.ZERO);
                detail.setSumAmt(BigDecimal.ZERO);
                detail.setOthAmt(BigDecimal.ZERO);
                result.put(k.getPropertycode(),detail);
            }
            //2-3模板使用类型
            if(PbcReportType.PBC_REPORT_2_3.equals(reportType)){
                PbcReportAreaDetailDto detail = new PbcReportAreaDetailDto();
                detail.setDetailuuid(IDGenerator.generate(36));
                detail.setReporttype(PbcReportType.PBC_REPORT_2_3);
                detail.setPropertycode(k.getPropertycode());
                detail.setReportuuid(uuid);
                detail.setBalance(BigDecimal.ZERO);
                result.put(k.getPropertycode(),detail);
            }

            else if(PbcReportType.PBC_REPORT_2_4.equals(reportType)){
                PbcReportAssetIncomeDetail detail = new PbcReportAssetIncomeDetail();
                detail.setDetailuuid(IDGenerator.generate(36));
                detail.setReporttype(PbcReportType.PBC_REPORT_2_4);
                detail.setPropertycode(k.getPropertycode());
                detail.setReportuuid(uuid);
                detail.setLoanIncomeRight(BigDecimal.ZERO);
                detail.setDebtSecuritiesIncomeRights(BigDecimal.ZERO);
                detail.setReceivableIncomeRights(BigDecimal.ZERO);
                detail.setOtherCreditorRights(BigDecimal.ZERO);
                detail.setStockIncomeRight(BigDecimal.ZERO);
                detail.setOtherEquityIncomeRights(BigDecimal.ZERO);
                detail.setPropertyRights(BigDecimal.ZERO);
                result.put(k.getPropertycode(),detail);

            }
            //2-5模板初始化类
            else if(PbcReportType.PBC_REPORT_2_5.equals(reportType)){
                PbcReportCompanyDetailDto detail = new PbcReportCompanyDetailDto();
                detail.setDetailuuid(IDGenerator.generate(36));
                detail.setReporttype(reportType);
                detail.setPropertycode(k.getPropertycode());
                detail.setReportuuid(uuid);
                detail.setLargeCompanyAmt(BigDecimal.ZERO);
                detail.setMediumCompanyAmt(BigDecimal.ZERO);
                detail.setMicroCompanyAmt(BigDecimal.ZERO);
                detail.setSmallCompanyAmt(BigDecimal.ZERO);
                detail.setOthAmt(BigDecimal.ZERO);
                result.put(k.getPropertycode(),detail);
            }else if (PbcReportType.PBC_REPORT_4_1.equals(reportType)) {
                PbcReportAssetConfigurationDto detail = new PbcReportAssetConfigurationDto();
                detail.setDetailuuid(IDGenerator.generate(36));
                detail.setReporttype(PbcReportType.PBC_REPORT_4_1);
                detail.setPropertycode(k.getPropertycode());
                detail.setReportuuid(uuid);
                detail.setBalance(BigDecimal.ZERO);
                result.put(k.getPropertycode(),detail);
            }
        }

        return result;
    }




    /**
     * 功能:生成表2-1 资管产品资产负债统计
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年3月12日
     * @param posAssetList 资产头寸
     * @param posProdList 产品头寸
     * @param generateDate 生成日
     * @param isEdit 是否为修改
     * @param reportType 报表类型
     * @param isOnePrd 是否单产品
     * @param prodAssetRegList 已登记的数据
     * @return 其他负债值
     * void
     */
    @SuppressWarnings("unused")
    public Map<String,BigDecimal> generateMonthAssetDebtReport(List<PbcMonthAssetPositionDto> posAssetList,List<PbcMonthPrdPostionDto> posProdList,
                                                               Date generateDate,boolean isEdit,String reportType,boolean isOnePrd,List<PbcMonthReport> prodAssetRegList){
        Map<String,BigDecimal> reback = new HashMap<>();

        // 从系统缓存中获取人行月报2-1 倒减逻辑：01 其他负债，02 未分配利润,改变815000其他负债、822000资本公积、823000未分配利润取值逻辑
        String svrMonthSubtractionLogic = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_MONTH_SUBTRACTION_LOGIC);

        // 江苏银行资本公积系统参数开关
        //String svrMonthZbGj = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ZBGJ_PERSON_LOGIC);



        // 生成月度报表主表数据
        PbcMonthReport pbcReport = genMonthReportByReporttype(reportType,generateDate,isEdit,isOnePrd);
        //update by leipeng 20200319  汇总数据提取全量模板
        Map<String,PbcReportAssetDebtDetailDto> result = genAssetMonthReportemplate(generateDate,  reportType, 151,pbcReport.getReportuuid());
        if(null==result){
            return null;
        }
        Map<String,Map<String,PbcReportAssetDebtDetailDto>> deptMap = new HashMap();

        // 汇率取值开关
        String source = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_EXCHANGE_SOURCE_PBC);
        // 根据template定义行编号对应数据
//        PbcReportAssetDebtDetailDto line700000 = result.get(PbcReportProperycode.AMT_700000_ZCHJ);
//        PbcReportAssetDebtDetailDto line710000 = result.get(PbcReportProperycode.AMT_710000_XJ);
//        PbcReportAssetDebtDetailDto line720000 = result.get(PbcReportProperycode.AMT_720000_CK);
//        PbcReportAssetDebtDetailDto line721000 = result.get(PbcReportProperycode.AMT_721000_JNCK);
//        PbcReportAssetDebtDetailDto line722000 = result.get(PbcReportProperycode.AMT_722000_JWCK);
//        PbcReportAssetDebtDetailDto line730000 = result.get(PbcReportProperycode.AMT_730000_KZRCD);
//        PbcReportAssetDebtDetailDto line731000 = result.get(PbcReportProperycode.AMT_731000_TYCD);
//        PbcReportAssetDebtDetailDto line732000 = result.get(PbcReportProperycode.AMT_732000_DECD);
//        PbcReportAssetDebtDetailDto line740000 = result.get(PbcReportProperycode.AMT_740000_ZWZQ);
//        PbcReportAssetDebtDetailDto line741000 = result.get(PbcReportProperycode.AMT_741000_ZFZQ);
//        PbcReportAssetDebtDetailDto line742000 = result.get(PbcReportProperycode.AMT_742000_ZYYXZQ);
//        PbcReportAssetDebtDetailDto line743000 = result.get(PbcReportProperycode.AMT_743000_ZCXJRZQ);
//        PbcReportAssetDebtDetailDto line744000 = result.get(PbcReportProperycode.AMT_744000_QTJRZQ);
//        PbcReportAssetDebtDetailDto line745000 = result.get(PbcReportProperycode.AMT_745000_QYZQ);
//        PbcReportAssetDebtDetailDto line746000 = result.get(PbcReportProperycode.AMT_746000_PJ);
//        PbcReportAssetDebtDetailDto line747000 = result.get(PbcReportProperycode.AMT_747000_ZCZCZQ);
//        PbcReportAssetDebtDetailDto line748000 = result.get(PbcReportProperycode.AMT_748000_JWZQ);
//        PbcReportAssetDebtDetailDto line750000 = result.get(PbcReportProperycode.AMT_750000_DK);
//        PbcReportAssetDebtDetailDto line751000 = result.get(PbcReportProperycode.AMT_751000_CHGHCJWDK);
//        PbcReportAssetDebtDetailDto line752000 = result.get(PbcReportProperycode.AMT_752000_HGHCJ);
//        PbcReportAssetDebtDetailDto line752100 = result.get(PbcReportProperycode.AMT_752100_ZH);
//        PbcReportAssetDebtDetailDto line752200 = result.get(PbcReportProperycode.AMT_752200_GYZF);
//        PbcReportAssetDebtDetailDto line752300 = result.get(PbcReportProperycode.AMT_752300_FJRQY);
//        PbcReportAssetDebtDetailDto line752400 = result.get(PbcReportProperycode.AMT_752400_YXYCKLJRJG);
//        PbcReportAssetDebtDetailDto line752500 = result.get(PbcReportProperycode.AMT_752500_YXYFCKLJRJG);
//        PbcReportAssetDebtDetailDto line752600 = result.get(PbcReportProperycode.AMT_752600_FYXYJRJG);
//        PbcReportAssetDebtDetailDto line752610 = result.get(PbcReportProperycode.AMT_752610_QZZYJYDSF);
//        PbcReportAssetDebtDetailDto line752700 = result.get(PbcReportProperycode.AMT_752700_TDMDZT);
//        PbcReportAssetDebtDetailDto line752710 = result.get(PbcReportProperycode.AMT_752710_YXFBBLC);
//        PbcReportAssetDebtDetailDto line752720 = result.get(PbcReportProperycode.AMT_752720_XTGSZGCP);
//        PbcReportAssetDebtDetailDto line752730 = result.get(PbcReportProperycode.AMT_752730_ZQGSJQZGSZGCP);
//        PbcReportAssetDebtDetailDto line752740 = result.get(PbcReportProperycode.AMT_752740_JJGLGSJQZGSZH);
//        PbcReportAssetDebtDetailDto line752750 = result.get(PbcReportProperycode.AMT_752750_QHGSJQZGSZGCP);
//        PbcReportAssetDebtDetailDto line752760 = result.get(PbcReportProperycode.AMT_752760_BXZGCP);
//        PbcReportAssetDebtDetailDto line752770 = result.get(PbcReportProperycode.AMT_752770_JRZCTZGSZGCP);
//        PbcReportAssetDebtDetailDto line752780 = result.get(PbcReportProperycode.AMT_752780_GMJJ);
//        PbcReportAssetDebtDetailDto line752790 = result.get(PbcReportProperycode.AMT_752790_SMJGSMJJ);
//        PbcReportAssetDebtDetailDto line7527a0 = result.get(PbcReportProperycode.AMT_7527A0_QTTDMDZT);
//        PbcReportAssetDebtDetailDto line752800 = result.get(PbcReportProperycode.AMT_752800_JW);
//        PbcReportAssetDebtDetailDto line760000 = result.get(PbcReportProperycode.AMT_760000_ZCSYQ);
//        PbcReportAssetDebtDetailDto line761000 = result.get(PbcReportProperycode.AMT_761000_DKSYQ);
//        PbcReportAssetDebtDetailDto line762000 = result.get(PbcReportProperycode.AMT_762000_ZWZQSYQ);
//        PbcReportAssetDebtDetailDto line763000 = result.get(PbcReportProperycode.AMT_763000_YSZKSYQ);
//        PbcReportAssetDebtDetailDto line764000 = result.get(PbcReportProperycode.AMT_764000_QTZQSYQ);
//        PbcReportAssetDebtDetailDto line765000 = result.get(PbcReportProperycode.AMT_765000_GPSYQ);
//        PbcReportAssetDebtDetailDto line766000 = result.get(PbcReportProperycode.AMT_766000_QTGQSYQ);
//        PbcReportAssetDebtDetailDto line767000 = result.get(PbcReportProperycode.AMT_767000_WQZCSYQ);
//        PbcReportAssetDebtDetailDto line770000 = result.get(PbcReportProperycode.AMT_770000_GQJTDMDZTFE);
//        PbcReportAssetDebtDetailDto line771000 = result.get(PbcReportProperycode.AMT_771000_GP);
//        PbcReportAssetDebtDetailDto line771100 = result.get(PbcReportProperycode.AMT_771100_FJRQYGP);
//        PbcReportAssetDebtDetailDto line771200 = result.get(PbcReportProperycode.AMT_771200_JRJGGP);
//        PbcReportAssetDebtDetailDto line772000 = result.get(PbcReportProperycode.AMT_772000_TDMDZTFE);
//        PbcReportAssetDebtDetailDto line772100 = result.get(PbcReportProperycode.AMT_772100_YXFBBLC);
//        PbcReportAssetDebtDetailDto line772200 = result.get(PbcReportProperycode.AMT_772200_XTGSZGCP);
//        PbcReportAssetDebtDetailDto line772300 = result.get(PbcReportProperycode.AMT_772300_ZQGSJQZGSZGCP);
//        PbcReportAssetDebtDetailDto line772400 = result.get(PbcReportProperycode.AMT_772400_JJGLGSJQZGSZH);
//        PbcReportAssetDebtDetailDto line772500 = result.get(PbcReportProperycode.AMT_772500_QHGSJQZGSZGCP);
//        PbcReportAssetDebtDetailDto line772600 = result.get(PbcReportProperycode.AMT_772600_BXZGCP);
//        PbcReportAssetDebtDetailDto line772700 = result.get(PbcReportProperycode.AMT_772700_JRZCTZGSZGCP);
//        PbcReportAssetDebtDetailDto line772800 = result.get(PbcReportProperycode.AMT_772800_GMJJ);
//        PbcReportAssetDebtDetailDto line772900 = result.get(PbcReportProperycode.AMT_772900_SMJGSMJJ);
//        PbcReportAssetDebtDetailDto line772a00 = result.get(PbcReportProperycode.AMT_772A00_QTTDMDZT);
//        PbcReportAssetDebtDetailDto line773000 = result.get(PbcReportProperycode.AMT_773000_QTGQ);
//        PbcReportAssetDebtDetailDto line773100 = result.get(PbcReportProperycode.AMT_773100_FJRQYGQ);
//        PbcReportAssetDebtDetailDto line773200 = result.get(PbcReportProperycode.AMT_773200_JRJGGQ);
//        PbcReportAssetDebtDetailDto line774000 = result.get(PbcReportProperycode.AMT_774000_JWGQ);
//        PbcReportAssetDebtDetailDto line780000 = result.get(PbcReportProperycode.AMT_780000_JRYSP);
//        PbcReportAssetDebtDetailDto line781000 = result.get(PbcReportProperycode.AMT_781000_JN);
//        PbcReportAssetDebtDetailDto line782000 = result.get(PbcReportProperycode.AMT_782000_JW);
//        PbcReportAssetDebtDetailDto line790000 = result.get(PbcReportProperycode.AMT_790000_YSZK);
//        PbcReportAssetDebtDetailDto line7a0000 = result.get(PbcReportProperycode.AMT_7A0000_HJ);
//        PbcReportAssetDebtDetailDto line7b0000 = result.get(PbcReportProperycode.AMT_7B0000_FJRZC);
//        PbcReportAssetDebtDetailDto line800000 = result.get(PbcReportProperycode.AMT_800000_FZJQYHJ);
//        PbcReportAssetDebtDetailDto line810000 = result.get(PbcReportProperycode.AMT_810000_FZHJ);
//        PbcReportAssetDebtDetailDto line811000 = result.get(PbcReportProperycode.AMT_811000_DK);
//        PbcReportAssetDebtDetailDto line811100 = result.get(PbcReportProperycode.AMT_811100_CHGHCJWDK);
//        PbcReportAssetDebtDetailDto line811200 = result.get(PbcReportProperycode.AMT_811200_HGHCJ);
//        PbcReportAssetDebtDetailDto line811210 = result.get(PbcReportProperycode.AMT_811210_ZH);
//        PbcReportAssetDebtDetailDto line811220 = result.get(PbcReportProperycode.AMT_811220_GYZF);
//        PbcReportAssetDebtDetailDto line811230 = result.get(PbcReportProperycode.AMT_811230_FJRQY);
//        PbcReportAssetDebtDetailDto line811240 = result.get(PbcReportProperycode.AMT_811240_YXYCKLJRJG);
//        PbcReportAssetDebtDetailDto line811250 = result.get(PbcReportProperycode.AMT_811250_YXYFCKLJRJG);
//        PbcReportAssetDebtDetailDto line811260 = result.get(PbcReportProperycode.AMT_811260_FYXYJRJG);
//        PbcReportAssetDebtDetailDto line811261 = result.get(PbcReportProperycode.AMT_811261_QZZYJYDSF);
//        PbcReportAssetDebtDetailDto line811270 = result.get(PbcReportProperycode.AMT_811270_TDMDZT);
//        PbcReportAssetDebtDetailDto line811271 = result.get(PbcReportProperycode.AMT_811271_YXFBBLC);
//        PbcReportAssetDebtDetailDto line811272 = result.get(PbcReportProperycode.AMT_811272_XTGSZGCP);
//        PbcReportAssetDebtDetailDto line811273 = result.get(PbcReportProperycode.AMT_811273_ZQGSJQZGSZGCP);
//        PbcReportAssetDebtDetailDto line811274 = result.get(PbcReportProperycode.AMT_811274_JJGLGSJQZGSZH);
//        PbcReportAssetDebtDetailDto line811275 = result.get(PbcReportProperycode.AMT_811275_QHGSJQZGSZGCP);
//        PbcReportAssetDebtDetailDto line811276 = result.get(PbcReportProperycode.AMT_811276_BXZGCP);
//        PbcReportAssetDebtDetailDto line811277 = result.get(PbcReportProperycode.AMT_811277_JRZCTZGSZGCP);
//        PbcReportAssetDebtDetailDto line811278 = result.get(PbcReportProperycode.AMT_811278_GMJJ);
//        PbcReportAssetDebtDetailDto line811279 = result.get(PbcReportProperycode.AMT_811279_SMJGSMJJ);
//        PbcReportAssetDebtDetailDto line81127a = result.get(PbcReportProperycode.AMT_81127A_QTTDMDZT);
//        PbcReportAssetDebtDetailDto line811280 = result.get(PbcReportProperycode.AMT_811280_JW);
//        PbcReportAssetDebtDetailDto line812000 = result.get(PbcReportProperycode.AMT_812000_ZCZCZQ);
//        PbcReportAssetDebtDetailDto line813000 = result.get(PbcReportProperycode.AMT_813000_JRYSP);
//        PbcReportAssetDebtDetailDto line813100 = result.get(PbcReportProperycode.AMT_813100_JN);
//        PbcReportAssetDebtDetailDto line813200 = result.get(PbcReportProperycode.AMT_813200_JW);
//        PbcReportAssetDebtDetailDto line814000 = result.get(PbcReportProperycode.AMT_814000_YFZK);
//        PbcReportAssetDebtDetailDto line815000 = result.get(PbcReportProperycode.AMT_815000_QTFZ);
//        PbcReportAssetDebtDetailDto line820000 = result.get(PbcReportProperycode.AMT_820000_QYHJ);
//        PbcReportAssetDebtDetailDto line821000 = result.get(PbcReportProperycode.AMT_821000_SSBJ);
//        PbcReportAssetDebtDetailDto line821100 = result.get(PbcReportProperycode.AMT_821100_YXJ);
//        PbcReportAssetDebtDetailDto line821110 = result.get(PbcReportProperycode.AMT_821110_ZH);
//        PbcReportAssetDebtDetailDto line821120 = result.get(PbcReportProperycode.AMT_821120_GYZF);
//        PbcReportAssetDebtDetailDto line821130 = result.get(PbcReportProperycode.AMT_821130_FJRQY);
//        PbcReportAssetDebtDetailDto line821140 = result.get(PbcReportProperycode.AMT_821140_YXYCKLJRJG);
//        PbcReportAssetDebtDetailDto line821150 = result.get(PbcReportProperycode.AMT_821150_YXYFCKLJRJG);
//        PbcReportAssetDebtDetailDto line821160 = result.get(PbcReportProperycode.AMT_821160_FYXYJRJG);
//        PbcReportAssetDebtDetailDto line821170 = result.get(PbcReportProperycode.AMT_821170_TDMDZT);
//        PbcReportAssetDebtDetailDto line821171 = result.get(PbcReportProperycode.AMT_821171_YXFBBLC);
//        PbcReportAssetDebtDetailDto line821172 = result.get(PbcReportProperycode.AMT_821172_XTGSZGCP);
//        PbcReportAssetDebtDetailDto line821173 = result.get(PbcReportProperycode.AMT_821173_ZQGSJQZGSZGCP);
//        PbcReportAssetDebtDetailDto line821174 = result.get(PbcReportProperycode.AMT_821174_JJGLGSJQZGSZH);
//        PbcReportAssetDebtDetailDto line821175 = result.get(PbcReportProperycode.AMT_821175_QHGSJQZGSZGCP);
//        PbcReportAssetDebtDetailDto line821176 = result.get(PbcReportProperycode.AMT_821176_BXZGCP);
//        PbcReportAssetDebtDetailDto line821177 = result.get(PbcReportProperycode.AMT_821177_JRZCTZGSZGCP);
//        PbcReportAssetDebtDetailDto line821178 = result.get(PbcReportProperycode.AMT_821178_GMJJ);
//        PbcReportAssetDebtDetailDto line821179 = result.get(PbcReportProperycode.AMT_821179_SMJGSMJJ);
//        PbcReportAssetDebtDetailDto line82117a = result.get(PbcReportProperycode.AMT_82117A_QTTDMDZT);
//        PbcReportAssetDebtDetailDto line821180 = result.get(PbcReportProperycode.AMT_821180_JW);
//        PbcReportAssetDebtDetailDto line821200 = result.get(PbcReportProperycode.AMT_821200_LHJ);
//        PbcReportAssetDebtDetailDto line821210 = result.get(PbcReportProperycode.AMT_821210_ZH);
//        PbcReportAssetDebtDetailDto line821220 = result.get(PbcReportProperycode.AMT_821220_GYZF);
//        PbcReportAssetDebtDetailDto line821230 = result.get(PbcReportProperycode.AMT_821230_FJRQY);
//        PbcReportAssetDebtDetailDto line821240 = result.get(PbcReportProperycode.AMT_821240_YXYCKLJRJG);
//        PbcReportAssetDebtDetailDto line821250 = result.get(PbcReportProperycode.AMT_821250_YXYFCKLJRJG);
//        PbcReportAssetDebtDetailDto line821260 = result.get(PbcReportProperycode.AMT_821260_FYXYJRJG);
//        PbcReportAssetDebtDetailDto line821270 = result.get(PbcReportProperycode.AMT_821270_TDMDZT);
//        PbcReportAssetDebtDetailDto line821271 = result.get(PbcReportProperycode.AMT_821271_YXFBBLC);
//        PbcReportAssetDebtDetailDto line821272 = result.get(PbcReportProperycode.AMT_821272_XTGSZGCP);
//        PbcReportAssetDebtDetailDto line821273 = result.get(PbcReportProperycode.AMT_821273_ZQGSJQZGSZGCP);
//        PbcReportAssetDebtDetailDto line821274 = result.get(PbcReportProperycode.AMT_821274_JJGLGSJQZGSZH);
//        PbcReportAssetDebtDetailDto line821275 = result.get(PbcReportProperycode.AMT_821275_QHGSJQZGSZGCP);
//        PbcReportAssetDebtDetailDto line821276 = result.get(PbcReportProperycode.AMT_821276_BXZGCP);
//        PbcReportAssetDebtDetailDto line821277 = result.get(PbcReportProperycode.AMT_821277_JRZCTZGSZGCP);
//        PbcReportAssetDebtDetailDto line821278 = result.get(PbcReportProperycode.AMT_821278_GMJJ);
//        PbcReportAssetDebtDetailDto line821279 = result.get(PbcReportProperycode.AMT_821279_SMJGSMJJ);
//        PbcReportAssetDebtDetailDto line82127a = result.get(PbcReportProperycode.AMT_82127A_QTTDMDZT);
//        PbcReportAssetDebtDetailDto line821280 = result.get(PbcReportProperycode.AMT_821280_JW);
//        PbcReportAssetDebtDetailDto line822000 = result.get(PbcReportProperycode.AMT_822000_ZBGJ);
//        PbcReportAssetDebtDetailDto line823000 = result.get(PbcReportProperycode.AMT_823000_WFPLR);
//        PbcReportAssetDebtDetailDto line000001 = result.get(PbcReportProperycode.AMT_000001_FFXRBNZCYEBWBHJ);
//        PbcReportAssetDebtDetailDto line000002 = result.get(PbcReportProperycode.AMT_000002_FXRBNJRZCYEBWBHJ);

        // 汇率map,可重复使用
        Map<String, BigDecimal> rateMap = new HashMap<>();

        Map<String,Date> massageMap = new HashMap<>();

        massageMap.put("endDate", pbcReport.getEnddate());

        for(PbcMonthAssetPositionDto pos :posAssetList){
            // 头寸-科目余额是否为资本公积金、未分配理论
            String assetType1ForBalance = "";
            if (PbcMonthReportConstants.POSITION_TYPE_KMYE.equals(pos.getPositionType())){
                assetType1ForBalance = chbTypeMappingService.getMappingValue(PbcMonthReportConstants.MAPPINGCODE_SUBJECTNO,
                        pos.getAssetCode());
            }



//			// add by leipeng 20200319 获取所有部门对应信息,并列出单独的表数据
            if(null!=pos.getInstitutionCode()&&null==deptMap.get(pos.getInstitutionCode())){
                Map<String,PbcReportAssetDebtDetailDto> singDeptMap = genAssetMonthReportemplate(generateDate,  reportType, 146,pbcReport.getReportuuid());
                singDeptMap.putAll(singDeptMap);
                deptMap.put(pos.getInstitutionCode(), singDeptMap);
            }

            // 资产合计统计头寸类型为资产：01 (境内外资产为03或者04代表为负债 add by gaojian 2019-07-30)
            if (PbcMonthReportConstants.POSITION_TYPE_ZC.equals(pos.getPositionType())
                    && !PbcMonthReportConstants.PBC_ASSETTYPE3_J03.equals(pos.getAssetType3())
                    && !PbcMonthReportConstants.PBC_ASSETTYPE3_J04.equals(pos.getAssetType3())) {
                // 710000 现金:资产三级分类=X01 现金
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                //202112人行升级修存款取值逻辑
//                if (PbcMonthReportConstants.PBC_ASSETTYPE3_X01.equals(pos.getAssetType3())) {
//
//                	setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_710000_XJ, reportType, rateMap, source, null, massageMap);
//
//                	setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);
////                    // 710000 现金
////                    setAssetDebtDetailValue(line710000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
////                    // 700000 资产合计
////                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 720000 存款 = 境内存款 + 境外存款
                // 721000 境内存款：资产三级分类=C01 境内存款
//                if (PbcMonthReportConstants.PBC_ASSETTYPE3_C01.equals(pos.getAssetType3())) {
//
//                	setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_721000_JNCK, reportType, rateMap, source, null, massageMap);
//
//                	setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_720000_CK, reportType, rateMap, source, null, massageMap);
//
//                	setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);
//
//                    // 721000 境内存款
////                    setAssetDebtDetailValue(line721000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 720000 存款
////                    setAssetDebtDetailValue(line720000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
////                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 720000 存款 = 境内存款 + 境外存款
                // 722000 境外存款：资产三级分类=C02 境外存款
//                if (PbcMonthReportConstants.PBC_ASSETTYPE3_C02.equals(pos.getAssetType3())) {
//
//                	setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_722000_JWCK, reportType, rateMap, source, null, massageMap);
//
//                	setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_720000_CK, reportType, rateMap, source, null, massageMap);
//
//                	setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);
//                    // 722000 境外存款
////                    setAssetDebtDetailValue(line722000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 720000 存款
////                    setAssetDebtDetailValue(line720000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
////                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产-“（减）资产减值准备”（AK000）
                // 720000 存款 = 境内存款 + 境外存款
                // 721100 境内存款= D01 活期存款+ D02 定期和其他存款
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_D01.equals(pos.getAssetType3())) {
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_721100_HQCK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_721000_JNCK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_720000_CK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产-“（减）资产减值准备”（AK000）
                // 720000 存款 = 境内存款 + 境外存款
                // 721000 境内存款= D01 活期存款+ D02 定期和其他存款
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_D02.equals(pos.getAssetType3())) {
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_721200_DQHQTCK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_721000_JNCK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_720000_CK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产 -“（减）资产减值准备”（AK000）
                // 720000 存款 = 境内存款 + 境外存款
                // 722000 境内存款= D03 活期存款+ D04 定期和其他存款
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_D03.equals(pos.getAssetType3())) {
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_722100_HQCK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_722000_JWCK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_720000_CK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产-“（减）资产减值准备”（AK000）
                // 720000 存款 = 境内存款 + 境外存款
                // 722000 境内存款= D03 活期存款+ D04 定期和其他存款
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_D04.equals(pos.getAssetType3())) {
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_722200_DQHQTCK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_722000_JWCK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_720000_CK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);
                }

                if (PbcMonthReportConstants.PBC_ASSETTYPE1_P14.equals(pos.getAssetType1())) {
                    //减值
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_7K0000_ZCJZZB, reportType, rateMap, source, null, massageMap);

                    setAssetDebtDetailValueSubtract(result.get(PbcReportProperycode.AMT_700000_ZCHJ),pos.getCcy(),pos.getAmt(),rateMap,(Date) massageMap.get("endDate"),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 730000 可转让存单 = 731000 同业存单+732000 大额存单
                // 731000 同业存单:资产三级分类=K01 同业存单
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_K01.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_731000_TYCD, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_730000_KZRCD, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 731000 同业存单
//                    setAssetDebtDetailValue(line731000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 730000 可转让存单
//                    setAssetDebtDetailValue(line730000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 730000 可转让存单 = 731000 同业存单+732000 大额存单
                // 732000 大额存单 :资产三级分类=K02 大额存单
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_K02.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_732000_DECD, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_730000_KZRCD, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 732000 大额存单
//                    setAssetDebtDetailValue(line732000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 730000 可转让存单
//                    setAssetDebtDetailValue(line730000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 740000 债务证券 = 741000 政府债券+742000中央银行债券
                // +743000政策性金融债券+744000其他金融债券+745000企业债券
                // +746000 票据+747000资产支持证券+748000境外债券
                // 741000 政府债券 :资产三级分类=S01 政府债券
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_S01.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_741000_ZFZQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_740000_ZWZQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 741000 政府债券
//                    setAssetDebtDetailValue(line741000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 740000 债务证券
//                    setAssetDebtDetailValue(line740000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 740000 债务证券 = 741000 政府债券+742000中央银行债券
                // +743000政策性金融债券+744000其他金融债券+745000企业债券
                // +746000 票据+747000资产支持证券+748000境外债券
                // 742000 中央银行债券:资产三级分类=S02 中央银行债券
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_S02.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_742000_ZYYXZQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_740000_ZWZQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 742000 中央银行债券
//                    setAssetDebtDetailValue(line742000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 740000 债务证券
//                    setAssetDebtDetailValue(line740000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 740000 债务证券 = 741000 政府债券+742000中央银行债券
                // +743000政策性金融债券+744000其他金融债券+745000企业债券
                // +746000 票据+747000资产支持证券+748000境外债券
                // 743000 政策性金融债券:资产三级分类=S03 政策性金融债券
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_S03.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_743000_ZCXJRZQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_740000_ZWZQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 743000 政策性金融债券
//                    setAssetDebtDetailValue(line743000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 740000 债务证券
//                    setAssetDebtDetailValue(line740000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 740000 债务证券 = 741000 政府债券+742000中央银行债券
                // +743000政策性金融债券+744000其他金融债券+745000企业债券
                // +746000 票据+747000资产支持证券+748000境外债券
                // 744000 其他金融债券:资产三级分类=S04 其他金融债券
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_S04.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_744000_QTJRZQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_740000_ZWZQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 744000 其他金融债券
//                    setAssetDebtDetailValue(line744000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 740000 债务证券
//                    setAssetDebtDetailValue(line740000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 740000 债务证券 = 741000 政府债券+742000中央银行债券
                // +743000政策性金融债券+744000其他金融债券+745000企业债券
                // +746000 票据+747000资产支持证券+748000境外债券
                // 745000 企业债券:资产三级分类=S05 企业债券
                if (PbcMonthReportConstants.PBC_ASSETTYPE2_S05.equals(pos.getAssetType2())) {


                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_745000_QYZQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_740000_ZWZQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 745000 企业债券
//                    setAssetDebtDetailValue(line745000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 740000 债务证券
//                    setAssetDebtDetailValue(line740000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 740000 债务证券 = 741000 政府债券+742000中央银行债券
                // +743000政策性金融债券+744000其他金融债券+745000企业债券
                // +746000 票据+747000资产支持证券+748000境外债券
                // 746000 票据:资产三级分类=S06 票据
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_S06.equals(pos.getAssetType3())) {


                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_746000_PJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_740000_ZWZQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 746000 票据
//                    setAssetDebtDetailValue(line746000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 740000 债务证券
//                    setAssetDebtDetailValue(line740000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 740000 债务证券 = 741000 政府债券+742000中央银行债券
                // +743000政策性金融债券+744000其他金融债券+745000企业债券
                // +746000 票据+747000资产支持证券+748000境外债券
                // 747000 资产支持证券:资产三级分类=S07 资产支持证券
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_S07.equals(pos.getAssetType3())) {


                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_747000_ZCZCZQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_740000_ZWZQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 747000 资产支持证券
//                    setAssetDebtDetailValue(line747000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 740000 债务证券
//                    setAssetDebtDetailValue(line740000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 740000 债务证券 = 741000 政府债券+742000中央银行债券
                // +743000政策性金融债券+744000其他金融债券+745000企业债券
                // +746000 票据+747000资产支持证券+748000境外债券
                // 748000 境外债券:资产三级分类=S08 境外债券
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_S08.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_748000_JWZQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_740000_ZWZQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 748000 境外债券
//                    setAssetDebtDetailValue(line748000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 740000 债务证券
//                    setAssetDebtDetailValue(line740000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 751000 除回购和拆借外贷款:资产三级分类=R02 其他
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_R02.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_751000_CHGHCJWDK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 751000 除回购和拆借外贷款
//                    setAssetDebtDetailValue(line751000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 752000 回购和拆借 = 752100 住户+752200 广义政府+752300 非金融企业+752400
                // 银行业存款类金融机构+752500 银行业非存款类金融机构
                // +752600 非银行业金融机构+752700 特定目的载体+752800 境外
                // 752100 住户:资产三级分类=L01 住户
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L01.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752100_ZH, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752000_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 752100 住户
//                    setAssetDebtDetailValue(line752100, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 752000 回购和拆借
//                    setAssetDebtDetailValue(line752000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 752000 回购和拆借 = 752100 住户+752200 广义政府+752300 非金融企业+752400
                // 银行业存款类金融机构+752500 银行业非存款类金融机构
                // +752600 非银行业金融机构+752700 特定目的载体+752800 境外
                // 752200 广义政府:资产三级分类=L02 广义政府:
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L02.equals(pos.getAssetType3())) {


                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752200_GYZF, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752000_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 752200 广义政府
//                    setAssetDebtDetailValue(line752200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 752000 回购和拆借
//                    setAssetDebtDetailValue(line752000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 752000 回购和拆借 = 752100 住户+752200 广义政府+752300 非金融企业+752400
                // 银行业存款类金融机构+752500 银行业非存款类金融机构
                // +752600 非银行业金融机构+752700 特定目的载体+752800 境外
                // 752300 非金融企业:=资产三级分类=L03 非金融企业
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L03.equals(pos.getAssetType3())) {


                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752300_FJRQY, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752000_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 752300 非金融企业
//                    setAssetDebtDetailValue(line752300, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 752000 回购和拆借
//                    setAssetDebtDetailValue(line752000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 752000 回购和拆借 = 752100 住户+752200 广义政府+752300 非金融企业+752400
                // 银行业存款类金融机构+752500 银行业非存款类金融机构
                // +752600 非银行业金融机构+752700 特定目的载体+752800 境外
                // 752400 银行业存款类金融机构:=资产三级分类=L04 银行业存款类金融机构
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L04.equals(pos.getAssetType3())) {


                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752400_YXYCKLJRJG, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752000_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);


                    // 752400 银行业存款类金融机构
//                    setAssetDebtDetailValue(line752400, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 752000 回购和拆借
//                    setAssetDebtDetailValue(line752000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 752000 回购和拆借 = 752100 住户+752200 广义政府+752300 非金融企业+752400
                // 银行业存款类金融机构+752500 银行业非存款类金融机构
                // +752600 非银行业金融机构+752700 特定目的载体+752800 境外
                // 752500 银行业非存款类金融机构:资产三级分类=L05 银行业非存款类金融机构
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L05.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752500_YXYFCKLJRJG, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752000_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 752500 银行业非存款类金融机构
//                    setAssetDebtDetailValue(line752500, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 752000 回购和拆借
//                    setAssetDebtDetailValue(line752000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 752000 回购和拆借 = 752100 住户+752200 广义政府+752300 非金融企业+752400
                // 银行业存款类金融机构+752500 银行业非存款类金融机构
                // +752600 非银行业金融机构+752700 特定目的载体+752800 境外
                // 752600 非银行业金融机构 = 752610 其中：中央交易对手方 + 资产三级分类=L06 非银行业金融机构-非中央交易对手
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L06.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752600_FYXYJRJG, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752000_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 752600 非银行业金融机构
//                    setAssetDebtDetailValue(line752600, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 752000 回购和拆借
//                    setAssetDebtDetailValue(line752000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 752000 回购和拆借 = 752100 住户+752200 广义政府+752300 非金融企业+752400
                // 银行业存款类金融机构+752500 银行业非存款类金融机构
                // +752600 非银行业金融机构+752700 特定目的载体+752800 境外
                // 752600 非银行业金融机构 = 752610 其中：中央交易对手方 + 资产三级分类=L06 非银行业金融机构-非中央交易对手
                // 752610 其中：中央交易对手方:资产三级分类=L07 非银行业金融机构-中央交易对手
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L07.equals(pos.getAssetType3())) {


                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752610_QZZYJYDSF, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752600_FYXYJRJG, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752000_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 752610 其中：中央交易对手方
//                    setAssetDebtDetailValue(line752610, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 752600 非银行业金融机构
//                    setAssetDebtDetailValue(line752600, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 752000 回购和拆借
//                    setAssetDebtDetailValue(line752000,pos.getCcy(),pos.getAmt(),rateMap,generateDate,source);
                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000,pos.getCcy(),pos.getAmt(),rateMap,generateDate,source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000,pos.getCcy(),pos.getAmt(),rateMap,generateDate,source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 752000 回购和拆借 = 752100 住户+752200 广义政府+752300 非金融企业+752400
                // 银行业存款类金融机构+752500 银行业非存款类金融机构
                // +752600 非银行业金融机构+752700 特定目的载体+752800 境外
                // 752700 特定目的载体 =752710银行非保本理财+752720信托公司资管产品
                // +752730证券公司及其子公司资管产品+752740基金管理公司及其子公司专户
                // +752750期货公司及其子公司资管产品+752760保险资管产品+752770金融资产投资公司资管产品
                // +752780公募基金+752790私募机构私募基金+7527a0其他特定目的载体
                // 752710 银行非保本理财:资产三级分类=L08 银行非保本理财
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L08.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752710_YXFBBLC, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752700_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752000_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 752710 银行非保本理财
//                    setAssetDebtDetailValue(line752710, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 752700 特定目的载体
//                    setAssetDebtDetailValue(line752700, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 752000 回购和拆借
//                    setAssetDebtDetailValue(line752000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 752000 回购和拆借 = 752100 住户+752200 广义政府+752300 非金融企业+752400
                // 银行业存款类金融机构+752500 银行业非存款类金融机构
                // +752600 非银行业金融机构+752700 特定目的载体+752800 境外
                // 752700 特定目的载体 =752710银行非保本理财+752720信托公司资管产品
                // +752730证券公司及其子公司资管产品+752740基金管理公司及其子公司专户
                // +752750期货公司及其子公司资管产品+752760保险资管产品+752770金融资产投资公司资管产品
                // +752780公募基金+752790私募机构私募基金+7527a0其他特定目的载体
                // 752720 信托公司资管产品:资产三级分类=L09 信托公司资管产品
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L09.equals(pos.getAssetType3())) {


                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752720_XTGSZGCP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752700_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752000_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 752720 信托公司资管产品
//                    setAssetDebtDetailValue(line752720, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 752700 特定目的载体
//                    setAssetDebtDetailValue(line752700, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 752000 回购和拆借
//                    setAssetDebtDetailValue(line752000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 752000 回购和拆借 = 752100 住户+752200 广义政府+752300 非金融企业+752400
                // 银行业存款类金融机构+752500 银行业非存款类金融机构
                // +752600 非银行业金融机构+752700 特定目的载体+752800 境外
                // 752700 特定目的载体 =752710银行非保本理财+752720信托公司资管产品
                // +752730证券公司及其子公司资管产品+752740基金管理公司及其子公司专户
                // +752750期货公司及其子公司资管产品+752760保险资管产品+752770金融资产投资公司资管产品
                // +752780公募基金+752790私募机构私募基金+7527a0其他特定目的载体
                // 752730 证券公司及其子公司资管产品:资产三级分类=L10 证券公司及其子公司资管产品
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L10.equals(pos.getAssetType3())) {


                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752730_ZQGSJQZGSZGCP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752700_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752000_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 752730 证券公司及其子公司资管产品
//                    setAssetDebtDetailValue(line752730, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 752700 特定目的载体
//                    setAssetDebtDetailValue(line752700, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 752000 回购和拆借
//                    setAssetDebtDetailValue(line752000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 752000 回购和拆借 = 752100 住户+752200 广义政府+752300 非金融企业+752400
                // 银行业存款类金融机构+752500 银行业非存款类金融机构
                // +752600 非银行业金融机构+752700 特定目的载体+752800 境外
                // 752700 特定目的载体 =752710银行非保本理财+752720信托公司资管产品
                // +752730证券公司及其子公司资管产品+752740基金管理公司及其子公司专户
                // +752750期货公司及其子公司资管产品+752760保险资管产品+752770金融资产投资公司资管产品
                // +752780公募基金+752790私募机构私募基金+7527a0其他特定目的载体
                // 752740 基金管理公司及其子公司专户:资产三级分类=L11 基金管理公司及其子公司专户
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L11.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752740_JJGLGSJQZGSZH, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752700_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752000_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 752740 基金管理公司及其子公司专户
//                    setAssetDebtDetailValue(line752740, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 752700 特定目的载体
//                    setAssetDebtDetailValue(line752700, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 752000 回购和拆借
//                    setAssetDebtDetailValue(line752000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 752000 回购和拆借 = 752100 住户+752200 广义政府+752300 非金融企业+752400
                // 银行业存款类金融机构+752500 银行业非存款类金融机构
                // +752600 非银行业金融机构+752700 特定目的载体+752800 境外
                // 752700 特定目的载体 =752710银行非保本理财+752720信托公司资管产品
                // +752730证券公司及其子公司资管产品+752740基金管理公司及其子公司专户
                // +752750期货公司及其子公司资管产品+752760保险资管产品+752770金融资产投资公司资管产品
                // +752780公募基金+752790私募机构私募基金+7527a0其他特定目的载体
                // 752750 期货公司及其子公司资管产品:资产三级分类=L12 期货公司及其子公司资管产品
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L12.equals(pos.getAssetType3())) {


                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752750_QHGSJQZGSZGCP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752700_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752000_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 752750 期货公司及其子公司资管产品
//                    setAssetDebtDetailValue(line752750, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 752700 特定目的载体
//                    setAssetDebtDetailValue(line752700, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 752000 回购和拆借
//                    setAssetDebtDetailValue(line752000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 752000 回购和拆借 = 752100 住户+752200 广义政府+752300 非金融企业+752400
                // 银行业存款类金融机构+752500 银行业非存款类金融机构
                // +752600 非银行业金融机构+752700 特定目的载体+752800 境外
                // 752700 特定目的载体 =752710银行非保本理财+752720信托公司资管产品
                // +752730证券公司及其子公司资管产品+752740基金管理公司及其子公司专户
                // +752750期货公司及其子公司资管产品+752760保险资管产品+752770金融资产投资公司资管产品
                // +752780公募基金+752790私募机构私募基金+7527a0其他特定目的载体
                // 752760 保险资管产品:资产三级分类=L13 保险资管产品
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L13.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752760_BXZGCP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752700_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752000_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 752760 保险资管产品
//                    setAssetDebtDetailValue(line752760, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 752700 特定目的载体
//                    setAssetDebtDetailValue(line752700, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 752000 回购和拆借
//                    setAssetDebtDetailValue(line752000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 752000 回购和拆借 = 752100 住户+752200 广义政府+752300 非金融企业+752400
                // 银行业存款类金融机构+752500 银行业非存款类金融机构
                // +752600 非银行业金融机构+752700 特定目的载体+752800 境外
                // 752700 特定目的载体 =752710银行非保本理财+752720信托公司资管产品
                // +752730证券公司及其子公司资管产品+752740基金管理公司及其子公司专户
                // +752750期货公司及其子公司资管产品+752760保险资管产品+752770金融资产投资公司资管产品
                // +752780公募基金+752790私募机构私募基金+7527a0其他特定目的载体
                // 752770 金融资产投资公司资管产品:资产三级分类=L14 金融资产投资公司资管产品
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L14.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752770_JRZCTZGSZGCP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752700_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752000_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 752770 金融资产投资公司资管产品
//                    setAssetDebtDetailValue(line752770, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 752700 特定目的载体
//                    setAssetDebtDetailValue(line752700, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 752000 回购和拆借
//                    setAssetDebtDetailValue(line752000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 752000 回购和拆借 = 752100 住户+752200 广义政府+752300 非金融企业+752400
                // 银行业存款类金融机构+752500 银行业非存款类金融机构
                // +752600 非银行业金融机构+752700 特定目的载体+752800 境外
                // 752700 特定目的载体 =752710银行非保本理财+752720信托公司资管产品
                // +752730证券公司及其子公司资管产品+752740基金管理公司及其子公司专户
                // +752750期货公司及其子公司资管产品+752760保险资管产品+752770金融资产投资公司资管产品
                // +752780公募基金+752790私募机构私募基金+7527a0其他特定目的载体
                // 752780 公募基金:资产三级分类=L15 公募基金
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L15.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752780_GMJJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752700_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752000_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 752780 公募基金
//                    setAssetDebtDetailValue(line752780, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 752700 特定目的载体
//                    setAssetDebtDetailValue(line752700, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 752000 回购和拆借
//                    setAssetDebtDetailValue(line752000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 752000 回购和拆借 = 752100 住户+752200 广义政府+752300 非金融企业+752400
                // 银行业存款类金融机构+752500 银行业非存款类金融机构
                // +752600 非银行业金融机构+752700 特定目的载体+752800 境外
                // 752700 特定目的载体 =752710银行非保本理财+752720信托公司资管产品
                // +752730证券公司及其子公司资管产品+752740基金管理公司及其子公司专户
                // +752750期货公司及其子公司资管产品+752760保险资管产品+752770金融资产投资公司资管产品
                // +752780公募基金+752790私募机构私募基金+7527a0其他特定目的载体
                // 752790 私募机构私募基金:资产三级分类=L16 私募机构私募基金
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L16.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752790_SMJGSMJJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752700_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752000_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 752790 私募机构私募基金
//                    setAssetDebtDetailValue(line752790, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 752700 特定目的载体
//                    setAssetDebtDetailValue(line752700, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 752000 回购和拆借
//                    setAssetDebtDetailValue(line752000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 752000 回购和拆借 = 752100 住户+752200 广义政府+752300 非金融企业+752400
                // 银行业存款类金融机构+752500 银行业非存款类金融机构
                // +752600 非银行业金融机构+752700 特定目的载体+752800 境外
                // 752700 特定目的载体 =752710银行非保本理财+752720信托公司资管产品
                // +752730证券公司及其子公司资管产品+752740基金管理公司及其子公司专户
                // +752750期货公司及其子公司资管产品+752760保险资管产品+752770金融资产投资公司资管产品
                // +752780公募基金+752790私募机构私募基金+7527a0其他特定目的载体
                // 7527a0 其他特定目的载体:资产三级分类=L17 其他特定目的载体
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L99.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_7527A0_QTTDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752700_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752000_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 7527a0 其他特定目的载体
//                    setAssetDebtDetailValue(line7527a0, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 752700 特定目的载体
//                    setAssetDebtDetailValue(line752700, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 752000 回购和拆借
//                    setAssetDebtDetailValue(line752000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 750000 贷款 = 751000除回购和拆借外贷款+752000回购和拆借
                // 752000 回购和拆借 = 752100 住户+752200 广义政府+752300 非金融企业+752400
                // 银行业存款类金融机构+752500 银行业非存款类金融机构
                // +752600 非银行业金融机构+752700 特定目的载体+752800 境外
                // 752800 境外:资产三级分类=L18 境外
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L98.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752800_JW, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_752000_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_750000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 752800 境外
//                    setAssetDebtDetailValue(line752800, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 752000 回购和拆借
//                    setAssetDebtDetailValue(line752000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 750000 贷款
//                    setAssetDebtDetailValue(line750000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 760000 资产收益权 = 761000 贷款收益权+762000 债务证券收益权+763000 应收账款收益权
                // +764000其他债权收益权+765000 股票收益权+766000 其他股权收益权
                // +767000 物权资产收益权
                // 761000 贷款收益权:资产三级分类=Q01 贷款收益权
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_Q01.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_761000_DKSYQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_760000_ZCSYQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 761000 贷款收益权
//                    setAssetDebtDetailValue(line761000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 760000 资产收益权
//                    setAssetDebtDetailValue(line760000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 760000 资产收益权 = 761000 贷款收益权+762000 债务证券收益权+763000 应收账款收益权
                // +764000其他债权收益权+765000 股票收益权+766000 其他股权收益权
                // +767000 物权资产收益权
                // 762000 债务证券收益权:资产三级分类=Q02 债务证券收益权
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_Q02.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_762000_ZWZQSYQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_760000_ZCSYQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 762000 债务证券收益权
//                    setAssetDebtDetailValue(line762000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 760000 资产收益权
//                    setAssetDebtDetailValue(line760000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 760000 资产收益权 = 761000 贷款收益权+762000 债务证券收益权+763000 应收账款收益权
                // +764000其他债权收益权+765000 股票收益权+766000 其他股权收益权
                // +767000 物权资产收益权
                // 763000 应收账款收益权:资产三级分类=Q03 应收账款收益权
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_Q03.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_763000_YSZKSYQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_760000_ZCSYQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 763000 应收账款收益权
//                    setAssetDebtDetailValue(line763000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 760000 资产收益权
//                    setAssetDebtDetailValue(line760000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 760000 资产收益权 = 761000 贷款收益权+762000 债务证券收益权+763000 应收账款收益权
                // +764000其他债权收益权+765000 股票收益权+766000 其他股权收益权
                // +767000 物权资产收益权
                // 764000 其他债权收益权:资产三级分类=Q04 其他债权收益权
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_Q04.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_764000_QTZQSYQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_760000_ZCSYQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 764000 其他债权收益权
//                    setAssetDebtDetailValue(line764000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 760000 资产收益权
//                    setAssetDebtDetailValue(line760000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 760000 资产收益权 = 761000 贷款收益权+762000 债务证券收益权+763000 应收账款收益权
                // +764000其他债权收益权+765000 股票收益权+766000 其他股权收益权
                // +767000 物权资产收益权
                // 765000 股票收益权:资产三级分类=Q05 股票收益权
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_Q05.equals(pos.getAssetType3())) {
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_765000_GPSYQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_760000_ZCSYQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 765000 股票收益权
//                    setAssetDebtDetailValue(line765000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 760000 资产收益权
//                    setAssetDebtDetailValue(line760000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 760000 资产收益权 = 761000 贷款收益权+762000 债务证券收益权+763000 应收账款收益权
                // +764000其他债权收益权+765000 股票收益权+766000 其他股权收益权
                // +767000 物权资产收益权
                // 766000 其他股权收益权:资产三级分类=Q06 其他股权收益权
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_Q06.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_766000_QTGQSYQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_760000_ZCSYQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 766000 其他股权收益权
//                    setAssetDebtDetailValue(line766000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 760000 资产收益权
//                    setAssetDebtDetailValue(line760000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 760000 资产收益权 = 761000 贷款收益权+762000 债务证券收益权+763000 应收账款收益权
                // +764000其他债权收益权+765000 股票收益权+766000 其他股权收益权
                // +767000 物权资产收益权
                // 767000 物权资产收益权:资产三级分类=Q07 物权资产收益权
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_Q07.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_767000_WQZCSYQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_760000_ZCSYQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 767000 物权资产收益权
//                    setAssetDebtDetailValue(line767000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 760000 资产收益权
//                    setAssetDebtDetailValue(line760000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 770000 股权及特定目的载体份额 =
                // 771000股票+772000特定目的载体份额+773000其他股权+774000境外股权
                // 771000 股票 = 771100非金融企业股票+771200金融机构股票
                // 771100 非金融企业股票:资产三级分类=G01 非金融企业股票
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_G01.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_771100_FJRQYGP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_771000_GP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_770000_GQJTDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 771100 非金融企业股票
//                    setAssetDebtDetailValue(line771100, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 771000 股票
//                    setAssetDebtDetailValue(line771000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 770000 股权及特定目的载体份额
//                    setAssetDebtDetailValue(line770000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 770000 股权及特定目的载体份额 =
                // 771000股票+772000特定目的载体份额+773000其他股权+774000境外股权
                // 771000 股票 = 771100非金融企业股票+771200金融机构股票
                // 771200 金融机构股票:资产三级分类=G02 金融机构股票
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_G02.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_771200_JRJGGP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_771000_GP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_770000_GQJTDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 771200 金融机构股票
//                    setAssetDebtDetailValue(line771200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 771000 股票
//                    setAssetDebtDetailValue(line771000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 770000 股权及特定目的载体份额
//                    setAssetDebtDetailValue(line770000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 770000 股权及特定目的载体份额 =
                // 771000股票+772000特定目的载体份额+773000其他股权+774000境外股权
                // 772000 特定目的载体份额 =
                // 772100银行非保本理财+772200信托公司资管产品+772300证券公司及其子公司资管产品
                // +772400 基金管理公司及其子公司专户+772500期货公司及其子公司资管产品+772600保险资管产品
                // +772700金融资产投资公司资管产品+772800公募基金+772900私募机构私募基金+772a00其他特定目的载体
                // 772100 银行非保本理财:资产三级分类=M01 银行非保本理财
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_M01.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772100_YXFBBLC, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772000_TDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_770000_GQJTDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 772100 银行非保本理财
//                    setAssetDebtDetailValue(line772100, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 772000 特定目的载体份额
//                    setAssetDebtDetailValue(line772000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 770000 股权及特定目的载体份额
//                    setAssetDebtDetailValue(line770000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 770000 股权及特定目的载体份额 =
                // 771000股票+772000特定目的载体份额+773000其他股权+774000境外股权
                // 772000 特定目的载体份额 =
                // 772100银行非保本理财+772200信托公司资管产品+772300证券公司及其子公司资管产品
                // +772400 基金管理公司及其子公司专户+772500期货公司及其子公司资管产品+772600保险资管产品
                // +772700金融资产投资公司资管产品+772800公募基金+772900私募机构私募基金+772a00其他特定目的载体
                // 772200 信托公司资管产品:资产三级分类=M02 信托公司资管产品
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_M02.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772200_XTGSZGCP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772000_TDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_770000_GQJTDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 772200 信托公司资管产品
//                    setAssetDebtDetailValue(line772200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 772000 特定目的载体份额
//                    setAssetDebtDetailValue(line772000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 770000 股权及特定目的载体份额
//                    setAssetDebtDetailValue(line770000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 770000 股权及特定目的载体份额 =
                // 771000股票+772000特定目的载体份额+773000其他股权+774000境外股权
                // 772000 特定目的载体份额 =
                // 772100银行非保本理财+772200信托公司资管产品+772300证券公司及其子公司资管产品
                // +772400 基金管理公司及其子公司专户+772500期货公司及其子公司资管产品+772600保险资管产品
                // +772700金融资产投资公司资管产品+772800公募基金+772900私募机构私募基金+772a00其他特定目的载体
                // 772300 证券公司及其子公司资管产品:资产三级分类=M03 证券公司及其子公司资管产品
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_M03.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772300_ZQGSJQZGSZGCP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772000_TDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_770000_GQJTDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 772300 证券公司及其子公司资管产品
//                    setAssetDebtDetailValue(line772300, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 772000 特定目的载体份额
//                    setAssetDebtDetailValue(line772000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 770000 股权及特定目的载体份额
//                    setAssetDebtDetailValue(line770000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 770000 股权及特定目的载体份额 =
                // 771000股票+772000特定目的载体份额+773000其他股权+774000境外股权
                // 772000 特定目的载体份额 =
                // 772100银行非保本理财+772200信托公司资管产品+772300证券公司及其子公司资管产品
                // +772400 基金管理公司及其子公司专户+772500期货公司及其子公司资管产品+772600保险资管产品
                // +772700金融资产投资公司资管产品+772800公募基金+772900私募机构私募基金+772a00其他特定目的载体
                // 772400 基金管理公司及其子公司专户:资产三级分类=M04 基金管理公司及其子公司专户
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_M04.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772400_JJGLGSJQZGSZH, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772000_TDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_770000_GQJTDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 772400 基金管理公司及其子公司专户
//                    setAssetDebtDetailValue(line772400, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 772000 特定目的载体份额
//                    setAssetDebtDetailValue(line772000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 770000 股权及特定目的载体份额
//                    setAssetDebtDetailValue(line770000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 770000 股权及特定目的载体份额 =
                // 771000股票+772000特定目的载体份额+773000其他股权+774000境外股权
                // 772000 特定目的载体份额 =
                // 772100银行非保本理财+772200信托公司资管产品+772300证券公司及其子公司资管产品
                // +772400 基金管理公司及其子公司专户+772500期货公司及其子公司资管产品+772600保险资管产品
                // +772700金融资产投资公司资管产品+772800公募基金+772900私募机构私募基金+772a00其他特定目的载体
                // 772500 期货公司及其子公司资管产品 :资产三级分类=M05 期货公司及其子公司资管产品
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_M05.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772500_QHGSJQZGSZGCP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772000_TDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_770000_GQJTDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 772500 期货公司及其子公司资管产品
//                    setAssetDebtDetailValue(line772500, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 772000 特定目的载体份额
//                    setAssetDebtDetailValue(line772000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 770000 股权及特定目的载体份额
//                    setAssetDebtDetailValue(line770000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 770000 股权及特定目的载体份额 =
                // 771000股票+772000特定目的载体份额+773000其他股权+774000境外股权
                // 772000 特定目的载体份额 =
                // 772100银行非保本理财+772200信托公司资管产品+772300证券公司及其子公司资管产品
                // +772400 基金管理公司及其子公司专户+772500期货公司及其子公司资管产品+772600保险资管产品
                // +772700金融资产投资公司资管产品+772800公募基金+772900私募机构私募基金+772a00其他特定目的载体
                // 772600 保险资管产品:资产三级分类=M06 保险资管产品
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_M06.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772600_BXZGCP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772000_TDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_770000_GQJTDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 772600 保险资管产品
//                    setAssetDebtDetailValue(line772600, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 772000 特定目的载体份额
//                    setAssetDebtDetailValue(line772000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 770000 股权及特定目的载体份额
//                    setAssetDebtDetailValue(line770000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 770000 股权及特定目的载体份额 =
                // 771000股票+772000特定目的载体份额+773000其他股权+774000境外股权
                // 772000 特定目的载体份额 =
                // 772100银行非保本理财+772200信托公司资管产品+772300证券公司及其子公司资管产品
                // +772400 基金管理公司及其子公司专户+772500期货公司及其子公司资管产品+772600保险资管产品
                // +772700金融资产投资公司资管产品+772800公募基金+772900私募机构私募基金+772a00其他特定目的载体
                // 772700 金融资产投资公司资管产品:资产三级分类=M07 金融资产投资公司资管产品
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_M07.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772700_JRZCTZGSZGCP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772000_TDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_770000_GQJTDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 772700 金融资产投资公司资管产品
//                    setAssetDebtDetailValue(line772700, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 772000 特定目的载体份额
//                    setAssetDebtDetailValue(line772000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 770000 股权及特定目的载体份额
//                    setAssetDebtDetailValue(line770000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 770000 股权及特定目的载体份额 =
                // 771000股票+772000特定目的载体份额+773000其他股权+774000境外股权
                // 772000 特定目的载体份额 =
                // 772100银行非保本理财+772200信托公司资管产品+772300证券公司及其子公司资管产品
                // +772400 基金管理公司及其子公司专户+772500期货公司及其子公司资管产品+772600保险资管产品
                // +772700金融资产投资公司资管产品+772800公募基金+772900私募机构私募基金+772a00其他特定目的载体
                // 772800 公募基金:资产三级分类=M08 公募基金
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_M08.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772800_GMJJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772000_TDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_770000_GQJTDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 772800 公募基金
//                    setAssetDebtDetailValue(line772800, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 772000 特定目的载体份额
//                    setAssetDebtDetailValue(line772000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 770000 股权及特定目的载体份额
//                    setAssetDebtDetailValue(line770000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 770000 股权及特定目的载体份额 =
                // 771000股票+772000特定目的载体份额+773000其他股权+774000境外股权
                // 772000 特定目的载体份额 =
                // 772100银行非保本理财+772200信托公司资管产品+772300证券公司及其子公司资管产品
                // +772400 基金管理公司及其子公司专户+772500期货公司及其子公司资管产品+772600保险资管产品
                // +772700金融资产投资公司资管产品+772800公募基金+772900私募机构私募基金+772a00其他特定目的载体
                // 772900 私募机构私募基金:资产三级分类=M09 私募机构私募基金
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_M09.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772900_SMJGSMJJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772000_TDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_770000_GQJTDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 772900 私募机构私募基金
//                    setAssetDebtDetailValue(line772900, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 772000 特定目的载体份额
//                    setAssetDebtDetailValue(line772000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 770000 股权及特定目的载体份额
//                    setAssetDebtDetailValue(line770000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 770000 股权及特定目的载体份额 =
                // 771000股票+772000特定目的载体份额+773000其他股权+774000境外股权
                // 772000 特定目的载体份额 =
                // 772100银行非保本理财+772200信托公司资管产品+772300证券公司及其子公司资管产品
                // +772400 基金管理公司及其子公司专户+772500期货公司及其子公司资管产品+772600保险资管产品
                // +772700金融资产投资公司资管产品+772800公募基金+772900私募机构私募基金+772a00其他特定目的载体
                // 772a00 其他特定目的载体:资产三级分类=M10 其他特定目的载体
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_M99.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772A00_QTTDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_772000_TDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_770000_GQJTDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 772a00 其他特定目的载体
//                    setAssetDebtDetailValue(line772a00, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 772000 特定目的载体份额
//                    setAssetDebtDetailValue(line772000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 770000 股权及特定目的载体份额
//                    setAssetDebtDetailValue(line770000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 770000 股权及特定目的载体份额 =
                // 771000股票+772000特定目的载体份额+773000其他股权+774000境外股权
                // 773000 其他股权：773100非金融企业股权+773200金融机构股权
                // 773100 非金融企业股权:资产三级分类=O01 非金融企业股权
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_O01.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_773100_FJRQYGQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_773000_QTGQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_770000_GQJTDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 773100 非金融企业股权
//                    setAssetDebtDetailValue(line773100, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 773000 其他股权
//                    setAssetDebtDetailValue(line773000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 770000 股权及特定目的载体份额
//                    setAssetDebtDetailValue(line770000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 770000 股权及特定目的载体份额 =
                // 771000股票+772000特定目的载体份额+773000其他股权+774000境外股权
                // 773000 其他股权：773100非金融企业股权+773200金融机构股权
                // 773200 金融机构股权:资产三级分类=O02 金融机构股权
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_O02.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_773200_JRJGGQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_773000_QTGQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_770000_GQJTDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 773200 金融机构股权
//                    setAssetDebtDetailValue(line773200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 773000 其他股权
//                    setAssetDebtDetailValue(line773000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 770000 股权及特定目的载体份额
//                    setAssetDebtDetailValue(line770000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 770000 股权及特定目的载体份额 =
                // 771000股票+772000特定目的载体份额+773000其他股权+774000境外股权
                // 774000 境外股权：:资产三级分类=T04
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_T04.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_774000_JWGQ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_770000_GQJTDMDZTFE, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 774000 境外股权
//                    setAssetDebtDetailValue(line774000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 770000 股权及特定目的载体份额
//                    setAssetDebtDetailValue(line770000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 780000 金融衍生品 = 781000境内+782000境外
                // 781000 境内:资产三级分类=J01 境内
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_J01.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_781000_JN, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_780000_JRYSP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 781000 境内
//                    setAssetDebtDetailValue(line781000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 780000 金融衍生品
//                    setAssetDebtDetailValue(line780000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 780000 金融衍生品 = 781000境内+782000境外
                // 782000 境外:资产三级分类=J02 境外
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_J02.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_782000_JW, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_780000_JRYSP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 782000 境外
//                    setAssetDebtDetailValue(line782000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 780000 金融衍生品
//                    setAssetDebtDetailValue(line780000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 790000 应收账款=所有本机构资产（类型=资产）的利息值+资产三级类型=P11金额值
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_P11.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_790000_YSZK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, null, massageMap);

                    // 790000 应收账款
//                    setAssetDebtDetailValue(line790000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
                // 790000 应收账款=所有本机构资产（类型=资产）的利息值+资产三级类型=P11金额值
                if (PbcMonthReportConstants.POSITION_TYPE_ZC.equals(pos.getPositionType())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_790000_YSZK, reportType, rateMap, source, "01", massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_700000_ZCHJ, reportType, rateMap, source, "01", massageMap);

                    // 790000 应收账款
//                    setAssetDebtDetailValue(line790000, pos.getCcy(), pos.getIntAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 700000 资产合计
//                    setAssetDebtDetailValue(line700000, pos.getCcy(), pos.getIntAmt(), rateMap, pbcReport.getEnddate(),source);
                }
//                // 700000 资产合计 = 现金 + 存款 + 可转让存单 + 债务证券
//                // + 贷款 + 资产收益权 + 股权及特定目的载体份额 + 金融衍生品 + 应收账款 + 黄金 + 非金融资产
//                // 7a0000 黄金和7b0000 非金融资产 不统计
//                // 000001和000002 不统计
//                // 7a0000 黄金
//                setAssetDebtDetailValue(line7a0000, pos.getCcy(), BigDecimal.ZERO, rateMap, pbcReport.getEnddate(),source);
//                // 700000 资产合计
//                setAssetDebtDetailValue(line700000, pos.getCcy(), BigDecimal.ZERO, rateMap, pbcReport.getEnddate(),source);
//                // 7b0000 非金融资产
//                setAssetDebtDetailValue(line7b0000, pos.getCcy(), BigDecimal.ZERO, rateMap, pbcReport.getEnddate(),source);
//                // 700000 资产合计
//                setAssetDebtDetailValue(line700000, pos.getCcy(), BigDecimal.ZERO, rateMap, pbcReport.getEnddate(),source);
//                // 000001
//                setAssetDebtDetailValue(line000001, pos.getCcy(), BigDecimal.ZERO, rateMap, pbcReport.getEnddate(),source);
//                // 000002
//                setAssetDebtDetailValue(line000002, pos.getCcy(), BigDecimal.ZERO, rateMap, pbcReport.getEnddate(),source);
            } else if (PbcMonthReportConstants.POSITION_TYPE_FZ.equals(pos.getPositionType())
                    // 境内外资产为03或者04也进负债（add by gaojian 2019-07-30）
                    || PbcMonthReportConstants.PBC_ASSETTYPE3_J03.equals(pos.getAssetType3())
                    || PbcMonthReportConstants.PBC_ASSETTYPE3_J04.equals(pos.getAssetType3())) {// 负债合计
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811100 除回购和拆借外贷款：资产三级类型=R02 其他
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_R02.equals(pos.getAssetType3())) {


                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811100_CHGHCJWDK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 811100 除回购和拆借外贷款
//                    setAssetDebtDetailValue(line811100, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811200 回购和拆借 = 811210住户+ 811220广义政府+811230非金融企业+811240银行业存款类金融机构
                //                  +811250银行业非存款类金融机构+811260非银行业金融机构+811270特定目的载体+811280境外
                // 811210 住户:资产三级分类=L01 住户
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L01.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811210_ZH, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811200_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 811210 住户
//                    setAssetDebtDetailValue(line811210, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811200 回购和拆借
//                    setAssetDebtDetailValue(line811200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811200 回购和拆借 = 811210住户+ 811220广义政府+811230非金融企业+811240银行业存款类金融机构
                //                  +811250银行业非存款类金融机构+811260非银行业金融机构+811270特定目的载体+811280境外
                // 811220 广义政府:资产三级分类=L02 广义政府
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L02.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811220_GYZF, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811200_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 811220 广义政府
//                    setAssetDebtDetailValue(line811220, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811200 回购和拆借
//                    setAssetDebtDetailValue(line811200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811200 回购和拆借 = 811210住户+ 811220广义政府+811230非金融企业+811240银行业存款类金融机构
                //                  +811250银行业非存款类金融机构+811260非银行业金融机构+811270特定目的载体+811280境外
                // 811230 非金融企业:资产三级分类=L03 非金融企业
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L03.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811230_FJRQY, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811200_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 811230 非金融企业
//                    setAssetDebtDetailValue(line811230, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811200 回购和拆借
//                    setAssetDebtDetailValue(line811200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811200 回购和拆借 = 811210住户+ 811220广义政府+811230非金融企业+811240银行业存款类金融机构
                //                  +811250银行业非存款类金融机构+811260非银行业金融机构+811270特定目的载体+811280境外
                // 811240 银行业存款类金融机构:资产三级分类=L04 银行业存款类金融机构
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L04.equals(pos.getAssetType3())) {


                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811240_YXYCKLJRJG, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811200_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 811240 银行业存款类金融机构
//                    setAssetDebtDetailValue(line811240, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811200 回购和拆借
//                    setAssetDebtDetailValue(line811200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811200 回购和拆借 = 811210住户+ 811220广义政府+811230非金融企业+811240银行业存款类金融机构
                //                  +811250银行业非存款类金融机构+811260非银行业金融机构+811270特定目的载体+811280境外
                // 811250 银行业非存款类金融机构:资产三级分类=L05 银行业非存款类金融机构
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L05.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811250_YXYFCKLJRJG, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811200_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 811250 银行业非存款类金融机构
//                    setAssetDebtDetailValue(line811250, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811200 回购和拆借
//                    setAssetDebtDetailValue(line811200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811200 回购和拆借 = 811210住户+ 811220广义政府+811230非金融企业+811240银行业存款类金融机构
                //                  +811250银行业非存款类金融机构+811260非银行业金融机构+811270特定目的载体+811280境外
                // 811260 非银行业金融机构= 811261 其中：中央交易对手方 + 资产三级分类=L06 非银行业金融机构-非中央交易对手
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L06.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811260_FYXYJRJG, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811200_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 811260 非银行业金融机构
//                    setAssetDebtDetailValue(line811260, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811200 回购和拆借
//                    setAssetDebtDetailValue(line811200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811200 回购和拆借 = 811210住户+ 811220广义政府+811230非金融企业+811240银行业存款类金融机构
                //                  +811250银行业非存款类金融机构+811260非银行业金融机构+811270特定目的载体+811280境外
                // 811260 非银行业金融机构= 811261 其中：中央交易对手方 + 资产三级分类=L06 非银行业金融机构-非中央交易对手
                // 811261 其中：中央交易对手方:资产三级分类=L07 非银行业金融机构-中央交易对手
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L07.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811261_QZZYJYDSF, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811260_FYXYJRJG, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811200_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 811261 其中：中央交易对手方
//                    setAssetDebtDetailValue(line811261, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811260 非银行业金融机构
//                    setAssetDebtDetailValue(line811260, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811200 回购和拆借
//                    setAssetDebtDetailValue(line811200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811200 回购和拆借 = 811210住户+ 811220广义政府+811230非金融企业+811240银行业存款类金融机构
                //                  +811250银行业非存款类金融机构+811260非银行业金融机构+811270特定目的载体+811280境外
                // 811270 特定目的载体 =811271银行非保本理财+811272信托公司资管产品+811273证券公司及其子公司资管产品
                //                  +811274基金管理公司及其子公司专户+811275期货公司及其子公司资管产品+811276保险资管产品
                //                  +811277金融资产投资公司资管产品+811278公募基金+811279私募机构私募基金+81127a其他特定目的载体
                // 811271 银行非保本理财:资产三级分类=L08 银行非保本理财
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L08.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811271_YXFBBLC, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811270_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811200_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 811271 银行非保本理财
//                    setAssetDebtDetailValue(line811271, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811270 特定目的载体
//                    setAssetDebtDetailValue(line811270, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811200 回购和拆借
//                    setAssetDebtDetailValue(line811200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811200 回购和拆借 = 811210住户+ 811220广义政府+811230非金融企业+811240银行业存款类金融机构
                //                  +811250银行业非存款类金融机构+811260非银行业金融机构+811270特定目的载体+811280境外
                // 811270 特定目的载体 =811271银行非保本理财+811272信托公司资管产品+811273证券公司及其子公司资管产品
                //                  +811274基金管理公司及其子公司专户+811275期货公司及其子公司资管产品+811276保险资管产品
                //                  +811277金融资产投资公司资管产品+811278公募基金+811279私募机构私募基金+81127a其他特定目的载体
                // 811272 信托公司资管产品:资产三级分类=L09 信托公司资管产品
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L09.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811272_XTGSZGCP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811270_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811200_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 811272 信托公司资管产品
//                    setAssetDebtDetailValue(line811272, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811270 特定目的载体
//                    setAssetDebtDetailValue(line811270, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811200 回购和拆借
//                    setAssetDebtDetailValue(line811200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811200 回购和拆借 = 811210住户+ 811220广义政府+811230非金融企业+811240银行业存款类金融机构
                //                  +811250银行业非存款类金融机构+811260非银行业金融机构+811270特定目的载体+811280境外
                // 811270 特定目的载体 =811271银行非保本理财+811272信托公司资管产品+811273证券公司及其子公司资管产品
                //                  +811274基金管理公司及其子公司专户+811275期货公司及其子公司资管产品+811276保险资管产品
                //                  +811277金融资产投资公司资管产品+811278公募基金+811279私募机构私募基金+81127a其他特定目的载体
                // 811273 证券公司及其子公司资管产品:资产三级分类=L10 证券公司及其子公司资管产品
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L10.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811273_ZQGSJQZGSZGCP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811270_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811200_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 811273 证券公司及其子公司资管产品
//                    setAssetDebtDetailValue(line811273, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811270 特定目的载体
//                    setAssetDebtDetailValue(line811270, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811200 回购和拆借
//                    setAssetDebtDetailValue(line811200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811200 回购和拆借 = 811210住户+ 811220广义政府+811230非金融企业+811240银行业存款类金融机构
                //                  +811250银行业非存款类金融机构+811260非银行业金融机构+811270特定目的载体+811280境外
                // 811270 特定目的载体 =811271银行非保本理财+811272信托公司资管产品+811273证券公司及其子公司资管产品
                //                  +811274基金管理公司及其子公司专户+811275期货公司及其子公司资管产品+811276保险资管产品
                //                  +811277金融资产投资公司资管产品+811278公募基金+811279私募机构私募基金+81127a其他特定目的载体
                // 811274 基金管理公司及其子公司专户:资产三级分类=L11 基金管理公司及其子公司专户
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L11.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811274_JJGLGSJQZGSZH, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811270_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811200_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 811274 基金管理公司及其子公司专户
//                    setAssetDebtDetailValue(line811274, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811270 特定目的载体
//                    setAssetDebtDetailValue(line811270, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811200 回购和拆借
//                    setAssetDebtDetailValue(line811200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811200 回购和拆借 = 811210住户+ 811220广义政府+811230非金融企业+811240银行业存款类金融机构
                //                  +811250银行业非存款类金融机构+811260非银行业金融机构+811270特定目的载体+811280境外
                // 811270 特定目的载体 =811271银行非保本理财+811272信托公司资管产品+811273证券公司及其子公司资管产品
                //                  +811274基金管理公司及其子公司专户+811275期货公司及其子公司资管产品+811276保险资管产品
                //                  +811277金融资产投资公司资管产品+811278公募基金+811279私募机构私募基金+81127a其他特定目的载体
                // 811275 期货公司及其子公司资管产品:资产三级分类=L12 期货公司及其子公司资管产品
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L12.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811275_QHGSJQZGSZGCP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811270_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811200_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 811275 期货公司及其子公司资管产品
//                    setAssetDebtDetailValue(line811275, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811270 特定目的载体
//                    setAssetDebtDetailValue(line811270, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811200 回购和拆借
//                    setAssetDebtDetailValue(line811200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811200 回购和拆借 = 811210住户+ 811220广义政府+811230非金融企业+811240银行业存款类金融机构
                //                  +811250银行业非存款类金融机构+811260非银行业金融机构+811270特定目的载体+811280境外
                // 811270 特定目的载体 =811271银行非保本理财+811272信托公司资管产品+811273证券公司及其子公司资管产品
                //                  +811274基金管理公司及其子公司专户+811275期货公司及其子公司资管产品+811276保险资管产品
                //                  +811277金融资产投资公司资管产品+811278公募基金+811279私募机构私募基金+81127a其他特定目的载体
                // 811276 保险资管产品:资产三级分类=L13 保险资管产品
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L13.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811276_BXZGCP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811270_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811200_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 811276 保险资管产品
//                    setAssetDebtDetailValue(line811276, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811270 特定目的载体
//                    setAssetDebtDetailValue(line811270, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811200 回购和拆借
//                    setAssetDebtDetailValue(line811200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811200 回购和拆借 = 811210住户+ 811220广义政府+811230非金融企业+811240银行业存款类金融机构
                //                  +811250银行业非存款类金融机构+811260非银行业金融机构+811270特定目的载体+811280境外
                // 811270 特定目的载体 =811271银行非保本理财+811272信托公司资管产品+811273证券公司及其子公司资管产品
                //                  +811274基金管理公司及其子公司专户+811275期货公司及其子公司资管产品+811276保险资管产品
                //                  +811277金融资产投资公司资管产品+811278公募基金+811279私募机构私募基金+81127a其他特定目的载体
                // 811277 金融资产投资公司资管产品:资产三级分类=L14 金融资产投资公司资管产品
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L14.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811277_JRZCTZGSZGCP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811270_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811200_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 811277 金融资产投资公司资管产品
//                    setAssetDebtDetailValue(line811277, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811270 特定目的载体
//                    setAssetDebtDetailValue(line811270, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811200 回购和拆借
//                    setAssetDebtDetailValue(line811200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811200 回购和拆借 = 811210住户+ 811220广义政府+811230非金融企业+811240银行业存款类金融机构
                //                  +811250银行业非存款类金融机构+811260非银行业金融机构+811270特定目的载体+811280境外
                // 811270 特定目的载体 =811271银行非保本理财+811272信托公司资管产品+811273证券公司及其子公司资管产品
                //                  +811274基金管理公司及其子公司专户+811275期货公司及其子公司资管产品+811276保险资管产品
                //                  +811277金融资产投资公司资管产品+811278公募基金+811279私募机构私募基金+81127a其他特定目的载体
                // 811278 公募基金:资产三级分类=L15 公募基金
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L15.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811278_GMJJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811270_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811200_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 811278 公募基金
//                    setAssetDebtDetailValue(line811278, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811270 特定目的载体
//                    setAssetDebtDetailValue(line811270, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811200 回购和拆借
//                    setAssetDebtDetailValue(line811200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811200 回购和拆借 = 811210住户+ 811220广义政府+811230非金融企业+811240银行业存款类金融机构
                //                  +811250银行业非存款类金融机构+811260非银行业金融机构+811270特定目的载体+811280境外
                // 811270 特定目的载体 =811271银行非保本理财+811272信托公司资管产品+811273证券公司及其子公司资管产品
                //                  +811274基金管理公司及其子公司专户+811275期货公司及其子公司资管产品+811276保险资管产品
                //                  +811277金融资产投资公司资管产品+811278公募基金+811279私募机构私募基金+81127a其他特定目的载体
                // 811279 私募机构私募基金:资产三级分类=L16 私募机构私募基金
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L16.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811279_SMJGSMJJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811270_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811200_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 811279 私募机构私募基金
//                    setAssetDebtDetailValue(line811279, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811270 特定目的载体
//                    setAssetDebtDetailValue(line811270, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811200 回购和拆借
//                    setAssetDebtDetailValue(line811200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811200 回购和拆借 = 811210住户+ 811220广义政府+811230非金融企业+811240银行业存款类金融机构
                //                  +811250银行业非存款类金融机构+811260非银行业金融机构+811270特定目的载体+811280境外
                // 811270 特定目的载体 =811271银行非保本理财+811272信托公司资管产品+811273证券公司及其子公司资管产品
                //                  +811274基金管理公司及其子公司专户+811275期货公司及其子公司资管产品+811276保险资管产品
                //                  +811277金融资产投资公司资管产品+811278公募基金+811279私募机构私募基金+81127a其他特定目的载体
                // 81127a 其他特定目的载体:资产三级分类=L17 其他特定目的载体
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L99.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_81127A_QTTDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811270_TDMDZT, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811200_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 81127a 其他特定目的载体
//                    setAssetDebtDetailValue(line81127a, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811270 特定目的载体
//                    setAssetDebtDetailValue(line811270, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811200 回购和拆借
//                    setAssetDebtDetailValue(line811200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 811000 贷款=811100除回购和拆借外贷款+811200回购和拆借
                // 811200 回购和拆借 = 811210住户+ 811220广义政府+811230非金融企业+811240银行业存款类金融机构
                //                  +811250银行业非存款类金融机构+811260非银行业金融机构+811270特定目的载体+811280境外
                // 811280 境外:资产三级分类=L18 境外
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_L98.equals(pos.getAssetType3())) {

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811280_JW, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811200_HGHCJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_811000_DK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 811280 境外
//                    setAssetDebtDetailValue(line811280, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 811200 回购和拆借
//                    setAssetDebtDetailValue(line811200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 811000 贷款
//                    setAssetDebtDetailValue(line811000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
//                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
//                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
//                // 812000 资产支持证券： 不统计
//                // 812000 资产支持证券
//                setAssetDebtDetailValue(line812000, pos.getCcy(), BigDecimal.ZERO, rateMap, pbcReport.getEnddate(),source);
//                // 810000 负债合计
//                setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                // 800000 负债及权益合计
//                setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 813000 金融衍生品： 813100 境内+813200 境外
                // 813100 境内:资产三级分类=J03 境内
//                if (PbcMonthReportConstants.PBC_ASSETTYPE3_J01.equals(pos.getAssetType3())) {
                /* 负债金融衍生品境内外枚举值修改 modify by gaojian 2019-07-22 begin*/
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_J03.equals(pos.getAssetType3())) {
                    /* 负债金融衍生品境内外枚举值修改 modify by gaojian 2019-07-22 end*/


                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_813100_JN, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_813000_JRYSP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 813100 境内
//                    setAssetDebtDetailValue(line813100, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 813000 金融衍生品
//                    setAssetDebtDetailValue(line813000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 813000 金融衍生品： 813100 境内+813200 境外
                // 813200 境外:资产三级分类=J04 境外
//                if (PbcMonthReportConstants.PBC_ASSETTYPE3_J02.equals(pos.getAssetType3())) {
                /* 负债金融衍生品境内外枚举值修改 modify by gaojian 2019-07-22 begin*/
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_J04.equals(pos.getAssetType3())) {
                    /* 负债金融衍生品境内外枚举值修改 modify by gaojian 2019-07-22 end*/

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_813200_JW, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_813000_JRYSP, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 813200 境外
//                    setAssetDebtDetailValue(line813200, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 813000 金融衍生品
//                    setAssetDebtDetailValue(line813000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 814000 应付账款=统计所有本机构资产（类型=负债）的利息值+资产三级类型=P12 应付账款 的金额值
                if (PbcMonthReportConstants.PBC_ASSETTYPE3_P12.equals(pos.getAssetType3())) {


                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_814000_YFZK, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 814000 应付账款
//                    setAssetDebtDetailValue(line814000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
                // 814000 应付账款=统计所有本机构资产（类型=负债）的利息值+资产三级类型=P12 应付账款 的金额值
                if (PbcMonthReportConstants.POSITION_TYPE_FZ.equals(pos.getPositionType())
                        || PbcMonthReportConstants.PBC_ASSETTYPE3_J03.equals(pos.getAssetType3())
                        || PbcMonthReportConstants.PBC_ASSETTYPE3_J04.equals(pos.getAssetType3())) {


                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_814000_YFZK, reportType, rateMap, source, "01", massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_810000_FZHJ, reportType, rateMap, source, "01", massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, "01", massageMap);

//                    // 814000 应付账款
//                    setAssetDebtDetailValue(line814000, pos.getCcy(), pos.getIntAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 810000 负债合计
//                    setAssetDebtDetailValue(line810000, pos.getCcy(), pos.getIntAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getIntAmt(), rateMap, pbcReport.getEnddate(),source);
                }
            }
            else if (PbcMonthReportConstants.POSITION_TYPE_KMYE.equals(pos.getPositionType())){
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
                // 822000 资本公积：根据系统配置的科目号从【人行报表资产头寸】.【资产代码】=相应科目号的【金额】值
                if (PbcMonthReportConstants.PBC_ASSETTYPE1_ZBGJJ.equals(assetType1ForBalance)) {
                    // 822000 资本公积
                    // 822000 资本公积(取值修改逻辑为	822000 统计逻辑：根据系统配置的科目号从【人行报表资产头寸】.【资产代码】=相应科目号的【金额】值，同时加上下方的值：
                    // 人行产品头寸的sum（剩余成本-剩余份额）

//                	setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_822000_ZBGJ, reportType, rateMap, source, null, massageMap);
//
//                	setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_820000_QYHJ, reportType, rateMap, source, null, massageMap);
//
//                	setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

//                    setAssetDebtDetailValue(line822000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);

                    // 820000 权益合计
//                    setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
                if(PbcMonthReportConstants.PBC_ASSETTYPE1_QTZHSY.equals(assetType1ForBalance)){
                    //其他综合收益
                    //"权益合计”（C0000）=“实收本金”（C1000）+“未分配利润”（C3000）+“其他综合收益”（C4000)
                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_824000_QTZHSY, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_820000_QYHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);
                }
                // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
                // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
                // 823000 未分配利润：根据系统配置的科目号从【人行报表资产头寸】.【资产代码】=相应科目号的【金额】值
                if (PbcMonthReportConstants.PBC_ASSETTYPE1_WFPLR.equals(assetType1ForBalance)) {
                    // 823000 未分配利润
                    // 增加参数“人行月报2-1 倒减逻辑：01 其他负债，02 未分配利润”
                    // 参数值=01 时，根据系统配置的科目号从【人行报表资产头寸】.【资产代码】=相应科目号的【金额】值，同时减去下方的值：
                    // 人行产品头寸的sum（剩余成本-剩余份额）
                    // 参数值=02 时，823000未分配利润：=资产合计-负债合计-实收本金-资本公积；合计列倒减，人民币列=合计列，其他列不统计
                    if(StringUtils.isNotBlank(svrMonthSubtractionLogic)){
                        if(StringUtil.equals(DictCodeListExt1.是或否_是.getValue(), svrMonthSubtractionLogic)) {

                            setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_823000_WFPLR, reportType, rateMap, source, null, massageMap);

//                            setAssetDebtDetailValue(line823000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                        }
                    }


                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_820000_QYHJ, reportType, rateMap, source, null, massageMap);

                    setAssetReportValue(pos, result, deptMap, PbcReportProperycode.AMT_800000_FZJQYHJ, reportType, rateMap, source, null, massageMap);

                    // 820000 权益合计
//                    setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
//                    // 800000 负债及权益合计
//                    setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getAmt(), rateMap, pbcReport.getEnddate(),source);
                }
            }
        }
        Set<String>  set=new HashSet<String>();
        for(PbcMonthPrdPostionDto pos :posProdList){
            set.add(pos.getProdid());
            // 销售区域为计划单列市的不统计（省份内已经统计）：210200大连市、330200宁波市、350200厦门市、370200青岛市、440300深圳市
            if("210200".equals(pos.getBuyPlace()) || "330200".equals(pos.getBuyPlace()) || "350200".equals(pos.getBuyPlace()) ||
                    "370200".equals(pos.getBuyPlace()) || "440300".equals(pos.getBuyPlace())) {
                continue;
            }

            // add by leipeng 20200319 获取所有部门对应信息,并列出单独的表结构
            if(null!=pos.getInstitutionCode()&&null==deptMap.get(pos.getInstitutionCode())){
                Map<String,PbcReportAssetDebtDetailDto> singDeptMap = genAssetMonthReportemplate(generateDate,  reportType, 146,pbcReport.getReportuuid());
                singDeptMap.putAll(singDeptMap);
                deptMap.put(pos.getInstitutionCode(), singDeptMap);
            }

            // 参数值=01 时，根据系统配置的科目号从【人行报表资产头寸】.【资产代码】=相应科目号的【金额】值，同时减去下方的值：
            // 人行产品头寸的sum（剩余成本-剩余份额）
            if(StringUtils.isNotBlank(svrMonthSubtractionLogic)){
                if(StringUtil.equals(DictCodeListExt1.是或否_是.getValue(), svrMonthSubtractionLogic)) {
                    if(pos.getEndCost() == null){
                        pos.setEndCost(new BigDecimal("0"));
                    }
                    if(pos.getEndqtysum() == null){
                        pos.setEndqtysum(new BigDecimal("0"));
                    }

//        			setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_823000_WFPLR,
//        					reportType,rateMap, source,null, massageMap,BigDecimal.ZERO.subtract(pos.getEndCost().subtract(pos.getEndqtysum())));

//                    setAssetDebtDetailValue(line823000, pos.getCcy(), BigDecimal.ZERO.subtract(pos.getEndCost().subtract(pos.getEndqtysum())), rateMap, pbcReport.getEnddate(),source);
                }
            }
            if(pos.getEndCost() == null){
                pos.setEndCost(new BigDecimal("0"));
            }
            if(pos.getEndqtysum() == null){
                pos.setEndqtysum(new BigDecimal("0"));
            }

            /**  add by dongxiangyang   资本公积设计变更  start  */
            //if(StringUtil.isNotEmpty(svrMonthZbGj)&&!svrMonthZbGj.equals("01")){

//            	setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_822000_ZBGJ,
//    					reportType,rateMap, source,null, massageMap,(pos.getEndCost().subtract(pos.getEndqtysum())));

//                setAssetDebtDetailValue(line822000, pos.getCcy(), (pos.getEndCost().subtract(pos.getEndqtysum())), rateMap, pbcReport.getEnddate(),source);
            //}
            /** add by  dongxiangyang   资本公积设计变更    end */

          /*  if(StringUtils.isNotBlank(svrMonthSubtractionLogic)) {
                if (StringUtil.equals(DictCodeListExt1.是或否_否.getValue(), svrMonthSubtractionLogic)) {
                    line823000.setTotal(line700000.getTotal().subtract(line810000.getTotal()).subtract(line821000.getTotal()).subtract(line822000.getTotal()));
                    line823000.setCny(line823000.getTotal());
                    setAssetDebtDetailValue(line823000, pos.getCcy(), BigDecimal.ZERO, rateMap, pbcReport.getEnddate(),source);
                    // 820000 权益合计
                    line820000.setTotal(line821000.getTotal().add(line822000.getTotal()).add(line823000.getTotal()));
                    line820000.setCny(line820000.getTotal());
                    setAssetDebtDetailValue(line820000, pos.getCcy(), BigDecimal.ZERO, rateMap, pbcReport.getEnddate(),source);
                    // 800000 负债及权益合计
                    line800000.setTotal(line810000.getTotal().add(line820000.getTotal()));
                    line800000.setCny(line800000.getTotal());
                    setAssetDebtDetailValue(line800000, pos.getCcy(), BigDecimal.ZERO, rateMap, pbcReport.getEnddate(),source);
                }
            }*/
            // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
            // 820000权益合计=821000实收本金+822000资本公积+823000未分配利润
            // 821000 实收资本 = 821100优先级+821200劣后级
            // 821100 优先级不统计
            // 821100 优先级 = 821110 住户   +821120 广义政府+821130 非金融企业+821140 银行业存款类金融机构
            //               +821150 银行业非存款类金融机构 +821160 非银行业金融机构  +821170 特定目的载体 +821180 境外
            // 821110 住户
            // 821120 广义政府
            // 821130 非金融企业
            // 821140 银行业存款类金融机构
            // 821150 银行业非存款类金融机构
            // 821160 非银行业金融机构
            // 821170 特定目的载体 = 821171 银行非保本理财 +821172 信托公司资管产品+821173 证券公司及其子公司资管产品
            //                   +821174 基金管理公司及其子公司专户 +821175 期货公司及其子公司资管产品 +821176 保险资管产品
            //                   +821177 金融资产投资公司资管产品 +821178 公募基金+821179 私募机构私募基金 +82117a 其他特定目的载体
            // 821171 银行非保本理财
            // 821172 信托公司资管产品
            // 821173 证券公司及其子公司资管产品
            // 821174 基金管理公司及其子公司专户
            // 821175 期货公司及其子公司资管产品
            // 821176 保险资管产品
            // 821177 金融资产投资公司资管产品
            // 821178 公募基金
            // 821179 私募机构私募基金
            // 82117a 其他特定目的载体
            // 821180 境外

            // 权益合计统计产品头寸
            // 劣后级从【人行报表产品头寸】统计【剩余份额】，根据客户二级分类进行统计
            // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
            // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
            // 821000 实收资本 = 821100优先级+821200劣后级
            // 821200 劣后级 = 821210 住户   +822120 广义政府+822130 非金融企业+822140 银行业存款类金融机构
            //               +822150 银行业非存款类金融机构 +822160 非银行业金融机构  +822170 特定目的载体 +822180 境外
            // 821210 住户 :客户二级类型=C0100住户
            if (DictCodeListExt2.客户二级类型_住户.getValue().equals(pos.getCustomerType2())) {

                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821210_ZH,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821200_LHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821000_SSBJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_820000_QYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_800000_FZJQYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());

                // 821210 住户
//                setAssetDebtDetailValue(line821210, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 821200 劣后级
//                setAssetDebtDetailValue(line821200, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 821000 实收资本
//                setAssetDebtDetailValue(line821000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 820000 权益合计
//                setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 800000 负债及权益合计
//                setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
            }
            // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
            // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
            // 821000 实收资本 = 821100优先级+821200劣后级
            // 821200 劣后级 = 821210 住户   +822120 广义政府+822130 非金融企业+822140 银行业存款类金融机构
            //               +822150 银行业非存款类金融机构 +822160 非银行业金融机构  +822170 特定目的载体 +822180 境外
            // 821220 广义政府:客户二级类型=C0200广义政府
            if (DictCodeListExt2.客户二级类型_广义政府.getValue().equals(pos.getCustomerType2())) {

                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821220_GYZF,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821200_LHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821000_SSBJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_820000_QYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_800000_FZJQYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());

                // 821220 广义政府
//                setAssetDebtDetailValue(line821220, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 821200 劣后级
//                setAssetDebtDetailValue(line821200, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821000 实收资本
//                setAssetDebtDetailValue(line821000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 820000 权益合计
//                setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 800000 负债及权益合计
//                setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
            }
            // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
            // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
            // 821000 实收资本 = 821100优先级+821200劣后级
            // 821200 劣后级 = 821210 住户   +822120 广义政府+822130 非金融企业+822140 银行业存款类金融机构
            //               +822150 银行业非存款类金融机构 +822160 非银行业金融机构  +822170 特定目的载体 +822180 境外
            // 821230 非金融企业 :客户二级类型=C0300非金融企业
            if (DictCodeListExt2.客户二级类型_非金融企业.getValue().equals(pos.getCustomerType2())) {

                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821230_FJRQY,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821200_LHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821000_SSBJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_820000_QYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_800000_FZJQYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());

                // 821230 非金融企业
//                setAssetDebtDetailValue(line821230, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 821200 劣后级
//                setAssetDebtDetailValue(line821200, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821000 实收资本
//                setAssetDebtDetailValue(line821000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 820000 权益合计
//                setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 800000 负债及权益合计
//                setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
            }
            // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
            // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
            // 821000 实收资本 = 821100优先级+821200劣后级
            // 821200 劣后级 = 821210 住户   +822120 广义政府+822130 非金融企业+822140 银行业存款类金融机构
            //               +822150 银行业非存款类金融机构 +822160 非银行业金融机构  +822170 特定目的载体 +822180 境外
            // 821240 银行业存款类金融机构:客户二级类型=C0400银行业存款类金融机构
            if (DictCodeListExt2.客户二级类型_银行业存款类金融机构.getValue().equals(pos.getCustomerType2())) {

                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821240_YXYCKLJRJG,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821200_LHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821000_SSBJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_820000_QYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_800000_FZJQYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());

                // 821240 银行业存款类金融机构
//                setAssetDebtDetailValue(line821240, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 821200 劣后级
//                setAssetDebtDetailValue(line821200, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821000 实收资本
//                setAssetDebtDetailValue(line821000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 820000 权益合计
//                setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 800000 负债及权益合计
//                setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
            }
            // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
            // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
            // 821000 实收资本 = 821100优先级+821200劣后级
            // 821200 劣后级 = 821210 住户   +822120 广义政府+822130 非金融企业+822140 银行业存款类金融机构
            //               +822150 银行业非存款类金融机构 +822160 非银行业金融机构  +822170 特定目的载体 +822180 境外
            // 821250 银行业非存款类金融机构 :客户二级类型=C0401银行业非存款类金融机构
            if (DictCodeListExt2.客户二级类型_银行业非存款类金融机构.getValue().equals(pos.getCustomerType2())) {

                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821250_YXYFCKLJRJG,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821200_LHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821000_SSBJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_820000_QYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_800000_FZJQYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());

                // 821250 银行业非存款类金融机构
//                setAssetDebtDetailValue(line821250, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 821200 劣后级
//                setAssetDebtDetailValue(line821200, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821000 实收资本
//                setAssetDebtDetailValue(line821000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 820000 权益合计
//                setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 800000 负债及权益合计
//                setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
            }
            // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
            // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
            // 821000 实收资本 = 821100优先级+821200劣后级
            // 821200 劣后级 = 821210 住户   +822120 广义政府+822130 非金融企业+822140 银行业存款类金融机构
            //               +822150 银行业非存款类金融机构 +822160 非银行业金融机构  +822170 特定目的载体 +822180 境外
            // 821260 非银行业金融机构 :客户二级类型=C0402非银行业金融机构
            if (DictCodeListExt2.客户二级类型_非银行业金融机构.getValue().equals(pos.getCustomerType2())
                    /*****G06升级改造 leipeng 20210128   start**/
                    ||DictCodeListExt2.客户二级类型_保险业金融机构.getValue().equals(pos.getCustomerType2())
                    ||DictCodeListExt2.客户二级类型_信托公司.getValue().equals(pos.getCustomerType2())
                    ||DictCodeListExt2.客户二级类型_证券公司.getValue().equals(pos.getCustomerType2())
                    ||DictCodeListExt2.客户二级类型_基金公司.getValue().equals(pos.getCustomerType2())
                    ||DictCodeListExt2.客户二级类型_其他金融机构.getValue().equals(pos.getCustomerType2())
            /*****G06升级改造 leipeng 20210128   end**/
            ) {

                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821260_FYXYJRJG,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821200_LHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821000_SSBJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_820000_QYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_800000_FZJQYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());

                // 821260 非银行业金融机构
//                setAssetDebtDetailValue(line821260, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 821200 劣后级
//                setAssetDebtDetailValue(line821200, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821000 实收资本
//                setAssetDebtDetailValue(line821000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 820000 权益合计
//                setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 800000 负债及权益合计
//                setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
            }
            // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
            // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
            // 821000 实收资本 = 821100优先级+821200劣后级
            // 821200 劣后级 = 821210 住户   +822120 广义政府+822130 非金融企业+822140 银行业存款类金融机构
            //               +822150 银行业非存款类金融机构 +822160 非银行业金融机构  +822170 特定目的载体 +822180 境外
            // 821270 特定目的载体 = 822171 银行非保本理财 +822172 信托公司资管产品+822173 证券公司及其子公司资管产品
            //                   +822174 基金管理公司及其子公司专户 +822175 期货公司及其子公司资管产品 +822176 保险资管产品
            //                   +822177 金融资产投资公司资管产品 +822178 公募基金+822179 私募机构私募基金 +82217a 其他特定目的载体
            // 821271 银行非保本理财  :客户二级类型=C0500银行非保本理财
            if (DictCodeListExt2.客户二级类型_银行非保本理财.getValue().equals(pos.getCustomerType2())) {
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821271_YXFBBLC,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821270_TDMDZT,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821200_LHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821000_SSBJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_820000_QYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_800000_FZJQYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());

                // 821271 银行非保本理财
//                setAssetDebtDetailValue(line821271, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 821270 特定目的载体
//                setAssetDebtDetailValue(line821270, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821200 劣后级
//                setAssetDebtDetailValue(line821200, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821000 实收资本
//                setAssetDebtDetailValue(line821000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 820000 权益合计
//                setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 800000 负债及权益合计
//                setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
            }
            // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
            // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
            // 821000 实收资本 = 821100优先级+821200劣后级
            // 821200 劣后级 = 821210 住户   +822120 广义政府+822130 非金融企业+822140 银行业存款类金融机构
            //               +822150 银行业非存款类金融机构 +822160 非银行业金融机构  +822170 特定目的载体 +822180 境外
            // 821270 特定目的载体 = 822171 银行非保本理财 +822172 信托公司资管产品+822173 证券公司及其子公司资管产品
            //                   +822174 基金管理公司及其子公司专户 +822175 期货公司及其子公司资管产品 +822176 保险资管产品
            //                   +822177 金融资产投资公司资管产品 +822178 公募基金+822179 私募机构私募基金 +82217a 其他特定目的载体
            // 821272 信托公司资管产品 :客户二级类型=C0501信托公司资管产品
            if (DictCodeListExt2.客户二级类型_信托公司资管产品.getValue().equals(pos.getCustomerType2())) {

                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821272_XTGSZGCP,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821270_TDMDZT,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821200_LHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821000_SSBJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_820000_QYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_800000_FZJQYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());

                // 821272 信托公司资管产品
//                setAssetDebtDetailValue(line821272, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 821270 特定目的载体
//                setAssetDebtDetailValue(line821270, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821200 劣后级
//                setAssetDebtDetailValue(line821200, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821000 实收资本
//                setAssetDebtDetailValue(line821000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 820000 权益合计
//                setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 800000 负债及权益合计
//                setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
            }
            // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
            // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
            // 821000 实收资本 = 821100优先级+821200劣后级
            // 821200 劣后级 = 821210 住户   +822120 广义政府+822130 非金融企业+822140 银行业存款类金融机构
            //               +822150 银行业非存款类金融机构 +822160 非银行业金融机构  +822170 特定目的载体 +822180 境外
            // 821270 特定目的载体 = 822171 银行非保本理财 +822172 信托公司资管产品+822173 证券公司及其子公司资管产品
            //                   +822174 基金管理公司及其子公司专户 +822175 期货公司及其子公司资管产品 +822176 保险资管产品
            //                   +822177 金融资产投资公司资管产品 +822178 公募基金+822179 私募机构私募基金 +82217a 其他特定目的载体
            // 821273 证券公司及其子公司资管产品:客户二级类型=C0502证券公司及其子公司资管产品
            if (DictCodeListExt2.客户二级类型_证券公司及其子公司资管产品.getValue().equals(pos.getCustomerType2())) {

                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821273_ZQGSJQZGSZGCP,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821270_TDMDZT,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821200_LHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821000_SSBJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_820000_QYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_800000_FZJQYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());

                // 821273 证券公司及其子公司资管产品
//                setAssetDebtDetailValue(line821273, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 821270 特定目的载体
//                setAssetDebtDetailValue(line821270, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821200 劣后级
//                setAssetDebtDetailValue(line821200, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821000 实收资本
//                setAssetDebtDetailValue(line821000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 820000 权益合计
//                setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 800000 负债及权益合计
//                setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
            }
            // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
            // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
            // 821000 实收资本 = 821100优先级+821200劣后级
            // 821200 劣后级 = 821210 住户   +822120 广义政府+822130 非金融企业+822140 银行业存款类金融机构
            //               +822150 银行业非存款类金融机构 +822160 非银行业金融机构  +822170 特定目的载体 +822180 境外
            // 821270 特定目的载体 = 822171 银行非保本理财 +822172 信托公司资管产品+822173 证券公司及其子公司资管产品
            //                   +822174 基金管理公司及其子公司专户 +822175 期货公司及其子公司资管产品 +822176 保险资管产品
            //                   +822177 金融资产投资公司资管产品 +822178 公募基金+822179 私募机构私募基金 +82217a 其他特定目的载体
            // 821274 基金管理公司及其子公司专户:客户二级类型=C0503基金管理公司及其子公司专户
            if (DictCodeListExt2.客户二级类型_基金管理公司及其子公司专户.getValue().equals(pos.getCustomerType2())) {

                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821274_JJGLGSJQZGSZH,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821270_TDMDZT,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821200_LHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821000_SSBJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_820000_QYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_800000_FZJQYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());

                // 821274 基金管理公司及其子公司专户
//                setAssetDebtDetailValue(line821274, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 821270 特定目的载体
//                setAssetDebtDetailValue(line821270, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821200 劣后级
//                setAssetDebtDetailValue(line821200, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821000 实收资本
//                setAssetDebtDetailValue(line821000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 820000 权益合计
//                setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 800000 负债及权益合计
//                setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
            }
            // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
            // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
            // 821000 实收资本 = 821100优先级+821200劣后级
            // 821200 劣后级 = 821210 住户   +822120 广义政府+822130 非金融企业+822140 银行业存款类金融机构
            //               +822150 银行业非存款类金融机构 +822160 非银行业金融机构  +822170 特定目的载体 +822180 境外
            // 821270 特定目的载体 = 822171 银行非保本理财 +822172 信托公司资管产品+822173 证券公司及其子公司资管产品
            //                   +822174 基金管理公司及其子公司专户 +822175 期货公司及其子公司资管产品 +822176 保险资管产品
            //                   +822177 金融资产投资公司资管产品 +822178 公募基金+822179 私募机构私募基金 +82217a 其他特定目的载体
            // 821275 期货公司及其子公司资管产品:客户二级类型=C0504期货公司及其子公司资管产品
            if (DictCodeListExt2.客户二级类型_期货公司及其子公司资管产品.getValue().equals(pos.getCustomerType2())) {

                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821275_QHGSJQZGSZGCP,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821270_TDMDZT,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821200_LHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821000_SSBJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_820000_QYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_800000_FZJQYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());

                // 821275 期货公司及其子公司资管产品
//                setAssetDebtDetailValue(line821275, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 821270 特定目的载体
//                setAssetDebtDetailValue(line821270, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821200 劣后级
//                setAssetDebtDetailValue(line821200, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821000 实收资本
//                setAssetDebtDetailValue(line821000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 820000 权益合计
//                setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 800000 负债及权益合计
//                setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
            }
            // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
            // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
            // 821000 实收资本 = 821100优先级+821200劣后级
            // 821200 劣后级 = 821210 住户   +822120 广义政府+822130 非金融企业+822140 银行业存款类金融机构
            //               +822150 银行业非存款类金融机构 +822160 非银行业金融机构  +822170 特定目的载体 +822180 境外
            // 821270 特定目的载体 = 822171 银行非保本理财 +822172 信托公司资管产品+822173 证券公司及其子公司资管产品
            //                   +822174 基金管理公司及其子公司专户 +822175 期货公司及其子公司资管产品 +822176 保险资管产品
            //                   +822177 金融资产投资公司资管产品 +822178 公募基金+822179 私募机构私募基金 +82217a 其他特定目的载体
            // 821276 保险资管产品:客户二级类型=C0505保险资管产品
            if (DictCodeListExt2.客户二级类型_保险资管产品.getValue().equals(pos.getCustomerType2())) {

                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821276_BXZGCP,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821270_TDMDZT,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821200_LHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821000_SSBJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_820000_QYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_800000_FZJQYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());

                // 821276 保险资管产品
//                setAssetDebtDetailValue(line821276, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 821270 特定目的载体
//                setAssetDebtDetailValue(line821270, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821200 劣后级
//                setAssetDebtDetailValue(line821200, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821000 实收资本
//                setAssetDebtDetailValue(line821000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 820000 权益合计
//                setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 800000 负债及权益合计
//                setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
            }
            // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
            // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
            // 821000 实收资本 = 821100优先级+821200劣后级
            // 821200 劣后级 = 821210 住户   +822120 广义政府+822130 非金融企业+822140 银行业存款类金融机构
            //               +822150 银行业非存款类金融机构 +822160 非银行业金融机构  +822170 特定目的载体 +822180 境外
            // 821270 特定目的载体 = 822171 银行非保本理财 +822172 信托公司资管产品+822173 证券公司及其子公司资管产品
            //                   +822174 基金管理公司及其子公司专户 +822175 期货公司及其子公司资管产品 +822176 保险资管产品
            //                   +822177 金融资产投资公司资管产品 +822178 公募基金+822179 私募机构私募基金 +82217a 其他特定目的载体
            // 821277 金融资产投资公司资管产品:客户二级类型=C0506金融资产投资公司资管产品
            if (DictCodeListExt2.客户二级类型_金融资产投资公司资管产品.getValue().equals(pos.getCustomerType2())) {


                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821277_JRZCTZGSZGCP,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821270_TDMDZT,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821200_LHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821000_SSBJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_820000_QYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_800000_FZJQYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());

                // 821277 金融资产投资公司资管产品
//                setAssetDebtDetailValue(line821277, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 821270 特定目的载体
//                setAssetDebtDetailValue(line821270, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821200 劣后级
//                setAssetDebtDetailValue(line821200, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821000 实收资本
//                setAssetDebtDetailValue(line821000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 820000 权益合计
//                setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 800000 负债及权益合计
//                setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
            }
            // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
            // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
            // 821000 实收资本 = 821100优先级+821200劣后级
            // 821200 劣后级 = 821210 住户   +822120 广义政府+822130 非金融企业+822140 银行业存款类金融机构
            //               +822150 银行业非存款类金融机构 +822160 非银行业金融机构  +822170 特定目的载体 +822180 境外
            // 821270 特定目的载体 = 822171 银行非保本理财 +822172 信托公司资管产品+822173 证券公司及其子公司资管产品
            //                   +822174 基金管理公司及其子公司专户 +822175 期货公司及其子公司资管产品 +822176 保险资管产品
            //                   +822177 金融资产投资公司资管产品 +822178 公募基金+822179 私募机构私募基金 +82217a 其他特定目的载体
            // 821278 公募基金 :客户二级类型=C0507公募基金
            if (DictCodeListExt2.客户二级类型_公募基金.getValue().equals(pos.getCustomerType2())) {

                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821278_GMJJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821270_TDMDZT,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821200_LHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821000_SSBJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_820000_QYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_800000_FZJQYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());

                // 821278 公募基金
//                setAssetDebtDetailValue(line821278, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 821270 特定目的载体
//                setAssetDebtDetailValue(line821270, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821200 劣后级
//                setAssetDebtDetailValue(line821200, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821000 实收资本
//                setAssetDebtDetailValue(line821000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 820000 权益合计
//                setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 800000 负债及权益合计
//                setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
            }
            // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
            // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
            // 821000 实收资本 = 821100优先级+821200劣后级
            // 821200 劣后级 = 821210 住户   +822120 广义政府+822130 非金融企业+822140 银行业存款类金融机构
            //               +822150 银行业非存款类金融机构 +822160 非银行业金融机构  +822170 特定目的载体 +822180 境外
            // 821270 特定目的载体 = 822171 银行非保本理财 +822172 信托公司资管产品+822173 证券公司及其子公司资管产品
            //                   +822174 基金管理公司及其子公司专户 +822175 期货公司及其子公司资管产品 +822176 保险资管产品
            //                   +822177 金融资产投资公司资管产品 +822178 公募基金+822179 私募机构私募基金 +82217a 其他特定目的载体
            // 821279 私募机构私募基金  :客户二级类型=C0508私募机构私募基金
            if (DictCodeListExt2.客户二级类型_私募机构私募基金.getValue().equals(pos.getCustomerType2())) {

                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821279_SMJGSMJJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821270_TDMDZT,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821200_LHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821000_SSBJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_820000_QYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_800000_FZJQYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());

                // 821279 私募机构私募基金
//                setAssetDebtDetailValue(line821279, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 821270 特定目的载体
//                setAssetDebtDetailValue(line821270, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821200 劣后级
//                setAssetDebtDetailValue(line821200, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821000 实收资本
//                setAssetDebtDetailValue(line821000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 820000 权益合计
//                setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 800000 负债及权益合计
//                setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
            }
            // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
            // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
            // 821000 实收资本 = 821100优先级+821200劣后级
            // 821200 劣后级 = 821210 住户   +822120 广义政府+822130 非金融企业+822140 银行业存款类金融机构
            //               +822150 银行业非存款类金融机构 +822160 非银行业金融机构  +822170 特定目的载体 +822180 境外
            // 821270 特定目的载体 = 822171 银行非保本理财 +822172 信托公司资管产品+822173 证券公司及其子公司资管产品
            //                   +822174 基金管理公司及其子公司专户 +822175 期货公司及其子公司资管产品 +822176 保险资管产品
            //                   +822177 金融资产投资公司资管产品 +822178 公募基金+822179 私募机构私募基金 +82217a 其他特定目的载体
            // 82127a 其他特定目的载体:客户二级类型=C0509其他特定目的载体
            if (DictCodeListExt2.客户二级类型_其他特定目的载体.getValue().equals(pos.getCustomerType2())) {

                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_82127A_QTTDMDZT,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821270_TDMDZT,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821200_LHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821000_SSBJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_820000_QYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_800000_FZJQYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());

                // 82127a 其他特定目的载体
//                setAssetDebtDetailValue(line82127a, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 821270 特定目的载体
//                setAssetDebtDetailValue(line821270, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821200 劣后级
//                setAssetDebtDetailValue(line821200, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821000 实收资本
//                setAssetDebtDetailValue(line821000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 820000 权益合计
//                setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 800000 负债及权益合计
//                setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
            }
            // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
            // 820000 权益合计=821000实收本金+822000资本公积+823000未分配利润
            // 821000 实收资本 = 821100优先级+821200劣后级
            // 821200 劣后级 = 821210 住户   +822120 广义政府+822130 非金融企业+822140 银行业存款类金融机构
            //               +822150 银行业非存款类金融机构 +822160 非银行业金融机构  +822170 特定目的载体 +822180 境外
            // 821280 境外:客户二级类型=C0509境外
            if (pos.getCustomerType2()!=null&&pos.getCustomerType2().contains(DictCodeListExt2.客户一级类型_境外.getValue())) {


                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821280_JW,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821200_LHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_821000_SSBJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_820000_QYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());
                setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_800000_FZJQYHJ,
                        reportType,rateMap, source,null, massageMap,pos.getEndqtysum());

                // 821280 境外
//                setAssetDebtDetailValue(line821280, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
                // 821200 劣后级
//                setAssetDebtDetailValue(line821200, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 821000 实收资本
//                setAssetDebtDetailValue(line821000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 820000 权益合计
//                setAssetDebtDetailValue(line820000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
//                // 800000 负债及权益合计
//                setAssetDebtDetailValue(line800000, pos.getCcy(), pos.getEndqtysum(), rateMap, pbcReport.getEnddate(),source);
            }
            if(StringUtils.isNotBlank(svrMonthSubtractionLogic)) {
                if (StringUtil.equals(DictCodeListExt1.是或否_否.getValue(), svrMonthSubtractionLogic)) {


                    buildSvrMonthSubtraction(result, deptMap.get(pos.getInstitutionCode()),pos.getCcy());
//                    line823000.setTotal(line700000.getTotal().subtract(line810000.getTotal()).subtract(line821000.getTotal()).subtract(line822000.getTotal()));
//                    line823000.setCny(line823000.getTotal());
//                    //setAssetDebtDetailValue(line823000, pos.getCcy(), BigDecimal.ZERO, rateMap, pbcReport.getEnddate(),source);
//                    String ccyAmount823000 = pos.getCcy()+GlobalConstant.COMMA+line823000.getTotal().setScale(2,RoundingMode.HALF_UP)
//                            +GlobalConstant.COMMA+line823000.getTotal().setScale(2,RoundingMode.HALF_UP);
//                 	line823000.setCcyAmount(ccyAmount823000);
//                    // 820000 权益合计
//                    line820000.setTotal(line821000.getTotal().add(line822000.getTotal()).add(line823000.getTotal()));
//                    line820000.setCny(line820000.getTotal());
//                    //setAssetDebtDetailValue(line820000, pos.getCcy(), BigDecimal.ZERO, rateMap, pbcReport.getEnddate(),source);
//                    String ccyAmount820000 = pos.getCcy()+GlobalConstant.COMMA+line820000.getTotal().setScale(2,RoundingMode.HALF_UP)
//                            +GlobalConstant.COMMA+line820000.getTotal().setScale(2,RoundingMode.HALF_UP);
//                    line820000.setCcyAmount(ccyAmount820000);
//                    // 800000 负债及权益合计
//                    line800000.setTotal(line810000.getTotal().add(line820000.getTotal()));
//                    line800000.setCny(line800000.getTotal());
//                    //setAssetDebtDetailValue(line800000, pos.getCcy(), BigDecimal.ZERO, rateMap, pbcReport.getEnddate(),source);
//                    String ccyAmount800000 = pos.getCcy()+GlobalConstant.COMMA+line800000.getTotal().setScale(2,RoundingMode.HALF_UP)
//                            +GlobalConstant.COMMA+line800000.getTotal().setScale(2,RoundingMode.HALF_UP);
//                    line800000.setCcyAmount(ccyAmount800000);
                }
            }
        }
        /**  add by dongxiangyang   资本公积设计变更  start
         * 江苏银行个性化.注释掉不再使用 leipeng 20200325
         * */
//    	if(StringUtil.isNotEmpty(svrMonthZbGj)&&svrMonthZbGj.equals("01")){
//        	  //循环去重产品集合
//            for(String str:set){
//                	//根据超代码获取资本公积参数
//                	PbcMonthPrdPostionDto  pdto=pbcMonthAssetReportMapper.selectChbPrdBalance(str, pbcReport.getEnddate());
//                	if(pdto!=null){
//                		//获取资本公积
//                    	setAssetDebtDetailValue(line822000, pdto.getCcy(), (pdto.getEndCost().subtract(pdto.getEndqtysum())), rateMap, pbcReport.getEnddate(),source);
//                    	 //未分配利润
//                     	//setAssetDebtDetailValue(line823000,pdto.getCcy(),line823000.getTotal().subtract(line822000.getTotal()),rateMap, pbcReport.getEnddate(),source);
//                	 }
//            }
//            line823000.setCny(line823000.getCny().subtract(line822000.getCny()));
//         	line823000.setTotal(line823000.getTotal().subtract(line822000.getTotal()));
//         	String ccyAmount = DictCodeListExt1.币种_中债_人民币.getValue()+GlobalConstant.COMMA+line823000.getTotal().setScale(2,RoundingMode.HALF_UP)
//                      +GlobalConstant.COMMA+line823000.getTotal().setScale(2,RoundingMode.HALF_UP);
//         	line823000.setCcyAmount(ccyAmount);
//
//
//      	  //未分配利润
////      	  line823000.setTotal(line823000.getTotal().subtract(line822000.getTotal()));
//
//        }
        /**  add by dongxiangyang   资本公积设计变更  end
         * 江苏银行个性化.注释掉不再使用 leipeng 20200325
         * */
        // 800000 负债及权益合计 = 810000 负债合计+820000权益合计
        // 810000 负债合计 = 811000贷款+812000资产支持证券+813000金融衍生品+814000应付账款+815000其他负债
        // 815000 其他负债：= 700000资产合计-820000权益合计-811000贷款-812000资产支持证券-813000金融衍生品-814000应付账款
//        line815000.setCny(line700000.getCny().subtract(line820000.getCny())
//                .subtract(line811000.getCny()).subtract(line812000.getCny())
//                .subtract(line813000.getCny()).subtract(line814000.getCny()));
//        line815000.setUsdToCny(line700000.getUsdToCny().subtract(line820000.getUsdToCny())
//                .subtract(line811000.getUsdToCny()).subtract(line812000.getUsdToCny())
//                .subtract(line813000.getUsdToCny()).subtract(line814000.getUsdToCny()));
//        line815000.setEurToCny(line700000.getEurToCny().subtract(line820000.getEurToCny())
//                .subtract(line811000.getEurToCny()).subtract(line812000.getEurToCny())
//                .subtract(line813000.getEurToCny()).subtract(line814000.getEurToCny()));
//        line815000.setGbpToCny(line700000.getGbpToCny().subtract(line820000.getGbpToCny())
//                .subtract(line811000.getGbpToCny()).subtract(line812000.getGbpToCny())
//                .subtract(line813000.getGbpToCny()).subtract(line814000.getGbpToCny()));
//        line815000.setJpyToCny(line700000.getJpyToCny().subtract(line820000.getJpyToCny())
//                .subtract(line811000.getJpyToCny()).subtract(line812000.getJpyToCny())
//                .subtract(line813000.getJpyToCny()).subtract(line814000.getJpyToCny()));
//        line815000.setOthToCny(line700000.getOthToCny().subtract(line820000.getOthToCny())
//                .subtract(line811000.getOthToCny()).subtract(line812000.getOthToCny())
//                .subtract(line813000.getOthToCny()).subtract(line814000.getOthToCny()));

//        setAssetDebtDetailValue(line815000, line700000, true, rateMap, pbcReport.getEnddate(),source);
//        setAssetDebtDetailValue(line815000, line820000, false, rateMap, pbcReport.getEnddate(),source);
//        setAssetDebtDetailValue(line815000, line811000, false, rateMap, pbcReport.getEnddate(),source);
//        setAssetDebtDetailValue(line815000, line812000, false, rateMap, pbcReport.getEnddate(),source);
//        setAssetDebtDetailValue(line815000, line813000, false, rateMap, pbcReport.getEnddate(),source);
//        setAssetDebtDetailValue(line815000, line814000, false, rateMap, pbcReport.getEnddate(),source);

//        if(StringUtils.isNotBlank(svrMonthSubtractionLogic)){
//            if(StringUtil.equals(DictCodeListExt1.是或否_是.getValue(), svrMonthSubtractionLogic)){
//                line815000.setTotal(line700000.getTotal().subtract(line820000.getTotal())
//                        .subtract(line811000.getTotal()).subtract(line812000.getTotal())
//                        .subtract(line813000.getTotal()).subtract(line814000.getTotal()));
//                line815000.setCny(line815000.getTotal());
//                String ccyAmount = DictCodeListExt1.币种_中债_人民币.getValue()+GlobalConstant.COMMA+line815000.getTotal().setScale(2,RoundingMode.HALF_UP)
//                        +GlobalConstant.COMMA+line815000.getTotal().setScale(2,RoundingMode.HALF_UP);
//                line815000.setCcyAmount(ccyAmount);
//            }
//        }

        // 810000 负债合计
//        line810000.setCny(line810000.getCny().add(line815000.getCny()));
//        line810000.setUsdToCny(line810000.getUsdToCny().add(line815000.getUsdToCny()));
//        line810000.setEurToCny(line810000.getEurToCny().add(line815000.getEurToCny()));
//        line810000.setGbpToCny(line810000.getGbpToCny().add(line815000.getGbpToCny()));
//        line810000.setJpyToCny(line810000.getJpyToCny().add(line815000.getJpyToCny()));
//        line810000.setOthToCny(line810000.getOthToCny().add(line815000.getOthToCny()));
//        line810000.setTotal(line810000.getTotal().add(line815000.getTotal()));
//        setAssetDebtDetailValue(line810000, line815000, true, rateMap, pbcReport.getEnddate(),source);
//        // 800000 负债及权益合计
//        line800000.setCny(line800000.getCny().add(line815000.getCny()));
//        line800000.setUsdToCny(line800000.getUsdToCny().add(line815000.getUsdToCny()));
//        line800000.setEurToCny(line800000.getEurToCny().add(line815000.getEurToCny()));
//        line800000.setGbpToCny(line800000.getGbpToCny().add(line815000.getGbpToCny()));
//        line800000.setJpyToCny(line800000.getJpyToCny().add(line815000.getJpyToCny()));
//        line800000.setOthToCny(line800000.getOthToCny().add(line815000.getOthToCny()));
//        line800000.setTotal(line800000.getTotal().add(line815000.getTotal()));
//        setAssetDebtDetailValue(line800000, line815000, true, rateMap, pbcReport.getEnddate(),source);
        // 单产品报表
        if (isOnePrd){
//            // 人行单产品报表如果头寸数据为空则不生成对应数据
//            if (CollectionUtils.isEmpty(posAssetList)
//                    && CollectionUtils.isEmpty(posProdList)){
//                pbcReport.setGen(false);
//            }
            // 设置资产池代码 和机构代码
            //  START add by wujian 20200317 多法人改造--单产品报表生成 区分部门  增加createDept值的判断
            String assetPoolCode = "";
            String orgCode = "";
            String createDept = "";
            if (!CollectionUtils.isEmpty(posAssetList)){
                assetPoolCode = posAssetList.get(0).getAssetPoolCode();
                orgCode = posAssetList.get(0).getOrgCode();
                createDept = posAssetList.get(0).getCreateDept();
            }
            if (StringUtil.isEmpty(assetPoolCode) && !CollectionUtils.isEmpty(posProdList)){
                assetPoolCode = posProdList.get(0).getAssetPoolCode();
                orgCode = posProdList.get(0).getOrgCode();
                createDept = posProdList.get(0).getInstitutionCode();
            }
            pbcReport.setPbcAccountId(assetPoolCode);
            pbcReport.setOrgCode(orgCode);
            pbcReport.setCreateDept(createDept);
            //  END add by wujian 20200317 多法人改造--单产品报表生成 区分部门

            // 生成修改未报送
            // 数据的状态:默认新建
            String status = DictCodeListExt2.报送数据状态_新建.getValue();
            // 如果有已报送的数据
            if (!CollectionUtils.isEmpty(prodAssetRegList)){
                // 没有头寸数据，则生成删除未报送
                if (CollectionUtils.isEmpty(posAssetList)
                        && CollectionUtils.isEmpty(posProdList)){
                    // 如果登记数据是删除已报送则不生成
                    if(DictCodeListExt2.报送数据状态_删除.getValue()
                            .equals(prodAssetRegList.get(0).getStatus())){
                        pbcReport.setPbcAccountId(prodAssetRegList.get(0).getPbcAccountId());
                        pbcReport.setOrgCode(prodAssetRegList.get(0).getOrgCode());
                        pbcReport.setGen(false);
                    } else {
                        status = DictCodeListExt2.报送数据状态_删除.getValue();
                    }
                } else {
                    // 如果登记数据是删除已报送则生成新建未报送
                    if(DictCodeListExt2.报送数据状态_删除.getValue()
                            .equals(prodAssetRegList.get(0).getStatus())){
                        status = DictCodeListExt2.报送数据状态_新建.getValue();
                    } else {
                        // 判断数据是否发生变化，发生变化则生成才能修改未报送
                        if (!compareData(result, prodAssetRegList)){
                            status = DictCodeListExt2.报送数据状态_修改.getValue();
                        } else { // 否则不生成报表
                            pbcReport.setPbcAccountId(prodAssetRegList.get(0).getPbcAccountId());
                            pbcReport.setOrgCode(prodAssetRegList.get(0).getOrgCode());
                            pbcReport.setGen(false);
                        }
                    }
                }
            } else { // 没有已报送的数据
                // 没有头寸数据则不生成报表
                if (CollectionUtils.isEmpty(posAssetList)
                        && CollectionUtils.isEmpty(posProdList)){
                    pbcReport.setPbcAccountId(prodAssetRegList.get(0).getPbcAccountId());
                    pbcReport.setOrgCode(prodAssetRegList.get(0).getOrgCode());
                    pbcReport.setGen(false);
                } else{
                    // 没有已报送的数据则生成新建数据
                    status = DictCodeListExt2.报送数据状态_新建.getValue();
                }
            }
            pbcReport.setStatus(status);
            //报送截止日
            Map<String, List<Date>> dateMap = new HashMap<String, List<Date>>();
            List<Date> dateList = DeadlineUtil.getChbEndDate(DictCodeListExt2.报表名称_资产负债信息.getValue(),
                    pbcReport.getStatus(),dateMap, pbcReport.getEnddate(),SVReportGlobalConstant.SVR_SUBMIT_TIME_PBC);
            if (!StringUtil.isEmptyList(dateList)) {
                pbcReport.setChbSubmitDeadline(dateList.get(0));
                pbcReport.setSubmitDeadline(dateList.get(1));
            }
        }


        // 插入数据，先删除明细表数据，再删除主表数据
        //WFPLR倒剪数据处理
        if(!StringUtil.isEmptyList(assetDetailList)){
            assetDetailList.add(pbcMonthReportDetailAssetUtil.buildAssetDetailwfplr(PbcReportProperycode.AMT_823000_WFPLR,result.get(PbcReportProperycode.AMT_823000_WFPLR).getTotal(),generateDate));

        }

        buildSumValAndInster(result, null, pbcReport, !CollectionUtils.isEmpty(posAssetList) ||
                        !CollectionUtils.isEmpty(posProdList) ||
                        DictCodeListExt2.报送数据状态_删除.getValue()
                                .equals(pbcReport.getStatus()), 	StringUtils.isNotBlank(svrMonthSubtractionLogic)&&StringUtil.equals(DictCodeListExt1.是或否_是.getValue(), svrMonthSubtractionLogic)
                ,rateMap, source,prodAssetRegList);

        if(!PbcReportType.PBC_REPORT_ONEPRD_ASSETDEBT.equals(reportType)){
            //循环分支机构的数据组装及入库
            for (Map.Entry<String, Map<String, PbcReportAssetDebtDetailDto>> dtoMap : deptMap.entrySet()) {
                buildSumValAndInster(dtoMap.getValue(), dtoMap.getKey(), pbcReport,!CollectionUtils.isEmpty(posAssetList) ||
                                !CollectionUtils.isEmpty(posProdList) ||
                                DictCodeListExt2.报送数据状态_删除.getValue()
                                        .equals(pbcReport.getStatus()),  StringUtils.isNotBlank(svrMonthSubtractionLogic)&&StringUtil.equals(DictCodeListExt1.是或否_是.getValue(), svrMonthSubtractionLogic)
                        ,rateMap, source,prodAssetRegList);
            }
        }
//        // 插入数据，先删除明细表数据，再删除主表数据
//        // 删除未报送的数据
//        pbcReportAssetDebtDetailMapper.deleteByReportDate(DateUtil.format(pbcReport.getStartdate()),
//                DateUtil.format(pbcReport.getEnddate()),reportType,pbcReport.getPbcAccountId());
//        PbcMonthReport param = new PbcMonthReport();
//        param.setStartdate(pbcReport.getStartdate());
//        param.setEnddate(pbcReport.getEnddate());
//        param.setReporttype(reportType);
//        param.setPbcAccountId(pbcReport.getPbcAccountId());
//        Map<String, Object> paraMap = new HashMap<String, Object>();
//        paraMap.put("startdate", pbcReport.getStartdate());
//        paraMap.put("enddate", pbcReport.getEnddate());
//        paraMap.put("reporttype", reportType);
//        paraMap.put("pbcAccountId", pbcReport.getPbcAccountId());
//        pbcLoanDetailInfoSubMapper.deleteByModel(paraMap);
//        pbcCapitalTrustDetailSubMapper.deleteByModel(paraMap);
//        pbcMonthReportMapper.deleteByModel(param);
//
//        if (DictCodeListExt2.报送数据状态_删除.getValue()
//                .equals(pbcReport.getStatus())){
//            for ( PbcMonthReport dl : prodAssetRegList ) {
//                // 删除未报送的数据
//                pbcReportAssetDebtDetailMapper.deleteByReportDate(DateUtil.format(dl.getStartdate()),
//                        DateUtil.format(dl.getEnddate()),reportType,dl.getPbcAccountId());
//                PbcMonthReport paramDl = new PbcMonthReport();
//                paramDl.setStartdate(dl.getStartdate());
//                paramDl.setEnddate(dl.getEnddate());
//                paramDl.setReporttype(reportType);
//                paramDl.setPbcAccountId(dl.getPbcAccountId());
//                Map<String, Object> paraMap1 = new HashMap<String, Object>();
//                paraMap1.put("startdate", dl.getStartdate());
//                paraMap1.put("enddate", dl.getEnddate());
//                paraMap1.put("reporttype", reportType);
//                paraMap1.put("pbcAccountId", dl.getPbcAccountId());
//                pbcLoanDetailInfoSubMapper.deleteByModel(paraMap1);
//                pbcCapitalTrustDetailSubMapper.deleteByModel(paraMap1);
//                pbcMonthReportMapper.deleteByModel(paramDl);
//
//            }
//        }
//        if (pbcReport.isGen()){
//	        // 判断是否有头寸数据有则插入
//	        // 或者需要生成删除未报送时生成数据
//	        if (!CollectionUtils.isEmpty(posAssetList) ||
//	                !CollectionUtils.isEmpty(posProdList) ||
//	                DictCodeListExt2.报送数据状态_删除.getValue()
//	                .equals(pbcReport.getStatus())){
//	            // 删除未报送的数据根据已报送的数据生成
//	            if (DictCodeListExt2.报送数据状态_删除.getValue()
//	                    .equals(pbcReport.getStatus())){
//                    PbcMonthReport reg = prodAssetRegList.get(0);
//                    String reportuuidNew = IDGenerator.generate(36);
//                    reg.setReportuuid(reportuuidNew);
//                    reg.setSendStatus(DictCodeListExt2.报送状态_未报送.getValue());
//                    reg.setSubmitFeedbackStatus(null);
//                    reg.setSubmitTime(null);
//                    reg.setStatus(DictCodeListExt2.报送数据状态_删除.getValue());
//                    // 插入主表数据
//	                pbcMonthReportMapper.insert(reg);
//	                // 根据reportuuid查询对应的明细信息
//	                String reportuuid = prodAssetRegList.get(0).getReportuuid();
//	                List<PbcReportAssetDebtDetailDto> pbcAssetDetailRegList = pbcReportAssetDebtDetailMapper.selectByReportuuid(reportuuid);
//                    // 插入资产负债表数据
//                    for ( PbcReportAssetDebtDetailDto v : pbcAssetDetailRegList ) {
//                        v.setReportuuid(reportuuidNew);
//                        v.setDetailuuid(IDGenerator.generate(36));
//                        String ss1=setCcyAmountSacle2(v.getCcyAmount());
//                        v.setCcyAmount(ss1);
//                        pbcReportAssetDebtDetailMapper.insert(v);
//                    }
//	            } else {
//    	            // 插入主表数据
//    	            pbcMonthReportMapper.insert(pbcReport);
//    	            // 插入资产负债表数据
//    	            for ( PbcReportAssetDebtDetailDto v:result.values() ) {
//    	            	String ss1=setCcyAmountSacle2(v.getCcyAmount());
//                        v.setCcyAmount(ss1);
//    	                pbcReportAssetDebtDetailMapper.insert(v);
//    	            }
//    	            if (PbcReportType.PBC_REPORT_ONEPRD_ASSETDEBT.equals(reportType) && pbcReport.getPbcAccountId() !=null) {
//						// 生成贷款明细记录 === zhangyf 20191218 start
//						genChbLoanDetailInfo(pbcReport, generateDate);
//						// 生成贷款明细记录 === zhangyf 20191218 end
//						/* 生成生成资金信托类资产投资明细信息 add by gaojian 20191220 begin */
//						genChbCapitalTrustDetailInof(pbcReport, generateDate);
//						/* 生成生成资金信托类资产投资明细信息 add by gaojian 20191220 end */
//    	            }
//	            }
//	        }
//        }

        //返回所有的其他负债
        reback.put("ALL", result.get(PbcReportProperycode.AMT_815000_QTFZ).getTotal());

        for (Map.Entry<String, Map<String, PbcReportAssetDebtDetailDto>> dtoMap : deptMap.entrySet()) {
            reback.put(dtoMap.getKey(), dtoMap.getValue().get(PbcReportProperycode.AMT_815000_QTFZ).getTotal());
        }

        // 返回其他负债
//        return line815000.getTotal();
        return reback;
    }

    /**
     * 生成贷款明细记录
     * @param pbcReport 主表数据
     * @param generateDate 跑批日期
     */
    private void genChbLoanDetailInfo(PbcMonthReport pbcReport, Date generateDate,String deptCode) {
        String loanDetailVeidoo = SvrDataUtil.getSysParam(SVReportGlobalConstant.LOAN_DETAIL_VEIDOO);
        Date dayOfLastMonth  = DateUtil.addMonths(generateDate, -1);
        Date lastDayOfLastMonth  = DateUtil.getLastDay(dayOfLastMonth);
        String firstDayOfLastMonth = DateUtil.format(DateUtil.getFirstDay(dayOfLastMonth));
        Map<String, String> param = new HashMap<>();
        param.put("firstDayOfLastMonth", firstDayOfLastMonth);
        param.put("pbcAccountId", pbcReport.getPbcAccountId());
        param.put("assetType", "load");
        param.put("deptCode", deptCode);
        // 查询贷款资产
        List<PbcMonthAssetPositionDto> assetList = pbcMonthAssetPositionMapper.queryMonthAssetPositionInfoByData(param);
        if (CollectionUtils.isEmpty(assetList)) {
            return;
        }
        List<PbcLoanDetailInfoSub> pbcLoanDetailInfoSubList = pbcMonthAssetPositionMapper.listAllEffectiveLoanDetailInfo();
        if (CollectionUtils.isEmpty(pbcLoanDetailInfoSubList)) {
            return;
        }
        // 资产代码为Key 资产收益权贷款明细基本信息为value
        Map<String, PbcLoanDetailInfoSub> pbcLoanDetailInfoSubMap = new HashMap<String, PbcLoanDetailInfoSub>();
        pbcLoanDetailInfoSubMap = pbcLoanDetailInfoSubList.stream().collect(Collectors.toMap(PbcLoanDetailInfoSub::getAssetCode,
                Function.identity(), (key1, key2) -> key2));
        PbcLoanDetailInfoSub pbcLoanDetailInfoSub;
        List<ChbLoanDetailContractDto> chbLoanDetailContractDtoList=null;
        Map<String, ChbLoanDetailContractDto> pbcLoanDetailContractDtoMap = new HashMap<String, ChbLoanDetailContractDto>();
        ChbLoanDetailContractDto chbLoanDetailContractDto;
        if(loanDetailVeidoo!=null&&"02".equals(loanDetailVeidoo)){
            //查询贷款明细 产品+资产维度原始合同额
            chbLoanDetailContractDtoList = chbLoanDetailContractDtoMapper.selectAllValid();
            // 产品代码和资产代码为Key原始合同额明细为value
            pbcLoanDetailContractDtoMap = chbLoanDetailContractDtoList.stream().collect(JoyinCollectors.toMap(
                    x -> x.getProdId() + GlobalConstant.UNDER_LINE + x.getAssetCode(),x -> x));

        }
        String exchangeSource = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_EXCHANGE_SOURCE);
        for (PbcMonthAssetPositionDto dto : assetList) {
            if (StringUtil.isEmpty(dto.getAssetCode())) {
                continue;
            }
            // 查询：资产收益权贷款明细基本信息表
//			pbcLoanDetailInfoSub = pbcMonthAssetPositionMapper.queryLoanDetailInfoByAssetCode(dto);
            pbcLoanDetailInfoSub = pbcLoanDetailInfoSubMap.get(dto.getAssetCode());
            if(null == pbcLoanDetailInfoSub) {
                continue;
            }
            if(loanDetailVeidoo!=null&&"02".equals(loanDetailVeidoo)){
                chbLoanDetailContractDto=pbcLoanDetailContractDtoMap.get(dto.getProdid()+ GlobalConstant.UNDER_LINE +dto.getAssetCode());
                if(chbLoanDetailContractDto!=null){
                    pbcLoanDetailInfoSub.setOrigContractAmount(chbLoanDetailContractDto.getOrigContractAmount());
                    pbcLoanDetailInfoSub.setOrigContractAmountCny(chbLoanDetailContractDto.getOrigContractAmountCny());
                    pbcLoanDetailInfoSub.setOrigCcy(chbLoanDetailContractDto.getOrigCcy());
                }
            }
            pbcLoanDetailInfoSub.setRegistSeqno(IDGenerator.generate(36));
            pbcLoanDetailInfoSub.setReportuuid(pbcReport.getReportuuid());
            pbcLoanDetailInfoSub.setPbcAccountId(pbcReport.getPbcAccountId());
            pbcLoanDetailInfoSub.setProdId(dto.getProdid());
            pbcLoanDetailInfoSub.setAssetCode(dto.getAssetCode());
            pbcLoanDetailInfoSub.setAmtCcy(dto.getCcy());
            pbcLoanDetailInfoSub.setContractAmount(dto.getAmt());
            // 用共通方法折算
            BigDecimal contractAmountCny = ChbCcyConvertUtil.exchangeMoney(
                    dto.getCcy(),DictCodeListExt1.币种_中债_人民币.getValue(),lastDayOfLastMonth,dto.getAmt(),null,exchangeSource);
            pbcLoanDetailInfoSub.setContractAmountCny(contractAmountCny);
            pbcLoanDetailInfoSub.setCreateDept(deptCode);
            // 贷款明细入库
            pbcLoanDetailInfoSubMapper.insert(pbcLoanDetailInfoSub);
        }
    }

    /**
     *
     * @Description 生成资金信托类资产投资明细信息
     * @Author      高健
     * @Date        2019年12月20日 下午4:01:42
     * @param @param pbcReport
     * @param @param generateDate 参数
     * @return void 返回类型
     * @throws
     */
    private void genChbCapitalTrustDetailInof(PbcMonthReport pbcReport, Date generateDate,String deptCode) {
        Date dayOfLastMonth  = DateUtil.addMonths(generateDate, -1);
        Date lastDayOfLastMonth  = DateUtil.getLastDay(dayOfLastMonth);
        String firstDayOfLastMonth = DateUtil.format(DateUtil.getFirstDay(dayOfLastMonth));
        Map<String, String> param = new HashMap<>();
        param.put("firstDayOfLastMonth", firstDayOfLastMonth);
        param.put("pbcAccountId", pbcReport.getPbcAccountId());
        param.put("assetType", "capital");
        param.put("deptCode", deptCode);
        List<PbcMonthAssetPositionDto> assetList = pbcMonthAssetPositionMapper.queryMonthAssetPositionInfoByData(param);
        if (CollectionUtils.isEmpty(assetList)) {
            return;
        }
        List<PbcCapitalTrustDetailSub> pbcCapitalTrustDetailSubList = pbcMonthAssetPositionMapper.listAllEffectiveCapitalTrustDetailInfo();
        if (CollectionUtils.isEmpty(pbcCapitalTrustDetailSubList)) {
            return;
        }
        // 资产代码为Key 资金信托类资产投资明细信息为value
        Map<String, PbcCapitalTrustDetailSub> pbcCapitalTrustDetailSubMap = new HashMap<String, PbcCapitalTrustDetailSub>();
        pbcCapitalTrustDetailSubMap = pbcCapitalTrustDetailSubList.stream().collect(Collectors.toMap(PbcCapitalTrustDetailSub::getAssetCode,
                Function.identity(), (key1, key2) -> key2));
        String exchangeSource = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_EXCHANGE_SOURCE);
        for (PbcMonthAssetPositionDto dto : assetList) {
            if (StringUtil.isEmpty(dto.getAssetCode())) {
                continue;
            }
//			PbcCapitalTrustDetailSub pbcCapitalTrustDetailSub  = pbcMonthAssetPositionMapper.queryCapitalTrustDetailInfoByAssetCode(dto);
            PbcCapitalTrustDetailSub pbcCapitalTrustDetailSub = pbcCapitalTrustDetailSubMap.get(dto.getAssetCode());
            if(null == pbcCapitalTrustDetailSub) {
                continue;
            }
            // 人行资金信托代码
            String pobcassetid = pbcCapitalTrustDetailSub.getPobcassetid();
            // 产品发起机构编码
            String capitalTrustOrgCode = "";
            if (StringUtil.isNotEmpty(pobcassetid)) {
                capitalTrustOrgCode = pobcassetid.substring(0, pobcassetid.length() >= 6 ? 6 : pobcassetid.length());
            }
            pbcCapitalTrustDetailSub.setCapitalTrustOrgCode(capitalTrustOrgCode);
            pbcCapitalTrustDetailSub.setRegistSeqno(IDGenerator.generate(36));
            pbcCapitalTrustDetailSub.setReportuuid(pbcReport.getReportuuid());
            pbcCapitalTrustDetailSub.setPbcAccountId(pbcReport.getPbcAccountId());
            pbcCapitalTrustDetailSub.setProdId(dto.getProdid());
            pbcCapitalTrustDetailSub.setAssetCode(dto.getAssetCode());
            pbcCapitalTrustDetailSub.setStockType(DEFAULT_STOCK_TYPE);
            pbcCapitalTrustDetailSub.setCcy(dto.getCcy());
            pbcCapitalTrustDetailSub.setCapitalTrustAmount(dto.getAmt());
            pbcCapitalTrustDetailSub.setPobcassetid(pobcassetid);
            // 用共通方法折算
            BigDecimal contractAmountCny = ChbCcyConvertUtil.exchangeMoney(
                    dto.getCcy(),DictCodeListExt1.币种_中债_人民币.getValue(),lastDayOfLastMonth,dto.getAmt(),null,exchangeSource);
            pbcCapitalTrustDetailSub.setCapitalTrustAmountCny(contractAmountCny);
            pbcCapitalTrustDetailSub.setCreateDept(deptCode);

            // 资金信托类资产投资明细信息入库
            pbcCapitalTrustDetailSubMapper.insert(pbcCapitalTrustDetailSub);
        }
    }
    /**
     * 将ss 非人民币 折算后金额保留2位有效数字
     * @param ss
     * @return
     */
    private String setCcyAmountSacle2(String ss){
        if (StringUtil.isNotEmpty(ss)){
            // 按币种分组
            List<String> ccyAmountList = StringUtil.splitToList(GlobalConstant.SEMICOLON, ss);
            Map<String,String> ccyAmountMap = ccyAmountList.stream()
                    .collect(Collectors.toMap(p->
                    {
                        return StringUtil.splitToList(GlobalConstant.COMMA, p).get(0);
                    }, p->p,(key1,key2)->key2));
            for(Map.Entry<String, String> entry : ccyAmountMap.entrySet()){
                if(DictCodeListExt1.币种_中债_人民币.getValue().equals(entry.getKey())){
                    continue;
                }else{
                    String value=entry.getValue();
                    List<String> singleCcyAmountList = StringUtil
                            .splitToList(GlobalConstant.COMMA, value);
                    if(!singleCcyAmountList.isEmpty()&&singleCcyAmountList.size()>=3){
                        String s3=singleCcyAmountList.get(2);
                        BigDecimal bs3=new BigDecimal(s3);
                        bs3=bs3.setScale(2,RoundingMode.HALF_UP);
                        singleCcyAmountList.set(2, bs3.toString());
                    }
                    String newValue=StringUtil.joinString(singleCcyAmountList,GlobalConstant.COMMA);
                    entry.setValue(newValue);
                }
            }
            List<String> ccyAmountResult = new ArrayList<>(ccyAmountMap.values());
            String  ss1 = StringUtil.joinString(ccyAmountResult, GlobalConstant.SEMICOLON);
            return ss1;
        }
        return ss;
    }

    /**
     * 功能:2-2  除回购和拆借外贷款分行业、企业规模统计
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年3月12日
     * @param
     * @return 其他负债值
     * void
     */
    public void generateMonthReportCompanyWithoutDebt(List<PbcMonthAssetPositionDto> posAssetList,Date generateDate,
                                                      Map<String,ChbAssetInfo> assetMap,boolean isEdit){

        // 生成月度报表主表数据
        PbcMonthReport pbcReport = genMonthReportByReporttype(PbcReportType.PBC_REPORT_2_2,generateDate,isEdit,false);
        Map<String,PbcReportCompanyDetailDto> result = genAssetMonthReportemplate(generateDate,  PbcReportType.PBC_REPORT_2_2, 23,pbcReport.getReportuuid());
        if(null==result){
            return;
        }
        Map<String,Map<String,PbcReportCompanyDetailDto>> deptMap = new HashMap();
        // 汇率取值开关
        String source = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_EXCHANGE_SOURCE_PBC);
        // 根据template定义行编号对应数据
        /*PbcReportCompanyDetailDto line900000 = result.get(PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ);
        PbcReportCompanyDetailDto line910000 = result.get(PbcReportProperycode.AMT_910000_NLMYY);
        PbcReportCompanyDetailDto line920000 = result.get(PbcReportProperycode.AMT_920000_CKY);
        PbcReportCompanyDetailDto line930000 = result.get(PbcReportProperycode.AMT_930000_ZZY);
        PbcReportCompanyDetailDto line940000 = result.get(PbcReportProperycode.AMT_940000_DLRLRQJSSCHGYY);
        PbcReportCompanyDetailDto line950000 = result.get(PbcReportProperycode.AMT_950000_JZY);
        PbcReportCompanyDetailDto line960000 = result.get(PbcReportProperycode.AMT_960000_PFHLSY);
        PbcReportCompanyDetailDto line970000 = result.get(PbcReportProperycode.AMT_970000_JTYSCCHYZY);
        PbcReportCompanyDetailDto line980000 = result.get(PbcReportProperycode.AMT_980000_ZSHCYY);
        PbcReportCompanyDetailDto line990000 = result.get(PbcReportProperycode.AMT_990000_XXCSRJHXXJSFWY);
        PbcReportCompanyDetailDto line9a0000 = result.get(PbcReportProperycode.AMT_9a0000_JRY);
        PbcReportCompanyDetailDto line9b0000 = result.get(PbcReportProperycode.AMT_9b0000_FDCY);
        PbcReportCompanyDetailDto line9c0000 = result.get(PbcReportProperycode.AMT_9c0000_ZLHSWFWY);
        PbcReportCompanyDetailDto line9d0000 = result.get(PbcReportProperycode.AMT_9d0000_KXYJHJSFWY);
        PbcReportCompanyDetailDto line9e0000 = result.get(PbcReportProperycode.AMT_9e0000_SLHJHGGSSGLY);
        PbcReportCompanyDetailDto line9f0000 = result.get(PbcReportProperycode.AMT_9f0000_JMFWXLHQTFWY);
        PbcReportCompanyDetailDto line9g0000 = result.get(PbcReportProperycode.AMT_9g0000_JY);
        PbcReportCompanyDetailDto line9h0000 = result.get(PbcReportProperycode.AMT_9h0000_WSHSHGZ);
        PbcReportCompanyDetailDto line9i0000 = result.get(PbcReportProperycode.AMT_9i0000_WHTYHYLY);
        PbcReportCompanyDetailDto line9j0000 = result.get(PbcReportProperycode.AMT_9j0000_GGGLSHBZHSHZZ);
        PbcReportCompanyDetailDto line9k0000 = result.get(PbcReportProperycode.AMT_9k0000_GJZZ);
        PbcReportCompanyDetailDto line9m0000 = result.get(PbcReportProperycode.AMT_9m0000_JNGR);
        PbcReportCompanyDetailDto line9n0000 = result.get(PbcReportProperycode.AMT_9n0000_JW);*/

        // 汇率map,可重复使用
        Map<String, BigDecimal> rateMap = new HashMap<>();
        for(PbcMonthAssetPositionDto pos :posAssetList){
            // 统计资产一级分类=P05 贷款&&资产二级分类=R02 其他记录的金额字段
            if (PbcMonthReportConstants.POSITION_TYPE_ZC.equals(pos.getPositionType())
                    &&PbcMonthReportConstants.PBC_ASSETTYPE1_P05.equals(pos.getAssetType1())
                    && PbcMonthReportConstants.PBC_ASSETTYPE2_R02.equals(pos.getAssetType2())) {
                //查询资产要素信息获取发行机构所属行业一级分类和发行机构类型（按规模划分）
                ChbAssetInfo assetinfo = assetMap.get(pos.getAssetCode());
                // 去除科目余额
                if(assetinfo == null){
                    continue;
                }
                // 发行机构所属行业一级分类
                String industryType1 = null;
                if (assetinfo != null){
                    industryType1 = assetinfo.getIndustry();
//                    if (StringUtils.isEmpty(industryType1)){
//                        industryType1 = assetinfo.getIndustryPbc();
//                    }
                }
                if(null!=pos.getInstitutionCode()&&null==deptMap.get(pos.getInstitutionCode())){
                    Map<String,PbcReportCompanyDetailDto> singDeptMap = genAssetMonthReportemplate(generateDate,  PbcReportType.PBC_REPORT_2_2, 23,pbcReport.getReportuuid());
                    deptMap.put(pos.getInstitutionCode(), singDeptMap);
                }
                // 发行机构类型（按规模划分）
                String  companySize = "";
                // 客户主体类型
                String customerType = assetinfo.getCustomerObjType();
                // 如果CUSTOMER_TYPE 为C03 非金融企业、C04 金融机构（实体）、C05 特定目的载体 则需根据CHB_ASSET_INFO资产的“发行机构类型（按规模划分）”区分
                if (StringUtil.isNotEmpty(customerType)){
                    // 其他（住户、广义政府及境外）,不统计到企业里面
                    // CUSTOMER_TYPE 为C01 住户、C02统计到境内个人  广义政府和C06 境外时统计到其他（住户、广义政府及境外）
                    if (DictCodeListExt2.客户一级类型_非金融企业.getValue().equals(customerType)
                            || DictCodeListExt2.客户一级类型_金融机构.getValue().equals(customerType)
                            || DictCodeListExt2.客户一级类型_特定目的载体.getValue().equals(customerType)){
                        //companySize = assetinfo.getInstitSize();
                        companySize = assetinfo.getPartyType1();
                    } else if (DictCodeListExt2.客户一级类型_住户.getValue().equals(customerType)){
                        //setCompanyDetailValue(pos, line9m0000, rateMap, source, null,true);
                        //setCompanyDetailValue(pos, line900000, rateMap, source, null,true);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9m0000_JNGR,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, null, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, null, null);
                    } else if (DictCodeListExt2.客户一级类型_广义政府.getValue().equals(customerType)){
                        //setCompanyDetailValue(pos, line9j0000, rateMap, source, null,true);
                        //setCompanyDetailValue(pos, line900000, rateMap, source, null,true);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9j0000_GGGLSHBZHSHZZ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, null, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, null, null);
                    }else if (DictCodeListExt2.客户一级类型_境外.getValue().equals(customerType)){
                        //setCompanyDetailValue(pos, line9n0000, rateMap, source, null,true);
                        //setCompanyDetailValue(pos, line900000, rateMap, source, null,true);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9n0000_JW,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, null, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, null, null);
                    }
                }
                // 过滤C01 C06时的数据
                if (StringUtil.isEmpty(companySize)){
                    continue;
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 910000   农、林、牧、渔业
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_01.equals(industryType1)){
                    //setCompanyDetailValue(pos, line910000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_910000_NLMYY,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 920000   采矿业
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_02.equals(industryType1)){
                    //setCompanyDetailValue(pos, line920000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_920000_CKY,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 930000   制造业
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_03.equals(industryType1)){
                    //setCompanyDetailValue(pos, line930000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_930000_ZZY,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 940000   电力、热力、燃气及水生产和供应业
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_04.equals(industryType1)){
                    //setCompanyDetailValue(pos, line940000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_940000_DLRLRQJSSCHGYY,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 950000   建筑业
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_05.equals(industryType1)){
                    //setCompanyDetailValue(pos, line950000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_950000_JZY,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 960000   批发和零售业
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_06.equals(industryType1)){
                    //setCompanyDetailValue(pos, line960000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_960000_PFHLSY,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 970000   交通运输、仓储和邮政业
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_07.equals(industryType1)){
                    //setCompanyDetailValue(pos, line970000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_970000_JTYSCCHYZY,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 980000   住宿和餐饮业
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_08.equals(industryType1)){
                    //setCompanyDetailValue(pos, line980000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_980000_ZSHCYY,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 990000   信息传输、软件和信息技术服务业
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_09.equals(industryType1)){
                    //setCompanyDetailValue(pos, line990000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_990000_XXCSRJHXXJSFWY,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 9a0000   金融业
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_10.equals(industryType1)){
                    //setCompanyDetailValue(pos, line9a0000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9a0000_JRY,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 9b0000   房地产业
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_11.equals(industryType1)){
                    //setCompanyDetailValue(pos, line9b0000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9b0000_FDCY,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 9c0000   租赁和商务服务业
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_12.equals(industryType1)){
                    //setCompanyDetailValue(pos, line9c0000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9c0000_ZLHSWFWY,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 9d0000   科学研究和技术服务业
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_13.equals(industryType1)){
                    //setCompanyDetailValue(pos, line9d0000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9d0000_KXYJHJSFWY,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 9e0000   水利、环境和公共设施管理业
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_14.equals(industryType1)){
                    //setCompanyDetailValue(pos, line9e0000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9e0000_SLHJHGGSSGLY,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 9f0000   居民服务、修理和其他服务业
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_15.equals(industryType1)){
                    //setCompanyDetailValue(pos, line9f0000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9f0000_JMFWXLHQTFWY,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 9g0000   教育
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_16.equals(industryType1)){
                    //setCompanyDetailValue(pos, line9g0000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9g0000_JY,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 9h0000   卫生和社会工作
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_17.equals(industryType1)){
                    //setCompanyDetailValue(pos, line9h0000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9h0000_WSHSHGZ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 9i0000   文化、体育和娱乐业
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_18.equals(industryType1)){
                    //setCompanyDetailValue(pos, line9i0000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9i0000_WHTYHYLY,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 9j0000   公共管理、社会保障和社会组织 + "多种行业" + 空
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_19.equals(industryType1)
                        || StringUtil.isEmpty(industryType1)){
                    //setCompanyDetailValue(pos, line9j0000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9j0000_GGGLSHBZHSHZZ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
                //  900000 除回购和拆借外贷款余额总计 = 910000   农、林、牧、渔业  +920000   采矿业 + 930000   制造业
                //                 +940000   电力、热力、燃气及水生产和供应业 + 950000   建筑业
                //                 +960000   批发和零售业 + 970000   交通运输、仓储和邮政业  + 980000   住宿和餐饮业
                //                 +990000   信息传输、软件和信息技术服务业  + 9a0000   金融业 +  9b0000   房地产业
                //                 +9c0000   租赁和商务服务业 + 9d0000   科学研究和技术服务业 + 9e0000   水利、环境和公共设施管理业
                //                 +9f0000   居民服务、修理和其他服务业  + 9g0000   教育  + 9h0000   卫生和社会工作 + 9i0000   文化、体育和娱乐业
                //                 +9j0000   公共管理、社会保障和社会组织 +9k0000   国际组织 +9m0000     境内个人 +9n0000   境外
                // 9k0000   国际组织
                if (PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_20.equals(industryType1)){
                    //setCompanyDetailValue(pos, line9k0000, rateMap, source, companySize,false);
                    //setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9k0000_GJZZ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                }
            }
        }
        // 插入数据，先删除明细表数据，再删除主表数据
        buildSumValAndInster(result, null, pbcReport, !CollectionUtils.isEmpty(posAssetList),false,null,null,null);
        //循环分支机构的数据组装及入库
        for (Map.Entry<String, Map<String, PbcReportCompanyDetailDto>> dtoMap : deptMap.entrySet()) {
            buildSumValAndInster(dtoMap.getValue(), dtoMap.getKey(), pbcReport, !CollectionUtils.isEmpty(posAssetList),false,null,null,null);
        }

        /*if (pbcReport.isGen()){
	        pbcReportCompanyDetailMapper.deleteByReportDate(DateUtil.format(pbcReport.getStartdate()),
	                DateUtil.format(pbcReport.getEnddate()),PbcReportType.PBC_REPORT_2_2,null);
	        PbcMonthReport param = new PbcMonthReport();
	        param.setStartdate(pbcReport.getStartdate());
	        param.setEnddate(pbcReport.getEnddate());
	        param.setReporttype(PbcReportType.PBC_REPORT_2_2);
	        pbcMonthReportMapper.deleteByModel(param);
	        // 判断是否有头寸数据有则插入
	        if (!CollectionUtils.isEmpty(posAssetList)){
	            // 插入主表数据
	            pbcMonthReportMapper.insert(pbcReport);
	            // 插入资产负债表数据
	            for ( PbcReportCompanyDetailDto v:result.values() ) {
	                pbcReportCompanyDetailMapper.insert(v);
	            }
	        }
        }*/
    }

    /**
     *
     * @Description 2-2  除回购和拆借外贷款分行业、企业规模统计
     * @Author      高健
     * @Date        2019年12月18日 下午3:24:08
     * @param @param posAssetList
     * @param @param generateDate
     * @param @param chbLoadMap
     * @param @param isEdit 参数
     * @return void 返回类型
     * @throws
     */
    public void generateMonthReportCompanyWithoutDebtEx(List<PbcMonthAssetPositionDto> posAssetList,Date generateDate,
                                                        Map<String,ChbLoanDetailInfo> chbLoandMap,boolean isEdit){


        // 生成月度报表主表数据
        PbcMonthReport pbcReport = genMonthReportByReporttype(PbcReportType.PBC_REPORT_2_2,generateDate,isEdit,false);

        //update by leipeng 20200319  汇总数据提取全量模板
        Map<String,PbcReportCompanyDetailDto> result = genAssetMonthReportemplate(generateDate,  PbcReportType.PBC_REPORT_2_2, 23,pbcReport.getReportuuid());
//        List<PbcReportCompanyDetailDto> tjList=new ArrayList<PbcReportCompanyDetailDto>();

        if(null==result){
            return;
        }
        Map<String,Map<String,PbcReportCompanyDetailDto>> deptMap = new HashMap();


//    	Map<String,PbcReportCompanyDetailDto> result = new HashMap<String,PbcReportCompanyDetailDto>();
//        // 查询产品月报需要统计的项
//        List<PbcReportTemplateDto> listKey = pbcReportTemplateMapper.selectByReporttype(PbcReportType.PBC_REPORT_2_2,
//                DictCodeListExt1.维护数据状态_有效.getValue());
//        if ( listKey.size() != 23 || listKey.size() == 0)  {
//            logger.error("月报模板配置错误，请检查");
//            return ;
//        }
//        // 生成月度报表主表数据
//        PbcMonthReport pbcReport = genMonthReportByReporttype(PbcReportType.PBC_REPORT_2_2,generateDate,isEdit,false);
//        for ( PbcReportTemplateDto k:listKey ) {
//            PbcReportCompanyDetailDto detail = new PbcReportCompanyDetailDto();
//            detail.setDetailuuid(IDGenerator.generate(36));
//            detail.setReporttype(PbcReportType.PBC_REPORT_2_2);
//            detail.setPropertycode(k.getPropertycode());
//            detail.setReportuuid(pbcReport.getReportuuid());
//            detail.setLargeCompanyAmt(BigDecimal.ZERO);
//            detail.setMediumCompanyAmt(BigDecimal.ZERO);
//            detail.setMicroCompanyAmt(BigDecimal.ZERO);
//            detail.setSmallCompanyAmt(BigDecimal.ZERO);
//            detail.setSumAmt(BigDecimal.ZERO);
//			detail.setOthAmt(BigDecimal.ZERO);
//            result.put(k.getPropertycode(),detail);
//        }
        // 汇率取值开关
        String source = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_EXCHANGE_SOURCE_PBC);
        // 根据template定义行编号对应数据
//        PbcReportCompanyDetailDto line900000 = result.get(PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ);
//        PbcReportCompanyDetailDto line910000 = result.get(PbcReportProperycode.AMT_910000_NLMYY);
//        PbcReportCompanyDetailDto line920000 = result.get(PbcReportProperycode.AMT_920000_CKY);
//        PbcReportCompanyDetailDto line930000 = result.get(PbcReportProperycode.AMT_930000_ZZY);
//        PbcReportCompanyDetailDto line940000 = result.get(PbcReportProperycode.AMT_940000_DLRLRQJSSCHGYY);
//        PbcReportCompanyDetailDto line950000 = result.get(PbcReportProperycode.AMT_950000_JZY);
//        PbcReportCompanyDetailDto line960000 = result.get(PbcReportProperycode.AMT_960000_PFHLSY);
//        PbcReportCompanyDetailDto line970000 = result.get(PbcReportProperycode.AMT_970000_JTYSCCHYZY);
//        PbcReportCompanyDetailDto line980000 = result.get(PbcReportProperycode.AMT_980000_ZSHCYY);
//        PbcReportCompanyDetailDto line990000 = result.get(PbcReportProperycode.AMT_990000_XXCSRJHXXJSFWY);
//        PbcReportCompanyDetailDto line9a0000 = result.get(PbcReportProperycode.AMT_9a0000_JRY);
//        PbcReportCompanyDetailDto line9b0000 = result.get(PbcReportProperycode.AMT_9b0000_FDCY);
//        PbcReportCompanyDetailDto line9c0000 = result.get(PbcReportProperycode.AMT_9c0000_ZLHSWFWY);
//        PbcReportCompanyDetailDto line9d0000 = result.get(PbcReportProperycode.AMT_9d0000_KXYJHJSFWY);
//        PbcReportCompanyDetailDto line9e0000 = result.get(PbcReportProperycode.AMT_9e0000_SLHJHGGSSGLY);
//        PbcReportCompanyDetailDto line9f0000 = result.get(PbcReportProperycode.AMT_9f0000_JMFWXLHQTFWY);
//        PbcReportCompanyDetailDto line9g0000 = result.get(PbcReportProperycode.AMT_9g0000_JY);
//        PbcReportCompanyDetailDto line9h0000 = result.get(PbcReportProperycode.AMT_9h0000_WSHSHGZ);
//        PbcReportCompanyDetailDto line9i0000 = result.get(PbcReportProperycode.AMT_9i0000_WHTYHYLY);
//        PbcReportCompanyDetailDto line9j0000 = result.get(PbcReportProperycode.AMT_9j0000_GGGLSHBZHSHZZ);
//        PbcReportCompanyDetailDto line9k0000 = result.get(PbcReportProperycode.AMT_9k0000_GJZZ);
//        PbcReportCompanyDetailDto line9m0000 = result.get(PbcReportProperycode.AMT_9m0000_JNGR);
//        PbcReportCompanyDetailDto line9n0000 = result.get(PbcReportProperycode.AMT_9n0000_JW);

        // 汇率map,可重复使用
        Map<String, BigDecimal> rateMap = new HashMap<>();
        for(PbcMonthAssetPositionDto pos :posAssetList){
            // 统计资产一级分类=P05 贷款&&资产二级分类=R02 其他记录的金额字段
            if (PbcMonthReportConstants.POSITION_TYPE_ZC.equals(pos.getPositionType())
                    &&PbcMonthReportConstants.PBC_ASSETTYPE1_P05.equals(pos.getAssetType1())
                    && PbcMonthReportConstants.PBC_ASSETTYPE2_R02.equals(pos.getAssetType2())) {
                //查询资产收益权贷款明细基本信息获取行业信息、借款人类型、企业规模
                ChbLoanDetailInfo chbLoanDetailInfo = chbLoandMap.get(pos.getAssetCode());
                // 该资产无资产收益权贷款明细基本信息
                if(chbLoanDetailInfo == null){
                    continue;
                }

                // add by leipeng 20200319 获取所有部门对应信息,并列出单独的表结构
                if(null!=pos.getInstitutionCode()&&null==deptMap.get(pos.getInstitutionCode())){
                    Map<String,PbcReportCompanyDetailDto> singDeptMap = genAssetMonthReportemplate(generateDate,  PbcReportType.PBC_REPORT_2_2, 23,pbcReport.getReportuuid());
                    deptMap.put(pos.getInstitutionCode(), singDeptMap);
                }


                // 借款人类型
                String borrowerType = chbLoanDetailInfo.getBorrowerType();
                // 是否通过借款人类型判断标识
                boolean borrowerTypeFlag = false;
                // 借款人类型=1 住户 则统计至： “境内个人”的E其他（住户、广义政府及境外），即H28单元格
                if (StringUtil.equals(PbcMonthReportConstants.SVR_DEPT_TYPE_1, borrowerType)) {


                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9m0000_JNGR,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, null, null);

//                	setCompanyDetailValue(pos, line9m0000, rateMap, source, null,false);
//                	setCompanyDetailValue(pos, line900000, rateMap, source, null,false);
                    borrowerTypeFlag = true;
                    // 借款人类型=2 广义政府 则统计至：“公共管理、社会保障和社会组织”的E其他（住户、广义政府及境外），即H26单元格
                } else if (StringUtil.equals(PbcMonthReportConstants.SVR_DEPT_TYPE_2, borrowerType)) {

                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9j0000_GGGLSHBZHSHZZ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, null, null);

//                	setCompanyDetailValue(pos, line9j0000, rateMap, source, null,false);
//                	setCompanyDetailValue(pos, line900000, rateMap, source, null,false);
                    borrowerTypeFlag = true;
                    // 借款人类型=6 境外 则统计至：“境外”的E其他（住户、广义政府及境外），即H29单元格
                } else if (StringUtil.equals(PbcMonthReportConstants.SVR_DEPT_TYPE_6, borrowerType)) {
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9n0000_JW,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                            PbcReportType.PBC_REPORT_2_2,rateMap, source, null, null);

//                	setCompanyDetailValue(pos, line9n0000, rateMap, source, null,false);
//                	setCompanyDetailValue(pos, line900000, rateMap, source, null,false);
                    borrowerTypeFlag = true;
                }
                // 若已经通过借款人类型统计，不用进行接下来操作
                if (borrowerTypeFlag) {
                    continue;
                }
                // 行业信息
                String industryInfo = chbLoanDetailInfo.getIndustryInfo();
                // 企业规模
                String enterpriseScale = chbLoanDetailInfo.getEnterpriseScale();
                if (StringUtil.isEmpty(enterpriseScale)) {
                    continue;
                }
                // 企业规模实际值
                String  companySize = ENTERPRISE_SCALE_MAP.get(enterpriseScale);
                if (StringUtil.isEmpty(companySize)) {
                    continue;
                }
                // 行业信息为空，统计到公共管理、社会保障和社会组织中
                if (StringUtil.isEmpty(industryInfo)) {
                    industryInfo = PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_S;
                }
                switch(industryInfo) {
                    // 910000   农、林、牧、渔业
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_A:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_910000_NLMYY,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);

//                		setCompanyDetailValue(pos, line910000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 920000   采矿业
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_B:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_920000_CKY,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
//                		setCompanyDetailValue(pos, line920000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 930000   制造业
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_C:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_930000_ZZY,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
//                		setCompanyDetailValue(pos, line930000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 940000   电力、热力、燃气及水生产和供应业
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_D:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_940000_DLRLRQJSSCHGYY,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);

//                		setCompanyDetailValue(pos, line940000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 950000   建筑业
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_E:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_950000_JZY,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
//                		setCompanyDetailValue(pos, line950000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 960000   批发和零售业
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_F:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_960000_PFHLSY,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);

//                		setCompanyDetailValue(pos, line960000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 970000   交通运输、仓储和邮政业
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_G:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_970000_JTYSCCHYZY,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
//                		setCompanyDetailValue(pos, line970000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 980000   住宿和餐饮业
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_H:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_980000_ZSHCYY,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
//                		setCompanyDetailValue(pos, line980000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 990000   信息传输、软件和信息技术服务业
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_I:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_990000_XXCSRJHXXJSFWY,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
//                		setCompanyDetailValue(pos, line990000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 9a0000   金融业
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_J:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9a0000_JRY,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
//                		setCompanyDetailValue(pos, line9a0000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 9b0000   房地产业
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_K:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9b0000_FDCY,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
//                		setCompanyDetailValue(pos, line9b0000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 9c0000   租赁和商务服务业
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_L:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9c0000_ZLHSWFWY,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);

//                		setCompanyDetailValue(pos, line9c0000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 9d0000   科学研究和技术服务业
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_M:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9d0000_KXYJHJSFWY,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);

//                		setCompanyDetailValue(pos, line9d0000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 9e0000   水利、环境和公共设施管理业
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_N:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9e0000_SLHJHGGSSGLY,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);

//                		setCompanyDetailValue(pos, line9e0000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 9f0000   居民服务、修理和其他服务业
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_O:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9f0000_JMFWXLHQTFWY,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);

//                		setCompanyDetailValue(pos, line9f0000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 9g0000   教育
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_P:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9g0000_JY,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);

//                		setCompanyDetailValue(pos, line9g0000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 9h0000   卫生和社会工作
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_Q:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9h0000_WSHSHGZ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);

//                		setCompanyDetailValue(pos, line9h0000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 9i0000   文化、体育和娱乐业
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_R:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9i0000_WHTYHYLY,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);

//                		setCompanyDetailValue(pos, line9i0000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 9j0000   公共管理、社会保障和社会组织
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_S:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9j0000_GGGLSHBZHSHZZ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);

//                		setCompanyDetailValue(pos, line9j0000, rateMap, source, companySize,false);
//                        setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                    // 9k0000   国际组织
                    case PbcMonthReportConstants.SVR_INDUSTRY_DOOR_CODE_T:
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_9k0000_GJZZ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);
                        setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_900000_CHGHCJWDKYEZJ,
                                PbcReportType.PBC_REPORT_2_2,rateMap, source, companySize, null);

//                		setCompanyDetailValue(pos, line9k0000, rateMap, source, companySize,false);
//                      setCompanyDetailValue(pos, line900000, rateMap, source, companySize,false);
                        break;
                }
            }
        }
        // 插入数据，先删除明细表数据，再删除主表数据

        buildSumValAndInster(result, null, pbcReport, !CollectionUtils.isEmpty(posAssetList),false,null,null,null);
        //循环分支机构的数据组装及入库
        for (Map.Entry<String, Map<String, PbcReportCompanyDetailDto>> dtoMap : deptMap.entrySet()) {
            buildSumValAndInster(dtoMap.getValue(), dtoMap.getKey(), pbcReport, !CollectionUtils.isEmpty(posAssetList),false,null,null,null);
        }

//        if (pbcReport.isGen()){
//	        pbcReportCompanyDetailMapper.deleteByReportDate(DateUtil.format(pbcReport.getStartdate()),
//	                DateUtil.format(pbcReport.getEnddate()),PbcReportType.PBC_REPORT_2_2,null);
//	        PbcMonthReport param = new PbcMonthReport();
//	        param.setStartdate(pbcReport.getStartdate());
//	        param.setEnddate(pbcReport.getEnddate());
//	        param.setReporttype(PbcReportType.PBC_REPORT_2_2);
//	        pbcMonthReportMapper.deleteByModel(param);
//	        // 判断是否有头寸数据有则插入
//	        if (!CollectionUtils.isEmpty(posAssetList)){
//	            // 插入主表数据
//	            pbcMonthReportMapper.insert(pbcReport);
//	            // 插入资产负债表数据
//	            for ( PbcReportCompanyDetailDto v:result.values() ) {
//	                pbcReportCompanyDetailMapper.insert(v);
//	            }
//	        }
//        }
    }

    /**
     * 功能:2-3除回购和拆借外贷款分地区统计
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年3月12日
     * @param
     * @return 其他负债值
     * void
     */
    public void generateMonthReportAreaWithoutDebt(List<PbcMonthAssetPositionDto> posAssetList,Date generateDate,
                                                   Map<String,ChbAssetInfo> assetMap,boolean isEdit){

        // 生成月度报表主表数据
        PbcMonthReport pbcReport = genMonthReportByReporttype(PbcReportType.PBC_REPORT_2_3,generateDate,isEdit,false);
        Map<String,PbcReportCompanyDetailDto> result = genAssetMonthReportemplate(generateDate,  PbcReportType.PBC_REPORT_2_3, 37,pbcReport.getReportuuid());
        Map<String,Map<String,PbcReportCompanyDetailDto>> deptMap = new HashMap();
        // 汇率取值开关
        String source = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_EXCHANGE_SOURCE_PBC);
        // 根据template定义行编号对应数据
        /*PbcReportAreaDetailDto linea00000 = result.get(PbcReportProperycode.AMT_a00000_ZJ);
        PbcReportAreaDetailDto linea01000 = result.get(PbcReportProperycode.AMT_a01000_BJ);
        PbcReportAreaDetailDto linea02000 = result.get(PbcReportProperycode.AMT_a02000_TJ);
        PbcReportAreaDetailDto linea03000 = result.get(PbcReportProperycode.AMT_a03000_HB);
        PbcReportAreaDetailDto linea04000 = result.get(PbcReportProperycode.AMT_a04000_SX);
        PbcReportAreaDetailDto linea05000 = result.get(PbcReportProperycode.AMT_a05000_NMG);
        PbcReportAreaDetailDto linea06000 = result.get(PbcReportProperycode.AMT_a06000_LN);
        PbcReportAreaDetailDto linea06001 = result.get(PbcReportProperycode.AMT_a06001_QZDL);
        PbcReportAreaDetailDto linea07000 = result.get(PbcReportProperycode.AMT_a07000_JL);
        PbcReportAreaDetailDto linea08000 = result.get(PbcReportProperycode.AMT_a08000_HLJ);
        PbcReportAreaDetailDto linea09000 = result.get(PbcReportProperycode.AMT_a09000_SH);
        PbcReportAreaDetailDto linea10000 = result.get(PbcReportProperycode.AMT_a10000_JS);
        PbcReportAreaDetailDto linea11000 = result.get(PbcReportProperycode.AMT_a11000_ZJ);
        PbcReportAreaDetailDto linea11001 = result.get(PbcReportProperycode.AMT_a11001_QZNB);
        PbcReportAreaDetailDto linea12000 = result.get(PbcReportProperycode.AMT_a12000_AH);
        PbcReportAreaDetailDto linea13000 = result.get(PbcReportProperycode.AMT_a13000_FJ);
        PbcReportAreaDetailDto linea13001 = result.get(PbcReportProperycode.AMT_a13001_QZSM);
        PbcReportAreaDetailDto linea14000 = result.get(PbcReportProperycode.AMT_a14000_JX);
        PbcReportAreaDetailDto linea15000 = result.get(PbcReportProperycode.AMT_a15000_SD);
        PbcReportAreaDetailDto linea15001 = result.get(PbcReportProperycode.AMT_a15001_QZQD);
        PbcReportAreaDetailDto linea16000 = result.get(PbcReportProperycode.AMT_a16000_HN);
        PbcReportAreaDetailDto linea17000 = result.get(PbcReportProperycode.AMT_a17000_HB);
        PbcReportAreaDetailDto linea18000 = result.get(PbcReportProperycode.AMT_a18000_HN);
        PbcReportAreaDetailDto linea19000 = result.get(PbcReportProperycode.AMT_a19000_GD);
        PbcReportAreaDetailDto linea19001 = result.get(PbcReportProperycode.AMT_a19001_QZSZ);
        PbcReportAreaDetailDto linea20000 = result.get(PbcReportProperycode.AMT_a20000_GX);
        PbcReportAreaDetailDto linea21000 = result.get(PbcReportProperycode.AMT_a21000_HN);
        PbcReportAreaDetailDto linea22000 = result.get(PbcReportProperycode.AMT_a22000_ZQ);
        PbcReportAreaDetailDto linea23000 = result.get(PbcReportProperycode.AMT_a23000_SC);
        PbcReportAreaDetailDto linea24000 = result.get(PbcReportProperycode.AMT_a24000_GZ);
        PbcReportAreaDetailDto linea25000 = result.get(PbcReportProperycode.AMT_a25000_YN);
        PbcReportAreaDetailDto linea26000 = result.get(PbcReportProperycode.AMT_a26000_XZ);
        PbcReportAreaDetailDto linea27000 = result.get(PbcReportProperycode.AMT_a27000_SX);
        PbcReportAreaDetailDto linea28000 = result.get(PbcReportProperycode.AMT_a28000_GS);
        PbcReportAreaDetailDto linea29000 = result.get(PbcReportProperycode.AMT_a29000_QH);
        PbcReportAreaDetailDto linea30000 = result.get(PbcReportProperycode.AMT_a30000_NX);
        PbcReportAreaDetailDto linea31000 = result.get(PbcReportProperycode.AMT_a31000_XJ);*/

        // 汇率map,可重复使用
        Map<String, BigDecimal> rateMap = new HashMap<>();
        for(PbcMonthAssetPositionDto pos :posAssetList){
            // 统计资产一级分类=P05 贷款&&资产二级分类=R02 其他记录的金额字段
            if (PbcMonthReportConstants.POSITION_TYPE_ZC.equals(pos.getPositionType())
                    &&PbcMonthReportConstants.PBC_ASSETTYPE1_P05.equals(pos.getAssetType1())
                    && PbcMonthReportConstants.PBC_ASSETTYPE2_R02.equals(pos.getAssetType2())) {
                //查询资产要素信息获取融资人所属地区
                ChbAssetInfo assetinfo = assetMap.get(pos.getAssetCode());
                // 去除科目余额
                if(assetinfo == null){
                    continue;
                }
                // 过滤客户主体类型CUSTOMER_TYPE in （C02、C03、C04、C05）
                String customerType = assetinfo.getCustomerObjType();
                if (!DictCodeListExt2.客户一级类型_广义政府.getValue().equals(customerType) && !DictCodeListExt2.客户一级类型_非金融企业.getValue().equals(customerType) && !DictCodeListExt2.客户一级类型_金融机构.getValue().equals(customerType) && !DictCodeListExt2.客户一级类型_特定目的载体.getValue().equals(customerType)){
                    continue;
                }
                // 发行机构所属行业一级分类
                String finArea = null;
                if (assetinfo != null){
                    finArea = assetinfo.getBuyPlace();
                }
                if (StringUtil.isEmpty(finArea)){
                    continue;
                }
                if(null!=pos.getInstitutionCode()&&null==deptMap.get(pos.getInstitutionCode())){
                    Map<String,PbcReportCompanyDetailDto> singDeptMap = genAssetMonthReportemplate(generateDate,  PbcReportType.PBC_REPORT_2_3, 37,pbcReport.getReportuuid());
                    deptMap.put(pos.getInstitutionCode(), singDeptMap);
                }
                // 所属省
                String province = finArea;
                // 通过融资人所属地区获取上级省
                if (SvrDataUtil.getNameFromDataDict(DictCode.行政区划_县, province) != null){
                    province = SvrDataUtil.getParentCodeFromDataDict(DictCode.行政区划_县, province);
                }
                if (SvrDataUtil.getNameFromDataDict(DictCode.行政区划_市, province) != null){
                    province = SvrDataUtil.getParentCodeFromDataDict(DictCode.行政区划_市, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a01000 北京
                if (PbcMonthReportConstants.PBC_PROVINCE_BJa01000.equals(province)){
                    //setAreaDetailValue(pos, linea01000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a01000_BJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a02000 天津
                if (PbcMonthReportConstants.PBC_PROVINCE_TJa02000.equals(province)){
                    //setAreaDetailValue(pos, linea02000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a02000_TJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a03000 河北
                if (PbcMonthReportConstants.PBC_PROVINCE_HBa03000.equals(province)){
                    //setAreaDetailValue(pos, linea03000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a03000_HB,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a04000 山西
                if (PbcMonthReportConstants.PBC_PROVINCE_SXa04000.equals(province)){
                    //setAreaDetailValue(pos, linea04000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a04000_SX,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a05000 内蒙古
                if (PbcMonthReportConstants.PBC_PROVINCE_NMGa05000.equals(province)){
                    //setAreaDetailValue(pos, linea05000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a05000_NMG,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a06000 辽宁
                if (PbcMonthReportConstants.PBC_PROVINCE_LNa06000.equals(province)){
                    //setAreaDetailValue(pos, linea06000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a06000_LN,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a06001 其中：大连
                if (PbcMonthReportConstants.PBC_PROVINCE_QZDLa06001.equals(finArea)){
                    //setAreaDetailValue(pos, linea06001, rateMap, source, province);
                    //setAreaDetailValue(pos, linea06000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a06001_QZDL,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a06000_LN,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a07000 吉林
                if (PbcMonthReportConstants.PBC_PROVINCE_JLa07000.equals(province)){
                    //setAreaDetailValue(pos, linea07000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a07000_JL,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a08000 黑龙江
                if (PbcMonthReportConstants.PBC_PROVINCE_HLJa08000.equals(province)){
                    //setAreaDetailValue(pos, linea08000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a08000_HLJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a09000 上海
                if (PbcMonthReportConstants.PBC_PROVINCE_SHa09000.equals(province)){
                    //setAreaDetailValue(pos, linea09000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a09000_SH,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a10000 江苏
                if (PbcMonthReportConstants.PBC_PROVINCE_JSa10000.equals(province)){
                    //setAreaDetailValue(pos, linea10000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a10000_JS,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a11000 浙江
                if (PbcMonthReportConstants.PBC_PROVINCE_ZJa11000.equals(province)){
                    //setAreaDetailValue(pos, linea11000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a11000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a11001 其中：宁波
                if (PbcMonthReportConstants.PBC_PROVINCE_QZNBa11001.equals(finArea)){
                    //setAreaDetailValue(pos, linea11001, rateMap, source, province);
                    //setAreaDetailValue(pos, linea11000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a11001_QZNB,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a11000_ZJ,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a12000 安徽
                if (PbcMonthReportConstants.PBC_PROVINCE_AHa12000.equals(province)){
                    //setAreaDetailValue(pos, linea12000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a12000_AH,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a13000 福建
                if (PbcMonthReportConstants.PBC_PROVINCE_FJa13000.equals(province)){
                    //setAreaDetailValue(pos, linea13000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a13000_FJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a13001 其中：厦门
                if (PbcMonthReportConstants.PBC_PROVINCE_QZSMa13001.equals(finArea)){
                    //setAreaDetailValue(pos, linea13001, rateMap, source, province);
                    //setAreaDetailValue(pos, linea13000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a13001_QZSM,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a13000_FJ,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a14000 江西
                if (PbcMonthReportConstants.PBC_PROVINCE_JXa14000.equals(province)){
                    //setAreaDetailValue(pos, linea14000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a14000_JX,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a15000 山东
                if (PbcMonthReportConstants.PBC_PROVINCE_SDa15000.equals(province)){
                    //setAreaDetailValue(pos, linea15000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a15000_SD,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a15001 其中：青岛
                if (PbcMonthReportConstants.PBC_PROVINCE_QZQDa15001.equals(finArea)){
                    //setAreaDetailValue(pos, linea15001, rateMap, source, province);
                    //setAreaDetailValue(pos, linea15000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a15001_QZQD,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a15000_SD,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a16000 河南
                if (PbcMonthReportConstants.PBC_PROVINCE_HNa16000.equals(province)){
                    //setAreaDetailValue(pos, linea16000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a16000_HN,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a17000 湖北
                if (PbcMonthReportConstants.PBC_PROVINCE_HBa17000.equals(province)){
                    //setAreaDetailValue(pos, linea17000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a17000_HB,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a18000 湖南
                if (PbcMonthReportConstants.PBC_PROVINCE_HNa18000.equals(province)){
                    //setAreaDetailValue(pos, linea18000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a18000_HN,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a19000 广东
                if (PbcMonthReportConstants.PBC_PROVINCE_GDa19000.equals(province)){
                    //setAreaDetailValue(pos, linea19000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a19000_GD,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a19001 其中：深圳
                if (PbcMonthReportConstants.PBC_PROVINCE_QZSZa19001.equals(finArea)){
                    //setAreaDetailValue(pos, linea19001, rateMap, source, province);
                    //setAreaDetailValue(pos, linea19000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a19001_QZSZ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a19000_GD,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a20000 广西
                if (PbcMonthReportConstants.PBC_PROVINCE_GXa20000.equals(province)){
                    //setAreaDetailValue(pos, linea20000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a20000_GX,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a21000 海南
                if (PbcMonthReportConstants.PBC_PROVINCE_HNa21000.equals(province)){
                    //setAreaDetailValue(pos, linea21000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a21000_HN,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a22000 重庆
                if (PbcMonthReportConstants.PBC_PROVINCE_ZQa22000.equals(province)){
                    //setAreaDetailValue(pos, linea22000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a22000_ZQ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a23000 四川
                if (PbcMonthReportConstants.PBC_PROVINCE_SCa23000.equals(province)){
                    //setAreaDetailValue(pos, linea23000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a23000_SC,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a24000 贵州
                if (PbcMonthReportConstants.PBC_PROVINCE_GZa24000.equals(province)){
                    //setAreaDetailValue(pos, linea24000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a24000_GZ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a25000 云南
                if (PbcMonthReportConstants.PBC_PROVINCE_YNa25000.equals(province)){
                    //setAreaDetailValue(pos, linea25000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a25000_YN,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a26000 西藏
                if (PbcMonthReportConstants.PBC_PROVINCE_XZa26000.equals(province)){
                    //setAreaDetailValue(pos, linea26000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a26000_XZ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a27000 陕西
                if (PbcMonthReportConstants.PBC_PROVINCE_SXa27000.equals(province)){
                    //setAreaDetailValue(pos, linea27000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a27000_SX,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a28000 甘肃
                if (PbcMonthReportConstants.PBC_PROVINCE_GSa28000.equals(province)){
                    //setAreaDetailValue(pos, linea28000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a28000_GS,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a29000 青海
                if (PbcMonthReportConstants.PBC_PROVINCE_QHa29000.equals(province)){
                    //setAreaDetailValue(pos, linea29000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a29000_QH,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a30000 宁夏
                if (PbcMonthReportConstants.PBC_PROVINCE_NXa30000.equals(province)){
                    //setAreaDetailValue(pos, linea30000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a30000_NX,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a31000 新疆
                if (PbcMonthReportConstants.PBC_PROVINCE_XJa31000.equals(province)){
                    //setAreaDetailValue(pos, linea31000, rateMap, source, province);
                    //setAreaDetailValue(pos, linea00000, rateMap, source, province);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a31000_XJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, province, null);
                }
            }
        }
        // 插入数据，先删除明细表数据，再删除主表数据
        //修改数据组装及入库公共方法
        buildSumValAndInster(result, null, pbcReport, !CollectionUtils.isEmpty(posAssetList),false,null,null,null);
        //循环分支机构的数据组装及入库
        for (Map.Entry<String, Map<String, PbcReportCompanyDetailDto>> dtoMap : deptMap.entrySet()) {
            buildSumValAndInster(dtoMap.getValue(), dtoMap.getKey(), pbcReport, !CollectionUtils.isEmpty(posAssetList),false,null,null,null);
        }
        /*if (pbcReport.isGen()){
	        pbcReportAreaDetailMapper.deleteByReportDate(DateUtil.format(pbcReport.getStartdate()),
	                DateUtil.format(pbcReport.getEnddate()),PbcReportType.PBC_REPORT_2_3,null);
	        PbcMonthReport param = new PbcMonthReport();
	        param.setStartdate(pbcReport.getStartdate());
	        param.setEnddate(pbcReport.getEnddate());
	        param.setReporttype(PbcReportType.PBC_REPORT_2_3);
	        pbcMonthReportMapper.deleteByModel(param);
	        // 判断是否有头寸数据有则插入
	        if (!CollectionUtils.isEmpty(posAssetList)){
	            // 插入主表数据
	            pbcMonthReportMapper.insert(pbcReport);
	            // 插入资产负债表数据
	            for ( PbcReportAreaDetailDto v:result.values() ) {
	                pbcReportAreaDetailMapper.insert(v);
	            }
	        }
        }*/
    }

    /**
     *
     * @Description 功能:2-3除回购和拆借外贷款分地区统计
     * @Author      高健
     * @Date        2019年12月18日 下午4:47:21
     * @param @param posAssetList
     * @param @param generateDate
     * @param @param chbLoandMap
     * @param @param isEdit 参数
     * @return void 返回类型
     * @throws
     */
    public void generateMonthReportAreaWithoutDebtEx(List<PbcMonthAssetPositionDto> posAssetList,Date generateDate,
                                                     Map<String, ChbLoanDetailInfo> chbLoandMap,boolean isEdit){
        // 生成月度报表主表数据
        PbcMonthReport pbcReport = genMonthReportByReporttype(PbcReportType.PBC_REPORT_2_3,generateDate,isEdit,false);

        //update by leipeng 20200319  汇总数据提取全量模板
        Map<String,PbcReportCompanyDetailDto> result = genAssetMonthReportemplate(generateDate,  PbcReportType.PBC_REPORT_2_3, 37,pbcReport.getReportuuid());
//        List<PbcReportCompanyDetailDto> tjList=new ArrayList<PbcReportCompanyDetailDto>();

        if(null==result){

            return;
        }
        Map<String,Map<String,PbcReportCompanyDetailDto>> deptMap = new HashMap();
        // 汇率取值开关
        String source = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_EXCHANGE_SOURCE_PBC);
        // 根据template定义行编号对应数据
//        PbcReportAreaDetailDto linea00000 = result.get(PbcReportProperycode.AMT_a00000_ZJ);
//        PbcReportAreaDetailDto linea01000 = result.get(PbcReportProperycode.AMT_a01000_BJ);
//        PbcReportAreaDetailDto linea02000 = result.get(PbcReportProperycode.AMT_a02000_TJ);
//        PbcReportAreaDetailDto linea03000 = result.get(PbcReportProperycode.AMT_a03000_HB);
//        PbcReportAreaDetailDto linea04000 = result.get(PbcReportProperycode.AMT_a04000_SX);
//        PbcReportAreaDetailDto linea05000 = result.get(PbcReportProperycode.AMT_a05000_NMG);
//        PbcReportAreaDetailDto linea06000 = result.get(PbcReportProperycode.AMT_a06000_LN);
//        PbcReportAreaDetailDto linea06001 = result.get(PbcReportProperycode.AMT_a06001_QZDL);
//        PbcReportAreaDetailDto linea07000 = result.get(PbcReportProperycode.AMT_a07000_JL);
//        PbcReportAreaDetailDto linea08000 = result.get(PbcReportProperycode.AMT_a08000_HLJ);
//        PbcReportAreaDetailDto linea09000 = result.get(PbcReportProperycode.AMT_a09000_SH);
//        PbcReportAreaDetailDto linea10000 = result.get(PbcReportProperycode.AMT_a10000_JS);
//        PbcReportAreaDetailDto linea11000 = result.get(PbcReportProperycode.AMT_a11000_ZJ);
//        PbcReportAreaDetailDto linea11001 = result.get(PbcReportProperycode.AMT_a11001_QZNB);
//        PbcReportAreaDetailDto linea12000 = result.get(PbcReportProperycode.AMT_a12000_AH);
//        PbcReportAreaDetailDto linea13000 = result.get(PbcReportProperycode.AMT_a13000_FJ);
//        PbcReportAreaDetailDto linea13001 = result.get(PbcReportProperycode.AMT_a13001_QZSM);
//        PbcReportAreaDetailDto linea14000 = result.get(PbcReportProperycode.AMT_a14000_JX);
//        PbcReportAreaDetailDto linea15000 = result.get(PbcReportProperycode.AMT_a15000_SD);
//        PbcReportAreaDetailDto linea15001 = result.get(PbcReportProperycode.AMT_a15001_QZQD);
//        PbcReportAreaDetailDto linea16000 = result.get(PbcReportProperycode.AMT_a16000_HN);
//        PbcReportAreaDetailDto linea17000 = result.get(PbcReportProperycode.AMT_a17000_HB);
//        PbcReportAreaDetailDto linea18000 = result.get(PbcReportProperycode.AMT_a18000_HN);
//        PbcReportAreaDetailDto linea19000 = result.get(PbcReportProperycode.AMT_a19000_GD);
//        PbcReportAreaDetailDto linea19001 = result.get(PbcReportProperycode.AMT_a19001_QZSZ);
//        PbcReportAreaDetailDto linea20000 = result.get(PbcReportProperycode.AMT_a20000_GX);
//        PbcReportAreaDetailDto linea21000 = result.get(PbcReportProperycode.AMT_a21000_HN);
//        PbcReportAreaDetailDto linea22000 = result.get(PbcReportProperycode.AMT_a22000_ZQ);
//        PbcReportAreaDetailDto linea23000 = result.get(PbcReportProperycode.AMT_a23000_SC);
//        PbcReportAreaDetailDto linea24000 = result.get(PbcReportProperycode.AMT_a24000_GZ);
//        PbcReportAreaDetailDto linea25000 = result.get(PbcReportProperycode.AMT_a25000_YN);
//        PbcReportAreaDetailDto linea26000 = result.get(PbcReportProperycode.AMT_a26000_XZ);
//        PbcReportAreaDetailDto linea27000 = result.get(PbcReportProperycode.AMT_a27000_SX);
//        PbcReportAreaDetailDto linea28000 = result.get(PbcReportProperycode.AMT_a28000_GS);
//        PbcReportAreaDetailDto linea29000 = result.get(PbcReportProperycode.AMT_a29000_QH);
//        PbcReportAreaDetailDto linea30000 = result.get(PbcReportProperycode.AMT_a30000_NX);
//        PbcReportAreaDetailDto linea31000 = result.get(PbcReportProperycode.AMT_a31000_XJ);

        // 汇率map,可重复使用
        Map<String, BigDecimal> rateMap = new HashMap<>();
        for(PbcMonthAssetPositionDto pos :posAssetList){
            // 统计资产一级分类=P05 贷款&&资产二级分类=R02 其他记录的金额字段
            if (PbcMonthReportConstants.POSITION_TYPE_ZC.equals(pos.getPositionType())
                    &&PbcMonthReportConstants.PBC_ASSETTYPE1_P05.equals(pos.getAssetType1())
                    && PbcMonthReportConstants.PBC_ASSETTYPE2_R02.equals(pos.getAssetType2())) {
                //查询资产要素信息获取融资人所属地区
                ChbLoanDetailInfo chbLoanDetailInfo = chbLoandMap.get(pos.getAssetCode());
                // 去除科目余额
                if(chbLoanDetailInfo == null){
                    continue;
                }
                // 去除借款人类型不在(2， 3， 4， 5)的资产
                String borrowerType = chbLoanDetailInfo.getBorrowerType();
                if (!StringUtil.equals(PbcMonthReportConstants.SVR_DEPT_TYPE_2, borrowerType)
                        && !StringUtil.equals(PbcMonthReportConstants.SVR_DEPT_TYPE_3, borrowerType)
                        && !StringUtil.equals(PbcMonthReportConstants.SVR_DEPT_TYPE_4, borrowerType)
                        && !StringUtil.equals(PbcMonthReportConstants.SVR_DEPT_TYPE_5, borrowerType)) {
                    continue;
                }
                // 发行机构所属行业一级分类
                String finArea = chbLoanDetailInfo.getBorrowerCity();
//                if (assetinfo != null){
//                    finArea = assetinfo.getBuyPlace();
//                }
                if (StringUtil.isEmpty(finArea)){
                    continue;
                }


                // add by leipeng 20200319 获取所有部门对应信息,并列出单独的表结构
                if(null!=pos.getInstitutionCode()&&null==deptMap.get(pos.getInstitutionCode())){
                    Map<String,PbcReportCompanyDetailDto> singDeptMap = genAssetMonthReportemplate(generateDate,  PbcReportType.PBC_REPORT_2_3, 37,pbcReport.getReportuuid());
                    deptMap.put(pos.getInstitutionCode(), singDeptMap);
                }

                // 所属省
                String province = finArea;
                // 通过融资人所属地区获取上级省
                if (SvrDataUtil.getNameFromDataDict(DictCode.行政区划_县, province) != null){
                    province = SvrDataUtil.getParentCodeFromDataDict(DictCode.行政区划_县, province);
                }
                if (SvrDataUtil.getNameFromDataDict(DictCode.行政区划_市, province) != null){
                    province = SvrDataUtil.getParentCodeFromDataDict(DictCode.行政区划_市, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a01000 北京
                if (PbcMonthReportConstants.PBC_PROVINCE_BJa01000.equals(province)){


                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a01000_BJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
//
//                	setAssetReportValue(pos, linea01000, rateMap, source, province);
//                	setAssetReportValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a02000 天津
                if (PbcMonthReportConstants.PBC_PROVINCE_TJa02000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a02000_TJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea02000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a03000 河北
                if (PbcMonthReportConstants.PBC_PROVINCE_HBa03000.equals(province)){

                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a03000_HB,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
//                    setAreaDetailValue(pos, linea03000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a04000 山西
                if (PbcMonthReportConstants.PBC_PROVINCE_SXa04000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a04000_SX,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea04000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a05000 内蒙古
                if (PbcMonthReportConstants.PBC_PROVINCE_NMGa05000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a05000_NMG,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea05000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a06000 辽宁
                if (PbcMonthReportConstants.PBC_PROVINCE_LNa06000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a06000_LN,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea06000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a06001 其中：大连
                if (PbcMonthReportConstants.PBC_PROVINCE_QZDLa06001.equals(finArea)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a06001_QZDL,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a06000_LN,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
//                    setAreaDetailValue(pos, linea06001, rateMap, source, province);
//                    setAreaDetailValue(pos, linea06000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a07000 吉林
                if (PbcMonthReportConstants.PBC_PROVINCE_JLa07000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a07000_JL,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
//                    setAreaDetailValue(pos, linea07000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a08000 黑龙江
                if (PbcMonthReportConstants.PBC_PROVINCE_HLJa08000.equals(province)){

                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a08000_HLJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
//                    setAreaDetailValue(pos, linea08000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a09000 上海
                if (PbcMonthReportConstants.PBC_PROVINCE_SHa09000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a09000_SH,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
//                    setAreaDetailValue(pos, linea09000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a10000 江苏
                if (PbcMonthReportConstants.PBC_PROVINCE_JSa10000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a10000_JS,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
//                    setAreaDetailValue(pos, linea10000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a11000 浙江
                if (PbcMonthReportConstants.PBC_PROVINCE_ZJa11000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a11000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
//                    setAreaDetailValue(pos, linea11000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a11001 其中：宁波
                if (PbcMonthReportConstants.PBC_PROVINCE_QZNBa11001.equals(finArea)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a11001_QZNB,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a11000_ZJ,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea11001, rateMap, source, province);
//                    setAreaDetailValue(pos, linea11000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a12000 安徽
                if (PbcMonthReportConstants.PBC_PROVINCE_AHa12000.equals(province)){

                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a12000_AH,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
//                    setAreaDetailValue(pos, linea12000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a13000 福建
                if (PbcMonthReportConstants.PBC_PROVINCE_FJa13000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a13000_FJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
//                    setAreaDetailValue(pos, linea13000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a13001 其中：厦门
                if (PbcMonthReportConstants.PBC_PROVINCE_QZSMa13001.equals(finArea)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a13001_QZSM,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a13000_FJ,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea13001, rateMap, source, province);
//                    setAreaDetailValue(pos, linea13000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a14000 江西
                if (PbcMonthReportConstants.PBC_PROVINCE_JXa14000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a14000_JX,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea14000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a15000 山东
                if (PbcMonthReportConstants.PBC_PROVINCE_SDa15000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a15000_SD,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
//                    setAreaDetailValue(pos, linea15000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a15001 其中：青岛
                if (PbcMonthReportConstants.PBC_PROVINCE_QZQDa15001.equals(finArea)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a15001_QZQD,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a15000_SD,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea15001, rateMap, source, province);
//                    setAreaDetailValue(pos, linea15000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a16000 河南
                if (PbcMonthReportConstants.PBC_PROVINCE_HNa16000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a16000_HN,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea16000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a17000 湖北
                if (PbcMonthReportConstants.PBC_PROVINCE_HBa17000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a17000_HB,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea17000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a18000 湖南
                if (PbcMonthReportConstants.PBC_PROVINCE_HNa18000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a18000_HN,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea18000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a19000 广东
                if (PbcMonthReportConstants.PBC_PROVINCE_GDa19000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a19000_GD,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea19000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a19001 其中：深圳
                if (PbcMonthReportConstants.PBC_PROVINCE_QZSZa19001.equals(finArea)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a19001_QZSZ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a19000_GD,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    //setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                    //		PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea19001, rateMap, source, province);
//                    setAreaDetailValue(pos, linea19000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a20000 广西
                if (PbcMonthReportConstants.PBC_PROVINCE_GXa20000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a20000_GX,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea20000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a21000 海南
                if (PbcMonthReportConstants.PBC_PROVINCE_HNa21000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a21000_HN,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea21000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a22000 重庆
                if (PbcMonthReportConstants.PBC_PROVINCE_ZQa22000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a22000_ZQ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea22000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a23000 四川
                if (PbcMonthReportConstants.PBC_PROVINCE_SCa23000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a23000_SC,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea23000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a24000 贵州
                if (PbcMonthReportConstants.PBC_PROVINCE_GZa24000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a24000_GZ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea24000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a25000 云南
                if (PbcMonthReportConstants.PBC_PROVINCE_YNa25000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a25000_YN,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea25000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a26000 西藏
                if (PbcMonthReportConstants.PBC_PROVINCE_XZa26000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a26000_XZ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea26000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a27000 陕西
                if (PbcMonthReportConstants.PBC_PROVINCE_SXa27000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a27000_SX,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea27000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a28000 甘肃
                if (PbcMonthReportConstants.PBC_PROVINCE_GSa28000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a28000_GS,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea28000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a29000 青海
                if (PbcMonthReportConstants.PBC_PROVINCE_QHa29000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a29000_QH,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea29000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a30000 宁夏
                if (PbcMonthReportConstants.PBC_PROVINCE_NXa30000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a30000_NX,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea30000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
                //  a00000 总计 =  a01000 北京+ a02000 天津+ a03000 河北+ a04000 山西
                //              + a05000 内蒙古+ a06000 辽宁+ a06001 其中：大连+ a07000 吉林
                //              + a08000 黑龙江+ a09000 上海+ a10000 江苏+ a11000 浙江
                //              + a11001 其中：宁波+ a12000 安徽+ a13000 福建
                //              + a13001 其中：厦门+ a14000 江西+ a15000 山东
                //              + a15001 其中：青岛+ a16000 河南+ a17000 湖北
                //              + a18000 湖南+ a19000 广东+ a19001 其中：深圳+ a20000 广西
                //              + a21000 海南+ a22000 重庆+ a23000 四川+ a24000 贵州
                //              + a25000 云南+ a26000 西藏+ a27000 陕西+ a28000 甘肃+ a29000 青海
                //              + a30000 宁夏+ a31000 新疆
                // a31000 新疆
                if (PbcMonthReportConstants.PBC_PROVINCE_XJa31000.equals(province)){
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a31000_XJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);
                    setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_a00000_ZJ,
                            PbcReportType.PBC_REPORT_2_3,rateMap, source, null, null);

//                    setAreaDetailValue(pos, linea31000, rateMap, source, province);
//                    setAreaDetailValue(pos, linea00000, rateMap, source, province);
                }
            }
        }



        //修改数据组装及入库公共方法
        buildSumValAndInster(result, null, pbcReport, !CollectionUtils.isEmpty(posAssetList),false,null,null,null);
        //循环分支机构的数据组装及入库
        for (Map.Entry<String, Map<String, PbcReportCompanyDetailDto>> dtoMap : deptMap.entrySet()) {
            buildSumValAndInster(dtoMap.getValue(), dtoMap.getKey(), pbcReport, !CollectionUtils.isEmpty(posAssetList),false,null,null,null);
        }

        // 插入数据，先删除明细表数据，再删除主表数据
//        if (pbcReport.isGen()){
//	        pbcReportAreaDetailMapper.deleteByReportDate(DateUtil.format(pbcReport.getStartdate()),
//	                DateUtil.format(pbcReport.getEnddate()),PbcReportType.PBC_REPORT_2_3);
//	        PbcMonthReport param = new PbcMonthReport();
//	        param.setStartdate(pbcReport.getStartdate());
//	        param.setEnddate(pbcReport.getEnddate());
//	        param.setReporttype(PbcReportType.PBC_REPORT_2_3);
//	        pbcMonthReportMapper.deleteByModel(param);
//	        // 判断是否有头寸数据有则插入
//	        if (!CollectionUtils.isEmpty(posAssetList)){
//	            // 插入主表数据
//	            pbcMonthReportMapper.insert(pbcReport);
//	            // 插入资产负债表数据
//	            for ( PbcReportAreaDetailDto v:result.values() ) {
//	                pbcReportAreaDetailMapper.insert(v);
//	            }
//	        }
//        }
    }

    /**
     * 功能:生成表2-4 资产收益权按基础资产投向分类统计表
     * @since 1.6.0
     * @version 1.6.0
     * @author：yuluan 2019年3月12日
     * @param
     *
     */
    public void generateMonthAssetReportInCome(List<PbcMonthAssetPositionDto> posAssetList,Date generateDate,
                                               Map<String,ChbAssetInfo> assetMap,boolean isEdit){

        // 生成月度报表主表数据
        PbcMonthReport pbcReport = genMonthReportByReporttype(PbcReportType.PBC_REPORT_2_4,generateDate,isEdit,false);
        Map<String,PbcReportAssetIncomeDetail> result = genAssetMonthReportemplate(generateDate,  PbcReportType.PBC_REPORT_2_4, 7,pbcReport.getReportuuid());
        if(null==result){
            return;
        }

        Map<String,Map<String,PbcReportAssetIncomeDetail>> deptMap = new HashMap();

        // 根据template定义行编号对应数据
        /* PbcReportAssetIncomeDetail lineb00000 = result.get(PbcReportProperycode.AMT_B00000_ZJ);
         List<PbcReportAssetIncomeDetail> zjList=new ArrayList<PbcReportAssetIncomeDetail>();
         PbcReportAssetIncomeDetail lineb10000 = result.get(PbcReportProperycode.AMT_B10000_ZH);
         PbcReportAssetIncomeDetail lineb20000 = result.get(PbcReportProperycode.AMT_B20000_GYZF);
         PbcReportAssetIncomeDetail lineb30000 = result.get(PbcReportProperycode.AMT_B30000_FJRQY);
         PbcReportAssetIncomeDetail lineb40000 = result.get(PbcReportProperycode.AMT_B40000_JRJGST);
         PbcReportAssetIncomeDetail lineb50000 = result.get(PbcReportProperycode.AMT_B50000_TDMDZT);
         PbcReportAssetIncomeDetail lineb60000 = result.get(PbcReportProperycode.AMT_B60000_JW);
         zjList.add(lineb10000);
         zjList.add(lineb20000);
         zjList.add(lineb30000);
         zjList.add(lineb40000);
         zjList.add(lineb50000);
         zjList.add(lineb60000);*/
        Map<String, BigDecimal> rateMap = new HashMap<>();
        // 汇率取值开关
        String source = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_EXCHANGE_SOURCE_PBC);
        for(PbcMonthAssetPositionDto pos :posAssetList){
            // add by leipeng 20200319 获取所有部门对应信息,并列出单独的表结构
            if(null!=pos.getInstitutionCode()&&null==deptMap.get(pos.getInstitutionCode())){
                Map<String,PbcReportAssetIncomeDetail> singDeptMap = genAssetMonthReportemplate(generateDate,  PbcReportType.PBC_REPORT_2_4, 7,pbcReport.getReportuuid());
                deptMap.put(pos.getInstitutionCode(), singDeptMap);
            }
            //只取收/受益权
            if(pos.getAssetType1()!=null&&PbcMonthReportConstants.PBC_ASSETTYPE1_P06.equals(pos.getAssetType1())){
        			 /*setAssetReportInComeValue(pos,lineb10000,rateMap,source,"ZH",assetMap);
        			 setAssetReportInComeValue(pos,lineb20000,rateMap,source,"GYZF",assetMap);
        			 setAssetReportInComeValue(pos,lineb30000,rateMap,source,"FJRQY",assetMap);
        			 setAssetReportInComeValue(pos,lineb40000,rateMap,source,"JRJGST",assetMap);
        			 setAssetReportInComeValue(pos,lineb50000,rateMap,source,"TDMDZT",assetMap);
        			 setAssetReportInComeValue(pos,lineb60000,rateMap,source,"JW",assetMap);*/

                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_B10000_ZH, PbcReportType.PBC_REPORT_2_4,
                        rateMap, source, "ZH", assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_B20000_GYZF, PbcReportType.PBC_REPORT_2_4,
                        rateMap, source, "GYZF", assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_B30000_FJRQY, PbcReportType.PBC_REPORT_2_4,
                        rateMap, source, "FJRQY", assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_B40000_JRJGST, PbcReportType.PBC_REPORT_2_4,
                        rateMap, source, "JRJGST", assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_B50000_TDMDZT, PbcReportType.PBC_REPORT_2_4,
                        rateMap, source, "TDMDZT", assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_B60000_JW, PbcReportType.PBC_REPORT_2_4,
                        rateMap, source, "JW", assetMap);
            }
        }
         /*BigDecimal hjLoanIncomeRight=BigDecimal.ZERO;
         BigDecimal hjDebtSecuritiesIncomeRights=BigDecimal.ZERO;
         BigDecimal hjReceivableIncomeRights=BigDecimal.ZERO;
         BigDecimal hjOtherCreditorRights=BigDecimal.ZERO;
         BigDecimal hjStockIncomeRight=BigDecimal.ZERO;
         BigDecimal hjOtherEquityIncomeRights=BigDecimal.ZERO;
         BigDecimal hjPropertyRights=BigDecimal.ZERO;
        for(PbcReportAssetIncomeDetail single:zjList){
        	 BigDecimal sgLoanIncomeRight=single.getLoanIncomeRight()==null?BigDecimal.ZERO:single.getLoanIncomeRight();
             BigDecimal sgDebtSecuritiesIncomeRights=single.getDebtSecuritiesIncomeRights()==null?BigDecimal.ZERO:single.getDebtSecuritiesIncomeRights();
             BigDecimal sgReceivableIncomeRights=single.getReceivableIncomeRights()==null?BigDecimal.ZERO:single.getReceivableIncomeRights();
             BigDecimal sgOtherCreditorRights=single.getOtherCreditorRights()==null?BigDecimal.ZERO:single.getOtherCreditorRights();
             BigDecimal sgStockIncomeRight=single.getStockIncomeRight()==null?BigDecimal.ZERO:single.getStockIncomeRight();
             BigDecimal sgOtherEquityIncomeRights=single.getOtherEquityIncomeRights()==null?BigDecimal.ZERO:single.getOtherEquityIncomeRights();
             BigDecimal sgPropertyRights=single.getPropertyRights()==null?BigDecimal.ZERO:single.getPropertyRights();
        	hjLoanIncomeRight=hjLoanIncomeRight.add(sgLoanIncomeRight);
        	hjDebtSecuritiesIncomeRights=hjDebtSecuritiesIncomeRights.add(sgDebtSecuritiesIncomeRights);
        	hjReceivableIncomeRights=hjReceivableIncomeRights.add(sgReceivableIncomeRights);
        	hjOtherCreditorRights=hjOtherCreditorRights.add(sgOtherCreditorRights);
        	hjStockIncomeRight=hjStockIncomeRight.add(sgStockIncomeRight);
        	hjOtherEquityIncomeRights=hjOtherEquityIncomeRights.add(sgOtherEquityIncomeRights);
        	hjPropertyRights=hjPropertyRights.add(sgPropertyRights);
        	BigDecimal sghj=sgLoanIncomeRight.add(sgDebtSecuritiesIncomeRights).add(sgReceivableIncomeRights)
        			.add(sgOtherCreditorRights).add(sgStockIncomeRight).add(sgOtherEquityIncomeRights).add(sgPropertyRights);
        	single.setTotal(sghj);
        }
        BigDecimal totalhj=hjLoanIncomeRight.add(hjDebtSecuritiesIncomeRights).add(hjReceivableIncomeRights)
    			.add(hjOtherCreditorRights).add(hjStockIncomeRight).add(hjOtherEquityIncomeRights).add(hjPropertyRights);
         lineb00000.setLoanIncomeRight(hjLoanIncomeRight);
         lineb00000.setDebtSecuritiesIncomeRights(hjDebtSecuritiesIncomeRights);
         lineb00000.setReceivableIncomeRights(hjReceivableIncomeRights);
         lineb00000.setOtherCreditorRights(hjOtherCreditorRights);
         lineb00000.setStockIncomeRight(hjStockIncomeRight);
         lineb00000.setOtherEquityIncomeRights(hjOtherEquityIncomeRights);
         lineb00000.setPropertyRights(hjPropertyRights);
         lineb00000.setTotal(totalhj);*/
        // 插入数据，先删除明细表数据，再删除主表数据
        //修改数据组装及入库公共方法
        buildSumValAndInster(result, null, pbcReport, !CollectionUtils.isEmpty(posAssetList),false,null,null,null);
        //循环分支机构的数据组装及入库
        for (Map.Entry<String, Map<String, PbcReportAssetIncomeDetail>> dtoMap : deptMap.entrySet()) {
            buildSumValAndInster(dtoMap.getValue(), dtoMap.getKey(), pbcReport, !CollectionUtils.isEmpty(posAssetList),false,null,null,null);
        }
         /*if (pbcReport.isGen()){
	         pbcReportAssetIncomeDetailMapper.deleteByReportDate(DateUtil.format(pbcReport.getStartdate()),
	                 DateUtil.format(pbcReport.getEnddate()),PbcReportType.PBC_REPORT_2_4,null);
	         PbcMonthReport param = new PbcMonthReport();
	         param.setStartdate(pbcReport.getStartdate());
	         param.setEnddate(pbcReport.getEnddate());
	         param.setReporttype(PbcReportType.PBC_REPORT_2_4);
	         pbcMonthReportMapper.deleteByModel(param);
	         // 判断是否有头寸数据有则插入
	         if (!CollectionUtils.isEmpty(posAssetList)){
	             // 插入主表数据
	             pbcMonthReportMapper.insert(pbcReport);
	             // 插入资产负债表数据
	             for ( PbcReportAssetIncomeDetail v:result.values() ) {
	            	 pbcReportAssetIncomeDetailMapper.insert(v);
	             }
	         }
         }*/

    }


    /**
     * 功能:生成表2-4 资产收益权按基础资产投向分类统计表
     * @since 1.6.0
     * @version 1.6.0
     * @author：yuluan 2019年3月12日
     * @param
     *
     */
    public void generateMonthAssetReportInComeEx(List<PbcMonthAssetPositionDto> posAssetList,Date generateDate,
                                                 Map<String, ChbLoanDetailInfo> chbLoandMap,boolean isEdit){

        // 生成月度报表主表数据
        PbcMonthReport pbcReport = genMonthReportByReporttype(PbcReportType.PBC_REPORT_2_4,generateDate,isEdit,false);

        //update by leipeng 20200319  汇总数据提取全量模板
        Map<String,PbcReportAssetIncomeDetail> result = genAssetMonthReportemplate(generateDate,  PbcReportType.PBC_REPORT_2_4, 7,pbcReport.getReportuuid());
//         List<PbcReportCompanyDetailDto> tjList=new ArrayList<PbcReportCompanyDetailDto>();

        if(null==result){
            return;
        }

        Map<String,Map<String,PbcReportAssetIncomeDetail>> deptMap = new HashMap();



        // 根据template定义行编号对应数据
//         PbcReportAssetIncomeDetail lineb00000 = result.get(PbcReportProperycode.AMT_B00000_ZJ);
//         List<PbcReportAssetIncomeDetail> zjList=new ArrayList<PbcReportAssetIncomeDetail>();
//         PbcReportAssetIncomeDetail lineb10000 = result.get(PbcReportProperycode.AMT_B10000_ZH);
//         PbcReportAssetIncomeDetail lineb20000 = result.get(PbcReportProperycode.AMT_B20000_GYZF);
//         PbcReportAssetIncomeDetail lineb30000 = result.get(PbcReportProperycode.AMT_B30000_FJRQY);
//         PbcReportAssetIncomeDetail lineb40000 = result.get(PbcReportProperycode.AMT_B40000_JRJGST);
//         PbcReportAssetIncomeDetail lineb50000 = result.get(PbcReportProperycode.AMT_B50000_TDMDZT);
//         PbcReportAssetIncomeDetail lineb60000 = result.get(PbcReportProperycode.AMT_B60000_JW);
//         zjList.add(lineb10000);
//         zjList.add(lineb20000);
//         zjList.add(lineb30000);
//         zjList.add(lineb40000);
//         zjList.add(lineb50000);
//         zjList.add(lineb60000);
        Map<String, BigDecimal> rateMap = new HashMap<>();
        // 汇率取值开关
        String source = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_EXCHANGE_SOURCE_PBC);
        // 资产头寸信息按比例拆分
        List<PbcMonthAssetPositionDto> posAssetNewList = new ArrayList<PbcMonthAssetPositionDto>();

        for (PbcMonthAssetPositionDto pos :posAssetList) {
            if(pos.getAssetType1()!=null&&PbcMonthReportConstants.PBC_ASSETTYPE1_P06.equals(pos.getAssetType1())
                    && null != pos.getAmt() && pos.getAmt().compareTo(BigDecimal.ZERO) != 0){

                ChbLoanDetailInfo chbLoanDetailInfo = chbLoandMap.get(pos.getAssetCode());
                // 资产要素不存在
                if (null == chbLoanDetailInfo) {
                    continue;
                }
                // 基础资产投向分类
                String assetsInvest = chbLoanDetailInfo.getAssetsInvest();
                // 各比例
                String eachProportion = chbLoanDetailInfo.getEachProportion();
                if (StringUtil.isEmpty(assetsInvest) || StringUtil.isEmpty(eachProportion)) {
                    continue;
                }
                String[] assetsInvestArr = assetsInvest.split(";");
                String[] eachProportionArr = eachProportion.split(":");
                int length = findMinNum(assetsInvestArr.length, eachProportionArr.length);
                BigDecimal[] realAmtArr = new BigDecimal[length];
                // 比例总额
                BigDecimal totalProportion = BigDecimal.ZERO;
                // 已统计金额
                BigDecimal countProportion = BigDecimal.ZERO;
                for (int i = 0; i < length; i ++) {
                    totalProportion = totalProportion.add(new BigDecimal(eachProportionArr[i]));
                }
                for (int i = 0; i < length - 1; i ++) {
                    BigDecimal realAmt =  pos.getAmt().multiply(
                                    new BigDecimal(eachProportionArr[i]).divide(totalProportion))
                            .setScale(2, RoundingMode.HALF_UP);
                    realAmtArr[i] = realAmt;
                    countProportion = countProportion.add(realAmt);
                }
                // 差额计算最后一部分金额
                realAmtArr[length - 1] = pos.getAmt().subtract(countProportion);
                // 拆分资产头寸信息
                for (int i = 0; i < length; i ++) {
                    PbcMonthAssetPositionDto pbcMonthAssetPositionDtoNew = new PbcMonthAssetPositionDto();
                    try {
                        BeanUtil.copyProperties(pbcMonthAssetPositionDtoNew, pos);
                        pbcMonthAssetPositionDtoNew.setAmt(realAmtArr[i]);
                        pbcMonthAssetPositionDtoNew.setInvestDirection(assetsInvestArr[i]);
                        posAssetNewList.add(pbcMonthAssetPositionDtoNew);
                    } catch (IllegalAccessException e) {
                        logger.error(e.getMessage(),e);
                    } catch (InvocationTargetException e) {
                        logger.error(e.getMessage(),e);
                    }
                }
            }
        }

        for(PbcMonthAssetPositionDto pos :posAssetNewList){


            // add by leipeng 20200319 获取所有部门对应信息,并列出单独的表结构
            if(null!=pos.getInstitutionCode()&&null==deptMap.get(pos.getInstitutionCode())){
                Map<String,PbcReportAssetIncomeDetail> singDeptMap = genAssetMonthReportemplate(generateDate,  PbcReportType.PBC_REPORT_2_4, 7,pbcReport.getReportuuid());
                deptMap.put(pos.getInstitutionCode(), singDeptMap);
            }

            //只取收/受益权
            if(pos.getAssetType1()!=null&&PbcMonthReportConstants.PBC_ASSETTYPE1_P06.equals(pos.getAssetType1())){

                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_B10000_ZH, PbcReportType.PBC_REPORT_2_4,
                        rateMap, source, "ZH", chbLoandMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_B20000_GYZF, PbcReportType.PBC_REPORT_2_4,
                        rateMap, source, "GYZF", chbLoandMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_B30000_FJRQY, PbcReportType.PBC_REPORT_2_4,
                        rateMap, source, "FJRQY", chbLoandMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_B40000_JRJGST, PbcReportType.PBC_REPORT_2_4,
                        rateMap, source, "JRJGST", chbLoandMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_B50000_TDMDZT, PbcReportType.PBC_REPORT_2_4,
                        rateMap, source, "TDMDZT", chbLoandMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_B60000_JW, PbcReportType.PBC_REPORT_2_4,
                        rateMap, source, "JW", chbLoandMap);

//        			 setAssetReportInComeValueEx(pos,lineb10000,rateMap,source,"ZH",chbLoandMap);
//        			 setAssetReportInComeValueEx(pos,lineb20000,rateMap,source,"GYZF",chbLoandMap);
//        			 setAssetReportInComeValueEx(pos,lineb30000,rateMap,source,"FJRQY",chbLoandMap);
//        			 setAssetReportInComeValueEx(pos,lineb40000,rateMap,source,"JRJGST",chbLoandMap);
//        			 setAssetReportInComeValueEx(pos,lineb50000,rateMap,source,"TDMDZT",chbLoandMap);
//        			 setAssetReportInComeValueEx(pos,lineb60000,rateMap,source,"JW",chbLoandMap);
            }
        }
        //修改数据组装及入库公共方法
        buildSumValAndInster(result, null, pbcReport, !CollectionUtils.isEmpty(posAssetList),false,null,null,null);
        //循环分支机构的数据组装及入库
        for (Map.Entry<String, Map<String, PbcReportAssetIncomeDetail>> dtoMap : deptMap.entrySet()) {
            buildSumValAndInster(dtoMap.getValue(), dtoMap.getKey(), pbcReport, !CollectionUtils.isEmpty(posAssetList),false,null,null,null);
        }

    }
    /**
     * 功能:生成表2-5 企业债券分行业、企业规模统计
     * @since 1.6.0
     * @version 1.6.0
     * @author：yuluan 2019年3月12日
     * @param
     *
     */
    public void generateMonthReportCompanyBond(List<PbcMonthAssetPositionDto> posAssetList,Date generateDate,
                                               Map<String,ChbAssetInfo> assetMap,boolean isEdit){

        // 生成月度报表主表数据
        PbcMonthReport pbcReport = genMonthReportByReporttype(PbcReportType.PBC_REPORT_2_5,generateDate,isEdit,false);

        Map<String,PbcReportCompanyDetailDto> result = genAssetMonthReportemplate(generateDate,  PbcReportType.PBC_REPORT_2_5, 20,pbcReport.getReportuuid());
//        List<PbcReportCompanyDetailDto> tjList=new ArrayList<PbcReportCompanyDetailDto>();

        if(null==result){
            return;
        }

        Map<String,Map<String,PbcReportCompanyDetailDto>> deptMap = new HashMap();


        // 根据template定义行编号对应数据
//        PbcReportCompanyDetailDto linec00000 = result.get(PbcReportProperycode.AMT_C00000_QYZQYEZJ);
//        PbcReportCompanyDetailDto linec10000 = result.get(PbcReportProperycode.AMT_C10000_NLMYY);
//        PbcReportCompanyDetailDto linec20000 = result.get(PbcReportProperycode.AMT_C20000_CKY);
//        PbcReportCompanyDetailDto linec30000 = result.get(PbcReportProperycode.AMT_C30000_ZZY);
//        PbcReportCompanyDetailDto linec40000 = result.get(PbcReportProperycode.AMT_C40000_DLRLRQJSSCHGYY);
//        PbcReportCompanyDetailDto linec50000 = result.get(PbcReportProperycode.AMT_C50000_JZY);
//        PbcReportCompanyDetailDto linec60000 = result.get(PbcReportProperycode.AMT_C60000_PFHLSY);
//        PbcReportCompanyDetailDto linec70000 = result.get(PbcReportProperycode.AMT_C70000_JTYSCCHYZY);
//        PbcReportCompanyDetailDto linec80000 = result.get(PbcReportProperycode.AMT_C80000_ZSHCYY);
//        PbcReportCompanyDetailDto linec90000 = result.get(PbcReportProperycode.AMT_C90000_XXCSRJHXXJSFWY);
//        PbcReportCompanyDetailDto lineca0000 = result.get(PbcReportProperycode.AMT_CA0000_JRY);
//        PbcReportCompanyDetailDto linecb0000 = result.get(PbcReportProperycode.AMT_CB0000_FDCY);
//        PbcReportCompanyDetailDto linecc0000 = result.get(PbcReportProperycode.AMT_CC0000_ZLHSWFWY);
//        PbcReportCompanyDetailDto linecd0000 = result.get(PbcReportProperycode.AMT_CD0000_KXYJHJSFWY);
//        PbcReportCompanyDetailDto linece0000 = result.get(PbcReportProperycode.AMT_CE0000_SLHJHGGSSGLY);
//        PbcReportCompanyDetailDto linecf0000 = result.get(PbcReportProperycode.AMT_CF0000_JMFWXLHQTFWY);
//        PbcReportCompanyDetailDto linecg0000 = result.get(PbcReportProperycode.AMT_CG0000_JY);
//        PbcReportCompanyDetailDto linech0000 = result.get(PbcReportProperycode.AMT_CH0000_WSHSHGZ);
//        PbcReportCompanyDetailDto lineci0000 = result.get(PbcReportProperycode.AMT_CI0000_WHTYHYLY);
//        PbcReportCompanyDetailDto linecj0000 = result.get(PbcReportProperycode.AMT_CJ0000_GGGLSHBZHSHZZ);
//        tjList.add(linec10000);
//        tjList.add(linec20000);
//        tjList.add(linec30000);
//        tjList.add(linec40000);
//        tjList.add(linec50000);
//        tjList.add(linec60000);
//        tjList.add(linec70000);
//        tjList.add(linec80000);
//        tjList.add(linec90000);
//        tjList.add(lineca0000);
//        tjList.add(linecb0000);
//        tjList.add(linecc0000);
//        tjList.add(linecd0000);
//        tjList.add(linece0000);
//        tjList.add(linecf0000);
//        tjList.add(linecg0000);
//        tjList.add(linech0000);
//        tjList.add(lineci0000);
//        tjList.add(linecj0000);
        Map<String, BigDecimal> rateMap = new HashMap<>();
        // 汇率取值开关
        String source = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_EXCHANGE_SOURCE_PBC);
        for(PbcMonthAssetPositionDto pos :posAssetList){
            if(pos.getAssetType3()!=null&&PbcMonthReportConstants.PBC_ASSETTYPE2_S05.equals(pos.getAssetType2())){//企业债券才统计


                // add by leipeng 20200319 获取所有部门对应信息,并列出单独的表结构
                if(null!=pos.getInstitutionCode()&&null==deptMap.get(pos.getInstitutionCode())){
                    Map<String,PbcReportCompanyDetailDto> singDeptMap = genAssetMonthReportemplate(generateDate,  PbcReportType.PBC_REPORT_2_5, 20,pbcReport.getReportuuid());

                    deptMap.put(pos.getInstitutionCode(), singDeptMap);
                }


                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_C10000_NLMYY, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_01, assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_C20000_CKY, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_02, assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_C30000_ZZY, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_03, assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_C40000_DLRLRQJSSCHGYY, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_04, assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_C50000_JZY, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_05, assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_C60000_PFHLSY, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_06, assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_C70000_JTYSCCHYZY, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_07, assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_C80000_ZSHCYY, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_08, assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_C90000_XXCSRJHXXJSFWY, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_09, assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_CA0000_JRY, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_10, assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_CB0000_FDCY, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_11, assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_CC0000_ZLHSWFWY, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_12, assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_CD0000_KXYJHJSFWY, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_13, assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_CE0000_SLHJHGGSSGLY, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_14, assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_CF0000_JMFWXLHQTFWY, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_15, assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_CG0000_JY, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_16, assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_CH0000_WSHSHGZ, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_17, assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_CI0000_WHTYHYLY, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_18, assetMap);
                setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.AMT_CJ0000_GGGLSHBZHSHZZ, PbcReportType.PBC_REPORT_2_5,
                        rateMap, source, PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_19, assetMap);

//		   			  setAssetReportCompanyBondValue(pos,linec10000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_01,assetMap);
//	   			      setAssetReportCompanyBondValue(pos,linec20000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_02,assetMap);
//        			  setAssetReportCompanyBondValue(pos,linec30000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_03,assetMap);
//        			  setAssetReportCompanyBondValue(pos,linec40000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_04,assetMap);
//        			  setAssetReportCompanyBondValue(pos,linec50000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_05,assetMap);
//        			  setAssetReportCompanyBondValue(pos,linec60000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_06,assetMap);
//        			  setAssetReportCompanyBondValue(pos,linec70000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_07,assetMap);
//        			  setAssetReportCompanyBondValue(pos,linec80000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_08,assetMap);
//        			  setAssetReportCompanyBondValue(pos,linec90000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_09,assetMap);
//        			  setAssetReportCompanyBondValue(pos,lineca0000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_10,assetMap);
//        			  setAssetReportCompanyBondValue(pos,linecb0000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_11,assetMap);
//        			  setAssetReportCompanyBondValue(pos,linecc0000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_12,assetMap);
//        			  setAssetReportCompanyBondValue(pos,linecd0000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_13,assetMap);
//        			  setAssetReportCompanyBondValue(pos,linece0000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_14,assetMap);
//        			  setAssetReportCompanyBondValue(pos,linecf0000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_15,assetMap);
//        			  setAssetReportCompanyBondValue(pos,linecg0000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_16,assetMap);
//        			  setAssetReportCompanyBondValue(pos,linech0000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_17,assetMap);
//        			  setAssetReportCompanyBondValue(pos,lineci0000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_18,assetMap);
//        			  setAssetReportCompanyBondValue(pos,linecj0000,rateMap,source,PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_19,assetMap);
            }
        }
//        BigDecimal hjdxqy=BigDecimal.ZERO;//合计大型企业
//        BigDecimal hjzxqy=BigDecimal.ZERO;//合计中型企业
//        BigDecimal hjwxqy=BigDecimal.ZERO;//合计微型企业
//        BigDecimal hjxxqy=BigDecimal.ZERO;//合计小型企业
//        for(PbcReportCompanyDetailDto single:tjList){
//        	hjdxqy=hjdxqy.add(single.getLargeCompanyAmt()==null?BigDecimal.ZERO:single.getLargeCompanyAmt());
//        	hjzxqy=hjzxqy.add(single.getMediumCompanyAmt()==null?BigDecimal.ZERO:single.getMediumCompanyAmt());
//        	hjwxqy=hjwxqy.add(single.getMicroCompanyAmt()==null?BigDecimal.ZERO:single.getMicroCompanyAmt());
//        	hjxxqy=hjxxqy.add(single.getSmallCompanyAmt()==null?BigDecimal.ZERO:single.getSmallCompanyAmt());
//        	single.setSumAmt(single.getLargeCompanyAmt()==null?BigDecimal.ZERO:single.getLargeCompanyAmt()
//        			.add(single.getMediumCompanyAmt()==null?BigDecimal.ZERO:single.getMediumCompanyAmt())
//        			.add(single.getMicroCompanyAmt()==null?BigDecimal.ZERO:single.getMicroCompanyAmt())
//        			.add(single.getSmallCompanyAmt()==null?BigDecimal.ZERO:single.getSmallCompanyAmt()));
//        }

        //修改数据组装及入库公共方法
        buildSumValAndInster(result, null, pbcReport, !CollectionUtils.isEmpty(posAssetList),false,null,null,null);
        //循环分支机构的数据组装及入库
        for (Map.Entry<String, Map<String, PbcReportCompanyDetailDto>> dtoMap : deptMap.entrySet()) {
            buildSumValAndInster(dtoMap.getValue(), dtoMap.getKey(), pbcReport, !CollectionUtils.isEmpty(posAssetList),false,null,null,null);
        }

        // 插入数据，先删除明细表数据，再删除主表数据

    }
    /**
     * 功能:生成表3-3 资管产品资产负债剩余期限
     * @since 1.6.0
     * @version 1.6.0
     * @author：yuluan 2019年3月12日
     * @param
     *
     */
    public void generateMonthReportDebtterm(List<PbcMonthAssetPositionDto> posAssetList,List<PbcMonthPrdPostionDto> posProdList
            ,Date generateDate,Map<String,BigDecimal> qtfz,boolean isEdit) {

        // 生成月度报表主表数据
        PbcMonthReport pbcReport = genMonthReportByReporttype(PbcReportType.PBC_REPORT_3_3, generateDate, isEdit, false);
        //update by leipeng 20200319  汇总数据提取全量模板
        Map<String, PbcReportDebtTermDetail> result = genAssetMonthReportemplate(generateDate, PbcReportType.PBC_REPORT_3_3, 18, pbcReport.getReportuuid());
        if (null == result) {
            return;
        }
        Map<String, Map<String, PbcReportDebtTermDetail>> deptMap = new HashMap();


//    	Map<String,PbcReportDebtTermDetail> result = new HashMap<String,PbcReportDebtTermDetail>();
//    	// 查询产品月报需要统计的项
//        List<PbcReportTemplateDto> listKey = pbcReportTemplateMapper.selectByReporttype(PbcReportType.PBC_REPORT_3_3,
//                DictCodeListExt1.维护数据状态_有效.getValue());
//        if ( listKey.size() != 18 || listKey.size() == 0)  {
//            logger.error("月报模板配置错误，请检查");
//            return;
//        }
//        // 生成月度报表主表数据
//        PbcMonthReport pbcReport = genMonthReportByReporttype(PbcReportType.PBC_REPORT_3_3,generateDate,isEdit,false);
//        for ( PbcReportTemplateDto k:listKey ) {
//        	PbcReportDebtTermDetail detail = new PbcReportDebtTermDetail();
//            detail.setDetailuuid(IDGenerator.generate(36));
//            detail.setReporttype(PbcReportType.PBC_REPORT_3_3);
//            detail.setPropertycode(k.getPropertycode());
//            detail.setGrouptype(k.getGrouptype());
//            detail.setReportuuid(pbcReport.getReportuuid());
//            detail.setDebtInstrument(BigDecimal.ZERO);
//            detail.setStockRight(BigDecimal.ZERO);
//            detail.setSpecialShare(BigDecimal.ZERO);
//            detail.setActualAmt(BigDecimal.ZERO);
//            result.put(k.getPropertycode()+k.getGrouptype(),detail);
//        }
        // 根据template定义行编号对应数据
//        PbcReportDebtTermDetail lineg00000 = result.get(PbcReportProperycode.ASSET_G00000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01);
//        List<PbcReportDebtTermDetail> tjList=new ArrayList<PbcReportDebtTermDetail>();
//        PbcReportDebtTermDetail lineg10000 = result.get(PbcReportProperycode.ASSET_G10000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01);
//        PbcReportDebtTermDetail lineg20000 = result.get(PbcReportProperycode.ASSET_G20000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01);
//        PbcReportDebtTermDetail lineg30000 = result.get(PbcReportProperycode.ASSET_G30000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01);
//        PbcReportDebtTermDetail lineg40000 = result.get(PbcReportProperycode.ASSET_G40000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01);
//        PbcReportDebtTermDetail lineg50000 = result.get(PbcReportProperycode.ASSET_G50000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01);
//        PbcReportDebtTermDetail lineg60000 = result.get(PbcReportProperycode.ASSET_G60000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01);
//        PbcReportDebtTermDetail lineg70000 = result.get(PbcReportProperycode.ASSET_G70000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01);
//        PbcReportDebtTermDetail lineg80000 = result.get(PbcReportProperycode.ASSET_G80000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01);
//        tjList.add(lineg10000);
//        tjList.add(lineg20000);
//        tjList.add(lineg30000);
//        tjList.add(lineg40000);
//        tjList.add(lineg50000);
//        tjList.add(lineg60000);
//        tjList.add(lineg70000);
//        tjList.add(lineg80000);
//        PbcReportDebtTermDetail lineg00000fz = result.get(PbcReportProperycode.DEBT_G00000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02);
//        List<PbcReportDebtTermDetail> tjListfz=new ArrayList<PbcReportDebtTermDetail>();
//        PbcReportDebtTermDetail lineg10000fz = result.get(PbcReportProperycode.DEBT_G10000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02);
//        PbcReportDebtTermDetail lineg20000fz = result.get(PbcReportProperycode.DEBT_G20000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02);
//        PbcReportDebtTermDetail lineg30000fz = result.get(PbcReportProperycode.DEBT_G30000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02);
//        PbcReportDebtTermDetail lineg40000fz = result.get(PbcReportProperycode.DEBT_G40000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02);
//        PbcReportDebtTermDetail lineg50000fz = result.get(PbcReportProperycode.DEBT_G50000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02);
//        PbcReportDebtTermDetail lineg60000fz = result.get(PbcReportProperycode.DEBT_G60000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02);
//        PbcReportDebtTermDetail lineg70000fz = result.get(PbcReportProperycode.DEBT_G70000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02);
//        PbcReportDebtTermDetail lineg80000fz = result.get(PbcReportProperycode.DEBT_G80000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02);
//
//        lineg80000fz.setDebtInstrument(lineg80000fz.getDebtInstrument().add(qtfz));
//        tjListfz.add(lineg10000fz);
//        tjListfz.add(lineg20000fz);
//        tjListfz.add(lineg30000fz);
//        tjListfz.add(lineg40000fz);
//        tjListfz.add(lineg50000fz);
//        tjListfz.add(lineg60000fz);
//        tjListfz.add(lineg70000fz);
//        tjListfz.add(lineg80000fz);


        Map<String, BigDecimal> rateMap = new HashMap<>();
        /* add by jyl 19.11.25*/
        PbcMonthAssetPositionDto qmode = new PbcMonthAssetPositionDto();
        List<String> prodList = new ArrayList<>();
        for (PbcMonthPrdPostionDto dto : posProdList) {
            prodList.add(dto.getProdid());
        }
        //产品代码去重 leipeng 20200916
        prodList = prodList.stream().distinct().collect(Collectors.toList());

        qmode.setBookdate(DateUtil.parse(DateUtil.getLastDayOfLayMonth(generateDate)));
        qmode.setProdids(prodList);
        String strj = SvrDataUtil.getSysParam(SVReportGlobalConstant.CHB_ASSETS_BASIS_DATA_CHILD);
        if ("Y".equals(strj)) {
            //查询子资管计划资产基础指标数据表
            List<PbcMonthAssetPositionDto> posAssetListdtx = pbcMonthAssetPositionMapper.selectPositionFromValutionSubSystemdtx(qmode);
            Map<String, List<PbcMonthAssetPositionDto>> cprodListMap =
                    posAssetListdtx.stream().collect(Collectors.groupingBy(PbcMonthAssetPositionDto::getProdid));

            for (ListIterator<PbcMonthAssetPositionDto> iterator = posAssetList.listIterator(); iterator.hasNext(); ) {
                PbcMonthAssetPositionDto it = iterator.next();
                //新疆农信：人行月报表3-3 资管产品资产负债剩余期限统计模板除实收本金栏目数据正确外，其他数据均不对 modify by hpp 2020-12-02 start
                if (cprodListMap.containsKey(it.getProdid())) {
                    //BigDecimal amt=it.getAmt();
//                    iterator.remove();
                    //GZYY-431 这里去除母资管计划但是只补充了一条子资管计划,需要将所有的子资管计划都补上
                    List<PbcMonthAssetPositionDto>cprodList=cprodListMap.get(it.getProdid());
                    for (PbcMonthAssetPositionDto single : cprodList) {
                        //single.setAmt(amt);
                        if (single.getAssetCode().equals(it.getAssetCode())) {
                            iterator.remove();
                            for (PbcMonthAssetPositionDto dto : cprodList) {
                                iterator.add(convertBalanceToPrdPosition1(dto, generateDate, it));
                            }
                            break;
                        }
                    }
                }
            }
        }
        /*end by jyl*/
        // 汇率取值开关
        String source = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_EXCHANGE_SOURCE_PBC);
        for (PbcMonthAssetPositionDto pos : posAssetList) {
            // add by leipeng 20200319 获取所有部门对应信息,并列出单独的表结构
            if (null != pos.getInstitutionCode() && null == deptMap.get(pos.getInstitutionCode())) {
                Map<String, PbcReportDebtTermDetail> singDeptMap = genAssetMonthReportemplate(generateDate, PbcReportType.PBC_REPORT_3_3, 18, pbcReport.getReportuuid());
                deptMap.put(pos.getInstitutionCode(), singDeptMap);
            }

            setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.ASSET_G10000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "01", null);
            setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.ASSET_G20000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "02", null);
            setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.ASSET_G30000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "03", null);
            setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.ASSET_G40000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "04", null);
            setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.ASSET_G50000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "05", null);
            setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.ASSET_G60000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "06", null);
            setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.ASSET_G70000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "07", null);
            setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.ASSET_G80000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "08", null);


            setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.DEBT_G10000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "01", null);
            setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.DEBT_G20000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "02", null);
            setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.DEBT_G30000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "03", null);
            setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.DEBT_G40000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "04", null);
            setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.DEBT_G50000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "05", null);
            setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.DEBT_G60000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "06", null);
            setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.DEBT_G70000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "07", null);
            setAssetReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.DEBT_G80000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "08", null);

//        	setAssetReportDebttermValue(pos,lineg10000,rateMap,source,"01");
//        	setAssetReportDebttermValue(pos,lineg20000,rateMap,source,"02");
//        	setAssetReportDebttermValue(pos,lineg30000,rateMap,source,"03");
//        	setAssetReportDebttermValue(pos,lineg40000,rateMap,source,"04");
//        	setAssetReportDebttermValue(pos,lineg50000,rateMap,source,"05");
//        	setAssetReportDebttermValue(pos,lineg60000,rateMap,source,"06");
//        	setAssetReportDebttermValue(pos,lineg70000,rateMap,source,"07");
//        	setAssetReportDebttermValue(pos,lineg80000,rateMap,source,"08");
//        	setAssetReportDebttermValue(pos,lineg10000fz,rateMap,source,"01");
//        	setAssetReportDebttermValue(pos,lineg20000fz,rateMap,source,"02");
//        	setAssetReportDebttermValue(pos,lineg30000fz,rateMap,source,"03");
//        	setAssetReportDebttermValue(pos,lineg40000fz,rateMap,source,"04");
//        	setAssetReportDebttermValue(pos,lineg50000fz,rateMap,source,"05");
//        	setAssetReportDebttermValue(pos,lineg60000fz,rateMap,source,"06");
//        	setAssetReportDebttermValue(pos,lineg70000fz,rateMap,source,"07");
//        	setAssetReportDebttermValue(pos,lineg80000fz,rateMap,source,"08");
        }

        //按照产品到期日还是期次到期日出3-3报表
        String str = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_PERIOD);
        if ("Y".equals(str)) {
            //查询对应月末的周期型产品数据
            List<ChbCprodBalanceDto> cprodList = chbCprodBalanceDtoMapper.selectByValuedate(pbcReport.getEnddate());
            Map<String, List<ChbCprodBalanceDto>> cprodListMap1 =
                    cprodList.stream().collect(Collectors.groupingBy(ChbCprodBalanceDto::getProdId));

            for (ListIterator<PbcMonthPrdPostionDto> iterator = posProdList.listIterator(); iterator.hasNext(); ) {
                PbcMonthPrdPostionDto it = iterator.next();
                if (cprodListMap1.containsKey(it.getProdid())) {
                    iterator.remove();
                    for (ChbCprodBalanceDto single : cprodListMap1.get(it.getProdid())) {
                        iterator.add(convertBalanceToPrdPosition(single, it));
                    }
                }
            }
        }

        for (PbcMonthPrdPostionDto pos : posProdList) {
            // 销售区域为计划单列市的不统计（省份内已经统计）：210200大连市、330200宁波市、350200厦门市、370200青岛市、440300深圳市
            if ("210200".equals(pos.getBuyPlace()) || "330200".equals(pos.getBuyPlace()) || "350200".equals(pos.getBuyPlace()) ||
                    "370200".equals(pos.getBuyPlace()) || "440300".equals(pos.getBuyPlace())) {
                continue;
            }

            if (null != pos.getInstitutionCode() && null == deptMap.get(pos.getInstitutionCode())) {
                Map<String, PbcReportDebtTermDetail> singDeptMap = genAssetMonthReportemplate(generateDate, PbcReportType.PBC_REPORT_3_3, 18, pbcReport.getReportuuid());
                deptMap.put(pos.getInstitutionCode(), singDeptMap);
            }


            setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.DEBT_G10000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "01", null, null);
            setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.DEBT_G20000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "02", null, null);
            setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.DEBT_G30000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "03", null, null);
            setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.DEBT_G40000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "04", null, null);
            setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.DEBT_G50000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "05", null, null);
            setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.DEBT_G60000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "06", null, null);
            setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.DEBT_G70000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "07", null, null);
            setPrdReportValue(pos, result, deptMap.get(pos.getInstitutionCode()), PbcReportProperycode.DEBT_G80000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02,
                    PbcReportType.PBC_REPORT_3_3, rateMap, source, "08", null, null);

//        	setReportDebttermValueProd(pos,lineg10000fz,rateMap,source,"01");
//        	setReportDebttermValueProd(pos,lineg20000fz,rateMap,source,"02");
//        	setReportDebttermValueProd(pos,lineg30000fz,rateMap,source,"03");
//        	setReportDebttermValueProd(pos,lineg40000fz,rateMap,source,"04");
//        	setReportDebttermValueProd(pos,lineg50000fz,rateMap,source,"05");
//        	setReportDebttermValueProd(pos,lineg60000fz,rateMap,source,"06");
//        	setReportDebttermValueProd(pos,lineg70000fz,rateMap,source,"07");
//        	setReportDebttermValueProd(pos,lineg80000fz,rateMap,source,"08");
        }


        // 负债及权益_债务工具+其他负债
        result.get(PbcReportProperycode.DEBT_G80000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02).
                setDebtInstrument(result.get(PbcReportProperycode.DEBT_G80000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02).getDebtInstrument().add(qtfz.get("ALL")));

        //修改数据组装及入库公共方法
        buildSumValAndInster(result, null, pbcReport, (!CollectionUtils.isEmpty(posAssetList) ||
                !CollectionUtils.isEmpty(posProdList)), false, null, null, null);
        //循环分支机构的数据组装及入库
        for (Map.Entry<String, Map<String, PbcReportDebtTermDetail>> dtoMap : deptMap.entrySet()) {

            dtoMap.getValue().get(PbcReportProperycode.DEBT_G80000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02).
                    setDebtInstrument(dtoMap.getValue().get(PbcReportProperycode.DEBT_G80000 + PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02).getDebtInstrument().add(qtfz.get(dtoMap.getKey())));


            buildSumValAndInster(dtoMap.getValue(), dtoMap.getKey(), pbcReport, (!CollectionUtils.isEmpty(posAssetList) ||
                    !CollectionUtils.isEmpty(posProdList)), false, null, null, null);
        }


//        BigDecimal hjzczwgz=BigDecimal.ZERO;//资产债务工具
//        BigDecimal hjzcgq=BigDecimal.ZERO;//资产股权
//        BigDecimal hjzctd=BigDecimal.ZERO;//资产特定目的载体份额
//        for(PbcReportDebtTermDetail singel:tjList){
//        	hjzczwgz=hjzczwgz.add(singel.getDebtInstrument()==null?BigDecimal.ZERO:singel.getDebtInstrument());
//        	hjzcgq=hjzcgq.add(singel.getStockRight()==null?BigDecimal.ZERO:singel.getStockRight());
//        	hjzctd=hjzctd.add(singel.getSpecialShare()==null?BigDecimal.ZERO:singel.getSpecialShare());
//        }
//        lineg00000.setDebtInstrument(hjzczwgz);
//        lineg00000.setStockRight(hjzcgq);
//        lineg00000.setSpecialShare(hjzctd);
//        BigDecimal hjfzzwgz=BigDecimal.ZERO;//负债债务工具
//        BigDecimal hjfzssbj=BigDecimal.ZERO;//负债实收本金
//        for(PbcReportDebtTermDetail singel:tjListfz){
//        	hjfzzwgz=hjfzzwgz.add(singel.getDebtInstrument()==null?BigDecimal.ZERO:singel.getDebtInstrument());
//        	hjfzssbj=hjfzssbj.add(singel.getActualAmt()==null?BigDecimal.ZERO:singel.getActualAmt());
//        }
//        lineg00000fz.setDebtInstrument(hjfzzwgz);
//        lineg00000fz.setActualAmt(hjfzssbj);
//        // 插入数据，先删除明细表数据，再删除主表数据
//        if (pbcReport.isGen()){
//	        pbcReportDebtTermDetailMapper.deleteByReportDate(DateUtil.format(pbcReport.getStartdate()),
//	                DateUtil.format(pbcReport.getEnddate()),PbcReportType.PBC_REPORT_3_3);
//	        PbcMonthReport param = new PbcMonthReport();
//	        param.setStartdate(pbcReport.getStartdate());
//	        param.setEnddate(pbcReport.getEnddate());
//	        param.setReporttype(PbcReportType.PBC_REPORT_3_3);
//	        pbcMonthReportMapper.deleteByModel(param);
//	        // 判断是否有头寸数据有则插入
//	        if (!CollectionUtils.isEmpty(posAssetList) ||
//	                !CollectionUtils.isEmpty(posProdList)){
//	            // 插入主表数据
//	            pbcMonthReportMapper.insert(pbcReport);
//	            // 插入资产负债表数据
//	            for ( PbcReportDebtTermDetail v:result.values() ) {
//	            	pbcReportDebtTermDetailMapper.insert(v);
//	            }
//	        }
//        }

    }
    /**
     * 将周期型产品余额数据转换成计算需要的产品头寸数据
     * @param balance
     * @return
     */
    private PbcMonthPrdPostionDto convertBalanceToPrdPosition(ChbCprodBalanceDto balance,PbcMonthPrdPostionDto dto){
        PbcMonthPrdPostionDto position=new PbcMonthPrdPostionDto();
        position.setProdid(balance.getProdId());
        position.setCcy(balance.getCcy());
        int days=DateUtil.getBetweenDayNumber(balance.getValueDate(), balance.getMdate());
        if(days<0){
            days=0;
        }
        position.setEndDays(new BigDecimal(days));
        position.setEndqtysum(balance.getBalance());
        position.setEnddate(balance.getValueDate());
        position.setInstitutionCode(dto.getInstitutionCode());
        return position;
    }
    /**
     * 将周期型产品余额数据转换成计算需要的资产头寸数据
     * @param balance
     * @return
     */
    /*start add by jyl 19.11.25*/
    private PbcMonthAssetPositionDto convertBalanceToPrdPosition1(PbcMonthAssetPositionDto balance, Date generateDate,PbcMonthAssetPositionDto dto){
        PbcMonthAssetPositionDto position=new PbcMonthAssetPositionDto();
        position.setProdid(balance.getProdid());
        position.setCcy(balance.getCcy());
        int days=DateUtil.getBetweenDayNumber(balance.getBookdate(), balance.getMdate());
        if(days<0){
            days=0;
        }
        position.setEndDays(new BigDecimal(days));
        position.setAmt(balance.getAmt());
        position.setIntAmt(balance.getIntAmt());
        position.setEnddate(DateUtil.parse(DateUtil.getLastDayOfLayMonth(generateDate)));
//        position.setEnddate(balance.getEnddate());
        position.setBookdate(balance.getBookdate());
        position.setPositionType(balance.getPositionType());
        position.setAssetType1(balance.getAssetType1());
        position.setAssetType2(balance.getAssetType2());
        position.setAssetCode(balance.getAssetCode());
        position.setDelayPayAmt(balance.getDelayPayAmt());
        position.setBlnarea(balance.getBlnarea());
        position.setCounterid(balance.getCounterid());
        position.setIndustry(balance.getIndustry());
        position.setRepprofittype(balance.getRepprofittype());
        position.setMdate(balance.getMdate());
        position.setInstitutionCode(dto.getInstitutionCode());
        return position;
    }
    /*end*/
    /**
     * 功能:根据报表类型报表日期生成人行报表主数据
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年3月12日
     * @param reporttype 报表类型
     * @param
     * @return
     * PbcMonthReport
     */
    private PbcMonthReport genMonthReportByReporttype(String reporttype,Date reportDate,
                                                      boolean isEdit,boolean isOnePrd){
        PbcMonthReport pbcMonthReport = new PbcMonthReport();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(reportDate);
        // 获取上月的第一天
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date reportStartDate = calendar.getTime();
        // 获取上月的最后一天
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        Date reportEndDate = calendar.getTime();
        // 报表ID
        pbcMonthReport.setReportuuid(IDGenerator.generate(36));
        // 报表类型
        pbcMonthReport.setReporttype(reporttype);
        // 起止日期
        pbcMonthReport.setStartdate(reportStartDate);
        pbcMonthReport.setEnddate(reportEndDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        pbcMonthReport.setTermid(sdf.format(reportStartDate));
        // 数据日期
        pbcMonthReport.setStatusDate(reportDate);
        // 制表日期
//        pbcMonthReport.setReportDate(reportEndDate);
        // 报表频率：月报
        pbcMonthReport.setReportfrequ(PbcMonthReportConstants.REPORT_FREQU_M);
        // 发行机构代码取值
        pbcMonthReport.setIssuePartyId(CommonUtil.selectBankCode());
        pbcMonthReport.setIssuePartyName(CommonUtil.selectBankCode());
        // 用户信息
        pbcMonthReport.setOrgCode(CommonUtil.getOrgCode());
        pbcMonthReport.setCreateDept(CommonUtil.getDeptCode());
        pbcMonthReport.setCreateUser(CommonUtil.getUserName());
        pbcMonthReport.setUpdateUser(CommonUtil.getUserName());
        // 数据状态
        // 根据是否编辑处理数据状态
        pbcMonthReport.setGen(true);
        // 不是单产品报表按月报出数据状态
        if (!isOnePrd){
//            if (isEdit){
//                pbcMonthReport.setStatus(DictCodeListExt2.报送数据状态_修改.getValue());
//            } else {
            // 查询库中报表的数据状态
            List<PbcMonthReport> apbcMonthReport = pbcMonthReportMapper.selectSendDataByDateAndType(DateUtil.format(reportEndDate), reporttype);
            if (CollectionUtils.isEmpty(apbcMonthReport)){
                pbcMonthReport.setStatus(DictCodeListExt2.报送数据状态_新建.getValue());
            } else {// 用原报表的数据状态
                String status = DictCodeListExt2.报送数据状态_新建.getValue();
                for (PbcMonthReport report: apbcMonthReport){
                    if (DictCodeListExt2.报送状态_未报送.getValue().equals(report.getSendStatus())){
                        status = report.getSendStatus();
                    }
                }
                pbcMonthReport.setStatus(status);
            }
            // 如果只有一条已报送的数据并且不是消息触发则不生成报表
            if (!isEdit && (apbcMonthReport.size() == 1 && (DictCodeListExt2.报送状态_接口报送.equals(apbcMonthReport.get(0).getSendStatus())
                    ||DictCodeListExt2.报送状态_手工处理.equals(apbcMonthReport.get(0).getSendStatus())))){
                pbcMonthReport.setGen(false);
            }
//            }
        }
        pbcMonthReport.setSendStatus(DictCodeListExt2.报送状态_未报送.getValue());
        // 报送时点
        // 当前批次报送时点集合
        Map<String, List<Date>> dateMap = new HashMap<String, List<Date>>();
        if (isOnePrd){
            List<Date> dateList = DeadlineUtil.getChbEndDate(DictCodeListExt2.报表名称_资产负债信息.getValue(),
                    pbcMonthReport.getStatus(),dateMap, pbcMonthReport.getEnddate(),SVReportGlobalConstant.SVR_SUBMIT_TIME_PBC);
            if (!StringUtil.isEmptyList(dateList)) {
                pbcMonthReport.setChbSubmitDeadline(dateList.get(0));
                pbcMonthReport.setSubmitDeadline(dateList.get(1));
            }
        } else {
            List<Date> dateList = DeadlineUtil.getChbEndDate(DictCodeListExt2.报表名称_人行月度统计报表.getValue(),
                    pbcMonthReport.getStatus(),dateMap, pbcMonthReport.getEnddate(),SVReportGlobalConstant.SVR_SUBMIT_TIME_PBC);
            if (!StringUtil.isEmptyList(dateList)) {
                pbcMonthReport.setChbSubmitDeadline(dateList.get(0));
                pbcMonthReport.setSubmitDeadline(dateList.get(1));
            }
        }
        // 填报机构名称
        pbcMonthReport.setReportOrgName(CommonUtil.selectBankCode());
        // 社会信用代码
        // pbcMonthReport.setSocialCreditCode(SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_SOCIAL_CREDIT_CODE));
        // 金融机构编码
//        pbcMonthReport.setFinInstitutionCode(SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ISSUE_PARTY_ID));
        // pbcMonthReport.setFinInstitutionCode(SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_FIN_INSTITUTION_CODE));//add by zhangyf 20191024
        //多级法人改造start add by hxy 20200619,联社模式，在后续中修改，此处只修改银行模式
        if(!MultilevelDataAuthorityUtil.isEnabled()) {
            //银行模式
            SysInstRegBankCode001Dto bankDto=sysInstRegBankCode001Mapper.queryForPageWithMode(null);
            if(null!=bankDto){
                // 社会信用代码
                pbcMonthReport.setSocialCreditCode(bankDto.getInsitituteCode());
                // 金融机构编码
                pbcMonthReport.setFinInstitutionCode(bankDto.getRegistBankCode());

            }
        }
        //多级法人改造end add by hxy 20200619




        // 产品品种01银行非保本理财
        pbcMonthReport.setProdVariety(PbcMonthReportConstants.PBC_PROD_VARIETY_01);
        return pbcMonthReport;
    }

    /**
     * 功能:2_1资产负债汇率折算人民币
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年3月12日
     * @param dto
     * @param ccy
     * @param value
     * @param rateMap
     * @param generateDate
     * void
     */
    public static void setAssetDebtDetailValue(PbcReportAssetDebtDetailDto dto,String ccy,BigDecimal value,Map<String,
            BigDecimal> rateMap,Date generateDate,String source){
        BigDecimal cny = ChbCcyConvertUtil.exchangeMoney(
                ccy,DictCodeListExt1.币种_中债_人民币.getValue(),generateDate,value,rateMap,source);
        if(DictCodeListExt1.币种_中债_人民币.getValue().equals(ccy)){//人民币
            dto.setCny(dto.getCny().add(cny));
        }else if(DictCodeListExt1.币种_中债_美元.getValue().equals(ccy)){//美元
            dto.setUsdToCny((dto.getUsdToCny().add(cny)));
        }else if(DictCodeListExt1.币种_中债_欧元.getValue().equals(ccy)){//欧元
            dto.setEurToCny((dto.getEurToCny().add(cny)));
        }else if(DictCodeListExt1.币种_中债_英镑.getValue().equals(ccy)){//英镑
            dto.setGbpToCny((dto.getGbpToCny().add(cny)));
        }else if(DictCodeListExt1.币种_中债_日元.getValue().equals(ccy)){//日元
            dto.setJpyToCny((dto.getJpyToCny().add(cny)));
        }else{ //其他币种
            dto.setOthToCny((dto.getOthToCny().add(cny)));
        }
        //合计
        dto.setTotal(dto.getTotal().add(cny));
        // 记录币种金额信息，多币种存值，人民币也存值，格式：CNY,原币金额,折人民币金额;USD,原币金额,折人民币金额
        String ccyAmount = dto.getCcyAmount();
        if (StringUtil.isNotEmpty(ccyAmount)){
            // 按币种分组
            List<String> ccyAmountList = StringUtil.splitToList(GlobalConstant.SEMICOLON, ccyAmount);
            Map<String,String> ccyAmountMap = ccyAmountList.stream()
                    .collect(Collectors.toMap(p->
                    {
                        return StringUtil.splitToList(GlobalConstant.COMMA, p).get(0);
                    }, p->p,(key1,key2)->key2));
            // 按币种汇总
            if (ccyAmountMap.get(ccy) != null){
                String singleCcyAmount = ccyAmountMap.get(ccy);
                List<String> singleCcyAmountList = StringUtil
                        .splitToList(GlobalConstant.COMMA, singleCcyAmount);
                String oldCcy = singleCcyAmountList.get(0);
                BigDecimal oldOrgAmt = new BigDecimal(singleCcyAmountList.get(1));
                oldOrgAmt = oldOrgAmt.add(value);
                BigDecimal oldCnyAmt = new BigDecimal(singleCcyAmountList.get(2));
                oldCnyAmt = oldCnyAmt.add(cny);
                ccyAmountMap.put(oldCcy, oldCcy + GlobalConstant.COMMA + oldOrgAmt
                        + GlobalConstant.COMMA + oldCnyAmt);
                List<String> ccyAmountResult = new ArrayList<>(ccyAmountMap.values());
                ccyAmount = StringUtil.joinString(ccyAmountResult, GlobalConstant.SEMICOLON);
            } else {
                ccyAmount += GlobalConstant.SEMICOLON + ccy + GlobalConstant.COMMA + value
                        + GlobalConstant.COMMA + cny ;
            }
        } else {
            ccyAmount = ccy + GlobalConstant.COMMA + value
                    + GlobalConstant.COMMA + cny ;
        }
        dto.setCcyAmount(ccyAmount);
    }
    /**
     * 功能:2_1资产负债汇率折算人民币
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年3月12日
     * @param dto
     * @param
     * @param
     * @param rateMap
     * @param generateDate
     * void
     */
    private void setAssetDebtDetailValue(PbcReportAssetDebtDetailDto dto,PbcReportAssetDebtDetailDto todto,
                                         boolean isAdd,Map<String, BigDecimal> rateMap,Date generateDate,String source){
        // 记录待处理币种金额信息，多币种存值，人民币也存值，格式：CNY,原币金额,折人民币金额;USD,原币金额,折人民币金额
        String toCcyAmount = todto.getCcyAmount();
        // 按币种分组
        List<String> toCcyAmountList = StringUtil.splitToList(GlobalConstant.SEMICOLON, toCcyAmount);
        if (!CollectionUtils.isEmpty(toCcyAmountList)){
            for (String tcAmount : toCcyAmountList){
                List<String> tcAmountList = StringUtil.splitToList(GlobalConstant.COMMA, tcAmount);
                String ccy = tcAmountList.get(0);
                BigDecimal value = new BigDecimal(tcAmountList.get(1));
                // 币种折人民
                BigDecimal cny = ChbCcyConvertUtil.exchangeMoney(
                        ccy,DictCodeListExt1.币种_中债_人民币.getValue(),generateDate,value,rateMap,source);
                // 记录币种金额信息，多币种存值，人民币也存值，格式：CNY,原币金额,折人民币金额;USD,原币金额,折人民币金额
                String ccyAmount = dto.getCcyAmount();
                if (StringUtil.isNotEmpty(ccyAmount)){
                    // 按币种分组
                    List<String> ccyAmountList = StringUtil.splitToList(GlobalConstant.SEMICOLON, ccyAmount);
                    Map<String,String> ccyAmountMap = ccyAmountList.stream()
                            .collect(Collectors.toMap(p->
                            {
                                return StringUtil.splitToList(GlobalConstant.COMMA, p).get(0);
                            }, p->p,(key1,key2)->key2));
                    // 按币种汇总
                    if (ccyAmountMap.get(ccy) != null){
                        String singleCcyAmount = ccyAmountMap.get(ccy);
                        List<String> singleCcyAmountList = StringUtil
                                .splitToList(GlobalConstant.COMMA, singleCcyAmount);
                        String oldCcy = singleCcyAmountList.get(0);
                        BigDecimal oldOrgAmt = new BigDecimal(singleCcyAmountList.get(1));
                        // 是否为+操作
                        if (isAdd){
                            oldOrgAmt = oldOrgAmt.add(value);
                        } else {
                            oldOrgAmt = oldOrgAmt.subtract(value);
                        }
                        BigDecimal oldCnyAmt = new BigDecimal(singleCcyAmountList.get(2));
                        // 是否为+操作
                        if (isAdd){
                            oldCnyAmt = oldCnyAmt.add(cny);
                        } else {
                            oldCnyAmt = oldCnyAmt.subtract(cny);
                        }
                        ccyAmountMap.put(oldCcy, oldCcy + GlobalConstant.COMMA + oldOrgAmt
                                + GlobalConstant.COMMA + oldCnyAmt);
                        List<String> ccyAmountResult = new ArrayList<>(ccyAmountMap.values());
                        ccyAmount = StringUtil.joinString(ccyAmountResult, GlobalConstant.SEMICOLON);
                    } else {
                        if (!isAdd){
                            value = value.negate();
                            cny = cny.negate();
                        }
                        ccyAmount += GlobalConstant.SEMICOLON + ccy + GlobalConstant.COMMA + value
                                + GlobalConstant.COMMA + cny ;
                    }
                } else {
                    if (!isAdd){
                        value = value.negate();
                        cny = cny.negate();
                    }
                    ccyAmount = ccy + GlobalConstant.COMMA + value
                            + GlobalConstant.COMMA + cny ;
                }
                dto.setCcyAmount(ccyAmount);
            }
        }
    }
    /**
     * 表2-4 行塞值
     * @param pos
     * @param line
     * @param rateMap 汇率
     * @param source  汇率取值开关
     * @param hlx    行类型 （识别号）
     */
    private  void setAssetReportInComeValueEx(PbcMonthAssetPositionDto pos,PbcReportAssetIncomeDetail line
            ,Map<String, BigDecimal> rateMap,String source,String hlx,Map<String ,ChbLoanDetailInfo> assetMap){
        //查询资产要素信息获取资产客户主体信息
        String khztlx="";
        khztlx = pos.getInvestDirection();
        if(DictCodeListExt2.客户一级类型_住户.getValue().equals(khztlx)&&"ZH".equals(hlx)){//住户
            setInComeValue(pos,line,rateMap,source);
        }else if(DictCodeListExt2.客户一级类型_广义政府.getValue().equals(khztlx)&&"GYZF".equals(hlx)){//广义政府
            setInComeValue(pos,line,rateMap,source);
        }else if(DictCodeListExt2.客户一级类型_非金融企业.getValue().equals(khztlx)&&"FJRQY".equals(hlx)){//非金融企业
            setInComeValue(pos,line,rateMap,source);
        }else if(DictCodeListExt2.客户一级类型_金融机构.getValue().equals(khztlx)&&"JRJGST".equals(hlx)){//金融机构（实体）
            setInComeValue(pos,line,rateMap,source);
        }else if(DictCodeListExt2.客户一级类型_特定目的载体.getValue().equals(khztlx)&&"TDMDZT".equals(hlx)){//特定目的载体
            setInComeValue(pos,line,rateMap,source);
        }else if(DictCodeListExt2.客户一级类型_境外.getValue().equals(khztlx)&&"JW".equals(hlx)){//境外
            setInComeValue(pos,line,rateMap,source);
        }
    }

    /**
     * 表2-4 行塞值
     * @param pos
     * @param line
     * @param rateMap 汇率
     * @param source  汇率取值开关
     * @param hlx    行类型 （识别号）
     */
    private  void setAssetReportInComeValue(PbcMonthAssetPositionDto pos,PbcReportAssetIncomeDetail line
            ,Map<String, BigDecimal> rateMap,String source,String hlx,Map<String ,ChbAssetInfo> assetMap){
        //查询资产要素信息获取资产客户主体信息
        String khztlx="";
        ChbAssetInfo assetinfo=assetMap.get(pos.getAssetCode());
        if(assetinfo==null){
            logger.error("资产要素不存在");
            return;
        }
        khztlx=assetinfo.getCustomerObjType();
        if(DictCodeListExt2.客户一级类型_住户.getValue().equals(khztlx)&&"ZH".equals(hlx)){//住户
            setInComeValue(pos,line,rateMap,source);
        }else if(DictCodeListExt2.客户一级类型_广义政府.getValue().equals(khztlx)&&"GYZF".equals(hlx)){//广义政府
            setInComeValue(pos,line,rateMap,source);
        }else if(DictCodeListExt2.客户一级类型_非金融企业.getValue().equals(khztlx)&&"FJRQY".equals(hlx)){//非金融企业
            setInComeValue(pos,line,rateMap,source);
        }else if(DictCodeListExt2.客户一级类型_金融机构.getValue().equals(khztlx)&&"JRJGST".equals(hlx)){//金融机构（实体）
            setInComeValue(pos,line,rateMap,source);
        }else if(DictCodeListExt2.客户一级类型_特定目的载体.getValue().equals(khztlx)&&"TDMDZT".equals(hlx)){//特定目的载体
            setInComeValue(pos,line,rateMap,source);
        }else if(DictCodeListExt2.客户一级类型_境外.getValue().equals(khztlx)&&"JW".equals(hlx)){//境外
            setInComeValue(pos,line,rateMap,source);
        }
    }

    private void setInComeValue(PbcMonthAssetPositionDto pos,PbcReportAssetIncomeDetail line
            ,Map<String, BigDecimal> rateMap,String source){
        BigDecimal cnyAmt = ChbCcyConvertUtil.exchangeMoney(
                pos.getCcy(),DictCodeListExt1.币种_中债_人民币.getValue(),pos.getEnddate(),pos.getAmt(),rateMap,source);
        String code2 = "";
        if(PbcMonthReportConstants.PBC_ASSETTYPE3_Q01.equals(pos.getAssetType3())){//贷款收益权
            if(line.getLoanIncomeRight()!=null){
                line.setLoanIncomeRight(line.getLoanIncomeRight().add(cnyAmt));
            }else{
                line.setLoanIncomeRight(cnyAmt);
            }
            code2 = PbcReportProperycode.AMT_761000_DKSYQ;
        }else if(PbcMonthReportConstants.PBC_ASSETTYPE3_Q02.equals(pos.getAssetType3())){//债务证券收益权
            if(line.getDebtSecuritiesIncomeRights()!=null){
                line.setDebtSecuritiesIncomeRights(line.getDebtSecuritiesIncomeRights().add(cnyAmt));
            }else{
                line.setDebtSecuritiesIncomeRights(cnyAmt);
            }
            code2 = PbcReportProperycode.AMT_762000_ZWZQSYQ;

        }else if(PbcMonthReportConstants.PBC_ASSETTYPE3_Q03.equals(pos.getAssetType3())){//应收账款收益权
            if(line.getReceivableIncomeRights()!=null){
                line.setReceivableIncomeRights(line.getReceivableIncomeRights().add(cnyAmt));
            }else{
                line.setReceivableIncomeRights(cnyAmt);
            }
            code2 = PbcReportProperycode.AMT_763000_YSZKSYQ;

        }else if(PbcMonthReportConstants.PBC_ASSETTYPE3_Q04.equals(pos.getAssetType3())){//其他债权收益权
            if(line.getOtherCreditorRights()!=null){
                line.setOtherCreditorRights(line.getOtherCreditorRights().add(cnyAmt));
            }else{
                line.setOtherCreditorRights(cnyAmt);
            }
            code2 = PbcReportProperycode.AMT_764000_QTZQSYQ;

        }else if(PbcMonthReportConstants.PBC_ASSETTYPE3_Q05.equals(pos.getAssetType3())){//股票收益权
            if(line.getStockIncomeRight()!=null){
                line.setStockIncomeRight(line.getStockIncomeRight().add(cnyAmt));
            }else{
                line.setStockIncomeRight(cnyAmt);
            }
            code2 = PbcReportProperycode.AMT_765000_GPSYQ;

        }else if(PbcMonthReportConstants.PBC_ASSETTYPE3_Q06.equals(pos.getAssetType3())){//其他股权收益权
            if(line.getOtherEquityIncomeRights()!=null){
                line.setOtherEquityIncomeRights(line.getOtherEquityIncomeRights().add(cnyAmt));
            }else{
                line.setOtherEquityIncomeRights(cnyAmt);
            }
            code2 = PbcReportProperycode.AMT_766000_QTGQSYQ;

        }else if(PbcMonthReportConstants.PBC_ASSETTYPE3_Q07.equals(pos.getAssetType3())){//物权资产收益权
            if(line.getPropertyRights()!=null){
                line.setPropertyRights(line.getPropertyRights().add(cnyAmt));
            }else{
                line.setPropertyRights(cnyAmt);
            }
            code2 = PbcReportProperycode.AMT_767000_WQZCSYQ;

        }else{
            return;
        }
        if(null!=line.getPropertycode()&&!PbcReportProperycode.AMT_B00000_ZJ.equals(line.getPropertycode())){
            assetDetailList.add(PbcMonthReportDetailAssetUtil.buildAssetDetail2(pos,line,line.getPropertycode(),code2));
        }
    }
    /**
     * 表2-4 行塞值
     * @param pos
     * @param line
     * @param rateMap 汇率
     * @param source  汇率取值开关
     * @param
     */
    private  void setAssetReportCompanyBondValue(PbcMonthAssetPositionDto pos,PbcReportCompanyDetailDto line
            ,Map<String, BigDecimal> rateMap,String source,String hxl,Map<String,ChbAssetInfo> assetMap){
        BigDecimal cnyAmt = ChbCcyConvertUtil.exchangeMoney(
                pos.getCcy(),DictCodeListExt1.币种_中债_人民币.getValue(),pos.getEnddate(),pos.getAmt(),rateMap,source);
        //查询资产要素信息获取发行机构所属行业一级分类和发行机构类型（按规模划分）
        ChbAssetInfo assetinfo=assetMap.get(pos.getAssetCode());
        if(assetinfo==null){
            logger.error("资产要素不存在");
            return;
        }
        String industryType1 = null;
        if (assetinfo != null){
            industryType1 = assetinfo.getIndustry();
            if (StringUtils.isEmpty(industryType1)){
                industryType1 = assetinfo.getIndustryPbc();
            }
        }
        String  qygm=assetinfo.getInstitSize();
        if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_01.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_01.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }else if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_02.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_02.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }else if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_03.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_03.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }else if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_04.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_04.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }else if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_05.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_05.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }else if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_06.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_06.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }else if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_07.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_07.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }else if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_08.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_08.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }else if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_09.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_09.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }else if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_10.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_10.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }else if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_11.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_11.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }else if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_12.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_12.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }else if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_13.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_13.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }else if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_14.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_14.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }else if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_15.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_15.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }else if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_16.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_16.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }else if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_17.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_17.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }else if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_18.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_18.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }else if(PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_19.equals(industryType1)&&PbcMonthReportConstants.PBC_INDUSTRY_TYPE1_19.equals(hxl)){
            setCompanyBondValue(pos,qygm,line,cnyAmt);
        }
    }


    private void buildSvrMonthSubtraction(Map<String,PbcReportAssetDebtDetailDto> dto,Map<String,PbcReportAssetDebtDetailDto> deptDto,String ccy){

        build21report(dto, ccy);

        if(null!=deptDto){
            build21report(deptDto, ccy);
        }

    }


    private void build21report(Map<String,PbcReportAssetDebtDetailDto> dto,String ccy){
        PbcReportAssetDebtDetailDto line823000 = dto.get(PbcReportProperycode.AMT_823000_WFPLR);
        PbcReportAssetDebtDetailDto line700000 = dto.get(PbcReportProperycode.AMT_700000_ZCHJ);
        PbcReportAssetDebtDetailDto line810000 = dto.get(PbcReportProperycode.AMT_810000_FZHJ);
        PbcReportAssetDebtDetailDto line821000 = dto.get(PbcReportProperycode.AMT_821000_SSBJ);
        PbcReportAssetDebtDetailDto line822000 = dto.get(PbcReportProperycode.AMT_822000_ZBGJ);
        PbcReportAssetDebtDetailDto line820000 = dto.get(PbcReportProperycode.AMT_820000_QYHJ);
        PbcReportAssetDebtDetailDto line800000 = dto.get(PbcReportProperycode.AMT_800000_FZJQYHJ);

        PbcReportAssetDebtDetailDto line824000 = dto.get(PbcReportProperycode.AMT_824000_QTZHSY);


        line823000.setTotal(line700000.getTotal().subtract(line810000.getTotal()).subtract(line821000.getTotal()).subtract(line824000.getTotal()));
        line823000.setCny(line823000.getTotal());
        //setAssetDebtDetailValue(line823000, pos.getCcy(), BigDecimal.ZERO, rateMap, pbcReport.getEnddate(),source);
        String ccyAmount823000 = ccy+GlobalConstant.COMMA+line823000.getTotal().setScale(2,RoundingMode.HALF_UP)
                +GlobalConstant.COMMA+line823000.getTotal().setScale(2,RoundingMode.HALF_UP);
        line823000.setCcyAmount(ccyAmount823000);
        // 820000 权益合计
        line820000.setTotal(line821000.getTotal().add(line824000.getTotal()).add(line823000.getTotal()));
        line820000.setCny(line820000.getTotal());
        //setAssetDebtDetailValue(line820000, ccy, BigDecimal.ZERO, rateMap, pbcReport.getEnddate(),source);
        String ccyAmount820000 = ccy+GlobalConstant.COMMA+line820000.getTotal().setScale(2,RoundingMode.HALF_UP)
                +GlobalConstant.COMMA+line820000.getTotal().setScale(2,RoundingMode.HALF_UP);
        line820000.setCcyAmount(ccyAmount820000);
        // 800000 负债及权益合计
        line800000.setTotal(line810000.getTotal().add(line820000.getTotal()));
        line800000.setCny(line800000.getTotal());
        //setAssetDebtDetailValue(line800000, ccy, BigDecimal.ZERO, rateMap, pbcReport.getEnddate(),source);
        String ccyAmount800000 = ccy+GlobalConstant.COMMA+line800000.getTotal().setScale(2,RoundingMode.HALF_UP)
                +GlobalConstant.COMMA+line800000.getTotal().setScale(2,RoundingMode.HALF_UP);
        line800000.setCcyAmount(ccyAmount800000);
    }


    /**
     * 新增资产多法人统一入口
     * @param pos
     * @param dto
     * @param deptDto
     * @param code
     * @param rateMap
     * @param source
     * @param hxl
     * @param assetMap
     */
    private void setAssetReportValue(PbcMonthAssetPositionDto pos,Map dto,
                                     Map deptDto,String code,String rep_code,Map<String, BigDecimal> rateMap,String source,String hxl,Map assetMap){


        if(PbcReportType.PBC_REPORT_2_1.equals(rep_code)||PbcReportType.PBC_REPORT_ONEPRD_ASSETDEBT.equals(rep_code)){

            //相同科目存在取AMT或者INTAMT的情况
            BigDecimal amt = pos.getAmt();
            if(StringUtil.isNotEmpty(hxl)){
                amt = pos.getIntAmt();
            }
            //汇总数据计算
            setAssetDebtDetailValue((PbcReportAssetDebtDetailDto)dto.get(code), pos.getCcy(), amt, rateMap, (Date) assetMap.get("endDate"),source);
            if(PbcMonthReportDetailAssetUtil.isAssetDate(code)&&PbcReportType.PBC_REPORT_2_1.equals(rep_code)){
                assetDetailList.add(PbcMonthReportDetailAssetUtil.buildAssetDetail(pos,dto.get(code),code,hxl));
            }
            if(null!=deptDto&&null!=deptDto.get(pos.getInstitutionCode())){
                setAssetDebtDetailValue(((Map<String,PbcReportAssetDebtDetailDto>)deptDto.get(pos.getInstitutionCode())).get(code), pos.getCcy(), amt, rateMap, (Date) assetMap.get("endDate"),source);
            }
            //表3-3
        }else if(PbcReportType.PBC_REPORT_3_3.equals(rep_code)){
            //汇总数据计算
            setAssetReportDebttermValue(pos,(PbcReportDebtTermDetail)dto.get(code),rateMap,source,hxl);
            if(null!=deptDto&&null!=deptDto.get(code)){
                setAssetReportDebttermValue(pos,(PbcReportDebtTermDetail)deptDto.get(code),rateMap,source,hxl);
            }
            //表2-2数据组装
        }else if(PbcReportType.PBC_REPORT_2_2.equals(rep_code)){
            //汇总数据计算
            setCompanyDetailValue(pos, (PbcReportCompanyDetailDto)dto.get(code), rateMap, source, hxl, false);
            //部门数据计算
            if(null!=deptDto&&null!=deptDto.get(code)){
                setCompanyDetailValue(pos, (PbcReportCompanyDetailDto)deptDto.get(code), rateMap, source, hxl, false);
            }

            //表2-3数据组装
        }else if(PbcReportType.PBC_REPORT_2_3.equals(rep_code)){
            //汇总数据计算
            setAreaDetailValue(pos,(PbcReportAreaDetailDto)dto.get(code), rateMap,source,hxl);
            //部门数据计算
            if(null!=deptDto&&null!=deptDto.get(code)){
                setAreaDetailValue(pos,(PbcReportAreaDetailDto)deptDto.get(code), rateMap,source,hxl);
            }
            //表2-4数据组装
        }else if(PbcReportType.PBC_REPORT_2_4.equals(rep_code)){
            //汇总数据计算
            setAssetReportInComeValueEx(pos,(PbcReportAssetIncomeDetail)dto.get(code), rateMap,source,hxl,assetMap);
            //部门数据计算
            if(null!=deptDto&&null!=deptDto.get(code)){
                setAssetReportInComeValueEx(pos,(PbcReportAssetIncomeDetail)deptDto.get(code), rateMap,source,hxl,assetMap);
            }
            //表2-5数据组装
        }else if(PbcReportType.PBC_REPORT_2_5.equals(rep_code)){
            //汇总数据计算
            setAssetReportCompanyBondValue(pos,(PbcReportCompanyDetailDto)dto.get(code), rateMap,source,hxl,assetMap);
            //部门数据计算
            if(null!=deptDto&&null!=deptDto.get(code)){

                setAssetReportCompanyBondValue(pos,(PbcReportCompanyDetailDto)deptDto.get(code), rateMap,source,hxl,assetMap);
            }
        }//表4-1数据组装
        else if(PbcReportType.PBC_REPORT_4_1.equals(rep_code)){

            //汇总数据计算
            setAssetConfigurationDetailValue(pos,(PbcReportAssetConfigurationDto)dto.get(code), rateMap,source);
            if(PbcMonthReportDetailAssetUtil.isAssetDate3(code)&&PbcReportType.PBC_REPORT_4_1.equals(rep_code)){
                assetDetailList.add(PbcMonthReportDetailAssetUtil.buildAssetDetail3(pos,dto.get(code),code,hxl));
            }
            //部门数据计算
            if(null!=deptDto&&null!=deptDto.get(code)){

                setAssetConfigurationDetailValue(pos,(PbcReportAssetConfigurationDto)deptDto.get(code), rateMap,source);
            }

        }

    }

    public static void setAssetDebtDetailValueSubtract(PbcReportAssetDebtDetailDto dto,String ccy,BigDecimal value,Map<String,
            BigDecimal> rateMap,Date generateDate,String source){
        BigDecimal cny = ChbCcyConvertUtil.exchangeMoney(
                ccy,DictCodeListExt1.币种_中债_人民币.getValue(),generateDate,value,rateMap,source);
        if(DictCodeListExt1.币种_中债_人民币.getValue().equals(ccy)){//人民币
            dto.setCny(dto.getCny().subtract(cny));
        }else if(DictCodeListExt1.币种_中债_美元.getValue().equals(ccy)){//美元
            dto.setUsdToCny((dto.getUsdToCny().subtract(cny)));
        }else if(DictCodeListExt1.币种_中债_欧元.getValue().equals(ccy)){//欧元
            dto.setEurToCny((dto.getEurToCny().subtract(cny)));
        }else if(DictCodeListExt1.币种_中债_英镑.getValue().equals(ccy)){//英镑
            dto.setGbpToCny((dto.getGbpToCny().subtract(cny)));
        }else if(DictCodeListExt1.币种_中债_日元.getValue().equals(ccy)){//日元
            dto.setJpyToCny((dto.getJpyToCny().subtract(cny)));
        }else{ //其他币种
            dto.setOthToCny((dto.getOthToCny().subtract(cny)));
        }
        //合计
        dto.setTotal(dto.getTotal().subtract(cny));
        // 记录币种金额信息，多币种存值，人民币也存值，格式：CNY,原币金额,折人民币金额;USD,原币金额,折人民币金额
        String ccyAmount = dto.getCcyAmount();
        if (StringUtil.isNotEmpty(ccyAmount)){
            // 按币种分组
            List<String> ccyAmountList = StringUtil.splitToList(GlobalConstant.SEMICOLON, ccyAmount);
            Map<String,String> ccyAmountMap = ccyAmountList.stream()
                    .collect(Collectors.toMap(p->
                    {
                        return StringUtil.splitToList(GlobalConstant.COMMA, p).get(0);
                    }, p->p,(key1,key2)->key2));
            // 按币种汇总
            if (ccyAmountMap.get(ccy) != null){
                String singleCcyAmount = ccyAmountMap.get(ccy);
                List<String> singleCcyAmountList = StringUtil
                        .splitToList(GlobalConstant.COMMA, singleCcyAmount);
                String oldCcy = singleCcyAmountList.get(0);
                BigDecimal oldOrgAmt = new BigDecimal(singleCcyAmountList.get(1));
                oldOrgAmt = oldOrgAmt.subtract(value);
                BigDecimal oldCnyAmt = new BigDecimal(singleCcyAmountList.get(2));
                oldCnyAmt = oldCnyAmt.subtract(cny);
                ccyAmountMap.put(oldCcy, oldCcy + GlobalConstant.COMMA + oldOrgAmt
                        + GlobalConstant.COMMA + oldCnyAmt);
                List<String> ccyAmountResult = new ArrayList<>(ccyAmountMap.values());
                ccyAmount = StringUtil.joinString(ccyAmountResult, GlobalConstant.SEMICOLON);
            } else {
                ccyAmount += GlobalConstant.SEMICOLON + ccy + GlobalConstant.COMMA + value
                        + GlobalConstant.COMMA + cny ;
            }
        } else {
            ccyAmount = ccy + GlobalConstant.COMMA + value
                    + GlobalConstant.COMMA + cny ;
        }
        dto.setCcyAmount(ccyAmount);
    }



    /**
     * 新增资产多法人统一入口(产品头寸入口)
     * @param pos
     * @param dto
     * @param deptDto
     * @param code
     * @param rateMap
     * @param source
     * @param hxl
     * @param assetMap
     */
    private  void setPrdReportValue(PbcMonthPrdPostionDto pos,Map dto,
                                    Map deptDto,String code,String rep_code,Map<String, BigDecimal> rateMap,String source,String hxl,Map assetMap,BigDecimal amt){

        if(PbcReportType.PBC_REPORT_2_1.equals(rep_code)||PbcReportType.PBC_REPORT_ONEPRD_ASSETDEBT.equals(rep_code)){

            setAssetDebtDetailValue((PbcReportAssetDebtDetailDto)dto.get(code), pos.getCcy(),
                    amt, rateMap, (Date)assetMap.get("endDate"),source);

            if(PbcMonthReportDetailAssetUtil.isAssetPrd(code)&&PbcReportType.PBC_REPORT_2_1.equals(rep_code)){
                assetDetailList.add(PbcMonthReportDetailAssetUtil.buildAssetDetailPrd(pos,dto.get(code),code,amt));
            }

            if(null!=deptDto&&null!=deptDto.get(code)){
                setAssetDebtDetailValue((PbcReportAssetDebtDetailDto)deptDto.get(code), pos.getCcy(),
                        amt, rateMap, (Date)assetMap.get("endDate"),source);
            }

            //表3-3
        }else if(PbcReportType.PBC_REPORT_3_3.equals(rep_code)){
            //汇总数据计算
            setReportDebttermValueProd(pos,(PbcReportDebtTermDetail)dto.get(code),rateMap,source,hxl);
            if(null!=deptDto&&null!=deptDto.get(code)){
                setReportDebttermValueProd(pos,(PbcReportDebtTermDetail)deptDto.get(code),rateMap,source,hxl);
            }
        }

    }

    /**
     * 组装数据汇总项
     * @param result
     * @param deptCode
     * @param pbcReport
     * @param
     */
    private void  buildSumValAndInster  (Map result,String deptCode,
                                         PbcMonthReport pbcReport,Boolean insertFlag,Boolean subtractionFlag,Map<String, BigDecimal> rateMap,String source,List<PbcMonthReport> prodAssetRegList){



        if(PbcReportType.PBC_REPORT_2_1.equals(pbcReport.getReporttype())||PbcReportType.PBC_REPORT_ONEPRD_ASSETDEBT.equals(pbcReport.getReporttype())){

            PbcReportAssetDebtDetailDto line700000 = (PbcReportAssetDebtDetailDto) result.get(PbcReportProperycode.AMT_700000_ZCHJ);
            PbcReportAssetDebtDetailDto line800000 = (PbcReportAssetDebtDetailDto)result.get(PbcReportProperycode.AMT_800000_FZJQYHJ);
            PbcReportAssetDebtDetailDto line810000 = (PbcReportAssetDebtDetailDto)result.get(PbcReportProperycode.AMT_810000_FZHJ);
            PbcReportAssetDebtDetailDto line811000 = (PbcReportAssetDebtDetailDto)result.get(PbcReportProperycode.AMT_811000_DK);
            PbcReportAssetDebtDetailDto line812000 = (PbcReportAssetDebtDetailDto)result.get(PbcReportProperycode.AMT_812000_ZCZCZQ);
            PbcReportAssetDebtDetailDto line813000 = (PbcReportAssetDebtDetailDto)result.get(PbcReportProperycode.AMT_813000_JRYSP);
            PbcReportAssetDebtDetailDto line814000 = (PbcReportAssetDebtDetailDto)result.get(PbcReportProperycode.AMT_814000_YFZK);
            PbcReportAssetDebtDetailDto line815000 = (PbcReportAssetDebtDetailDto)result.get(PbcReportProperycode.AMT_815000_QTFZ);
            PbcReportAssetDebtDetailDto line820000 = (PbcReportAssetDebtDetailDto)result.get(PbcReportProperycode.AMT_820000_QYHJ);


            if(subtractionFlag){
                line815000.setTotal(line700000.getTotal().subtract(line820000.getTotal())
                        .subtract(line811000.getTotal()).subtract(line812000.getTotal())
                        .subtract(line813000.getTotal()).subtract(line814000.getTotal()));
                line815000.setCny(line815000.getTotal());
                String ccyAmount = DictCodeListExt1.币种_中债_人民币.getValue()+GlobalConstant.COMMA+line815000.getTotal().setScale(2,RoundingMode.HALF_UP)
                        +GlobalConstant.COMMA+line815000.getTotal().setScale(2,RoundingMode.HALF_UP);
                line815000.setCcyAmount(ccyAmount);

            }
            // 810000 负债合计
            line810000.setCny(line810000.getCny().add(line815000.getCny()));
            line810000.setUsdToCny(line810000.getUsdToCny().add(line815000.getUsdToCny()));
            line810000.setEurToCny(line810000.getEurToCny().add(line815000.getEurToCny()));
            line810000.setGbpToCny(line810000.getGbpToCny().add(line815000.getGbpToCny()));
            line810000.setJpyToCny(line810000.getJpyToCny().add(line815000.getJpyToCny()));
            line810000.setOthToCny(line810000.getOthToCny().add(line815000.getOthToCny()));
            line810000.setTotal(line810000.getTotal().add(line815000.getTotal()));
            setAssetDebtDetailValue(line810000, line815000, true, rateMap, pbcReport.getEnddate(),source);
            // 800000 负债及权益合计
            line800000.setCny(line800000.getCny().add(line815000.getCny()));
            line800000.setUsdToCny(line800000.getUsdToCny().add(line815000.getUsdToCny()));
            line800000.setEurToCny(line800000.getEurToCny().add(line815000.getEurToCny()));
            line800000.setGbpToCny(line800000.getGbpToCny().add(line815000.getGbpToCny()));
            line800000.setJpyToCny(line800000.getJpyToCny().add(line815000.getJpyToCny()));
            line800000.setOthToCny(line800000.getOthToCny().add(line815000.getOthToCny()));
            line800000.setTotal(line800000.getTotal().add(line815000.getTotal()));
            setAssetDebtDetailValue(line800000, line815000, true, rateMap, pbcReport.getEnddate(),source);



        }else if(PbcReportType.PBC_REPORT_3_3.equals(pbcReport.getReporttype())){
            //资产端
            BigDecimal hjzczwgz=BigDecimal.ZERO;//资产债务工具
            BigDecimal hjzcgq=BigDecimal.ZERO;//资产股权
            BigDecimal hjzctd=BigDecimal.ZERO;//资产特定目的载体份额

            //产品端
            BigDecimal hjfzzwgz=BigDecimal.ZERO;//负债债务工具
            BigDecimal hjfzssbj=BigDecimal.ZERO;//负债实收本金
            Iterator<Entry> entries = result.entrySet().iterator();

            while(entries.hasNext()){
                Entry<String, PbcReportDebtTermDetail> dtoMap = entries.next();
                //如果key包含DEPTcode则是
                if(dtoMap.getKey().contains(PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02)){
                    hjfzzwgz=hjfzzwgz.add(dtoMap.getValue().getDebtInstrument()==null?BigDecimal.ZERO:dtoMap.getValue().getDebtInstrument());
                    hjfzssbj=hjfzssbj.add(dtoMap.getValue().getActualAmt()==null?BigDecimal.ZERO:dtoMap.getValue().getActualAmt());
                }else{
                    hjzczwgz=hjzczwgz.add(dtoMap.getValue().getDebtInstrument()==null?BigDecimal.ZERO:dtoMap.getValue().getDebtInstrument());
                    hjzcgq=hjzcgq.add(dtoMap.getValue().getStockRight()==null?BigDecimal.ZERO:dtoMap.getValue().getStockRight());
                    hjzctd=hjzctd.add(dtoMap.getValue().getSpecialShare()==null?BigDecimal.ZERO:dtoMap.getValue().getSpecialShare());
                }
            }

            PbcReportDebtTermDetail lineg00000fz = (PbcReportDebtTermDetail) result.get(PbcReportProperycode.DEBT_G00000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02);
            PbcReportDebtTermDetail lineg00000 = (PbcReportDebtTermDetail) result.get(PbcReportProperycode.ASSET_G00000+PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01);

            lineg00000.setDebtInstrument(hjzczwgz);
            lineg00000.setStockRight(hjzcgq);
            lineg00000.setSpecialShare(hjzctd);

            lineg00000fz.setDebtInstrument(hjfzzwgz);
            lineg00000fz.setActualAmt(hjfzssbj);

            //表2-2数据组装
        }else if(PbcReportType.PBC_REPORT_2_2.equals(pbcReport.getReporttype())){
            //无需汇总

            //表2-3数据组装
        }else if(PbcReportType.PBC_REPORT_2_3.equals(pbcReport.getReporttype())){
            //无汇总数据操作

            //表2-4数据组装
        }else if(PbcReportType.PBC_REPORT_2_4.equals(pbcReport.getReporttype())){
            //数据组装
            BigDecimal hjLoanIncomeRight=BigDecimal.ZERO;
            BigDecimal hjDebtSecuritiesIncomeRights=BigDecimal.ZERO;
            BigDecimal hjReceivableIncomeRights=BigDecimal.ZERO;
            BigDecimal hjOtherCreditorRights=BigDecimal.ZERO;
            BigDecimal hjStockIncomeRight=BigDecimal.ZERO;
            BigDecimal hjOtherEquityIncomeRights=BigDecimal.ZERO;
            BigDecimal hjPropertyRights=BigDecimal.ZERO;
            Iterator<Entry> entries = result.entrySet().iterator();
            while(entries.hasNext()){
                Entry<String, PbcReportAssetIncomeDetail> dtoMap = entries.next();
                BigDecimal sgLoanIncomeRight=dtoMap.getValue().getLoanIncomeRight()==null?BigDecimal.ZERO:dtoMap.getValue().getLoanIncomeRight();
                BigDecimal sgDebtSecuritiesIncomeRights=dtoMap.getValue().getDebtSecuritiesIncomeRights()==null?BigDecimal.ZERO:dtoMap.getValue().getDebtSecuritiesIncomeRights();
                BigDecimal sgReceivableIncomeRights=dtoMap.getValue().getReceivableIncomeRights()==null?BigDecimal.ZERO:dtoMap.getValue().getReceivableIncomeRights();
                BigDecimal sgOtherCreditorRights=dtoMap.getValue().getOtherCreditorRights()==null?BigDecimal.ZERO:dtoMap.getValue().getOtherCreditorRights();
                BigDecimal sgStockIncomeRight=dtoMap.getValue().getStockIncomeRight()==null?BigDecimal.ZERO:dtoMap.getValue().getStockIncomeRight();
                BigDecimal sgOtherEquityIncomeRights=dtoMap.getValue().getOtherEquityIncomeRights()==null?BigDecimal.ZERO:dtoMap.getValue().getOtherEquityIncomeRights();
                BigDecimal sgPropertyRights=dtoMap.getValue().getPropertyRights()==null?BigDecimal.ZERO:dtoMap.getValue().getPropertyRights();
                hjLoanIncomeRight=hjLoanIncomeRight.add(sgLoanIncomeRight);
                hjDebtSecuritiesIncomeRights=hjDebtSecuritiesIncomeRights.add(sgDebtSecuritiesIncomeRights);
                hjReceivableIncomeRights=hjReceivableIncomeRights.add(sgReceivableIncomeRights);
                hjOtherCreditorRights=hjOtherCreditorRights.add(sgOtherCreditorRights);
                hjStockIncomeRight=hjStockIncomeRight.add(sgStockIncomeRight);
                hjOtherEquityIncomeRights=hjOtherEquityIncomeRights.add(sgOtherEquityIncomeRights);
                hjPropertyRights=hjPropertyRights.add(sgPropertyRights);
                BigDecimal sghj=sgLoanIncomeRight.add(sgDebtSecuritiesIncomeRights).add(sgReceivableIncomeRights)
                        .add(sgOtherCreditorRights).add(sgStockIncomeRight).add(sgOtherEquityIncomeRights).add(sgPropertyRights);
                dtoMap.getValue().setTotal(sghj);
            }
            PbcReportAssetIncomeDetail sumDto = (PbcReportAssetIncomeDetail) result.get(PbcReportProperycode.AMT_B00000_ZJ);
            BigDecimal totalhj=hjLoanIncomeRight.add(hjDebtSecuritiesIncomeRights).add(hjReceivableIncomeRights)
                    .add(hjOtherCreditorRights).add(hjStockIncomeRight).add(hjOtherEquityIncomeRights).add(hjPropertyRights);
            sumDto.setLoanIncomeRight(hjLoanIncomeRight);
            sumDto.setDebtSecuritiesIncomeRights(hjDebtSecuritiesIncomeRights);
            sumDto.setReceivableIncomeRights(hjReceivableIncomeRights);
            sumDto.setOtherCreditorRights(hjOtherCreditorRights);
            sumDto.setStockIncomeRight(hjStockIncomeRight);
            sumDto.setOtherEquityIncomeRights(hjOtherEquityIncomeRights);
            sumDto.setPropertyRights(hjPropertyRights);
            sumDto.setTotal(totalhj);

            //表2-5数据组装
        }else if(PbcReportType.PBC_REPORT_2_5.equals(pbcReport.getReporttype())){
            BigDecimal hjdxqy=BigDecimal.ZERO;//合计大型企业
            BigDecimal hjzxqy=BigDecimal.ZERO;//合计中型企业
            BigDecimal hjwxqy=BigDecimal.ZERO;//合计微型企业
            BigDecimal hjxxqy=BigDecimal.ZERO;//合计小型企业
            Iterator<Entry> entries = result.entrySet().iterator();

            while(entries.hasNext()){
                Entry<String, PbcReportCompanyDetailDto> dtoMap = entries.next();
                hjdxqy=hjdxqy.add(dtoMap.getValue().getLargeCompanyAmt()==null?BigDecimal.ZERO:dtoMap.getValue().getLargeCompanyAmt());
                hjzxqy=hjzxqy.add(dtoMap.getValue().getMediumCompanyAmt()==null?BigDecimal.ZERO:dtoMap.getValue().getMediumCompanyAmt());
                hjwxqy=hjwxqy.add(dtoMap.getValue().getMicroCompanyAmt()==null?BigDecimal.ZERO:dtoMap.getValue().getMicroCompanyAmt());
                hjxxqy=hjxxqy.add(dtoMap.getValue().getSmallCompanyAmt()==null?BigDecimal.ZERO:dtoMap.getValue().getSmallCompanyAmt());
                dtoMap.getValue().setSumAmt(dtoMap.getValue().getLargeCompanyAmt()==null?BigDecimal.ZERO:dtoMap.getValue().getLargeCompanyAmt()
                        .add(dtoMap.getValue().getMediumCompanyAmt()==null?BigDecimal.ZERO:dtoMap.getValue().getMediumCompanyAmt())
                        .add(dtoMap.getValue().getMicroCompanyAmt()==null?BigDecimal.ZERO:dtoMap.getValue().getMicroCompanyAmt())
                        .add(dtoMap.getValue().getSmallCompanyAmt()==null?BigDecimal.ZERO:dtoMap.getValue().getSmallCompanyAmt()));
            }

            PbcReportCompanyDetailDto sumDto = (PbcReportCompanyDetailDto) result.get(PbcReportProperycode.AMT_C00000_QYZQYEZJ);
            sumDto.setLargeCompanyAmt(hjdxqy);
            sumDto.setMediumCompanyAmt(hjzxqy);
            sumDto.setMicroCompanyAmt(hjwxqy);
            sumDto.setSmallCompanyAmt(hjxxqy);
            sumDto.setSumAmt(hjdxqy.add(hjzxqy).add(hjwxqy).add(hjxxqy));
        }//表4-1数据组装
        else if(PbcReportType.PBC_REPORT_4_1.equals(pbcReport.getReporttype())){
            //无汇总数据操作

        }



        //根据部门进行数据插入
        if (pbcReport.isGen()){
            //表2-1||数据组装
            if(PbcReportType.PBC_REPORT_2_1.equals(pbcReport.getReporttype())||PbcReportType.PBC_REPORT_ONEPRD_ASSETDEBT.equals(pbcReport.getReporttype())){
                pbcReportAssetDebtDetailMapper.deleteByReportDate(DateUtil.format(pbcReport.getStartdate()),
                        DateUtil.format(pbcReport.getEnddate()),pbcReport.getReporttype(),pbcReport.getPbcAccountId(),deptCode);
            }else if(PbcReportType.PBC_REPORT_3_3.equals(pbcReport.getReporttype())){
                pbcReportDebtTermDetailMapper.deleteByReportDate(DateUtil.format(pbcReport.getStartdate()),
                        DateUtil.format(pbcReport.getEnddate()),PbcReportType.PBC_REPORT_3_3,deptCode);
                //表2-2数据组装
            }else if(PbcReportType.PBC_REPORT_2_2.equals(pbcReport.getReporttype())){
                pbcReportCompanyDetailMapper.deleteByReportDate(DateUtil.format(pbcReport.getStartdate()),
                        DateUtil.format(pbcReport.getEnddate()),PbcReportType.PBC_REPORT_2_2,deptCode);
                //表2-3数据组装
            }else if(PbcReportType.PBC_REPORT_2_3.equals(pbcReport.getReporttype())){
                pbcReportAreaDetailMapper.deleteByReportDate(DateUtil.format(pbcReport.getStartdate()),
                        DateUtil.format(pbcReport.getEnddate()),PbcReportType.PBC_REPORT_2_3,deptCode);
                //表2-4数据组装
            }else if(PbcReportType.PBC_REPORT_2_4.equals(pbcReport.getReporttype())){
                pbcReportAssetIncomeDetailMapper.deleteByReportDate(DateUtil.format(pbcReport.getStartdate()),
                        DateUtil.format(pbcReport.getEnddate()),PbcReportType.PBC_REPORT_2_4,deptCode);
                //表2-5数据组装
            }else if(PbcReportType.PBC_REPORT_2_5.equals(pbcReport.getReporttype())){
                pbcReportCompanyDetailMapper.deleteByReportDate(DateUtil.format(pbcReport.getStartdate()),
                        DateUtil.format(pbcReport.getEnddate()),PbcReportType.PBC_REPORT_2_5,deptCode);
            }else if(PbcReportType.PBC_REPORT_4_1.equals(pbcReport.getReporttype())){
                pbcReportAssetConfigurationMapper.deleteByReportDate(DateUtil.format(pbcReport.getStartdate()),
                        DateUtil.format(pbcReport.getEnddate()),PbcReportType.PBC_REPORT_4_1,deptCode);
            }


            PbcMonthReport param = new PbcMonthReport();
            param.setStartdate(pbcReport.getStartdate());
            param.setEnddate(pbcReport.getEnddate());
            param.setDeptCode(deptCode);
            param.setReporttype(pbcReport.getReporttype());
            //2-1额外参数
            param.setPbcAccountId(pbcReport.getPbcAccountId());
            pbcMonthReportMapper.deleteByModel(param);


            //表2-1特殊处理
            if(PbcReportType.PBC_REPORT_2_1.equals(pbcReport.getReporttype())||PbcReportType.PBC_REPORT_ONEPRD_ASSETDEBT.equals(pbcReport.getReporttype())){
                Map<String, Object> paraMap = new HashMap<String, Object>();
                paraMap.put("startdate", pbcReport.getStartdate());
                paraMap.put("enddate", pbcReport.getEnddate());
                paraMap.put("reporttype", pbcReport.getReporttype());
                paraMap.put("deptCode", deptCode);
                paraMap.put("pbcAccountId", pbcReport.getPbcAccountId());
                pbcLoanDetailInfoSubMapper.deleteByModel(paraMap);
                pbcCapitalTrustDetailSubMapper.deleteByModel(paraMap);

                if (DictCodeListExt2.报送数据状态_删除.getValue()
                        .equals(pbcReport.getStatus())){
                    for ( PbcMonthReport dl : prodAssetRegList ) {
                        // 删除未报送的数据
                        pbcReportAssetDebtDetailMapper.deleteByReportDate(DateUtil.format(dl.getStartdate()),
                                DateUtil.format(dl.getEnddate()),pbcReport.getReporttype(),dl.getPbcAccountId(),deptCode);
                        PbcMonthReport paramDl = new PbcMonthReport();
                        paramDl.setStartdate(dl.getStartdate());
                        paramDl.setEnddate(dl.getEnddate());
                        paramDl.setReporttype(pbcReport.getReporttype());
                        paramDl.setPbcAccountId(dl.getPbcAccountId());
                        paramDl.setDeptCode(deptCode);
                        Map<String, Object> paraMap1 = new HashMap<String, Object>();
                        paraMap1.put("startdate", dl.getStartdate());
                        paraMap1.put("enddate", dl.getEnddate());
                        paraMap1.put("reporttype", pbcReport.getReporttype());
                        paraMap1.put("pbcAccountId", dl.getPbcAccountId());
                        paraMap1.put("deptCode", deptCode);
                        pbcLoanDetailInfoSubMapper.deleteByModel(paraMap1);
                        pbcCapitalTrustDetailSubMapper.deleteByModel(paraMap1);
                        pbcMonthReportMapper.deleteByModel(paramDl);

                    }
                }

            }

            // 判断是否有头寸数据有则插入
            if (insertFlag){

                String[] code = getRegistCode(deptCode);

                String uuid = IDGenerator.generate(36);
                if(!PbcReportType.PBC_REPORT_ONEPRD_ASSETDEBT.equals(pbcReport.getReporttype())){
                    pbcReport.setCreateDept(deptCode);
                    pbcReport.setDeptCode(deptCode);

                    pbcReport.setIssuePartyId(code[0]);
                    pbcReport.setIssuePartyName(code[1]);
                    pbcReport.setReportOrgName(code[1]);

                    //TODO hxy
                    //多级法人改造，add 20200620 hxy start 当机构不为空时，去查询机构登记银行代码管理中的登记银行代码
                    if(null!=deptCode && StringUtil.isNotEmpty(deptCode)){
                        //联社模式
                        Map<String, Object> parammode=new HashMap<String, Object>();
                        parammode.put("orgCode", deptCode);
                        SysInstRegBankCode001Dto bankDto=sysInstRegBankCode001Mapper.queryForPageWithMode(parammode);
                        if(null!=bankDto){
                            // 社会信用代码
                            pbcReport.setSocialCreditCode(bankDto.getInsitituteCode());
                            // 金融机构编码
                            pbcReport.setFinInstitutionCode(bankDto.getRegistBankCode());

                        }
                    }
                    //多级法人改造，add 20200620 hxy end 当机构不为空时，去查询机构登记银行代码管理中的登记银行代码
                }
                pbcReport.setReportuuid(uuid);
                pbcReport.setOrgCode(deptCode);
                // 插入主表数据
                pbcMonthReportMapper.insert(pbcReport);

                // 插入资产负债表数据
                Iterator<Entry> entries = result.entrySet().iterator();

                //表2-1数据组装
                if(PbcReportType.PBC_REPORT_2_1.equals(pbcReport.getReporttype())||PbcReportType.PBC_REPORT_ONEPRD_ASSETDEBT.equals(pbcReport.getReporttype())){
                    //TODO
                    if (pbcReport.isGen()){
                        // 判断是否有头寸数据有则插入
                        // 或者需要生成删除未报送时生成数据
                        if (insertFlag){
                            // 删除未报送的数据根据已报送的数据生成
                            if (DictCodeListExt2.报送数据状态_删除.getValue()
                                    .equals(pbcReport.getStatus())){
                                PbcMonthReport reg = prodAssetRegList.get(0);
                                String reportuuidNew = IDGenerator.generate(36);
                                reg.setReportuuid(reportuuidNew);
                                reg.setSendStatus(DictCodeListExt2.报送状态_未报送.getValue());
                                reg.setSubmitFeedbackStatus(null);
                                reg.setSubmitTime(null);
                                reg.setDeptCode(deptCode);
                                reg.setStatus(DictCodeListExt2.报送数据状态_删除.getValue());

                                reg.setIssuePartyId(code[0]);
                                reg.setIssuePartyName(code[1]);
                                reg.setReportOrgName(code[1]);

                                // 插入主表数据
                                pbcMonthReportMapper.insert(reg);
                                // 根据reportuuid查询对应的明细信息
                                String reportuuid = prodAssetRegList.get(0).getReportuuid();
                                List<PbcReportAssetDebtDetailDto> pbcAssetDetailRegList = pbcReportAssetDebtDetailMapper.selectByReportuuid(reportuuid);
                                // 插入资产负债表数据
                                for ( PbcReportAssetDebtDetailDto v : pbcAssetDetailRegList ) {
                                    v.setReportuuid(reportuuidNew);
                                    v.setDetailuuid(IDGenerator.generate(36));
                                    String ss1=setCcyAmountSacle2(v.getCcyAmount());
                                    v.setCcyAmount(ss1);
                                    pbcReportAssetDebtDetailMapper.insert(v);
                                }
                            } else {
                                // 插入主表数据
//		        	            pbcMonthReportMapper.insert(pbcReport);
                                // 插入资产负债表数据

                                while(entries.hasNext()){
                                    Entry<String, PbcReportAssetDebtDetailDto> dtoMap = entries.next();
                                    //add by leipeng 20200319  多法人改造
                                    dtoMap.getValue().setReportuuid(uuid);
                                    pbcReportAssetDebtDetailMapper.insert(dtoMap.getValue());
                                }

                                if (PbcReportType.PBC_REPORT_ONEPRD_ASSETDEBT.equals(pbcReport.getReporttype()) && pbcReport.getPbcAccountId() !=null) {
                                    // 生成贷款明细记录 === zhangyf 20191218 start
                                    genChbLoanDetailInfo(pbcReport, pbcReport.getStatusDate(),deptCode);
                                    // 生成贷款明细记录 === zhangyf 20191218 end
                                    /* 生成生成资金信托类资产投资明细信息 add by gaojian 20191220 begin */
                                    genChbCapitalTrustDetailInof(pbcReport, pbcReport.getStatusDate(),deptCode);
                                    /* 生成生成资金信托类资产投资明细信息 add by gaojian 20191220 end */
                                }
                            }
                        }
                    }


                    //表3-3数据组装
                }else if(PbcReportType.PBC_REPORT_3_3.equals(pbcReport.getReporttype())){
                    while(entries.hasNext()){
                        Entry<String, PbcReportDebtTermDetail> dtoMap = entries.next();
                        //add by leipeng 20200319  多法人改造
                        dtoMap.getValue().setReportuuid(uuid);
                        pbcReportDebtTermDetailMapper.insert(dtoMap.getValue());
                    }

                    //表2-2数据组装
                }else if(PbcReportType.PBC_REPORT_2_2.equals(pbcReport.getReporttype())){
                    while(entries.hasNext()){
                        Entry<String, PbcReportCompanyDetailDto> dtoMap = entries.next();
                        //add by leipeng 20200319  多法人改造
                        dtoMap.getValue().setReportuuid(uuid);
                        pbcReportCompanyDetailMapper.insert(dtoMap.getValue());
                    }
                    //表2-3数据组装
                }else if(PbcReportType.PBC_REPORT_2_3.equals(pbcReport.getReporttype())){
                    //遍历数据并入库
                    while(entries.hasNext()){
                        Entry<String, PbcReportAreaDetailDto> dtoMap = entries.next();
                        //add by leipeng 20200319  多法人改造
                        dtoMap.getValue().setReportuuid(uuid);
                        pbcReportAreaDetailMapper.insert(dtoMap.getValue());
                    }
                    //表2-4数据组装
                }else if(PbcReportType.PBC_REPORT_2_4.equals(pbcReport.getReporttype())){
                    //遍历数据并入库
                    while(entries.hasNext()){
                        Entry<String, PbcReportAssetIncomeDetail> dtoMap = entries.next();
                        //add by leipeng 20200319  多法人改造
                        dtoMap.getValue().setReportuuid(uuid);
                        pbcReportAssetIncomeDetailMapper.insert(dtoMap.getValue());
                    }

                    //表2-5数据组装
                }else if(PbcReportType.PBC_REPORT_2_5.equals(pbcReport.getReporttype())){
                    //遍历数据并入库
                    while(entries.hasNext()){
                        // 插入资产负债表数据
                        Entry<String, PbcReportCompanyDetailDto> dtoMap = entries.next();
                        //add by leipeng 20200319  多法人改造
                        dtoMap.getValue().setReportuuid(uuid);
                        pbcReportCompanyDetailMapper.insert(dtoMap.getValue());
                    }

                }else if(PbcReportType.PBC_REPORT_4_1.equals(pbcReport.getReporttype())){
                    //遍历数据并入库
                    while(entries.hasNext()){
                        // 插入资产负债表数据
                        Entry<String, PbcReportAssetConfigurationDto> dtoMap = entries.next();
                        //add by leipeng 20200319  多法人改造
                        dtoMap.getValue().setReportuuid(uuid);
                        pbcReportAssetConfigurationMapper.insert(dtoMap.getValue());
                    }

                }

            }
        }

    }
    private void setCompanyBondValue(PbcMonthAssetPositionDto pos,String qygm,PbcReportCompanyDetailDto line,BigDecimal cnyAmt){
        // 企业规模 (数据规范化变更)01 中型企业/02 大型企业/03 小型企业/04 微型企业/99 其他
        if (PbcMonthReportConstants.SVR_INSTIT_SIZE_01.equals(qygm)) {
            if (line.getMediumCompanyAmt() != null) {
                line.setMediumCompanyAmt(line.getMediumCompanyAmt().add(cnyAmt));
            } else {
                line.setMediumCompanyAmt(cnyAmt);
            }
        } else if (PbcMonthReportConstants.SVR_INSTIT_SIZE_02.equals(qygm)) {
            if (line.getLargeCompanyAmt() != null) {
                line.setLargeCompanyAmt(line.getLargeCompanyAmt().add(cnyAmt));
            } else {
                line.setLargeCompanyAmt(cnyAmt);
            }
        } else if (PbcMonthReportConstants.SVR_INSTIT_SIZE_03.equals(qygm)) {
            if (line.getSmallCompanyAmt() != null) {
                line.setSmallCompanyAmt(line.getSmallCompanyAmt().add(cnyAmt));
            } else {
                line.setSmallCompanyAmt(cnyAmt);
            }
        } else if (PbcMonthReportConstants.SVR_INSTIT_SIZE_04.equals(qygm)) {
            if (line.getMicroCompanyAmt() != null) {
                line.setMicroCompanyAmt(line.getMicroCompanyAmt().add(cnyAmt));
            } else {
                line.setMicroCompanyAmt(cnyAmt);
            }
        } else {
            // 企业规模(数据规范化变更)/99 其他-统计到中型企业
            if (line.getMediumCompanyAmt() != null) {
                line.setMediumCompanyAmt(line.getMediumCompanyAmt().add(cnyAmt));
            } else {
                line.setMediumCompanyAmt(cnyAmt);
            }
        }
//        assetDetailList.add(PbcMonthReportDetailAssetUtil.buildAssetDetail(pos,line,line.getPropertycode()));

    }
    /**
     * 表3-3 行塞值 从资产头寸
     * @param
     * @param line
     * @param rateMap 汇率
     * @param source  汇率取值开关
     * @param
     */
    private  void setAssetReportDebttermValue(PbcMonthAssetPositionDto pos,PbcReportDebtTermDetail line
            ,Map<String, BigDecimal> rateMap,String source,String hxl){
        if(pos.getEndDays()==null){
            pos.setEndDays(BigDecimal.ZERO);
        }
        if(pos.getEndDays().compareTo(BigDecimal.ZERO)>0&&pos.getEndDays().compareTo(new BigDecimal(30))<=0
                &&"01".equals(hxl)){
            setDebttermValue(pos,line,rateMap,source);
        }else if(pos.getEndDays().compareTo(new BigDecimal(30))>0&&pos.getEndDays().compareTo(new BigDecimal(90))<=0
                &&"02".equals(hxl)){
            setDebttermValue(pos,line,rateMap,source);
        }else if(pos.getEndDays().compareTo(new BigDecimal(90))>0&&pos.getEndDays().compareTo(new BigDecimal(180))<=0
                &&"03".equals(hxl)){
            setDebttermValue(pos,line,rateMap,source);
        }else if(pos.getEndDays().compareTo(new BigDecimal(180))>0&&pos.getEndDays().compareTo(new BigDecimal(360))<=0
                &&"04".equals(hxl)){
            setDebttermValue(pos,line,rateMap,source);
        }else if(pos.getEndDays().compareTo(new BigDecimal(360))>0&&pos.getEndDays().compareTo(new BigDecimal(720))<=0
                &&"05".equals(hxl)){
            setDebttermValue(pos,line,rateMap,source);
        }else if(pos.getEndDays().compareTo(new BigDecimal(720))>0&&pos.getEndDays().compareTo(new BigDecimal(1080))<=0
                &&"06".equals(hxl)){
            setDebttermValue(pos,line,rateMap,source);
        }else if(pos.getEndDays().compareTo(new BigDecimal(1080))>0
                &&"07".equals(hxl)){
            setDebttermValue(pos,line,rateMap,source);
        }else if(pos.getEndDays().compareTo(BigDecimal.ZERO)==0
                &&"08".equals(hxl)){
            setDebttermValue(pos,line,rateMap,source);
        }
    }
    private void setDebttermValue(PbcMonthAssetPositionDto pos,PbcReportDebtTermDetail line,Map<String, BigDecimal> rateMap,String source){
        BigDecimal cnyAmt = ChbCcyConvertUtil.exchangeMoney(
                pos.getCcy(),DictCodeListExt1.币种_中债_人民币.getValue(),pos.getEnddate(),pos.getAmt(),rateMap,source);
        BigDecimal cnyIntAmt = ChbCcyConvertUtil.exchangeMoney(
                pos.getCcy(),DictCodeListExt1.币种_中债_人民币.getValue(),pos.getEnddate(),pos.getIntAmt(),rateMap,source);
        if (PbcMonthReportConstants.PBC_GROUPTYPE_3_3_01.equals(line.getGrouptype())
                && PbcMonthReportConstants.POSITION_TYPE_ZC.equals(pos.getPositionType())
                && !PbcMonthReportConstants.PBC_ASSETTYPE3_J03.equals(pos.getAssetType3())
                && !PbcMonthReportConstants.PBC_ASSETTYPE3_J04.equals(pos.getAssetType3())
        ) {
            // 应收账款的利息值统计到资产：债务工具
            if (line.getDebtInstrument() != null) {
                line.setDebtInstrument(line.getDebtInstrument().add(cnyIntAmt));
            } else {
                line.setDebtInstrument(cnyIntAmt);
            }
            if (// PbcMonthReportConstants.PBC_ASSETTYPE1_P01.equals(pos.getAssetType1())// 现金
                // ||
                    PbcMonthReportConstants.PBC_ASSETTYPE1_P02.equals(pos.getAssetType1())// 存款
                            || PbcMonthReportConstants.PBC_ASSETTYPE1_P03.equals(pos.getAssetType1())// 可转让存单
                            || PbcMonthReportConstants.PBC_ASSETTYPE1_P04.equals(pos.getAssetType1())// 债务证券
                            || PbcMonthReportConstants.PBC_ASSETTYPE1_P05.equals(pos.getAssetType1())// 贷款
                            || PbcMonthReportConstants.PBC_ASSETTYPE1_P06.equals(pos.getAssetType1())// 资产收益权
                            || PbcMonthReportConstants.PBC_ASSETTYPE1_P08.equals(pos.getAssetType1())// 金融衍生品
                            || PbcMonthReportConstants.PBC_ASSETTYPE1_P11.equals(pos.getAssetType1())) {// 应收账款
                // 债务工具
                if (line.getDebtInstrument() != null) {
                    line.setDebtInstrument(line.getDebtInstrument().add(cnyAmt));
                } else {
                    line.setDebtInstrument(cnyAmt);
                }
            } else if (PbcMonthReportConstants.PBC_ASSETTYPE2_T01.equals(pos.getAssetType2())// 股票
                    || PbcMonthReportConstants.PBC_ASSETTYPE2_T03.equals(pos.getAssetType2())// 其他股权
                    || PbcMonthReportConstants.PBC_ASSETTYPE2_T04.equals(pos.getAssetType2())// 境外股权
            ) {// 股权
                if (line.getStockRight() != null) {
                    line.setStockRight(line.getStockRight().add(cnyAmt));
                } else {
                    line.setStockRight(cnyAmt);
                }
            } else if (PbcMonthReportConstants.PBC_ASSETTYPE2_T02.equals(pos.getAssetType2())) {// 特定目的载体份额
                if (line.getSpecialShare() != null) {
                    line.setSpecialShare(line.getSpecialShare().add(cnyAmt));
                } else {
                    line.setSpecialShare(cnyAmt);
                }
            }
//            assetDetailList.add(PbcMonthReportDetailAssetUtil.buildAssetDetail(pos,line,line.getPropertycode()));
        } else if (PbcMonthReportConstants.PBC_GROUPTYPE_3_3_02.equals(line.getGrouptype())
                && (PbcMonthReportConstants.POSITION_TYPE_FZ.equals(pos.getPositionType())
                || PbcMonthReportConstants.PBC_ASSETTYPE3_J03.equals(pos.getAssetType3())
                || PbcMonthReportConstants.PBC_ASSETTYPE3_J04.equals(pos.getAssetType3())
        )){
            // 应付账款的利息值统计到负债：债务工具
            if (line.getDebtInstrument() != null) {
                line.setDebtInstrument(line.getDebtInstrument().add(cnyIntAmt));
            } else {
                line.setDebtInstrument(cnyIntAmt);
            }
            if (PbcMonthReportConstants.PBC_ASSETTYPE1_P05.equals(pos.getAssetType1())// 贷款
                    || PbcMonthReportConstants.PBC_ASSETTYPE1_P08.equals(pos.getAssetType1())// 金融衍生品
                    || PbcMonthReportConstants.PBC_ASSETTYPE1_P12.equals(pos.getAssetType1())// 应付账款
                    || PbcMonthReportConstants.PBC_ASSETTYPE1_P13.equals(pos.getAssetType1())// 其他负债
            ) {// 债务工具 负债权益类
                if (line.getDebtInstrument() != null) {
                    line.setDebtInstrument(line.getDebtInstrument().add(cnyAmt));
                } else {
                    line.setDebtInstrument(cnyAmt);
                }
            }
//            assetDetailList.add(PbcMonthReportDetailAssetUtil.buildAssetDetail(pos,line,line.getPropertycode()));
        }
    }
    private  void setReportDebttermValueProd(PbcMonthPrdPostionDto pos,PbcReportDebtTermDetail line
            ,Map<String, BigDecimal> rateMap,String source,String hxl){
        BigDecimal cnyAmt = ChbCcyConvertUtil.exchangeMoney(
                pos.getCcy(),DictCodeListExt1.币种_中债_人民币.getValue(),pos.getEnddate(),pos.getEndqtysum(),rateMap,source);
        if(pos.getEndDays().compareTo(BigDecimal.ZERO)>0&&pos.getEndDays().compareTo(new BigDecimal(30))<=0
                &&"01".equals(hxl)){
            if(line.getActualAmt()!=null){
                line.setActualAmt(line.getActualAmt().add(cnyAmt));
            }else{
                line.setActualAmt(cnyAmt);
            }
        }else if(pos.getEndDays().compareTo(new BigDecimal(30))>0&&pos.getEndDays().compareTo(new BigDecimal(90))<=0
                &&"02".equals(hxl)){
            if(line.getActualAmt()!=null){
                line.setActualAmt(line.getActualAmt().add(cnyAmt));
            }else{
                line.setActualAmt(cnyAmt);
            }
        }else if(pos.getEndDays().compareTo(new BigDecimal(90))>0&&pos.getEndDays().compareTo(new BigDecimal(180))<=0
                &&"03".equals(hxl)){
            if(line.getActualAmt()!=null){
                line.setActualAmt(line.getActualAmt().add(cnyAmt));
            }else{
                line.setActualAmt(cnyAmt);
            }
        }else if(pos.getEndDays().compareTo(new BigDecimal(180))>0&&pos.getEndDays().compareTo(new BigDecimal(360))<=0
                &&"04".equals(hxl)){
            if(line.getActualAmt()!=null){
                line.setActualAmt(line.getActualAmt().add(cnyAmt));
            }else{
                line.setActualAmt(cnyAmt);
            }
        }else if(pos.getEndDays().compareTo(new BigDecimal(360))>0&&pos.getEndDays().compareTo(new BigDecimal(720))<=0
                &&"05".equals(hxl)){
            if(line.getActualAmt()!=null){
                line.setActualAmt(line.getActualAmt().add(cnyAmt));
            }else{
                line.setActualAmt(cnyAmt);
            }
        }else if(pos.getEndDays().compareTo(new BigDecimal(720))>0&&pos.getEndDays().compareTo(new BigDecimal(1080))<=0
                &&"06".equals(hxl)){
            if(line.getActualAmt()!=null){
                line.setActualAmt(line.getActualAmt().add(cnyAmt));
            }else{
                line.setActualAmt(cnyAmt);
            }
        }else if(pos.getEndDays().compareTo(new BigDecimal(1080))>0
                &&"07".equals(hxl)){
            if(line.getActualAmt()!=null){
                line.setActualAmt(line.getActualAmt().add(cnyAmt));
            }else{
                line.setActualAmt(cnyAmt);
            }
        }else if(pos.getEndDays().compareTo(BigDecimal.ZERO)==0
                &&"08".equals(hxl)){
            if(line.getActualAmt()!=null){
                line.setActualAmt(line.getActualAmt().add(cnyAmt));
            }else{
                line.setActualAmt(cnyAmt);
            }
        }
    }
    /**
     * 功能:查询资产要素
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年3月11日
     * @param
     * @return
     * List<AssetInfoDto>
     */
    public List<ChbAssetInfo> queryAssetInfoBatch(){
        List<ChbAssetInfo> assetInfoList = new ArrayList<ChbAssetInfo>();
        Map<String,Object> param = new HashMap<String,Object>();
        List<String> statusList = new ArrayList<>();
        statusList.add(DictCodeListExt1.维护数据状态_有效.getValue());
        statusList.add(DictCodeListExt1.维护数据状态_修改.getValue());
        param.put("statusList", statusList);
        template.select("com.joyintech.tams.svreport.pbc.assetposition.dao.PbcMonthAssetPositionMapper.selectAssetByStatus",
                param,new ResultHandler() {
                    @Override
                    public void handleResult(ResultContext resultContext) {
                        assetInfoList.add((ChbAssetInfo) resultContext.getResultObject());
                    }
                });
        return assetInfoList;
    }


    /**
     * 功能:2-2除回购和拆借外贷款按行业、企业
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年3月12日
     * @param pos 头寸数据
     * @param dto 生成的报表数据
     * @param rateMap 汇率map
     * @param source 汇率取值来源
     * @param companySize 企业规模
     * @param isCountBlnArea 是否统计客户主体类型
     * void
     */
    public void setCompanyDetailValue(PbcMonthAssetPositionDto pos,PbcReportCompanyDetailDto dto,
                                      Map<String, BigDecimal> rateMap,String source,String companySize,boolean isCountBlnArea){
        BigDecimal cnyAmt = ChbCcyConvertUtil.exchangeMoney(
                pos.getCcy(),DictCodeListExt1.币种_中债_人民币.getValue(),pos.getEnddate(),pos.getAmt(),rateMap,source);
        //企业规模  (数据规范化变更)01 中型企业/02 大型企业/03 小型企业/04 微型企业/99 其他
        if(PbcMonthReportConstants.SVR_INSTIT_SIZE_01.equals(companySize)){
            dto.setMediumCompanyAmt(dto.getMediumCompanyAmt().add(cnyAmt));
        }else if(PbcMonthReportConstants.SVR_INSTIT_SIZE_02.equals(companySize)){
            dto.setLargeCompanyAmt(dto.getLargeCompanyAmt().add(cnyAmt));
        }else if(PbcMonthReportConstants.SVR_INSTIT_SIZE_03.equals(companySize)){
            dto.setSmallCompanyAmt(dto.getSmallCompanyAmt().add(cnyAmt));
        }else if(PbcMonthReportConstants.SVR_INSTIT_SIZE_04.equals(companySize)){
            dto.setMicroCompanyAmt(dto.getMicroCompanyAmt().add(cnyAmt));
        }else{
            dto.setOthAmt(dto.getOthAmt().add(cnyAmt));
        }

        if (isCountBlnArea){
            dto.setOthAmt(dto.getOthAmt().add(cnyAmt));
        }
        // 合计
        dto.setSumAmt(dto.getSumAmt().add(cnyAmt));
    }


    /**
     * 功能:2-3除回购和拆借外贷款分地区统计
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年3月12日
     * @param pos
     * @param dto
     * void
     */
    public void setAreaDetailValue(PbcMonthAssetPositionDto pos,PbcReportAreaDetailDto dto,
                                   Map<String, BigDecimal> rateMap,String source,String province){
        BigDecimal cnyAmt = ChbCcyConvertUtil.exchangeMoney(
                pos.getCcy(),DictCodeListExt1.币种_中债_人民币.getValue(),pos.getEnddate(),pos.getAmt(),rateMap,source);
        dto.setBalance(dto.getBalance().add(cnyAmt));
    }

    /**
     * 功能:4-1 资管产品资产配置情况
     * @since 1.1.0
     * @version 1.1.0
     * @author：zhengxiaolian 2022年1月11日
     * @param pos
     * @param dto
     * void
     */
    public void setAssetConfigurationDetailValue(PbcMonthAssetPositionDto pos,PbcReportAssetConfigurationDto dto,
                                                 Map<String, BigDecimal> rateMap,String source){
        BigDecimal cnyAmt = ChbCcyConvertUtil.exchangeMoney(
                pos.getCcy(),DictCodeListExt1.币种_中债_人民币.getValue(),pos.getEnddate(),pos.getAmt(),rateMap,source);
        dto.setBalance(dto.getBalance().add(cnyAmt));
    }

    /**
     * 功能:判断俩个登记数据是否一致
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年6月17日
     * @param
     * @param pbcMonthReportRegList
     * @return
     * boolean
     */
    public boolean compareData(Map<String,PbcReportAssetDebtDetailDto> pbcAssetDetailMap,List<PbcMonthReport> pbcMonthReportRegList){
        if (CollectionUtils.isEmpty(pbcMonthReportRegList)){
            return false;
        }
        // 先判断已登记的数据是否大小超过1
        if (pbcMonthReportRegList.size() > 1){
            return false;
        }
        // 根据reportuuid查询对应的明细信息
        String reportuuid = pbcMonthReportRegList.get(0).getReportuuid();
        List<PbcReportAssetDebtDetailDto> pbcAssetDetailRegList = pbcReportAssetDebtDetailMapper.selectByReportuuid(reportuuid);
        // 俩者只要有一个为空则认为不相等
        if (MapUtils.isEmpty(pbcAssetDetailMap)|| CollectionUtils.isEmpty(pbcAssetDetailRegList)){
            return false;
        }
        // 先判断生成的数据大小是否一致
        if (pbcAssetDetailMap.keySet().size() != pbcAssetDetailRegList.size()){
            return false;
        }
        // 根据报表类型分map
        Map<String,PbcReportAssetDebtDetailDto> pbcAssetDetailRegMap = pbcAssetDetailRegList.stream()
                .collect(Collectors.toMap(p->p.getPropertycode(), p->p,(key1,key2)->key2));
        // 判断数据是否相等
        for (String key : pbcAssetDetailMap.keySet()){
            if (!pbcAssetDetailRegMap.containsKey(key)){
                return false;
            }
            PbcReportAssetDebtDetailDto compare1 = genDto(pbcAssetDetailMap.get(key));
            PbcReportAssetDebtDetailDto compare2 = genDto(pbcAssetDetailRegMap.get(key));
            if(!JSON.toJSONString(compare1).equals(JSON.toJSONString(compare2))){
                return false;
            }
        }
        return true;
    }

    /**
     * 功能:根据源数据获取比较的dto
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年6月17日
     * @param orgDto
     * @return
     * PbcProdRaiseSub
     */
    private PbcReportAssetDebtDetailDto genDto(PbcReportAssetDebtDetailDto orgDto) {
        PbcReportAssetDebtDetailDto sub = new PbcReportAssetDebtDetailDto();
        sub.setReporttype(orgDto.getReporttype());
        sub.setGrouptype(orgDto.getGrouptype());
        sub.setPropertycode(orgDto.getGrouptype());
        sub.setCny((orgDto.getCny()==null?BigDecimal.ZERO:orgDto.getCny()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setUsdToCny((orgDto.getUsdToCny()==null?BigDecimal.ZERO:orgDto.getUsdToCny()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setEurToCny((orgDto.getEurToCny()==null?BigDecimal.ZERO:orgDto.getEurToCny()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setGbpToCny((orgDto.getGbpToCny()==null?BigDecimal.ZERO:orgDto.getGbpToCny()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setJpyToCny((orgDto.getJpyToCny()==null?BigDecimal.ZERO:orgDto.getJpyToCny()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setOthToCny((orgDto.getOthToCny()==null?BigDecimal.ZERO:orgDto.getOthToCny()).setScale(2, BigDecimal.ROUND_HALF_UP));
        sub.setTotal((orgDto.getTotal()==null?BigDecimal.ZERO:orgDto.getTotal()).setScale(2, BigDecimal.ROUND_HALF_UP));
        return sub;
    }
    /**
     *
     * @Description 找出两个整型的较小值
     * @Author      高健
     * @Date        2019年12月18日 下午5:55:42
     * @param @param firstNum
     * @param @param secondNum
     * @param @return 参数
     * @return Integer 返回类型
     * @throws
     */
    private Integer findMinNum(Integer firstNum, Integer secondNum) {
        if (firstNum < secondNum) {
            return firstNum;
        } else {
            return secondNum;
        }
    }

    public String[] getRegistCode(String orgCode){
        // String issuePartyId = CommonUtil.selectBankCode();
//        String issuePartyId = CommonUtil.selectBankCode();
//        String  issuePartyName = CommonUtil.selectBankCode();
        String issuePartyId = CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
        String issuePartyName = CacheUtil.getSysParam("SVR_ISSUE_PARTY_NAME");
        // 发行机构代码取值
        if(null!=orgCode){
            Map<String,Object> param = new HashMap<>();
            List<String> orgCodeList = new ArrayList<>();
            orgCodeList.add(orgCode);
            param.put("orgCodeList", orgCodeList);
            List<SysInstRegBankCode001Dto> list = sysInstRegBankCode001Mapper.queryForPage(param);

            if(null!=list&&list.size()>0){
                issuePartyId = list.get(0).getRegistBankCode();
                SysInstitution dtp = (SysInstitution) CommonUtil.queryCode(GlobalConstant.SYS_INSTITUTION_KEY,orgCode);

                issuePartyName =  null!=dtp?dtp.getInstitutionName():issuePartyName;
            }
        }
        return new String[]{issuePartyId,issuePartyName};
    }

}
