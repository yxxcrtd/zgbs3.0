package com.joyintech.tams.component.business.trade.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.joyintech.tams.invest.common.component.base.trade.BaseTradeProductDealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.EtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.EtrFinishStatusMapper;
import com.joyintech.tams.common.base.dao.FinTradeProductMapper;
import com.joyintech.tams.common.base.dao.InterfOpicsTradRepoMapper;
import com.joyintech.tams.common.base.dao.InterfTradSecdealMapper;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.dao.PtlTradeMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealAddPreMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealPreMapper;
import com.joyintech.tams.common.base.dao.TrdTradeFeeDetailMapper;
import com.joyintech.tams.common.base.dao.WflPaTrdFinDealTotalMapper;
import com.joyintech.tams.common.base.dao.WflPaTrdProductDealMapper;
import com.joyintech.tams.common.base.dao.WflStlPayOrdersMapper;
import com.joyintech.tams.common.base.dao.WflTrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.WflTrdProductDealAddMapper;
import com.joyintech.tams.common.base.dao.WflTrdProductDealMapper;
import com.joyintech.tams.common.base.dao.WflTrdProductDealOthMapper;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.EtrFinishStatus;
import com.joyintech.tams.common.base.entity.EtrFinishStatusKey;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.common.base.entity.FinTradeProductKey;
import com.joyintech.tams.common.base.entity.InterfOpicsTradRepo;
import com.joyintech.tams.common.base.entity.InterfTradSecdeal;
import com.joyintech.tams.common.base.entity.PaTrdProductDeal;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.TrdProductDealAddPre;
import com.joyintech.tams.common.base.entity.TrdProductDealPre;
import com.joyintech.tams.common.base.entity.TrdProductDealPreKey;
import com.joyintech.tams.common.base.entity.WflStlPayOrders;
import com.joyintech.tams.common.base.entity.WflTrdProductDealAdd;
import com.joyintech.tams.component.business.settle.BussSettleOrderCommService;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.CommonResult;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.component.business.common.BaseAssetCashPositionCommService;
import com.joyintech.tams.invest.common.component.business.common.dto.PortfolioCashPositionsFlowInputDto;
import com.joyintech.tams.invest.common.component.business.common.dto.PortfolioPositionsFlowInputDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealRevokeDto;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlPositionHoldDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussTrdTransferOrderBase;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.dto.trade.entrust.EtrFinishStatusDto;
import com.joyintech.tams.invest.common.service.MapperDelegateService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dao.BaseAssetCashPositionCommMapper;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: tams-invest-parent
 * @Package com.joyintech.component.business.period.service.impl
 * @Description: 金融产品交易共通 实现类
 * @date Date : 2018年09月27日 15:31
 */
@Service
public class BaseTradeProductDealCommServiceImpl extends BaseService implements BaseTradeProductDealCommService {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 业务共通组件.资产头寸现金持仓共通组件
     */
    @Autowired
    private BaseAssetCashPositionCommService assetPositionsCashCommServiceImpl;

    @Autowired
    private BaseAssetCashPositionCommMapper baseAssetCashPositionCommMapper;

    @Autowired(required = false)
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;

    @Autowired(required = false)
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;
    /**
     * 持仓现金执行共通
     */
    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;
    @Autowired
    private InterfTradSecdealMapper interfTradSecdealMapper;
    @Autowired
    private InterfOpicsTradRepoMapper interfOpicsTradRepoMapper;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealOthMapper wflTrdProductDealOthMapper;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;
	
    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;
    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;
    @Autowired
    private EtrFinishStatusMapper etrFinishStatusMapper;

    /**
     * BussCommonMapperDelegateService
     */
    @Autowired
    private MapperDelegateService mapperDelegateService;
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;
    @Autowired
    private TrdProductDealPreMapper trdProductDealPreMapper;
    @Autowired
    private TrdProductDealAddPreMapper trdProductDealAddPreMapper;
    /**
     * 清算指令共通Service
     */
    @Autowired(required = false)
    private BussSettleOrderCommService bussSettleOrderCommService;
    /**
     * 划款指令业务组件service
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    @Autowired
    private BaseTradeProductDealService baseTradeProductDealService;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    private static final List<String> 认购确认_申购确认_赎回确认_除权登记_红利再投 = Arrays.asList(
            DictCodeList.交易方向_认购确认.getValue(), DictCodeList.交易方向_申购确认.getValue(),
            DictCodeList.交易方向_赎回确认.getValue(), DictCodeList.交易方向_除权登记.getValue(), DictCodeList.交易方向_红利再投.getValue());
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CommonResult execute(FinProductTradeDto finProductTradeDto) {
        logger.debug("【金融产品交易共通业务逻辑处理开始】,finProductTradeDto:{}", JSON.toJSONString(finProductTradeDto));
        if (finProductTradeDto == null) {
            return new CommonResult(CommonResult.FAILD, "没有数据需要处理");
        }
        //处理CSTP下行数据
        if(finProductTradeDto.getBaseTradeProductDealDtos() != null && !finProductTradeDto.getBaseTradeProductDealDtos().isEmpty()
                && finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal() != null &&
                StringUtil.isNotBlank(finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getfTradeId())){
            if(StringUtil.equals(finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getBusiType(),DictCodeList.共通业务类型_债券类资产交易.getValue())){
                updateInterfForZhaiJuan(finProductTradeDto);
            }else if(StringUtil.equals(finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getBusiType(),DictCodeList.共通业务类型_质押式回购交易.getValue())){
                updateInterfForZhiYaShi(finProductTradeDto);
            }
        }
        String workflowStatus = finProductTradeDto.getWorkflowStatus();
        if (DictCodeListExt1.交易流程状态_流程中.getValue().equals(workflowStatus)) {
            return deal流程中(finProductTradeDto);
        }
        else if (DictCodeListExt1.交易流程状态_有效.getValue().equals(workflowStatus)) {
            return deal有效(finProductTradeDto);
        }
        else if (DictCodeListExt1.交易流程状态_撤销.getValue().equals(workflowStatus)) {
            return deal撤销(finProductTradeDto);
        }
        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    private CommonResult deal撤销(FinProductTradeDto finProductTradeDto) {
        if (finProductTradeDto.getBaseTradeProductDealDtos() == null || finProductTradeDto.getBaseTradeProductDealDtos().isEmpty()) {
            return new CommonResult(CommonResult.FAILD, "没有数据需要处理");
        }
        if (!finProductTradeDto.isNeedSum()) {
            // 2.调用基础组件.【金融产品交易组件.撤销交易金融产品交易信息】，撤销金融产品交易信息
            List<BaseTradeProductDealRevokeDto> baseTradeProductDealRevokeDtos = new ArrayList<>();
            for (BaseTradeProductDealDto baseTradeProductDealDto : finProductTradeDto.getBaseTradeProductDealDtos()) {
                BaseTradeProductDealRevokeDto dto = new BaseTradeProductDealRevokeDto();
                dto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
                dto.setTrdProductDealAdd(baseTradeProductDealDto.getTrdProductDealAdd());
                dto.setStlPayOrdersList(baseTradeProductDealDto.getStlPayOrdersList());
                dto.setTrdFinProductBarterList(baseTradeProductDealDto.getTrdFinProductBarterList());
                dto.setTrdTradeFeeDetailList(baseTradeProductDealDto.getTrdTradeFeeDetailList());
                dto.setMstFileInputParameter(baseTradeProductDealDto.getMstFileInputParameter());
                dto.setMstFileOutputParameter(baseTradeProductDealDto.getMstFileOutputParameter());
                dto.setCancelRemark(baseTradeProductDealDto.getTrdProductDeal().getCancelRemark());
                dto.setpTradeId(baseTradeProductDealDto.getTrdProductDeal().getTradeId());
                dto.setActPortfolioId(baseTradeProductDealDto.getActPortfolioId());
                baseTradeProductDealRevokeDtos.add(dto);
            }
            Boolean result = restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/revokeTradeInfo",
                baseTradeProductDealRevokeDtos, Boolean.class);
            if (!result) {
                return new CommonResult(CommonResult.FAILD, "处理失败");
            }
        }

        /*----------------------zhangliang----add------2019/02/23-------------start--------------------*/
        BaseTradeProductDealDto baseTradeDto = finProductTradeDto.getBaseTradeProductDealDtos().get(0);
        if (baseTradeDto.getTrdProductDeal() != null
        			&& StringUtil.equals(baseTradeDto.getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_买断式回购交易.getValue())){
        	if (baseTradeDto.getSecAmount() != null){
        		baseTradeDto.getTrdProductDeal().setShareAmt(baseTradeDto.getSecAmount());
        	}
        }
        /*----------------------zhangliang----add------2019/02/23-------------end--------------------*/
        /*----------------------TISHKBS-1003 资产配置分析：现金取值错误（原因：计划类净值型交易确认撤销时 PTL_CASH_POSITION 表金额未更新） add by hpp start--------------------*/
        Map<String, Object> vDateNato = baseAssetCashPositionCommMapper.selectVDateNato(baseTradeDto.getTrdProductDeal().getFinprodId());
        String profitType = vDateNato == null ? null : (String)vDateNato.get("PROFIT_TYPE");//获取收益类型
        List<StlPayOrdersDto> globalStlOrderList=new ArrayList<StlPayOrdersDto>();
        if(认购确认_申购确认_赎回确认_除权登记_红利再投.contains(baseTradeDto.getTrdProductDeal().getPs())
                && DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(baseTradeDto.getTrdProductDeal().getFinprodType2())||
                认购确认_申购确认_赎回确认_除权登记_红利再投.contains(baseTradeDto.getTrdProductDeal().getPs())&&
                        DictCodeList.收益类型_净值型.getValue().equals(profitType)&& DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(baseTradeDto.getTrdProductDeal().getFinprodType2())||
                认购确认_申购确认_赎回确认_除权登记_红利再投.contains(baseTradeDto.getTrdProductDeal().getPs())&&
                        DictCodeList.收益类型_货币型.getValue().equals(profitType)&& DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(baseTradeDto.getTrdProductDeal().getFinprodType2())){
            //查询划款指令详细信息
            /*----------------------TISHKBS-1583 计划类净值型存续期交易撤销报错,前置交易编号为空时取交易编号去掉R modify by msk start--------------------*/
            List<BussTrdTransferOrderBase> bttob;
            if(StringUtil.isNotEmpty(baseTradeDto.getTrdProductDeal().getbTradeId())){
                bttob = bussSettleOrderCommService.selectOrdersByBizCode(baseTradeDto.getTrdProductDeal().getbTradeId());
            }else{
                bttob = bussSettleOrderCommService.selectOrdersByBizCode(baseTradeDto.getTrdProductDeal().getTradeId().substring(0,(baseTradeDto.getTrdProductDeal().getTradeId().length()-1)));
            }
            //撤销基金除权登记时，根据前置交易编号查询不到划款指令特殊处理
            if((bttob == null || bttob.isEmpty())
                && StringUtil.isNotBlank(baseTradeDto.getTrdProductDeal().getbTradeId())){
                bttob = bussSettleOrderCommService.selectOrdersByBizCodeByBTradeId(baseTradeDto.getTrdProductDeal().getbTradeId());
            }
            /*----------------------TISHKBS-1583 计划类净值型存续期交易撤销报错,前置交易编号为空时取交易编号去掉R modify by msk end--------------------*/
            List<BussTrdTransferOrderDto> reslist = new ArrayList<BussTrdTransferOrderDto>();
            if (bttob != null && bttob.size() > 0) {
                List<BussTrdTransferOrderDto> bttodList = new ArrayList<>();
                BussTrdTransferOrderDto bttod = new BussTrdTransferOrderDto();
                BeanUtil.copyPropertiesIgnoreNull(bttod, bttob.get(0));
                bttodList.add(bttod);
                reslist = bussTrdTransferOrderService.save(bttodList);
                reslist.get(0).setIsTotalTrade(DictCodeList.是否_否.getValue());

            }
            for (BussTrdTransferOrderDto bttod : reslist) {
                globalStlOrderList.add(bttod);
            }

            baseTradeDto.setStlPayOrdersList(globalStlOrderList);
        }
        /*----------------------计划类净值型交易确认撤销时 PTL_CASH_POSITION 表金额未更新 add by hpp end--------------------*/
        
        // 3.调用【组合可用持仓与实际持仓查询更新组件】，写入类型为[实际]的组合持仓流水
        if (!insertPosition(finProductTradeDto)) {
            return new CommonResult(CommonResult.FAILD, "处理失败");
        }
        // 调用基础组件.【投资委托组件.取消完成委托】，撤销委托完成状况信息(投资委托场合才执行此步)
        if(!finProductTradeDto.isNeedHandleFinishEntrust()){
            finishEntrust(finProductTradeDto.getBaseTradeProductDealDtos(), true);
        }

        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    private CommonResult deal有效(FinProductTradeDto finProductTradeDto) {
        if (finProductTradeDto.getBaseTradeProductDealDtos() == null || finProductTradeDto.getBaseTradeProductDealDtos().isEmpty()) {
            return new CommonResult(CommonResult.FAILD, "没有数据需要处理");
        }

        /*----------------------裴家俊-----add------2018/11/23-------------start--------------*/
        BaseTradeProductDealDto baseTradeDto=finProductTradeDto.getBaseTradeProductDealDtos().get(0);
        Map<String, Object> vDateNato = baseAssetCashPositionCommMapper.selectVDateNato(baseTradeDto.getTrdProductDeal().getFinprodId());
        String profitType = vDateNato == null ? null : (String)vDateNato.get("PROFIT_TYPE");//获取收益类型
        List<StlPayOrdersDto> globalStlOrderList=new ArrayList<StlPayOrdersDto>();
        if(认购确认_申购确认_赎回确认_除权登记_红利再投.contains(baseTradeDto.getTrdProductDeal().getPs())
        		&& DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(baseTradeDto.getTrdProductDeal().getFinprodType2())||
        		认购确认_申购确认_赎回确认_除权登记_红利再投.contains(baseTradeDto.getTrdProductDeal().getPs())&&
        		DictCodeList.收益类型_净值型.getValue().equals(profitType)&& DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(baseTradeDto.getTrdProductDeal().getFinprodType2())||
        		认购确认_申购确认_赎回确认_除权登记_红利再投.contains(baseTradeDto.getTrdProductDeal().getPs())&&
                DictCodeList.收益类型_货币型.getValue().equals(profitType)&& DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(baseTradeDto.getTrdProductDeal().getFinprodType2())
                // add by etliu 20210421 权益类资产相关代码合并 start
                || 认购确认_申购确认_赎回确认_除权登记_红利再投.contains(baseTradeDto.getTrdProductDeal().getPs())&&
                        DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue().equals(baseTradeDto.getTrdProductDeal().getFinprodType2())
                // add by etliu 20210421 权益类资产相关代码合并 end
        ){
        			globalStlOrderList=baseTradeDto.getStlPayOrdersList();
        			baseTradeDto.setStlPayOrdersList(new ArrayList<StlPayOrdersDto>());
        }
        /*-------------------裴家俊------add------2018/11/23-------------end--------------------*/
        if (!finProductTradeDto.isNeedSum()) {
        	//update by wangpeng  20191225 通道费改造 项目类，计划类利率 不需要入库通道信息
            String chlModelType = CacheUtil.getSysParam("CHL_MODEL_TYPE");
            String chlid = finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getChlId();
        	if((StringUtil.equals(finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_项目类资产交易.getValue())
        			||StringUtil.equals(finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue())
        			)&&StringUtil.isNotEmpty(finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getChlId())){
                if (StringUtil.isNotEmpty(chlModelType) && chlModelType.equals("01")) {
                    finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().setChlId(null);
                }
        	}else if(StringUtil.equals(finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_存款类资产交易.getValue())){
        		//01 借款利率 不需要处理通道信息,  否则置空
            	if(!StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")){
            		finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().setChlId(null);
            	}
        	}
            // 2.调用基础组件.【金融产品交易组件.新建金融产品交易信息】,写入金融产品交易信息
            Boolean result = restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertTradeInfo",
                    finProductTradeDto.getBaseTradeProductDealDtos(), Boolean.class);
            finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().setChlId(chlid);
          //update by wangpeng  20191225 通道费改造 项目类，计划类净值 不需要入库通道信息
        }
        /*----------------------裴家俊----add------2018/11/23-------------start--------------------*/
        if(认购确认_申购确认_赎回确认_除权登记_红利再投.contains(baseTradeDto.getTrdProductDeal().getPs())
        		&& DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(baseTradeDto.getTrdProductDeal().getFinprodType2())||
        		认购确认_申购确认_赎回确认_除权登记_红利再投.contains(baseTradeDto.getTrdProductDeal().getPs())&&
        		DictCodeList.收益类型_净值型.getValue().equals(profitType)&& DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(baseTradeDto.getTrdProductDeal().getFinprodType2())||
        		认购确认_申购确认_赎回确认_除权登记_红利再投.contains(baseTradeDto.getTrdProductDeal().getPs())&&
                DictCodeList.收益类型_货币型.getValue().equals(profitType)&& DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(baseTradeDto.getTrdProductDeal().getFinprodType2())
                // add by etliu 20210421 权益类资产相关代码合并 start
                || 认购确认_申购确认_赎回确认_除权登记_红利再投.contains(baseTradeDto.getTrdProductDeal().getPs())&&
                DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue().equals(baseTradeDto.getTrdProductDeal().getFinprodType2())
                // add by etliu 20210421 权益类资产相关代码合并 end
        ){
        		baseTradeDto.setStlPayOrdersList(globalStlOrderList);
        }
        /*----------------------裴家俊----add------2018/11/23-------------end--------------------*/
        
        // 3.写入类型为[实际]的组合持仓流水与组合现金流水
        // 手工交收处理头寸字段为Y时，不更新头寸
        if(StringUtil.isEmpty(finProductTradeDto.getIsDealSettlePosition())
                || !StringUtil.equals("Y",finProductTradeDto.getIsDealSettlePosition())) {
            insertPosition(finProductTradeDto);
        }
        // 手工交收判断 add by yufei 20201022 start

        /*----------------------zhangliang----add------2019/02/23-------------start--------------------*/
        BigDecimal shareAmt = baseTradeDto.getTrdProductDeal().getShareAmt();
        if (baseTradeDto.getTrdProductDeal() != null
        		&& StringUtil.equals(baseTradeDto.getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_买断式回购交易.getValue())){
        	if (baseTradeDto.getSecAmount() != null){
        		baseTradeDto.getTrdProductDeal().setShareAmt(baseTradeDto.getSecAmount());
        	}
        }
        /*----------------------zhangliang----add------2019/02/23-------------end--------------------*/
        
        // 3.调用基础组件.【投资委托组件.完成委托】，写入委托完成状况信息(投资委托场合才执行此步)
        if(!finProductTradeDto.isNeedHandleFinishEntrust()){
            finishEntrust(finProductTradeDto.getBaseTradeProductDealDtos(), false);
        }

        /*----------------------zhangliang----add------2019/02/23-------------start--------------------*/
        baseTradeDto.getTrdProductDeal().setShareAmt(shareAmt);
        /*----------------------zhangliang----add------2019/02/23-------------end--------------------*/
        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    public Boolean finishEntrust(List<BaseTradeProductDealDto> BaseTradeProductDealDtoList, boolean isCancel) {
        List<EtrFinishStatusDto> baseEtrInvestInfoDtos = new ArrayList<>();
        for (BaseTradeProductDealDto baseTradeProductDealDto : BaseTradeProductDealDtoList) {
            if(StringUtil.isNotBlank(baseTradeProductDealDto.getPreTradeId())){
                TrdProductDealPreKey key = new TrdProductDealPreKey();
                key.setTradeId(baseTradeProductDealDto.getPreTradeId());
                key.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                TrdProductDealPre trdProductDealPre = trdProductDealPreMapper.selectByPrimaryKey(key);
                TrdProductDealAddPre trdProductDealAddPre = trdProductDealAddPreMapper.selectByPrimaryKey(baseTradeProductDealDto.getPreTradeId());
                EtrFinishStatusKey etrKey = new EtrFinishStatusKey();
                etrKey.setEntrustId(trdProductDealAddPre.getEntrustId());
                etrKey.setTradeId(baseTradeProductDealDto.getPreTradeId());
                EtrFinishStatus etrFinishStatus = etrFinishStatusMapper.selectByPrimaryKey(etrKey);
                if(trdProductDealPre!=null && StringUtil.isNotEmpty(trdProductDealAddPre.getEntrustId())){
                    if (StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                            || StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                            || StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                        EtrFinishStatusDto status = new EtrFinishStatusDto();
                        status.setDayEndBatch(baseTradeProductDealDto.isDayEndBatch());
                        status.setEntrustId(trdProductDealAddPre.getEntrustId());
                        status.setTradeId(baseTradeProductDealDto.getTrdProductDealAdd().getTradeId());
                        status.setFinishDate(baseTradeProductDealDto.getTrdProductDeal().getTradeDate());
                        // 完成额度取值判断修改 add by yufei 20200807 start
                        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(trdProductDealAddPre.getEntrustId());
                        if(etrEntrustAdd != null && StringUtil.isNotEmpty(etrEntrustAdd.getTargetType())
                          && StringUtil.equals(DictCodeListExt3.目标类型_金额.getValue(),etrEntrustAdd.getTargetType())){
                            status.setFinishAmt(baseTradeProductDealDto.getTrdProductDeal().getTradeAmt());
                        }
                        else {
                            status.setFinishAmt(baseTradeProductDealDto.getTrdProductDeal().getShareAmt());
                        }
                        status.setEntFinishAmt(etrFinishStatus != null ? etrFinishStatus.getEntFinishAmt() : BigDecimal.ZERO);
                        status.setTrader(baseTradeProductDealDto.getTrdProductDeal().getTrader());
                        if(StringUtil.isNotBlank(baseTradeProductDealDto.getfTradeId())){
                            status.setCstpTradeId(baseTradeProductDealDto.getfTradeId());
                        }
                        status.setTradeId(this.removeLasStr(status.getTradeId()));
                        baseEtrInvestInfoDtos.add(status);
                        continue;
                    }
                }
            }
            if (StringUtil.isEmpty(baseTradeProductDealDto.getTrdProductDealAdd().getEntrustId())) {
                continue;
            }
            //可转债申购不需要更新委托完成度，service已经更新过委托状态
            if(StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                    &&baseTradeProductDealDto.getTrdFinProductDealTotal()!=null
            		&&(StringUtil.equals(DictCodeList.交易方向_申购.getValue(),baseTradeProductDealDto.getTrdFinProductDealTotal().getPs())||StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(),baseTradeProductDealDto.getTrdFinProductDealTotal().getPs()))){
            	 continue;
            }
            // 外汇远期掉期到期时不需要更新委托完成度（到期copy的买入，所以委托代码也有值了） add by renping 20191125 start
            if ((StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(),
                DictCodeListExt1.金融产品类型_投管用_远期.getValue())
                 || StringUtil.equals(
                     baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(),
                     DictCodeListExt1.金融产品类型_投管用_掉期.getValue()))
                && (StringUtil.equals(DictCodeList.交易方向_到期.getValue(),
                    baseTradeProductDealDto.getTrdProductDeal().getPs()))) {
                continue;
            }
            // 外汇远期掉期到期时不需要更新委托完成度（到期copy的买入，所以委托代码也有值了） add by renping 20191125 end

            // 申购确认的不走
            if ((baseTradeProductDealDto.getTrdFinProductDealTotal() != null
                    && (StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(),baseTradeProductDealDto.getTrdFinProductDealTotal().getPs())
                    || StringUtil.equals(DictCodeList.交易方向_赎回确认.getValue(), baseTradeProductDealDto.getTrdFinProductDealTotal().getPs())
                    || StringUtil.equals(DictCodeList.交易方向_认购确认.getValue(), baseTradeProductDealDto.getTrdFinProductDealTotal().getPs()))
                )
                  || StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), baseTradeProductDealDto.getTrdProductDeal().getPs())
                  || StringUtil.equals(DictCodeList.交易方向_赎回到账.getValue(), baseTradeProductDealDto.getTrdProductDeal().getPs())
                  || StringUtil.equals(DictCodeList.交易方向_赎回确认.getValue(), baseTradeProductDealDto.getTrdProductDeal().getPs())
                  || StringUtil.equals(DictCodeList.交易方向_认购确认.getValue(), baseTradeProductDealDto.getTrdProductDeal().getPs())
            ) {
                continue;
            }
            EtrFinishStatusDto status = new EtrFinishStatusDto();
            status.setDayEndBatch(baseTradeProductDealDto.isDayEndBatch());
            status.setEntrustId(baseTradeProductDealDto.getTrdProductDealAdd().getEntrustId());
            status.setTradeId(baseTradeProductDealDto.getTrdProductDealAdd().getTradeId());
            status.setFinishDate(baseTradeProductDealDto.getTrdProductDeal().getTradeDate());
            if (StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
            		|| StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue())
            		|| StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue())
                    || StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                    || StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
            ) {
//                status.setFinishAmt(new BigDecimal(baseTradeProductDealDto.getTrdProductDeal().getShareAmt().intValue() * 100));
                // 完成额度取值判断修改 add by yufei 20200807 start
                EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(baseTradeProductDealDto.getTrdProductDealAdd().getEntrustId());
                if(StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdFinProductDealTotal())
                        && StringUtil.equals(baseTradeProductDealDto.getTrdFinProductDealTotal().getPs(),DictCodeListExt3.交易方向_中途换券.getValue())) {
                    //中途换券换出交易不更新指令完成度
                    if(StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getPs(),DictCodeListExt3.质押券置换_置换方向_换出.getValue())) {
                        continue;
                    }
                    status.setFinishAmt(baseTradeProductDealDto.getTrdProductDeal().getShareAmt());
                }
                else if(StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdFinProductDealTotal())
                        &&StringUtil.equals(baseTradeProductDealDto.getTrdFinProductDealTotal().getPs(),DictCodeListExt3.交易方向_解除质押.getValue())) {
                    status.setFinishAmt(baseTradeProductDealDto.getTrdProductDeal().getShareAmt());
                }
                else if(StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdFinProductDealTotal())
                        &&StringUtil.equals(baseTradeProductDealDto.getTrdFinProductDealTotal().getPs(),DictCodeList.交易方向_到期续做.getValue())) {
                    //到期续作中，只考虑新做首期，收息到账和还本到账不写入指令完成度
                    if(!StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getPs(),DictCodeList.交易方向_买入.getValue())) {
                        continue;
                    }
                    status.setFinishAmt(baseTradeProductDealDto.getTrdProductDeal().getShareAmt());
                }
                // add by etliu 20220116 start
                // GZYHTZJY-1145: 【场内对账清结算1231】深交所协议回购首期交易，日终头寸更新有误【解决日期：2022-01-14】】
                else if (etrEntrustAdd != null && StringUtil.equals(DictCodeList.回购类型_质押式协议回购.getValue(), etrEntrustAdd.getAssetType1())) {
                    status.setFinishAmt(baseTradeProductDealDto.getTrdProductDeal().getShareAmt());
                }
                // add by etliu 20220116 end
                else if(etrEntrustAdd != null && StringUtil.isNotEmpty(etrEntrustAdd.getTargetType())
                   && StringUtil.equals(DictCodeListExt3.目标类型_金额.getValue(),etrEntrustAdd.getTargetType())){
                    status.setFinishAmt(baseTradeProductDealDto.getTrdProductDeal().getTradeAmt());
                }
                else {
                    status.setFinishAmt(baseTradeProductDealDto.getTrdProductDeal().getShareAmt());
                }
                // 完成额度取值判断修改 add by yufei 20200807 end
            }else if(StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_股票.getValue())||
                     StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())||
                    StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())
            ||StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue())){
                // 完成额度取值判断修改 add by yufei 20200807 start
                EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(baseTradeProductDealDto.getTrdProductDealAdd().getEntrustId());
                if(etrEntrustAdd != null && StringUtil.isNotEmpty(etrEntrustAdd.getTargetType()) ){
                    if(StringUtil.equals(DictCodeListExt3.目标类型_金额.getValue(),etrEntrustAdd.getTargetType())) {
                        status.setFinishAmt(baseTradeProductDealDto.getTrdProductDeal().getTradeAmt());
                    }
                    else {
                        status.setFinishAmt(baseTradeProductDealDto.getTrdProductDeal().getShareAmt());
                    }
                }
                else {
                    EtrEntrust etr = etrEntrustMapper.selectByPrimaryKey(baseTradeProductDealDto.getTrdProductDealAdd().getEntrustId());
                    //委托是金额 更新金额
                    if (StringUtil.isNotEmpty(etr) && StringUtil.isNotEmpty(etr.getEntrustAmt())) {
                        status.setFinishAmt(baseTradeProductDealDto.getTrdProductDeal().getTradeAmt());
                    }
                    //委托是数量 更新数量
                    else {
                        status.setFinishAmt(baseTradeProductDealDto.getTrdProductDeal().getShareAmt());
                    }
                }
                // 完成额度取值判断修改 add by yufei 20200807 end
            }
            else {
                status.setFinishAmt(StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdProductDeal().getPrinAmt())?baseTradeProductDealDto.getTrdProductDeal().getPrinAmt(): StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdProductDeal().getShareAmt())?baseTradeProductDealDto.getTrdProductDeal().getShareAmt():baseTradeProductDealDto.getTrdProductDeal().getTradeAmt());
            }
            status.setTrader(baseTradeProductDealDto.getTrdProductDeal().getTrader());
            if(StringUtil.isNotBlank(baseTradeProductDealDto.getfTradeId())){
                status.setCstpTradeId(baseTradeProductDealDto.getfTradeId());
            }
            status.setTradeId(this.removeLasStr(status.getTradeId()));
            baseEtrInvestInfoDtos.add(status);
        }
        if (!baseEtrInvestInfoDtos.isEmpty()) {
            if (isCancel) {
                return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/cancelEntrust", baseEtrInvestInfoDtos, Boolean.class);
            } else {
                return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/finishEntrust", baseEtrInvestInfoDtos, Boolean.class);
            }
        }
        return Boolean.TRUE;
    }

    private String removeLasStr(String str){
        if (StringUtil.isNotEmpty(str)&&str.lastIndexOf('R') == str.length() - 1) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }
    /**
     * 处理流程中状态
     *
     * @return CommonResult
     * @param finProductTradeDto
     */
    private CommonResult deal流程中(FinProductTradeDto finProductTradeDto) {
        if (finProductTradeDto.getBaseTradeProductDealDtos() == null || finProductTradeDto.getBaseTradeProductDealDtos().isEmpty()) {
            return new CommonResult(CommonResult.FAILD, "没有数据需要处理");
        }
        // 1.调用【组合可用持仓与实际持仓查询更新组件】与【组合可用现金与实际现金查询更新组件】执行持仓与现金的插入操作

        /*----------------------zhangliang----add------2019/02/23-------------start--------------------*/
        BaseTradeProductDealDto baseTradeDto = finProductTradeDto.getBaseTradeProductDealDtos().get(0);
        if (baseTradeDto.getTrdProductDeal() != null
        		&& StringUtil.equals(baseTradeDto.getTrdProductDeal().getBusiType(), DictCodeList.共通业务类型_买断式回购交易.getValue())){
        	if (baseTradeDto.getSecAmount() != null){
        		baseTradeDto.getTrdProductDeal().setShareAmt(baseTradeDto.getSecAmount());
        	}
        }
        /*----------------------zhangliang----add------2019/02/23-------------end--------------------*/

        if (!insertPosition(finProductTradeDto)) {
            return new CommonResult(CommonResult.FAILD, "处理失败");
        }

        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    /**
     * 更新债券CSTP的交易
     */
    @DataSource(DataSourceType.OTHER)
    @DTXTransaction
    public void updateInterfForZhaiJuan(FinProductTradeDto finProductTradeDto){
        TrdProductDeal trdProductDeal = finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal();
        InterfTradSecdeal interfTradSecdeal = interfTradSecdealMapper.selectByPrimaryKey(trdProductDeal.getfTradeId());
        InterfTradSecdeal record = new InterfTradSecdeal();
        if (DictCodeListExt1.交易流程状态_撤销.getValue().equals(finProductTradeDto.getWorkflowStatus())) {
            if(interfTradSecdeal!=null){
                record.setDealno(trdProductDeal.getfTradeId());
                BigDecimal prinamt = interfTradSecdeal.getPrinamt();
                BigDecimal subtract = ArithmeticUtil.doAdd(prinamt, finProductTradeDto.getTotalFaceValue());
                if(subtract.compareTo(BigDecimal.ZERO)==-1||subtract.compareTo(BigDecimal.ZERO)==0){//小于0的话
                    record.setPrinamt(BigDecimal.ZERO);
                }else{
                    record.setPrinamt(subtract);
                }
                interfTradSecdealMapper.updateByPrimaryKeySelective(record);
            }
        }
        else if (DictCodeListExt1.交易流程状态_流程中.getValue().equals(finProductTradeDto.getWorkflowStatus())) {
            if(interfTradSecdeal!=null){
                record.setDealno(trdProductDeal.getfTradeId());
                BigDecimal prinamt = interfTradSecdeal.getPrinamt();
                BigDecimal subtract = ArithmeticUtil.subtract(prinamt, finProductTradeDto.getTotalFaceValue());
               if(subtract.compareTo(BigDecimal.ZERO)==-1||subtract.compareTo(BigDecimal.ZERO)==0){//小于0的话
                   record.setPrinamt(BigDecimal.ZERO);
               }else{
                   record.setPrinamt(subtract);
               }
               interfTradSecdealMapper.updateByPrimaryKeySelective(record);
            }
        }
        else if (DictCodeListExt1.交易流程状态_有效.getValue().equals(finProductTradeDto.getWorkflowStatus())) {

        }
    }

    /**
     * 更新CSTP质押式交易
     * @param finProductTradeDto
     */
    @DataSource(DataSourceType.OTHER)
    @DTXTransaction
    public void updateInterfForZhiYaShi(FinProductTradeDto finProductTradeDto){
        TrdProductDeal trdProductDeal = finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal();
        InterfOpicsTradRepo interfOpicsTradRepo = interfOpicsTradRepoMapper.selectByPrimaryKey(trdProductDeal.getfTradeId());
        InterfOpicsTradRepo record = new InterfOpicsTradRepo();
        if (DictCodeListExt1.交易流程状态_撤销.getValue().equals(finProductTradeDto.getWorkflowStatus())) {
            if(interfOpicsTradRepo!=null){
                record.setDealno(trdProductDeal.getfTradeId());
                record.setTradeid(trdProductDeal.getfTradeId());
                BigDecimal prinamt = interfOpicsTradRepo.getRepoamt();
                BigDecimal subtract = ArithmeticUtil.doAdd(prinamt, trdProductDeal.getPrinAmt());
                if(subtract.compareTo(BigDecimal.ZERO)==-1||subtract.compareTo(BigDecimal.ZERO)==0){//小于0的话
                    record.setRepoamt(BigDecimal.ZERO);
                }else{
                    record.setRepoamt(subtract);
                }
                interfOpicsTradRepoMapper.updateByPrimaryKeySelective(record);
            }
        }
        else if (DictCodeListExt1.交易流程状态_流程中.getValue().equals(finProductTradeDto.getWorkflowStatus())) {
            if(interfOpicsTradRepo!=null){
                record.setDealno(trdProductDeal.getfTradeId());
                record.setTradeid(trdProductDeal.getfTradeId());
                BigDecimal prinamt = interfOpicsTradRepo.getRepoamt();
                BigDecimal subtract = ArithmeticUtil.subtract(prinamt, trdProductDeal.getPrinAmt());
                if(subtract.compareTo(BigDecimal.ZERO)==-1||subtract.compareTo(BigDecimal.ZERO)==0){//小于0的话
                    record.setRepoamt(BigDecimal.ZERO);
                }else{
                    record.setRepoamt(subtract);
                }
                interfOpicsTradRepoMapper.updateByPrimaryKeySelective(record);
            }
        }
        else if (DictCodeListExt1.交易流程状态_有效.getValue().equals(finProductTradeDto.getWorkflowStatus())) {
        }
    }

    /**
     * @description: 2.写入类型为[实际]的组合持仓流水与组合现金流水
     * @param: [BaseTradeProductDealDtoList, 持仓类型]
     * @return: void
     * @date: 2018/9/29
     */
    @Override
    public Boolean insertPosition(FinProductTradeDto finProductTradeDto) {
        // modify by etliu 20220308 start
        // 场内的头寸处理，全部已经对接了头寸引擎，需要标记下
        if (!StringUtil.isEmptyList(finProductTradeDto.getBaseTradeProductDealDtos())
            && finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal() != null
            && (StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getTradeMarket())
                || StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), finProductTradeDto.getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getTradeMarket()))) {
            finProductTradeDto.setIsExistNew(DictCodeList.是否_是.getValue());
        }
        // modify by etliu 20220308 end

        DictCodeList 持仓类型 = null;
        boolean needNegative = false;
        if (DictCodeListExt1.交易流程状态_撤销.getValue().equals(finProductTradeDto.getWorkflowStatus())) {
            持仓类型 = DictCodeList.持仓类型_实际;
            needNegative = true;
        }
        else if (DictCodeListExt1.交易流程状态_流程中.getValue().equals(finProductTradeDto.getWorkflowStatus())) {
            持仓类型 = DictCodeList.持仓类型_可用;
            needNegative = false;
        }
        else if (DictCodeListExt1.交易流程状态_有效.getValue().equals(finProductTradeDto.getWorkflowStatus())) {
            持仓类型 = DictCodeList.持仓类型_实际;
            needNegative = false;
        }
        if (持仓类型 == null) {
            return false;
        }
        boolean 是否处理持仓 = AllTradeCommonUtil.是否处理持仓(finProductTradeDto.getWorkflowId(), LoginUserUtil.getUserName(), 持仓类型);
        List<PtlSecPositionFlowDto> ptlSecPositionFlows = new ArrayList<>();
        List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<>();
        for (BaseTradeProductDealDto baseTradeProductDealDto : finProductTradeDto.getBaseTradeProductDealDtos()) {
            PortfolioPositionsFlowInputDto inputDto = new PortfolioPositionsFlowInputDto();
            inputDto.setPositionType(持仓类型.getValue());
            inputDto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
            // 增加附属表dto
            inputDto.setTrdFinProductBarterList(baseTradeProductDealDto.getTrdFinProductBarterList());
            inputDto.setPortfolioId(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId());
            inputDto.setBonusMode(baseTradeProductDealDto.getTrdProductDealAdd().getPayType());
            inputDto.setSecAmount(baseTradeProductDealDto.getSecAmount());
            // GNS-949:增加是否工作流数据标识 modify by fuyongsheng 20220227 start
            inputDto.setNotWorkflow(finProductTradeDto.isNotWorkflow());
            baseTradeProductDealDto.setNotWorkflow(finProductTradeDto.isNotWorkflow());
            // GNS-949:增加是否工作流数据标识 modify by fuyongsheng 20220227 end
            if (StringUtil.isNotEmpty(finProductTradeDto.getAssetType2())) {
            	inputDto.setAssetType2(finProductTradeDto.getAssetType2());
            }
            else {
            	inputDto.setAssetType2(baseTradeProductDealDto.getAssetType2());
            }
			inputDto.setTrdProductDealAdd(baseTradeProductDealDto.getTrdProductDealAdd());
            // 针对新UI添加 用于区分登记/成交/交收/交收失败
            inputDto.setStage(baseTradeProductDealDto.getStage());

            if (是否处理持仓) {
                List<PtlSecPositionFlowDto> ptlSecPositionFlowDtoss = assetPositionsCashCommServiceImpl.positionDeal(inputDto);//组合持仓
                if (持仓类型 == DictCodeList.持仓类型_实际 && ptlSecPositionFlowDtoss != null && !ptlSecPositionFlowDtoss.isEmpty()) {
                    //交易所品种头寸改造相关内容
                    addJiaoyisuoChuliForSec(ptlSecPositionFlowDtoss, inputDto);
                }

                //买断式回购持仓特殊处理
                for (PtlSecPositionFlowDto dto : ptlSecPositionFlowDtoss){
                    // 债券  申购确认  持仓类型为冻结
                    if (StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                            && (StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getPs(), DictCodeList.交易方向_申购确认.getValue())
                            // GNS-879: 配债生成的持仓类型是上市前冻结 modify by fuyongsheng 20220217 start
                            || StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getPs(), DictCodeListExt3.交易方向_配债.getValue()))
                            // GNS-879: 配债生成的持仓类型是上市前冻结 modify by fuyongsheng 20220217 start
                    ) {
                       dto.setHodingType(DictCodeListExt3.持仓类型_场内上市前冻结.getValue());
                    }
                    // 股票存续期  申购确认、配股  持仓类型为冻结
                    if (StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_股票.getValue())
                            && (StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getPs(), DictCodeList.交易方向_申购确认.getValue())
                            // GNS-878: 配股生成的持仓类型是上市前冻结 modify by fuyongsheng 20220217 start
                            || StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getPs(), DictCodeListExt2.交易方向_配股.getValue())
                            // GNS-878: 配股生成的持仓类型是上市前冻结 modify by fuyongsheng 20220217 start
                            ||
                            // GNS-932:上交所的红利再投需生成上市前冻结持仓 modify by fuyongsheng 20220225 start
                            (StringUtil.equals(baseTradeProductDealDto.getTrdProductDeal().getPs(), DictCodeList.交易方向_红利再投.getValue())
                                    && DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(baseTradeProductDealDto.getTrdProductDeal().getTradeMarket())
                            )
                            // GNS-932:上交所的红利再投需生成上市前冻结持仓 modify by fuyongsheng 20220225 end
                    )
                    ) {
                        dto.setHodingType(DictCodeListExt3.持仓类型_场内上市前冻结.getValue());
                    }
                    if (DictCodeListExt1.交易场所_银行间.getValue().equals(baseTradeProductDealDto.getTrdProductDeal().getTradeMarket())
                            && DictCodeList.交易方向_买入.getValue().equals(baseTradeProductDealDto.getTrdProductDeal().getPs())
                            && (DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue().equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2())
                            || DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue().equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2()))){
                        dto.setAmount(baseTradeProductDealDto.getSecAmount());
                    }
                    if (DictCodeListExt1.交易场所_银行间.getValue().equals(baseTradeProductDealDto.getTrdProductDeal().getTradeMarket())
                            && DictCodeList.交易方向_到期.getValue().equals(baseTradeProductDealDto.getTrdProductDeal().getPs())
                            && (DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue().equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2())
                            || DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue().equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2()))){
                        dto.setAmount(ArithmeticUtil.divide(dto.getAmount(),new BigDecimal(100),4));
                    }
                    if(StringUtil.isNotEmpty(finProductTradeDto.getIsExistNew())){
                        //新头寸特殊处理质押式回购持仓
                        if ((DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2())
                                || DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2()))
                                && DictCodeListExt1.交易场所_银行间.getValue().equals(baseTradeProductDealDto.getTrdProductDeal().getTradeMarket())
                                && DictCodeList.交易方向_买入.getValue().equals(baseTradeProductDealDto.getTrdProductDeal().getPs())){
                            dto.setCdate(baseTradeProductDealDto.getTrdProductDeal().getTradeDate());
                        }
                    }

                }
                ptlSecPositionFlows.addAll(ptlSecPositionFlowDtoss);
            }



            if (baseTradeProductDealDto.getStlPayOrdersList() != null && !baseTradeProductDealDto.getStlPayOrdersList().isEmpty()) {
                PortfolioCashPositionsFlowInputDto cashInputDto = new PortfolioCashPositionsFlowInputDto();
                cashInputDto.setPositionType(持仓类型.getValue());
                cashInputDto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
                cashInputDto.setPortfolioId(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId());
                cashInputDto.setBonusMode(baseTradeProductDealDto.getTrdProductDealAdd().getPayType());
                cashInputDto.setAssetType2(finProductTradeDto.getAssetType2());
                cashInputDto.setStlPayOrders(baseTradeProductDealDto.getStlPayOrdersList());
                cashInputDto.setIeFlag(finProductTradeDto.getIeFlag());
                // GNS-949:增加是否工作流数据标识 modify by fuyongsheng 20220227 start
                cashInputDto.setNotWorkflow(finProductTradeDto.isNotWorkflow());
                // GNS-949:增加是否工作流数据标识 modify by fuyongsheng 20220227 end
                cashInputDto.setTrdProductDealAdd(baseTradeProductDealDto.getTrdProductDealAdd());
                // 针对新UI添加 用于区分登记/成交/交收/交收失败
                cashInputDto.setStage(baseTradeProductDealDto.getStage());
                if (是否处理持仓) {
                    List<PtlCashPositionFlowDto> resultList = assetPositionsCashCommServiceImpl.cashDeal(cashInputDto);//现金处理详细
//                    if (持仓类型 == DictCodeList.持仓类型_可用
//                            && AllTradeCommonUtil.自动生成反向交易(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(),
//                            baseTradeProductDealDto.getTrdProductDeal().getPs(), baseTradeProductDealDto.getTrdProductDeal().getCounterId())) {
//                        resultList.addAll(处理交易对手流水(baseTradeProductDealDto.getStlPayOrdersList(), baseTradeProductDealDto.getTrdProductDeal(),
//                                baseTradeProductDealDto.getTrdProductDealAdd()));
//                    }
//                    if (持仓类型 == DictCodeList.持仓类型_实际 && resultList != null && !resultList.isEmpty()) {
//                    	//交易所品种头寸改造相关内容
//                    	addJiaoyisuoChuli(resultList, cashInputDto);
//                    }
                    ptlCashPositionFlows.addAll(resultList);
                }
            }
            //        现金与持仓执行共同
            if(baseEtrCashPositionCommService != null && !finProductTradeDto.isNeedExecuteEtrPosition() && 是否处理持仓){
                if(StringUtil.isNotEmpty(finProductTradeDto.getBussRepoSec())){
                	baseTradeProductDealDto.setBussRepoSec(finProductTradeDto.getBussRepoSec());
                }
                baseTradeProductDealDto.setAssetType2(finProductTradeDto.getAssetType2());
                baseTradeProductDealDto.setIsExistNew(finProductTradeDto.getIsExistNew());
                //指令现金持仓共同处理
                baseEtrCashPositionCommService.executeEtrCashPosition(baseTradeProductDealDto, 持仓类型, needNegative,finProductTradeDto.getIeFlag(),finProductTradeDto.getIsOverdueDeal());

//                if (!DictCodeListExt1.交易流程状态_撤销.getValue().equals(finProductTradeDto.getWorkflowStatus()) && AllTradeCommonUtil.自动生成反向交易(baseTradeProductDealDto.getTrdProductDeal().getFinprodType2(),
//                        baseTradeProductDealDto.getTrdProductDeal().getPs(), baseTradeProductDealDto.getTrdProductDeal().getCounterId())) {
//                    BaseTradeProductDealDto baseTradeProductDealDtoC = 把交易信息转换成交易对手的交易信息(baseTradeProductDealDto);
//                    baseEtrCashPositionCommService.executeEtrCashPosition(baseTradeProductDealDtoC, 持仓类型, needNegative,finProductTradeDto.getIeFlag(),finProductTradeDto.getIsOverdueDeal());
//                }
            }
        }
        if (needNegative) {
            if (ptlCashPositionFlows != null && !ptlCashPositionFlows.isEmpty()) {
                for (PtlCashPositionFlowDto ptlCashPosition : ptlCashPositionFlows) {
                    if (ptlCashPosition.getAmount() != null) {
                        ptlCashPosition.setAmount(ptlCashPosition.getAmount().negate());
                    }
                    if (ptlCashPosition.getOrderId().lastIndexOf('R') != ptlCashPosition.getOrderId().length() - 1) {
                        ptlCashPosition.setOrderId(ptlCashPosition.getOrderId() + "R");
                    }
                }
            }
            if (ptlSecPositionFlows != null && !ptlSecPositionFlows.isEmpty()) {
                for (PtlSecPositionFlowDto ptlSecPosition : ptlSecPositionFlows) {
                    if (ptlSecPosition.getAmount() != null) {
                        ptlSecPosition.setAmount(ptlSecPosition.getAmount().negate());
                    }
                    if (ptlSecPosition.getTradeId().lastIndexOf('R') != ptlSecPosition.getTradeId().length() - 1) {
                        ptlSecPosition.setTradeId(ptlSecPosition.getTradeId() + "R");
                    }
                }
            }
        }
        PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
        ptlPositionHoldDto.setPtlSecPositionFlows(ptlSecPositionFlows);
        ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);
        // 数据是否已入新头寸
        if(StringUtil.isNotEmpty(finProductTradeDto.getIsExistNew())) {
            ptlPositionHoldDto.getPtlCashPositionFlows().forEach(item->item.setIsExistNew(finProductTradeDto.getIsExistNew()));
            ptlPositionHoldDto.getPtlSecPositionFlows().forEach(item->item.setIsExistNew(finProductTradeDto.getIsExistNew()));
        }
        //不是逾期交易，更新组合持仓流水
        if(!(StringUtil.isNotEmpty(finProductTradeDto.getIsOverdueDeal())&&StringUtil.equals(DictCodeList.是否_是.getValue(), finProductTradeDto.getIsOverdueDeal()))){
        	if (ptlPositionHoldDto.getPtlSecPositionFlows() != null && !ptlPositionHoldDto.getPtlSecPositionFlows().isEmpty()) {
                for (PtlSecPositionFlowDto ptlSecPositionFlow : ptlPositionHoldDto.getPtlSecPositionFlows()) {
                    basePtlSecPositionInfoService.inertSecPositionFlow(ptlSecPositionFlow);
                }
            }
        }
        if (ptlPositionHoldDto.getPtlCashPositionFlows() != null && !ptlPositionHoldDto.getPtlCashPositionFlows().isEmpty()) {
            for (PtlCashPositionFlowDto ptlCashPositionFlow : ptlPositionHoldDto.getPtlCashPositionFlows()) {
                basePtlCashPositionInfoService.insertCashPositionFlow(ptlCashPositionFlow);
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 交易所品种头寸改造相关内容
     * @param ptlSecPositionFlows
     * @param secInputDto
     */
    private void addJiaoyisuoChuliForSec(List<PtlSecPositionFlowDto> ptlSecPositionFlows,
    		PortfolioPositionsFlowInputDto secInputDto) {
    	TrdProductDeal deal = secInputDto.getTrdProductDeal();
    	if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())) {
    		for (PtlSecPositionFlowDto dto : ptlSecPositionFlows) {
    			if (!DictCodeList.持仓类型_实际.getValue().equals(dto.getHodingType())) {
    				continue;
    			}
				if (DictCodeList.交易方向_行权_回售.getValue().equals(deal.getPs())
						|| DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())
						|| DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
						|| DictCodeListExt3.交易方向_配债.getValue().equals(deal.getPs())) {
					dto.setCdate(
							StringUtil.isNotEmpty(deal.getSettleDate()) ? deal.getSettleDate() : deal.getTradeDate());
				}
    		}
    	}
    	if ((DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(deal.getFinprodType2())
    			|| DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(deal.getFinprodType2()))
                && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                && (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                		|| DictCodeList.交易方向_到期.getValue().equals(deal.getPs()))) {
    		for (PtlSecPositionFlowDto dto : ptlSecPositionFlows) {
    			if (!DictCodeList.持仓类型_实际.getValue().equals(dto.getHodingType())) {
    				continue;
    			}
    			dto.setCdate(
						StringUtil.isNotEmpty(deal.getSettleDate()) ? deal.getSettleDate() : deal.getTradeDate());
    		}
    	}
    }
    /**
     * 交易所品种头寸改造相关内容
     * @param ptlCashPositionFlows
     * @param cashInputDto
     */
    private void addJiaoyisuoChuli(List<PtlCashPositionFlowDto> ptlCashPositionFlows,
    		PortfolioCashPositionsFlowInputDto cashInputDto) {
    	TrdProductDeal deal = cashInputDto.getTrdProductDeal();
    	List<PtlCashPositionFlowDto> rstList = new ArrayList<PtlCashPositionFlowDto>();
    	if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())) {
    		for (PtlCashPositionFlowDto dto : ptlCashPositionFlows) {
    			if (!DictCodeList.持仓类型_实际.getValue().equals(dto.getHodingType())) {
    				continue;
    			}
    			//是否满足净额结算,不满足则不变更
				FinProductAdd finProductAdd = basePtlCashPositionInfoService.getFinProductAdd(deal.getFinprodId());
				if (finProductAdd == null || !DictCodeList.是否_是.getValue().equals(finProductAdd.getIsNetSett())) {
					continue;
				}
    			//T日影响头寸
        		PtlCashPositionFlowDto dtoT = new PtlCashPositionFlowDto();
        	    BeanUtil.copyPropertiesIgnoreNull(dtoT, dto);
        	    //T+1日影响头寸
        		PtlCashPositionFlowDto dtoT1 = new PtlCashPositionFlowDto();
        	    BeanUtil.copyPropertiesIgnoreNull(dtoT1, dto);
        	    if (DictCodeList.交易方向_行权_回售.getValue().equals(deal.getPs())
        	    		|| DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())
        	    		|| DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
        	    		|| DictCodeListExt3.交易方向_配债.getValue().equals(deal.getPs())) {
        	    	//T日和实际持仓方向相同
					dtoT.setHodingType(DictCodeListExt1.持仓类型_不可支取.getValue());
					//T+1日和实际持仓方向相反
					dtoT1.setHodingType(DictCodeListExt1.持仓类型_不可支取.getValue());
					dtoT1.setCdate(basePtlCashPositionInfoService.selectNextWorkDay(null, dtoT1.getCdate(),
							deal.getFinprodId()));
					dtoT1.setAmount(dto.getAmount().negate());
					rstList.add(dtoT);
					rstList.add(dtoT1);
        	    }
    		}
    	}
    	if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(deal.getFinprodType2())
                && (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                        || DictCodeListExt2.交易方向_配股.getValue().equals(deal.getPs())
                        || DictCodeListExt2.交易方向_增发.getValue().equals(deal.getPs())
                        || DictCodeListExt2.交易方向_新股中签.getValue().equals(deal.getPs())
                        || DictCodeList.交易方向_卖出.getValue().equals(deal.getPs()))) {
    		for (PtlCashPositionFlowDto dto : ptlCashPositionFlows) {
    			if (!DictCodeList.持仓类型_实际.getValue().equals(dto.getHodingType())) {
    				continue;
    			}
    			//T日影响头寸
        		PtlCashPositionFlowDto dtoT = new PtlCashPositionFlowDto();
        	    BeanUtil.copyPropertiesIgnoreNull(dtoT, dto);
        	    //T+1日影响头寸
        		PtlCashPositionFlowDto dtoT1 = new PtlCashPositionFlowDto();
        	    BeanUtil.copyPropertiesIgnoreNull(dtoT1, dto);

    	    	//T日和实际持仓方向相同
    	    	dtoT.setHodingType(DictCodeListExt1.持仓类型_不可支取.getValue());
    	    	//T日和实际持仓方向相反
    	    	dtoT1.setHodingType(DictCodeListExt1.持仓类型_不可支取.getValue());
				dtoT1.setCdate(basePtlCashPositionInfoService.selectNextWorkDay(null, dtoT1.getCdate(),
						deal.getFinprodId()));
    	    	dtoT1.setAmount(dto.getAmount().negate());
				rstList.add(dtoT);
				rstList.add(dtoT1);
    		}
    	}
    	if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(deal.getFinprodType2())
    			&& !DictCodeListExt1.交易场所_OTC场外.getValue().equals(deal.getTradeMarket())
    			&& (DictCodeList.交易方向_申购.getValue().equals(deal.getPs())
    					|| DictCodeList.交易方向_赎回.getValue().equals(deal.getPs())
    					|| DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
    					|| DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())
    					|| DictCodeList.交易方向_申购确认.getValue().equals(deal.getPs())
    					|| DictCodeList.交易方向_赎回确认.getValue().equals(deal.getPs()))) {
    		for (PtlCashPositionFlowDto dto : ptlCashPositionFlows) {
    			if (!DictCodeList.持仓类型_实际.getValue().equals(dto.getHodingType())) {
    				continue;
    			}
    			//T日影响头寸
        		PtlCashPositionFlowDto dtoT = new PtlCashPositionFlowDto();
        	    BeanUtil.copyPropertiesIgnoreNull(dtoT, dto);
        	    //T+1日影响头寸
        		PtlCashPositionFlowDto dtoT1 = new PtlCashPositionFlowDto();
        	    BeanUtil.copyPropertiesIgnoreNull(dtoT1, dto);

    	    	//T日和实际持仓方向相同
    	    	dtoT.setHodingType(DictCodeListExt1.持仓类型_不可支取.getValue());
    	    	//T日和实际持仓方向相反
    	    	dtoT1.setHodingType(DictCodeListExt1.持仓类型_不可支取.getValue());
				dtoT1.setCdate(basePtlCashPositionInfoService.selectNextWorkDay(null, dtoT1.getCdate(),
						deal.getFinprodId()));
    	    	dtoT1.setAmount(dto.getAmount().negate());
				rstList.add(dtoT);
				rstList.add(dtoT1);
    		}
    	}
        // 3、资产二级类型
        String assetType2 = StringUtil.isNotEmpty(
        		cashInputDto.getAssetType2()) ? cashInputDto.getAssetType2() : baseAssetCashPositionCommMapper.selectFundType(
        				cashInputDto.getTrdProductDeal().getFinprodId());
    	if ((DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(deal.getFinprodType2())
    			|| DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(deal.getFinprodType2()))
                && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                && DictCodeList.回购类型_质押式竞价回购.getValue().equals(assetType2)
                && (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                		|| DictCodeList.交易方向_到期.getValue().equals(deal.getPs()))) {
    		for (PtlCashPositionFlowDto dto : ptlCashPositionFlows) {
    			if (!DictCodeList.持仓类型_实际.getValue().equals(dto.getHodingType())) {
    				continue;
    			}
    			//T日影响头寸
        		PtlCashPositionFlowDto dtoT = new PtlCashPositionFlowDto();
        	    BeanUtil.copyPropertiesIgnoreNull(dtoT, dto);
        	    //T+1日影响头寸
        		PtlCashPositionFlowDto dtoT1 = new PtlCashPositionFlowDto();
        	    BeanUtil.copyPropertiesIgnoreNull(dtoT1, dto);

    	    	//T日和实际持仓方向相同
    	    	dtoT.setHodingType(DictCodeListExt1.持仓类型_不可支取.getValue());
    	    	//T日和实际持仓方向相反
    	    	dtoT1.setHodingType(DictCodeListExt1.持仓类型_不可支取.getValue());
				dtoT1.setCdate(basePtlCashPositionInfoService.selectNextWorkDay(deal.getTradeMarket(), dtoT1.getCdate(),
						deal.getFinprodId()));
    	    	dtoT1.setAmount(dto.getAmount().negate());
				rstList.add(dtoT);
				rstList.add(dtoT1);
    		}
    	}
    	ptlCashPositionFlows.addAll(rstList);
    }
    
    
    private BaseTradeProductDealDto 把交易信息转换成交易对手的交易信息(BaseTradeProductDealDto btpdd) {
        if (btpdd.getTrdProductDeal() == null) {
            return null;
        }

        String busiType = btpdd.getTrdProductDeal().getBusiType();
        PtlPortfolio mcp = ptlPortfolioMapper.selectByPrimaryKey(btpdd.getTrdProductDeal().getCounterId());
        if (mcp != null) { // 内部交易对手
            BaseTradeProductDealDto result = new BaseTradeProductDealDto();
            BeanUtil.copyPropertiesIgnoreNull(result, btpdd);
            TrdProductDeal deal = new TrdProductDeal();
            result.setTrdProductDeal(deal);
            BeanUtil.copyPropertiesIgnoreNull(deal, btpdd.getTrdProductDeal());
            deal.setTradeId(btpdd.getTrdProductDeal().getTradeId() + "C");
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            result.setTrdProductDealAdd(trdProductDealAdd);
            if (btpdd.getTrdProductDealAdd() != null) {
                BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, btpdd.getTrdProductDealAdd());
            }
            PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
            result.setPaTrdProductDeal(paTrdProductDeal);
            if (btpdd.getPaTrdProductDeal() != null) {
                BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, btpdd.getPaTrdProductDeal());
            }
            String 交易对手_子组合 = deal.getCounterId();// 子组合交易对手

            // 组合代码与交易对手对调
            // 交易方向为买入；
            // 其他字段与原始交易相同；
            // String 原交易母组合代码 = trdProductDealAdd.getPortfolioId();
            String 原交易子组合代码 = btpdd.getActPortfolioId();
            deal.setCounterId(原交易子组合代码);
            trdProductDealAdd.setPortfolioId(交易对手_子组合);

            trdProductDealAdd.setTradeId(deal.getTradeId());

            //交易费用不为空 入库交易费用信息
            if (!StringUtil.isEmptyList(btpdd.getTrdTradeFeeDetailList())) {
                btpdd.getTrdTradeFeeDetailList().forEach(fee -> {
                    fee.setTradeId(deal.getTradeId());
                });
            }

            if (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())) {
                deal.setPs(DictCodeList.交易方向_卖出.getValue());
                // 交易类型和业务类型的对应，若是存错，现金流查询页面展示错误，
                // 对应关系现在先确认这债券和存款类交易，待优化补充 yjm start 20190730
                if (DictCodeList.共通业务类型_债券类资产交易.getValue().equals(busiType)) {
                    paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产卖出.getValue());
                } else if (DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue().equals(busiType)) {
                    paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_赎回.getValue());
                } else if (DictCodeList.共通业务类型_项目类资产交易.getValue().equals(busiType)) {
                    paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_项目类资产卖出.getValue());
                }
                // add by dubaojun 2020-07-24 增加其他交易的映射 start
                else if (DictCodeList.共通业务类型_计划类资产_净值型_交易.getValue().equals(busiType)) {
                    paTrdProductDeal.setPaBussType(DictCodeListExt3.平安业务类型_计划类资产_净值型_卖出.getValue());
                } else if (DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue().equals(busiType)) {
                    paTrdProductDeal.setPaBussType(DictCodeListExt3.平安业务类型_计划类资产_货币型_卖出.getValue());
                } else if (DictCodeList.共通业务类型_基金类资产交易.getValue().equals(busiType)) {
                    if (btpdd.getPaTrdProductDeal() != null) {
                        if (DictCodeListExt3.平安业务类型_货币基金买入.getValue().equals(btpdd.getPaTrdProductDeal().getPaBussType())) {
                            paTrdProductDeal.setPaBussType(DictCodeListExt3.平安业务类型_货币基金卖出.getValue());
                        } else if (DictCodeListExt3.平安业务类型_非货币基金买入.getValue().equals(btpdd.getPaTrdProductDeal().getPaBussType())) {
                            paTrdProductDeal.setPaBussType(DictCodeListExt3.平安业务类型_非货币基金卖出.getValue());
                        }
                    }
                }
                else if(DictCodeListExt3.共通业务类型_权益类资产投资交易.getValue().equals(busiType)) {
                    paTrdProductDeal.setPaBussType(DictCodeListExt3.平安业务类型_权益类资产_卖出.getValue());
                }
                // add by dubaojun 2020-07-24 增加其他交易的映射 end
                else {
                    paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_存款类资产卖出.getValue());
                }
                // 交易类型和业务类型的对应，若是存错，现金流查询页面展示错误，
                // 对应关系现在先确认这债券和存款类交易，待优化补充 yjm end 20190730

            } else if (DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())) {
                deal.setPs(DictCodeList.交易方向_买入.getValue());
                // 交易类型和业务类型的对应，若是存错，现金流查询页面展示错误，
                // 对应关系现在先确认这债券和存款类交易，待优化补充 yjm start 20190730
                if (DictCodeList.共通业务类型_债券类资产交易.getValue().equals(busiType)) {
                    paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
                } else if (DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue().equals(busiType)) {
                    paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_申购.getValue());
                } else if (DictCodeList.共通业务类型_项目类资产交易.getValue().equals(busiType)) {
                    paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_项目类资产买入.getValue());
                }
                // add by dubaojun 2020-07-24 增加其他交易的映射 start
                else if (DictCodeList.共通业务类型_计划类资产_净值型_交易.getValue().equals(busiType)) {
                    paTrdProductDeal.setPaBussType(DictCodeListExt3.平安业务类型_计划类资产_净值型_买入.getValue());
                } else if (DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue().equals(busiType)) {
                    paTrdProductDeal.setPaBussType(DictCodeListExt3.平安业务类型_计划类资产_货币型_买入.getValue());
                } else if (DictCodeList.共通业务类型_基金类资产交易.getValue().equals(busiType)) {
                    if (btpdd.getPaTrdProductDeal() != null) {
                        if (DictCodeListExt3.平安业务类型_货币基金卖出.getValue().equals(btpdd.getPaTrdProductDeal().getPaBussType())) {
                            paTrdProductDeal.setPaBussType(DictCodeListExt3.平安业务类型_货币基金买入.getValue());
                        } else if (DictCodeListExt3.平安业务类型_非货币基金卖出.getValue().equals(btpdd.getPaTrdProductDeal().getPaBussType())) {
                            paTrdProductDeal.setPaBussType(DictCodeListExt3.平安业务类型_非货币基金买入.getValue());
                        }
                    }
                }
                else if(DictCodeListExt3.共通业务类型_权益类资产投资交易.getValue().equals(busiType)) {
                    paTrdProductDeal.setPaBussType(DictCodeListExt3.平安业务类型_权益类资产_买入.getValue());
                }
                // add by dubaojun 2020-07-24 增加其他交易的映射 end
                else {
                    paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_存款类资产买入.getValue());
                }
                paTrdProductDeal.setDeptCode(LoginUserUtil.getDeptCode());//内转交易没有存部门信息
                // 交易类型和业务类型的对应，若是存错，现金流查询页面展示错误，
                // 对应关系现在先确认这债券和存款类交易，待优化补充 end start 20190730
//                if (deal.getRInvAim() != null) {
//                    deal.setInvAim(allTradeCommonInputDto.getTurnInvAim());
//                }
            }
            // 反向金融产品交易登陆
            //内转交易过滤通道信息
            String chlid = deal.getChlId();
            if (StringUtil.equals(deal.getBusiType(), DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue())
                    || StringUtil.equals(deal.getBusiType(), DictCodeList.共通业务类型_项目类资产交易.getValue())
                    || StringUtil.equals(deal.getBusiType(), DictCodeList.共通业务类型_项目类资产存续期.getValue())
                    || StringUtil.equals(deal.getBusiType(), DictCodeList.共通业务类型_计划类资产_利率型_存续期.getValue())
            ) {
                deal.setChlId(null);
            } else if (DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(deal.getFinprodType2())) {
                //IS_CAL_LOADRATE 01 借款利率
                if (!StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE), "01")) {
                    deal.setChlId(null);
                }
            }
            deal.setChlId(chlid);
            // 反向金融产品交易附属表登陆
            if (paTrdProductDeal != null) {
                paTrdProductDeal.setTradeId(deal.getTradeId());
            }

            // 反向划款申请登陆
            List<StlPayOrdersDto> stlPayOrders = new ArrayList<>();
            if (!StringUtil.isEmptyList(btpdd.getStlPayOrdersList())) {
                for (StlPayOrdersDto stlPayOrdersDto : btpdd.getStlPayOrdersList()) {
                    StlPayOrdersDto stl = new StlPayOrdersDto();
                    BeanUtil.copyPropertiesIgnoreNull(stl, stlPayOrdersDto);
                    // 生成的业务编号
                    String orderId = stlPayOrdersDto.getOrderId() + "C";
                    stl.setOrderId(orderId);

                    // 划款信息（如有）中的付款方和收款方对调；
                    String myAcctNo = stl.getMyAcctNo();
                    stl.setMyAcctNo(stl.getCounterAcctNo());
                    stl.setCounterAcctNo(myAcctNo);
                    stl.setPs(DictCodeListExt1.收支方向_支出.getValue().equals(
                            stl.getPs()) ? DictCodeListExt1.收支方向_收入.getValue() : DictCodeListExt1.收支方向_支出.getValue());
                    stl.setTradeId(deal.getTradeId());
                    stlPayOrders.add(stl);
                }
            }
            result.setStlPayOrdersList(stlPayOrders);
            return result;
        }
        return null;
    }

    private List<PtlCashPositionFlowDto> 处理交易对手流水(List<StlPayOrdersDto> stlPayOrdersDtos, TrdProductDeal trdProductDeal,
                                                                  TrdProductDealAdd trdProductDealAdd) {
        List<PtlCashPositionFlowDto> result = new ArrayList<>();
        if (!StringUtil.isEmptyList(stlPayOrdersDtos)) {
            String 交易对手_子组合 = trdProductDeal.getCounterId();// 子组合交易对手
            String 交易对手的母组合 = AllTradeCommonUtil.exchangePortfolio(trdProductDeal.getCounterId());// 交易对手的母组合，设置成反向交易的组合代码
            changePs(trdProductDeal);
            for (StlPayOrdersDto stlPayOrdersDto : stlPayOrdersDtos) {
                StlPayOrdersDto stl = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stl, stlPayOrdersDto);
                // 生成的业务编号
                String orderId = stlPayOrdersDto.getOrderId() + "C";
                stl.setOrderId(orderId);
                stl.setPortfolioId(交易对手_子组合);
                // 划款信息（如有）中的付款方和收款方对调；
                String myAcctNo = stl.getMyAcctNo();
                stl.setMyAcctNo(stl.getCounterAcctNo());
                stl.setCounterAcctNo(myAcctNo);
                result.addAll(AllTradeCommonUtil.getPtlCashPositionFlowDto(trdProductDeal, trdProductDealAdd, stl, 交易对手_子组合,
                        交易对手的母组合, DictCodeList.持仓类型_可用));
            }
            changePs(trdProductDeal);
        }
        return result;
    }

    private void changePs(TrdProductDeal trdProductDeal) {
        if (DictCodeList.交易方向_买入.getValue().equals(trdProductDeal.getPs())) {
            trdProductDeal.setPs(DictCodeList.交易方向_卖出.getValue());
        }
        else if (DictCodeList.交易方向_卖出.getValue().equals(trdProductDeal.getPs())) {
            trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommonResult delete(String workflowId) {
        // 根据工作流代码查询交易编号、组合代码
        WflTrdProductDealAdd wflTrdProductDealAdd = restTemplate.getForObject(
                ServiceApplication.TRADE_SERVICE_NAME
                        + "/common/trdcommonbase/selectWflTrdProductDealAdd/" + workflowId, WflTrdProductDealAdd.class);

        // 根据工作流代码查询指令编号
        List<WflStlPayOrders> orderList = restTemplate.getForList(
                ServiceApplication.TRADE_SERVICE_NAME
                        + "/common/trdcommonbase/selectOrderIdList/" + workflowId, WflStlPayOrders.class);
        // 调用授信共同 sjj start
        //根据工作流代码查询交易相关信息
        AllTradeCommonInputDto inputDto = restTemplate.getForObject(
                ServiceApplication.TRADE_SERVICE_NAME
                        + "/common/trdcommonbase/selectWflTrdProductDeal/" + workflowId, AllTradeCommonInputDto.class);
        // 所有交易生效后调用的共通方法
        if(inputDto !=null && wflTrdProductDealAdd !=null){
//        	AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
//            inputDto.setTrdProductDeal(trdProductDeal);
            //作废流程不做授信处理 20191029 add
            //  AllTradeCommonUtil.handleBeforeTradeComplete(inputDto,true);
        }
        // 调用授信共同 sjj end
        // 1.删除各业务流程信息

        // 1.删除各业务流程信息
        // 汇总表
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_FIN_PRODUCT_DEAL_TOTAL", "WORKFLOW_ID",
                workflowId);
//        wflTrdFinProductDealTotalMapper.deleteByWorkflowId(workflowId);
        // 汇总扩展表

//        wflPaTrdFinDealTotalMapper.deleteByWorkflowId(workflowId);
        mapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_FIN_DEAL_TOTAL", "WORKFLOW_ID",
                workflowId);
//        mapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_FIN_DEAL_TOTAL", "WORKFLOW_ID",
//                workflowId);

        mapperDelegateService.deleteBySpecificKey("WFL_TRD_DEAL_TOTAL_ADD", "WORKFLOW_ID",
                workflowId);
        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL", "WORKFLOW_ID",
                workflowId);
//        wflTrdProductDealMapper.deleteByWorkflowId(workflowId);

        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_ADD", "WORKFLOW_ID",
                workflowId);
//        wflTrdProductDealAddMapper.deleteByWorkflowId(workflowId);
        // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_PRODUCT_DEAL", "WORKFLOW_ID",
                workflowId);
//        wflPaTrdProductDealMapper.deleteByWorkflowId(workflowId);
        // 金融产品交易业务流程其他信息Entity -> 金融产品交易业务流程其他信息表
        mapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_OTH", "WORKFLOW_ID",
                workflowId);
        wflTrdProductDealOthMapper.deleteByWorkflowId(workflowId);
        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        mapperDelegateService.deleteBySpecificKey("WFL_STL_PAY_ORDERS", "WORKFLOW_ID",
                workflowId);
//        wflStlPayOrdersMapper.deleteByWorkflowId(workflowId);

        if (wflTrdProductDealAdd == null) {
            return new CommonResult(CommonResult.FAILD, "没有数据需要处理");
        }
        else {
            // 费用明细表
            mapperDelegateService.deleteBySpecificKey("TRD_TRADE_FEE_DETAIL", "TRADE_ID",
                    wflTrdProductDealAdd.getTradeId());
            FinTradeProductKey key = new FinTradeProductKey();
            key.setFinprodId(wflTrdProductDealAdd.getTradeId());
            key.setBranch(new Long(0));
            FinTradeProduct finTradeProduct = finTradeProductMapper.selectByPrimaryKey(key);
            if(finTradeProduct!=null){
                mapperDelegateService.deleteBySpecificKey("FIN_TRADE_PRODUCT", "FINPROD_ID",
                        wflTrdProductDealAdd.getTradeId());
//                finTradeProductMapper.deleteByPrimaryKey(key);
            }
        }
        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommonResult deleteWflData(String workflowId) {
        // 1.删除各业务流程信息
        // 汇总表
        wflTrdFinProductDealTotalMapper.deleteByWorkflowId(workflowId);
        // 汇总扩展表
        wflPaTrdFinDealTotalMapper.deleteByWorkflowId(workflowId);
        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        wflTrdProductDealMapper.deleteByWorkflowId(workflowId);

        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        wflTrdProductDealAddMapper.deleteByWorkflowId(workflowId);

        // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
        wflPaTrdProductDealMapper.deleteByWorkflowId(workflowId);

        // 金融产品交易业务流程其他信息Entity -> 金融产品交易业务流程其他信息表
        wflTrdProductDealOthMapper.deleteByWorkflowId(workflowId);

        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        wflStlPayOrdersMapper.deleteByWorkflowId(workflowId);

        return new CommonResult(CommonResult.SUCCED, "处理完成");
    }

    @Override
    public Integer insertPtlTrade(PtlTrade ptlTrade) {
        // 1.插入组合交易表
        return ptlTradeMapper.insert(ptlTrade);
    }
}
