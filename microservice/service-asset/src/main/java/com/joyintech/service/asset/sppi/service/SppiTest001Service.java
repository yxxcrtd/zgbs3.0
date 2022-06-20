/*
 * 文件名：SppiTest001Service
 * 版权：Copyright 2017-2020 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Jiaoxujin
 * 修改时间：2020-10-12
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.service.asset.sppi.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.asset.sppi.dao.SppiTest001Mapper;
import com.joyintech.service.asset.sppi.dto.SppiImportDto;
import com.joyintech.service.asset.sppi.dto.SppiTest001Dto;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.SppiImportDataMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.SppiImportData;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.dto.common.ExcelValidDto;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产标的SPPI测试结果查询服务
 *
 * @author Jiaoxujin
 * @version 1.0 2020-10-12
 */
@Service("backSppiTest001Service")
public class SppiTest001Service extends BaseService<SppiTest001Dto> {
    @Autowired
    private SppiTest001Mapper sppiTest001Mapper;
    @Autowired
    private SppiImportDataMapper sppiImportDataMapper;
    @Autowired(required=false)
    private IPlatformDate iPlatformDate;
    @Autowired
    private FinProductMapper finProductMapper;

    /**
     * 查询SPPI测试结果列表
     * @param page int
     * @param length int
     * @param pageParam Map&lt;String, Object&gt;
     * @return Page&lt;SppiTest001Dto&gt;
     */
    public Page<SppiTest001Dto> selectForPage(int page, int length, Map<String, Object> pageParam) {
        Page<SppiTest001Dto> pageObj = super.queryForPage(page, length, () -> sppiTest001Mapper.queryForPage(pageParam));
        return pageObj;
    }

    /**
     * 查询适用于SPPI测试的资产标的列表<br />
     * 仅能查询出债券类资产、项目类资产、计划类利率型资产
     * @param paramMap Map&lt;String, Object&gt;
     * @return List&lt;SelectOptionDto&gt;
     */
    public List<SelectOptionDto> querySppiAssetList(Map<String, Object> paramMap) {
        List<SelectOptionDto> dataList = sppiTest001Mapper.querySppiAssetList(paramMap);
        return dataList;
    }
    
    /**
     * 查询债券ID是否重复
     * @param list
     * @return
     */
    public List<SppiImportDto> getBondId(List<SppiImportDto> list){
    	return sppiTest001Mapper.getBondId(list);
    }
    
    
    /**
     * 导入文件
     * <p>
     * 解析excel文件并进行验证
     * </p>
     *
     * @param fileName 导入的文件名名
     * @return 导入结果
     */
    public List<ExcelValidDto> importData(List<List<String>> dataList,List<List<String>> tempList,String bankCode,String orgCode,String deptCode) {
        if (!StringUtil.isEmptyList(dataList)) {
            List<String> headList = new ArrayList<String>();
            headList.add("债券代码");
            headList.add("债券简称");
            headList.add("是否通过SPPI");
            headList.add("未通过原因");
            headList.add("使权不重大的购入价条件");
            headList.add("权的价值");
            headList.add("其他条件");
            List<ExcelValidDto> tempCheckResult = ExcelUtil.checkExcelTemp(tempList, headList);
            if (!StringUtil.isEmptyList(tempCheckResult)) {
                return tempCheckResult;
            }
            if (null != dataList) {
                List<SppiImportData> entities = new ArrayList<>(dataList.size());
                Map <String,String> box= new HashMap<String, String>();
                List<ExcelValidDto> checkResult = convertToEntity(dataList, entities,box);
                if(checkResult.isEmpty()) {
                    entities.forEach(item -> {
                        
                    	SppiImportData dto = sppiImportDataMapper.selectByPrimaryKey(item.getBondId());
                        if (dto != null){
                        	sppiImportDataMapper.updateByPrimaryKeySelective(item);
                        }
                        else{
                            //不存在插入新数据
                        	sppiImportDataMapper.insertSelective(item);
                        }
                        //更新产品测试状态
                        FinProduct finProduct = finProductMapper.selectByPrimaryKey(item.getBondId());
                        if(StringUtil.isNotEmpty(finProduct)) {
                        	finProduct.setSppiTestStatus(item.getIsSppiPass());
                        	finProductMapper.updateByPrimaryKeySelective(finProduct);
                        }
                    });

                }
                return checkResult;
            }
        }
        return null;
    }
    
    /**
     * excel数据转为java对象
     * <p> 将excel导入的数据，按行转为ProRegistCodeManage001Dto对象，同时验证各字段值</p>
     *
     * @param dataList excel导入的数据
     * @param entities 转化后的列表
     * @param box
     * @return 验证信息
     */
    private List<ExcelValidDto> convertToEntity(List<List<String>> dataList,
                                                List<SppiImportData> entities, Map<String, String> box) {
        List<ExcelValidDto> checkResult = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++ ) {
            List<String> list = dataList.get(i);
            if (null == list || list.size() != 7) {
                checkResult.add(new ExcelValidDto(StringUtil.EMPTY, String.valueOf(i),
                    this.getMessage("SPPI_COMMON_FILE_E_0001", "文件内容")));
                continue;
            }
            SppiImportData entity = new SppiImportData();
            // 验证消息
            List<String> message = new ArrayList<>();
            //系统中所有产品代码对应产品登记编码信息
            //Map<String,String> prodIdToRegCodeMap = proRegistCodeManage001Mapper.selectProdInfo().stream().collect(JoyinCollectors.toMap(x->x.get("PRODID"),x->x.get("REGCODE")));
            //债券代码代码
            if(!StringUtil.isNotEmpty(list.get(0))) {
            	message.add("债券代码不能为空");
            }else {
            	//债券代码必须存在对应债券
                Map<String, Object> param = new HashMap<>();
                param.put("finprodId", list.get(0));
                param.put("TYPE_TOUGUAN_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                param.put("TYPE_TOUGUAN_F32", DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
            	List<FinProduct> finProducts =  sppiTest001Mapper.getBondAssetInfo(param);
            	if(StringUtil.isEmptyList(finProducts)) {
            		message.add(list.get(0) + "不存在对应债券");
            	}else {
                    FinProduct finProduct = finProducts.get(0);
            		if(!StringUtil.equals(finProduct.getSppiTestStatus(), DictCodeListExt3.SPPI测试状态_未测试.getValue())) {
            			message.add(list.get(0) + "只有未测试状态才可以导入");
            		}
                    entity.setBondId(finProduct.getFinprodId());
            		if (StringUtil.isNotEmpty(list.get(1)) && (!StringUtil.equals(finProduct.getFinprodAbbr(),list.get(1).toString()))){
                        message.add(list.get(0) + "模板提供的数据债券简称："+list.get(1).toString()+"\t"+"与实际债券简称："+finProduct.getFinprodAbbr()+"\t"+"不一致！");
                    }
            	}
            }
            //债券简称
            if(!StringUtil.isNotEmpty(list.get(1))) {
            	message.add("债券简称不能为空");
            }else {
				entity.setBondAbbr(list.get(1));
			}
            //是否通过SPPI
            if(!StringUtil.isNotEmpty(list.get(2))) {
            	message.add("是否通过SPPI不能为空");
            }else {
            	if(StringUtil.equals("通过", list.get(2))) {
            		entity.setIsSppiPass(DictCodeListExt3.SPPI测试状态_测试通过.getValue());
            	}else if(StringUtil.equals("未通过", list.get(2))){
            		entity.setIsSppiPass(DictCodeListExt3.SPPI测试状态_测试不通过.getValue());
                    entity.setReason(list.get(3));
            		if(!StringUtil.isNotEmpty(list.get(3))) {
            			message.add("未通过原因不能为空");
            		}
            	}
			}

            //使权不重大的购入价条件	[type 数值]
            entity.setBidPriceCon(list.get(4));
            //mody  by  mefiaxin  20210929  start
//            if (StringUtil.isNotEmpty(list.get(4)) && !(CommonUtil.isFloatNumeric(list.get(4)))){
//                message.add(list.get(4)+"使权不重大的购入价条件的字段类型应为数值");
//            }
            //mody  by  mefiaxin  20210929  end
            //权的价值	[type 数值]
            entity.setCostValue(list.get(5));
            if (StringUtil.isNotEmpty(list.get(5)) && !(CommonUtil.isFloatNumeric(list.get(5)))){
                message.add(list.get(5)+"权的价值的字段类型应为数值");
            }
            entity.setOtherCondition(list.get(6).trim());
            entity.setImportDate(iPlatformDate.getSysDate());
  
            if (CollectionUtils.isNotEmpty(message)) {
                checkResult.add(new ExcelValidDto(entity.getBondId(), String.valueOf(i+1),
                    StringUtil.joinString(message, GlobalConstant.COMMA)));
            }
            else {
                entities.add(entity);
            }
        }
        return checkResult;
    }
    
    /**
     * 查询中债导入数据信息
     * @param finprodId
     * @return
     */
    public SppiImportDto selectByBondId(String finprodId) {
		return sppiTest001Mapper.selectByBondId(finprodId);
	}

}
