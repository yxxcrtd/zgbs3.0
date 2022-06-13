package com.joyintech.service.riskrds.limitmanagement.rskportautorelatepoint.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.riskrds.limitmanagement.rskportautorelatepoint.dao.RskPortAutoRelatePointMapper;
import com.joyintech.service.riskrds.limitmanagement.rskportautorelatepoint.dto.RskPortAutoRelatePoint002Dto;
import com.joyintech.service.riskrds.limitmanagement.rskportautorelatepoint.dto.RskPortAutoRelatePointDeleteDto;
import com.joyintech.service.riskrds.limitmanagement.rskportautorelatepoint.dto.RskPortAutoRelatePointDto;
import com.joyintech.tams.common.base.dao.RskRuleIndicatorMapper;
import com.joyintech.tams.common.base.dao.RskRuleInfoMapper;
import com.joyintech.tams.common.base.dao.RskRulePoolConfigMapper;
import com.joyintech.tams.common.base.entity.RskRuleIndicator;
import com.joyintech.tams.common.base.entity.RskRuleIndicatorKey;
import com.joyintech.tams.common.base.entity.RskRuleInfo;
import com.joyintech.tams.common.base.entity.RskRulePoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import com.joyintech.tams.invest.common.prodinfo.dao.RskRuleCommonMapper;

@SuppressWarnings("rawtypes")
@Service("backRskPortAutoRelatePointService")
public class RskPortAutoRelatePointService extends BaseService {

    @Autowired
    private RskRuleIndicatorMapper rskRuleIndicatorMapper;

    @Autowired
    private RskPortAutoRelatePointMapper rskPortAutoRelatePointMapper;

    @Autowired
    private RskRuleInfoMapper rskRuleInfoMapper;

    @Autowired
    private RskRuleCommonMapper rskRuleCommonMapper;

    @Autowired
    private RskRulePoolConfigMapper rskRulePoolConfigMapper;

    /**
     * 查询指标名称
     * 
     * @param param
     * @return
     */
    public List<RskPortAutoRelatePointDto> getRuleName(Map<String, Object> param) {
        return rskPortAutoRelatePointMapper.getRuleName(param);
    }

    @SuppressWarnings("unchecked")
    public Page<RskPortAutoRelatePointDto> selectForPage(int page, int length,
                                                         Map<String, Object> param) {
        return super.queryForPage(page, length,
            () -> rskPortAutoRelatePointMapper.selectForPage(param));
    }

    @Transactional(rollbackFor = Throwable.class)
    public int insertRule(RskPortAutoRelatePoint002Dto dto) {
        RskRuleInfo rskRuleInfo = new RskRuleInfo();
        BeanUtil.copyPropertiesIgnoreNull(rskRuleInfo, dto);
        // 插入规则
        rskRuleInfoMapper.insert(rskRuleInfo);
        RskRulePoolConfig rskRulePoolConfig = new RskRulePoolConfig();
        // 循环插入规则入池
        for (RskPortAutoRelatePoint002Dto e : dto.getSelectedItem()) {
            BeanUtil.copyPropertiesIgnoreNull(rskRulePoolConfig, e);
            rskRulePoolConfig.setRuleId(dto.getRuleId());
            rskRulePoolConfigMapper.insert(rskRulePoolConfig);
        }
        return 1;
    }

    @Transactional(rollbackFor = Throwable.class)
    public int updateRule(RskPortAutoRelatePoint002Dto dto) {
        deleteRule(dto);
        insertRule(dto);
        /*
         * RskRuleInfo rskRuleInfo = new RskRuleInfo();
         * BeanUtil.copyPropertiesIgnoreNull(rskRuleInfo, dto); //插入规则
         * rskRuleInfoMapper.updateByPrimaryKey(rskRuleInfo); RskRulePoolConfig rskRulePoolConfig =
         * new RskRulePoolConfig(); //循环插入规则入池 for(RskPortAutoRelatePoint002Dto
         * e:dto.getSelectedItem()){ BeanUtil.copyPropertiesIgnoreNull(rskRulePoolConfig, e);
         * rskRulePoolConfig.setRuleId(dto.getRuleId());
         * rskRulePoolConfigMapper.updateByPrimaryKey(rskRulePoolConfig); }
         */
        return 1;
    }

    @Transactional(rollbackFor = Throwable.class)
    public int deleteRule(RskPortAutoRelatePoint002Dto dto) {
        rskRuleInfoMapper.deleteByPrimaryKey(dto.getRuleId());
        // 删除规则关联组合信息
        rskRuleCommonMapper.deleteRulePort(dto.getRuleId(),null);
        // 删除规则入池配置
        rskPortAutoRelatePointMapper.deletePoolConfigByRuleId(dto.getRuleId());
        // 删除规则指标关联
        //rskPortAutoRelatePointMapper.deleteRuleIndicatorByRuleId(dto.getRuleId());
        return 0;
    }

    @SuppressWarnings("unchecked")
    public Page<RskPortAutoRelatePoint002Dto> selectAllRule(int page, int length,
                                                            Map<String, Object> param) {
        return super.queryForPage(page, length,
            () -> rskPortAutoRelatePointMapper.selectAllRule(param));
    }

    @SuppressWarnings("unchecked")
    public Page<RskPortAutoRelatePointDto> findAllInt(int page, int length,
                                                      Map<String, Object> param) {
        return super.queryForPage(page, length,
            () -> rskPortAutoRelatePointMapper.findAllInt(param));
    }

    @Transactional(rollbackFor = Throwable.class)
    public int delete(RskPortAutoRelatePointDeleteDto dto) {
        RskRuleIndicatorKey key = new RskRuleIndicatorKey();
        BeanUtil.copyPropertiesIgnoreNull(key, dto);
        // key.setRuleId(dto.getRuleId());
        rskRuleIndicatorMapper.deleteByPrimaryKey(key);
        return 1;
    }

    @Transactional(rollbackFor = Throwable.class)
    public int insert(RskPortAutoRelatePointDto dto) {
        RskRuleIndicator rskRuleIndicator = new RskRuleIndicator();
        for (RskPortAutoRelatePointDto e : dto.getSelectedItem()) {
            BeanUtil.copyPropertiesIgnoreNull(rskRuleIndicator, e);
            rskRuleIndicator.setRuleId(dto.getRuleId());
            rskRuleIndicatorMapper.insert(rskRuleIndicator);
        }
        return 1;
    }

    public boolean isExist(RskPortAutoRelatePoint002Dto dto) {
        return rskPortAutoRelatePointMapper.isExist(dto) > 0;
    }

    public boolean isAreadyHaveInd(String ruleId) {
        return rskPortAutoRelatePointMapper.isAreadyHaveInd(ruleId) > 0;
    }

    /**
     * 查询规则信息
     * @param map
     */
    public RskRuleInfo getBussinessEntityByRuleId(Map<String, Object> map) {
        if(StringUtil.isNotEmpty(map.get("ruleId"))) {
            return rskRuleInfoMapper.selectByPrimaryKey(map.get("ruleId").toString());
        }else{
            return null;
        }
    }

    /**
     * 查询规则指标信息
     * @param map
     */
    public RskRuleIndicator getRelInfoByRuleId(Map<String, Object> map) {
        if(StringUtil.isNotEmpty(map.get("ruleId"))&&StringUtil.isNotEmpty(map.get("indId"))) {
            RskRuleIndicatorKey rskRuleIndicatorKey=new RskRuleIndicatorKey();
            rskRuleIndicatorKey.setIndId(map.get("indId").toString());
            rskRuleIndicatorKey.setRuleId(map.get("ruleId").toString());
            return rskRuleIndicatorMapper.selectByPrimaryKey(rskRuleIndicatorKey);
        }else{
            return null;
        }
    }
}
