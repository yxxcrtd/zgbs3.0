/*
 * 文件名：EtrBrowDepoInfoService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2018年9月19日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.entrust.etrbrowdepoinfo.service;

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
import com.joyintech.service.entrust.etrbrowdepoinfo.dao.EtrBrowDepoInfoMapper;
import com.joyintech.service.entrust.etrbrowdepoinfo.dto.EtrBrowDepoInfoDto;
import com.joyintech.service.entrust.etrcommanddispatcher.service.EtrCommandDispatcherService;
import com.joyintech.service.entrust.etrinstruction.dto.EtrInstructionDto;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.entrust.BusiAutoEtrDispService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.dto.*;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 存款委托
 *
 * @author huwenbin
 * @version 1.0 2018年9月19日
 * @see EtrBrowDepoInfoService
 * @since 1.0
 */
@Service("backEtrBrowDepoInfoService")
public class EtrBrowDepoInfoService extends BaseService<EtrBrowDepoInfoDto> {

    /**
     * 自定义Mapper
     */
    @Autowired
    private EtrBrowDepoInfoMapper etrBrowDepoInfoMapper;

    /**
     * 委托人员信息
     */
    @Autowired
    private EntrustCommonPersonSelectMapper entrustCommonPersonSelectMapper;

    /**
     * 附件信息共通Service
     */
    @Autowired(required = false)
    private MstFileCommonService mstFileCommonService;

    /**
     * 投资委托基础组件
     */
    @Autowired(required = false)
    private BaseEtrInvestInfoService baseEtrInvestInfoService;

    /**
     * 平台日期获取
     */
    @Autowired(required = false)
    private IPlatformDate platformDateImpl;

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
     * 委托自动分发
     */
    @Autowired(required = false)
    private BusiAutoEtrDispService busiAutoEtrDispService;

    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    @Autowired(required = false)
    private EtrCommandDispatcherService etrCommandDispatcherService;

    /**
     * 使用给定的检索分页数据。返回类型是Service的泛型。
     *
     * @param length    int
     * @param page      int
     * @param pageParam 查询方法
     * @return Page类
     * @throws Exception 异常信息
     */
    public Page<EtrBrowDepoInfoDto> selectForPage(int page, int length,
                                                  Map<String, Object> pageParam) {
        String finprodType2 = (String) pageParam.get("finprodType2");
        if (StringUtil.isNotEmpty(finprodType2)) {
            pageParam.put("finprodType2List", StringUtil.splitToList(GlobalConstant.COMMA, finprodType2));
        }
        //子组合查询改造 王贵奇 2019/8/21
        pageParam.put("portfolioType", DictCodeListExt1.组合类型_子组合.getValue());
        pageParam.put("portfolioType1", DictCodeListExt1.组合类型_投资类组合.getValue());
        pageParam.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        pageParam.put("endDate", DateUtil.parse((String) pageParam.get("endDate")));
        pageParam.put("startDate", DateUtil.parse((String) pageParam.get("startDate")));
        String portfolioId = (String) pageParam.get("portfolioId");
        if (StringUtil.isNotEmpty(portfolioId)) {
            pageParam.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }
        pageParam.put("ENTRUST_STATUS_08", DictCodeListExt3.委托状态_流程中.getValue());
        return super.queryForPage(page, length, () -> etrBrowDepoInfoMapper.select(pageParam));
    }

    /**
     * 组合代码联想下拉框 <功能详细描述>
     *
     * @param param 组合代码 用户Id
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getPortfolioId(Map<String, Object> param) {
        param.put("portType", DictCodeListExt1.组合类型_投资类组合.getValue());
        param.put("portType1", DictCodeListExt1.组合类型_子组合.getValue());
        return etrBrowDepoInfoMapper.getPortfolioId(param);
    }

    /**
     * 资产代码联想下拉框 <功能详细描述>
     *
     * @param finprodId 资产代码
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getFinprodMarketId(String finprodId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("finprodId", finprodId);
        map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        return etrBrowDepoInfoMapper.getFinprodMarketId(map);
    }

    /**
     * 新建页面资产代码联想 <功能详细描述>
     *
     * @param map 用户输入值
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getSelectFinprodMarketId(int page, int length, Map<String, Object> map) {
        map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        map.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        return this.queryForPageForSelectReturn(page, length, () -> etrBrowDepoInfoMapper.getSelectFinprodMarketId(map));
    }

    /**
     * 新建页面组合代码联想下拉框 <功能详细描述>
     *
     * @param map Map<String, Object>
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getSelectPortfolioId(int page, int length, Map<String, Object> map) {
        String types = DictCodeListExt1.组合类型_子组合.getValue() + "," + DictCodeListExt1.组合类型_投资类组合.getValue();
        map.put("portTypes", StringUtil.splitToList(GlobalConstant.COMMA, types));
        return this.queryForPageForSelectReturn(page, length, () -> etrBrowDepoInfoMapper.getSelectPortfolioId(map));
    }

    /**
     * 获取通道
     *
     * @param map Map<String, Object>
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getChannelId(int page, int length, Map<String, Object> map) {
        return this.queryForPageForSelectReturn(page, length, () -> etrBrowDepoInfoMapper.getChannelId(map));
    }

    /**
     * 获取通道可用现金
     *
     * @param map Map<String, Object>
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public BigDecimal getChannelAvailableCash(int page, int length, Map<String, Object> map) {
        map.put("platDate", platformDateImpl.getSysDate());
        return etrBrowDepoInfoMapper.getChannelAvailableCash(map);
    }

    /**
     * 关闭、撤销、删除画面校验 <功能详细描述>
     *
     * @param dto EtrBrowDepoInfoDto
     * @return List<ValidMessage>
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtrBrowDepoInfoDto dto) {
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
     * @param entity 实体类
     * @return boolean
     * @throws InvocationTargetException IllegalAccessException
     * @throws IllegalAccessException    InvocationTargetException
     * @see
     * @since 1.0
     */
    public boolean recallDelegate(EtrBrowDepoInfoDto entity)
            throws IllegalAccessException,
            InvocationTargetException {
        // 投资委托信息dto
        BaseEtrInvestInfoDto baseEtrInvestInfoDto = new BaseEtrInvestInfoDto();
        // 委托指令Entity
        EtrEntrust etrEntrust = new EtrEntrust();
        BeanUtil.copyProperties(etrEntrust, entity);
        // 委托指令附属表Entity
        EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
        BeanUtil.copyProperties(etrEntrustAdd, entity);
        // List<委托指令回购质押品Entity>

        // List<委托指令分发Entity>
        List<EtrDistribute> etrDistributeList = new ArrayList<>();
        EtrDistribute etrDistribute = new EtrDistribute();
        BeanUtil.copyProperties(etrDistribute, entity);
        etrDistributeList.add(etrDistribute);
        // List<委托完成状况表Entity>
        List<EtrFinishStatus> etrFinishStatusList = new ArrayList<>();
        EtrFinishStatus etrFinishStatus = new EtrFinishStatus();
        BeanUtil.copyProperties(etrFinishStatus, entity);
        etrFinishStatusList.add(etrFinishStatus);

        baseEtrInvestInfoDto.setEtrEntrust(etrEntrust);
        baseEtrInvestInfoDto.setEtrEntrustAdd(etrEntrustAdd);
        baseEtrInvestInfoDto.setEtrDistributeList(etrDistributeList);
        baseEtrInvestInfoDto.setEtrFinishStatusList(etrFinishStatusList);
        // 撤回委托
        if (baseEtrInvestInfoService != null) {
            return baseEtrInvestInfoService.recallDelegate(baseEtrInvestInfoDto);
        } else {
            return false;
        }
    }

    /**
     * 关闭委托 <一句话功能简述> <功能详细描述>
     *
     * @param entrustId String
     * @param closeWay  String
     * @return boolean
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
     * 删除存款委托 <功能详细描述>
     *
     * @param entrustId String
     * @return boolean
     * @see
     * @since 1.0
     */
    public boolean deleteDelegate(String entrustId) {
        if (baseEtrInvestInfoService != null) {
            return baseEtrInvestInfoService.deleteDelegate(entrustId);
        } else {
            return false;
        }
    }

    /**
     * 根据金融产品代码获取委托金额 <功能详细描述>
     *
     * @param finprodId 金融产品代码
     * @return long
     * @see
     * @since 1.0
     */
    public BigDecimal selectEntrustAmt(String finprodId) {
        List<String> statusList = new ArrayList<String>();
        statusList.add(DictCodeList.委托状态_待接收.getValue());
        statusList.add(DictCodeListExt3.委托状态_待分发.getValue());
        statusList.add(DictCodeList.委托状态_已接收.getValue());
        statusList.add(DictCodeList.委托状态_已完成.getValue());
        statusList.add(DictCodeList.委托状态_已撤回.getValue());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("finprodId", finprodId);
        map.put("statusList", statusList);
        map.put("ps", DictCodeList.交易方向_买入.getValue());
        return etrBrowDepoInfoMapper.selectEntrustAmt(map);
    }

    /**
     * 根据金融产品代码获取发行规模 <功能详细描述>
     *
     * @param finprodId String
     * @return long
     * @see
     * @since 1.0
     */
    public BigDecimal selectIssueAmt(String finprodId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("finprodId", finprodId);
        return etrBrowDepoInfoMapper.selectIssueAmt(map);

    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param entity EtrBrowDepoInfoDto
     * @throws IllegalAccessException    IllegalAccessException
     * @throws InvocationTargetException InvocationTargetException
     * @see
     * @since 1.0
     */
    public void doInsert(EtrBrowDepoInfoDto entity)
            throws IllegalAccessException,
            InvocationTargetException {
        //文件
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setFileList(entity.getFileArray());
        mstFileInputParameter.setBussinessId(entity.getWorkflowId());
        if (mstFileCommonService != null) {
            mstFileCommonService.updateBusinessId(mstFileInputParameter);
        }
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param entity EtrBrowDepoInfoDto
     * @throws IllegalAccessException    IllegalAccessException
     * @throws InvocationTargetException InvocationTargetException
     * @see
     * @since 1.0
     */
    public void doDelete(EtrBrowDepoInfoDto entity)
            throws IllegalAccessException,
            InvocationTargetException {
        //文件
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setFileList(entity.getFileArray());
        mstFileInputParameter.setBussinessId(entity.getEntrustId());
        if (mstFileCommonService != null) {
            mstFileCommonService.updateBusinessId(mstFileInputParameter);
        }
    }


    /**
     * 实现docomplement方法
     *
     * @param entity EtrBrowDepoInfoDto
     * @throws InvocationTargetException IllegalAccessException
     * @throws IllegalAccessException    InvocationTargetException
     */
    public void doComplete(EtrBrowDepoInfoDto entity)
            throws IllegalAccessException,
            InvocationTargetException {
        // 投资委托信息dto
        BaseEtrInvestInfoDto baseEtrInvestInfoDto = new BaseEtrInvestInfoDto();
        EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
        String type = entity.getOperateType();
        String isPushMsg = DictCodeList.是否_否.getValue();
        if (StringUtil.equals(type, "DELETE")) {
            // 删除委托
        } else if (StringUtil.equals(type, "NEW")) {
            // 委托指令Entity
            EtrEntrust etrEntrust = new EtrEntrust();
            BeanUtil.copyProperties(etrEntrust, entity);
            BeanUtil.copyProperties(etrEntrust, entity.getBussEtrInvestPersonInfoDto());
            //设置委托状态
            etrEntrust.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            etrEntrust.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_存款.getValue());
            etrEntrust.setIsOutTrade(DictCodeList.是否_是.getValue());
//            if(platformDateImpl != null){
//                etrEntrust.setEntrustDate(platformDateImpl.getSysDate());
//            }
            // 委托指令附属表Entity
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_存款.getValue());
            BeanUtil.copyProperties(etrEntrustAdd, entity);
            //设置资产一级类型：资产一级分类=资产类型        
//            etrEntrustAdd.setAssetType1(entity.getFinprodType2());	//	finprodType2已弃用
            //设置起息日到期日
            etrEntrustAdd.setVdate(entity.getDateStart());
            etrEntrustAdd.setMdate(entity.getDateEnd());
            // List<委托指令回购质押品Entity>

            // List<委托指令分发Entity>
            List<EtrDistribute> etrDistributeList = new ArrayList<>();
            if (StringUtil.isNotEmpty(entity.getBussEtrInvestPersonInfoDto().getTrader())) {
                List<String> traderList = StringUtil.splitToList(GlobalConstant.COMMA, entity.getBussEtrInvestPersonInfoDto().getTrader());
                for (String trader : traderList) {
                    EtrDistribute etrDistribute = new EtrDistribute();
                    //设置接收状态
                    etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                    //设置指定交易员指定交易员 = 指定交易员
                    etrDistribute.setTrader(trader);
                    //设置委托代码
                    etrDistribute.setEntrustId(entity.getEntrustId());
                    //
                    etrDistributeList.add(etrDistribute);
                }
            }
            // List<委托完成状况表Entity>
            List<EtrFinishStatus> etrFinishStatusList = new ArrayList<>();
            /* EtrFinishStatus etrFinishStatus = new EtrFinishStatus();
            BeanUtil.copyProperties(etrFinishStatus, entity);
            etrFinishStatusList.add(etrFinishStatus);*/

            baseEtrInvestInfoDto.setEtrEntrust(etrEntrust);
            baseEtrInvestInfoDto.setEtrEntrustAdd(etrEntrustAdd);

            if (busiAutoEtrDispService != null) {
                //检测是否设置交易员
                if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                    //跳出自动分发
                    baseEtrInvestInfoDto.setEtrDistributeList(etrDistributeList);
                    isPushMsg = DictCodeList.是否_是.getValue();
                } else {
                    //查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                    if (busiAutoEtrDispService.checkEntrustTime(etrEntrust)) {
                        //自动分发
                        baseEtrInvestInfoDto.setEtrDistributeList(busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));
                        isPushMsg = DictCodeList.是否_是.getValue();
                    }
                }
            }
            baseEtrInvestInfoDto.setEtrFinishStatusList(etrFinishStatusList);

            // 新建委托
            if (baseEtrInvestInfoService != null) {
                baseEtrInvestInfoService.newDelegate(baseEtrInvestInfoDto);
            }
            //文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(entity.getFileArray());
            mstFileInputParameter.setBussinessId(entity.getEntrustId());
            if (mstFileCommonService != null) {
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
            entrustCommonDto.setEtrEntrust(etrEntrust);
            entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
        } else {
            // 委托指令Entity
            EtrEntrust etrEntrust = new EtrEntrust();
            BeanUtil.copyProperties(etrEntrust, entity);
            BeanUtil.copyProperties(etrEntrust, entity.getBussEtrInvestPersonInfoDto());
            //设置委托状态
            etrEntrust.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            etrEntrust.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_存款.getValue());
            etrEntrust.setIsOutTrade(DictCodeList.是否_是.getValue());
//            if(platformDateImpl != null){
//                etrEntrust.setEntrustDate(platformDateImpl.getSysDate());
//            }
            // 委托指令附属表Entity
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_存款.getValue());
            BeanUtil.copyProperties(etrEntrustAdd, entity);
            //设置资产一级类型：资产一级分类=资产类型
//            etrEntrustAdd.setAssetType1(entity.getFinprodType2()); //	finprodType2已弃用
            //设置起息日到期日
            etrEntrustAdd.setVdate(entity.getDateStart());
            etrEntrustAdd.setMdate(entity.getDateEnd());
            // List<委托指令回购质押品Entity>

            // List<委托指令分发Entity>
            List<EtrDistribute> etrDistributeList = new ArrayList<>();
            if (StringUtil.isNotEmpty(entity.getBussEtrInvestPersonInfoDto().getTrader())) {
                List<String> traderList = StringUtil.splitToList(GlobalConstant.COMMA, entity.getBussEtrInvestPersonInfoDto().getTrader());
                for (String trader : traderList) {
                    EtrDistribute etrDistribute = new EtrDistribute();
                    //设置接收状态
                    etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                    //设置指定交易员指定交易员 = 指定交易员       
                    etrDistribute.setTrader(trader);
                    //设置委托代码
                    etrDistribute.setEntrustId(entity.getEntrustId());
                    //
                    etrDistributeList.add(etrDistribute);
                }
            }
            // List<委托完成状况表Entity>
            List<EtrFinishStatus> etrFinishStatusList = new ArrayList<>();
           /* EtrFinishStatus etrFinishStatus = new EtrFinishStatus();
            BeanUtil.copyProperties(etrFinishStatus, entity);
            etrFinishStatusList.add(etrFinishStatus);*/

            baseEtrInvestInfoDto.setEtrEntrust(etrEntrust);
            baseEtrInvestInfoDto.setEtrEntrustAdd(etrEntrustAdd);
            if (busiAutoEtrDispService != null) {
                //检测是否设置交易员
                if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                    //跳出自动分发
                    baseEtrInvestInfoDto.setEtrDistributeList(etrDistributeList);
                    isPushMsg = DictCodeList.是否_是.getValue();
                } else {
                    //查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                    if (busiAutoEtrDispService.checkEntrustTime(etrEntrust)) {
                        //自动分发
                        baseEtrInvestInfoDto.setEtrDistributeList(busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));
                        isPushMsg = DictCodeList.是否_是.getValue();
                    }
                }
            }
            baseEtrInvestInfoDto.setEtrFinishStatusList(etrFinishStatusList);

            // 修改委托
            if (baseEtrInvestInfoService != null) {
                baseEtrInvestInfoService.updateDelegate(baseEtrInvestInfoDto);
            }
            //上传文件修改更新
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setBussinessId(entity.getEntrustId());
            mstFileInputParameter.setFileList(entity.getFileArray());
            if (mstFileCommonService != null) {
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }

            entrustCommonDto.setEtrEntrust(etrEntrust);
            entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
        }
//        if (baseEtrCashPositionCommService != null) {
//            baseEtrCashPositionCommService.entrustStartExecute(entrustCommonDto);
//        }
        if (DictCodeList.是否_是.getValue().equals(isPushMsg)) {
            Map<String, Object> param = new HashMap<>();
            param.put("entrustId", entity.getEntrustId());
            etrCommandDispatcherService.pushCommandMsg(param);
        }
    }

    /**
     * 编辑、双击查看页面详情 <功能详细描述>
     *
     * @param entrustId   String
     * @param finprodId   String
     * @param portfolioId String
     * @return EtrBrowDepoInfoDto
     * @see
     * @since 1.0
     */
    public EtrBrowDepoInfoDto selectByKey(String entrustId, String finprodId, String portfolioId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("entrustId", entrustId);
        List<String> traderList = entrustCommonPersonSelectMapper.getTraderList(map);
        List<EtrBrowDepoInfoDto> list = etrBrowDepoInfoMapper.selectByKey(entrustId);
        EtrBrowDepoInfoDto etrBrowDepoInfoDto = list.get(0);
        String trader = "";
        for (int i = 0; i < traderList.size(); i++) {
            trader = trader + "," + traderList.get(i);
        }
        etrBrowDepoInfoDto.getBussEtrInvestPersonInfoDto().setTrader(trader);
        ;
        return etrBrowDepoInfoDto;
    }

    /**
     * 查询上传文件
     * <功能详细描述>
     *
     * @param businessId businessId
     * @return List<FileArray>
     * @see
     * @since 1.0
     */
    public List<SysUploadFileInfo> selectFileByBusinessId(String businessId) {
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setBussinessId(businessId);
        if (mstFileCommonService != null) {
            MstFileOutputParameter output = mstFileCommonService.getFileInfoByBusiId(businessId);
            return output.getUploadFileList();
        } else {
            return null;
        }
    }

    /**
     * 组合可用现金查询
     * <功能详细描述>
     *
     * @param param PtlCashPositionFlowDto
     * @return Map<String, Object>
     * @see
     * @since 1.0
     */
    public Map<String, Object> getProAvailableCash(Map<String, Object> param) {
        /*BigDecimal result=null;
        if(basePtlCashPositionInfoService!=null){
            result = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
        }
        return result;*/
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
            if (StringUtil.isNotEmpty(param.get("vdate1"))) {
                PtlCashPositionFlowDto dto2 = new PtlCashPositionFlowDto();
                Date vdate1 = DateUtil.parse((String) param.get("vdate1"));
                dto2.setCdate(vdate1);
                dto2.setAcctType(acctTypeList);
                dto2.setPortfolioId((String) param.get("portfolioId"));
                dto2.setCcy((String) param.get("ccy"));
                BigDecimal cashT1 = basePtlCashPositionInfoService.queryUsableCashPosition(dto2);
                result.put("cashT1", cashT1);
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * 组合可用持仓查询
     * <功能详细描述>
     *
     * @param dto PtlSecPositionFlowDto
     * @return BigDecimal
     * @see
     * @since 1.0
     */
    public BigDecimal getAssetAvailablePosition(PtlSecPositionFlowDto dto) {
        BigDecimal result = null;
        if (basePtlSecPositionInfoService != null) {
            result = basePtlSecPositionInfoService.queryUsableSecPosition(dto);
        }
        return result;
    }

    public BaseEtrInvestInfoDto initBaseDto(EtrBrowDepoInfoDto dto) {
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();

        try {
            // etrEntrust
            EtrEntrust etrEntrust = new EtrEntrust();
            BeanUtil.copyProperties(etrEntrust, dto);
            BeanUtil.copyProperties(etrEntrust, dto.getBussEtrInvestPersonInfoDto());
            //设置委托状态
            etrEntrust.setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
            etrEntrust.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_存款.getValue());
            etrEntrust.setIsOutTrade(DictCodeList.是否_是.getValue());
            entity.setEtrEntrust(etrEntrust);

            // etrEntrustAdd
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_存款.getValue());
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            //设置资产一级类型：资产一级分类=资产类型        
//            etrEntrustAdd.setAssetType1(entity.getFinprodType2());	//	finprodType2已弃用
            //设置起息日到期日
            etrEntrustAdd.setVdate(dto.getDateStart());
            etrEntrustAdd.setMdate(dto.getDateEnd());
            entity.setEtrEntrustAdd(etrEntrustAdd);

            // 添加流程编号
            entity.setWorkFlowId(dto.getWorkflowId());
        } catch (IllegalAccessException | InvocationTargetException e) {
            // 对象转换赋值失败
            throw new RuntimeException("对象转换赋值失败", e);
        }
        return entity;
    }
}
