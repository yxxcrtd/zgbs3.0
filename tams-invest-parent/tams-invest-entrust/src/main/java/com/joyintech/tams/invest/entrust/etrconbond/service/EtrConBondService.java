/*
 * 文件名：EtrBondFirstBiddingService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrconbond.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.system.base.entity.SysUser;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etrconbond.dto.EtrConBondDto;
import com.joyintech.tams.invest.entrust.etrwtspotex.dto.EtrInvestPersonInfoDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 可转债委托处理详细 〈功能详细描述〉
 *
 * @author gejiaao
 * @version 1.0 2019年10月23日
 * @see EtrConBondService
 * @since 1.0
 */
@Service
public class EtrConBondService extends CommonExportService<EtrConBondDto> implements BaseProcessService<EtrConBondDto> {

    /**
     * 日期相关
     */
    @Autowired
    private DateCalService dateCalService;
    /**
     * 委托共通
     */
    @Autowired
    private EtrCommonService etrCommonService;

    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    /**
     * 获取资产代码的联想文本框
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodId(Map<String, Object> params) {
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME
                + "/EtrConBond/getFinprodId", params, SelectOptionDto.class);
    }

    /**
     * 获取新建界面的资产代码联想下拉框
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodIdForAdd(Map<String, Object> param) {
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrConBond/getFinprodIdForAdd"
                , param, SelectOptionDto.class);

    }

    /**
     * 业务处理
     */
    @Override
    public void doComplete(EtrConBondDto dto, OperationType operationType) {
        dto.setOperateType(operationType.toString());
        basRiskMathCommService.insert(dto.getRiskTrialRetDto(), dto.getEntrustId());
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrConBond/doComplete",dto,
                Integer.class);
    }

    @Override
    public void doInsert(EtrConBondDto entity, OperationType type) {
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrConBond/doInsert",entity,
                Integer.class);
    }

    @Override
    public void doUpdate(EtrConBondDto entity) {
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrConBond/doUpdate",entity,
                Integer.class);
    }

    @Override
    public void doBack(EtrConBondDto entity) {
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrConBond/doBack",entity,
                Integer.class);
    }

    @Override
    public void doDelete(String workflowId) {
        Map<String,Object> param = new HashMap<>();
        param.put("workflowId",workflowId);
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrConBond/doDelete",param,
                Integer.class);
    }

    /**
     * 分页查询可转债委托信息
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<EtrConBondDto> selectForPage(Map<String, Object> param) {
        param.put("userId", SecurityUtil.getLoginUser().getUserName());
        //配置单位 edit by yeming 20210802 start
        Page<EtrConBondDto> page = restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrConBond/findPage",
                param, EtrConBondDto.class);
        List<SysUser> userList = restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EntrustCommon/getAllUserList",
                param, SysUser.class);
        for(EtrConBondDto dto:page.getRoot()){
            if(dto.getPurchBondTotal() != null) {
                dto.setPurchBondTotal(dto.getPurchBondTotal().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
            }
            if(dto.getWinlotteryAmt() != null) {
                dto.setWinlotteryAmt(dto.getWinlotteryAmt().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
            }
            if(StringUtil.isNotEmpty(dto.getNextUser())){
                String sysUsers = userList.stream().filter(x-> Arrays.asList(dto.getNextUser().split(",")).contains(x.getUserName()))
                        .map(SysUser::getRealName)
                        .collect(Collectors.joining(",","",""));
                dto.setNextUser(sysUsers);
            }
        }
        return page;
        //配置单位 edit by yeming 20210802 end
//        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrConBond/findPage",
//            param, EtrConBondDto.class);
    }

    /**
     * 校验方法
     *
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtrConBondDto dto) {
        dto.setCloseWay(BussinessDictCodeList.委托关闭方式__人工.getValue());
        return restTemplate.postForList(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrConBond/check", dto,
                ValidMessage.class);
    }

    public String getCounterIdByFinprodId(String finprodId){
        return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME +
                "/EtrConBond/getCounterIdByFinprodId", finprodId, String.class);
    }
    /**
     * 组合可用现金查询
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getProAvailableCash(Map<String, Object> param) {
        Date sysDate = DateUtil.platDateFromCache();
        param.put("sysDate",DateUtil.format(sysDate));
        String calId = (String)param.get("calId");
        if(StringUtil.isNotEmpty(calId)){
            List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(sysDate)),DateUtil.addDay(DateUtil.parse(DateUtil.format(sysDate)), 15),calId);
            Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(sysDate)), 1, holidayList, "1");
            param.put("sysDateT1",DateUtil.format(tradeDay));
        }
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrConBond/getProAvailableCash",param,
                Map.class);
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
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrConBond/delete?entrustId="+entrustId,
                boolean.class);
    }


    /**
     * 修改/双击查看详情查询信息
     *
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public EtrConBondDto selectByKey(String entrustId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                + "/EtrConBond/selectByKey?entrustId=" + entrustId, EtrConBondDto.class);
    }


    /**
     * 撤回委托 <功能详细描述>
     *
     * @param dto 实体类
     * @return boolean
     * @see
     * @since 1.0
     */
    public boolean recallDelegate(EtrConBondDto dto) {
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrConBond/recall", dto,
                boolean.class);
    }
    /**
     * 关闭委托
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    public boolean closeDelegate(String entrustId,String closeWay) {
        return restTemplate.getForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrConBond/close?entrustId="+entrustId+"&closeWay="+closeWay,
                boolean.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<EtrConBondDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
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
        exportParam.config("/com/joyintech/tams/invest/entrust/etrconbond/conf/export_conf.xml")
                .dataSource(DataSourceType.MASERT);
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
        Map<String, Object> cash =  etrCommonService.getProAvailableCash(param);
        return new BigDecimal(Double.valueOf(cash.get("cashT2").toString()));
    }

    /**
     * 验证子组合是否可以申购
     * @param etrConBondDto
     * @return
     */
    public Boolean checkChildPortfolio(EtrConBondDto etrConBondDto){
        return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrConBond/checkChildPortfolio",
                etrConBondDto, boolean.class);
    }

    /**
     * 为调授信 封装信息
     * @param etrConBondDto
     * @return
     */
    public TrdProductDeal getTrdProductDeal(EtrConBondDto etrConBondDto) throws InvocationTargetException, IllegalAccessException {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyProperties(trdProductDeal, etrConBondDto);
        //如果画面交易对手未填，则取债券标的的发行人
        if (StringUtil.isNotEmpty(etrConBondDto.getEtrInvestPersonInfo().getCounterId())){
            trdProductDeal.setCounterId(etrConBondDto.getEtrInvestPersonInfo().getCounterId());
        }else {
            String counterId = this.getCounterIdByFinprodId(etrConBondDto.getFinprodId());
            trdProductDeal.setCounterId(counterId);
        }
        trdProductDeal.setShareAmt(etrConBondDto.getPurchBondTotal().divide(BigDecimal.valueOf(100),2));
        trdProductDeal.setCpriceAmt(etrConBondDto.getPurchBondTotal());
        trdProductDeal.setFpriceAmt(etrConBondDto.getPurchBondTotal());
        trdProductDeal.setPrinAmt(etrConBondDto.getPurchBondTotal());
        trdProductDeal.setIntAmt(BigDecimal.ZERO);
        trdProductDeal.setTradeAmt(etrConBondDto.getPurchBondTotal());
        trdProductDeal.setTradeDate(etrConBondDto.getEntrustDate());
        return trdProductDeal;
    }
    // add by gaopeng 20191219 多组合多资产导入 start
    /**
     * 获取委托列表数据
     *
     * @return
     */
    public EtrConBondDto getEntrustListData(String tempList) {
        EtrConBondDto dto = new EtrConBondDto();
        List<EtrConBondDto> etrEntrustDtos = new ArrayList<>();
        JSONArray objects = JSONArray.parseArray(tempList);
        for (int i = 0; i < objects.size(); i++) {
            EtrConBondDto etrConBondDto = new EtrConBondDto();
            JSONArray data = JSONArray.parseArray(((JSONArray) objects.get(i)).toJSONString());
            //根据模板中固定的下标来赋值
            etrConBondDto.setIndexId(data.get(0).toString());
            //申购日期
            etrConBondDto.setPurchDate(DateUtil.parse(data.get(1).toString()));
            //委托日期
            etrConBondDto.setEntrustDate(DateUtil.parse(data.get(1).toString()));
            //组合代码
            etrConBondDto.setPortfolioId(data.get(2).toString().trim());
            //金融产品代码
            etrConBondDto.setFinprodMarketId(data.get(3).toString().trim());
            //根据资产代码查出联动值
            Map<String, Object> map = new HashMap<>();
            map.put("initValue", data.get(3).toString());
            map.put("page", 1);
            map.put("length", 20);
            Page<SelectOptionDto> finprodIds = getFinprodIdForAdd(map);
            SelectOptionDto selectOptionDto = finprodIds.getRoot().get(0);
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(selectOptionDto.getObject()));
            etrConBondDto.setPs(DictCodeList.交易方向_申购.getValue());
            //资产代码
            etrConBondDto.setFinprodId(jsonObject.get("finprodId").toString());
            //交易场所
            etrConBondDto.setTradeMarket(jsonObject.get("tradeMarket").toString());
            //资产名称
            etrConBondDto.setFinprodName(jsonObject.get("finprodName").toString());
            //债券一级类型
            etrConBondDto.setAssetType1(jsonObject.get("assetType1").toString());
            //币种
            etrConBondDto.setCcy(data.get(4).toString().split(":")[0]);
           /* //申购方式
            etrConBondDto.setPurchModle(data.get(5).toString().split(":")[0]);*/
            //申购类型
            etrConBondDto.setPurchType(data.get(5).toString().split(":")[0]);
            //申购券面总额
            etrConBondDto.setPurchBondTotal(new BigDecimal((data.get(6).toString())));
            //投资目的
            String invAim = data.get(7).toString().trim();
            etrConBondDto.setInvAim(invAim.split(":")[0]);
            Map<String, Object> param = new HashMap<>();
            // 证券账户
            if (StringUtil.isNotBlank(data.get(8).toString())) {
                etrConBondDto.setSecAcctId(data.get(8).toString());
                param = new HashMap<>();
                param.put("initValue", data.get(8).toString());
                param.put("portfolioId", data.get(2).toString());
                param.put("tradeMarket", jsonObject.get("tradeMarket").toString());
                param.put("page", 1);
                param.put("length", 20);
                List<SuggestDto> selectOptionDtoList = restTemplate.postForList(
                        ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getSecManageAcctId", param, SuggestDto.class);
                if (!StringUtil.isEmptyList(selectOptionDtoList)) {
                    etrConBondDto.setSecAcctName(selectOptionDtoList.get(0).getName());
                }
            }
            // 通道
            if (StringUtil.isNotBlank(data.get(9).toString())) {
                etrConBondDto.setChlId(data.get(9).toString());
                param = new HashMap<>();
                param.put("initValue", data.get(9).toString());
                param.put("page", 1);
                param.put("length", 20);
                Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
                        ServiceApplication.COMMON_SERVICE_NAME + "/common/BussChannelCommonService/getChannelId",
                        param, SelectOptionDto.class);
                if (!StringUtil.isEmptyList(selectOptionDtoPage.getRoot())) {
                    etrConBondDto.setChlIdName(selectOptionDtoPage.getRoot().get(0).getText());
                }
            }
            // 备注
            etrConBondDto.setRemark(data.get(10).toString());
            param.put("portfolioId", etrConBondDto.getPortfolioId());
            param.put("ccy", etrConBondDto.getCcy());
            param.put("vdate", etrConBondDto.getPurchDate());
            param.put("calId", etrConBondDto.getCalendarId());
            param.put("manageId", etrConBondDto.getSecAcctId());
            param.put("chlId", etrConBondDto.getChlId());
            Map<String, Object> cashMap = this.getProAvailableCash(param);
            // 指令可用现金
            if (cashMap != null && cashMap.get("cashT") != null) {
                etrConBondDto.setProAvailableCash(
                        new BigDecimal(cashMap.get("cashT").toString()));
            }
            else {
                etrConBondDto.setProAvailableCash(BigDecimal.ZERO);
            }
            //通道可用现金
            if (cashMap != null && cashMap.get("cashT2") != null) {
                etrConBondDto.setChannelAvailableCash(
                        new BigDecimal(cashMap.get("cashT2").toString()));
            }
            else {
                etrConBondDto.setChannelAvailableCash(BigDecimal.ZERO);
            }
            etrEntrustDtos.add(etrConBondDto);
        }
        dto.setSelectedItem(etrEntrustDtos);
        return dto;
    }

    /**
     * 验证导入的数据
     * @param fileName 文件路径
     * @return
     */
    public AjaxResult checkImportData(String fileName) {
        List<List<String>> objects = ExcelUtil.getTempList(fileName);
        if (objects.size() - 1 > 100) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("errorMsg", getMessage("ETR_E_0021"));
            AjaxResult wr = new AjaxResult();
            wr.setData(resultMap);
            return wr;
        }

        String totalMsg = "";
        JSONArray jsonArrays = new JSONArray();
        // 失败条数
        int errorNum = 0;
        for (int i = 1; i < objects.size(); i++ ) {
            String confirmMsg = "";
            // excel的行号
            String lineNo = String.valueOf(i + 1);
            List<String> rowData = objects.get(i);
            // 申购日期非空
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(rowData.get(1).toString(), "EtrConBond001.purchDate", lineNo));
            // 申购日期格式check
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsDate(rowData.get(1).toString(), "EtrConBond001.purchDate", lineNo));
            // 组合代码非空
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(rowData.get(2).toString(), "EtrConBond002.portfolioId", lineNo));
            // 组合代码check
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkPortfolioIdExist(rowData.get(2).toString(), "EtrConBond002.portfolioId", lineNo));
            //资产代码
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(rowData.get(3).toString(), "EtrConBond003.finprodId", lineNo));
            if (StringUtil.isNotEmpty(rowData.get(3).toString())) {
                Map<String, Object> map = new HashMap<>();
                map.put("initValue", rowData.get(3).toString());
                map.put("page", 1);
                map.put("length", 20);
                Page<SelectOptionDto> finprodIds = getFinprodIdForAdd(map);
                if (finprodIds.getRoot().isEmpty()) {
                    confirmMsg = etrCommonService.StringAddByBr(confirmMsg, getMessage("ETR_E_0017",getMessage("EtrConBond003.finprodId"), lineNo));
                }else {
                    SelectOptionDto selectOptionDto = finprodIds.getRoot().get(0);
                    JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(selectOptionDto.getObject()));
                    String tradeMarket = jsonObject.get("tradeMarket").toString();
                    // 证券账户存在
                    if (StringUtil.isNotBlank(rowData.get(8).toString())
                            && StringUtil.isNotBlank(rowData.get(2).toString())
                            && StringUtil.isNotBlank(tradeMarket)) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("initValue", rowData.get(8).toString());
                        param.put("portfolioId", rowData.get(2).toString());
                        param.put("tradeMarket", tradeMarket);
                        param.put("page", 1);
                        param.put("length", 20);
                        List<SelectOptionDto> selectOptionDtoList = restTemplate.postForList(
                                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrRepo/getSecManageAcctId",
                                param, SelectOptionDto.class);
                        if (StringUtil.isEmptyList(selectOptionDtoList)) {
                            confirmMsg =  etrCommonService.StringAddByBr(confirmMsg, getMessage("ETR_E_0017",
                                    getMessage("EtrConBond003.secAcctId"), lineNo));
                        }
                    }
                }


            }
            // 币种非空
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(rowData.get(4).toString(), "EtrConBond003.ccy", lineNo));
               /* // 申购方式非空
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(rowData.get(5).toString(), "EtrConBond001.purchModle", lineNo));*/
            // 申购类型非空
            confirmMsg =  etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(rowData.get(5).toString(), "EtrConBond001.purchType", lineNo));
            String purchBondTotal = rowData.get(6).toString();
            // 申购券面总额(元)非空
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(purchBondTotal, "EtrConBond001.purchBondTotal", lineNo));
            // 申购券面总额(元)是数值
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsNumber(purchBondTotal, "EtrConBond001.purchBondTotal", lineNo));
            // 申购券面总额(元)是大于0
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsGtZero(purchBondTotal, "EtrConBond001.purchBondTotal", lineNo));
            // 投资目的非空
            confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
                    etrCommonService.checkIsBlank(rowData.get(7).toString(), "EtrConBond003.invAim", lineNo));
            // 证券账户
            // 通道
            if ((StringUtil.isBlank(rowData.get(8).toString ())
                    && StringUtil.isBlank(rowData.get(9).toString()))
                    || (StringUtil.isNotBlank(rowData.get(8).toString())
                    && StringUtil.isNotBlank(rowData.get(9).toString()))) {
                confirmMsg = etrCommonService.StringAddByBr(confirmMsg, getMessage("ETR_E_0020",
                        getMessage( "EtrConBond003.secAcctId"), getMessage("invest.channelId"), lineNo));
            }
            // 通道存在
            if (StringUtil.isNotBlank(rowData.get(9).toString())) {
                Map<String, Object> param = new HashMap<>();
                param.put("initValue", rowData.get(9).toString());
                param.put("page", 1);
                param.put("length", 20);
                Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
                        ServiceApplication.COMMON_SERVICE_NAME + "/common/BussChannelCommonService/getChannelId",
                        param, SelectOptionDto.class);
                if (selectOptionDtoPage.getRoot().isEmpty()) {
                    confirmMsg =  etrCommonService.StringAddByBr(confirmMsg,
                            getMessage("ETR_E_0017", getMessage("invest.channelId"), lineNo));
                }
            }
            if (StringUtil.isBlank(confirmMsg)) {
                jsonArrays.add(rowData);
            } else {
                totalMsg = etrCommonService.StringAddByBr(totalMsg, confirmMsg);
                errorNum++ ;
            }
        }
        totalMsg = etrCommonService.StringAddByBr(totalMsg,
                getMessage("ETR_C_0001", String.valueOf(errorNum)));
        Map<String, Object> resultMap = new HashMap<>();
        if (StringUtil.isNotBlank(totalMsg)) {
            resultMap.put("confirmMsg", totalMsg);
        }
        resultMap.put("correctData", JSONArray.toJSONString(jsonArrays));
        AjaxResult wr = new AjaxResult();
        wr.setData(resultMap);
        return wr;
    }
    // add by gaopeng 20191219 多组合多资产导入 end
    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        EtrConBondDto dto = this.getBussinessEntity(entity);
        CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
        if (taskId != null) {
            try {
                if (dto.isImportFlow()) { // 只有在多对多组合提交时，才为true，供弹出用户选择，选择用户备用，让需要上级审批的流程走到这个人这里，不需要的直接结束
                    Map<String, Object> param = new HashMap<>();
                    param.put(BussinessConstant.是否需要上级审批, true);
                    return param;
                }
                Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getPurchBondTotal(), LoginUserUtil.getUserName(),
                        DictCodeList.平安资产类型_债券.getValue(), null, dto.getTradeMarket(), dto.getPs()
                        // 资产类型改为授权类型 add by renping 2019-08-06  start
                        , dto.getFinprodId(), entity.getWorkflowId()
                        // 资产类型改为授权类型 add by renping 2019-08-06  end
                );

                params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
                return commonBusinessService.是否需要上级审批(params);
            } catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        } else {
            return null;
        }
//        return null;
    }
}
