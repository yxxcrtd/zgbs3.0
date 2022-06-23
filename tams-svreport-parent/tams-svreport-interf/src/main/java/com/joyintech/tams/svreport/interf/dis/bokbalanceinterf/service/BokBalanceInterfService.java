package com.joyintech.tams.svreport.interf.dis.bokbalanceinterf.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.FileImpService;
import com.joyintech.tams.common.InterfDefine;
import com.joyintech.tams.common.InterfFile;
import com.joyintech.tams.common.base.entity.ChbInterfConfigDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.svreport.chb.util.ChbInterfUtil;
import com.joyintech.tams.svreport.interf.dis.bokbalanceinterf.dao.DisBokBalanceMapper;
import com.joyintech.tams.svreport.interf.dis.bokbalanceinterf.dto.BokBalance;
import com.joyintech.tams.svreport.interf.dis.common.DisInterfDecimalUtil;

/**
 * 科目余额表接口
 * @author dongxiangyang
 *
 */
@Service
public class BokBalanceInterfService extends FileImpService{

      @Autowired
      private DisBokBalanceMapper bokBalanceMapper;
	  /**
   * 长sql最大一次插入或者更新条数
   */
  @Value("${application.reportInsertLongCount}")
  public int reportInsertLongCount;
	/**
	 * 重写saveData方法
	 * 产品指标信息入库
	 * listData 费用交易list saveFlag 入库标识
	 */
	@Override
	public String saveData(List<Map<String, String>> listData, String saveFlag, String dateSource) {
		 if (StringUtil.isEmptyList(listData)) {
	            logger.info("saveData方法参数listData为空。");
	            return "success";
	        }
		    //全部删除
			bokBalanceMapper.delete(DateUtil.parse(listData.get(0).get("BALANCE_DATE")));
			List<BokBalance>  list=new ArrayList<>();
			//循环参数
			for(Map<String, String> map:listData){
				BokBalance dto=new BokBalance();
				try{
					dto.setBooksetId(map.get("BOOKSET_ID"));	
					dto.setBalanceDate(DateUtil.parse(map.get("BALANCE_DATE")));
					dto.setSubjectNo(map.get("SUBJECT_NO"));
					dto.setFsubjectNo(map.get("FSUBJECT_NO"));
					dto.setSubjectLevel(DisInterfDecimalUtil.str2Long(map.get("SUBJECT_LEVEL")));
					dto.setBalFlag(map.get("BAL_FLAG"));
					//dto.setBalCoFlag(map.get("BAL_CO_FLAG"));
					dto.setoCcy(map.get("O_CCY"));
					dto.setoAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("O_AMT")));
					dto.setoCAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("O_C_AMT")));
					dto.setoDAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("O_D_AMT")));
					dto.setoCoAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("O_CO_AMT")));
					dto.setoCoCAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("O_CO_C_AMT")));
					dto.setoCoDAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("O_CO_D_AMT")));
					/**add  by  dongxiangyang   2019/10/15   本位币塞入原币 start */
					dto.setbCcy(map.get("B_CCY"));
					dto.setbAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("B_AMT")));
					dto.setbCAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("B_C_AMT")));
					dto.setbCoAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("B_CO_AMT")));
					dto.setbCoCAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("B_CO_C_AMT")));
					dto.setbCoDAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("B_CO_D_AMT")));
					dto.setbDAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("B_D_AMT")));
					/**add  by  dongxiangyang   2019/10/15   本位币塞入原币 end */
					//dto.setTdyOAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_O_AMT")));

					dto.setTdyOCAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_O_C_AMT")));
					dto.setTdyODAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_O_D_AMT")));
//					dto.setTdyOCoAmt(new BigDecimal(map.get("TDY_O_CO_AMT")));
					dto.setTdyOCoCAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_O_CO_C_AMT")));
					dto.setTdyOCoDAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_O_CO_D_AMT")));
					/**add  by  dongxiangyang   2019/10/15   本位币塞入原币 start */
					dto.setTdyBAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_B_AMT")));
					dto.setTdyBCAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_B_C_AMT")));
					dto.setTdyBDAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_B_D_AMT")));
					dto.setTdyBCoAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_B_CO_AMT")));
					dto.setTdyBCoCAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_B_CO_C_AMT")));
					dto.setTdyBCoDAmt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_B_CO_D_AMT")));
					/**add  by  dongxiangyang   2019/10/15   本位币塞入原币 end */
					dto.setIsLeaf(map.get("IS_LEAF"));
//					dto.setNumAmt(new BigDecimal(map.get("NUM_AMT")));
				    dto.setCreateUser("interf");
				    dto.setCreateDept(null);
				    dto.setUpdateUser("interf");
				    
//					dto.setTdyAmtFlag(map.get("TDY_AMT_FLAG"));
//					dto.setTdyCoFlag(map.get("TDY_CO_FLAG"));
					dto.setTdyPurODUlamt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_PUR_O_D_ULAMT")));
					dto.setTdyRedODUlamt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_RED_O_D_ULAMT")));
			        dto.setTdyPurOCUlamt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_PUR_O_C_ULAMT")));
			        dto.setTdyRedOCUlamt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_RED_O_C_ULAMT")));
					/**add  by  dongxiangyang   2019/10/15   本位币塞入原币 start */
			        dto.setTdyPurBDUlamt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_PUR_B_D_ULAMT")));
			        dto.setTdyRedBDUlamt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_RED_B_D_ULAMT")));
			        dto.setTdyPurBCUlamt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_PUR_B_C_ULAMT")));
			        dto.setTdyRedBCUlamt(DisInterfDecimalUtil.str2BigDecimal(map.get("TDY_RED_B_C_ULAMT")));
					/**add  by  dongxiangyang   2019/10/15   本位币塞入原币 end */
			        // dto.setInputType(null);
				    //dto.setInputUser(null);
			        //dto.setAdapterId("0");

				}
				catch(Exception e){
					logger.error("解析数据错误：",e.getMessage(),e);
	           		return  "fail";
				}
			    try {
		            if(StringUtil.isNotEmpty(map.get("CREATE_TIME"))){
		                dto.setCreateTime(DateUtil.parseDate(map.get("CREATE_TIME"),new String[] {DateUtil.DEFAULT_DATETIME_FORMAT_ODS}));
		            }else{
		                dto.setCreateTime(new Date());
		            }
		            if(StringUtil.isNotEmpty(map.get("UPDATE_TIME"))){
		                dto.setUpdateTime(DateUtil.parseDate(map.get("UPDATE_TIME"),new String[] {DateUtil.DEFAULT_DATETIME_FORMAT_ODS}));
		            }else{
		                dto.setUpdateTime(new Date());
		            }
		        } catch (ParseException e) {
		            logger.error("transMap2Dto方法转换CreateTime或UpdateTime异常");
		            logger.error(e.getMessage(),e);
		            return  "fail";
		        }
		        if (list.size() < reportInsertLongCount) {
					list.add(dto);
				} else if (list.size()  >= reportInsertLongCount) {
					bokBalanceMapper.insert(list);
					list.clear();
					list.add(dto);
				}
			}// 如果循环结束，小于最大值
			if (CollectionUtils.isNotEmpty(list)) {	
				bokBalanceMapper .insert(list);
			}
		
		return "success";
	}


	@Override
	public String importDB(List<Map<String, String>> listData, InterfFile interfFile, String date) {
        if(CollectionUtils.isEmpty(listData)){
            return "success";
        }
        // 数据检查
        checkData(listData, interfFile.getFilename(), InterfDefine.科目余额数据.getCode(),
            InterfDefine.科目余额数据.getCode(), null,
            new String[]{"BOOKSET_ID","SUBJECT_NO","BALANCE_DATE"});
        ChbInterfConfigDto interfConfig = ChbInterfUtil.queryInterfConfigByInterfid(
            InterfDefine.科目余额数据.getCode());
        if (interfConfig == null) {
            logger.info("CHB_INTERF_CONFIG表中无配置项！");
            return "fail";
        }
        String saveFlag = interfConfig.getContentType();
        return saveData(listData, saveFlag, interfFile.getDataSource());
    }

}
