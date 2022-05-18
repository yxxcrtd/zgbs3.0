package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.util.BigDecimalUtil;
import com.joyintech.tams.cfets.etrdispatcher.service.CfetsEtrDispatcherService;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondEtrMapper;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dao.CfetsTrdFirstBidFirm001Mapper;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdBondFirstBidFirmEntrustRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmEntrustDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.service.CfetsTrdBondFirstSub001Service;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrDistributeInterfTmp;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.invest.common.component.base.entrust.impl.BaseEtrInvestInfoServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.joyintech.tams.invest.trade.bondtostock.service.TrdBondToStock001Service.setParam;

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
     * 退回指令 check
     * @param param
     * @return
     */
    public AjaxResult entrustRefuceCheck(Map<String, Object> param) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        if (!StringUtil.isNotEmpty(param.get("remark"))) {
            errorList.add(new ValidMessage("请输入退回原因!"));
        }
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }

    /**
     * 退回指令
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
        param.put("ISSUE_CLASS_01", DictCodeListExt4.新债_发行方式_报价发行.getValue());
        param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        param.put("YN_N", DictCodeListExt1.是否_否.getValue());
        param.put("entrustIdList",param.get("entrustIds"));
        String loginUserId = LoginUserUtil.getUserName();
        param.put("loginUserId", loginUserId);
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsTrdFirstBidFirm001Mapper.listEntrust(param));
    }
}
