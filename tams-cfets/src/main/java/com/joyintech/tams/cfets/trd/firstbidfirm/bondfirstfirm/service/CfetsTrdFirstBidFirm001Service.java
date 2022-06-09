package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.entity.CfgDataTableShow;
import com.joyintech.tams.business.common.entity.CfgReadonlyFormShow;
import com.joyintech.tams.business.common.service.CfgDataTableService;
import com.joyintech.tams.business.common.service.CfgReadonlyFormService;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.util.BigDecimalUtil;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsBussCombDistribtBaseDto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbdoRiskcalDto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbdoRiskcalRequest;
import com.joyintech.tams.cfets.etrdispatcher.service.CfetsEtrDispatcherService;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondEtrMapper;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dao.CfetsTrdFirstBidFirm001Mapper;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdBondFirsBidFirmBidOrderDetailDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdBondFirsBidFirmDetailDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdBondFirstBidFirmEntrustRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdBondFirstBidFirmEtrRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdBondFirstBidFirmProductDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdBondFirstBidFirmUndoSumRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmDistributionInfoDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmDistributionInfoRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmEntrustDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmTenderInfoDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmTenderInfoRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.service.CfetsTrdBondFirstSub001Service;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.TotalEtrEntrustRelMapper;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrDistributeInterfTmp;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.TotalEtrEntrustRel;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.invest.common.component.base.entrust.impl.BaseEtrInvestInfoServiceImpl;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 一级投标确认
 *
 * @Author: kchen
 * @Date: 2022/5/14 11:14
 */
@Service("CfetsTrdFirstBidFirm001Service")
public class CfetsTrdFirstBidFirm001Service extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(CfetsTrdFirstBidFirm001Service.class);

    /**
     * 投资委托委托组件
     */
    @Autowired(required = false)
    private BaseEtrInvestInfoServiceImpl etrInvesInfo;

    @Autowired
    private CfetsTrdBondFirstSub001Service cfetsTrdBondFirstSub001Service;

    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private BaseEtrInvestInfoServiceImpl etrInvestInfoServiceImpl;

    @Autowired
    private CfetsIbuQuoteBondEtrMapper cfetsIbuQuoteBondEtrMapper;

    @Autowired
    private CfetsTrdFirstBidFirm001Mapper cfetsTrdFirstBidFirm001Mapper;

    @Autowired
    private CfetsEtrDispatcherService cfetsEtrDispatcherService;

    @Autowired
    private TotalEtrEntrustRelMapper totalEtrEntrustRelMapper;

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

    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 页面设置pageid
     */
    private static final String PAGECODE = "TenderBondFirm";

    /**
     * 指令信息
     * 页面设置区域id-表格
     */
    private static final String TABLE_DTCODE_ETR = "ETRDETINFOLIST";

    /**
     * 指令信息
     * 页面设置区域id-表单
     */
    private static final String FORM_DTCODE_ETR = "ETRDETINFO";

    /**
     * 投标确认执行明细
     * 页面设置区域id-表格
     */
    private static final String TABLE_DTCODE_BID = "BIDORDERTRANSLIST";


    /**
     * 通用设置 默认999999
     */
    private static final String COMM = "999999";

    /**
     * 汇总标识 01 取消汇总指令
     */
    private static final String SUM_FLAG_01 = "01";

    /**
     * 汇总标识 02 表示一条有交易对手的指令与所有没有交易对手的指令进行汇总
     */
    private static final String SUM_FLAG_02 = "02";

    /**
     * 指令拖出 取消汇总
     */
    private static final String OPERATE_SIDE_OUT = "01";

    /**
     * 指令拖入
     */
    private static final String OPERATE_SIDE_IN = "02";

    /**
     * 接收指令检查
     * @param requestList
     * @return
     */
    public AjaxResult receiveEntrustCheck(List<CfetsTrdBondFirstBidFirmEtrRequest> requestList) {
        AjaxResult ajaxResult = new AjaxResult();
        if (CollectionUtils.isEmpty(requestList)) {
            return ajaxResult;
        }
        List<String> errorList = new ArrayList<String>();
        List<String> entrustIdListAll = new ArrayList<>();
        Map<String, String> finprodAbbrMap = requestList.stream().collect(Collectors.toMap(CfetsTrdBondFirstBidFirmEtrRequest::getFinprodId,CfetsTrdBondFirstBidFirmEtrRequest::getFinprodAbbr,(v1, v2) ->v2));
        Map<String,List<CfetsTrdBondFirstBidFirmEtrRequest>> finprodIdMap = requestList.stream().collect(Collectors.groupingBy(o -> o.getFinprodId()));
        for (Map.Entry<String, List<CfetsTrdBondFirstBidFirmEtrRequest>> entry : finprodIdMap.entrySet()) {
            List<String> entrustIdListOld = entry.getValue().stream().map(CfetsTrdBondFirstBidFirmEtrRequest::getEntrustId).collect(Collectors.toList());
            //查询
            Map<String, Object> param = new HashMap<>();
            this.setParam(param);
            param.put("finprodId",entry.getKey());
            //筛选出未接收的指令
            List<String> entrustIdListNew = cfetsTrdFirstBidFirm001Mapper.listEntrust(param).stream()
                    .filter(item ->DictCodeList.委托状态_待接收.getValue().equals(item.getEntrustStatus())).map(CfetsTrdFirstBidFirmEntrustDto::getEntrustId).collect(Collectors.toList());
            Collections.sort(entrustIdListOld);
            Collections.sort(entrustIdListNew);
            if (!StringUtil.equals(entrustIdListOld.toString(),entrustIdListNew.toString())) {
                errorList.add("存在同一投标日资产["+finprodAbbrMap.get(entry.getKey())+"]的其他未接收指令，是否全部接收？");
                entrustIdListAll.addAll(entrustIdListNew);
            }

        }

        Map<String, Object> map = new HashMap<>();
        map.put("massege", errorList);
        map.put("ids", entrustIdListAll);
        ajaxResult.setData(map);
        ajaxResult.setMessageType(MessageType.确认消息);
        return ajaxResult;
    }

    /**
     * 接收指令
     *
     * @param param
     * @return
     */
    public List<ValidMessage> receiveEntrust(Map<String, Object> param) {
        List<ValidMessage> errorList = new ArrayList<>();
        param.put("trader", LoginUserUtil.getUserName());
        List<EtrDistribute> list = cfetsTrdBondFirstSub001Service.getDistributeTrader(param);
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
     * 指令放弃
     *
     * @param entrustId
     * @return
     */
    public String giveUpEntrust(String entrustId) {
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
        List<CfetsTrdFirstBidFirmEntrustDto> cfetsTrdFirstBidFirmEntrustDtoList = cfetsTrdFirstBidFirm001Mapper.listEntrust(params);
        logger.debug("查询指令状态及执行进度,结果:" + JsonUtils.object2Json(cfetsTrdFirstBidFirmEntrustDtoList));
        // 已接收且执行进度<100%的指令可以选择【放弃】接收
        if (CollectionUtils.isNotEmpty(cfetsTrdFirstBidFirmEntrustDtoList)) {
            for (CfetsTrdFirstBidFirmEntrustDto item : cfetsTrdFirstBidFirmEntrustDtoList) {
                if (!StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_已接收.getValue())
                        && BigDecimalUtil.ACompareToB(item.getTransactioncom(), new BigDecimal(100), 2) >= 0) {
                    return "已接收且执行进度<100%的指令,才能执行放弃操作！";
                }
            }
        }
        //是否存在流程中或者有效且数据状态不为 已撤回 已终止
        int i = cfetsTrdFirstBidFirm001Mapper.checkDataStatus(params);
        if (i > 0) {
            return "存在流程中的一级投标确认，不可放弃！";
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

        return massege;
    }

    /**
     * 接收指令检查
     * @param requestList
     * @return
     */
    public AjaxResult entrustGiveUpHandleCheck(List<CfetsTrdBondFirstBidFirmEtrRequest> requestList) {
        AjaxResult ajaxResult = new AjaxResult();
        if (CollectionUtils.isEmpty(requestList)) {
            return ajaxResult;
        }
        List<String> errorList = new ArrayList<String>();
        List<String> entrustIdListAll = new ArrayList<>();
        Map<String, String> finprodAbbrMap = requestList.stream().collect(Collectors.toMap(CfetsTrdBondFirstBidFirmEtrRequest::getFinprodId,CfetsTrdBondFirstBidFirmEtrRequest::getFinprodAbbr,(v1, v2) ->v2));

        Map<String, List<CfetsTrdBondFirstBidFirmEtrRequest>> finprodIdMap = requestList.stream().collect(Collectors.groupingBy(o -> o.getFinprodId()));
        for (Map.Entry<String, List<CfetsTrdBondFirstBidFirmEtrRequest>> entry : finprodIdMap.entrySet()) {
            List<String> entrustIdListOld = entry.getValue().stream().map(CfetsTrdBondFirstBidFirmEtrRequest::getEntrustId).collect(Collectors.toList());
            //查询
            Map<String, Object> param = new HashMap<>();
            this.setParam(param);
            param.put("finprodId", entry.getKey());
            //筛选出未接收的指令
            List<String> entrustIdListNew = cfetsTrdFirstBidFirm001Mapper.listEntrust(param).stream()
                    .filter(item -> DictCodeList.委托状态_已接收.getValue().equals(item.getEntrustStatus()) && DictCodeList.一级投标中标状态_待投标.getValue().equals(item.getFirstBidStatus()))
                    .map(CfetsTrdFirstBidFirmEntrustDto::getEntrustId).collect(Collectors.toList());
            Collections.sort(entrustIdListOld);
            Collections.sort(entrustIdListNew);
            if (!StringUtil.equals(entrustIdListOld.toString(), entrustIdListNew.toString())) {
                errorList.add("存在同一投标日资产[" + finprodAbbrMap.get(entry.getKey()) + "]的其他已接收待投标指令，是否全部放弃");
                entrustIdListAll.addAll(entrustIdListNew);
            }

        }
        Map<String, Object> map = new HashMap<>();
        map.put("massege", errorList);
        map.put("ids", entrustIdListAll);
        ajaxResult.setData(map);
        ajaxResult.setMessageType(MessageType.确认消息);
        return ajaxResult;
    }

    /**
     * 退回指令 check
     *
     * @param param
     * @return
     */
    public AjaxResult entrustRefuceCheck(Map<String, Object> param) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        if (!StringUtil.isNotEmpty(param.get("remark"))) {
            errorList.add(new ValidMessage("请输入退回原因!"));
            ajaxResult.setErrorList(errorList);
            return ajaxResult;
        }

        // 2.查询指令状态及执行进度
        this.setParam(param);
        List<CfetsTrdFirstBidFirmEntrustDto> cfetsTrdFirstBidFirmEntrustDtos = cfetsTrdFirstBidFirm001Mapper.listEntrust(param);
        if (CollectionUtils.isNotEmpty(cfetsTrdFirstBidFirmEntrustDtos)) {
            for (CfetsTrdFirstBidFirmEntrustDto item : cfetsTrdFirstBidFirmEntrustDtos) {
                if (!(StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_已接收.getValue()) &&
                        StringUtil.equals(item.getFirstBidStatus(), DictCodeList.一级投标中标状态_待投标.getValue()))) {
                    errorList.add(new ValidMessage("只可对已接收且中标状态为待投标指令执行退回操作!"));
                    ajaxResult.setErrorList(errorList);
                    return ajaxResult;
                }

                if (!(item.getTransactioncom().compareTo(BigDecimal.ZERO) == 0)) {
                    errorList.add(new ValidMessage("只可对执行进度为0的指令执行退回操作!"));
                    ajaxResult.setErrorList(errorList);
                    return ajaxResult;
                }
                param.put("entrustId", item.getEntrustId());
                if (cfetsTrdFirstBidFirm001Mapper.checkDataStatus(param) > 0) {
                    errorList.add(new ValidMessage("存在流程中的交易数据，不可退回!"));
                    ajaxResult.setErrorList(errorList);
                    return ajaxResult;
                }
            }
        }
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }

    /**
     * 退回指令
     *
     * @param param
     */
    @Transactional(rollbackFor = Exception.class)
    @DTXTransaction
    public AjaxResult entrustRefuce(Map<String, Object> param) {
        param.put("rekReason", (String) param.get("remark"));
        return cfetsEtrDispatcherService.refuseEntrust(param);
    }

    /**
     * 查询指令信息
     *
     * @param dto
     * @return
     */
    public Page<CfetsTrdFirstBidFirmEntrustDto> pageEntrust(CfetsTrdBondFirstBidFirmEntrustRequest dto) {
        Map<String, Object> param = BeanUtil.object2Map(dto);
        this.setParam(param);
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsTrdFirstBidFirm001Mapper.listEntrust(param));
    }

    /**
     * 查询指令信息
     *
     * @param dto
     * @return
     */
    public List<CfetsTrdFirstBidFirmEntrustDto> listEntrust(CfetsTrdBondFirstBidFirmEntrustRequest dto) {
        Map<String, Object> param = BeanUtil.object2Map(dto);
        this.setParam(param);
        return cfetsTrdFirstBidFirm001Mapper.listEntrust(param);
    }

    /**
     * 指令查询参数初始化
     *
     * @param param
     */
    private void setParam(Map<String, Object> param) {
        param.put("ISSUE_CLASS_02", DictCodeListExt4.新债_发行方式_招标发行.getValue());
        param.put("ISSUE_CLASS_03", DictCodeListExt4.新债_发行方式_簿记建档.getValue());
        param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        param.put("YN_N", DictCodeListExt1.是否_否.getValue());
        String loginUserId = LoginUserUtil.getUserName();
        param.put("loginUserId", loginUserId);
        param.put("year", DateUtil.getYear(iPlatformDate.getSysDateStr()));
        //param.put("loginUserId", "chujy");
    }

    /**
     * 指令查询参数初始化
     *
     * @param param
     */
    private void setParams(Map<String, Object> param) {
        param.put("ISSUE_CLASS_02", DictCodeListExt4.新债_发行方式_招标发行.getValue());
        param.put("ISSUE_CLASS_03", DictCodeListExt4.新债_发行方式_簿记建档.getValue());
        param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        param.put("YN_N", DictCodeListExt1.是否_否.getValue());
        String loginUserId = LoginUserUtil.getUserName();
        param.put("loginUserId", loginUserId);
        param.put("year", DateUtil.getYear(iPlatformDate.getSysDateStr()));
        if (!StringUtil.isNotEmpty(param.get("firstBidStatus"))) {
            param.put("firstBidStatus", Arrays.asList(DictCodeList.一级投标中标状态_已投标.getValue(),
                    DictCodeList.一级投标中标状态_未中标.getValue(), DictCodeList.一级投标中标状态_已中标.getValue()));
        }
    }


    /**
     * 查询指令汇总信息
     *
     * @param dto
     * @return
     */
//    public List<CfetsTrdFirstBidFirmEntrustDto> pageSumEntrust(CfetsTrdBondFirstBidFirmEntrustRequest dto) {
//        Map<String, Object> param = BeanUtil.object2Map(dto);
//        this.setParam(param);
//        //指令汇总数据
//        List<CfetsTrdFirstBidFirmEntrustDto> bidFirmSumDtoList = new ArrayList<>();
//        //手动汇总
//        List<CfetsTrdFirstBidFirmEntrustDto> bidFirmSumDtoListHand = new ArrayList<>();
//        List<CfetsTrdFirstBidFirmEntrustDto> bidFirmDtos = cfetsTrdFirstBidFirm001Mapper.listEntrust(param);
//        bidFirmDtos.stream().forEach(x -> {
//            x.setSumFlag(Boolean.FALSE);
//            x.setEntrustIdKey(x.getEntrustId());
//        });
//
//        //查询汇总关系表数据  汇总关系表数据单独汇总
//        Map<String, Object> relParam = new HashMap<>();
//        String loginUserId = LoginUserUtil.getUserName();
//        relParam.put("trader", loginUserId);
//        relParam.put("sumFlag", this.SUM_FLAG_01);
//        //单独汇总数据
//        List<TotalEtrEntrustRel> relList01 = cfetsTrdFirstBidFirm001Mapper.queryTotalEtrEntrustRel(relParam);
//        List<String> entrustId01 = relList01.stream().map(TotalEtrEntrustRel::getEntrustId).collect(Collectors.toList());
//        relParam.put("sumFlag", this.SUM_FLAG_02);
//        List<TotalEtrEntrustRel> relList02 = cfetsTrdFirstBidFirm001Mapper.queryTotalEtrEntrustRel(relParam);
//        List<String> entrustId02 = relList02.stream().map(TotalEtrEntrustRel::getEntrustId).collect(Collectors.toList());
//
//        Iterator<CfetsTrdFirstBidFirmEntrustDto> iterator = bidFirmDtos.iterator();
//        while (iterator.hasNext()) {
//            CfetsTrdFirstBidFirmEntrustDto bidFirm = iterator.next();
//            //匹配出取消汇总（单独进行汇总）的数据放入-指令汇总数据
//            if (entrustId01.contains(bidFirm.getEntrustId())) {
//                bidFirm.setSumFlag(Boolean.TRUE);
//                bidFirmSumDtoList.add(bidFirm);
//                iterator.remove();
//            }
//            //匹配出手动汇总的数据
//            if (entrustId02.contains(bidFirm.getEntrustId())) {
//                bidFirmSumDtoListHand.add(bidFirm);
//                iterator.remove();
//            }
//        }
//        Map<String, List<CfetsTrdFirstBidFirmEntrustDto>> handSumMap = new HashMap<>();
//        if (CollectionUtils.isNotEmpty(bidFirmSumDtoListHand)) {
//            handSumMap = bidFirmSumDtoListHand.stream()
//                    .collect(Collectors.groupingBy(o -> o.getFinprodId() + "_" + o.getIssueDateStr() + "_" + o.getEntrustStatus()));
//        }
//
//
//        //交易对手为空
//        List<CfetsTrdFirstBidFirmEntrustDto> bidFirmCounterIsNull = bidFirmDtos.stream()
//                .filter(x -> StringUtil.isEmpty(x.getCounterId())).collect(Collectors.toList());
//
//        //交易对手不为空
//        List<CfetsTrdFirstBidFirmEntrustDto> bidFirmCounterIsNotNull = bidFirmDtos.stream()
//                .filter(x -> StringUtil.isNotEmpty(x.getCounterId())).collect(Collectors.toList());
//
//        //汇总数据维度（新债标的OR投标日OR交易对手or指令状态）
//        Map<String, List<CfetsTrdFirstBidFirmEntrustDto>> counterIsNullMapSel = new HashMap<>();
//        Map<String, List<CfetsTrdFirstBidFirmEntrustDto>> counterIsNullMap = bidFirmCounterIsNull.stream()
//                .collect(Collectors.groupingBy(o -> o.getFinprodId() + "_" + o.getIssueDateStr() + "_" + o.getEntrustStatus()));
//
//        for (Map.Entry<String, List<CfetsTrdFirstBidFirmEntrustDto>> entry : counterIsNullMap.entrySet()) {
//            if (CollectionUtils.isNotEmpty(handSumMap.get(entry.getKey()))) {
//                List <CfetsTrdFirstBidFirmEntrustDto> handList = new ArrayList<>();
//                CfetsTrdFirstBidFirmEntrustDto entrustDto = handSumMap.get(entry.getKey()).get(0);
//                if (entrustDto != null) {
//                    handList.add(entrustDto);
//                    handList.addAll(entry.getValue());
//                    handSumMap.put(entry.getKey(), handList);
//                }
//            } else {
//                counterIsNullMapSel.put(entry.getKey(), entry.getValue());
//            }
//
//        }
//
//        Map<String, List<CfetsTrdFirstBidFirmEntrustDto>> counterIsNotNullMap = bidFirmCounterIsNotNull.stream()
//                .collect(Collectors.groupingBy(o -> o.getFinprodId() + "_" + o.getIssueDateStr() + "_" + o.getCounterId() + "_" + o.getEntrustStatus()));
//
//        List<CfetsTrdFirstBidFirmEntrustDto> bidFirmSumIsNullList = getSumListInfo(counterIsNullMapSel);
//        List<CfetsTrdFirstBidFirmEntrustDto> bidFirmSumIsNotNullList = getSumListInfo(counterIsNotNullMap);
//        List<CfetsTrdFirstBidFirmEntrustDto> handSumList = getSumListInfo(handSumMap);
//        if (CollectionUtils.isNotEmpty(bidFirmSumIsNullList)) {
//            bidFirmSumDtoList.addAll(bidFirmSumIsNullList);
//        }
//        if (CollectionUtils.isNotEmpty(bidFirmSumIsNotNullList)) {
//            bidFirmSumDtoList.addAll(bidFirmSumIsNotNullList);
//        }
//        if (CollectionUtils.isNotEmpty(handSumList)) {
//            bidFirmSumDtoList.addAll(handSumList);
//        }
//        String sort = dto.getPageMode().getSort();
//        String direction = dto.getPageMode().getDirection();
//        return getSortByBidFirmList(bidFirmSumDtoList, sort, direction);
//    }

    /**
     * 查询指令汇总信息
     *
     * @param dto
     * @return
     */
    public List<CfetsTrdFirstBidFirmEntrustDto> pageSumEntrust(CfetsTrdBondFirstBidFirmEntrustRequest dto) {
        Map<String, Object> param = BeanUtil.object2Map(dto);
        this.setParam(param);
        //指令汇总数据
        List<CfetsTrdFirstBidFirmEntrustDto> bidFirmSumDtoList = new ArrayList<>();
        //手动汇总
        List<CfetsTrdFirstBidFirmEntrustDto> bidFirmSumDtoListHand = new ArrayList<>();
        List<CfetsTrdFirstBidFirmEntrustDto> bidFirmDtos = cfetsTrdFirstBidFirm001Mapper.listEntrust(param);

        Iterator<CfetsTrdFirstBidFirmEntrustDto> iterator = bidFirmDtos.iterator();
        while (iterator.hasNext()) {
            CfetsTrdFirstBidFirmEntrustDto bidFirm = iterator.next();
            bidFirm.setSumFlag(Boolean.FALSE);
            bidFirm.setEntrustIdKey(bidFirm.getEntrustId());
            if (StringUtil.isNotEmpty(bidFirm.getTotleId())) {
                bidFirmSumDtoListHand.add(bidFirm);
                iterator.remove();;
            }

        }

        //手动汇总
        Map<String, List<CfetsTrdFirstBidFirmEntrustDto>> handSumMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(bidFirmSumDtoListHand)) {
            handSumMap = bidFirmSumDtoListHand.stream().collect(Collectors.groupingBy(o -> o.getTotleId()));
        }

        //交易对手为空
        List<CfetsTrdFirstBidFirmEntrustDto> bidFirmCounterIsNull = bidFirmDtos.stream()
                .filter(x -> StringUtil.isEmpty(x.getCounterId())).collect(Collectors.toList());

        //交易对手不为空
        List<CfetsTrdFirstBidFirmEntrustDto> bidFirmCounterIsNotNull = bidFirmDtos.stream()
                .filter(x -> StringUtil.isNotEmpty(x.getCounterId())).collect(Collectors.toList());

        //汇总数据维度（新债标的OR投标日OR交易对手or指令状态）
        Map<String, List<CfetsTrdFirstBidFirmEntrustDto>> counterIsNullMapSel = bidFirmCounterIsNull.stream()
                .collect(Collectors.groupingBy(o -> o.getFinprodId() + "_" + o.getIssueDateStr() + "_" + o.getEntrustStatus()));
        Map<String, List<CfetsTrdFirstBidFirmEntrustDto>> counterIsNotNullMap = bidFirmCounterIsNotNull.stream()
                .collect(Collectors.groupingBy(o -> o.getFinprodId() + "_" + o.getIssueDateStr() + "_" + o.getCounterId() + "_" + o.getEntrustStatus()));

        List<CfetsTrdFirstBidFirmEntrustDto> bidFirmSumIsNullList = getSumListInfo(counterIsNullMapSel);
        List<CfetsTrdFirstBidFirmEntrustDto> bidFirmSumIsNotNullList = getSumListInfo(counterIsNotNullMap);
        List<CfetsTrdFirstBidFirmEntrustDto> handSumList = getSumListInfo(handSumMap);
        if (CollectionUtils.isNotEmpty(bidFirmSumIsNullList)) {
            bidFirmSumDtoList.addAll(bidFirmSumIsNullList);
        }
        if (CollectionUtils.isNotEmpty(bidFirmSumIsNotNullList)) {
            bidFirmSumDtoList.addAll(bidFirmSumIsNotNullList);
        }
        if (CollectionUtils.isNotEmpty(handSumList)) {
            bidFirmSumDtoList.addAll(handSumList);
        }
        String sort = dto.getPageMode().getSort();
        String direction = dto.getPageMode().getDirection();
        return getSortByBidFirmList(bidFirmSumDtoList, sort, direction);
    }

    /**
     * 对汇总数据进行排序
     *
     * @param bidFirmSumDtoList
     * @param sort
     * @param direction
     * @return
     */
    private List<CfetsTrdFirstBidFirmEntrustDto> getSortByBidFirmList(List<CfetsTrdFirstBidFirmEntrustDto> bidFirmSumDtoList, String sort, String direction) {
        if (StringUtil.isEmpty(sort)) {
            return bidFirmSumDtoList;
        }
        if ("entrustStatus".equals(sort)) {
            if ("desc".equals(direction)) {
                return bidFirmSumDtoList.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getEntrustStatus).reversed()).collect(Collectors.toList());
            } else {
                return bidFirmSumDtoList.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getEntrustStatus)).collect(Collectors.toList());
            }
        }

        if ("firstBidStatus".equals(sort)) {
            if ("desc".equals(direction)) {
                return bidFirmSumDtoList.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getFirstBidStatus).reversed()).collect(Collectors.toList());
            } else {
                return bidFirmSumDtoList.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getFirstBidStatus)).collect(Collectors.toList());
            }
        }

        if ("finprodMarketId".equals(sort)) {
            if ("desc".equals(direction)) {
                return bidFirmSumDtoList.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getFinprodMarketId).reversed()).collect(Collectors.toList());
            } else {
                return bidFirmSumDtoList.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getFinprodMarketId)).collect(Collectors.toList());
            }
        }

        if ("finprodAbbr".equals(sort)) {
            if ("desc".equals(direction)) {
                return bidFirmSumDtoList.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getFinprodAbbr).reversed()).collect(Collectors.toList());
            } else {
                return bidFirmSumDtoList.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getFinprodAbbr)).collect(Collectors.toList());
            }
        }

        if ("tenderTarget".equals(sort)) {
            if ("desc".equals(direction)) {
                return bidFirmSumDtoList.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getTenderTarget).reversed()).collect(Collectors.toList());
            } else {
                return bidFirmSumDtoList.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getTenderTarget)).collect(Collectors.toList());
            }
        }

        if ("portfolioId".equals(sort)) {
            if ("desc".equals(direction)) {
                return bidFirmSumDtoList.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getPortfolioId).reversed()).collect(Collectors.toList());
            } else {
                return bidFirmSumDtoList.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getPortfolioId)).collect(Collectors.toList());
            }
        }

        if ("portfolioName".equals(sort)) {
            if ("desc".equals(direction)) {
                return bidFirmSumDtoList.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getPortfolioName).reversed()).collect(Collectors.toList());
            } else {
                return bidFirmSumDtoList.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getPortfolioName)).collect(Collectors.toList());
            }
        }

        if ("tradeMarket".equals(sort)) {
            if ("desc".equals(direction)) {
                return bidFirmSumDtoList.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getTradeMarket).reversed()).collect(Collectors.toList());
            } else {
                return bidFirmSumDtoList.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getTradeMarket)).collect(Collectors.toList());
            }
        }

        if ("invManagerName".equals(sort)) {
            if ("desc".equals(direction)) {
                return bidFirmSumDtoList.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getInvManagerName).reversed()).collect(Collectors.toList());
            } else {
                return bidFirmSumDtoList.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getInvManagerName)).collect(Collectors.toList());
            }
        }

        if ("counterName".equals(sort)) {
            List<CfetsTrdFirstBidFirmEntrustDto> bidFirmSumDtoListNew = new ArrayList<>();
            List<CfetsTrdFirstBidFirmEntrustDto> counterNotNull = bidFirmSumDtoList.stream()
                    .filter(item -> StringUtil.isNotEmpty(item.getCounterName())).collect(Collectors.toList());
            List<CfetsTrdFirstBidFirmEntrustDto> counterNull = bidFirmSumDtoList.stream()
                    .filter(item -> StringUtil.isEmpty(item.getCounterName())).collect(Collectors.toList());
            if ("desc".equals(direction)) {
                bidFirmSumDtoListNew.addAll(counterNotNull.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getCounterName).reversed()).collect(Collectors.toList()));
            } else {
                bidFirmSumDtoListNew.addAll(counterNotNull.stream().sorted(Comparator.comparing(CfetsTrdFirstBidFirmEntrustDto::getCounterName)).collect(Collectors.toList()));
            }
            bidFirmSumDtoListNew.addAll(counterNull);
            return bidFirmSumDtoListNew;
        }

        return bidFirmSumDtoList;
    }

    /**
     * 明细信息进行汇总
     *
     * @param counterIsNullMap
     * @return
     */
    private List<CfetsTrdFirstBidFirmEntrustDto> getSumListInfo(Map<String, List<CfetsTrdFirstBidFirmEntrustDto>> counterIsNullMap) {
        if (counterIsNullMap.isEmpty()) {
            return null;
        }
        List<CfetsTrdFirstBidFirmEntrustDto> bidFirmSumDtoList = new ArrayList<>();
        for (Map.Entry<String, List<CfetsTrdFirstBidFirmEntrustDto>> entry : counterIsNullMap.entrySet()) {
            CfetsTrdFirstBidFirmEntrustDto bidFirmSum = new CfetsTrdFirstBidFirmEntrustDto();
            List<CfetsTrdFirstBidFirmEntrustDto> bidFirmDetail = entry.getValue();
            if (bidFirmDetail.size() == 1) {
                bidFirmDetail.stream().forEach(item -> {
                    item.setSumFlag(Boolean.TRUE);
                });
                bidFirmSumDtoList.addAll(bidFirmDetail);
            } else {
                bidFirmSum.setCfetsTrdFirstBidFirmEntrustDtos(bidFirmDetail);
                bidFirmSum.setEntrustStatus(bidFirmDetail.get(0).getEntrustStatus());
                bidFirmSum.setFirstBidStatus(bidFirmDetail.get(0).getFirstBidStatus());
                //唯一标识 前端用
                bidFirmSum.setEntrustIdKey(bidFirmDetail.get(0).getEntrustId() + "KEY");
                //重新计算执行进度和成交进度
                //已完成额度
                BigDecimal sumFinishAmt = bidFirmDetail.stream().map(CfetsTrdFirstBidFirmEntrustDto::getFinishAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                //已成交额度
                BigDecimal sumFinishDealAmt = bidFirmDetail.stream().map(CfetsTrdFirstBidFirmEntrustDto::getFinishDealAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                //剩余指令额度
                BigDecimal sumAvaNum = bidFirmDetail.stream().map(CfetsTrdFirstBidFirmEntrustDto::getAvaNum).reduce(BigDecimal.ZERO, BigDecimal::add);
                //剩余成交额度
                BigDecimal sumAvaDealAmt = bidFirmDetail.stream().map(CfetsTrdFirstBidFirmEntrustDto::getAvaDealAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal sumEntrustNum = bidFirmDetail.stream().map(CfetsTrdFirstBidFirmEntrustDto::getEntrustNum).reduce(BigDecimal.ZERO, BigDecimal::add);

                bidFirmSum.setFinishAmt(sumFinishAmt);
                bidFirmSum.setFinishDealAmt(sumFinishDealAmt);
                bidFirmSum.setAvaNum(sumAvaNum);
                bidFirmSum.setAvaDealAmt(sumAvaDealAmt);
                bidFirmSum.setTransactioncom(ArithmeticUtil.divide(sumFinishAmt, sumEntrustNum, 2).multiply(new BigDecimal("100")));
                bidFirmSum.setTradeFinishPercent(ArithmeticUtil.divide(sumFinishDealAmt, sumEntrustNum, 2).multiply(new BigDecimal("100")));
                bidFirmSum.setFinprodId(bidFirmDetail.get(0).getFinprodId());
                bidFirmSum.setFinprodMarketId(bidFirmDetail.get(0).getFinprodMarketId());
                bidFirmSum.setFinprodAbbr(bidFirmDetail.get(0).getFinprodAbbr());
                bidFirmSum.setTenderTarget(bidFirmDetail.get(0).getTenderTarget());
                bidFirmSum.setTenderTargetStr(bidFirmDetail.get(0).getTenderTargetStr());
                bidFirmSum.setIssueDateStr(bidFirmDetail.get(0).getIssueDateStr());

                bidFirmSum.setEntrustNum(sumEntrustNum);
                //投标标位去重
                List<String> bidPriceList = bidFirmDetail.stream().map(CfetsTrdFirstBidFirmEntrustDto::getBidPrice)
                        .distinct().sorted().collect(Collectors.toList());
                bidFirmSum.setBidPrice(StringUtil.join(bidPriceList, ","));
                List<String> portfolioIdList = bidFirmDetail.stream().map(CfetsTrdFirstBidFirmEntrustDto::getPortfolioId)
                        .distinct().sorted().collect(Collectors.toList());
                List<String> portfolioNameList = bidFirmDetail.stream().map(CfetsTrdFirstBidFirmEntrustDto::getPortfolioName)
                        .distinct().sorted().collect(Collectors.toList());
                bidFirmSum.setPortfolioId(StringUtil.join(portfolioIdList, ","));
                bidFirmSum.setPortfolioName(StringUtil.join(portfolioNameList, ","));
                bidFirmSum.setTradeMarket(bidFirmDetail.get(0).getTradeMarket());
                bidFirmSum.setTradeMarketStr(bidFirmDetail.get(0).getTradeMarketStr());

                List<String> invManagerIdList = bidFirmDetail.stream().map(CfetsTrdFirstBidFirmEntrustDto::getInvManagerId)
                        .distinct().sorted().collect(Collectors.toList());
                List<String> invManagerNameList = bidFirmDetail.stream().map(CfetsTrdFirstBidFirmEntrustDto::getInvManagerName)
                        .distinct().sorted().collect(Collectors.toList());
                bidFirmSum.setInvManagerId(StringUtil.join(invManagerIdList, ","));
                bidFirmSum.setInvManagerName(StringUtil.join(invManagerNameList, ","));

                List<String> counterIdList = new ArrayList<>();
                List<String> counterNameList = new ArrayList<>();
                for (CfetsTrdFirstBidFirmEntrustDto bidFirmDto : bidFirmDetail) {

                    if (StringUtil.isNotEmpty(bidFirmDto.getCounterId())) {
                        counterIdList.add(bidFirmDto.getCounterId());
                    }
                    if (StringUtil.isNotEmpty(bidFirmDto.getCounterName())) {
                        counterNameList.add(bidFirmDto.getCounterName());
                    }
                }

                if (CollectionUtils.isNotEmpty(counterIdList)) {
                    bidFirmSum.setCounterId(StringUtil.join(counterIdList.stream().distinct().collect(Collectors.toList()), ","));
                }
                if (CollectionUtils.isNotEmpty(counterNameList)) {
                    bidFirmSum.setCounterName(StringUtil.join(counterNameList.stream().distinct().collect(Collectors.toList()), ","));
                }


                bidFirmSum.setSumFlag(Boolean.TRUE);

                bidFirmSumDtoList.add(bidFirmSum);
            }


        }
        return bidFirmSumDtoList;
    }

    /**
     * 指令取消汇总保存
     *
     * @param cfetsDto
     * @return
     */
    public int undoSumSave(CfetsTrdBondFirstBidFirmUndoSumRequest cfetsDto) {
        TotalEtrEntrustRel totalEtrEntrustRel = new TotalEtrEntrustRel();
        String uuid = IDGenerator.generate();
        totalEtrEntrustRel.setTotleId(uuid);
        totalEtrEntrustRel.setEntrustId(cfetsDto.getEntrustId());
        totalEtrEntrustRel.setSumFlag(cfetsDto.getSumFlag());
        String loginUserId = LoginUserUtil.getUserName();
        // param.put("loginUserId", loginUserId);
        totalEtrEntrustRel.setTrader(loginUserId);
        //先删除后插入
        cfetsTrdFirstBidFirm001Mapper.deleteEtrEntrustRel(cfetsDto.getEntrustId(), loginUserId);
        return totalEtrEntrustRelMapper.insertSelective(totalEtrEntrustRel);
    }

    /**
     * 获取指令详细信息
     *
     * @param entrustId
     * @return
     */
    public Map<String, Object> getEntrustInfo(String entrustId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("entrustId", entrustId);
        //查询指令详细信息
        CfetsTrdBondFirsBidFirmDetailDto formData = cfetsTrdFirstBidFirm001Mapper.getEntrustInfo(param);
        //查询投标产品列表
        List<CfetsTrdBondFirstBidFirmProductDto> productDtos = cfetsTrdFirstBidFirm001Mapper.getEntrustProductInfo(param);
        List<String> spcdCodeList = new ArrayList<>();
        result.put("form", getReadonlyForms(this.PAGECODE, FORM_DTCODE_ETR, spcdCodeList));
        if (StringUtil.isNotEmpty(formData)) {
            result.put("formData", formData);
        }

        // 表格格式
        result.put("table", getBySpcdCodes(this.PAGECODE, TABLE_DTCODE_ETR, new ArrayList<>()));
        if (CollectionUtils.isNotEmpty(productDtos)) {
            // 表单数据
            result.put("tableData", productDtos);
            BigDecimal sumAmt = productDtos.stream().map(CfetsTrdBondFirstBidFirmProductDto::getBidPrinAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
            // 汇总投标券面总额
            result.put("sumAmt", sumAmt);
        }

        return result;
    }

    /**
     * 查询投标确认执行明细
     *
     * @param entrustId
     * @return
     */
    public Map<String, Object> getBidOrderTransactionDetails(String entrustId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("entrustId", entrustId);
        //查询投标确认执行明细
        List<CfetsTrdBondFirsBidFirmBidOrderDetailDto> tableData = cfetsTrdFirstBidFirm001Mapper.getBidOrderTransactionDetails(param);
        if (CollectionUtils.isNotEmpty(tableData)) {
            // 表格格式
            List<String> spcdCodeList = new ArrayList<>();
            String biddingMethod = tableData.get(0).getBiddingMethod();
            if (StringUtil.equals(biddingMethod, DictCodeListExt4.投标方式_分销买入.getValue())) {
                spcdCodeList.add("03");
            } else {
                spcdCodeList.add("01");
            }
            result.put("table", getBySpcdCodes(this.PAGECODE, this.TABLE_DTCODE_BID, spcdCodeList));
            // 表单数据
            result.put("tableData", tableData);
        }


        return result;
    }

    /**
     * 查询表单表头 (私有方法 pageCode,dtCode,spcdCodeList仅组装该页面的数据)
     */
    private List<CfgReadonlyFormShow> getReadonlyForms(String pageCode, String dtCode, List<String> spcdCodeList) {
        if (CollectionUtils.isEmpty(spcdCodeList)) {
            spcdCodeList = new ArrayList<>();
        }
        spcdCodeList.add(COMM);
        List<CfgReadonlyFormShow> cfgReadonlyFormShows = cfgReadonlyFormService.getReadonlyForms(pageCode, dtCode, spcdCodeList);
        return cfgReadonlyFormShows;
    }

    /**
     * 查询表单表头 (私有方法 pageCode,dtCode,spcdCodeList仅组装该页面的数据)
     */
    private List<CfgDataTableShow> getBySpcdCodes(String pageCode, String dtCode, List<String> spcdCodeList) {
        if (spcdCodeList == null) {
            spcdCodeList = new ArrayList<>();
        }
        spcdCodeList.add(COMM);//通用配置
        List<CfgDataTableShow> infoForm = cfgDataTableService.getByPageCodeAndDtCodes(pageCode, dtCode, spcdCodeList);
        return infoForm;
    }

    /**
     * 投标查询
     *
     * @param request 查询条件
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmTenderInfoDto>
     * @author jiangpeng
     * @date 2022/5/23 16:58
     */
    public Page<CfetsTrdFirstBidFirmTenderInfoDto> tenderSearch(CfetsTrdFirstBidFirmTenderInfoRequest request) {
        Map<String, Object> param = BeanUtil.object2Map(request);
        this.setParams(param);
        return super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(), () -> cfetsTrdFirstBidFirm001Mapper.tenderSearch(param));
    }

    /**
     * 分销买入查询
     *
     * @param request 查询条件
     * @return com.joyintech.framework.grid.Page<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmDistributionInfoDto>
     * @author jiangpeng
     * @date 2022/5/23 16:58
     */
    public Page<CfetsTrdFirstBidFirmDistributionInfoDto> distributionSearch(CfetsTrdFirstBidFirmDistributionInfoRequest request) {
        Map<String, Object> param = BeanUtil.object2Map(request);
        this.setParams(param);
        return super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(), () -> cfetsTrdFirstBidFirm001Mapper.distributionSearch(param));
    }

    /**
     * 指令汇总操作
     *
     * @param entrustDtoList
     * @return
     */
    public List<ValidMessage> etrSumOperate(List<CfetsTrdFirstBidFirmEntrustDto> entrustDtoList) {
        List<ValidMessage> errorList = new ArrayList<>();
        if (CollectionUtils.isEmpty(entrustDtoList)) {
            errorList.add(new ValidMessage("指令信息不能为空!"));
            return errorList;
        }

        List<CfetsTrdFirstBidFirmEntrustDto> outList = entrustDtoList.stream().filter(dto -> OPERATE_SIDE_OUT.equals(dto.getOperateSide()))
                .collect(Collectors.toList());
        List<CfetsTrdFirstBidFirmEntrustDto> inList = entrustDtoList.stream().filter(dto -> OPERATE_SIDE_IN.equals(dto.getOperateSide()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(outList) && CollectionUtils.isNotEmpty(inList)) {
            errorList.add(new ValidMessage("指令信息同时存在手动汇总和取消汇总数据!"));
            return errorList;
        }
        if (CollectionUtils.isNotEmpty(outList) && outList.size() > 1) {
            errorList.add(new ValidMessage("不允许同时取消汇总多条指令信息!"));
            return errorList;
        }
        //拖出：取消汇总
        if (CollectionUtils.isNotEmpty(outList)) {
            CfetsTrdFirstBidFirmEntrustDto cfetsDto = outList.get(0);
            undoSumSave(cfetsDto, this.SUM_FLAG_01);
        }

        if (CollectionUtils.isNotEmpty(inList)) {

            //汇总数据维度（新债标的OR投标日OR指令状态）
            Map<String, List<CfetsTrdFirstBidFirmEntrustDto>> counterIsNullMap = inList.stream()
                    .collect(Collectors.groupingBy(o -> o.getFinprodId() + "_" + o.getIssueDateStr() + "_" + o.getEntrustStatus()));
            if (!counterIsNullMap.isEmpty() && counterIsNullMap.size() > 1) {
                errorList.add(new ValidMessage("所选指令不满足汇总条件：【新债标的OR投标日OR交易对手OR指令状态】不同"));
                return errorList;
            }

            List<CfetsTrdFirstBidFirmEntrustDto> counterIdList = inList.stream().filter(x -> StringUtil.isNotEmpty(x.getCounterId()))
                    .collect(Collectors.toList());
            List<CfetsTrdFirstBidFirmEntrustDto> counterIdIsNullList = inList.stream().filter(x -> StringUtil.isEmpty(x.getCounterId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(counterIdList) && counterIdList.size() > 1) {
                errorList.add(new ValidMessage("所选指令不满足汇总条件：手动汇总时指令中有多个交易对手!"));
                return errorList;
            }
            //拖入：没有交易对手方 删除汇总关系 （指令信息自动汇总）
            if (CollectionUtils.isEmpty(counterIdList)) {
                if (CollectionUtils.isNotEmpty(counterIdIsNullList)) {
                    String loginUserId = LoginUserUtil.getUserName();
                    for (CfetsTrdFirstBidFirmEntrustDto dto : counterIdIsNullList) {
                        cfetsTrdFirstBidFirm001Mapper.deleteEtrEntrustRel(dto.getEntrustId(), loginUserId);
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(counterIdList)) {
                String loginUserId = LoginUserUtil.getUserName();
                String uuid = IDGenerator.generate();
                if (CollectionUtils.isNotEmpty(counterIdList)) {
                    for (CfetsTrdFirstBidFirmEntrustDto dto :counterIdList) {
                        cfetsTrdFirstBidFirm001Mapper.deleteEtrEntrustRel(dto.getEntrustId(), loginUserId);
                        TotalEtrEntrustRel totalEtrEntrustRel = new TotalEtrEntrustRel();
                        totalEtrEntrustRel.setTotleId(uuid);
                        totalEtrEntrustRel.setEntrustId(dto.getEntrustId());
                        totalEtrEntrustRel.setSumFlag(this.SUM_FLAG_02);
                        totalEtrEntrustRel.setTrader(loginUserId);
                        totalEtrEntrustRelMapper.insertSelective(totalEtrEntrustRel);
                    }
                }
                if (CollectionUtils.isNotEmpty(counterIdIsNullList)) {
                    for (CfetsTrdFirstBidFirmEntrustDto dto :counterIdIsNullList) {
                        cfetsTrdFirstBidFirm001Mapper.deleteEtrEntrustRel(dto.getEntrustId(), loginUserId);
                        TotalEtrEntrustRel totalEtrEntrustRel = new TotalEtrEntrustRel();
                        totalEtrEntrustRel.setTotleId(uuid);
                        totalEtrEntrustRel.setEntrustId(dto.getEntrustId());
                        totalEtrEntrustRel.setSumFlag(this.SUM_FLAG_02);
                        totalEtrEntrustRel.setTrader(loginUserId);
                        totalEtrEntrustRelMapper.insertSelective(totalEtrEntrustRel);
                    }
                }

            }
        }

        return errorList;
    }

    /**
     * 指令取消汇总保存
     *
     * @param cfetsDto
     * @return
     */
    public int undoSumSave(CfetsTrdFirstBidFirmEntrustDto cfetsDto, String sumFlag) {
        TotalEtrEntrustRel totalEtrEntrustRel = new TotalEtrEntrustRel();
        String uuid = IDGenerator.generate();
        totalEtrEntrustRel.setTotleId(uuid);
        totalEtrEntrustRel.setEntrustId(cfetsDto.getEntrustId());
        totalEtrEntrustRel.setSumFlag(sumFlag);
        String loginUserId = LoginUserUtil.getUserName();
        // param.put("loginUserId", loginUserId);
        totalEtrEntrustRel.setTrader(loginUserId);
        if (StringUtil.isEmpty(sumFlag)) {
            return cfetsTrdFirstBidFirm001Mapper.deleteEtrEntrustRel(cfetsDto.getEntrustId(), loginUserId);
        } else {
            //先删除后插入
            cfetsTrdFirstBidFirm001Mapper.deleteEtrEntrustRel(cfetsDto.getEntrustId(), loginUserId);
            return totalEtrEntrustRelMapper.insertSelective(totalEtrEntrustRel);
        }
    }


    /**
     * 风险试算参数校验
     *
     * @param dto
     * @return
     */
    public String doRiskcalCheckMethod(CfetsEtrIbdoRiskcalRequest dto) {
        //参数不能为空
        if (StringUtil.isEmptyList(dto.getBussCombDistribtList())) {
            return "组合分配列表不能为空";
        }
        List<String> messages = new ArrayList<>();
        if (StringUtil.isNotEmpty(dto.getBussCombDistribtList())) {
            for (CfetsBussCombDistribtBaseDto item : dto.getBussCombDistribtList()) {
                if (!StringUtil.isNotEmpty(item.getPortfolioId())) {
                    messages.add("请输入组合名称。");
                }
                if (!StringUtil.isNotEmpty(item.getEntrustNum())) {
                    messages.add("请输入券面总额。");
                }
            }
        }
        if (!StringUtil.isNotEmpty(dto.getFinprodId())) {
            messages.add("请输入资产代码。");
        }
        if (messages.size() > 0) {
            return StringUtil.joinString(messages, GlobalConstant.VERTICAL_LINE);
        }
        return null;
    }


    /**
     * 风险试算，后台数据转换
     *
     * @param dto
     * @return
     */
    public Map<String, Object> doRiskcalPackageEquity(CfetsEtrIbdoRiskcalRequest dto) {
        Map<String, Object> dataMap;
        //风险试算封装dto
        CfetsEtrIbdoRiskcalDto d = new CfetsEtrIbdoRiskcalDto();
        BeanUtil.copyPropertiesIgnoreNull(d, dto);
        List<Map> bussTrdTransfenpeiDto = new ArrayList<>();
        for (CfetsBussCombDistribtBaseDto pt : dto.getBussCombDistribtList()) {
            pt.setInvAim(pt.getBusinessModel());
            bussTrdTransfenpeiDto.add(BeanUtil.object2Map(pt));
        }
        d.setBussTrdTransfenpeiDto(bussTrdTransfenpeiDto);
        d.setBusiType("02");
        d.setTradeMarket(BussinessDictCodeList.交易场所_银行间.getValue());
        d.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        d.setVdate(dto.getEtrDate());
        d.setMdate(dto.getEtrDate());
        d.setCcy("CNY");
        d.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());

        dataMap = BeanUtil.object2Map(d);
        return dataMap;
    }

    /**
     * 状态转换
     *
     * @param kriStatus
     * @return
     */
    public String getKriStatusCnByKriStatus(String kriStatus) {
        if (StringUtil.isNotEmpty(kriStatus)) {
            if (StringUtil.equals(kriStatus, "0")) {
                return "正常";
            } else if (StringUtil.equals(kriStatus, "1")) {
                return "预警";
            } else if (StringUtil.equals(kriStatus, "2")) {
                return "审批";
            } else if (StringUtil.equals(kriStatus, "3")) {
                return "违规";
            } else if (StringUtil.equals(kriStatus, "4")) {
                return "异常";
            }
        }
        return null;
    }


}
