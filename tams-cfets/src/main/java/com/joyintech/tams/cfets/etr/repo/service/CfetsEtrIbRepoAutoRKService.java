package com.joyintech.tams.cfets.etr.repo.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.dao.JoyinbusinessCommonMapper;
import com.joyintech.tams.business.common.dto.FinProductSpecialDto;
import com.joyintech.tams.cfets.etr.repo.dao.CfetsEtrIbRepoinfoMapper;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrIbRepoLibDto;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrIbRepoRkDto;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrSecStockConfig;
import com.joyintech.tams.common.base.entity.EtrSecStock;
import com.joyintech.tams.common.base.entity.EtrSecStockConfig;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CfetsEtrIbRepoAutoRKService extends BaseService {

    private Logger logger = LoggerFactory.getLogger(CfetsEtrIbRepoAutoRKService.class);

    @Autowired
    private CfetsEtrIbRepoinfoMapper cfetsEtrIbRepoinfoMapper;

    @Autowired
    private CfetsEtrIbRepoRKService cfetsEtrIbRepoRKService;

    @Autowired
    private CfetsIbRepoLibService cfetsIbRepoLibService;

    @Autowired
    private JoyinbusinessCommonMapper joyinbusinessCommonMapper;
    /**
     * 平台日期
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    public void autoInLibrary(String userId, String finprodId, List<String> portfolioIds, String finprodType2, String ps) {
        logger.debug("质押券自动入库 userId:" + userId + " finprodId:" + finprodId +
                " portfolioIds:" + JsonUtils.object2Json(portfolioIds) + " finprodType2:" + finprodType2 + " ps:" + ps);
        List<FinProductSpecialDto> finProductSpecialDtos = joyinbusinessCommonMapper.queryFinProductSpecialByFinprodIds(Arrays.asList(finprodId));
        finProductSpecialDtos.forEach(finProductSpecialDto->{
            if (StringUtil.equals(finProductSpecialDto.getIsDefaultBondIndicator(), DictCodeList.是否_是.getValue())) {
                logger.debug(finprodId + " 到期违约债不自动入库");
                return;
            }
        });
        String repo_pledge_model = CacheUtil.getSysParam("repo_pledge_model");
        Map<String, Object> param = new HashMap<>();
        if (!StringUtil.equals(repo_pledge_model, "03")) {
            return;
        }
        if (!(StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                && StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue()))) {
            return;
        }
        this.createComRule(userId);
        List<CfetsEtrIbRepoLibDto> pledgeBondList = new ArrayList<>();
        param.put("portfolioIdList", portfolioIds);
        param.put("userId", userId);
        List<CfetsEtrIbRepoRkDto> cfetsEtrIbRepoRkDtoList = cfetsEtrIbRepoinfoMapper.selectRepoRkInfoAndCofigs(param);
        param.put("plateDate", iPlatformDate.getSysDateStr());
        param.put("finprodId", finprodId);
        CfetsEtrSecStockConfig findprodInfo = cfetsEtrIbRepoinfoMapper.selectFinprodInfo(param);
        // 组合为空
        List<CfetsEtrIbRepoRkDto> cfetsEtrIbRepoRkDtoAllList = new ArrayList<>();
        // 组合不为空
        List<CfetsEtrIbRepoRkDto> cfetsEtrIbRepoRkDtoPortList = new ArrayList<>();
        // 不存在的对应配置的组合
        List<String> portfolioIdsNotIn = new ArrayList<>();
        // 配置信息拆分
        cfetsEtrIbRepoRkDtoList.forEach(item -> {
            if (StringUtil.isNotEmpty(item.getPortfolioId())) {
                cfetsEtrIbRepoRkDtoPortList.add(item);
            } else {
                cfetsEtrIbRepoRkDtoAllList.add(item);
            }
        });
        for (String portfolioId : portfolioIds) {
            boolean isMatch = false;
            for (CfetsEtrIbRepoRkDto portDto : cfetsEtrIbRepoRkDtoPortList) {
                if (StringUtil.equals(portfolioId, portDto.getPortfolioId())) {
                    isMatch = true;
                    this.formatRepoLibData(portDto.getCfetsEtrSecStockConfig(), findprodInfo, pledgeBondList, portfolioId, finprodId, userId);
                }
            }
            if (!isMatch) {
                portfolioIdsNotIn.add(portfolioId);
            }
        }
        if (cfetsEtrIbRepoRkDtoAllList == null || cfetsEtrIbRepoRkDtoAllList.isEmpty()) {
            return;
        }
        for (String portfolioId : portfolioIdsNotIn) {
            for (CfetsEtrIbRepoRkDto portDto : cfetsEtrIbRepoRkDtoAllList) {
                this.formatRepoLibData(portDto.getCfetsEtrSecStockConfig(), findprodInfo, pledgeBondList, portfolioId, finprodId, userId);
            }
        }
        CfetsEtrIbRepoLibDto repoLibDto = new CfetsEtrIbRepoLibDto();
        repoLibDto.setPledgeBondList(pledgeBondList);
        cfetsIbRepoLibService.inSave(repoLibDto);
    }

    /**
     * 创建 通用质押券库
     *
     * @param userId 用户id
     * @return boolean
     * @author jiangpeng
     * @date 2022/2/17 19:34
     */
    public boolean createComRule(String userId) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        List<EtrSecStock> etrSecStocks = cfetsEtrIbRepoinfoMapper.selectEtrSecStockByUser(param);
        // 存在质押券自动入库设置则不创建通用质押券库
        if (etrSecStocks != null && !etrSecStocks.isEmpty()) {
            return false;
        }
        String repo_pledge_model = CacheUtil.getSysParam("repo_pledge_model");
        if (!StringUtil.equals(repo_pledge_model, "03")) {
            return false;
        }
        param.put("pledgeStockId", "comConfig");
        List<EtrSecStockConfig> configs = cfetsEtrIbRepoinfoMapper.getAssetDetailBySeqno(param);
        CfetsEtrIbRepoRkDto dto = new CfetsEtrIbRepoRkDto();
        dto.setPledgeStockId(String.valueOf(new Date().getTime()));
        dto.setDefaultStock(dto.getPledgeStockId());
        dto.setPledgeStockName("通用质押券库");
        dto.setIsDefStock(DictCodeList.是否_是.getValue());
        dto.setCreateUser(userId);
        if (configs != null) {
            configs.forEach(item -> {
                item.setPledgeStockId(dto.getPledgeStockId());
                item.setConfigId(IDGenerator.generate());
            });
        }
        dto.setEtrSecStockConfig(configs);
        dto.setPtlSecStock(new ArrayList<>());
        cfetsEtrIbRepoRKService.insert(dto);
        cfetsEtrIbRepoRKService.inserttotal(dto);
        return true;
    }

    /**
     * 根据入库规则判断债券是否符合自动入库的条件
     *
     * @param configs        设置信息
     * @param pledgeBondList
     * @param portfolioId    组合代码
     * @return com.joyintech.tams.cfets.etrib.repo.dto.CfetsEtrIbRepoLibDto
     * @author jiangpeng
     * @date 2022/2/18 11:32
     */
    private void formatRepoLibData(List<CfetsEtrSecStockConfig> configs, CfetsEtrSecStockConfig findprodInfo,
                                   List<CfetsEtrIbRepoLibDto> pledgeBondList, String portfolioId, String finprodId, String userId) {
        for (CfetsEtrSecStockConfig config : configs) {
            boolean isTrue = true;
            // 三级分类
            if (StringUtil.isNotEmpty(config.getWindtypeone())
                    && !StringUtil.equals(config.getWindtypeone(), findprodInfo.getWindtypeone())) {
                isTrue = false;
                continue;
            }
            // 四级分类
            if (StringUtil.isNotEmpty(config.getWindtypetwo())
                    && !StringUtil.equals(config.getWindtypetwo(), findprodInfo.getWindtypetwo())) {
                isTrue = false;
                continue;
            }
            // 主体评级
            if (StringUtil.isNotEmpty(config.getmGradeResult())
                    && !StringUtil.equals(config.getmGradeResult(), findprodInfo.getmGradeResult())) {
                isTrue = false;
                continue;
            }
            // 债项评级
            if (StringUtil.isNotEmpty(config.getSecGradeResult())
                    && !StringUtil.equals(config.getSecGradeResult(), findprodInfo.getSecGradeResult())) {
                isTrue = false;
                continue;
            }
            // 是否次级债
            if (StringUtil.isNotEmpty(config.getIsSecBond())
                    && !StringUtil.equals(config.getIsSecBond(), findprodInfo.getIsSecBond())) {
                isTrue = false;
                continue;
            }
            // 是否含权债
            if (StringUtil.isNotEmpty(config.getIsRight())
                    && !StringUtil.equals(config.getIsRight(), findprodInfo.getIsRight())) {
                isTrue = false;
                continue;
            }
            // 是否私募债
            if (StringUtil.isNotEmpty(config.getIsPpb())
                    && !StringUtil.equals(config.getIsPpb(), findprodInfo.getIsPpb())) {
                isTrue = false;
                continue;
            }
            if (isTrue) {
                // 查询持仓数据
                Map<String, Object> pageParam = new HashMap<>();
                pageParam.put("userId", userId);
                pageParam.put("A_001", DictCodeListExt3.资产类型_资产账户持仓.getValue());
                pageParam.put("S_002", DictCodeListExt3.资产类型_可质押持仓.getValue());
                pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
                pageParam.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                pageParam.put("TRADEPLACE01", DictCodeListExt1.交易场所_银行间.getValue());
                pageParam.put("TRADEPLACE03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
                pageParam.put("TRADEPLACE04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
                pageParam.put("plateDate", iPlatformDate.getSysDateStr());
                pageParam.put("finprodId", finprodId);
                pageParam.put("portfolioId", portfolioId);
                CfetsEtrIbRepoLibDto temp = cfetsEtrIbRepoinfoMapper.findSecPosition(pageParam);
                // 组装数据
                CfetsEtrIbRepoLibDto repoLibDto = new CfetsEtrIbRepoLibDto();
                repoLibDto.setPortfolioId(portfolioId);
                repoLibDto.setFinprodId(finprodId);
                repoLibDto.setBranch(temp.getBranch());
                repoLibDto.setSecAcctId(temp.getSecAcctId());
                repoLibDto.setScale(temp.getScale());
                repoLibDto.setPledgeStockId(config.getPledgeStockId());
                pledgeBondList.add(repoLibDto);
                break;
            }
        }
    }

}
