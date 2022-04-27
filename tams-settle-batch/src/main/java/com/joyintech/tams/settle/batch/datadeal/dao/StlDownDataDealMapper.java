package com.joyintech.tams.settle.batch.datadeal.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.settle.common.dto.StlDownDataDealAccountInfoDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 数据清算Mapper
 * 
 * @author 陈卓
 * @date 2020/7/7
 * @see StlDownDataDealMapper
 * @since 1.0
 */
@Repository
public interface StlDownDataDealMapper {
	
	/**
     * 查询所有参数
     * @return List<SettleBatchPara>
     */
    List<SettleBatchPara> selectAllParam(@Param("dealDate") Date dealDate);

    // 查询需要撤销的清算明细数据数据
    List<SettleTransDataTotalMatch> selectNeedRevokeSettleTransDataTotals(@Param("dealDate") Date dealDate, @Param("portfolioIdList") List<String> portfolioIdList,
                   @Param("secTypeList") List<String> secTypeList);

    // 删除【成交数据与投管交易关系】
    int deleteSettleTransTgMapping(@Param("dealDate") Date dealDate, @Param("portfolioIdList") List<String> portfolioIdList, 
    		@Param("secTypeList") List<String> secTypeList);
    
    // 删除【成交数据明细质押品信息表】
    int deleteSettleTransDataPledge(@Param("dealDate") Date dealDate, @Param("portfolioIdList") List<String> portfolioIdList, 
    		@Param("secTypeList") List<String> secTypeList);
    
    // 删除【成交数据明细附属表】
    int deleteSettleTransDataAdd(@Param("dealDate") Date dealDate, @Param("portfolioIdList") List<String> portfolioIdList, 
    		@Param("secTypeList") List<String> secTypeList);
    
    // 删除【佣金计算过程表】
    int deleteCommisionCalcProcess(@Param("dealDate") Date dealDate, @Param("portfolioIdList") List<String> portfolioIdList, 
    		@Param("secTypeList") List<String> secTypeList);

    // 删除【成交数据明细表】
    int deleteSettleTransData(@Param("dealDate") Date dealDate, @Param("portfolioIdList") List<String> portfolioIdList, 
    		@Param("secTypeList") List<String> secTypeList);
    
    // 查询清算数据规则表
    List<SettleDataRule> selectSettleDataRuleBySecType(@Param("secTypeList") List<String> secTypeList);
    
    // 查询上中登JSMX
    List<SzdJsmx> selectSzdJsmx(@Param("dealDate") Date dealDate, @Param("dataFile") String dataFile, @Param("searchMap") Map<String, List<String>> searchMap);
    
    // 查询深中登SJSMX
    List<SzdSjsmx> selectSzdSjsmx(@Param("dealDate") Date dealDate, @Param("dataFile") String dataFile, @Param("searchMap") Map<String, List<String>> searchMap);
    
    // 查询深中登SJSJG
    List<SzdSjsjg> selectSzdSjsjg(@Param("dealDate") Date dealDate, @Param("dataFile") String dataFile, @Param("searchMap") Map<String, List<String>> searchMap);
    
    // 查询深中登SJSFX
    List<SzdSjsfx> selectSzdSjsfx(@Param("dealDate") Date dealDate, @Param("dataFile") String dataFile, @Param("searchMap") Map<String, List<String>> searchMap);
    
    // 查询上中登ZQBD
    List<SzdZqbd> selectSzdZqbd(@Param("dealDate") Date dealDate, @Param("dataFile") String dataFile, @Param("searchMap") Map<String, List<String>> searchMap);
    
    // 查询上中登ABCSJ
    List<SzdAbcsj> selectSzdAbcsj(@Param("dealDate") Date dealDate, @Param("dataFile") String dataFile, @Param("searchMap") Map<String, List<String>> searchMap);
    
    // 查询深中登ZSMX
    List<SzdZsmx> selectSzdZsmx(@Param("dealDate") Date dealDate, @Param("dataFile") String dataFile, @Param("searchMap") Map<String, List<String>> searchMap);
    
    // 获取下一工作日
    Date getNextWorkDay(@Param("nowday") Date nowday, @Param("calendarId") String calendarId);
    
    // 获取上一工作日
    Date getBeforeWorkDay(@Param("nowday") Date nowday, @Param("calendarId") String calendarId);

    /**
     *
     * 是否节假日
     * @param cdate 查询日期
     * @param calendarId 交易日历
     * @return 是否节假日（0：工作日；其他：节假日）
     * @see
     * @since 1.0
     */
    String getIsHoliday(@Param("cdate") Date cdate,@Param("calendarId") String calendarId);

    // 删除SETTLE_BATCH_PARA
    int deleteSettleBatchPara(@Param("dealDate") Date dealDate, @Param("portfolioIdList") List<String> portfolioIdList, 
    		@Param("secTypeList") List<String> secTypeList);
    
    /**
     * 查询所有债券的金融产品代码
     * @return List<String>
     */
    List<String> selectAllBondFinprodId();
    
    /**
     * 查询组合的收益类型
     * @param portfolioId 
     * @return List<String>
     */
    List<String> getPortfolioProfitType(@Param("portfolioId") String portfolioId);
    
    /**
     * 根据资产申报代码检索股票代码
     * @param othCodeType
     * @param othCode
     * @return List<String>
     */
    List<String> getFinproductIdByApprovalNo(@Param("othCodeType") String othCodeType, @Param("othCode") String othCode);

    /**
     * 根据资产申报代码检索股票代码
     * @return List<String>
     */
    String getLastFinproductIdByApprovalNo(@Param("othCode") String othCode);

    /**
     * 根据资产代码查询申报价格
     * @param othCodeType
     * @param finprodId
     * @return
     */
    BigDecimal getPriceByFinproductId(@Param("othCodeType") String othCodeType, @Param("finprodId") String finprodId);

    // 删除交易所品种额度（其他业务类型List）
    int deleteFinProdQuotByOthBussType(@Param("dealDate") Date dealDate, @Param("portfolioIdList") List<String> portfolioIdList, 
    		@Param("otherBusinessTypeList") List<String> otherBusinessTypeList);

    // 删除交易所品种额度（新股申购业务类型List）
    int deleteFinProdQuotByXgsgBussType(@Param("dealDate") Date dealDate, @Param("xgsgBusinessTypeList") List<String> xgsgBusinessTypeList);
    
    // 查询上中登ZQY
    List<SzdZqy> selectSzdZqy(@Param("dealDate") Date dealDate, @Param("dataFile") String dataFile);
    
    // 查询深中登SJSKS
    List<SzdSjsks> selectSzdSjsks(@Param("dealDate") Date dealDate, @Param("dataFile") String dataFile, @Param("searchMap") Map<String, List<String>> searchMap);

    // 资金账户查询
    List<StlDownDataDealAccountInfoDto> getAllStlDownDataDealAccountInfoDtoList();

    // 查询上中登TZXX
    List<SzdTzxx> selectSzdTzxx(@Param("dealDate") Date dealDate, @Param("dataFile") String dataFile, @Param("searchMap") Map<String, List<String>> searchMap);

    // 查询深中登SJSGB
    List<SzdSjsgb> selectSzdSjsgb(@Param("dealDate") Date dealDate, @Param("dataFile") String dataFile, @Param("searchMap") Map<String, List<String>> searchMap);

    // 删除【送股权益登记】
    int deleteShareBonusRegByCdate(@Param("dealDate") Date dealDate);

    // 删除【基金分红公告】
    int deletemstFundBonusNoticeByCdate(@Param("dealDate") Date dealDate);

   List<String> getZzgFinprodId(@Param("zqdm1") String zqdm1);

    /**
     * 查询首期质押券信息
     * @param sqcjbh
     * @param seatNumber
     * @param secAcctId
     * @return
     */
    List<SettleTransDataPledge> selectSqSettleTransDataPledges(@Param("sqcjbh") String sqcjbh, @Param("seatNumber") String seatNumber,
                                                               @Param("secAcctId") String secAcctId, @Param("vdate") Date vdate);

    /**
     * 根据市场代码查询标的
     * @param finProdMarketId
     * @return List<String>
     */
    List<FinProduct> getFinproductByFinProdMarketId(@Param("finProdMarketId") String finProdMarketId);

    List<SzdSjsjg> getSzdSjsjgByApplyTradeId(@Param("applyTradeId") String applyTradeId);
}
