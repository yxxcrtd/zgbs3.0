/*
 * 文件名：MstTradeOpponent002Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年8月23日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.basicdata.comtradeopponent.service;

import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.MstCounterPartyMapper;
import com.joyintech.tams.common.base.entity.MstCounterAcctKey;
import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.tams.valuation.basicdata.comtradeopponent.dto.ComTradeOpponentDto;
import com.joyintech.tams.valuation.basicdata.comtradeopponent.dto.FinCashDto;
import com.joyintech.tams.valuation.basicdata.comtradeopponent.dto.StlCapacctDto;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 交易对手管理Service
 * 
 * @author fanshuanghu
 * @version 1.0 2018年8月23日
 * @see ComTradeOpponent002Service
 * @since 1.0
 */
@Service
public class ComTradeOpponent002Service extends CommonExportService<ComTradeOpponentDto> implements BaseProcessService<ComTradeOpponentDto> {

    /**
     * 现金流mapper
     */
    @Autowired
    private MstCounterPartyMapper mstCounterPartyMapper;
    /**
     * 附件上传类
     */
    @Autowired
    private FileAttachmentProcess fileAttachmentProcess;
    

    @Override
    public void doComplete(ComTradeOpponentDto entity, OperationType type) {
//        if (OperationType.NEW.equals(type)) {
//            // 调用父类的主键插入方法
//            entity.setFinprodType(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
//            insertAount(entity);
//            if ("03".equals(entity.getCounterType())) {
//                List<FinCashDto> finDto = entity.getSelectFinCash();
//                mstTradeOpponent002Mapper.insertFinProduct(entity);
//                mstTradeOpponent002Mapper.insertFinProductType(entity);
//                mstTradeOpponent002Mapper.insertRepAssetMngPlan(entity);
//                for (FinCashDto dto : finDto) {
//                    dto.setFinprodId(entity.getFinprodId());
//                    dto.setFinprodType(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
//                    dto.setCashType1(DictCodeListExt1.现金流一级类型_费用.getValue());
//                    Map<String,Object> feeMap = new HashMap<String,Object>();
//                    feeMap.put("feeId", dto.getFeeId());
//                    dto.setCashType2(mstTradeOpponent002Mapper.selectCashType2(feeMap));
//                    dto.setCashId(md5(entity.getFinprodId()+
//                        DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue()+dto.getCashType2()));
//                    mstTradeOpponent002Mapper.insertFinCashCalcRule(dto);
//                    mstTradeOpponent002Mapper.insertFinCashInfo(dto);
//                    mstTradeOpponent002Mapper.insertFinRateInfo(dto);
//                }
//            }
//        }
//        else {
//            entity.setLinkId(entity.getFinprodId());
//            mstTradeOpponent001Mapper.deleteCounterAcct(entity);
//            mstTradeOpponent001Mapper.deleteMstCounterParty(entity);
//            if ("03".equals(entity.getCounterType())) {
//                mstTradeOpponent001Mapper.deleteFinCashCalcRule(entity);
//                mstTradeOpponent001Mapper.deleteFinCashInfo(entity);
//                mstTradeOpponent001Mapper.deleteFinCashPlan(entity);
//                mstTradeOpponent001Mapper.deleteFinProduct(entity);
//                mstTradeOpponent001Mapper.deleteFinProductType(entity);
//                mstTradeOpponent001Mapper.deleteRepAssetMngPlan(entity);
//            }
//            entity.setFinprodType(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
//            mstCounterPartyMapper.insertSelective(entity);
//            List<StlCapacctDto> dtos = entity.getSelectStlCapacct();
//            for (StlCapacctDto dto : dtos) {
//                dto.setCounterId(entity.getCounterId());
//                String[] noAndCcy = dto.getAcctNo().split("_");
//                dto.setAcctNo(noAndCcy[0]);
//                dto.setCcy(noAndCcy[1]);
//                mstTradeOpponent002Mapper.insertCounterAcct(dto);
//            }
//            if ("03".equals(entity.getCounterType())) {
//                List<FinCashDto> finDto = entity.getSelectFinCash();
//                mstTradeOpponent002Mapper.insertFinProduct(entity);
//                mstTradeOpponent002Mapper.insertFinProductType(entity);
//                mstTradeOpponent002Mapper.insertRepAssetMngPlan(entity);
//                for (FinCashDto dto : finDto) {
//                    dto.setFinprodId(entity.getFinprodId());
//                    dto.setFinprodType(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
//                    dto.setCashType1(DictCodeListExt1.现金流一级类型_费用.getValue());
//                    Map<String,Object> feeMap = new HashMap<String,Object>();
//                    feeMap.put("feeId", dto.getFeeId());
//                    dto.setCashType2(mstTradeOpponent002Mapper.selectCashType2(feeMap));
//                    dto.setCashId(md5(entity.getFinprodId()+
//                        DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue()+dto.getCashType2()));
//                    dto.setCashType2(mstTradeOpponent002Mapper.selectCashType2(feeMap));
//                    mstTradeOpponent002Mapper.insertFinCashCalcRule(dto);
//                    mstTradeOpponent002Mapper.insertFinCashInfo(dto);
//                    mstTradeOpponent002Mapper.insertFinRateInfo(dto);
//                }
//            }
//
//        }
        // 二级法人权限处理
        if(MultilevelDataAuthorityUtil.isEnabled()) {
            //创建机构代码
            String createInst =   MultilevelDataAuthorityUtil.getService().queryOrganizationCode(entity.getCreateUser());
            entity.setCreateInst(createInst);

            // 有查询权限的机构代码
            List<String> searchOrgCodeList = null;
            // 有编辑权限的机构代码
            List<String> editOrgCodeList = null;
            // 新建或修改时重新设置二级法人权限，删除时则清空
            if (!OperationType.DELETE.equals(type)) {
                //获取所有父级机构。  拥有查看权限
                searchOrgCodeList = CommonUtil.getParentInstitution(createInst);
                // 仅资产的创建机构（当前登录用户所在机构）有编辑权限
                editOrgCodeList = Arrays.asList(entity.getCreateInst());
            }
            //if(!(DictCodeListExt1.交易对手类型_内部交易对手.getValue().equals(entity.getCounterType())&&OperationType.MODIFY.equals(type))) {//内部交易对手保存不进行权限数据保存
            	// 记录二级法人权限
                insertDataAuthTypeInst(entity.getCounterId(), searchOrgCodeList, editOrgCodeList,DictCodeList.共通业务类型_交易对手管理.getValue());
            //}
        }
        entity.setLinkId(entity.getCustomerId());
        if ("03".equals(entity.getCounterType())) {
            entity.setLinkId(entity.getFinprodId());
        }
        entity.setBranch(Long.valueOf(CacheUtil.getSysParam("noBranchNoValue")));
        entity.setOperateType(type.toString());
        if (CollectionUtils.isNotEmpty(entity.getFileArray())
            && StringUtil.isNotEmpty(entity.getCounterId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                entity.getFileArray().stream().map(UploadFile::getFileNo).collect(
                    Collectors.toList()),
                entity.getCounterId());
        }
        // 调用投管接口 add by wangqidong 20190209 start
//        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
//        investInterfCommDto.setPageId("MstTradeOpponent002");
//        investInterfCommDto.setMstCounterParty(entity);
//        investInterfCommService.doInvestInterf(investInterfCommDto);
        // 调用投管接口 add by wangqidong 20190209 end
        restTemplate.postForObject(ServiceApplication.VALUATION_BASICDATA_SERVICE_NAME + "/ComTradeOpponent/doComplete002", entity, Integer.class);
    }

    /**
     * @param entity 交易对手实体类
     * @see
     * @since 1.0
     */
    public void insertAount(ComTradeOpponentDto entity) {
        insertSelective(entity);
        List<StlCapacctDto> dtos = entity.getSelectStlCapacct();
        for (StlCapacctDto dto : dtos) {
            dto.setCounterId(entity.getCounterId());
            String[] noAndCcy = dto.getAcctNo().split("_");
            dto.setAcctNo(noAndCcy[0]);
            dto.setCcy(noAndCcy[1]);
            insertCounterAcct(dto);
        }
    }

    /**
     * 选择插入
     * <功能详细描述>
     * @param entity 数据
     * @see
     * @since 1.0
     */
    public void insertSelective(ComTradeOpponentDto entity) {
        mstCounterPartyMapper.insertSelective(entity);
    }

    /**
     * 插入数据入库
     * <功能详细描述>
     * @param dto 参数
     * @see
     * @since 1.0
     */
    private void insertCounterAcct(StlCapacctDto dto) {
//        mstTradeOpponent002Mapper.insertCounterAcct(dto);
        restTemplate.postForObject(
            ServiceApplication.VALUATION_BASICDATA_SERVICE_NAME + "/ComTradeOpponent/insertCounterAcct",
            dto, Integer.class);
    }
    /**
     * 查找交易对手信息
     * @param map 交易对手代码
     * @return 交易对手实体类
     * @see
     * @since 1.0
     */
    public ComTradeOpponentDto selectCounterInfo(Map<String, Object> map) {
//        return mstTradeOpponent002Mapper.selectCounterInfo(map);
        return restTemplate.postForObject(
            ServiceApplication.VALUATION_BASICDATA_SERVICE_NAME + "/ComTradeOpponent/selectCounterInfo",
            map, ComTradeOpponentDto.class);
    }
    /**
     * 交易对手账号信息
     * @param map 交易对手账号
     * @return 交易对手账号实体类
     * @see
     * @since 1.0
     */
    public List<StlCapacctDto> selectStlCapacctInfo(Map<String, Object> map) {
//        return mstTradeOpponent002Mapper.selectStlCapacctInfo(map);
        return restTemplate.postForList(
            ServiceApplication.VALUATION_BASICDATA_SERVICE_NAME + "/ComTradeOpponent/selectStlCapacctInfo",
            map, StlCapacctDto.class);
    }
    /**
     * 
     * 现金流信息
     * @param map 现金流代码
     * @return 现金流实体类集合
     * @see
     * @since 1.0
     */
    public List<FinCashDto> selectFinCashInfo(Map<String, Object> map) {
//        return mstTradeOpponent002Mapper.selectFinCashInfo(map);
        return restTemplate.postForList(
            ServiceApplication.VALUATION_BASICDATA_SERVICE_NAME + "/ComTradeOpponent/selectFinCashInfo",
            map, FinCashDto.class);
    }
    /**
     * 
     * 获取信息
     * <功能详细描述>
     * @param map 参数
     * @return 返回结果
     * @see
     * @since 1.0
     */
    public StlCapacctInfo getCctInfo(Map<String, Object> map) {
//        mstTradeOpponent001Mapper.getCctInfo(map);
        return restTemplate.postForObject(
            ServiceApplication.VALUATION_BASICDATA_SERVICE_NAME + "/ComTradeOpponent/getCctInfo",
            map, StlCapacctInfo.class);
    }
    /**
     * 加密算法
     * @param plainText 加密文件
     * @return 加密后的文件
     * @see
     * @since 1.0
     */
    public static String md5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } 
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }
    /**
     * 查询交易对手是否使用该资金账户做过交易
     * @param mstCounterAcctKey
     * @return
     */
    public int isExistHistoryDeal(MstCounterAcctKey mstCounterAcctKey) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("acctNo", mstCounterAcctKey.getAcctNo());
    	map.put("counterId", mstCounterAcctKey.getCounterId());
    	map.put("ccy", mstCounterAcctKey.getCcy());
    	map.put("isCancel", DictCodeList.是否_否.getValue());
    	map.put("tradeStatus", DictCodeListExt1.交易状态_初始.getValue());
		return restTemplate.postForObject(
            ServiceApplication.VALUATION_BASICDATA_SERVICE_NAME + "/ComTradeOpponent/isExistHistoryDeal",
            map, Integer.class);	
    }

}
