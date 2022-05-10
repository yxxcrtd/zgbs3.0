/*
 * 文件名：PtlManagementInfo001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.service.portfolio.ptlmanagementinfo.service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.tams.invest.common.asset.util.AssetCommonUtil;
import com.joyintech.tams.invest.common.prodinfo.BussRuleService;
import com.joyintech.tams.invest.common.prodinfo.dto.IRskRulePoolConfig;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.tams.common.base.entity.FinBaseRuleInfo;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.common.base.entity.FinOpenPlan;
import com.joyintech.tams.common.base.entity.FinRateInfo;
import com.joyintech.tams.common.base.entity.MstIntRateInfo;
import com.joyintech.tams.invest.common.dto.mock.portfolio.MockPtlPortfolioDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.portfolio.ptlmanagementinfo.dao.PtlManagementInfo001Mapper;
import com.joyintech.service.portfolio.ptlmanagementinfo.dto.FundAccountInfoDto;
import com.joyintech.service.portfolio.ptlmanagementinfo.dto.NodeDto;
import com.joyintech.service.portfolio.ptlmanagementinfo.dto.PtlManagementInfo003DataDto;
import com.joyintech.service.portfolio.ptlmanagementinfo.dto.PtlManagementInfoDto;
import com.joyintech.service.portfolio.ptlmanagementinfo.dto.SecuritiesAccountInfoDto;
import com.joyintech.tams.common.base.dao.FinCashInfoMapper;
import com.joyintech.tams.common.base.dao.MstCounterAcctMapper;
import com.joyintech.tams.common.base.dao.MstCounterPartyMapper;
import com.joyintech.tams.common.base.dao.PaPtlPortfolioMapper;
import com.joyintech.tams.common.base.dao.PtlCapacctInfoMapper;
import com.joyintech.tams.common.base.dao.PtlPortfolioAuthorityMapper;
import com.joyintech.tams.common.base.dao.PtlPortfolioDisMapper;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.dao.PtlSecAcctMapper;
import com.joyintech.tams.common.base.dao.PtlValParamMapper;
import com.joyintech.tams.common.base.dao.RskPortfolioIndicatorMapper;
import com.joyintech.tams.common.base.dao.RskPortfolioPackageDtlMapper;
import com.joyintech.tams.common.base.entity.FinCashInfo;
import com.joyintech.tams.common.base.entity.MstCounterAcct;
import com.joyintech.tams.common.base.entity.MstCounterAcctKey;
import com.joyintech.tams.common.base.entity.MstCounterParty;
import com.joyintech.tams.common.base.entity.PaPtlPortfolio;
import com.joyintech.tams.common.base.entity.PtlCapacctInfo;
import com.joyintech.tams.common.base.entity.PtlParamPoolConfig;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.common.base.entity.PtlPortfolioAuthority;
import com.joyintech.tams.common.base.entity.PtlPortfolioDis;
import com.joyintech.tams.common.base.entity.PtlSecAcct;
import com.joyintech.tams.common.base.entity.PtlValParam;
import com.joyintech.tams.common.base.entity.RskPortfolioIndicator;
import com.joyintech.tams.common.base.entity.RskPortfolioPackageDtl;
import com.joyintech.tams.common.base.entity.RskRulePoolConfig;
import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import com.joyintech.tams.common.base.entity.User;
import com.joyintech.tams.component.base.core.MstUserAndRoleService;
import com.joyintech.tams.component.base.core.dto.MstUserAndRoleOutputParameter;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.prodinfo.dao.RskRuleCommonMapper;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import com.joyintech.tams.invest.common.service.MapperDelegateService;
import com.joyintech.tams.invest.common.valuationinterf.ValuationInterfService;
import com.joyintech.tams.invest.common.valuationinterf.dto.ProdOtherInfoInitInputDto;
//import com.joyintech.tams.valuation.common.interf.common.ResultCode;
//import com.joyintech.tams.valuation.common.interf.common.ResultObject;

/**
 * 组合管理Service
 *
 * @author fanshuanghu
 * @version 1.0 2018年9月7日
 * @see PtlManagementInfo001Service
 * @since 1.0
 */
@Service("backPtlManagementInfo001Service")
public class PtlManagementInfo001Service extends BaseService<PtlManagementInfoDto> {

    @Autowired
    private BussRuleService bussRuleService;

    // 注入组合微服务的Mapper
    /**
     * 組合管理Mapper
     */
    @Autowired
    private PtlManagementInfo001Mapper ptlManagementInfo001Mapper;

    /**
     * 组合Mapper
     */
    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;

    /**
     * 组合扩展表Mapper
     */
    @Autowired
    private PaPtlPortfolioMapper paPtlPortfolioMapper;

    /**
     * 证券账户表Mapper
     */
    @Autowired
    private PtlSecAcctMapper ptlSecAcctMapper;

    /**
     * 组合清算账户表Mapper
     */
    @Autowired
    private PtlCapacctInfoMapper ptlCapacctInfoMapper;

    /**
     * 组合包明细Mapper
     */
    @Autowired
    private RskPortfolioPackageDtlMapper rskPortfolioPackageDtlMapper;

    //---------redis版风险对应 裴家俊  start-------------
    /**
     * 组合关联指标Mapper
     */
    @Autowired
    private RskPortfolioIndicatorMapper rskPortfolioIndicatorMapper;

    @Autowired
    private RskRuleCommonMapper rskRuleCommonMapper;

    //---------redis版风险对应 裴家俊  end-------------

    // 一体化对应etliu start
    /**
     * ValuationInterfService
     */
    @Autowired
    private ValuationInterfService valuationInterfService;
    // 一体化对应etliu end

    @Autowired
    private PtlPortfolioDisMapper ptlPortfolioDisMapper;

    @Autowired
    private PtlPortfolioAuthorityMapper ptlPortfolioAuthorityMapper;

    /**
     * 注入用户service
     */
    @Autowired(required = false)
    private MstUserAndRoleService mstUserAndRoleService;
    /**
     * 注入输出实体类
     */
    private MstUserAndRoleOutputParameter mstUserAndRoleOutputParameter = null;

    @Autowired
    private MstCounterPartyMapper mstCounterPartyMapper;

    @Autowired
    private MstCounterAcctMapper mstCounterAcctMapper;

    @Autowired
    private MapperDelegateService bussCommonMapperDelegateService;

    @Autowired
    private PtlValParamMapper ptlValParamMapper;

    @Autowired
    private FinCashInfoMapper finCashInfoMapper;

    /**
     * 插入操作
     *
     * @param entity    组合管理实体类
     * @param isNewData 是否是新建数据
     * @return 是否成功
     * @see
     * @since 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    // 一体化对应etliu start
//    public int insertData (PtlManagementInfoDto entity){
    public void insertData(PtlManagementInfoDto entity, boolean isNewData) {
//    	ResultObject resultObject = new ResultObject(ResultCode.SUCCESS);
        // 一体化对应etliu end
        PaPtlPortfolio paPtlPortfolio = new PaPtlPortfolio();
        BeanUtil.copyPropertiesIgnoreNull(paPtlPortfolio, entity);
        paPtlPortfolioMapper.deleteByPrimaryKey(paPtlPortfolio.getPortfolioId());
        paPtlPortfolioMapper.insertSelective(paPtlPortfolio);
        PtlPortfolio ptlPortfolio = new PtlPortfolio();
        BeanUtil.copyPropertiesIgnoreNull(ptlPortfolio, entity);
        ptlPortfolioMapper.deleteByPrimaryKey(ptlPortfolio.getPortfolioId());
        ptlPortfolioMapper.insertSelective(ptlPortfolio);

        //------update by 裴家俊 2020-01-07 新版风险核对应-----start------
       /* Map<String, Object> map = new HashMap<String, Object>();
        map.put("classify_dimension_01", DictCodeListExt1.分类维度_收益类型.getValue());
        map.put("classify_dimension_02", DictCodeListExt1.分类维度_开放类型.getValue());
        map.put("classify_dimension_03", DictCodeListExt1.分类维度_募集方式.getValue());
        map.put("classify_dimension_04", DictCodeListExt1.分类维度_投资性质.getValue());

        map.put("dimension_01", entity.getProfitType());// 收益类型
        map.put("dimension_02", entity.getOpenType());// 开放类型
        map.put("dimension_03", entity.getRaiseType());// 募集方式
        map.put("dimension_04", entity.getInvestNature());// 投资性质

        List<String> ptlBagIdList = ptlManagementInfo001Mapper.selectPtlBagId(map);
        ptlManagementInfo001Mapper.deleteRskPortfolioPackageDtlByportId(entity.getPortfolioId());
        if (ptlBagIdList != null && ptlBagIdList.size() > 0) {
            for (String ptlBagId : ptlBagIdList) {
                RskPortfolioPackageDtl rskPortfolioPackageDtl = new RskPortfolioPackageDtl();
                rskPortfolioPackageDtl.setPtlBagId(ptlBagId);
                rskPortfolioPackageDtl.setPortfolioId(entity.getPortfolioId());
                rskPortfolioPackageDtlMapper.insertSelective(rskPortfolioPackageDtl);
            }
        }*/
        //处理自动关联组合包
        relateAutoBagIndicator(entity);
        //处理自动关联规则
        relateAutoRuleIndicator(entity);
        //------update by 裴家俊 2020-01-07 新版风险核对应-----end--------

        //新增子组合的继承比例 add by gejiaao 2019/08/29
        if (StringUtil.equals(entity.getPortfolioType(), DictCodeListExt1.组合类型_子组合.getValue())) {
            PtlPortfolioDis portfolioDis = new PtlPortfolioDis();
            portfolioDis.setPortfolioId(entity.getPortfolioId());
            portfolioDis.setDisPercent(entity.getDisPercent().divide(BigDecimal.valueOf(100)));
            ptlPortfolioDisMapper.deleteByPrimaryKey(portfolioDis.getPortfolioId());
            ptlPortfolioDisMapper.insertSelective(portfolioDis);
        }

        // 一体化对应etliu start
        // 组合新建，调用估值接口  (子组合新建不需要调用)
        /** modify by yanya on 20191114 子组合也调用估值接口，生成账套
        if (!StringUtil.equals(entity.getPortfolioType(), DictCodeListExt1.组合类型_子组合.getValue())) {**/
//            ProdOtherInfoInitInputDto inputDto = new ProdOtherInfoInitInputDto();
//            inputDto.setPageId("PtlManagementInfo002");
//            inputDto.setPtlPortfolio(ptlPortfolio);
//            inputDto.setPtlValParam(entity.getPtlValParam());
//            inputDto.setNewData(isNewData);
//            if (!StringUtil.isEmptyList(entity.getFundAccountInfoDto())) {
//                List<PtlCapacctInfo> prodAcctDataList = new ArrayList<>();
//                for (FundAccountInfoDto fundAccountInfoDto : entity.getFundAccountInfoDto()) {
//                    PtlCapacctInfo ptlCapacctInfo = new PtlCapacctInfo();
//                    BeanUtil.copyPropertiesIgnoreNull(ptlCapacctInfo, fundAccountInfoDto);
//                    prodAcctDataList.add(ptlCapacctInfo);
//                }
//                inputDto.setProdAcctDataList(prodAcctDataList);
//            }

//            resultObject = valuationInterfService.prodOtherInfoInit(inputDto);
        /** modify by yanya on 20191114 子组合也调用估值接口，生成账套
        }**/
        // 一体化对应etliu end
//        
//        ptlManagementInfo001Mapper.insertPaPtlPortfolio(entity);
//        ptlManagementInfo001Mapper.insertPtlPortfolio(entity);
//        return resultObject;
    }
    /**
     * 匹配组合包以便关联指标
     * @param entity
     */
    @Transactional(rollbackFor = Exception.class)
    public void relateAutoBagIndicator(PtlManagementInfoDto entity){
        List<String> ptlBagIdList =new ArrayList<>();
        List<PtlParamPoolConfig> ptlPoolList=ptlManagementInfo001Mapper.selectPtlPoolAll();
        Map<String,List<PtlParamPoolConfig>> ptlPoolMap=ptlPoolList.stream().collect(Collectors.groupingBy(PtlParamPoolConfig::getPtlBagId));
        ptlPoolMap.keySet().forEach(key->{
            //按paramNum倒序排
            List<PtlParamPoolConfig> newPtlPoolList=ptlPoolMap.get(key).stream().sorted(Comparator.comparing(PtlParamPoolConfig::getParamNum).reversed()).collect(Collectors.toList());
            //系统内处理为（（A∪B）∩C）∪D，由外向内逐层处理
            String sqlWhere=StringUtil.EMPTY;
            for(PtlParamPoolConfig dto:newPtlPoolList){
                String column= RiskUtil.getTableColumn(dto.getClassifyDimension());
                sqlWhere= RiskUtil.getTableColumnSql(dto.getLinkSign(),column,dto.getClassifyDimensionVal(),sqlWhere);
            }
            //查询符合规则的组合
            if(!StringUtil.isEmptyList(ptlManagementInfo001Mapper.getMatchPortData(sqlWhere,entity.getPortfolioId()))){
                ptlBagIdList.add(key);//说明当前这个组合包符合这个组合的属性
            }
        });
        if (!StringUtil.isEmptyList(ptlBagIdList)) {
            ptlManagementInfo001Mapper.deleteRskPortfolioPackageDtlByportId(entity.getPortfolioId());
            for (String ptlBagId : ptlBagIdList) {
                RskPortfolioPackageDtl rskPortfolioPackageDtl = new RskPortfolioPackageDtl();
                rskPortfolioPackageDtl.setPtlBagId(ptlBagId);
                rskPortfolioPackageDtl.setPortfolioId(entity.getPortfolioId());
                rskPortfolioPackageDtlMapper.insertSelective(rskPortfolioPackageDtl);
            }
        }
    }
    /**
     * 处理自动关联规则
     * @param entity
     */
    @Transactional(rollbackFor = Exception.class)
    public void relateAutoRuleIndicator(PtlManagementInfoDto entity){
        List<String> ptlRuleIdList =new ArrayList<>();
        List<IRskRulePoolConfig> ptlPoolList=ptlManagementInfo001Mapper.selectPtlRuleAll();
        Map<String,List<IRskRulePoolConfig>> ptlPoolMap=ptlPoolList.stream().collect(Collectors.groupingBy(IRskRulePoolConfig::getRuleId));
        ptlPoolMap.keySet().forEach(key->{
            List<String> portList =new ArrayList<>();
            if(CommonUtil.isType(entity.getOpenType(),DictCodeList.开放类型_开放式)){
                portList.add(entity.getPortfolioId());
            }
            //按paramNum倒序排
            List<IRskRulePoolConfig> newPtlPoolList=ptlPoolMap.get(key).stream().sorted(Comparator.comparing(IRskRulePoolConfig::getParamNum).reversed()).collect(Collectors.toList());
            //系统内处理为（（A∪B）∩C）∪D，由外向内逐层处理
            String sqlWhere=StringUtil.EMPTY;
            for(IRskRulePoolConfig dto:newPtlPoolList){
                //---流动性风险改造过滤开放周期 start---
                sqlWhere=dto.getPoolSql(portList,sqlWhere);
                //---流动性风险改造过滤开放周期 end---
            }
            //查询符合规则的组合
            if(!StringUtil.isEmptyList(ptlManagementInfo001Mapper.getMatchPortData(sqlWhere,entity.getPortfolioId()))){
                ptlRuleIdList.add(key);//说明当前这个规则符合这个组合的属性
            }
        });
        if (!StringUtil.isEmptyList(ptlRuleIdList)) {
            //---流动性风险改造入规则关联组合表 start---
            bussRuleService.insertRulePortList(Arrays.asList(entity.getPortfolioId()),ptlRuleIdList,()->Arrays.asList(entity.getPortfolioId()));
            //---流动性风险改造入规则关联组合表 end---
            List<String> ruleIndList=ptlManagementInfo001Mapper.selectRuleRelInd(ptlRuleIdList);
            for (String indId : ruleIndList) {
                RskPortfolioIndicator rskPortfolioIndicator = new RskPortfolioIndicator();
                rskPortfolioIndicator.setIndId(indId);
                rskPortfolioIndicator.setStartDate(DateUtil.platDateFromCache());
                rskPortfolioIndicator.setPortfolioLevel(BussinessDictCodeListRds.组合层次_单组合.getValue());
                rskPortfolioIndicator.setPortfolioId(entity.getPortfolioId());
                rskPortfolioIndicatorMapper.deleteByPrimaryKey(rskPortfolioIndicator);
                rskPortfolioIndicatorMapper.insertSelective(rskPortfolioIndicator);
            }
        }
    }

    /**
     * 删除数据
     *
     * @param entity 组合实体类
     * @see
     * @since 1.0
     */
    public void deleteData(PtlManagementInfoDto entity) {
        ptlManagementInfo001Mapper.deleteRskPortfolioPackageDtlByportId(entity.getPortfolioId());
        paPtlPortfolioMapper.deleteByPrimaryKey(entity.getPortfolioId());
        ptlPortfolioMapper.deleteByPrimaryKey(entity.getPortfolioId());
//        ptlManagementInfo001Mapper.deletePtlCapacctInfoByPrimaryKey(entity.getPortfolioId());

        //如果是子组合删除组合资金分配比例数据 add by gejiaao 2019/08/27 start
        if (StringUtil.equals(entity.getPortfolioType(), DictCodeListExt1.组合类型_子组合.getValue())
                || !StringUtil.isEmpty(entity.getChildPortfolioId())) {
            ptlPortfolioDisMapper.deleteByPrimaryKey(entity.getPortfolioId());
            //如果删除了母组合最后一个子组合，则修改其母组合为可投资
            List<PtlManagementInfoDto> childPortfolio = getChildPortfolio(entity.getParentPortfolioId());
            if (childPortfolio.isEmpty()) {
                PtlPortfolio ptlPortfolio = new PtlPortfolio();
                ptlPortfolio.setPortfolioId(entity.getParentPortfolioId());
                ptlPortfolio.setInvestYesorno(DictCodeList.是否_是.getValue());
                ptlPortfolioMapper.updateByPrimaryKeySelective(ptlPortfolio);
                //获取母组合的信息
                PtlPortfolio ptlPortfolioInfo = ptlPortfolioMapper.selectByPrimaryKey(entity.getParentPortfolioId());
                entity.setPortfolioId(ptlPortfolioInfo.getPortfolioId());
                entity.setPortfolioName(ptlPortfolioInfo.getPortfolioName());
                Map<String, Object> map = new HashMap<>();
                map.put("portfolioId",entity.getPortfolioId());
                List<FundAccountInfoDto> funDto = ptlManagementInfo001Mapper.getFunInfo(map);
                entity.setFundAccountInfoDto(funDto);
                //当删除了母组合最后一个子组合 则母组合生成内部交易对手
                createCounter(entity);
            }
        }
        //如果是子组合删除组合资金分配比例数据 add by gejiaao 2019/08/27 end

        // ptlManagementInfo001Mapper.deletePaPtlPortfolioByPrimaryKey(entity);
        // ptlManagementInfo001Mapper.deletePtlPortfolioByPrimaryKey(entity);
    }

    /**
     * 删除资金账户信息
     *
     * @param portfolioId 组合代码
     * @return 是否
     * @see
     * @since 1.0
     */
    public int secInfoDelete(String portfolioId) {
        return ptlManagementInfo001Mapper.deletePtlSecAcctByPrimaryKey(portfolioId);
    }

    /**
     * 删除操作调用
     *
     * @param dto 组合实体类
     * @return 是否成功
     * @see
     * @since 1.0
     */
    public int delete(PtlManagementInfoDto dto) {
        deleteData(dto);
        return 1;
    }

    /**
     * 插入操作
     *
     * @param dto 组合实体类
     * @return 是否成功
     * @see
     * @since 1.0
     */
    public void insert(PtlManagementInfoDto dto) {
        // 一体化对应etliu start
//        return insertData(dto);
        insertData(dto, true);
//        return insertData(dto, true);
        // 一体化对应etliu end
    }

    /**
     * 修改操作
     *
     * @param dto 组合实体类
     * @return 是否修改成功
     * @see
     * @since 1.0
     */
    public void update(PtlManagementInfoDto dto) {
        deleteData(dto);

        // 一体化对应etliu start
//        return insertData(dto);
        insertData(dto, false);
//        return insertData(dto, false);
        // 一体化对应etliu end
    }

    /**
     * 分页查询
     *
     * @param length 长度
     * @param page   页数
     * @param map    分页查询参数
     * @return 组合管理集合
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<PtlManagementInfoDto> selectForPage(int page, int length, Map<String, Object> map) {
        String vDate = (String) map.get("vdate");
        if (StringUtil.isNotEmpty(vDate)) {
            String[] split = vDate.split(" - ");
            map.put("startVDate", split[0]);
            map.put("endVDate", split[1]);
        }
        String mDate = (String) map.get("mdate");
        if (StringUtil.isNotEmpty(mDate)) {
            String[] split = mDate.split(" - ");
            map.put("startMDate", split[0]);
            map.put("endMDate", split[1]);
        }
        String portfolioId = (String) map.get("portfolioId");
        if (StringUtil.isNotEmpty(portfolioId)) {
            map.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }
        map.put("userId", LoginUserUtil.getUserName());
        return super.queryForPage(page, length,
                () -> ptlManagementInfo001Mapper.selectForPage(map));
    }

    /**
     * 获取组合列表
     *
     * @param map 请求参数
     * @return 分页查询结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public List<PtlManagementInfoDto> getPortPage(Map<String, Object> map)
    {
        return ptlManagementInfo001Mapper.getPortPage(map);
    }

    /**
     * 获取资金账户账号（资金账户信息业务组件）
     *
     * @param param 请求参数
     * @return 下拉框对象集合
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getAcctNo(Map<String, Object> param, int page, int length) {
        Page<SelectOptionDto> list = super.queryForPageForSelectReturn(page, length, () -> ptlManagementInfo001Mapper.getAcctNo(param));
        return list;
    }

    /**
     * 获取组合下拉框列表
     *
     * @param map 请求参数
     * @return 下拉框集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getPortfolioId(Map<String, Object> map) {
        return ptlManagementInfo001Mapper.getPortfolioId(map);
    }

    /**
     * 获取已分配用户
     *
     * @param map 组合代码
     * @return 已分配用户集合
     * @see
     * @since 1.0
     */
    public List<PtlManagementInfoDto> selectUserNameList(Map<String, Object> map) {
        return ptlManagementInfo001Mapper.selectUserNameList(map);
    }

    /**
     * 获取资金账号信息
     *
     * @param acctNo 资金账号
     * @return 资金账号信息
     * @see
     * @since 1.0
     */
    public StlCapacctInfo getCctInfo(String acctNo) {
        String[] list = acctNo.split("_");
        acctNo = list[0];
        String ccy = list[1];
        return ptlManagementInfo001Mapper.getCctInfo(acctNo, ccy);
    }

    /**
     * 获取证券账号
     *
     * @param param 模糊查询参数
     * @return 下拉框实体类列表
     * @see
     * @since 1.0
     */

    public List<SelectOptionDto> getSecAcctId(Map<String, Object> param) {
        return ptlManagementInfo001Mapper.getSecAcctId(param);
    }

    /**
     * 获取证券账号信息
     *
     * @param secAcctNo 证券账号
     * @return 证券账号实体类
     * @see
     * @since 1.0
     */
    public SecuritiesAccountInfoDto getSecAcctInfo(String secAcctNo) {
        return ptlManagementInfo001Mapper.getSecAcctInfo(secAcctNo);
    }

    /**
     * 组合对应的证券账号
     *
     * @param map
     * @return 组合信息实体类
     * @see
     * @since 1.0
     */
    public PtlManagementInfoDto getInitInfo(Map<String, Object> map) {
        List<String> idList = ptlManagementInfo001Mapper.getsecId(map);
        List<SecuritiesAccountInfoDto> secDto = new ArrayList<SecuritiesAccountInfoDto>();
        for (String id : idList) {
            SecuritiesAccountInfoDto dto = ptlManagementInfo001Mapper.getSecAcctInfo(id);
            dto.setSecAcctId(id);
            secDto.add(dto);
        }
        List<FundAccountInfoDto> funDto = ptlManagementInfo001Mapper.getFunInfo(map);
        // for(FundAccountInfoDto d : funDto){
        // d.setAcctNo(d.getAcctNo()+"_"+d.getCcy());
        // }
        PtlManagementInfoDto baseDto = ptlManagementInfo001Mapper.getBaseInfo(map);
        List<String> userIdList = ptlManagementInfo001Mapper.getUserIdInfo(map);
        if (baseDto != null) {
            baseDto.setUserId(userIdList);
            baseDto.setSecuritiesAccountInfoDto(secDto);
            baseDto.setFundAccountInfoDto(funDto);
        }
        return baseDto;
    }

    /**
     * 删除资金账户信息
     *
     * @param dto 组合代码
     * @return 是否
     * @see
     * @since 1.0
     */
    public int funInfoDelete(String dto) {
        return ptlManagementInfo001Mapper.deletePtlCapacctInfoByPrimaryKey(dto);
    }

    /**
     * 插入资金账户信息
     *
     * @param dto 资金账户实体类
     * @return 是否
     * @see
     * @since 1.0
     */
    public int funInfoInsert(FundAccountInfoDto dto) {
        PtlCapacctInfo ptlCapacctInfo = new PtlCapacctInfo();
        BeanUtil.copyPropertiesIgnoreNull(ptlCapacctInfo, dto);
        return ptlCapacctInfoMapper.insertSelective(ptlCapacctInfo);
        // return ptlManagementInfo001Mapper.insertPtlCapacctInfo(dto);
    }

    /**
     * 插入证券账户信息
     *
     * @param dto 证券账户实体类
     * @return 是否
     * @see
     * @since 1.0
     */
    public int secInfoInsert(SecuritiesAccountInfoDto dto) {
        PtlSecAcct ptlSecAcct = new PtlSecAcct();
        BeanUtil.copyPropertiesIgnoreNull(ptlSecAcct, dto);
        ptlSecAcct.setAcctNo(dto.getSecAcctId());
        return ptlSecAcctMapper.insertSelective(ptlSecAcct);
        // return 1;
        // return ptlManagementInfo001Mapper.insertPtlSecAcct(dto);
    }

    /**
     * 删除userId
     *
     * @param portfolioId 组合代码
     * @return 是否
     * @see
     * @since 1.0
     */
    public int deleteUserId(String portfolioId) {
        return ptlManagementInfo001Mapper.deleteUserId(portfolioId);
    }

    /**
     * 通过用户进行删除
     *
     * @param userId 用户名
     * @see
     * @since 1.0
     */
    public void deleteByUserId(String userId) {
        ptlManagementInfo001Mapper.deleteByUserId(userId);
    }

    /**
     * 获取组合集合
     *
     * @param map 请求参数
     * @return 组合集合
     * @see
     * @since 1.0
     */
    public List<PtlPortfolio> getSelectedport(Map<String, Object> map) {
        return ptlManagementInfo001Mapper.getSelectedport(map);
    }

    /**
     * 删除组合信息
     *
     * @param dto 组合管理
     * @return 是否
     * @see
     * @since 1.0
     *//*
     * public boolean deletePaPtlPortfolioByPrimaryKey(PtlManagementInfoDto dto){ return
     * ptlManagementInfo001Mapper.deletePaPtlPortfolioByPrimaryKey(dto) > 0 &&
     * ptlManagementInfo001Mapper.deletePtlCapacctInfoByPrimaryKey(dto) > 0 &&
     * ptlManagementInfo001Mapper.deletePtlPortfolioAuthorityByPrimaryKey(dto) > 0 &&
     * ptlManagementInfo001Mapper.deletePtlPortfolioByPrimaryKey(dto) > 0 &&
     * ptlManagementInfo001Mapper.deletePtlSecAcctByPrimaryKey(dto) > 0 &&
     * ptlManagementInfo001Mapper.deletePtlValParamByPrimaryKey(dto) > 0; }
     */

    /**
     * 组合删除校验 <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public boolean deleteCheck(PtlManagementInfoDto dto) {
        int flg = 0;
        for (PtlManagementInfoDto entity : dto.getSelectedItem()) {
            Map<String, Object> map = new HashMap<>();
            map.put("portfolioId", entity.getPortfolioId());
            map.put("tradeStatus", DictCodeListExt1.交易状态_初始.getValue());
            map.put("yesno_n", DictCodeList.是否_否.getValue());
            flg = flg + ptlManagementInfo001Mapper.deleteCheck(map);
        }
        return flg == 0 ? true : false;
    }

    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<NodeDto> getInstitu(Map<String,Object> param) {
        //多级法人改造 add by jingqiongqiong 20200618
//        Map<String, Object> param = new HashMap<String, Object>();
//        // 二级法人权限处理
//        if(MultilevelDataAuthorityUtil.isEnabled()) {
//            //当前用户所属机构代码
//            String createInst =  MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
//            param.put("institutionCode",createInst);
//            //
//            List<String> searchOrgCodeList = com.joyintech.tams.systemtools.util.CommonUtil.getParentInstitution(createInst);
//            if(StringUtil.isNotEmpty(searchOrgCodeList)){
//                searchOrgCodeList.add(createInst);
//            }
//            param.put("resourcesTreeInstCodeList",searchOrgCodeList);
//        }
        return ptlManagementInfo001Mapper.getInstitu(param);
        //多级法人改造 add by jingqiongqiong 20200618
    }


    /*add by liupengfei for 多级法人改造 默认选中 上级管理员用户和本级管理员 start*/
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<String> getAdminUserList(Map<String,Object> param){
        List<String> userList = new ArrayList<>();
        List<NodeDto> nodeList = ptlManagementInfo001Mapper.getUserList(param);
        for (NodeDto node:nodeList) {
            userList.add(node.getCode());
        }
        return userList;
    }
    /*add by liupengfei for 多级法人改造 默认选中 上级管理员用户和本级管理员 end*/
    
    
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<String> getUserList(Map<String,Object> param){
    	if(param.get("resourcesTreeInstCodeList")==null||((List)param.get("resourcesTreeInstCodeList")).size()==0) {
    		return new ArrayList<String>();
    	}
        List<String> userList = ptlManagementInfo001Mapper.getAllUserList(param);
        return userList;
    }
    
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String getUserInstitution(String userName){
        return ptlManagementInfo001Mapper.getUserInstitution(userName);
    }

    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<NodeDto> getDep(Map<String,Object> param) {
//        //多级法人改造 add by jingqiongqiong 20200618
//        Map<String, Object> param = new HashMap<String, Object>();
//        // 二级法人权限处理
//        if(MultilevelDataAuthorityUtil.isEnabled()) {
//            //当前用户所属机构代码
//            String createInst =  MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
//            param.put("institutionCode",createInst);
//            //
//            List<String> searchOrgCodeList = com.joyintech.tams.systemtools.util.CommonUtil.getParentInstitution(createInst);
//            if(StringUtil.isNotEmpty(searchOrgCodeList)){
//                searchOrgCodeList.add(createInst);
//            }
//            param.put("resourcesTreeInstCodeList",searchOrgCodeList);
//        }
        return ptlManagementInfo001Mapper.getDep(param);
        //多级法人改造 add by jingqiongqiong 20200618
    }

    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<NodeDto> getUser(Map<String,Object> param) {
        //多级法人改造 add by jingqiongqiong 20200618
        List<NodeDto> returnList = new ArrayList<NodeDto>();
//        Map<String, Object> param = new HashMap<String, Object>();
//        // 二级法人权限处理
//        if(MultilevelDataAuthorityUtil.isEnabled()) {
//            //当前用户所属机构代码
//            String createInst =  MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
//            param.put("institutionCode",createInst);
//            List<String> searchOrgCodeList = com.joyintech.tams.systemtools.util.CommonUtil.getParentInstitution(createInst);
//            param.put("resourcesTreeInstCodeList",searchOrgCodeList);
//        }

        List<NodeDto> createInstList=ptlManagementInfo001Mapper.getUser(param);
        List<NodeDto> resourcesTreeInstCodeList=ptlManagementInfo001Mapper.getUserList(param);
        returnList.addAll(createInstList);
        returnList.addAll(resourcesTreeInstCodeList);
        return returnList;
        //多级法人改造 add by jingqiongqiong 20200618
    }

    public int deleteFinCashInfoByFinprodId(String finprodId) {
        return ptlManagementInfo001Mapper.deleteFinCashInfoByFinprodId(finprodId, DictCodeListExt1.现金流业务类型_投资组合.getValue());
    }

    public List<PtlCapacctInfo> getFundAccountInfo(String portfolioId) {
        return ptlManagementInfo001Mapper.getFundAccountInfo(portfolioId);
    }

    public List<PtlSecAcct> getSecuritiesAccountInfo(String portfolioId) {
        return ptlManagementInfo001Mapper.getSecuritiesAccountInfo(portfolioId);
    }

    public List<PtlPortfolioAuthority> getAuthority(String portfolioId) {
        return ptlManagementInfo001Mapper.getAuthority(portfolioId);
    }

    /**
     * 获取投资策略
     *
     * @param map 请求参数
     * @return 投资策略下拉框集合
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getStrategy(Map<String, Object> map) {
        return ptlManagementInfo001Mapper.getStrategy(map);
    }

    /**
     * 获取子组合
     *
     * @param portfolioId 请求参数
     * @return 子组合集合
     * @see
     * @since 1.0
     */
    public List<PtlManagementInfoDto> getChildPortfolio(String portfolioId) {
        return ptlManagementInfo001Mapper.getChildPortfolio(portfolioId);
    }

    public PtlPortfolio getPortfolio(String portfolioId) {
        return ptlPortfolioMapper.selectByPrimaryKey(portfolioId);
    }

    public int updateChildPortfolio(Map<String, Object> map) {
        //修改子组合名称
        PtlPortfolio ptlPortfolio = new PtlPortfolio();
        ptlPortfolio.setPortfolioId(map.get("portfolioId").toString());
        ptlPortfolio.setPortfolioName(map.get("portfolioName").toString());
        ptlPortfolioMapper.updateByPrimaryKeySelective(ptlPortfolio);
        //修改子组合继承比例
        PtlPortfolioDis portfolioDis = new PtlPortfolioDis();
        portfolioDis.setPortfolioId(map.get("portfolioId").toString());
        portfolioDis.setDisPercent(BigDecimal.valueOf(Double.valueOf(map.get("disPercent").toString())));
        ptlPortfolioDisMapper.updateByPrimaryKeySelective(portfolioDis);
        return 1;
    }

    public int createSequence(String sequenceName) {
        return ptlManagementInfo001Mapper.createSequence(sequenceName);
    }

    public String getSequence(String sequenceName) {
        return ptlManagementInfo001Mapper.getSequence(sequenceName);
    }

    /**
     * 获取自增序列值
     *
     * @param sequenceName 序列名
     * @return
     */
    public int getNextId(String sequenceName) {
        return ptlManagementInfo001Mapper.getNextId(sequenceName);
    }

    public String createChildPortfolioId(String portfolioId) {
        String childPortfolioId;
        String sequenceName = this.getSequence("SEQUENCE_" + portfolioId.toUpperCase());
        //如果序列不存在则创建，存在则直接获取自增数
        if (StringUtil.isEmpty(sequenceName)) {
            sequenceName = "SEQUENCE_" + portfolioId.toUpperCase();
            this.createSequence(sequenceName);
        }
        while (true) {
            //获取自增数
            int nextId = this.getNextId(sequenceName);
            //拼接组合代码和自增数
            childPortfolioId = portfolioId + nextId;
            //查询是否重复
            PtlPortfolio ptlPortfolio = this.getPortfolio(childPortfolioId);
            //如果自增的子组合代码不存在，则跳出循环
            if (null == ptlPortfolio) {
                break;
            }
        }
        return childPortfolioId;
    }

    public int updateInvestYesorno(Map<String, Object> map) {
        PtlPortfolio ptlPortfolio = new PtlPortfolio();
        ptlPortfolio.setPortfolioId(map.get("portfolioId").toString());
        ptlPortfolio.setInvestYesorno(map.get("investYesorno").toString());
        return ptlPortfolioMapper.updateByPrimaryKeySelective(ptlPortfolio);
    }

    public List<PtlManagementInfoDto> setParentPortfolioId(Map<String, Object> map) {
        List<PtlManagementInfoDto> selectedItem = JSONArray.parseArray(JSON.toJSONString(map.get("selectedItem")), PtlManagementInfoDto.class);
        for (PtlManagementInfoDto ptlManagementInfoDto : selectedItem) {
            PtlPortfolio ptlPortfolio = ptlPortfolioMapper.selectByPrimaryKey(ptlManagementInfoDto.getPortfolioId());
            ptlManagementInfoDto.setParentPortfolioId(ptlPortfolio.getParentPortfolioId());
            //如果是子组合
            if (!StringUtil.isEmpty(ptlPortfolio.getParentPortfolioId())) {
                //查询出来母组合 信息
                PtlPortfolio dto = ptlPortfolioMapper.selectByPrimaryKey(ptlPortfolio.getParentPortfolioId());
                ptlManagementInfoDto.setChildPortfolioId(ptlPortfolio.getPortfolioId());
                ptlManagementInfoDto.setChildPortfolioName(ptlPortfolio.getPortfolioName());
                ptlManagementInfoDto.setPortfolioId(ptlPortfolio.getParentPortfolioId());
                ptlManagementInfoDto.setPortfolioName(dto.getPortfolioName());
                ptlManagementInfoDto.setPortfolioType(DictCodeListExt1.组合类型_子组合.getValue());
            }

        }
        return selectedItem;
    }

    public List<SelectOptionDto> selectPortfolioId(Map<String, Object> param) {
        param.put("userId", LoginUserUtil.getUserName());
        return ptlManagementInfo001Mapper.selectPortfolioId(param);
    }

    /**
     * 删除交易对手信息
     */
    public int deleteByCounterId(PtlManagementInfoDto entity) {
        MstCounterAcctKey key = new MstCounterAcctKey();
        key.setCounterId(entity.getPortfolioId());
        mstCounterPartyMapper.deleteByPrimaryKey(key.getCounterId());
        return ptlManagementInfo001Mapper.deleteByCounterId(key);
    }

    public List<SelectOptionDto> getUserList() {
        if (mstUserAndRoleService != null) {
            mstUserAndRoleOutputParameter = mstUserAndRoleService.getAllUserInfo();
        }
        List<User> userList = mstUserAndRoleOutputParameter.getUserList();
        List<SelectOptionDto> selectOptDto = new ArrayList<SelectOptionDto>();
        for (User user : userList) {
            SelectOptionDto secDto = new SelectOptionDto();
            secDto.setId(user.getUserName());
            secDto.setText(user.getRealName());
            secDto.setObject(user.getDeptName());
            if (secDto.getId() != null) {
                selectOptDto.add(secDto);
            }
        }
        return selectOptDto;
    }

    public int insertAuthority(PtlManagementInfoDto entity) {
        if (entity.getUserId() != null && entity.getUserId().size() > 0) {
            List<SelectOptionDto> allUserList = getUserList();
            for (String userId : entity.getUserId()) {
                PtlPortfolioAuthority ptlPortfolioAuthority = new PtlPortfolioAuthority();
                ptlPortfolioAuthority.setPortfolioId(entity.getPortfolioId());
                ptlPortfolioAuthority.setUserId(userId);
                if (allUserList != null && allUserList.size() > 0) {
                    for (SelectOptionDto allUser : allUserList) {
                        if (StringUtil.equals(allUser.getId(), userId)) {
                            ptlPortfolioAuthorityMapper.deleteByPrimaryKey(ptlPortfolioAuthority);
                            ptlPortfolioAuthorityMapper.insertSelective(ptlPortfolioAuthority);
                        }
                    }
                }
            }
        }
        return 1;
    }

    public int createCounter(PtlManagementInfoDto entity) {
        MstCounterParty data = mstCounterPartyMapper.selectByPrimaryKey(entity.getPortfolioId());
        if (null == data) {
            MstCounterParty mstCounterParty = new MstCounterParty();
            mstCounterParty.setCounterId(entity.getPortfolioId());
            mstCounterParty.setCounterName(entity.getPortfolioName());
            mstCounterParty.setCounterType(DictCodeListExt1.交易对手类型_内部交易对手.getValue());
            mstCounterPartyMapper.insertSelective(mstCounterParty);
            for (FundAccountInfoDto fundAccountInfoDto : entity.getFundAccountInfoDto()) {
                if (StringUtil.equals(fundAccountInfoDto.getAcctType(), DictCodeListExt1.资金账户类型_产品托管户.getValue())) {
                    MstCounterAcct mstCounterAcct = new MstCounterAcct();
                    String[] noAndCcy = fundAccountInfoDto.getAcctNo().split("_");
                    fundAccountInfoDto.setAcctNo(noAndCcy[0]);
                    BeanUtil.copyPropertiesIgnoreNull(mstCounterAcct, fundAccountInfoDto);
                    mstCounterAcct.setCounterId(entity.getPortfolioId());
                    mstCounterAcctMapper.insertSelective(mstCounterAcct);
                }
            }
        }
        return 1;
    }

    public String deleteAction(String portfolioId) {
        bussCommonMapperDelegateService.deleteByPrimaryKey(PtlValParamMapper.class, portfolioId);
        deleteUserId(portfolioId);
        deleteFinCashInfoByFinprodId(portfolioId);

        //删除组合时同步删除银行间账户组合关系（IBU_PORTFOLIO_ACCT_RELA）
        ptlManagementInfo001Mapper.deleteByPortfolioId(portfolioId);

        return "Success";
    }

    public String insertAction(PtlManagementInfoDto entity) {
        insertAuthority(entity);
        insertPtlParam(entity);

        // 组合插入现金流信息表
        if (StringUtil.equals(entity.getProfitType(), DictCodeList.收益类型_收益型.getValue())) {
            FinCashInfo finCashInfo = new FinCashInfo();
            finCashInfo.setCashId(CommonUtil.generateCashFlowCode(
                    entity.getPortfolioId(),
                    DictCodeListExt1.现金流业务类型_金融产品.getValue(), 0l,
                    DictCodeListExt1.现金流二级类型_浮动管理费.getValue()));
            finCashInfo.setCashType1(DictCodeListExt1.现金流一级类型_费用.getValue());
            finCashInfo.setCashType2(DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
            finCashInfo.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            finCashInfo.setFinprodId(entity.getPortfolioId());
            finCashInfo.setFinprodType(DictCodeListExt1.现金流业务类型_投资组合.getValue());
            finCashInfo.setBranch(0l);
            finCashInfo.setIsCalc(DictCodeList.是否_是.getValue());
            finCashInfoMapper.deleteByPrimaryKey(finCashInfo.getCashId());
            finCashInfoMapper.insertSelective(finCashInfo);
        }
        return "Success";
    }

    public String insertPtlParam(PtlManagementInfoDto entity) {
        bussCommonMapperDelegateService.deleteByPrimaryKey(PtlValParamMapper.class, entity.getPortfolioId());
        PtlValParam ptlValParam = entity.getPtlValParam();
        ptlValParam.setFinprodId(entity.getPortfolioId());
        bussCommonMapperDelegateService.insertSelective(PtlValParamMapper.class, ptlValParam);
        return "Success";
    }

    public String doComplete003(PtlManagementInfo003DataDto dto) {
        List<PtlPortfolioAuthority> ptlPortfolioAuthorities = new ArrayList<>();
        if (StringUtil.equals(dto.getAllocationWay(), "01")) {
            ptlManagementInfo001Mapper.deleteUserId(dto.getPortfolioId());
            for (String user : dto.getUserList()) {
                List<SelectOptionDto> allUserList = getUserList();
                if (allUserList != null && allUserList.size() > 0) {
                    for (SelectOptionDto allUser : allUserList) {
                        if (StringUtil.equals(allUser.getId(), user)) {
                            PtlPortfolioAuthority ptlPortfolioAuthority = new PtlPortfolioAuthority();
                            ptlPortfolioAuthority.setPortfolioId(dto.getPortfolioId());
                            ptlPortfolioAuthority.setUserId(user);
                            ptlPortfolioAuthority.setCreateDept(dto.getCreateDept());
                            ptlPortfolioAuthority.setCreateTime(new Date());
                            ptlPortfolioAuthority.setCreateUser(dto.getCreateUser());
                            ptlPortfolioAuthority.setUpdateTime(new Date());
                            ptlPortfolioAuthority.setUpdateUser(dto.getCreateUser());
                            ptlPortfolioAuthorities.add(ptlPortfolioAuthority);
                        }
                    }
                }
            }
        } else if (StringUtil.equals(dto.getAllocationWay(), "02")) {
            this.deleteByUserId(dto.getSelectedList().get(0).getUserId());
            for (PtlManagementInfo003DataDto selectItem : dto.getSelectedList()) {
                PtlPortfolioAuthority ptlPortfolioAuthority = new PtlPortfolioAuthority();
                ptlPortfolioAuthority.setPortfolioId(selectItem.getPortfolioId());
                ptlPortfolioAuthority.setUserId(selectItem.getUserId());
                ptlPortfolioAuthority.setCreateDept(dto.getCreateDept());
                ptlPortfolioAuthority.setCreateTime(new Date());
                ptlPortfolioAuthority.setCreateUser(dto.getCreateUser());
                ptlPortfolioAuthority.setUpdateTime(new Date());
                ptlPortfolioAuthority.setUpdateUser(dto.getCreateUser());
                ptlPortfolioAuthorities.add(ptlPortfolioAuthority);
            }
        }
        batchInsertPtlAuths(ptlPortfolioAuthorities);

        return "Success";
    }

    public String doInsertAction(PtlManagementInfoDto entity) {
        //继承母组合的清算账户信息
        List<PtlCapacctInfo> fundAccountInfo = getFundAccountInfo(entity.getPortfolioId());
        List<FundAccountInfoDto> fundAccountInfoDtos = new ArrayList<>();
        for (PtlCapacctInfo ptlCapacctInfo : fundAccountInfo) {
            FundAccountInfoDto fundAccountInfoDto = new FundAccountInfoDto();
            BeanUtil.copyPropertiesIgnoreNull(fundAccountInfoDto, ptlCapacctInfo);
            fundAccountInfoDtos.add(fundAccountInfoDto);
        }
        entity.setFundAccountInfoDto(fundAccountInfoDtos);

        for (FundAccountInfoDto dto : fundAccountInfoDtos) {
            dto.setPortfolioId(entity.getChildPortfolioId());
            String[] temp = dto.getAcctNo().split("_");
            dto.setAcctNo(temp[0]);
            //增加判断 by gejiaao 2019/8/15
            if (temp.length > 1){
                dto.setCcy(temp[1]);
            }
            PtlCapacctInfo ptlCapacctInfo = new PtlCapacctInfo();
            BeanUtil.copyPropertiesIgnoreNull(ptlCapacctInfo, dto);
            ptlCapacctInfoMapper.insertSelective(ptlCapacctInfo);
        }

        //继承母组合的证券账户信息
        List<PtlSecAcct> securitiesAccountInfos = getSecuritiesAccountInfo(entity.getPortfolioId());
        List<SecuritiesAccountInfoDto> securitiesAccountInfoDtos = new ArrayList<>();
        for (PtlSecAcct securitiesAccountInfo : securitiesAccountInfos) {
            SecuritiesAccountInfoDto securitiesAccountInfoDto = new SecuritiesAccountInfoDto();
            BeanUtil.copyPropertiesIgnoreNull(securitiesAccountInfoDto, securitiesAccountInfo);
            securitiesAccountInfoDto.setSecAcctId(securitiesAccountInfo.getAcctNo());
            securitiesAccountInfoDtos.add(securitiesAccountInfoDto);
        }
        for(SecuritiesAccountInfoDto securitiesAccountInfoDto : securitiesAccountInfoDtos) {
            PtlSecAcct ptlSecAcct = new PtlSecAcct();
            BeanUtil.copyPropertiesIgnoreNull(ptlSecAcct, securitiesAccountInfoDto);
            ptlSecAcct.setAcctNo(securitiesAccountInfoDto.getSecAcctId());
            ptlSecAcct.setPortfolioId(entity.getChildPortfolioId());
            ptlSecAcctMapper.insertSelective(ptlSecAcct);
        }

        //继承母组合的组合权限信息
        List<PtlPortfolioAuthority> authoritys = getAuthority(entity.getPortfolioId());
        List<String> userList = new ArrayList<>();
        for (PtlPortfolioAuthority portfolioAuthority : authoritys) {
            userList.add(portfolioAuthority.getUserId());
        }
        PtlManagementInfoDto ptlManagementInfo004Dto = new PtlManagementInfoDto();
        ptlManagementInfo004Dto.setPortfolioId(entity.getChildPortfolioId());
        ptlManagementInfo004Dto.setUserId(userList);
        insertAuthority(ptlManagementInfo004Dto);

        //继承母组合估值参数信息
        if (null != bussCommonMapperDelegateService) {
            PtlValParam ptlValParam = ptlValParamMapper.selectByPrimaryKey(entity.getPortfolioId());
            ptlValParam.setFinprodId(entity.getChildPortfolioId());
            bussCommonMapperDelegateService.insertSelective(PtlValParamMapper.class, ptlValParam);
            entity.setPtlValParam(ptlValParam);
        }
        // 子组合插入现金流信息表
        if (StringUtil.equals(entity.getProfitType(), DictCodeList.收益类型_收益型.getValue())) {
            FinCashInfo finCashInfo = new FinCashInfo();
            finCashInfo.setCashId(CommonUtil.generateCashFlowCode(
                    entity.getChildPortfolioId(),
                    DictCodeListExt1.现金流业务类型_金融产品.getValue(), 0l,
                    DictCodeListExt1.现金流二级类型_浮动管理费.getValue()));
            finCashInfo.setCashType1(DictCodeListExt1.现金流一级类型_费用.getValue());
            finCashInfo.setCashType2(DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
            finCashInfo.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            finCashInfo.setFinprodId(entity.getChildPortfolioId());
            finCashInfo.setFinprodType(DictCodeListExt1.现金流业务类型_投资组合.getValue());
            finCashInfo.setBranch(0l);
            finCashInfo.setIsCalc(DictCodeList.是否_是.getValue());
            if (bussCommonMapperDelegateService != null) {
                bussCommonMapperDelegateService.insertSelective(FinCashInfoMapper.class, finCashInfo);
            }
        }

        //修改母组合是否能投资字段为否
        Map<String, Object> map = new HashMap<>();
        map.put("portfolioId", entity.getPortfolioId());
        map.put("investYesorno", DictCodeList.是否_否.getValue());
        updateInvestYesorno(map);
        //给母组合代码赋值
        entity.setParentPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(entity.getChildPortfolioId());
        entity.setPortfolioName(entity.getChildPortfolioName());
        //子组合时是否保本为空
//            entity.setIsGuarantee(null);
        //是否能投资为是
        entity.setInvestYesorno(DictCodeList.是否_是.getValue());
        insert(entity);
        //生成子组合的内部交易对手，并删除母组合内部交易对手
        PtlManagementInfoDto dto = new PtlManagementInfoDto();
        dto.setPortfolioId(entity.getChildPortfolioId());
        dto.setPortfolioName(entity.getChildPortfolioName());
        dto.setFundAccountInfoDto(entity.getFundAccountInfoDto());
        createCounter(dto);
        dto.setPortfolioId(entity.getParentPortfolioId());
        deleteByCounterId(dto);

        /** add by yanya on 20191114 子组合新建调用估值接口 start**/
        PtlPortfolio ptlPortfolio = new PtlPortfolio();
        BeanUtil.copyPropertiesIgnoreNull(ptlPortfolio, entity);
        ProdOtherInfoInitInputDto inputDto = new ProdOtherInfoInitInputDto();
        inputDto.setPageId("PtlManagementInfo002");
        inputDto.setPtlPortfolio(ptlPortfolio);
        inputDto.setPtlValParam(entity.getPtlValParam());
        inputDto.setNewData(true);//新增
        if (!StringUtil.isEmptyList(entity.getFundAccountInfoDto())) {
            List<PtlCapacctInfo> prodAcctDataList = new ArrayList<>();
            for (FundAccountInfoDto fundAccountInfoDto : entity.getFundAccountInfoDto()) {
                PtlCapacctInfo ptlCapacctInfo = new PtlCapacctInfo();
                BeanUtil.copyPropertiesIgnoreNull(ptlCapacctInfo, fundAccountInfoDto);
                prodAcctDataList.add(ptlCapacctInfo);
            }
            inputDto.setProdAcctDataList(prodAcctDataList);
        }
//        valuationInterfService.prodOtherInfoInit(inputDto);
        /** add by yanya on 20191114 子组合新建调用估值接口 end**/
        return "Success";
    }

    public String doDeleteAction(PtlManagementInfoDto entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("portfolioId", entity.getChildPortfolioId());
        map.put("portfolioName", entity.getChildPortfolioName());
        map.put("disPercent", entity.getDisPercent().divide(BigDecimal.valueOf(100)));
        updateChildPortfolio(map);

        //修改子组合权限信息
        deleteUserId(entity.getChildPortfolioId());
        PtlValParam ptlValParam = ptlValParamMapper.selectByPrimaryKey(entity.getPortfolioId());
        ptlValParam.setFinprodId(entity.getChildPortfolioId());
        entity.setPtlValParam(ptlValParam);
        entity.setPortfolioId(entity.getChildPortfolioId());
        insertAuthority(entity);

        /** add by yanya on 20191114 子组合修改调用估值接口 start**/
        PtlPortfolio ptlPortfolio = new PtlPortfolio();
        BeanUtil.copyPropertiesIgnoreNull(ptlPortfolio, entity);
        ProdOtherInfoInitInputDto inputDto = new ProdOtherInfoInitInputDto();
        inputDto.setPageId("PtlManagementInfo002");
        inputDto.setPtlPortfolio(ptlPortfolio);
        inputDto.setPtlValParam(entity.getPtlValParam());
        inputDto.setNewData(false);//修改
        if (!StringUtil.isEmptyList(entity.getFundAccountInfoDto())) {
            List<PtlCapacctInfo> prodAcctDataList = new ArrayList<>();
            for (FundAccountInfoDto fundAccountInfoDto : entity.getFundAccountInfoDto()) {
                PtlCapacctInfo ptlCapacctInfo = new PtlCapacctInfo();
                BeanUtil.copyPropertiesIgnoreNull(ptlCapacctInfo, fundAccountInfoDto);
                prodAcctDataList.add(ptlCapacctInfo);
            }
            inputDto.setProdAcctDataList(prodAcctDataList);
        }
//        valuationInterfService.prodOtherInfoInit(inputDto);
        /** add by yanya on 20191114 子组合修改调用估值接口 end**/
        return "Success";
    }

    public String insertUser(PtlManagementInfo003DataDto entity) {
        deleteByUserId(entity.getSelectedList().get(0).getUserId());
        List<PtlPortfolioAuthority> ptlPortfolioAuthorities = new ArrayList<>();
        for(PtlManagementInfo003DataDto selectItem : entity.getSelectedList()){
            PtlPortfolioAuthority ptlPortfolioAuthority = new PtlPortfolioAuthority();
            ptlPortfolioAuthority.setPortfolioId(selectItem.getPortfolioId());
            ptlPortfolioAuthority.setUserId(selectItem.getUserId());
            ptlPortfolioAuthority.setCreateDept(entity.getCreateDept());
            ptlPortfolioAuthority.setCreateTime(new Date());
            ptlPortfolioAuthority.setCreateUser(entity.getCreateUser());
            ptlPortfolioAuthority.setUpdateTime(new Date());
            ptlPortfolioAuthority.setUpdateUser(entity.getCreateUser());
            ptlPortfolioAuthorities.add(ptlPortfolioAuthority);
        }

        batchInsertPtlAuths(ptlPortfolioAuthorities);
        return "Success";
    }

    private void batchInsertPtlAuths(List<PtlPortfolioAuthority> ptlPortfolioAuthorities) {
        if (!ptlPortfolioAuthorities.isEmpty()) {
            int pointsDataLimit = 200;//限制条数
            Integer size = ptlPortfolioAuthorities.size();
            if (pointsDataLimit < size) {//判断是否有必要分批
                int part = size / pointsDataLimit;//分批数
                for (int i = 0; i < part; i++) {
                    List<PtlPortfolioAuthority> listPage = ptlPortfolioAuthorities.subList(0, pointsDataLimit);
                    ptlManagementInfo001Mapper.batchInsertPtlPortAuth(listPage);
                    ptlPortfolioAuthorities.subList(0, pointsDataLimit).clear();
                }
                if (!ptlPortfolioAuthorities.isEmpty()) {
                    ptlManagementInfo001Mapper.batchInsertPtlPortAuth(ptlPortfolioAuthorities);
                }
            } else {
                ptlManagementInfo001Mapper.batchInsertPtlPortAuth(ptlPortfolioAuthorities);
            }
        }
    }

    /**
     * 投管接口（组合信息输入接口）
     * 
     * @param entity 参数
     * @return 是否成功
     * @see
     * @since 1.0
     */
    public String insertPortfolioInfo(PtlManagementInfoDto entity) {
        entity.setInvestYesorno(DictCodeList.是否_是.getValue());
        ptlPortfolioMapper.insert(entity);
        PaPtlPortfolio paPtlPortfolio = new PaPtlPortfolio();
        // 组合代码
        paPtlPortfolio.setPortfolioId(entity.getPortfolioId());
        // 是否保本，保本、非保本
        paPtlPortfolio.setIsGuarantee(entity.getIncomeFlag());;
        // 开放类型，开放式、封闭式
        paPtlPortfolio.setOpenType(entity.getOpenType());
        // 投资性质，固定收益类、权益类、商品及金融衍生品类、混合类
        paPtlPortfolio.setInvestNature(entity.getInvestNature());
        paPtlPortfolioMapper.insert(paPtlPortfolio);
        return DictCodeList.是否_是.getValue();
    }

    /**
     * 投管接口（组合证券账户信息）
     * 
     * @param entity 参数
     * @return 是否成功
     * @see
     * @since 1.0
     */
    public String insertPtlSecAcct(PtlSecAcct entity) {
    	//PtlSecAcct ptlSecAcct = new PtlSecAcct();
    	ptlSecAcctMapper.insertSelective(entity);
        return DictCodeList.是否_是.getValue();
    }
    
    /**
     * 投管接口（组合资金账户信息）
     * 
     * @param entity 参数
     * @return 是否成功
     * @see
     * @since 1.0
     */
    public String insertPtlCapacctInfo(PtlCapacctInfo entity) {
    	//PtlCapacctInfo ptlCapacctInfo = new PtlCapacctInfo();
    	ptlCapacctInfoMapper.insertSelective(entity);
        return DictCodeList.是否_是.getValue();
    }

    public int getReAcct(Map<String, Object> map) {
        return ptlCapacctInfoMapper.getReAcct(map);
    }
    
    /**
     * 模拟试算 组合信息服务
     *
     * @param map 参数
     * @throws Exception 异常
     * @return 组合基本信息
     * @see
     * @since 1.0
     */
    public MockPtlPortfolioDto selectPortfolioDetailInfo(Map<String, Object> map)
    {
        String portfolioId = (String)map.get("portfolioId");
        String paramDate = (String)map.get("paramDate");
        if (StringUtil.isNotBlank(paramDate)) {
            map.remove("paramDate");
            map.put("paramDate", DateUtil.parse(paramDate));
        }

        // 根据组合代码查询组合信息表数据
        PtlPortfolio ptlPortfolio = ptlPortfolioMapper.selectByPrimaryKey(portfolioId);
        MockPtlPortfolioDto d = new MockPtlPortfolioDto();
        BeanUtil.copyPropertiesIgnoreNull(d, ptlPortfolio);

        // 根据组合代码查询组合对应产品的产品金融产品代码
        String finprodId = valuationInterfService.selectFinprodIdByPortfolioId(portfolioId);
        if (StringUtil.isBlank(finprodId)) {
            finprodId = portfolioId;
        }
        map.put("finprodId", finprodId);

        // 查询业绩基准
        BigDecimal jizhun = findBenchmark(map);
        d.setFinBaseRuleYield(jizhun);

        // 管理费率
        FinCashCalcRule yield = ptlManagementInfo001Mapper.selectYield(map);
        if (StringUtil.isNotEmpty(yield)) {
            d.setManageFeeRate(yield.getYield());
        }

        // 查询分红规则
        FinCashPayRule paycycle = ptlManagementInfo001Mapper.selectFhgz(map);
        if (StringUtil.isNotEmpty(paycycle)) {
            d.setBonusPayCycle(paycycle.getPayCycle());
        }

        // 查询计息基础
        FinRateInfo basis = ptlManagementInfo001Mapper.selectBasis(map);
        if (StringUtil.isNotEmpty(basis)) {
            d.setBasis(basis.getBasis());
        }

        // 查询开放计划
        List<FinOpenPlan> openjh = ptlManagementInfo001Mapper.selectOpenjh(map);
        if (StringUtil.isNotEmpty(openjh)) {
            d.setFinOpenPlanList(openjh);
        }

        // 查询组合扩展表数据
        PaPtlPortfolio kz = paPtlPortfolioMapper.selectByPrimaryKey(portfolioId);
        d.setPaPtlPortfolio(kz);
        return d;
    }

    /**
     *
     * @param map
     * @return
     */
    public List<FinBaseRuleInfo> selectAllBaseRule(Map<String,Object> map){
        return ptlManagementInfo001Mapper.selectAllBaseRule(map);
    }

    /**
     * 模拟试算 业绩基准
     *
     * @param map 参数
     * @return 业绩基准
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    private BigDecimal findBenchmark(Map<String, Object> map)
    {
        // 查询业绩基准
        FinBaseRuleInfo dto = ptlManagementInfo001Mapper.selectBenchmark(map);
        BigDecimal a = null;
        if (StringUtil.isNotEmpty(dto)) {
            map.put("indexId", dto.getIndexId());
            if (StringUtil.equals(DictCodeList.利率类型_固定利率.getValue(), dto.getRateType())) {
                // 固定利率
                a = dto.getRate();
            }
            else if (StringUtil.equals(DictCodeList.利率类型_浮动利率.getValue(), dto.getRateType())) {
                // 基准来源利率
//                map.put("indexId", dto.getBaseRuleId());
                MstIntRateInfo dto1 = ptlManagementInfo001Mapper.selectlv(map);
                if (StringUtil.isNotEmpty(dto1)) {
                    a = ArithmeticUtil.doAdd(
                            ArithmeticUtil.multiply(dto1.getIntRate(),dto.getCoefficient()==null?BigDecimal.ONE:dto.getCoefficient()),dto.getSpreadRate());
//                    a = dto1.getIntRate();
                }
//                // 浮动利率
//                if (StringUtil.equals(DictCodeListExt1.基准类型_2_利率.getValue(),
//                    dto.getIndexSource())) {
//                    // 基准来源利率
//                    map.put("indexId", dto.getBaseRuleId());
//                    MstIntRateInfo dto1 = ptlManagementInfo001Mapper.selectlv(map);
//                    if (StringUtil.isNotEmpty(dto1)) {
//                        a = dto1.getIntRate();
//                    }
//                }
//                else if (StringUtil.equals(DictCodeListExt1.基准类型_2_指数.getValue(),
//                    dto.getIndexSource())) {
//                    // 基准来源指数
//                    MstExponentQuotation dto2 = ptlManagementInfo001Mapper.selectSpj(map);
//                    if (StringUtil.isNotEmpty(dto2)) {
//                        a = dto2.getClosePrice();
//                    }
//                }
//                else if (StringUtil.equals(DictCodeListExt1.基准类型_2_复合.getValue(),
//                    dto.getIndexSource())) {
//                    // 基准来源复合
//                    List<MstIndexComponent> dto3 = ptlManagementInfo001Mapper.selectJzcf(map);
//                    if (!StringUtil.isEmptyList(dto3)) {
//                        BigDecimal jz = new BigDecimal(0);
//                        for (MstIndexComponent dto4 : dto3) {
//                            if (StringUtil.equals(DictCodeList.行情类型_利率.getValue(),
//                                dto4.getComponentType())) {
//                                map.put("indexId", dto4.getComponentId());
//                                MstIntRateInfo dto5 = ptlManagementInfo001Mapper.selectlv(map);
//                                if (StringUtil.isNotEmpty(dto5)) {
//                                    jz = ArithmeticUtil.doAdd(
//                                        dto4.getWeight().multiply(dto5.getIntRate()), jz);
//                                }
//                            }
//                            else if (StringUtil.equals(DictCodeList.行情类型_指数.getValue(),
//                                dto4.getComponentType())) {
//                                map.put("indexId", dto4.getComponentId());
//                                MstExponentQuotation dto6 = ptlManagementInfo001Mapper.selectSpj(
//                                    map);
//                                if (StringUtil.isNotEmpty(dto6)) {
//                                    jz = ArithmeticUtil.doAdd(
//                                        dto4.getWeight().multiply(dto6.getClosePrice()), jz);
//                                }
//                            }
//                        }
//                        a = jz;
//                    }
//                }
            }
        }
        return a;
    }

    /**
     *
     * 查询基准来源为利率时所有的业绩基准
     * @param map
     * @return
     */
    public List<MstIntRateInfo> selectAlllv(Map<String,Object> map){
        return ptlManagementInfo001Mapper.selectAlllv(map);
    }

    /**
     *
     * @Description //组合权限接口
     * @Param [param]
     * @return java.util.List<com.joyintech.service.portfolio.ptlmanagementinfo.dto.PtlAuthInfoDto>
     * @version 1.0
     * @see
     **/
    public Map<String,Object> getPtlAuthInfo(Map<String, Object> param) {
        Map<String,Object> returnMap  = new HashMap<String,Object>();
        returnMap.put("errorCode",0);
        returnMap.put("errorMsg","成功");
        returnMap.put("data",ptlManagementInfo001Mapper.getPtlAuthInfo(param));
        return returnMap;
    }
}
