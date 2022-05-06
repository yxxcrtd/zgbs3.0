package com.joyintech.tams.invest.common.position.service;

import static com.joyintech.tams.constant.DictCodeList.交易平台_大宗交易平台;
import static com.joyintech.tams.constant.DictCodeList.交易平台_竞价撮合平台;
import static com.joyintech.tams.constant.DictCodeList.交易平台_综合协议交易平台;
import static com.joyintech.tams.constant.DictCodeList.交易方向_买入;
import static com.joyintech.tams.constant.DictCodeList.交易方向_卖出;
import static com.joyintech.tams.constant.DictCodeListExt3.申报类型_不指定;
import static com.joyintech.tams.constant.DictCodeListExt3.申报类型_定价申报;
import static com.joyintech.tams.constant.DictCodeListExt3.申报类型_意向申报;
import static com.joyintech.tams.constant.DictCodeListExt3.申报类型_成交申报;
import static com.joyintech.tams.constant.DictCodeListExt3.申报类型_收盘价固定价格申报;
import static com.joyintech.tams.constant.DictCodeListExt3.申报类型_日均价固定价格申报;
import static com.joyintech.tams.constant.DictCodeListExt3.申报类型_点击成交申报;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.INTERF_TYPE_BOND002;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.INTERF_TYPE_BOND003;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.joyintech.tams.common.base.dao.EtrEntrustAddMapper;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.FinProductTypeKey;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.position.dao.InterfPositionConfigMapper;
import com.joyintech.tams.invest.common.position.dto.InterfPositionConfigDto;

@Service
public class CommonPositionEngineService {

    private static final Logger log = LoggerFactory.getLogger(CommonPositionEngineService.class);
    private static final Map<String, InterfPositionConfigDto> CONFIG_MAP = new ConcurrentHashMap<String, InterfPositionConfigDto>();

    /**
     * 获取mapper对象
     */
    @Autowired
    private InterfPositionConfigMapper interfPositionConfigMapper;
    @Autowired
    private FinProductAddMapper finProductAddMapper;
    @Autowired
    private FinProductTypeMapper finProductTypeMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;
    /**
     * 获取结算方式
     *
     * @param finprodId
     * @return
     */
    public String getDeliveryType(String finprodId) {
        String deliveryType = StringUtil.EMPTY;
        FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(finprodId);
        String isNoGuard = finProductAdd.getIsNoGuard();
        if (StringUtil.isNotEmpty(isNoGuard) && isNoGuard.contains(DictCodeList.是否_否.getValue())) {
            deliveryType = DictCodeListExt3.结算方式_头寸引擎_净额结算.getValue();
        } else {
            deliveryType = DictCodeListExt3.结算方式_头寸引擎_RTGS.getValue();
        }
        return deliveryType;
    }

    /**
     * 解析清算速度
     * <p>
     * 清算速度	见数据字典：清算速度
     * "IF 变量.结算方式 = 数据字典.DELIVERY_TYPE.净额结算 THEN 数据字典.清算速度.T+1
     * ELSE IF 变量.结算方式 = 数据字典.DELIVERY_TYPE.RTGS THEN 数据字典.清算速度.T+0
     * ELSE 数据字典.清算速度.T+1
     *
     * @param deliveryType 结算方式
     * @return 清算速度
     */
    public String parseSettleSpreed(String deliveryType) {
        String settleSpreed = BussinessDictCodeList.清算速度_T加1.getCodeName();
        if (DictCodeListExt3.结算方式_头寸引擎_净额结算.getValue().equals(deliveryType)) {
            settleSpreed = BussinessDictCodeList.清算速度_T加1.getValue();
        } else if ((DictCodeListExt3.结算方式_头寸引擎_RTGS.getValue()).equals(deliveryType)) {
            settleSpreed = BussinessDictCodeList.清算速度_T加0.getValue();
        }
        return settleSpreed;
    }

    public BigDecimal getSecAmountByBondUnit(Long entrustNum) {
        return this.getSecAmountByBondUnit(BigDecimal.valueOf(entrustNum));
    }

    /**
     * 根据指令单位是张还是手来处理交易金额
     * 债券使用
     *
     * @param entrustNum 委托数量
     */
    public BigDecimal getSecAmountByBondUnit(BigDecimal entrustNum) {
        BigDecimal secAmount = BigDecimal.ZERO;
        if (Objects.isNull(entrustNum)) {
            return secAmount;
        }
//        String bondTradedUnit = CacheUtil.getSysParam("bond_traded_unit");
//        if (交易单位_张.getValue().equals(bondTradedUnit)) {
//            secAmount = entrustNum.multiply(BigDecimal.valueOf(100L));
//        } else if (交易单位_份.getValue().equals(bondTradedUnit)) {
//            secAmount = entrustNum.multiply(BigDecimal.valueOf(1000L));
//        }
        return entrustNum.multiply(BigDecimal.valueOf(100L));
    }

    /**
     * 获取type6
     *
     * @param finprodId
     * @return
     */
    public String getType6(String finprodId) {
        FinProductTypeKey key = new FinProductTypeKey();
        key.setFinprodId(finprodId);
        key.setBranch((long) 0);
        // String type6 = finProductTypeMapper.selectByPrimaryKey(key).getType6();
        FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
        return finProductType != null ? finProductType.getType6() : StringUtil.EMPTY;
    }

    /**
     * 查询 头寸接口配置
     *
     * @return
     */
    public List<InterfPositionConfigDto> searchInterfPositionConfig() {
        return interfPositionConfigMapper.searchInterfPositionConfig();
    }

    /**
     * 获取头寸接口类型
     *
     * @param positionType
     * @param tradePlat
     * @param finprodType
     * @param ps
     * @param busiAct
     * @param entity
     * @return
     */
    public String getPositionInterType(String positionType, String tradePlat, String finprodType, String ps, String busiAct, String entity) {
        log.info("getPositionInterType() called with parameters => 【positionType = {}】, 【tradePlat = {}】, 【finprodType = {}】, 【ps = {}】, 【busiAct = {}】, 【entity = {}】", positionType, tradePlat, finprodType, ps, busiAct, entity);
        String key = positionType + tradePlat + finprodType + ps + busiAct + entity;
        InterfPositionConfigDto interfPositionConfigDto = CONFIG_MAP.get(key);
        if (interfPositionConfigDto == null || cacheInvalid(interfPositionConfigDto.getUpdateTime())) {
            InterfPositionConfigDto where = new InterfPositionConfigDto();
            where.setPositionType(positionType);
            where.setTradePlat(tradePlat);
            where.setFinprodType(finprodType);
            where.setPs(ps);
            where.setBusiAct(busiAct);
            where.setEntity(entity);
            List<InterfPositionConfigDto> list = interfPositionConfigMapper.searchInterfPositionConfigByWhere(where);
            if (null != list && list.size() > 0) {
                InterfPositionConfigDto dto = list.get(0);
                dto.setUpdateTime(new Date());
                CONFIG_MAP.put(key, dto);
            } else {
                CONFIG_MAP.remove(key);
            }
        }
        InterfPositionConfigDto configDto = CONFIG_MAP.get(key);
        if (configDto != null) {
            return configDto.getInterfType();
        }
        return null;
    }

    /**
     * 获取交易系统SharesTradePlatform 【02 竞价交易；04 大宗交易】
     *
     * @param tradePlace 交易平台
     * @param ps         交易方向
     * @param declaType  申报类型
     * @return 交易系统
     */
    public String getSharesTradePlatform(String tradePlace, String ps, String declaType, String entrustProp) {
        if (isSharesTradePlatform(tradePlace, ps, declaType, entrustProp)) {
            return 交易平台_大宗交易平台.getValue();
        }
        return 交易平台_竞价撮合平台.getValue();
    }

    /**
     * 获取场内交易平台
     * @param entrustId
     * @param tradePlat
     * @param ps
     * @param interfaceType
     * @return
     */
    public String getExchangeTradePlatform(String entrustId, String tradePlat, String ps, String finprodId, String interfaceType){
        // 债券接口调用头寸引擎判断逻辑使用是否非担保进行判断是竞价交易还是大宗交易
        if(INTERF_TYPE_BOND002.equals(interfaceType) || INTERF_TYPE_BOND003.equals(interfaceType)){
            FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(finprodId);
            return DictCodeList.是否_是.getValue().equals(finProductAdd.getIsNoGuard()) ?
                    DictCodeList.交易平台_大宗交易平台.getValue() : DictCodeList.交易平台_竞价撮合平台.getValue();
        }else{
            if(StringUtil.isEmpty(entrustId)){ // 存续期无指令
                return tradePlat;
            }else{
                EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entrustId);
                if((DictCodeList.交易平台_大宗交易平台.getValue().equals(tradePlat)
                        ||DictCodeList.交易平台_综合协议交易平台.getValue().equals(tradePlat))
                        &&(DictCodeList.交易方向_买入.getValue().equals(ps)
                        ||DictCodeList.交易方向_卖出.getValue().equals(ps))
                        &&(DictCodeListExt3.申报类型_意向申报.getValue().equals(etrEntrustAdd.getDealMode())
                        ||DictCodeListExt3.申报类型_定价申报.getValue().equals(etrEntrustAdd.getDealMode())
                        ||DictCodeListExt3.申报类型_点击成交申报.getValue().equals(etrEntrustAdd.getDealMode())
                        ||DictCodeListExt3.申报类型_成交申报.getValue().equals(etrEntrustAdd.getDealMode())
                        ||DictCodeListExt3.申报类型_日均价固定价格申报.getValue().equals(etrEntrustAdd.getDealMode())
                        ||DictCodeListExt3.申报类型_收盘价固定价格申报.getValue().equals(etrEntrustAdd.getDealMode())
                        ||DictCodeListExt3.申报类型_不指定.getValue().equals(etrEntrustAdd.getDealMode()))){
                    return DictCodeList.交易平台_大宗交易平台.getValue();
                }else {
                    return DictCodeList.交易平台_竞价撮合平台.getValue();
                }
            }
        }
    }

    public BigDecimal multiplyWithOneHundred(Long value) {
        return multiplyWithOneHundred(BigDecimal.valueOf(value));
    }

    public BigDecimal multiplyWithOneHundred(BigDecimal value) {
        if (Objects.isNull(value)) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(100L).multiply(value);
    }

    private boolean isSharesTradePlatform(String tradePlace, String ps, String declaType, String entrustProp) {
        return isBulkTradePlace(tradePlace) && isBulkPs(ps) && (isBulkDeclaType(declaType) || isBulkEntrustProp(entrustProp));
    }

    private boolean isBulkTradePlace(String tradePlace) {
        return 交易平台_大宗交易平台.getValue().equals(tradePlace) || 交易平台_综合协议交易平台.getValue().equals(tradePlace);
    }

    private boolean isBulkPs(String ps) {
        return 交易方向_买入.getValue().equals(ps) || 交易方向_卖出.getValue().equals(ps);
    }

    private boolean isBulkDeclaType(String declaType) {
        return 申报类型_不指定.getValue().equals(declaType)
                || 申报类型_意向申报.getValue().equals(declaType)
                || 申报类型_定价申报.getValue().equals(declaType)
                || 申报类型_点击成交申报.getValue().equals(declaType)
                || 申报类型_成交申报.getValue().equals(declaType)
                || 申报类型_收盘价固定价格申报.getValue().equals(declaType)
                || 申报类型_日均价固定价格申报.getValue().equals(declaType)
                ;
    }
    
    private boolean isBulkEntrustProp(String entrustProp) {
        return "a".equals(entrustProp)
                || "b".equals(entrustProp)
                || "c".equals(entrustProp)
                || "e".equals(entrustProp)
                || "AFC".equals(entrustProp)
                || "AFW".equals(entrustProp)
                ;
    }

    /**
     * 获取头寸业务类型--日间使用
     *
     * @param finprodType2
     * @param ps
     * @return
     */
    public String getPositionBusiType(String finprodType2, String ps, String assetType2) {
        log.debug("getPositionBusiType() called with parameters => 【finprodType2 = {}】, 【ps = {}】, 【assetType2 = {}】", finprodType2, ps, assetType2);
        String busiType = "";
        if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType2)) {
            if (BussinessDictCodeList.交易方向_行权回售.getValue().equals(ps)) {
                busiType = DictCodeListExt3.头寸业务类型_债券行权交易.getValue();
            } else if (BussinessDictCodeList.交易方向_买入.getValue().equals(ps) || BussinessDictCodeList.交易方向_卖出.getValue().equals(ps)) {
                busiType = DictCodeListExt3.头寸业务类型_现券交易.getValue();
            } else if (DictCodeList.交易方向_申购.getValue().equals(ps)) {
                busiType = DictCodeListExt3.头寸业务类型_网上申购.getValue();
            } else if (DictCodeListExt3.交易方向_配债.getValue().equals(ps)) {
                busiType = DictCodeListExt3.头寸业务类型_配债.getValue();
            } else if (DictCodeListExt3.交易方向_债转股.getValue().equals(ps)) {
                busiType = DictCodeListExt3.头寸业务类型_债转股.getValue();
            } else if (DictCodeListExt3.交易方向_质押券入库.getValue().equals(ps)) {
                busiType = DictCodeListExt3.头寸业务类型_通用回购债券入库.getValue();
            } else if (DictCodeListExt3.交易方向_质押券出库.getValue().equals(ps)) {
                busiType = DictCodeListExt3.头寸业务类型_通用回购债券出库.getValue();
            }
        }
        if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(finprodType2)) {
            if (DictCodeList.交易方向_申购.getValue().equals(ps)) {
                busiType = DictCodeListExt3.头寸业务类型_网上申购.getValue();
            } else if (BussinessDictCodeList.交易方向_买入.getValue().equals(ps) || BussinessDictCodeList.交易方向_卖出.getValue().equals(ps)) {
                busiType = DictCodeListExt3.头寸业务类型_股票交易.getValue();
            } else if (DictCodeListExt2.交易方向_配股.getValue().equals(ps)) {
                busiType = DictCodeListExt3.头寸业务类型_配股.getValue();
            }
        }
        if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(finprodType2)) {
            busiType = DictCodeListExt3.头寸业务类型_基金交易.getValue();
        }
        if ((DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(finprodType2) || DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(finprodType2))
                && DictCodeList.回购类型_质押式竞价回购.getValue().equals(assetType2)) {
            busiType = DictCodeListExt3.头寸业务类型_通用回购首期.getValue();
        }
        if ((DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(finprodType2) || DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(finprodType2))
                && DictCodeList.回购类型_质押式协议回购.getValue().equals(assetType2)) {
            if (DictCodeListExt3.交易方向_首期.getValue().equals(ps)
                    || DictCodeListExt3.交易方向_正回购.getValue().equals(ps)
                    || DictCodeListExt3.交易方向_逆回购.getValue().equals(ps)) {
                busiType = DictCodeListExt3.头寸业务类型_协议回购首期.getValue();
            } else if (DictCodeListExt3.交易方向_提前购回.getValue().equals(ps)) {
                busiType = DictCodeListExt3.头寸业务类型_协议回购提前购回.getValue();
            } else if (DictCodeListExt3.交易方向_到期购回.getValue().equals(ps)) {
                busiType = DictCodeListExt3.头寸业务类型_协议回购到期购回.getValue();
            } else if (DictCodeListExt3.交易方向_到期续做.getValue().equals(ps)) {
                busiType = DictCodeListExt3.头寸业务类型_协议回购到期续作.getValue();
            } else if (DictCodeListExt3.交易方向_解除质押.getValue().equals(ps)) {
                busiType = DictCodeListExt3.头寸业务类型_协议回购解除质押.getValue();
            } else if (DictCodeListExt3.交易方向_中途换券.getValue().equals(ps)) {
                busiType = DictCodeListExt3.头寸业务类型_协议回购中途换券.getValue();
            }
        }
        return busiType;
    }

    /**
     * 获取头寸业务类型--存续期使用
     *
     * @param finprodType2
     * @param ps
     * @return
     */
    public String getDurationPositionBusiType(String finprodType2, String ps, String assetType2) {
        log.debug("getPositionBusiType() called with parameters => 【finprodType2 = {}】, 【ps = {}】, 【assetType2 = {}】", finprodType2, ps, assetType2);
        String busiType = "";
        if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType2)) {
            if(DictCodeList.交易方向_申购确认.getValue().equals(ps)){
                busiType = DictCodeListExt3.头寸业务类型_网上申购.getValue();
            }else if(DictCodeList.交易方向_行权_赎回.getValue().equals(ps)){
                busiType = DictCodeListExt3.头寸业务类型_债券行权交易.getValue();
            }else if(DictCodeList.交易方向_上市流通.getValue().equals(ps)){
                busiType = DictCodeListExt3.头寸业务类型_上市流通.getValue();
            }else{
                busiType = DictCodeListExt3.头寸业务类型_债券存续期交易.getValue();
            }
        }
        if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(finprodType2)) {
            if(DictCodeList.交易方向_现金分红.getValue().equals(ps)){
                busiType = DictCodeListExt3.头寸业务类型_股票分红.getValue();
            }else if(DictCodeList.交易方向_红利再投.getValue().equals(ps)){
                busiType = DictCodeListExt3.头寸业务类型_股票送股.getValue();
            }else if(DictCodeList.交易方向_上市流通.getValue().equals(ps)){
                busiType = DictCodeListExt3.头寸业务类型_上市流通.getValue();
            }else if(DictCodeList.交易方向_申购确认.getValue().equals(ps)){
                busiType = DictCodeListExt3.头寸业务类型_网上申购.getValue();
            }
        }
        if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(finprodType2)) {
            busiType = DictCodeListExt3.头寸业务类型_基金存续期.getValue();
        }
        if ((DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(finprodType2)
                || DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(finprodType2))) {
            if(DictCodeList.回购类型_质押式协议回购.getValue().equals(assetType2)){
                busiType = DictCodeListExt3.头寸业务类型_协议回购到期购回.getValue();
            }else{
                busiType = DictCodeListExt3.头寸业务类型_通用回购到期.getValue();
            }
        }
        return busiType;
    }

    /**
     * 判断缓存是否过期
     *
     * @param date
     * @return
     */
    private boolean cacheInvalid(Date date) {
        if (date == null) {
            return true;
        }
        Date cacheTime = new Date(date.getTime() + 60 * 1000);//TODO生产环境时间调长，或者从系统变量中取值
        Date nowTime = new Date();
        if (nowTime.after(cacheTime)) {
            return true;
        } else {
            return false;
        }
    }

}
