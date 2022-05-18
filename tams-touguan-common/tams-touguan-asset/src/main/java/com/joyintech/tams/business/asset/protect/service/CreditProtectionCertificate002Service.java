package com.joyintech.tams.business.asset.protect.service;

import com.joyintech.background.component.common.pack.common.util.DateUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.asset.protect.dao.CreditProtectionCertificateMapper;
import com.joyintech.tams.business.asset.protect.dao.InterfCreditInfoMapper;
import com.joyintech.tams.business.asset.protect.dto.CreditProtectionCertificate002Dto;
import com.joyintech.tams.business.asset.protect.dto.CreditProtectionCertificateDto;
import com.joyintech.tams.business.asset.tenderbond.dto.FinProductAddDto;
import com.joyintech.tams.business.asset.tenderbond.service.FinTenderBondAssetService;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.util.ValidUtils;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 信用保护凭证标的
 *
 * @author wsz
 * @version 1.0 2022年5月11日
 * @see FinTenderBondAssetService
 * @since 1.0
 */
@Service
public class CreditProtectionCertificate002Service extends BaseService<CreditProtectionCertificate002Dto> implements BaseProcessService<CreditProtectionCertificate002Dto> {

    @Autowired(required = false)
    private CreditProtectionCertificateMapper creditProtectionCertificateMapper;

    @Autowired(required = false)
    private InterfCreditInfoMapper interfCreditInfoMapper;
    @Resource
    private FinProductMapper finProductMapper;
    @Resource
    private FinProductAddMapper finProductAddMapper;
    @Resource
    private FinProductTypeMapper finProductTypeMapper;
    /**
     * 现金流信息表
     */
    @Autowired
    private FinCashInfoMapper finCashInfoMapper;
    /**
     * 现金流计算规则表
     */
    @Autowired
    private FinCashCalcRuleMapper finCashCalcRuleMapper;
    /**
     * 现金流计划
     */
    @Autowired
    private  FinCashPlanMapper finCashPlanMapper;
    @Resource
    private JoyinbusinessCommonService joyinbusinessCommonService;

    /**
     * 保存操作类型
     *
     * @param tradeId
     * @param remark
     * @param operType
     */
    public void saveOperate(String tradeId, String remark, DictCodeListExt3 operType) {
        CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
        oprHisReq.setEntrustId(tradeId);
        oprHisReq.setOperType(operType.getValue());
        oprHisReq.setBusiDes(remark);
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
    }

    @Override
    public void doComplete(CreditProtectionCertificate002Dto dto, OperationType type) {
        if (OperationType.DELETE == type) {
            this.delete(dto);
            this.saveOperate(dto.getFinprodMarketId(), dto.getRemark(), DictCodeListExt3.操作类型_放弃);
        } else if (OperationType.NEW == type && "auto".equals(dto.getOperateType2())) {
            this.insert(dto);
            this.saveOperate(dto.getFinprodMarketId(), dto.getRemark(), DictCodeListExt3.操作类型_自动加券);
        } else if (OperationType.NEW == type && StringUtil.isEmpty(dto.getOperateType2())) {
            this.insert(dto);
            this.saveOperate(dto.getFinprodMarketId(), dto.getRemark(), DictCodeListExt3.操作类型_新建);
        } else if (OperationType.MODIFY == type && "cover".equals(dto.getOperateType2())) {
            this.update(dto);
            this.saveOperate(dto.getFinprodMarketId(), dto.getRemark(), DictCodeListExt3.操作类型_资讯数据覆盖);
        } else if (OperationType.MODIFY == type && StringUtil.isEmpty(dto.getOperateType2())) {
            this.update(dto);
            this.saveOperate(dto.getFinprodMarketId(), dto.getRemark(), DictCodeListExt3.操作类型_修改);
        }
    }

    /**
     * 新增
     *
     * @param dto
     */
//    @Transactional
    public void insert(CreditProtectionCertificate002Dto dto) {
        if (StringUtil.isEmpty(dto.getCalendarId())) {//根据交易场所固定交易日历
            if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                dto.setCalendarId("04");
            }else if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                dto.setCalendarId("05");
            }
        }
        //1.保存金融产品标的表
        //金融产品类型_资产代码_增发批次_交易场所进行拼接
        dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        dto.setFinprodType(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        dto.setFinprodId(getFinprodId(dto));
        FinProduct finProduct = new FinProduct();
        BeanUtil.copyPropertiesIgnoreNull(finProduct, dto);
        finProductMapper.insert(finProduct);
        //2.保存金融产品标的附属表
        FinProductAdd productAdd = new FinProductAdd();
        BeanUtil.copyPropertiesIgnoreNull(productAdd, dto);
        productAdd.setFinprodId(dto.getFinprodId());
        BigDecimal cash = StringUtil.isNotEmpty(dto.getNomCapi())? dto.getNomCapi().multiply(new BigDecimal(10000)):new BigDecimal(0.00);
        productAdd.setNomCapi(cash);
        finProductAddMapper.insert(productAdd);
        //3.保存融产品分类表
        FinProductType finProductType = new FinProductType();
        BeanUtil.copyPropertiesIgnoreNull(finProductType, dto);
        finProductType.setBranch(0l);
        finProductType.setType8(DictCodeList.平安资产类型_信用保护凭证.getValue());
        finProductTypeMapper.insert(finProductType);
        //新增现金流信息
        String cashId = CommonUtil.generateCashFlowCode(dto.getFinprodId(),
                DictCodeListExt1.现金流业务类型_金融产品.getValue(), 0L,
                DictCodeListExt1.现金流二级类型_本金.getValue());
        FinCashInfo finCashInfo = new FinCashInfo();
        finCashInfo.setCashId(cashId);
        finCashInfo.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
        finCashInfo.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
        finCashInfo.setFinprodId(dto.getFinprodId());
        finCashInfo.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        finCashInfo.setBranch(0l);
        BeanUtil.copyPropertiesIgnoreNull(finCashInfo, dto);
        finCashInfoMapper.insert(finCashInfo);
        //添加现金流计算规则
        FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
        BeanUtil.copyPropertiesIgnoreNull(finCashCalcRule, dto);
        finCashCalcRule.setCashId(cashId);
        finCashCalcRule.setEffDate(dto.getVdate());
        finCashCalcRule.setEffDate(dto.getVdate());
        finCashCalcRule.setBaseType(DictCodeList.基数类型_实收资本.getValue());
        finCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
        finCashCalcRule.setBasis(dto.getBasis());
        finCashCalcRule.setFinprodId(dto.getFinprodId());
        finCashCalcRule.setBranch(0l);
        finCashCalcRuleMapper.insert(finCashCalcRule);
        //添加资金流计划
        FinCashPlan finCashPlan = new FinCashPlan();
        finCashPlan.setCashId(cashId);
        finCashPlan.setCashNum(0l);
        finCashPlan.setCashType(DictCodeList.现金流类型_到期还本.getValue());
        finCashPlan.setVdateUnadjust(dto.getVdate());
        finCashPlan.setMdateUnadjust(dto.getMdate());
        finCashPlan.setPayDateUnadjust(dto.getMdate());
        finCashPlan.setVdate(dto.getVdate());
        finCashPlan.setMdate(dto.getMdate());
        finCashPlan.setPayDate(dto.getMdate());
        Long day = DateUtil.getBetweenDayNumber(DateUtil.format(dto.getVdate()),DateUtil.format(dto.getMdate()));
        finCashPlan.setTermdays(day);
        finCashPlan.setVdateTerm(dto.getVdate());
        finCashPlan.setMdateTerm(dto.getMdate());
        finCashPlan.setIntPrinAmt(cash);
        finCashPlan.setCashBaseamt(cash);
        finCashPlan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        finCashPlan.setFinprodId(dto.getFinprodId());
        finCashPlan.setFinprodType(dto.getFinprodType());
        finCashPlan.setFinprodType2(dto.getFinprodType2());
        finCashPlan.setPayType(DictCodeList.支付方式_现金.getValue());
        finCashPlan.setBranch(0l);
        finCashPlan.setBasis(dto.getBasis());
        finCashPlanMapper.insert(finCashPlan);
    }

    /**
     * 删除
     *
     * @param dto
     */
    public void delete(CreditProtectionCertificate002Dto dto) {
        //1.删除融产品分类表
        FinProductTypeKey typeKey = new FinProductTypeKey();
        typeKey.setBranch(0l);
        typeKey.setFinprodId(dto.getFinprodId());
        finProductTypeMapper.deleteByPrimaryKey(typeKey);
        //3.删除金融产品标的附属表
        finProductAddMapper.deleteByPrimaryKey(dto.getFinprodId());
        //4.删除金融产品标的表
        finProductMapper.deleteByPrimaryKey(dto.getFinprodId());
        //5.删除现金流信息
        String cashId = CommonUtil.generateCashFlowCode(dto.getFinprodId(),
                DictCodeListExt1.现金流业务类型_金融产品.getValue(), 0L,
                DictCodeListExt1.现金流二级类型_本金.getValue());
        finCashInfoMapper.deleteByPrimaryKey(cashId);
        //6.删除现金流计算规则
        FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
        finCashCalcRule.setCashId(cashId);
        finCashCalcRule.setEffDate(dto.getVdate());
        finCashCalcRuleMapper.deleteByPrimaryKey(finCashCalcRule);
        //7.删除资金流计划
        FinCashPlan finCashPlan = new FinCashPlan();
        finCashPlan.setCashId(cashId);
        finCashPlan.setMdate(dto.getMdate());
        finCashPlanMapper.deleteByPrimaryKey(finCashPlan);
    }

    /**
     * 修改
     *
     * @param dto
     */
    public void update(CreditProtectionCertificate002Dto dto) {
        //采用先删除在插入的方式处理
        this.delete(dto);
        this.insert(dto);
    }

    /**
     * 加券
     *
     * @param finprodMarketId
     * @return
     */
    public CreditProtectionCertificate002Dto addCoupons(String finprodMarketId) {
//        CreditProtectionCertificate002Dto dto = new CreditProtectionCertificate002Dto();
        Map<String, String> param = new HashMap<>();
        param.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        param.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        param.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        param.put("finprodMarketId", finprodMarketId);
//        String finprodId = creditProtectionCertificateMapper.selectFinprodId(param);
//        if (StringUtil.isNotEmpty(finprodId)) {
//            return getById(finprodId);
//        } else {//从接口中获取
        CreditProtectionCertificate002Dto dto = interfCreditInfoMapper.getFromInterface(param);
            if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                dto.setCalendarId("04");
            }else if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                dto.setCalendarId("05");
            }
            return dto;
//        }
    }

    /**
     * 通过主键查询列表数据
     *
     * @param finprodId
     * @return
     */
    public CreditProtectionCertificate002Dto getById(String finprodId) {
        //1.查金融产品标的表
        CreditProtectionCertificate002Dto entity = new CreditProtectionCertificate002Dto();
        entity.setFinprodMarketId(finprodId);
        finprodId = getFinprodId(entity);
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
        if (Objects.isNull(finProduct)) return null;
        BeanUtil.copyPropertiesIgnoreNull(entity, finProduct);
        //2.查金融产品标的附属表
        FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(finprodId);
        FinProductAddDto finProductAddDto = new FinProductAddDto();
        BeanUtil.copyPropertiesIgnoreNull(finProductAddDto, finProductAdd);
        //查询计费年度天数
        FinCashPlan finCashPlan = new FinCashPlan();
        String cashId = CommonUtil.generateCashFlowCode(finProduct.getFinprodId(),
                DictCodeListExt1.现金流业务类型_金融产品.getValue(), 0L,
                DictCodeListExt1.现金流二级类型_本金.getValue());
        finCashPlan.setCashId(cashId);
        finCashPlan.setMdate(finProduct.getMdate());
        finCashPlan = finCashPlanMapper.selectByPrimaryKey(finCashPlan);
        entity.setBasis(finCashPlan.getBasis());
        BigDecimal cash = StringUtil.isNotEmpty(entity.getNomCapi())? entity.getNomCapi().divide(new BigDecimal(10000)):new BigDecimal(0.00);
        entity.setNomCapi(cash);
        return entity;
    }

    @Override
    public void doInsert(CreditProtectionCertificate002Dto entity, OperationType type) {

    }

    @Override
    public void doUpdate(CreditProtectionCertificate002Dto entity) {

    }

    @Override
    public void doDelete(String workflowId) {

    }

    @Override
    public void doBack(CreditProtectionCertificate002Dto entity) {

    }


    /**
     * 保存、修改提交校验
     *
     * @param dto
     * @return
     */
    public List<ValidMessage> valid(CreditProtectionCertificate002Dto dto) {
        List<ValidMessage> validList = new ArrayList<>();
        String operateType = dto.getOperateType();
        if (StringUtil.isEmpty(dto.getWorkflowId()) &&  isInProcess(dto.getFinprodMarketId(), "CreditProtectionCertificate002")) {
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
                if(StringUtil.isNotEmpty(creditProtectionCertificateMapper.selectFinprodId(param))) {
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
            Integer count = creditProtectionCertificateMapper.countFinprodIdUse(dto.getFinprodId(), DictCodeList.是否_否.getValue(),null);
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
    public List<ValidMessage> modifyAndDeleteCheck(CreditProtectionCertificateDto dto) {
        List<ValidMessage> validList = new ArrayList<>();
        String operateType = dto.getOperateType();
        if (isInProcess(dto.getFinprodMarketId(), "CreditProtectionCertificate002")) {
            validList.add(new ValidMessage("存在流程中的数据"));
        }
        if (StringUtil.equals(operateType, OperationType.MODIFY.name())) {
            //修改时以及流程中修改数据审批时，需要判断当前资产是否存在有效或流程中的待执行指令及交易数据，如存在，则不允许修改；
            Integer count = creditProtectionCertificateMapper.countFinprodIdUse(dto.getFinprodId(), DictCodeList.是否_否.getValue(),null);
            if (Objects.nonNull(count) && count > 0) {
                validList.add(new ValidMessage("存在指令或者交易使用了该记录信息"));
            }
        }
        if (StringUtil.equals(operateType, OperationType.DELETE.name())) {
            //删除时，需要判断当前资产是否存在指令及交易数据，如存在，则不允许删除；
            Integer count = creditProtectionCertificateMapper.countFinprodIdUse(dto.getFinprodId(), null,null);
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
    public String getFinprodId(CreditProtectionCertificate002Dto dto) {
        return String.format("%s_%s", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue(),dto.getFinprodMarketId());
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
            List<String> result = creditProtectionCertificateMapper.getBusinessData(wflValidInputParameter);
            if (CollectionUtils.isNotEmpty(result)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }


    /**
     * 空检查
     *
     * @param dto
     * @param validList
     */
    private void emptyCheck(CreditProtectionCertificate002Dto dto, List<ValidMessage> validList) {
        ValidUtils.valid(dto, validList);
    }

}
