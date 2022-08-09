/*
 * 文件名：EtrSecLendService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.entrust.etrseclend.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.service.entrust.etrseclend.dto.BussEtrInvestPersonInfoDto;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.dao.EtrTargetMapper;
import com.joyintech.tams.common.base.dao.WflEtrTargetMapper;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.EtrPledge;
import com.joyintech.tams.common.base.entity.EtrTarget;
import com.joyintech.tams.common.base.entity.WflEtrTarget;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.entrust.common.dao.EntrustCommonPersonSelectMapper;
import com.joyintech.service.entrust.etrseclend.dao.EtrSecLendMapper;
import com.joyintech.service.entrust.etrseclend.dto.EtrSecLendDto;
import com.joyintech.tams.component.base.entrust.BusiAutoEtrDispService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;

/**
 * 债券投资委托微服务端service 〈功能详细描述〉
 * 
 * @author wangzhuangzhuang
 * @version 1.0 2019年7月5日
 * @see EtrSecLendService
 * @since 1.0
 */
@Service("backEtrSecLendService")
public class EtrSecLendService extends BaseService {

    /**
     * 委托人员信息
     */
    @Autowired
    private EntrustCommonPersonSelectMapper entrustCommonPersonSelectMapper;
    /**
     * 委托指令标的信息
     */
    @Autowired
    private EtrTargetMapper etrTargetMapper;
    
    @Autowired
    private WflEtrTargetMapper wflEtrTargetMapper;
    /**
     * 附件信息共通Service
     */
    @Autowired(required = false)
    private MstFileCommonService mstFileCommonService;

    /**
     * 自定义债券投资委托mapper
     */
    @Autowired(required = false)
    private EtrSecLendMapper etrSecLendMapper;

    /**
     * 投资委托基础组件
     */
    @Autowired(required = false)
    private BaseEtrInvestInfoService baseEtrInvestInfoService;

    /**
     * 组合可用持仓和实际持仓查询更新组件接口
     */
    @Autowired(required = false)
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;
    /**
     * 平台日期
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 委托自动分发
     */
    @Autowired(required = false)
    private BusiAutoEtrDispService busiAutoEtrDispService;

    @Autowired(required=false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;


    /**
     * 债券借贷委托投资分页查询 <功能详细描述>
     * 
     * @param param
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<EtrSecLendDto> selectForPage(int page, int length,
                                                    Map<String, Object> param)
    {
        param.put("ENTRUST_STATUS_08",DictCodeListExt3.委托状态_流程中.getValue());
        param.put("TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue());
        param.put("bondList", Arrays.asList(DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue(),DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue()));
        Page<EtrSecLendDto> pageDto = queryForPage(page, length,
            () -> etrSecLendMapper.selectForPage(param));
        return pageDto;
    }
    /**
     * 资产代码：下拉框
     *
     * @param params 画面参数
     * @return 资产代码：下拉框
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodId(int page, int length, Map<String, Object> param)
    {
        param.put("FINPROD_TYPE_TG", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        // 可选择的债券标的只能是银行间 add by renping 20190920 start
        param.put("FINPROD_MARKET",DictCodeListExt1.交易场所_银行间.getValue());
        param.put("FINPROD_WIND_MARKET",DictCodeListExt1.交易场所_WIND_银行间.getValue());
        // 可选择的债券标的只能是银行间 add by renping 20190920 end
        return this.queryForPage(page, length,
            () -> etrSecLendMapper.getFinprodId(param));

    }
    /**
     * 查询证券托管户：联想框文本框
     *
     * @param param 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getSecManageAcctId(Map<String, Object> param) {
        return this.queryForSuggest(() -> etrSecLendMapper.getSecManageAcctId(param),
            null);
    }
    /**
     * 画面校验 <功能详细描述>
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtrSecLendDto dto) {
        String entrustId = dto.getEntrustId();
        String delegateOperationType = dto.getOperateType();
        String delegateCloseWay = dto.getCloseWay();
        List<String> traderList = new ArrayList<>();
        traderList.add(dto.getTrader());
        return baseEtrInvestInfoService.checkDelegateOperation(entrustId, delegateOperationType,
            traderList, delegateCloseWay);
    }

    /**
     * 撤回委托
     * 
     * @param dto 实体类
     * @return boolean
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @see
     * @since 1.0
     */
    public boolean recallDelegate(EtrSecLendDto dto)
        throws IllegalAccessException, InvocationTargetException {
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
        EtrEntrust etrEntrust = new EtrEntrust();
        EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
        BeanUtil.copyProperties(etrEntrust, dto);
        BeanUtil.copyProperties(etrEntrustAdd, dto);
        List<EtrDistribute> etrDistributeList = new ArrayList<>();
        EtrDistribute etrDistribute = new EtrDistribute();
        BeanUtil.copyProperties(etrDistribute, dto);
        etrDistributeList.add(etrDistribute);
        entity.setEtrEntrust(etrEntrust);
        entity.setEtrEntrustAdd(etrEntrustAdd);
        entity.setEtrDistributeList(etrDistributeList);
        return baseEtrInvestInfoService.recallDelegate(entity);
    }

    /**
     * 关闭委托 <一句话功能简述> <功能详细描述>
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public boolean closeDelegate(String entrustId, String closeWay) {
        return baseEtrInvestInfoService.closeDelegate(entrustId, closeWay);
    }

    /**
     * 删除投资委托 <功能详细描述>
     * 
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public boolean deleteDelegate(String entrustId) {
        return baseEtrInvestInfoService.deleteDelegate(entrustId);
    }

    /**
     * 根据主键查询修改/查看画面信息 <功能详细描述>
     * 
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public EtrSecLendDto selectByKey(String entrustId, String finprodId,
                                            String portfolioId) {
        Map<String, Object> map = new HashMap<String, Object>();
        BussEtrInvestPersonInfoDto dto = new BussEtrInvestPersonInfoDto();
        map.put("entrustId", entrustId);
        map.put("finprodId", finprodId);
        map.put("portfolioId", portfolioId);
        List<String> traderList = entrustCommonPersonSelectMapper.getTraderList(map);
        EtrSecLendDto etrInvestBondInfoDto = etrSecLendMapper.selectByKey(map);
        if(etrInvestBondInfoDto == null) {
        	etrInvestBondInfoDto = etrSecLendMapper.selectWflByKey(map);
        }
        String trader = "";
        for (int i = 0; i < traderList.size(); i++ ) {
            trader = trader + "," + traderList.get(i);
        }
        etrInvestBondInfoDto.getEtrInvestPersonInfo().setTrader(trader);
        return etrInvestBondInfoDto;
    }

    /**
     * 工作流完成方法 <功能详细描述>
     * 
     * @param dto
     * @param operationType
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @see
     * @since 1.0
     */
    public void doComplete(EtrSecLendDto dto, String operationType)
        throws IllegalAccessException, InvocationTargetException {
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
        EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
        EtrEntrust etrEntrust = new EtrEntrust();
        EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
        EtrTarget etrTarget=new EtrTarget();
        if (StringUtil.equals(operationType, "NEW")) {
            dto.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
            BeanUtil.copyProperties(etrEntrust, dto.getEtrInvestPersonInfo());
            
           if(dto.getBusiPs().equals(DictCodeList.回购拆借方向_融入.getValue())){
               etrEntrust.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
           }else if(dto.getBusiPs().equals(DictCodeList.回购拆借方向_融出.getValue())){
               etrEntrust.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());
           }
           etrEntrust.setTradeMarket(dto.getTradeMarket());
           etrEntrust.setPs(DictCodeList.交易方向_买入.getValue());
           etrEntrust.setIsOutTrade(DictCodeList.是否_是.getValue());
           etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
//           etrEntrust.setEntrustAmt(dto.getTotalAmt().multiply(new BigDecimal(10000)));
           etrEntrust.setEntrustAmt(dto.getTotalAmt());
           etrEntrust.setEntrustNum(dto.getTotalAmt());
           etrEntrust.setAvailableNum(dto.getObjectAvailablePosition());
           etrEntrust.setPrice(dto.getBusiRate());
           etrEntrust.setCalendarId(dto.getCalendarId());
            
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_0.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)0);
            }
            if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_1.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)1);
            }
            etrEntrustAdd.setTermType(dto.getTermType());
            etrEntrustAdd.setAssetType(DictCodeListExt2.平安资产类型_债券借贷.getValue());
            etrEntrustAdd.setBusiPs(dto.getBusiPs());
            etrEntrustAdd.setSecAcctId(dto.getSecAcctId());
            
            BeanUtil.copyProperties(etrTarget, dto);
            etrTarget.setTargetFinprodId(dto.getFinprodId());
            //标的金融产品类型???
//            etrTarget.setTargetFaceValue(dto.getTotalAmt().multiply(new BigDecimal(10000)));
            etrTarget.setTargetFaceValue(dto.getTotalAmt());
            etrTargetMapper.insert(etrTarget);
            
            List<EtrDistribute> etrDistributeList = new ArrayList<>();
            if (StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo().getTrader())) {

                List<String> traderList = StringUtil.splitToList(GlobalConstant.COMMA,
                    dto.getEtrInvestPersonInfo().getTrader());
                for (String trader : traderList) {
                    EtrDistribute etrDistribute = new EtrDistribute();
                    // 设置接收状态
                    etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                    // 设置指定交易员指定交易员 = 指定交易员
                    etrDistribute.setTrader(trader);
                    // 设置委托代码
                    etrDistribute.setEntrustId(dto.getEntrustId());
                    //
                    etrDistributeList.add(etrDistribute);
                }
                // entity.setEtrDistributeList(etrDistributeList);
            }
            // 自动分发
            if (busiAutoEtrDispService != null) {
                // 检测是否设置交易员
                if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                    // 跳出自动分发
                    entity.setEtrDistributeList(etrDistributeList);
                }
                else {
                    // 查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                    if (busiAutoEtrDispService.checkEntrustTime(etrEntrust)) {
                        // 自动分发
                        entity.setEtrDistributeList(
                            busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));
                    }
                }
            }
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            //插入数据表 add  by yeming 20210817 start
            if( dto.getBussRepoSec().getTotalPrinAmt()  != null){
                dto.getBussRepoSec().setTotalPrinAmt(ArithmeticUtil.multiply( dto.getBussRepoSec().getTotalPrinAmt(),new BigDecimal(10000)));
            }
            if(StringUtil.isNotEmpty(dto.getBussRepoSec()) && !StringUtil.isEmptyList(dto.getBussRepoSec().getPledgeBondList())) {
                for (PledgeBondInfoDto pledgeBond : dto.getBussRepoSec().getPledgeBondList()) {
                    if (pledgeBond.getAvaPledgePrin() != null) {
                        pledgeBond.setAvaPledgePrin(ArithmeticUtil.multiply(pledgeBond.getAvaPledgePrin(), new BigDecimal(10000)));
                    }
                    if (pledgeBond.getPledgePrin() != null) {
                        pledgeBond.setPledgePrin(ArithmeticUtil.multiply(pledgeBond.getPledgePrin(), new BigDecimal(10000)));
                    }
                }
            }
            List<EtrPledge> etrPledgeList = new ArrayList<>();
            if(StringUtil.isNotEmpty(dto.getBussRepoSec()) && !StringUtil.isEmptyList(dto.getBussRepoSec().getPledgeBondList())) {
                for(PledgeBondInfoDto pledgeBond : dto.getBussRepoSec().getPledgeBondList()) {
                    EtrPledge etrPledge = new EtrPledge();
                    // 委托代码 = 委托代码
                    etrPledge.setEntrustId(dto.getEntrustId());
                    // 金融产品代码 = 输出质押品DTO.金融产品代码
                    etrPledge.setFinprodId(pledgeBond.getFinprodId());
                    // 质押面值 = 输出质押品DTO.质押面值
                    etrPledge.setPledgeFaceValue(new BigDecimal("100"));
                    // 质押数量 = 输出质押品DTO.质押数量
                    etrPledge.setPledgeNumber(ArithmeticUtil.divide(pledgeBond.getPledgePrin(), new BigDecimal("100"), 4));
                    // 质押率 = 输出质押品DTO.质押率
                    etrPledge.setPledgeRatio(ArithmeticUtil.divide(pledgeBond.getPledgeRatio(), new BigDecimal("100"), 4));
                    // 质押金额 = 输出质押品DTO.质押金额
                    etrPledge.setPledgeAmt(ArithmeticUtil.multiply(pledgeBond.getPledgePrin(), etrPledge.getPledgeRatio()));
                    etrPledgeList.add(etrPledge);
                }
            }
            entity.setEtrPledgeList(etrPledgeList);
            //插入数据表 add  by yeming 20210817 end
            baseEtrInvestInfoService.newDelegate(entity);
            // 文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());
            if (mstFileCommonService != null) {
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
        }
        else {
            dto.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
            BeanUtil.copyProperties(etrEntrust, dto.getEtrInvestPersonInfo());
            
           if(dto.getBusiPs().equals(DictCodeList.回购拆借方向_融入.getValue())){
               etrEntrust.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
           }else if(dto.getBusiPs().equals(DictCodeList.回购拆借方向_融出.getValue())){
               etrEntrust.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());
           }
           etrEntrust.setTradeMarket(dto.getTradeMarket());
           etrEntrust.setPs(DictCodeList.交易方向_买入.getValue());
           etrEntrust.setIsOutTrade(DictCodeList.是否_是.getValue());
           etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
//           etrEntrust.setEntrustAmt(dto.getTotalAmt().multiply(new BigDecimal(10000)));
           etrEntrust.setEntrustAmt(dto.getTotalAmt());
           etrEntrust.setEntrustNum(dto.getTotalAmt());
           etrEntrust.setAvailableNum(dto.getObjectAvailablePosition());
           etrEntrust.setPrice(dto.getBusiRate());
           etrEntrust.setCalendarId(dto.getCalendarId());
            
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_0.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)0);
            }
            if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_1.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)1);
            }
            etrEntrustAdd.setTermType(dto.getTermType());
            etrEntrustAdd.setAssetType(DictCodeListExt2.平安资产类型_债券借贷.getValue());
            etrEntrustAdd.setBusiPs(dto.getBusiPs());
            etrEntrustAdd.setSecAcctId(dto.getSecAcctId());
            
            BeanUtil.copyProperties(etrTarget, dto);
            etrTarget.setTargetFinprodId(dto.getFinprodId());
            //标的金融产品类型???
            etrTarget.setTargetFaceValue(dto.getTotalAmt());
//            etrTarget.setTargetFaceValue(dto.getTotalAmt().multiply(new BigDecimal(10000)));
            etrTargetMapper.updateByPrimaryKey(etrTarget);
            
            List<EtrDistribute> etrDistributeList = new ArrayList<>();
            if (StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo().getTrader())) {
                List<String> traderList = StringUtil.splitToList(GlobalConstant.COMMA,
                    dto.getEtrInvestPersonInfo().getTrader());
                for (String trader : traderList) {
                    EtrDistribute etrDistribute = new EtrDistribute();
                    // 设置接收状态
                    etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                    // 设置指定交易员指定交易员 = 指定交易员
                    etrDistribute.setTrader(trader);
                    // 设置委托代码
                    etrDistribute.setEntrustId(dto.getEntrustId());
                    //
                    etrDistributeList.add(etrDistribute);
                }
                // entity.setEtrDistributeList(etrDistributeList);
            }
            // 自动分发
            if (busiAutoEtrDispService != null) {
                // 检测是否设置交易员
                if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                    // 跳出自动分发
                    entity.setEtrDistributeList(etrDistributeList);
                }
                else {
                    // 查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                    if (busiAutoEtrDispService.checkEntrustTime(etrEntrust)) {
                        // 自动分发
                        entity.setEtrDistributeList(
                            busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));
                    }
                }
            }
            if( dto.getBussRepoSec().getTotalPrinAmt()  != null){
                dto.getBussRepoSec().setTotalPrinAmt(ArithmeticUtil.multiply( dto.getBussRepoSec().getTotalPrinAmt(),new BigDecimal(10000)));
            }
            if(StringUtil.isNotEmpty(dto.getBussRepoSec()) && !StringUtil.isEmptyList(dto.getBussRepoSec().getPledgeBondList())) {
                for (PledgeBondInfoDto pledgeBond : dto.getBussRepoSec().getPledgeBondList()) {
                    if (pledgeBond.getAvaPledgePrin() != null) {
                        pledgeBond.setAvaPledgePrin(ArithmeticUtil.multiply(pledgeBond.getAvaPledgePrin(), new BigDecimal(10000)));
                    }
                    if (pledgeBond.getPledgePrin() != null) {
                        pledgeBond.setPledgePrin(ArithmeticUtil.multiply(pledgeBond.getPledgePrin(), new BigDecimal(10000)));
                    }
                }
            }
            List<EtrPledge> etrPledgeList = new ArrayList<>();
            if(StringUtil.isNotEmpty(dto.getBussRepoSec()) && !StringUtil.isEmptyList(dto.getBussRepoSec().getPledgeBondList())) {
                for(PledgeBondInfoDto pledgeBond : dto.getBussRepoSec().getPledgeBondList()) {
                    EtrPledge etrPledge = new EtrPledge();
                    // 委托代码 = 委托代码
                    etrPledge.setEntrustId(dto.getEntrustId());
                    // 金融产品代码 = 输出质押品DTO.金融产品代码
                    etrPledge.setFinprodId(pledgeBond.getFinprodId());
                    // 质押面值 = 输出质押品DTO.质押面值
                    etrPledge.setPledgeFaceValue(new BigDecimal("100"));
                    // 质押数量 = 输出质押品DTO.质押数量
                    etrPledge.setPledgeNumber(ArithmeticUtil.divide(pledgeBond.getPledgePrin(), new BigDecimal("100"), 4));
                    // 质押率 = 输出质押品DTO.质押率
                    etrPledge.setPledgeRatio(ArithmeticUtil.divide(pledgeBond.getPledgeRatio(), new BigDecimal("100"), 4));
                    // 质押金额 = 输出质押品DTO.质押金额
                    etrPledge.setPledgeAmt(ArithmeticUtil.multiply(pledgeBond.getPledgePrin(), etrPledge.getPledgeRatio()));
                    etrPledgeList.add(etrPledge);
                }
            }
            entity.setEtrPledgeList(etrPledgeList);
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            baseEtrInvestInfoService.updateDelegate(entity);
            // 文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());
            if (mstFileCommonService != null) {
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
        }
        /*//委托开始日对委托的持仓流水和现金流水做处理
        if(baseEtrCashPositionCommService!=null){
            entrustCommonDto.setEtrEntrust(etrEntrust);
            entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
            entrustCommonDto.setBussRepoSecDto(dto.getBussRepoSec());
            baseEtrCashPositionCommService.entrustStartExecute(entrustCommonDto);
        }*/
    }

    /**
     * 流程启动时为附件绑定工作流代码 <功能详细描述>
     * 
     * @param dto
     * @param operateType
     * @see
     * @since 1.0
     */
    public void doInsert(EtrSecLendDto dto, String operateType) {
    	//添加流程中数据 add by yzg 20210628 start 
    	BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
    	entity.setWorkFlowId(dto.getWorkflowId());
        EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
        EtrEntrust etrEntrust = new EtrEntrust();
        EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
        WflEtrTarget wflEtrTarget=new WflEtrTarget();
        if (StringUtil.equals(operateType, "submit") || StringUtil.equals(operateType, "NEW") || StringUtil.equals(operateType, "MODIFY") ) {
            dto.setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, dto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, dto.getEtrInvestPersonInfo());
            
           if(dto.getBusiPs().equals(DictCodeList.回购拆借方向_融入.getValue())){
               etrEntrust.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
           }else if(dto.getBusiPs().equals(DictCodeList.回购拆借方向_融出.getValue())){
               etrEntrust.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());
           }
           etrEntrust.setTradeMarket(dto.getTradeMarket());
           etrEntrust.setPs(DictCodeList.交易方向_买入.getValue());
           etrEntrust.setIsOutTrade(DictCodeList.是否_是.getValue());
           etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
//           etrEntrust.setEntrustAmt(dto.getTotalAmt().multiply(new BigDecimal(10000)));
           etrEntrust.setEntrustAmt(dto.getTotalAmt());
           if(StringUtil.isNotEmpty(dto.getTotalAmt())){
               etrEntrust.setEntrustNum(dto.getTotalAmt().multiply(new BigDecimal(100)));
           }
           etrEntrust.setAvailableNum(dto.getObjectAvailablePosition());
           etrEntrust.setPrice(dto.getBusiRate());
           etrEntrust.setCalendarId(dto.getCalendarId());
            
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, dto);
            if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_0.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)0);
            }
            if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_1.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)1);
            }
            etrEntrustAdd.setTermType(dto.getTermType());
            etrEntrustAdd.setAssetType(DictCodeListExt2.平安资产类型_债券借贷.getValue());
            etrEntrustAdd.setBusiPs(dto.getBusiPs());
            etrEntrustAdd.setSecAcctId(dto.getSecAcctId());
            
            BeanUtil.copyPropertiesIgnoreNull(wflEtrTarget, dto);
            wflEtrTarget.setTargetFinprodId(dto.getFinprodId());
            //标的金融产品类型???
//            wflEtrTarget.setTargetFaceValue(dto.getTotalAmt().multiply(new BigDecimal(10000)));
            wflEtrTarget.setTargetFaceValue(dto.getTotalAmt());
            wflEtrTargetMapper.insert(wflEtrTarget);
            //质押券信息
            //万元转换 add  by yeming 20210817 start
            if( dto.getBussRepoSec().getTotalPrinAmt()  != null){
                dto.getBussRepoSec().setTotalPrinAmt(ArithmeticUtil.multiply( dto.getBussRepoSec().getTotalPrinAmt(),new BigDecimal(10000)));
            }
            if(dto.getBussRepoSec() !=null && dto.getBussRepoSec().getPledgeBondList() !=null){
                for (PledgeBondInfoDto pledgeBond :  dto.getBussRepoSec().getPledgeBondList()) {
                    if (pledgeBond.getAvaPledgePrin() != null) {
                        pledgeBond.setAvaPledgePrin(ArithmeticUtil.multiply(pledgeBond.getAvaPledgePrin(), new BigDecimal(10000)));
                    }
                    if (pledgeBond.getPledgePrin() != null) {
                        pledgeBond.setPledgePrin(ArithmeticUtil.multiply(pledgeBond.getPledgePrin(), new BigDecimal(10000)));
                    }
                }
            }
            //万元转换 add  by yeming 20210817 end
            List<EtrPledge> etrPledgeList = new ArrayList<>();
            if(StringUtil.isNotEmpty(dto.getBussRepoSec()) && !StringUtil.isEmptyList(dto.getBussRepoSec().getPledgeBondList())) {
            	for(PledgeBondInfoDto pledgeBond : dto.getBussRepoSec().getPledgeBondList()) {
		            EtrPledge etrPledge = new EtrPledge();
		            // 委托代码 = 委托代码
		            etrPledge.setEntrustId(dto.getEntrustId());
		            // 金融产品代码 = 输出质押品DTO.金融产品代码
		            etrPledge.setFinprodId(pledgeBond.getFinprodId());
		            // 质押面值 = 输出质押品DTO.质押面值
		            etrPledge.setPledgeFaceValue(new BigDecimal("100"));
		            // 质押数量 = 输出质押品DTO.质押数量
		            etrPledge.setPledgeNumber(ArithmeticUtil.divide(pledgeBond.getPledgePrin(), new BigDecimal("100"), 4));
		            // 质押率 = 输出质押品DTO.质押率
		            etrPledge.setPledgeRatio(ArithmeticUtil.divide(pledgeBond.getPledgeRatio(), new BigDecimal("100"), 4));
		            // 质押金额 = 输出质押品DTO.质押金额
		            etrPledge.setPledgeAmt(ArithmeticUtil.multiply(pledgeBond.getPledgePrin(), etrPledge.getPledgeRatio()));
		            etrPledgeList.add(etrPledge);
            	}
            }
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            entity.setEtrPledgeList(etrPledgeList);
            baseEtrInvestInfoService.newDelegateFlow(entity);
            //委托开始日对委托的持仓流水和现金流水做处理
            if(baseEtrCashPositionCommService!=null){
                entrustCommonDto.setEtrEntrust(etrEntrust);
                entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
                entrustCommonDto.setBussRepoSecDto(dto.getBussRepoSec());
                baseEtrCashPositionCommService.entrustStartExecute(entrustCommonDto);
            }
        }
    	//添加流程中数据 add by yzg 20210628 end
        // 上传文件修改更新
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setBussinessId(dto.getWorkflowId());
        mstFileInputParameter.setFileList(dto.getFileArray());
        if (mstFileCommonService != null) {
            mstFileCommonService.updateBusinessId(mstFileInputParameter);
        }
    }
    
    /**
     * 删除流程中数据和流水
     * @param dto
     */
    public void doDeleteFlow(EtrSecLendDto dto) {
    	//删除流水
    	baseEtrCashPositionCommService.entrustCloseExecute(dto.getEntrustId());
    	//删除流程中数据
    	Map<String, Object> param = new HashMap<>();
    	param.put("workflowId", dto.getWorkflowId());
    	etrSecLendMapper.deleteWflwflEtrTarget(dto.getWorkflowId());
    	baseEtrInvestInfoService.deleteDelegateFlow(param);
    }

    /**
     * 删除流程中数据和流水
     * @param dto
     */
    public void doDeleteWfl(EtrSecLendDto dto) {
        //删除流水
//        baseEtrCashPositionCommService.entrustCloseExecute(dto.getEntrustId());
        //删除流程中数据
        Map<String, Object> param = new HashMap<>();
        param.put("workflowId", dto.getWorkflowId());
        etrSecLendMapper.deleteWflwflEtrTarget(dto.getWorkflowId());
        baseEtrInvestInfoService.deleteDelegateFlow(param);
    }


    /**
     * 组合可用持仓查询 <功能详细描述>
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> getAssetAvailablePosition(Map<String, Object> param) {
        PtlSecPositionFlowDto dto = new PtlSecPositionFlowDto();
        dto.setEntrust(true);
        Date sysdate = DateUtil.parse((String)param.get("sysDate"));
        dto.setCdate(sysdate);
        dto.setFinprodId((String)param.get("finprodId"));
        dto.setCcy((String)param.get("ccy"));
        dto.setHodingType(DictCodeListExt3.资产类型_债券借贷本方券可用持仓.getValue());
        dto.setPortfolioId((String)param.get("portfolioId"));
        dto.setSecAcctId((String)param.get("secAcctId"));
        Map<String, Object> result = new HashMap<>();
        BigDecimal position = etrSecLendMapper.queryUsableSecPosition(dto);
        result.put("position", position);
        return result;
    }

    /**
     * 退回更新流程,删除流水
     * @param dto
     */
    public void doBackFlow(EtrSecLendDto dto) {
        //删除流水
        baseEtrCashPositionCommService.entrustCloseExecute(dto.getEntrustId(), DictCodeList.是否_是.getValue());
        //删除流程中数据
        Map<String, Object> param = new HashMap<>();
        param.put("workflowId", dto.getWorkflowId());
        etrSecLendMapper.deleteWflwflEtrTarget(dto.getWorkflowId());
    }
    
}
