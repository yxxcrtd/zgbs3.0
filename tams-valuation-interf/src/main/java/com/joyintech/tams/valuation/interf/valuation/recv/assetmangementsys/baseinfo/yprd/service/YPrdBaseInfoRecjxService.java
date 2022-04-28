package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.yprd.service;

import com.jixu.common.type.ResultInterface;
import com.jixu.joyin2jixu.service.Joyin2JixuInterface;
import com.jixu.msg.dto.JJPortfolioDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValPrdBaseInfo;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.yprd.dto.YPrdBaseInfoProcessorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 利率型产品产品信息接口
 * 对利率型基础信息筛选处理，供给jixu使用
 * @author chufengjie
 * @version 1.0 2018年5月22日
 * @see YPrdBaseInfoRecjxService
 * @since 1.0
 */
@Service
public class YPrdBaseInfoRecjxService {
	
    /**
     * 接口共通处理Service
     */
    @Autowired
    private InterfCommonService interfCommonService;
    
    /**
     * 几许为joyin调用接口，新建产品等信息处理Service
     */
    @Autowired
    private Joyin2JixuInterface joyin2JixuInterface;
	
	
	public void prodInfoJx(YPrdBaseInfoProcessorResult  result) {
		List<ValPrdBaseInfo> listValPrdInfoY = result.getListValPrdInfoY();
	       List<JJPortfolioDto> jjPortfolioList = new ArrayList<JJPortfolioDto>(); 
	        // 获取基础信息Dto
	        for (ValPrdBaseInfo dto : listValPrdInfoY) {
	            JJPortfolioDto jjPortfolioDto = getJjPortfolio(dto);
	            if(jjPortfolioDto != null){
	            	jjPortfolioList.add(jjPortfolioDto);
	            } 	        	
	        } 	        
            ResultInterface resultInterface= joyin2JixuInterface.createPortfolios(jjPortfolioList);
	   }
	
     /**
      *  几许数据初始化
      */
    private JJPortfolioDto getJjPortfolio(ValPrdBaseInfo dto){
    	JJPortfolioDto jjPortfolioDto = new JJPortfolioDto();
    	//账套
    	jjPortfolioDto.setBookSet_id(dto.getProdId());
    	//货币
    	jjPortfolioDto.setStandard_ccy(dto.getCcy());
		// add by weipeng on 20220408：会计引擎升级改造，若按照内部科目编码分录，则需要对应新会计引擎模板 start
		// 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录 start
		String accountEntryType =  CacheUtil.getSysParam(GlobalConstant.ACCOUNT_ENTRY_TYPE);
		//调用service方法进行逻辑逻辑
		if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
			//账套模板
			if(DictCodeList.是否_是.getValue().equals(dto.getIsAccount())){
				jjPortfolioDto.setBookset_id_tmpl(InterfCommonConstant.BOOKSETID_NEW);
			}
			else {
				jjPortfolioDto.setBookset_id_tmpl(InterfCommonConstant.BOOKSETID_LAYERING_NOR_NEW);
			}
		}
		else{
			//账套模板
			if(DictCodeList.是否_是.getValue().equals(dto.getIsAccount())){
				jjPortfolioDto.setBookset_id_tmpl(InterfCommonConstant.BOOKSETID);
			}
			else {
				jjPortfolioDto.setBookset_id_tmpl(InterfCommonConstant.BOOKSETID_LAYERING_NOR);
			}
		}
		// add by weipeng on 20220408：会计引擎升级改造，若按照内部科目编码分录，则需要对应新会计引擎模板 end
    	 //账套创建日期
    	 jjPortfolioDto.setStart_date(dto.getVdate());
    	 //账套全名
    	 jjPortfolioDto.setBookset_fullname(dto.getProdName());
    	 //账套名
         if (dto.getProdAbbr() == null || "".equals(dto.getProdAbbr())) {
        	 jjPortfolioDto.setBookset_name(dto.getProdId() + "账套");
         }
         else {
        	 jjPortfolioDto.setBookset_name(dto.getProdAbbr());
         } 
         //账套节假日配置
         jjPortfolioDto.setCalendar_id(interfCommonService.getCalendarId(dto.getTradeMarket()));     
         return jjPortfolioDto;
    }	
	

}
