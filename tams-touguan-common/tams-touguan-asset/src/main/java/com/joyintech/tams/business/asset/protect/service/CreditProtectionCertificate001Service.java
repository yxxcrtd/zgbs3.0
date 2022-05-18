package com.joyintech.tams.business.asset.protect.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.asset.protect.dao.CreditProtectionCertificateMapper;
import com.joyintech.tams.business.asset.protect.dto.CreditProtectionCertificateDto;
import com.joyintech.tams.business.asset.protect.dto.CreditProtectionCertificateRequest;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CreditProtectionCertificate001Service extends BaseService implements BaseProcessService<CreditProtectionCertificateDto> {

    @Autowired(required = false)
    private CreditProtectionCertificateMapper creditProtectionCertificateMapper;

    /**
     * 使用给定的sqlID检索分页数据。返回类型是Service的泛型。
     *
     * @param request request
     * @return Page类 Page
     * @throws Exception 异常信息
     */
    public Page<CreditProtectionCertificateDto> selectForPage(CreditProtectionCertificateRequest request){
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        pageParam.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        pageParam.put("issueRshortStr",StringUtil.splitToList(null, request.getIssueRshortStr())); // 创立机构
        pageParam.put("refEntity", StringUtil.splitToList(null, request.getRefEntity())); //参考实体
        pageParam.put("finprodType", DictCodeListExt1.金融产品类型_信用保护凭证.getValue());//现金流业务类型
        pageParam.put("finprodIdList", StringUtil.splitToList(null, request.getFinprodIdAbrr()));
        pageParam.put("refSec", StringUtil.splitToList(null, request.getRefSec())); //参考债项
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

        Page<CreditProtectionCertificateDto> page
                =  super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(),
                () -> creditProtectionCertificateMapper.queryForPage(pageParam));
        return page;
    }

    /**
     * 资产代码/简称查询
     *
     * @param request
     * @return
     */
    public Page<SelectOptionDto> getFinprodIdForSelect(CreditProtectionCertificateRequest request) {
        Map<String, Object> param = BeanUtil.object2Map(request);
        param.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        param.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        param.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        Page<SelectOptionDto> page = super.queryForPage(request.getPageMode().getPageNo(),
                request.getPageMode().getPageSize(),
                () -> creditProtectionCertificateMapper.getFinprodIdForSelect(param));
        return page;
    }

    /**
     * 资产代码/简称查询
     *
     * @param request
     * @return
     */
    public Page<SelectOptionDto> findForSelect(CreditProtectionCertificateRequest request) {
        Map<String, Object> param = BeanUtil.object2Map(request);
        param.put("finprodType2", DictCodeListExt1.金融产品类型_债券.getValue());
        param.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        param.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        Page<SelectOptionDto> page = super.queryForPage(request.getPageMode().getPageNo(),
                request.getPageMode().getPageSize(),
                () -> creditProtectionCertificateMapper.getFinprodIdForSelect(param));
        return page;
    }

    @Override
    public void doComplete(CreditProtectionCertificateDto entity, OperationType type) {

    }

    @Override
    public void doInsert(CreditProtectionCertificateDto entity, OperationType type) {

    }

    @Override
    public void doUpdate(CreditProtectionCertificateDto entity) {

    }

    @Override
    public void doDelete(String workflowId) {

    }

    @Override
    public void doBack(CreditProtectionCertificateDto entity) {

    }
}
