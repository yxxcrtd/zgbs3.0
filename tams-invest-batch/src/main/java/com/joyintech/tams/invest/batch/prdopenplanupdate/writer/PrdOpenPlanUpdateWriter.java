package com.joyintech.tams.invest.batch.prdopenplanupdate.writer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.web.PlatformDate;
import com.joyintech.tams.common.base.dao.FinCashPlanMapper;
import com.joyintech.tams.common.base.dao.FinFloatAccrualScheMapper;
import com.joyintech.tams.common.base.dao.FinOpenPlanMapper;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.common.base.entity.FinCashPlanKey;
import com.joyintech.tams.common.base.entity.FinFloatAccrualSche;
import com.joyintech.tams.common.base.entity.FinFloatAccrualScheKey;
import com.joyintech.tams.common.base.entity.FinOpenPlan;
import com.joyintech.tams.common.base.entity.FinOpenPlanKey;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.batch.prdopenplanupdate.dao.PrdOpenPlanUpdateMapper;
import com.joyintech.tams.invest.batch.prdopenplanupdate.dto.AccrDateNeedOtherDto;
import com.joyintech.tams.invest.batch.prdopenplanupdate.dto.AccrDateOtherDto;
import com.joyintech.tams.invest.batch.prdopenplanupdate.dto.FinOpenPlanDto;
import com.joyintech.tams.invest.batch.prdopenplanupdate.dto.PrdOpenPlanUpdateDto;

/**
 *  批处理-市场日历接入后更新产品开放计划 writer
 */
@Service
@Scope("prototype")
public class PrdOpenPlanUpdateWriter extends BaseWriter<NeedToWriteEntity>{
	@Autowired
	private FinOpenPlanMapper finOpenPlanMapper;
	@Autowired
	private PrdOpenPlanUpdateMapper prdOpenPlanUpdateMapper;
	/**
     * 平台日期接口
     */
    @Autowired(required = false)
    private PlatformDate platformDateService;
    //新增浮动管理费计划
  	@Autowired
  	private FinFloatAccrualScheMapper finFloatAccrualScheMapper;
  	@Autowired
  	private FinCashPlanMapper finCashPlanMapper;
    

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void write(List<? extends NeedToWriteEntity> items) throws Exception {

		List<FinOpenPlanDto> finOpenPlanDtoList = (List<FinOpenPlanDto>) items;
		
		List<String> prodidList=new ArrayList<String>();
		
		for (FinOpenPlanDto finOpenPlanDto : finOpenPlanDtoList) {
			//更新开放计划
			if (!StringUtil.isEmptyList(finOpenPlanDto.getFinOpenPlanList())) {
				// 先删后插
				PrdOpenPlanUpdateDto dto = finOpenPlanDto.getPrdOpenPlanUpdateDto();
				FinOpenPlanKey fopKey = new FinOpenPlanKey();
				fopKey.setCashId(dto.getCashId());
				fopKey.setOpenType(dto.getOpenType());
				fopKey.setOpenDateEnd(dto.getOpenDateEnd());
				prdOpenPlanUpdateMapper.deleteOldOpenPlans(fopKey);
				for (FinOpenPlan finOpenPlan : finOpenPlanDto.getFinOpenPlanList()) {
					// 查询无重复
					FinOpenPlanKey key = new FinOpenPlanKey();
					key.setCashId(finOpenPlan.getCashId());
					key.setOpenType(finOpenPlan.getOpenType());
					key.setOpenDateEnd(finOpenPlan.getOpenDateEnd());
					FinOpenPlan fop = finOpenPlanMapper.selectByPrimaryKey(key);
					
					if(!StringUtil.isNotEmpty(fop)) {
						finOpenPlanMapper.insert(finOpenPlan); // 如果为空再插入操作
					}
					//设置List存储唯一的产品代码
					if(StringUtil.isNotEmpty(finOpenPlan.getFinprodId())){
						if(!prodidList.contains(finOpenPlan.getFinprodId())){
							prodidList.add(finOpenPlan.getFinprodId());
						}
					}
				}
				
				//生成最新的浮动管理费计提计划 
				createFinFloatAccrualSche(prodidList);
				//生成最新的回补计划 
				createFinAnapleroticAccrualSche(prodidList);
				//若分红与申购同频时，当新的开放计划生成时生成新的分红计划
				if(!StringUtil.equals(finOpenPlanDto.getPrdOpenPlanUpdateDto().getOpenType(), "02" )
				&&StringUtil.equals(finOpenPlanDto.getPrdOpenPlanUpdateDto().getPayCycleV(), "08")
				&&StringUtil.equals(finOpenPlanDto.getPrdOpenPlanUpdateDto().getProfitType(), "03")) {
					createFinCashPlan(prodidList);
				}
				//若付息与申购同频时，当新的付息计划生成时生成新的付息计划
				if(!StringUtil.equals(finOpenPlanDto.getPrdOpenPlanUpdateDto().getOpenType(), "02" )
				&&StringUtil.equals(finOpenPlanDto.getPrdOpenPlanUpdateDto().getPayCycleV(), "03")
				&&StringUtil.equals(finOpenPlanDto.getPrdOpenPlanUpdateDto().getProfitType(), "02")) {
					createFinCashPlan1(prodidList);
				}
			}
			
			//更新付息/分红计划
			if (!StringUtil.isEmptyList(finOpenPlanDto.getFinCashPlanList())) {
				// 先删后插
				PrdOpenPlanUpdateDto dto = finOpenPlanDto.getPrdOpenPlanUpdateDto();
				FinCashPlanKey fcpKey = new FinCashPlanKey();
				fcpKey.setCashId(dto.getCashId());
				fcpKey.setMdate(dto.getPayFPMdate());
				prdOpenPlanUpdateMapper.deleteOldPayPlans(fcpKey);
				for (FinCashPlan finCashPlan : finOpenPlanDto.getFinCashPlanList()) {
					// 查询无重复
					FinCashPlanKey key = new FinCashPlanKey();
					key.setCashId(finCashPlan.getCashId());
					key.setMdate(finCashPlan.getMdate());
					FinCashPlan fcp = finCashPlanMapper.selectByPrimaryKey(key);
					
					if(!StringUtil.isNotEmpty(fcp)) {
						finCashPlanMapper.insert(finCashPlan); // 如果为空再插入操作
					}
				}
			}
			//更新付费计划
			if (!StringUtil.isEmptyList(finOpenPlanDto.getFinFeePlanList())) {
				// 先删后插
				PrdOpenPlanUpdateDto dto = finOpenPlanDto.getPrdOpenPlanUpdateDto();
				FinCashPlanKey fcpKey = new FinCashPlanKey();
				fcpKey.setCashId(dto.getCashId());
				fcpKey.setMdate(dto.getFirstCalEndDate());
				prdOpenPlanUpdateMapper.deleteOldPayPlans(fcpKey);
				for (FinCashPlan finCashPlan : finOpenPlanDto.getFinFeePlanList()) {
					// 查询无重复
					FinCashPlanKey key = new FinCashPlanKey();
					key.setCashId(finCashPlan.getCashId());
					key.setMdate(finCashPlan.getMdate());
					FinCashPlan fcp = finCashPlanMapper.selectByPrimaryKey(key);
					
					if(!StringUtil.isNotEmpty(fcp)) {
						finCashPlanMapper.insert(finCashPlan); // 如果为空再插入操作
					}
				}
			}			
		}
		//更新系统参数-市场日历更新联动更新的开发计划日期（年）
		String platYear = DateUtil.format(platformDateService.getSysDate()).substring(0, 4);
		prdOpenPlanUpdateMapper.updateSysParam(platYear);
	}

	/**
	 * 根据开放计划生成浮动管理费计提计划 
	 */
	public void createFinFloatAccrualSche(List<String> finprodidList){
		
		if (!StringUtil.isEmptyList(finprodidList)) {
			for (String finprodId : finprodidList) {
				// 1.根据产品代码  查询到相应的开放计划净值日
				List<Date> dateOpenPlanList = prdOpenPlanUpdateMapper.findNdateOrEndDateByProdid(finprodId);
				if(StringUtil.isEmptyList(dateOpenPlanList)){//过滤收益型产品
					continue;
				}
				// 1.2 当净值型产品没有挂浮动管理费时，无需更新浮动管理费计划
				Map<String,Object> map = new HashMap<String,Object>();
		        map.put("FINPROD_ID", finprodId);
		        map.put("CASH_TYPE_2", DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
		        //查询浮动管理费的现金流代码
		        String cashId = prdOpenPlanUpdateMapper.selectfinCashInfo(map);
		        if(StringUtil.isEmpty(cashId)){
		        	continue;
		        }
				// 2.将其他时间点也塞进dateOpenPlanList内
				AccrDateNeedOtherDto accrDateNeedOtherDto = prdOpenPlanUpdateMapper
						.findOtherNeedDateByProdid(finprodId);
				//3.根据浮动管理费的cashid删除产品原计提计划
				FinFloatAccrualScheKey finFloatAccrualScheKey=new FinFloatAccrualScheKey();
				finFloatAccrualScheKey.setCashId(accrDateNeedOtherDto.getCashId());
				prdOpenPlanUpdateMapper.deleteFloatAccrualScheByPrimaryKey(finFloatAccrualScheKey);
				
				if (StringUtil.isNotEmpty(accrDateNeedOtherDto)) {
					addDateToList(dateOpenPlanList, accrDateNeedOtherDto.getVdate());
					addDateToList(dateOpenPlanList, accrDateNeedOtherDto.getMdate());
					addDateToList(dateOpenPlanList, accrDateNeedOtherDto.getProvisionFirstDay());
					addDateToList(dateOpenPlanList, accrDateNeedOtherDto.geteAccrDate());
				}
				// 对dateOpenPlanList进行处理
				Date startDate = dateOpenPlanList.get(0);
				boolean flag = true;
				for (Date endDate : dateOpenPlanList) {
					if (startDate.compareTo(endDate) == 0) {
						startDate = endDate;
						continue;
					}
					if (endDate.compareTo(accrDateNeedOtherDto.getProvisionFirstDay()) < 0) {
						startDate = endDate;
						continue;
					}
					if (endDate.compareTo(accrDateNeedOtherDto.geteAccrDate()) > 0) {
						startDate = endDate;
						continue;
					}
					FinFloatAccrualSche finFloatAccrualScheFirst = new FinFloatAccrualSche();
					finFloatAccrualScheFirst.setCashId(accrDateNeedOtherDto.getCashId());
					finFloatAccrualScheFirst.setAccrDate(endDate);
					finFloatAccrualScheFirst.setAccrStrDate(startDate);
					if (flag) {
						finFloatAccrualScheFirst.setAccrStrDate(DateUtil.addDay(startDate, -1));
						flag = false;
					}

					finFloatAccrualScheFirst.setAccrEndDate(endDate);
					finFloatAccrualScheFirst.setValDate(endDate);
					finFloatAccrualScheFirst.setUpdateTime(new Date());
					//插入更新的浮动管理费计提计划
					finFloatAccrualScheMapper.insert(finFloatAccrualScheFirst);
					startDate = endDate;
				}
			}
		}
	}
	
	/**
	 * 根据开放计划生成回补计划 
	 */
	public void createFinAnapleroticAccrualSche(List<String> finprodidList){
		
		if (!StringUtil.isEmptyList(finprodidList)) {
			for (String finprodId : finprodidList) {
				// 1.根据产品代码  查询到相应的开放计划净值日
				List<Date> dateOpenPlanList = prdOpenPlanUpdateMapper.findNdateOrEndDateByProdid(finprodId);
				if(StringUtil.isEmptyList(dateOpenPlanList)){//过滤收益型产品
					continue;
				}
				// 1.2 当净值型产品没有回补时，无需更新回补计划
				Map<String,Object> map = new HashMap<String,Object>();
		        map.put("FINPROD_ID", finprodId);
		        //查询回补的现金流代码
		        String cashId = prdOpenPlanUpdateMapper.selectfinAnapleroticInfo(map);
		        if(StringUtil.isEmpty(cashId)){
		        	continue;
		        }
				// 2.将其他时间点也塞进dateOpenPlanList内
		        AccrDateOtherDto accrDateOtherDto = prdOpenPlanUpdateMapper
						.findOtherNeedDate(finprodId);
				//3.根据回补的cashid删除产品原计提计划
				FinFloatAccrualScheKey finFloatAccrualScheKey=new FinFloatAccrualScheKey();
				finFloatAccrualScheKey.setCashId(accrDateOtherDto.getCashId());
				prdOpenPlanUpdateMapper.deleteFloatAccrualScheByPrimaryKey(finFloatAccrualScheKey);
				
				if (StringUtil.isNotEmpty(accrDateOtherDto)) {
					addDateToList(dateOpenPlanList, accrDateOtherDto.getVdate());
					addDateToList(dateOpenPlanList, accrDateOtherDto.getMdate());
					addDateToList(dateOpenPlanList, accrDateOtherDto.getFirstAnapleroticDate());
					addDateToList(dateOpenPlanList, accrDateOtherDto.getAnapleroticEndDate());
					addDateToList(dateOpenPlanList, accrDateOtherDto.getFirstAnapleroticCycleBegDate());
				}
				if(accrDateOtherDto.getFirstAnapleroticCycleBegDate().compareTo(accrDateOtherDto.getFirstAnapleroticDate())<0) {
					FinFloatAccrualSche finFloatAccrualScheFirst = new FinFloatAccrualSche();
					finFloatAccrualScheFirst.setCashId(accrDateOtherDto.getCashId());
					finFloatAccrualScheFirst.setAccrDate(accrDateOtherDto.getFirstAnapleroticDate());
					finFloatAccrualScheFirst.setAccrStrDate(accrDateOtherDto.getFirstAnapleroticCycleBegDate());
					finFloatAccrualScheFirst.setAccrEndDate(accrDateOtherDto.getFirstAnapleroticDate());
					finFloatAccrualScheFirst.setValDate(accrDateOtherDto.getFirstAnapleroticDate());
					finFloatAccrualScheFirst.setUpdateTime(new Date());
					finFloatAccrualScheMapper.insert(finFloatAccrualScheFirst);
				}
				// 对dateOpenPlanList进行处理
				Date startDate = dateOpenPlanList.get(0);
				boolean flag = true;
				for (Date endDate : dateOpenPlanList) {
					if (startDate.compareTo(endDate) == 0) {
						startDate = endDate;
						continue;
					}
					if (endDate.compareTo(accrDateOtherDto.getFirstAnapleroticCycleBegDate()) < 0) {
						startDate = endDate;
						continue;
					}
					if (endDate.compareTo(accrDateOtherDto.getFirstAnapleroticDate()) < 0) {
						startDate = endDate;
						continue;
					}
					if (endDate.compareTo(accrDateOtherDto.getAnapleroticEndDate()) > 0) {
						startDate = endDate;
						continue;
					}
					FinFloatAccrualSche finFloatAccrualScheFirst = new FinFloatAccrualSche();
					finFloatAccrualScheFirst.setCashId(accrDateOtherDto.getCashId());
					finFloatAccrualScheFirst.setAccrDate(endDate);
					finFloatAccrualScheFirst.setAccrStrDate(startDate);
					if (flag) {
						finFloatAccrualScheFirst.setAccrStrDate(DateUtil.addDay(startDate, -1));
						flag = false;
					}

					finFloatAccrualScheFirst.setAccrEndDate(endDate);
					finFloatAccrualScheFirst.setValDate(endDate);
					finFloatAccrualScheFirst.setUpdateTime(new Date());
					//插入更新的回补计提计划
					if(finFloatAccrualScheFirst.getAccrStrDate().compareTo(accrDateOtherDto.getFirstAnapleroticCycleBegDate()) >= 0) {	
						// 查询无重复
						FinFloatAccrualScheKey key = new FinFloatAccrualScheKey();
						key.setCashId(accrDateOtherDto.getCashId());
						key.setAccrDate(finFloatAccrualScheFirst.getAccrDate());
						FinFloatAccrualSche fcp = finFloatAccrualScheMapper.selectByPrimaryKey(key);						
						if(!StringUtil.isNotEmpty(fcp)) {
							finFloatAccrualScheMapper.insert(finFloatAccrualScheFirst); // 如果为空再插入操作
						}					
					}
					startDate = endDate;
				}
			}
		}
	}
	

	/**
	 * 若分红计划与申购同频时，当新的开放计划生成时生成新的分红计划 
	 */
	public void createFinCashPlan(List<String> finprodidList){
		
		if (!StringUtil.isEmptyList(finprodidList)) {
			for (String finprodId : finprodidList) {
				// 根据产品代码  查询到相应的申购开放计划
				List<FinOpenPlan> openPlanList = prdOpenPlanUpdateMapper.findOpenPlanListByProdid(finprodId);
				if(openPlanList.get(0).getCashId() != null) {					
					String cashId = openPlanList.get(0).getCashId();
					// 先删后插
					FinCashPlanKey fcpKey = new FinCashPlanKey();
					fcpKey.setCashId(cashId);
					prdOpenPlanUpdateMapper.deleteOldPayPlan(fcpKey);
				}
				if(!StringUtil.isEmptyList(openPlanList)){
					  for(int i = 0; i < openPlanList.size();i++){
	                    	FinCashPlan finCashPlan = new FinCashPlan();
	                    	FinOpenPlan finOpenPlan = openPlanList.get(i);
	                    	if(finOpenPlan.getCashId() != null) {
	                    	if(0 == i){
	                    		finCashPlan.setCashId(finOpenPlan.getCashId());
	                    		finCashPlan.setCashNum(1L);
	                            finCashPlan.setCashType(DictCodeList.现金流类型_中间收息.getValue());
	                            finCashPlan.setVdateUnadjust(finOpenPlan.getOpenVdateUn());
	                            finCashPlan.setMdateUnadjust(finOpenPlan.getOpenDateEnd());
	                            finCashPlan.setPayDateUnadjust(finOpenPlan.getOpenVdate());
	                            finCashPlan.setVdate(finOpenPlan.getOpenVdateUn());
	                            finCashPlan.setMdate(finOpenPlan.getOpenDateEnd());
	                            finCashPlan.setPayDate(finOpenPlan.getOpenVdate());
	                            finCashPlan.setFinprodId(finprodId);
	                            finCashPlan.setFinprodType("F16");
	                            finCashPlan.setFinprodType2("F16");
	                            finCashPlan.setBranch(0L);
	                    	}else{
	                    		finCashPlan.setCashId(finOpenPlan.getCashId());
								// Sonar Fix modify by huwei 220609
	                    		finCashPlan.setCashNum((long)i+1);
	                            finCashPlan.setCashType(DictCodeList.现金流类型_中间收息.getValue());
	                            finCashPlan.setVdateUnadjust(openPlanList.get(i - 1).getOpenDateEnd());
	                            finCashPlan.setMdateUnadjust(finOpenPlan.getOpenDateEnd());
	                            finCashPlan.setPayDateUnadjust(finOpenPlan.getOpenVdate());
	                            finCashPlan.setVdate(openPlanList.get(i - 1).getOpenDateEnd());
	                            finCashPlan.setMdate(finOpenPlan.getOpenDateEnd());
	                            finCashPlan.setPayDate(finOpenPlan.getOpenVdate());
	                            finCashPlan.setFinprodId(finprodId);
	                            finCashPlan.setFinprodType("F16");
	                            finCashPlan.setFinprodType2("F16");
	                            finCashPlan.setBranch(0L);
	                    	}		                    
	                    	// 查询无重复
							FinCashPlanKey key = new FinCashPlanKey();
							key.setCashId(finCashPlan.getCashId());
							key.setMdate(finCashPlan.getMdate());
							FinCashPlan fcp = finCashPlanMapper.selectByPrimaryKey(key);
							
							if(!StringUtil.isNotEmpty(fcp)) {
								finCashPlanMapper.insert(finCashPlan); // 如果为空再插入操作
	                    }
				}
					  }
				}
			}
		}							
			
	}

	/**
	 * 若付息计划与申购同频时，当新的开放计划生成时生成新的付息计划 
	 */
	public void createFinCashPlan1(List<String> finprodidList){
		
		if (!StringUtil.isEmptyList(finprodidList)) {
			for (String finprodId : finprodidList) {
				// 根据产品代码  查询到相应的申购开放计划
				List<FinOpenPlan> openPlanList = prdOpenPlanUpdateMapper.findOpenPlanListByProdid(finprodId);
				if(openPlanList.get(0).getCashId() != null) {					
					String cashId = openPlanList.get(0).getCashId();
					// 先删后插
					FinCashPlanKey fcpKey = new FinCashPlanKey();
					fcpKey.setCashId(cashId);
					prdOpenPlanUpdateMapper.deleteOldPayPlan(fcpKey);
				}
				if(!StringUtil.isEmptyList(openPlanList)){
					  for(int i = 0; i < openPlanList.size();i++){
	                    	FinCashPlan finCashPlan = new FinCashPlan();
	                    	FinOpenPlan finOpenPlan = openPlanList.get(i);
	                    	if(finOpenPlan.getCashId() != null) {
	                    	if(0 == i){
	                    		finCashPlan.setCashId(finOpenPlan.getCashId());
	                    		finCashPlan.setCashNum(1L);
	                            finCashPlan.setCashType(DictCodeList.现金流类型_中间收息.getValue());
	                            finCashPlan.setVdateUnadjust(finOpenPlan.getOpenVdateUn());
	                            finCashPlan.setMdateUnadjust(finOpenPlan.getOpenVdate());
	                            finCashPlan.setPayDateUnadjust(finOpenPlan.getOpenVdate());
	                            finCashPlan.setVdate(finOpenPlan.getOpenVdateUn());
	                            finCashPlan.setMdate(finOpenPlan.getOpenVdate());
	                            finCashPlan.setPayDate(finOpenPlan.getOpenVdate());
	                            finCashPlan.setFinprodId(finprodId);
	                            finCashPlan.setFinprodType("F16");
	                            finCashPlan.setFinprodType2("F16");
	                            finCashPlan.setBranch(0L);
	                    	}else{
	                    		finCashPlan.setCashId(finOpenPlan.getCashId());
								// Sonar Fix modify by huwei 220609
	                    		finCashPlan.setCashNum((long)i + 1);
	                            finCashPlan.setCashType(DictCodeList.现金流类型_中间收息.getValue());
	                            finCashPlan.setVdateUnadjust(openPlanList.get(i - 1).getOpenVdate());
	                            finCashPlan.setMdateUnadjust(finOpenPlan.getOpenVdate());
	                            finCashPlan.setPayDateUnadjust(finOpenPlan.getOpenVdate());
	                            finCashPlan.setVdate(openPlanList.get(i - 1).getOpenVdate());
	                            finCashPlan.setMdate(finOpenPlan.getOpenVdate());
	                            finCashPlan.setPayDate(finOpenPlan.getOpenVdate());
	                            finCashPlan.setFinprodId(finprodId);
	                            finCashPlan.setFinprodType("F16");
	                            finCashPlan.setFinprodType2("F16");
	                            finCashPlan.setBranch(0L);
	                    	}
	                    	// 查询无重复
							FinCashPlanKey key = new FinCashPlanKey();
							key.setCashId(finCashPlan.getCashId());
							key.setMdate(finCashPlan.getMdate());
							FinCashPlan fcp = finCashPlanMapper.selectByPrimaryKey(key);
							
							if(!StringUtil.isNotEmpty(fcp)) {
								finCashPlanMapper.insert(finCashPlan); // 如果为空再插入操作
	                    }
				}
				}
			}	  
			}
		}							
			
	}
	
	/**
	 * 按降序向列表添加日期
	 * @param list
	 * @param date
	 */
	private void  addDateToList(List<Date> list, Date date){
	    if(date == null){
	        return;
	    }
	    for(int i=0;i<list.size();i++){
	        if(list.get(i).compareTo(date)==0){
	            return;
	        } 
	        if(list.get(i).compareTo(date)>0){
	            list.add(i,date);
	            return;
            }
	    }
	    list.add(date);
	}
	
}
