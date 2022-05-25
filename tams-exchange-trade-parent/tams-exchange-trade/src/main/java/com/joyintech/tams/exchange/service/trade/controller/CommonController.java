package com.joyintech.tams.exchange.service.trade.controller;

import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.ResponseResultBody;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.SysParamBean;
import com.joyintech.tams.exchange.common.bean.request.*;
import com.joyintech.tams.exchange.common.bean.response.CounterAcctResponse;
import com.joyintech.tams.exchange.common.bean.response.ExchangeContextResponse;
import com.joyintech.tams.exchange.common.bean.response.TradeInfoProResponse;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.domain.Message;
import com.joyintech.tams.exchange.service.trade.management.service.CommonManagement;
import com.joyintech.tams.exchange.util.MasterUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/trade/joyin")
@ResponseResultBody
@Api(tags = "场内交易-共通操作接口")
public class CommonController {
    @Autowired
    private CommonManagement commonManagement;

    @ApiOperation(value = "市场合约代码查询", notes = "市场合约代码查询")
    @PostMapping(value = "/common/instrument")
    @Deprecated
    public ResultBean queryInstrument(@RequestParam(value = "market", required = false) String market,
                                      @RequestParam(value = "asset_type", required = false) String assetType,
                                      @RequestParam(value = "keyword", required = false) String keyword) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObjectResultBean(null);
        }
        return commonManagement.queryInstrument(market, assetType, keyword);
    }

    @ApiOperation(value = "市场合约代码查询", notes = "根据交易场所查询市场合约代码，可传入关键词进行过滤")
    @PostMapping(value = "/common/instrumentByMarket")
    public Message<List<Instrument>> queryInstrumentByMarket(@RequestParam @NotBlank(message = "交易场所不可为空") String market,
                                                             @RequestParam(required = false) String keyword) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObject(null);
        }
        return new Message<>(commonManagement.queryInstrument(market, null, keyword));
    }

    @ApiOperation(value = "市场合约代码查询", notes = "根据资产类型查询市场合约代码，可传入关键词进行过滤")
    @PostMapping(value = "/common/instrumentByAssetType")
    public Message<List<Instrument>> queryInstrumentByAssetType(@RequestParam @NotBlank(message = "资产类型不可为空") String assetType,
                                                                @RequestParam(required = false) String keyword) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObject(null);
        }
        return new Message<>(commonManagement.queryInstrument(null, assetType, keyword));
    }

    @ApiOperation(value = "市场合约代码查询", notes = "根据关键字查询市场合约代码，不传关键词检索全部")
    @PostMapping(value = "/common/instrumentByKeyword")
    public Message<List<Instrument>> queryInstrumentByKeyword(@RequestParam(required = false) String keyword) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObject(null);
        }
        return new Message<>(commonManagement.queryInstrument(null, null, keyword));
    }

    @ApiOperation(value = "期货市场合约代码查询", notes = "期货市场合约代码查询")
    @PostMapping(value = "/common/futuresInstrument")
    public ResultBean queryInstrument(@RequestParam(value = "stock_code", required = false, defaultValue = "") String stockCode) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObjectResultBean(null);
        }
        return commonManagement.queryFuturesInstrument(stockCode);
    }

    @ApiOperation(value = "公平交易参数查询", notes = "公平交易参数查询, 01：开；02：关")
    @PostMapping(value = "/common/fairtrade")
    public Message<String> queryFairTradeParam() {
        return new Message<>(commonManagement.queryFairTradeParam());
    }

    @ApiOperation(value = "场内交易系统上下文查询", notes = "场内交易系统上下文查询")
    @PostMapping(value = "/common/exchangeContext")
    public Message<ExchangeContextResponse> queryExchangeContext() {
        return new Message<>(commonManagement.queryExchangeContext());
    }

    @ApiOperation(value = "获取系统平台日", notes = "获取系统平台日")
    @PostMapping(value = "/common/sysdate")
    public Message<Date> queryInvestSystemDate() {
        return new Message<>(commonManagement.queryInvestSystemDate());
    }

    @ApiOperation(value = "获取服务器时间", notes = "获取服务器时间")
    @PostMapping(value = "/common/localTime")
    public ResultBean queryLocalTime() {
        return new ResultBean(ErrorCode.SUCCESS, DateUtil.now());
    }

    @ApiOperation(value = "获取当前用户", notes = "获取当前用户")
    @PostMapping(value = "/common/localUser")
    public ResultBean queryLocalUser() {
        User user = SecurityUtil.getLoginUser();
        return new ResultBean(ErrorCode.SUCCESS, Objects.isNull(user) ? "lh1" : user.getUserName());
    }

    @ApiOperation(value = "获取系统参数", notes = "获取系统参数")
    @PostMapping(value = "/common/sysparam")
    public ResultBean querySysParam(@RequestParam @NotBlank(message = "参数编号不可为空") String paraCode,
                                    @RequestParam @NotBlank(message = "子系统编号不可为空") String systemCode) {
        SysParamBean bean = new SysParamBean(paraCode, systemCode);
        return new ResultBean(ErrorCode.SUCCESS, commonManagement.querySysParam(bean));
    }

    @PostMapping(value = "/common/service/node")
    public ResultBean queryServiceNode(@RequestParam(value = "node_name") String nodeName) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObjectResultBean(null);
        }
        return new ResultBean(ErrorCode.SUCCESS, commonManagement.queryServiceNode(nodeName));
    }

    @PostMapping(value = "/common/eureka/services")
    public ResultBean getEurekaServices(@RequestParam(value = "service_name") String serviceName) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObjectResultBean(null);
        }
        return new ResultBean(ErrorCode.SUCCESS, commonManagement.getEurekaServices(serviceName));
    }

    @ApiOperation(value = "获取主节点信息", notes = "获取主节点信息")
    @PostMapping(value = "/common/master")
    public ResultBean queryMasterAddress() {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObjectResultBean(null);
        }
        return commonManagement.queryMaster();
    }

    /**
     * 参数控制，是否启用redis以及是否同步写入db
     * 支持redis和写入db之前的搭配操作
     *
     * @param userId   用户id
     * @param useRedis 是否启用redis  false：不启用redis   true：启用redis
     * @param asyncDb  是否异步写入数据库 false: 同步写入数据库  true： 异步写入数据库
     * @return ResultBean
     */
    @ApiOperation(value = "是否启用redis以及是否同步写入db", notes = "是否启用redis以及是否同步写入db")
    @PostMapping(value = "/common/redis/use")
    public ResultBean changeDbType(@RequestParam(value = "user_id") String userId,
                                   @RequestParam(value = "use_redis", required = false) boolean useRedis,
                                   @RequestParam(value = "async_db", required = false) boolean asyncDb) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObjectResultBean(null);
        }
        return commonManagement.setDataServiceConfig(userId, asyncDb, useRedis);
    }

    @ApiOperation(value = "切换账户持仓检查状态", notes = "切换账户持仓检查状态")
    @PostMapping(value = "/common/position/check")
    public ResultBean changePositionCheck(@RequestParam(value = "user_id") String userId,
                                          @RequestParam(value = "position_check_flag", required = false) boolean positionCheckFlag) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObjectResultBean(null);
        }
        return commonManagement.setPositionCheckFlag(userId, positionCheckFlag);
    }


    /**
     * 取交易日
     *
     * @param tradeCalendarType 交易日类型        3-上交所 4-深交所
     * @param cDate             日期              格式：yyyyMMdd，如20200512
     * @param nDay              第N个交易日       可以传入正数、负数、0
     * @return ResultBean
     */
    @ApiOperation(value = "获取交易日信息", notes = "获取交易日信息")
    @PostMapping(value = "/common/trade/date")
    public ResultBean queryTradeDate(@RequestParam(value = "calender_type") String tradeCalendarType,
                                     @RequestParam(value = "current_date") String cDate,
                                     @RequestParam(value = "days") int nDay) {
        return commonManagement.getTradeDate(tradeCalendarType, cDate, nDay);
    }

    /**
     * @param tradeMarket 交易场所
     * @param tradePlat   交易平台
     * @param assetType   资产一级分类
     * @param assetType1  资产二级分类
     * @param assetType2  资产三级分类
     * @param finProdId   金融产品代码
     * @return ResultBean
     * @Description: 查询交易时间段数据
     */
    @ApiOperation(value = "获取交易时间段信息", notes = "获取交易时间段信息")
    @PostMapping(value = "/common/trade/time")
    public ResultBean queryTradeTime(@RequestParam(value = "trade_market", required = false) String tradeMarket,
                                     @RequestParam(value = "trade_plat", required = false) String tradePlat,
                                     @RequestParam(value = "asset_type", required = false) String assetType,
                                     @RequestParam(value = "asset_type1", required = false) String assetType1,
                                     @RequestParam(value = "asset_type2", required = false) String assetType2,
                                     @RequestParam(value = "declaration_type", required = false) String declarationType,
                                     @RequestParam(value = "fin_prod_id", required = false) String finProdId) {
        return commonManagement.queryTradeTime(tradeMarket, tradePlat, assetType, assetType1, assetType2, declarationType, finProdId);
    }

    @ApiOperation(value = "缓存交易时间段", notes = "缓存交易时间段")
    @PostMapping(value = "/common/trade/time/flush")
    public ResultBean tradeTimeCacheFlush() {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObjectResultBean(null);
        }
        commonManagement.minPriceInCache();
        commonManagement.minTrdUnitLimitInCache();
        return commonManagement.tradeTimeCache();
    }

    @ApiOperation(value = "查询指令明细信息", notes = "根据指令代码查询指令明细信息")
    @PostMapping(value = "/common/etrDispatcher/info")
    public Message<Map<String, Object>> getEtrDispatcherInfo(@RequestBody EtrDispatcherInfoRequest request) {
        return commonManagement.getEtrDispatcherInfo(request);
    }

    @ApiOperation(value = "查询偏离度", notes = "根据资产代码和净价，计算偏离度")
    @PostMapping(value = "/common/deviationDegree")
    public Message<String> getDeviationDegree(@RequestBody DeviationDegreeRequest request) {
        return commonManagement.getDeviationDegree(request);
    }

    @ApiOperation(value = "查询最新面值", notes = "根据资产代码查询最新面值")
    @PostMapping(value = "/common/getFaceValue")
    public Message<BigDecimal> getFaceValue(@RequestBody FaceValueRequest request) {
        return commonManagement.getFaceValue(request.getFinprodId());
    }

    @ApiOperation(value = "查询交易商交易员信息", notes = "查询交易商交易员信息")
    @PostMapping("/common/getDealerTraderInfo")
    public List<TradeInfoProResponse> selectTraderInfo(@RequestBody TradeInfoProRequest dto) {
        if (StringUtil.equals(dto.getIsOur(), "Y")) {
            return commonManagement.getDealerTraderInfo(dto);
        }
        return commonManagement.getCounterDealerTraderInfo(dto);
    }

    @ApiOperation(value = "检查本方交易商交易员信息", notes = "检查交易商交易员信息")
    @PostMapping("/common/checkSelfDealerTraderInfo")
    public Message checkSelfDealerTraderInfo(@RequestBody @Validated CheckTradeInfoRequest dto) {
        return commonManagement.checkSelfDealerTraderInfo(dto);
    }

    @ApiOperation(value = "查询对手方资金账户信息", notes = "查询对手方资金账户信息")
    @PostMapping("/common/getCounterAcctNoInfo")
    public List<CounterAcctResponse> selectCounterAcctNoInfo(@RequestBody @Validated CounterAcctRequest dto) {
        return commonManagement.selectCounterAcctNoInfo(dto);
    }

}
