package com.joyintech.tams.cfets.manualsettle.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
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
import com.joyintech.tams.cfets.trdquery.dao.CfetsTrdQueryMapper;
import com.joyintech.tams.cfets.trdquery.dto.*;
import com.joyintech.tams.common.base.entity.User;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 手工交收
 * @author sjj
 * @version 1.0 2021年11月12日
 * @see CfetsManualSettleQueryService
 * @since 1.0
 */
@Service("CfetsManualSettleQueryService")
public class CfetsManualSettleQueryService extends BaseService{

    private Logger logger = LoggerFactory.getLogger(CfetsManualSettleQueryService.class);

    @Autowired
    private CfetsManualSettleMapper cfetsManualSettleMapper;

    @Autowired
    private CfgReadonlyFormShowMapper cfgReadonlyFormShowMapper;

    @Autowired
    private CfgDataTableFlagMapper cfgDataTableFlagMapper;

    @Autowired
    private CfgDataTableMapper cfgDataTableMapper;

    @Autowired
    private CfetsTrdQueryMapper cfetsTrdQueryMapper;

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
     * 手工交收列表分页查询
     * @param dto 入数
     * @return Page<CfetsManualSettleTraderListRespond>
     */
    public Page<CfetsManualSettleTradeListRespond> findPage(CfetsManualSettleTradeListRequest dto) {
        Map<String,Object> pageParam = BeanUtil.object2Map(dto);
        List<String> listTemp = Arrays.asList(dto.getSettleStatus());
        if(!listTemp.contains("all")){
            pageParam.put("settleStatusList", listTemp);
            if(listTemp.contains(DictCodeListExt3.交收状态_未交收.getValue())){
                pageParam.put("isContainDelivery01", true);
            }
        }else{
            pageParam.put("isContainDelivery01", true);
        }
        if(dto.getSettleDate() == null){
            pageParam.put("settleDateStart", DateUtil.format(DateUtil.platDateFromCache()));
            pageParam.put("settleDateEnd", DateUtil.format(DateUtil.platDateFromCache()));
        }else{
            pageParam.put("settleDateStart", DateUtil.parse(dto.getSettleDate()[0]));
            pageParam.put("settleDateEnd", DateUtil.parse(dto.getSettleDate()[1]));
        }
        if(dto.getTradeDate() != null){
            pageParam.put("tradeDateStart", DateUtil.parse(dto.getTradeDate()[0]));
            pageParam.put("tradeDateEnd", DateUtil.parse(dto.getTradeDate()[1]));
        }
        if(StringUtil.isNotEmpty(pageParam.get("portfolioId"))){
            List<String> portfolioIdList = StringUtil.splitToList(",",(String)pageParam.get("portfolioId"));
            pageParam.put("portfolioIdList", portfolioIdList);
        }
        if(StringUtil.isNotEmpty(pageParam.get("finprodMarketId"))){
            List<String> finprodMarketIdList = StringUtil.splitToList(",",(String)pageParam.get("finprodMarketId"));
            pageParam.put("finprodMarketIdList", finprodMarketIdList);
        }
        //平安业务类型
        pageParam.put("PINAN_BUSINESS_TYPE_09", DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_10", DictCodeListExt1.平安业务类型_债券类资产卖出.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_11", DictCodeListExt1.平安业务类型_债券类资产行权.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_35", DictCodeListExt1.平安业务类型_债券中间收息.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_36", DictCodeListExt1.平安业务类型_债券中间还本.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_37", DictCodeListExt1.平安业务类型_债券到期.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_20", DictCodeListExt1.平安业务类型_正回购.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_51", DictCodeListExt1.平安业务类型_逆回购到期.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_52", DictCodeListExt1.平安业务类型_正回购到期.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_74", DictCodeListExt1.平安业务类型_买断式正回购.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_75", DictCodeListExt1.平安业务类型_买断式逆回购.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_76", DictCodeListExt1.平安业务类型_买断式正回购到期.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_77", DictCodeListExt1.平安业务类型_买断式逆回购到期.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_105", DictCodeListExt2.平安业务类型_债券借贷融入.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_106", DictCodeListExt2.平安业务类型_债券借贷融出.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_107", DictCodeListExt2.平安业务类型_债券借贷融入到期.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_108", DictCodeListExt2.平安业务类型_债券借贷融出到期.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_180", DictCodeListExt4.平安业务类型_一级申购.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_151", DictCodeListExt4.平安业务类型_中标结果维护.getValue());
        pageParam.put("tgBusiTypes", Arrays.asList( DictCodeListExt1.平安业务类型_债券类资产买入.getValue(), DictCodeListExt1.平安业务类型_债券类资产卖出.getValue()
                , DictCodeListExt1.平安业务类型_债券类资产行权.getValue(), DictCodeListExt1.平安业务类型_债券中间收息.getValue(), DictCodeListExt1.平安业务类型_债券中间还本.getValue()
                , DictCodeListExt1.平安业务类型_债券到期.getValue(), DictCodeListExt1.平安业务类型_正回购.getValue(), DictCodeListExt1.平安业务类型_逆回购到期.getValue()
                , DictCodeListExt1.平安业务类型_正回购到期.getValue(), DictCodeListExt1.平安业务类型_买断式正回购.getValue(), DictCodeListExt1.平安业务类型_买断式逆回购.getValue()
                , DictCodeListExt1.平安业务类型_买断式正回购到期.getValue(), DictCodeListExt1.平安业务类型_买断式逆回购到期.getValue(), DictCodeListExt2.平安业务类型_债券借贷融入.getValue()
                , DictCodeListExt2.平安业务类型_债券借贷融出.getValue(), DictCodeListExt2.平安业务类型_债券借贷融入到期.getValue(), DictCodeListExt2.平安业务类型_债券借贷融出到期.getValue()
                , DictCodeListExt4.平安业务类型_一级申购.getValue(),DictCodeListExt4.平安业务类型_中标结果维护.getValue(),DictCodeListExt4.平安业务类型_债券分销买入确认.getValue()));
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
        pageParam.put("DELIVERY_STATUS_04", DictCodeListExt3.交收状态_交收失败.getValue());
        pageParam.put("DELIVERY_STATUS_NAME01", DictCodeListExt3.交收状态_未交收.getCodeName());
        //交易状态
        pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("TRADE_STATUS_03", DictCodeListExt1.交易状态_撤销.getValue());

        pageParam.put("isCalcN", BussinessDictCodeList.是否_否.getValue());
        //交易状态
        pageParam.put("PS_T07", DictCodeList.交易方向_到期.getValue());
        pageParam.put("PS_T08", DictCodeList.交易方向_还本.getValue());
        pageParam.put("PS_T09", DictCodeList.交易方向_收息.getValue());
        pageParam.put("PS_T39", DictCodeList.交易方向_行权_赎回.getValue());
        pageParam.put("PS_T03", DictCodeList.交易方向_行权_回售.getValue());
        pageParam.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        pageParam.put("PS_T02", DictCodeList.交易方向_卖出.getValue());

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int year = c.get(Calendar.YEAR);
        pageParam.put("year",year);
        pageParam.put("sort",dto.getPageMode().getSort());
        pageParam.put("direction",dto.getPageMode().getDirection());
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),() -> cfetsManualSettleMapper.findPage(pageParam));
    }

    /**
     * 交收流水列表分页查询
     *
     * @param dto
     * @return Page<CfetsManualSettleFlowRespond>
     */
    public Page<CfetsManualSettleFlowRespond> findManualSettleFlow(CfetsManualSettleCommonRequest dto) {
        if(dto.getSelectedItem().size() == 0){
            return new Page<CfetsManualSettleFlowRespond>();
        }
        CfetsManualSettleDto cfetsManualSettleDto = dto.getSelectedItem().get(0);
        Map<String,Object> pageParam = BeanUtil.object2Map(cfetsManualSettleDto);
        List<CfetsManualSettleFlowRespond> cfetsManualSettleFlowRespondList = new ArrayList<CfetsManualSettleFlowRespond>();
        Page<CfetsManualSettleFlowRespond> pageLimit = new Page<CfetsManualSettleFlowRespond>();
        if(StringUtil.isBlank((String)pageParam.get("settleStatus")) ||(StringUtil.equals(DictCodeListExt3.交收状态_未交收.getValue(),(String)pageParam.get("settleStatus"))
           && StringUtil.isBlank((String)pageParam.get("settleSeqNo")))) { //组装流水信息
            CfetsManualSettleFlowRespond cfetsManualSettleFlowRespond = new CfetsManualSettleFlowRespond();
            cfetsManualSettleFlowRespond.setSeqNo(1);
            cfetsManualSettleFlowRespond.setSettleDate(cfetsManualSettleDto.getSettleDate());
            cfetsManualSettleFlowRespond.setJiaoShouDate(DateUtil.format(new Date(),"HH:mm:ss"));
            cfetsManualSettleFlowRespond.setFinprodMarketId(cfetsManualSettleDto.getFinprodMarketId());
            cfetsManualSettleFlowRespond.setFinprodAbbr(cfetsManualSettleDto.getFinprodAbbr());
            cfetsManualSettleFlowRespond.setSettleNum(cfetsManualSettleDto.getSettleNum());
            if(StringUtil.equals(cfetsManualSettleDto.getTgBusiType(),DictCodeListExt3.头寸业务类型_现券交易.getValue()) && StringUtil.equals(cfetsManualSettleDto.getPs(),DictCodeList.交易方向_买入.getValue())) {
            }else{
                cfetsManualSettleFlowRespond.setSettleAmt(cfetsManualSettleDto.getSettleAmt());
            }
            cfetsManualSettleFlowRespond.setCreateUser(LoginUserUtil.getUserName());
            cfetsManualSettleFlowRespondList.add(cfetsManualSettleFlowRespond);
            pageLimit.setRoot(cfetsManualSettleFlowRespondList);
        }else{
            pageLimit =  super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),() -> cfetsManualSettleMapper.findManualSettleFlow(pageParam));
        }
        //将交易员和投资经理转为真实姓名
        List<CfetsManualSettleFlowRespond> list = pageLimit.getRoot();
        if(!list.isEmpty()){
            for(CfetsManualSettleFlowRespond cfetsManualSettleFlowRespond : list) {
                if (StringUtil.isNotEmpty(cfetsManualSettleFlowRespond.getCreateUser())) {
                    User user = cfetsManualSettleMapper.getRealNameByUserName(cfetsManualSettleFlowRespond.getCreateUser());
                    if(StringUtil.isNotEmpty(user)){
                        cfetsManualSettleFlowRespond.setCreateUser(user.getRealName());
                    }
                }
            }
        }

        return pageLimit;
    }

    /**
     * 只读表单设置及数据查询_成交信息查询
     * @param pageCode
     * @param dtCode
     * @param spcdCode
     * @return
     */
    public Map<String, Object> findManualSettleInfo(String pageCode, String dtCode, String spcdCode,CfetsManualSettleCommonRequest dto) {
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtil.isBlank(spcdCode)) {
            return resultData;
        }
        String userId = SecurityUtil.getLoginUser().getUserName();
        CfetsManualSettleDto settleDto = dto.getSelectedItem().get(0);
        if (DictCodeListExt3.头寸业务类型_质押式回购首期.getValue().equals(settleDto.getTgBusiType())
                || DictCodeListExt3.头寸业务类型_质押式回购到期.getValue().equals(settleDto.getTgBusiType())) {//质押式回购
            // String totalTradeId = settleDto.getFinprodId()t();
            resultData = this.findRepoManualSettleInfo(pageCode, dtCode, spcdCode, userId, settleDto);
        } else if (DictCodeListExt3.头寸业务类型_债券一级申购.getValue().equals(settleDto.getTgBusiType())) {//一级申购
            resultData = this.findSubManualSettleInfo(pageCode, dtCode, spcdCode, userId, settleDto);
        } else if (DictCodeListExt3.头寸业务类型_债券一级投标.getValue().equals(settleDto.getTgBusiType())) {//一级投标
            resultData = this.findBidManualSettleInfo(pageCode, dtCode, spcdCode, userId, settleDto);
        } else {//现券买卖
            resultData = this.findSecManualSettleInfo(pageCode, dtCode, spcdCode, userId, settleDto);
        }
        return resultData;
    }

    /**
     * 查询一级投标成交信息
     *
     * @param pageCode 页面编码
     * @param dtCode   区域编码
     * @param spcdCode 条件编码
     * @param userId   用户id
     * @param dto      数据
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author jiangpeng
     * @date 2022/5/31 11:05
     */
    private Map<String, Object> findBidManualSettleInfo(String pageCode, String dtCode, String spcdCode, String userId, CfetsManualSettleDto settleDto) {
        // 返回结果
        Map<String, Object> resultData = new HashMap<>();
        // 需要区分分销买入和中标结果维护的数据,所以需要先查询出数据
        Map<String, Object> pageParam = BeanUtil.object2Map(settleDto);
        pageParam.put("TENDER_TYPE_01", DictCodeListExt4.招标方式_单一价格招标.getValue());
        pageParam.put("TENDER_TYPE_02", DictCodeListExt4.招标方式_数量招标.getValue());
        pageParam.put("TENDER_TYPE_03", DictCodeListExt4.招标方式_荷兰式招标.getValue());
        pageParam.put("TENDER_TYPE_04", DictCodeListExt4.招标方式_混合式招标.getValue());

        pageParam.put("WIN_TYPE_01", DictCodeListExt4.投标方式_自主投标.getValue());
        pageParam.put("WIN_TYPE_02", DictCodeListExt4.投标方式_代投代缴.getValue());
        pageParam.put("WIN_TYPE_03", DictCodeListExt4.投标方式_分销买入.getValue());

        pageParam.put("ISSUE_CLASS_02", DictCodeListExt4.新债_发行方式_招标发行.getValue());
        pageParam.put("ISSUE_CLASS_03", DictCodeListExt4.新债_发行方式_簿记建档.getValue());
        pageParam.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("busiTypes",
                Arrays.asList(BussinessDictCodeList.共同业务类型_中标结果维护.getValue(), BussinessDictCodeList.共同业务类型_一级投标确认_投标_分销买入.getValue()));
        //基本信息
        CfetsManualSettleInfoOtherListRespond respond = cfetsManualSettleMapper.findManualSettleInfoBidFirm(pageParam);
        if (!StringUtil.isNotEmpty(respond)){
            return null;
        }
        if (StringUtil.isNotEmpty(respond) && (StringUtil.isNotEmpty(respond.getBidDateStart()) ||StringUtil.isNotEmpty(respond.getBidDateEnd()))) {
            List<Date> dates = new ArrayList<>();
            dates.add(respond.getBidDateStart());
            dates.add(respond.getBidDateEnd());
            respond.setBidDateList(dates);
        }
        String spcdCodeTemp = "01";
        if (StringUtil.equals(respond.getBusiType(), BussinessDictCodeList.共同业务类型_一级投标确认_投标_分销买入.getValue())) {
            spcdCode = spcdCodeTemp + "FXB";
            // 基本信息
            List<CfgReadonlyFormShow> infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            resultData.put("infoForm", infoForm);
            // 金额信息
            spcdCode = spcdCodeTemp + "FXA";
            List<CfgReadonlyFormShow> infoForm1 = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            resultData.put("infoForm1", infoForm1);
            // 投标产品列表
            spcdCode = spcdCodeTemp + "FXP";
            List<CfgReadonlyFormShow> infoForm2 = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            resultData.put("infoForm2", infoForm2);
            // 查看页面表格不可编辑,不会有个性化配置
            List<CfgDataTableShow> infoForm4 = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode, EFFERANGE_NO, USER_ID);
            List<PortIntFenPeiInfoDto> portIntFenPeiInfoDtoList = cfetsManualSettleMapper.findBidFirmPortIntFenPeiInfoList(pageParam);
            BigDecimal etrBidPriceTotal = BigDecimal.ZERO;
            for (PortIntFenPeiInfoDto item : portIntFenPeiInfoDtoList) {
                etrBidPriceTotal = ArithmeticUtil.doAdd(etrBidPriceTotal, item.getUnFinshAmt());
            }
            respond.setEtrBidPriceTotal(etrBidPriceTotal);
            resultData.put("talbeForm", infoForm4);
            resultData.put("infoData", respond);
            resultData.put("infoData1", respond);
            resultData.put("infoData2", SysDataUtil.getNameFromDataDict(DictCode.组合分配方式, respond.getSpanTextOne()));
            resultData.put("talbeData", portIntFenPeiInfoDtoList);
            // 金额信息
            spcdCode = spcdCodeTemp + "FXR";
            List<CfgReadonlyFormShow> infoForm3 = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            resultData.put("infoForm3", infoForm3);
            resultData.put("infoData3", respond.getRemark());
        } else {
            spcdCode = spcdCodeTemp + "ZBB";
            // 基本信息
            List<CfgReadonlyFormShow> infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            resultData.put("infoForm", infoForm);
            // 金额信息
            spcdCode = spcdCodeTemp + "ZBA";
            List<CfgReadonlyFormShow> infoForm1 = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            resultData.put("infoForm1", infoForm1);
            // 投标产品列表
            spcdCode = spcdCodeTemp + "ZBP";
            List<CfgReadonlyFormShow> infoForm2 = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            resultData.put("infoForm2", infoForm2);
            // 查看页面表格不可编辑,不会有个性化配置
            List<CfgDataTableShow> infoForm4 = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode, EFFERANGE_NO, USER_ID);
            List<PortIntFenPeiInfoDto> portIntFenPeiInfoDtoList = cfetsManualSettleMapper.findBidFirmPortIntFenPeiInfoList(pageParam);
            BigDecimal etrBidPriceTotal = BigDecimal.ZERO;
            for (PortIntFenPeiInfoDto item : portIntFenPeiInfoDtoList) {
                etrBidPriceTotal = ArithmeticUtil.doAdd(etrBidPriceTotal, item.getUnFinshAmt());
            }
            respond.setEtrBidPriceTotal(etrBidPriceTotal);
            resultData.put("talbeForm", infoForm4);
            resultData.put("infoData", respond);
            resultData.put("infoData1", respond);
            resultData.put("infoData2", SysDataUtil.getNameFromDataDict(DictCode.中标方式, respond.getSpanTextOne()));
            resultData.put("talbeData", portIntFenPeiInfoDtoList);
            // 金额信息
            spcdCode = spcdCodeTemp + "ZBR";
            List<CfgReadonlyFormShow> infoForm3 = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
            resultData.put("infoForm3", infoForm3);
            resultData.put("infoData3", respond.getRemark());
        }
        return resultData;
    }

    /**
     * 现券买卖成交信息
     *
     * @param pageCode 页面编码
     * @param dtCode   区域编码
     * @param spcdCode 条件编码
     * @param userId   用户id
     * @param dto      数据
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author jiangpeng
     * @date 2022/5/31 11:05
     */
    private Map<String, Object> findSecManualSettleInfo(String pageCode, String dtCode, String spcdCode, String userId, CfetsManualSettleDto settleDto) {
        Map<String, Object> resultData = new HashMap<>();
        String spcdCodeTemp = spcdCode;
        //基本信息
        List<CfgReadonlyFormShow> infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
        //金额信息
        spcdCode = spcdCode + ",M";
        List<CfgReadonlyFormShow> infoForm1 = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
        //组合
        spcdCode = spcdCode + ",P";
        List<CfgDataTableShow> infoForm2;
        //获取登录用户的ID
        CfgDataTableShowFlag flag = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCode, spcdCode, userId);
        if (null == flag) {
            infoForm2 = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode, EFFERANGE_NO, USER_ID);
        } else {
            infoForm2 = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode, EFFERANGE_IS, userId);
        }

        Map<String, Object> pageParam = BeanUtil.object2Map(settleDto);
        pageParam.put("FINPROD_TYPE_TGF01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        pageParam.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("YESNO_N", DictCodeList.是否_否.getValue());
        pageParam.put("spcdCode", spcdCode);
        pageParam.put("feeType06", BussinessDictCodeList.费用类型_交易手续费.getValue());
        pageParam.put("feeType08", BussinessDictCodeList.费用类型_结算手续费.getValue());
        pageParam.put("psT39", BussinessDictCodeList.交易方向_行权赎回.getValue());
        pageParam.put("A_001", DictCodeListExt3.资产类型_资产账户持仓.getValue());
        pageParam.put("cdate", iPlatformDate.getSysDate());
        pageParam.put("DATE_TYPE_0", 0);
        resultData.put(dtCode + "_form", infoForm);
        resultData.put(dtCode + "_form1", infoForm1);
        resultData.put(dtCode + "_form2", infoForm2);

        //组合分配信息
        pageParam.put("CASH_TYPE_1_02", DictCodeListExt1.现金流一级类型_利息.getValue());
        pageParam.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
        List<PortIntFenPeiInfoDto> portIntFenPeiInfoDtoList = cfetsManualSettleMapper.findPortIntFenPeiInfoList(pageParam);
        if (CollectionUtils.isEmpty(portIntFenPeiInfoDtoList)) {
            portIntFenPeiInfoDtoList = cfetsManualSettleMapper.findTempPortIntFenPeiInfoList(pageParam);
        }
        if (StringUtil.equals(spcdCodeTemp, "04")) {
            CfetsManualSettleInfoRespond cfetsManualSettleInfoRespond = cfetsManualSettleMapper.findManualSettleInfo(pageParam);
            if (Objects.isNull(cfetsManualSettleInfoRespond)) {
                cfetsManualSettleInfoRespond = cfetsManualSettleMapper.findTempManualSettleInfo(pageParam);
            }
            resultData.put(dtCode + "_data", cfetsManualSettleInfoRespond);
            resultData.put(dtCode + "_data1", cfetsManualSettleInfoRespond);
        } else {
            CfetsManualSettleInfoOtherListRespond cfetsManualSettleInfoOtherListRespond = cfetsManualSettleMapper.findManualSettleInfoOther(pageParam);
            if (Objects.isNull(cfetsManualSettleInfoOtherListRespond)) {
                cfetsManualSettleInfoOtherListRespond = cfetsManualSettleMapper.findTempManualSettleInfoOther(pageParam);
            }
            resultData.put(dtCode + "_data", cfetsManualSettleInfoOtherListRespond);
            resultData.put(dtCode + "_data1", cfetsManualSettleInfoOtherListRespond);
        }
        resultData.put(dtCode + "_data2", portIntFenPeiInfoDtoList);
        return resultData;
    }

    /**
     * 质押式回购成交信息
     *
     * @param pageCode 页面编码
     * @param dtCode   区域编码
     * @param spcdCode 条件编码
     * @param userId   用户id
     * @param dto      数据
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author jiangpeng
     * @date 2022/5/31 11:05
     */
    private Map<String, Object> findRepoManualSettleInfo(String pageCode, String dtCode, String spcdCode, String userId, CfetsManualSettleDto settleDto) {
        Map<String, Object> resultData = new HashMap<>();
        if (DictCodeListExt3.头寸业务类型_质押式回购到期.getValue().equals(settleDto.getTgBusiType())) {
            settleDto.setTradeId(cfetsTrdQueryMapper.getTotalTradeId(settleDto.getFinprodId()));
        }
        //基础信息
        List<CfgReadonlyFormShow> baseForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "05");
        //金额信息05,M
        List<CfgReadonlyFormShow> amtForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "05,M");
        //金额信息05,P
        List<CfgReadonlyFormShow> fForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "05,P");
        //备注05,O
        List<CfgReadonlyFormShow> infoFormo = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, "05,O");
        //组合信息05,M,P
        CfgDataTableShowFlag flag = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCode, spcdCode, userId);
        List<CfgDataTableShow> zhTable = null;
        if (null == flag) {
            zhTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, "05,M,P", EFFERANGE_NO, USER_ID);
        } else {
            zhTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, "05,M,P", EFFERANGE_IS, userId);
        }
        //质押券信息05
        flag = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCode, spcdCode, userId);
        List<CfgDataTableShow> pledgeTable = null;
        if (null == flag) {
            pledgeTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, "05", EFFERANGE_NO, USER_ID);
        } else {
            pledgeTable = cfgDataTableMapper.getByPageCode(pageCode, dtCode, "05", EFFERANGE_IS, userId);
        }
        //逆回购剔除组合代码和名称
        List<CfgDataTableShow> pledgeTable04 = new ArrayList<>();
        for (CfgDataTableShow show : pledgeTable) {
            if (settleDto.getPs().equals("04") && ("portfolioId".equals(show.getColCode()) || "portfolioName".equals(show.getColCode())))
                continue;
            pledgeTable04.add(show);
        }
        resultData.put("CfetsTrdQueryRegInfo_formb", baseForm);
        resultData.put("CfetsTrdQueryRegInfo_formm", amtForm);
        resultData.put("CfetsTrdQueryRegInfo_formp", fForm);
        resultData.put("CfetsTrdQueryRegInfo_formo", infoFormo);
        resultData.put("CfetsTrdQueryRegInfo_tablep", zhTable);
        resultData.put("CfetsTrdQueryRegInfo_tablepledge", pledgeTable04);

        Map<String, Object> pageParam = new HashMap<>();
        pageParam.put("totleTradeId", settleDto.getTradeId());
        //判断角色配置
        List<String> roleList = cfetsTrdQueryMapper.selectSysRoleById(userId);
        if (roleList.contains(DictCodeList.角色岗位_领导.getValue()) || roleList.contains(DictCodeList.角色岗位_投资处长.getValue()) || roleList.contains(DictCodeList.角色岗位_交易处长.getValue())) {
            //角色配置为领导、交易处长、投资处长可查看所有数据
        } else if (roleList.contains(DictCodeList.角色岗位_交易员.getValue())) {
            //交易员
            pageParam.put("submitUserId", SecurityUtil.getLoginUser().getUserName());
        } else if (roleList.contains(DictCodeList.角色岗位_投资经理.getValue())) {
            //投资经理
            pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
        }
        pageParam.put("effectiveStatus", DictCodeListExt1.有效状态_有效.getValue());
        pageParam.put("STATUS_VALID_01", DictCodeListExt1.有效状态_有效.getValue());
        pageParam.put("STATUS_VALID_03", DictCodeListExt1.有效状态_撤销.getValue());
        pageParam.put("STATUS_VALID_04", DictCodeListExt1.有效状态_流程中.getValue());

        pageParam.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("YESNO_N", DictCodeList.是否_否.getValue());
        pageParam.put("spcdCode", spcdCode);
        pageParam.put("feeType06", BussinessDictCodeList.费用类型_交易手续费.getValue());
        pageParam.put("feeType08", BussinessDictCodeList.费用类型_结算手续费.getValue());
        //组合分配信息
        pageParam.put("CASH_TYPE_1_02", DictCodeListExt1.现金流一级类型_利息.getValue());
        pageParam.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
        //表单data
        CfetsTrdSettleInfoResponse dataform = cfetsManualSettleMapper.findManualSettleInfoRepo(pageParam);
        //表格组合分配data
        pageParam.put("totleTradeId", settleDto.getTradeId());
        List<PortIntFenPeiInfoDto> portIntFenPeiInfoDtoList = cfetsTrdQueryMapper.findRepoPortIntFenPeiInfoList(pageParam);

        //表格质押券data
        pageParam.put("TRADEPLACE01", DictCodeListExt1.交易场所_银行间.getValue());
        pageParam.put("TRADEPLACE03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        pageParam.put("TRADEPLACE04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        pageParam.put("platDate", iPlatformDate.getSysDateStr());
        pageParam.put("totleTradeId", settleDto.getTradeId());
        List<CfetsPledgeInfoResponse> pledgeInfoDtoList = cfetsTrdQueryMapper.findRepoPledgeInfo(pageParam);
//            if(CollectionUtils.isEmpty(pledgeInfoDtoList)) {
//                pageParam.put("totleTradeId",cfetsTrdQueryMapper.getTradeId(settleDto.getTradeId()));
//                pledgeInfoDtoList =  cfetsTrdQueryMapper.selectRegTablePledgeInfoList(pageParam);
//            }

        resultData.put("CfetsTrdQueryRegInfo_dataform", dataform);
        resultData.put("CfetsTrdQueryRegInfo_datatablep", portIntFenPeiInfoDtoList);
        resultData.put("CfetsTrdQueryRegInfo_datatablepledge", pledgeInfoDtoList);
        return resultData;
    }

    /**
     * 一级申购成交信息
     *
     * @param pageCode 页面编码
     * @param dtCode   区域编码
     * @param spcdCode 条件编码
     * @param userId   用户id
     * @param dto      数据
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author jiangpeng
     * @date 2022/5/31 11:05
     */
    private Map<String, Object> findSubManualSettleInfo(String pageCode, String dtCode, String spcdCode, String userId, CfetsManualSettleDto settleDto) {
        Map<String, Object> resultData = new HashMap<>();

        //基本信息
        List<CfgReadonlyFormShow> infoForm = cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
        //组合
        spcdCode = spcdCode + ",P";

        List<CfgDataTableShow> infoForm2;
        //获取登录用户的ID
        CfgDataTableShowFlag flag = cfgDataTableFlagMapper.getByPageCodeAndDtCode(pageCode, dtCode, spcdCode, userId);
        if (null == flag) {
            infoForm2 = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode, EFFERANGE_NO, USER_ID);
        } else {
            infoForm2 = cfgDataTableMapper.getByPageCode(pageCode, dtCode, spcdCode, EFFERANGE_IS, userId);
        }

        Map<String, Object> pageParam = BeanUtil.object2Map(settleDto);
        pageParam.put("FINPROD_TYPE_TGF01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        pageParam.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("YESNO_N", DictCodeList.是否_否.getValue());
        pageParam.put("spcdCode", spcdCode);
        pageParam.put("feeType06", BussinessDictCodeList.费用类型_交易手续费.getValue());
        pageParam.put("feeType08", BussinessDictCodeList.费用类型_结算手续费.getValue());
        pageParam.put("cdate", iPlatformDate.getSysDate());
        resultData.put(dtCode + "_form", infoForm);
        resultData.put(dtCode + "_form2", infoForm2);
        //基本信息
        CfetsManualSettleInfoOtherListRespond cfetsManualSettleInfoOtherListRespond = cfetsManualSettleMapper.findManualSettleInfoBid(pageParam);
        //组合分配信息
        List<PortIntFenPeiInfoDto> portIntFenPeiInfoDtoList = cfetsManualSettleMapper.findBidPortIntFenPeiInfoList(pageParam);
        resultData.put(dtCode + "_data", cfetsManualSettleInfoOtherListRespond);

        resultData.put(dtCode + "_data2", portIntFenPeiInfoDtoList);
        return resultData;
    }

}
