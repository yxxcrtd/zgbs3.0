package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.business.common.entity.CfgDataTableShow;
import com.joyintech.tams.business.common.entity.CfgReadonlyFormShow;
import com.joyintech.tams.business.common.service.CfgDataTableService;
import com.joyintech.tams.business.common.service.CfgReadonlyFormService;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.util.BigDecimalUtil;
import com.joyintech.tams.cfets.etrdispatcher.service.CfetsEtrDispatcherService;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondEtrMapper;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondRegCheckRequst;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dao.CfetsTrdBondFirstSub001Mapper;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.*;
import com.joyintech.tams.common.base.dao.EtrDistributeMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.WflEtrEntrustMapper;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrDistributeInterfTmp;
import com.joyintech.tams.common.base.entity.EtrDistributeKey;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.invest.common.component.base.entrust.impl.BaseEtrInvestInfoServiceImpl;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 一级申购SERVICE
 *
 * @author jp
 * @date 2022/04/18
 */
@Service
public class CfetsTrdBondFirstSub001Service extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(CfetsTrdBondFirstSub001Service.class);

    @Autowired
    private CfetsTrdBondFirstSub001Mapper cfetsTrdBondFirstSub001Mapper;

    @Autowired
    private CfetsIbuQuoteBondEtrMapper cfetsIbuQuoteBondEtrMapper;
    /**
     * 指令信息
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;
    /**
     * 指令信息(流程中)
     */
    @Autowired
    private WflEtrEntrustMapper wflEtrEntrustMapper;

    @Autowired
    private EtrDistributeMapper etrDistributeMapper;
    /**
     * 平台日期组件接口，提供平台日期的获取和更新
     */
    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * 投资委托信息Service
     */
    @Autowired
    private BaseEtrInvestInfoServiceImpl etrInvestInfoServiceImpl;
    /**
     * 场外指令分发
     */
    @Autowired
    private CfetsEtrDispatcherService cfetsEtrDispatcherService;

    /**
     * 投资委托委托组件
     */
    @Autowired(required = false)
    private BaseEtrInvestInfoServiceImpl etrInvesInfo;
    /**
     * 公共方法service
     */
    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    /**
     * 页面只读表单展示配置service
     */
    @Autowired
    private CfgReadonlyFormService cfgReadonlyFormService;

    /**
     * 页面展示设置service
     */
    @Autowired
    private CfgDataTableService cfgDataTableService;

    /**
     * 页面设置pageid
     */
    private static final String pageCode = "TenderBondSub";

    /**
     * 页面设置区域id-表格
     */
    private static final String dtCode = "ETRDETINFOLIST";

    /**
     * 页面设置区域id-表单
     */
    private static final String formDtCode = "ETRDETINFO";

    /**
     * 通用设置 默认999999
     */
    private static final String COMM = "999999";

    /**
     * 查询指令列表
     *
     * @param dto         入参 画面元素
     * @param loginUserId 登录用户
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.bond.dto.EntrustDto>
     * @author jiangpeng
     * @date 2022/4/18 15:01
     */
    public List<CfetsTrdBondFirstSubEntrustDto> listEntrust(CfetsTrdBondFirstSubEntrustRequest dto, String loginUserId) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        this.setParam(pageParam);
        logger.debug("查询指令,入参:" + JsonUtils.object2Json(pageParam));
        return cfetsTrdBondFirstSub001Mapper.listEntrust(pageParam);
    }

    /**
     * 查询指令列表
     *
     * @param pageParam 入参
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.bond.dto.EntrustDto>
     * @author jiangpeng
     * @date 2022/4/18 15:01
     */
    public List<CfetsTrdBondFirstSubEntrustDto> listEntrust(Map<String, Object> pageParam) {
        logger.debug("查询指令,入参:" + JsonUtils.object2Json(pageParam));
        this.setParam(pageParam);
        return cfetsTrdBondFirstSub001Mapper.listEntrust(pageParam);
    }

    /**
     * 初始化指令列表(分页)
     *
     * @param dto 入参 查询条件
     * @return com.joyintech.framework.grid.Page<com.joyintech.tams.cfets.finbond.tenderBondSub.dto.TenderBondSubEntrustDto>
     * @author jiangpeng
     * @date 2022/4/19 14:16
     */
    public Page<CfetsTrdBondFirstSubEntrustDto> pageEntrust(CfetsTrdBondFirstSubEntrustRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        this.setParam(pageParam);
        PageMode pageMode = this.setPageMode(dto.getPageMode());
        logger.debug("查询指令,入参:" + JsonUtils.object2Json(pageParam));
        return super.queryForPage(pageMode.getPageNo(), pageMode.getPageSize(), () -> cfetsTrdBondFirstSub001Mapper.listEntrust(pageParam));
    }

    /**
     * 指令查询明细(分页) 查询已接收的指令数据
     *
     * @param dto 入参 查询条件
     * @return com.joyintech.framework.grid.Page<com.joyintech.tams.cfets.finbond.tenderBondSub.dto.TenderBondSubEntrustDto>
     * @author jiangpeng
     * @date 2022/4/19 14:16
     */
    public Page<CfetsTrdBondFirstSubEntrustDto> etrAddSearch(CfetsTrdBondFirstSubEntrustRequest request) {
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        this.setParam(pageParam);
        pageParam.put("entrustStatus", Collections.singletonList(DictCodeList.委托状态_已接收.getValue()));
        PageMode pageMode = this.setPageMode(request.getPageMode());
        logger.debug("查询指令,入参:" + JsonUtils.object2Json(pageParam));
        return super.queryForPage(pageMode.getPageNo(), pageMode.getPageSize(), () -> cfetsTrdBondFirstSub001Mapper.listEntrust(pageParam));
    }

    /**
     * 接收指令
     *
     * @param param 指令代码列表等
     * @return java.util.List<com.joyintech.framework.validation.ValidMessage>
     * @author jiangpeng
     * @date 2022/4/18 15:03
     */
    public List<ValidMessage> receiveEntrust(Map<String, Object> param) {
        List<ValidMessage> errorList = new ArrayList<>();
        param.put("trader", LoginUserUtil.getUserName());
        List<EtrDistribute> list = this.getDistributeTrader(param);
        logger.debug("接收指令,入参:" + JsonUtils.object2Json(param));
        List<String> traderList = new ArrayList<>();
        for (EtrDistribute etrDis : list) {
            errorList.addAll(etrInvesInfo.checkDelegateOperation(etrDis.getEntrustId(), DictCodeListExt1.委托操作类型_接收.getValue(), traderList, DictCodeListExt1.委托关闭方式_人工.getValue()));
            etrDis.setUpdateUser(etrDis.getTrader());
        }
        //错误时，返回错误信息
        if (CollectionUtils.isNotEmpty(errorList) && errorList.size() > 0) {
            return errorList;
        }
        // 接受委托
        etrInvesInfo.receptionDelegate(list);
        List<String> entrustIds = StringUtil.splitToList(",", (String) param.get("entrustIds"));
        for (String entrustId : entrustIds) {
            //更新操作记录日志
            CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
            oprHisReq.setEntrustId(entrustId);
            oprHisReq.setOperType(DictCodeListExt3.操作类型_接收.getValue());
            joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
        }
        return errorList;
    }

    /**
     * 获取指令
     *
     * @param param
     * @return java.util.List<com.joyintech.tams.common.base.entity.EtrDistribute>
     * @author jiangpeng
     * @date 2022/4/18 15:05
     */
    public List<EtrDistribute> getDistributeTrader(Map<String, Object> param) {
        List<EtrDistribute> list = new ArrayList<>();
        EtrDistributeKey etrKey = new EtrDistributeKey();
        etrKey.setTrader((String) param.get("trader"));
        List<String> entrustIds = StringUtil.splitToList(",", (String) param.get("entrustIds"));
        for (String entrustId : entrustIds) {
            etrKey.setEntrustId(entrustId);
            list.add(etrDistributeMapper.selectByPrimaryKey(etrKey));
        }
        return list;
    }

    /**
     * 指令放弃
     *
     * @param entrustId 指令代码
     * @return java.lang.String
     * @author jiangpeng
     * @date 2022/4/21 14:29
     */
    public String giveUpEntrust(String entrustId) {
        logger.debug("指令放弃,指令代码:" + entrustId);
        String massege = "";
        if (StringUtil.isEmpty(entrustId)) {
            massege = "请选择指令！";
        }
        // 1.查询指令信息
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entrustId);
        if (etrEntrust == null) {
            massege = "未查询到指令信息！";
        }

        Map<String, Object> params = new HashMap<>();
        params.put("entrustId", entrustId);
        setParam(params);
        // 2.查询指令状态及执行进度
        List<CfetsTrdBondFirstSubEntrustDto> cfetsTrdBondFirstSubEntrustDtos = cfetsTrdBondFirstSub001Mapper.listEntrust(params);
        logger.debug("查询指令状态及执行进度,结果:" + JsonUtils.object2Json(cfetsTrdBondFirstSubEntrustDtos));
        // 已接收且执行进度<100%的指令可以选择【放弃】接收
        if (CollectionUtils.isNotEmpty(cfetsTrdBondFirstSubEntrustDtos)) {
            for (CfetsTrdBondFirstSubEntrustDto item : cfetsTrdBondFirstSubEntrustDtos) {
                if (!StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_已接收.getValue())
                        && BigDecimalUtil.ACompareToB(item.getTransactioncom(), new BigDecimal(100), 2) >= 0) {
                    return "已接收且执行进度<100%的指令,才能执行放弃操作！";
                }
            }
        }
        //是否存在流程中或者有效且数据状态不为 已撤回 已终止
        int i = cfetsTrdBondFirstSub001Mapper.checkDataStatus(params);
        if (i > 0) {
            return "存在流程中的申购，不可放弃！";
        }
        //2.查询指令分发的临时表数据  插入  指令分发表，并将状态改为  待接收。
        List<EtrDistributeInterfTmp> distmpList = cfetsIbuQuoteBondEtrMapper.listEtrDistributeInterfTmp(params);
        if (!StringUtil.isEmptyList(distmpList)) {
            boolean status = etrInvestInfoServiceImpl.updateDelegateStatus(entrustId, DictCodeList.委托状态_待接收.getValue());
            List<EtrDistribute> disList = new ArrayList<>();
            for (EtrDistributeInterfTmp entity : distmpList) {
                EtrDistribute dis = new EtrDistribute();
                dis.setEntrustId(entity.getEntrustId());
                dis.setTrader(entity.getTrader());
                dis.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                dis.setTradingHeader(entity.getTradingHeader());
                disList.add(dis);
            }
            this.etrInvestInfoServiceImpl.distributeDelegate(disList);
        } else {
            boolean status = etrInvestInfoServiceImpl.updateDelegateStatus(entrustId, DictCodeListExt3.委托状态_待分发.getValue());
        }
        // 更新履历在AOP中
        return null;
    }


    /**
     * 退回指令 check
     *
     * @param param 退户原因、指令代码等
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2022/4/21 14:29
     */
    public AjaxResult entrustRefuceCheck(Map<String, Object> param) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        if (!StringUtil.isNotEmpty(param.get("remark"))) {
            errorList.add(new ValidMessage("请输入退回原因!"));
        }
        this.setParam(param);
        // 2.查询指令状态及执行进度
        List<CfetsTrdBondFirstSubEntrustDto> cfetsTrdBondFirstSubEntrustDtos = cfetsTrdBondFirstSub001Mapper.listEntrust(param);
        if (CollectionUtils.isNotEmpty(cfetsTrdBondFirstSubEntrustDtos)) {
            cfetsTrdBondFirstSubEntrustDtos.forEach(item -> {
                if (StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_待接收.getValue())
                        || (StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_待接收.getValue()) && item.getTransactioncom().compareTo(BigDecimal.ZERO) != 0)) {
                    errorList.add(new ValidMessage("只可对待分发或者已接收执行进度为0的指令执行退回操作!"));
                }
                param.put("entrustId", item.getEntrustId());
                if (cfetsTrdBondFirstSub001Mapper.checkDataStatus(param) > 0) {
                    errorList.add(new ValidMessage("存在流程中的交易数据，不可退回!"));
                }
            });
        }
        /*if(cfetsIbuQuoteBondEtrMapper.countNotDjsEtr(param)>0){
            errorList.add(new ValidMessage("只可对待分发指令执行退回操作!"));
        }*/
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }

    /**
     * 指令退回
     *
     * @param param
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2022/4/21 14:28
     */
    public AjaxResult entrustRefuce(Map<String, Object> param) {
        param.put("rekReason", (String) param.get("remark"));
        return cfetsEtrDispatcherService.refuseEntrust(param);
    }

    /**
     * 获取指令详细信息
     *
     * @param entrustId 指令代码
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author jiangpeng
     * @date 2022/4/21 14:27
     */
    public Map<String, Object> getEntrustInfo(String entrustId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        logger.debug("获取指令详细信息,指令代码:" + entrustId);
        param.put("entrustId", entrustId);
        List<CfetsTrdBondFirstSubEntrustDetailDto> formData = cfetsTrdBondFirstSub001Mapper.getEntrustInfo(param);
        List<CfetsTrdBondFirstSubEntrustProductDto> productDtos = new ArrayList<>();
        logger.debug("获取指令详细信息,查询数据:" + JsonUtils.object2Json(formData));
        // 表单格式
        result.put("form", getReadonlyForms(new ArrayList<>()));
        if (StringUtil.isNotEmpty(formData)) {
            // 表单数据
            result.put("formData", formData.get(0));
            productDtos = formData.get(0).getProductDtos();
        }
        // 表格格式
        result.put("table", getBySpcdCodes(new ArrayList<>()));
        // 表格数据
        if (CollectionUtils.isNotEmpty(productDtos)) {
            int index = 0;
            BigDecimal sumAmt = BigDecimal.ZERO;
            for (CfetsTrdBondFirstSubEntrustProductDto dto : productDtos) {
                index++;
                dto.setIndex(index);
                sumAmt = ArithmeticUtil.doAdd(sumAmt, dto.getBidPrinAmt());
            }
            // 表单数据
            result.put("tableData", productDtos);
            // 汇总投标券面总额
            result.put("sumAmt", sumAmt);
        }
        logger.debug("获取指令详细信息,结果:" + JsonUtils.object2Json(result));
        return result;
    }


    /**
     * 当日申购(分页列表)
     *
     * @param dto 入参 查询条件
     * @return com.joyintech.framework.grid.Page<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstDaySubDto>
     * @author jiangpeng
     * @date 2022/4/21 14:28
     */
    public Page<CfetsTrdBondFirstDaySubDto> pageDaySub(CfetsTrdBondFirstDaySubRequest dto) {
        logger.debug("当日申购,入参:" + JsonUtils.object2Json(dto));
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("loginUserId", LoginUserUtil.getUserName());
        pageParam.put("sysDate", iPlatformDate.getSysDateStr());
        this.setParam(pageParam);
        PageMode pageMode = this.setPageMode(dto.getPageMode());
        return super.queryForPage(pageMode.getPageNo(), pageMode.getPageSize(), () -> cfetsTrdBondFirstSub001Mapper.listDaySub(pageParam));
    }

    /**
     * 当日申购(列表) 导出用
     *
     * @param pageParam 入参 查询条件
     * @return java.util.List<com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDayRegDto>
     * @author jiangpeng
     * @date 2022/4/21 14:28
     */
    public List<CfetsTrdBondFirstDaySubDto> listDaySub(Map<String, Object> pageParam) {
        logger.debug("当日申购,入参:" + JsonUtils.object2Json(pageParam));
        pageParam.put("loginUserId", LoginUserUtil.getUserName());
        pageParam.put("platDate", iPlatformDate.getSysDateStr());
        pageParam.put("OPR_TYPE_09", DictCodeListExt3.操作类型_导出.getValue());
        this.setParam(pageParam);
        pageParam.put("CONFIRM_STATUS_0", com.joyintech.tams.constant.DictCodeList.确认状态_未确认.getValue());
        return cfetsTrdBondFirstSub001Mapper.listDaySub(pageParam);
    }

    /**
     * 申购明细
     *
     * @param request
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto>
     * @author jiangpeng
     * @date 2022/4/26 19:30
     */
    public List<CfetsTrdBondFirstSubDealDetailDto> listDaySubDetail(CfetsTrdBondFirstDaySubRequest request) {
        logger.debug("申购明细,入参:" + JsonUtils.object2Json(request));
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        return cfetsTrdBondFirstSub001Mapper.listDaySubDetail(pageParam);
    }

    /**
     * 申购明细
     *
     * @param request
     * @return com.joyintech.framework.grid.Page<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto>
     * @author jiangpeng
     * @date 2022/4/26 19:37
     */
    public Page<CfetsTrdBondFirstSubDealDetailDto> pageDaySubDetail(CfetsTrdBondFirstDaySubRequest request) {
        logger.debug("申购明细,入参:" + JsonUtils.object2Json(request));
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        PageMode pageMode = this.setPageMode(request.getPageMode());
        return super.queryForPage(pageMode.getPageNo(), pageMode.getPageSize(), () -> cfetsTrdBondFirstSub001Mapper.listDaySubDetail(pageParam));
    }

    /**
     * 申购执行明细
     *
     * @param request
     * @return com.joyintech.framework.grid.Page<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubDealDetailDto>
     * @author jiangpeng
     * @date 2022/4/26 19:36
     */
    public Page<CfetsTrdBondFirstSubDealDetailDto> pageDaySubDealDetail(CfetsTrdBondFirstDaySubRequest request) {
        logger.debug("申购执行明细,入参:" + JsonUtils.object2Json(request));
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        PageMode pageMode = this.setPageMode(request.getPageMode());
        return super.queryForPage(pageMode.getPageNo(), pageMode.getPageSize(), () -> cfetsTrdBondFirstSub001Mapper.listDaySubDealDetail(pageParam));
    }

    /**
     * 组装输入参数
     *
     * @param param 参数map集
     * @author jiangpeng
     * @date 2022/4/19 14:21
     */
    private void setParam(Map<String, Object> param) {
        param.put("ISSUE_CLASS_01", DictCodeListExt4.新债_发行方式_报价发行.getValue());
        param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        param.put("YN_N", DictCodeListExt1.是否_否.getValue());
        param.put("entrustIdList",param.get("entrustIds"));
        String loginUserId = LoginUserUtil.getUserName();
        // 测试代码
        if ("admin".equals(loginUserId)||StringUtil.isNotEmpty(loginUserId)) {
            loginUserId = "jp";
        }
        param.put("loginUserId", loginUserId);
    }

    /**
     * 分页设置默认值
     *
     * @param pageMode 页面信息
     * @author jiangpeng
     * @date 2022/4/19 14:22
     */
    private PageMode setPageMode(PageMode pageMode) {
        if (pageMode == null) {
            pageMode = new PageMode();
        }
        int pageNo = StringUtil.isNotEmpty(pageMode.getPageNo()) && pageMode.getPageNo() != 0 ? pageMode.getPageNo() : 1;//默认第一页
        int pageSize = StringUtil.isNotEmpty(pageMode.getPageSize()) && pageMode.getPageSize() != 0 ? pageMode.getPageSize() : 10;//默认每页10条
        pageMode.setPageNo(pageNo);
        pageMode.setPageSize(pageSize);
        return pageMode;
    }

    /**
     * 查询表单表头 (私有方法 pageCode,dtCode,spcdCodeList仅组装该页面的数据)
     *
     * @return java.util.List<com.joyintech.tams.business.common.entity.CfgReadonlyFormShow>
     * @author jiangpeng
     * @date 2022/4/21 14:54
     */
    private List<CfgReadonlyFormShow> getReadonlyForms(List<String> spcdCodeList) {
        //页面id
        String pageCode = this.pageCode;
        //区域id
        String dtCode = this.formDtCode;
        if (CollectionUtils.isEmpty(spcdCodeList)) {
            spcdCodeList = new ArrayList<>();
        }
        spcdCodeList.add(COMM);
        List<CfgReadonlyFormShow> cfgReadonlyFormShows = cfgReadonlyFormService.getReadonlyForms(pageCode, dtCode, spcdCodeList);
        return cfgReadonlyFormShows;
    }

    /**
     * 查询表单表头 (私有方法 pageCode,dtCode,spcdCodeList仅组装该页面的数据)
     *
     * @return java.util.List<com.joyintech.tams.business.common.entity.CfgReadonlyFormShow>
     * @author jiangpeng
     * @date 2022/4/21 14:54
     */
    private List<CfgDataTableShow> getBySpcdCodes(List<String> spcdCodeList) {
        //页面id
        String pageCode = this.pageCode;
        //区域id
        String dtCode = this.dtCode;
        if (spcdCodeList == null) {
            spcdCodeList = new ArrayList<>();
        }
        spcdCodeList.add(COMM);//通用配置
        List<CfgDataTableShow> infoForm = cfgDataTableService.getByPageCodeAndDtCodes(pageCode, dtCode, spcdCodeList);
        return infoForm;
    }
}

