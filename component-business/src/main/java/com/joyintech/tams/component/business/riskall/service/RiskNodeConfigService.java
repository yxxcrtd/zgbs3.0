package com.joyintech.tams.component.business.riskall.service;

import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.common.base.dao.RiskNodeConfigDtlMapper;
import com.joyintech.tams.common.base.dao.RiskNodeConfigMapper;
import com.joyintech.tams.common.base.entity.RiskNodeConfig;
import com.joyintech.tams.common.base.entity.RiskNodeConfigDtl;
import com.joyintech.tams.component.business.riskall.constant.NodeConfigConstant;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 风控节点配置服务类型
 *
 * @author gwl
 * @version 1.0 2022年5月31日
 * @see RiskNodeConfigService
 * @since 1.0
 */
@Service
public class RiskNodeConfigService {

    @Resource
    private RiskNodeConfigMapper rskNodeConfigMapper;

    @Resource
    private RiskNodeConfigDtlMapper rskNodeConfigDtlMapper;

    @Resource
    private RedisTemplate redisTemplate;


    /**
     * <p>根据传入的对象获取配置数据
     * <p>报文实例：
     * <p>{
     *     <p>"interfaceType":"Bond001", 场外现券：Bond001,场外质押式回购：REPO003,场内现券：Bond002,配置在RSK_NODE_CONFIG表中根节点的值
     *     <p>"busiAct":"02", 业务动作,对应数据字典 DictCode.业务动作
     *     <p>"ps":"T01", 交易方向,对应数据字典 DictCode.交易方向
     *     <p>"tradePlace":"01", 交易方向,对应数据字典 DictCode.交易场所
     *     <p>"decisionAct":"01",系统动作,对应数据字典 DictCode.系统动作
     *     <p>"flowStatus":"01", 流程节点,对应数据字典 DictCode.流程节点
     *    <p> "busiType":"02", 业务类型,对应数据字典 DictCode.头寸业务类型
     * <p>}
     * <p>具体字段以RSK_NODE_CONFIG表中配置的字段为准
     * @param param
     * @return
     */
    public RiskNodeConfigDtl getNodeConfig(BaseMap param) {
        RiskNodeConfig nodeConfig = buildTree(param.getString(NodeConfigConstant.INTERFACE_TYPE));
        if (Objects.isNull(nodeConfig)) return null;
        RiskNodeConfig leafNode = compute(param, nodeConfig.getChildren());
        if (Objects.isNull(leafNode)) return null;
        return rskNodeConfigDtlMapper.selectByPrimaryKey(leafNode.getNodeId());
    }

    /**
     * 根据传入的rootId构建树形
     *
     * @param rootId
     * @return
     */
    private RiskNodeConfig buildTree(String rootId) {
        List<RiskNodeConfig> trees = rskNodeConfigMapper.selectByRootId(rootId);
        if (CollectionUtils.isEmpty(trees)) return null;
        List<RiskNodeConfig> list = trees.stream().filter(item -> item.getpNodeId().equals("0")).collect(Collectors.toList());
        Map<String, List<RiskNodeConfig>> map = trees.stream().collect(Collectors.groupingBy(RiskNodeConfig::getpNodeId));
        recursionFnTree(list, map);
        return list.get(0);
    }


    /**
     * 递归遍历节点
     *
     * @param list
     * @param map
     */
    private void recursionFnTree(List<RiskNodeConfig> list, Map<String, List<RiskNodeConfig>> map) {
        for (RiskNodeConfig treeSelect : list) {
            List<RiskNodeConfig> childList = map.get(treeSelect.getNodeId());
            treeSelect.setChildren(childList);
            if (null != childList && 0 < childList.size()) {
                recursionFnTree(childList, map);
            }
        }
    }

    /**
     * 找到匹配的叶子节点
     *
     * @param param
     * @param nodes
     * @return
     */
    private RiskNodeConfig compute(BaseMap param, List<RiskNodeConfig> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return null;
        }
        for (RiskNodeConfig node : nodes) {
            if (NodeConfigConstant.LEAF.equals(node.getNodeType())) {
                return node;
            }
            Object value = param.get(node.getNodeCode());
            if (String.valueOf(value).equals(node.getNodeVal())) {
                return compute(param, node.getChildren());
            }

        }
        return null;
    }
}
