/*
 * 文件名：EtrPlanAssetCService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2019年1月3日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.entrust.etrplanassetc.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.entrust.common.dao.EntrustCommonPersonSelectMapper;
import com.joyintech.service.entrust.etrplanassetc.dao.EtrPlanAssetCMapper;
import com.joyintech.service.entrust.etrplanassetc.dto.EtrPlanAssetCDto;
import com.joyintech.service.entrust.etrplanassetr.dto.EtrPlanAssetRDto;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.component.base.entrust.BusiAutoEtrDispService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2019年1月3日
 * @see EtrPlanAssetCService
 * @since 1.0
 */
@Service("backEtrPlanAssetCService")
public class EtrPlanAssetCService extends BaseService<EtrPlanAssetCDto>{

    /**
     * 委托人员信息
     */
    @Autowired
    private EntrustCommonPersonSelectMapper entrustCommonPersonSelectMapper;
    /**
     * 平台日获取接口
     */
    @Autowired(required=false)
    private IPlatformDate iPlatformDate;

    /**
     * 附件信息共通Service
     */
    @Autowired(required = false)
    private MstFileCommonService mstFileCommonService;

    /**
     * 组合可用持仓和实际持仓查询更新组件接口
     */
    @Autowired(required = false)
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;

    /**
     * 组合可用现金和实际现金查询更新组件
     */
    @Autowired(required = false)
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;
    /**
     * 自定义mapper
     */
    @Autowired
    private EtrPlanAssetCMapper etrPlanAssetCMapper;
    
    /**
     * 投资委托基础组件
     */
    @Autowired
    private BaseEtrInvestInfoService baseEtrInvestInfoService;
    
    /**
     * 委托自动分发
     */
    @Autowired(required=false)
    private BusiAutoEtrDispService busiAutoEtrDispService;

    /**
     * 委托指令共通
     */
    @Autowired(required=false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;
    /**
     * 计划类货币委托查询 <功能详细描述>
     * 
     * @param page
     * @param length
     * @param param
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<EtrPlanAssetCDto> selectForPage(int page, int length, Map<String, Object> param)
    {
        param.put("FINPROD_TYPE2", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        param.put("PROFIT_TYPE", DictCodeList.收益类型_货币型.getValue());
        String portfolioId = (String)param.get("portfolioId");
        if(StringUtil.isNotEmpty(portfolioId)){
            param.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA,portfolioId));
        }
        param.put("ENTRUST_STATUS_08", DictCodeListExt3.委托状态_流程中.getValue());
        return queryForPage(page, length, () -> etrPlanAssetCMapper.selectForPage(param));
    }
    
    /**
     * 获取组合代码的联想文本框 <功能详细描述>
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getentrustId(Map<String, Object> map) {
        return etrPlanAssetCMapper.getentrustId(map);
    }

    /**
     * 获取资产编号的联想文本框 <功能详细描述>
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getfinprodId(String param) {
        Map<String, String> map = new HashMap<>();
        map.put("param", param);
        map.put("planasset", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        map.put("notworth", DictCodeList.收益类型_货币型.getValue());
        return etrPlanAssetCMapper.getfinprodId(map);
    }

    /**
     * 获取新建界面的资产代码联想下拉框框 <功能详细描述>
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getfinprodIdForAdd(int page, int length,Map<String, String> map) {
        map.put("planasset", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        map.put("notworth", DictCodeList.收益类型_货币型.getValue());
        return this.queryForPageForSelectReturn(page, length,()->etrPlanAssetCMapper.getfinprodIdForAdd(map));
    }

    /**
     * 获取新建界面的组合代码联想下拉框框 <功能详细描述>
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getentrustIdAdd(int page, int length,Map<String, Object> map) {
        return this.queryForPageForSelectReturn(page, length,()->etrPlanAssetCMapper.getentrustIdAdd(map));
    }

    /**
     * 画面校验 <功能详细描述>
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtrPlanAssetCDto dto) {
        String entrustId = dto.getEntrustId();
        String delegateOperationType = dto.getOperateType();
        String delegateCloseWay = dto.getCloseWay();
        List<String> traderList = new ArrayList<>();
        if (dto.getEtrInvestPersonInfo() != null
            && StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo().getTrader())) {
            traderList.add(dto.getEtrInvestPersonInfo().getTrader());
        }
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
    public boolean recallDelegate(EtrPlanAssetCDto dto)
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
     * 工作流完成方法 <功能详细描述>
     * 
     * @param dto
     * @param operationType
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @see
     * @since 1.0
     */
    public void doComplete(EtrPlanAssetCDto dto, String operationType)
        throws IllegalAccessException, InvocationTargetException {
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
        EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
        EtrEntrust etrEntrust = new EtrEntrust();
        EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
        entrustCommonDto.setEtrEntrust(etrEntrust);
        entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
        if (StringUtil.equals(operationType, "NEW")) {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
            dto.setIsOutTrade(judgeCounterType(dto.getEtrInvestPersonInfo().getCounterId()));
            dto.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, dto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, dto.getEtrInvestPersonInfo());
            if(iPlatformDate != null){
                etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            }
            if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()) || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())){
                etrEntrust.setEntrustAmt(dto.getEntrustAmt2());
                etrEntrust.setEntrustNum(dto.getEntrustNum2());
            }
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, dto);
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_计划类资产_货币型.getValue());
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
                //entity.setEtrDistributeList(etrDistributeList);
            }
            //自动分发
            if(busiAutoEtrDispService != null){
                //检测是否设置交易员
                if(!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)){
                    //跳出自动分发
                    entity.setEtrDistributeList(etrDistributeList);
                }
                else{
                    //查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                    if(busiAutoEtrDispService.checkEntrustTime(etrEntrust)){
                        //自动分发
                        entity.setEtrDistributeList(busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));
                    }
                }
            }
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_计划类资产_货币型.getValue());
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
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
            dto.setIsOutTrade(judgeCounterType(dto.getEtrInvestPersonInfo().getCounterId()));
            dto.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, dto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, dto.getEtrInvestPersonInfo());
            if(iPlatformDate != null){
                etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            }
            //add wsz ZGCPCN-1036 20220602 start
            //计划类货币型卖出指令提交后，分发接收，指令接收画面的指令份额、待结转份额和指令金额显示均不对
            if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()) || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())){
                etrEntrust.setEntrustAmt(dto.getEntrustAmt2());
                etrEntrust.setEntrustNum(dto.getEntrustNum2());
            }
            //add wsz ZGCPCN-1036 20220602 end
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, dto);
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_计划类资产_货币型.getValue());
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
                //entity.setEtrDistributeList(etrDistributeList);
            }
            //自动分发
            if(busiAutoEtrDispService != null){
                //检测是否设置交易员
                if(!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)){
                    //跳出自动分发
                    entity.setEtrDistributeList(etrDistributeList);
                }
                else{
                    //查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                    if(busiAutoEtrDispService.checkEntrustTime(etrEntrust)){
                        //自动分发
                        entity.setEtrDistributeList(busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));
                    }
                }
            }
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
        //委托开始日对委托的持仓流水和现金流水做处理
//        if(baseEtrCashPositionCommService!=null){
//            baseEtrCashPositionCommService.entrustStartExecute(entrustCommonDto);
//        }
    }

    /**
     * 通过主键查询反显 <功能详细描述>
     * 
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public EtrPlanAssetCDto selectByKey(String entrustId) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("entrustId", entrustId);
        List<String> traderList = entrustCommonPersonSelectMapper.getTraderList(map);
        EtrPlanAssetCDto EtrPlanAssetCDto = etrPlanAssetCMapper.selectByKey(entrustId);
        String trader = "";
        for(int i=0; i<traderList.size(); i++){
            trader = trader +","+ traderList.get(i);
        }
        EtrPlanAssetCDto.getEtrInvestPersonInfo().setTrader(trader);
        return EtrPlanAssetCDto;
    }
    
    /**
     * 组合可用现金查询 <功能详细描述>
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> getProAvailableCash(Map<String, Object> param) {
        if (basePtlCashPositionInfoService != null) {
            PtlCashPositionFlowDto dto = new PtlCashPositionFlowDto();
            Date vdate = DateUtil.parse((String)param.get("vdate"));
            dto.setCdate(vdate);
            List<String> acctTypeList = new ArrayList<>();
            acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
            dto.setAcctType(acctTypeList);
            dto.setPortfolioId((String)param.get("portfolioId"));
            dto.setCcy((String)param.get("ccy"));
            BigDecimal cashTG = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
            Map<String, Object> result = new HashMap<>();
            result.put("cashTG", cashTG);
            acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
            BigDecimal cashT = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
            result.put("cashT", cashT);

            // 通道交易 新增 强成西 2019/03/26 start
            if (StringUtil.isNotEmpty(param.get("chlId"))) {
                dto.setNeedAcctType(false);
                dto.setChlFinprodId((String)param.get("chlId"));
                dto.setAcctType(null);
                result.put("cashTD", basePtlCashPositionInfoService.queryUsableCashPosition(dto));
            }
            // 通道交易 新增 强成西 2019/03/26 end

            if (StringUtil.isNotEmpty(param.get("calId"))) {
                PtlCashPositionFlowDto dto2 = new PtlCashPositionFlowDto();
                Date vdate1 = DateUtil.parse((String)param.get("vdate1"));
                dto2.setCdate(vdate1);
                dto2.setAcctType(acctTypeList);
                dto2.setPortfolioId((String)param.get("portfolioId"));
                dto2.setCcy((String)param.get("ccy"));
                BigDecimal cashT1 = basePtlCashPositionInfoService.queryUsableCashPosition(dto2);
                result.put("cashT1", cashT1);
            }
            return result;
        }
        else {
            return null;
        }
    }

    /**
     * 组合可用持仓查询 <功能详细描述>
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getAssetAvailablePosition(PtlSecPositionFlowDto dto) {
        BigDecimal result = null;
        List<String> acctTypeList = new ArrayList<>();
        acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
        acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
        if (StringUtil.isNotEmpty(dto.getSecAcctId())) {
            dto.setChannel(true);
        }
        dto.setAcctType(acctTypeList);
        if (basePtlSecPositionInfoService != null) {
            result = basePtlSecPositionInfoService.queryUsableSecPosition(dto);
        }
        return result;
    }

    /**
     * 判断交易对手类型 <功能详细描述>
     * 
     * @return
     * @see
     * @since 1.0
     */
    public String judgeCounterType(String counterId) {
        String result = etrPlanAssetCMapper.judgeCounterType(counterId);
        if (StringUtil.equals(DictCodeListExt1.交易对手类型_内部交易对手.getValue(), result)) {
            return DictCodeList.是否_是.getValue();
        }
        else {
            return DictCodeList.是否_否.getValue();
        }

    }

    /**
     * 流程启动时为附件绑定工作流代码 <功能详细描述>
     * 
     * @param dto
     * @param operateType
     * @see
     * @since 1.0
     */
    public void doInsert(EtrPlanAssetCDto dto, String operateType) {
        // 上传文件修改更新
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setBussinessId(dto.getWorkflowId());
        mstFileInputParameter.setFileList(dto.getFileArray());
        if (mstFileCommonService != null) {
            mstFileCommonService.updateBusinessId(mstFileInputParameter);
        }
    }

    public BaseEtrInvestInfoDto initBaseDto(EtrPlanAssetCDto dto){
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();

        try {
            // etrEntrust
            EtrEntrust etrEntrust = new EtrEntrust();
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, dto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, dto.getEtrInvestPersonInfo());
            etrEntrust.setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
            if(iPlatformDate != null){
                etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            }
            // ZGZGST-2045: 计划类货币性指令买入方向用EntrustAmt fuyongsheng 20210907 edit start
            if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())){
                etrEntrust.setEntrustAmt(dto.getEntrustAmt2());
                etrEntrust.setEntrustNum(dto.getEntrustNum2());
            }
            if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()) || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue()) || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())){
                etrEntrust.setEntrustAmt(dto.getEntrustAmt());
                etrEntrust.setEntrustNum(dto.getEntrustNum());
            }
            // ZGZGST-2045: 计划类货币性指令买入方向用EntrustAmt fuyongsheng 20210907 edit end
            entity.setEtrEntrust(etrEntrust);
            // etrEntrustAdd
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, dto);
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_计划类资产_货币型.getValue());
            entity.setEtrEntrustAdd(etrEntrustAdd);

            // 添加流程编号
            entity.setWorkFlowId(dto.getWorkflowId());
        } catch (Exception e) {
            // 对象转换赋值失败
            throw new RuntimeException("对象转换赋值失败", e);
        }
        return entity;
    }
}
