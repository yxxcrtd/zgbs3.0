/*
 * 文件名：BatchCommonService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangzhiang
 * 修改时间：2018年7月13日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.common.service;

import com.joyintech.framework.util.EncryptUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokBookdetailCurrent;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.BokSubject;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.systemtools.service.SystemToolService;
import com.joyintech.tams.valuation.common.constants.SubjectConfigCode;
import com.joyintech.tams.valuation.common.dao.BookAccountCommonMapper;
import com.joyintech.tams.valuation.common.dto.SubjectMappingConfigDto;
import com.joyintech.tams.valuation.common.utils.FelAnalysisUtil;
import com.joyintech.tams.valuation.common.utils.ValStringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author wangzhiang
 * @version 1.0 2018年7月13日
 * @see BookAccountCommonService
 * @since 1.0
 */
@Service
public class BookAccountCommonService {
    
    @Autowired
    private BookAccountCommonMapper bookAccountCommonMapper;
    
    /**
     * 1
     */
    private static final String I1 = "1";

    /**
     * 2
     */
    private static final String I2 = "2";

    /**
     * 3
     */
    private static final String I3 = "3";

    /**
     * 4
     */
    private static final String I4 = "4";
    
    /**
     * 5
     */
    private static final String I5 = "5";

    /**
     * 6
     */
    private static final String I6 = "6";
    
    /**
     * 系统工具类
     */
    @Autowired
    private SystemToolService systemToolService;
    
    /**
     * 生成MD5账务明细代码
     *
     * @param valBokDetailId 账务明细代码生成参数
     * @param busiDetailType 类型
     * @return String 账务明细代码
     * @see
     * @since 1.0
     */
    public String genMd5BokDetailId(BokDetailId valBokDetailId) {
        String tempBokDetailId = null;
        // 判断分支: 除了债券，均为业务明细代码 + 业务明细类型
        if (DictCodeListExt1.金融产品类型_债券.getValue().equals(valBokDetailId.getBusiDetailType())) {
            // 债券
            // 账务明细代码 = 业务明细代码 + 业务明细类型 + 投资目的 + 证券管理户代码
            tempBokDetailId = I1 + ValStringUtil.getNotNullStr(valBokDetailId.getBusiId()) + I2
                              + ValStringUtil.getNotNullStr(valBokDetailId.getBusiDetailType());
            // 融入债券走的也是债券逻辑，不需要3,4加密，其他债券投资目的一定不为空
            if(StringUtil.isNotEmpty(valBokDetailId.getInvAim())){
                tempBokDetailId += I3 + ValStringUtil.getNotNullStr(valBokDetailId.getInvAim()) + I4
                + ValStringUtil.getNotNullStr(valBokDetailId.getSecManageAcctId());
            }

        }
        /**add by wujie on 20201015 start remark:计划类收益型资产改造**/
        else if (DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(valBokDetailId.getBusiDetailType())) {
            tempBokDetailId = I1 + ValStringUtil.getNotNullStr(valBokDetailId.getBusiId()) + I2
                              + ValStringUtil.getNotNullStr(valBokDetailId.getBusiDetailType());
        	if(StringUtil.isNotEmpty(valBokDetailId.getInvAim())){
        		 tempBokDetailId += I3 + ValStringUtil.getNotNullStr(valBokDetailId.getInvAim());
        	}
        }
        /**add by wujie on 20201015 end**/
        else {
            // 账务明细代码 = 业务明细代码 + 业务明细类型  
            tempBokDetailId = I1 + ValStringUtil.getNotNullStr(valBokDetailId.getBusiId()) + I2
                              + ValStringUtil.getNotNullStr(valBokDetailId.getBusiDetailType());
            // (如果传了分支序号，则需要增加分支序号)
            tempBokDetailId = addBokDetailIdParam(tempBokDetailId, I5, valBokDetailId.getBranch());

            // IF 输入.账务明细代码Dto.币种 IS NOT NULL
            if (StringUtils.isNotEmpty(valBokDetailId.getCcy())) {
                // 变量.账务明细代码 += 6 + 输入.账务明细代码Dto.币种
                if(DictCodeListExt1.金融产品类型_资金账户.getValue().equals(valBokDetailId.getBusiDetailType())
                		||systemToolService.getDataDictItem(DictCode.资金账户类型.getValue(), valBokDetailId.getBusiDetailType()) != null){
                	tempBokDetailId += I6 + valBokDetailId.getCcy();
                }
                
            }

        }
        // MD5转换
        String md5BokDetailId = EncryptUtil.getInstance().md5(tempBokDetailId);
        valBokDetailId.setBokDetailId(md5BokDetailId);
        return md5BokDetailId;
    }
    
    // 增加账务明细代码组装内容
    private String addBokDetailIdParam(String str, String index, Object value) {
        if (value == null) {
            return str;
        }
        else if (value instanceof Long && ((Long)value).longValue() != 0) {
            // Long型时，如果为0 也不增加组装内容
            str = str + index + String.valueOf(value);
        }
        
        return str;
    }
    /**
     * 凭证编码生成
     * @param bookDetail
     * @see
     * @since 1.0
     */
    public String genBookId(BokBookdetailCurrent bookDetail) {
        String bookId = null;
        // modified by wangzhiang 20180807 增加 start
        if (DictCodeListTableId.会计场景代码_产品_已实现损益结转.getValue().equals(bookDetail.getTableId())
            || DictCodeListTableId.会计场景代码_产品_未实现损益结转.getValue().equals(bookDetail.getTableId())
            || DictCodeListTableId.会计场景代码_产品_未分配利润结转.getValue().equals(bookDetail.getTableId())
            || DictCodeListTableId.会计场景代码_产品_损益平准金结转.getValue().equals(bookDetail.getTableId())) {
            // 损益结转等特殊类：场景代码 + 发生日 + 冲销标识
            bookId = bookDetail.getBooksetId() + bookDetail.getTableId()
                     + bookDetail.getHappenDate() + bookDetail.getOffsetFlag();
        }
        else if (!StringUtils.isEmpty(bookDetail.getTradeId())) {
            // 交易类：场景代码 + 交易编号 + 冲销标识
            bookId = bookDetail.getBooksetId() + bookDetail.getTableId() + bookDetail.getTradeId()
                     + bookDetail.getOffsetFlag();
        }
        else {
            // 计提类：场景代码 + 发生日 + 账务明细代码 + 冲销标识
            bookId = bookDetail.getBooksetId() + bookDetail.getTableId()
                     + bookDetail.getHappenDate() + bookDetail.getBokDetailId()
                     + bookDetail.getOffsetFlag();
        }
        // modified by wangzhiang 20180807 end
        bookId = EncryptUtil.getInstance().md5(bookId);
        bookDetail.setBookId(bookId);
        return bookId;
    }
    

    /**
     * 组装账务明细(资金帐户/现金类)
     *
     * @param busiId
     * @param bokDetailId
     * @return
     * @see
     * @since 1.0
     */
    public BokDetailId getBokDetailId(String busiId, String bokDetailId,
                                            String busiDetailType) {
        BokDetailId valBokDetailId = new BokDetailId();
        valBokDetailId.setBusiId(busiId);
        valBokDetailId.setBusiDetailType(busiDetailType);
        if (bokDetailId == null) {
            valBokDetailId.setBokDetailId(genMd5BokDetailId(valBokDetailId));
        }
        else {
            valBokDetailId.setBokDetailId(bokDetailId);
        }
        return valBokDetailId;
    }
    
    /**
     * 获取最大凭证序号
     *
     * @param vouchYear 年份
     * @param vouchMonth 月份
     * @param booksetId 账套代码
     * @return 最大凭证序号
     * @see
     * @since 1.0
     */
    public String getMaxBookAccountNum(String vouchYear, String vouchMonth, String booksetId) {
        return bookAccountCommonMapper.getMaxBookAccountNum(vouchYear, vouchMonth, booksetId);
    }
    
    /**
     * 凭证序号补零
     *
     * @param num 凭证序号
     * @param length 长度
     * @return 凭证序号
     * @see
     * @since 1.0
     */
    public String padBookAccountNum(String num, int length) {
        return StringUtil.leftPad(num, length, "0");
    }
    
    /**
     * 
     * 更新历史业务凭证编号
     * 
     * @param vouchYear
     * @param vouchMonth
     * @see
     * @since 1.0
     */
    public void updateHistory(String vouchYear, String vouchMonth) {
        bookAccountCommonMapper.updateHistory(vouchYear, vouchMonth);
    }
    /**
     * 查询账务明细代码
     */
	public List<BokDetailId> queryBokDetailIdList(Map<String, Object> map) {
		return bookAccountCommonMapper.queryBokDetailIdList(map);
	}

    /**
     * 根据辅助核算项匹配查找对应科目
     * @param listBokSubject 科目信息列表
     * @param subjectMappingConfigDto 辅助核算项
     * @param ccy 币种
     * @return
     */
    public BokSubject getSubjectByInnerCode(List<BokSubject> listBokSubject, SubjectMappingConfigDto subjectMappingConfigDto){
        BokSubject bokSubject = null;
        if(CollectionUtils.isEmpty(listBokSubject)){
            return bokSubject;
        }
        // 定义 变量.资产辅助核算项 封装辅助核算项为map对象
        Map<String, Object> params = initMappingParams(subjectMappingConfigDto);
        // 定义 变量.默认辅助核算项 封装辅助核算项为map对象
        Map<String, Object> paramsDefualt = initMappingParams(subjectMappingConfigDto);
        // 增加辅助核算项默认（DEFAULT) = 是(Y)
        paramsDefualt.put(SubjectConfigCode.默认.getValue(), DictCodeList.是否_是.getValue());
        // 定义 变量.默认科目
        BokSubject bokSubjectDefault = null;
        // 若只有一条记录，则直接取值
        if(listBokSubject.size() == 1){
            bokSubject = listBokSubject.get(0);
        }
        else{
            for(BokSubject item : listBokSubject){
                // 根据变量.资产辅助核算
                // 项通过辅助核算项表达式匹配
                //  科目内部科目编码不为空 且 科目内部科目编码匹配辅助核算项
                if (StringUtil.isNotEmpty(item.getSubjectHandFelCode()) &&
                    FelAnalysisUtil.booleanValue(item.getSubjectHandFelCode(), params)
                ) {
                    bokSubject = item;
                    break;
                }
                // 比较科目与默认辅助核算项，获取默认DEFAULT=='Y'的科目,即默认科目
                if (StringUtil.isNotEmpty(item.getSubjectHandFelCode()) &&
                    FelAnalysisUtil.booleanValue(item.getSubjectHandFelCode(), paramsDefualt)
                ) {
                    // 默认科目 = 匹配科目
                    bokSubjectDefault = item;
                }
            }
        }
        // 若科目信息为空，则说明未匹配对应科目信息，取默认科目
        if(bokSubject ==null){
            bokSubject = bokSubjectDefault;
        }
        return bokSubject;
    }

    /**
     * 初始化
     * @param mappingConfig
     * @return
     */
    public Map<String,Object> initMappingParams(SubjectMappingConfigDto mappingConfig){
        Map<String, Object> params = new HashMap<>();
        if(mappingConfig == null){
            return params;
        }
        params.put(SubjectConfigCode.证券品种.getValue(), mappingConfig.getSecType());
        params.put(SubjectConfigCode.债券类型.getValue(), mappingConfig.getAssetType());
        params.put(SubjectConfigCode.存款类资产类型.getValue(), mappingConfig.getAssetType());
        params.put(SubjectConfigCode.基金类型.getValue(), mappingConfig.getAssetType());
        params.put(SubjectConfigCode.回购标的类型.getValue(), mappingConfig.getAssetType());
        // add by weipeng on 20220408：计划类净值型、货币型、利率型以及项目类资产全部默认场外
        if(StringUtil.equals(DictCodeListExt1.证券品种_计划类资产_净值型.getValue(), mappingConfig.getSecType())
           || StringUtil.equals(DictCodeListExt1.证券品种_计划类资产_货币型.getValue(), mappingConfig.getSecType())
           || StringUtil.equals(DictCodeListExt1.证券品种_计划类资产_收益型.getValue(), mappingConfig.getSecType())
           || StringUtil.equals(DictCodeListExt1.证券品种_项目类资产.getValue(), mappingConfig.getSecType())){
            params.put(SubjectConfigCode.估值交易市场.getValue(), DictCodeListExt1.估值交易市场_场外.getValue());
        }
        else{
            params.put(SubjectConfigCode.估值交易市场.getValue(), mappingConfig.getTradeMarket());
        }
        params.put(SubjectConfigCode.交易市场.getValue(), mappingConfig.getTradeMarket());
        //        params.put(SubjectConfigCode.交易对手.getValue(), mappingConfig.getTradeMarket());
        params.put(SubjectConfigCode.账户类型.getValue(), mappingConfig.getAccountType());
        params.put(SubjectConfigCode.费用类型.getValue(), mappingConfig.getFeeType());
        params.put(SubjectConfigCode.税类型.getValue(), mappingConfig.getTaxType());
        if(StringUtil.isNotEmpty(mappingConfig.getFinanceOrgName())){
            params.put(SubjectConfigCode.金融机构简称.getValue(), FinanceOrgEnum.getEnumByName(mappingConfig.getFinanceOrgName()).getValue());
        }
        params.put(SubjectConfigCode.回购类型.getValue(), mappingConfig.getTargetBackType());
        params.put(SubjectConfigCode.现金流二级类型.getValue(), mappingConfig.getCashType2());
        params.put(SubjectConfigCode.登记托管机构.getValue(), mappingConfig.getRegistOrg());
        params.put(SubjectConfigCode.账户细分类.getValue(), mappingConfig.getAcctDetailType());
        params.put(SubjectConfigCode.投资目的.getValue(), mappingConfig.getInvAim());
        return params;
    }
}
