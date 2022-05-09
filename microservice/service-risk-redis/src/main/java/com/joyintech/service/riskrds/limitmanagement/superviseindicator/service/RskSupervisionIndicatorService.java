
package com.joyintech.service.riskrds.limitmanagement.superviseindicator.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.TreeNode;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.riskrds.limitmanagement.superviseindicator.dao.RskSupervisionIndicatorMapper;
import com.joyintech.service.riskrds.limitmanagement.superviseindicator.dto.RskPortfolioIndicatorDto;
import com.joyintech.service.riskrds.limitmanagement.superviseindicator.dto.RskPortfolioPackageDto;
import com.joyintech.service.riskrds.limitmanagement.superviseindicator.dto.RskSupervisionIndicator002Dto;
import com.joyintech.service.riskrds.limitmanagement.superviseindicator.dto.RskSupervisionIndicatorDto;
import com.joyintech.tams.common.base.dao.RskIsupIndicatorMapper;

/**
 * 监控指标
 * 
 * @author 裴家俊
 * @version 1.0 2018年10月24日
 * @see RskSupervisionIndicatorService
 * @since 1.0
 */
@Service("backRskSupervisionIndicatorService")
public class RskSupervisionIndicatorService extends BaseService<RskSupervisionIndicator002Dto> {
    @Autowired
    private RskSupervisionIndicatorMapper rskSupervisionIndicatorMapper;

    @Autowired
    private RskIsupIndicatorMapper rskIsupIndicatorMapper;

    private static final String paramPref = "${";

    private static final String paramSuff = "}";

    /**
     * 画面查询方法 <功能详细描述>
     * 
     * @param page
     * @param length
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<RskSupervisionIndicator002Dto> selectForPage(int page, int length,
                                                             Map<String, Object> param) {
        return super.queryForPage(page, length,
            () -> rskSupervisionIndicatorMapper.selectForPage(param));
    }

    /**
     * 指标参数下拉框 <功能详细描述>
     * 
     * @return
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getIndicatorParamList() {
        return rskSupervisionIndicatorMapper.getIndicatorParamList();
    }

    /**
     * 动态获取禁投或可投项--add by jjpei 20190811--- <功能详细描述>
     * 
     * @param map
     * @return
     */
    public List<SelectOptionDto> getForbidEnumInfo(Map<String, Object> map) {
        String forbidEnum = StringUtil.isNotEmpty(
            map.get("forbidEnum")) ? map.get("forbidEnum").toString() : null;
        return rskSupervisionIndicatorMapper.getForbidEnumInfo(forbidEnum);
    }

    /**
     * 指标参数下拉框
     * @param indId
     * @param portfName
     * @param profitType
     * @param issueMode
     * @param raiseType
     * @param investType
     * @return
     */
    public List<TreeNode<RskSupervisionIndicatorDto>> getAllPortfolioByProfit(String indId,
                                                                              String portfName,
                                                                              String profitType,
                                                                              String issueMode,
                                                                              String raiseType,
                                                                              String investType) {
        String platDate=DateUtil.format(DateUtil.platDateFromCache());
    	return rskSupervisionIndicatorMapper.getAllPortfolioByProfit(indId,
            BussinessDictCodeListRds.组合层次_单组合.getValue(), portfName, profitType, issueMode, raiseType,
            investType,platDate);
    }

    public List<TreeNode<RskSupervisionIndicatorDto>> getAllPortfolioByOther(String fields,
                                                                             String indId,
                                                                             String portfName,
                                                                             String profitType,
                                                                             String issueMode,
                                                                             String raiseType,
                                                                             String investType) {
        String platDate=DateUtil.format(DateUtil.platDateFromCache());
        return rskSupervisionIndicatorMapper.getAllPortfolioByOther(fields,
            BussinessDictCodeListRds.组合层次_单组合.getValue(), indId, portfName, profitType, issueMode,
            raiseType, investType,platDate);
    }

    public List<TreeNode<RskPortfolioPackageDto>> getAllPortfolioBag(String bagName,
                                                                     String indId) {
        return rskSupervisionIndicatorMapper.getAllPortfolioBag(bagName, indId,
            BussinessDictCodeListRds.组合层次_组合包.getValue());
    }

    /**
     * 插入
     * 
     * @param entity
     * @return
     * @since 1.0
     */
    @Transactional(rollbackFor = Throwable.class)
    public void insert(RskSupervisionIndicator002Dto entity) {
        if (null != entity.getRskSupervisionIndicatorDto()) {
            // 监督指标基本信息
            rskSupervisionIndicatorMapper.insertSupervisionIndicator(
                entity.getRskSupervisionIndicatorDto());
        }
        if (null != entity.getSelectedPortfIndItem()) {
            // 组合关联指标
            for (RskPortfolioIndicatorDto portDto : entity.getSelectedPortfIndItem()) {
                portDto.setIndId(entity.getRskSupervisionIndicatorDto().getIndId());
                portDto.setPortfolioLevel(
                    entity.getRskPortfolioIndicatorDto().getPortfolioLevel());
                portDto.setStartDate(entity.getRskPortfolioIndicatorDto().getStartDate());
                portDto.setEndDate(entity.getRskPortfolioIndicatorDto().getEndDate());
                rskSupervisionIndicatorMapper.insertPortIndicator(portDto);
            }
        }
    }

    private void parseParamName(Set<String> paramList, String param) {

        if (StringUtil.isNotEmpty(param) && param.indexOf(paramPref) != -1
            && param.indexOf(paramSuff) != -1) {
            String paramPre = param.substring(param.indexOf(paramPref) + 2,
                param.indexOf(paramSuff));
            String exprNew = param.substring(param.indexOf(paramSuff) + 1);
            paramList.add(paramPre);
            parseParamName(paramList, exprNew);
        }
    }

    /**
     * 更新
     * 
     * @param entity
     * @return
     * @since 1.0
     */
    @Transactional(rollbackFor = Throwable.class)
    public void update(RskSupervisionIndicator002Dto entity) {
        if (null != entity.getRskSupervisionIndicatorDto()) {
            // 监督指标基本信息
            rskIsupIndicatorMapper.updateByPrimaryKeySelective(
                entity.getRskSupervisionIndicatorDto());
        }

    }

    /**
     * 更新启用状态
     * 
     * @param entity
     * @return
     * @since 1.0
     */
    public void updateIsEnabled(RskSupervisionIndicator002Dto entity) {
        rskSupervisionIndicatorMapper.updateIsEnabled(entity);
    }

    /**
     * 查询监督指标
     * 
     * @param map
     * @return
     * @since 1.0
     */
    public RskSupervisionIndicator002Dto querySuperIndicatorInfoByIndId(Map<String, Object> map) {
        return rskSupervisionIndicatorMapper.querySuperIndicatorInfoByIndId(map);
    }

    /**
     * 查询监督指标
     *
     * @param map
     * @return
     * @since 1.0
     */
    public RskSupervisionIndicator002Dto querySuperIndicatorInfoByIndId_RULE(Map<String, Object> map) {
        return rskSupervisionIndicatorMapper.querySuperIndicatorInfoByIndId_RULE(map);
    }

    /**
     * 查询组合关联指标
     * 
     * @param map
     * @return
     * @since 1.0
     */
    public List<RskPortfolioIndicatorDto> queryPortfIndicatorInfoByIndId(Map<String, Object> map) {
        return rskSupervisionIndicatorMapper.queryPortfIndicatorInfoByIndId(map);
    }

    /**
     * 新建编辑场合时查询监督指标名称是否重复
     * @param indName
     * @param indId
     */
    public int searchRepeatByIndName(String indName, String indId) {
        return rskSupervisionIndicatorMapper.searchRepeatByIndName(indName, indId);
    }

    /**
     * 删除场合时查询指标是否关联指标
     * 
     * @param indId
     */
    public int searchReleteCountByIndId(String indId) {
        return rskSupervisionIndicatorMapper.searchReleteCountByIndId(indId);
    }
    /**
     * 删除场合时查询指标是否关联规则
     *
     * @param indId
     */
    public int searchReleteCountByRuleId(String indId) {
        return rskSupervisionIndicatorMapper.searchReleteCountByRuleId(indId);
    }

    /**
     * 删除监督指标
     * 
     * @param dto
     */
    @Transactional(rollbackFor = Throwable.class)
    public int delete(RskSupervisionIndicator002Dto dto) {
    	rskIsupIndicatorMapper.deleteByPrimaryKey(dto.getIndId());
    	// 删除该指标下所有个性化设置
    	rskSupervisionIndicatorMapper.deleteCondition(dto.getIndId());
        // 将该指标的联合设置都删除
    	rskSupervisionIndicatorMapper.deleteAllUnite(dto.getIndId());
        // 将该指标的剔除资产都删除
    	rskSupervisionIndicatorMapper.deleteAllAssetDel(dto.getIndId());
        // 将该组合下指标的特殊阶段都删除
    	rskSupervisionIndicatorMapper.deleteAllSpecialStage(dto.getIndId());
        return 1;
    }

    public int isConfigure(String indId) {
        return rskSupervisionIndicatorMapper.isConfigure(indId);
    }

	public List<String> getAllPortfolioId(String indId) {
		return rskSupervisionIndicatorMapper.getAllPortfolioId(indId);
	}
}
