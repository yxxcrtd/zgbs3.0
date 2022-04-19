package com.joyintech.tams.business.asset.tenderbond.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.FinProductIssueMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.business.asset.tenderbond.dao.FinTenderBondAssetMapper;
import com.joyintech.tams.business.asset.tenderbond.dto.*;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.CollectionUtils;
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
public class FinTenderBondAssetService extends BaseService<FinTenderBondAsset002Dto>  implements BaseProcessService<FinTenderBondAsset002Dto> {

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

    @Resource
    private IPlatformDate iPlatformDate;

    @Resource
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Override
    public String generateTitle(FinTenderBondAsset002Dto entity, OperationType operationType) {
        return String.format("资产代码(%s) 资产简称(%s)", StringUtil.isEmpty(entity.getFinprodMarketId()) ? "" : entity.getFinprodMarketId(),
                StringUtil.isEmpty(entity.getFinprodAbbr()) ? "" : entity.getFinprodAbbr());
    }

    @Override
    public void doComplete(FinTenderBondAsset002Dto dto, OperationType type) {
        if (OperationType.DELETE == type) {
            this.delete(dto);
        } else if (OperationType.NEW == type) {
            this.insert(dto);
        } else if(OperationType.MODIFY == type){
            this.update(dto);
        }
        //记录操作日志
        this.saveOperate(dto.getFinprodId(), dto.getRemark(), type);
    }

    /**
     * 分页查询
     *
     * @param search
     * @return
     */
    public Page<FinTenderBondAsset001Dto> selectForPage(FinTenderBondAssetSearch search) {
        search.setPlatformDate(iPlatformDate.getSysDateStr());
        search.setFinprodType2(DictCodeListExt2.平安资产类型_一级市场债.getValue());
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
        FinProductIssueDto productIssueDto = new FinProductIssueDto();
        BeanUtil.copyPropertiesIgnoreNull(productIssueDto, productIssue);
        entity.setIssueDto(productIssueDto);
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
        param.put("finprodType2", DictCodeListExt2.平安资产类型_一级市场债.getValue());
        param.put("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        param.put("TRADE_PLACE_02", DictCodeListExt1.交易场所_商业银行柜台.getValue());
        param.put("finprodMarketId", finprodMarketId);
        String finprodId = finTenderBondAssetMapper.selectFinprodId(param);
        if (StringUtil.isNotEmpty(finprodId)) {
            return getById(finprodId);
        } else {//TODO 从接口中获取
            return null;
        }
    }

    public void insert(FinTenderBondAsset002Dto dto) {
        if (StringUtil.isEmpty(dto.getCalendarId())) {//根据交易场所固定交易日历
            if (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())) {
                dto.setCalendarId("03");
            } else if (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_商业银行柜台.getValue())) {
                dto.setCalendarId("06");
            }
        }
        //1.保存金融产品标的表
        //金融产品类型_资产代码_增发批次_交易场所进行拼接
        dto.setFinprodId(getFinprodId(dto));
        FinProduct finProduct = new FinProduct();
        BeanUtil.copyPropertiesIgnoreNull(finProduct, dto);
        finProductMapper.insert(finProduct);
        //2.保存金融产品标的附属表
        FinProductAdd productAdd = new FinProductAdd();
        BeanUtil.copyPropertiesIgnoreNull(productAdd, dto.getProductAddDto());
        finProductAddMapper.insert(productAdd);
        //3.保存标的类金融产品发行信息
        FinProductIssue productIssue = new FinProductIssue();
        BeanUtil.copyPropertiesIgnoreNull(productIssue, dto.getIssueDto());
        finProductIssueMapper.insert(productIssue);
        //4.保存融产品分类表
        FinProductType finProductType = new FinProductType();
        BeanUtil.copyPropertiesIgnoreNull(finProductType, dto);
        finProductType.setBranch(0l);
        //TODO 这里需要通过映射表增加type的类型
        //finProductType.setType6(dto.getType4());
        //finProductType.setType7(dto.getType5());
        finProductType.setType8(DictCodeListExt2.平安资产类型_一级市场债.getValue());
        finProductTypeMapper.insert(finProductType);
    }

    /**
     * 修改
     *
     * @param dto
     */
    public void update(FinTenderBondAsset002Dto dto) {
        //采用先删除在插入的方式处理
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
     * @param type
     */
    public void saveOperate(String tradeId, String remark, OperationType type) {
        CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
        oprHisReq.setEntrustId(tradeId);
        if (type == OperationType.NEW) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_新建.getValue());
        } else if ( type == OperationType.MODIFY) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_修改.getValue());
        } else if (type == OperationType.REVOKE) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_撤回.getValue());
        } else {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_关闭.getValue());
        }
        oprHisReq.setBusiDes(remark);
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
    }

    /**
     * 资产代码/简称查询
     *
     * @param param
     * @return
     */
    public Page<SelectOptionDto> getFinprodIdForSelect(Map<String, Object> param) {
        param.put("finprodType2", DictCodeListExt2.平安资产类型_一级市场债.getValue());
        param.put("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        param.put("TRADE_PLACE_02", DictCodeListExt1.交易场所_商业银行柜台.getValue());
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
        param.put("finprodType2", DictCodeListExt2.平安资产类型_一级市场债.getValue());
        param.put("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        param.put("TRADE_PLACE_02", DictCodeListExt1.交易场所_商业银行柜台.getValue());
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
        return qty == null ? true : qty > 1 ? false : true;
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


    /**
     * 保存、修改、删除提交校验
     *
     * @param dto
     * @return
     */
    public List<ValidMessage> valid(FinTenderBondAsset002Dto dto) {
        List<ValidMessage> validList = new ArrayList<>();
        String operateType = dto.getOperateType();
        if (isInProcess(dto.getFinprodMarketId(), "FinTenderBondAsset002", "FinTenderBondAsset003")) {
            validList.add(new ValidMessage("存在流程中的数据"));
        }
        if (StringUtil.equals(operateType, OperationType.NEW.name())) {
            this.emptyCheck(dto, validList);
            String finprodId = getFinprodId(dto);
            FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
            if (Objects.nonNull(finProduct)) { //同一资产代码只能存在一条数据
                validList.add(new ValidMessage("同一资产代码只能存在一条数据"));
            }
        }
        if (StringUtil.equals(operateType, OperationType.MODIFY.name())) {
            this.emptyCheck(dto, validList);
            //修改时以及流程中修改数据审批时，需要判断当前资产是否存在有效或流程中的待执行指令及交易数据，如存在，则不允许修改；
            Integer count = finTenderBondAssetMapper.countFinprodIdUse(dto.getFinprodId(), DictCodeList.是否_否.getValue());
            if (Objects.nonNull(count) && count > 0) {
                validList.add(new ValidMessage("存在指令或者交易使用了该记录信息"));
            }
        }
        if (StringUtil.equals(operateType, OperationType.DELETE.name())) {
            //删除时，需要判断当前资产是否存在指令及交易数据，如存在，则不允许删除；
            Integer count = finTenderBondAssetMapper.countFinprodIdUse(dto.getFinprodId(), null);
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
        return String.format("%s_%s_%d_%s_%s", DictCodeListExt2.平安资产类型_一级市场债.getValue(), dto.getFinprodMarketId(),
                dto.getIssueDto().getAddBatch(), dto.getTradeMarket(), DateUtil.format(dto.getProductAddDto().getBidDate(), "yyyyMMdd"));
    }


    /**
     * 空检查
     *
     * @param dto
     * @param validList
     */
    private void emptyCheck(FinTenderBondAsset002Dto dto, List<ValidMessage> validList) {
        this.valid(dto, validList);
        this.valid(dto.getProductAddDto(), validList);
        this.valid(dto.getIssueDto(), validList);
        if (StringUtil.isNotEmpty(dto.getIssueDto().getIssueStyle()) && StringUtil.equals(dto.getIssueDto().getIssueStyle(), DictCodeListExt4.发行类型_增发)) {
            if (StringUtil.isNotEmpty(dto.getProductAddDto().getCrossMarket()) && StringUtil.equals(dto.getProductAddDto().getCrossMarket(), DictCodeList.是否_是.getValue())) {
                if (StringUtil.isEmpty(dto.getProductAddDto().getCrossMarketCode())) {
                    validList.add(new ValidMessage("跨市场代码不能为空"));
                }
            }
            if (StringUtil.isEmpty(dto.getProductAddDto().getAppearMarketId())) {
                validList.add(new ValidMessage("上市代码不能为空"));
            }
            if (StringUtil.isNotEmpty(dto.getProductAddDto().getBidDate()) && DateUtil.compareDate(dto.getProductAddDto().getBidDate(), iPlatformDate.getSysDate()) < 0) {
                validList.add(new ValidMessage("增发投标日需大于等于平台日"));
            }
        } else {
            if (StringUtil.isNotEmpty(dto.getProductAddDto().getBidDate()) && DateUtil.compareDate(dto.getProductAddDto().getBidDate(), iPlatformDate.getSysDate()) < 0) {
                validList.add(new ValidMessage("投标日需大于等于平台日"));
            }
        }

    }


    /**
     * 校验是否必填
     *
     * @param obj
     * @param validList
     */
    public void valid(Object obj, List<ValidMessage> validList) {
        Field[] methods = obj.getClass().getDeclaredFields();
        if (methods == null || methods.length == 0) return;
        for (Field field : methods) {
            ApiModelProperty ap = field.getAnnotation(ApiModelProperty.class);
            System.out.println(field.getGenericType().getTypeName());
            if (Objects.nonNull(ap) && ap.required()) {//是必填字段
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    if (field.getGenericType().getTypeName().equals("java.lang.String")) {
                        if (Objects.isNull(value) || value.equals("")) {
                            validList.add(new ValidMessage(String.format("%s%s", ap.value(), "不能为空")));
                        }
                    } else if (Objects.isNull(value)) {
                        validList.add(new ValidMessage(String.format("%s%s", ap.value(), "不能为空")));
                    }
                } catch (IllegalAccessException e) {
                    //e.printStackTrace();
                }
            }
        }
    }

}
