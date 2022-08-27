/*
 * 文件名：MstCaptialAccountService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2018年8月3日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.basicdata.mstcaptialaccount.service;

import java.math.BigDecimal;
import java.util.*;

import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.EncryptUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.basicdata.mstcaptialaccount.dao.BasicMstCaptialAccountMapper;
import com.joyintech.service.basicdata.mstcaptialaccount.dto.MstCaptialAccountDto;
import com.joyintech.service.basicdata.mstcaptialaccount.dto.MstCaptialAccountDto2;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.dto.MstFileOutputParameter;

/**
 * 
 * 资金账户
 * 〈功能详细描述〉
 * @author huwenbin
 * @version 1.0 2018年8月20日
 * @see MstCaptialAccountService
 * @since 1.0
 */
@Service("backMstCaptialAccountService")
public class MstCaptialAccountService extends BaseService<MstCaptialAccountDto>{
    
    /**
     * 附件信息共通Service
     */
    @Autowired(required=false)
    private MstFileCommonService mstFileCommonService;
    
    /**
     * 资金账户管理mapper
     */
    @Autowired
    private BasicMstCaptialAccountMapper basicMstCaptialAccountMapper;

    /**
     * 资金账户管理mapper(自动生成)
     */
    @Autowired
    private StlCapacctInfoMapper stlCapacctInfoMapper;
    
    @Autowired
    private StlCapacctReInfoMapper stlCapacctReInfoMapper;
    
    /**
     * 现金流信息表Mapper
     */
    @Autowired
    private FinCashInfoMapper finCashInfoMapper;
    /**
     * 利率信息表Mapper
     */
    @Autowired
    private FinRateInfoMapper finRateInfoMapper;
    /**
     * 现金流计算规则表Mapper
     */
    @Autowired
    private FinCashCalcRuleMapper finCashCalcRuleMapper;

    /**
     * 新增资金账户管理，所属柜台与初始利率关系表
     */
    @Autowired
    private FinInitrateDatadictMapper finInitrateDatadictMapper;


    /**
     * 使用给定的acctName检索分页数据。返回类型是Service的泛型。
     * @param length 
     * @param page 
     *
     * @param pageParam 查询方法
     * @return Page类
     * @throws Exception 异常信息
     */
    public Page<MstCaptialAccountDto> selectForPage(int page, int length, Map<String, Object> pageParam){
        return super.queryForPage(page,length,() -> basicMstCaptialAccountMapper.select(pageParam));
    }

    /**
     * 联想文本框service层实现
     *
     * @param acctName 检索参数
     * @return SuggestDto
     */
    public List<SuggestDto> getAcctName(String acctName) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("acctName", acctName);
        return basicMstCaptialAccountMapper.getAcctName(map);
    }

    /**
     * 根据网点账号与币种查询详细信息 双击修改页面
     * 
     * @param map 网点账号与币种
     * @return 资金账户实体
     * @see
     * @since 1.0
     */
    public MstCaptialAccountDto selectDetail(Map<String, Object> map) {
        map.put("CASH_TYPE_1_02", DictCodeListExt1.现金流一级类型_利息.getValue());
        map.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
        map.put("FIN_CASH_BUSINESS_TYPE_02", DictCodeListExt1.现金流业务类型_资金账户.getValue());
        map.put("INT_TYPE_01", DictCodeList.利率类型_固定利率.getValue());
        map.put("INT_TYPE_02", DictCodeList.利率类型_浮动利率.getValue());
        map.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        map.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        
        MstCaptialAccountDto dto = basicMstCaptialAccountMapper.selectDetail(map);
        
        //获取资金账户关联表数据
        if(StringUtil.isNotEmpty(dto)) {
            Map<String, Object> reMap = new HashMap<String, Object>();
            reMap.put("acctNo", dto.getAcctNo());
            reMap.put("ccy", dto.getCcy());
            List<StlCapacctReInfo> stlCapacctReInfoList = basicMstCaptialAccountMapper.getStlCapacctReInfo(reMap);
            if(!StringUtil.isEmptyList(stlCapacctReInfoList)) {
                String acctType = "";
                for(StlCapacctReInfo stlCapacctReInfo:stlCapacctReInfoList) {
                    acctType = acctType + ","+ stlCapacctReInfo.getAcctType();
                }
                dto.setAccountType(acctType);
            }
            //将账户密码和交易密码解密
            if(StringUtil.indexOf(dto.getAccountType(), DictCodeListExt1.资金账户类型_券商资金账户.getValue())>0) {
                dto.setAccPass(StringUtil.isNotEmpty(dto.getAccPass())? EncryptUtil.getInstance().decodeBase64(dto.getAccPass()):dto.getAccPass());
                dto.setTradePass(StringUtil.isNotEmpty(dto.getTradePass())?EncryptUtil.getInstance().decodeBase64(dto.getTradePass()):dto.getTradePass()); 
            }
        }
        
        
//        if(StringUtil.equals(dto.getIsCalint(), DictCodeList.是否_是.getValue())) {
//            List<MstCaptialAccountDto> accCurrentInterestList = this.getAccCurrentInterestList(dto.getAcctNo());
//            if(!StringUtil.isEmptyList(accCurrentInterestList)) {
//                MstCaptialAccountDto tmpDto = accCurrentInterestList.get(0);
//                dto.setEffectDate(tmpDto.getEffectDate());
//                dto.setBasis(tmpDto.getBasis());
//                dto.setRateType(tmpDto.getRateType());
//                dto.setRate(tmpDto.getRate());
//                dto.setCurrentRate(tmpDto.getCurrentRate());
//                dto.setIndexId(tmpDto.getIndexId());
//                dto.setCoefficient(tmpDto.getCoefficient());
//                dto.setSpreadRate(tmpDto.getSpreadRate());
//            }
//        }
        return dto;
    }

    /**
     * 实现docomplement方法
     */
    public void doComplete(MstCaptialAccountDto entity) {
        String type = entity.getOperateType();
        List<StlCapacctReInfo> stlCapacctReInfoList = this.genStlCapacctReInfo(entity);
        if (StringUtil.equals(type, "DELETE")) {
//            entity.setAcctNo(entity.getLargePayno()+":"+entity.getBusiAcctNo());
            //资金账户关联表删除
            basicMstCaptialAccountMapper.deleteStlCapacctReInfo(entity);
            //资金账户删除
            stlCapacctInfoMapper.deleteByPrimaryKey(entity);   
        }
        else if (StringUtil.equals(type, "NEW")) {
//            entity.setAcctNo(entity.getLargePayno()+":" + entity.getBusiAcctNo());
            
            //将账户密码和交易密码加密
            entity.setAccPass(EncryptUtil.getInstance().encodeBase64(entity.getAccPass()));
            
            entity.setTradePass(EncryptUtil.getInstance().encodeBase64(entity.getTradePass()));
            
            stlCapacctInfoMapper.insert(entity);
            
            if(!StringUtil.isEmptyList(stlCapacctReInfoList)) {
                for (StlCapacctReInfo stlCapacctReInfo : stlCapacctReInfoList) {
                    stlCapacctReInfoMapper.insertSelective(stlCapacctReInfo); 
                }
            }
            
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            for(SysUploadFileInfo fileArray:entity.getFileArray()){
                fileArray.setSeqNo(fileArray.getSeqNo());
            }
            mstFileInputParameter.setFileList(entity.getFileArray());
            mstFileInputParameter.setBussinessId(entity.getLargePayno()+":"+entity.getBusiAcctNo());
            if(mstFileCommonService != null){
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
            
            if(StringUtil.equals(entity.getIsCalint(),DictCodeList.是否_是.getValue())) {
                this.insertAccCurrentInterest(entity);
            }


            //账户资金管理，所属柜台初始利率表
            //查询是否存在所属柜台对应利率，无记录，则新增  add by xxg start
            FinInitrateDatadict ft = finInitrateDatadictMapper.selectByPrimaryKey(entity.getOwnDept());
            if(ft == null && "A07".equals(entity.getAccountType()) && "01".equals(entity.getRateType())){
                FinInitrateDatadict finInitrateDatadict = new FinInitrateDatadict();
                finInitrateDatadict.setRate(entity.getRate());
                finInitrateDatadict.setOwnDept(entity.getOwnDept());
                finInitrateDatadict.setCreateTime(new Date());
                finInitrateDatadict.setCreateUser(entity.getCreateUser());
                finInitrateDatadict.setCreateDept(entity.getCreateDept());
                finInitrateDatadictMapper.insert(finInitrateDatadict);
            }
           //查询是否存在所属柜台对应利率，无记录，则新增 add by xxg end


        }
        else {
            //资金账户关联表更新
            basicMstCaptialAccountMapper.deleteStlCapacctReInfo(entity);
            if(!StringUtil.isEmptyList(stlCapacctReInfoList)) {
                for (StlCapacctReInfo stlCapacctReInfo : stlCapacctReInfoList) {
                    stlCapacctReInfoMapper.insertSelective(stlCapacctReInfo); 
                }
            }
            
            //将账户密码和交易密码加密
            entity.setAccPass(EncryptUtil.getInstance().encodeBase64(entity.getAccPass()));
            
            entity.setTradePass(EncryptUtil.getInstance().encodeBase64(entity.getTradePass()));
            //资金账户更新
//            entity.setAcctNo(entity.getLargePayno()+":"+entity.getBusiAcctNo());
            stlCapacctInfoMapper.updateByPrimaryKeySelective(entity);

            // 账户类型为“券商资金账户“时，支持修改利率 add by xxg start
            if("01".equals(entity.getRateType())){
                String cashId = CommonUtil.generateCashFlowCode(entity.getLargePayno()+":"+entity.getBusiAcctNo(), DictCodeListExt1.现金流业务类型_资金账户.getValue(), 0L, DictCodeListExt1.现金流二级类型_利息.getValue());
                FinRateInfo finRateInfo = new FinRateInfo();
                finRateInfo.setEffDate(entity.getEffectDate());
                finRateInfo.setCashId(cashId);
                finRateInfo.setRate(entity.getRate().divide(new BigDecimal("100")));
                finRateInfoMapper.updateByPrimaryKeySelective(finRateInfo);
            }
            // 账户类型为“券商资金账户“时，支持修改利率 add by xxg end

            //上传文件修改更新
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            for(SysUploadFileInfo fileArray:entity.getFileArray()){
                fileArray.setSeqNo(fileArray.getSeqNo());//生成流水号
            }
            mstFileInputParameter.setBussinessId(entity.getLargePayno()+":"+entity.getBusiAcctNo());
            mstFileInputParameter.setFileList(entity.getFileArray());
            if(mstFileCommonService != null){
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }
        }
    }

    /**
     * 删除检查数据是否被使用过 <功能详细描述>
     * 
     * @param dto 业务实体
     * @return 查询条数
     * @see
     * @since 1.0
     */
    public int checkCodeIsUse(MstCaptialAccountDto dto) {
        int i = basicMstCaptialAccountMapper.checkCodeIsUse(dto);
        return i;
    }

    /**
     * 批量删除检查数据是否被使用过 <功能详细描述>
     * 
     * @param item 资金账户实体集合
     * @return 返回查询的所有条数
     * @see
     * @since 1.0
     */
    public List<MstCaptialAccountDto> checkAllCodeIsUse(List<MstCaptialAccountDto> item) {
        return basicMstCaptialAccountMapper.checkAllCodeIsUse(item);
    }

//    /**
//     * 
//     * {@inheritDoc}
//     */
//    @Override
//    protected void buildExportParam(ExportParam exportParam) {
//        exportParam.config("/com/joyintech/basicdata/mstcaptialaccount/conf/export_conf.xml").select(
//            () -> mstCaptialAccountMapper.select(exportParam.getSelectParam())).dataSource(
//                DataSourceType.MASERT);
//    }

    
    /**
     * 
     * 查询上传文件
     * <功能详细描述>
     * @param businessId businessId
     * @return List<FileArray>
     * @see
     * @since 1.0
     */
    public List<SysUploadFileInfo> selectFileByBusinessId(String businessId){
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setBussinessId(businessId);
        if(mstFileCommonService != null){
            MstFileOutputParameter output = mstFileCommonService.getFileInfoByBusiId(businessId);
            return output.getUploadFileList();
        }
        else{
            return null;
        }
    }

    /**
     * 查询 
     * <功能详细描述>
     * @param map 参数
     * @return 结果
     * @see
     * @since 1.0
     */
    public List<MstCaptialAccountDto> select(Map<String, Object> map) {
        return basicMstCaptialAccountMapper.select(map);
    }

    public int updateTitle(Map<String, Object> map){
        return basicMstCaptialAccountMapper.updateTitle(map);
    }

    public int getUniqueAcctNoPayNo(Map<String, Object> map){
        return basicMstCaptialAccountMapper.getUniqueAcctNoPayNo(map);
    }

    
    public List<MstCaptialAccountDto> selectCheck(Map<String, Object> map) {
        return basicMstCaptialAccountMapper.selectCheck(map);
    }

    public List<MstCaptialAccountDto2> selectForInterf(Map<String, Object> map) {
        return basicMstCaptialAccountMapper.selectForInterf(map);
    }
		
    /**
     * 查询流程当前节点信息
     * <一句话功能简述>
     * <功能详细描述>
     * @param workflowId
     * @return
     * @see
     * @since 1.0
     */
    public SysWflBusinessList getSysWflBusinessList(String workflowId){
        return basicMstCaptialAccountMapper.getSysWflBusinessList(workflowId);
    }
    
    private void insertAccCurrentInterest(MstCaptialAccountDto dto) {
        
        if(dto.getSpreadRate() == null){
            dto.setSpreadRate(new BigDecimal("0"));
        }
        if(dto.getCoefficient() == null){
            dto.setCoefficient(new BigDecimal("1"));
        }
        
        String cashId = CommonUtil.generateCashFlowCode(dto.getLargePayno()+":"+dto.getBusiAcctNo(), DictCodeListExt1.现金流业务类型_资金账户.getValue(), 0L, DictCodeListExt1.现金流二级类型_利息.getValue());
        FinCashInfo finCashInfo = new FinCashInfo();
        finCashInfo.setCashId(cashId);
        finCashInfo.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        finCashInfo.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
        finCashInfo.setCcy(dto.getCcy());
        finCashInfo.setFinprodId(dto.getLargePayno()+":"+dto.getBusiAcctNo());
        finCashInfo.setFinprodType(DictCodeListExt1.现金流业务类型_资金账户.getValue());
        finCashInfo.setBranch(Long.valueOf(0));
        FinCashInfo oldCashId = finCashInfoMapper.selectByPrimaryKey(cashId);
        // modify by etliu 20220320 start
        // 3.0.5产品 ZGTFCP-1165:清算交收资金账户管理新建时，提交后显示系统异常
        if(oldCashId == null){
            finCashInfoMapper.insertSelective(finCashInfo);
        } else {
            finCashInfoMapper.deleteByPrimaryKey(cashId);
            finCashInfoMapper.insertSelective(finCashInfo);
        }
        // modify by etliu 20220320 end

        FinRateInfo finRateInfo = new FinRateInfo();
        finRateInfo.setCashId(cashId);
        finRateInfo.setEffDate(dto.getEffectDate());
        finRateInfo.setIntType(dto.getRateType());
        finRateInfo.setBasis(dto.getBasis());
        if(StringUtil.equals(dto.getRateType(), DictCodeList.利率类型_浮动利率.getValue())){
            finRateInfo.setRate(getIndexRate(dto));
            finRateInfo.setResetType(DictCodeListExt2.重置类型_随调.getValue());
            finRateInfo.setCoefficient(dto.getCoefficient());
            finRateInfo.setSpreadRate(dto.getSpreadRate().divide(new BigDecimal("100")));
        }
        else {
            finRateInfo.setRate(dto.getRate().divide(new BigDecimal("100")));
        }

        finRateInfo.setBenchmarkId(dto.getIndexId());
        finRateInfo.setBenchmarkType(dto.getIndexSource());
        finRateInfo.setBranch(Long.valueOf(0));
        finRateInfo.setFinprodId(dto.getLargePayno()+":"+dto.getBusiAcctNo());
        finRateInfo.setFinprodType(DictCodeListExt1.现金流业务类型_资金账户.getValue());

        // modify by etliu 20220320 start
        // 3.0.5产品 ZGTFCP-1165:清算交收资金账户管理新建时，提交后显示系统异常
        if (finRateInfoMapper.selectByPrimaryKey(finRateInfo) != null) {
            finRateInfoMapper.deleteByPrimaryKey(finRateInfo);
        }
        finRateInfoMapper.insertSelective(finRateInfo);
        // modify by etliu 20220320 end

        FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
        finCashCalcRule.setCashId(cashId);
        finCashCalcRule.setEffDate(dto.getEffectDate());
        finCashCalcRule.setCalcType(dto.getRateType());
        finCashCalcRule.setBasis(dto.getBasis());
        if(StringUtil.equals(dto.getRateType(), DictCodeList.利率类型_浮动利率.getValue())){
            finCashCalcRule.setYield(getIndexRate(dto).multiply(dto.getCoefficient()).add(dto.getSpreadRate().divide(new BigDecimal("100")))); 
        } 
        else {
            finCashCalcRule.setYield(dto.getRate().divide(new BigDecimal("100")));
        }
        
        finCashCalcRule.setFinprodId(dto.getLargePayno()+":"+dto.getBusiAcctNo());
        finCashCalcRule.setFinprodType(DictCodeListExt1.现金流业务类型_资金账户.getValue());
        
        finCashCalcRule.setBranch(Long.valueOf(0));

        // modify by etliu 20220320 start
        // 3.0.5产品 ZGTFCP-1165:清算交收资金账户管理新建时，提交后显示系统异常
        if (finCashCalcRuleMapper.selectByPrimaryKey(finCashCalcRule) != null) {
            finCashCalcRuleMapper.deleteByPrimaryKey(finCashCalcRule);
        }
        // modify by etliu 20220320 end
        finCashCalcRuleMapper.insertSelective(finCashCalcRule);
    }
    
    /**
     * 计算浮动利率的值
     * @param dto 账户活期利率实体类
     * @return 浮动利率
     * @see
     * @since 1.0
     */
    public BigDecimal getIndexRate(MstCaptialAccountDto dto){
        BigDecimal realIntRate = new BigDecimal("0");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("indexId", dto.getIndexId());
        param.put("effDate", DateUtil.format(dto.getEffectDate()));
        param.put("ccy", dto.getCcy());
      //基准来源 = 利率行情时
        //从利率行情表中取得基准利率
        if(DictCodeListExt1.基准类型_2_利率.getValue().equals(dto.getIndexSource())){
            realIntRate = realIntRate.add(basicMstCaptialAccountMapper.getIndexIntRate(param)
                    != null ? basicMstCaptialAccountMapper.getIndexIntRate(param) : new BigDecimal("0"));
        }
        return realIntRate;
    }
    
//    /**
//     * 获取活期利率信息
//     * <功能详细描述>
//     * @param finprodId
//     * @return
//     * @see
//     * @since 1.0
//     */
//    public List<MstCaptialAccountDto> getAccCurrentInterestList(String finprodId){
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("CASH_TYPE_1_02", DictCodeListExt1.现金流一级类型_利息.getValue());
//        map.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
//        map.put("FIN_CASH_BUSINESS_TYPE_02", DictCodeListExt1.现金流业务类型_资金账户.getValue());
//        map.put("finprodId", finprodId);
//        map.put("INT_TYPE_01", DictCodeList.利率类型_固定利率.getValue());
//        map.put("INT_TYPE_02", DictCodeList.利率类型_浮动利率.getValue());
//        return basicMstCaptialAccountMapper.getAccCurrentInterestList(map);
//    }
    
    public Page<SelectOptionDto> getOwnDept(Map<String,Object> map,int page,int length){
        return super.queryForPageForSelectReturn(page,length,()->basicMstCaptialAccountMapper.getOwnDept(map)); 
    }
    
    
    private List<StlCapacctReInfo> genStlCapacctReInfo(MstCaptialAccountDto entity) {
        List<String> acctTypeList = StringUtil.splitToList(",", entity.getAccountType());
        List<StlCapacctReInfo> list = new ArrayList<StlCapacctReInfo>();
        if(!StringUtil.isEmptyList(acctTypeList)) {
            for (String acctType : acctTypeList) {
                StlCapacctReInfo stlCapacctReInfo = new StlCapacctReInfo();
                stlCapacctReInfo.setAcctNo(entity.getAcctNo());
                stlCapacctReInfo.setAcctType(acctType);
                stlCapacctReInfo.setCcy(entity.getCcy());
                list.add(stlCapacctReInfo);
            }
        } 
        return list;  
    }



    /**
     * add by xxg 20210726
     * 根据所属柜台获取第一次设置的利率
     * @param ownDept  所属柜台
     * @return
     */
    public FinInitrateDatadict getFirstRateByOwnDept(String ownDept){
        return finInitrateDatadictMapper.selectByPrimaryKey(ownDept);
    }




    /**
     * add by caozhenming 20210728
     * 根据所属券商获取资金账号
     * @param param
     * @return
     */
    public List<SelectOptionDto>  getAcctInfo(Map<String,Object> param){
        return basicMstCaptialAccountMapper.getAcctInfo(param);
    }
    
}
