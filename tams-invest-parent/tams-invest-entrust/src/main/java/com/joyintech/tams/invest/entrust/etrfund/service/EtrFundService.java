/*
 * 文件名：EteFundService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2018年10月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrfund.service;

import com.alibaba.fastjson.JSONArray;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.system.base.entity.SysUser;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.trade.dto.BussEtrInvestPersonInfoDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etragrereptrade.dto.EtrAgreRepTrade003Dto;
import com.joyintech.tams.invest.entrust.etrfund.dto.EtrFundDto;
import com.joyintech.tams.invest.entrust.etrstock.service.EtrStockService;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 基金委托service
 * 〈功能详细描述〉
 * @author huwenbin
 * @version 1.0 2018年10月8日
 * @see EtrFundService
 * @since 1.0
 */
@Service
public class EtrFundService extends CommonExportService<EtrFundDto> implements BaseProcessService<EtrFundDto>{
    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;
    /**
     *
     */
    @Autowired
    private FinProductMapper finProductMapper;


    @Autowired
    private FinProductAddMapper finProductAddMapper;

    /**
     *
     */
    @Autowired
    private DateCalService dateCalService;

    /**
     *
     */
    @Autowired
    private CommonBusinessService commonBusinessService;
    /**
     * 多对多指令共同check的Service
     */
    @Autowired
    private EtrCommonService etrCommonService;
    /**
     * 股票的的Service
     */
    @Autowired
    private EtrStockService etrStockService;
    /**
     * 基金委托查询 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<EtrFundDto> selectForPage(Map<String, Object> param) {
        User user = SecurityUtil.getLoginUser();
        param.put("userId", user.getUserName());
        Page<EtrFundDto> page =  restTemplate.postForPage(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrFund/findPage", param,
                EtrFundDto.class);
        List<SysUser> userList = restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EntrustCommon/getAllUserList",
                param, SysUser.class);
        for(EtrFundDto dto:page.getRoot()){
            if(StringUtil.isNotEmpty(dto.getNextUser())){
                String sysUsers = userList.stream().filter(x-> Arrays.asList(dto.getNextUser().split(",")).contains(x.getUserName()))
                        .map(SysUser::getRealName)
                        .collect(Collectors.joining(",","",""));
                dto.setNextUser(sysUsers);
            }
        }
        return page;

    }

    /**
     *
     * 组合代码联想文本框
     * <功能详细描述>
     * @param portfolioId 组合代码
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getPortfolioId(String portfolioId){
        User user = SecurityUtil.getLoginUser();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getUserName());
        map.put("portfolioId", portfolioId);
        return restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME+"/EtrFund/getPortfolioId",
                map, SuggestDto.class);
    }

    /**
     *
     * 资产代码联想文本框
     * <功能详细描述>
     * @param finprodId 资产代码
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getFinprodMarketId(String finprodId, String pageId){

        return restTemplate.getForList(ServiceApplication.ENTRUST_SERVICE_NAME+"/EtrFund/getFinprodMarketId?finprodId="+finprodId + "&pageId="+pageId,
                SuggestDto.class);
    }

    /**
     *
     * 删除、撤销、关闭委托校验方法
     * <功能详细描述>
     * @param dto EtrFundDto
     * @return List<ValidMessage>
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtrFundDto dto) {
        return restTemplate.postForList(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrFund/check", dto,
                ValidMessage.class);
    }

    /**
     * 撤回委托
     * <功能详细描述>
     * @param entrustId 实体类
     * @return boolean
     * @see
     * @since 1.0
     */
    public boolean recallDelegate(String entrustId) {
        return restTemplate.getForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrFund/recall?entrustId="+entrustId,
                boolean.class);
    }
    /**
     * 关闭委托
     * <功能详细描述>
     * @param entrustId String
     * @param closeWay String
     * @return boolean
     * @see
     * @since 1.0
     */
    public boolean closeDelegate(String entrustId,String closeWay) {
        return restTemplate.getForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrFund/close?entrustId="+entrustId+"&closeWay="+closeWay,
                boolean.class);
    }

    /**
     *
     * 删除投资委托
     * <功能详细描述>
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public boolean deleteDelegate(String entrustId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrFund/delete?entrustId="+entrustId,
                boolean.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doInsert(EtrFundDto entity, OperationType type){
        //entity.setOperateType(type.toString());
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrFund/doInsert",
                entity, Integer.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doUpdate(EtrFundDto entity){
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrFund/doUpdate",
                entity, Integer.class);
    }

    @Override
    public void doBack(EtrFundDto entity) {
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrFund/doBack",
                entity, Integer.class);
    }

    /**
     * 导出
     *
     * @param exportParam 导出信息
     * @see
     * @since 1.0
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config("/com/joyintech/tams/invest/entrust/etrfund/conf/export_conf.xml")
                .dataSource(DataSourceType.MASERT);
    }
    @Override
    public Page<EtrFundDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if(StringUtil.equals(propName,"transactioncom")){
            cell.setCellValue(value+"%");
        }
    }
    /**
     * 获取新建页面组合代码的联想文本框 <功能详细描述>
     *
     * @param param Map<String, Object>
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getSelectPortfolioId(Map<String, Object> param) {
        User user = SecurityUtil.getLoginUser();
        param.put("loginUser", user.getUserName());
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrFund/getSelectPortfolioId" , param,
                SelectOptionDto.class);
    }

    /**
     * 获取新建页面资产代码的联想文本框 <功能详细描述>
     *
     * @param param Map<String, Object>
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getSelectFinprodMarketId(Map<String, Object> param) {
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrFund/getSelectFinprodMarketId" ,param,
                SelectOptionDto.class);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(EtrFundDto entity, OperationType type) {
        entity.setInvManagerId(SecurityUtil.getLoginUser().getUserName());
        entity.setOperateType(type.toString());
        basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getEntrustId());
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrFund/doComplete",
                entity, Integer.class);

    }

    /**
     *
     * 编辑、双击查看页面详情
     * 根据主键查询
     * @param entrustId String
     * @param finprodId String
     * @param portfolioId String
     * @return EtrBrowDepoInfoDto
     * @see
     * @since 1.0
     */
    public EtrFundDto selectByKey(String entrustId,String finprodId, String portfolioId){
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrFund/selectByKey?entrustId=" + entrustId+"&finprodId="+ finprodId+"&portfolioId="+ portfolioId,
                EtrFundDto.class);
    }

    /**
     *
     * 根据金融产品代码获取委托金额
     * <功能详细描述>
     * @param finprodId String
     * @return long
     * @see
     * @since 1.0
     */
    public BigDecimal selectEntrustAmt(String finprodId){
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrFund/selectEntrustAmt?finprodId="+finprodId,
                BigDecimal.class);
    }

    /**
     *
     * 根据金融产品代码获取发行规模
     * <功能详细描述>
     * @param finprodId String
     * @return long
     * @see
     * @since 1.0
     */
    public BigDecimal selectIssueAmt(String finprodId){
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrFund/selectIssueAmt?finprodId="+finprodId,
                BigDecimal.class);
    }

    /**
     * 组合可用现金查询
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Map<String,Object> getProAvailableCash(Map<String,Object> param) {
        // modify by etliu 20211129 start
        // TISHKS-465: 组合T日可用现金、组合T+1日可用现金为交易可用，应为指令可用
//        Date sysDate = DateUtil.platDateFromCache();
//        param.put("vdate", DateUtil.format(sysDate));
//        /*List<String> acctTypeList = new ArrayList<>();
//        acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
//        acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
//        dto.setAcctType(acctTypeList);*/
//        if(StringUtil.isNotEmpty(param.get("finprodId"))){
//            FinProduct finProduct = finProductMapper.selectByPrimaryKey((String)param.get("finprodId"));
//            List<String> holidayList = dateCalService.getDealCalendarHoliday(sysDate,DateUtil.addDay(sysDate, 15),finProduct.getCalendarId());
//            Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(sysDate).toString()), 1, holidayList, "1");
//            param.put("vdate1", DateUtil.format(tradeDay));
//        }
//        return restTemplate.postForObject(
//            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrFund/getProAvailableCash",param,
//            Map.class);
        Date vdate = DateUtil.parse((String)param.get("vdate"));
        String calId = (String)param.get("calId");
        if(StringUtil.isNotEmpty(calId)){
            List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(vdate)),DateUtil.addDay(DateUtil.parse(DateUtil.format(vdate)), 15),calId);
            Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(vdate)), 1, holidayList, "1");
            param.put("sysDateT1",DateUtil.format(tradeDay));
        }
        return etrCommonService.getProAvailableCash(param);
        // modify by etliu 20211129 end
    }

    /**
     * 组合T+1可用现金查询
     * <功能详细描述>
     * @param dto PtlCashPositionFlowDto
     * @return BigDecimal
     * @see
     * @since 1.0
     */
    public BigDecimal getProAvailableCash1(PtlCashPositionFlowDto dto) {
        Date sysDate = DateUtil.platDateFromCache();
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
        List<String> holidayList = dateCalService.getDealCalendarHoliday(sysDate,DateUtil.addDay(sysDate, 15),finProduct.getCalendarId());
        Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(sysDate).toString()), 1, holidayList, "1");
        dto.setCdate(tradeDay);
        List<String> acctTypeList = new ArrayList<>();
        acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
        acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
        dto.setAcctType(acctTypeList);
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrBrowDepoInfo/getProAvailableCash",dto,
                BigDecimal.class);
    }

    /**
     * 组合可用持仓查询
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getAssetAvailablePosition(PtlSecPositionFlowDto dto) {
        Date sysDate = DateUtil.platDateFromCache();
        dto.setCdate(DateUtil.parse(DateUtil.format(sysDate)));
        List<String> acctTypeList = new ArrayList<>();
        acctTypeList.add(DictCodeListExt1.资金账户类型_产品托管户.getValue());
        acctTypeList.add(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
        dto.setAcctType(acctTypeList);
        //dto.setHodingType(DictCodeList.持仓类型_可用.getValue());
        dto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
        if (StringUtil.isNotEmpty(dto.getSecAcctId())) {
            dto.setChannel(true);
        }
//        return restTemplate.postForObject(
//            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrFund/getAssetAvailablePosition",dto,
//            BigDecimal.class);
        EtrSecPositionDto dto1= new EtrSecPositionDto();
        BeanUtil.copyPropertiesIgnoreNull(dto1, dto);
        BigDecimal position = etrCommonService.getAssetAvailablePosition(dto1);
        return position;
    }

    /**
     *
     * 查询单位净值
     * <功能详细描述>
     * @param map Map<String,Object>
     * @return BigDecimal
     * @see
     * @since 1.0
     */
    public BigDecimal getPrice(Map<String,Object> map){
        Date sysDate = DateUtil.platDateFromCache();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(sysDate);
        calendar.add(calendar.DATE, -1);
        sysDate=calendar.getTime();
        map.put("sysDate", DateUtil.format(sysDate));
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrFund/getPrice",map,
                BigDecimal.class);
    }
    /**
     *
     * 查询授权额度
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal selectLimitAmt(EtrFundDto dto){
        Map<String, Object> map = new HashMap<>();
        map.put("userName", SecurityUtil.getLoginUser().getUserName());
        map.put("assetType", DictCodeList.平安资产类型_基金.getValue());
        map.put("assetSecType", dto.getAssetType1());
        map.put("tradeMarket", dto.getTradeMarket());
        map.put("ps",dto.getPs());
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/SelectLimitAmt/selectLimitAmt", map,
                BigDecimal.class);
    }
    /**
     * 通道查询下拉框查询
     * @param map
     * @return
     * @see
     */
    public Page<SelectOptionDto> getSelectTongDao(Map<String, Object> map){
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrFund/getSelectTongDao" , map,
                SelectOptionDto.class);
    }

    /**
     * 获取通道可用现金
     *getChannelAvailableCash
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getChannelAvailableCash(Map<String, Object> param){
        Date sysDate = DateUtil.platDateFromCache();
        param.put("sysDate",DateUtil.format(sysDate));
        String calId = (String)param.get("calId");
        if(StringUtil.isNotEmpty(calId)){
            List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(sysDate)),DateUtil.addDay(DateUtil.parse(DateUtil.format(sysDate)), 15),calId);
            Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(sysDate)), 1, holidayList, "1");
            param.put("sysDateT1",DateUtil.format(tradeDay));
        }
        Map<String, Object> cash = restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrInvestBondInfo/getProAvailableCash",param,
                Map.class);
        return new BigDecimal((String) cash.get("cashT2"));
    }

    /**
     *
     * 查询资产管理人
     * <功能详细描述>
     * @param issuer
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, String> selectManeger(String issuer){
        String id = restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrFund/selectManeger?issuer="+
                issuer,String.class);
        Map<String, String> result = new HashMap<>();
        result.put("id", id);
        return result;
    }


    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        EtrFundDto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
            BigDecimal amt = dto.getEntrustAmt();
            if (amt == null || amt.compareTo(BigDecimal.ZERO) <= 0) {
                amt = dto.getEntrustNum();
            }
            if (dto.isImportFlow()) { // 只有在多对多组合提交时，才为true，供弹出用户选择，选择用户备用，让需要上级审批的流程走到这个人这里，不需要的直接结束
                Map<String, Object> param = new HashMap<>();
                param.put(BussinessConstant.是否需要上级审批, true);
                return param;
            }
            try {
//              Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), amt, LoginUserUtil.getUserName(),
//                      BussinessDictCodeList.平安资产类型_基金.getValue(), dto.getAssetType1(), dto.getTradeMarket(), dto.getPs()
//                      // 资产类型改为授权类型 add by renping 2019-08-06  start
//                      , dto.getFinprodId()
//                      // 资产类型改为授权类型 add by renping 2019-08-06  end
//              );
                Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), amt, LoginUserUtil.getUserName(),
                        BussinessDictCodeList.平安资产类型_基金.getValue(), dto.getAssetType1(), dto.getTradeMarket(), dto.getPs()
                        , dto.getFinprodId(), dto.getWorkflowId()
                );
                return commonBusinessService.是否需要上级审批_委托用(params);
            }
            catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        }
        else {
            return null;
        }
    }

    /**
     *
     * 查询标的附属表信息
     * <功能详细描述>
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    public FinProductAdd selectFinProductAdd(String finprodId){
        return finProductAddMapper.selectByPrimaryKey(finprodId);
    }



    /**
     * 查询交易对手名称
     *
     * @return
     */
    public String getCounterName(String counterId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrBondFirstLBidding/getCounterName?counterId="
                        + counterId,
                String.class);
    }

    /**
     * 获取委托列表数据
     *
     * @return
     */
    public List<EtrFundDto> getEntrustListData(String tempList) {
        List<EtrFundDto> etrFundDtoList = new ArrayList<>();
        JSONArray objects = JSONArray.parseArray(tempList);
        for (int i = 0; i < objects.size(); i++ ) {
            EtrFundDto dto = new EtrFundDto();
            String issuer = "";
            JSONArray data = JSONArray.parseArray(((JSONArray)objects.get(i)).toJSONString());
            // 根据模板中固定的下标来复制
            dto.setIndexId(data.get(0).toString().trim());
            // 组合代码
            dto.setPortfolioId(data.get(3).toString().trim());
            // 委托开始日
            dto.setVdate(DateUtil.parse(data.get(1).toString().trim()));
            // 委托结束日
            dto.setMdate(DateUtil.parse(data.get(2).toString().trim()));
            // 资产代码
            dto.setFinprodMarketId(data.get(4).toString().trim());
            if (StringUtil.isNotBlank(data.get(5).toString())) {
                // 分割交易方向，获取code
                String[] s = data.get(5).toString().split(":");
                dto.setPs(s[0]);
            }
            // 委托金额
            if (StringUtil.isNotBlank(data.get(6).toString())) {
                dto.setEntrustAmt(
                        BigDecimal.valueOf(Double.valueOf(data.get(6).toString().trim())));
            }
            // 委托数量
            if (StringUtil.isNotBlank(data.get(7).toString())) {
                dto.setEntrustNum(
                        BigDecimal.valueOf(Double.valueOf(data.get(7).toString().trim())));
            }
            // 委托价格
            if (StringUtil.isNotBlank(data.get(8).toString())) {
                dto.setPrice(BigDecimal.valueOf(Double.valueOf(data.get(8).toString().trim())));
            }
            // 证券账户
            dto.setSecAcctId(data.get(9).toString().trim());
            if (StringUtil.isNotEmpty(dto.getSecAcctId())) {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("initValue", data.get(9).toString().trim());
                map1.put("page", 1);
                map1.put("length", 20);
                map1.put("portfolioId", data.get(3).toString());
                List<SelectOptionDto> secAcctIds = etrStockService.getSecAcctId(map1);
                // 证券账户名称
                dto.setSecAcctIdName(secAcctIds.get(0).getText());
            }
            // 通道
            dto.setChlId(data.get(10).toString().trim());
            try {
                if (StringUtil.isNotEmpty(dto.getChlId())) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("initValue", data.get(10).toString());
                    param.put("page", 1);
                    param.put("length", 20);
                    Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
                            ServiceApplication.COMMON_SERVICE_NAME + "/common/BussChannelCommonService/getChannelId",
                            param, SelectOptionDto.class);
                    // 通道名称
                    dto.setChlIdName(selectOptionDtoPage.getRoot().get(0).getText());
                }
            }
            catch (Exception e) {
                dto.setChlIdName(null);
            }
            // 备注
            dto.setRemark(data.get(11).toString().trim());
            // 转入资产代码，转入资产名称, 转入金融产品代码
            //dto.setChangeToFinprodMarketId(data.get(13).toString().trim());
            if (StringUtil.isNotBlank(data.get(12).toString())) {
                dto.setChangeToFinprodMarketId(data.get(12).toString().trim());
            }
            if(StringUtil.isNotEmpty(dto.getChangeToFinprodMarketId())) {
                Map<String, Object> mapcTfinprod = new HashMap<String, Object>();
                mapcTfinprod.put("initValue", dto.getChangeToFinprodMarketId());
                Page<SelectOptionDto> page = getSelectFinprodMarketId(mapcTfinprod);
                if (StringUtil.isNotEmpty(page) && StringUtil.isNotEmpty(page.getRoot())) {
                    if (page.getRoot().size() == 1 ) {
                        SelectOptionDto d = page.getRoot().get(0);
                        dto.setChangeToFinprodName(d.getText());
                        if (StringUtil.isNotEmpty(d.getObject())) {
                            Map<String, Object> m = (Map<String, Object>)d.getObject();
                            dto.setrFinprodId((String)m.get("finprodId"));
                        }
                    }else if(page.getRoot().size()>1) {
                        for(SelectOptionDto d:page.getRoot() ) {
                            if(StringUtil.equals(d.getId(), dto.getChangeToFinprodMarketId())) {
                                dto.setChangeToFinprodName(d.getText());
                                if (StringUtil.isNotEmpty(d.getObject())) {
                                    Map<String, Object> m = (Map<String, Object>)d.getObject();
                                    dto.setrFinprodId((String)m.get("finprodId"));
                                }
                            }
                        }
                    }
                }
            }
            // 根据资产代码查出
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("initValue", dto.getFinprodMarketId());
            Page<SelectOptionDto> page = getSelectFinprodMarketId(map);
            if (StringUtil.isNotEmpty(page) && StringUtil.isNotEmpty(page.getRoot())) {
                if (page.getRoot().size() > 0) {
                    SelectOptionDto d = page.getRoot().get(0);
                    dto.setFinprodName(d.getText());
                    if (StringUtil.isNotEmpty(d.getObject())) {
                        Map<String, Object> m = (Map<String, Object>)d.getObject();
                        // 市场代码
                        dto.setTradeMarket((String)m.get("tradeMarket"));
                        // 二级分类
                        dto.setAssetType1((String)m.get("assetType1"));
                        // 三级分类
                        dto.setAssetType10((String)m.get("assetType10"));
                        // 四级分类
                        dto.setAssetType11((String)m.get("assetType11"));
                        // 资产代码
                        dto.setFinprodId((String)m.get("finprodId"));
                        // 币种
                        dto.setCcy((String)m.get("ccy"));
                        issuer = (String)m.get("issuer");
                        try {
                            // 交易所基金简称
                            dto.setTrmFundAbbr((String)m.get("trmFundAbbr"));
                        }
                        catch (Exception e) {
                            dto.setTrmFundAbbr(null);
                        }
                        try {
                            // 交易所基金类型
                            dto.setTrmFundType((String)m.get("trmFundType"));
                        }
                        catch (Exception e) {
                            dto.setTrmFundType(null);
                        }
                    }
                }
            }
            // 交易对手
            // 当交易场所为上海证券交易所,交易方向为买入或卖出，交易对手为上海证券交易所
            if (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                    && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()))) {
                if(!StringUtil.isNotBlank(data.get(11).toString().trim())) {
                    dto.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHANGHAI);
                    dto.setCounterIdName(getCounterName(dto.getCounterId()));
                }else {
                    dto.setCounterId(data.get(11).toString().trim());
                    String counterName = getCounterName(data.get(11).toString().trim());
                    dto.setCounterIdName(counterName);
                }
            }
            // 当交易场所为深圳证券交易所,交易方向为买入或卖出，交易对手为深圳证券交易所
            else if (StringUtil.equals(dto.getTradeMarket(),
                    DictCodeListExt1.交易场所_深圳证券交易所.getValue())
                    && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()))) {
                if(!StringUtil.isNotBlank(data.get(11).toString().trim())) {
                    dto.setCounterId(BussinessConstant.COUNTER_PARTY_ID_SHENZHEN);
                    dto.setCounterIdName(getCounterName(dto.getCounterId()));
                }else {
                    dto.setCounterId(data.get(11).toString().trim());
                    String counterName = getCounterName(data.get(11).toString().trim());
                    dto.setCounterIdName(counterName);
                }
            }
            // 当交易场所为上海证券交易所和深圳证券交易所，交易方向不为买入或卖出，交易对手为自动带出的管理员
            else if (StringUtil.equals(dto.getTradeMarket(),
                    DictCodeListExt1.交易场所_上海证券交易所.getValue())
                    || StringUtil.equals(dto.getTradeMarket(),
                    DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                if (!(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                        || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()))) {
                    Map<String, Object> param = new HashMap<String, Object>();
                    param.put("isAssteManager", DictCodeListExt1.是否_是.getValue());
                    if (StringUtil.isNotEmpty(issuer)) {
                        Map<String, String> issuers = selectManeger(issuer);
                        param.put("initValue", issuers.get("id"));
                    }
                    param.put("ps", dto.getPs());
                    param.put("innerTrade", null);
                    param.put("counterType", null);
                    param.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
                    param.put("tradeMarket", dto.getTradeMarket());
                    try {
                        Page<SelectOptionDto> p = restTemplate.postForPage(
                                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/EntrustCommonPersonSelect/getCounterIdDepat",
                                param, SelectOptionDto.class);
                        dto.setCounterId(p.getRoot().get(0).getId());
                        dto.setCounterIdName(p.getRoot().get(0).getText());
                    }
                    catch (Exception e) {
                        dto.setCounterId(null);
                        dto.setCounterIdName(null);
                    }
                }
            }
            else {
                dto.setCounterId(data.get(11).toString().trim());
                String counterName = getCounterName(data.get(11).toString().trim());
                dto.setCounterIdName(counterName);
            }
            // 当证券账户有值时，查询交易所资金账户可用现金
            if (StringUtil.isNotEmpty(dto.getSecAcctId())) {
                String vdate = DateUtil.format(dto.getVdate(), "yyyyMMdd");
                String calId = null;
                Map<String, Object> map1 = new HashMap<String, Object>();
                map1.put("vdate", vdate);
                map1.put("calId", calId);
                map1.put("portfolioId", dto.getPortfolioId());
                map1.put("ccy", dto.getCcy());
                Map<String, Object> map2 = getProAvailableCash(map1);
                try {
                    if (StringUtil.isNotEmpty(map2.get("cashT4"))) {
                        dto.setTradeMarketCash(
                                BigDecimal.valueOf(Double.valueOf(map2.get("cashT4").toString())));
                    }
                    else {
                        dto.setTradeMarketCash(BigDecimal.ZERO);
                    }
                }
                catch (Exception e) {
                    dto.setTradeMarketCash(BigDecimal.ZERO);
                }
            }
            // 当通道有值时，查询通道可用现金
            if (StringUtil.isNotBlank(data.get(10).toString())
                    && StringUtil.isNotEmpty(dto.getPortfolioId())
                    && StringUtil.isNotEmpty(dto.getCcy())) {
                Map<String, Object> mapChlId = new HashMap<String, Object>();
                mapChlId.put("portfolioId", dto.getPortfolioId());
                mapChlId.put("chlFinprodId", dto.getChlId());
                mapChlId.put("ccy", dto.getCcy());
                if (StringUtil.isNotEmpty(getChannelAvailableCash(mapChlId))) {
                    dto.setChlIdCash(getChannelAvailableCash(mapChlId));
                }
                else {
                    dto.setChlIdCash(BigDecimal.ZERO);
                }

            }
            // 指令可用现金
            if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_认购.getValue())) {
                Map<String, Object> map1 = new HashMap<String, Object>();
                String vdate = DateUtil.format(dto.getVdate(), "yyyyMMdd");
                String calId = null;
                map1.put("vdate", vdate);
                map1.put("calId", calId);
                map1.put("portfolioId", dto.getPortfolioId());
                map1.put("ccy", dto.getCcy());
                Map<String, Object> map2 = getProAvailableCash(map1);
                if (StringUtil.isNotEmpty(map2.get("cashT"))) {
                    dto.setProAvailableCash(
                            BigDecimal.valueOf(Double.valueOf(map2.get("cashT").toString())));
                }
                else {
                    dto.setProAvailableCash(BigDecimal.ZERO);
                }
            }else {
                dto.setProAvailableCash(null);
            }
            // 指令可用持仓
            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_转换申请.getValue())) {
                PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
                ptlSecPositionFlowDto.setCcy(dto.getCcy());
                ptlSecPositionFlowDto.setFinprodId(dto.getFinprodId());
                // ZGZGST-1879:修改证券管理账号赋值 20210902 fuyongsheng edit start
                ptlSecPositionFlowDto.setSecAcctId(StringUtil.isEmpty(dto.getSecAcctId()) ? dto.getChlId() : dto.getSecAcctId());
                // ZGZGST-1879:修改证券管理账号赋值 20210902 fuyongsheng edit end
                ptlSecPositionFlowDto.setPortfolioId(dto.getPortfolioId());
                dto.setAssetAvailablePosition(getAssetAvailablePosition(ptlSecPositionFlowDto));
            }else {
                dto.setAssetAvailablePosition(null);
            }
            dto.setBussEtrInvestPersonInfoDto(new BussEtrInvestPersonInfoDto());
            // 投资经理
            dto.getBussEtrInvestPersonInfoDto().setInvManagerId(dto.getCreateUser());
            // 所属部门
            dto.getBussEtrInvestPersonInfoDto().setRoom(dto.getCreateDept());
            // 交易对手
            dto.getBussEtrInvestPersonInfoDto().setCounterId(dto.getCounterId());
            etrFundDtoList.add(dto);
        }
        return etrFundDtoList;
    }

    /**
     * 验证导入的数据
     *
     * @param fileName 导入的数据
     * @return wr 报错信息与正确的数据
     */
    public AjaxResult checkImportData(String fileName) {
        List<List<String>> objects = ExcelUtil.getTempList(fileName);

        int totalData = 0;
        for (int i = 1; i < objects.size(); i++ ) {
            List<String> rowData = objects.get(i);
            totalData++ ;
        }
        AjaxResult wr = new AjaxResult();
        // check导入数据是否大于100条
        if (totalData > 100) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("errorMsg", getMessage("ETR_E_0021"));
            wr.setData(resultMap);
        }
        if (!StringUtil.isNotEmpty(wr.getData())) {
            String totalMsg = "";
            List<List<String>> jsonArrays = new ArrayList<>();
            // 失败条数
            int errorNum = 0;
            for (int i = 1; i < objects.size(); i++ ) {
                String confirmMsg = "";
                // excel的行号
                String lineNo = String.valueOf(i + 1);
                List<String> rowData = objects.get(i);
                boolean isEntrust = true;
                if (isEntrust) {
                    // 委托开始日非空
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                            etrCommonService.checkIsBlank(rowData.get(1).toString(),
                                    "EtrFund001.entrustVDate", lineNo));
                    // 委托开始日格式check
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                            etrCommonService.checkIsDate(rowData.get(1).toString(),
                                    "EtrFund001.entrustVDate", lineNo));
                    // 委托终止日非空
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                            etrCommonService.checkIsBlank(rowData.get(2).toString(),
                                    "EtrFund001.entrustMDate", lineNo));
                    // 委托终止日格式check
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                            etrCommonService.checkIsDate(rowData.get(2).toString(),
                                    "EtrFund001.entrustMDate", lineNo));
                    // 委托开始、终止日是否是平台日check
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                            EtrCommonService.checkIsPlateDate(rowData.get(1).toString(), rowData.get(2).toString(),lineNo));
                    // 组合代码非空
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                            etrCommonService.checkIsBlank(rowData.get(3).toString(),
                                    "EtrFund001.portfolioId", lineNo));
                    // 组合代码check
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                            etrCommonService.checkPortfolioIdExist(rowData.get(3).toString(),
                                    "EtrFund001.portfolioId", lineNo));
                    // 资产代码
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                            etrCommonService.checkIsBlank(rowData.get(4).toString(),
                                    "EtrFund001.finprodId", lineNo));
                    // 交易方向非空
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                            etrCommonService.checkIsBlank(rowData.get(5).toString(), "EtrFund001.ps",
                                    lineNo));
                    // 委托份额
                    String entrustNum = rowData.get(7).toString();
                    if (StringUtil.isNotBlank(rowData.get(5).toString())) {
                        String[] s1 = rowData.get(5).toString().trim().split(":");
                        // 如果交易方向为买入或卖出，委托份额必填否则不可填
                        if (!(StringUtil.equals(s1[0], DictCodeList.交易方向_申购.getValue())
                                || StringUtil.equals(s1[0], DictCodeListExt2.交易方向_认购.getValue()))) {
                            // 委托份额非空
                            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                    etrCommonService.checkIsBlank(entrustNum, "EtrStock001.entrustNum",
                                            lineNo));
                            // 委托份额是数值
                            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                    etrCommonService.checkIsNumber(entrustNum,
                                            "EtrStock001.entrustNum", lineNo));
                            // 委托份额是大于0
                            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                    etrCommonService.checkIsGtZero(entrustNum,
                                            "EtrStock001.entrustNum", lineNo));
                        }
                        else {
                            if (StringUtil.isNotBlank(entrustNum)) {
                                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                        getMessage("ETR_FU_005", lineNo));
                            }
                        }
                        // 委托金额
                        String entrustAmt = rowData.get(6).toString();
                        // 当交易方向是卖出,赎回，转换申请时，委托金额不可填，其他情况必填
                        if (StringUtil.equals(s1[0], DictCodeList.交易方向_卖出.getValue())
                                || StringUtil.equals(s1[0], DictCodeList.交易方向_赎回.getValue())
                                || StringUtil.equals(s1[0], DictCodeListExt2.交易方向_转换申请.getValue())) {
                            if (StringUtil.isNotBlank(entrustAmt)) {
                                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                        getMessage("ETR_FU_006", lineNo));
                            }
                        }
                        else {
                            // 委托金额非空
                            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                    etrCommonService.checkIsBlank(entrustAmt, "EtrFund001.entrustAmt",
                                            lineNo));
                            // 委托金额是数值
                            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                    etrCommonService.checkIsNumber(entrustAmt, "EtrFund001.entrustAmt",
                                            lineNo));
                            // 委托金额是大于0
                            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                    etrCommonService.checkIsGtZero(entrustAmt, "EtrFund001.entrustAmt",
                                            lineNo));
                        }
                    }
                    // check资产代码存在 根据资产代码查询交易场所，用做check委托价格和证券账户的条件
                    Map<String, Object> mapFinprodId = new HashMap<String, Object>();
                    Map<String, Object> m = new HashMap<String, Object>();
                    mapFinprodId.put("initValue", rowData.get(4).toString());
                    mapFinprodId.put("page", 1);
                    mapFinprodId.put("length", 20);
                    Page<SelectOptionDto> page = getSelectFinprodMarketId(mapFinprodId);
                    if (!page.getRoot().isEmpty()) {
                        if (StringUtil.isNotEmpty(page) && StringUtil.isNotEmpty(page.getRoot())) {
                            if (page.getRoot().size() > 0) {
                                SelectOptionDto d = page.getRoot().get(0);
                                if (StringUtil.isNotEmpty(d.getObject())) {
                                    m = (Map<String, Object>)d.getObject();
                                }
                            }
                        }
                        // 委托价格当是上海证券交易所和深圳证券交易所而且交易方向为买入或卖出时必填，其他交易场所不可填
                        if (StringUtil.equals((String)m.get("tradeMarket"),
                                DictCodeListExt1.交易场所_上海证券交易所.getValue())
                                || StringUtil.equals((String)m.get("tradeMarket"),
                                DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                            if (StringUtil.isNotBlank(rowData.get(5).toString().trim())) {
                                String[] s1 = rowData.get(5).toString().trim().split(":");
                                if (StringUtil.equals(s1[0], DictCodeList.交易方向_卖出.getValue())
                                        || StringUtil.equals(s1[0], DictCodeList.交易方向_买入.getValue())) {
                                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                            etrCommonService.checkIsBlank(rowData.get(8).toString(),
                                                    "EtrFund002.price", lineNo));
                                }
                            }
                        }
                        else {
                            if (StringUtil.isNotBlank(rowData.get(8).toString())) {
                                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                        getMessage("ETR_FU_001", lineNo));
                            }
                        }
                        // 证券账户当是上海证券交易所和深圳证券交易所时必填，其他交易场所不可填，若是通道有值证券账户不可填
                        if (StringUtil.equals((String)m.get("tradeMarket"),
                                DictCodeListExt1.交易场所_上海证券交易所.getValue())
                                || StringUtil.equals((String)m.get("tradeMarket"),
                                DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                            if (StringUtil.isNotBlank(rowData.get(9).toString())) {
                                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                        getMessage("ETR_FU_003", lineNo,
                                                getMessage("invest.channelId")));
                            }
                        }
                        else {
                            if (StringUtil.isNotBlank(rowData.get(9).toString())) {
                                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                        getMessage("ETR_FU_004", lineNo));
                            }
                        }

                        // 交易对手的check
                        // 当交易场所为上海证券交易所或深圳证券交易所时，交易对手带出
                        if (StringUtil.equals((String)m.get("tradeMarket"),
                                DictCodeListExt1.交易场所_上海证券交易所.getValue())
                                || StringUtil.equals((String)m.get("tradeMarket"),
                                DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                            String[] s1 = rowData.get(5).toString().trim().split(":");
                            if (!(StringUtil.equals(s1[0], DictCodeList.交易方向_卖出.getValue())
                                    || StringUtil.equals(s1[0], DictCodeList.交易方向_买入.getValue()))) {
                                if (StringUtil.isNotBlank(rowData.get(10).toString())) {
                                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                            getMessage("ETR_FU_007", lineNo));
                                }
                            }
                            else {
                                if (StringUtil.isNotBlank(rowData.get(10).toString())) {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("initValue", rowData.get(10).toString());
                                    map.put("page", 1);
                                    map.put("length", 20);
                                    map.put("counterType", DictCodeListExt1.交易对手类型_内部交易对手.getValue());
                                    Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
                                            ServiceApplication.ENTRUST_SERVICE_NAME + "/common/EntrustCommonPersonSelect/getCounterId",
                                            map, SelectOptionDto.class);
                                    if (selectOptionDtoPage.getRoot().isEmpty()) {
                                        confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                                getMessage("ETR_E_0017",
                                                        getMessage("etrInvestPersonInfo.counterId"),
                                                        lineNo));
                                    }
                                }
                            }
                        }
                        else {
                            // 其他的交易场所，交易对手模板填入，check交易对手是否存在
                            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                    etrCommonService.checkIsBlank(rowData.get(10).toString(),
                                            "etrInvestPersonInfo.counterId", lineNo));
                            if (StringUtil.isNotBlank(rowData.get(10).toString())) {
                                if(StringUtil.isNotBlank(rowData.get(5).toString())) {
                                    String[] s1 = rowData.get(5).toString().trim().split(":");
                                    if(StringUtil.equals(s1[0], DictCodeList.交易方向_卖出.getValue())
                                            || StringUtil.equals(s1[0], DictCodeList.交易方向_买入.getValue())) {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("initValue", rowData.get(10).toString());
                                        map.put("page", 1);
                                        map.put("length", 20);
                                        map.put("counterType", DictCodeListExt1.交易对手类型_内部交易对手.getValue());
                                        Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
                                                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/EntrustCommonPersonSelect/getCounterId",
                                                map, SelectOptionDto.class);
                                        if (selectOptionDtoPage.getRoot().isEmpty()) {
                                            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                                    getMessage("ETR_E_0017",
                                                            getMessage("etrInvestPersonInfo.counterId"), lineNo));
                                        }
                                    }else if(StringUtil.equals(s1[0], DictCodeList.交易方向_申购.getValue())
                                            || StringUtil.equals(s1[0], DictCodeList.交易方向_赎回.getValue())) {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("initValue", rowData.get(10).toString());
                                        map.put("page", 1);
                                        map.put("length", 20);
                                        map.put("counterType", null);
                                        map.put("isAssteManager",DictCodeListExt1.是否_是.getValue());
                                        Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
                                                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/EntrustCommonPersonSelect/getCounterIdDepat",
                                                map, SelectOptionDto.class);
                                        if (selectOptionDtoPage.getRoot().isEmpty()) {
                                            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                                    getMessage("ETR_E_0017",
                                                            getMessage("etrInvestPersonInfo.counterId"), lineNo));
                                        }
                                    }else {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("initValue", rowData.get(10).toString());
                                        map.put("page", 1);
                                        map.put("length", 20);
                                        map.put("counterType", null);
                                        Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
                                                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/EntrustCommonPersonSelect/getCounterIdDepat",
                                                map, SelectOptionDto.class);
                                        if (selectOptionDtoPage.getRoot().isEmpty()) {
                                            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                                    getMessage("ETR_E_0017",
                                                            getMessage("etrInvestPersonInfo.counterId"), lineNo));
                                        }
                                    }

                                }
                            }
                        }
                        // 交易场所为交易所时，通道和证券托管户二选一必填
                        if(StringUtil.equals((String)m.get("tradeMarket"),
                                DictCodeListExt1.交易场所_上海证券交易所.getValue())
                                || StringUtil.equals((String)m.get("tradeMarket"),
                                DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                            if ((StringUtil.isBlank(rowData.get(9).toString())
                                    && StringUtil.isBlank(rowData.get(10).toString()))
                                    || (StringUtil.isNotBlank(rowData.get(9).toString())
                                    && StringUtil.isNotBlank(rowData.get(10).toString()))) {
                                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                        getMessage("ETR_E_0020", getMessage("invest.channelId"),
                                                getMessage("EtrStock002.secAcctId"), lineNo));
                            }
                        }
                    }
                    else {
                        // 资产代码不存在，提示信息
                        confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                getMessage("ETR_FU_009", lineNo));
                    }
                    //选择内部交易对手时，不能选择通道
                    if(StringUtil.isNotBlank(rowData.get(10).toString())){
                        Map<String, Object> map01 = new HashMap<>();
                        map01.put("initValue", rowData.get(10).toString());
                        map01.put("page", 1);
                        map01.put("length", 20);
                        map01.put("counterType", DictCodeListExt1.交易对手类型_内部交易对手.getValue());
                        Page<SelectOptionDto> selectOptionDtoPage1 = restTemplate.postForPage(
                                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/EntrustCommonPersonSelect/getCounterId",
                                map01, SelectOptionDto.class);
                        if (selectOptionDtoPage1.getRoot().isEmpty() && selectOptionDtoPage1.getRoot().size() >=1) {
                            if(StringUtil.isNotBlank(rowData.get(10).toString())) {
                                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                                        getMessage("ETR_FU_0010",
                                                getMessage("invest.channelId"), lineNo));
                            }
                        }
                    }
                    // 证券账户存在
                    if (StringUtil.isNotBlank(rowData.get(9).toString())
                            && StringUtil.isNotBlank(rowData.get(3).toString())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("initValue", rowData.get(9).toString());
                        map.put("page", 1);
                        map.put("length", 20);
                        map.put("portfolioId", rowData.get(3).toString());
                        List<SelectOptionDto> secAcctIds = etrStockService.getSecAcctId(map);
                        if (secAcctIds.isEmpty()) {
                            confirmMsg = etrCommonService.StringAddByBr(confirmMsg, getMessage(
                                    "ETR_E_0017", getMessage("EtrStock002.secAcctId"), lineNo));
                        }
                    }
                    // 通道存在
//                    通道校验去除
//                    if (StringUtil.isNotBlank(rowData.get(10).toString())) {
//                        Map<String, Object> param = new HashMap<>();
//                        param.put("page", 1);
//                        param.put("length", 20);
//                        if(StringUtil.isNotEmpty(rowData.get(10).toString())){
//                            param.put("initValue", "F28_"+rowData.get(10).toString());
//                        }
//                        Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
//                            ServiceApplication.COMMON_SERVICE_NAME + "/common/BussChannelCommonService/getChannelId",
//                            param, SelectOptionDto.class);
//                        if (selectOptionDtoPage.getRoot().isEmpty()) {
//                            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
//                                getMessage("ETR_E_0017", getMessage("invest.channelId"), lineNo));
//                        }
//                    }
                }
                if (StringUtil.isBlank(confirmMsg)) {
                    jsonArrays.add(rowData);
                }
                else {
                    // 导入失败条数统计
                    totalMsg = etrCommonService.StringAddByBr(totalMsg, confirmMsg);
                    if (isEntrust) {
                        errorNum++ ;
                    }
                }
            }
            totalMsg = etrCommonService.StringAddByBr(totalMsg,
                    getMessage("ETR_C_0001", String.valueOf(errorNum)));
            Map<String, Object> resultMap = new HashMap<>();
            if (StringUtil.isNotBlank(totalMsg)) {
                resultMap.put("confirmMsg", totalMsg);
            }
            resultMap.put("correctData", JSONArray.toJSONString(jsonArrays));
            wr.setData(resultMap);
        }
        return wr;
    }


    public List<String> getSameNameCounterId(Map<String, Object> param) {

        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME+"/EtrFund/findSameNameCounterId", param,
                List.class);

    }
}
