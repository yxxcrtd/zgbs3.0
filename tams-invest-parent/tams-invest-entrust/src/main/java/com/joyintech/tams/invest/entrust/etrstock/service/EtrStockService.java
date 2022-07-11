package com.joyintech.tams.invest.entrust.etrstock.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etrstock.dto.EtrStockDto;
import com.joyintech.tams.invest.entrust.etrwtspotex.dto.EtrInvestPersonInfoDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;


/**   
 * @title: EtrStockService
 * @package com.joyintech.tams.invest.entrust.etrstock.service
 * @description: 
 * @author: junYin
 * @update: modify by daizhiwei 20190703
 * @date: 2019-06-03 23:50
*/
@Service
public class EtrStockService extends CommonExportService<EtrStockDto> implements BaseProcessService<EtrStockDto> {
    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;
    /**
     * 日期相关
     */
    @Autowired
    private DateCalService dateCalService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private EtrCommonService etrCommonService;

    @Autowired
    private IPlatformDate platformDateImpl;

    /**   
     * @title: EtrStockService
     * @package com.joyintech.tams.invest.entrust.etrstock.service
     * @description: 股票委托分页查询
     * @author: junYin
     * @update: modify by daizhiwei 20190703
     * @date: 2019-06-03 23:56
    */
    public Page<EtrStockDto> selectForPage(Map<String, Object> param) {
 
        if (StringUtil.isNotEmpty(param.get("entrustDate"))) {
            List<String> dateList = StringUtil.splitToList(" ", String.valueOf(param.get("entrustDate")));
            param.put("startDate", dateList.get(0));
            param.put("endDate", dateList.get(2));
        }
        param.put("userId", SecurityUtil.getLoginUser().getUserName());
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/findPage", param, EtrStockDto.class);
    }


    /**
     *@title: getentrustId
     *@description: 组合代码下拉框联想查询
     *@author: junYin
     *@date: 2019/06/04 0:19   
     *@param: * @param param 1
     *@return: java.util.List<com.joyintech.framework.select.SuggestDto>
     *@throws:
     */
    public List<SuggestDto> getentrustId(String param) {
 
        User user = SecurityUtil.getLoginUser();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getUserName());
        map.put("param", param);
        return restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/getentrustId" , map, SuggestDto.class);
    }

    /**
     *@title: getfinprodId
     *@description: 资产代码联想文本框
     *@author: junYin
     *@date: 2019/06/04 0:23   
     *@param: * @param param 1
     *@return: java.util.List<com.joyintech.framework.select.SuggestDto>
     *@throws:
     */
    public List<SuggestDto> getfinprodId(String param) {
        return restTemplate.getForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/getfinprodId?finprodId=" + param, SuggestDto.class);
    }

    /**
     *@title: deleteDelegate
     *@description: 删除委托
     *@author: junYin
     *@date: 2019/06/04 0:24   
     *@param: * @param entrustId 1
     *@return: boolean
     *@throws:
     */
    public boolean deleteDelegate(String entrustId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/delete?entrustId=" + entrustId, boolean.class);
    }

    /**
     *@title: closeDelegate
     *@description: 关闭委托
     *@author: junYin
     *@date: 2019/06/04 0:25   
     *@param: * @param entrustId 1
     * @param closeWay 2
     *@return: boolean
     *@throws:
     */
    public boolean closeDelegate(String entrustId, String closeWay) {
        String uriParams = "/EtrStock/close?entrustId=" + entrustId + "&closeWay=" + closeWay;
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME + uriParams, boolean.class);
    }

    /**
     *@title: recallDelegate
     *@description: 撤回委托
     *@author: junYin
     *@date: 2019/06/04 0:26   
     *@param: * @param dto 1
     *@return: boolean
     *@throws:
     */
    public boolean recallDelegate(EtrStockDto dto) {
        return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/recall", dto, boolean.class);
    }

    /**
     * 工作流完成
     */
    @Override
    public void doComplete(EtrStockDto dto, OperationType operationType) {
        dto.setOperateType(operationType.toString());
        Date sysDate =  CacheUtil.getAppCache(GlobalConstant.NOW_SYS_DATE, Date.class);
        dto.setEntrustDate(sysDate);
        basRiskMathCommService.insert(dto.getRiskTrialRetDto(), dto.getEntrustId());
        restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/doComplete", dto, Integer.class);
    }
    
    /**
     * 
     * 流程发起时调用
     */
     @Override
     public void doInsert(EtrStockDto dto, OperationType type){
         //GLCP-119:【指令查询】股票指令流程中数据在指令查询里看不到 start
         //dto.setOperateType(type.toString());
         //GLCP-119:【指令查询】股票指令流程中数据在指令查询里看不到 end
         restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/doInsert",dto, Integer.class);
     }

     /**
      * 
      * 流程修改时调用
      */
     @Override
     public void doUpdate(EtrStockDto dto){
         restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/doUpdate",dto, Integer.class);
     }

    @Override
    public void doBack(EtrStockDto entity) {
        restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/doBack", entity, Integer.class);
    }

    /**
     *@title: getentrustIdAdd
     *@description: 新建页面组合代码联想下拉框
     *@author: junYin
     *@date: 2019/06/04 0:28   
     *@param: * @param map 1
     *@return: com.joyintech.framework.grid.Page<com.joyintech.framework.select.SelectOptionDto>
     *@throws:
     */
    public Page<SelectOptionDto> getentrustIdAdd(Map<String, Object> map) {
        map.put("userId", SecurityUtil.getLoginUser().getUserName());
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/getentrustIdAdd" ,map, SelectOptionDto.class);
    }

    /**
     *@title: getfinprodIdForAdd
     *@description: 新建页面资产代码联想下拉框
     *@author: junYin
     *@date: 2019/06/04 0:28   
     *@param: * @param map 1
     *@return: com.joyintech.framework.grid.Page<com.joyintech.framework.select.SelectOptionDto>
     *@throws:
     */
    public Page<SelectOptionDto> getfinprodIdForAdd(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/getfinprodIdForAdd",map,SelectOptionDto.class);
    }

    /**
     *@title: getSelectTongDao
     *@description: 获取新建界面的通道联想下拉框
     *@author: Daizhiwei
     *@date: 2019/07/03 15:10
     *@param: * @param  1
     *@return: com.joyintech.framework.grid.Page<com.joyintech.framework.select.SelectOptionDto>
     *@throws:
     */
    public Page<SelectOptionDto> getSelectTongDao(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/getSelectTongDao",map,SelectOptionDto.class);
    }
    /**
     * 通道可用现金查询
     * @param map
     * @return BigDecimal
     * @see
     *
     */
    public BigDecimal getTongDaoCash(Map<String, Object> map) {

        map.put("platDate",platformDateImpl.getSysDate());
        return  restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/getTongDaoCash" , map, BigDecimal.class);
    }
    /**
     *@title: check
     *@description: 校验方法
     *@author: junYin
     *@date: 2019/06/04 0:29   
     *@param: * @param dto 1
     *@return: java.util.List<com.joyintech.framework.validation.ValidMessage>
     *@throws:
     */
    public List<ValidMessage> check(EtrStockDto dto) {
        dto.setCloseWay(DictCodeListExt1.委托关闭方式_人工.getValue());
        return restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/check", dto, ValidMessage.class);
    }

    /**
     *@title: selectByKey
     *@description: 根据主键查询修改/查看信息 
     *@author: junYin
     *@date: 2019/06/04 0:29   
     *@param: * @param entrustId 1
     *@return: com.joyintech.tams.invest.entrust.etrstock.dto.EtrStockDto
     *@throws:
     */
    public EtrStockDto selectByKey(String entrustId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/selectByKey?entrustId=" + entrustId,EtrStockDto.class);
    }

    /**
     *@title: buildExportParam
     *@description: 导出
     *@author: junYin
     *@date: 2019/06/04 0:31   
     *@param: * @param exportParam 1
     *@return: void
     *@throws:
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config("/com/joyintech/tams/invest/entrust/etrstock/conf/export_conf.xml").dataSource(DataSourceType.MASERT);
    }
    @Override
    public Page<EtrStockDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if(StringUtil.equals(propName, "transactioncom")){
            cell.setCellValue(value+"%");
        }
    }

    /**
     *@title: getProAvailableCash
     *@description: 组合可用现金查询
     *@author: junYin
     *@date: 2019/06/04 0:32
     *@param: * @param param 1
     *@return: java.util.Map<java.lang.String,java.lang.Object>
     *@throws:
     */
    public Map<String, Object> getProAvailableCash(Map<String, Object> param) {

        Date vdate = DateUtil.parse((String)param.get("vdate"));
        String calId = (String)param.get("calId");
        if(StringUtil.isNotEmpty(calId)){
            List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(vdate)),DateUtil.addDay(DateUtil.parse(DateUtil.format(vdate)), 15),calId);
            Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(vdate)), 1, holidayList, "1");
            param.put("sysDateT1",DateUtil.format(tradeDay));
        }
        return etrCommonService.getProAvailableCash(param);
//        return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/getProAvailableCash",param,Map.class);
    }

    /**
     *@title: getAssetAvailablePosition
     *@description: 组合可用持仓查询
     *@author: junYin
     *@date: 2019/06/04 0:32
     *@param: * @param dto 1
     *@return: java.math.BigDecimal
     *@throws:
     */
    public BigDecimal getAssetAvailablePosition(EtrSecPositionDto dto) {

        Date sysDate =  CacheUtil.getAppCache(GlobalConstant.NOW_SYS_DATE, Date.class);
        dto.setCdate(DateUtil.parse(DateUtil.format(sysDate)));
        dto.setHodingType(DictCodeList.持仓类型_可用.getValue());
        return etrCommonService.getAssetAvailablePosition(dto);
//        return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/getAssetAvailablePosition",dto, BigDecimal.class);
    }

    /**
     *@title: selectLimitAmt
     *@description: 查询授权额度
     *@author: junYin
     *@date: 2019/06/04 0:32
     *@param: * @param dto 1
     *@return: java.math.BigDecimal
     *@throws:
     */
    public BigDecimal selectLimitAmt(EtrStockDto dto){
        Map<String, Object> map = new HashMap<>();
        map.put("userName", SecurityUtil.getLoginUser().getUserName());
        map.put("assetType", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        map.put("assetSecType", dto.getAssetType1());
        map.put("tradeMarket", dto.getTradeMarket());
        map.put("ps",dto.getPs());
        return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/common/SelectLimitAmt/selectLimitAmt", map, BigDecimal.class);
    }

    /**
     * 根据组合代码查询证券托管户
     * @param map
     * @return
     */
    public List<SelectOptionDto> getSecAcctId(Map<String, Object> map) {
        List<SuggestDto> tempList = restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/getSecAcctId",map, SuggestDto.class);
        List<SelectOptionDto> resultList = new ArrayList<SelectOptionDto>();
        if (!StringUtil.isEmptyList(tempList)) {
            for (SuggestDto suggestDto : tempList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(suggestDto.getCode());
                selectOptionDto.setText(suggestDto.getName());
                if (StringUtil.isNotBlank((String) map.get("portfolioId"))) {
                    selectOptionDto.setParentId((String) map.get("portfolioId"));
                }
                resultList.add(selectOptionDto);
            }
        }

        return resultList;
    }
    
    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        EtrStockDto dto = this.getBussinessEntity(entity);
     // 将交易方向不是卖出的，都写成买入by赵东东 2019.8.26 start
        String ps=null;
        // 将代码中的字符串去掉by赵东东 2019.8.26 start
        if(!StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
        	ps = DictCodeList.交易方向_买入.getValue();
        // 将代码中的字符串去掉by赵东东 2019.8.26 start
        }
     // 将交易方向不是卖出的，都写成买入by赵东东 2019.8.26 end
        if (taskId != null) {
            try {
            	
            	Map<String, Object> params = CommonBusinessService.setParams(
                        dto.getCounterId(), dto.getEntrustAmt(), LoginUserUtil.getUserName(),
                        DictCodeListExt2.平安资产类型_股票.getValue(), null, dto.getTradeMarket(), dto.getPs()
                        // 资产类型改为授权类型 add by renping 2019-08-06  start
                        , null, dto.getWorkflowId()
                        // 资产类型改为授权类型 add by renping 2019-08-06  end
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
     * 查询估值净价
     * @author wangguiqi
     */
	public BigDecimal getClosePriceByPara(Map<String, Object> paramMap) {
		paramMap.put("sysdate", DateUtil.getSysDate());
		return restTemplate.postForObject(
	            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrStock/getClosePriceByPara", paramMap,
	            BigDecimal.class);
	}

	/**
	 * 查询交易对手名称
	 *
	 * @return
	 */
	public String getCounterName(String counterId) {
		return restTemplate.getForObject(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrBondFirstLBidding/getCounterName?counterId=" + counterId,
				String.class);
	}

	/**
	 * 获取委托列表数据
	 * 
	 * @return
	 */
    public List<EtrStockDto> getEntrustListData(String tempList) {
        List<EtrStockDto> etrStockDtoList = new ArrayList<>();
        JSONArray objects = JSONArray.parseArray(tempList);

        for (int i = 1; i < objects.size(); i++ ) {
            EtrStockDto dto = new EtrStockDto();
            JSONArray data = JSONArray.parseArray(((JSONArray)objects.get(i)).toJSONString());
            // 根据模板中固定的下标来复制
            // 序号
            dto.setIndexId(data.get(0).toString().trim());
            // 组合代码
            dto.setPortfolioId(data.get(1).toString().trim());
            // 委托开始日
            dto.setVdate(DateUtil.parse(data.get(2).toString().trim()));
            // 委托结束日
            dto.setMdate(DateUtil.parse(data.get(3).toString().trim()));
            // 资产代码
            dto.setFinprodMarketId(data.get(4).toString().trim());
            if (StringUtil.isNotEmpty(data.get(5))) {
                // 分割交易对手，获取code
                String[] s = data.get(5).toString().split(":");
                dto.setPs(s[0]);
            }
            if (StringUtil.isNotEmpty(data.get(6))) {
                // 分割交易模式，获取code
                String[] s1 = data.get(6).toString().split(":");
                dto.setDealMode(s1[0]);
            }
            // 证券账户
            Map<String, Object> map1 = new HashMap<>();
            map1.put("initValue", data.get(7).toString().trim());
            map1.put("page", 1);
            map1.put("length", 20);
            map1.put("portfolioId", data.get(1).toString());
            List<SelectOptionDto> secAcctIds = getSecAcctId(map1);
            dto.setSecAcctIdName(secAcctIds.get(0).getText());
            dto.setSecAcctId(data.get(7).toString());
            if (StringUtil.isNotEmpty(data.get(8))) {
                // 分割价格模式，获取code
                String[] s2 = data.get(8).toString().split(":");
                dto.setPriceModel(s2[0]);
            }
            // 委托金额
            if (StringUtil.isNotBlank(data.get(9).toString())) {
                dto.setEntrustAmt(
                    BigDecimal.valueOf(Double.valueOf(data.get(9).toString().trim())));
            }
            // 委托数量
            if (StringUtil.isNotBlank(data.get(10).toString())) {
                dto.setEntrustNum(
                    BigDecimal.valueOf(Double.valueOf(data.get(10).toString().trim())));
            }
            // 委托价格
            if (StringUtil.isNotBlank(data.get(11).toString())) {
                dto.setPrice(BigDecimal.valueOf(Double.valueOf(data.get(11).toString().trim())));
            }
            // 配售代码
            //dto.setSaleCode(data.get(12).toString().trim());
            // 备注
            dto.setRemark(data.get(12).toString().trim());
            // 交易对手
            dto.setCounterId(data.get(13).toString().trim());
            // 交易对手名称
            String counterName = getCounterName(data.get(13).toString().trim());
            dto.setCounterIdName(counterName);
            // 币种
            dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            // 根据资产代码查出资产名称、交易场所、上市板
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("search", dto.getFinprodMarketId());
            Page<SelectOptionDto> page = getfinprodIdForAdd(map);
            if (StringUtil.isNotEmpty(page) && StringUtil.isNotEmpty(page.getRoot())) {
                // 如果只查询到一条数据
                if (page.getRoot().size() == 1) {
                    SelectOptionDto d = page.getRoot().get(0);
                    if (StringUtil.isNotEmpty(d.getObject())) {
                        Map<String, Object> m = (Map<String, Object>)d.getObject();
                        // 交易场所
                        dto.setTradeMarket((String)m.get("tradeMarket"));
                        // 上市板
                        dto.setBoard((String)m.get("board"));
                        // 资产名称
                        dto.setFinprodName((String)m.get("finprodName"));
                        // 资产代码
                        dto.setFinprodId((String)m.get("finprodId"));
                    }
                    // 如果查询到多条数据，遍历page.getRoot,找到id与资产代码相同的数据
                }
                else if (page.getRoot().size() > 1) {
                    for (SelectOptionDto d : page.getRoot()) {
                        if (StringUtil.equals(d.getId(), dto.getFinprodMarketId())) {
                            if (StringUtil.isNotEmpty(d.getObject())) {
                                Map<String, Object> m = (Map<String, Object>)d.getObject();
                                // 交易场所
                                dto.setTradeMarket((String)m.get("tradeMarket"));
                                // 上市板
                                dto.setBoard((String)m.get("board"));
                                // 资产名称
                                dto.setFinprodName((String)m.get("finprodName"));
                                // 资产代码
                                dto.setFinprodId((String)m.get("finprodId"));
                            }
                        }
                    }
                }
            }
            // 指令可用现金
            if(!StringUtil.equals(dto.getPs(),DictCodeList.交易方向_卖出.getValue())) {
            String vdate1 = dto.getVdate().toString();
            String vdate = DateUtil.format(dto.getVdate(), "yyyyMMdd");
            String calId = null;
            map.put("vdate", vdate);
            map.put("calId", calId);
            map.put("portfolioId", dto.getPortfolioId());
            map.put("ccy", dto.getCcy());
            Map<String, Object> map2 = getProAvailableCash(map);
            if (StringUtil.isNotEmpty(map2.get("cashT"))) {
                dto.setProAvailableCash(
                    BigDecimal.valueOf(Double.valueOf(map2.get("cashT").toString())));
            }
            else {
                dto.setProAvailableCash(BigDecimal.ZERO);
            }
            if (StringUtil.isNotEmpty(map2.get("cashT4"))) {
                // 交易所资金账户可用现金
                dto.setTradeMarketAccAvailableCash(
                    BigDecimal.valueOf(Double.valueOf(map2.get("cashT4").toString())));
            }
            else {
                dto.setTradeMarketAccAvailableCash(BigDecimal.ZERO);
            }
            }
            // 指令可用持仓
            if(StringUtil.equals(dto.getPs(),DictCodeList.交易方向_卖出.getValue())) {
            map.put("finprodId", dto.getFinprodMarketId());
            EtrSecPositionDto ptlSecPositionFlowDto = new EtrSecPositionDto();
            ptlSecPositionFlowDto.setCcy(dto.getCcy());
            ptlSecPositionFlowDto.setFinprodId(dto.getFinprodId());
            ptlSecPositionFlowDto.setSecAcctId(dto.getSecAcctId());
            ptlSecPositionFlowDto.setPortfolioId(dto.getPortfolioId());
            dto.setAssetAvailablePosition(getAssetAvailablePosition(ptlSecPositionFlowDto));
            }else {
                dto.setAssetAvailablePosition(null);
            }
            dto.setEtrInvestPersonInfo(new EtrInvestPersonInfoDto());
            // 交易对手
            dto.getEtrInvestPersonInfo().setCounterId(dto.getCounterId());
            // 投资经理
            dto.getEtrInvestPersonInfo().setInvManagerId(dto.getCreateUser());
            // 投资处室
            dto.getEtrInvestPersonInfo().setRoom(dto.getCreateDept());
            etrStockDtoList.add(dto);
        }
        return etrStockDtoList;
    }
    
	/**
     * 验证导入的数据
     * @param fileName 导入的数据
     * @return wr 报错信息与正确的数据
     */
    public AjaxResult checkImportData(String fileName) {
        String confirmMsg = "";
        String changeLineMark = "<br/>";
        List<List<String>> objects = ExcelUtil.getTempList(fileName);
        Map<String, Object> resultMap = new HashMap<>();
        // 判断导入数据是否大于100条，如果大于100条，报出信息
        if (objects.size() < 102) {
            int x = objects.size();
            List<List<String>> jsonArrays = new ArrayList<>();
            jsonArrays.add(objects.get(0));
            // 循环check每条数据
            for (int i = 1; i < objects.size(); i++ ) {
                String lineNo = String.valueOf(i + 1);
                String confirmMsg1 = "";
                List<String> rowData = objects.get(i);
                String indexId = rowData.get(0).toString();
                if (StringUtil.isBlank(indexId)) {
                    confirmMsg1 = getMessage("ETR_ST_002", i + 1);
                    break;
                }

                // 委托开始日check是否为空和日期格式是否正确
                String message = etrCommonService.checkIsBlank(rowData.get(2).toString(),
                    "EtrStock002.vdate", lineNo);
                confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1, message);
                message = etrCommonService.checkIsDate(rowData.get(2).toString(),
                    "EtrStock002.vdate", lineNo);
                confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1, message);

                // 委托结束日非空和日期格式是否正确
                message = etrCommonService.checkIsBlank(rowData.get(3).toString(),
                    "EtrStock002.mdate", lineNo);
                confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1, message);
                message = etrCommonService.checkIsDate(rowData.get(3).toString(),
                    "EtrStock002.mdate", lineNo);
                confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1, message);

                // 委托开始、终止日是否是平台日check
                confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1,
                        EtrCommonService.checkIsPlateDate(rowData.get(2).toString(), rowData.get(3).toString(),lineNo));

                // 组合代码非空和是否存在check
                message = etrCommonService.checkIsBlank(rowData.get(1).toString(),
                    "EtrStock002.portfolioId", lineNo);
                confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1, message);
                message = etrCommonService.checkPortfolioIdExist(rowData.get(1).toString(),
                    "EtrStock002.portfolioId", lineNo);
                confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1, message);

                // 资产代码非空和是否存在check
                message = etrCommonService.checkIsBlank(rowData.get(4).toString(),
                    "EtrStock002.finprodId", lineNo);
                confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1, message);
                if (StringUtil.isNotEmpty(rowData.get(4).toString())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("search", rowData.get(4).toString());
                    map.put("page", 1);
                    map.put("length", 20);
                    Page<SelectOptionDto> finprodIds = getfinprodIdForAdd(map);
                    if (finprodIds.getRoot().isEmpty()) {
                        confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1, getMessage(
                            "ETR_E_0017", getMessage("EtrStock002.finprodId"), lineNo));
                    }
                }

                // 交易方向非空check
                message = etrCommonService.checkIsBlank(rowData.get(5).toString(),
                    "EtrStock002.ps", lineNo);
                confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1, message);

                // 投资目的非空check
                message = etrCommonService.checkIsBlank(rowData.get(6).toString(),
                    "EtrStock002.dealMode", lineNo);
                confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1, message);

                // 证券账户非空和是否存在check
                message = etrCommonService.checkIsBlank(rowData.get(7).toString(),
                    "EtrStock002.secAcctId", lineNo);
                confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1, message);
                if (StringUtil.isNotEmpty(rowData.get(7).toString())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("initValue", rowData.get(7).toString().trim());
                    map.put("page", 1);
                    map.put("length", 20);
                    map.put("portfolioId", rowData.get(1).toString());
                    List<SelectOptionDto> secAcctIds = getSecAcctId(map);
                    if (secAcctIds.isEmpty()) {
                        confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1, getMessage(
                            "ETR_E_0017", getMessage("EtrStock002.secAcctId"), lineNo));
                    }
                }

                // 价格模式非空
                message = etrCommonService.checkIsBlank(rowData.get(8).toString(),
                    "EtrStock002.priceModel", lineNo);
                confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1, message);
                // 当价格模式为市价指令是，委托价格不可填,其他的价格模式，委托价格必填
                if (StringUtil.isNotEmpty(rowData.get(8).toString())) {
                    String[] t = rowData.get(8).toString().trim().split(":");
                    if (StringUtil.equals(t[0], DictCodeListExt2.价格模式_市价指令.getValue())) {
                        if (StringUtil.isNotBlank(rowData.get(11).toString())) {
                            if (StringUtil.isNotBlank(confirmMsg1)) {
                                confirmMsg1 += changeLineMark;
                            }
                            confirmMsg1 += getMessage("ETR_ST_003",
                                getMessage("EtrStock002.price"), lineNo);
                        }
                    }
                    else {
                        message = etrCommonService.checkIsBlank(rowData.get(11).toString(),
                            "EtrStock002.price", lineNo);
                        confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1, message);
                    }
                }
                // 当委托金额非空时，委托数量不可填，当委托数量非空时，委托金额不可填
                if (StringUtil.isNotBlank(rowData.get(9).toString())
                    && StringUtil.isNotBlank(rowData.get(10).toString())) {
                    if (StringUtil.isNotBlank(confirmMsg1)) {
                        confirmMsg1 += changeLineMark;
                    }
                    confirmMsg1 += getMessage("ETR_ST_004", lineNo);
                }
                // 交易对手非空和是否存在check
                message = etrCommonService.checkIsBlank(rowData.get(13).toString(),
                    "etrInvestPersonInfo.counterId", lineNo);
                confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1, message);
                if (StringUtil.isNotBlank(rowData.get(13).toString())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("initValue", rowData.get(13).toString());
                    map.put("page", 1);
                    map.put("length", 20);
                    map.put("counterType", null);
                    Page<SelectOptionDto> selectOptionDtoPage = restTemplate.postForPage(
                        ServiceApplication.ENTRUST_SERVICE_NAME + "/common/EntrustCommonPersonSelect/getCounterId",
                        map, SelectOptionDto.class);
                    if (selectOptionDtoPage.getRoot().isEmpty()) {
                        confirmMsg1 = etrCommonService.StringAddByBr(confirmMsg1, getMessage(
                            "ETR_E_0017", getMessage("etrInvestPersonInfo.counterId"), lineNo));
                    }
                }
               
                
                // 若是数据合格，将此条数据放在jsonArrays中返回
                if (StringUtil.isBlank(confirmMsg1)) {
                    jsonArrays.add(rowData);
                }
                // 将所有错误信息汇总返回
                if (StringUtil.isNotBlank(confirmMsg1)) {
                    confirmMsg += confirmMsg1;
                    confirmMsg += changeLineMark;
                }
            }
            // 计算多少条数据导入失败
            int y = jsonArrays.size();
            int num = x - y;
            // 导入失败多少条数据报出的信息
            confirmMsg += changeLineMark + getMessage("ETR_ST_009", num);

            if (StringUtil.isNotBlank(confirmMsg)) {
                confirmMsg += changeLineMark + getMessage("ETR_ST_010");
                resultMap.put("confirmMsg", confirmMsg);
            }
            resultMap.put("correctData", JSONArray.toJSONString(jsonArrays));
        }
        else {
            // 大于100条，提示大于100条，重新导入信息
            JSONArray jsonArrays = new JSONArray();
            confirmMsg = this.getMessage("ETR_ST_001");
            resultMap.put("confirmMsg", confirmMsg);
            jsonArrays.add(0);
            resultMap.put("correctData", JSONArray.toJSONString(jsonArrays));
        }
        AjaxResult wr = new AjaxResult();
        wr.setData(resultMap);
        return wr;
    }

    /**
     * 获取市价
     * @param param 界面参数
     * @return 返回值
     */
    public AjaxResult getClosePrice(Map<String, Object> param) {
        AjaxResult wr = new AjaxResult();
        BigDecimal price = EtrCommonCashPositionDealService.getClosePrice((String)param.get("finprodMarketId"),DateUtil.addDay(DateUtil.platDateFromCache(),-1));
        wr.setData(price);
        return wr;
    }
}
