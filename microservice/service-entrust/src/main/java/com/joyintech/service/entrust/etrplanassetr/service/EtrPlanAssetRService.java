/*
 * 文件名：EtrPlanAssetService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangjun
 * 修改时间：2018年9月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.entrust.etrplanassetr.service;

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
import com.joyintech.service.entrust.common.service.EntrustCommonPersonSelectService;
import com.joyintech.service.entrust.etrcommanddispatcher.service.EtrCommandDispatcherService;
import com.joyintech.service.entrust.etrplanassetn.dto.EtrPlanAssetNDto;
import com.joyintech.service.entrust.etrplanassetr.dao.EtrPlanAssetRMapper;
import com.joyintech.service.entrust.etrplanassetr.dto.EtrPlanAssetRDto;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.FinProduct;
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
 * 计划类利率委托微服务Service 〈功能详细描述〉
 *
 * @author zhangjun
 * @version 1.0 2018年9月25日
 * @see EtrPlanAssetRService
 * @since 1.0
 */
@Service("backEtrPlanAssetRService")
public class EtrPlanAssetRService extends BaseService<EtrPlanAssetRDto> {

    /**
     * 平台日获取接口
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    /**
     * 委托人员信息
     */
    @Autowired
    private EntrustCommonPersonSelectMapper entrustCommonPersonSelectMapper;
    @Autowired
    private EntrustCommonPersonSelectService entrustCommonPersonSelectService;
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
     * 计划类利率委托微服务自定义mapper
     */
    @Autowired
    private EtrPlanAssetRMapper etrPlanAssetMapper;

    /**
     * 投资委托基础组件
     */
    @Autowired(required = false)
    private BaseEtrInvestInfoService baseEtrInvestInfoService;

    /**
     * 委托自动分发
     */
    @Autowired(required = false)
    private BusiAutoEtrDispService busiAutoEtrDispService;

    @Autowired
    private FinProductMapper finProductMapper;
    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    @Autowired(required = false)
    private EtrCommandDispatcherService etrCommandDispatcherService;

    /**
     * 拆借投资委托分页查询 <功能详细描述>
     *
     * @param page
     * @param length
     * @param param
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<EtrPlanAssetRDto> selectForPage(int page, int length, Map<String, Object> param) {
        String assetType1 = (String) param.get("assetType1");
        if (StringUtil.isNotEmpty(assetType1)) {
            List<String> assetType1List = StringUtil.splitToList(",", assetType1);
            param.put("assetType1List", assetType1List);
        }
        /*更、zhuyongkang 2019/7/29  start*/
        String assetType10 = (String) param.get("assetType10");
        if (StringUtil.isNotEmpty(assetType10)) {
            List<String> assetType10List = StringUtil.splitToList(",", assetType10);
            param.put("assetType10List", assetType10List);
        }
        String assetType11 = (String) param.get("assetType11");
        if (StringUtil.isNotEmpty(assetType11)) {
            List<String> assetType11List = StringUtil.splitToList(",", assetType11);
            param.put("assetType11List", assetType11List);
        }
        /*更、zhuyongkang 2019/7/29  end*/
        //子组合查询改造 王贵奇 2019/8/20
        param.put("portfolioType", DictCodeListExt1.组合类型_子组合.getValue());
        String portfolioId = (String) param.get("portfolioId");
        if (StringUtil.isNotEmpty(portfolioId)) {
            param.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }
        param.put("FINPROD_TYPE2", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        param.put("PROFIT_TYPE", DictCodeList.收益类型_收益型.getValue());
        param.put("ENTRUST_STATUS_08", DictCodeListExt3.委托状态_流程中.getValue());
        //获取投资经理真实姓名 guyunfei 2019/09/03 start
        Page<EtrPlanAssetRDto> pageDto = queryForPage(page, length, () -> etrPlanAssetMapper.selectForPage(param));
        List<EtrPlanAssetRDto> dtoList = pageDto.getRoot();
        if (dtoList != null && dtoList.size() > 0) {
            dtoList = entrustCommonPersonSelectService.getRealName4(dtoList);
            pageDto.setRoot(dtoList);
        }
        return pageDto;
        //return queryForPage(page, length, () -> etrPlanAssetMapper.selectForPage(param));
        //获取投资经理真实姓名 guyunfei 2019/09/03 end
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
        map.put("portType", DictCodeListExt1.组合类型_投资类组合.getValue());
        map.put("portType1", DictCodeListExt1.组合类型_子组合.getValue());
        return etrPlanAssetMapper.getentrustId(map);
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
        Map<String, Object> map = new HashMap<>();
        map.put("param", param);
        map.put("FINPROD_TYPE_TG_F17", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        map.put("PROFIT_TYPE_02", DictCodeList.收益类型_收益型.getValue());
        return etrPlanAssetMapper.getfinprodId(map);

    }

    /**
     * 撤回委托
     *
     * @param entity 实体类
     * @return boolean
     * @see
     * @since 1.0
     */
    public boolean recallDelegate(EtrPlanAssetRDto dto)
            throws IllegalAccessException,
            InvocationTargetException {
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
     * @param entrustId 委托代码
     * @param closeWay
     * @return
     * @see
     * @since 1.0
     */
    public boolean closeDelegate(String entrustId, String closeWay) {
        if (baseEtrInvestInfoService != null) {
            return baseEtrInvestInfoService.closeDelegate(entrustId, closeWay);
        } else {
            return false;
        }
    }

    /**
     * 获取新建界面的资产代码联想下拉框框 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getfinprodIdForAdd(int page, int length, Map<String, Object> map) {
        map.put("FINPROD_TYPE_TG_F17", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        map.put("PROFIT_TYPE_02", DictCodeList.收益类型_收益型.getValue());
        return this.queryForPageForSelectReturn(page, length, () -> etrPlanAssetMapper.getfinprodIdForAdd(map));
    }

    /**
     * 获取新建界面的组合代码联想下拉框框 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getentrustIdAdd(int page, int length, Map<String, Object> map) {
        String types = DictCodeListExt1.组合类型_子组合.getValue() + "," + DictCodeListExt1.组合类型_投资类组合.getValue();
        map.put("portTypes", StringUtil.splitToList(GlobalConstant.COMMA, types));
        return this.queryForPageForSelectReturn(page, length, () -> etrPlanAssetMapper.getentrustIdAdd(map));
    }

    /**
     * 画面校验 <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtrPlanAssetRDto dto) {
        String entrustId = dto.getEntrustId();
        String delegateOperationType = dto.getOperateType();
        String delegateCloseWay = dto.getCloseWay();
        List<String> traderList = new ArrayList<>();
        if (dto.getEtrInvestPersonInfo() != null && StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo().getTrader())) {
            traderList.add(dto.getEtrInvestPersonInfo().getTrader());
        }
        return baseEtrInvestInfoService.checkDelegateOperation(entrustId, delegateOperationType,
                traderList, delegateCloseWay);
    }

    /**
     * 工作流完成方法 <功能详细描述>
     *
     * @param entity EtrPlanAssetDto
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @see
     * @since 1.0
     */
    public void doComplete(EtrPlanAssetRDto dto, String operationType)
            throws IllegalAccessException, InvocationTargetException {
        EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
        EtrEntrust etrEntrust = new EtrEntrust();
        EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
        String isPushMsg = DictCodeList.是否_否.getValue();
        if (StringUtil.equals(operationType, "NEW")) {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
            dto.setIsOutTrade(judgeCounterType(dto.getEtrInvestPersonInfo().getCounterId()));
            dto.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
            BeanUtil.copyProperties(etrEntrust, dto.getEtrInvestPersonInfo());
            if (iPlatformDate != null) {
                etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            }
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_计划类资产_利率型.getValue());
            etrEntrust.setBusinessModel(dto.getBusinessModel());
            //根据投资目的与业务模式的对应关系入库时赋值
            if (StringUtil.equals("Y1", dto.getBusinessModel())) {
                dto.setInvAim("AC");
                etrEntrust.setInvAim("AC");
            } else if (StringUtil.equals("Y2", dto.getBusinessModel())) {
                dto.setInvAim("FVTPL");
                etrEntrust.setInvAim("FVTPL");
            }
            List<EtrDistribute> etrDistributeList = new ArrayList<>();
            if (StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo().getTrader())) {
                List<String> traderList = StringUtil.splitToList(GlobalConstant.COMMA, dto.getEtrInvestPersonInfo().getTrader());
                for (String trader : traderList) {
                    EtrDistribute etrDistribute = new EtrDistribute();
                    //设置接收状态
                    etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                    //设置指定交易员指定交易员 = 指定交易员
                    etrDistribute.setTrader(trader);
                    //设置委托代码
                    etrDistribute.setEntrustId(dto.getEntrustId());
                    //
                    etrDistributeList.add(etrDistribute);
                }
                entity.setEtrDistributeList(etrDistributeList);
            }
            //自动分发
            if (busiAutoEtrDispService != null) {
                //检测是否设置交易员
                if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                    //跳出自动分发
                    entity.setEtrDistributeList(etrDistributeList);
                    isPushMsg = DictCodeList.是否_是.getValue();
                } else {
                    //查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                    if (busiAutoEtrDispService.checkEntrustTime(etrEntrust)) {
                        //自动分发
                        entity.setEtrDistributeList(busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));
                        isPushMsg = DictCodeList.是否_是.getValue();
                    }
                }
            }
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            baseEtrInvestInfoService.newDelegate(entity);
            //文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());
            if (mstFileCommonService != null) {
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
        } else {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
            dto.setIsOutTrade(judgeCounterType(dto.getEtrInvestPersonInfo().getCounterId()));
            dto.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
            BeanUtil.copyProperties(etrEntrust, dto.getEtrInvestPersonInfo());
            if (iPlatformDate != null) {
                etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            }
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_计划类资产_利率型.getValue());
            List<EtrDistribute> etrDistributeList = new ArrayList<>();
            if (StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo().getTrader())) {
                List<String> traderList = StringUtil.splitToList(GlobalConstant.COMMA, dto.getEtrInvestPersonInfo().getTrader());
                for (String trader : traderList) {
                    EtrDistribute etrDistribute = new EtrDistribute();
                    //设置接收状态
                    etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                    //设置指定交易员指定交易员 = 指定交易员       
                    etrDistribute.setTrader(trader);
                    //设置委托代码
                    etrDistribute.setEntrustId(dto.getEntrustId());
                    //
                    etrDistributeList.add(etrDistribute);
                }
                //entity.setEtrDistributeList(etrDistributeList);
            }
            //自动分发
            if (busiAutoEtrDispService != null) {
                //检测是否设置交易员
                if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                    //跳出自动分发
                    entity.setEtrDistributeList(etrDistributeList);
                    isPushMsg = DictCodeList.是否_是.getValue();
                } else {
                    //查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                    if (busiAutoEtrDispService.checkEntrustTime(etrEntrust)) {
                        //自动分发
                        entity.setEtrDistributeList(busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));
                        isPushMsg = DictCodeList.是否_是.getValue();
                    }
                }
            } else {
                entity.setEtrDistributeList(etrDistributeList);
            }
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            baseEtrInvestInfoService.updateDelegate(entity);
            //文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());
            if (mstFileCommonService != null) {
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
        }
        if (DictCodeList.是否_是.getValue().equals(isPushMsg)) {
            Map<String, Object> param = new HashMap<>();
            param.put("entrustId", dto.getEntrustId());
            etrCommandDispatcherService.pushCommandMsg(param);
        }
        //update by zznn 20210826  start
        //指令头寸在流程中处理
        //委托开始日对委托的持仓流水和现金流水做处理
        /* if(baseEtrCashPositionCommService!=null){
             entrustCommonDto.setEtrEntrust(etrEntrust);
             entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
             baseEtrCashPositionCommService.entrustStartExecute(entrustCommonDto);
         }*/
        //update by zznn 20210826  end

    }

    /**
     * 流程启动时为附件绑定工作流代码
     * <功能详细描述>
     *
     * @param dto
     * @param operateType
     * @see
     * @since 1.0
     */
    public void doInsert(EtrPlanAssetRDto dto, String operateType) {
        //上传文件修改更新
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setBussinessId(dto.getWorkflowId());
        mstFileInputParameter.setFileList(dto.getFileArray());
        if (mstFileCommonService != null) {
            mstFileCommonService.updateBusinessId(mstFileInputParameter);
        }
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
    public EtrPlanAssetRDto selectByKey(String entrustId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("entrustId", entrustId);
        List<String> traderList = entrustCommonPersonSelectMapper.getTraderList(map);
        EtrPlanAssetRDto etrPlanAssetRDto = etrPlanAssetMapper.selectByKey(entrustId);
        String trader = "";
        for (int i = 0; i < traderList.size(); i++) {
            trader = trader + "," + traderList.get(i);
        }
        etrPlanAssetRDto.getEtrInvestPersonInfo().setTrader(trader);
        return etrPlanAssetRDto;
    }

    /**
     * 通过资产代码获取资产类型
     * <功能详细描述>
     *
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    public String searchType(String finprodId) {
        return etrPlanAssetMapper.searchType(finprodId);
    }

    /**
     * 组合可用现金查询
     * <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> getProAvailableCash(Map<String, Object> param) {
        if (basePtlCashPositionInfoService != null) {
            PtlCashPositionFlowDto dto = new PtlCashPositionFlowDto();
            Date vdate = DateUtil.parse((String) param.get("vdate"));
            dto.setCdate(vdate);
            List<String> acctTypeList = new ArrayList<>();
            acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
            dto.setAcctType(acctTypeList);
            dto.setPortfolioId((String) param.get("portfolioId"));
            dto.setCcy((String) param.get("ccy"));
            BigDecimal cashTG = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
            Map<String, Object> result = new HashMap<>();
            result.put("cashTG", cashTG);
            acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
            BigDecimal cashT = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
            result.put("cashT", cashT);
            if (StringUtil.isNotEmpty(param.get("calId"))) {
                PtlCashPositionFlowDto dto2 = new PtlCashPositionFlowDto();
                Date vdate1 = DateUtil.parse((String) param.get("vdate1"));
                dto2.setCdate(vdate1);
                dto2.setAcctType(acctTypeList);
                dto2.setPortfolioId((String) param.get("portfolioId"));
                dto2.setCcy((String) param.get("ccy"));
                BigDecimal cashT1 = basePtlCashPositionInfoService.queryUsableCashPosition(dto2);
                result.put("cashT1", cashT1);
            }
            // 通道交易 新增 强成西 2019/03/26 start
            if (StringUtil.isNotEmpty(param.get("chlId"))) {
                dto.setNeedAcctType(false);
                dto.setChlFinprodId((String) param.get("chlId"));
                dto.setAcctType(null);
                result.put("cashTD", basePtlCashPositionInfoService.queryUsableCashPosition(dto));
            }
            // 通道交易 新增 强成西 2019/03/26 end+
            return result;
        } else {
            return null;
        }
    }

    /**
     * 组合可用持仓查询
     * <功能详细描述>
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
        dto.setAcctType(acctTypeList);
        if (basePtlSecPositionInfoService != null) {
            result = basePtlSecPositionInfoService.queryUsableSecPosition(dto);
        }
        return result;
    }

    /**
     * 判断交易对手类型
     * <功能详细描述>
     *
     * @return
     * @see
     * @since 1.0
     */
    public String judgeCounterType(String counterId) {
        String result = etrPlanAssetMapper.judgeCounterType(counterId);
        if (StringUtil.equals(DictCodeListExt1.交易对手类型_内部交易对手.getValue(), result)) {
            return DictCodeList.是否_是.getValue();
        } else {
            return DictCodeList.是否_否.getValue();
        }

    }

    /**
     * 查询金融产品信息
     *
     * @param finprodId
     * @return
     */
    public FinProduct getFinProductInfo(String finprodId) {
        return finProductMapper.selectByPrimaryKey(finprodId);
    }

    public BaseEtrInvestInfoDto initBaseDto(EtrPlanAssetRDto dto) {
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();

        try {
            // etrEntrust
            EtrEntrust etrEntrust = new EtrEntrust();
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
            dto.setIsOutTrade(judgeCounterType(dto.getEtrInvestPersonInfo().getCounterId()));
            dto.setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
            BeanUtil.copyProperties(etrEntrust, dto.getEtrInvestPersonInfo());
            if (iPlatformDate != null) {
                etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            }
            entity.setEtrEntrust(etrEntrust);

            // etrEntrustAdd
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            etrEntrustAdd.setVdate(null);
            etrEntrustAdd.setMdate(null);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_计划类资产_利率型.getValue());
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
