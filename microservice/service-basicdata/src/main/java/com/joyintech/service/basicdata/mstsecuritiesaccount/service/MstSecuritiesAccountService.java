/*
 * 文件名：MstSecuritiesAccountService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangjun
 * 修改时间：2018年9月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.basicdata.mstsecuritiesaccount.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.tams.constant.DictCodeListExt3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.dao.StlSecAcctInfoMapper;
import com.joyintech.tams.common.base.entity.StlSecAcctInfo;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.basicdata.mstsecuritiesaccount.dao.MstSecuritiesAccountMapper;
import com.joyintech.service.basicdata.mstsecuritiesaccount.dto.MstSecuritiesAccountDto;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author zhangjun
 * @version 1.0 2018年9月11日
 * @see MstSecuritiesAccountService
 * @since 1.0
 */
@Service("backMstSecuritiesAccountService")
public class MstSecuritiesAccountService extends BaseService<MstSecuritiesAccountDto>{
//    /**
//     * 附件信息共通Service
//     */
//    @Autowired(required=false)
//    private MstFileCommonService mstFileCommonService;
    /**
     * 手写证券账号管理Mapper
     */
    @Autowired
    private MstSecuritiesAccountMapper mstSecuritiesAccountMapper;
    /**
     * 自动生成Mapper
     */
    @Autowired
    private StlSecAcctInfoMapper  stlSecAcctInfoMapper;
    
    /**
     * 查询
     * @param param
     * @return Page类
     * @throws Exception 异常信息
     */
    public Page<MstSecuritiesAccountDto> selectForPage(int page, int length,Map<String, Object> param)
    {
        return super.queryForPage(page, length, () -> mstSecuritiesAccountMapper.select(param));
    }
    
    /**
     * 组合代码联想下拉框 <功能详细描述>
     * 
     * @param param 组合代码 用户Id
     * @return List<SuggestDto>
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getPortfolioId(Map<String, Object> param) {
        return mstSecuritiesAccountMapper.getPortfolioId(param);
    }
    
    /**
     * 联想文本框service层实现
     *
     * @param param String
     * @return SuggestDto
     */
    public List<SuggestDto> getSecAcctId(String param) {
        return  mstSecuritiesAccountMapper.getSecAcctId(param);
    }
    /**
     * 联想文本框service层实现
     *
     * @param secAcctId String
     * @return SuggestDto
     */
    public MstSecuritiesAccountDto selectBySecAcctId(String secAcctId) {
        return  mstSecuritiesAccountMapper.selectBySecAcctId(secAcctId);
    }
    /**
     * 回款账户以及资金结算账户联想文本框service层实现
     *
     * @param search String
     * @return SuggestDto
     */
    public List<SelectOptionDto> getBusiAcctNo(String search) { 
        return  mstSecuritiesAccountMapper.getBusiAcctNo(search);
    }
    /**
     * 删除操作
     *
     * @param entity
     * @return SuggestDto
     */
    public int delete(StlSecAcctInfo entity) {
//        mstSecuritiesAccountMapper.deleteFileByBusinessId(entity.getSecAcctId());
        return  stlSecAcctInfoMapper.deleteByPrimaryKey(entity.getSecAcctId());
    }
    /**
     * 插入操作
     *
     * @param entity
     * @return SuggestDto
     */
    public int insert(MstSecuritiesAccountDto entity) {
//        if(entity.getFileName()!=null){
//          //查询上传的文件
//            FileDto dto=new FileDto();
//            dto.setFileName(entity.getFileName());
//            dto.setBusinessId(entity.getSecAcctId());
//            dto.setPath(entity.getPath());
//            dto.setSeqNo(entity.getSeqNo());//生成流水号
//            dto.setCreateDept(entity.getCreateDept());
//            mstSecuritiesAccountMapper.insertIntoFile(dto);
//        }
//        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
//        List<SysUploadFileInfo> fileList = new ArrayList<>();
//        if(entity.getFileArray()!=null){
//            for(FileDto fileArray:entity.getFileArray()){
//                SysUploadFileInfo sysUploadFileInfo = new SysUploadFileInfo();
//                sysUploadFileInfo.setSeqNo(fileArray.getFileNo());//生成流水号
//                fileList.add(sysUploadFileInfo);
//            }
//            mstFileInputParameter.setBussinessId(entity.getSecAcctId());
//            mstFileInputParameter.setFileList(fileList);
//            if(mstFileCommonService != null){
//                mstFileCommonService.updateBusinessId(mstFileInputParameter);
//            }
//        }
        return  stlSecAcctInfoMapper.insert(entity);
    }
    /**
     * 修改操作
     *
     * @param entity
     * @return SuggestDto
     */
    public int update(MstSecuritiesAccountDto entity) {
//        List<FileDto> dto=this.selectFileByBusinessId(entity.getSecAcctId());
//        if(dto!=null){
//            mstSecuritiesAccountMapper.deleteFileByBusinessId(entity.getSecAcctId());
//            dto.setFileName(entity.getFileName());
//            dto.setBusinessId(entity.getSecAcctId());
//            dto.setPath(entity.getPath());
//            dto.setSeqNo(entity.getSeqNo());//生成流水号
//            dto.setCreateDept(entity.getCreateDept());
//            mstSecuritiesAccountMapper.insertIntoFile(dto);
//        }
//        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
//        List<SysUploadFileInfo> fileList = new ArrayList<>();
//        if(entity.getFileArray()!=null){
//            for(FileDto fileArray:entity.getFileArray()){
//                SysUploadFileInfo sysUploadFileInfo = new SysUploadFileInfo();
//                sysUploadFileInfo.setSeqNo(fileArray.getFileNo());//生成流水号
//                fileList.add(sysUploadFileInfo);
//            }
//            mstFileInputParameter.setBussinessId(entity.getSecAcctId());
//            mstFileInputParameter.setFileList(fileList);
//            if(mstFileCommonService != null){
//                mstFileCommonService.updateBusinessId(mstFileInputParameter);
//            }
//        }
        return  stlSecAcctInfoMapper.updateByPrimaryKey(entity);
    }
    /**
     *检查证券账户账号是否用过
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public int checkBySecAcctId(MstSecuritiesAccountDto dto) {
        return mstSecuritiesAccountMapper.checkBySecAcctId(dto);
    }
    /**
     * 检查证券账户名称是否用过
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public int checkBySecAcctName(MstSecuritiesAccountDto dto) {
        return mstSecuritiesAccountMapper.checkBySecAcctName(dto);
    }
    /**
     * 检查资金结算账户是否存在
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public int checkByTrdAcctId(MstSecuritiesAccountDto dto) {
        return mstSecuritiesAccountMapper.checkByTrdAcctId(dto);
    }
    /**
     * 删除时检查数据是否被使用过
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public int checkCodeIsUse(MstSecuritiesAccountDto dto) {
        return mstSecuritiesAccountMapper.checkCodeIsUse(dto);
    }
    /**
     * 批量删除时检查数据是否被使用过
     * <功能详细描述>
     * @param item
     * @return
     * @see
     * @since 1.0
     */
    public List<MstSecuritiesAccountDto> checkAllCodeIsUse(List<MstSecuritiesAccountDto> item) {
        return mstSecuritiesAccountMapper.checkAllCodeIsUse(item);
    }
//    /**
//     * 查询上传的文件
//     * <功能详细描述>
//     * @param item
//     * @return
//     * @see
//     * @since 1.0
//     */
//    public List<FileDto> selectFileByBusinessId(String businessId) {
//        return mstSecuritiesAccountMapper.selectFileByBusinessId(businessId);
//    }
//    /**
//     * 插入附件文件
//     * <功能详细描述>
//     * @param item
//     * @return
//     * @see
//     * @since 1.0
//     */
//    public int insertIntoFile(FileDto dto) {
//        return mstSecuritiesAccountMapper.insertIntoFile(dto);
//    }
//    /**
//     * 插入附件文件
//     * <功能详细描述>
//     * @param item
//     * @return
//     * @see
//     * @since 1.0
//     */
//    public int deleteFileByBusinessId(String businessId) {
//        return mstSecuritiesAccountMapper.deleteFileByBusinessId(businessId);
//    }
    
	/**
	 * 证券代码
	 * 
	 * @param map
	 * @return
	 */
	public Page<SelectOptionDto> getFinprodMarketInfo(int page, int length, Map<String, Object> map) throws Exception {
		// 交易平台
		map.put("SHARES_TRADE_PLATFORM_02", DictCodeList.交易平台_竞价撮合平台.getValue());
		map.put("SHARES_TRADE_PLATFORM_03", DictCodeList.交易平台_固定收益平台.getValue());
		map.put("SHARES_TRADE_PLATFORM_04", DictCodeList.交易平台_大宗交易平台.getValue());
		// 交易场所
		map.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
		map.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());

		map.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
		map.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
		map.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
		map.put("FINPROD_TYPE_TG_ZYSHG", "ZYSHG");
		map.put("FINPROD_OTH_CODE_TYPE_05", DictCodeListExt3.金融产品其他代码类型_配债代码.getValue());
		map.put("FINPROD_OTH_CODE_TYPE_06", DictCodeListExt3.金融产品其他代码类型_配股代码.getValue());
		
		map.put("FINPROD_TYPE_TG_F01_B14", DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue());
		return super.queryForPageForSelectReturn(page, length, () -> mstSecuritiesAccountMapper.getFinprodMarketInfo(map));

	}
    
    /**
     * 组合资金账户
     * @param map
     * @return
     */
    public List<SelectOptionDto> getPtlSecAcctInfo(Map<String,Object> map){
        List<SelectOptionDto> list = mstSecuritiesAccountMapper.getPtlSecAcctInfo(map);
    	return list;
    }
    
    /**
     * 标的信息
     * @param map
     * @return
     */
    public List<SelectOptionDto> getFinprodId(Map<String,Object> map){
    	return mstSecuritiesAccountMapper.getPtlSecAcctInfo(map);
    }
    
    /**
     * 证券账户表
     * @param map
     * @return
     */
    public List<SelectOptionDto> getSecAcctInfo(Map<String,Object> map){
    	return mstSecuritiesAccountMapper.getSecAcctInfo(map);
    }

    public Page<SelectOptionDto> getFinprodMarketInfoPosition(int page, int length, Map<String, Object> params) {
        params.put("TRADE_MARKET_03",DictCodeListExt1.交易场所_上海证券交易所.getValue());
        params.put("TRADE_MARKET_04",DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        params.put("ASSET_TYPE_F01", DictCodeList.平安资产类型_债券.getValue());
        params.put("ASSET_TYPE_F02", DictCodeList.平安资产类型_基金.getValue());
        params.put("ASSET_TYPE_F11", DictCodeListExt2.平安资产类型_股票.getValue());

        return super.queryForPageForSelectReturn(page, length, () -> mstSecuritiesAccountMapper.getFinprodMarketInfoPosition(params));
    }

    public List<SelectOptionDto> getFinprodIdByMarketId(Map<String, Object> map) {
        return mstSecuritiesAccountMapper.getFinprodIdByMarketId(map);
    }
    
    public List<SelectOptionDto> getFuturesCompany(String companyNo) {
        return mstSecuritiesAccountMapper.getFuturesCompany(companyNo);
    }

    public List<Map<String, Object>> getFaceValueList(Map<String, Object> map) {
        return mstSecuritiesAccountMapper.getFaceValueList(map);
    }

    public String IsBondAsset(Map<String, Object> map) {
        Boolean sinal = StringUtil.equals(mstSecuritiesAccountMapper.IsBondAsset(map),"F01");
        if(!sinal){
            return "1";
        }else{
           String testStatus = "";
           testStatus = mstSecuritiesAccountMapper.QueryForTestStatus(map);
           if(StringUtil.equals("2",testStatus)){
               return "2";
           }else if(StringUtil.equals("3",testStatus)){
               return "3";
           }else {
               return "4";
           }
        }
    }

    /**
     * 证券代码
     *
     * @param map
     * @return
     */
    public Page<SelectOptionDto> getFinprodMarketInfoNewUi(int page, int length, Map<String, Object> map) throws Exception {
        // 交易平台
        map.put("SHARES_TRADE_PLATFORM_02", DictCodeList.交易平台_竞价撮合平台.getValue());
        map.put("SHARES_TRADE_PLATFORM_03", DictCodeList.交易平台_固定收益平台.getValue());
        map.put("SHARES_TRADE_PLATFORM_04", DictCodeList.交易平台_大宗交易平台.getValue());
        // 交易场所
        map.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        map.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());

        map.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        map.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        map.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        map.put("FINPROD_TYPE_TG_ZYSHG", "ZYSHG");
        map.put("FINPROD_OTH_CODE_TYPE_05", DictCodeListExt3.金融产品其他代码类型_配债代码.getValue());
        map.put("FINPROD_OTH_CODE_TYPE_06", DictCodeListExt3.金融产品其他代码类型_配股代码.getValue());
        map.put("FINPROD_OTH_CODE_TYPE_07", DictCodeListExt3.金融产品其他代码类型_新股申购代码.getValue());
        map.put("FINPROD_TYPE_TG_F01_B14", DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue());
        if (StringUtil.isNotEmpty(map.get("tradePlat"))
                && StringUtil.equals(map.get("tradePlat").toString(),DictCodeList.交易平台_非交易业务指令平台.getValue())){
            map.put("tradePlat", DictCodeList.交易平台_竞价撮合平台.getValue());
            map.remove("SHARES_TRADE_PLATFORM_02");
        }
        String entrustBussType=StringUtil.isNotEmpty(map.get("entrustBussType"))?map.get("entrustBussType").toString():"";
        if(StringUtil.isNotEmpty(entrustBussType)&&
                (DictCodeListExt3.上农商_指令业务类型_债券回售指令.getValue().equals(entrustBussType)
                || DictCodeListExt3.上农商_指令业务类型_配债指令.getValue().equals(entrustBussType)
                || DictCodeListExt3.上农商_指令业务类型_可转债申购指令.getValue().equals(entrustBussType)
                || DictCodeListExt3.上农商_指令业务类型_标准券出入库指令.getValue().equals(entrustBussType)
                || DictCodeListExt3.上农商_指令业务类型_债转股指令.getValue().equals(entrustBussType))){
            map.put("tradePlat", "");
        }
        Map pageMode = (Map) map.get("pageMode");
        if(pageMode != null) { // 设置导出条数
            if(pageMode.get("pageNo")!=null){
                page=Integer.parseInt(pageMode.get("pageNo").toString());
            }
            if(pageMode.get("pageSize")!=null){
                length=Integer.parseInt(pageMode.get("pageSize").toString());
            }

        }
        return super.queryForPageForSelectReturn(page, length, () -> mstSecuritiesAccountMapper.getFinprodMarketInfoNewUi(map));

    }


    /**
     * 证券代码(债券 质押式回购)
     *
     * @param map
     * @return
     */
    public Page<SelectOptionDto> getFinprodMarketInfoSecRepo(int page, int length, Map<String, Object> map) throws Exception {
        // 交易场所
        map.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        map.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        map.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        map.put("FINPROD_TYPE_TG_ZYSHG", "ZYSHG");
        map.put("CUSTOM_ASSET_TYPE_FOUR_013", DictCodeListExt3.行内资产四级分类_可转债.getValue()); // 可转债
        Map pageMode = (Map) map.get("pageMode");
        if(pageMode != null) { // 设置导出条数
            if(pageMode.get("pageNo")!=null){
                page=Integer.parseInt(pageMode.get("pageNo").toString());
            }
            if(pageMode.get("pageSize")!=null){
                length=Integer.parseInt(pageMode.get("pageSize").toString());
            }
        }
        return super.queryForPageForSelectReturn(page, length, () -> mstSecuritiesAccountMapper.getFinprodMarketInfoSecRepo(map));

    }
}
