package com.joyintech.tams.invest.risk.creditmanagement.creditlinemanage.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

import com.joyintech.framework.util.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.dto.common.ExcelValidDto;
import com.joyintech.tams.systemtools.util.CommonUtil;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.risk.creditmanagement.ceorg.service.CeOrg002Service;
import com.joyintech.tams.invest.risk.creditmanagement.creditlinemanage.dto.CreditTagInfoDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 额度标签管理
 * @author chenlei1
 * @version 1.0 2019年02月15日
 * @see CeLineService
 * @since 1.0
 */
@Service
public class CeLineService extends CommonExportService<CreditTagInfoDto>
		implements BaseProcessService<CreditTagInfoDto> {

   
    @Autowired(required = false)
    private CeOrg002Service ceOrg002Service;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doComplete(CreditTagInfoDto entity, OperationType type) {
		 String action = StringUtil.EMPTY;
	        if (OperationType.DELETE.equals(type)) {
	            action = "delete";
	        }
	        else if (OperationType.NEW.equals(type)) {
	            action = "insert";
	            if(entity.getRiskRatio()==null){
	            	entity.setRiskRatio(new BigDecimal(100));        		
	        	}
                if(StringUtil.isBlank(entity.getIsPerpetualSec())){
                    entity.setIsPerpetualSec(DictCodeList.是否_否.getValue());
                }
                if(StringUtil.isBlank(entity.getIssueType())){
                    entity.setIssueType(DictCodeList.发行方式_公开发行.getValue());
                }
	        }
	        else {
	            action = "update";
	            if(entity.getRiskRatio()==null){
	            	entity.setRiskRatio(new BigDecimal(100));       		
	        	}
                if(StringUtil.isBlank(entity.getIsPerpetualSec())){
                    entity.setIsPerpetualSec(DictCodeList.是否_否.getValue());
                }
                if(StringUtil.isBlank(entity.getIssueType())){
                    entity.setIssueType(DictCodeList.发行方式_公开发行.getValue());
                }
	        }
	        restTemplate.postForObject(
	            ServiceApplication.RISK_SERVICE_NAME + "/CeLine002/" + action, entity,
	            Integer.class);

	}
	
	 /**
     * 客户代码联想文本框service层实现
      *
      * @param map 检索参数
      * @return SuggestDto
      */
	public Page<SelectOptionDto> getLimitLabel(Map<String, Object> map) {
        return restTemplate.postForPage(
            ServiceApplication.RISK_SERVICE_NAME + "/CeLine001/getLimitLabel",
            map, SelectOptionDto.class);
    }
    
	/**
	 * 画面查询 <功能详细描述>
	 * 
	 * @param parameters
	 *            parameters
	 * @return RskSupervisionIndicatorDto
	 * @see
	 * @since 1.0
	 */
	public Page<CreditTagInfoDto> selectForPage(Map<String, Object> parameters) {
		return restTemplate.postForPage(ServiceApplication.RISK_SERVICE_NAME + "/CeLine001/selectForPage",
				parameters, CreditTagInfoDto.class);
	}

	/**
	 * 导出
	 */
	@Override
	protected void buildExportParam(ExportParam exportParam) {
		exportParam.config("/com/joyintech/tams/invest/risk/creditmanagement/creditlinemanage/conf/export_conf.xml")
				.dataSource(DataSourceType.MASERT);
	}

	 /**
     * 设置预警与违规格式
     * {@inheritDoc}
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
		if(StringUtil.equals(propName,"vdate") || StringUtil.equals(propName,"mdate")){
			if(value instanceof Date){
				cell.setCellValue(DateUtil.format((Date)value));
			}else if (value instanceof Long){
				cell.setCellValue(DateUtil.format(new Date((Long) value)));
			}
		}
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<CreditTagInfoDto> selectPageData(Map<String, Object> selectParam) {
		try {
			Page<CreditTagInfoDto> pageList=selectForPage(selectParam);
			return pageList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	 /**
     * 根据授权额度编号查询额度信息
     * @param seqno 额度编号
     * @return 额度信息
     */
   public CreditTagInfoDto getBussinessEntityByIndId(String seqno) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("seqno", seqno);
        return restTemplate.postForObject(
            ServiceApplication.RISK_SERVICE_NAME + "/CeLine002/queryCeLineBySeqno",
            map, CreditTagInfoDto.class);
    }
	
   /**
    * 获取额度标签
    * @return List<SelectOptionDto>
    * @see
    * @since 1.0
    */
	public Page<SelectOptionDto> getCreditTagInfoList(Map<String, Object> parameters) {
		
		Page<SelectOptionDto> tempList = restTemplate.postForPage(
               ServiceApplication.RISK_SERVICE_NAME + "/CeLine002/getCreditTagInfoList",
               parameters, SelectOptionDto.class);
       return tempList;
   }
	
   public boolean checkTag(CreditTagInfoDto dto){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tagName", dto.getCreditTagName());
		map.put("tagId", dto.getCreditTagId());
		return restTemplate.postForObject(
				ServiceApplication.RISK_SERVICE_NAME + "/CeLine002/checkTag",map,
				Boolean.class);	
	}

	// add by yushilong 20200630 增加额度标签管理导入功能 start
	/**
	* excel导入功能
	*
	* @param flieName 文件名称
	* @return 检验list
	* @throws Exception 异常
	* @see
	* @since 1.0
	*/
	@Transactional(rollbackFor = Exception.class)
	public List<ExcelValidDto> importData(String flieName)
		throws Exception {
		// 检查文件名是否为空
		if (StringUtil.isNotEmpty(flieName)) {
            ExcelUtil excelUtil = ExcelUtil.build(FileNameUtil.getUploadFilePath(flieName),
                null);
            List<List<String>> dataList = excelUtil.readToList(0, 1, 0);
            if (null != dataList && dataList.size() > 0) {
                List<ExcelValidDto> checkResult = checkExcelData(dataList);
                if (checkResult.isEmpty()) {
                    // 将excel表格中的数据转化为dto
                    List<CreditTagInfoDto> equityList = converToBean(dataList);
                    // 保存，已存在则更新
                    dealAfterCheck(equityList);
                }
                return checkResult;
            }
		}
		return null;
	}

	/**
	 * 导入文件内容检查 <功能详细描述>
	 *
	 * @param dataList 导入数据
	 * @return 返回检查结果
	 * @throws ParseException 抛出异常
	 * @see
	 * @since 1.0
	 */
	private List<ExcelValidDto> checkExcelData(List<List<String>> dataList)
			throws ParseException {
		List<ExcelValidDto> checkResult = new ArrayList<>();
		if (dataList.size() >= 1) {
			for (int i = 0; i < dataList.size(); i++ ) {
				// 检查表内数据每行是否合法
				List<ExcelValidDto> validList = checkExcelCow(i + 2, dataList.get(i));
				checkResult.addAll(validList);
				// 检查表内数据是否有额度标签重复
				if (dataList.size() >= 2) {
					for (int k = 0; k < dataList.size(); k++ ) {
						if (dataList.get(k).get(1).equals(dataList.get(i).get(1)) && k != i) {
							checkResult.add(new ExcelValidDto(dataList.get(i).get(1),
									String.valueOf(i + 2), "额度标签存在重名"));
							break;
						}
					}
				}
			}
		}
		return checkResult;
	}

	/**
	 * 检查excel是否为空或者传入列是否正确
	 *
	 * @param i 第几行
	 * @param list 每一列解析后的List
	 * @return 检查结构
	 * @see
	 * @since 1.0
	 */
	@SuppressWarnings("unchecked")
    private List<ExcelValidDto> checkExcelCow(int i, List<String> list) {
		List<ExcelValidDto> excelValidDtolist = new ArrayList<ExcelValidDto>();
		// 列数为9
		if (null == list || list.size() != 8) {
			ExcelValidDto dto = new ExcelValidDto(StringUtil.EMPTY, String.valueOf(i), "数据列数错误");
			excelValidDtolist.add(dto);
		}
		// 验证导入额度分类是否为空
		if (StringUtil.isEmpty(list.get(0))) {
			ExcelValidDto dto = new ExcelValidDto(list.get(0), String.valueOf(i), "额度分类为空");
			excelValidDtolist.add(dto);
		}
		else {
		    List<DataDictItem> dictItemList = (List<DataDictItem>)SysDataUtil.getDataDictItem(DictCode.额度分类.getValue());
		    String reditType = list.get(0).trim().split(":")[0].trim();
		    boolean isMatching  = this.isMatchingDict(dictItemList, reditType);
		    
		    if(!isMatching) {
		     ExcelValidDto dto = new ExcelValidDto(list.get(0), String.valueOf(i), "输入有误,不存在该额度分类");
		            excelValidDtolist.add(dto);   
		    }
		      //只有额度分类是债券投资类额度的时候 是否永续债和发行方式必填
	        if( StringUtil.equals(reditType, DictCodeListRsk.额度分类_债券投资类额度.getValue())) {
	            if("02".equals(CacheUtil.getSysParam("IS_NB_CREDIT"))){   
	                  // 验证导入是否永续债是否为空
	                if (StringUtil.isEmpty(list.get(3))) {
	                    ExcelValidDto dto = new ExcelValidDto(list.get(3), String.valueOf(i), "是否永续债为空");
	                    excelValidDtolist.add(dto);
	                }
	                // 验证导入发行方式是否为空
	                if (StringUtil.isEmpty(list.get(4))) {
	                    ExcelValidDto dto = new ExcelValidDto(list.get(4), String.valueOf(i), "发行方式为空");
	                    excelValidDtolist.add(dto);
	                }
	            }
	        }
		}
		// 验证导入额度标签是否为空
		if (StringUtil.isEmpty(list.get(1))) {
			ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i), "额度标签为空");
			excelValidDtolist.add(dto);
		} 
		else {
		    //判断是不是和系统里已经有的额度标签重复
		    CreditTagInfoDto creditTagInfoDto = new CreditTagInfoDto();
		    creditTagInfoDto.setCreditTagName(list.get(1));
		    boolean issame= this.checkTag(creditTagInfoDto);
		    if(issame) {
		        ExcelValidDto dto = new ExcelValidDto(list.get(1), String.valueOf(i), "与已有的额度标签重复");
	            excelValidDtolist.add(dto); 
		    }
		}
		// 验证导入扣减规则是否为空
		if (StringUtil.isEmpty(list.get(2))) {
			ExcelValidDto dto = new ExcelValidDto(list.get(2), String.valueOf(i), "扣减规则为空");
			excelValidDtolist.add(dto);
		}
		else {
		    List<DataDictItem> dictItemList = (List<DataDictItem>)SysDataUtil.getDataDictItem(DictCode.扣减规则.getValue());
		    String deductRule = list.get(2).trim().split(":")[0].trim();
	        boolean isMatching  = this.isMatchingDict(dictItemList, deductRule);
            if(!isMatching) {
             ExcelValidDto dto = new ExcelValidDto(list.get(2), String.valueOf(i), "输入有误，不存在该扣减规则");
                    excelValidDtolist.add(dto);   
            } 
		}
        
        if (StringUtil.isNotEmpty(list.get(3))) {
            List<DataDictItem> dictItemList3 = (List<DataDictItem>)SysDataUtil.getDataDictItem(DictCode.是否.getValue());
            String isPerpetualSec = list.get(3).trim().split(":")[0].trim();
            boolean isMatching3  = this.isMatchingDict(dictItemList3, isPerpetualSec);
            if(!isMatching3) {
             ExcelValidDto dto = new ExcelValidDto(list.get(3), String.valueOf(i), "是否永续只能选择是或者否");
                    excelValidDtolist.add(dto);   
            } 
        }
        
        if (StringUtil.isNotEmpty(list.get(4))) {
            List<DataDictItem> dictItemList4 = (List<DataDictItem>)SysDataUtil.getDataDictItem(DictCode.发行方式.getValue());
            String issueType = list.get(4).trim().split(":")[0].trim();
            boolean isMatching4  = this.isMatchingDict(dictItemList4, issueType);
            if(!isMatching4) {
             ExcelValidDto dto = new ExcelValidDto(list.get(4), String.valueOf(i), "输入有误，不存在该发行方式");
             excelValidDtolist.add(dto);   
            } 
        }


		// 验证导入期限不为空时,期限单位也不能为空
		if (StringUtil.isNotEmpty(list.get(5)) && StringUtil.isNotEmpty(list.get(6))) {
		    //期限只能是正整数
		    if(!StringUtil.isNumeric(list.get(5))) {
                ExcelValidDto dto = new ExcelValidDto(list.get(5), String.valueOf(i), "期限不为正整数");
                excelValidDtolist.add(dto);
		    } 
		    else if(BigDecimal.ZERO.compareTo(new BigDecimal(list.get(5)))==0) {
		        ExcelValidDto dto = new ExcelValidDto(list.get(5), String.valueOf(i), "期限不为正整数");
                excelValidDtolist.add(dto); 
		    }
		    
		    List<DataDictItem> dictItemList = (List<DataDictItem>)SysDataUtil.getDataDictItem(DictCode.期限种类.getValue());
		    boolean isMatching = false;//是否和字典值匹配

            for(DataDictItem item:dictItemList) {
                if(StringUtil.equals(item.getItemValue(), list.get(6).trim())) {
                  list.set(6, item.getItemCode()) ;
                  isMatching = true;
                  break;
                }
            }
            if(!isMatching) {
                ExcelValidDto dto = new ExcelValidDto(list.get(6), String.valueOf(i), "期限单位只能为日月周年");
                excelValidDtolist.add(dto); 
            }
		    

		} 
		else if (StringUtil.isEmpty(list.get(5))  && StringUtil.isNotEmpty(list.get(6))) {
            ExcelValidDto dto = new ExcelValidDto(list.get(5), String.valueOf(i), "期限单位不为空但期限为空");
            excelValidDtolist.add(dto); 
        }
		else if (StringUtil.isEmpty(list.get(6)) && StringUtil.isNotEmpty(list.get(5))) {
            ExcelValidDto dto = new ExcelValidDto(list.get(6), String.valueOf(i), "期限不为空但期限单位为空");
            excelValidDtolist.add(dto); 
		} 
		
		if(StringUtil.isNotEmpty(list.get(7))) {
		    if(!NumberUtils.isNumber(list.get(7))) {
	            ExcelValidDto dto = new ExcelValidDto(list.get(7), String.valueOf(i), "风险系数不是数字");
	            excelValidDtolist.add(dto); 		        
		    }
		    else {
		        if(BigDecimal.ZERO.compareTo(new BigDecimal(list.get(7)))>0) {
		            ExcelValidDto dto = new ExcelValidDto(list.get(7), String.valueOf(i), "风险系数是负数");
	                excelValidDtolist.add(dto);   
		        }
		    }
		}
		return excelValidDtolist;
	}

	/**
	 * list转换为Bean
	 *
	 * @param dataList excel解析后的list
	 * @return List<CreditTagInfoDto>
	 * @throws ParseException 抛出异常
	 * @see
	 * @since 1.0
	 */
	private List<CreditTagInfoDto> converToBean(List<List<String>> dataList)
			throws ParseException {
		// 返回的list定义
		List<CreditTagInfoDto> returnInfoList = new ArrayList<CreditTagInfoDto>();
		CreditTagInfoDto creditTagInfoDto = null;
		

		for (int i = 0; i < dataList.size(); i++ ) {
			List<String> list = dataList.get(i);
			creditTagInfoDto = new CreditTagInfoDto();
			creditTagInfoDto.setCreditTagId(IDGenerator.generate());
			if (StringUtil.isNotEmpty(list.get(0))) {
				creditTagInfoDto.setCreditType(list.get(0).trim().split(":")[0].trim());
			}
			if (StringUtil.isNotEmpty(list.get(1))) {
				creditTagInfoDto.setCreditTagName(list.get(1).trim());
			}
			if (StringUtil.isNotEmpty(list.get(2))) {
				creditTagInfoDto.setDeductRule(list.get(2).trim().split(":")[0].trim());
			}
			if (StringUtil.isNotEmpty(list.get(3))) {
				creditTagInfoDto.setIsPerpetualSec(list.get(3).trim().split(":")[0].trim());
			}
			if (StringUtil.isNotEmpty(list.get(4))) {
				creditTagInfoDto.setIssueType(list.get(4).trim().split(":")[0].trim());
			}
//			if (StringUtil.isNotEmpty(list.get(5))) {
//				CreditTagInfoDto.setIsFx(list.get(5).trim().split(":")[0].trim());
//			}
//			if (StringUtil.isNotEmpty(list.get(6))) {
//				CreditTagInfoDto.setCurPair(list.get(6).trim().split(":")[0].trim());
//			}
			if (StringUtil.isNotEmpty(list.get(5))) {
				creditTagInfoDto.setTermValue(Long.valueOf(list.get(5).trim()));
			    if (StringUtil.isNotEmpty(list.get(6))) {
					creditTagInfoDto.setTermUnit(list.get(6).trim());
	            }
	            
			}
			if (StringUtil.isNotEmpty(list.get(7))) {
				creditTagInfoDto.setRiskRatio(new BigDecimal(list.get(7).trim()));
			}
			if (creditTagInfoDto.getRiskRatio() == null){
				creditTagInfoDto.setRiskRatio(new BigDecimal(100));
			}

			creditTagInfoDto.setCreateUser(CommonUtil.getUserName());
			creditTagInfoDto.setCreateDept(CommonUtil.getDeptCode());
			creditTagInfoDto.setUpdateUser(CommonUtil.getUserName());
			returnInfoList.add(creditTagInfoDto);
		}
		return returnInfoList;
	}

	/**
	 * check后的保存
	 *
	 * @param equityList 需要保存的数据
	 * @see
	 * @since 1.0
	 */
	public void dealAfterCheck(List<CreditTagInfoDto> equityList) {
		restTemplate.postForObject(
				ServiceApplication.RISK_SERVICE_NAME + "/CeLine002/mergeInfoList",
				equityList, Integer.class);
	}
	// add by yushilong 20200630 增加额度标签管理导入功能 end

	private boolean isMatchingDict(List<DataDictItem> list,String value) {
	    boolean isMatching = false;
	    for(DataDictItem item:list) {
	        if(StringUtil.equals(item.getItemCode(), value)){
	            isMatching = true;
	            break;
	        }
	    }
        return isMatching;
	    
	}

    public int checkTagCanDelete(CreditTagInfoDto dto) {
		return restTemplate.postForObject(
				ServiceApplication.RISK_SERVICE_NAME + "/CeLine002/checkTagCanDelete",
				dto, Integer.class);
    }
}
