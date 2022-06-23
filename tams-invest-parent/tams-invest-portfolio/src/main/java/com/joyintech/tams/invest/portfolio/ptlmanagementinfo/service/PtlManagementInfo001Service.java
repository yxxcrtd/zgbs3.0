/*
 * 文件名：PtlManagementInfo001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年9月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.invest.portfolio.ptlmanagementinfo.service;

import com.alibaba.fastjson.JSONObject;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.select.TreeNode;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.portfolio.FundAccountService;
import com.joyintech.tams.component.business.portfolio.SecuritiesAccountService;
import com.joyintech.tams.component.business.portfolio.dto.FundAccountInfoDto;
import com.joyintech.tams.component.business.portfolio.dto.SecuritiesAccountInfoDto;
import com.joyintech.tams.component.business.prod.BussFinProductValuationService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.redisinit.dto.ImplCond;
import com.joyintech.tams.invest.common.redisinit.service.RiskRedisInitService;
import com.joyintech.tams.invest.interf.trade.fromxp.service.InterfServiceLogService;
import com.joyintech.tams.invest.portfolio.ptlmanagementinfo.dto.NodeDto;
import com.joyintech.tams.invest.portfolio.ptlmanagementinfo.dto.PtlManagementInfo004Dto;
import com.joyintech.tams.invest.portfolio.ptlmanagementinfo.dto.PtlManagementInfoDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

//import com.joyintech.tams.valuation.common.interf.common.ResultCode;
//import com.joyintech.tams.valuation.common.interf.common.ResultObject;

/**
 * 组合信息Service
 *
 * @author fanshuanghu
 * @version 1.0 2018年9月6日
 * @see PtlManagementInfo001Service
 * @since 1.0
 */
@Service("PtlManagementInfo001Service")
@Transactional(rollbackFor = Exception.class)
public class PtlManagementInfo001Service extends CommonExportService<PtlManagementInfoDto>
        implements BaseProcessService<PtlManagementInfoDto> {
    /**
     * 注入资金账户业务组件Service
     */
    @Autowired(required = false)
    public FundAccountService FundAccountService;
    /**
     * 注入微服务代理类
     */
    @Autowired(required = false)
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;
    /**
     * 注入组合权限表Mapper
     */
    @Autowired
    public PtlPortfolioAuthorityMapper ptlPortfolioAuthorityMapper;
    /**
     * 注入估值参数表Mapper
     */
    @Autowired
    public PtlValParamMapper ptlValParamMapper;
    /**
     * 注入估值信息的Service
     */
    @Autowired(required = false)
    public BussFinProductValuationService bussFinProductValuationService;
    /**
     * 注入证券账户业务组件Service
     */
    @Autowired(required = false)
    public SecuritiesAccountService securitiesAccountService;

    @Autowired
    private PtlManagementInfo001Service ptlManagementInfo001Service;

    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;

    @Autowired
    private FileAttachmentProcess fileAttachmentProcess;

    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(PtlManagementInfo001Service.class);
    //-------核风险对应-裴家俊-20200107----start----
    /**
     * redis数据初始化
     */
    @Autowired
    private RiskRedisInitService<ImplCond> redisRriter;

    /**
     * 平台日期组件接口
     */
    @Autowired(required=false)
    private IPlatformDate iPlatformDate;
    //-------核风险对应-裴家俊-20200107----end----

    /**
     * 线程池
     */
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private InterfServiceLogService interfServiceLogService;

    @Autowired
    private StlCapacctInfoMapper stlCapacctInfoMapper;

    @Autowired
    private StlCapacctReInfoMapper stlCapacctReInfoMapper;


    @Autowired
    private PtlCapacctInfoMapper ptlCapacctInfoMapper;

    @Autowired
    private StlSecAcctInfoMapper stlSecAcctInfoMapper;

    @Autowired
    private PtlSecAcctMapper ptlSecAcctMapper;

    @Autowired
    private FinCashInfoMapper finCashInfoMapper;

    @Autowired
    private FinOpenRuleMapper finOpenRuleMapper;

    @Autowired
    private FinOpenPlanMapper finOpenPlanMapper;


    /**
     * 使用给定的sqlID检索分页数据。返回类型是Service的泛型。
     *
     * @param pageParam pageParam
     * @return Page类 Page
     * @throws Exception 异常信息
     */
    public Page<PtlManagementInfoDto> selectForPage(Map<String, Object> pageParam) {
        return restTemplate.postForPage(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/findPage",
                pageParam, PtlManagementInfoDto.class);
    }

    /**
     * 获取组合列表
     *
     * @param map 请求参数
     * @return 组合分页实体类
     * @see
     * @since 1.0
     */
    public List<PtlManagementInfoDto> getPortPage(Map<String, Object> map) {
        return restTemplate.postForList(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/getPortPage",
                map, PtlManagementInfoDto.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void doComplete(PtlManagementInfoDto entity, OperationType type) {
        String action = StringUtil.EMPTY;

        if (OperationType.DELETE.equals(type)) {
            if (!StringUtil.isEmpty(entity.getChildPortfolioId())) {
                entity.setPortfolioId(entity.getChildPortfolioId());
            }
            // 调用父类的主键删除方法
            action = "delete";
            FundAccountService.delete(entity.getPortfolioId());
            securitiesAccountService.delete(entity.getPortfolioId());
//            ptlValParamMapper.deleteByPrimaryKey(entity.getPortfolioId());

            restTemplate.delete(
                    ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/deleteAction?portfolioId=" + entity.getPortfolioId(),
                    String.class);

        } else if (OperationType.NEW.equals(type)) {
            action = "insert";
//            for(FundAccountInfoDto dto : entity.getFundAccountInfoDto()){
//                dto.setPortfolioId(entity.getPortfolioId());
//                FundAccountService.insert(dto);
//            }
            FundAccountService.insertMulti(entity.getFundAccountInfoDto(), entity.getPortfolioId());
//            for(SecuritiesAccountInfoDto dto : entity.getSecuritiesAccountInfoDto()){
//                dto.setPortfolioId(entity.getPortfolioId());
//                securitiesAccountService.insert(dto);
//            }
            securitiesAccountService.insertMulti(entity.getSecuritiesAccountInfoDto(), entity.getPortfolioId());

            //添加组合权限
            restTemplate.postForObject(
                    ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/insertAction", entity,
                    String.class);

            //是否能投资为否
            if (StringUtil.equals(entity.getPortfolioType(), DictCodeListExt1.组合类型_投资类组合.getValue())) {
                entity.setInvestYesorno(DictCodeList.是否_是.getValue());
            }

            //产品组合时生成内部交易对手
            if (StringUtil.equals(entity.getPortfolioType(), DictCodeListExt1.组合类型_投资类组合.getValue())) {
                createCounter(entity);
            }
        } else {
            action = "update";
        }
        if (action.equals("update")) {
            //查询是否有子组合，有则逐个修改 add by gejiaao start
            List<PtlManagementInfo004Dto> ptlPortfolios = restTemplate.getForList(ServiceApplication.PORTFOLIO_SERVICE_NAME +
                    "/ptlmanagementinfo/getChildPortfolio?portfolioId=" + entity.getPortfolioId(), PtlManagementInfo004Dto.class);

            if (StringUtil.equals(entity.getPortfolioType(), DictCodeListExt1.组合类型_投资类组合.getValue())) {
                if (!ptlPortfolios.isEmpty()) {
                    entity.setInvestYesorno(DictCodeList.是否_否.getValue());
                } else {
                    entity.setInvestYesorno(DictCodeList.是否_是.getValue());
                }
            }
//            ResultObject resultObject = updatePortfolio(entity);
            updatePortfolio(entity);
//            if (!StringUtil.equals(resultObject.getResultCode(), ResultCode.SUCCESS.getCode())) {
//                throw new RuntimeException(resultObject.getResultMsg());
//            }
            String portfolioId = entity.getPortfolioId();
            if (!ptlPortfolios.isEmpty()) {
                for (PtlManagementInfo004Dto ptlPortfolio : ptlPortfolios) {
                    entity.setPortfolioId(ptlPortfolio.getPortfolioId());
                    entity.setPortfolioName(ptlPortfolio.getPortfolioName());
                    //子组合时是否保本为空
//                    entity.setIsGuarantee(null);
                    //是否能投资为是
                    entity.setInvestYesorno(DictCodeList.是否_是.getValue());
                    entity.setPortfolioType(DictCodeListExt1.组合类型_子组合.getValue());
                    entity.setParentPortfolioId(portfolioId);
                    entity.setDisPercent(ptlPortfolio.getDisPercent());
                    updatePortfolio(entity);
                }
            }
            //查询是否有子组合，有则逐个修改 add by gejiaao end

        } else if (action.equals("delete")) {
            //删除交易对手信息
            deleteByCounterId(entity);
            restTemplate.postForObject(
                    ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/" + action, entity,
                    boolean.class);



            //如果删除了母组合最后一个子组合，则修改其母组合为可投资
//            if (StringUtil.equals(entity.getPortfolioType(), DictCodeListExt1.组合类型_子组合.getValue())
//                    || !StringUtil.isEmpty(entity.getChildPortfolioId())) {
//                List<PtlManagementInfo004Dto> childPortfolio = getChildPortfolios(entity.getParentPortfolioId());
//                if (childPortfolio.isEmpty()) {
//                    PtlPortfolio ptlPortfolio = new PtlPortfolio();
//                    ptlPortfolio.setPortfolioId(entity.getParentPortfolioId());
//                    ptlPortfolio.setInvestYesorno(DictCodeList.是否_是.getValue());
//                    ptlPortfolioMapper.updateByPrimaryKeySelective(ptlPortfolio);
//                    //获取母组合的信息
//                    PtlPortfolio ptlPortfolioInfo = bussCommonMapperDelegateService.selectBySinglePrimaryKey(PtlPortfolioMapper.class,
//                            entity.getParentPortfolioId(), PtlPortfolio.class);
//                    entity.setPortfolioId(ptlPortfolioInfo.getPortfolioId());
//                    entity.setPortfolioName(ptlPortfolioInfo.getPortfolioName());
//                    //当删除了母组合最后一个子组合 则母组合生成内部交易对手
//                    createCounter(entity);
//                }
//            }
        } else {
            restTemplate.postForObject(
                    ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/" + action, entity,
                    boolean.class);
        }
        // 二级法人权限处理
        if(MultilevelDataAuthorityUtil.isEnabled()) {
            //创建机构代码
            String createInst =   MultilevelDataAuthorityUtil.getService().queryOrganizationCode(entity.getCreateUser());
            entity.setCreateInst(createInst);

            // 有查询权限的机构代码
            List<String> searchOrgCodeList = null;
            // 有编辑权限的机构代码
            List<String> editOrgCodeList = null;
            // 新建或修改时重新设置二级法人权限，删除时则清空
            if (!OperationType.DELETE.equals(type)) {
                //获取所有父级机构。  拥有查看权限
                searchOrgCodeList = com.joyintech.tams.systemtools.util.CommonUtil.getParentInstitution(createInst);
                // 仅资产的创建机构（当前登录用户所在机构）有编辑权限
                editOrgCodeList = Arrays.asList(entity.getCreateInst());
            }
            // 记录二级法人权限
            insertDataAuthTypeInst(entity.getPortfolioId(), searchOrgCodeList, editOrgCodeList,DictCodeList.共通业务类型_组合管理.getValue());
//            insertDataAuthTypeInst(entity.getPortfolioId(), searchOrgCodeList, editOrgCodeList,DictCodeList.共通业务类型_交易对手管理.getValue());
        }
        //----add by 裴家俊 核风险 产品或组合进redis 2020-01-07 -----start----------------
        if (CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)) {
            threadPoolTaskExecutor.execute(()->{
                try {
                    logger.info("===========组合进入redis开始=============");
                    Thread.sleep(6000);
                    ImplCond cond = new ImplCond();
                    cond.setDel(false);
                    cond.setPortfIdArray(entity.getPortfolioId());
                    // 设置平台时间
                    cond.setPlateDate(iPlatformDate.getSysDate());
                    // 更新Redis
                    redisRriter.dealPortData(cond);
                    logger.info("===========组合进入redis结束=============");

                    logger.info("----------->组合关联指标进入redis开始--------------");
                    redisRriter.dealRiskPtlIndicator(cond);
                    logger.info("----------->组合关联指标进入redis结束--------------");
                } catch (InterruptedException e) {
                    logger.error("===========组合进入redis异常=============",e);
                }
            });
        }
        //----add by 裴家俊 核风险 产品或组合进redis 2020-01-07 -----start---------------- 
    }

    /**
     * 生成内部交易对手
     *
     * @param entity
     */
    public void createCounter(PtlManagementInfoDto entity) {
        restTemplate.postForObject(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/createCounter", entity,
                Integer.class);
    }

    public void deleteByCounterId(PtlManagementInfoDto entity) {
        restTemplate.postForObject(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/deleteByCounterId", entity,
                Integer.class);
    }

    private void updatePortfolio(PtlManagementInfoDto entity) {
//        for(FundAccountInfoDto dto : entity.getFundAccountInfoDto()){
//            dto.setPortfolioId(entity.getPortfolioId());
//            FundAccountService.insert(dto);
//        }
//        for(SecuritiesAccountInfoDto dto : entity.getSecuritiesAccountInfoDto()){
//            dto.setPortfolioId(entity.getPortfolioId());
//            securitiesAccountService.insert(dto);
//        }


        //修改母组合权限不联动子组合权限
//        restTemplate.postForObject(
//                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/delete", entity,
//                Integer.class);
        FundAccountService.insertMulti(entity.getFundAccountInfoDto(), entity.getPortfolioId());
        securitiesAccountService.insertMulti(entity.getSecuritiesAccountInfoDto(), entity.getPortfolioId());

         restTemplate.postForObject(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/updateInsertPortfolio", entity,
                boolean.class);
    }

    /**
     * 获取用户下拉框
     *
     * @return 下拉框集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getUserList() {
        return restTemplate.getForList(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/getUserList",
                SelectOptionDto.class);
    }

    public List<SelectOptionDto> getPortfolioId(Map<String, Object> map) {
        return restTemplate.postForList(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/getPortfolioId", map,
                SelectOptionDto.class);
    }

    public List<PtlManagementInfo004Dto> getChildPortfolios(String portfolioId) {
        return restTemplate.getForList(ServiceApplication.PORTFOLIO_SERVICE_NAME +
                "/ptlmanagementinfo/getChildPortfolio?portfolioId=" + portfolioId, PtlManagementInfo004Dto.class);
    }

    /**
     * 获取已分配用户
     *
     * @param portfolioId 组合代码
     * @return 已分配用户集合
     * @see
     * @since 1.0
     */
    public List<PtlManagementInfoDto> selectUserNameList(String portfolioId) {
        Map<String, Object> map = new HashMap<>();
        map.put("portfolioId", portfolioId);
        return restTemplate.postForList(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/selectUserNameList", map,
                PtlManagementInfoDto.class);
    }

    /**
     * 获取初始化界面信息
     *
     * @param map 请求集合
     * @return 组合实体类
     * @see
     * @since 1.0
     */
    public PtlManagementInfoDto getInitInfo(Map<String, Object> map) {
        return restTemplate.postForObject(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/getInitInfo", map,
                PtlManagementInfoDto.class);
    }

    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config(
                "/com/joyintech/tams/invest/portfolio/ptlmanagementinfo/conf/export_conf.xml").dataSource(
                DataSourceType.MASERT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PtlManagementInfoDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }

    /**
     * 设置单元格值
     * <p>
     * 如果单元格值需要特殊处理，比如数值计算等，可以实现该方法
     * </p>
     *
     * @param beanName sheet对应的JavaBean
     * @param cell     单元格
     * @param propName 属性名
     * @param value    单元格的值
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if ("vdate".equals(propName) || "mdate".equals(propName)) {
            cell.setCellValue(DateUtil.format((Date) value, "yyyy-MM-dd"));
        }
    }

    /**
     * 组合删除校验
     * <功能详细描述>
     *
     * @return
     * @see
     * @since 1.0
     */
    public boolean deleteCheck(PtlManagementInfoDto dto) {
        return restTemplate.postForObject(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/deleteCheck", dto,
                boolean.class);
    }

    public AjaxResult checkAddChild(PtlManagementInfoDto dto) {
        AjaxResult ajaxResult = new AjaxResult();
        ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();
        //验证组合是否到期
        if (dto.getMdate().compareTo(DateUtil.platDateFromCache()) < 0) {
            errorList.add(new ValidMessage(getMessage("PTL_COM_E_0024")));
        }
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }

    public List<TreeNode<NodeDto>> getTree(Map<String, Object> map) {

        if(MultilevelDataAuthorityUtil.isEnabled()){



            String userOrgType = LoginUserUtil.getUserOrgType();
            //List<String> instCode = LoginUserUtil.getInstCode();
            String userOrgCode = MultilevelDataAuthorityUtil.getOrganizationCode(LoginUserUtil.getUserName());

            String  portfolioId = (String)map.get("portfolioId");

            List<String> resourceOrgCode = new ArrayList<>();
            //String orgCode = "";
            if(StringUtils.equals(userOrgType,"01") || StringUtils.equals(userOrgType,"02")  ){
                //如果登录用户是省联社用户

                if(StringUtils.isNotEmpty(portfolioId)){
                    // 组合机构id
                    String portfolioOrgCode = MultilevelDataAuthorityUtil.getOrgCodeByBussinessIDType(portfolioId, "02", DictCodeList.共通业务类型_组合管理.getValue());

                    if(StringUtils.isNotEmpty(portfolioOrgCode)){
                        resourceOrgCode = com.joyintech.tams.systemtools.util.CommonUtil.getParentInstitution(portfolioOrgCode);

                        userOrgCode = portfolioOrgCode ;
                    }else{
                        resourceOrgCode = MultilevelDataAuthorityUtil.getChildrenOrgCode(userOrgCode);
                    }

                }else {
                    resourceOrgCode = MultilevelDataAuthorityUtil.getChildrenOrgCode(userOrgCode);
                }
            }else{
                // 如果是市级 或者成员行
                resourceOrgCode = com.joyintech.tams.systemtools.util.CommonUtil.getParentInstitution(userOrgCode);
            }

            map.put("institutionCode",userOrgCode);
            resourceOrgCode.add(userOrgCode);
            map.put("resourcesTreeInstCodeList",resourceOrgCode);

        }


        List<TreeNode<NodeDto>> tree = new ArrayList<>();
        List<NodeDto> inisNodeList = restTemplate.postForList(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/getInstitu", map,
                NodeDto.class);
        List<NodeDto> depNodeList = restTemplate.postForList(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/getDep", map,
                NodeDto.class);
        List<NodeDto> userNodeList = restTemplate.postForList(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/getUser", map,
                NodeDto.class);
        if (inisNodeList != null && inisNodeList.size() > 0) {
            for (NodeDto insNode : inisNodeList) {
                TreeNode<NodeDto> node = new TreeNode<NodeDto>();
                node.setId(insNode.getCode());
                node.setName(insNode.getName());
                if (StringUtil.isNotEmpty(insNode.getUpperCode())) {
                    node.setpId(insNode.getUpperCode());
                }
                node.setParent(true);
                tree.add(node);
            }
        }

        if (depNodeList != null && depNodeList.size() > 0) {
            for (NodeDto depNode : depNodeList) {
                TreeNode<NodeDto> node = new TreeNode<NodeDto>();
                node.setId(depNode.getCode());
                node.setName(depNode.getName());
                if (StringUtil.isNotEmpty(depNode.getUpperCode())) {
                    node.setpId(depNode.getUpperCode());
                }
                node.setParent(true);
                tree.add(node);
            }
        }

        if (userNodeList != null && userNodeList.size() > 0) {
            for (NodeDto userNode : userNodeList) {
                TreeNode<NodeDto> node = new TreeNode<NodeDto>();
                node.setId(userNode.getCode());
                node.setName(userNode.getName());
                if (StringUtil.isNotEmpty(userNode.getUpperCode())) {
                    node.setpId(userNode.getUpperCode());
                }
                node.setParent(false);
                tree.add(node);
            }
        }

        boolean userIdIsEmpty = true;
        String workflowId = (String) map.get(GlobalConstant.WORKFLOW_ID);
        if (!StringUtil.equals(workflowId, "null") && StringUtil.isNotEmpty(workflowId)) {
            PtlManagementInfoDto entity = this.getBussinessEntityByWorkflowId(workflowId);
            if (entity.getUserId() != null) {
                userIdIsEmpty = false;
                for (String userName : entity.getUserId()) {
                    for (TreeNode<NodeDto> treeNode : tree) {
                        if (StringUtil.equals(treeNode.getId(), userName)) {
                            treeNode.setChecked(true);
                        }
                    }
                }
            }
        }

        String portfolioId = (String) map.get("portfolioId");
        // 检索DB中存储的数据权限信息，已经选中的数据权限的check设成ON
        if (userIdIsEmpty && StringUtil.isNotEmpty(portfolioId)) {
            List<PtlManagementInfoDto> ptlManagementInfoDtoList = ptlManagementInfo001Service.selectUserNameList(portfolioId);
            for (PtlManagementInfoDto ptlManagementInfoDto : ptlManagementInfoDtoList) {
                for (TreeNode<NodeDto> treeNode : tree) {
                    if (StringUtil.equals(treeNode.getId(), ptlManagementInfoDto.getPortfolioId())) {
                        treeNode.setChecked(true);
                        while (treeNode != null && StringUtil.isNotEmpty(treeNode.getpId()) && !StringUtil.equals(treeNode.getpId(), treeNode.getId())) {
                            TreeNode<NodeDto> temp = null;
                            for (TreeNode<NodeDto> node : tree) {
                                if (StringUtil.equals(node.getId(), treeNode.getpId())) {
                                    node.setChecked(true);
                                    temp = node;
                                }
                            }
                            treeNode = temp;
                        }
                    }
                }
            }
        }
//        else {
//            for (TreeNode<NodeDto> treeNode : tree) {
//                if (StringUtil.equals(treeNode.getId(), LoginUserUtil.getUserName())) {
//                    treeNode.setChecked(true);
//                    while (treeNode != null && StringUtil.isNotEmpty(treeNode.getpId()) && !StringUtil.equals(treeNode.getpId(), treeNode.getId())) {
//                        TreeNode<NodeDto> temp = null;
//                        for (TreeNode<NodeDto> node : tree) {
//                            if (StringUtil.equals(node.getId(), treeNode.getpId())) {
//                                node.setChecked(true);
//                                temp = node;
//                            }
//                        }
//                        treeNode = temp;
//                    }
//                }
//            }
//        }
        return tree;

    }

    public List<SuggestDto> getStrategy(Map<String, Object> map) {
        return restTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/getStrategy", map, SuggestDto.class);
    }

    public List<PtlManagementInfoDto> setPtlManagementInfoDto(PtlManagementInfoDto dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("selectedItem", dto.getSelectedItem());
        return restTemplate.postForList(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/setParentPortfolioId", map, PtlManagementInfoDto.class);
    }

    public List<SelectOptionDto> selectPortfolioId(Map<String, Object> param) {
        return restTemplate.postForList(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/selectPortfolioId", param, SelectOptionDto.class);
    }
    // add by xingxiaokang 2020-03-09 start 自动生成的组合在流程中增加附件，审批及生效后附件没显示
    @Override
    public void doUpdate(PtlManagementInfoDto entity) {
        //文件信息
        if (CollectionUtils.isNotEmpty(entity.getFileArray())
                && StringUtil.isNotEmpty(entity.getPortfolioId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getFileArray().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),
                    entity.getPortfolioId());
        }
    }
    // add by xingxiaokang 2020-03-09 end

    public int getReAcct(Map<String, Object> param) {
        return restTemplate.postForObject(
                ServiceApplication.ASSET_SERVICE_NAME + "/ptlmanagementinfo/getReAcct", param,Integer.class);    
    }

    /**
     * 接口接入 组合信息
     * @param id
     * @param msgbody
     */
    public void executeInterf(String id,String msgbody) {
        System.out.println("##############################组合信息 接口数据入库start#############################");
        try {
            if(StringUtil.isNotEmpty(msgbody)) {
                PtlManagementInfoDto dto = new PtlManagementInfoDto();
                JSONObject msgJSON = JSONObject.parseObject(msgbody);
                if(StringUtil.equals(id,"REQ00001")) {
                    //取得操作类型：1-新建，2-修改，3-删除
                    String operateType = msgJSON.getString("operate_type");
                    dto.setPortfolioId(msgJSON.getString("portfolio_id"));
                    dto.setPortfolioName(msgJSON.getString("portfolio_name"));
                    dto.setPortfolioType(StringUtil.isNotEmpty(msgJSON.getString("portfolio_type"))?msgJSON.getString("portfolio_type"):DictCodeListExt1.组合类型_投资类组合.getValue());
                    dto.setPortfolioSeries(msgJSON.getString("portfolio_series"));
                    dto.setVdate(msgJSON.getDate("vdate"));
                    dto.setMdate(msgJSON.getDate("mdate"));
                    dto.setProfitType(msgJSON.getString("profit_type"));
                    dto.setOpenType(msgJSON.getString("issue_mode"));
                    dto.setRaiseType(msgJSON.getString("raise_type"));
                    dto.setInvestNature(msgJSON.getString("invest_nature"));

                    if(StringUtil.equals(operateType,"1")) {
                        restTemplate.postForObject(
                                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/insert", dto,
                                boolean.class);
                    } else {
                        restTemplate.postForObject(
                                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/update", dto,
                                boolean.class);
                    }

                    //如果是开房类型为开放式，则需要生成开放计划
                    if(StringUtil.equals(DictCodeList.开放类型_开放式.getValue(),dto.getOpenType())) {
                        handleOpenPlan(dto, msgJSON, StringUtil.equals(operateType,"1")? OperationType.NEW:OperationType.MODIFY);
                    }

                    //----add by 裴家俊 核风险 产品或组合进redis 2020-01-07 -----start----------------
                    if (CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)) {
                        threadPoolTaskExecutor.execute(()->{
                            try {
                                logger.info("===========组合进入redis开始=============");
                                Thread.sleep(6000);
                                ImplCond cond = new ImplCond();
                                cond.setDel(false);
                                cond.setPortfIdArray(msgJSON.getString("portfolio_id"));
                                // 设置平台时间
                                cond.setPlateDate(iPlatformDate.getSysDate());
                                // 更新Redis
                                redisRriter.dealPortData(cond);
                                logger.info("===========组合进入redis结束=============");

                                logger.info("----------->组合关联指标进入redis开始--------------");
                                redisRriter.dealRiskPtlIndicator(cond);
                                logger.info("----------->组合关联指标进入redis结束--------------");
                            } catch (InterruptedException e) {
                                logger.error("===========组合进入redis异常=============",e);
                            }
                        });
                    }
                    //----add by 裴家俊 核风险 产品或组合进redis 2020-01-07 -----start----------------

                    //修改接口服务日志表状态--成功
                    interfServiceLogService.updateInterfServiceLog(id, true,"组合信息接口数据入库成功");
                }
                else if(StringUtil.equals(id,"REQ00001R")) {
                    Map<String ,Object > map = new HashMap<>();
                    map.put("portfolioId",msgJSON.getString("portfolio_id"));
                    dto = ptlManagementInfo001Service.getInitInfo(map);

                    if(StringUtil.isNotEmpty(dto)) {
                        restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/delete", dto,
                                boolean.class);
                    }

                    //如果是开放类型是开放式，则删除现金流信息
                    if(StringUtil.equals(DictCodeList.开放类型_开放式.getValue(),dto.getOpenType())) {
                        handleOpenPlan(dto, msgJSON, OperationType.DELETE);
                    }


                    //修改接口服务日志表状态--成功
                    interfServiceLogService.updateInterfServiceLog(id, true,"组合信息接口数据删除成功");
                }
                else if(StringUtil.equals(id,"REQ00002")) {
                    FundAccountInfoDto fundAccountInfoDto = new FundAccountInfoDto();
                    fundAccountInfoDto.setPortfolioId(msgJSON.getString("portfolio_id"));
                    fundAccountInfoDto.setAcctType(msgJSON.getString("acct_type"));
                    fundAccountInfoDto.setAcctNo(msgJSON.getString("large_payno") + ":"+ msgJSON.getString("acct_no"));
                    fundAccountInfoDto.setBusiAcctNo(msgJSON.getString("acct_no"));
                    fundAccountInfoDto.setCcy(msgJSON.getString("ccy"));
                    fundAccountInfoDto.setAcctName(msgJSON.getString("acct_name"));
                    fundAccountInfoDto.setBankDep(msgJSON.getString("bank_dep"));
                    fundAccountInfoDto.setCorrNo(msgJSON.getString("corr_no"));
                    fundAccountInfoDto.setLargePayno(msgJSON.getString("large_payno"));
                    fundAccountInfoDto.setSwiftCode(msgJSON.getString("swift_code"));
                    fundAccountInfoDto.setBlnArea(msgJSON.getString("bln_area"));
                    fundAccountInfoDto.setInOutBank(msgJSON.getString("in_out_bank"));
                    fundAccountInfoDto.setIsCalint(DictCodeList.是否_否.getValue());
                    fundAccountInfoDto.setAccountNature(DictCodeListExt3.账户性质_银行账户.getValue());

                    //先检查资金账户是否存在，如果不存在就新建资金账户
                    StlCapacctInfo stlCapacctInfo = stlCapacctInfoMapper.selectByPrimaryKey(fundAccountInfoDto);
                    if(!StringUtil.isNotEmpty(stlCapacctInfo)) {
                        stlCapacctInfoMapper.insertSelective(fundAccountInfoDto);

                        StlCapacctReInfo stlCapacctReInfo = new StlCapacctReInfo();
                        BeanUtil.copyPropertiesIgnoreNull(stlCapacctReInfo,fundAccountInfoDto);
                        stlCapacctReInfoMapper.insertSelective(stlCapacctReInfo);
                    }
                    //查询组合关联的资金账户,如果存在就更新，如果不存在就新建
                    PtlCapacctInfo ptlCapacctInfo = new PtlCapacctInfo();
                    BeanUtil.copyPropertiesIgnoreNull(ptlCapacctInfo,fundAccountInfoDto);
                    PtlCapacctInfo oldPtlCapacctInfo = ptlCapacctInfoMapper.selectByPrimaryKey(ptlCapacctInfo);
                    if(!StringUtil.isNotEmpty(oldPtlCapacctInfo)) {
                        ptlCapacctInfoMapper.insertSelective(ptlCapacctInfo);
                    }
                    else {
                        ptlCapacctInfoMapper.deleteByPrimaryKey(oldPtlCapacctInfo);
                        ptlCapacctInfoMapper.insertSelective(ptlCapacctInfo);
                    }

                    //修改接口服务日志表状态--成功
                    interfServiceLogService.updateInterfServiceLog(id, true,"组合信息接口资金账户信息数据入库成功");
                }
                else if(StringUtil.equals(id,"REQ00002R")) {
                    //删除组合资金账户
                    FundAccountInfoDto fundAccountInfoDto = new FundAccountInfoDto();
                    fundAccountInfoDto.setPortfolioId(msgJSON.getString("portfolio_id"));
                    fundAccountInfoDto.setAcctType(msgJSON.getString("acct_type"));
                    fundAccountInfoDto.setAcctNo(msgJSON.getString("large_payno") + ":"+ msgJSON.getString("acct_no"));
                    fundAccountInfoDto.setCcy(msgJSON.getString("ccy"));

                    PtlCapacctInfo ptlCapacctInfoDto = new PtlCapacctInfo();
                    BeanUtil.copyPropertiesIgnoreNull(ptlCapacctInfoDto,fundAccountInfoDto);
                    ptlCapacctInfoMapper.deleteByPrimaryKey(ptlCapacctInfoDto);
                }
                else if(StringUtil.equals(id,"REQ00003")) {
                    SecuritiesAccountInfoDto securitiesAccountInfoDto = new SecuritiesAccountInfoDto();
                    securitiesAccountInfoDto.setPortfolioId(msgJSON.getString("portfolio_id"));
                    securitiesAccountInfoDto.setSecAcctType(msgJSON.getString("acct_type"));
                    securitiesAccountInfoDto.setSecAcctId(msgJSON.getString("acct_no"));
                    securitiesAccountInfoDto.setSecAcctName(msgJSON.getString("acct_name"));
                    securitiesAccountInfoDto.setRegistOrg(msgJSON.getString("regist_org"));
                    securitiesAccountInfoDto.setTradePlace(msgJSON.getString("trade_market"));
                    securitiesAccountInfoDto.setBondId(msgJSON.getString("holder_id"));
                    securitiesAccountInfoDto.setBrokerName(msgJSON.getString("broker_id"));
                    securitiesAccountInfoDto.setTrdAcctId(msgJSON.getString("trd_acct_id"));
                    securitiesAccountInfoDto.setTrdAcctCcy(msgJSON.getString("trd_acct_ccy"));
                    securitiesAccountInfoDto.setSecAcctSource(DictCodeListExt1.账户来源_理财.getValue());

                    //判断证券账户存不存在，如果不存在则新建
                    StlSecAcctInfo stlSecAcctInfo = stlSecAcctInfoMapper.selectByPrimaryKey(securitiesAccountInfoDto.getSecAcctId());
                    if(!StringUtil.isNotEmpty(stlSecAcctInfo)) {
                        stlSecAcctInfoMapper.insertSelective(securitiesAccountInfoDto);
                    }

                    //判断组合证券账户存不存在，如果存在则更新，如果不存在则新建
                    PtlSecAcct ptlSecAcct = new PtlSecAcct();
                    ptlSecAcct.setPortfolioId(securitiesAccountInfoDto.getPortfolioId());
                    ptlSecAcct.setAcctNo(securitiesAccountInfoDto.getSecAcctId());
                    PtlSecAcct oldPtlSecAcct = ptlSecAcctMapper.selectByPrimaryKey(ptlSecAcct);
                    if(!StringUtil.isNotEmpty(oldPtlSecAcct)) {
                        ptlSecAcctMapper.insertSelective(ptlSecAcct);
                    }
                    else {
                        ptlSecAcctMapper.deleteByPrimaryKey(oldPtlSecAcct);
                        ptlSecAcctMapper.insertSelective(ptlSecAcct);
                    }
                    //修改接口服务日志表状态--成功
                    interfServiceLogService.updateInterfServiceLog(id, true,"组合信息接口证券账户信息数据入库成功");
                }
                else if(StringUtil.equals(id,"REQ00003R")) {
                    SecuritiesAccountInfoDto securitiesAccountInfoDto = new SecuritiesAccountInfoDto();
                    securitiesAccountInfoDto.setPortfolioId(msgJSON.getString("portfolio_id"));
                    securitiesAccountInfoDto.setSecAcctType(msgJSON.getString("acct_type"));
                    securitiesAccountInfoDto.setSecAcctId(msgJSON.getString("acct_no"));

                    PtlSecAcct ptlSecAcct = new PtlSecAcct();
                    ptlSecAcct.setPortfolioId(securitiesAccountInfoDto.getPortfolioId());
                    ptlSecAcct.setAcctNo(securitiesAccountInfoDto.getSecAcctId());
                    ptlSecAcctMapper.deleteByPrimaryKey(ptlSecAcct);
                }

                //每次接口结束前需要往数据留痕表中插入一条有效的流程数据,如果组合信息还没有接进来，则不需要处理
                Map<String,Object> map = new HashMap<>();
                map.put("portfolioId",msgJSON.getString("portfolio_id"));
                PtlManagementInfoDto ptlInfo = this.getInitInfo(map);
                if(StringUtil.isNotEmpty(ptlInfo)) {
                    //插入留痕表数据操作
                }
            }
            else {
                //修改接口服务日志表状态--失败
                interfServiceLogService.updateInterfServiceLog(id, false, "组合信息接口数据入库失败，接口数据为空");
            }


        } catch (Exception e) {
            //修改接口服务日志表状态--失败
            interfServiceLogService.updateInterfServiceLog(id, false, e.getMessage());
        }

        System.out.println("##############################组合信息 接口数据入库end#############################");
    }

    private void handleOpenPlan(PtlManagementInfoDto dto,JSONObject msgJSON, OperationType operationType) {
        if(StringUtil.equals(operationType,OperationType.NEW)) {
            String cashId = CommonUtil.generateCashFlowCode(dto.getPortfolioId(),
                    DictCodeListExt1.现金流业务类型_投资组合.getValue(), 0L,
                    DictCodeListExt1.现金流二级类型_本金.getValue());
            //本金的现金流信息
            FinCashInfo finCashInfo = new FinCashInfo();
            finCashInfo.setCashId(cashId);
            finCashInfo.setBranch(0L);
            finCashInfo.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
            finCashInfo.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
            finCashInfo.setCcy(dto.getCcy());
            finCashInfo.setFinprodId(dto.getPortfolioId());
            finCashInfo.setFinprodType(DictCodeListExt1.现金流业务类型_投资组合.getValue());
            finCashInfo.setIsCalc(DictCodeList.是否_是.getValue());
            finCashInfoMapper.insert(finCashInfo);

            //开放规则

//                    dto.setPortfolioName(msgJSON.getString("risk_level"));
//                    dto.setPortfolioSeries(msgJSON.getString("open_type"));
//                    dto.setVdate(msgJSON.getDate("open_mode"));
//                    dto.setMdate(msgJSON.getDate("open_days"));
//                    dto.setProfitType(msgJSON.getString("open_cycle"));
//                    dto.setOpenType(msgJSON.getString("open_cycle_unit"));
//                    dto.setOpenType(msgJSON.getString("open_days_type"));
            FinOpenRule finOpenRule = new FinOpenRule();
            finOpenRule.setCashId(cashId);
            finOpenRule.setOpenType(msgJSON.getString("open_type"));
            finOpenRule.setOpenMode(msgJSON.getString("open_mode"));
            finOpenRule.setOpenDays(msgJSON.getLong("open_days"));
            finOpenRule.setOpenCycle(msgJSON.getLong("open_cycle"));
            finOpenRule.setOpenCycleUnit(msgJSON.getString("open_cycle_unit"));
            finOpenRule.setOpenDaysType(msgJSON.getString("open_days_type"));
            finOpenRule.setCalcMethod(DictCodeList.推算方式_起息日向后.getValue());
            finOpenRule.setTradedayRule(DictCodeList.营业日准则_向后调整.getValue());
        }
        else if (StringUtil.equals(operationType,OperationType.MODIFY)) {

        }
        else if (StringUtil.equals(operationType,OperationType.DELETE)) {

        }
    }
}
