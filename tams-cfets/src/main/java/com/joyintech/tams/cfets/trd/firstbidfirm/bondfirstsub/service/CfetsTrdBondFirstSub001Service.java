package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.util.BigDecimalUtil;
import com.joyintech.tams.cfets.trd.bond.dto.*;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dao.CfetsTrdBondFirstSub001Mapper;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.*;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.invest.common.component.base.entrust.impl.BaseEtrInvestInfoServiceImpl;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jp
 * @date 2022/04/18
 */
@Service
public class CfetsTrdBondFirstSub001Service extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(CfetsTrdBondFirstSub001Service.class);

    @Autowired
    private CfetsTrdBondFirstSub001Mapper cfetsTrdBondFirstSub001Mapper;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * 投资委托委托组件
     */
    @Autowired(required = false)
    private BaseEtrInvestInfoServiceImpl etrInvesInfo;

    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    /**
     * 风险试算check
     *
     * @param entity 入参
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2022/4/18 15:00
     */
    public AjaxResult riskCheckNotEmpty(RiskCalRequest entity) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(entity.getPtlAllocations())) {
            ajaxResult.setErrorList(errorList);
        }
        return ajaxResult;
    }

    /**
     * 查询指令列表
     *
     * @param dto
     * @param loginUserId
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.bond.dto.EntrustDto>
     * @author jiangpeng
     * @date 2022/4/18 15:01
     */
    public List<EntrustDto> listEntrust(EntrustRequest dto, String loginUserId) {
        List<EntrustDto> result = new ArrayList<>();
        return result;
    }

    /**
     * 初始化指令列表(分页)
     *
     * @param dto
     * @return com.joyintech.framework.grid.Page<com.joyintech.tams.cfets.finbond.tenderBondSub.dto.TenderBondSubEntrustDto>
     * @author jiangpeng
     * @date 2022/4/19 14:16
     */
    public Page<CfetsTrdBondFirstSubEntrustDto> pageEntrust(CfetsTrdBondFirstSubEntrustRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        setParam(pageParam);
        setPageMode(dto.getPageMode());
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsTrdBondFirstSub001Mapper.listEntrust(pageParam));
    }

    /**
     * 获取指令详细信息
     *
     * @param entrustId 指令代码
     * @return
     */
    public CfetsTrdBondFirstSubEntrustDetailDto getEntrustInfo(String entrustId) {
        CfetsTrdBondFirstSubEntrustDetailDto result = new CfetsTrdBondFirstSubEntrustDetailDto();
        return result;
    }

    public List<CfetsTrdBondFirstSubProductDto> autoAllocation(CfetsTrdBondFirstSubInfoDto autoRequest) {
        List<CfetsTrdBondFirstSubProductDto> list = new ArrayList<>();
        return list;
    }


    /**
     * 接收指令
     *
     * @param param
     * @return java.util.List<com.joyintech.framework.validation.ValidMessage>
     * @author jiangpeng
     * @date 2022/4/18 15:03
     */
    public List<ValidMessage> receiveEntrust(Map<String, Object> param) {
        List<ValidMessage> errorList = new ArrayList<>();
        param.put("trader", LoginUserUtil.getUserName());
        List<EtrDistribute> list = getDistributeTrader(param);
        List<String> traderList = new ArrayList<>();
        for (EtrDistribute etrDis : list) {
            errorList.addAll(etrInvesInfo.checkDelegateOperation(etrDis.getEntrustId(), DictCodeListExt1.委托操作类型_接收.getValue(), traderList, DictCodeListExt1.委托关闭方式_人工.getValue()));
            etrDis.setUpdateUser(etrDis.getTrader());
        }
        //无错误时，进行接收操作
        if (errorList.size() == 0) {
            etrInvesInfo.receptionDelegate(list);
            List<String> entrustIds = StringUtil.splitToList(",", (String) param.get("entrustIds"));
            for (String entrustId : entrustIds) {
                //更新操作记录日志
                CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
                oprHisReq.setEntrustId(entrustId);
                oprHisReq.setOperType(DictCodeListExt3.操作类型_接收.getValue());
                joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
            }
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
        List<EtrDistribute> list = new ArrayList<EtrDistribute>();
        EtrDistributeKey etrKey = new EtrDistributeKey();
        etrKey.setTrader((String) param.get("trader"));
        List<String> entrustIds = StringUtil.splitToList(",", (String) param.get("entrustIds"));
        for (String entrustId : entrustIds) {
            etrKey.setEntrustId(entrustId);
//            list.add(etrDistributeMapper.selectByPrimaryKey(etrKey));
        }
        return list;
    }

    /**
     * 初始化 申购页面
     *
     * @param request
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto
     * @author jiangpeng
     * @date 2022/4/19 19:32
     */
    public CfetsTrdBondFirstSubInfoDto queryEntrustInfoById(CfetsTrdBondFirstSubInitRequest request) {
        CfetsTrdBondFirstSubInfoDto cfetsTrdBondFirstSubInfoDto = new CfetsTrdBondFirstSubInfoDto();
        return cfetsTrdBondFirstSubInfoDto;
    }


    /**
     * 申购点击check
     *
     * @param request
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2022/4/18 15:16
     */
    public AjaxResult dealexecutioncheck(CfetsTrdBondFirstSubInitRequest request) {
        AjaxResult ajaxResult = new AjaxResult();
        return ajaxResult;
    }

    /**
     * 构造生成交易编号的参数
     *
     * @param ps
     * @return
     */
    public static BussinessCodeParameter getBussinessCodeParameter(String ps) {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        if (StringUtil.equals(com.joyintech.tams.constant.DictCodeList.交易方向_买入.getValue(), ps)) {
            codeParameter.setCommonBusinessType(com.joyintech.tams.constant.DictCodeList.共通业务类型_债券买入);
        } else {
            codeParameter.setCommonBusinessType(com.joyintech.tams.constant.DictCodeList.共通业务类型_债券卖出);
        }
        return codeParameter;
    }


    /**
     * 构建汇总费用实体
     *
     * @param bussinessId
     * @param feeType
     * @param amount
     * @return com.joyintech.tams.common.base.entity.RegTradeFeeTotal
     * @author jiangpeng
     * @date 2022/4/18 15:18
     */
    public RegTradeFeeTotal prepareRegTradeFeeTotal(String bussinessId, String feeType, BigDecimal amount) {
        RegTradeFeeTotal regTradeFeeTotal = new RegTradeFeeTotal();
        regTradeFeeTotal.setTradeId(bussinessId);
        regTradeFeeTotal.setFeeType(feeType);
        regTradeFeeTotal.setIsPayWithTrade(com.joyintech.tams.constant.DictCodeList.是否_否.getValue());
        regTradeFeeTotal.setFeeAmt(amount);
        return regTradeFeeTotal;
    }

    /**
     * 构建明细费用实体
     *
     * @param bussinessId
     * @param feeType
     * @param amount
     * @return com.joyintech.tams.common.base.entity.RegTradeFee
     * @author jiangpeng
     * @date 2022/4/18 15:18
     */
    public RegTradeFee prepareRegTradeFee(String bussinessId, String feeType, BigDecimal amount) {
        RegTradeFee regTradeFee = new RegTradeFee();
        regTradeFee.setTradeId(bussinessId);
        regTradeFee.setFeeType(feeType);
        regTradeFee.setIsPayWithTrade(com.joyintech.tams.constant.DictCodeList.是否_否.getValue());
        regTradeFee.setFeeAmt(amount);
        return regTradeFee;
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
        // 查询指令信息
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entrustId);
        if (etrEntrust == null) {
            massege = "未查询到指令信息！";
        }

        Map<String, Object> params = new HashMap<>();
        params.put("entrustId", entrustId);
        setParam(params);
        // 查询指令
        List<CfetsTrdBondFirstSubEntrustDto> cfetsBondDealEntrustOrderDtos = cfetsTrdBondFirstSub001Mapper.listEntrust(params);
        // 已接收且执行进度<100%的指令可以选择【放弃】接收
        if (CollectionUtils.isNotEmpty(cfetsBondDealEntrustOrderDtos)) {
            for (CfetsTrdBondFirstSubEntrustDto item : cfetsBondDealEntrustOrderDtos) {
                if (!StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_已接收)
                        && BigDecimalUtil.ACompareToB(item.getTransactioncom(), new BigDecimal(100), 2) >= 0) {
                    return "已接收且执行进度<100%的指令,才能执行放弃操作！";
                }
            }
        }
        //是否存在流程中或者有效且数据状态不为 已撤回 已终止
//        int i = cfetsIbuQuoteBondEtrMapper.checkDataStatus(param);
//        if (i > 0) {
//            return "存在流程中的报价，不可放弃！";
//        }
//        //2.查询指令分发的临时表数据  插入  指令分发表，并将状态改为  待接收。
//        List<EtrDistributeInterfTmp> distmpList = cfetsIbuQuoteBondEtrMapper.listEtrDistributeInterfTmp(param);
//        if (!StringUtil.isEmptyList(distmpList)) {
//            boolean status = etrInvestInfoServiceImpl.updateDelegateStatus(entrustId, DictCodeList.委托状态_待接收.getValue());
//            List<EtrDistribute> disList = new ArrayList<EtrDistribute>();
//            for (EtrDistributeInterfTmp entity : distmpList) {
//                EtrDistribute dis = new EtrDistribute();
//                dis.setEntrustId(entity.getEntrustId());
//                dis.setTrader(entity.getTrader());
//                dis.setRecStatus(DictCodeList.委托状态_待接收.getValue());
//                dis.setTradingHeader(entity.getTradingHeader());
//                disList.add(dis);
//            }
//            this.etrInvestInfoServiceImpl.distributeDelegate(disList);
//        } else {
//            boolean status = etrInvestInfoServiceImpl.updateDelegateStatus(entrustId, DictCodeListExt3.委托状态_待分发.getValue());
//        }
        //更新操作记录日志
        CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
        oprHisReq.setEntrustId(entrustId);
        oprHisReq.setOperType(DictCodeListExt3.操作类型_放弃.getValue());
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
        return null;
    }


    /**
     * 退回指令 check
     *
     * @param param
     * @return
     */
    public AjaxResult entrustRefuceCheck(Map<String, Object> param) {
        AjaxResult ajaxResult = new AjaxResult();
        return ajaxResult;
    }

    /**
     * 指令退回
     *
     * @param param
     * @return
     */
    public AjaxResult entrustRefuce(Map<String, Object> param) {
        return null;
    }

    /**
     * 风险试算实体组装
     *
     * @param riskCalRequest
     * @return
     */
    public Map<String, Object> doRiskcalPackage(RiskCalRequest riskCalRequest) {
        Map<String, Object> result = new HashMap<>();
        List<Map> bussTrdTransfenpeiDto = new ArrayList<>();
        for (PtlAllocationDto ptlAllocationDto : riskCalRequest.getPtlAllocations()) {
            Map<String, Object> stringObjectMap = BeanUtil.object2Map(ptlAllocationDto);
            stringObjectMap.put("tradeAmount", ptlAllocationDto.getShareAmt());
            stringObjectMap.put("fpriceAmt", ptlAllocationDto.getShareAmt());
            stringObjectMap.put("portfolioCode", ptlAllocationDto.getPortfolioId());
            bussTrdTransfenpeiDto.add(stringObjectMap);
        }
        result.put("bussTrdTransfenpeiDto", bussTrdTransfenpeiDto);
        result.put("finprodId", riskCalRequest.getFinprodId());
        result.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        result.put("ps", riskCalRequest.getPs());
        result.put("ccy", "CNY");
        return result;

    }

    /**
     * 当日申购(分页列表)
     *
     * @param dto
     * @return
     */
    public Page<CfetsTrdBondFirstDaySubDto> pageDaySub(CfetsTrdBondFirstDaySubRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("loginUserId", LoginUserUtil.getUserName());
        pageParam.put("platDate", iPlatformDate.getSysDateStr());
        pageParam.put("OPR_TYPE_09", DictCodeListExt3.操作类型_导出.getValue());
        setParam(pageParam);
        pageParam.put("CONFIRM_STATUS_0", com.joyintech.tams.constant.DictCodeList.确认状态_未确认.getValue());
//        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsIbuQuoteBondMapper.listDayReg(pageParam));
        return null;
    }

    /**
     * 当日申购(列表)
     *
     * @param pageParam
     * @return
     */
    public List<CfetsBondDayRegDto> listDaySub(Map<String, Object> pageParam) {
        pageParam.put("loginUserId", LoginUserUtil.getUserName());
        pageParam.put("platDate", iPlatformDate.getSysDateStr());
        pageParam.put("OPR_TYPE_09", DictCodeListExt3.操作类型_导出.getValue());
        setParam(pageParam);
        pageParam.put("CONFIRM_STATUS_0", com.joyintech.tams.constant.DictCodeList.确认状态_未确认.getValue());
//        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsIbuQuoteBondMapper.listDayReg(pageParam));
        return null;
    }

    /**
     * 当日申购撤销检查
     *
     * @param cfetsBondRegCheckRequst
     * @return
     */
    public AjaxResult checkDayRegRevokeCheck(CfetsBondRegCheckRequst cfetsBondRegCheckRequst) {
        AjaxResult ajaxResult = new AjaxResult();
        return ajaxResult;
    }

    /**
     * 组装输入参数
     *
     * @param param
     * @author jiangpeng
     * @date 2022/4/19 14:21
     */
    private void setParam(Map<String, Object> param) {
        param.put("ISSUE_CLASS_01", DictCodeListExt4.新债_发行方式_报价发行.getValue());
        param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        param.put("YN_N", DictCodeListExt1.是否_否.getValue());
    }

    /**
     * 分页设置默认值
     *
     * @param pageMode
     * @author jiangpeng
     * @date 2022/4/19 14:22
     */
    private void setPageMode(PageMode pageMode) {
        int pageNo = StringUtil.isNotEmpty(pageMode.getPageNo()) ? 1 : pageMode.getPageNo();//默认第一页
        int pageSize = StringUtil.isNotEmpty(pageMode.getPageSize()) ? 10 : pageMode.getPageSize();//默认每页10条
        pageMode.setPageNo(pageNo);
        pageMode.setPageSize(pageSize);
    }
}

