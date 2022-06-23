package com.joyintech.tams.invest.batch.feeprovisionquarterbegin.service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.common.base.dao.FinTradeProductMapper;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.common.base.entity.TrdAccountmfConfigDto;
import com.joyintech.tams.common.base.entity.TrdAccountmfConfigdetailDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.batch.feeprovisionquarterbegin.dao.FeeProvisionQuarterBeginMapper;
import com.joyintech.tams.invest.batch.feeprovisionquarterbegin.dto.FinTradeProductAddDto;

/**
 * 费用预提每季度初批处理 Service层
 * @author hpp
 *
 */
@Service
public class FeeProvisionQuarterBeginService {

	@Autowired
	private FeeProvisionQuarterBeginMapper feeProvisionQuarterBeginMapper;

//	/**
//     * 接口共通
//     */
//    @Autowired
//    private InterfCommonService interfCommonService;
//    /**
//     * 账务明细代码
//     */
//    @Autowired
//    private ValBokDetailIdBatchMapper valBokDetailIdBatchMapper;
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;
    @Autowired
    private FinProductTypeMapper finProductTypeMapper;
    
    /**
     * 组合持仓自动生成
     *
     * @param batchDate 执行日
     * @param productIdList
     * @throws ParseException 
     * @see
     * @since 1.0
     */
    public void autoGeneration(Date batchDate, List<String> productIdList) throws ParseException {
//
//    	//1 获取需要生成预提交易类标的的配置信息
//    	List<TrdAccountmfConfigDto> taclists = feeProvisionQuarterBeginMapper.selectTACInfoByDate(DateUtil.format(batchDate, "yyyy-MM-dd"));
//
//    	//系统平台日所在季度末日期
//    	Date quarterEndDate = getLastDayOfQuarter(batchDate);
//    	Date quarterStartDate = DateUtil.getFirstDayOfQuarter(batchDate);
////    	Date quarterEndDate = DateUtil.getLastDay(DateUtil.addMonthDate(DateUtil.getFirstDayOfQuarter(batchDate), 2));
//
//        // 定义 变量.入库账务明细代码List
//        List<BokDetailId> listBokDetailId = Lists.newArrayList();
//
//    	//2 循环步骤1的结果，生成交易类金融产品相关信息
//    	for(TrdAccountmfConfigDto dto:taclists){
//    		//if 系统平台日=计提开始日 || 系统平台日 = 季度第一天
//    		if(DateUtil.compareDate(batchDate, dto.getPositionStartDate()) == 0
//    				|| DateUtil.compareDate(batchDate, quarterStartDate) == 0){
//    			//证券账户
//    			String secAcctId = dto.getSecAcctId();
//    			//登记托管机构
//    			String registOrg = dto.getRegistOrg();
//    			//费用类型
//    			String feeType = dto.getFeeType();
//
//    			//2.1 根据主表流水号获取明细信息
//    			Map<String, Object> separam=new HashMap<String, Object>();
//                separam.put("pSeqNo",dto.getSeqNo());
//                separam.put("productIdList",productIdList);
//    			List<TrdAccountmfConfigdetailDto> tacdetails =
//    					feeProvisionQuarterBeginMapper.selectTACDetailInfoByPSeqNo(separam);
//
//    	    	//2.2 循环2.1查询结果处理生成交易类金融产品
//    			for(TrdAccountmfConfigdetailDto detail:tacdetails){
//    				// 定义 变量.交易类金融产品Entity
//    				FinTradeProduct finTradeProduct = new FinTradeProduct();
//    		        // 金融产品代码 = 自动生成（证券账户+产品代码+系统平台日）
//    		        finTradeProduct.setFinprodId(secAcctId+detail.getBooksetId()+DateUtil.format(batchDate, "yyyyMMdd"));
//    		        // 金融产品类型 =  数据字典.金融产品类型.预提费用
//    		        finTradeProduct.setFinprodType(DictCodeListExt1.金融产品类型_预提费用.getValue());
//    		        finTradeProduct.setFinprodType2(DictCodeListExt1.金融产品类型_预提费用.getValue());
//    		        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
//    		        finTradeProduct.setBranch(0L);
//    		        // 名义本金 = 产品每季费用金额
//    		        finTradeProduct.setPrin(detail.getFeeAmtDetail());
//    		        // 名义币种 = 数据字典.币种.人民币
//    		        finTradeProduct.setCcy(DictCodeList.币种_人民币_CNY.getValue());
//    		        // 起息日 = 系统平台日
//    		        finTradeProduct.setVdate(batchDate);
//    		        // 到期日 = 变量.季末日期>"规则计提结束日"-->“规则计提结束日”,否则取变量.季末日期+1
//    		        Date mDate = DateUtil.addDay(quarterEndDate, 1);
//    		        if(DateUtil.compareDate(quarterEndDate, dto.getPositionEndDate()) > 0){
//    		        	mDate = DateUtil.addDay(dto.getPositionEndDate(), 1);
//    		        }
//    		        finTradeProduct.setMdate(mDate);
//
//    		        finTradeProductMapper.deleteByPrimaryKey(finTradeProduct);
////    		        finTradeProductMapper.insertSelective(finTradeProduct);
//    		        feeProvisionQuarterBeginMapper.insertFTPSelective(finTradeProduct);
//
//    		        // 定义 变量.交易类金融产品附属Entity
//    		        FinTradeProductAddDto finTradeProductAddDto = new FinTradeProductAddDto();
//    		        // 金融产品代码 = 变量.交易类金融产品.金融产品代码
//    		        finTradeProductAddDto.setFinprodId(finTradeProduct.getFinprodId());
//    		        // 金融产品类型 = 变量.交易类金融产品.金融产品类型
//    		        finTradeProductAddDto.setFinprodType2(finTradeProduct.getFinprodType2());
//    		        finTradeProductAddDto.setFinprodType(finTradeProduct.getFinprodType2());
//    		        // 分支序号 = 变量.交易类金融产品.分支序号
//    		        finTradeProductAddDto.setBranch(finTradeProduct.getBranch());
//    		        // 登记托管机构
//    		        finTradeProductAddDto.setRegistOrg(registOrg);
//    		        // 证券账户 = 变量.证券账户
//    		        finTradeProductAddDto.setSecAcctId(secAcctId);
//
//    		        feeProvisionQuarterBeginMapper.deleteFTPAByPrimaryKey(finTradeProductAddDto);
//    		        feeProvisionQuarterBeginMapper.insertFTPA(finTradeProductAddDto);
//
//    		        // 定义 变量.金融产品分类Entity
//    		        FinProductType finProductType = new FinProductType();
//    		        // 金融产品代码 = 变量.交易类金融产品.金融产品代码
//    		        finProductType.setFinprodId(finTradeProduct.getFinprodId());
//    		        // 分支序号 = 变量.交易类金融产品.分支序号
//    		        finProductType.setBranch(finTradeProduct.getBranch());
//    		        // 分类1 = 变量.费用类型
//    		        finProductType.setType1(feeType);
//
//    		        finProductTypeMapper.deleteByPrimaryKey(finProductType);
//    		        finProductTypeMapper.insertSelective(finProductType);
//
//    				//定义 变量.账务明细代码
//    		        //定义 变量.账务明细代码Entity
//                    BokDetailId bokDetailId = new BokDetailId();
//                    // 账套代码
//                    //根据产品代码查询资产类型对应的账套代码，净值化产品带前缀“F16_”,收益型产品不带
//                    String booksetId = feeProvisionQuarterBeginMapper.getBooksetIdByProdId(detail.getBooksetId());
//                    bokDetailId.setBooksetId(booksetId);
//                    // 业务明细代码 = 变量.交易类金融产品.金融产品代码
//                    bokDetailId.setBusiId(finTradeProduct.getFinprodId());
//                    // 业务明细类型 = 变量.交易类金融产品.金融产品类型
//                    bokDetailId.setBusiDetailType(finTradeProduct.getFinprodType2());
//                    // 变量.账务明细代码 = interfCommonService.genMd5BokDetailId(变量.账务明细代码Dto )
//                    String strBokDetailId = interfCommonService.genMd5BokDetailId(bokDetailId);
//                    // 变量.账务明细代码Dto.账务明细代码 = 变量.账务明细代码
//                    bokDetailId.setBokDetailId(strBokDetailId);
//
//                    // 交易编号 = 变量.交易类金融产品.金融产品代码
//                    bokDetailId.setTradeId(finTradeProduct.getFinprodId());
//                    // 四级科目尾号 = 变量.费用类型
//                    bokDetailId.setDetailSubjectNoTail(feeType);
//                    // 四级科目尾号名称 = 变量.费用类型对应名称
//                    bokDetailId.setDetailSubnameTail(SysDataUtil.getNameFromDataDict(DictCode.费用类型, feeType));
//                    // 分支序号 = 变量.交易类金融产品.分支序号
//                    bokDetailId.setBranch(finTradeProduct.getBranch());
//                    // 币种 = 数据字典.币种.人民币
//                    bokDetailId.setCcy(DictCodeList.币种_人民币_CNY.getValue());
//
//                    listBokDetailId.add(bokDetailId);
//    			}
//    		}
//    	}
//
//    	// 变量.入库账务明细代码List 账务明细代码表 BOK_DETAIL_ID 先根据账套代码、账务明细代码删除，然后入库
//        if (CollectionUtils.isNotEmpty(listBokDetailId)) {
//            valBokDetailIdBatchMapper.deleteBatch(listBokDetailId);
//            valBokDetailIdBatchMapper.insertBatch(listBokDetailId);
//        }
    	
    }
    
    /**
     * 返回指定日期的季的最后一天
     * Calendar 的 month 从 0 开始，也就是全年 12 个月由 0 ~ 11 进行表示。
     * 而 Calendar.DAY_OF_WEEK 定义和值如下：
	 *  Calendar.SUNDAY = 1  Calendar.MONDAY = 2  Calendar.TUESDAY = 3
	 *  Calendar.WEDNESDAY = 4  Calendar.THURSDAY = 5  Calendar.FRIDAY = 6  Calendar.SATURDAY = 7
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date getLastDayOfQuarter(Date date)
            throws ParseException {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int quarterOfYear = calendar.get(Calendar.MONTH) / 3 + 1;
            int month = 0;
            switch (quarterOfYear) {
                case 1:
                    month = 2;
                    break;
                case 2:
                    month = 5;
                    break;
                case 3:
                    month = 8;
                    break;
                case 4:
                    month = 11;
                    break;
                default:
                    month = calendar.get(Calendar.MONTH);
            }
            calendar.set(calendar.get(Calendar.YEAR), month, 1);
            return DateUtil.getLastDay(calendar.getTime());
        }
    
}
