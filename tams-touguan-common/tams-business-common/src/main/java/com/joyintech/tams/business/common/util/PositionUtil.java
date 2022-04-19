package com.joyintech.tams.business.common.util;

import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.constant.PositionDictCodeListExt;
import com.joyintech.tams.business.common.dto.PositionBondExecuteParam;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.CashPositionService;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class PositionUtil {

    private static Logger logger = LoggerFactory.getLogger(PositionUtil.class);

    private static PtlPortfolioMapper ptlPortfolioMapper;

    private static CashPositionService cashPositionService;

    /**
     * 平台日期
     */
    private static IPlatformDate iPlatformDate;

    /**
     * 初始化头寸通用信息
     * @param totalTradeId
     * @return
     */
    public static PositionBondExecuteParam initDto(String totalTradeId){
        PositionBondExecuteParam result = new PositionBondExecuteParam();
        result.setInterfaceType("Bond001"); // 规则根节点的编号，默认现券根节点
        result.setUserId(LoginUserUtil.getUserName());
//        result.setComputerId(PositionFutureManager.QUEUE_POSITION_CALLBACK); // 从配置文件获取，机器标识"queue.position.callback"
        result.setTotalTradeId(totalTradeId); // 委托或交易编号
        if (StringUtil.isEmpty(result.getBalanceCheck())){
            result.setBalanceCheck(PositionDictCodeListExt.是否_是.getValue()); // Y：需校验   N：不需校验, 初始化默认为Y
        }
        return result;
    }

    /**
     * 捕获异常处理
     * @param e
     * @return
     */
    public static Message ajaxErrorReturn(Exception e) {
        String message = e.getMessage();
        if (StringUtil.isNotEmpty(message)){
            String[] arr = message.split("\\|");
            if (arr != null && arr.length == 2){
                /**
                 * 头寸引擎业务失败，待前端确认是否校验
                 */
                if (StringUtil.equals(PositionHttpRequest.CODE_BUSI_RK, arr[0]) ||
                        StringUtil.equals(PositionHttpRequest.CODE_BUSI_YK, arr[0])){
                    // 头寸返回对象
                    BaseMap data = JsonUtils.json2Object(arr[1], BaseMap.class);

                    StringBuffer strb = null;
                    Map responseMap_data = (Map) data.get("data");
                    if (responseMap_data != null){
                        strb = new StringBuffer("【<br/>");
                        List checkResults = (List) responseMap_data.get("checkResults");
                        if (checkResults != null) {
                            for (int i=0; i<checkResults.size();i++){
                                BaseMap flow = BaseMap.newMap((Map) checkResults.get(i));
                                BigDecimal CHECKBALANCE = flow.getBigDecimal("CHECKBALANCE");
                                String UNIT = flow.getString("UNIT");
                                String PORTFOLIO_ID = flow.getString("PORTFOLIO_ID");
                                String HODING_TYPE = flow.getString("HODING_TYPE");
                                PtlPortfolio obj = ptlPortfolioMapper.selectByPrimaryKey(PORTFOLIO_ID);
                                String portfolioName = "";
                                if (obj != null) {
                                    portfolioName = obj.getPortfolioName();
                                }else{
                                    portfolioName=PORTFOLIO_ID;
                                }
                                strb.append(PORTFOLIO_ID);
                                strb.append("(");
                                strb.append(portfolioName);
                                strb.append(")");
//                                strb.append(CHECKBALANCE.toPlainString());
                                strb.append("【");
                                strb.append(SysDataUtil.getNameFromDataDict(DictCode.头寸资产类型, HODING_TYPE));
                                strb.append("】余额不足，当前余额为：").append(CHECKBALANCE.toPlainString()).append(UNIT.toString());
                                if ((i+1) == checkResults.size()){
                                    strb.append("<br/>");
                                }else{
                                    strb.append("；<br/>");
                                }
                            }
                        }
                    }

                    if (StringUtil.equals(PositionHttpRequest.CODE_BUSI_RK, arr[0])){
                        strb.append("】，请确认是否继续？");
                        /**
                         * 余额不足，软控确认
                         */
                        BaseMap bm = BaseMap.newMap()
                                .put("code", arr[0])
                                .put("message", strb)
                                .put("data", data);
                        return new Message().success().setData(bm);
                    }else {
                        strb.append("】");
                        /**
                         * 余额不足，硬控提示
                         */
                        return new Message().fail().setMessage(strb.toString());
                    }
                }
            }else {
                return new Message().fail().setMessage(message).setData(null);
            }
        }
        return new Message().fail().setMessage("无返回信息，未知异常").setData(null);
    }

    /**
     * 头寸C_02、 C_03、 C_04不足需要再校验
     * @param req
     * @param msgMap
     * @return
     */
    public static boolean checkForFL0007(Object req, Map msgMap) {
        BaseMap tamsReq = JsonUtils.json2Object(JsonUtils.object2Json(req), BaseMap.class);

        Map responseMap_data = (Map) msgMap.get("data");
        Date CDATE = iPlatformDate.getSysDate();
        if (responseMap_data != null){
            List checkResults = (List) responseMap_data.get("checkResults");
            List newcheckResults = new ArrayList();
            if (checkResults != null) {
                for (int i=0; i<checkResults.size();i++){
                    BaseMap flow = BaseMap.newMap((Map) checkResults.get(i));
                    String HODING_TYPE = flow.getString("HODING_TYPE");
                    String PORTFOLIO_ID = flow.getString("PORTFOLIO_ID");
//                    Date CDATE = flow.getDate("CDATE");
//                    if (CDATE == null){
//                        CDATE=iPlatformDate.getSysDate();
//                    }
                    logger.info("CDATE平台日期：" + CDATE);
                    logger.info("CDATE平台日期：" + DateUtil.format(CDATE));
                    if (StringUtil.equals(HODING_TYPE, DictCodeListExt3.资产类型_托管户可用资金.getValue())
//                            || StringUtil.equals(HODING_TYPE, DictCodeListExt3.资产类型_证券户可取资金.getValue())
//                            || StringUtil.equals(HODING_TYPE, DictCodeListExt3.资产类型_证券户可用资金.getValue())
                    ){
                        // 需要校验投管现金
                        // 校验仍不足时add
                        BaseMap paramMap = BaseMap.newMap()
                                .put("portfolioId", PORTFOLIO_ID)
                                .put("ccy", DictCodeList.币种_人民币_CNY.getValue())
                                .put("vdate", CDATE)
                                ;
                        BigDecimal doAdd = BigDecimal.ZERO;
                        // 原投管余额
                        BigDecimal cashT = BigDecimal.ZERO;
                        if (StringUtil.equals(DictCodeListExt1.子系统代码_委托.getValue(), tamsReq.getString("modelType"))){
                            cashT = cashPositionService.etrQryCommandAvailableCash(paramMap);
                        }else if (StringUtil.equals(DictCodeListExt1.子系统代码_交易.getValue(), tamsReq.getString("modelType"))){
                            cashT = cashPositionService.trdQryCommandAvailableCash(paramMap);
                        }
                        // 头寸余额
                        BigDecimal CHECKBALANCE = flow.getBigDecimal("CHECKBALANCE");
                        // 发生额
                        BigDecimal tamsCash = getCashByPId(PORTFOLIO_ID, tamsReq);
                        doAdd = doAdd.add(ArithmeticUtil.doAdd(cashT, CHECKBALANCE));
                        BigDecimal doSub = doAdd.subtract(tamsCash);
                        if (doSub.compareTo(BigDecimal.ZERO) >= 0){
                            // 头寸+投管余额足够
                            continue;
                        }else{
                            flow.put("CHECKBALANCE", doAdd);
                            newcheckResults.add(flow);
                        }
                    }else{
                        newcheckResults.add(flow);
                    }
                }
            }
            if (newcheckResults.isEmpty()){
                // 不存在最终余额不足情况，重发
                tamsReq.put("balanceCheck", "N");
                PositionHttpRequest.send(tamsReq);
                return true;
            }else{
                responseMap_data.put("checkResults", newcheckResults);
                return false;
            }
        }
        return false;
    }

    /**
     * 从请求数据中获取金额
     * @param PORTFOLIO_ID
     * @param tamsReq
     * @return
     */
    private static BigDecimal getCashByPId(String PORTFOLIO_ID, BaseMap tamsReq){
        BigDecimal rs = BigDecimal.ZERO;
        if (tamsReq != null) {
            List list = (List) tamsReq.get("ptlList");
            if(StringUtil.isEmptyList(list)){
                list = (List) tamsReq.get("ptlCashList");
            }
            if(StringUtil.isEmptyList(list)){
                list = (List) tamsReq.get("ptlTrusteeshipList");
            }
            if (list != null && !list.isEmpty()){
                for (int i=0;i<list.size(); i++){
                    BaseMap each = BaseMap.newMap((Map) list.get(i));
                    if (StringUtil.equals(PORTFOLIO_ID, each.get("portfolioId"))){
                        return each.getBigDecimal("cashAmount");
                    }
                }
            }
        }
        return null;
    }

    @Autowired
    public void setPtlPortfolioMapper(PtlPortfolioMapper ptlPortfolioMapper) {
        PositionUtil.ptlPortfolioMapper = ptlPortfolioMapper;
    }
    @Autowired
    public void setCashPositionService(CashPositionService cashPositionService) {
        PositionUtil.cashPositionService = cashPositionService;
    }
    @Autowired(required = false)
    public void setiPlatformDate(IPlatformDate iPlatformDate) {
        PositionUtil.iPlatformDate = iPlatformDate;
    }
}

