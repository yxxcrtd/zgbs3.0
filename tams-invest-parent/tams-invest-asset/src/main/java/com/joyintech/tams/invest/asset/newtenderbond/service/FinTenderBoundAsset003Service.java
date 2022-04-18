package com.joyintech.tams.invest.asset.newtenderbond.service;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.FinProductIssueMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.asset.newtenderbond.dao.FinTenderBoundAssetMapper;
import com.joyintech.tams.invest.asset.newtenderbond.dto.FinTenderBoundAsset003Dto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 新债标的服务类
 *
 * @author gwl
 * @version 1.0 2022年4月11日
 * @see FinTenderBoundAsset003Service
 * @since 1.0
 */
@Service
public class FinTenderBoundAsset003Service implements BaseProcessService<FinTenderBoundAsset003Dto> {

    @Resource
    private FinTenderBoundAssetMapper finTenderBoundAssetMapper;

    @Resource
    private FinProductMapper finProductMapper;

    @Resource
    private FinProductAddMapper finProductAddMapper;

    @Resource
    private FinProductTypeMapper finProductTypeMapper;

    @Resource
    private FinProductIssueMapper finProductIssueMapper;

    @Resource
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Resource
    private IPlatformDate iPlatformDate;

    @Override
    public String generateTitle(FinTenderBoundAsset003Dto entity, OperationType operationType) {
        return String.format("资产代码(%) 资产简称(%) 上市流通", StringUtil.isEmpty(entity.getFinprodMarketId()) ? "" : entity.getFinprodMarketId(),
                StringUtil.isEmpty(entity.getFinprodAbbr()) ? "" : entity.getFinprodAbbr());
    }

    @Override
    public void doComplete(FinTenderBoundAsset003Dto dto, OperationType type) {
        //处理数据
        if (OperationType.DELETE.equals(type)) {
        } else if (OperationType.NEW.equals(type)) {
        } else {
        }
        //记录操作日志
    }


    /**
     * 上市流通/撤销
     *
     * @param dto
     * @return
     */
    public List<ValidMessage> valid(FinTenderBoundAsset003Dto dto) {
        List<ValidMessage> validMessages = new ArrayList<>();
        if(StringUtil.equals(dto.getOperateType(),OperationType.REVOKE.name())) {

        }else {
            if (dto.getListingDate().compareTo(iPlatformDate.getSysDate()) >= 0) {
                validMessages.add(new ValidMessage("上市日需大于等于平台日"));
            }
            if (StringUtil.equals(dto.getIssueStatus(), DictCodeListExt1.发行状态_发行失败.getValue())) {
                validMessages.add(new ValidMessage("发行失败的不能进行上市流通"));
            }
            if (!StringUtil.equals(dto.getIssueStage(), DictCodeListExt4.发行阶段_已发行.getValue())) {
                validMessages.add(new ValidMessage("发行阶段不为已发行的不能进行上市流通"));
            }
        }
        return validMessages;
    }

}
