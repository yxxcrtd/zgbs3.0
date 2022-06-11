package com.joyintech.service.entrust.etrtradesysinterf.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.entrust.etrtradesysinterf.dao.EtrDistributeInterfTmpInfoMapper;
import com.joyintech.service.entrust.etrtradesysinterf.dao.EtrTradeSysInterfMapper;
import com.joyintech.service.entrust.etrtradesysinterf.dto.EtrDistributeInterfTmp;
import com.joyintech.service.entrust.etrtradesysinterf.dto.EtrTradePledgeDto;
import com.joyintech.service.entrust.etrtradesysinterf.dto.EtrTradeSysInterfDto;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrDistributeKey;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.entrust.impl.BaseEtrInvestInfoServiceImpl;
import com.joyintech.tams.invest.common.component.base.entrust.impl.dao.BaseEtrInvestInfoMapper;


/**   
 * @title: EtrStockService
 * @package com.joyintech.service.entrust.etrstock.service
 * @description: 
 * @author: junYin
 * @date: 2019-06-04 11:16
*/
@Service
public class EtrTradeSysInterfService extends BaseService<EtrTradeSysInterfDto>{

	@Autowired
	private EtrDistributeInterfTmpInfoMapper etrDistributeInterfTmpInfoMapper;
	
	@Autowired
	private EtrTradeSysInterfMapper etrTradeSysInterfMapper;
	
    /**
     * 投资委托信息Service
     */
    @Autowired
    private BaseEtrInvestInfoServiceImpl etrInvestInfoServiceImpl;
    
    /**
     * 投资委托信息Mapper
     */
    @Autowired
    private BaseEtrInvestInfoMapper etrInvestInfoMapper;

    @Autowired
	private JoyinbusinessCommonService joyinbusinessCommonService;
	
    /**
     * 指令接口查询
     * @param page
     * @param length
     * @param param
     * @return
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	public Page<EtrTradeSysInterfDto> selectForPage(int page, int length, Map<String, Object> param) throws Exception{
	
		List<String> traderList = (List<String>) param.get("traderList");
        if (!StringUtil.isEmptyList(traderList)) {
            param.put("traderList2", traderList);
        }
        return queryForPage(page, length, () -> etrTradeSysInterfMapper.selectList(param));
        
	}
	
	
	/**
     * 指令接口查询
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
	@SuppressWarnings("unchecked")
    public List<EtrTradeSysInterfDto> selectList(Map<String, Object> param){
		List<String> traderList = (List<String>) param.get("traderList");
        if (!StringUtil.isEmptyList(traderList)) {
            param.put("traderList2", traderList);
        }
    	return etrTradeSysInterfMapper.selectList(param);
    }
    
    
    /**
     * 指令回购质押品查询
     * <功能详细描述>
     * @param list
     * @return
     * @see
     * @since 1.0
     */
    public List<EtrTradePledgeDto> selectPledgeList(List<EtrTradeSysInterfDto> list){
    	List<EtrTradePledgeDto> pledgelist = new ArrayList<EtrTradePledgeDto>(); 
    	
    	if(list != null && !list.isEmpty()) {
	    	for(EtrTradeSysInterfDto dto : list) {
	    		if("F02".equals(dto.getFinprodType2()) || "F03".equals(dto.getFinprodType2()) ) {
		    		List<EtrTradePledgeDto> queryEtrPledgeList = etrTradeSysInterfMapper.queryEtrPledgeListByEtrId(dto.getEntrustId());
		    		if(queryEtrPledgeList!=null && !queryEtrPledgeList.isEmpty()) {
		    			pledgelist.addAll(queryEtrPledgeList);
		    		}
	    		}
	    	}
    	}
    	return pledgelist;
    }
    
    /**
     * 指令接收|拒绝
     * @param entrustId
     * @param recStatus
     * @return
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     */
    @Transactional(rollbackFor=Exception.class)
    public int handleEntrust(String entrustId,String recStatus,String trader) throws Exception {
    	int result = 0;
    	String entrustStatus = "";
    	if("1".equals(recStatus)) {//接收
    		entrustStatus = "03";//已接收
    	} else if("2".equals(recStatus)) {//释放
    		entrustStatus = "02";//待接收
    	}
    	
    	if(StringUtil.isNotEmpty(entrustStatus) && StringUtil.isNotEmpty(trader) && StringUtil.isNotEmpty(entrustId)) {
    		//校验数据合法性  根据委托代码和交易员信息 查询指令是否存在
    		EtrDistributeKey key = new EtrDistributeKey();
			key.setEntrustId(entrustId);
			key.setTrader(trader);
    		List<EtrDistribute> checkdata = this.etrInvestInfoMapper.selectEtrDistributeByPrimaryKey(key);
    		if(StringUtil.isEmptyList(checkdata)) {
    			return 0;
    		}
    		
    		//接收处理逻辑
    		if(entrustStatus.equals("03")) {
    			//1.删除3天前临时表的数据
    			etrDistributeInterfTmpInfoMapper.deleteBydate(DateUtil.addDay(new Date(), -2));
    			
    			//2.将指令分发信息 保存进入临时表，用于释放指令时使用。
    			etrDistributeInterfTmpInfoMapper.delete(entrustId);
    			EtrDistributeKey diskey = new EtrDistributeKey();
    			diskey.setEntrustId(entrustId);
    			List<EtrDistribute> etrDistributeList = etrInvestInfoMapper.selectEtrDistributeByPrimaryKey(diskey);
    			for(EtrDistribute dis: etrDistributeList) {
    				EtrDistributeInterfTmp tmp = new EtrDistributeInterfTmp();
    				BeanUtil.copyProperties(tmp,dis);
    				this.etrDistributeInterfTmpInfoMapper.insert(tmp); 
    			}
    			//3.更新指令表的ETR_ENTRUST状态为已接收|更新指令分发表的ETR_DISTRIBUTE状态|删除其他交易员的分发指令。
    			List<EtrDistribute> dislist = new ArrayList<EtrDistribute>();
    			EtrDistribute dis = new EtrDistribute();
    			dis.setEntrustId(entrustId);
    			dis.setTrader(trader);
    			dislist.add(dis);
    			boolean status = this.etrInvestInfoServiceImpl.receptionDelegate(dislist);
    			if(status) {
    				result = 1;
    				//处理履历
					CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
					oprHisReq.setEntrustId(entrustId);
					oprHisReq.setOperType(DictCodeListExt3.操作类型_接收.getValue());
					joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
    			}
    			
    		} 
    		//释放的处理逻辑
    		else {
    			
    			
    			//2.查询指令分发的临时表数据  插入  指令分发表，并将状态改为  待接收。
    			List<EtrDistributeInterfTmp> distmpList = etrDistributeInterfTmpInfoMapper.selectList(entrustId);
    			
    			if(!StringUtil.isEmptyList(distmpList)) {
    				//1.更新指令表的ETR_ENTRUST状态为待接收。
        			boolean status = etrInvestInfoServiceImpl.updateDelegateStatus(entrustId, DictCodeList.委托状态_待接收.getValue());
        			if(status) {
        				result = 1;
						//处理履历
						CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
						oprHisReq.setEntrustId(entrustId);
						oprHisReq.setOperType(DictCodeListExt3.操作类型_放弃.getValue());
						joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
        			}
    				List<EtrDistribute> disList = new ArrayList<EtrDistribute>();
    				
    				for(EtrDistributeInterfTmp entity : distmpList) {
    					 EtrDistribute dis = new EtrDistribute();
                         dis.setEntrustId(entity.getEntrustId());
                         dis.setTrader(entity.getTrader());
                         dis.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                         dis.setTradingHeader(entity.getTradingHeader());
                         disList.add(dis);
    				}
    				
    				this.etrInvestInfoServiceImpl.distributeDelegate(disList);
    				
    			}else{
    				//1.更新指令表的ETR_ENTRUST状态为待接收。
        			boolean status = etrInvestInfoServiceImpl.updateDelegateStatus(entrustId, DictCodeListExt3.委托状态_待分发.getValue());
        			if(status) {
        				result = 1;
						//处理履历
						CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
						oprHisReq.setEntrustId(entrustId);
						oprHisReq.setOperType(DictCodeListExt3.操作类型_放弃.getValue());
						joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
        			}
    			}
    			
    		}
    		
    	}
    	
    	
    	return result;
    }

	public String getRealNameByUser(String param){
		return etrTradeSysInterfMapper.getRealNameByUser(param);
	}

}
