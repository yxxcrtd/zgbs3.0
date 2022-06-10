/*
 * 文件名：CfetsTrdConfirmService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：sjj
 * 修改时间：2021年11月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trdquery.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.dao.CfgDataTableFlagMapper;
import com.joyintech.tams.business.common.dao.CfgDataTableMapper;
import com.joyintech.tams.business.common.dao.CfgReadonlyFormShowMapper;
import com.joyintech.tams.business.common.entity.CfgDataTableShow;
import com.joyintech.tams.business.common.entity.CfgDataTableShowFlag;
import com.joyintech.tams.business.common.entity.CfgReadonlyFormShow;
import com.joyintech.tams.cfets.manualsettle.dao.CfetsManualSettleMapper;
import com.joyintech.tams.cfets.manualsettle.dto.*;
import com.joyintech.tams.cfets.manualsettle.service.CfetsManualSettleQueryService;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dao.CfetsTrdBondFirstSub002Mapper;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdConfirmExecFirstBondInfoResponse;
import com.joyintech.tams.cfets.trdquery.dao.CfetsTrdQueryMapper;
import com.joyintech.tams.cfets.trdquery.dto.*;
import com.joyintech.tams.common.base.dao.FinProductIssueMapper;
import com.joyintech.tams.common.base.entity.FinProductIssue;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


/**
 * 银行间交易查询
 * @author
 * @version 1.0 2021年10月26日
 * @see CfetsTrdQueryService
 * @since 1.0
 */
@Service("CfetsTrdQueryService")
public class CfetsTrdQueryService extends BaseService{

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(CfetsTrdQueryService.class);
    @Autowired
    private CfetsManualSettleQueryService cfetsManualSettleQueryService;
    @Autowired
    private CfetsTrdQueryMapper cfetsTrdQueryMapper;
    @Autowired
    private CfgReadonlyFormShowMapper cfgReadonlyFormShowMapper;
    @Autowired
    private CfgDataTableFlagMapper cfgDataTableFlagMapper;

    @Autowired
    private CfgDataTableMapper cfgDataTableMapper;
    @Autowired
    private CfetsManualSettleMapper cfetsManualSettleMapper;

    @Autowired
    private CfetsTrdBondFirstSub002Mapper cfetsTrdBondFirstSub002Mapper;

    @Autowired
    private FinProductIssueMapper finProductIssueMapper;

    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    /**
     * 默认设置
     */
    private static final int EFFERANGE_NO = 0;


    /**
     * 默认设置用户ID
     */
    private static final String USER_ID = "COMM";

    /**
     * 用户自定义设置
     */
    private static final int EFFERANGE_IS = 1;
    /**
     * 交易登记列表分页查询
     *
     * @param page int
     * @param length int
     * @param pageParam int
     * @return
     * @see
     * @since 1.0
     */
    public Page<CfetsTrdQueryRegInfoListResponse> queryAllRegInfoList(CfetsTrdQueryRegInfoListRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        String regStatus = (String)pageParam.get("regStatus");
        if(StringUtil.isNotEmpty(regStatus)){
            pageParam.put("regStatusList", StringUtil.splitToList(GlobalConstant.COMMA,regStatus));
        }
        String portfolioId = (String)pageParam.get("portfolioId");
        if(StringUtil.isNotEmpty(portfolioId)){
            pageParam.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA,portfolioId));
        }
        String finprodId = (String)pageParam.get("finprodId");
        if(StringUtil.isNotEmpty(finprodId)){
            pageParam.put("finprodIdList", StringUtil.splitToList(GlobalConstant.COMMA,finprodId));
        }
        if(StringUtil.isNotEmpty(dto.getBusiTypePosition())){
            pageParam.put("busiTypePositionList", StringUtil.splitToList(null,dto.getBusiTypePosition()));
        }
        String regAssetType= (String)pageParam.get("regAssetType");
        if(StringUtil.isNotEmpty(regAssetType)){
            List<String> finprodType2List=new ArrayList<>();
           if(StringUtil.equals(regAssetType,"01")){
               finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
           }else{
               finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
               finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
           }
            pageParam.put("finprodType2List",finprodType2List);
        }
        String secAcctId=(String)pageParam.get("secAcctId");
        if(StringUtil.isNotEmpty(secAcctId)){
            pageParam.put("secAcctIdList", StringUtil.splitToList(GlobalConstant.COMMA,secAcctId));
        }
        if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_0.getValue())) {
            pageParam.put("settleSpeed","0");
        }
        if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_1.getValue())) {
            pageParam.put("settleSpeed","1");
        }
        if(StringUtil.equals(dto.getTabType(),"regCur")){
            pageParam.put("sysDate",iPlatformDate.getSysDate());
        }else if(StringUtil.equals(dto.getTabType(),"regPre")){
          if(!StringUtil.isNotEmpty(dto.getStartDate()) && !StringUtil.isNotEmpty(dto.getEndDate())){
              pageParam.put("startDate",DateUtil.addMonthDate(iPlatformDate.getSysDate(),-1));
              pageParam.put("endDate",iPlatformDate.getSysDate());
          }
        }
        Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.parse(DateUtil.getSysDate()));
        int year = c.get(Calendar.YEAR);
        pageParam.put("year",year);
        pageParam.put("sort",dto.getPageMode().getSort());
        pageParam.put("direction",dto.getPageMode().getDirection());
        String userId = SecurityUtil.getLoginUser().getUserName();
        pageParam.put("ISSUE_CLASS_02", DictCodeListExt4.新债_发行方式_招标发行.getValue());
        pageParam.put("ISSUE_CLASS_03",DictCodeListExt4.新债_发行方式_簿记建档.getValue());
        pageParam.put("ISSUE_CLASS_01",DictCodeListExt4.新债_发行方式_报价发行.getValue());
        pageParam.put("PS_T01",DictCodeList.交易方向_买入.getValue());
        pageParam.put("WIN_TYPE_01",DictCodeListExt4.投标方式_自主投标.getValue());
        pageParam.put("WIN_TYPE_02",DictCodeListExt4.投标方式_代投代缴.getValue());
        pageParam.put("WIN_TYPE_03",DictCodeListExt4.投标方式_分销买入.getValue());
        pageParam.put("TRADE_STATUS_01",DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("TRADE_PLACE_01",DictCodeListExt1.交易场所_银行间.getValue());
        pageParam.put("TRADE_PLACE_02",DictCodeListExt1.交易场所_商业银行柜台.getValue());
        pageParam.put("STATUS_VALID_01",DictCodeListExt1.有效状态_有效.getValue());
        pageParam.put("STATUS_VALID_04",DictCodeListExt1.有效状态_流程中.getValue());
        pageParam.put("BUSI_TYPE_POSITION_01","01");
        pageParam.put("BUSI_TYPE_POSITION_34","34");

        if (StringUtils.isNotEmpty(userId)) {
            //判断角色配置
            List<String> roleList=cfetsTrdQueryMapper.selectSysRoleById(userId);
            if(roleList.contains(DictCodeList.角色岗位_领导.getValue())
                    || roleList.contains(DictCodeList.角色岗位_投资处长.getValue())
                    || roleList.contains(DictCodeList.角色岗位_交易处长.getValue())
            ){
                //角色配置为领导、交易处长、投资处长可查看所有数据
            }else if(roleList.contains(DictCodeList.角色岗位_交易员.getValue())){
                //交易员
                pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
            }else if(roleList.contains(DictCodeList.角色岗位_投资经理.getValue())){
                //投资经理
                pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            }
            //登记
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsTrdQueryMapper.queryAllRegInfoList(pageParam));

        }else{
            return null;
        }
    }
    /**
     * 只读表单设置及数据查询_交易登记列表分页查询
     * @param pageCode
     * @param dtCode
     * @param spcdCode
     * @return
     */
    public Map<String, Object> findRegInfo(String pageCode, String dtCode, String spcdCode, CfetsTrdQueryRegInfoListResponse dto) {
        //spcdCode 01现券买卖 02 回售 03 回购 04 一级申购
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtil.isBlank(spcdCode)) {
            return resultData;
        }
        String spcdCodeTemp = spcdCode;
        //基本信息表单
        spcdCode = spcdCodeTemp + ",B";
        List<CfgReadonlyFormShow> infoFormb = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
        //金额信息表单
        spcdCode = spcdCodeTemp + ",M";
        List<CfgReadonlyFormShow> infoFormm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
        //组合表单
        spcdCode = spcdCodeTemp + ",P";
        List<CfgReadonlyFormShow> infoFormp = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
        //其它表单
        spcdCode = spcdCodeTemp + ",O";
        List<CfgReadonlyFormShow> infoFormo = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);

        //组合表格
        String dtCodeTemp = "CfetsTrdQueryRegPInfo";
        List<CfgDataTableShow> infoTableP;
        //获取登录用户的ID
        String userId = SecurityUtil.getLoginUser().getUserName();
        if (StringUtil.isBlank(userId)) {
            return resultData;
        }
        CfgDataTableShowFlag flagP = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCodeTemp, spcdCodeTemp, userId);
        if (null == flagP) {
            infoTableP = cfgDataTableMapper.getByPageCode(pageCode, dtCodeTemp, spcdCodeTemp, EFFERANGE_NO, USER_ID);
        } else {
            infoTableP = cfgDataTableMapper.getByPageCode(pageCode, dtCodeTemp, spcdCodeTemp, EFFERANGE_IS, userId);
        }
        //质押券表格
        dtCodeTemp = "CfetsTrdQueryRegPledgeInfo";
        List<CfgDataTableShow> infoTablePledge;
        //获取登录用户的ID
        CfgDataTableShowFlag flagPledge = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCodeTemp, spcdCodeTemp, userId);
        if (null == flagPledge) {
            infoTablePledge = cfgDataTableMapper.getByPageCode(pageCode, dtCodeTemp, spcdCodeTemp, EFFERANGE_NO, USER_ID);
        } else {
            infoTablePledge = cfgDataTableMapper.getByPageCode(pageCode, dtCodeTemp, spcdCodeTemp, EFFERANGE_IS, userId);
        }
        resultData.put(dtCode + "_formb", infoFormb);
        resultData.put(dtCode + "_formm", infoFormm);
        resultData.put(dtCode + "_formp", infoFormp);
        resultData.put(dtCode + "_formo", infoFormo);
        resultData.put(dtCode + "_tablep", infoTableP);
        resultData.put(dtCode + "_tablepledge", infoTablePledge);

        Map<String, Object> pageParam = new HashMap<>();
        pageParam.put("totleTradeId", dto.getBusiCode());
        pageParam.put("bidId", dto.getBusiCode());
        //判断角色配置
        List<String> roleList = cfetsTrdQueryMapper.selectSysRoleById(userId);
        if (roleList.contains(DictCodeList.角色岗位_领导.getValue())
                || roleList.contains(DictCodeList.角色岗位_投资处长.getValue())
                || roleList.contains(DictCodeList.角色岗位_交易处长.getValue())
        ) {
            //角色配置为领导、交易处长、投资处长可查看所有数据
        } else if (roleList.contains(DictCodeList.角色岗位_交易员.getValue())) {
            //交易员
            pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
        } else if (roleList.contains(DictCodeList.角色岗位_投资经理.getValue())) {
            //投资经理
            pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
        }
        if (StringUtil.equals(dto.getEffectiveStatus(), DictCodeListExt1.有效状态_流程中.getValue())) {
            pageParam.put("effectiveStatus", DictCodeListExt1.有效状态_流程中.getValue());
        } else if (StringUtil.equals(dto.getEffectiveStatus(), DictCodeListExt1.有效状态_撤销.getValue())) {
            pageParam.put("effectiveStatus", DictCodeListExt1.有效状态_撤销.getValue());
        } else {
            pageParam.put("effectiveStatus", DictCodeListExt1.有效状态_有效.getValue());
        }
        pageParam.put("STATUS_VALID_01", DictCodeListExt1.有效状态_有效.getValue());
        pageParam.put("STATUS_VALID_03", DictCodeListExt1.有效状态_撤销.getValue());
        pageParam.put("STATUS_VALID_04", DictCodeListExt1.有效状态_流程中.getValue());

        //表单data
        CfetsTrdBondFirstSubInfoDto dataformBid = new CfetsTrdBondFirstSubInfoDto();
        CfetsTrdQueryRegInfoListResponse dataform = new CfetsTrdQueryRegInfoListResponse();
        if (StringUtil.equals("04", spcdCodeTemp)) {
            List<CfetsTrdBondFirstSubInfoDto> listTmp = cfetsTrdBondFirstSub002Mapper.viewPage(pageParam);
            if (StringUtil.isNotEmpty(listTmp)) {
                dataformBid = listTmp.get(0);
            }
        } else {
            dataform = cfetsTrdQueryMapper.selectRegFormDataByKey(pageParam);
        }
        //表格组合分配data
        List<CfetsPortfolioIdFenPeiInfoResponse> portIntFenPeiInfoDtoList = cfetsTrdQueryMapper.selectRegTableFenPeiInfoList(pageParam);
        //表格质押券data
        pageParam.put("TRADEPLACE01", DictCodeListExt1.交易场所_银行间.getValue());
        pageParam.put("TRADEPLACE03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        pageParam.put("TRADEPLACE04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        pageParam.put("platDate", iPlatformDate.getSysDateStr());
        pageParam.put("S_002", DictCodeListExt3.资产类型_可质押持仓.getValue());
        pageParam.put("sysDate", iPlatformDate.getSysDateStr());
        //    pageParam.put("totleTradeId",dto.getBusiCode());
        List<CfetsPledgeInfoResponse> pledgeInfoDtoList = cfetsTrdQueryMapper.selectRegTablePledgeInfoList(pageParam);

        if (StringUtil.equals("04", spcdCodeTemp)) {
            if (StringUtil.isNotEmpty(dataformBid.getRate())) {//当息票品种为附息-固定利率时显示票面利率
                dataformBid.setRateStr(ArithmeticUtil.round(
                        ArithmeticUtil.multiply(dataformBid.getRate(), new BigDecimal(100)), 4, RoundingMode.HALF_UP) + "%");
            } else if (StringUtil.isNotEmpty(dataformBid.getReferenceRateStr())) {//当息票品种为附息-浮动利率时显示参考收益率
                dataformBid.setReferenceRateStr(dataformBid.getReferenceRateStr() + "BP");
            } else {//当息票品种为零息、贴现、利随本清时显示参考收益率
                dataformBid.setReferenceRateStr(ArithmeticUtil.round(
                        ArithmeticUtil.multiply(dataformBid.getReferenceRate(), new BigDecimal(100)), 4, RoundingMode.HALF_UP) + "%");
            }
            dataformBid.setDipType(SysDataUtil.getNameFromDataDict(BussinessDictCode.分配方式, dataformBid.getDipType()));
            dataformBid.getProductDtos().forEach(item->item.setSubscriptionAmount(ArithmeticUtil.divide(item.getSubscriptionAmount(),new BigDecimal(10000),2)));
            resultData.put(dtCode + "_dataform", dataformBid);
            resultData.put(dtCode + "_datatablep", dataformBid.getProductDtos());
        } else {
            resultData.put(dtCode + "_dataform", dataform);
            resultData.put(dtCode + "_datatablep", portIntFenPeiInfoDtoList);
        }
        resultData.put(dtCode + "_datatablepledge", pledgeInfoDtoList);

        return resultData;
    }

    /**
     * 只读表单设置及数据查询_交易登记列表分页查询
     * @param pageCode
     * @param dtCode
     * @param spcdCode
     * @return
     */
    public Map<String, Object> findRegBidInfo(String pageCode, String dtCode, String spcdCode, CfetsTrdQueryRegInfoListResponse dto) {
        //spcdCode 01现券买卖 02 回售 03 回购 04 一级申购
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtil.isBlank(spcdCode)) {
            return resultData;
        }

        String spcdCodeTemp = spcdCode;
        if (StringUtil.equals(spcdCode, DictCodeListExt3.头寸业务类型_债券一级投标.getValue())) {
            //基本信息表单
            spcdCode = spcdCodeTemp + ",BB";
            List<CfgReadonlyFormShow> infoFormb = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            //其它表单
            spcdCode = spcdCodeTemp + ",BO";
            List<CfgReadonlyFormShow> infoFormo = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            //组合表格
            List<CfgDataTableShow> infoTableP;
            List<CfgDataTableShow> infoTableH;
            //获取登录用户的ID
            String userId = SecurityUtil.getLoginUser().getUserName();
            if (StringUtil.isBlank(userId)) {
                return resultData;
            }
            String spcdCodeTabTemp = spcdCodeTemp + ",B";
            CfgDataTableShowFlag flagP = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCode, spcdCodeTabTemp, userId);
            if (null == flagP) {
                infoTableP = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCodeTabTemp, EFFERANGE_NO, USER_ID);
            } else {
                infoTableP = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCodeTabTemp, EFFERANGE_IS, userId);
            }
            spcdCodeTabTemp = spcdCodeTemp + ",H";
            CfgDataTableShowFlag flagH = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCode, spcdCodeTabTemp, userId);
            if (null == flagH) {
                infoTableH = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCodeTabTemp, EFFERANGE_NO, USER_ID);
            } else {
                infoTableH = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCodeTabTemp, EFFERANGE_IS, userId);
            }

            resultData.put(dtCode + "_formb", infoFormb);
            resultData.put(dtCode + "_formo", infoFormo);
            resultData.put(dtCode + "_tablep", infoTableP);
            resultData.put(dtCode + "_tableh", infoTableH);
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("bidId", dto.getBusiCode());
            //判断角色配置
            List<String> roleList = cfetsTrdQueryMapper.selectSysRoleById(userId);
            if (roleList.contains(DictCodeList.角色岗位_领导.getValue())
                    || roleList.contains(DictCodeList.角色岗位_投资处长.getValue())
                    || roleList.contains(DictCodeList.角色岗位_交易处长.getValue())
            ) {
                //角色配置为领导、交易处长、投资处长可查看所有数据
            } else if (roleList.contains(DictCodeList.角色岗位_交易员.getValue())) {
                //交易员
                pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
            } else if (roleList.contains(DictCodeList.角色岗位_投资经理.getValue())) {
                //投资经理
                pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            }
            pageParam.put("TRADE_STATUS_01", DictCodeListExt1.有效状态_有效.getValue());
            //表单data
            CfetsTrdBidInfoResponse dataform = cfetsTrdQueryMapper.findYjtbBidTradeDetailInfo(pageParam);
            List<BidPortIntFenPeiInfoDto> datalist = cfetsTrdQueryMapper.findYjtbFenPeiInfoList(pageParam);
            List<BidPortIntFenPeiInfoDto> datahlist = new ArrayList<>();
            BidPortIntFenPeiInfoDto bidPortIntFenPeiInfoDto = new BidPortIntFenPeiInfoDto();
            BigDecimal winPrice = null;
            BigDecimal totalWinPrinAmt = null;
            BigDecimal bidPrinAmt = null;
            for(BidPortIntFenPeiInfoDto fenPeiInfoDto:datalist){
                if (null!=fenPeiInfoDto.getTotalWinPrinAmt() && fenPeiInfoDto.getTotalWinPrinAmt().compareTo(BigDecimal.ONE)!=0){
                    winPrice = ArithmeticUtil.doAdd(winPrice,fenPeiInfoDto.getWinPrice());
                    totalWinPrinAmt= ArithmeticUtil.doAdd(totalWinPrinAmt,fenPeiInfoDto.getTotalWinPrinAmt());
                    bidPrinAmt= ArithmeticUtil.doAdd(bidPrinAmt,fenPeiInfoDto.getBidPrinAmt());
                }
            }
            if (StringUtil.isNotEmpty(winPrice)){
                bidPortIntFenPeiInfoDto.setWinPrice(winPrice);
                bidPortIntFenPeiInfoDto.setTotalWinPrinAmt(totalWinPrinAmt);
                bidPortIntFenPeiInfoDto.setBidPrinAmt(bidPrinAmt);
                datahlist.add(bidPortIntFenPeiInfoDto);
            }
            resultData.put(dtCode + "_dataform", dataform);
            resultData.put(dtCode + "_datalist", datalist);
            resultData.put(dtCode + "_datahlist", datahlist);
        }else if (StringUtil.equals(spcdCode, DictCodeListExt3.头寸业务类型_债券一级申购.getValue())) {
            //基本信息表单
            spcdCode = spcdCodeTemp + ",B";
            List<CfgReadonlyFormShow> infoFormb = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            //其它表单
            spcdCode = spcdCodeTemp + ",O";
            List<CfgReadonlyFormShow> infoFormo = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            //组合表格
            List<CfgDataTableShow> infoTableP;
            List<CfgDataTableShow> infoTableH;
            //获取登录用户的ID
            String userId = SecurityUtil.getLoginUser().getUserName();
            if (StringUtil.isBlank(userId)) {
                return resultData;
            }
            String spcdCodeTabTemp = spcdCodeTemp + ",B";
            CfgDataTableShowFlag flagP = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCode, spcdCodeTabTemp, userId);
            if (null == flagP) {
                infoTableP = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCodeTabTemp, EFFERANGE_NO, USER_ID);
            } else {
                infoTableP = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCodeTabTemp, EFFERANGE_IS, userId);
            }
            resultData.put(dtCode + "_formb", infoFormb);
            resultData.put(dtCode + "_formo", infoFormo);
            resultData.put(dtCode + "_tablep", infoTableP);
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("bidId", dto.getBusiCode());
            //判断角色配置
            List<String> roleList = cfetsTrdQueryMapper.selectSysRoleById(userId);
            if (roleList.contains(DictCodeList.角色岗位_领导.getValue())
                    || roleList.contains(DictCodeList.角色岗位_投资处长.getValue())
                    || roleList.contains(DictCodeList.角色岗位_交易处长.getValue())
            ) {
                //角色配置为领导、交易处长、投资处长可查看所有数据
            } else if (roleList.contains(DictCodeList.角色岗位_交易员.getValue())) {
                //交易员
                pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
            } else if (roleList.contains(DictCodeList.角色岗位_投资经理.getValue())) {
                //投资经理
                pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            }
            pageParam.put("TRADE_STATUS_01", DictCodeListExt1.有效状态_有效.getValue());
            //表单data
            CfetsTrdBidInfoResponse dataform = cfetsTrdQueryMapper.findYjsgBidTradeDetailInfo(pageParam);
            List<BidPortIntFenPeiInfoDto> datalist = cfetsTrdQueryMapper.findYjsgFenPeiInfoList(pageParam);
            resultData.put(dtCode + "_dataform", dataform);
            resultData.put(dtCode + "_datalist", datalist);
        }
        return resultData;
    }

    /**
     * 报价列表分页查询
     *
     * @return
     * @see
     * @since 1.0
     */
    public Page<CfetsTrdQueryIbuInfoListResponse> queryAllIbuInfoList(CfetsTrdQueryIbuInfoListRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        String regStatus = (String)pageParam.get("quotationState");
        if(StringUtil.isNotEmpty(regStatus)){
            pageParam.put("quotationStateList", StringUtil.splitToList(GlobalConstant.COMMA,regStatus));
        }

        String portfolioId = (String)pageParam.get("portfolioId");
        if(StringUtil.isNotEmpty(portfolioId)){
            pageParam.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA,portfolioId));
        }
        String finprodId = (String)pageParam.get("finprodId");
        if(StringUtil.isNotEmpty(finprodId)){
            pageParam.put("finprodIdList", StringUtil.splitToList(GlobalConstant.COMMA,finprodId));
        }
     /*   String regAssetType= (String)pageParam.get("regAssetType");
        if(StringUtil.isNotEmpty(regAssetType)){
            List<String> finprodType2List=new ArrayList<>();
            if(StringUtil.equals(regAssetType,"01")){
                finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            }else{
                finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
                finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            }
            pageParam.put("finprodType2List",finprodType2List);
        }*/
        String secAcctId=(String)pageParam.get("secAcctId");
        if(StringUtil.isNotEmpty(secAcctId)){
            pageParam.put("secAcctIdList", StringUtil.splitToList(GlobalConstant.COMMA,secAcctId));
        }
        if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_0.getValue())) {
            pageParam.put("settleSpeed","0");
        }
        if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_1.getValue())) {
            pageParam.put("settleSpeed","1");
        }
        if(StringUtil.equals(dto.getTabType(),"ibuCur")){
            pageParam.put("sysDate",iPlatformDate.getSysDate());
        }else if(StringUtil.equals(dto.getTabType(),"ibuPre")){
            if(!StringUtil.isNotEmpty(dto.getStartDate()) && !StringUtil.isNotEmpty(dto.getEndDate())){
                pageParam.put("startDate",DateUtil.addMonthDate(iPlatformDate.getSysDate(),-1));
                pageParam.put("endDate",iPlatformDate.getSysDate());
            }
        }
        //有效状态
        pageParam.put("STATUS_VALID_01", DictCodeListExt1.有效状态_有效.getValue());
        pageParam.put("STATUS_VALID_03", DictCodeListExt1.有效状态_撤销.getValue());
        pageParam.put("STATUS_VALID_04", DictCodeListExt1.有效状态_流程中.getValue());

        //报价状态
        pageParam.put("DEAL_TYPE_IBU_01", BussinessDictCodeList.银行间成交状态_未成交.getValue());
        pageParam.put("DEAL_TYPE_IBU_02", BussinessDictCodeList.银行间成交状态_已成交.getValue());

        //资产类型
        pageParam.put("REG_ASSET_TYPE_01", DictCodeListExt1.登记资产类型_债券.getValue());
        pageParam.put("REG_ASSET_TYPE_02", DictCodeListExt1.登记资产类型_质押式回购.getValue());


        //报价状态为已撤销就是已撤销

        Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.parse(DateUtil.getSysDate()));
        int year = c.get(Calendar.YEAR);
        pageParam.put("year",year);
        pageParam.put("sort",dto.getPageMode().getSort());
        pageParam.put("direction",dto.getPageMode().getDirection());
        String userId = SecurityUtil.getLoginUser().getUserName();
        if (StringUtils.isNotEmpty(userId)) {
            //判断角色配置
            List<String> roleList=cfetsTrdQueryMapper.selectSysRoleById(userId);
            if(roleList.contains(DictCodeList.角色岗位_领导.getValue())
                    || roleList.contains(DictCodeList.角色岗位_投资处长.getValue())
                    || roleList.contains(DictCodeList.角色岗位_交易处长.getValue())
            ){
                //角色配置为领导、交易处长、投资处长可查看所有数据
            }else if(roleList.contains(DictCodeList.角色岗位_交易员.getValue())){
                //交易员
                pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
            }else if(roleList.contains(DictCodeList.角色岗位_投资经理.getValue())){
                //投资经理
                pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            }
            //报价
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsTrdQueryMapper.queryAllIbuInfoList(pageParam));

        }else{
            return null;
        }
    }
    /**
     * 只读表单设置及数据查询_报价列表分页查询
     * @param pageCode
     * @param dtCode
     * @param spcdCode
     * @return
     */
    public Map<String, Object> findIbuInfo(String pageCode, String dtCode, String spcdCode, CfetsTrdQueryIbuInfoListResponse dto) {
        //spcdCode 01:现券买卖 03:回购
        Map<String, Object> resultData = new HashMap<>();
        if(StringUtil.isBlank(spcdCode)){
            return resultData;
        }
        String spcdCodeTemp =spcdCode;
        //交谈记录信息
        spcdCode = spcdCodeTemp+",T";
        List<CfgReadonlyFormShow> infoFormt = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode);
        //基本信息表单
        spcdCode = spcdCodeTemp+",B";
        List<CfgReadonlyFormShow> infoFormb = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode);
        //金额信息表单
        spcdCode = spcdCodeTemp+",M";
        List<CfgReadonlyFormShow> infoFormm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode);
        //组合表单
        spcdCode = spcdCodeTemp+",P";
        List<CfgReadonlyFormShow> infoFormp = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode);
        //其它表单
        spcdCode = spcdCodeTemp+",O";
        List<CfgReadonlyFormShow> infoFormo = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode);

        //组合表格
        String dtCodeTemp ="CfetsTrdQueryIbuPInfo";
        List<CfgDataTableShow> infoTableP;
        //获取登录用户的ID
        String userId = SecurityUtil.getLoginUser().getUserName();
        if(StringUtil.isBlank(userId)){
            return resultData;
        }
        CfgDataTableShowFlag flagP = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCodeTemp,spcdCodeTemp, userId);
        if (null == flagP) {
            infoTableP = cfgDataTableMapper.getByPageCode(pageCode, dtCodeTemp,spcdCodeTemp, EFFERANGE_NO, USER_ID);
        } else {
            infoTableP = cfgDataTableMapper.getByPageCode(pageCode, dtCodeTemp,spcdCodeTemp, EFFERANGE_IS, userId);;
        }
        //质押券表格
        dtCodeTemp ="CfetsTrdQueryIbuPledgeInfo";
        List<CfgDataTableShow> infoTablePledge;
        //获取登录用户的ID
        CfgDataTableShowFlag flagPledge = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCodeTemp,spcdCodeTemp, userId);
        if (null == flagPledge) {
            infoTablePledge = cfgDataTableMapper.getByPageCode(pageCode, dtCodeTemp,spcdCodeTemp, EFFERANGE_NO, USER_ID);
        } else {
            infoTablePledge = cfgDataTableMapper.getByPageCode(pageCode, dtCodeTemp,spcdCodeTemp, EFFERANGE_IS, userId);;
        }
        resultData.put(dtCode+"_formt",infoFormt);
        resultData.put(dtCode+"_formb",infoFormb);
        resultData.put(dtCode+"_formm",infoFormm);
        resultData.put(dtCode+"_formp",infoFormp);
        resultData.put(dtCode+"_formo",infoFormo);
        resultData.put(dtCode+"_tablep",infoTableP);
        resultData.put(dtCode+"_tablepledge",infoTablePledge);

        Map<String, Object> pageParam = new HashMap<>();
        pageParam.put("quoteId",dto.getQuoteId());
        //判断角色配置
        List<String> roleList=cfetsTrdQueryMapper.selectSysRoleById(userId);
        if(roleList.contains(DictCodeList.角色岗位_领导.getValue())
                || roleList.contains(DictCodeList.角色岗位_投资处长.getValue())
                || roleList.contains(DictCodeList.角色岗位_交易处长.getValue())
        ){
            //角色配置为领导、交易处长、投资处长可查看所有数据
        }else if(roleList.contains(DictCodeList.角色岗位_交易员.getValue())){
            //交易员
            pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
        }else if(roleList.contains(DictCodeList.角色岗位_投资经理.getValue())){
            //投资经理
            pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
        }

        //表单data
        CfetsTrdQueryIbuInfoListResponse dataform=new CfetsTrdQueryIbuInfoListResponse();
        pageParam.put("effectiveStatus", dto.getEffectiveStatus() == null ? dto.getEffectFlag() : dto.getEffectiveStatus());
        //有效状态
        pageParam.put("STATUS_VALID_01", DictCodeListExt1.有效状态_有效.getValue());
        pageParam.put("STATUS_VALID_03", DictCodeListExt1.有效状态_撤销.getValue());
        pageParam.put("STATUS_VALID_04", DictCodeListExt1.有效状态_流程中.getValue());
        pageParam.put("TRADE_FLOW_STATUS01", DictCodeListExt1.交易流程状态_流程中.getValue());
        pageParam.put("TRADE_FLOW_STATUS02", DictCodeListExt1.交易流程状态_有效.getValue());
        pageParam.put("TRADE_FLOW_STATUS03", DictCodeListExt1.交易流程状态_撤销.getValue());
        if (StringUtil.equals(dto.getFinprodType2(), "01")) {
            dataform = cfetsTrdQueryMapper.selectIbuBondFormDataByKey(pageParam);
        } else {
            dataform = cfetsTrdQueryMapper.selectIbuRepoFormDataByKey(pageParam);
        }
        //表格组合分配data
        List<CfetsPortfolioIdFenPeiInfoResponse> portIntFenPeiInfoDtoList =new ArrayList<>();
        if(StringUtil.equals(dto.getFinprodType2(),"01")){
            portIntFenPeiInfoDtoList =  cfetsTrdQueryMapper.selectIbuBondTableFenPeiInfoList(pageParam);
        }else{
            portIntFenPeiInfoDtoList =  cfetsTrdQueryMapper.selectIbuRepoTableFenPeiInfoList(pageParam);
        }
        //表格质押券data
        pageParam.put("TRADEPLACE01", DictCodeListExt1.交易场所_银行间.getValue());
        pageParam.put("TRADEPLACE03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        pageParam.put("TRADEPLACE04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        pageParam.put("platDate", iPlatformDate.getSysDateStr());
        //表格质押券data
        List<CfetsPledgeInfoResponse> pledgeInfoDtoList =  cfetsTrdQueryMapper.selectIbuTablePledgeInfoList(pageParam);


        resultData.put(dtCode+"_dataform",dataform);
        resultData.put(dtCode+"_datatablep",portIntFenPeiInfoDtoList);
        resultData.put(dtCode+"_datatablepledge",pledgeInfoDtoList);

        return resultData;
    }
    /**
     * 成交列表分页查询
     *
     * @return
     * @see
     * @since 1.0
     */
    public Page<CfetsTrdQueryCstpInfoListResponse> queryAllCstpInfoList(CfetsTrdQueryCstpInfoListRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);

        String matchStatus = (String)pageParam.get("matchStatus");
        if(StringUtil.isNotEmpty(matchStatus)){
            pageParam.put("matchStatusList", StringUtil.splitToList(GlobalConstant.COMMA,matchStatus));
        }
        String finprodId = (String)pageParam.get("finprodId");
        if(StringUtil.isNotEmpty(finprodId)){
            pageParam.put("finprodIdList", StringUtil.splitToList(GlobalConstant.COMMA,finprodId));
        }
        String regAssetType= (String)pageParam.get("regAssetType");
        if(StringUtil.isNotEmpty(regAssetType)){
            if(StringUtil.equals(regAssetType,"01")){
                pageParam.put("regAssetType","BOND");
            }else{
                pageParam.put("regAssetType","REPO");
            }
        }
        String secAcctId=(String)pageParam.get("acctNo");
        if(StringUtil.isNotEmpty(secAcctId)){
            pageParam.put("secAcctIdList", StringUtil.splitToList(GlobalConstant.COMMA,secAcctId));
        }
        String traderid=(String)pageParam.get("traderid");
        if(StringUtil.isNotEmpty(traderid)){
            pageParam.put("traderidList", StringUtil.splitToList(GlobalConstant.COMMA,traderid));
        }
        String counterId=(String)pageParam.get("counterId");
        if(StringUtil.isNotEmpty(counterId)){
            pageParam.put("counterIdList", StringUtil.splitToList(GlobalConstant.COMMA,counterId));
        }
     /*   if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_0.getValue())) {
            pageParam.put("settleSpeed","0");
        }
        if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_1.getValue())) {
            pageParam.put("settleSpeed","1");
        }*/
        if(StringUtil.equals(dto.getTabType(),"cstpCur")){
            pageParam.put("sysDate",iPlatformDate.getSysDate());
        }else if(StringUtil.equals(dto.getTabType(),"cstpPre")){
            if(!StringUtil.isNotEmpty(dto.getStartDate()) && !StringUtil.isNotEmpty(dto.getEndDate())){
                pageParam.put("startDate",DateUtil.addMonthDate(iPlatformDate.getSysDate(),-1));
                pageParam.put("endDate",iPlatformDate.getSysDate());
            }
        }
        Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.parse(DateUtil.getSysDate()));
        int year = c.get(Calendar.YEAR);
        pageParam.put("year",year);
        pageParam.put("sort",dto.getPageMode().getSort());
        pageParam.put("direction",dto.getPageMode().getDirection());
        String userId = SecurityUtil.getLoginUser().getUserName();
        if (StringUtils.isNotEmpty(userId)) {
            //判断角色配置
            List<String> roleList=cfetsTrdQueryMapper.selectSysRoleById(userId);
            if(roleList.contains(DictCodeList.角色岗位_领导.getValue())
                    || roleList.contains(DictCodeList.角色岗位_投资处长.getValue())
                    || roleList.contains(DictCodeList.角色岗位_交易处长.getValue())
            ){
                //角色配置为领导、交易处长、投资处长可查看所有数据
            }else if(roleList.contains(DictCodeList.角色岗位_交易员.getValue())){
                //交易员
                pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
            }else if(roleList.contains(DictCodeList.角色岗位_投资经理.getValue())){
                //投资经理
                pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            }
            pageParam.put("BUSI_TYPE_POSITION_34", DictCodeListExt3.头寸业务类型_债券一级申购.getValue());
            pageParam.put("PS_T01", DictCodeList.交易方向_买入.getValue());
            pageParam.put("TENDER_TARGET_01", DictCodeListExt4.招标标的_利率.getValue());
            pageParam.put("TENDER_TARGET_02", DictCodeListExt4.招标标的_价格.getValue());
            pageParam.put("TENDER_TARGET_03", DictCodeListExt4.招标标的_利差.getValue());
            pageParam.put("TRADE_PLACE_01", DictCodeListExt3.交易场所_银行间.getValue());
            pageParam.put("TRADE_PLACE_02", DictCodeListExt3.交易场所_商业银行柜台.getValue());
            //成交编号
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsTrdQueryMapper.queryAllCstpInfoList(pageParam));

        }else{
            return null;
        }
    }
    /**
     * 只读表单设置及数据查询_报价列表分页查询
     * @param pageCode
     * @param dtCode
     * @param spcdCode
     * @return
     */
    public Map<String, Object> findCstpInfo(String pageCode, String dtCode, String spcdCode, CfetsTrdQueryCstpInfoListResponse dto) {
        Map<String, Object> resultData = new HashMap<>();
        if(StringUtil.isBlank(spcdCode)){
            return resultData;
        }
        String spcdCodeTemp =spcdCode;
        //基本信息表单
        spcdCode = spcdCodeTemp+",B";
        List<CfgReadonlyFormShow> infoFormb = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode);
        //金额信息表单
        spcdCode = spcdCodeTemp+",M";
        List<CfgReadonlyFormShow> infoFormm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode);
        List<CfgReadonlyFormShow> infoFormbf=new ArrayList<>();
        List<CfgReadonlyFormShow> infoFormsf=new ArrayList<>();
        List<CfgReadonlyFormShow> infoFormrs=new ArrayList<>();
        List<CfgReadonlyFormShow> infoFormrb=new ArrayList<>();
        if(StringUtil.equals(spcdCodeTemp,"01")){
            //组合表单
            spcdCode = spcdCodeTemp+",BF";
            infoFormbf = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode);
            //其它表单
            spcdCode = spcdCodeTemp+",SF";
            infoFormsf = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode);
        }else if(StringUtil.equals(spcdCodeTemp,"03")){
            //组合表单
            spcdCode = spcdCodeTemp+",RS";
            infoFormrs = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode);
            //其它表单
            spcdCode = spcdCodeTemp+",RB";
            infoFormrb = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode);
        }

        //获取登录用户的ID
        String userId = SecurityUtil.getLoginUser().getUserName();
        //质押券表格
        String dtCodeTemp ="CfetsTrdQueryCstpPledgeInfo";
        List<CfgDataTableShow> infoTablePledge;
        //获取登录用户的ID
        CfgDataTableShowFlag flagPledge = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCodeTemp,spcdCodeTemp, userId);
        if (null == flagPledge) {
            infoTablePledge = cfgDataTableMapper.getByPageCode(pageCode, dtCodeTemp,spcdCodeTemp, EFFERANGE_NO, USER_ID);
        } else {
            infoTablePledge = cfgDataTableMapper.getByPageCode(pageCode, dtCodeTemp,spcdCodeTemp, EFFERANGE_IS, userId);;
        }

        resultData.put(dtCode+"_formb",infoFormb);
        resultData.put(dtCode+"_formm",infoFormm);
        resultData.put(dtCode+"_formbf",infoFormbf);
        resultData.put(dtCode+"_formsf",infoFormsf);
        resultData.put(dtCode+"_formrs",infoFormrs);
        resultData.put(dtCode+"_formrb",infoFormrb);
        resultData.put(dtCode+"_tablepledge",infoTablePledge);

        Map<String, Object> pageParam = new HashMap<>();
        pageParam.put("execId",dto.getExecid());
        //判断角色配置
        List<String> roleList=cfetsTrdQueryMapper.selectSysRoleById(userId);
        if(roleList.contains(DictCodeList.角色岗位_领导.getValue())
                || roleList.contains(DictCodeList.角色岗位_投资处长.getValue())
                || roleList.contains(DictCodeList.角色岗位_交易处长.getValue())
        ){
            //角色配置为领导、交易处长、投资处长可查看所有数据
        }else if(roleList.contains(DictCodeList.角色岗位_交易员.getValue())){
            //交易员
            pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
        }else if(roleList.contains(DictCodeList.角色岗位_投资经理.getValue())){
            //投资经理
            pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
        }

        //表单data
        CfetsTrdQueryCstpInfoListResponse dataform=new CfetsTrdQueryCstpInfoListResponse();
        if(StringUtil.equals(dto.getBusinesstype(),"BOND")){
            dataform=cfetsTrdQueryMapper.selectCstpBondFormDataByKey(pageParam);
        }else if(StringUtil.equals(dto.getBusinesstype(),"REPO")){
            dataform=cfetsTrdQueryMapper.selectCstpRepoFormDataByKey(pageParam);
        }
        //表格质押券data
        List<CfetsPledgeInfoResponse> pledgeInfoDtoList =  cfetsTrdQueryMapper.selectCstpTablePledgeInfoList(pageParam);

        resultData.put(dtCode+"_dataform",dataform);
        resultData.put(dtCode+"_datatablepledge",pledgeInfoDtoList);

        return resultData;
    }

    /**
     * 只读表单设置及数据查询_报价列表分页查询
     * @param pageCode
     * @param dtCode
     * @param spcdCode
     * @return
     */
    public Map<String, Object> findBidCstpInfo(String pageCode, String dtCode, String spcdCode, CfetsTrdQueryCstpInfoListResponse dto) {
        Map<String, Object> resultData = new HashMap<>();
        if(StringUtil.isBlank(spcdCode)){
            return resultData;
        }
        String spcdCodeTemp =spcdCode;
        //基本信息
        spcdCode = spcdCodeTemp+",BB";
        List<CfgReadonlyFormShow> infoFormb = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode);
        //发行信息
        spcdCode = spcdCodeTemp+",BS";
        List<CfgReadonlyFormShow> infoForms = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode);
        //发行信息
        spcdCode = spcdCodeTemp+",BF";
        List<CfgReadonlyFormShow> infoFormf = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode);

        resultData.put(dtCode+"_formb",infoFormb);
        resultData.put(dtCode+"_forms",infoForms);
        resultData.put(dtCode+"_formf",infoFormf);

        Map<String, Object> pageParam = new HashMap<>();
        pageParam.put("quoteId",dto.getQuoteId());
        //获取登录用户的ID
        String userId = SecurityUtil.getLoginUser().getUserName();
        //判断角色配置
        List<String> roleList=cfetsTrdQueryMapper.selectSysRoleById(userId);
        if(roleList.contains(DictCodeList.角色岗位_领导.getValue())
                || roleList.contains(DictCodeList.角色岗位_投资处长.getValue())
                || roleList.contains(DictCodeList.角色岗位_交易处长.getValue())
        ){
            //角色配置为领导、交易处长、投资处长可查看所有数据
        }else if(roleList.contains(DictCodeList.角色岗位_交易员.getValue())){
            //交易员
            pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
        }else if(roleList.contains(DictCodeList.角色岗位_投资经理.getValue())){
            //投资经理
            pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
        }
        CfetsTrdQueryCstpInfoListResponse dataform = cfetsTrdQueryMapper.selectCstpBidFormDataByKey(pageParam);
        //表格质押券data
        resultData.put(dtCode+"_dataform",dataform);
        return resultData;
    }

    /**
     * 交易汇总分页查询
     * @param dto 入数
     * @return Page<CfetsTrdQueryTradeInfoListResponse>
     */
    public Page<CfetsTrdQueryTradeInfoListResponse> queryAllTradeInfoList(CfetsTrdQueryTradeInfoListRequest dto) {
        Map<String,Object> pageParam = BeanUtil.object2Map(dto);
        String effectiveStatus = (String)pageParam.get("effectiveStatus");
       /* if(StringUtil.isNotEmpty(effectiveStatus)){
            List<String> list=StringUtil.splitToList(GlobalConstant.COMMA,effectiveStatus);
            if(list.contains(DictCodeListExt1.有效状态_有效.getValue())){
                list.add("02");
            }
            pageParam.put("effectiveStatusList",list);
        }*/
        if(StringUtil.isNotEmpty(effectiveStatus)){
            pageParam.put("effectiveStatusList",StringUtil.splitToList(GlobalConstant.COMMA,effectiveStatus));
        }
        String regAssetType= (String)pageParam.get("regAssetType");
        if(StringUtil.isNotEmpty(regAssetType)){
            List<String> finprodType2List=new ArrayList<>();
            if(StringUtil.equals(regAssetType,"01")){
                finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            }else{
                finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
                finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            }
            pageParam.put("finprodType2List",finprodType2List);
        }
        String finprodId = (String)pageParam.get("finprodId");
        if(StringUtil.isNotEmpty(finprodId)){
            pageParam.put("finprodIdList", StringUtil.splitToList(GlobalConstant.COMMA,finprodId));
        }
        if(!StringUtil.isNotEmpty(dto.getStartDate()) && !StringUtil.isNotEmpty(dto.getEndDate())){
            pageParam.put("startDate",iPlatformDate.getSysDate());
            pageParam.put("endDate",iPlatformDate.getSysDate());
        }

        //有效状态
//        pageParam.put("STATUS_VALID_01", DictCodeListExt1.有效状态_有效.getValue());
        pageParam.put("STATUS_VALID_03", DictCodeListExt1.有效状态_撤销.getValue());

        //平安业务类型
        pageParam.put("PINAN_BUSINESS_TYPE_09", DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_10", DictCodeListExt1.平安业务类型_债券类资产卖出.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_11", DictCodeListExt1.平安业务类型_债券类资产行权.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_20", DictCodeListExt1.平安业务类型_正回购.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_74", DictCodeListExt1.平安业务类型_买断式正回购.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_75", DictCodeListExt1.平安业务类型_买断式逆回购.getValue());

        //收付方向
        pageParam.put("feePs02", DictCodeListExt1.收付方向_支出.getValue());
        //结算状态
        pageParam.put("settlementStatus01", DictCodeList.结算状态_已结算.getValue());
        pageParam.put("settlementStatus02", DictCodeList.结算状态_已结算.getValue());
        //指令处理状态
        pageParam.put("commandDealStatus01", DictCodeList.指令处理状态_处理成功.getValue());
        pageParam.put("commandDealStatus02", DictCodeList.指令处理状态_处理失败.getValue());
        //指令处理状态
        pageParam.put("cleanStatus01", DictCodeListExt3.清算状态_清算成功.getValue());
        pageParam.put("cleanStatus02", DictCodeListExt3.清算状态_清算失败.getValue());
        pageParam.put("cleanStatus03", DictCodeListExt3.清算状态_清算中.getValue());

        //交收状态
        pageParam.put("DELIVERY_STATUS_01", DictCodeListExt3.交收状态_未交收.getValue());
        pageParam.put("DELIVERY_STATUS_02", DictCodeListExt3.交收状态_已交收.getValue());
        pageParam.put("DELIVERY_STATUS_NAME01", DictCodeListExt3.交收状态_未交收.getCodeName());

        pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("TRADE_STATUS_03", DictCodeListExt1.交易状态_撤销.getValue());

        pageParam.put("isCalcN", BussinessDictCodeList.是否_否.getValue());
        pageParam.put("PS_T07", DictCodeList.交易方向_到期.getValue());
        pageParam.put("PS_T08", DictCodeList.交易方向_还本.getValue());
        pageParam.put("PS_T09", DictCodeList.交易方向_收息.getValue());
        pageParam.put("PS_T39", DictCodeList.交易方向_行权_赎回.getValue());
        pageParam.put("PS_T03", DictCodeList.交易方向_行权_回售.getValue());
        pageParam.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        pageParam.put("PS_T02", DictCodeList.交易方向_卖出.getValue());

        pageParam.put("ISSUE_CLASS_02", DictCodeListExt4.新债_发行方式_招标发行.getValue());
        pageParam.put("ISSUE_CLASS_03",DictCodeListExt4.新债_发行方式_簿记建档.getValue());
        pageParam.put("ISSUE_CLASS_01",DictCodeListExt4.新债_发行方式_报价发行.getValue());
        pageParam.put("WIN_TYPE_01",DictCodeListExt4.投标方式_自主投标.getValue());
        pageParam.put("WIN_TYPE_02",DictCodeListExt4.投标方式_代投代缴.getValue());
        pageParam.put("WIN_TYPE_03",DictCodeListExt4.投标方式_分销买入.getValue());
        pageParam.put("TRADE_PLACE_01",DictCodeListExt1.交易场所_银行间.getValue());
        pageParam.put("TRADE_PLACE_02",DictCodeListExt1.交易场所_商业银行柜台.getValue());
        pageParam.put("STATUS_VALID_01",DictCodeListExt1.有效状态_有效.getValue());
        pageParam.put("STATUS_VALID_04",DictCodeListExt1.有效状态_流程中.getValue());
        pageParam.put("BUSI_TYPE_POSITION_01","01");
        pageParam.put("BUSI_TYPE_POSITION_34","34");
        pageParam.put("FINPROD_TYPE_TG_F32",DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
        Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.parse(DateUtil.getSysDate()));
        int year = c.get(Calendar.YEAR);
        pageParam.put("year",year);



        pageParam.put("sort",dto.getPageMode().getSort());
        pageParam.put("direction",dto.getPageMode().getDirection());
        String userId = SecurityUtil.getLoginUser().getUserName();
        if (StringUtils.isNotEmpty(userId)) {
            //判断角色配置
            List<String> roleList=cfetsTrdQueryMapper.selectSysRoleById(userId);
            if(roleList.contains(DictCodeList.角色岗位_领导.getValue())
                    || roleList.contains(DictCodeList.角色岗位_投资处长.getValue())
                    || roleList.contains(DictCodeList.角色岗位_交易处长.getValue())
            ){
                //角色配置为领导、交易处长、投资处长可查看所有数据
            }else if(roleList.contains(DictCodeList.角色岗位_交易员.getValue())){
                //交易员
                pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
            }else if(roleList.contains(DictCodeList.角色岗位_投资经理.getValue())){
                //投资经理
                pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            }
        }

        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                () -> cfetsTrdQueryMapper.queryAllTradeInfoList(pageParam));
    }
    /**
     * 交易明细分页查询
     * @param dto 入数
     * @return Page<CfetsTrdQueryTradeInfoListResponse>
     */
    public Page<CfetsTrdQueryTradeInfoListResponse> queryAllTradeDetailInfoList(CfetsTrdQueryTradeInfoListRequest dto) {
        Map<String,Object> pageParam = BeanUtil.object2Map(dto);
        String effectiveStatus = (String)pageParam.get("effectiveStatus");
        if(StringUtil.isNotEmpty(effectiveStatus)){
            List<String> list=StringUtil.splitToList(GlobalConstant.COMMA,effectiveStatus);
            if(list.contains(DictCodeListExt1.有效状态_有效.getValue())){
                list.add("02");
            }
            pageParam.put("effectiveStatusList",list);
        }
        String regAssetType= (String)pageParam.get("regAssetType");
        if(StringUtil.isNotEmpty(regAssetType)){
            List<String> finprodType2List=new ArrayList<>();
            if(StringUtil.equals(regAssetType,"01")){
                finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            }else{
                finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
                finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            }
            pageParam.put("finprodType2List",finprodType2List);
        }
        String portfolioId = (String)pageParam.get("portfolioId");
        if(StringUtil.isNotEmpty(portfolioId)){
            pageParam.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA,portfolioId));
        }
        String finprodId = (String)pageParam.get("finprodId");
        if(StringUtil.isNotEmpty(finprodId)){
            pageParam.put("finprodIdList", StringUtil.splitToList(GlobalConstant.COMMA,finprodId));
        }
        if(!StringUtil.isNotEmpty(dto.getStartDate()) && !StringUtil.isNotEmpty(dto.getEndDate())){
            pageParam.put("startDate",iPlatformDate.getSysDate());
            pageParam.put("endDate",iPlatformDate.getSysDate());
        }

        //有效状态
//        pageParam.put("STATUS_VALID_01", DictCodeListExt1.有效状态_有效.getValue());
        pageParam.put("STATUS_VALID_03", DictCodeListExt1.有效状态_撤销.getValue());


        //平安业务类型
        pageParam.put("PINAN_BUSINESS_TYPE_09", DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_10", DictCodeListExt1.平安业务类型_债券类资产卖出.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_11", DictCodeListExt1.平安业务类型_债券类资产行权.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_20", DictCodeListExt1.平安业务类型_正回购.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_74", DictCodeListExt1.平安业务类型_买断式正回购.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_75", DictCodeListExt1.平安业务类型_买断式逆回购.getValue());

        //收付方向
        pageParam.put("feePs02", DictCodeListExt1.收付方向_支出.getValue());
        //结算状态
        pageParam.put("settlementStatus01", DictCodeList.结算状态_已结算.getValue());
        pageParam.put("settlementStatus02", DictCodeList.结算状态_已结算.getValue());
        //指令处理状态
        pageParam.put("commandDealStatus01", DictCodeList.指令处理状态_处理成功.getValue());
        pageParam.put("commandDealStatus02", DictCodeList.指令处理状态_处理失败.getValue());
        //指令处理状态
        pageParam.put("cleanStatus01", DictCodeListExt3.清算状态_清算成功.getValue());
        pageParam.put("cleanStatus02", DictCodeListExt3.清算状态_清算失败.getValue());
        pageParam.put("cleanStatus03", DictCodeListExt3.清算状态_清算中.getValue());

        //交收状态
        pageParam.put("DELIVERY_STATUS_01", DictCodeListExt3.交收状态_未交收.getValue());
        pageParam.put("DELIVERY_STATUS_02", DictCodeListExt3.交收状态_已交收.getValue());
        pageParam.put("DELIVERY_STATUS_NAME01", DictCodeListExt3.交收状态_未交收.getCodeName());

        pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("TRADE_STATUS_03", DictCodeListExt1.交易状态_撤销.getValue());

        pageParam.put("isCalcN", BussinessDictCodeList.是否_否.getValue());
        pageParam.put("PS_T07", DictCodeList.交易方向_到期.getValue());
        pageParam.put("PS_T08", DictCodeList.交易方向_还本.getValue());
        pageParam.put("PS_T09", DictCodeList.交易方向_收息.getValue());
        pageParam.put("PS_T39", DictCodeList.交易方向_行权_赎回.getValue());
        pageParam.put("PS_T03", DictCodeList.交易方向_行权_回售.getValue());
        pageParam.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        pageParam.put("PS_T02", DictCodeList.交易方向_卖出.getValue());

        pageParam.put("ISSUE_CLASS_02", DictCodeListExt4.新债_发行方式_招标发行.getValue());
        pageParam.put("ISSUE_CLASS_03",DictCodeListExt4.新债_发行方式_簿记建档.getValue());
        pageParam.put("ISSUE_CLASS_01",DictCodeListExt4.新债_发行方式_报价发行.getValue());
        pageParam.put("WIN_TYPE_01",DictCodeListExt4.投标方式_自主投标.getValue());
        pageParam.put("WIN_TYPE_02",DictCodeListExt4.投标方式_代投代缴.getValue());
        pageParam.put("WIN_TYPE_03",DictCodeListExt4.投标方式_分销买入.getValue());
        pageParam.put("TRADE_PLACE_01",DictCodeListExt1.交易场所_银行间.getValue());
        pageParam.put("TRADE_PLACE_02",DictCodeListExt1.交易场所_商业银行柜台.getValue());
        pageParam.put("STATUS_VALID_01",DictCodeListExt1.有效状态_有效.getValue());
        pageParam.put("STATUS_VALID_04",DictCodeListExt1.有效状态_流程中.getValue());
        pageParam.put("BUSI_TYPE_POSITION_01","01");
        pageParam.put("BUSI_TYPE_POSITION_34","34");
        pageParam.put("FINPROD_TYPE_TG_F32",DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
        Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.parse(DateUtil.getSysDate()));
        int year = c.get(Calendar.YEAR);
        pageParam.put("year",year);

        pageParam.put("sort",dto.getPageMode().getSort());
        pageParam.put("direction",dto.getPageMode().getDirection());
        String userId = SecurityUtil.getLoginUser().getUserName();
        if (StringUtils.isNotEmpty(userId)) {
            //判断角色配置
            List<String> roleList=cfetsTrdQueryMapper.selectSysRoleById(userId);
            if(roleList.contains(DictCodeList.角色岗位_领导.getValue())
                    || roleList.contains(DictCodeList.角色岗位_投资处长.getValue())
                    || roleList.contains(DictCodeList.角色岗位_交易处长.getValue())
            ){
                //角色配置为领导、交易处长、投资处长可查看所有数据
            }else if(roleList.contains(DictCodeList.角色岗位_交易员.getValue())){
                //交易员
                pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
            }else if(roleList.contains(DictCodeList.角色岗位_投资经理.getValue())){
                //投资经理
                pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            }
        }
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                () -> cfetsTrdQueryMapper.queryAllTradeDetailInfoList(pageParam));
    }

    /**
     * 只读表单设置及数据查询_交易汇总信息查询
     * @param pageCode
     * @param dtCode
     * @param spcdCode
     * @return
     */
    public Map<String, Object> findTradeInfo(String pageCode, String dtCode, String spcdCode, CfetsTrdQueryTradeInfoListResponse dto) {
        CfetsManualSettleCommonRequest entity=new CfetsManualSettleCommonRequest();
        List<CfetsManualSettleDto> list=new ArrayList<>();
        CfetsManualSettleDto item=new CfetsManualSettleDto();
        item.setTradeId(dto.getTotleTradeId());
        list.add(item);
        entity.setSelectedItem(list);
        Map<String, Object> manualSettleInfo = this.selectManualSettleInfo(pageCode, dtCode, spcdCode, dto);
        if (StringUtil.isNotEmpty(manualSettleInfo)) {
            manualSettleInfo = cfetsManualSettleQueryService.findManualSettleInfo(pageCode, dtCode, spcdCode, entity);
        }
        return manualSettleInfo;
    }

    /**
     * 只读表单设置及数据查询_成交信息查询
     * @param pageCode
     * @param dtCode
     * @param spcdCode
     * @return
     */
    public Map<String, Object> selectManualSettleInfo(String pageCode, String dtCode, String spcdCode,CfetsTrdQueryTradeInfoListResponse settleDto) {
        Map<String, Object> resultData = new HashMap<>();
        if(StringUtil.isBlank(spcdCode)){
            return resultData;
        }
        String userId = SecurityUtil.getLoginUser().getUserName();
        if(DictCodeListExt4.投标方式_分销买入.getValue().equals(settleDto.getBidModle())) {
             //基础信息
            List<CfgReadonlyFormShow> baseForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "03W");
            //金额信息03W,M
            List<CfgReadonlyFormShow> amtForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "03W,M");
            //备注03W,O
            List<CfgReadonlyFormShow> infoFormo = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "03W,O");
            //组合信息03W,M,P
            CfgDataTableShowFlag flag = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCode, "03W,M,P", userId);
            List<CfgDataTableShow> zhTable = null;
            if (null == flag) {
                zhTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, "03W,M,P", EFFERANGE_NO, USER_ID);
            } else {
                zhTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, "03W,M,P", EFFERANGE_IS, userId);
            }
            resultData.put("CfetsTrdQueryBidInfo_formb", baseForm);
            resultData.put("CfetsTrdQueryBidInfo_formm", amtForm);
            resultData.put("CfetsTrdQueryBidInfo_formo", infoFormo);
            resultData.put("CfetsTrdQueryBidInfo_tablep", zhTable);
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("totleTradeId", settleDto.getTotleTradeId());
            //判断角色配置
            List<String> roleList = cfetsTrdQueryMapper.selectSysRoleById(userId);
            if (roleList.contains(DictCodeList.角色岗位_领导.getValue()) || roleList.contains(DictCodeList.角色岗位_投资处长.getValue())|| roleList.contains(DictCodeList.角色岗位_交易处长.getValue())) {
                //角色配置为领导、交易处长、投资处长可查看所有数据
            } else if (roleList.contains(DictCodeList.角色岗位_交易员.getValue())) {
                //交易员
                pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
            } else if (roleList.contains(DictCodeList.角色岗位_投资经理.getValue())) {
                //投资经理
                pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            }
            pageParam.put("TENDER_TYPE_01", DictCodeListExt4.招标方式_单一价格招标.getValue());
            pageParam.put("TENDER_TYPE_02", DictCodeListExt4.招标方式_数量招标.getValue());
            pageParam.put("TENDER_TYPE_03", DictCodeListExt4.招标方式_荷兰式招标.getValue());
            pageParam.put("TENDER_TYPE_04", DictCodeListExt4.招标方式_混合式招标.getValue());
            pageParam.put("ISSUE_CLASS_02", DictCodeListExt4.新债_发行方式_招标发行.getValue());
            pageParam.put("ISSUE_CLASS_03", DictCodeListExt4.新债_发行方式_簿记建档.getValue());
            pageParam.put("spcdCode", spcdCode);
            pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
            pageParam.put("WIN_TYPE_03", DictCodeListExt4.投标方式_分销买入.getValue());
            //表单data
            CfetsTrdBidInfoResponse dataform = cfetsTrdQueryMapper.findFxmrManualSettleInfo(pageParam);
            //表格组合分配data
            pageParam.put("totleTradeId", settleDto.getTotleTradeId());
            List<BidPortIntFenPeiInfoDto> portIntFenPeiInfoDtoList = cfetsTrdQueryMapper.findFxmrPortIntFenPeiInfoList(pageParam);
            resultData.put("CfetsTrdQueryBidInfo_dataform", dataform);
            resultData.put("CfetsTrdQueryBidInfo_datatablep", portIntFenPeiInfoDtoList);
            return resultData;
        } else if(DictCodeListExt4.投标方式_自主投标.getValue().equals(settleDto.getBidModle())) {
            //基础信息
            List<CfgReadonlyFormShow> baseForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "01W");
            //金额信息05,M
            List<CfgReadonlyFormShow> amtForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "01W,M");
            //备注05,O
            List<CfgReadonlyFormShow> infoFormo = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "01W,O");
            //组合信息05,M,P
            CfgDataTableShowFlag flag = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCode, "01W,M,P", userId);
            List<CfgDataTableShow> zhTable = null;
            if (null == flag) {
                zhTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, "01W,M,P", EFFERANGE_NO, USER_ID);
            } else {
                zhTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, "01W,M,P", EFFERANGE_IS, userId);
            }
            resultData.put("CfetsTrdQueryBidInfo_formb", baseForm);
            resultData.put("CfetsTrdQueryBidInfo_formm", amtForm);
            resultData.put("CfetsTrdQueryBidInfo_formo", infoFormo);
            resultData.put("CfetsTrdQueryBidInfo_tablep", zhTable);
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("totleTradeId", settleDto.getTotleTradeId());
            //判断角色配置
            List<String> roleList = cfetsTrdQueryMapper.selectSysRoleById(userId);
            if (roleList.contains(DictCodeList.角色岗位_领导.getValue()) || roleList.contains(DictCodeList.角色岗位_投资处长.getValue())|| roleList.contains(DictCodeList.角色岗位_交易处长.getValue())) {
                //角色配置为领导、交易处长、投资处长可查看所有数据
            } else if (roleList.contains(DictCodeList.角色岗位_交易员.getValue())) {
                //交易员
                pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
            } else if (roleList.contains(DictCodeList.角色岗位_投资经理.getValue())) {
                //投资经理
                pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            }
            pageParam.put("TENDER_TYPE_01", DictCodeListExt4.招标方式_单一价格招标.getValue());
            pageParam.put("TENDER_TYPE_02", DictCodeListExt4.招标方式_数量招标.getValue());
            pageParam.put("TENDER_TYPE_03", DictCodeListExt4.招标方式_荷兰式招标.getValue());
            pageParam.put("TENDER_TYPE_04", DictCodeListExt4.招标方式_混合式招标.getValue());
            pageParam.put("ISSUE_CLASS_02", DictCodeListExt4.新债_发行方式_招标发行.getValue());
            pageParam.put("ISSUE_CLASS_03", DictCodeListExt4.新债_发行方式_簿记建档.getValue());
            pageParam.put("spcdCode", spcdCode);
            pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
            pageParam.put("STATUS_VALID_01", DictCodeListExt1.有效状态_有效.getValue());
            pageParam.put("WIN_TYPE_01", DictCodeListExt4.投标方式_自主投标.getValue());
            pageParam.put("WIN_TYPE_02", DictCodeListExt4.投标方式_代投代缴.getValue());
            pageParam.put("WIN_TYPE_03", DictCodeListExt4.投标方式_分销买入.getValue());
            //表单data
            CfetsTrdBidInfoResponse dataform = cfetsTrdQueryMapper.findZztbManualSettleInfo(pageParam);
            //表格组合分配data
            pageParam.put("totleTradeId", settleDto.getTotleTradeId());
            List<BidPortIntFenPeiInfoDto> portIntFenPeiInfoDtoList = cfetsTrdQueryMapper.findZztbPortIntFenPeiInfoList(pageParam);
            resultData.put("CfetsTrdQueryBidInfo_dataform", dataform);
            resultData.put("CfetsTrdQueryBidInfo_datatablep", portIntFenPeiInfoDtoList);
            return resultData;
        } else if (DictCodeListExt3.头寸业务类型_债券一级申购.getValue().equals(settleDto.getTgBusiType())
        && !StringUtil.isNotEmpty(settleDto.getBidModle())) {//一级申购
            //基础信息
            List<CfgReadonlyFormShow> baseForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "34W");
            //备注05,O
            List<CfgReadonlyFormShow> infoFormo = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "34W,O");
            //组合信息05,M,P
            CfgDataTableShowFlag flag = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCode, "34W,M,P", userId);
            List<CfgDataTableShow> zhTable = null;
            if (null == flag) {
                zhTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, "34W,M,P", EFFERANGE_NO, USER_ID);
            } else {
                zhTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, "34W,M,P", EFFERANGE_IS, userId);
            }
            resultData.put("CfetsTrdQueryBidInfo_formb", baseForm);
            resultData.put("CfetsTrdQueryBidInfo_formo", infoFormo);
            resultData.put("CfetsTrdQueryBidInfo_tablep", zhTable);
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("totleTradeId", settleDto.getTotleTradeId());
            //判断角色配置
            List<String> roleList = cfetsTrdQueryMapper.selectSysRoleById(userId);
            if (roleList.contains(DictCodeList.角色岗位_领导.getValue()) || roleList.contains(DictCodeList.角色岗位_投资处长.getValue())|| roleList.contains(DictCodeList.角色岗位_交易处长.getValue())) {
                //角色配置为领导、交易处长、投资处长可查看所有数据
            } else if (roleList.contains(DictCodeList.角色岗位_交易员.getValue())) {
                //交易员
                pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
            } else if (roleList.contains(DictCodeList.角色岗位_投资经理.getValue())) {
                //投资经理
                pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            }
            pageParam.put("TENDER_TYPE_01", DictCodeListExt4.招标方式_单一价格招标.getValue());
            pageParam.put("TENDER_TYPE_02", DictCodeListExt4.招标方式_数量招标.getValue());
            pageParam.put("TENDER_TYPE_03", DictCodeListExt4.招标方式_荷兰式招标.getValue());
            pageParam.put("TENDER_TYPE_04", DictCodeListExt4.招标方式_混合式招标.getValue());
            pageParam.put("ISSUE_CLASS_02", DictCodeListExt4.新债_发行方式_招标发行.getValue());
            pageParam.put("ISSUE_CLASS_03", DictCodeListExt4.新债_发行方式_簿记建档.getValue());
            pageParam.put("spcdCode", spcdCode);
            pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
            pageParam.put("WIN_TYPE_03", DictCodeListExt4.投标方式_分销买入.getValue());
            //表单data
            CfetsTrdBidInfoResponse dataform = cfetsTrdQueryMapper.findYjsgManualSettleInfo(pageParam);
            //表格组合分配data
            pageParam.put("totleTradeId", settleDto.getTotleTradeId());
            List<BidPortIntFenPeiInfoDto> portIntFenPeiInfoDtoList = cfetsTrdQueryMapper.findYjsgPortIntFenPeiInfoList(pageParam);
            resultData.put("CfetsTrdQueryBidInfo_dataform", dataform);
            resultData.put("CfetsTrdQueryBidInfo_datatablep", portIntFenPeiInfoDtoList);
            return resultData;
        }
        return resultData;
    }


    /**
     * 只读表单设置及数据查询_交易明细信息查询
     * @param pageCode
     * @param dtCode
     * @param spcdCode
     * @return
     */
    public Map<String, Object> findTradeDetailInfo(String pageCode, String dtCode, String spcdCode, CfetsTrdQueryTradeInfoListResponse dto) {
        Map<String, Object> resultData = new HashMap<>();
        if(StringUtil.isBlank(spcdCode)){
            return resultData;
        }
        if("05".equals(spcdCode)) {//质押式回购
            String spcdCodeTemp =spcdCode;
            //基础信息表单
            List<CfgReadonlyFormShow> baseForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            //金额信息表单
            spcdCode=spcdCodeTemp+",M";
            List<CfgReadonlyFormShow> amtForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            //金额信息05,P
            spcdCode=spcdCodeTemp+",P";
            List<CfgReadonlyFormShow> fForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            //备注05,O
            spcdCode=spcdCodeTemp+",O";
            List<CfgReadonlyFormShow> infoFormo = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            //组合信息05,M,P
            spcdCode=spcdCodeTemp+",M,P";
            String userId = SecurityUtil.getLoginUser().getUserName();
            CfgDataTableShowFlag flag = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCode, spcdCode, userId);
            List<CfgDataTableShow> zhTable = null;
            if (null == flag) {
                zhTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode, EFFERANGE_NO, USER_ID);
            } else {
                zhTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode, EFFERANGE_IS, userId);
            }
            //质押券信息05
            flag = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCode, spcdCode, userId);
            List<CfgDataTableShow> pledgeTable = null;
            if (null == flag) {
                pledgeTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode,"05", EFFERANGE_NO, USER_ID);
            } else {
                pledgeTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, "05", EFFERANGE_IS, userId);
            }
            resultData.put(dtCode+"_formb", baseForm);
            resultData.put(dtCode+"_formm", amtForm);
            resultData.put(dtCode+"_formp", fForm);
            resultData.put(dtCode+"_formo", infoFormo);
            resultData.put(dtCode+"_tablep", zhTable);
            resultData.put(dtCode+"_tablepledge", pledgeTable);
            Map<String, Object> pageParam = BeanUtil.object2Map(dto);
            //判断角色配置
            List<String> roleList = cfetsTrdQueryMapper.selectSysRoleById(userId);
            if(roleList.contains(DictCodeList.角色岗位_领导.getValue())
                    || roleList.contains(DictCodeList.角色岗位_投资处长.getValue())
                    || roleList.contains(DictCodeList.角色岗位_交易处长.getValue())
            ){
                //角色配置为领导、交易处长、投资处长可查看所有数据
            }else if(roleList.contains(DictCodeList.角色岗位_交易员.getValue())){
                //交易员
                pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
            }else if(roleList.contains(DictCodeList.角色岗位_投资经理.getValue())){
                //投资经理
                pageParam.put("userName", SecurityUtil.getLoginUser().getUserName());
            }
            pageParam.put("effectiveStatus",DictCodeListExt1.有效状态_有效.getValue());
            pageParam.put("STATUS_VALID_01", DictCodeListExt1.有效状态_有效.getValue());
            pageParam.put("STATUS_VALID_03", DictCodeListExt1.有效状态_撤销.getValue());
            pageParam.put("STATUS_VALID_04", DictCodeListExt1.有效状态_流程中.getValue());
            pageParam.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
            pageParam.put("feeType06", BussinessDictCodeList.费用类型_交易手续费.getValue());
            pageParam.put("feeType08", BussinessDictCodeList.费用类型_结算手续费.getValue());
            //表单data
            CfetsTrdSettleInfoResponse dataform= cfetsTrdQueryMapper.findRepoManualSettleInfoOther(pageParam);

            pageParam.put("CASH_TYPE_1_02", DictCodeListExt1.现金流一级类型_利息.getValue());
            pageParam.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
            //表格组合分配data
            List<PortIntFenPeiInfoDto> portIntFenPeiInfoDtoList = cfetsTrdQueryMapper.findRepoPortIntFenPeiInfoList(pageParam);
            //表格质押券data
            pageParam.put("TRADEPLACE01", DictCodeListExt1.交易场所_银行间.getValue());
            pageParam.put("TRADEPLACE03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
            pageParam.put("TRADEPLACE04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
            pageParam.put("platDate", iPlatformDate.getSysDateStr());
            pageParam.put("S_002", DictCodeListExt3.资产类型_可质押持仓.getValue());
            pageParam.put("sysDate",iPlatformDate.getSysDateStr());
            List<CfetsPledgeInfoResponse> pledgeInfoDtoList =  cfetsTrdQueryMapper.findRepoPledgeInfoList(pageParam);
            resultData.put(dtCode+"_dataform",dataform);
            resultData.put(dtCode+"_datatablep",portIntFenPeiInfoDtoList);
            resultData.put(dtCode+"_datatablepledge",pledgeInfoDtoList);
            return resultData;
        }else{
            String spcdCodeTemp = spcdCode;
            //基本信息
            List<CfgReadonlyFormShow> infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            //金额信息
            spcdCode = spcdCode + ",M";
            List<CfgReadonlyFormShow> infoForm1 = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            //组合
            spcdCode = spcdCode + ",P";

            //List<CfgReadonlyFormShow> infoFormp = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode,dtCode,spcdCode);
            List<CfgDataTableShow> infoForm2;
            //获取登录用户的ID
            String userId = SecurityUtil.getLoginUser().getUserName();
            CfgDataTableShowFlag flag = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCode, spcdCode, userId);
            if (null == flag) {
                infoForm2 = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode, EFFERANGE_NO, USER_ID);
            } else {
                infoForm2 = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode, EFFERANGE_IS, userId);
            }
            resultData.put(dtCode + "_form", infoForm);
            resultData.put(dtCode + "_form1", infoForm1);
            resultData.put(dtCode + "_form2", infoForm2);
            // resultData.put(dtCode+"_formp",infoFormp);


            Map<String, Object> pageParam = BeanUtil.object2Map(dto);
            pageParam.put("STATUS_VALID_01", DictCodeListExt1.有效状态_有效.getValue());
            pageParam.put("FINPROD_TYPE_TGF01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            pageParam.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
            pageParam.put("YESNO_N", DictCodeList.是否_否.getValue());
            pageParam.put("spcdCode", spcdCode);
            pageParam.put("feeType06", BussinessDictCodeList.费用类型_交易手续费.getValue());
            pageParam.put("feeType08", BussinessDictCodeList.费用类型_结算手续费.getValue());

            //现券买卖 或回售
            CfetsManualSettleInfoOtherListRespond cfetsManualSettleInfoOtherListRespond = cfetsTrdQueryMapper.findManualSettleInfoOther(pageParam);
            resultData.put(dtCode + "_data", cfetsManualSettleInfoOtherListRespond);
            resultData.put(dtCode + "_data1", cfetsManualSettleInfoOtherListRespond);
            // resultData.put(dtCode+"_datap",cfetsManualSettleInfoOtherListRespond);

            //组合分配信息
            if (StringUtils.isNotEmpty(userId)) {
                //判断角色配置
                List<String> roleList = cfetsTrdQueryMapper.selectSysRoleById(userId);
                if (roleList.contains(DictCodeList.角色岗位_投资经理.getValue())) {
                    //投资经理
                    pageParam.put("userName", SecurityUtil.getLoginUser().getUserName());
                }
            }
            pageParam.put("CASH_TYPE_1_02", DictCodeListExt1.现金流一级类型_利息.getValue());
            pageParam.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
            List<PortIntFenPeiInfoDto> portIntFenPeiInfoDtoList = cfetsTrdQueryMapper.findPortIntFenPeiInfoList(pageParam);

            //resultData.put(dtCode+"_datap",portIntFenPeiInfoDtoList);
            resultData.put(dtCode + "_data2", portIntFenPeiInfoDtoList);

            return resultData;
        }
    }

    /**
     * 只读表单设置及数据查询_成交信息查询
     * @param pageCode
     * @param dtCode
     * @param spcdCode
     * @return
     */
    public Map<String, Object> findBidTradeDetailInfo(String pageCode, String dtCode, String spcdCode,CfetsTrdQueryTradeInfoListResponse settleDto) {
        Map<String, Object> resultData = new HashMap<>();
        if(StringUtil.isBlank(spcdCode)){
            return resultData;
        }
        String userId = SecurityUtil.getLoginUser().getUserName();
        if(DictCodeListExt4.投标方式_分销买入.getValue().equals(settleDto.getBidModle())) {
            //基础信息
            List<CfgReadonlyFormShow> baseForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "03W");
            //金额信息03W,M
            List<CfgReadonlyFormShow> amtForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "03W,M");
            //备注03W,O
            List<CfgReadonlyFormShow> infoFormo = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "03W,O");
            //组合信息03W,M,P
            CfgDataTableShowFlag flag = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCode, "03W,M,P", userId);
            List<CfgDataTableShow> zhTable = null;
            if (null == flag) {
                zhTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, "03W,M,P", EFFERANGE_NO, USER_ID);
            } else {
                zhTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, "03W,M,P", EFFERANGE_IS, userId);
            }
            resultData.put("CfetsTrdQueryBidInfo_formb", baseForm);
            resultData.put("CfetsTrdQueryBidInfo_formm", amtForm);
            resultData.put("CfetsTrdQueryBidInfo_formo", infoFormo);
            resultData.put("CfetsTrdQueryBidInfo_tablep", zhTable);
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("tradeId", settleDto.getBusiCode());
            //判断角色配置
            List<String> roleList = cfetsTrdQueryMapper.selectSysRoleById(userId);
            if (roleList.contains(DictCodeList.角色岗位_领导.getValue()) || roleList.contains(DictCodeList.角色岗位_投资处长.getValue())|| roleList.contains(DictCodeList.角色岗位_交易处长.getValue())) {
                //角色配置为领导、交易处长、投资处长可查看所有数据
            } else if (roleList.contains(DictCodeList.角色岗位_交易员.getValue())) {
                //交易员
                pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
            } else if (roleList.contains(DictCodeList.角色岗位_投资经理.getValue())) {
                //投资经理
                pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            }
            pageParam.put("TENDER_TYPE_01", DictCodeListExt4.招标方式_单一价格招标.getValue());
            pageParam.put("TENDER_TYPE_02", DictCodeListExt4.招标方式_数量招标.getValue());
            pageParam.put("TENDER_TYPE_03", DictCodeListExt4.招标方式_荷兰式招标.getValue());
            pageParam.put("TENDER_TYPE_04", DictCodeListExt4.招标方式_混合式招标.getValue());
            pageParam.put("ISSUE_CLASS_02", DictCodeListExt4.新债_发行方式_招标发行.getValue());
            pageParam.put("ISSUE_CLASS_03", DictCodeListExt4.新债_发行方式_簿记建档.getValue());
            pageParam.put("spcdCode", spcdCode);
            pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
            pageParam.put("WIN_TYPE_03", DictCodeListExt4.投标方式_分销买入.getValue());
            //表单data
            CfetsTrdBidInfoResponse dataform = cfetsTrdQueryMapper.findFxmrManualSettleInfo(pageParam);
            //表格组合分配data
            pageParam.put("totleTradeId", settleDto.getTotleTradeId());
            List<BidPortIntFenPeiInfoDto> portIntFenPeiInfoDtoList = cfetsTrdQueryMapper.findFxmrPortIntFenPeiInfoList(pageParam);
            resultData.put("CfetsTrdQueryBidInfo_dataform", dataform);
            resultData.put("CfetsTrdQueryBidInfo_datatablep", portIntFenPeiInfoDtoList);
            return resultData;
        } else if(DictCodeListExt4.投标方式_自主投标.getValue().equals(settleDto.getBidModle())) {
            //基础信息
            List<CfgReadonlyFormShow> baseForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "01W");
            //金额信息05,M
            List<CfgReadonlyFormShow> amtForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "01W,M");
            //备注05,O
            List<CfgReadonlyFormShow> infoFormo = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "01W,O");
            //组合信息05,M,P
            CfgDataTableShowFlag flag = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCode, "01W,M,P", userId);
            List<CfgDataTableShow> zhTable = null;
            if (null == flag) {
                zhTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, "01W,M,P", EFFERANGE_NO, USER_ID);
            } else {
                zhTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, "01W,M,P", EFFERANGE_IS, userId);
            }
            resultData.put("CfetsTrdQueryBidInfo_formb", baseForm);
            resultData.put("CfetsTrdQueryBidInfo_formm", amtForm);
            resultData.put("CfetsTrdQueryBidInfo_formo", infoFormo);
            resultData.put("CfetsTrdQueryBidInfo_tablep", zhTable);
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("tradeId", settleDto.getBusiCode());
            //判断角色配置
            List<String> roleList = cfetsTrdQueryMapper.selectSysRoleById(userId);
            if (roleList.contains(DictCodeList.角色岗位_领导.getValue()) || roleList.contains(DictCodeList.角色岗位_投资处长.getValue())|| roleList.contains(DictCodeList.角色岗位_交易处长.getValue())) {
                //角色配置为领导、交易处长、投资处长可查看所有数据
            } else if (roleList.contains(DictCodeList.角色岗位_交易员.getValue())) {
                //交易员
                pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
            } else if (roleList.contains(DictCodeList.角色岗位_投资经理.getValue())) {
                //投资经理
                pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            }
            pageParam.put("TENDER_TYPE_01", DictCodeListExt4.招标方式_单一价格招标.getValue());
            pageParam.put("TENDER_TYPE_02", DictCodeListExt4.招标方式_数量招标.getValue());
            pageParam.put("TENDER_TYPE_03", DictCodeListExt4.招标方式_荷兰式招标.getValue());
            pageParam.put("TENDER_TYPE_04", DictCodeListExt4.招标方式_混合式招标.getValue());
            pageParam.put("ISSUE_CLASS_02", DictCodeListExt4.新债_发行方式_招标发行.getValue());
            pageParam.put("ISSUE_CLASS_03", DictCodeListExt4.新债_发行方式_簿记建档.getValue());
            pageParam.put("spcdCode", spcdCode);
            pageParam.put("STATUS_VALID_01", DictCodeListExt1.有效状态_有效.getValue());
            pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
            pageParam.put("WIN_TYPE_01", DictCodeListExt4.投标方式_自主投标.getValue());
            pageParam.put("WIN_TYPE_02", DictCodeListExt4.投标方式_代投代缴.getValue());
            pageParam.put("WIN_TYPE_03", DictCodeListExt4.投标方式_分销买入.getValue());
            //表单data
            CfetsTrdBidInfoResponse dataform = cfetsTrdQueryMapper.findZztbManualSettleInfo(pageParam);
            //表格组合分配data
            pageParam.put("totleTradeId", settleDto.getTotleTradeId());
            List<BidPortIntFenPeiInfoDto> portIntFenPeiInfoDtoList = cfetsTrdQueryMapper.findZztbPortIntFenPeiInfoList(pageParam);
            resultData.put("CfetsTrdQueryBidInfo_dataform", dataform);
            resultData.put("CfetsTrdQueryBidInfo_datatablep", portIntFenPeiInfoDtoList);
            return resultData;
        } else if (DictCodeListExt3.头寸业务类型_债券一级申购.getValue().equals(settleDto.getTgBusiType())
                && !StringUtil.isNotEmpty(settleDto.getBidModle())) {//一级申购
            //基础信息
            List<CfgReadonlyFormShow> baseForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "34W");
            //备注05,O
            List<CfgReadonlyFormShow> infoFormo = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "34W,O");
            //组合信息05,M,P
            CfgDataTableShowFlag flag = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCode, "34W,M,P", userId);
            List<CfgDataTableShow> zhTable = null;
            if (null == flag) {
                zhTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, "34W,M,P", EFFERANGE_NO, USER_ID);
            } else {
                zhTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, "34W,M,P", EFFERANGE_IS, userId);
            }
            resultData.put("CfetsTrdQueryBidInfo_formb", baseForm);
            resultData.put("CfetsTrdQueryBidInfo_formo", infoFormo);
            resultData.put("CfetsTrdQueryBidInfo_tablep", zhTable);
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("tradeId", settleDto.getBusiCode());
            //判断角色配置
            List<String> roleList = cfetsTrdQueryMapper.selectSysRoleById(userId);
            if (roleList.contains(DictCodeList.角色岗位_领导.getValue()) || roleList.contains(DictCodeList.角色岗位_投资处长.getValue())|| roleList.contains(DictCodeList.角色岗位_交易处长.getValue())) {
                //角色配置为领导、交易处长、投资处长可查看所有数据
            } else if (roleList.contains(DictCodeList.角色岗位_交易员.getValue())) {
                //交易员
                pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
            } else if (roleList.contains(DictCodeList.角色岗位_投资经理.getValue())) {
                //投资经理
                pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            }
            pageParam.put("TENDER_TYPE_01", DictCodeListExt4.招标方式_单一价格招标.getValue());
            pageParam.put("TENDER_TYPE_02", DictCodeListExt4.招标方式_数量招标.getValue());
            pageParam.put("TENDER_TYPE_03", DictCodeListExt4.招标方式_荷兰式招标.getValue());
            pageParam.put("TENDER_TYPE_04", DictCodeListExt4.招标方式_混合式招标.getValue());
            pageParam.put("ISSUE_CLASS_02", DictCodeListExt4.新债_发行方式_招标发行.getValue());
            pageParam.put("ISSUE_CLASS_03", DictCodeListExt4.新债_发行方式_簿记建档.getValue());
            pageParam.put("spcdCode", spcdCode);
            pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
            pageParam.put("WIN_TYPE_03", DictCodeListExt4.投标方式_分销买入.getValue());
            //表单data
            CfetsTrdBidInfoResponse dataform = cfetsTrdQueryMapper.findYjsgManualSettleInfo(pageParam);
            //表格组合分配data
            pageParam.put("totleTradeId", settleDto.getTotleTradeId());
            List<BidPortIntFenPeiInfoDto> portIntFenPeiInfoDtoList = cfetsTrdQueryMapper.findYjsgPortIntFenPeiInfoList(pageParam);
            resultData.put("CfetsTrdQueryBidInfo_dataform", dataform);
            resultData.put("CfetsTrdQueryBidInfo_datatablep", portIntFenPeiInfoDtoList);
            return resultData;
        }
        return resultData;
    }
}
