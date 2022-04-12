/*
 * 文件名：EtrBondFirstLBiddingService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.etrib.repo.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dao.JoyinbusinessCommonMapper;
import com.joyintech.tams.business.common.dto.SecManageAcctIdRequestDto;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.cfets.bankuplink.repo.dao.CfetsIbuQuoteRepoMapper;
import com.joyintech.tams.cfets.bankuplink.repo.dto.CfetsRepoDealQuoteCommonDto;
import com.joyintech.tams.cfets.bankuplink.repo.dto.RepoEntrustPledgeDto;
import com.joyintech.tams.cfets.etrib.bond.dao.CfetsEtrIbBondinfoMapper;
import com.joyintech.tams.cfets.etrib.repo.dao.CfetsEtrIbRepoLib001Mapper;
import com.joyintech.tams.cfets.etrib.repo.dao.CfetsEtrIbRepoinfoMapper;
import com.joyintech.tams.cfets.etrib.repo.dto.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.*;

/**
 * 银行间债券指令
 * @author chengzhong
 * @version 1.0 2021年1月19日
 * @see CfetsEtrIbRepoinfoService
 * @since 1.0
 */
@Service
public class CfetsEtrIbRepoinfoService extends BaseService{

    @Autowired
    private CfetsEtrIbRepoinfoMapper cfetsEtrIbRepoinfoMapper;
    
    @Autowired
    private EtrSecStockMapper etrSecStockMapper;
    @Autowired
    private PtlSecStockMapper ptlSecStockMapper;
    @Autowired
    private EtrSecStockConfigMapper etrSecStockConfigMapper;
    
    @Autowired
    private EtrEntrustPledgeMapper etrEntrustPledgeMapper;
    
	@Autowired
	private CfetsEtrIbRepoLib001Mapper cfetsEtrIbRepoLib001Mapper;


    @Autowired
    private CfetsEtrIbBondinfoMapper cfetsetrIbBondinfoMapper;
    /**
     * 平台日期
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;


    @Autowired(required = false)
    private WflEtrEntrustMapper wflEtrEntrustMapper;

    @Autowired
    private JoyinbusinessCommonMapper joyinbusinessCommonMapper;


    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired
    private CfetsIbuQuoteRepoMapper cfetsIbuQuoteRepoMapper;

    @Autowired(required = false)
    private DateCalService dateCalService;

    /**
     * 指令管理查询
     *
     * @return
     * @see
     * @since 1.0
     */
    public Page<CfetsEtrRepoZLCXResponse> findZlglPage(CfetsEtrRepoZLCXRequest cfetsEtrRepoZLCXRequest) {
        Map<String, Object> pageParam = BeanUtil.object2Map(cfetsEtrRepoZLCXRequest);
        pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
        pageParam.put("FINPROD_TYPE2_Z", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        pageParam.put("FINPROD_TYPE2_N", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        pageParam.put("ENTRUST_STATUS_08", DictCodeListExt3.委托状态_流程中.getValue());
        pageParam.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        pageParam.put("sysDate", iPlatformDate.getSysDateStr());
        pageParam.put("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.parse(DateUtil.getSysDate()));
        int year = c.get(Calendar.YEAR);
        pageParam.put("year",year);
        pageParam.put("sort",cfetsEtrRepoZLCXRequest.getPageMode().getSort());
        pageParam.put("direction",cfetsEtrRepoZLCXRequest.getPageMode().getDirection());
        Page<CfetsEtrRepoZLCXResponse> result = super.queryForPage(cfetsEtrRepoZLCXRequest.getPageMode().getPageNo(), cfetsEtrRepoZLCXRequest.getPageMode().getPageSize(), () -> cfetsEtrIbRepoinfoMapper.findZlglPage(pageParam));
        return result;
    }

    /**
     * 质押券入库画面，查询可质押债券列表
     *
     * @throws Exception 异常
     * @see
     * @since 1.0
     * @return
     */
    public Page<CfetsEtrIbRepoLibAvaPledgeBondResponse> selectAvaPledgeBondList(CfetsEtrRepoLibAvaPledgeBondRequest dto) {
        if(!StringUtil.isNotEmpty(dto.getPledgeStockId())){
            dto.setPledgeStockId("999999");
        }
        Map<String,Object> pageParam = BeanUtil.object2Map(dto);
        this.setParam(pageParam);
        pageParam.put("sort",dto.getPageMode().getSort());
        pageParam.put("direction",dto.getPageMode().getDirection());
        Page<CfetsEtrIbRepoLibAvaPledgeBondResponse> result = super.queryForPage(dto.getPageMode().getPageNo(),dto.getPageMode().getPageSize(),() ->cfetsEtrIbRepoLib001Mapper.selectAvaPledgeBondList(pageParam));
        for (CfetsEtrIbRepoLibAvaPledgeBondResponse response : result.getRoot()) {
            String pendingPeriodStr = joyinbusinessCommonService.calPendingPeriod(response.getFinprodId());
            String period = pendingPeriodStr.substring(0, pendingPeriodStr.length() - 1);
            String periodUnit = pendingPeriodStr.substring(pendingPeriodStr.length() - 1, pendingPeriodStr.length());
            response.setWaitPeriod(pendingPeriodStr);
        }
        return result;
    }
    /**
     * 检索参数设置：
     * 标准券查询画面，查询标准券信息列表
     * 质押券入库画面，查询可质押债券列表
     * 质押券出库画面，查询待出库信息
     *
     * @param pageParam
     */
    private void setParam(Map<String, Object> pageParam) {
        pageParam.put("A_001", DictCodeListExt3.资产类型_资产账户持仓.getValue());
        pageParam.put("S_002", DictCodeListExt3.资产类型_可质押持仓.getValue());
        pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        pageParam.put("TRADEPLACE01", DictCodeListExt1.交易场所_银行间.getValue());
        pageParam.put("TRADEPLACE03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        pageParam.put("TRADEPLACE04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
        pageParam.put("plateDate",iPlatformDate.getSysDateStr());
    }

    public AjaxResult checkSaveErrorMethod(CfetsEtrRepoSaveRequest cfetsDto, BindingResult result){
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        List<ValidMessage> messages = new ArrayList<>();
        boolean chlId = false;
        if(cfetsDto.getEtrInvestRepoInfoDtos()==null||cfetsDto.getEtrInvestRepoInfoDtos().size()<=0){
            messages.add(new ValidMessage("portfolioSeries", "请输入组合信息"));
        }
        List<CfetsEtrRepoSaveRequest> dtoList = cfetsDto.getEtrInvestRepoInfoDtos();
        CfetsEtrRepoSaveRequest dto = cfetsDto;
        if(!StringUtil.isNotEmpty(dto.getPriceModel()))
            messages.add(new ValidMessage("priceModel", "请输入价格模式。"));
        if(!StringUtil.isNotEmpty(dto.getEntrustDate()))
            messages.add(new ValidMessage("entrustDate", "请输入指令日期。"));
        if(!StringUtil.isNotEmpty(dto.getSettleSpreed()))
            messages.add(new ValidMessage("settleSpreed", "请输入清算速度。"));
        if(!StringUtil.isNotEmpty(dto.getRepoTerm()))
            messages.add(new ValidMessage("repoTerm", "请输入回购天数。"));
        if(!StringUtil.isNotEmpty(dto.getPrice()))
            messages.add(new ValidMessage("price", "请输入回购利率。"));
        if(StringUtil.isNotEmpty(dto.getPrice()) && dto.getPrice().compareTo(BigDecimal.ZERO)<=0)
            messages.add(new ValidMessage("回购利率不能小于等于0。"));
        //指令日期若是节假日，不可以保存、提交
        if(StringUtil.isNotEmpty(dto.getEntrustDate())){
            String isHoliday = cfetsetrIbBondinfoMapper.getIsHoliday(dto.getEntrustDate(), "03");
            if(StringUtil.isNotEmpty(isHoliday) && !StringUtil.equals(isHoliday,"0")){
                messages.add(new ValidMessage("非银行间交易日不可提交指令。"));

            }
        }

        for(int i = 0; i< dtoList.size(); i++) {
            if(!StringUtil.isNotEmpty(dtoList.get(i).getPortfolioId()))
                messages.add(new ValidMessage("组合信息(第"+(i+1)+"行)，请输入组合代码。"));
            if(!StringUtil.isNotEmpty(dtoList.get(i).getEntrustAmt()))
                messages.add(new ValidMessage("组合信息(第"+(i+1)+"行)，请输入指令金额。"));
            if(StringUtil.isNotEmpty(dtoList.get(i).getEntrustAmt()) && dtoList.get(i).getEntrustAmt().compareTo(BigDecimal.ZERO)<=0)
                messages.add(new ValidMessage("组合信息(第"+(i+1)+"行)，指令金额不能小于等于0。"));

            if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL),"03")
                    &&StringUtil.isNotEmpty(dto.getPs())
                    &&StringUtil.equals(DictCodeList.回购拆借方向_正回购.getValue(),dto.getPs())
                    &&!StringUtil.isNotEmpty(dtoList.get(i).getZyqk())){
                messages.add(new ValidMessage("组合信息(第"+(i+1)+"行)，请输入质押券库。"));
            }
            if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL),"02")
                    &&StringUtil.isNotEmpty(dto.getPs())
                    &&StringUtil.equals(DictCodeList.回购拆借方向_正回购.getValue(), dto.getPs())){
                if(StringUtil.isNotEmpty(dtoList.get(i).getTotalAmount())){
                    if(StringUtil.isNotEmpty(dtoList.get(i).getEntrustAmt())&&dtoList.get(i).getTotalAmount().compareTo(dtoList.get(i).getEntrustAmt())<0){
                        messages.add(new ValidMessage("组合信息(第"+(i+1)+"行)，指令金额不得大于该组合下的质押券折算总额。"));
                    }
                }else{
                    messages.add(new ValidMessage("请添加质押券信息(第"+(i+1)+"行)。"));
                }
            }
        }
        if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL),"02")
                &&StringUtil.isNotEmpty(dto.getPs())
                &&StringUtil.equals(DictCodeList.回购拆借方向_正回购.getValue(), dto.getPs())){
            List<CfetsPledgeBondInfoDto> list = dto.getPledgeBondTotalListDetailed();
            for(int i = 0; i< list.size(); i++){
                if(!StringUtil.isNotEmpty(list.get(i).getScale())){
                    messages.add(new ValidMessage("质押券信息(第"+(i+1)+"行)，折算比例必填。"));
                }
//                if(StringUtil.isNotEmpty(list.get(i).getPledgePrin())&&list.get(i).getPledgePrin().compareTo(list.get(i).getPledStrdPrinAmt())>0){
//                    messages.add(new ValidMessage("质押券信息(第"+(i+1)+"行)，质押券面总额不可超过可质押券面总额。"));
//                }
            }
        }
        if(messages.size()>0) {
            AjaxResult errorRs = new AjaxResult();
            errorRs.setErrorList(messages);
            return errorRs;
        }
        return new AjaxResult();
    }


	/**
     * 债券质押持仓信息查询
     *
     * @return
     * @see
     * @since 1.0
     */
    public Page<CfetsEtrRepoZLCXPositionResponse> findZlglPositionPage(CfetsEtrRepoZLCXPositionRequest cfetsEtrRepoZLCXPositionRequest) {
        String userId = SecurityUtil.getLoginUser().getUserName();
        Map<String, Object> map = new HashMap<>();
        map.put("role_pos", DictCodeListExt2.角色岗位_投资处长.getValue());
        map.put("userId", userId);
        Long count = selectUserRolePosList(map) ;
        Map<String, Object> pageParam = BeanUtil.object2Map(cfetsEtrRepoZLCXPositionRequest);
        if(count > 0){
            pageParam.put("userId", null);
        }else{
            pageParam.put("userId", userId);
        }
        pageParam.put("loginUserId", userId);
        pageParam.put("A_012", DictCodeListExt3.资产类型_质押式双边回购本方券已质押持仓.getValue());
        pageParam.put("S_002", DictCodeListExt3.资产类型_可质押持仓.getValue());
        pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        pageParam.put("sysDate", iPlatformDate.getSysDateStr());
        pageParam.put("TRADEPLACE01", DictCodeListExt1.交易场所_银行间.getValue());
        pageParam.put("VALUATION_SOURCE_CBC", DictCodeList.估值来源_中债登_CBC.getValue());
        Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.parse(DateUtil.getSysDate()));
        int year = c.get(Calendar.YEAR);
        pageParam.put("year",year);
        pageParam.put("sort",cfetsEtrRepoZLCXPositionRequest.getPageMode().getSort());
        pageParam.put("direction",cfetsEtrRepoZLCXPositionRequest.getPageMode().getDirection());
        pageParam.put("exPendingPeriod",cfetsEtrRepoZLCXPositionRequest.getExPendingPeriod());
        pageParam.put("exPendingPeriodUnit",cfetsEtrRepoZLCXPositionRequest.getExPendingPeriodUnit());
        pageParam.put("mPendingPeriod",cfetsEtrRepoZLCXPositionRequest.getmPendingPeriod());
        pageParam.put("mPendingPeriodUnit",cfetsEtrRepoZLCXPositionRequest.getmPendingPeriodUnit());
        Page<CfetsEtrRepoZLCXPositionResponse> result =
                super.queryForPage(cfetsEtrRepoZLCXPositionRequest.getPageMode().getPageNo(),
                        cfetsEtrRepoZLCXPositionRequest.getPageMode().getPageSize(), () -> cfetsEtrIbRepoinfoMapper.findZlglPositionPage(pageParam));
//        for (CfetsEtrRepoZLCXPositionResponse dto : result.getRoot()) {
//            String pendingPeriodStr = joyinbusinessCommonService.calPendingPeriod(dto.getFinprodId());
//            String period = pendingPeriodStr.substring(0, pendingPeriodStr.length() - 1);
//            String periodUnit = pendingPeriodStr.substring(pendingPeriodStr.length() - 1, pendingPeriodStr.length());
//            dto.setWaitPeriod(pendingPeriodStr);
//        }
        return result;
    }
    
    public Collection<CfetsEtrSecStockConfig> getAssetDetailBySeqno(CfetsEtrRepoPledgeAssetDetailRequest cfetsEtrRepoPledgeAssetDetailRequest) {
    	Map<String, Object> pageParam = BeanUtil.object2Map(cfetsEtrRepoPledgeAssetDetailRequest);
        List<EtrSecStockConfig> etrSecStockConfigList = cfetsEtrIbRepoinfoMapper.getAssetDetailBySeqno(pageParam);
    	Map<String,CfetsEtrSecStockConfig> map = new HashMap<>();
    	for(EtrSecStockConfig etrSecStockConfig:etrSecStockConfigList){
            String key =etrSecStockConfig.getIsPpb()+etrSecStockConfig.getIsRight()+etrSecStockConfig.getIsSecBond()
                    +etrSecStockConfig.getmGradeResult()+etrSecStockConfig.getSecGradeResult();
            CfetsEtrSecStockConfig cfetsEtrSecStockConfig = null;
            if(!StringUtil.isNotEmpty(etrSecStockConfig.getWindtypeone())&&!StringUtil.isNotEmpty(etrSecStockConfig.getWindtypetwo())){
                key = key+etrSecStockConfig.getWindtypeone();//二级分类与一级分类都为空时，单独处理  多一个null；
            }
            if(map.containsKey(key)){
                 cfetsEtrSecStockConfig = map.get(key);
//                cfetsEtrSecStockConfig.
//                List<>

            }else{
                 cfetsEtrSecStockConfig = new CfetsEtrSecStockConfig();
                BeanUtil.copyPropertiesIgnoreNull(cfetsEtrSecStockConfig,etrSecStockConfig);
            }
            List<String> windtype = new ArrayList<>();
            windtype.add(etrSecStockConfig.getWindtypeone());
            windtype.add(etrSecStockConfig.getWindtypetwo());
            if(cfetsEtrSecStockConfig.getWindtype()==null){
                List<List<String>> windtypeList = new ArrayList<>();
                windtypeList.add(windtype);
                cfetsEtrSecStockConfig.setWindtype(windtypeList);
            }else{
                cfetsEtrSecStockConfig.getWindtype().add(windtype);
            }
            map.put(key,cfetsEtrSecStockConfig);
        }
        Collection<CfetsEtrSecStockConfig> collection =map.values();
        return collection;
    }

    /**
     * 查询证券账户
     */
    public Page<SelectOptionDto> getSecManageAcctId(SecManageAcctIdRequestDto secManageAcctIdRequestDto) {
        Map<String,Object> pageParam = BeanUtil.object2Map(secManageAcctIdRequestDto);
        String portfolioId = (String)pageParam.get("portfolioId");
        if(StringUtil.isNotEmpty(pageParam.get("portfolioId"))){
            List<String> portfolioIdList = StringUtil.splitToList(",",(String)pageParam.get("portfolioId"));
            pageParam.put("portfolioIdList", portfolioIdList);
        }
        pageParam.put("S_002", DictCodeListExt3.资产类型_可质押持仓.getValue());
        pageParam.put("sysDate", iPlatformDate.getSysDateStr());
        return super.queryForPage(secManageAcctIdRequestDto.getPageMode().getPageNo(), secManageAcctIdRequestDto.getPageMode().getPageSize(),
                () -> cfetsEtrIbRepoinfoMapper.getSecManageAcctId(pageParam));
    }

    /**
     * <一句话功能简述>
     * 查询某系列下的资产明细
     * @param page
     * @param length
     * @param param
     * @return
     * @throws Exception 
     * @see
     * @since 1.0
     */
    public Page<EtrSecStockConfig> getAssetDetailBySeqno(int page, int length,
                                                                    Map<String, Object> param) throws Exception {
        return super.queryForPage(page, length, () ->cfetsEtrIbRepoinfoMapper.getAssetDetailBySeqno(param));
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @see
     * @since 1.0
     */
    public void insert(CfetsEtrIbRepoRkDto dto) {
    	//先删后插
    	cfetsEtrIbRepoinfoMapper.deleteByPrimaryKey(dto.getPledgeStockId());
    	cfetsEtrIbRepoinfoMapper.delete(dto.getPledgeStockId());
        etrSecStockMapper.deleteByPrimaryKey(dto.getPledgeStockId());
        CfetsEtrIbRepoRkDto record = new CfetsEtrIbRepoRkDto();
        record.setPledgeStockId(dto.getPledgeStockId());
        record.setPledgeStockName(dto.getPledgeStockName());
        etrSecStockMapper.insert(record);
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public int delete(CfetsEtrIbRepoRkDto dto) {
    	cfetsEtrIbRepoinfoMapper.deleteByPrimaryKey(dto.getPledgeStockId());
    	cfetsEtrIbRepoinfoMapper.delete(dto.getPledgeStockId());
        return etrSecStockMapper.deleteByPrimaryKey(dto.getPledgeStockId());
    }
    
    public int checkIsExistByPledgeStockName(Map<String, Object> map) {
        return cfetsEtrIbRepoinfoMapper.checkIsExistByPledgeStockName(map);
    }
    
    public CfetsEtrIbRepoRkDto getBusinessInfoByPledgeStockId(String pledgeStockId) {
        return cfetsEtrIbRepoinfoMapper.getBusinessInfoByPledgeStockId(pledgeStockId);
    }
    
    public int checkIsExistDetail(String pledgeStockId) {
        return cfetsEtrIbRepoinfoMapper.checkIsExistDetail(pledgeStockId);
    }
    
    public void inserttotalq(String pledgeStockId) {
    	//先删后插
    	cfetsEtrIbRepoinfoMapper.deleteByPrimaryKey(pledgeStockId);
    	cfetsEtrIbRepoinfoMapper.delete(pledgeStockId);
    	cfetsEtrIbRepoinfoMapper.deleteEtrEntrustPledgeByPledgeStockId(pledgeStockId);
    }
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @see
     * @since 1.0
     */
    public void inserttotal(EtrSecStockConfig dto) {
    	etrSecStockConfigMapper.insert(dto);
    }
    
    public void insertptl(PtlSecStock dto) {
    	ptlSecStockMapper.insert(dto);
    }
    
    @SuppressWarnings("rawtypes")
    public List<CfetsTreePortfolioResponse> getTreeNodeList(Map<String,Object> params){
    	User user = SecurityUtil.getLoginUser();
    	params.put("username", user.getUserName());
        List<CfetsTreePortfolioResponse> list=cfetsEtrIbRepoinfoMapper.findAllTask(params);
    	return list;
    }
    
    public CfetsEtrIbRepoRkDto getName(String pledgeStockId) {
        return cfetsEtrIbRepoinfoMapper.getName(pledgeStockId);
    }
    
    /**
     * 质押券出库，删除即可
     *
     * @return saveCount
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Long outSave(CfetsEtrRepoSaveRequest dto)  {
        Long deleteCount = 0L;
        if (!StringUtil.isEmptyList(dto.getSelectedItem())) {
            for (CfetsEtrRepoSaveRequest temp : dto.getSelectedItem()) {
                EtrEntrustPledge etrEntrustPledgeKey = new EtrEntrustPledge();
                etrEntrustPledgeKey.setPortfolioId(temp.getPortfolioId());
                etrEntrustPledgeKey.setFinprodId(temp.getFinprodId());
                etrEntrustPledgeKey.setBranch(temp.getBranch());
                etrEntrustPledgeKey.setSecAcctId(temp.getSecAcctId());
                etrEntrustPledgeKey.setPledgeStockId(temp.getPledgeStockId());
                
                deleteCount += cfetsEtrIbRepoinfoMapper.deleteEtrEntrustPledge(etrEntrustPledgeKey) ;
            }
        }
        /*if (!StringUtil.isEmptyList(dto.getSelectedItem())) {
            for (EtrIbRepo001Dto temp : dto.getSelectedItem()) {
                EtrEntrustPledgeKey etrEntrustPledgeKey = new EtrEntrustPledge();
                etrEntrustPledgeKey.setPortfolioId(temp.getPortfolioId());
                etrEntrustPledgeKey.setFinprodId(temp.getFinprodId());
                etrEntrustPledgeKey.setBranch(temp.getBranch());
                etrEntrustPledgeKey.setSecAcctId(temp.getSecAcctId());
                
                deleteCount += etrEntrustPledgeMapper.deleteByPrimaryKey(etrEntrustPledgeKey) ;
            }
        }*/
        return deleteCount;
    }
    
   public List<CfetsEtrRepoZLCXPositionResponse> selectF01List(Map<String, Object> pageParam){
       pageParam.put("A_012", DictCodeListExt3.资产类型_质押式双边回购本方券已质押持仓.getValue());
       pageParam.put("S_002", DictCodeListExt3.资产类型_可质押持仓.getValue());
       pageParam.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
       pageParam.put("sysDate", iPlatformDate.getSysDateStr());
       pageParam.put("TRADEPLACE01", DictCodeListExt1.交易场所_银行间.getValue());
       pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
       pageParam.put("VALUATION_SOURCE_CBC", DictCodeList.估值来源_中债登_CBC.getValue());
	   return cfetsEtrIbRepoinfoMapper.selectF01List(pageParam);
   }
   
   public int insertEtrEntrustPledge(EtrEntrustPledge etrEntrustPledge){
	   EtrEntrustPledge count = cfetsEtrIbRepoLib001Mapper.selectEtrEntrustPledge(etrEntrustPledge) ;
       if(count != null){
       	cfetsEtrIbRepoLib001Mapper.deleteEtrEntrustPledge(etrEntrustPledge) ;
       }
	   return etrEntrustPledgeMapper.insertSelective(etrEntrustPledge);
   }
   
   public List<CfetsEtrRepoZLCXPositionResponse> selectZlglPositionPage(Map<String, Object> pageParam){
	   
       pageParam.put("A_012", DictCodeListExt3.资产类型_质押式双边回购本方券已质押持仓.getValue());
       pageParam.put("S_002", DictCodeListExt3.资产类型_可质押持仓.getValue());
       pageParam.put("VALUATION_SOURCE_CBC", DictCodeList.估值来源_中债登_CBC.getValue());
       pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
       pageParam.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
       pageParam.put("sysDate", iPlatformDate.getSysDateStr());
       pageParam.put("TRADEPLACE01", DictCodeListExt1.交易场所_银行间.getValue());
       pageParam.put("TRADEPLACE03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
       pageParam.put("TRADEPLACE04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
       pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
       return cfetsEtrIbRepoinfoMapper.findZlglPositionPage(pageParam);
   }
   
   /**
    * 获取质押库下拉框
    * @return 质押库下拉框
    */
   public List<SelectOptionDto> getPledgeStockIdByPortfolioId(CfetsSearchPledgeStockRequest cfetsSearchPledgeStockRequest) {
	   Map<String,Object> param = BeanUtil.object2Map(cfetsSearchPledgeStockRequest);
	   param.put("userId",SecurityUtil.getLoginUser().getUserName());
       return  cfetsEtrIbRepoinfoMapper.getPledgeStockIdByPortfolioId(param);
   }

    /**
     * 获取质押库下拉框
     * @return 质押库下拉框
     */
    public List<SelectOptionDto> getPledgeStockId(CfetsSearchPledgeStockRequest cfetsSearchPledgeStockRequest) {
        Map<String,Object> param = BeanUtil.object2Map(cfetsSearchPledgeStockRequest);
        String userId = SecurityUtil.getLoginUser().getUserName();
        param.put("userId", userId);
        return  cfetsEtrIbRepoinfoMapper.getPledgeStockId(param);
    }

	public Long selectUserRolePosList(Map<String, Object> map) {
		return cfetsEtrIbRepoinfoMapper.selectUserRolePosList(map);
	}


    public String doRiskcalCheckMethod(CfetsEtribRepoInfo003Dto dto){
        //参数不能为空
        if(StringUtil.isEmpty(dto.getPs())){
            return "交易方向";
        }
        if(StringUtil.isEmptyList(dto.getEtrInvestRepoInfoDtos())){
            return "组合分配列表不能为空";
        }
        List<String> messages = new ArrayList<>();
        if(StringUtil.isNotEmpty(dto.getEtrInvestRepoInfoDtos())){
            for(CfetsEtribRepoInfo003Dto item:dto.getEtrInvestRepoInfoDtos()){
                if(!StringUtil.isNotEmpty(item.getPortfolioId())){
                    messages.add("请输入组合名称。");
                }
                if(!StringUtil.isNotEmpty(item.getEntrustAmt())){
                    messages.add("请输入指令金额。");
                }
            }
        }
        if(messages.size()>0) {
            return  StringUtil.joinString(messages,  GlobalConstant.VERTICAL_LINE);
        }
        return null;
    }

    public  List<Map<String, Object>> doRiskcalPackageEquity(CfetsEtribRepoInfo003Dto dto) {
        List<Map<String, Object>> dataMapList=new ArrayList<>();
        //风险试算封装dto
        List<Map> bussTrdTransfenpeiDto=new ArrayList<>();
        for(CfetsEtribRepoInfo003Dto pt:dto.getEtrInvestRepoInfoDtos()){
            pt.setInvAim(pt.getBusinessModel());
            pt.setTradeMarket(BussinessDictCodeList.交易场所_银行间.getValue());
            pt.setFinprodType2(DictCodeList.平安资产类型_质押式回购.getValue());
            pt.setVdate(dto.getEntrustDate());
            pt.setMdate(dto.getEntrustDate());;
            pt.setCcy("CNY");
            pt.setPs(dto.getPs());
            Map<String,Object> map = BeanUtil.object2Map(pt);
            map.put("vdate",DateUtil.format(dto.getEntrustDate()));
            dataMapList.add(map);
        }
        return dataMapList;

    }

    public  String getKriStatusCnByKriStatus(String kriStatus){
        if(StringUtil.isNotEmpty(kriStatus)){
            if(StringUtil.equals(kriStatus,"0")){
                return "正常";
            }else if(StringUtil.equals(kriStatus,"1")){
                return "预警";
            }else  if(StringUtil.equals(kriStatus,"2")){
                return "审批";
            }else  if(StringUtil.equals(kriStatus,"3")){
                return "违规";
            }else  if(StringUtil.equals(kriStatus,"4")){
                return "异常";
            }
        }
        return null;
    }

    public AjaxResult modifyDelegateCheckMethod(CfetsEtribRepoInfo003Dto dto){
        if(!StringUtil.isNotEmpty(dto)|| StringUtil.isEmptyList(dto.getSelectedItem())){
            AjaxResult res=new AjaxResult();
            ValidMessage validMessage= new ValidMessage();
            validMessage.setMessage(getMessage("COMMON_E_0002"));
            res.setErrorList(Arrays.asList(validMessage));
            return res;
        }
        List<CfetsEtribRepoInfo003Dto> selectedItem = dto.getSelectedItem();
        AjaxResult ajaxResult=new AjaxResult();
        if(StringUtil.isNotEmpty(selectedItem)){
            if(selectedItem.size()>1){
                AjaxResult res=new AjaxResult();
                ValidMessage  validMessage= new ValidMessage();
                validMessage.setMessage("委托指令修改仅允许一条数据");
                res.setErrorList(Arrays.asList(validMessage));
                return res;
            }
            for(CfetsEtribRepoInfo003Dto Item:selectedItem){
                if(!StringUtil.equals(Item.getEntrustStatus(),BussinessDictCodeList.委托接收状态_已撤回.getValue())
                        && !StringUtil.equals(Item.getEntrustStatus(),BussinessDictCodeList.委托状态_已退回.getValue())
                ){
                    AjaxResult res=new AjaxResult();
                    ValidMessage  validMessage= new ValidMessage();
                    validMessage.setMessage("只有指令状态为已撤回、已退回的指令允许修改");
                    res.setErrorList(Arrays.asList(validMessage));
                    return res;
                }

                if(StringUtil.isNotEmpty(Item.getEntrustId()))
                {
                    WflEtrEntrustKey wflEtrEntrustKey = new WflEtrEntrustKey();
                    wflEtrEntrustKey.setEntrustId(Item.getEntrustId());
                    wflEtrEntrustKey.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    WflEtrEntrust wflEtrEntrust = wflEtrEntrustMapper.selectByPrimaryKey(wflEtrEntrustKey);
                    if(wflEtrEntrust!=null){
                        AjaxResult res=new AjaxResult();
                        ValidMessage  validMessage= new ValidMessage();
                        validMessage.setMessage("存在流程中数据，无法修改。");
                        res.setErrorList(Arrays.asList(validMessage));
                        return res;
                    }
                }
            };
        }
        return new AjaxResult();
    }

    public CfetsEtribRepoInfo003Dto findEtrinfo(CfetsEtribRepoInfo003Dto dto) {
        Map<String,Object> param= BeanUtil.object2Map(dto);
        param.put("S_002", DictCodeListExt3.资产类型_可质押持仓.getValue());
        param.put("sysDate", iPlatformDate.getSysDateStr());
        param.put("VALUATION_SOURCE_CBC", DictCodeList.估值来源_中债登_CBC.getValue());
        return  cfetsEtrIbRepoinfoMapper.findEtrinfo(param);
    }



    /**
     * 获取当前月天数
     * @return
     */
    public String getTermType(Integer repoterm) {
        //对选择的数据进行check
        String termType  = null;
        if(repoterm!= null &&repoterm<=21){
            //期限品种小于21天
            if(repoterm==1){
                termType ="R001";
            }
            if(repoterm>1&&repoterm<=7){
                termType ="R007";
            }
            if(repoterm>7&&repoterm<=14){
                termType ="R014";
            }
            if(repoterm>14&&repoterm<=21){
                termType ="R021";
            }
            return termType;
        }
        int day=0;
        //date为空时  返回0
        Date date = iPlatformDate.getSysDate();
            //获取日期
            //获取之后月00天 即本月最后一天
        day =addDaysOfMonth(date,0);
        if(repoterm<=day){
            return "R1M";
        }
        day =addDaysOfMonth(date,1);
        if(repoterm<=day){
            return "R2M";
        }
        day =addDaysOfMonth(date,2);
        if(repoterm<=day){
            return "R3M";
        }
        day =addDaysOfMonth(date,3);
        if(repoterm<=day){
            return "R4M";
        }
        day =addDaysOfMonth(date,5);
        if(repoterm<=day){
            return "R6M";
        }
        day =addDaysOfMonth(date,8);
        if(repoterm<=day){
            return "R9M";
        }
        day =addDaysOfMonth(date,11);
        if(repoterm<=day){
            return "R1Y";
        }
        return null;
    }

    /**
     * 获取当前月 天数到指定月份天数 的合计
     * @return
     */
    public int addDaysOfMonth(Date date,int month) {
        int day = getDaysOfMonth(date);
        for(int i =0;i<month;i++){
            date = getPreMonth(date);
            day = day + getDaysOfMonth(date);//加下个月天数
        }
        return day;
    }

    /**
     * 获取当前月天数
     * @param date
     * @return
     */
    public int getDaysOfMonth(Date date) {
        Calendar calendar =Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取下一个月
     * @param date
     * @return
     */
    public Date getPreMonth(Date date) {
        Calendar calendar =Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH,1);
        return calendar.getTime();
    }


    public List<SelectOptionDto> queryMySideList(Map<String,Object> dto) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("sysDate",iPlatformDate.getSysDate());
        if(StringUtil.equals(dto.get("dealType"),"01")){
            //系统内的交易对手，即市场机构和资管产品的数据
            return joyinbusinessCommonMapper.queryCounterPartyList(param);
        }else if(StringUtil.equals(dto.get("dealType"),"02")){
            //银行间机构，且必须关联到系统的交易对手
            return joyinbusinessCommonMapper.queryMySideAndPartyList(param);
        }else if(StringUtil.equals(dto.get("dealType"),"03")){
            //CSTP下行接口对手方
            return joyinbusinessCommonMapper.queryCstpPartyList(param);
        }else{
            //组合关联的银行间机构,此处前端能拿到组合的21位码
            return joyinbusinessCommonMapper.queryMySideList(param);
        }
    }

    public BigDecimal getPledgeable(Map<String,Object> param) {
        BigDecimal pledgeable = BigDecimal.ZERO;
        param.put("A_012", DictCodeListExt3.资产类型_质押式双边回购本方券已质押持仓.getValue());
        param.put("S_002", DictCodeListExt3.资产类型_可质押持仓.getValue());
        param.put("sysDate", iPlatformDate.getSysDateStr());
        pledgeable = cfetsEtrIbRepoinfoMapper.getPledgeable(param);
        if (pledgeable == null) {
            pledgeable = BigDecimal.ZERO;
        }
        return pledgeable;
    }

    public AjaxResult checkBondInfo(@RequestBody CfetsEtrRepoBondInfoRequest request) {
        AjaxResult result = new AjaxResult();
        if (CollectionUtils.isEmpty(request.getCfetsEtrRepoZLCXPositionResponses())) {
            return result;
        }
        Map<String, Object> param = new HashMap<>();
        List<String> finProdIds = new ArrayList<>();
        for (CfetsEtrRepoZLCXPositionResponse response : request.getCfetsEtrRepoZLCXPositionResponses()) {
            finProdIds.add(response.getFinprodId());
        }
        param.put("finProdIds", finProdIds);
        param.put("platDate", iPlatformDate.getSysDateStr());
        if (StringUtil.equals(request.getPs(), DictCodeListExt3.交易方向_正回购.getValue())) {
            List<FinExercisePlan> finExercisePlans = cfetsIbuQuoteRepoMapper.findFinExercisePlan(param);
            if (CollectionUtils.isEmpty(finExercisePlans)) {
                return result;
            }
            Date settlementStartDate = null;
            // 回购期限
            int repoTerm = request.getRepoTerm().intValue();
            // 银行间交易日历的节假日取得
            List<String> holidayList = dateCalService.getDealCalendarHoliday(request.getEntrustDate(), DateUtil.addDay(request.getEntrustDate(), repoTerm + 15), "03");
            // 首次结算日：平台日+清算速度，根据银行间节假日规则遇节假日顺延至下一个工作日
            if (StringUtil.equals(request.getSettleSpreed(), BussinessDictCodeList.清算速度_T加0.getValue())) {
                settlementStartDate = request.getEntrustDate();
            }
            if (StringUtil.equals(request.getSettleSpreed(), BussinessDictCodeList.清算速度_T加1.getValue())) {
                settlementStartDate = dateCalService.calculationTradingDay(request.getEntrustDate(), 1, holidayList, "1");
            }
            if (StringUtil.equals(request.getSettleSpreed(), BussinessDictCodeList.清算速度_T加2.getValue())) {
                settlementStartDate = dateCalService.calculationTradingDay(request.getEntrustDate(), 2, holidayList, "1");
            }
            Date SettlementEndDate = null;
            // 到期结算日=首次结算日+回购期限，遇银行间节假日调整到下一工作日
            if (StringUtil.isNotEmpty(settlementStartDate)) {
                SettlementEndDate = DateUtil.addDay(settlementStartDate, repoTerm - 1);
                SettlementEndDate =
                        dateCalService.calculationTradingDay(SettlementEndDate, 1, holidayList, "1");
            }
            for (FinExercisePlan finExercisePlan : finExercisePlans) {
                for (CfetsEtrRepoZLCXPositionResponse response : request.getCfetsEtrRepoZLCXPositionResponses()) {
                    if (StringUtil.isNotEmpty(SettlementEndDate)
                            && DateUtil.compareTo(SettlementEndDate, response.getMdate()) < 0) {
                        result.setMessage("所选质押券" + response.getFinprodMarketId() +
                                response.getFinprodAbbr() + "到期日为" + response.getMdate() + "，不可小于等于回购到期结算日", MessageType.错误消息);
                        return result;
                    }
                    if (DateUtil.compareTo(finExercisePlan.getExerciseDate(), SettlementEndDate) < 0) {
                        result.setMessage("债券" + response.getFinprodMarketId() +
                                response.getFinprodAbbr() + "已做赎回登记，是否确认继续质押", MessageType.确认消息);
                    } else {
                        result.setMessage("债券" + response.getFinprodMarketId() +
                                response.getFinprodAbbr() + "已赎回登记，且到期结算日大于等于理论行权日，不可质押", MessageType.错误消息);
                    }
                    return result;
                }
            }
        }
        return result;
    }
}


