package com.joyintech.tams.business.common.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.dao.JoyinbusinessCommonMapper;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.dto.CfetsSearchInfoRequest;
import com.joyintech.tams.business.common.dto.SecInfoModelDto;
import com.joyintech.tams.business.common.dto.SecManageAcctIdRequestDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.commons.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公共方法service
 */
@Service("JoyinbusinessCommonService")
public class JoyinbusinessCommonService extends BaseService {

    @Autowired
    private JoyinbusinessCommonMapper joyinbusinessCommonMapper;
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 根据条件查询证券账户
     *
     * @param secManageAcctIdRequestDto
     * @return List<SelectOptionDto>
     */
    public Page<SelectOptionDto> getSecManageAcctId(SecManageAcctIdRequestDto secManageAcctIdRequestDto) {
        Map<String,Object> pageParam = BeanUtil.object2Map(secManageAcctIdRequestDto);
        String portfolioId = (String)pageParam.get("portfolioId");
        if(StringUtil.isNotEmpty(pageParam.get("portfolioId"))){
            List<String> portfolioIdList = StringUtil.splitToList(",",(String)pageParam.get("portfolioId"));
            pageParam.put("portfolioIdList", portfolioIdList);
        }

        String finprodMarketId = (String)pageParam.get("finprodMarketId");
        if(StringUtil.isNotEmpty(pageParam.get("finprodMarketId"))){
            List<String> finprodMarketIdList = StringUtil.splitToList(",",(String)pageParam.get("finprodMarketId"));
            pageParam.put("finprodMarketIdList", finprodMarketIdList);
        }
        return super.queryForPage(secManageAcctIdRequestDto.getPageMode().getPageNo(), secManageAcctIdRequestDto.getPageMode().getPageSize(),
                () -> joyinbusinessCommonMapper.getSecManageAcctId(pageParam));
    }

    /**
     * 委托代码的联想文本框的查询方法
     *
     * @param entrustIdRequestDto
     * @return List<SelectOptionDto>
     */
    public Page<SelectOptionDto> getEntrustId(CfetsSearchInfoRequest entrustIdRequestDto) {
        Map<String,Object> pageParam = new HashMap<String,Object>();
        pageParam.put("search", entrustIdRequestDto.getSearch());
        pageParam.put("entrustAwait", DictCodeList.委托状态_待接收.getValue());
        pageParam.put("entrustDistribute", DictCodeListExt3.委托状态_待分发.getValue());
        pageParam.put("entrustReceive", DictCodeList.委托状态_已接收.getValue());
        pageParam.put("entrustUntread", DictCodeList.委托状态_已完成.getValue());
        pageParam.put("entrustClose", DictCodeList.委托状态_已关闭.getValue());
        pageParam.put("entrustReturn", DictCodeList.委托状态_已退回.getValue());
        pageParam.put("entrustRecall", DictCodeList.委托状态_已撤回.getValue());
        return super.queryForPage(entrustIdRequestDto.getPageMode().getPageNo(), entrustIdRequestDto.getPageMode().getPageSize(),
                () ->joyinbusinessCommonMapper.getEntrustId(pageParam));
    }
    public Page<SelectOptionDto> getfinprodIdForAdd(CfetsSearchInfoRequest dto) {
        Map<String, Object> param =new HashMap<>();
        param.put("search",dto.getSearch());
        param.put("assestType", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        param.put("TRADE_PLACE_01",DictCodeListExt1.交易场所_银行间.getValue());
        param.put("TRADE_PLACE_02",DictCodeListExt1.交易场所_商业银行柜台.getValue());
        param.put("platDate",DateUtil.platDateFromCache());
        param.put("initValue",dto.getInitValue());
        if (StringUtil.isNotEmpty(dto.getDealType()) && StringUtil.equals(dto.getDealType(), "01")) {
            //需要查询债券资产和回购期限
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> joyinbusinessCommonMapper.getfinprodIdOfBondAndRepo(param));
        }
        if (StringUtil.isNotEmpty(dto.getDealType()) && StringUtil.equals(dto.getDealType(), "99")) {
            param.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
            param.put("TRADE_PLACE_09", DictCodeListExt1.交易场所_OTC场外.getValue());
            param.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
            //需要查询债券资产、回购期限、项目类资产、公募基金、计划类净值型、计划类利率型、计划类货币型、存款
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> joyinbusinessCommonMapper.getfinprodIdForAll(param));
        }
        if(StringUtil.isNotEmpty(dto.getPs()) && (StringUtil.equals(dto.getPs(),DictCodeList.交易方向_行权_回售.getValue())
                                                   ||StringUtil.equals(dto.getPs(),DictCodeList.交易方向_行权_赎回.getValue()))){
            param.put("ps",dto.getPs());
            String userId = SecurityUtil.getLoginUser().getUserName();
            if (StringUtil.equals(dto.getPs(),DictCodeList.交易方向_行权_回售.getValue()) && StringUtils.isNotEmpty(userId)) {
                param.put("userId", SecurityUtil.getLoginUser().getUserName());
            }
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), ()->joyinbusinessCommonMapper.getRightfinprodIdForAdd(param));

        }else{
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), ()->joyinbusinessCommonMapper.getfinprodIdForAdd(param));
        }
    }

    /**
     * 新债简称列表查询
     * @param dto
     * @return
     */
    public Page<SelectOptionDto> getfinprodIdForNewBond(CfetsSearchInfoRequest dto) {
        Map<String, Object> param = new HashMap<>();
        param.put("search", dto.getSearch());
        param.put("assestType", DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
        param.put("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        param.put("TRADE_PLACE_02", DictCodeListExt1.交易场所_商业银行柜台.getValue());
        param.put("platDate", DateUtil.platDateFromCache());

        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> joyinbusinessCommonMapper.getfinprodIdForNewBond(param));
    }

    public Page<SelectOptionDto> selectPortfolioIdList(CfetsSearchInfoRequest dto) {
        Map<String, Object> param =new HashMap<>();
        param.put("search",dto.getSearch());
        param.put("platDate",DateUtil.platDateFromCache());
        param.put("initValue",dto.getInitValue());
        if(StringUtil.isNotEmpty(dto.getPs()) && StringUtil.equals(dto.getPs(),DictCodeList.交易方向_行权_赎回.getValue())){
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), ()->joyinbusinessCommonMapper.selectPortfolioIdList(param));
        }
        String userId = SecurityUtil.getLoginUser().getUserName();
        if (StringUtils.isNotEmpty(userId)) {
            param.put("userId", SecurityUtil.getLoginUser().getUserName());
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), ()->joyinbusinessCommonMapper.selectPortfolioIdList(param));
        }else{
            return null;
        }
    }
    public Page<SelectOptionDto> getManager(CfetsSearchInfoRequest dto) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("manager", dto.getSearch());
        param.put("roleOne", DictCodeList.角色岗位_投资经理.getValue());
        param.put("roleTwo", DictCodeList.角色岗位_项目经理岗.getValue());
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), ()->joyinbusinessCommonMapper.getManagers(param));
    }

    /**
     * 修改 添加判断 通过流程id查询是否存在数据,存在则是退回数据需要更新历史操作记录,不存在则是新纪录,直接登录数据
     *
     * @param dto
     * @return java.lang.Boolean
     * @author jiangpeng
     * @date 2022/4/27 9:45
     */
    public Boolean updateCfetsOprHis(CfetsOprHisInfoRequest dto) {
        dto.setSeqNo(IDGenerator.generate(32));
        dto.setOperDate(DateUtil.platDateFromCache());
        dto.setUserId(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
        dto.setCreateUser(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
        dto.setCreateDept(CommonUtil.getDeptCode());
        dto.setUpdateUser(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "SYSTEM");
        List<CfetsOprHisInfoRequest> wflDto = new ArrayList<>();
        if (StringUtil.isNotEmpty(dto.getWorkflowId())) {
            wflDto = joyinbusinessCommonMapper.findCfetsOprHisByWorkflowId(dto.getWorkflowId());
        }
        int flag = 0;
        if (StringUtil.isNotEmpty(wflDto)) {
            flag = joyinbusinessCommonMapper.updateCfetsOprHis(dto);
        } else {
            flag = joyinbusinessCommonMapper.insertCfetsOprHis(dto);
        }
        if (flag > 0) {
            return true;
        }
        return false;
    }
    public Page<SelectOptionDto> queryMySideList(CfetsSearchInfoRequest dto) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("search", dto.getSearch());
        param.put("sysDate",iPlatformDate.getSysDate());
        param.put("initValue",dto.getInitValue());
        if(StringUtil.isNotEmpty(dto.getDateType()) && StringUtil.equals(dto.getDateType(),"01")){
            //查询历史数据，不需要当日日期
            param.remove("sysDate");
        }
        String searchs = dto.getSearchs();
        if(StringUtil.isNotEmpty(searchs)){
            param.put("searchList", StringUtil.splitToList(GlobalConstant.COMMA,searchs));
        }
        if(StringUtil.equals(dto.getDealType(),"01")){
            //系统内的交易对手，即市场机构和资管产品的数据
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), ()->joyinbusinessCommonMapper.queryCounterPartyList(param));
        }else if(StringUtil.equals(dto.getDealType(),"02")){
            //银行间机构，且必须关联到系统的交易对手
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), ()->joyinbusinessCommonMapper.queryMySideAndPartyList(param));
        }else if(StringUtil.equals(dto.getDealType(),"03")){
            //CSTP下行接口对手方
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), ()->joyinbusinessCommonMapper.queryCstpPartyList(param));
        }else if(StringUtil.equals(dto.getDealType(),"04")){
            //CSTP下行接口本方
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), ()->joyinbusinessCommonMapper.queryCstpOuPartyList(param));
        }else{
            //组合关联的银行间机构,此处前端能拿到组合的21位码
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), ()->joyinbusinessCommonMapper.queryMySideList(param));
        }
    }
    public Page<SelectOptionDto> queryMySideTraderList(CfetsSearchInfoRequest dto) {
        Map<String, Object> param = BeanUtil.object2Map(dto);
        if(StringUtil.equals(dto.getDealType(),"01")){
            //系统内的用户
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), ()->joyinbusinessCommonMapper.queryUserIdList(param));
        }else{
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), ()->joyinbusinessCommonMapper.queryMySideTraderList(param));
        }
    }

    /**
     * 查询页面、按钮是否走流程
     * @param param
     * @return
     */
    public String isNeedWorkflow(Map<String, String> param) {
        param.put("noneApproval", "noneApproval");
        param.put("yesOrNo01",DictCodeList.是否_中债_是.getValue());
        param.put("yesOrNo02",DictCodeList.是否_中债_否.getValue());
        param.put("yesOrNoY",DictCodeList.是否_是.getValue());
        param.put("yesOrNoN",DictCodeList.是否_否.getValue());
        return joyinbusinessCommonMapper.isNeedWorkflow(param);
    }

    /**
     * 审批页面 根据id查询本方或对手方
     *
     * @param dto
     * @return java.util.List<com.joyintech.framework.select.SelectOptionDto>
     * @author jiangpeng
     * @date 2022/1/14 14:37
     */
    public List<SelectOptionDto> queryMySideOrCount(CfetsSearchInfoRequest dto) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("search", dto.getSearch());
        param.put("sysDate", iPlatformDate.getSysDate());
        if (StringUtil.isNotEmpty(dto.getDateType()) && StringUtil.equals(dto.getDateType(), "01")) {
            //查询历史数据，不需要当日日期
            param.remove("sysDate");
        }
        String searchs = dto.getSearchs();
        if (StringUtil.isNotEmpty(searchs)) {
            param.put("searchList", StringUtil.splitToList(GlobalConstant.COMMA, searchs));
        }
        if (StringUtil.equals(dto.getDealType(), "01")) {
            //系统内的交易对手，即市场机构和资管产品的数据
            return joyinbusinessCommonMapper.queryCounterPartyList(param);
        } else if (StringUtil.equals(dto.getDealType(), "02")) {
            //银行间机构，且必须关联到系统的交易对手
            return joyinbusinessCommonMapper.queryMySideAndPartyList(param);
        } else if (StringUtil.equals(dto.getDealType(), "03")) {
            //CSTP下行接口对手方
            return joyinbusinessCommonMapper.queryCstpPartyList(param);
        } else if (StringUtil.equals(dto.getDealType(), "04")) {
            //CSTP下行接口本方
            return joyinbusinessCommonMapper.queryCstpOuPartyList(param);
        } else {
            //组合关联的银行间机构,此处前端能拿到组合的21位码
            return joyinbusinessCommonMapper.queryMySideList(param);
        }
    }

    /**
     * 审批页面 根据id查询本方交易员或对手方交易员
     *
     * @param dto
     * @return java.util.List<com.joyintech.framework.select.SelectOptionDto>
     * @author jiangpeng
     * @date 2022/1/14 14:42
     */
    public List<SelectOptionDto> queryMySideTraderOrCountTrader(CfetsSearchInfoRequest dto) {
        Map<String, Object> param = BeanUtil.object2Map(dto);
        if (StringUtil.equals(dto.getDealType(), "01")) {
            //系统内的用户
            return joyinbusinessCommonMapper.queryUserIdList(param);
        } else if (StringUtil.equals(dto.getDealType(), "02")) {
            // 根据系统内机构查询用户
            return joyinbusinessCommonMapper.queryMySideTraderListByCounterId(param);
        } else {
            return joyinbusinessCommonMapper.queryMySideTraderList(param);
        }
    }

    public Page<SelectOptionDto> gettotleTradeId(CfetsSearchInfoRequest entrustIdRequestDto) {
        return super.queryForPage(entrustIdRequestDto.getPageMode().getPageNo(), entrustIdRequestDto.getPageMode().getPageSize(),
                () ->joyinbusinessCommonMapper.gettotleTradeId());
    }

    /**
     * 计算存续期
     * @param secId
     * @return
     */
    public String calPendingPeriod(String secId) {
        Date cDate = iPlatformDate.getSysDate();
        Map map = new HashMap();
        map.put("finprodId",secId);
        SecInfoModelDto dto = getSecInfoModel(map);
        // tDate默认取到期日
        Date tDate = dto.getEndDate();
        Date startDate = dto.getStartDate();
        // 计算结果
        List<Date[]> result = new ArrayList<>();
        // 下一行权日
        Date nextExeDate = dto.getNextExeDate();
        // 含权标识
        String isRight = dto.getIsRight();
        String pendingPeriodStr1 = "";
        String pendingPeriodStr2 = "";
        // 到期展示--
        if (cDate.compareTo(tDate) == 1){
            return "--";
        }else{
            // 含权债TDTAE取值时，如当前计算日以后存在回售或赎回行权计划的且理论行权日不为到期日的，
            // 则TDATE取2个值计算2个结果，即TDATE分别为最近一个理论行权日、到期日，否则取到期日计算1个结果；
            if(DictCodeList.是否_是.getValue().equals(isRight) && null!=nextExeDate
                    && DateUtil.compareDate(nextExeDate, tDate)!=0){
                result = calcDayInYear(startDate,cDate, nextExeDate, 1);
                pendingPeriodStr1=getPendingPeriod(result,cDate,nextExeDate);
            }
            result = calcDayInYear(startDate,cDate, tDate, 1);
            pendingPeriodStr2=getPendingPeriod(result,cDate,tDate);
            if(CommonUtils.isNotEmpty(pendingPeriodStr1)){
                return pendingPeriodStr1+"+"+pendingPeriodStr2;
            }else{
                return pendingPeriodStr2;
            }
        }
    }


    /**
     * 查询债券基本信息
     *
     * @param map 参数
     * @return 债券基本信息
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public SecInfoModelDto getSecInfoModel(Map<String, Object> map) {
        map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        map.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        map.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        map.put("sysDate", iPlatformDate.getSysDate());
        map.put("YES_NO_Y", DictCodeList.是否_是.getValue());
        map.put("EXEC_MODE02", DictCodeList.行权方式_回售.getValue());
        map.put("EXEC_MODE01", DictCodeList.行权方式_赎回.getValue());
        map.put("psT03", DictCodeList.交易方向_行权_回售.getValue());
        map.put("psT39", DictCodeList.交易方向_行权_赎回.getValue());

        return joyinbusinessCommonMapper.getSecInfoModel(map);
    }

    /**
     * 计算calDate所在的计息年度结束日
     * @param startDate 开始日
     * @param calDate 计算日
     * @param flag 向前向后推算标识（1:向后，-1：向前）
     * @return 计息年度结束日
     */
    private List<Date[]> calcDayInYear(Date startDate, Date calDate, Date endDate,int flag) {
        List<Date[]> dateList = new ArrayList<>();
        // 计息年度开始日
        Date start = null;
        // 计息年度结束日
        Date end = startDate;
        int index = 0;
        do {
            index ++;
            start = end;
            end = calcOneYearDate(startDate, flag * index);
            if(flag > 0 ){
                if(start != null && (DateUtil.compareDate(end,calDate)>=0)) {
                    dateList.addAll(Collections.singleton(new Date[]{start,end}));
                }
            }else{
                if(start != null && (DateUtil.compareDate(calDate,start)>=0)) {
                    dateList.addAll(Collections.singleton(new Date[]{start,end}));
                }
            }
        } while((flag > 0 && (DateUtil.compareDate(endDate,end)>=0))
                || (flag < 0 && (DateUtil.compareDate(end,endDate)>=0)));
        return dateList;
    }

    /**
     * 计算从开始日过amount个年度的时间结束日
     * @param startDate 开始日期
     * @param amount 向前向后标识
     * @return 结束日
     */
    private Date calcOneYearDate(Date startDate, int amount) {
        if(null != startDate) {
            return DateUtil.addYear(startDate,amount);
        }
        return startDate;
    }


    private String getPendingPeriod(List<Date[]> result,Date cDate, Date tDate){
        Date yearStartDate = null;
        Date yearEndDate = null;
        BigDecimal pendingPeriod = BigDecimal.ZERO;
        if((int)DateUtil.getBetweenDayNumber(cDate,tDate)<365){
            pendingPeriod=BigDecimal.valueOf(DateUtil.getBetweenDayNumber(cDate,tDate));
            return pendingPeriod+"D";
        }else{
            for(int i = 0; i < result.size(); i++ ){
                Date maxStartDate = null;
                Date minEndDate = null;
                // 判断计息年度是否计算成功
                if(null != result && result.get(i).length == 2) {
                    yearStartDate=result.get(i)[0];
                    yearEndDate=result.get(i)[1];
                }
                // 比较计算日和计息年度开始日，取最大
                if(DateUtil.compareDate(cDate, yearStartDate)>=0){
                    maxStartDate = cDate;
                }else{
                    maxStartDate = yearStartDate;
                }
                // 比较计算日和计息年度截止日日，取最小
                if(DateUtil.compareDate(tDate, yearEndDate)==-1){
                    minEndDate = tDate;
                }else{
                    minEndDate = yearEndDate;
                }
                pendingPeriod = ArithmeticUtil.doAdd(pendingPeriod,
                        ArithmeticUtil.divide(BigDecimal.valueOf(DateUtil.getBetweenDayNumber(maxStartDate,minEndDate)),
                                BigDecimal.valueOf(DateUtil.getBetweenDayNumber(yearStartDate,yearEndDate)), 2));
            }
        }
        return pendingPeriod+"Y";
    }


    /**
     * 查询是否已银行间交易员关联的系统内用户
     *
     * @param mySideTrader 交易员id
     * @param mySide 21位机构码
     * @return java.lang.String
     * @author jiangpeng
     * @date 2021/12/31 10:23
     */
    public String findUserByMySideTrader(String mySideTrader, String mySide) {
        return joyinbusinessCommonMapper.findUserByMySideTrader(mySideTrader, mySide);
    }

    public List<String> findAllModuleIds(String userName) {
        return joyinbusinessCommonMapper.findAllModuleIds(userName);
    }

    /**
     * 统计匹配数
     *
     * @param quoteId       业务编号
     * @param confirmStatus 确认状态
     * @return int
     * @author jiangpeng
     * @date 2022/4/28 17:18
     */
    public int countMatchInfo(String quoteId, String confirmStatus) {
        return joyinbusinessCommonMapper.countMatchInfo(quoteId, confirmStatus);
    }
}
