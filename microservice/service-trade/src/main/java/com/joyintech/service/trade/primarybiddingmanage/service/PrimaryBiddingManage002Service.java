package com.joyintech.service.trade.primarybiddingmanage.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.tams.common.base.dao.MstCounterPartyMapper;
import com.joyintech.tams.common.base.entity.MstCounterParty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.service.trade.primarybiddingmanage.dao.PrimaryBiddingManage002Mapper;
import com.joyintech.service.trade.primarybiddingmanage.dto.PrimaryBiddingManageDto;
import com.joyintech.service.trade.primarybiddingmanage.dto.PrimaryBiddingPortfolioDto;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;

@Service("backPrimaryBiddingManage002Service")
public class PrimaryBiddingManage002Service extends BaseService<PrimaryBiddingManageDto> {

    @Autowired
    private PrimaryBiddingManage002Mapper primaryBiddingManage002Mapper;
    @Autowired
    private MstCounterPartyMapper mstCounterPartyMapper;
    /**
     * 获取entity <功能详细描述>
     * 
     * @param map 参数
     * @return 返回值
     * @see
     * @since 1.0
     */
    public PrimaryBiddingManageDto getEntityByMap(Map<String, Object> map) {
        // 返回DTO
        PrimaryBiddingManageDto primaryBiddingManageDto = new PrimaryBiddingManageDto();
        map.put("pinanassettypeyj", BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        map.put("entruststatusyjs", BussinessDictCodeList.委托状态_已接收.getValue());
        map.put("referencemodel", GlobalConstant.DETAIL_PAGE_MODEL_REFERENCE);

        // 查询汇总投标确认页面基本信息
        primaryBiddingManageDto = primaryBiddingManage002Mapper.getEntityByMap(map);
        primaryBiddingManageDto.setAllBidPrinAmt(new BigDecimal(map.get("allBidPrinAmt").toString()));

        // 查询投标组合列表,根据资产、交易对手
        List<PrimaryBiddingPortfolioDto> primaryBiddingPortfolioDtolist = new ArrayList<PrimaryBiddingPortfolioDto>();
        primaryBiddingPortfolioDtolist = primaryBiddingManage002Mapper.getPrimaryBiddingPortfoliolist(map);
        primaryBiddingManageDto.setPrimaryBiddingPortfolioDtolist(primaryBiddingPortfolioDtolist);

        // 查询汇总投标列表,根据资产、交易对手
        List<PrimaryBiddingPortfolioDto> primaryBiddingAlllist = new ArrayList<PrimaryBiddingPortfolioDto>();
        primaryBiddingAlllist = primaryBiddingManage002Mapper.getPrimaryBiddingAlllist(map);
        primaryBiddingManageDto.setPrimaryBiddingAllDtolist(primaryBiddingAlllist);

        return primaryBiddingManageDto;
    }

    /**
     * 获取指令状态 <功能详细描述>
     * 
     * @param map 参数
     * @return 返回值
     * @see
     * @since 1.0
     */
    public PrimaryBiddingManageDto getPrimaryBiddingManageOriginal(Map<String, Object> map) {
        map.put("bidstatusyzb", BussinessDictCodeList.中标状态_已中标.getValue());
        map.put("TENDERCONFIRMSTATUS02", BussinessDictCodeList.确认状态_已确认.getValue());
        map.put("TENDERCONFIRMSTATUS03", BussinessDictCodeList.确认状态_未中标.getValue());
        map.put("TENDERCONFIRMSTATUS04", BussinessDictCodeList.确认状态_已中标.getValue());
        return primaryBiddingManage002Mapper.getPrimaryBiddingManageOriginal(map);
    }
    public String getCounterName(Map<String, Object> map){
        MstCounterParty mstCounterParty = mstCounterPartyMapper.selectByPrimaryKey(map.get("counterId").toString());
        if (StringUtil.isNotEmpty(mstCounterParty)){
            return mstCounterParty.getCounterName();
        }
        return null;
    }
}
