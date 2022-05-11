package com.joyintech.tams.cfets.etr.bondfirstbid.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.joyintech.background.component.common.pack.common.util.ArithmeticUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dao.JoyinbusinessCommonMapper;
import com.joyintech.tams.business.common.dto.CfetsEtrFinProductInfoRequest;
import com.joyintech.tams.business.common.dto.CfetsEtrFinProductInfoResponse;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.service.EtrCommonFinProductInfoService;
import com.joyintech.tams.cfets.etr.bond.dao.CfetsEtrIbBondinfoMapper;
import com.joyintech.tams.cfets.etr.bond.service.CfetsEtrIbBondinfo002Service;
import com.joyintech.tams.cfets.etr.bondfirstbid.dao.CfetsEtrBondFirstBid001Mapper;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsBiddingProductDto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBid002Dto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBidReqDto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrIBondFirstBidFindEtrInfoDto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrIBondFirstBidFindEtrInfoRequest;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrIBondFirstBidFindIssueInfoDto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrIBondFirstBidFindIssueInfoRequest;
import com.joyintech.tams.common.base.dao.EtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustBidPriceMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustDetailMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.dao.WflEtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.WflEtrEntrustMapper;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustBidPrice;
import com.joyintech.tams.common.base.entity.EtrEntrustDetail;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.common.base.entity.SppiImportData;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 一级投标指令
 *
 * @Author: kchen
 * @Date: 2022/4/19 14:52
 */
@Service("CfetsEtrBondFirstBid001Service")
public class CfetsEtrBondFirstBid001Service extends BaseService {

    @Autowired
    private CfetsEtrBondFirstBid001Mapper cfetsEtrBondFirstBid001Mapper;

    @Autowired
    private CfetsEtrIbBondinfoMapper cfetsetrIbBondinfoMapper;

    @Resource
    private IPlatformDate iPlatformDate;

    @Autowired
    private CfetsEtrIbBondinfo002Service cfetsEtrIbBondinfo002Service;

    /**
     * 委托指令流程中Mapper
     */
    @Autowired
    private WflEtrEntrustMapper wflEtrEntrustMapper;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    @Autowired
    private WflEtrEntrustAddMapper wflEtrEntrustAddMapper;

    @Autowired
    private EtrEntrustBidPriceMapper etrEntrustBidPriceMapper;

    @Autowired
    private EtrEntrustDetailMapper etrEntrustDetailMapper;

    @Autowired
    private EtrCommonFinProductInfoService etrCommonFinProductInfoService;

    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;

    @Autowired
    private JoyinbusinessCommonMapper joyinbusinessCommonMapper;

    @Autowired
    private FinProductMapper finProductMapper;



    /**
     * 指令信息查询
     *
     * @param dto
     * @return
     */
    public Page<CfetsEtrIBondFirstBidFindEtrInfoDto> findEtrInfoPage(CfetsEtrIBondFirstBidFindEtrInfoRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue());
        pageParam.put("TRADE_MARKET_02", DictCodeListExt1.交易场所_商业银行柜台.getValue());
        pageParam.put("ENTRUST_STATUS_08", DictCodeListExt3.委托状态_流程中.getValue());
        pageParam.put("FINPROD_TYPE2", DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());

        List<String> statusList = dto.getEntrustStatus();
        if (!StringUtil.isEmptyList(statusList) && statusList.contains("all")) {
            dto.setEntrustStatus(null);
            pageParam.put("entrustStatus", null);
        }
        //组合
        String portfolioId = (String) pageParam.get("portfolioId");
        if (StringUtil.isNotEmpty(portfolioId)) {
            pageParam.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }
        //资产类型
        String finprodId = (String) pageParam.get("finprodId");
        if (StringUtil.isNotEmpty(finprodId)) {
            pageParam.put("finprodIdList", StringUtil.splitToList(GlobalConstant.COMMA, finprodId));
        }
        //发行方式
        String issueClass = (String) pageParam.get("issueClass");
        if (StringUtil.isNotEmpty(issueClass)) {
            pageParam.put("issueClassList", StringUtil.splitToList(GlobalConstant.COMMA, issueClass));
        }
        //投资经理
        String invManagerId = (String) pageParam.get("invManagerId");
        if (StringUtil.isNotEmpty(invManagerId)) {
            pageParam.put("invManagerIdList", StringUtil.splitToList(GlobalConstant.COMMA, invManagerId));
        }
        //交易对手
        String counterId = (String) pageParam.get("counterId");
        if (StringUtil.isNotEmpty(counterId)) {
            pageParam.put("counterIdList", StringUtil.splitToList(GlobalConstant.COMMA, counterId));
        }
        if (StringUtil.isNotEmpty(dto.getSearch())) {
            pageParam.put("search_isNumberFlag", BaseMap.isNumeric(dto.getSearch()));
        }

        Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.parse(DateUtil.getSysDate()));
        int year = c.get(Calendar.YEAR);
        pageParam.put("year", year);
        pageParam.put("sort", dto.getPageMode().getSort());
        pageParam.put("direction", dto.getPageMode().getDirection());
        String userId = SecurityUtil.getLoginUser().getUserName();

        if (StringUtils.isNotEmpty(userId)) {
            pageParam.put("userId", userId);
            return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsEtrBondFirstBid001Mapper.findEtrInfoPage(pageParam));
        } else {
            return null;
        }

    }

    /**
     * 查询SPPI测试结果
     *
     * @param finprodId
     * @return
     */
    public SppiImportData queryAssetIsSppiTest(String finprodId) {
        if (!StringUtil.isNotEmpty(finprodId)) {
            return null;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("finprodId", finprodId);
        return cfetsetrIbBondinfoMapper.queryAssetIsSppiTest(param);
    }

    /**
     * 查询待发行信息分页数据
     *
     * @param search
     * @return
     */
    public Page<CfetsEtrIBondFirstBidFindIssueInfoDto> findIssueInfoPage(CfetsEtrIBondFirstBidFindIssueInfoRequest search) {
        search.setPlatformDate(iPlatformDate.getSysDateStr());
        search.setFinprodType2(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());

        Map<String, Object> pageParam = BeanUtil.object2Map(search);
        pageParam.put("TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue());
        pageParam.put("TRADE_MARKET_02", DictCodeListExt1.交易场所_商业银行柜台.getValue());

        List<String> statusList = search.getIssueStage();
        if (!StringUtil.isEmptyList(statusList) && statusList.contains("all")) {
            search.setIssueStage(null);
            pageParam.put("issueStage", null);
        }

        PageHelper.startPage(search.getPageMode().getPageNo(), search.getPageMode().getPageSize());
        List<CfetsEtrIBondFirstBidFindIssueInfoDto> list = cfetsEtrBondFirstBid001Mapper.findIssueInfoPage(pageParam);
        return new Page<>(PageInfo.of(list));
    }

    /**
     * 查询业务模式
     *
     * @param portfolioId
     * @param secId
     * @return
     */
    public String queryInvAim(String portfolioId, String secId) {
        Map<String, Object> param = new HashMap<>();
        param.put("finprodId", secId);
        param.put("portfolioId", portfolioId);
        param.put("platDate", DateUtil.platDateFromCache());
        String invAim = cfetsEtrBondFirstBid001Mapper.findInvAimInEtr(param);
        return invAim;
    }

    /**
     * 资产检查
     *
     * @param finprodId
     * @return
     */
    public AjaxResult finprodIdCheck(String finprodId) {
        //查询投标日截止日/增发投标日截止日是否小于平台日
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> validList = new ArrayList<>();
        Date platDate = DateUtil.platDateFromCache();
        //查询新债标的信息
        if (StringUtil.isEmpty(platDate.toString())) {
            validList.add(new ValidMessage("资产投标日截止日/增发投标日截止日小于平台日"));
        }
        ajaxResult.setErrorList(validList);

        return ajaxResult;
    }

    /**
     * 查询证券账户
     *
     * @param portfolioId
     * @param finprodId
     * @return
     */
    public String queryPortfolioSecAcctId(String portfolioId, String finprodId) {
        if (StringUtil.isNotEmpty(portfolioId) && StringUtil.isNotEmpty(finprodId)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("portfolioId", portfolioId);
            map.put("finprodId", finprodId);
            // 证券账户
            String secAcctId = cfetsEtrIbBondinfo002Service.qryPortfolioSecAcctId(map);
            if (StringUtil.isNotEmpty(secAcctId)) {
                return secAcctId;
            }
        }
        return null;
    }

    /**
     * 根据指令代码查询指令新建信息查看界面
     * @param entrustId
     * @return
     */
    public CfetsEtrBondFirstBid002Dto getEtrEntrustInfo(String entrustId) {
        CfetsEtrBondFirstBid002Dto dto = new CfetsEtrBondFirstBid002Dto();

        //查询指令信息
        EtrEntrust etrEntrust = cfetsEtrBondFirstBid001Mapper.selectEntrustById(entrustId);
        if (etrEntrust == null ) {
            return dto;
        }
        //查询所有组合信息
        List<PtlPortfolio> ptlPortfolios = ptlPortfolioMapper.selectAllPortfolio();
        Map<String, String> ptlPortfolioMap = ptlPortfolios.stream()
                .collect(Collectors.toMap(PtlPortfolio::getPortfolioId,PtlPortfolio::getPortfolioName));

        dto.setFinprodId(etrEntrust.getFinprodId());
        dto.setEntrustDate(etrEntrust.getEntrustDate());
        dto.setRemark(etrEntrust.getRemark());
        dto.setTotalBiddingNum(ArithmeticUtil.multiply(etrEntrust.getEntrustNum(), BigDecimal.valueOf(100)));

        //查询资产信息
        CfetsEtrFinProductInfoRequest productInfoRequest = new CfetsEtrFinProductInfoRequest();
        productInfoRequest.setFinprodId(etrEntrust.getFinprodId());
        CfetsEtrFinProductInfoResponse finProductInfo = etrCommonFinProductInfoService.findFinProductInfo(null, null, productInfoRequest);

        dto.setIssueClass(finProductInfo.getIssueClass());
        if (StringUtil.equals(finProductInfo.getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue())) {
            dto.setIssueDate(finProductInfo.getIssueDate());
            dto.setPayDate(finProductInfo.getPayDate());
            dto.setIssuePrice(finProductInfo.getIssuePrice());
            dto.setReferYield(finProductInfo.getReferYield());

        }else {
            //查询所有的对手方
            List<SelectOptionDto> counterDtos = joyinbusinessCommonMapper.queryCounterPartyList(null);
            Map<String, String> counterMap = counterDtos.stream()
                    .collect(Collectors.toMap(SelectOptionDto::getId,SelectOptionDto::getText));

            dto.setTenderTarget(finProductInfo.getTenderTarget());
            dto.setTenderSdate(finProductInfo.getTenderSdate());
            dto.setTenderEdate(finProductInfo.getTenderEdate());
            dto.setIssueDate(finProductInfo.getIssueDate());
            dto.setPayEdate(finProductInfo.getPayEdate());
            dto.setCounterId(etrEntrust.getCounterId());
            dto.setCounterName(counterMap.get(etrEntrust.getCounterId()));
            dto.setTenderTarget(finProductInfo.getTenderTarget());
        }

        dto.setFinprodAbbr(finProductInfo.getFinprodAbbr());
        dto.setFinprodMarketId(finProductInfo.getFinprodMarketId());

        List<CfetsBiddingProductDto> biddingProductList = new ArrayList<>();
        //查询指令明细信息
        List<EtrEntrustDetail> etrEntrustDetails = cfetsEtrBondFirstBid001Mapper.selectEntrustDetailById(entrustId);

        if (StringUtil.equals(finProductInfo.getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue())) {
            dto.setIssueDate(finProductInfo.getIssueDate());
            dto.setPayDate(finProductInfo.getPayDate());

            for (EtrEntrustDetail detail : etrEntrustDetails) {
                CfetsBiddingProductDto productDto = new CfetsBiddingProductDto();
                productDto.setPortfolioId(detail.getPortfolioId());
                productDto.setPortfolioName(ptlPortfolioMap.get(detail.getPortfolioId()));
                productDto.setBusinessModel(detail.getInvAim());
                productDto.setBidPrinAmt(ArithmeticUtil.multiply(detail.getEntrustNum(), BigDecimal.valueOf(100)));
                productDto.setSecAcctId(detail.getSecAcctId());

                biddingProductList.add(productDto);
            }
        } else {
            if (finProductInfo.getIssueDate() != null) {
                dto.setIssueDate(finProductInfo.getIssueDate());
            } else {
                dto.setTenderSdate(finProductInfo.getTenderSdate());
                dto.setTenderEdate(finProductInfo.getTenderEdate());
            }
            dto.setPayEdate(finProductInfo.getPayEdate());

            //查询指令标位信息
            List<EtrEntrustBidPrice> bidPrices = cfetsEtrBondFirstBid001Mapper.selectBidPriceById(entrustId);
            for (EtrEntrustBidPrice bidPrice : bidPrices) {
                CfetsBiddingProductDto productDto = new CfetsBiddingProductDto();
                productDto.setPortfolioId(bidPrice.getPortfolioId());
                productDto.setPortfolioName(ptlPortfolioMap.get(bidPrice.getPortfolioId()));
                productDto.setBidPrice(bidPrice.getBidPrice());
                productDto.setBidPrinAmt(ArithmeticUtil.multiply(bidPrice.getBidPrinAmt(), BigDecimal.valueOf(100)));
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", bidPrice.getPortfolioId());
                map.put("finprodId", etrEntrust.getFinprodId());
                // 证券账户
                String secAcctId = cfetsEtrIbBondinfo002Service.qryPortfolioSecAcctId(map);
                if (StringUtil.isNotEmpty(secAcctId)) {
                    productDto.setSecAcctId(secAcctId);
                }
                EtrEntrustDetail etrEntrustDetail = etrEntrustDetails.stream()
                        .filter(x -> Objects.equals(x.getPortfolioId(),bidPrice.getPortfolioId())).findFirst().orElse(null);
                if (etrEntrustDetail != null) {
                    productDto.setBusinessModel(etrEntrustDetail.getInvAim());
                    productDto.setPortfolioTotalBidPrinAmt(ArithmeticUtil.multiply(etrEntrustDetail.getEntrustNum(), BigDecimal.valueOf(100)));
                }

                biddingProductList.add(productDto);
            }

        }

        dto.setBiddingProductList(biddingProductList);

        return dto;

    }

    /**
     * 指令修改校验
     * @param dto
     * @return
     */
    public AjaxResult modifyDelegateCheckMethod(CfetsEtrBondFirstBidReqDto dto) {
        if(!StringUtil.isNotEmpty(dto)|| StringUtil.isEmptyList(dto.getSelectedItem())){
            AjaxResult res=new AjaxResult();
            ValidMessage  validMessage= new ValidMessage();
            validMessage.setMessage(getMessage("COMMON_E_0002"));
            res.setErrorList(Arrays.asList(validMessage));
            return res;
        }
        List<CfetsEtrBondFirstBidReqDto> selectedItem = dto.getSelectedItem();
        AjaxResult ajaxResult=new AjaxResult();
        if(StringUtil.isNotEmpty(selectedItem)){
            if(selectedItem.size()>1){
                AjaxResult res=new AjaxResult();
                ValidMessage  validMessage= new ValidMessage();
                validMessage.setMessage("委托指令修改仅允许一条数据");
                res.setErrorList(Arrays.asList(validMessage));
                return res;
            }
            for(CfetsEtrBondFirstBidReqDto Item:selectedItem){
                if(!StringUtil.equals(Item.getEntrustStatus(), BussinessDictCodeList.委托接收状态_已撤回.getValue())
                        && !StringUtil.equals(Item.getEntrustStatus(),BussinessDictCodeList.委托状态_已退回.getValue())
                ){
                    AjaxResult res=new AjaxResult();
                    ValidMessage  validMessage= new ValidMessage();
                    validMessage.setMessage("只有指令状态为已撤回、已退回的指令允许修改");
                    res.setErrorList(Arrays.asList(validMessage));
                    return res;
                }
            };
        }
        return new AjaxResult();
    }

//    public Message<AjaxResult> closeCheckDelegate(CfetsEtrBondFirstBid002Dto dto) {
//        //对选择的数据进行check
//        AjaxResult ajaxResult = closeDelegateCheckMethod(dto);
//        return new Message<AjaxResult>().success().setData(ajaxResult);
//    }
//
//    /**
//     * 委托关闭检查
//     *
//     * @param dto
//     * @return
//     */
//    private AjaxResult closeDelegateCheckMethod(CfetsEtrBondFirstBid002Dto dto) {
//        if (CollectionUtils.isEmpty(dto.getSelectedItem())) {
//            AjaxResult res = new AjaxResult();
//            ValidMessage validMessage = new ValidMessage();
//            validMessage.setMessage(getMessage("COMMON_E_0002"));
//            res.setErrorList(Arrays.asList(validMessage));
//            return res;
//        }
//        List<CfetsEtrBondFirstBid002Dto> selectedItem = dto.getSelectedItem();
//        AjaxResult ajaxResult = new AjaxResult();
//        List<String> entrustIds = new ArrayList<>();
//        for (CfetsEtrBondFirstBid002Dto Item : selectedItem) {
//            if (StringUtil.equals(Item.getEntrustStatus(), BussinessDictCodeList.委托状态_流程中.getValue())) {
//                AjaxResult res = new AjaxResult();
//                ValidMessage validMessage = new ValidMessage();
//                validMessage.setMessage("流程中的数据不允许关闭");
//                res.setErrorList(Arrays.asList(validMessage));
//                return res;
//            }
//            if (StringUtil.isNotEmpty(Item.getEntrustId())) {
//                WflEtrEntrustKey wflEtrEntrustKey = new WflEtrEntrustKey();
//                wflEtrEntrustKey.setEntrustId(Item.getEntrustId());
//                wflEtrEntrustKey.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
//                WflEtrEntrust wflEtrEntrust = wflEtrEntrustMapper.selectByPrimaryKey(wflEtrEntrustKey);
//                if (wflEtrEntrust != null) {
//                    AjaxResult res = new AjaxResult();
//                    ValidMessage validMessage = new ValidMessage();
//                    validMessage.setMessage("存在流程中数据，无法关闭。");
//                    res.setErrorList(Arrays.asList(validMessage));
//                    return res;
//                }
//            }
//            entrustIds.add(Item.getEntrustId());
//        }
//        Map<String, Object> param = new HashMap<>();
//        param.put("entrustIds", entrustIds);
//        ajaxResult.setErrorList(entrustCheckCloseList(param));
//        return ajaxResult;
//    }
//
//    /**
//     * 委托关闭check
//     *
//     * @param param
//     * @return
//     */
//    private List<ValidMessage> entrustCheckCloseList(Map<String, Object> param) {
//        List<ValidMessage> errorList = new ArrayList<ValidMessage>();
//        @SuppressWarnings("unchecked")
//        List<String> entrustIds = (List<String>) param.get("entrustIds");
//        if (StringUtil.isNotEmpty(entrustIds)) {
//            for (String entrustId : entrustIds) {
//                String flag = "1";
//                if (StringUtil.equals("1", flag)) {
//                    errorList.add(new ValidMessage("已接收、已退回和已撤回,才能执行关闭操作！" + "指令编号：" + entrustId));
//                } else if (StringUtil.equals("2", flag)) {
//                    errorList.add(new ValidMessage("已接收指令，指令执行进度、成交进度要为0，且不存在在途委托/报价/交易，才可进行关闭。" + "指令编号：" + entrustId));
//                }
//            }
//        }
//        return errorList;
//    }
//
//    public Message<AjaxResult> closeDelegate(CfetsEtrBondFirstBid002Dto dto) {
//        return null;
//    }
}
