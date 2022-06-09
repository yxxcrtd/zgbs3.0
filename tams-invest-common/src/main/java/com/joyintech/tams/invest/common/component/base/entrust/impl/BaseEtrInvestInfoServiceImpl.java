/*
 * 文件名：BaseEtrInvestInfoServiceImpl.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：huwei 修改时间：2018年8月28日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.component.base.entrust.impl;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.message.PropertyMessageSource;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.base.entrust.dto.BaseEtrFinishAmtDto;
import com.joyintech.tams.invest.common.component.base.entrust.impl.dao.BaseEtrInvestInfoMapper;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.trade.entrust.EtrFinishStatusDto;
import com.joyintech.tams.invest.common.position.service.CommonPositionEngineService;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 投资委托
 *
 * @author huwei
 * @version 1.0 2018年8月28日
 * @see BaseEtrInvestInfoServiceImpl
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseEtrInvestInfoServiceImpl implements BaseEtrInvestInfoService {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(BaseEtrInvestInfoServiceImpl.class);
    /**
     * 委托指令Mapper
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    /**
     * 委托指令附属表Mapper
     */
    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    @Autowired
    private WflEtrEntrustDetailMapper wflEtrEntrustDetailMapper;

    /**
     * 指令废弃表
     */
    @Autowired
    private EtrEntrustBackupMapper etrEntrustBackupMapper;

    /**
     * 委托指令回购质押品Mapper
     */
    @Autowired
    private EtrPledgeMapper etrPledgeMapper;

    /**
     * 委托完成状况表Mapper
     */
    @Autowired
    private EtrFinishStatusMapper etrFinishStatusMapper;

    /**
     * 委托指令分发Mapper
     */
    @Autowired
    private EtrDistributeMapper etrDistributeMapper;
    /**
     * 委托指令明细
     */
    @Autowired
    private EtrEntrustDetailMapper etrEntrustDetailMapper;

    /**
     * 投资委托信息Mapper
     */
    @Autowired
    private BaseEtrInvestInfoMapper etrInvestInfoMapper;

    /**
     * 金融产品Mapper
     */
    @Autowired
    private FinProductMapper finProductMapper;

    /**
     * 投资委托信息Service
     */
    @Autowired
    private BaseEtrInvestInfoServiceImpl etrInvestInfoServiceImpl;

    /**
     * PropertyMessageSource
     */
    @Autowired
    private PropertyMessageSource messageResource;

    /**
     * 委托投标标位Mapper
     */
    @Autowired
    private EtrEntrustBidPriceMapper etrEntrustBidPriceMapper;
    @Autowired
    private WflEtrEntrustBidPriceMapper wflEtrEntrustBidPriceMapper;

    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;
    /**
     * 截标状态Mapper
     */
    @Autowired
    private BidCloseStatusMapper bidCloseStatusMapper;

    @Autowired
    private BaseEtrInvestInfoMapper baseEtrInvestInfoMapper;

    /**
     * 附件信息共通Service
     */
    @Autowired(required = false)
    private MstFileCommonService mstFileCommonService;

    /**
     * 委托指令流程中Mapper
     */
    @Autowired
    private WflEtrEntrustMapper wflEtrEntrustMapper;

    /**
     * 委托指令附属表流程中Mapper
     */
    @Autowired
    private WflEtrEntrustAddMapper wflEtrEntrustAddMapper;

    /**
     * 委托指令回购质押品流程中Mapper
     */
    @Autowired
    private WflEtrPledgeMapper wflEtrPledgeMapper;

    @Autowired
    private WflEtrEntrustWwDetailMapper wflEtrEntrustWwDetailMapper;

    @Autowired
    private WflEtrEntrustWwMapper wflEtrEntrustWwMapper;

    @Autowired
    private EtrEntrustWwMapper etrEntrustWwMapper;

    @Autowired
    private EtrEntrustWwDetailMapper etrEntrustWwDetailMapper;

    @Autowired
    private EtrDistributeInterfTmpMapper etrDistributeInterfTmpMapper;

    @Autowired
    private EtrEntrustDetailBackUpMapper etrEntrustDetailBackUpMapper;

    @Autowired
    private CommonPositionEngineService commonPositionEngineService;
    /* 委托操作相关校验 <一句话功能简述> <功能详细描述>
     *
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public List<ValidMessage> checkDelegateOperation(String entrustId, String delegateOperationType, List<String> traderList,
                                                     String delegateCloseWay) {
        List<ValidMessage> errorList = new ArrayList<>();
        // 调用查询已生效的委托，获得投资委托信息dto
        BaseEtrInvestInfoDto etrInvestInfoDto = etrInvestInfoServiceImpl.queryActiveMandate(entrustId);
        // 委托操作类型 = “删除”并且投资委托信息dto.委托指令Entity.委托状态不是“新建”、“已撤回”、“已退回”
        if (DictCodeListExt1.委托操作类型_删除.getValue().equals(delegateOperationType)) {
            if (etrInvestInfoDto.getEtrEntrust() != null) {
                if (!(DictCodeList.委托状态_新建.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus())
                      || DictCodeList.委托状态_已撤回.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus())
                      || DictCodeList.委托状态_已退回.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus()))) {
                    errorList.add(new ValidMessage(
                        // “新建”、“已撤回”、“已退回”
                        this.getMessage("ETR_E_0008", this.getMessage("BaseEtrInvestInfoServiceImpl.message01"))));
                }
            }
        }
        // 委托操作类型 = “修改”并且投资委托信息dto.委托指令Entity.委托状态不是“新建”、“已撤回”、“已退回”
        if (DictCodeListExt1.委托操作类型_修改.getValue().equals(delegateOperationType)) {
            if (etrInvestInfoDto.getEtrEntrust() != null) {
                // OPEX-869 【债券一级交易】投标信息确认后不可改。改为：投资经理可以修改除已关闭、已完成、已截标外的所有状态的信息  lizhi start
                if(StringUtil.equals(etrInvestInfoDto.getEtrEntrust().getFinprodType2(),DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue())){
                    BidCloseStatus bidCloseStatus = bidCloseStatusMapper.selectByPrimaryKey(etrInvestInfoDto.getEtrEntrust().getFinprodId());
                    boolean isCloseStatus = false ;
                    if (null != bidCloseStatus && StringUtil.equals(bidCloseStatus.getCloseStatus(),
                        DictCodeListExt3.截标状态_已截标.getValue())){
                        isCloseStatus = true ;
                    }
                    if (DictCodeList.委托状态_已关闭.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus())
                        || DictCodeList.委托状态_已完成.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus())
                        || isCloseStatus) {
                        errorList.add(new ValidMessage(this.getMessage("BaseEtrInvestInfoServiceImpl.message03")));
                    }
                }else{
                    if (!(DictCodeList.委托状态_新建.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus())
                        || DictCodeList.委托状态_已撤回.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus())
                        || DictCodeList.委托状态_已退回.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus()))) {
                        errorList.add(new ValidMessage(
                            // “新建”、“已撤回”、“已退回”
                            this.getMessage("ETR_E_0009", this.getMessage("BaseEtrInvestInfoServiceImpl.message01"))));
                    }
                }
                // OPEX-869 【债券一级交易】投标信息确认后不可改,投资经理可以修改除已关闭、已完成、已截标外的所有状态的信息  lizhi end
            }
        }
        // 委托操作类型 = “撤回”并且投资委托信息dto.委托指令Entity.委托状态不是“待接收”
        if (DictCodeListExt1.委托操作类型_撤回.getValue().equals(delegateOperationType)) {
            if (etrInvestInfoDto.getEtrEntrust() != null) {
                // modify by etliu 20211214 start
                // ZGZGST-4157:待分发的指令点撤回,提示【 该委托状态不是待接收或待分发，无法撤回。】
//                if (!(DictCodeList.委托状态_待接收.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus())
//                		)) {
                if (!(DictCodeList.委托状态_待接收.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus())
                    ) && !(DictCodeListExt3.委托状态_待分发.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus())
                    )) {
                    errorList.add(new ValidMessage(
                        // “待接收”
//                        this.getMessage("ETR_E_0010", this.getMessage("BaseEtrInvestInfoServiceImpl.toReceive"))));
                            this.getMessage("ETR_E_0010", "待接收或待分发")));
                }
                // modify by etliu 20211214 end
            }
        }
        // 委托操作类型 = “分发”并且投资委托信息dto.委托指令Entity.委托状态不是“待接收”
        if (DictCodeListExt1.委托操作类型_分发.getValue().equals(delegateOperationType)) {
            if (etrInvestInfoDto.getEtrEntrust() != null) {
                if (!(DictCodeList.委托状态_待接收.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus())
                		||DictCodeListExt3.委托状态_待分发.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus())
                      || DictCodeList.委托状态_已退回.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus()))) {
                    errorList.add(new ValidMessage(
                        // “待接收”
                        this.getMessage("ETR_E_0003", this.getMessage("BaseEtrInvestInfoServiceImpl.toReceive"))));
                }
            }
        }
        // 委托操作类型 = “分发”并且交易员列表不为空的情况下，遍历投资委托信息dto.List<委托指令分发Entity>,存在相同交易员的情况下
        if (DictCodeListExt1.委托操作类型_分发.getValue().equals(delegateOperationType)) {
            List<EtrDistribute> etrDistributeList = etrInvestInfoDto.getEtrDistributeList();
            Map<String, Object> map = new HashMap<>();
            for (String str : traderList) {
                map.put(str, str);
            }
            for (EtrDistribute etrDistribute : etrDistributeList) {
                if (map.containsKey(etrDistribute.getTrader())) {
                    errorList.add(new ValidMessage(
                        // 该委托分发已存在，无法分发！
                        this.getMessage("ETR_E_0011")));
                    break;
                }
            }
        }
        // 委托操作类型 = “接收”并且投资委托信息dto.委托指令Entity.委托状态不是“待接收”
        if (DictCodeListExt1.委托操作类型_接收.getValue().equals(delegateOperationType)
            && !(DictCodeList.委托状态_待接收.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus())
            		||DictCodeListExt3.委托状态_待分发.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus()))) {
            errorList.add(new ValidMessage(
                // “待接收”
                this.getMessage("ETR_E_0004", this.getMessage("BaseEtrInvestInfoServiceImpl.toReceive"))));
        }
        // 委托操作类型 = “退回”并且投资委托信息dto.委托指令Entity.委托状态不是“已接收”
        if (DictCodeListExt1.委托操作类型_退回.getValue().equals(delegateOperationType)
            && !DictCodeList.委托状态_已接收.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus())) {
            errorList.add(new ValidMessage(
                // “已接收”
                this.getMessage("ETR_E_0005", this.getMessage("BaseEtrInvestInfoServiceImpl.notfinished"))));
        }
        // 委托操作类型 = “关闭”
        if (DictCodeListExt1.委托操作类型_关闭.getValue().equals(delegateOperationType)) {

            // 输入.委托关闭方式 = “人工”并且投资委托信息dto.委托指令Entity.委托状态不是“已退回”
            if (DictCodeListExt1.委托关闭方式_人工.getValue().equals(delegateCloseWay)) {
                if (etrInvestInfoDto.getEtrEntrust() != null) {
                    if (!(DictCodeList.委托状态_已退回.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus())
                            || DictCodeList.委托状态_已接收.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus())
                            || DictCodeList.委托状态_已撤回.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus()))) {
                        errorList.add(new ValidMessage(
                                // “已退回”
                                this.getMessage("ETR_E_0006",
                                        this.getMessage("BaseEtrInvestInfoServiceImpl.hasReturned") + "/"
                                                + this.getMessage("BaseEtrInvestInfoServiceImpl.hasReceive") + "/"
                                                + this.getMessage("BaseEtrInvestInfoServiceImpl.hasWithdrawn"))));
                    }
                }
            }
            // 输入.委托关闭方式 = “自动”
            if (DictCodeListExt1.委托关闭方式_自动.getValue().equals(delegateCloseWay)) {
                // 不做校验
                return errorList;
            }
        }
        // 委托操作类型 = “完成”并且投资委托信息dto.委托指令Entity.委托状态不是“已接收”
        if (DictCodeListExt1.委托操作类型_完成.getValue().equals(delegateOperationType)
            && !DictCodeList.委托状态_已接收.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus())) {
            errorList.add(new ValidMessage(
                // 参数{0}：“已接收”、{1}:“完成”
                this.getMessage("ETR_E_0007", this.getMessage("BaseEtrInvestInfoServiceImpl.hasReceive"),
                    this.getMessage("BaseEtrInvestInfoServiceImpl.accomplish"))));
        }
        // 委托操作类型 = “取消完成”并且投资委托信息dto.委托指令Entity.委托状态不是“已接收”、“已完成”
        if (DictCodeListExt1.委托操作类型_取消完成.getValue().equals(delegateOperationType)) {
            if (!DictCodeList.委托状态_已接收.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus())
                || !DictCodeList.委托状态_已完成.getValue().equals(etrInvestInfoDto.getEtrEntrust().getEntrustStatus())) {
                errorList.add(new ValidMessage(
                    // 参数{0}：“已接收”、“已完成”、{1}:“取消完成”
                    this.getMessage("ETR_E_0007", this.getMessage("BaseEtrInvestInfoServiceImpl.message02"),
                        this.getMessage("BaseEtrInvestInfoServiceImpl.cancelComplete"))));
            }
        }
        // 委托操作类型 = “退回”且已完成部分交易，则不可退回
        if (DictCodeListExt1.委托操作类型_退回.getValue().equals(delegateOperationType)
            && CollectionUtils.isNotEmpty(etrInvestInfoDto.getEtrFinishStatusList())) {
            errorList.add(new ValidMessage(
                // 已有交易，不可再拒绝！
                this.getMessage("ETR_E_0013")));
        }
        return errorList;
    }

    /**
     * <一句话功能简述> <功能详细描述> 返回多条信息
     *
     * @param
     * @param key key
     * @param args args
     * @return key key
     * @see
     * @since 1.0
     */
    private String getMessage(String key, String... args) {
        return messageResource.getMessage(key, args, Locale.SIMPLIFIED_CHINESE);
    }

    /**
     * <一句话功能简述> <功能详细描述> 返回单条信息
     *
     * @param
     * @param key key
     * @return 国际化语言
     * @see
     * @since 1.0
     */
    private String getMessage(String key) {
        return messageResource.getMessage(key, null, Locale.SIMPLIFIED_CHINESE);
    }

    /**
     * 查询已经生效的委托
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public BaseEtrInvestInfoDto queryActiveMandate(String entrustId) {
        BaseEtrInvestInfoDto etrInvestInfoDto = new BaseEtrInvestInfoDto();
//        if (!"null".equals(entrustId)) {
          if(StringUtil.isNotEmpty(entrustId)){
            List<String> entrustIdList = StringUtil.splitToList(GlobalConstant.COMMA, entrustId);
            if (entrustIdList.size() > 1) {
                EtrEntrust etrEntrust = null;
                // 委托指令查询(多组合)
                Map<String, Object> pageParam = new HashMap<String, Object>();
                pageParam.put("entrustIdList", entrustIdList);
                List<EtrEntrust> etrEntrustList = etrInvestInfoMapper.selectByPrimaryKeyPtl(pageParam);
                BigDecimal entrustAmt = new BigDecimal(0);
                for (int i = 0; i < etrEntrustList.size(); i++ ) {
                    if(etrEntrustList.get(i).getEntrustAmt()!=null){entrustAmt = entrustAmt.add(etrEntrustList.get(i).getEntrustAmt());}else{
                         entrustAmt=entrustAmt.add(etrEntrustList.get(i).getEntrustNum());
                     }
                }
                etrEntrustList.get(0).setEntrustAmt(entrustAmt);
                etrEntrust = etrEntrustList.get(0);
                // 设置外置显示dto
                etrInvestInfoDto = queryActiveMandateBySingle(entrustIdList.get(0));
                etrInvestInfoDto.setEtrEntrust(etrEntrust);
                // 多笔委托信息存储容器
                List<BaseEtrInvestInfoDto> baseEtrInvestInfoDtoList = new ArrayList<>();
                etrInvestInfoDto.setBaseEtrInvestInfoDtoList(baseEtrInvestInfoDtoList);
                for (String entrustIdT : entrustIdList) {
                    // 获取单笔委托信息
                    BaseEtrInvestInfoDto etrInvestInfoDtoT = queryActiveMandateBySingle(entrustIdT);
                    baseEtrInvestInfoDtoList.add(etrInvestInfoDtoT);
                }
            }
            else {
                BaseEtrInvestInfoDto etrInvestInfoDtoTemp = queryActiveMandateBySingle(entrustId);
                BeanUtil.copyPropertiesIgnoreNull(etrInvestInfoDto, etrInvestInfoDtoTemp);
                etrInvestInfoDto.setBaseEtrInvestInfoDtoList(
                        Arrays.asList(new BaseEtrInvestInfoDto[] {etrInvestInfoDtoTemp}));
            }
        }

        // // 委托指令附属表查询
        // EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entrustId);
        // // 委托指令分发查询
        // EtrDistributeKey etrDistributeKey = new EtrDistributeKey();
        // etrDistributeKey.setEntrustId(entrustId);
        // List<EtrDistribute> etrDistributeList = etrInvestInfoMapper.selectEtrDistributeByPrimaryKey(
        // etrDistributeKey);
        // // 委托完成状况表查询
        // EtrFinishStatusKey etrFinishStatusKey = new EtrFinishStatusKey();
        // etrFinishStatusKey.setEntrustId(entrustId);
        // List<EtrFinishStatus> etrFinishStatusList = etrInvestInfoMapper.selectEtrFinishStatusByPrimaryKey(
        // etrFinishStatusKey);
        // // 委托指令回购质押品查询
        // EtrPledgeKey etrPledgeKey = new EtrPledgeKey();
        // etrPledgeKey.setEntrustId(entrustId);
        // List<EtrPledge> etrPledgeList = etrInvestInfoMapper.selectEtrPledgeByPrimaryKey(
        // etrPledgeKey);
        // // 将值传入EtrInvestInfoDto
        // BaseEtrInvestInfoDto etrInvestInfoDto = new BaseEtrInvestInfoDto();
        // etrInvestInfoDto.setEtrEntrust(etrEntrust);
        // etrInvestInfoDto.setEtrEntrustAdd(etrEntrustAdd);
        // etrInvestInfoDto.setEtrDistributeList(etrDistributeList);
        // etrInvestInfoDto.setEtrFinishStatusList(etrFinishStatusList);
        // etrInvestInfoDto.setEtrPledgeList(etrPledgeList);
        // if (etrEntrust != null && StringUtil.isNotEmpty(etrEntrust.getFinprodId())) {
        // FinProduct product = finProductMapper.selectByPrimaryKey(etrEntrust.getFinprodId());
        // if(product != null) {
        // etrInvestInfoDto.setFinprodMarketId(product.getFinprodMarketId());
        // }
        // }
        return etrInvestInfoDto;
    }



    /**
     * 查询已经生效的委托
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public String findEtrWordInfo(String entrustId) {
        return etrEntrustMapper.selectByPrimaryKey(entrustId).getBusinessModel();
    }

    /**
     * 查询已经生效的委托 单体
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    public BaseEtrInvestInfoDto queryActiveMandateBySingle(String entrustId) {
        // 委托
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entrustId);
        // 委托指令附属表查询
        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entrustId);
        // 委托指令分发查询
        EtrDistributeKey etrDistributeKey = new EtrDistributeKey();
        etrDistributeKey.setEntrustId(entrustId);
        List<EtrDistribute> etrDistributeList = etrInvestInfoMapper.selectEtrDistributeByPrimaryKey(etrDistributeKey);
        // 委托完成状况表查询
        EtrFinishStatusKey etrFinishStatusKey = new EtrFinishStatusKey();
        etrFinishStatusKey.setEntrustId(entrustId);
        List<EtrFinishStatus> etrFinishStatusList = etrInvestInfoMapper.selectEtrFinishStatusByPrimaryKey(etrFinishStatusKey);
        // 委托指令回购质押品查询
        EtrPledgeKey etrPledgeKey = new EtrPledgeKey();
        etrPledgeKey.setEntrustId(entrustId);
        List<EtrPledge> etrPledgeList = etrInvestInfoMapper.selectEtrPledgeByPrimaryKey(etrPledgeKey);
        // 将值传入EtrInvestInfoDto
        BaseEtrInvestInfoDto etrInvestInfoDto = new BaseEtrInvestInfoDto();
        etrInvestInfoDto.setEtrEntrust(etrEntrust);
        etrInvestInfoDto.setEtrEntrustAdd(etrEntrustAdd);
        etrInvestInfoDto.setEtrDistributeList(etrDistributeList);
        etrInvestInfoDto.setEtrFinishStatusList(etrFinishStatusList);
        etrInvestInfoDto.setEtrPledgeList(etrPledgeList);
        if(etrEntrust != null && null!=etrEntrust.getChlId()){
            etrInvestInfoDto.setChlId(etrEntrust.getChlId());
        }

        if (etrEntrustAdd != null && StringUtil.isNotEmpty(etrEntrustAdd.getrFinprodId())) {
            FinProduct finProduct = finProductMapper.selectByPrimaryKey(etrEntrustAdd.getrFinprodId());
            if (finProduct != null) {
                etrInvestInfoDto.setChangeToFinprodMarketId(finProduct.getFinprodMarketId());
                etrInvestInfoDto.setChangeToFinprodName(StringUtil.isEmpty(finProduct.getFinprodAbbr()) ? finProduct.getFinprodName() : finProduct.getFinprodAbbr());
            }
        }
        if (etrEntrust != null && StringUtil.isNotEmpty(etrEntrust.getFinprodId())) {
            FinProduct product = finProductMapper.selectByPrimaryKey(etrEntrust.getFinprodId());
            if (product != null) {
                etrInvestInfoDto.setFinprodMarketId(product.getFinprodMarketId());
                etrInvestInfoDto.setFinprodId(product.getFinprodId());
            }
        }
        return etrInvestInfoDto;
    }

    /**
     * 新建委托 <一句话功能简述> <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public boolean newDelegate(BaseEtrInvestInfoDto dto) {
        if (dto != null) {
            // 保存委托指令
        	 // 保存list<委托指令分发>
            if (dto.getEtrDistributeList() != null && dto.getEtrDistributeList().size() > 0) {
                for (EtrDistribute etrDistribute : dto.getEtrDistributeList()) {
                    if (!DictCodeList.委托状态_待接收.getValue().equals(etrDistribute.getRecStatus())) {
                        etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                    }
                    etrDistributeMapper.insertSelective(etrDistribute);
                }
            }else if (!DictCodeListExt3.委托状态_待分发.getValue().equals(dto.getEtrEntrust().getEntrustStatus())) {
                dto.getEtrEntrust().setEntrustStatus(DictCodeListExt3.委托状态_待分发.getValue());
            }
            etrEntrustMapper.insertSelective(dto.getEtrEntrust());
            // 保存委托指令附属表
            if (dto.getEtrEntrustAdd() != null&& dto.getEtrEntrustAdd().getTransmitTime()==null) {
                dto.getEtrEntrustAdd().setTransmitTime(new Date());
            }
            etrEntrustAddMapper.insertSelective(dto.getEtrEntrustAdd());
            // 保存List<委托指令回购质押品>
            if (dto.getEtrPledgeList() != null && dto.getEtrPledgeList().size() > 0) {
                for (EtrPledge etrPledge : dto.getEtrPledgeList()) {
                    etrPledgeMapper.insertSelective(etrPledge);
                }
            }
            // 保存list<委托指令分发>
//            if (dto.getEtrDistributeList() != null && dto.getEtrDistributeList().size() > 0) {
//                for (EtrDistribute etrDistribute : dto.getEtrDistributeList()) {
//                    if (!DictCodeList.委托状态_待接收.getValue().equals(etrDistribute.getRecStatus())) {
//                        etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
//                    }
//                    etrDistributeMapper.insertSelective(etrDistribute);
//                }
//            }
            // 保存List<委托完成状况表>
            if (dto.getEtrFinishStatusList() != null && dto.getEtrFinishStatusList().size() > 0) {
                for (EtrFinishStatus etrFinishStatus : dto.getEtrFinishStatusList()) {
                    etrFinishStatusMapper.insertSelective(etrFinishStatus);
                }
            }

            // 保存List<委托投标标位表>
            if (dto.getBidPriceList() != null && !dto.getBidPriceList().isEmpty()) {
                for (EtrEntrustBidPrice etrEntrustBidPrice : dto.getBidPriceList()) {
                    etrEntrustBidPrice.setBidPrice(etrEntrustBidPrice.getBidPrice());
                    etrEntrustBidPriceMapper.insertSelective(etrEntrustBidPrice);
                }
            }

            // 保存List<委托明细表>
            if (CollectionUtils.isNotEmpty(dto.getEtrEntrustDetailList())) {
                for (EtrEntrustDetail etrEntrustDetail : dto.getEtrEntrustDetailList()) {
                    etrEntrustDetailMapper.insertSelective(etrEntrustDetail);
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean newWwDelegate(BaseEtrInvestInfoDto dto) {
        if (dto != null) {
            // 保存委托指令
            if (!DictCodeList.委托状态_待接收.getValue().equals(dto.getEtrEntrustWw ().getEntrustStatus())) {
                dto.getEtrEntrustWw ().setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            }
            etrEntrustWwMapper.insertSelective(dto.getEtrEntrustWw ());
            // 保存委托指令附属表

            if(dto.getEtrEntrustWwDetailList () != null){
                for(int i = 0; i < dto.getEtrEntrustWwDetailList ().size (); i++){
                    EtrEntrustWwDetail etrEntrustWwDetail = new EtrEntrustWwDetail();
                    BeanUtil.copyPropertiesIgnoreNull(etrEntrustWwDetail,dto.getEtrEntrustWwDetailList().get (i));
                    etrEntrustWwDetailMapper.insertSelective(etrEntrustWwDetail);
                }
            }


            // 保存List<委托指令回购质押品>
            if (dto.getEtrPledgeList() != null && dto.getEtrPledgeList().size() > 0) {
                for (EtrPledge etrPledge : dto.getEtrPledgeList()) {
                    etrPledgeMapper.insertSelective(etrPledge);
                }
            }
            // 保存list<委托指令分发>
            if (dto.getEtrDistributeList() != null && dto.getEtrDistributeList().size() > 0) {
                for (EtrDistribute etrDistribute : dto.getEtrDistributeList()) {
                    if (!DictCodeList.委托状态_待接收.getValue().equals(etrDistribute.getRecStatus())) {
                        etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                    }
                    etrDistributeMapper.insertSelective(etrDistribute);
                }
            }
            // 保存List<委托完成状况表>
            if (dto.getEtrFinishStatusList() != null && dto.getEtrFinishStatusList().size() > 0) {
                for (EtrFinishStatus etrFinishStatus : dto.getEtrFinishStatusList()) {
                    etrFinishStatusMapper.insertSelective(etrFinishStatus);
                }
            }

            // 保存List<委托投标标位表>
            if (dto.getBidPriceList() != null && !dto.getBidPriceList().isEmpty()) {
                for (EtrEntrustBidPrice etrEntrustBidPrice : dto.getBidPriceList()) {
                    etrEntrustBidPrice.setBidPrice(etrEntrustBidPrice.getBidPrice());
                    etrEntrustBidPriceMapper.insertSelective(etrEntrustBidPrice);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 删除委托 <一句话功能简述> <功能详细描述>
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public boolean deleteDelegate(String entrustId) {
        if (entrustId != null) {
            // 删除委托指令
            etrEntrustMapper.deleteByPrimaryKey(entrustId);
            // 删除委托指令附属
            etrEntrustAddMapper.deleteByPrimaryKey(entrustId);
            // 删除委托指令回购质押品
            EtrPledgeKey etrPledgeKey = new EtrPledgeKey();
            etrPledgeKey.setEntrustId(entrustId);
            etrInvestInfoMapper.deleteEtrPledgeByPrimaryKey(etrPledgeKey);
            // 删除委托指令分发
            EtrDistribute etrDistribute = new EtrDistribute();
            etrDistribute.setEntrustId(entrustId);
            etrInvestInfoMapper.deleteEtrDistributeByPrimaryKey(etrDistribute);
            // 删除委托完成状况表
            EtrFinishStatus etrFinishStatus = new EtrFinishStatus();
            etrFinishStatus.setEntrustId(entrustId);
            etrInvestInfoMapper.deleteEtrFinishStatusByPrimaryKey(etrFinishStatus);
            // 全删全插委托投标标位表
            etrInvestInfoMapper.deleteBidPrice(entrustId);
            // 全删全插委托明细表表
            etrInvestInfoMapper.deleteEntrustDetail(entrustId);
            // 判断删除委托是否全部执行成功
            if (baseEtrCashPositionCommService != null) {
                baseEtrCashPositionCommService.entrustCloseExecute(entrustId);
            }
            return true;
        }
        return false;
    }

    /**
     * 删除委托 <一句话功能简述> <功能详细描述>
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public boolean deleteWwDelegate(String entrustId) {
        if (entrustId != null) {
            // 删除委托指令
            etrEntrustWwMapper.deleteByPrimaryKey(entrustId);
            // 删除委托指令附属
            etrEntrustWwDetailMapper.deleteByPrimaryKey(entrustId);
            // 删除委托指令回购质押品
            EtrPledgeKey etrPledgeKey = new EtrPledgeKey();
            etrPledgeKey.setEntrustId(entrustId);
            baseEtrInvestInfoMapper.deleteEtrPledgeByPrimaryKey(etrPledgeKey);
            // 删除委托指令分发
            EtrDistribute etrDistribute = new EtrDistribute();
            etrDistribute.setEntrustId(entrustId);
            baseEtrInvestInfoMapper.deleteEtrDistributeByPrimaryKey(etrDistribute);
            // 删除委托完成状况表
            EtrFinishStatus etrFinishStatus = new EtrFinishStatus();
            etrFinishStatus.setEntrustId(entrustId);
            baseEtrInvestInfoMapper.deleteEtrFinishStatusByPrimaryKey(etrFinishStatus);
            // 全删全插委托投标标位表
            baseEtrInvestInfoMapper.deleteBidPrice(entrustId);
            // 判断删除委托是否全部执行成功
            if (baseEtrCashPositionCommService != null) {
                baseEtrCashPositionCommService.entrustCloseExecute(entrustId);
            }
            return true;
        }
        return false;
    }

    /**
     * 修改委托 <一句话功能简述> <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public boolean updateDelegate(BaseEtrInvestInfoDto dto) {
        if (dto != null) {
            // 调用删除委托接口（投资委托信息dto.委托指令Entity.委托代码）
            etrInvestInfoServiceImpl.deleteDelegate(dto.getEtrEntrust().getEntrustId());
            // 调用新建委托接口
            etrInvestInfoServiceImpl.newDelegate(dto);
            // 判断修改委托是否执行成功
            return true;
        }
        return false;
    }

    @Override
    public boolean updateWwDelegate(BaseEtrInvestInfoDto dto) {
        if (dto != null) {
            // 调用删除委托接口（投资委托信息dto.委托指令Entity.委托代码）
            etrInvestInfoServiceImpl.deleteWwDelegate(dto.getEtrEntrustWw ().getEntrustId());
            // 调用新建委托接口
            etrInvestInfoServiceImpl.newWwDelegate(dto);
            // 判断修改委托是否执行成功
            return true;
        }
        return false;
    }

    /**
     * 更新委托状态 <一句话功能简述> <功能详细描述>
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public boolean updateDelegateStatus(String entrustId, String entrustStatus) {
        // 委托指令查询
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entrustId);
        // 更改委托状态
        etrEntrust.setEntrustStatus(entrustStatus);
        // 修改委托指令表中值
        int flag = etrEntrustMapper.updateByPrimaryKeySelective(etrEntrust);
        // 修改委托指令分发表中值 add by yangwenjun 20201209
        baseEtrInvestInfoMapper.updatEetrDistribute(etrEntrust);
        // 判断更新委托状态执行成功
        if (flag == 1) {
            return true;
        }
        return false;
    }

    /**
     * 更新委托状态 <一句话功能简述> <功能详细描述>
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public boolean updateWwDelegateStatus(String entrustId, String entrustStatus) {
        // 委托指令查询
        EtrEntrustWw etrEntrust = etrEntrustWwMapper.selectByPrimaryKey(entrustId);
        // 更改委托状态
        etrEntrust.setEntrustStatus(entrustStatus);
        // 修改委托指令表中值
        int flag = etrEntrustWwMapper.updateByPrimaryKeySelective(etrEntrust);
        // 判断更新委托状态执行成功
        if (flag == 1) {
            return true;
        }
        return false;
    }

    /**
     * 撤回委托 <一句话功能简述> <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public boolean recallDelegate(BaseEtrInvestInfoDto dto) {
        // 将传来dto中的委托指令的委托状态改为"已撤回"
        dto.getEtrEntrust().setEntrustStatus(DictCodeList.委托状态_已撤回.getValue());
        // 调用投资委托组件的更新委托状态接口，将委托状态设置为"已撤回"
        boolean flag = etrInvestInfoServiceImpl.updateDelegateStatus(dto.getEtrEntrust().getEntrustId(),
            dto.getEtrEntrust().getEntrustStatus());
        // 判断撤回委托是否执行成功
        if (baseEtrCashPositionCommService != null) {
            baseEtrCashPositionCommService.entrustCloseExecute(dto.getEtrEntrust().getEntrustId());
        }
        if (flag) {
            return true;
        }
        return false;
    }

    /**
     * 分发委托 <一句话功能简述> <功能详细描述>
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public boolean distributeDelegate(List<EtrDistribute> list) {
        // 保存list<委托指令分发>
        if (CollectionUtils.isNotEmpty(list)) {
            etrInvestInfoMapper.deletByList(list);
            for (EtrDistribute etrDistribute : list) {
                etrDistributeMapper.insertSelective(etrDistribute);
            }
            return true;
        }
        return false;
    }

    /**
     * 接收委托 <一句话功能简述> <功能详细描述>
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public boolean receptionDelegate(List<EtrDistribute> list) {
        if (list != null && list.size() > 0) {
            // 接收该委托的交易员的接收状态设为已接收，其他交易员的委托数据删除
            for (EtrDistribute etrDistribute : list) {
                etrInvestInfoServiceImpl.updateDelegateStatus(etrDistribute.getEntrustId(), DictCodeList.委托状态_已接收.getValue());
                EtrDistributeKey key = new EtrDistributeKey();
                key.setEntrustId(etrDistribute.getEntrustId());
                List<EtrDistribute> etrDistributeList = etrInvestInfoMapper.selectEtrDistributeByPrimaryKey(key);
                //删除临时分发表 ETR_DISTRIBUTE_INTERF_TMP
                baseEtrInvestInfoMapper.deleteEtrDistributeInterfTmp(etrDistribute.getEntrustId());
                for (EtrDistribute entity : etrDistributeList) {
                    if (StringUtil.equals(entity.getTrader(), etrDistribute.getTrader())) {
                        Map<String, Object> param2 = new HashMap<String, Object>();
                        param2.put("ENTRUST_RECEIVE_TYPE_HASCANCEL", DictCodeList.委托状态_已接收.getValue());
                        param2.put("entrustId", entity.getEntrustId());
                        param2.put("trader", entity.getTrader());
                        etrInvestInfoMapper.updateEtrDistributeByPrimaryKeySelective(param2);
                    }
                    else {
                        EtrDistributeKey etrDistributeKey = new EtrDistributeKey();
                        etrDistributeKey.setEntrustId(entity.getEntrustId());
                        etrDistributeKey.setTrader(entity.getTrader());
                        etrInvestInfoMapper.deleteEtrDistributeByPrimaryKey(etrDistributeKey);
                    }
                    //存入临时表ETR_DISTRIBUTE_INTERF_TMP,本币交易放弃指令后到待接收状态,需要找个指令对应的交易员
                    EtrDistributeInterfTmp etrDistributeInterfTmp = new EtrDistributeInterfTmp();
                    BeanUtil.copyPropertiesIgnoreNull(etrDistributeInterfTmp,entity);
                    etrDistributeInterfTmpMapper.insert(etrDistributeInterfTmp);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 退回委托 <一句话功能简述> <功能详细描述>
     *
     * @param reason 退回原因
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public boolean returnDelegate(List<EtrDistribute> list, String reason) {
        // 将委托状态更新为已退回，交易员的委托数据删除
        if (list != null && list.size() > 0) {
            for (EtrDistribute etrDistribute : list) {
                EtrDistributeKey key = new EtrDistributeKey();
                key.setEntrustId(etrDistribute.getEntrustId());
                List<EtrDistribute> etrDistributeList = etrInvestInfoMapper.selectEtrDistributeByPrimaryKey(key);
                // 更新委托表的委托状态
                etrInvestInfoServiceImpl.updateDelegateStatus(etrDistribute.getEntrustId(), DictCodeList.委托状态_已退回.getValue());
                // 委托退回原因
                etrInvestInfoServiceImpl.updateBackReason(etrDistribute.getEntrustId(), reason);
                // 删除交易员的委托数据
                for (EtrDistribute entity : etrDistributeList) {
                    entity.setRecStatus(DictCodeList.委托状态_已退回.getValue());
                    etrDistributeMapper.updateByPrimaryKeySelective(entity);
                    // etrDistributeMapper.deleteByPrimaryKey(entity);
                }
                if (baseEtrCashPositionCommService != null) {
                    baseEtrCashPositionCommService.entrustCloseExecute(etrDistribute.getEntrustId());
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 关闭委托 <一句话功能简述> <功能详细描述>
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public boolean closeDelegate(String entrustId, String closeWay) {
        EtrEntrust ee = etrEntrustMapper.selectByPrimaryKey(entrustId);
        if (ee != null) {
            // 调用投资委托组件的更新委托状态接口，将委托状态设置为“已关闭”
            String tempEntrustStatu = ee.getEntrustStatus();// 获取该委托原始状态，防止后续更改成关闭
            boolean flag = etrInvestInfoServiceImpl.updateDelegateStatus(entrustId, DictCodeList.委托状态_已关闭.getValue());

            if (baseEtrCashPositionCommService != null && DictCodeList.委托状态_已接收.getValue().equals(tempEntrustStatu)) {
                baseEtrCashPositionCommService.entrustCloseExecute(entrustId);
            }

            // 判断关闭委托是否完成
            if (flag) {
                return true;
            }
        }
        return false;
    }

    /**
     * 关闭委托 <一句话功能简述> <功能详细描述>
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public boolean closeWwDelegate(String entrustId, String closeWay) {
        EtrEntrustWw ee = etrEntrustWwMapper.selectByPrimaryKey(entrustId);
        if (ee != null) {
            // 调用投资委托组件的更新委托状态接口，将委托状态设置为“已关闭”
            String tempEntrustStatu = ee.getEntrustStatus();// 获取该委托原始状态，防止后续更改成关闭
            boolean flag = etrInvestInfoServiceImpl.updateWwDelegateStatus(entrustId, DictCodeList.委托状态_已关闭.getValue());

            if (baseEtrCashPositionCommService != null && DictCodeList.委托状态_已接收.getValue().equals(tempEntrustStatu)) {
                baseEtrCashPositionCommService.entrustCloseExecute(entrustId);
            }

            // 判断关闭委托是否完成
            if (flag) {
                return true;
            }
        }
        return false;
    }


    /**
     * 委托完成状况查询 <一句话功能简述> <功能详细描述>
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public List<BaseEtrFinishAmtDto> queryDelegateStatus(List<String> entrustIdList) {
        // 根据委托代码，查询“委托完成状况表”，汇总完成额度，获取委托完成额度
        List<BaseEtrFinishAmtDto> etrFinishAmtDtoList = new ArrayList<BaseEtrFinishAmtDto>();
        for (String entrustId : entrustIdList) {
            // 查询总完成额度
            BaseEtrFinishAmtDto etrFinishAmtDto = etrInvestInfoMapper.selectEtrFinishAmtDtoByPrimaryKey(entrustId);

            // 查询委托额度
            EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entrustId);
            // 指令已完成状态更新逻辑修改 modify by yufei 20200807 start
            EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entrustId);
            if (etrFinishAmtDto != null && etrEntrust != null) {
                BigDecimal 委托额度 = BigDecimal.ZERO;
                // 根据指令的目标类型判断
                if(StringUtil.equals(etrEntrust.getPs(),DictCodeListExt3.交易方向_解除质押.getValue())
                        || StringUtil.equals(etrEntrust.getPs(),DictCodeListExt3.交易方向_中途换券.getValue())){
                    // 解除质押/中途换券 委托额度取值修改成质押数量 add by yufei 2020-10-16
                    EtrPledgeKey key = new EtrPledgeKey();
                    key.setEntrustId(entrustId);
                    key.setFinprodId(etrEntrust.getFinprodId());
                    EtrPledge etrPledge = etrPledgeMapper.selectByPrimaryKey(key);
                    if(etrPledge != null){
                        委托额度 = etrPledge.getPledgeNumber();
                    }
                }
                //交易所竞价回购默认是已数量下单，委托完成度表中交易入库数据是已金额入的库，这里特殊处理下。不然拿数量比对金额，指令状态会不对
                else if(etrEntrustAdd != null && (StringUtil.equals(etrEntrust.getTradeMarket(),DictCodeListExt1.交易场所_上海证券交易所.getValue())
                        ||StringUtil.equals(etrEntrust.getTradeMarket(),DictCodeListExt1.交易场所_深圳证券交易所.getValue()))
                && StringUtil.equals(etrEntrustAdd.getAssetType(),DictCodeList.平安资产类型_质押式回购.getValue())
                && StringUtil.equals(etrEntrustAdd.getAssetType1(),"H02")) {
                    委托额度 = etrEntrust.getEntrustAmt();
                }
                else if(etrEntrustAdd != null && StringUtil.isNotEmpty(etrEntrustAdd.getTargetType())){
                    if(StringUtil.equals(DictCodeListExt3.目标类型_数量.getValue(),etrEntrustAdd.getTargetType())){
                        // 如果是数量则取份额来判断是否已完成
                        委托额度 = etrEntrust.getEntrustNum();
                    }
                    else {
                        // 否则取份额来判断是否已完成
                        委托额度 = etrEntrust.getEntrustAmt();
                    }
                }
                else {
                    if(etrEntrustAdd != null){
                        if (StringUtils.equals(DictCodeListExt3.平安资产类型_期货.getValue(), etrEntrustAdd.getAssetType())) {
                            if (StringUtils.equals(DictCodeListExt3.平安资产类型_期货.getValue(), etrEntrustAdd.getAssetType())) {
                                委托额度 = etrEntrust.getEntrustNum();
                            } else {
                                委托额度 = etrEntrust.getEntrustAmt();
                            }
                        }
                    }else{
                        委托额度 = etrEntrust.getEntrustAmt();
                    }
                	/*// 如果是期货 则按数量来判断  add by lijie2 20201120 start
					if (StringUtils.equals(DictCodeListExt3.平安资产类型_期货.getValue(), etrEntrustAdd.getAssetType())) {
						委托额度 = etrEntrust.getEntrustNum();
					} else {
						//add by lijie2 20201120 end
						// 如果不存在目标类型，则按照原来的逻辑获取委托额度
						委托额度 = etrEntrust.getEntrustAmt();
					}*/
                  /*  if (委托额度 == null) {
                        委托额度 = etrEntrust.getEntrustNum();
                    }*/
                }
                // 指令已完成状态更新逻辑修改 modify by yufei 20200807 end
                BigDecimal 总交易完成度 = ArithmeticUtil.divide(etrFinishAmtDto.getSumFinishAmt(),
                    委托额度, 4);
                总交易完成度 = ArithmeticUtil.multiply(总交易完成度, BigDecimal.valueOf(100));
                etrFinishAmtDto.setSumFinishPercent(总交易完成度);

                etrFinishAmtDtoList.add(etrFinishAmtDto);
            }

        }
        return etrFinishAmtDtoList;
    }

    /**
     * 完成委托 <一句话功能简述> <功能详细描述>
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public boolean completeDelegate(List<EtrFinishStatusDto> etrFinishStatusList) {
        if (CollectionUtils.isEmpty(etrFinishStatusList)) {
            return false;
        }
        // ZGCPCN-209:日间数据系列户多组合数据EntrustId和TradeId相同,需合并 modify by fuyongsheng 20220426 start
        etrFinishStatusList = mergeEtrFinishStatusList(etrFinishStatusList);
        // ZGCPCN-209:日间数据系列户多组合数据EntrustId和TradeId相同,需合并 modify by fuyongsheng 20220426 end
        // 是否日终批处理数据
        boolean isDayEndBatch = etrFinishStatusList.get(0).isDayEndBatch();
        // 遍历List<委托完成状况表Entity>，保存到“委托完成状况表”
        int count = 0;
        for (EtrFinishStatusDto etrFinishStatus : etrFinishStatusList) {
            if(StringUtil.isNotBlank(etrFinishStatus.getCstpTradeId())){
                EtrFinishStatusKey key = new EtrFinishStatusKey();
                key.setEntrustId(etrFinishStatus.getEntrustId());
                key.setTradeId(etrFinishStatus.getCstpTradeId());
                EtrFinishStatus etrFinishStatus1 = etrFinishStatusMapper.selectByPrimaryKey(key);
                if(etrFinishStatus1!=null){
                    etrFinishStatusMapper.deleteByPrimaryKey(key);
                }
            }
            int i = etrFinishStatusMapper.insertSelective(etrFinishStatus);
            count = count + i;
        }
        // 调用委托完成状况查询
        List<String> entrustIdList = new ArrayList<String>();
        for (EtrFinishStatus etrFinishStatus : etrFinishStatusList) {
            entrustIdList.add(etrFinishStatus.getEntrustId());
        }
        entrustIdList = entrustIdList.stream().distinct().collect(Collectors.toList());
        List<BaseEtrFinishAmtDto> etrFinishAmtDtoList = etrInvestInfoServiceImpl.queryDelegateStatus(entrustIdList);
        // 判断委托代码的完成额度是否大于等于100
        BigDecimal bigDecimal = new BigDecimal("100.00");
        // 指令完成关闭
        Map<String, Object> batchClose = new HashMap<>();
        // 完成指令关闭列表
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (BaseEtrFinishAmtDto etrFinishAmtDto : etrFinishAmtDtoList) {
            if (etrFinishAmtDto.getSumFinishPercent().compareTo(bigDecimal) == 1
                || etrFinishAmtDto.getSumFinishPercent().compareTo(bigDecimal) == 0) {
                // 如果满足，则调用投资委托组件的更新委托状态接口，将委托状态设置为“已完成”
                boolean result = etrInvestInfoServiceImpl.updateDelegateStatus(etrFinishAmtDto.getEntrustId(), DictCodeList.委托状态_已完成.getValue());
                // ZGTFCP-1529:指令完成后调用新头寸批量关闭接口 modify by fuyongsheng 20220327 start
                // 只有日终批处理数据才需调用新头寸接口
                if(result && isDayEndBatch){
                    EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(etrFinishAmtDto.getEntrustId());
                    EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(etrFinishAmtDto.getEntrustId());
                    if(etrEntrust != null){
                        Map<String, Object> data = new HashMap<>();
                        data.put("busiType", commonPositionEngineService.getPositionBusiType(etrEntrust.getFinprodType2(), etrEntrust.getPs(),
                                etrEntrustAdd.getAssetType1()));
                        data.put("ps", etrEntrust.getPs());
                        data.put("closeId", etrEntrust.getEntrustId());
                        data.put("systemAct", DictCodeListExt3.系统动作_完成.getValue());
                        data.put("busiAct", DictCodeListExt3.业务动作_指令自动完成.getValue());
                        dataList.add(data);
                    }
                }
            }
        }
        if(CollectionUtils.isNotEmpty(dataList)){
            batchClose.put("data", dataList);
            batchClose.put("userId", LoginUserUtil.getUserName());
            batchClose.put("totalTradeId", IDGenerator.generate());
            batchClose.put("modelType",DictCodeListExt1.子系统代码_委托.getValue());
            logger.debug("【批量关闭指令数据】batchRevoke:{}", JSON.toJSONString(batchClose));
            PositionHttpRequest.batchClose(batchClose);
        }
        // ZGTFCP-1529:指令完成后调用新头寸批量关闭接口 modify by fuyongsheng 20220327 end
        // 判断完成委托是否完成
        if (count == etrFinishStatusList.size()) {
            return true;
        }
        return false;
    }

    // ZGCPCN-209:日间数据系列户多组合数据EntrustId和TradeId相同,需合并 modify by fuyongsheng 20220426 start
    /**
     * 合并EntrustId和TradeId相同的数据
     * @param etrFinishStatusList
     * @return
     */
    private List<EtrFinishStatusDto> mergeEtrFinishStatusList(List<EtrFinishStatusDto> etrFinishStatusList){
        List<EtrFinishStatusDto> result = new ArrayList<>();
        Map<String, List<EtrFinishStatusDto>> mapList = etrFinishStatusList
                .stream()
                .collect(Collectors.groupingBy(dto -> dto.getEntrustId() + "_" + dto.getTradeId()));
        for(String key : mapList.keySet()){
            List<EtrFinishStatusDto> dtos = mapList.get(key);
            BigDecimal mergeFinishAmt = BigDecimal.ZERO;
            BigDecimal mergeEntFinishAmt = BigDecimal.ZERO;
            BigDecimal mergeOrderFinishAmt = BigDecimal.ZERO;
            for(EtrFinishStatusDto dto : dtos){
                mergeFinishAmt = ArithmeticUtil.doAdd(mergeFinishAmt, dto.getFinishAmt());
                mergeEntFinishAmt = ArithmeticUtil.doAdd(mergeEntFinishAmt, dto.getEntFinishAmt());
                mergeOrderFinishAmt = ArithmeticUtil.doAdd(mergeOrderFinishAmt, dto.getOrderFinishAmt());
            }
            EtrFinishStatusDto mergeEtrFinishStatus = new EtrFinishStatusDto();
            BeanUtil.copyPropertiesIgnoreNull(mergeEtrFinishStatus, dtos.get(0));
            mergeEtrFinishStatus.setFinishAmt(mergeFinishAmt);
            mergeEtrFinishStatus.setEntFinishAmt(mergeEntFinishAmt);
            mergeEtrFinishStatus.setOrderFinishAmt(mergeOrderFinishAmt);
            result.add(mergeEtrFinishStatus);
        }
        return result;
    }
    // ZGCPCN-209:日间数据系列户多组合数据EntrustId和TradeId相同,需合并 modify by fuyongsheng 20220426 end

    /**
     * 取消完成委托 <一句话功能简述> <功能详细描述>
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public boolean cancelCompleteDelegate(String entrustId, String tradeId) {
        // 删除委托完成状况表
        EtrFinishStatus etrFinishStatus = new EtrFinishStatus();
        etrFinishStatus.setEntrustId(entrustId);
        etrFinishStatus.setTradeId(tradeId);
        int count = baseEtrInvestInfoMapper.deleteEtrFinishStatusByPrimaryKey(etrFinishStatus);
        // 指令释放后撤销交易，如果指令是待接收状态，则不改变指令状态 modify by yufei 20200804 start
        // 委托指令查询
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entrustId);
        if (etrEntrust != null) {
	        String entrustStatus = DictCodeList.委托状态_已接收.getValue();
	        if(StringUtil.equals(etrEntrust.getEntrustStatus(),DictCodeList.委托状态_待接收.getValue())){
	            entrustStatus = DictCodeList.委托状态_待接收.getValue();
	        }
	        if(StringUtil.equals(etrEntrust.getEntrustStatus(),DictCodeListExt3.委托状态_待分发.getValue())){
	            entrustStatus = DictCodeListExt3.委托状态_待分发.getValue();
	        }
	        // 调用投资委托组件的更新委托状态接口，将委托状态设置为“已接收”
	        boolean flag = etrInvestInfoServiceImpl.updateDelegateStatus(entrustId, entrustStatus);
	        // 指令释放后撤销交易，如果指令是待接收状态，则不改变指令状态 modify by yufei 20200804 end
	        // 判断取消完成委托是否完成
	        if (flag && count >= 1) {
	            return true;
	        }
        }
        return false;
    }

    @Override
    public boolean updateBackReason(String entrustId, String reason) {
        if (StringUtil.isNotBlank(entrustId) && StringUtil.isNotBlank(reason)) {
            // 委托指令查询
            EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entrustId);
            //退回备注不覆盖之前的备注 edit by xueying 20200725 start
            // update by qk start HFZG-6 质押式回购退回，指令分发详情页面中的备注显示异常
            String remark=StringUtil.isEmpty(etrEntrust.getRemark()) ? "" : etrEntrust.getRemark();
            // update by qk end HFZG-6 质押式回购退回，指令分发详情页面中的备注显示异常
            if(StringUtil.isNotBlank(reason)){
                reason=remark+"\n"+"指令退回原因：" + reason;
            }else{
                reason="指令退回原因：" + reason;
            }
            // 更改委托状态
            etrEntrust.setRemark(reason);
            //退回备注不覆盖之前的备注 edit by xueying 20200725 end
            // 修改委托指令表中值
            int flag = etrEntrustMapper.updateByPrimaryKeySelective(etrEntrust);
            // 判断更新委托状态执行成功
            if (flag == 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BaseEtrFinishAmtDto selectEtrFinishAmtDtoByPrimaryKey(String entrustId) {
        return etrInvestInfoMapper.selectEtrFinishAmtDtoByPrimaryKey(entrustId);
    }

	@Override
    public String findInvAim(String portfolioId, String finprodId) {
        String invAim = baseEtrInvestInfoMapper.findInvAimByPortIdAndFinId(portfolioId, finprodId);
        if (StringUtil.isEmpty(invAim)) {
            invAim = baseEtrInvestInfoMapper.findInvAimByPortIdAndFinType(portfolioId, finprodId);
        }
        return invAim;
    }

    @Override
    public boolean newDelegateFlow(BaseEtrInvestInfoDto dto) {
        if (dto != null) {
            WflEtrEntrust entrust = new WflEtrEntrust();
            BeanUtil.copyPropertiesIgnoreNull(entrust,dto.getEtrEntrust());
            entrust.setWorkflowId(dto.getWorkFlowId());
            entrust.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflEtrEntrustMapper.insertSelective(entrust);
            // 保存委托指令附属表
            WflEtrEntrustAdd entrustAdd = new WflEtrEntrustAdd();
            BeanUtil.copyPropertiesIgnoreNull(entrustAdd,dto.getEtrEntrustAdd());
            entrustAdd.setWorkflowId(dto.getWorkFlowId());
            entrustAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            entrustAdd.setEntrustCreateSource(dto.getEtrEntrustAdd().getEntrustCreateSource());
            wflEtrEntrustAddMapper.insertSelective(entrustAdd);
            // 保存List<委托指令回购质押品>
            if (dto.getEtrPledgeList() != null && dto.getEtrPledgeList().size() > 0) {
                for (EtrPledge etrPledge : dto.getEtrPledgeList()) {
                    WflEtrPledge wflEtrPledge = new WflEtrPledge();
                    BeanUtil.copyPropertiesIgnoreNull(wflEtrPledge,etrPledge);
                    wflEtrPledge.setWorkflowId(dto.getWorkFlowId());
                    wflEtrPledge.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflEtrPledgeMapper.insertSelective(wflEtrPledge);
                }
            }
            // 保存List<委托投标标位表>
            if (dto.getBidPriceList() != null && !dto.getBidPriceList().isEmpty()) {
                for (EtrEntrustBidPrice etrEntrustBidPrice : dto.getBidPriceList()) {
                    etrEntrustBidPrice.setBidPrice(etrEntrustBidPrice.getBidPrice());
                    WflEtrEntrustBidPrice wflEtrEntrustBidPrice = new WflEtrEntrustBidPrice();
                    BeanUtil.copyPropertiesIgnoreNull(wflEtrEntrustBidPrice,etrEntrustBidPrice);
                    wflEtrEntrustBidPrice.setWorkflowId(dto.getWorkFlowId());
                    wflEtrEntrustBidPrice.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflEtrEntrustBidPriceMapper.insertSelective(wflEtrEntrustBidPrice);
                }
            }
            //保存List<委托指令明细>
            if(!StringUtil.isEmptyList(dto.getEtrEntrustDetailList())){
                for(EtrEntrustDetail etrEntrustDetail : dto.getEtrEntrustDetailList()) {
                    WflEtrEntrustDetail wflEtrEntrustDetail = new WflEtrEntrustDetail();
                    BeanUtil.copyPropertiesIgnoreNull(wflEtrEntrustDetail,etrEntrustDetail);
                    wflEtrEntrustDetail.setWorkflowId(dto.getWorkFlowId());
                    wflEtrEntrustDetail.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflEtrEntrustDetailMapper.insertSelective(wflEtrEntrustDetail);
                }
            }
            if (mstFileCommonService != null && dto.getMstFileInputParameter() != null) {
                dto.getMstFileInputParameter().setBussinessId(dto.getWorkFlowId());
                mstFileCommonService.updateBusinessId(dto.getMstFileInputParameter());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean newWwDelegateFlow(BaseEtrInvestInfoDto dto) {
        if (dto != null) {
            WflEtrEntrustWw entrustWw = new WflEtrEntrustWw();
            BeanUtil.copyPropertiesIgnoreNull(entrustWw,dto.getEtrEntrustWw ());
            entrustWw.setWorkflowId(dto.getWorkFlowId());
            entrustWw.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflEtrEntrustWwMapper.insertSelective(entrustWw);
            // 保存委托指令附属表

            if(dto.getEtrEntrustWwDetailList () != null){
                for(int i = 0; i < dto.getEtrEntrustWwDetailList ().size (); i++){
                    WflEtrEntrustWwDetail etrEntrustWwDetail = new WflEtrEntrustWwDetail();
                    BeanUtil.copyPropertiesIgnoreNull(etrEntrustWwDetail,dto.getEtrEntrustWwDetailList().get (i));
                    etrEntrustWwDetail.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflEtrEntrustWwDetailMapper.insertSelective(etrEntrustWwDetail);
                }
            }


            // 保存List<委托指令回购质押品>
            if (dto.getEtrPledgeList() != null && dto.getEtrPledgeList().size() > 0) {
                for (EtrPledge etrPledge : dto.getEtrPledgeList()) {
                    WflEtrPledge wflEtrPledge = new WflEtrPledge();
                    BeanUtil.copyPropertiesIgnoreNull(wflEtrPledge,etrPledge);
                    wflEtrPledge.setWorkflowId(dto.getWorkFlowId());
                    wflEtrPledge.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflEtrPledgeMapper.insertSelective(wflEtrPledge);
                }
            }
            // 保存List<委托投标标位表>
            if (dto.getBidPriceList() != null && !dto.getBidPriceList().isEmpty()) {
                for (EtrEntrustBidPrice etrEntrustBidPrice : dto.getBidPriceList()) {
                    etrEntrustBidPrice.setBidPrice(etrEntrustBidPrice.getBidPrice());
                    WflEtrEntrustBidPrice wflEtrEntrustBidPrice = new WflEtrEntrustBidPrice();
                    BeanUtil.copyPropertiesIgnoreNull(wflEtrEntrustBidPrice,etrEntrustBidPrice);
                    wflEtrEntrustBidPrice.setWorkflowId(dto.getWorkFlowId());
                    wflEtrEntrustBidPrice.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflEtrEntrustBidPriceMapper.insertSelective(wflEtrEntrustBidPrice);
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean deleteWwDelegateFlow(Map<String, Object> param) {
        String workflowId = (String)param.get("workflowId");
        if (workflowId != null) {
            // 删除委托指令
            wflEtrEntrustWwMapper.deleteByWorkflowId(workflowId);
            // 删除委托指令附属
            wflEtrEntrustWwDetailMapper.deleteByWorkflowId(workflowId);
            // 删除委托指令回购质押品
            wflEtrPledgeMapper.deleteByWorkflowId(workflowId);
            // 全删委托投标标位表
            wflEtrEntrustBidPriceMapper.deleteByWorkflowId(workflowId);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteDelegateFlow(Map<String, Object> param) {
        String workflowId = (String)param.get("workflowId");
        if (workflowId != null) {
            // 删除委托指令
            wflEtrEntrustMapper.deleteByWorkflowId(workflowId);
            // 删除委托指令附属
            wflEtrEntrustAddMapper.deleteByWorkflowId(workflowId);
            // 删除委托指令回购质押品
            wflEtrPledgeMapper.deleteByWorkflowId(workflowId);
            // 全删委托投标标位表
            wflEtrEntrustBidPriceMapper.deleteByWorkflowId(workflowId);
            //场内多组合系列户指令需要删除委托指令明细流程表
            wflEtrEntrustDetailMapper.deleteByWorkflowId(workflowId);
            return true;
        }
        return false;
    }

    /**
     * 撤回或退回再启动流程时转移至废弃备份表
     * 流程表数据入库
     * @param dto
     * @return
     */
    public boolean insertWithQuery(BaseEtrInvestInfoDto dto){
        EtrEntrust etr = etrEntrustMapper.selectByPrimaryKey(dto.getEtrEntrust().getEntrustId());
        if(etr != null){
//            if(StringUtil.equals(etr.getEntrustStatus(), DictCodeList.委托状态_已退回.getValue()) ||
//                    StringUtil.equals(etr.getEntrustStatus(), DictCodeList.委托状态_已撤回.getValue())){
//            }
            /**
             * 此处，流程启动时，检查指令有效表，发现有数据，正常情况为已退回或已撤回状态的数据，转移至备份表
             */
            // 有效数据转移
            // 转移至废弃表
            etrEntrustBackupMapper.deleteByPrimaryKey(dto.getEtrEntrust().getEntrustId());
            etrEntrustBackupMapper.deleteAddByPrimaryKey(dto.getEtrEntrust().getEntrustId());
            etrEntrustBackupMapper.deleteEntrustBidPriceByEntrustId(dto.getEtrEntrust().getEntrustId());
            etrEntrustBackupMapper.deleteEntrustDetailByEntrustId(dto.getEtrEntrust().getEntrustId());
            etrEntrustBackupMapper.insertFromQuery(dto.getEtrEntrust().getEntrustId());
            etrEntrustBackupMapper.insertAddFromQuery(dto.getEtrEntrust().getEntrustId());
            etrEntrustBackupMapper.insertBidPriceFromQuery(dto.getEtrEntrust().getEntrustId());
            etrEntrustBackupMapper.insertDetailFromQuery(dto.getEtrEntrust().getEntrustId());
            etrEntrustMapper.deleteByPrimaryKey(dto.getEtrEntrust().getEntrustId());
            etrEntrustAddMapper.deleteByPrimaryKey(dto.getEtrEntrustAdd().getEntrustId());
            etrInvestInfoMapper.deleteBidPrice(dto.getEtrEntrust().getEntrustId());
            etrInvestInfoMapper.deleteEntrustDetail(dto.getEtrEntrust().getEntrustId());
            // 使用退回或撤回状态
            dto.getEtrEntrust().setEntrustStatus(etr.getEntrustStatus());
        }else{
            dto.getEtrEntrust().setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
        }

        // 指令流程表入库
        return newDelegateFlow(dto);
    }
    /**
     * 撤回或退回再启动流程时转移至废弃备份表
     * 流程表数据入库
     * @param dto
     * @return
     */
    public void inserBackuptWithQuery(BaseEtrInvestInfoDto dto){
        EtrEntrust etr = etrEntrustMapper.selectByPrimaryKey(dto.getEtrEntrust().getEntrustId());
        if(etr != null){
            /**
             * 此处，流程启动时，检查指令有效表，发现有数据，正常情况为已退回或已撤回状态的数据，转移至备份表
             */
            // 有效数据转移
            // 转移至废弃表
            etrEntrustBackupMapper.deleteByPrimaryKey(dto.getEtrEntrust().getEntrustId());
            etrEntrustBackupMapper.deleteAddByPrimaryKey(dto.getEtrEntrust().getEntrustId());
            etrEntrustBackupMapper.deleteDetailByPrimaryKey(dto.getEtrEntrust().getEntrustId());
            etrEntrustBackupMapper.insertFromQuery(dto.getEtrEntrust().getEntrustId());
            etrEntrustBackupMapper.insertAddFromQuery(dto.getEtrEntrust().getEntrustId());
            List<EtrEntrustDetail> list=etrEntrustDetailMapper.selectList(dto.getEtrEntrust().getEntrustId());
            if(!CollectionUtils.isEmpty(list)){
                for(EtrEntrustDetail etrEntrustDetail:list){
                    EtrEntrustDetailBackUp etrEntrustDetailBackUp = new EtrEntrustDetailBackUp();
                    BeanUtil.copyPropertiesIgnoreNull(etrEntrustDetailBackUp,etrEntrustDetail);
                    etrEntrustDetailBackUpMapper.insert(etrEntrustDetailBackUp);
                }
            }
            etrEntrustMapper.deleteByPrimaryKey(dto.getEtrEntrust().getEntrustId());
            etrEntrustAddMapper.deleteByPrimaryKey(dto.getEtrEntrust().getEntrustId());
            etrEntrustDetailMapper.deleteByEntrustId(dto.getEtrEntrust().getEntrustId());
        }
    }
}

