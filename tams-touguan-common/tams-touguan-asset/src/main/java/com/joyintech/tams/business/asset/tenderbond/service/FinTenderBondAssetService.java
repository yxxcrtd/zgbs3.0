package com.joyintech.tams.business.asset.tenderbond.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.util.ValidUtils;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.FinProductIssueMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.business.asset.tenderbond.dao.FinTenderBondAssetMapper;
import com.joyintech.tams.business.asset.tenderbond.dto.*;
import com.joyintech.tams.invest.common.asset.dao.AssetCommon002Mapper;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * 新债标的服务类
 *
 * @author gwl
 * @version 1.0 2022年4月11日
 * @see FinTenderBondAssetService
 * @since 1.0
 */
@Service
public class FinTenderBondAssetService extends BaseService<FinTenderBondAsset002Dto> implements BaseProcessService<FinTenderBondAsset002Dto> {

    @Resource
    private FinTenderBondAssetMapper finTenderBondAssetMapper;

    @Resource
    private FinProductMapper finProductMapper;

    @Resource
    private FinProductAddMapper finProductAddMapper;

    @Resource
    private FinProductTypeMapper finProductTypeMapper;

    @Resource
    private FinProductIssueMapper finProductIssueMapper;

    @Autowired
    private AssetCommon002Mapper assetCommonMapper;

    @Resource
    private IPlatformDate iPlatformDate;

    @Resource
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Override
    public void doInsert(FinTenderBondAsset002Dto dto, OperationType type) {
        if (OperationType.DELETE == type) {
            this.saveOperate(dto.getFinprodMarketId(), dto.getRemark(), DictCodeListExt3.操作类型_删除,dto.getWorkflowId());
        } else if (OperationType.NEW == type) {
            this.saveOperate(dto.getFinprodMarketId(), "", DictCodeListExt3.操作类型_新建,dto.getWorkflowId());
        } else if (OperationType.MODIFY == type) {
            this.saveOperate(dto.getFinprodMarketId(), "", DictCodeListExt3.操作类型_修改,dto.getWorkflowId());
        }
    }

    @Override
    public void doDelete(String workflowId) {
        CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
        oprHisReq.setWorkflowId(workflowId);
        oprHisReq.setBusiDes("作废");
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
    }

    @Override
    public String generateTitle(FinTenderBondAsset002Dto entity, OperationType operationType) {
        String isssueClass = "";
        if(Objects.nonNull(entity.getIssueDto()) && StringUtil.isNotEmpty(entity.getIssueDto().getIssueClass())) {
            isssueClass = SysDataUtil.getNameFromDataDict(DictCode.新债_发行方式,entity.getIssueDto().getIssueClass());
        }
        return String.format("资产代码(%s) 资产简称(%s) 发行方式(%s)", StringUtil.isEmpty(entity.getFinprodMarketId()) ? "" : entity.getFinprodMarketId(),
                StringUtil.isEmpty(entity.getFinprodAbbr()) ? "" : entity.getFinprodAbbr(),isssueClass);
    }

    @Override
    public void doComplete(FinTenderBondAsset002Dto dto, OperationType type) {
        if (OperationType.DELETE == type) {
            this.delete(dto);
        } else if (OperationType.NEW == type) {
            this.insert(dto);
        } else if (OperationType.MODIFY == type) {
            this.update(dto);
        }
    }

    /**
     * 分页查询
     *
     * @param search
     * @return
     */
    public Page<FinTenderBondAsset001Dto> selectForPage(FinTenderBondAssetSearch search) {
        search.setPlatformDate(iPlatformDate.getSysDateStr());
        search.setFinprodType2(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
        List<String> type9List = new ArrayList<>();
        List<String> type10List = new ArrayList<>();
        List<String> type11List = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(search.getType())) {
            for (int i = 0; i < search.getType().size(); i++) {
                if (search.getType().get(i).size()>=1){
                    String type9 = search.getType().get(i).get(0);
                    type9List.add(type9);
                }
                if (search.getType().get(i).size()>=2){
                    String type10 = search.getType().get(i).get(1);
                    type10List.add(type10);
                }
                if (search.getType().get(i).size()>=3){
                    String type11 = search.getType().get(i).get(2);
                    type11List.add(type11);
                }
            }
            search.setType9List(type9List);
            search.setType10List(type10List);
            search.setType11List(type11List);
        }
        if(StringUtil.isNotEmpty(search.getPageMode().getSort())) {
            if(search.getPageMode().getSort().endsWith("Value")) {//特殊处理数据字典的排序问题
                search.getPageMode().setSort(search.getPageMode().getSort().substring(0,search.getPageMode().getSort().length()-5));
            }
        }
        PageHelper.startPage(search.getPageMode().getPageNo(), search.getPageMode().getPageSize());
        List<FinTenderBondAsset001Dto> list = finTenderBondAssetMapper.selectForPage(search);
        return new Page<>(PageInfo.of(list));
    }

    /**
     * 通过主键查询列表数据
     *
     * @param finprodId
     * @return
     */
    public FinTenderBondAsset002Dto getById(String finprodId) {
        //1.查金融产品标的表
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
        if (Objects.isNull(finProduct)) return null;
        FinTenderBondAsset002Dto entity = new FinTenderBondAsset002Dto();
        BeanUtil.copyPropertiesIgnoreNull(entity, finProduct);
        //2.查金融产品标的附属表
        FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(finprodId);
        FinProductAddDto finProductAddDto = new FinProductAddDto();
        BeanUtil.copyPropertiesIgnoreNull(finProductAddDto, finProductAdd);
        entity.setProductAddDto(finProductAddDto);
        if(StringUtil.isNotEmpty(finProductAdd.getDealMode())) {
            if(StringUtil.equals("01",finProductAdd.getDealMode())) {
                entity.setCalModel("rate");
            }else {
                entity.setCalModel("price");
            }
        }
        //3.查金融产品标的附属表
        FinProductTypeKey typeKey = new FinProductTypeKey();
        typeKey.setBranch(0l);
        typeKey.setFinprodId(finProduct.getFinprodId());
        FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(typeKey);
        entity.setType9(finProductType.getType9());
        entity.setType10(finProductType.getType10());
        entity.setType11(finProductType.getType11());
        //4.查标的类金融产品发行信息
        FinProductIssue productIssue = finProductIssueMapper.selectByPrimaryKey(finprodId);
        if(Objects.nonNull(productIssue)) {
            FinProductIssueDto productIssueDto = new FinProductIssueDto();
            BeanUtil.copyPropertiesIgnoreNull(productIssueDto, productIssue);
            entity.setIssueDto(productIssueDto);
        }
        //4.查债券主体和债项评级
        entity.setRatingList(finTenderBondAssetMapper.selectBondCreditRating(finProduct.getFinprodMarketId(), iPlatformDate.getSysDateStr()));
        return entity;
    }

    /**
     * 加券
     *
     * @param finprodMarketId
     * @return
     */
    public FinTenderBondAsset002Dto addCoupons(String finprodMarketId) {
        Map<String, String> param = new HashMap<>();
        param.put("finprodType2", DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
        param.put("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        param.put("TRADE_PLACE_02", DictCodeListExt1.交易场所_商业银行柜台.getValue());
        param.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        param.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        param.put("platformDate",iPlatformDate.getSysDateStr());
        param.put("finprodMarketId", finprodMarketId);
        String finprodId = finTenderBondAssetMapper.selectFinprodId(param);
        if (StringUtil.isNotEmpty(finprodId)) {
            return getById(finprodId);
        } else {//从接口中获取
            return getFromInterface(param);
        }
    }

    /**
     * 获取接口中的新债数据
     *
     * @param param
     * @return
     */
    public FinTenderBondAsset002Dto getFromInterface(Map<String, String> param) {
        List<FinTenderBondAsset002Dto> list = finTenderBondAssetMapper.getProductFromInterface(param);
        if(CollectionUtils.isEmpty(list)) return null;
        FinTenderBondAsset002Dto dto = list.get(0);
        dto.setCalModel("price");//默认计算价格
        FinProductTypeConfig config = new FinProductTypeConfig();
        config.setConfigType(DictCodeListExt2.配置类型_WIND分类.getValue());
        config.setInputValue2(dto.getType9());
        config.setInputValue3(dto.getType10());
        List<FinProductTypeConfig> resultTypeList = assetCommonMapper.getAssetThreeType(config);
        if(CollectionUtils.isNotEmpty(resultTypeList)) {
            dto.setType9(resultTypeList.get(0).getInputValue2());
            dto.setType10(resultTypeList.get(0).getInputValue3());
            dto.setType11(resultTypeList.get(0).getInputValue4());
        }
        FinProductAddDto addDto = finTenderBondAssetMapper.getProductAddFromInterface(param);
        addDto.setIsAutoUpdate(DictCodeList.是否_否.getValue());
        if(addDto != null) {
            if(StringUtil.isNotEmpty(addDto.getCrossMarketCode())) {
                addDto.setCrossMarket(DictCodeList.是否_是.getValue());
            }
            dto.setProductAddDto(addDto);
        }
        //dto.setCapiIncomeFeature(DictCodeList.是否_否.getValue());
        if(list.size() > 1 ) {
            param.put("flag","Y");
        }
        dto.setIssueDto(finTenderBondAssetMapper.getProductIssueFromInterface(param));
        return dto;
    }


    /**
     * 新增
     *
     * @param dto
     */
    public void insert(FinTenderBondAsset002Dto dto) {
        if (StringUtil.isEmpty(dto.getCalendarId())) {//根据交易场所固定交易日历
            if (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())) {
                dto.setCalendarId("03");
            } else if (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_商业银行柜台.getValue())) {
                dto.setCalendarId("06");
            }else if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                dto.setCalendarId("04");
            }else if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                dto.setCalendarId("05");
            }
        }
        if(Objects.isNull(dto.getIssueDto().getAddBatch())) {
            dto.getIssueDto().setAddBatch(0l);
        }
        //1.保存金融产品标的表
        //金融产品类型_资产代码_增发批次_交易场所进行拼接
        dto.setFinprodType2(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
        dto.setFinprodType(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
        dto.setFinprodId(getFinprodId(dto));
        FinProduct finProduct = new FinProduct();
        BeanUtil.copyPropertiesIgnoreNull(finProduct, dto);
        finProductMapper.insert(finProduct);
        //2.保存金融产品标的附属表
        FinProductAdd productAdd = new FinProductAdd();
        BeanUtil.copyPropertiesIgnoreNull(productAdd, dto.getProductAddDto());
        productAdd.setFinprodId(dto.getFinprodId());
        if(StringUtil.isNotEmpty(dto.getCalModel())) {
            if(StringUtil.equals("rate",dto.getCalModel())) {
                productAdd.setDealMode("01");
            }else {
                productAdd.setDealMode("02");
            }
        }
        finProductAddMapper.insert(productAdd);
        //3.保存标的类金融产品发行信息
        FinProductIssue productIssue = new FinProductIssue();
        BeanUtil.copyPropertiesIgnoreNull(productIssue, dto.getIssueDto());
        productIssue.setFinprodId(dto.getFinprodId());
        finProductIssueMapper.insert(productIssue);
        //4.保存融产品分类表
        FinProductType finProductType = new FinProductType();
        BeanUtil.copyPropertiesIgnoreNull(finProductType, dto);
        finProductType.setBranch(0l);
        finProductType.setType8(DictCodeListExt2.平安资产类型_债券.getValue());
        FinProductTypeConfig config = new FinProductTypeConfig();
        config.setConfigType(DictCodeListExt2.配置类型_WIND分类.getValue());
        config.setInputValue2(dto.getType9());
        config.setInputValue3(dto.getType10());
        config.setInputValue4(dto.getType11());
        FinProductTypeConfig resultType = finTenderBondAssetMapper.getWindType(config);
        if(Objects.nonNull(resultType)) {
            finProductType.setType6(resultType.getOutputValue1());
            finProductType.setType7(resultType.getOutputValue2());
        }
        finProductTypeMapper.insert(finProductType);
    }

    /**
     * 修改
     *
     * @param dto
     */
    public void update(FinTenderBondAsset002Dto dto) {
        //采用先删除在插入的方式处理
        FinProduct product = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
        if(Objects.nonNull(product)) {
            dto.setRemark(product.getRemark());
            dto.setCreateTime(product.getCreateTime());
            dto.setCreateUser(product.getCreateUser());
            dto.setCreateDept(product.getCreateDept());
        }
        this.delete(dto);
        this.insert(dto);
    }

    /**
     * 删除
     *
     * @param dto
     */
    public void delete(FinTenderBondAsset002Dto dto) {
        //1.删除融产品分类表
        FinProductTypeKey typeKey = new FinProductTypeKey();
        typeKey.setBranch(0l);
        typeKey.setFinprodId(dto.getFinprodId());
        finProductTypeMapper.deleteByPrimaryKey(typeKey);
        //2.删除标的类金融产品发行信息
        finProductIssueMapper.deleteByPrimaryKey(dto.getFinprodId());
        //3.删除金融产品标的附属表
        finProductAddMapper.deleteByPrimaryKey(dto.getFinprodId());
        //4.删除金融产品标的表
        finProductMapper.deleteByPrimaryKey(dto.getFinprodId());
    }

    /**
     * 保存操作类型
     *
     * @param tradeId
     * @param remark
     * @param operType
     */
    public void saveOperate(String tradeId, String remark, DictCodeListExt3 operType,String workFlowId) {
        CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
        oprHisReq.setEntrustId(tradeId);
        oprHisReq.setOperType(operType.getValue());
        oprHisReq.setBusiDes(remark);
        oprHisReq.setWorkflowId(workFlowId);
        oprHisReq.setBusiType(BussinessDictCodeList.共同业务类型_新债标的.getValue());
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
    }

    /**
     * 资产代码/简称查询
     *
     * @param param
     * @return
     */
    public Page<SelectOptionDto> getFinprodIdForSelect(Map<String, Object> param) {
        param.put("finprodType2", DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
        param.put("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        param.put("TRADE_PLACE_02", DictCodeListExt1.交易场所_商业银行柜台.getValue());
        param.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        param.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        param.put("platDate", DateUtil.platDateFromCache());
        PageHelper.startPage((Integer) param.get("pageNo"), (Integer) param.get("pageSize"));
        List<SelectOptionDto> list = finTenderBondAssetMapper.getFinprodIdForSelect(param);
        return new Page<>(PageInfo.of(list));
    }

    /**
     * 上市代码简称查询
     *
     * @param param
     * @return
     */
    public Page<SelectOptionDto> getAppearMarketIdForSelect(Map<String, Object> param) {
        param.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        param.put("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        param.put("TRADE_PLACE_02", DictCodeListExt1.交易场所_商业银行柜台.getValue());
        param.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        param.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        param.put("platDate", DateUtil.platDateFromCache());
        PageHelper.startPage((Integer) param.get("pageNo"), (Integer) param.get("pageSize"));
        List<SelectOptionDto> list = finTenderBondAssetMapper.getAppearMarketIdForSelect(param);
        return new Page<>(PageInfo.of(list));
    }

    /**
     * 跨市场代码简称查询
     *
     * @param param
     * @return
     */
    public Page<SelectOptionDto> getCrossMarketCodeForSelect(Map<String, Object> param) {
        param.put("finprodType2", DictCodeList.平安资产类型_债券.getValue());
        param.put("platDate", DateUtil.platDateFromCache());
        PageHelper.startPage((Integer) param.get("pageNo"), (Integer) param.get("pageSize"));
        if(Objects.nonNull(param.get("initValue"))) {
            String initValue = (String) param.get("initValue");
            if(StringUtil.isNotEmpty(initValue) && initValue.contains(",")) {
                List<String> values = new ArrayList<>();
                 String arr[] = initValue.split(",");
                 for(String a : arr) {
                     if(StringUtil.isNotEmpty(a)) {
                         values.add(a);
                     }
                 }
                 param.put("initList",values);
                 param.remove("initValue");
            }
        }
        List<SelectOptionDto> list = finTenderBondAssetMapper.getCrossMarketCodeForSelect(param);
        return new Page<>(PageInfo.of(list));
    }

    /**
     * 利率行情查询
     *
     * @param param
     * @return
     */
    public Page<SelectOptionDto> getIntRateForSelect(Map<String, Object> param) {
        param.put("intRateType", DictCodeList.行情类型_利率.getValue());
        param.put("platDate", DateUtil.platDateFromCache());
        PageHelper.startPage((Integer) param.get("pageNo"), (Integer) param.get("pageSize"));
        List<SelectOptionDto> list = finTenderBondAssetMapper.getIntRateForSelect(param);
        return new Page<>(PageInfo.of(list));
    }

    /**
     * 上市代码和上市日期是否可以编辑
     *
     * @param finprodId
     * @return
     */
    public Boolean editCheck(String finprodId) {
        // 同时需要判断该资产是否存在有效上市流通数据，如存在，则不允许修改上市日、上市代码
        Integer qty = finTenderBondAssetMapper.countListing(finprodId);
        return qty == null ? true : qty > 0 ? false : true;
    }


    /**
     * 根据发行价格计算参考收益率/根据参考收益率计算发行价格
     * 参考收益率和发行价格计算公式：
     * 起息日到期日换算成年，记为A
     * 当时A小于等于一年时：参考收益率=（100-发行价格）/（发行价格*A）
     * 当时A大于一年时：参考收益率=（（100/发行价格）的（1/A）次方）-1
     * 当时A小于等于一年时：发行价格= 100 / （(A * 参考收益率) + 1）
     * 当时A大于一年时：发行价格= 100/（（参考收益率+1） 的 A 次方）
     *
     * @param dto
     * @return
     */
    public CalcDto calc(CalcDto dto) {
        BigDecimal hundred = new BigDecimal("100");
        long day = DateUtil.getBetweenDate(dto.getVdate(), dto.getMdate(), DateUtil.DurationType.天);
        BigDecimal year = ArithmeticUtil.divide(new BigDecimal(day), new BigDecimal("365"), 14);
        if (StringUtil.equals(dto.getCalModel(), "rate")) {//计算参考收益率
            if (year.compareTo(BigDecimal.ONE) <= 0) {
                dto.setIntRate(ArithmeticUtil.divide(ArithmeticUtil.subtract(hundred, dto.getIssuePrice()), ArithmeticUtil.multiply(dto.getIssuePrice(), year), 14));
            } else {
                double a = Math.pow(ArithmeticUtil.divide(hundred, dto.getIssuePrice(), 14).doubleValue(), ArithmeticUtil.divide(BigDecimal.ONE, year, 14).doubleValue());
                dto.setIntRate(ArithmeticUtil.subtract(new BigDecimal(a), BigDecimal.ONE));
            }
        } else {//计算发行价格
            if (year.compareTo(BigDecimal.ONE) <= 0) {
                dto.setIssuePrice(ArithmeticUtil.divide(hundred, ArithmeticUtil.doAdd(ArithmeticUtil.multiply(dto.getIntRate(), year), BigDecimal.ONE), 14));
            } else {
                BigDecimal r = ArithmeticUtil.doAdd(dto.getIntRate(), BigDecimal.ONE);
                double temp = Math.pow(r.doubleValue(), year.doubleValue());
                dto.setIssuePrice(ArithmeticUtil.divide(hundred, new BigDecimal(temp), 14));
            }
        }
        return dto;
    }

    /**
     * 判断是否在流程中
     *
     * @param pk1
     * @return
     */
    public Boolean isInProcess(String pk1, String... pageId) {
        if (StringUtil.isEmpty(pk1)) return Boolean.FALSE;
        WflValidInputParameter wflValidInputParameter = null;
        for (String str : pageId) {
            wflValidInputParameter = new WflValidInputParameter();
            wflValidInputParameter.setPageId(str);
            wflValidInputParameter.setBusinessDataPk1(pk1);
            wflValidInputParameter.setSeachMode("01");
            List<String> result = finTenderBondAssetMapper.getBusinessData(wflValidInputParameter);
            if (CollectionUtils.isNotEmpty(result)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }


    public List<ValidMessage> validFinprodMarketId(FinTenderBondAsset002Dto dto) {
        List<ValidMessage> validList = new ArrayList<>();
        Map<String, String> param = new HashMap<>();
        param.put("finprodMarketId", dto.getFinprodMarketId());
        if(StringUtil.isNotEmpty(finTenderBondAssetMapper.selectFinprodId(param))) {
            validList.add(new ValidMessage("资产代码已存在"));
        }
        return validList;
    }

    /**
     * 保存、修改提交校验
     *
     * @param dto
     * @return
     */
    public List<ValidMessage> valid(FinTenderBondAsset002Dto dto) {
        List<ValidMessage> validList = new ArrayList<>();
        String operateType = dto.getOperateType();
        if (StringUtil.isEmpty(dto.getWorkflowId()) &&  isInProcess(dto.getFinprodMarketId(), "FinTenderBondAsset002", "FinTenderBondAsset003")) {
            validList.add(new ValidMessage("存在流程中的数据"));
        }
        if (StringUtil.isEmpty(operateType)) {
            validList.add(new ValidMessage("操作类型不能为空"));
        }
        if (StringUtil.equals(operateType, OperationType.NEW.name())) {
            this.emptyCheck(dto, validList);
            if(StringUtil.isNotEmpty(dto.getFinprodMarketId()) && dto.getFinprodMarketId().length() > 20) {
                validList.add(new ValidMessage("资产代码长度不能超过20"));
            }
            if(StringUtil.isNotEmpty(dto.getFinprodMarketId()) && !dto.getFinprodMarketId().matches("^[A-Za-z0-9]+.[A-Za-z0-9]+$")) {
                validList.add(new ValidMessage("资产代码只能由数字和字母组成"));
            }
            if (StringUtil.isNotEmpty(dto.getFinprodMarketId()) && StringUtil.isNotEmpty(dto.getTradeMarket())) {
                Map<String, String> param = new HashMap<>();
                param.put("finprodMarketId", dto.getFinprodMarketId());
                if(StringUtil.isNotEmpty(finTenderBondAssetMapper.selectFinprodId(param))) {
                    validList.add(new ValidMessage("资产代码已存在"));
                }
                String finprodId = getFinprodId(dto);
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
                if (Objects.nonNull(finProduct)) { //同一资产代码只能存在一条数据
                    validList.add(new ValidMessage("同一资产代码只能存在一条数据"));
                }
            }
        }
        if (StringUtil.equals(operateType, OperationType.MODIFY.name())) {
            this.emptyCheck(dto, validList);
            //修改时以及流程中修改数据审批时，需要判断当前资产是否存在有效或流程中的待执行指令及交易数据，如存在，则不允许修改；
            Integer count = finTenderBondAssetMapper.countFinprodIdUse(dto.getFinprodId(), DictCodeList.是否_否.getValue(),null);
            if (Objects.nonNull(count) && count > 0) {
                validList.add(new ValidMessage("存在指令或者交易使用了该记录信息"));
            }
        }
        return validList;
    }

    /**
     * 修改或者删除校验
     *
     * @param dto
     * @return
     */
    public List<ValidMessage> modifyAndDeleteCheck(FinTenderBondAsset001Dto dto) {
        List<ValidMessage> validList = new ArrayList<>();
        String operateType = dto.getOperateType();
        if (isInProcess(dto.getFinprodMarketId(), "FinTenderBondAsset002", "FinTenderBondAsset003")) {
            validList.add(new ValidMessage("存在流程中的数据"));
        }
        if (StringUtil.equals(operateType, OperationType.MODIFY.name())) {
            //修改时以及流程中修改数据审批时，需要判断当前资产是否存在有效或流程中的待执行指令及交易数据，如存在，则不允许修改；
            Integer count = finTenderBondAssetMapper.countFinprodIdUse(dto.getFinprodId(), DictCodeList.是否_否.getValue(),null);
            if (Objects.nonNull(count) && count > 0) {
                validList.add(new ValidMessage("存在指令或者交易使用了该记录信息"));
            }
        }
        if (StringUtil.equals(operateType, OperationType.DELETE.name())) {
            //删除时，需要判断当前资产是否存在指令及交易数据，如存在，则不允许删除；
            Integer count = finTenderBondAssetMapper.countFinprodIdUse(dto.getFinprodId(), null,null);
            if (Objects.nonNull(count) && count > 0) {
                validList.add(new ValidMessage("存在指令或者交易使用了该记录信息"));
            }
        }
        return validList;
    }

    /**
     * 构造金融标的代码
     *
     * @param dto
     * @return
     */
    public String getFinprodId(FinTenderBondAsset002Dto dto) {
        String[] arr = dto.getFinprodMarketId().split("\\.");
       if(StringUtil.equals(dto.getIssueDto().getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue())) {
           return String.format("%s_%s_%d_%s_%s.%s", DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue(), arr[0],
                   dto.getIssueDto().getAddBatch(), dto.getTradeMarket(), DateUtil.format(dto.getIssueDto().getIssueDate(), "yyyyMMdd"), arr[1]);
       }else {
           Date date = dto.getIssueDto().getIssueDate() == null ? dto.getIssueDto().getTenderEdate() : dto.getIssueDto().getIssueDate();
           return String.format("%s_%s_%d_%s_%s.%s", DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue(), arr[0],
                   dto.getIssueDto().getAddBatch(), dto.getTradeMarket(), DateUtil.format(date, "yyyyMMdd"), arr[1]);
       }
    }


    /**
     * 空检查
     *
     * @param dto
     * @param validList
     */
    private void emptyCheck(FinTenderBondAsset002Dto dto, List<ValidMessage> validList) {
        ValidUtils.valid(dto, validList);
        ValidUtils.valid(dto.getProductAddDto(), validList);
        ValidUtils.valid(dto.getIssueDto(), validList);
        if (Objects.isNull(dto.getIssueDto())) return;
        if(Objects.nonNull(dto.getVdate()) && Objects.nonNull(dto.getMdate()) && dto.getVdate().compareTo(dto.getMdate()) >= 0) {
            validList.add(new ValidMessage("起息日不能大于到期日"));
        }
        if (StringUtil.isNotEmpty(dto.getIssueDto().getIssueClass()) && (StringUtil.equals(dto.getIssueDto().getIssueClass(), DictCodeListExt4.新债_发行方式_招标发行.getValue()) ||
                StringUtil.equals(dto.getIssueDto().getIssueClass(), DictCodeListExt4.新债_发行方式_簿记建档.getValue()))) {
            if (StringUtil.isEmpty(dto.getIssueDto().getTenderTarget())) {
                validList.add(new ValidMessage("招标标的不能为空"));
            }
        }
        if (StringUtil.isNotEmpty(dto.getIssueDto().getIssueStyle()) && StringUtil.equals(dto.getIssueDto().getIssueStyle(), DictCodeListExt4.发行类型_增发.getValue())) {
            if (StringUtil.isEmpty(dto.getIssueDto().getTenderTarget())) {
                validList.add(new ValidMessage("增发批次不能为空"));
            }
        }
        if (StringUtil.isNotEmpty(dto.getIssueDto().getIssueClass()) && (StringUtil.equals(dto.getIssueDto().getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue()) || (
                StringUtil.equals(dto.getIssueDto().getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue()) && StringUtil.isNotEmpty(dto.getIssueDto().getIssueClass()) &&
                        (StringUtil.equals(dto.getIssueDto().getIssueClass(), DictCodeListExt4.招标方式_单一价格招标) || StringUtil.equals(dto.getIssueDto().getIssueClass(), DictCodeListExt4.招标方式_数量招标))
        ))) {
            if (Objects.isNull(dto.getIssueDto().getIssueDate())) {
                validList.add(new ValidMessage("发行日不能为空"));
            }else if(DateUtil.compareDate(dto.getIssueDto().getIssueDate(), iPlatformDate.getSysDate()) < 0){
                validList.add(new ValidMessage("发行日需大于等于平台日"));
            }
            if (Objects.isNull(dto.getIssueDto().getPayDate())) {
                validList.add(new ValidMessage("缴款日不能为空"));
            }
            if (Objects.isNull(dto.getIssueDto().getIssuePrice())) {
                validList.add(new ValidMessage("发行价格不能为空"));
            }
        }
        if (StringUtil.isNotEmpty(dto.getIssueDto().getIssueClass()) && StringUtil.equals(dto.getIssueDto().getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue()) &&
            StringUtil.equals(dto.getIssueDto().getIssueClass(), DictCodeListExt4.招标方式_数量招标) ) {
            if (Objects.isNull(dto.getIssueDto().getIssuePrice())) {
                validList.add(new ValidMessage("发行价格不能为空"));
            }
        }
        if (StringUtil.isNotEmpty(dto.getIssueDto().getIssueClass()) && StringUtil.equals(dto.getIssueDto().getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue()) &&
                (StringUtil.isNotEmpty(dto.getCouponSpecies()) && (StringUtil.equals(dto.getCouponSpecies(),DictCodeList.息票品种_贴现.getValue()) || StringUtil.equals(dto.getCouponSpecies(),DictCodeList.息票品种_零息.getValue()) ))){
            if (Objects.isNull(dto.getIssueDto().getIssuePrice())) {
                validList.add(new ValidMessage("参考收益率不能为空"));
            }
        }
        if (StringUtil.isNotEmpty(dto.getIssueDto().getIssueStyle()) && StringUtil.equals(dto.getIssueDto().getIssueStyle(), DictCodeListExt4.发行类型_增发.getValue())) {
            if (StringUtil.isNotEmpty(dto.getProductAddDto().getCrossMarket()) && StringUtil.equals(dto.getProductAddDto().getCrossMarket(), DictCodeList.是否_是.getValue())) {
                if (StringUtil.isEmpty(dto.getProductAddDto().getCrossMarketCode())) {
                    validList.add(new ValidMessage("跨市场代码不能为空"));
                }
            }
            if (StringUtil.isEmpty(dto.getProductAddDto().getAppearMarketId())) {
                validList.add(new ValidMessage("上市代码不能为空"));
            }
            if (StringUtil.isNotEmpty(dto.getIssueDto().getTenderEdate()) && DateUtil.compareDate(dto.getIssueDto().getTenderEdate(), iPlatformDate.getSysDate()) < 0) {
                validList.add(new ValidMessage("增发投标日截止日需大于等于平台日"));
            }
        } else {
            if (StringUtil.isNotEmpty(dto.getIssueDto().getTenderEdate()) && DateUtil.compareDate(dto.getIssueDto().getTenderEdate(), iPlatformDate.getSysDate()) < 0) {
                validList.add(new ValidMessage("投标日截止日需大于等于平台日"));
            }
        }
    }


}
