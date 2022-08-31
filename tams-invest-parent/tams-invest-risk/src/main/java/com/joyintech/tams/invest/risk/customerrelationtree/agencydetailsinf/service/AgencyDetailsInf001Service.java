/*
 * 文件名：AgencyDetailsInf001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangkang
 * 修改时间：2019年12月06日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.risk.customerrelationtree.agencydetailsinf.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.risk.customerrelationtree.agencydetailsinf.dto.AgencyDetailsInf001Dto;
import com.joyintech.tams.invest.risk.customerrelationtree.agencydetailsinf.dto.TreeNode;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 机构详细信息service
 * 〈功能详细描述〉
 * @author wangkang
 * @version 1.0 2019年12月06日
 * @see AgencyDetailsInf001Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class AgencyDetailsInf001Service extends CommonExportService<AgencyDetailsInf001Dto> {
    
	// 平台日期
	@Autowired(required = false)
	private IPlatformDate iPlatformDate;
	
    /**
     * 机构树展开最大节点数量
     */
    @Value("${application.maxTreeNodes:5}")
    private int maxTreeNodes;
    
	/**
     * 查询 <功能详细描述>
     * @param pageParam 查询信息
     * @return 查询结果
     * @throws Exception 抛出异常
     * @see
     * @since 1.0
     */
	public Page<AgencyDetailsInf001Dto> selectForPage(Map<String, Object> pageParam){
		pageParam.put("sysDate", iPlatformDate.getSysDateStr());
        return restTemplate.postForPage(
                ServiceApplication.RISK_SERVICE_NAME + "/AgencyDetailsInf001/findPage", pageParam,
                AgencyDetailsInf001Dto.class);
	}

	/**
	 * 机构联想框
	 * @param agencyId
	 * @return
	 * @see
	 * @since 1.0
	 */
	public Page<SelectOptionDto> getAgency(Map<String, Object> pageParam) {
        return restTemplate.postForPage(ServiceApplication.RISK_SERVICE_NAME
				+ "/AgencyDetailsInf001/getAgency", pageParam, SelectOptionDto.class);
    }
	
	/**
	 * 删除机构
	 * @param seqNo
	 * @return
	 * @see
	 * @since 1.0
	 */
	public int delNode(String seqNo) {
        return restTemplate.getForObject(ServiceApplication.RISK_SERVICE_NAME
                + "/AgencyDetailsInf001/delNode?seqNo=" +StringUtil.defaultString(seqNo),Integer.class);       
    }
	
	
	/**
	 * 查询树数据
	 * @param agencyId
	 * @return
	 * @see
	 * @since 1.0
	 */
	public TreeNode getTreeData(String agencyId ) {
	    List<AgencyDetailsInf001Dto> list = restTemplate.getForList(ServiceApplication.RISK_SERVICE_NAME
                + "/AgencyDetailsInf001/getTreeData?agencyId=" +StringUtil.defaultString(agencyId),AgencyDetailsInf001Dto.class);
	    TreeNode treeDat = toTreeDate(agencyId, list);
	    return treeDat;
    }
	
	/**
	 * 树数据处理
	 * @param agencyId
	 * @param twoNodeId
	 * @param list
	 * @return
	 * @see
	 * @since 1.0
	 */
	@SuppressWarnings("unused")
    private TreeNode toTreeDate(String agencyId, List<AgencyDetailsInf001Dto> list){
		TreeNode tree = new TreeNode();
        int count = 0;
	    if(!list.isEmpty()){
	    	tree.setId(agencyId);
			tree.setName(list.get(0).getpName());
			Map<String, List<TreeNode>> level2 = new HashMap<>();
			//遍历得出有多少二级节点  高管、股东等，整合到各自的二级节点中
			for (AgencyDetailsInf001Dto dto : list) {
				List<TreeNode> level3 = null;
				if(dto.getCorOrgtype() == null){
					dto.setCorOrgtype(StringUtil.isEmpty(dto.getCorClass()) ? "00":dto.getCorClass());
				}
				if (!level2.containsKey(dto.getCorOrgtype())) {
					level3 = new ArrayList<TreeNode>();
					level2.put(dto.getCorOrgtype(), level3);
				} else {
					level3 = level2.get(dto.getCorOrgtype());
				}
				TreeNode childNode = new TreeNode();
				childNode.setId(StringUtil.isEmpty(dto.getId())?dto.getSeqNo():dto.getId());
				childNode.setSeqNo(dto.getSeqNo());
				childNode.setName(dto.getName());
				childNode.setpId(dto.getCorOrgtype());
				childNode.setHasEndAgency(!StringUtil.isEmpty(dto.getAgencyId()) ? true : false);
				level3.add(childNode);
			}
			List<List<TreeNode>> level2List = new ArrayList<List<TreeNode>>(level2.values());
			int maxSideNum = (int) Math.floor(list.size() / 2);//单侧最大数量
			//动态规划算法，同01背包问题
			int[][] temp = new int[level2.size() + 1][maxSideNum + 1];
			String[][] tempStr = new String[level2.size() + 1][maxSideNum + 1];
			for (int i = 1; i <= level2List.size(); i++) {        //从第一个二级分类开始
				for (int j = 1; j <= maxSideNum; j++) {    //限制数量为j时
					int currentNum = level2List.get(i - 1).size();    //当前值
					if (j < currentNum || temp[i - 1][j - currentNum] + currentNum < temp[i - 1][j]) {
						temp[i][j] = temp[i - 1][j];
						tempStr[i][j] = tempStr[i - 1][j];
					} else {
						temp[i][j] = temp[i - 1][j - currentNum] + currentNum;
						if (tempStr[i - 1][j - currentNum] != null) {
							tempStr[i][j] = tempStr[i - 1][j - currentNum] + "," + level2List.get(i - 1).get(0).getpId();
						} else {
							tempStr[i][j] = level2List.get(i - 1).get(0).getpId();
						}

					}
				}
			}
			String pIdStr = tempStr[level2.size()][maxSideNum];
			String[] pIds = pIdStr==null?new String[0]:pIdStr.split(",");
			List<TreeNode> oneRightChiList = new ArrayList<>();
			List<TreeNode> oneLeftChiList = new ArrayList<>();
			for (String pId : pIds) {
				TreeNode two = new TreeNode();
				two.setId(pId);
				two.setpId(agencyId);
				two.setName(StringUtil.isEmpty(SysDataUtil.getNameFromDataDict(DictCode.关联机构类型, pId))?SysDataUtil.getNameFromDataDict(DictCode.关联分类, pId):SysDataUtil.getNameFromDataDict(DictCode.关联机构类型, pId));
				two.setRightChi(level2.get(pId));
				oneRightChiList.add(two);
				level2.remove(pId);
			}
			for (String pId : level2.keySet()) {
				TreeNode two = new TreeNode();
				two.setId(pId);
				two.setpId(agencyId);
				two.setName(StringUtil.isEmpty(SysDataUtil.getNameFromDataDict(DictCode.关联机构类型, pId))?SysDataUtil.getNameFromDataDict(DictCode.关联分类, pId):SysDataUtil.getNameFromDataDict(DictCode.关联机构类型, pId));
				two.setLeftChi(level2.get(pId));
				oneLeftChiList.add(two);
			}
			tree.setLeftChi(oneLeftChiList);
			tree.setRightChi(oneRightChiList);
			tree.setNodeCount(list.size());

		}
		return tree;
	}
	/**
	 * 将一个list均分成n个list,主要通过偏移量来实现的
	 * @param source
	 * @param n
	 * @return
	 * @see
	 * @since 1.0
	 */
    public static <T> List<List<T>> averageAssign(List<T> source,int n){
        List<List<T>> result=new ArrayList<List<T>>();
        //(先计算出余数)
        int remaider=source.size()%n;
        //商
        int number=source.size()/n;
        //偏移量
        int offset=0;
        for(int i=0;i<n;i++){
            List<T> value=null;
            if(remaider>0){
                value=source.subList(i*number+offset, (i+1)*number+offset+1);
                remaider--;
                offset++;
            }else{
                value=source.subList(i*number+offset, (i+1)*number+offset);
            }
            result.add(value);
        }
        return result;
    }	
    
	/**
    * 导出 
    */
	@Override
	protected void buildExportParam(ExportParam exportParam) {
	    exportParam.config("/com/joyintech/tams/invest/risk/customerrelationtree/agencydetailsinf/conf/export_conf.xml");
	}
	
	/**
	 * 
	 */
	@Override
	public Page<AgencyDetailsInf001Dto> selectPageData(Map<String, Object> selectParam) {
		// TODO Auto-generated method stub
	    return selectForPage(selectParam);
	}

	
	
	
}
