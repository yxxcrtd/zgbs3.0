package com.joyintech.tams.svreport.interf.chb.assetinfo.service;

import com.joyintech.framework.base.intf.DataDict;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.busicommon.productRating.service.CommonProductRatingService;
import com.joyintech.tams.common.BaseInterfService;
import com.joyintech.tams.common.InterfDefine;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.common.svreport.base.service.ChbTypeMappingService;
import com.joyintech.tams.common.svreport.chb.constant.ChbInterfConfig;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.ChbInterfUtil;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.report.interf.common.constant.InterfConstant;
import com.joyintech.tams.report.interf.common.constant.InterfConstants;
import com.joyintech.tams.report.interf.common.constant.InterfMappingConstants;
import com.joyintech.tams.svreport.interf.chb.assetinfo.dao.ChbAssetInfoInterfMapper;
import com.joyintech.tams.svreport.interf.chb.assetinfo.dao.ChbTypeMappingMapper;
import com.joyintech.tams.svreport.interf.chb.baseinfo.dao.SvrMstCustomerInfoAddImpMapper;
import com.joyintech.tams.svreport.interf.chb.conf.DataSourceType;
import com.joyintech.tams.svreport.interf.chb.investtrade.dao.ChbMstCounterPartyInterfMapper;
import com.joyintech.tams.systemtools.service.TamsBaseCommonService;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能:资产要素表接口
 * @since 1.6.0
 * @version 1.6.0
 * @作者：weiyuqiang
 * @创建时间：2019年2月18日
 * @修改记录：
 */
@SuppressWarnings("rawtypes")
@Service
@Transactional(rollbackFor = Exception.class)
public class ChbAssetInfoInterf  extends BaseInterfService {
    
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ChbAssetInfoInterfMapper chbAssetInfoInterfMapper;
    
    @Autowired
    private ChbTypeMappingMapper chbTypeMappingMapper;

    /**
     * 角色权限service
     */
    @Autowired
    private TamsBaseCommonService baseCommonService;
    
    /**
     * 映射service
     */
    @Autowired
    private ChbTypeMappingService chbTypeMappingService;
    
    @Autowired
    private ChbMstCounterPartyInterfMapper chbMstCounterPartyInterfMapper;

    @Autowired
    private CommonProductRatingService commonProductRatingService;

    @Autowired
    private SvrMstCustomerInfoAddImpMapper svrMstCustomerInfoAddImpMapper;
    
    @Autowired
    private SqlSessionTemplate template;
    
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 功能:接入资产要素信息
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年2月18日
     * @param batchDate 跑批日
     * @prodList 产品代码
     * @param isSynchronizeTask 是否为轮训批处理
     * void
     */
    public Map<String,List<String>> generateAssetInfoData(Date batchDate,List<String> prodList,boolean isSynchronizeTask,String institutionCode) {
        // 查询数据
        List<ChbAssetInfo> insertDataList = getBusEntity(prodList, batchDate, institutionCode, "D");

        if(CollectionUtils.isNotEmpty(insertDataList)){
            logger.debug("上游资产要素信息插入:----开始------" +  df.format(new Date()));
            insertDataMerge(insertDataList,null);
            logger.debug("上游资产要素信息插入:----结束------" +  df.format(new Date()));
        }
        return new HashMap<>();
    }

    @Override
    public List<ChbAssetInfo> getBusEntity(List<String> prodIdlist, Date date, String institutionCode, String source) {
        // 通道交易是否穿透報送 01 是 02 否
        String isChlBtm = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_CHL_BTM);
        // 用于返回的数据
        Map<String,List<String>> mapPara = new HashMap<String,List<String>>();
        List<String> assetList = new ArrayList<String>();
        // 切换数据源查询信息
        Date lastUpdatetime = ChbInterfUtil.queryUpdateTime(ChbInterfConfig.中债资产要素信息接口.getInterId());
//        Date lastUpdatetime = DateUtil.parse("2019-09-06");
        // 查询信息
        ChbAssetInfoInterf chbAssetInfoInterf =  SpringContextUtil.getBean(ChbAssetInfoInterf.class);
        logger.debug("上游资产要素信息查询:----开始------" +  df.format(new Date()));
        // 走共通逻辑取主体评级结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Object> productRatingOrderSqlMap = new HashMap<>();
//        String gradeDateEnd = formatter.format(batchDate);
        productRatingOrderSqlMap.put("oType","01");
        String getProductRatingOrderSqlByZt = commonProductRatingService.getProductRatingOrderSql(productRatingOrderSqlMap);
        // 调用原中债资产报送状态更新
        chbAssetInfoInterfMapper.updateRepAssetStatus(new RepAssetDept());
        // 查询当日新增/更改资产要素信息
        List<ChbAssetInfo> listChbAssetInfo = chbAssetInfoInterf.queryAssetByInterfdateBatch(lastUpdatetime,prodIdlist,"",getProductRatingOrderSqlByZt);
        // 不穿透报送通道资产信息
        if (DictCodeList.是否_中债_否.getValue().equals(isChlBtm)){
            // 查询当日新增/更改通道资产要素信息
            List<ChbAssetInfo> listChlAssetInfo = chbAssetInfoInterf.queryChlAssetByInterfdateBatch(lastUpdatetime,prodIdlist);
            if (CollectionUtils.isNotEmpty(listChlAssetInfo)){
                listChbAssetInfo.addAll(listChlAssetInfo);
            }
        }
        logger.debug("上游资产要素信息查询:----结束------" +  df.format(new Date()));
//      // 登记更新列表
//      List<ChbAssetInfo> updateDataList = new ArrayList<ChbAssetInfo>();
        // 登记插入列表
        List<ChbAssetInfo> insertDataList = new ArrayList<ChbAssetInfo>();
        // 发行机构代码
        //String issueId = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ISSUE_PARTY_ID);
        //String issueId = CommonUtil.selectBankCode();
        String issueId = "";
        //根据登录用户获取当前机构的登记编码
        if(institutionCode!=null) {
            String orgCode = "";
            if (MultilevelDataAuthorityUtil.isEnabled()) {
                orgCode = institutionCode;
            } else {
                // 银行模式下 只有一个总行  直接查总行对应的 code
                orgCode = baseCommonService.getYHGeneralBankCode();
            }

            String code = baseCommonService.selectBankCode(orgCode);

            if (MultilevelDataAuthorityUtil.isEnabled() && StringUtil.isEmpty(code)) {
                code = CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
            }
            issueId = null!=code?code:"";
        }else{
            issueId = CommonUtil.selectBankCode();
        }
        // 映射源数据和中债所需数据
        List<ChbTypeMapping> assetTypeTwoMappingList = chbTypeMappingMapper.selectMappingBySrcDataType(InterfMappingConstants.MAAPING_ASSETTYPETWO_ZG);
        Map<String, ChbTypeMapping> assetTypeTwoMappingMap  = new HashMap<String, ChbTypeMapping>();
        if (!StringUtil.isEmptyList(assetTypeTwoMappingList)){
            assetTypeTwoMappingMap = assetTypeTwoMappingList.stream().collect(Collectors.toMap(ChbTypeMapping::getSrcdatatypeid, chbTypeMapping -> chbTypeMapping));
        }
        // 交易对手信息
        List<ChbMstCounterParty> allCounterList = chbMstCounterPartyInterfMapper.selectAllCounterParty();
        // 交易对手信息
        Map<String,ChbMstCounterParty> counterPartyMap = allCounterList.stream().collect(
                Collectors.toMap(item->item.getCounterId(), item->item,(k1,k2)->k1));

        //获取市场机构规模数据
        //获取市场机构规模数据
        List<MstCustomerInfoAdd> institList = svrMstCustomerInfoAddImpMapper.getAllMstCustomerInfoInstit();
        Map<String,String> institMap = institList.stream().collect(HashMap<String,String>::new,
                (map, item)->map.put(item.getCustomerId(), item.getPartyType1()), (m, n)->m.putAll(n));
        for(ChbAssetInfo assetInfo : listChbAssetInfo){
            //新的最后更新时间
            if (assetInfo.getUpdateTime() != null
                    && lastUpdatetime.compareTo(assetInfo.getUpdateTime()) < 0){
                lastUpdatetime = assetInfo.getUpdateTime();
            }
            // 填充默认值
            assetInfo.setAssetUuid(IDGenerator.generate(36));
            // 查询sql中已经写了E状态，并且拆借、质押式回购、买断式回购交易需获取D状态的数据，估这里注释掉
//          assetInfo.setStatus(DictCodeListExt1.维护数据状态_有效.getValue());
            //发行机构代码取值
            assetInfo.setIssuePartyId(issueId);
            // 数据日期取原始数据日期
//          assetInfo.setStatusDate(batchDate);
            assetInfo.setOrgCode(CommonUtil.getOrgCode());
            assetInfo.setDeptCode(CommonUtil.getDeptCode());
            assetInfo.setStatusDate(date);
            // 映射源数据和中债所需数据
            if (!StringUtil.isEmptyList(assetTypeTwoMappingList)){
                if (assetTypeTwoMappingMap.get(assetInfo.getAssetType2()) != null){
                    assetInfo.setAssetType2(assetTypeTwoMappingMap.get(assetInfo.getAssetType2()).getObjdatatypeid());
                }
            }
            // 通过映射的中债字段反射资产负债类别 ,通过link表实现

            DataDictItem dictItem = getNameFromDataDict(DictCode.资产_负债二级类别,assetInfo.getAssetType2());
            if (dictItem != null && StringUtil.isNotEmpty(dictItem.getParentId())){
                String assetType = "";
                String[] parentList = StringUtil.split(dictItem.getParentId(),GlobalConstant.COMMA);
                for(String parent :parentList){
                    String[] p = StringUtil.split(parent,GlobalConstant.POINT);
                    if(DictCode.资产_负债类别.getValue().equals(p[0]))
                    {
                        if(!DictCodeListExt3.资产_负债类别_公募和私募基金.getValue().equals(p[1])){
                            assetType = p[1];
                        }
                    }

                }
                assetInfo.setAssetType(assetType);
            }
            // 为委外资产时，assetid=assetcode
            if("16".equals(assetInfo.getAssetType())) {
                assetInfo.setAssetId(assetInfo.getAssetCode());
            }
            // 项目类资产，资产类别信贷资产流转项目，资产/负债类别=2201,ASSET_TYPE3默认02,10月份版本投管修改后再更改
            if("2201".equals(assetInfo.getAssetType2())) {
                assetInfo.setAssetType3("02");
            }

            //modify by zhangliang 20201212 当是否有预期收益率为否时不取
            if (StringUtil.equals(assetInfo.getRateFlag(), "02")){
                assetInfo.setAssetYield(null);
            }
            // 是否字段
            // 是否为结构性存款
//          assetInfo.setIsStruct(SysDataUtil.getNameFromDataDict(DictCode.是否_中债字段映射,assetInfo.getIsStruct()));
//          // 是否属于买入返售
//          assetInfo.setIsResale(SysDataUtil.getNameFromDataDict(DictCode.是否_中债字段映射,assetInfo.getIsResale()));
//          // 是否有预期收益率
//          assetInfo.setRateFlag(SysDataUtil.getNameFromDataDict(DictCode.是否_中债字段映射,assetInfo.getRateFlag()));
//          // 项目是否属于重点监控行业和领域
//          assetInfo.setIndustryFlag(SysDataUtil.getNameFromDataDict(DictCode.是否_中债字段映射,assetInfo.getIndustryFlag()));
//          // 是否为收/受益权
//          assetInfo.setIsRight(SysDataUtil.getNameFromDataDict(DictCode.是否_中债字段映射,assetInfo.getIsRight()));
//          // 是否为质押融资
//          assetInfo.setIsPledge(SysDataUtil.getNameFromDataDict(DictCode.是否_中债字段映射,assetInfo.getIsPledge()));
//          // 是否掌控计划底层资产详情
//          assetInfo.setIsCtrlBottom(SysDataUtil.getNameFromDataDict(DictCode.是否_中债字段映射,assetInfo.getIsCtrlBottom()));
//          // 是否底层资产
//          assetInfo.setIsUnderAsset(SysDataUtil.getNameFromDataDict(DictCode.是否_中债字段映射,assetInfo.getIsUnderAsset()));
//          // 是否属于政府投资基金
//          assetInfo.setIsGovernFund(SysDataUtil.getNameFromDataDict(DictCode.是否_中债字段映射,assetInfo.getIsGovernFund()));
//          //   债券主体评级
//            assetInfo.setSecMainGrade(SvrDataUtil.getNameFromDataDict(DictCode.资产评级,assetInfo.getSecMainGrade()));
//          // 债券信用评级
//          assetInfo.setSecCreditGradeType(SysDataUtil.getNameFromDataDict(SVReportDictCode.债券评级映射,assetInfo.getSecCreditGradeType()));
//          // 发行机构主体信用评级
//          assetInfo.setPartyGrade(SysDataUtil.getNameFromDataDict(SVReportDictCode.债券评级映射,assetInfo.getPartyGrade()));
//          // 融资人内部信用评级
//          assetInfo.setFcrGradeIn(SysDataUtil.getNameFromDataDict(SVReportDictCode.债券评级映射,assetInfo.getFcrGradeIn()));
//          // 外部评级机构名称及融资人评级结果
//          assetInfo.setFcrGradeOut(SysDataUtil.getNameFromDataDict(SVReportDictCode.债券评级映射,assetInfo.getFcrGradeOut()));
            // 融资人主体评级
//          assetInfo.setFinancierGrade(SysDataUtil.getNameFromDataDict(SVReportDictCode.债券评级映射,assetInfo.getFinancierGrade()));
//          // 资产内部评级
//          assetInfo.setInGrade(SysDataUtil.getNameFromDataDict(SVReportDictCode.债券评级映射,assetInfo.getInGrade()));
//          // 资产外部评级
//          assetInfo.setOutGrade(SysDataUtil.getNameFromDataDict(SVReportDictCode.债券评级映射,assetInfo.getOutGrade()));
            // 数据来源
            assetInfo.setDataSource("DB");
            // 客户主体类型映射
            if (StringUtil.isNotEmpty(assetInfo.getCustomerObjType())){
                // 映射后的数据
                String customerType = chbTypeMappingService.getMappingValue(InterfConstant.MAPPINGCODE_KHZTLX,assetInfo.getCustomerObjType());
                if (StringUtil.isNotEmpty(customerType)){
                    assetInfo.setCustomerObjType(customerType);
                }
            }
//          if (StringUtil.isNotEmpty(assetInfo.getAssetCode())){
//              if (chbAssetInfoInterfMapper.selectIsExistAssetByAssetCode(assetInfo.getAssetCode()) > 0){
//                  // 创建人默认system
//                  assetInfo.setUpdateUser(SecurityUtil.getLoginUser().getUid());
//      //          dto.setCreateUser(SecurityUtil.getLoginUser().getUid());
//                  assetInfo.setUpdateTime(DateUtil.now());
//                  updateDataList.add(assetInfo);
//              } else {
            // 创建人默认system
            assetInfo.setCreateUser(CommonUtil.getUserName());
            //          dto.setCreateUser(SecurityUtil.getLoginUser().getUid());
            assetInfo.setCreateTime(DateUtil.now());
            assetInfo.setUpdateTime(null);
            assetInfo.setUpdateUser(CommonUtil.getUserName());
            // 映射人行资产一级二级三级分类
            convertPbcAssetType(assetInfo,counterPartyMap);
            //2-5企业债券分行业、企业规模统计的列统计维度调整为根据市场机构管理中“机构类型（规模）”字段来取值。
            convertInstitSize(assetInfo,institMap);
            if(DictCodeListExt2.资产_负债类别_债券类资产_理财直接融资工具_同业存单.getValue().equals(assetInfo.getAssetType())
                    ||DictCodeListExt2.资产_负债类别_公募基金.getValue().equals(assetInfo.getAssetType())
                    ||DictCodeListExt2.资产_负债类别_私募基金.getValue().equals(assetInfo.getAssetType())){
                if(StringUtil.isNotEmpty(assetInfo.getAssetId())){
                    String assetId=assetInfo.getAssetId();
                    if(assetId.contains(".")){
                        assetId=assetId.substring(0,assetId.indexOf("."));
                        assetInfo.setAssetId(assetId);
                    }
                }
            }
            insertDataList.add(assetInfo);

//            // 插入到返回的数据中
//            if (isSynchronizeTask){
//                if(!assetList.contains(assetInfo.getAssetCode())){
//                    assetList.add(assetInfo.getAssetCode());
//                }
//            }
//              }
//          }

        }
//      List<BaseEntity> allData = new ArrayList<BaseEntity>();
//      if (CollectionUtils.isNotEmpty(updateDataList)) {
////            allData.addAll(updateDataList);
//          // 批量更新
//          int  singalCount = reportSelectOrDeleteCount;
//          int totalCount = updateDataList.size();
//          if(totalCount>0){
//              if(totalCount<=singalCount){
//                  chbAssetInfoInterfMapper.updateBatch(updateDataList);
//              }
//              else{
//                  for(int i = 0; i < totalCount/singalCount ; i++){
//                      chbAssetInfoInterfMapper.updateBatch(updateDataList.subList(0, singalCount));
//                      updateDataList.removeAll(updateDataList.subList(0, singalCount));
//                  }
//                  if(CollectionUtils.isNotEmpty(updateDataList)){
//                      chbAssetInfoInterfMapper.updateBatch(updateDataList);
//                  }
//              }
//          }
//      }

        //抽数债券的还本付息计划
        this.generateChbSecRepayInfoData();
//      if (CollectionUtils.isNotEmpty(insertDataList)) {
////            allData.addAll(insertDataList);
//          // 批量插入
//          int  singalCount = Integer.parseInt( null == SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)?"500" : SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));
//          int totalCount = insertDataList.size();
//          if(totalCount>0){
//              if(totalCount<=singalCount){
//                  // 先删再插
//                  chbAssetInfoInterfMapper.deleteByAssetCode(insertDataList);
//                  chbAssetInfoInterfMapper.insertBatch(insertDataList);
//              }
//              else{
//                  for(int i = 0; i < totalCount/singalCount ; i++){
//                      chbAssetInfoInterfMapper.deleteByAssetCode(insertDataList.subList(0, singalCount));
//                      chbAssetInfoInterfMapper.insertBatch(insertDataList.subList(0, singalCount));
//                      insertDataList.removeAll(insertDataList.subList(0, singalCount));
//                  }
//                  if(CollectionUtils.isNotEmpty(insertDataList)){
//                      chbAssetInfoInterfMapper.deleteByAssetCode(insertDataList);
//                      chbAssetInfoInterfMapper.insertBatch(insertDataList);
//                  }
//              }
//          }
//      }
        if(lastUpdatetime!=null){
            ChbInterfUtil.updateLastupdatetime(ChbInterfConfig.中债资产要素信息接口.getInterId(), lastUpdatetime);
        }
        mapPara.put(InterfConstants.SVR_ASSETLIST, assetList);
        logger.debug("上游资产要素信息入库:----结束------" +  df.format(new Date()));
        return insertDataList;
    }

    @Override
    public Class getEntityClass() {
        return ChbAssetInfo.class;
    }

    @Override
    public String getFileName() {
        return InterfDefine.资产要素信息接口数据.getCode();
    }
    
    /**
     * 从数据字典中获取参数值
     *
     * @param dictCode 数据字典
     * @param dictItemCode 数据代码
     * @return 参数值
     * @see
     * @since 1.0
     */
    public DataDictItem getNameFromDataDict(final DictCode dictCode, String dictItemCode) {
        if (StringUtil.isNotEmpty(dictCode) && StringUtil.isNotEmpty(dictItemCode)) {
            final String itemCode = StringUtil.trim(dictItemCode);
            // 从缓存中获取所有数据字典
            Map<String, DataDict> dataDictMap = CacheUtil.getAllDictCode();
            String code = dictCode.getValue();
            if (null != dataDictMap) {
                DataDict dto = dataDictMap.get(code);

                if (null != dto) {
                    List<DataDictItem> dictItemList = dto.getItems().stream().filter(item -> {
                        return StringUtil.equals(itemCode, item.getItemCode())
                               && StringUtil.equals(code, item.getDictCode());
                    }).collect(Collectors.toList());

                    DataDictItem dictItem = null;
                    if (!StringUtil.isEmptyList(dictItemList)) {
                        dictItem = dictItemList.get(0);
                    }

                    if (null != dictItem) {
                        return dictItem;
                    }
                }
                else {
                    logger.warn("数据字典{}未定义", dictCode);
                }
            }

        }
        return null;
    }
    
    /**
     * 功能:查询接入日当天的资产要素
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年2月18日
     * @param interfDate 跑批日
     * @prodList 产品代码
     * @return 
     * List<ChbAssetInfo>
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    // 切换数据源
    @DataSource(DataSourceType.INTERF)
    public List<ChbAssetInfo> queryAssetByInterfdateBatch(Date interfDate,List<String> prodList,String gradeDateEnd,String getProductRatingOrderSqlByZt){
        List<ChbAssetInfo> assetList = new ArrayList<ChbAssetInfo>();
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("interfDate", interfDate);
        param.put("prodList", prodList);
        param.put("gradeDateEnd",gradeDateEnd);
        param.put("getProductRatingOrderSqlByZt",getProductRatingOrderSqlByZt);
        if(CommonUtil.isBAmodel()){
        	param.put("orgCode","01");
        }
        String sqlMethod = "";
        String bankCode=SvrDataUtil.getSysParam(SVReportGlobalConstant.CUSTOMER_BANK_CODE);
        if(StringUtil.equals(bankCode, "001")){
            sqlMethod ="com.joyintech.tams.svreport.interf.chb.assetinfo.dao.ChbAssetInfoInterfMapper.selectAssetByInterfdateBatchByPsbc";
        }else{
            sqlMethod ="com.joyintech.tams.svreport.interf.chb.assetinfo.dao.ChbAssetInfoInterfMapper.selectAssetByInterfdateBatch";
        }
        template.select(sqlMethod,
                param,new ResultHandler() {
            @Override
            public void handleResult(ResultContext resultContext) {
                assetList.add((ChbAssetInfo) resultContext.getResultObject());
            }
        });
        return assetList;
    }
 
    /**
     * 功能:查询接入日当天的通道资产要素
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年2月18日
     * @param interfDate 跑批日
     * @prodList 产品代码
     * @return 
     * List<ChbAssetInfo>
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    // 切换数据源
    @DataSource(DataSourceType.INTERF)
    public List<ChbAssetInfo> queryChlAssetByInterfdateBatch(Date interfDate,List<String> prodList){
        List<ChbAssetInfo> assetList = new ArrayList<ChbAssetInfo>();
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("interfDate", interfDate);
        param.put("prodList", prodList);
        String bankCode=SvrDataUtil.getSysParam(SVReportGlobalConstant.CUSTOMER_BANK_CODE);
        String sqlMethod ="";
        if(StringUtil.equals(bankCode, "001")){
            sqlMethod ="com.joyintech.tams.svreport.interf.chb.assetinfo.dao.ChbAssetInfoInterfMapper.selectChlAssetByInterfdateBatchPsbc";
        }else{
            sqlMethod ="com.joyintech.tams.svreport.interf.chb.assetinfo.dao.ChbAssetInfoInterfMapper.selectChlAssetByInterfdateBatch";
        }
        template.select(sqlMethod,
                param,new ResultHandler() {
            @Override
            public void handleResult(ResultContext resultContext) {
                assetList.add((ChbAssetInfo) resultContext.getResultObject());
            }
        });
        return assetList;
    }
    /**
     * 功能:带提交的方式单条处理数据结果，入库
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年3月7日
     * @param assetList 
     * void
     */
    @SuppressWarnings("unused")
    private void dealResultCommitSingle(List<ChbAssetInfo> assetList){
        if (CollectionUtils.isNotEmpty(assetList)) {
            int batchCount = Integer.parseInt( null == SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)?"50" : SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));//每批commit的个数
            int batchLastIndex = batchCount;//每批最后一个的下标
            logger.debug("上游债券信息先删后插-删除:----开始------" +  df.format(new Date()));    
            for(int index = 0; index < assetList.size();){
                if(batchLastIndex > assetList.size()-1){
                    batchLastIndex = assetList.size();
                    chbAssetInfoInterfMapper.deleteByAssetCodeList(assetList.subList(index, batchLastIndex));
//                  System.out.println("index:"+index+"     batchLastIndex:"+batchLastIndex+"list"+prodinfos.subList(index, batchLastIndex).size());
                    break;//数据插入完毕,退出循环
                }else{
                    chbAssetInfoInterfMapper.deleteByAssetCodeList(assetList.subList(index, batchLastIndex));
//                  System.out.println("index:"+index+"     batchLastIndex:"+batchLastIndex +"list"+prodinfos.subList(index, batchLastIndex).size());
                    index = batchLastIndex;//设置下一批下标
                    batchLastIndex = index + batchCount;                     
                }
            }
            logger.debug("上游债券信息先删后插-删除:----结束------" +  df.format(new Date()));    
            logger.debug("上游债券信息先删后插-插入:----开始------" +  df.format(new Date()));    
            int count = 0;
            SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
            try{
                ChbAssetInfoInterfMapper batchMapper =  session.getMapper(ChbAssetInfoInterfMapper.class);
                for(ChbAssetInfo asset : assetList){
                    String bankCode=SvrDataUtil.getSysParam(SVReportGlobalConstant.CUSTOMER_BANK_CODE);
                    if(StringUtil.equals(bankCode, "001")){
                        batchMapper.insertPsbc(asset);
                    }else{
                        batchMapper.insert(asset);
                    }
                    
                    count++;
                    if(count%1000==0){
                        session.commit();
                        // 清理缓存，防止溢出
                        session.clearCache();
                        logger.debug("上游债券信息先删后插-插入1000条第"+ count/1000 +"次:----结束------" +  df.format(new Date())); 
                    }
                }
                session.commit();
                // 清理缓存，防止溢出
                session.clearCache();
            }
//          catch(Exception e){
//              session.rollback();
//              throw e;
//          }
            finally {
                session.close();
            }
            logger.debug("上游债券信息先删后插-插入:----结束------" +  df.format(new Date()));    
        }
    }
    
    /**
     * 功能:数据入库
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年5月5日 
     * @param assetInfoList 资产要素信息
     * @param saveFlag:F－全量，I－增量
     */
    public void insertDataMerge(List<ChbAssetInfo> assetInfoList,String saveFlag) {

        // 先根据属性 assetcode 将重复数据过滤 否则下面会报错
        assetInfoList = assetInfoList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ChbAssetInfo::getAssetCode))), ArrayList::new));

        SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        try {
            int count = 0;
            logger.debug("资产要素插入开始--------------" + new Date());
            ChbAssetInfoInterfMapper batchMapper = session.getMapper(ChbAssetInfoInterfMapper.class);
            int batchCount = Integer.parseInt( null == SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)?"50" : SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));//每批commit的个数
            int batchLastIndex = batchCount;//每批最后一个的下标
            String bankCode=SvrDataUtil.getSysParam(SVReportGlobalConstant.CUSTOMER_BANK_CODE); 
            for(int index = 0; index < assetInfoList.size();){
                if(batchLastIndex > assetInfoList.size()-1){
                    batchLastIndex = assetInfoList.size();
                    if(StringUtil.equals(bankCode, "001")){
                       batchMapper.mergeInfoListExPsbc(assetInfoList.subList(index, batchLastIndex));
                    }else{
                       batchMapper.mergeInfoListEx(assetInfoList.subList(index, batchLastIndex));
                    }
                    break;//数据插入完毕,退出循环
                }else{
                    if(StringUtil.equals(bankCode, "001")){
                        batchMapper.mergeInfoListExPsbc(assetInfoList.subList(index, batchLastIndex));
                    }else{
                        batchMapper.mergeInfoListEx(assetInfoList.subList(index, batchLastIndex));
                    }
                    index = batchLastIndex;//设置下一批下标
                    batchLastIndex = index + batchCount;                     
                }
                count ++;
                // 每20次提交
                if (count % 20 == 0) {
                    session.commit();
                    // 清理缓存，防止溢出
                    session.clearCache();
                }
            }
            logger.debug("资产要素插入结束--------------" + new Date());
            session.commit();
            // 清理缓存，防止溢出
            session.clearCache();
        }
        finally {
            session.close();
        }
    }

    /**
     * 表2-5企业债券分行业、企业规模统计的列统计维度调整为根据市场机构管理中“机构类型（规模）”字段来取值。
     * @param chbAssetInfo  资产基本信息
     * @param institMap     市场机构数据
     */
    public static void convertInstitSize(ChbAssetInfo chbAssetInfo,Map<String,String> institMap){
            String institSize = chbAssetInfo.getInstitSize();
            if(null!=chbAssetInfo){
                //获取市场机构.优先级为:发行人→管理人→融资人 由于人行与市场机构数据字典不同.直接进行修改
                if(StringUtil.isNotEmpty(institMap.get(chbAssetInfo.getChbIssuerId()))){
                    institSize = changeInstitSize(
                            institMap.get(chbAssetInfo.getChbIssuerId()));
                }else if(StringUtil.isNotEmpty(institMap.get(chbAssetInfo.getChbManagerId()))){
                    institSize = changeInstitSize(
                            institMap.get(chbAssetInfo.getChbManagerId()));
                }else if(StringUtil.isNotEmpty(institMap.get(chbAssetInfo.getChbFinancierId()))){
                    institSize = changeInstitSize(
                            institMap.get(chbAssetInfo.getChbFinancierId()));
                }
            }
            chbAssetInfo.setInstitSize(institSize);
    }

    /**
     * 由于人行与市场机构数据字典不同.直接进行修改
     */
    public static String changeInstitSize(String institSize){
        switch (institSize){
            case "01":
                institSize = "01";
                break;
            case "02":
                institSize = "03";
                break;
            case "03":
                institSize = "04";
                break;
            case "04":
                institSize = "02";
                break;
            case "05":
                institSize = "99";
                break;
        }
        return institSize;
    }
    
    /**
     * 功能:通过资产一级二级三级分类映射人行一级二级三级分类
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年6月24日
     * @param chbAssetInfo 资产要素信息
     * @param counterPartyMap 交易对手信息
     * void
     */
    public static void convertPbcAssetType(ChbAssetInfo chbAssetInfo,Map<String,ChbMstCounterParty> counterPartyMap){
        if (StringUtil.isEmpty(chbAssetInfo.getPbcAssetType1())
                && StringUtil.isEmpty(chbAssetInfo.getPbcAssetType2())
                && StringUtil.isEmpty(chbAssetInfo.getPbcAssetType3())){
            /*邮储*/
            String bankCode=SvrDataUtil.getSysParam(SVReportGlobalConstant.CUSTOMER_BANK_CODE);
             //是否江苏银行个性化
            //add by hxy 删除江苏银行个性化
            //String jsbankCode=SvrDataUtil.getSysParam(SVReportGlobalConstant.CUSTOMER_JSBANK_CODE);
            if(StringUtil.equals(bankCode, "001")){
                chbAssetInfo.setPbcAssetType1(chbAssetInfo.getOrgAssetType3());
                chbAssetInfo.setPbcAssetType2(chbAssetInfo.getOrgAssetType4());
                chbAssetInfo.setPbcAssetType3(chbAssetInfo.getOrgAssetType5());
            } else {
                // 通过原资产类型映射人行资产类型
                chbAssetInfo.setPbcAssetType1(chbAssetInfo.getOrgAssetType1());
                chbAssetInfo.setPbcAssetType2(chbAssetInfo.getOrgAssetType2());
                chbAssetInfo.setPbcAssetType3(chbAssetInfo.getOrgAssetType3());
            }
            
            ChbTypeMappingService chbTypeMappingExService = SpringContextUtil.getBean(
                    ChbTypeMappingService.class);
            // 优先级先查资产代码取值映射
            ChbTypeMappingEx mappingEx = SpringContextUtil.getBean(
                    ChbTypeMappingService.class).getMapping(InterfMappingConstants.MAPPINGCODE_PBCASSETTYPE);
                    
            if (mappingEx == null){
                // 根据配置映射项判别是否带相应属性:三级资产类型
                if (!StringUtils.isEmpty(chbAssetInfo.getPbcAssetType3()) && chbTypeMappingExService.getMapping(InterfMappingConstants.MAPPINGCODE_PBCASSETTYPE, 
                        chbAssetInfo.getPbcAssetType1(),chbAssetInfo.getPbcAssetType2(),chbAssetInfo.getPbcAssetType3()) == null){
                    chbAssetInfo.setPbcAssetType3(null); 
                }
                // 根据配置映射项判别是否带相应属性:二级资产类型
                if (!StringUtils.isEmpty(chbAssetInfo.getPbcAssetType2()) && chbTypeMappingExService.getMapping(InterfMappingConstants.MAPPINGCODE_PBCASSETTYPE, 
                        chbAssetInfo.getPbcAssetType1(),chbAssetInfo.getPbcAssetType2(),chbAssetInfo.getPbcAssetType3()) == null){
                    chbAssetInfo.setPbcAssetType2(null);
                }
                // 融资人/发行人属性 先取发行人属性再取融资人属性
                // 行业一级类型10为金融 其他为非金融 
                // 资产要素信息
                String industry = null;
                if (chbAssetInfo != null){
                    industry = chbAssetInfo.getIndustry();
                    if (StringUtils.isEmpty(industry)){
                        industry = chbAssetInfo.getIndustryPbc();
                    }
                    if (StringUtils.isEmpty(industry)) {
                        industry = chbAssetInfo.getIndustryShares();
                    }
                }
                if (!StringUtils.isEmpty(industry)){
                    if("10".equals(industry)){
                        industry = "01";  
                    } else {
                        industry = "02"; 
                    }
                }
                // 邮储融资人属性特殊处理 是否持有金融牌照枚举值可能采用Y和N判断
                if (StringUtil.equals(bankCode, "001") && chbAssetInfo != null) {
                    industry = chbAssetInfo.getIsFinLicence();
                    if(industry!=null) {
                        if(industry.equals("Y")) {
                            industry = "01";
                        }else if(industry.equals("N")) {
                            industry = "02"; 
                        }
                    }
                    
                }
                // 根据配置映射项判别是否带相应属性
                if (!StringUtils.isEmpty(industry) && chbTypeMappingExService.getMappingBySrcField(InterfMappingConstants.PBCASSETTYPE_FAIN, 
                        chbAssetInfo.getPbcAssetType1(),chbAssetInfo.getPbcAssetType2(),chbAssetInfo.getPbcAssetType3()) == null){
                    industry = null;
                }
                // 收受益权
                String repprofittype = chbAssetInfo.getProfitType();
                // 根据配置映射项判别是否带相应属性
                if (!StringUtils.isEmpty(repprofittype) && chbTypeMappingExService.getMappingBySrcField(InterfMappingConstants.PBCASSETTYPE_REPP, 
                        chbAssetInfo.getPbcAssetType1(),chbAssetInfo.getPbcAssetType2(),chbAssetInfo.getPbcAssetType3()) == null){
                    repprofittype = null;
                }
                ChbMstCounterParty  counterinfo=counterPartyMap.get(chbAssetInfo.getRivalParty());
                String marketStr=chbAssetInfo.getTradeMarket();
                // 是否为中央交易对手  改为判断市场结构是否为交易所 03 上海证券交易所/04 深圳证券交易所
                String isCentralCounter=null;
                
                // 交易对手二级类型
                String counterType2 = counterinfo==null?null:counterinfo.getCounterType2();
                // 20200416 吉林银行修改-拆借回购根据交易对手二级类型细分类处理 wangwei start
//                if(StringUtil.equals(jsbankCode, "001")&&InterfMappingConstants.BS_ASSETTYPE3_C0402.equals(counterType2)){//非银行业金融机构 区分是否中央交易对手
//                	isCentralCounter = "02";
//                }
//                // 根据配置映射项判别是否带相应属性
//                if (!StringUtils.isEmpty(isCentralCounter) && chbTypeMappingExService.getMappingBySrcField(InterfMappingConstants.PBCASSETTYPE_CENTER,
//                        chbAssetInfo.getPbcAssetType1(),chbAssetInfo.getPbcAssetType2(),chbAssetInfo.getPbcAssetType3()) == null){
//                    isCentralCounter = null;
//                }
                // 非银行业金融机构 区分是否中央交易对手   修改获取非银行业金融机构类型方法
                //if(InterfMappingConstants.BS_ASSETTYPE3_C0402.equals(counterType2)) {
                if(DictCodeListExt3.对手方类型_非银行业金融机构.getValue().equals(chbAssetInfo.getRivalPartyType())){
                    // 根据配置映射项判别是否带相应属性
                    if (chbTypeMappingExService.getMappingBySrcField(InterfMappingConstants.PBCASSETTYPE_DSFLX,
                            chbAssetInfo.getPbcAssetType1(),chbAssetInfo.getPbcAssetType2(),chbAssetInfo.getPbcAssetType3()) != null) {
                        // 市场结构是否为交易所 03 上海证券交易所/04 深圳证券交易所,是-中央交易对手，否-非中央交易对手
                        if ("03".equals(marketStr) || "04".equals(marketStr) ) {
                            isCentralCounter = "01";
                        } else {
                            isCentralCounter = "02";
                        }
                    }
                }
                // 20200416 吉林银行修改-拆借回购根据交易对手二级类型细分类处理 wangwei end
                // 根据配置映射项判别是否带相应属性
                if (!StringUtils.isEmpty(counterType2) && chbTypeMappingExService.getMappingBySrcField(InterfMappingConstants.PBCASSETTYPE_COUNTER, 
                        chbAssetInfo.getPbcAssetType1(),chbAssetInfo.getPbcAssetType2(),chbAssetInfo.getPbcAssetType3()) == null){
                    counterType2 = null; 
                }
                // 交易对手二级类型
//                String counterType2 = counterinfo==null?null:counterinfo.getCounterType2();
                // 根据配置映射项判别是否带相应属性
                if (!StringUtils.isEmpty(counterType2) && chbTypeMappingExService.getMappingBySrcField(InterfMappingConstants.PBCASSETTYPE_COUNTER, 
                        chbAssetInfo.getPbcAssetType1(),chbAssetInfo.getPbcAssetType2(),chbAssetInfo.getPbcAssetType3()) == null){
                    counterType2 = null; 
                }
                // 境内\境外
                String blnarea = chbAssetInfo.getBlnArea();
    //                if (InterfMappingConstants.BS_ASSETTYPE2_D02.equals(chbAssetInfo.getPbcAssetType2())){
    //                    blnarea = chbAssetInfo.getBlnarea();
    //                } 
                // 根据配置映射项判别是否带相应属性
                if (!StringUtils.isEmpty(blnarea) && chbTypeMappingExService.getMappingBySrcField(InterfMappingConstants.PBCASSETTYPE_COUNTRY, 
                        chbAssetInfo.getPbcAssetType1(),chbAssetInfo.getPbcAssetType2(),chbAssetInfo.getPbcAssetType3()) == null){
                    blnarea = null;
                }
                String rivalPartyType = chbAssetInfo.getRivalPartyType();
                // 根据配置映射项判别是否带相应属性
                if (!StringUtils.isEmpty(rivalPartyType) && chbTypeMappingExService.getMappingBySrcField(InterfMappingConstants.PBCASSETTYPE_DSFLX,
                        chbAssetInfo.getPbcAssetType1(),chbAssetInfo.getPbcAssetType2(),chbAssetInfo.getPbcAssetType3()) == null){
                    rivalPartyType = null;
                }
                //TISHKBS-2835  监管报送-人行资管产品月度统计报告  添加对手方类型映射规则
                mappingEx = chbTypeMappingExService.getMappingBySrcField(InterfMappingConstants.MAPPINGCODE_PBCASSETTYPE, 
                        chbAssetInfo.getPbcAssetType1(),chbAssetInfo.getPbcAssetType2(),chbAssetInfo.getPbcAssetType3(),industry,repprofittype
                        ,isCentralCounter,counterType2,blnarea,rivalPartyType);
            }
            if(mappingEx!=null){
                chbAssetInfo.setPbcAssetType1(mappingEx.getObjfield1());
                chbAssetInfo.setPbcAssetType2(mappingEx.getObjfield2());
                chbAssetInfo.setPbcAssetType3(mappingEx.getObjfield3());
            }
            //ZGBSEI-16  【徽银理财】监管报送-贷款明细信息维护新建页面，资产代码下拉框可选择四级分类为北金所债权资产的项目类资产
            //当资产是北金所时.直接设置为贷款
            if(DictCodeList.是否_中债_是.getValue().equals(chbAssetInfo.getIsBjszq())){
                chbAssetInfo.setPbcAssetType1(DictCodeListExt3.人行资产一级分类_贷款.getValue());
                chbAssetInfo.setPbcAssetType2(DictCodeListExt3.人行资产二级分类_除回购或拆借外贷款.getValue());
                chbAssetInfo.setPbcAssetType3(DictCodeListExt3.人行资产三级分类_除回购或拆借外贷款.getValue());
            }

//            else {//映射不到 存成空
//                chbAssetInfo.setPbcAssetType1(null);
//                chbAssetInfo.setPbcAssetType2(null);
//                chbAssetInfo.setPbcAssetType3(null);
//            }
            // 邮储特殊处理 债券借贷的融入是负债
            if(StringUtil.equals(bankCode, "001") 
                    && StringUtil.equals(chbAssetInfo.getOrgAssetType3(), "50")
                    && StringUtil.equals(chbAssetInfo.getOrgAssetType4(), "102")
                    && StringUtil.equals(chbAssetInfo.getOrgAssetType5(), "104")
                    && StringUtil.equals(chbAssetInfo.getOrgAssetTypeEx(), "F29")){
                if (StringUtil.equals(chbAssetInfo.getPbcAssetType2(), "J01")) {
                    chbAssetInfo.setPbcAssetType3("J03");
                } else if (StringUtil.equals(chbAssetInfo.getPbcAssetType2(), "J02")) {
                    chbAssetInfo.setPbcAssetType3("J04");
                }
            }
        }
    }

    /**
     * 功能:抽数债券的还本付息计划
     * @since 2.1.8
     * @version 2.1.8
     * @author：yjp 2021年7月26日
     *
     */
    public void generateChbSecRepayInfoData() {
        //删除债券还本付息a表里的数据
        chbAssetInfoInterfMapper.deleteAllChbSecRepayInfoData();
        //插入查询债券的还本付息计划a
        ChbSecRepayInfo record = new ChbSecRepayInfo();
        chbAssetInfoInterfMapper.insertChbSecRepayInfoData(record);

    }
}
