package com.joyintech.tams.svreport.position.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.batch.report.autosendcb.common.service.SvrCBBaseService;
import com.joyintech.tams.common.base.dao.ChbTypeMappingExMapper;
import com.joyintech.tams.common.base.entity.ChbTypeMappingEx;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.report.common.constant.BatchBeanName;
import com.joyintech.tams.report.interf.common.constant.InterfConstants;
import com.joyintech.tams.report.interf.common.constant.InterfMappingConstants;
import com.joyintech.tams.svreport.interf.chb.conf.DataSourceType;
import com.joyintech.tams.svreport.position.dao.ChbAssetPositionMapper;
import com.joyintech.tams.svreport.position.dto.ChbAssetPosition;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.tools.collectors.JoyinCollectors;

/**
 * 银行月度报表生成资产端数据处理类
 * 
 * @author yuluan
 ** @version 1.0 2019年2月20日
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class AssetPositionService extends SvrCBBaseService{

    @Value("${application.reportSelectOrDeleteCount}")
    public int reportSelectOrDeleteCount;

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ChbAssetPositionMapper chbAssetPositionMapper;

    @Autowired
    private SqlSessionTemplate template;
    /**
     * 映射配置mapper
     */
    @Autowired
    private ChbTypeMappingExMapper chbTypeMappingExMapper;


    /**
     * 轮询时更新资产头寸
     * @param cdate
     * @param mappara
     */
    public void genAssetPosition(Date batchDate,Map<String,List<String>> mappara){

        String monthReportCalWay = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ASSETPOSITION_CALWAY);
        if(SVReportGlobalConstant.ASSETPOSITION_CALWAY_MARKETPRICE
                .equals(monthReportCalWay)){
            logger.info("生成资产头寸：市价法！");
            genAssetPositionMarketPrice(batchDate,mappara);
        } else if(SVReportGlobalConstant.ASSETPOSITION_CALWAY_AVERAGE
                .equals(monthReportCalWay)){
            logger.info("生成资产头寸：移动平均法！");
            genAssetPositionAverage(batchDate,mappara);
        } else {
            logger.error("系统参数配置错误,请检查->资产月报期末余额计算方式:SVR_MONTHREPORT_CALWAY");
        }
    }

    /**
     * 功能:市价法生成资产头寸
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年7月2日
     * @param batchDate
     * @param mappara 
     * void
     */
    private void genAssetPositionMarketPrice(Date batchDate, Map<String, List<String>> mappara) {
        // 获取需要跑批的资产代码和产品代码
        List<String> prodids = null;
        List<String> assetcodes = null;
        if (mappara != null) {
            // 当产品代码超过500条时进行不区分产品跑批
            prodids = mappara.get(InterfConstants.SVR_PRODLIST);
            if (prodids != null && prodids.size() > 500) {
                prodids = null;
            }
            // 当资产代码超过500条时进行不区分资产跑批
            assetcodes = mappara.get(InterfConstants.SVR_ASSETLIST);
            if (assetcodes != null && assetcodes.size() > 500) {
                assetcodes = null;
            }
        }
        // 日志记录批处理开始时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date bdate = new Date();
        Long bt = bdate.getTime();
        logger.info("资产头寸批处理启动:" + sdf.format(bdate));
        // 根据资产基础指标数据CHB_ASSETS_BASIS_DATA、科目余额数据BOK_BALANCE生成资产头寸
        // 处理资产基础指标数据，查询跑批日更新的数据,CHB_ASSETS_BASIS_DATA表更新日期=跑批日期
        // 通过产品代码PROD_ID、最小估值日期VALUE_DATE，从最小持仓日期到跑批日的持仓数据重新生成
        String isBtm = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_CHL_BTM);
        Date minSettdate = chbAssetPositionMapper.selectMinAssetsBasisDataByProdidAndAssetCode(batchDate,
                prodids, assetcodes,isBtm);
        for (Date startDate = minSettdate ; startDate.compareTo(batchDate) <= 0;) {
            genAssetPositionMP(startDate, prodids, assetcodes,false,null);
            startDate = DateUtil.addDay(startDate, 1);
        }
        // 处理科目余额数据，查询跑批日更新的数据,BOK_BALANCE表更新日期=跑批日期
        // 通过产品代码PROD_ID、最小估值日期BALANCE_DATE，从最小持仓日期到跑批日的持仓数据重新生成
        Date minBalancedate = new Date();
        // 读取开关查询上游数据 
        AssetPositionService asetPositionService =  SpringContextUtil.getBean(AssetPositionService.class);
        // 根据开关判断是否走中债表中取值还是走一体化上游表中取值
        if (DictCodeList.是否_中债_是.getValue().equals(SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_SUB_SYSTEM))){
            minBalancedate = asetPositionService.queryMintBalanceDateSubSystem(batchDate,
                    prodids, assetcodes);
        } else if (DictCodeList.是否_中债_否.getValue().equals(SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_SUB_SYSTEM))){
            minBalancedate = asetPositionService.queryMintBalanceDate(batchDate,
                    prodids, assetcodes);
        } else {
            logger.error("系统参数中：SVR_IS_SUB_SYSTEM配置错误！");
            return ;
        }
        for (Date startDate = minBalancedate ; startDate.compareTo(batchDate) <= 0;) {
            genAssetPositionMP(startDate, prodids, assetcodes,true,InterfMappingConstants.MAPPINGCODE_CASH_SUBJECTNO_MKT);
            startDate = DateUtil.addDay(startDate, 1);
        }
        // 记录批处理结束日志
        Date edate = new Date();
        Long et = edate.getTime();
        Long time = (et - bt) / 1000;
        logger.info("资产头寸批处理结束:" + sdf.format(edate) + "耗时" + time + "秒");
    }
    /**
     * 功能:市价法每日生成资产头寸
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年7月2日
     * @param batchDate 跑批日
     * @param prodids 产品代码
     * @param assetcodes 资产代码
     * @param isBalance 是否为剩余现金
     * void
     * @return 
     */
    @SuppressWarnings("rawtypes")
    private int genAssetPositionMP(Date batchDate, List<String> prodids, 
            List<String> assetcodes, boolean isBalance,String mappingCode) {
        // 删除大于等于今日的资产头寸
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("cdate", batchDate);
        param.put("prodids", prodids);
        param.put("assetcodes", assetcodes);
        if (isBalance){
            // 删除剩余现金的头寸
            param.put("positionType", "02");
        } else {
            // 删除非剩余现金的头寸
            param.put("positionType", "01");
        }
        chbAssetPositionMapper.deleteByDate(param);
        // 记录生成头寸条数
        int counts = 0;
        // 查询当日的头寸数据
        List<ChbAssetPosition> assetPositionListAdd = new ArrayList<>();
        // 流式查询
        String isBtm = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_CHL_BTM);
        if (isBalance){
            // 通过系统参数判断是查询一体化还是中债子系统
            String isSubSystem = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_SUB_SYSTEM);
            // 查询科目余额
            List<String> subjectNoList = new ArrayList<String>();
            List<ChbTypeMappingEx>  subjectNoMappingList = chbTypeMappingExMapper
                    .selectByMappingcode(mappingCode);
            for(ChbTypeMappingEx mapping: subjectNoMappingList){
                // 获取源属性1：科目号
                subjectNoList.add(mapping.getSrcfield1());
            }
            param.put("subjectNoList", subjectNoList);
            AssetPositionService asetPositionService =  SpringContextUtil.getBean(AssetPositionService.class);
           
            if (DictCodeList.是否_中债_是.getValue().equals(isSubSystem)){
                assetPositionListAdd.addAll(asetPositionService.queryBalanceDateSubSystem(param));
            } else if (DictCodeList.是否_中债_否.getValue().equals(isSubSystem)){
                // 读取是否穿透开关
                param.put("isBtm", isBtm);
                assetPositionListAdd.addAll(asetPositionService.queryBalanceDate(param));
            } else {
                logger.error("系统参数中：SVR_IS_SUB_SYSTEM配置错误！");
                return 0;
            }
        } else {
        	String monthReport=SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_CHBMONTH_REPORT);
        	param.put("isBtm", isBtm);
        	param.put("monthReport", monthReport);
            // 查询资产基础指标
            template.select("com.joyintech.tams.svreport.position.dao.ChbAssetPositionMapper.selectAssetsBasisData", param,
                    new ResultHandler() {
                        @Override
                        public void handleResult(ResultContext resultContext) {
                            ChbAssetPosition pos = (ChbAssetPosition) resultContext.getResultObject();
                            assetPositionListAdd.add(pos);
                        }
                    });
        }
        // 处理数据
        if (CollectionUtils.isNotEmpty(assetPositionListAdd)){
            for (ChbAssetPosition position : assetPositionListAdd) {
                // 头寸主键
                position.setPosuuid(IDGenerator.generate(36));
                // 创建人
                position.setCreateUser(CommonUtil.getUserName());
                // 更新人
                position.setUpdateUser(CommonUtil.getUserName());
                // 机构代码
                position.setOrgCode(CommonUtil.getOrgCode());
                // 部门代码
                position.setDeptCode(CommonUtil.getDeptCode());
            }
        }
        // sqlsession批量入库方式
        SqlSession session  = template.getSqlSessionFactory().openSession(ExecutorType.BATCH,false);
        int count = 0;
        try {
            ChbAssetPositionMapper batchMapper = session.getMapper(ChbAssetPositionMapper.class);
            for (ChbAssetPosition position : assetPositionListAdd) {
                batchMapper.insert(position);
                count++;
                if (count % 1000 == 0) {
                    session.commit();
                    session.clearCache();
                }
            }
            session.commit();
            // 清理缓存，防止溢出
            session.clearCache();
        } finally {
            session.close();
            counts += count;
        }
        // 记录插入数量
        return counts;
    }


    /**
     * 功能:查询跑批日当天科目余额变动的最小跑批日
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年3月11日
     * @param batchDate 跑批日
     * @param prodList 产品代码
     * @param assetList 资产代码
     * @return 
     * List<ChbAssetInfo>
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    // 切换数据源
    @DataSource(DataSourceType.INTERF)
    public Date queryMintBalanceDate(Date batchDate,List<String> prodList,
            List<String> assetList){
        return chbAssetPositionMapper.selectMintBalanceDate(batchDate,prodList,assetList);
    }

    /**
     * 功能:查询跑批日当天科目余额变动的最小跑批日-中债子系统
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年3月11日
     * @param batchDate 跑批日
     * @param prodList 产品代码
     * @param assetList 资产代码
     * @return 
     * List<ChbAssetInfo>
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public Date queryMintBalanceDateSubSystem(Date batchDate,List<String> prodList,
            List<String> assetList){
        return chbAssetPositionMapper.selectMintBalanceDateSubSystem(batchDate,prodList,assetList);
    }

    /**
     * 功能:查询接入日当天的资产头寸-科目余额部分
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年3月11日
     * @param interfDate 跑批日
     * @prodList 产品代码
     * @return 
     * List<ChbAssetInfo>
     */
    @SuppressWarnings("rawtypes")
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    // 切换数据源
    @DataSource(DataSourceType.INTERF)
    public List<ChbAssetPosition> queryBalanceDate(Map<String, Object> param){
        List<ChbAssetPosition> assetPositionList = new ArrayList<ChbAssetPosition>();
        template.select("com.joyintech.tams.svreport.position.dao.ChbAssetPositionMapper.selectBalanceData",
                param,new ResultHandler() {
            @Override
            public void handleResult(ResultContext resultContext) {
                assetPositionList.add((ChbAssetPosition) resultContext.getResultObject());
            }
        });
        return assetPositionList;
    }

    /**
     * 功能:查询接入日当天的资产头寸-科目余额部分-中债子系统
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年3月11日
     * @param interfDate 跑批日
     * @prodList 产品代码
     * @return 
     * List<ChbAssetInfo>
     */
    @SuppressWarnings("rawtypes")
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public List<ChbAssetPosition> queryBalanceDateSubSystem(Map<String, Object> param){
        List<ChbAssetPosition> assetPositionList = new ArrayList<ChbAssetPosition>();
        template.select("com.joyintech.tams.svreport.position.dao.ChbAssetPositionMapper.selectBalanceDataSubSystem",
                param,new ResultHandler() {
            @Override
            public void handleResult(ResultContext resultContext) {
                assetPositionList.add((ChbAssetPosition) resultContext.getResultObject());
            }
        });
        return assetPositionList;
    }
    
    /**
     * 功能:移动平均法生成资产头寸
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年7月2日
     * @param batchDate
     * @param mappara 
     * void
     */
    private void genAssetPositionAverage(Date batchDate,Map<String,List<String>> mappara){
    	 List<String> prodids;
    	 List<String> assetcodes;
    	if(mappara==null){
    		prodids=null;
    		assetcodes=null;
    	}else{
    		prodids=mappara.get(InterfConstants.SVR_PRODLIST);
    		if (prodids!=null&&prodids.size() > 500){
    			prodids = null;
    		}
    		assetcodes=mappara.get(InterfConstants.SVR_ASSETLIST);
    		if (assetcodes!=null&&assetcodes.size() > 500){
    			assetcodes = null;
    		}
    	}
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date bdate=new Date();
        Long bt=bdate.getTime();
        
        logger.info("资产头寸轮循批处理启动:"+sdf.format(bdate));
        
        //查询最小跑批日
        Date minSettdate = chbAssetPositionMapper.queryMinSettdate(batchDate, prodids,assetcodes);
        for (Date d = minSettdate; d.compareTo(batchDate) <= 0;) {
            genAssetPositionAvg(d,prodids,assetcodes);
            d = DateUtil.addDay(d, 1);
        }
        
        Date edate=new Date();
        Long et=edate.getTime();
        Long time=(et-bt)/1000;
        logger.info("资产头寸轮循批处理结束:"+sdf.format(edate)+"耗时"+time+"秒");
    }
    
    /**
     * 每日生成资产端头寸
     * 维度：同一个资产+投资类型+产品+通道+币种
     * @param cdate 跑批日 prodids 产品集合 assetcodes 资产集合
     * @see
     * @since 1.0
     */
    @SuppressWarnings("rawtypes")
    private int genAssetPositionAvg(Date cdate, List<String> prodids, List<String> assetcodes) {
        Map<String, BigDecimal> buyamtMap = new HashMap<String, BigDecimal>();
        Map<String, BigDecimal> buyqtyMap = new HashMap<String, BigDecimal>();
        //是否计算剩余现金 01 计算 02 从科目余额取
        String iscal= SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ASSETPOSITION_CASHCALWAY);
        // 批量插入轮循条数
        int singalCount = reportSelectOrDeleteCount;
        
        List<ChbAssetPosition> listDealP = new ArrayList<ChbAssetPosition>();
        List<ChbAssetPosition> listDealS = new ArrayList<ChbAssetPosition>();
        //产品剩余现金当日发生
        List<ChbAssetPosition> prdCashList = new ArrayList<ChbAssetPosition>();
        //产品剩余现金昨日头寸
        List<ChbAssetPosition> prdCashPositionList = new ArrayList<ChbAssetPosition>();

        Map<String, Object> param = new HashMap<String, Object>();
        if (prodids == null || prodids.isEmpty()) {
            prodids = null;
        }
        param.put("cdate", cdate);
        param.put("prodids", prodids);
        param.put("assetcodes", assetcodes);
        
        int counts = 0;
        
        if (prodids == null || prodids.isEmpty()) {  //不指定需要处理的产品列表
         // 查询今日的买入交易
            template.select(
                "com.joyintech.tams.svreport.position.dao.ChbAssetPositionMapper.selectInvestTradeBuy",
                param, new ResultHandler() {

                    @Override
                    public void handleResult(ResultContext resultContext) {
                        ChbAssetPosition pos = (ChbAssetPosition)resultContext.getResultObject();
                        listDealP.add(pos);
                    }
                });

            // 查询今日的卖出交易
            template.select(
                "com.joyintech.tams.svreport.position.dao.ChbAssetPositionMapper.selectInvestTradeSale",
                param, new ResultHandler() {

                    @Override
                    public void handleResult(ResultContext resultContext) {
                        ChbAssetPosition pos = (ChbAssetPosition)resultContext.getResultObject();
                        listDealS.add(pos);
                    }
                });
           if("01".equals(iscal)){ 
            // 查询今日产品端剩余现金
            template.select(
                    "com.joyintech.tams.svreport.position.dao.ChbAssetPositionMapper.selectPrdCashByModel",
                    param, new ResultHandler() {

                        @Override
                        public void handleResult(ResultContext resultContext) {
                            ChbAssetPosition pos = (ChbAssetPosition)resultContext.getResultObject();
                            prdCashList.add(pos);
                        }
                    });
         // 查询今日资产端剩余现金
            template.select(
                    "com.joyintech.tams.svreport.position.dao.ChbAssetPositionMapper.selectAssetCashByModel",
                    param, new ResultHandler() {

                        @Override
                        public void handleResult(ResultContext resultContext) {
                            ChbAssetPosition pos = (ChbAssetPosition)resultContext.getResultObject();
                            prdCashList.add(pos);
                        }
                    });
            }
            // 先删除今日头寸 重复跑批
            chbAssetPositionMapper.deleteByDate(param);
            
            // 将昨日头寸复制一份，变为今日头寸
            param.put("cdate", DateUtil.addDay(cdate, -1));
            counts += chbAssetPositionMapper.insertYsdPosition(param);
            if("01".equals(iscal)){
            // 查询昨日头寸剩余现金
            param.put("cdate", cdate);
            template.select(
            		"com.joyintech.tams.svreport.position.dao.ChbAssetPositionMapper.selectAssetPositionCashByModel",
            		param, new ResultHandler() {
            			
            			@Override
            			public void handleResult(ResultContext resultContext) {
            				ChbAssetPosition pos = (ChbAssetPosition)resultContext.getResultObject();
            				prdCashPositionList.add(pos);
            			}
            		});
            }
            
        } else {  //指定了产品列表
            int totalCount = prodids.size();
            int loopCount = (int)Math.ceil((double)totalCount/(double)singalCount);
             for (int i = 0; i < loopCount; i++ ) {
                param.put("prodids", prodids.subList(i*singalCount, (i+1)*singalCount>totalCount?totalCount:(i+1)*singalCount));
                param.put("cdate", cdate);
                template.select(
                    "com.joyintech.tams.svreport.position.dao.ChbAssetPositionMapper.selectInvestTradeBuy",
                    param, new ResultHandler() {

                        @Override
                        public void handleResult(ResultContext resultContext) {
                            ChbAssetPosition pos = (ChbAssetPosition)resultContext.getResultObject();
                            listDealP.add(pos);
                        }
                    });

                template.select(
                    "com.joyintech.tams.svreport.position.dao.ChbAssetPositionMapper.selectInvestTradeSale",
                    param, new ResultHandler() {

                        @Override
                        public void handleResult(ResultContext resultContext) {
                            ChbAssetPosition pos = (ChbAssetPosition)resultContext.getResultObject();
                            listDealS.add(pos);
                        }
                    });
                if("01".equals(iscal)){
                // 查询今日产品端剩余现金
                template.select(
                        "com.joyintech.tams.svreport.position.dao.ChbAssetPositionMapper.selectPrdCashByModel",
                        param, new ResultHandler() {

                            @Override
                            public void handleResult(ResultContext resultContext) {
                                ChbAssetPosition pos = (ChbAssetPosition)resultContext.getResultObject();
                                prdCashList.add(pos);
                            }
                        });
             // 查询今日资产端剩余现金
                template.select(
                        "com.joyintech.tams.svreport.position.dao.ChbAssetPositionMapper.selectAssetCashByModel",
                        param, new ResultHandler() {

                            @Override
                            public void handleResult(ResultContext resultContext) {
                                ChbAssetPosition pos = (ChbAssetPosition)resultContext.getResultObject();
                                prdCashList.add(pos);
                            }
                        });
                }
                // 先删除今日头寸 重复跑批
                chbAssetPositionMapper.deleteByDate(param);

                // 将昨日头寸复制一份，变为今日头寸

                param.put("cdate", DateUtil.addDay(cdate, -1));
                param.put("iscal", iscal);
                counts += chbAssetPositionMapper.insertYsdPosition(param);
              if("01".equals(iscal)){
             // 查询昨日头寸剩余现金
                param.put("cdate", cdate);
                template.select(
                		"com.joyintech.tams.svreport.position.dao.ChbAssetPositionMapper.selectAssetPositionCashByModel",
                		param, new ResultHandler() {
                			
                			@Override
                			public void handleResult(ResultContext resultContext) {
                				ChbAssetPosition pos = (ChbAssetPosition)resultContext.getResultObject();
                				prdCashPositionList.add(pos);
                			}
                		});
              }
            }
            
        }
        
        List<ChbAssetPosition> tdyAddPosList = new ArrayList<ChbAssetPosition>();
        List<ChbAssetPosition> tdyUpdatePosList = new ArrayList<ChbAssetPosition>();
        //查询所有当日头寸并以AssetCode，InvestType，ProdId，ChlId，TradeType，Isundertrade作为key组装成map
        ChbAssetPosition para = new ChbAssetPosition();
        para.setCdate(cdate);
        List<ChbAssetPosition> alllistTdyPos = chbAssetPositionMapper.select(para);
        Map<String,List<ChbAssetPosition>> allPosMap=alllistTdyPos.stream()
          		.collect(
                        JoyinCollectors.groupingBy(
                            x -> x.getAssetCode() 
                                 + GlobalConstant.VERTICAL_LINE + x.getInvestType()
                                 + GlobalConstant.VERTICAL_LINE + x.getProdId()
                                 + GlobalConstant.VERTICAL_LINE + x.getChlId()
                                 + GlobalConstant.VERTICAL_LINE + x.getTradeType()
                                 + GlobalConstant.VERTICAL_LINE + x.getIsundertrade()));
        for (ChbAssetPosition deal : listDealP) {
            // 如果投资品种是债券或ABS
            if (deal.getInvestVariety().equals("01") || deal.getInvestVariety().equals("02")) {
                if (deal.getFacevalue() != null) {
                    BigDecimal bl = deal.getFacevalue().divide(new BigDecimal(100), 4,
                        RoundingMode.HALF_UP);
                    deal.setDealqty(deal.getDealqty().multiply(bl));
                }
            }
            String key = deal.getAssetCode() + "-" + deal.getInvestType() + "-" + deal.getProdId()
                         + "-" + deal.getTradeType() + "-" + deal.getIsundertrade();
            buyamtMap.put(key, deal.getDealamt());
            buyqtyMap.put(key, deal.getDealqty());
            // 查询今日头寸
//            ChbAssetPosition para = new ChbAssetPosition();
//            para.setCdate(cdate);
//            para.setAssetCode(deal.getAssetCode());
//            para.setInvestType(deal.getInvestType());
//            para.setProdId(deal.getProdId());
//            para.setChlId(deal.getChlId());
//            para.setTradeType(deal.getTradeType());
//            para.setIsundertrade(deal.getIsundertrade());
            String keyp=deal.getAssetCode() 
                    + GlobalConstant.VERTICAL_LINE + deal.getInvestType()
                    + GlobalConstant.VERTICAL_LINE + deal.getProdId()
                    + GlobalConstant.VERTICAL_LINE + deal.getChlId()
                    + GlobalConstant.VERTICAL_LINE + deal.getTradeType()
                    + GlobalConstant.VERTICAL_LINE + deal.getIsundertrade();
            List<ChbAssetPosition> listTdyPos =allPosMap.get(keyp);
//            List<ChbAssetPosition> listTdyPos = chbAssetPositionMapper.select(para);
            ChbAssetPosition tdyPos=null;
            if (listTdyPos != null && !listTdyPos.isEmpty()) {
                tdyPos = listTdyPos.get(0);
                tdyPos.setDealamt(deal.getDealamt());
                tdyPos.setDealqty(deal.getDealqty());

                // 剩余本金=昨日本金*今日剩余份额/昨日份额
                tdyPos.setAccuamt(tdyPos.getAccuamt().add(deal.getDealamt()));
                // 剩余份额=昨日累计+今日交易份额
                tdyPos.setAccuqty(tdyPos.getAccuqty().add(deal.getDealqty()));
                // 创建人
                tdyPos.setCreateUser(CommonUtil.getUserName());
                // 更新人
                tdyPos.setUpdateUser(CommonUtil.getUserName());
                tdyUpdatePosList.add(tdyPos);
            }
            else {
                tdyPos = deal;
                tdyPos.setAccuqty(deal.getDealqty());
                tdyPos.setAccuamt(deal.getDealamt());
                tdyPos.setPosuuid(IDGenerator.generate(36));
                // 创建人
                tdyPos.setCreateUser(CommonUtil.getUserName());
                // 更新人
                tdyPos.setUpdateUser(CommonUtil.getUserName());
                tdyAddPosList.add(tdyPos);
                counts++;
            }

        }
        
        SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH,false);
        try {
            ChbAssetPositionMapper batchMapper = session.getMapper(ChbAssetPositionMapper.class);
            int count = 0;
            for (ChbAssetPosition position : tdyAddPosList) {
                batchMapper.insert(position);
                count++;
        		if(count % 1000==0){
            	   session.commit();
            	   session.clearCache();
        		}
            }
            for (ChbAssetPosition position : tdyUpdatePosList) {
                batchMapper.updateByPrimaryKeySelective(position);
                count++;
        		if(count % 1000==0){
            	   session.commit();
            	   session.clearCache();
        		}
            }
            session.commit();
            // 清理缓存，防止溢出
            session.clearCache();
        }
        finally {
            session.close();
        }
        List<ChbAssetPosition> alllistTdyPosAfterBuy = chbAssetPositionMapper.select(para);
        Map<String,List<ChbAssetPosition>> allPosMapAfterBuy=alllistTdyPosAfterBuy.stream()
          		.collect(
                        JoyinCollectors.groupingBy(
                            x -> x.getAssetCode() 
                                 + GlobalConstant.VERTICAL_LINE + x.getInvestType()
                                 + GlobalConstant.VERTICAL_LINE + x.getProdId()
                                 + GlobalConstant.VERTICAL_LINE + x.getChlId()
                                 + GlobalConstant.VERTICAL_LINE + x.getTradeType()
                                 + GlobalConstant.VERTICAL_LINE + x.getIsundertrade()));
        List<ChbAssetPosition> tdyUpdateSPosList = new ArrayList<ChbAssetPosition>();
        for (ChbAssetPosition deal : listDealS) {
            // 如果投资品种是债券或ABS
            if (deal.getInvestVariety().equals("01") || deal.getInvestVariety().equals("02")) {
                // 不是中间还本
                if (deal.getFacevalue() != null && !deal.getCurassettype().equals("11")) {
                    BigDecimal bl = deal.getFacevalue().divide(new BigDecimal(100), 4,
                        RoundingMode.HALF_UP);
                    deal.setDealqty(deal.getDealqty().multiply(bl));
                }
            }
            String key = deal.getAssetCode() + "-" + deal.getInvestType() + "-" + deal.getProdId()
                         + "-" + deal.getTradeType() + "-" + deal.getIsundertrade();
            // 查询今日头寸
//            ChbAssetPosition para = new ChbAssetPosition();
//            para.setCdate(cdate);            
//            para.setAssetCode(deal.getAssetCode());
//            para.setInvestType(deal.getInvestType());
//            para.setProdId(deal.getProdId());
//            para.setChlId(deal.getChlId());
//            para.setTradeType(deal.getTradeType());
//            para.setIsundertrade(deal.getIsundertrade());
            String keyp=deal.getAssetCode() 
                    + GlobalConstant.VERTICAL_LINE + deal.getInvestType()
                    + GlobalConstant.VERTICAL_LINE + deal.getProdId()
                    + GlobalConstant.VERTICAL_LINE + deal.getChlId()
                    + GlobalConstant.VERTICAL_LINE + deal.getTradeType()
                    + GlobalConstant.VERTICAL_LINE + deal.getIsundertrade();
            List<ChbAssetPosition> listTdyPos =allPosMapAfterBuy.get(keyp);
//            List<ChbAssetPosition> listTdyPos = chbAssetPositionMapper.select(para);
            ChbAssetPosition tdyPos=null;
            if (listTdyPos != null && !listTdyPos.isEmpty()) {
                tdyPos = listTdyPos.get(0);
                BigDecimal buyamt = buyamtMap.get(key);
                BigDecimal buyqty = buyqtyMap.get(key);
                if (buyamt != null) {
                    tdyPos.setDealamt(deal.getDealamt().add(buyamt));
                    tdyPos.setDealqty(deal.getDealqty().add(buyqty));
                }
                else {
                    tdyPos.setDealamt(deal.getDealamt());
                    tdyPos.setDealqty(deal.getDealqty());
                }
                // 剩余本金=昨日本金*今日剩余份额/昨日份额
                tdyPos.setAccuamt(
                    tdyPos.getAccuamt().multiply(tdyPos.getAccuqty().add(deal.getDealqty()).divide(
                        tdyPos.getAccuqty(), 12, RoundingMode.HALF_UP)));
                tdyPos.setAccuamt(tdyPos.getAccuamt().setScale(4, RoundingMode.HALF_UP));
                // 剩余份额=昨日累计+今日交易份额
                tdyPos.setAccuqty(tdyPos.getAccuqty().add(deal.getDealqty()));
                tdyUpdateSPosList.add(tdyPos);
            }
        }
        session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        try {
        	int count = 0;
            ChbAssetPositionMapper batchMapper = session.getMapper(ChbAssetPositionMapper.class);
            for (ChbAssetPosition position : tdyUpdateSPosList) {
                batchMapper.updateByPrimaryKeySelective(position);
                count++;
        		if(count % 1000==0){
            	   session.commit();
            	   session.clearCache();
        		}
            }
            session.commit();
            // 清理缓存，防止溢出
            session.clearCache();
        }
        finally {
            session.close();
        }
        if("01".equals(iscal)){
        //处理剩余现金
        Map<String,ChbAssetPosition>  cashMap=new HashMap<String,ChbAssetPosition>();
        //先将昨日头寸放入map
        for(ChbAssetPosition single:prdCashPositionList){
        	String key=single.getProdId()+ StringUtil.SPACE +single.getCcy();
        		cashMap.put(key, single);
        	
        }
        for(ChbAssetPosition single:prdCashList){
        	String key=single.getProdId()+ StringUtil.SPACE +single.getCcy();
        	if(cashMap.containsKey(key)){
        		ChbAssetPosition valueMap=cashMap.get(key);	
        		valueMap.setAccuamt(valueMap.getAccuamt().add(single.getDealamt()));
        		valueMap.setAccuqty(valueMap.getAccuamt());
        	}else{
        		single.setAssetCode("REMAININGCASH");
        		single.setTradeType("01");//直投
        		single.setAccuamt(single.getDealamt());
        		single.setAccuqty(single.getAccuamt());
        		single.setCdate(cdate);
        		 // 创建人
        		single.setCreateUser(CommonUtil.getUserName());
                // 更新人
        		single.setUpdateUser(CommonUtil.getUserName());
        		cashMap.put(key, single);
        	}
        }
        List<ChbAssetPosition> cashinsertList= new ArrayList<ChbAssetPosition>();
        List<ChbAssetPosition> cashupdateList= new ArrayList<ChbAssetPosition>();
        for(String key:cashMap.keySet()){
        	ChbAssetPosition cashmodel=	cashMap.get(key);
        	if(cashmodel.getPosuuid()==null){
        		cashmodel.setPosuuid(IDGenerator.generate(36));
        		cashinsertList.add(cashmodel);
        	}else{
        		cashupdateList.add(cashmodel);
        	}
        	
        }
         session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH,false);
        try {
            ChbAssetPositionMapper batchMapper = session.getMapper(ChbAssetPositionMapper.class);
            int count = 0;
            for (ChbAssetPosition position : cashinsertList) {
                batchMapper.insert(position);
                count++;
        		if(count % 1000==0){
            	   session.commit();
            	   session.clearCache();
        		}
            }
            for (ChbAssetPosition position : cashupdateList) {
                batchMapper.updateByPrimaryKeySelective(position);
                count++;
        		if(count % 1000==0){
            	   session.commit();
            	   session.clearCache();
        		}
            }
            session.commit();
            // 清理缓存，防止溢出
            session.clearCache();
        }
        finally {
            session.close();
        }
        }else{
       	 genAssetPositionMP(cdate, prodids, assetcodes,true,InterfMappingConstants.MAPPINGCODE_CASH_SUBJECTNO_AVG);
       }
        return counts;
    }

    /**
     * 功能:数据变更接口统一调度入口
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年5月9日
     * @param executeDate
     * @param prodList
     * @param assetList
     * @throws BusinessException 
     * @see com.joyintech.tams.batch.report.autosendcb.common.service.SvrCBBaseService#execute(java.util.Date, java.util.List, java.util.List)
     */
    @Override
    public void execute(Date executeDate, List<String> prodList, List<String> assetList,Map<String,List<String>> param) throws BusinessException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        logger.info("生成资产头寸："+sdf.format(executeDate)+"开始");
        // 设置批处理bean
        setBatchBeanName(BatchBeanName.资产头寸批处理);
        // 批处理开始
        logInfoBatchStart();
        genAssetPosition(executeDate,param);
        // 批处理开始
        logInfoBatchEnd();
        logger.info("生成资产头寸："+sdf.format(executeDate)+"结束");
    }

}
