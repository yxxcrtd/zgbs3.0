package com.joyintech.tams.business.asset.bond.service;

import com.github.pagehelper.PageInfo;
import com.joyintech.calculation.bonds.constant.Variety;
import com.joyintech.calculation.shell.PriceByYieldCalculator;
import com.joyintech.calculation.shell.YieldByPriceCalculator;
import com.joyintech.calculation.shell.param.*;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.file.SafelyFile;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.asset.bond.dao.FinBondAssetMapper;
import com.joyintech.tams.business.asset.bond.dto.*;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.asset.dto.BussFinRepaymentDto;
import com.joyintech.tams.component.business.asset.dto.FinPayInterestStdInfoDto;
import com.joyintech.tams.component.business.basicdata.dto.BusiFinProductRatingDto;
import com.joyintech.tams.component.business.basicdata.service.BusiProductRatingService;
import com.joyintech.tams.component.business.bond.BusiFinExercisePlanService;
import com.joyintech.tams.component.business.bond.dto.BusiFinExercisePlanDto;
import com.joyintech.tams.component.business.common.dto.InterfPayhsrcsecinfoDto;
import com.joyintech.tams.component.business.common.service.CommonAutoCouponService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.dto.FileCommonDto;
import com.joyintech.tams.component.business.core.service.MstFileCommonService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.asset.common.dto.BaseFinProductBaseInfoDto;
import com.joyintech.tams.invest.asset.common.service.AssetCommonService;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.calculator.AdjustedTradeDayCal;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.systemtools.dao.JoyinFSMapper;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 债券标的信息新建
 *
 * @author wangyang1
 * @version 1.0 2022年4月13日
 * @see FinBondAsset001Service
 * @since 1.0
 */
@Service
public class FinBondAsset001Service extends BaseService{


    @Autowired(required = false)
    private FinBondAssetMapper finBondAssetMapper;

    /**
     * 使用给定的sqlID检索分页数据。返回类型是Service的泛型。
     *
     * @param request request
     * @return Page类 Page
     * @throws Exception 异常信息
     */
    public Page<FinBondAssetResponse> selectForPage(FinBondAssetRequest request) {
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        pageParam.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        pageParam.put("FINPROD_TYPE_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        pageParam.put("cashType",DictCodeListExt1.现金流二级类型_利息.getValue());
        pageParam.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());//现金流业务类型
        if (CollectionUtils.isNotEmpty(request.getIdate())) {
            String idateStart = request.getIdate().get(0);
            if (StringUtil.isNotEmpty(idateStart)){
                pageParam.put("idateStart",idateStart);
            }
            String idateEnd = request.getIdate().get(1);
            if (StringUtil.isNotEmpty(idateEnd)){
                pageParam.put("idateEnd",idateEnd);
            }
        }
        if (CollectionUtils.isNotEmpty(request.getVdate())) {
            String vdateStart = request.getVdate().get(0);
            if (StringUtil.isNotEmpty(vdateStart)){
                pageParam.put("vdateStart",vdateStart);
            }
            String vdateEnd = request.getVdate().get(1);
            if (StringUtil.isNotEmpty(vdateEnd)){
                pageParam.put("vdateEnd",vdateEnd);
            }
        }
        if (CollectionUtils.isNotEmpty(request.getMdate())) {
            String mdateStart = request.getMdate().get(0);
            if (StringUtil.isNotEmpty(mdateStart)){
                pageParam.put("mdateStart",mdateStart);
            }
            String mdateEnd = request.getMdate().get(1);
            if (StringUtil.isNotEmpty(mdateEnd)){
                pageParam.put("mdateEnd",mdateEnd);
            }
        }
        if (CollectionUtils.isNotEmpty(request.getExerciseDate())) {
            String exerciseDateStart = request.getExerciseDate().get(0);
            if (StringUtil.isNotEmpty(exerciseDateStart)){
                pageParam.put("exerciseDateStart",exerciseDateStart);
            }
            String exerciseDateEnd = request.getExerciseDate().get(1);
            if (StringUtil.isNotEmpty(exerciseDateEnd)){
                pageParam.put("exerciseDateEnd",exerciseDateEnd);
            }
        }
        List<String> type9List = new ArrayList<>();
        List<String> type10List = new ArrayList<>();
        List<String> type11List = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(request.getAssetType())) {
            for (int i = 0; i < request.getAssetType().size(); i++) {
                if (request.getAssetType().get(i).size()>=1){
                     String type9 = request.getAssetType().get(i).get(0);
                    type9List.add(type9);
                }
                if (request.getAssetType().get(i).size()>=2){
                    String type10 = request.getAssetType().get(i).get(1);
                    type10List.add(type10);
                }
                if (request.getAssetType().get(i).size()>=3){
                    String type11 = request.getAssetType().get(i).get(2);
                    type11List.add(type11);
                }
            }
        }
        pageParam.put("type9",CollectionUtils.isNotEmpty(type9List)?type9List:null);
        pageParam.put("type10", CollectionUtils.isNotEmpty(type10List)?type10List:null);
        pageParam.put("type11", CollectionUtils.isNotEmpty(type11List)?type11List:null);
        pageParam.put("finprodIdList", StringUtil.splitToList(null, request.getFinprodId()));
        pageParam.put("tradeMarketList", StringUtil.splitToList(null, request.getTradeMarketId()));
        pageParam.put("ccyList", StringUtil.splitToList(null, request.getCcy()));
        pageParam.put("registOrgList", StringUtil.splitToList(null, request.getRegistOrg()));
        pageParam.put("rateTypeList", StringUtil.splitToList(null, request.getRateType()));
        pageParam.put("couponSpeciesList", StringUtil.splitToList(null, request.getCouponSpecies()));
        pageParam.put("exerciseTypeList", StringUtil.splitToList(null, request.getExerciseType()));
        pageParam.put("issuerList", StringUtil.splitToList(null, request.getIssuer()));
        pageParam.put("payCycleList", StringUtil.splitToList(null, request.getPayCycle()));
        pageParam.put("crossMarketCodeList", StringUtil.splitToList(null, request.getCrossMarketCode()));
        pageParam.put("mGradeResultList", StringUtil.splitToList(null, request.getmGradeResult()));
        pageParam.put("bGradeResultList", StringUtil.splitToList(null, request.getbGradeResult()));
        //添加是否新增债券查询条件 add by liupeilin 2020-01-15 begin
//        if(!pageParam.containsKey("isFileter")){
//            pageParam.put("isFileter", "");
//        }
        pageParam.put("platDate", DateUtil.format(DateUtil.platDateFromCache()));
        //添加是否新增债券查询条件 add by liupeilin 2020-01-15 end
        pageParam.put("MAIN_GRADE_TYPE_01", DictCodeList.评级类型_主体评级.getValue());
        pageParam.put("MAIN_GRADE_TYPE_02", DictCodeList.评级类型_债项评级.getValue());
        pageParam.put("sort",request.getPageMode().getSort());
        pageParam.put("direction",request.getPageMode().getDirection());
        Page<FinBondAssetResponse> page
                =  super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(),
                () -> finBondAssetMapper.queryForPage(pageParam));
        return page;
    }


    /**
     * 查询债券资产代码
     *
     * <功能详细描述>
     * @param request
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getCrossMarketCode(SelectOptionCommonRequest request) {
        Map<String, Object> param = BeanUtil.object2Map(request);
        param.put("FINPROD_TYPE_TG_ZQ", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        //增加母资管计划筛选条件
        if (null!=request.getTradeMarket() && (StringUtil.equals(request.getTradeMarket(),DictCodeListExt1.交易场所_上海证券交易所.getValue())
        || StringUtil.equals(request.getTradeMarket(),DictCodeListExt1.交易场所_深圳证券交易所.getValue()))){
            List<String> tradeMarkets = new ArrayList<>();
            tradeMarkets.add(DictCodeListExt1.交易场所_银行间.getValue());
            tradeMarkets.add(DictCodeListExt1.交易场所_商业银行柜台.getValue());
            param.put("tradeMarkets", tradeMarkets);
        }
        param.put("finprodMarketIdList", StringUtil.splitToList(null,request.getFinprodMarketId()));
        Page<SelectOptionDto> page = super.queryForPage(request.getPageMode().getPageNo(),
                request.getPageMode().getPageSize(),
                () -> finBondAssetMapper.getCrossMarketCode(param));
        return page;
    }

    /**
     * 查询债券资产代码
     *
     * <功能详细描述>
     * @param request
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getAssetId(SelectOptionCommonRequest request) {
        Map<String, Object> param = BeanUtil.object2Map(request);
        param.put("FINPROD_TYPE_TG_ZQ", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        //增加母资管计划筛选条件
        if (StringUtil.isNotEmpty(param.get("finprodType2"))) {
            if(StringUtil.equals(param.get("finprodType2"), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())){
                param.put("finprodType2", StringUtil.splitToList(null, param.get("finprodType2").toString()+","+DictCodeListExt1.金融产品类型_投管用_母资管计划.getValue()));
            }else{
                param.put("finprodType2", StringUtil.splitToList(null, param.get("finprodType2").toString()));
            }
        }else {
            param.put("finprodType2",null);
        }
        Page<SelectOptionDto> page = super.queryForPage(request.getPageMode().getPageNo(),
                request.getPageMode().getPageSize(),
                () -> finBondAssetMapper.getAssetId(param));
        return page;
    }

    /**
     * 发行人联想
     *
     * <功能详细描述>
     * @param request
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getIssuerId(SelectOptionCommonRequest request) {
        Map<String, Object> map = BeanUtil.object2Map(request);
        map.put("IS_ISSUER", DictCodeList.是否_是.getValue());
        Page<SelectOptionDto> page = super.queryForPage(request.getPageMode().getPageNo(),
                request.getPageMode().getPageSize(),
                () -> finBondAssetMapper.getCustomer(map));
        return page;
    }

    /**
     * 操作记录查询
     *
     * @param dto
     * @return Page<FieldEtrInfoOperateListResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public Page<SearchInfoOperateListResponse> getSearchTabInfoQueryOperate(SearchOperateListRequest dto) {

        //dto转成map
        Map<String,Object> pageParam = BeanUtil.object2Map(dto);
        List<String> entrustIdList = StringUtil.splitToList(",",(String)pageParam.get("entrustId"));
        if(StringUtil.isBlank(dto.getEntrustId()) || entrustIdList.size() > 1){
            return new Page<SearchInfoOperateListResponse>();
        }else{
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                    () -> finBondAssetMapper.getSearchTabInfoQueryOperate(pageParam));
        }
    }

    public Page<SelectOptionDto> getFinprodMarketIdList(SelectOptionCommonRequest request) {
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        param.put("search", request.getSearch());
        Page<SelectOptionDto> page = super.queryForPage(request.getPageMode().getPageNo(),
                request.getPageMode().getPageSize(),
                () -> finBondAssetMapper.getFinProdMarketIdList(param));
        return page;
    }
}
