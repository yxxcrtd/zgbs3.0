package com.joyintech.tams.exchange.service.trade.controller;

import cn.hutool.core.util.NumberUtil;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.constant.EntrustBsProp;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3;
import com.joyintech.tams.exchange.common.db.domain.Algorithm;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.domain.Message;
import com.joyintech.tams.exchange.common.utils.EntrustPropUtils;
import com.joyintech.tams.exchange.datacommon.db.service.InstructionService;
import com.joyintech.tams.exchange.service.trade.management.service.OrderManagement;
import com.joyintech.tams.exchange.service.trade.processor.check.CommonCheckKit;
import com.joyintech.tams.exchange.util.MasterUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3.投管业务类型_质押券出入库;

@RestController("OrderController_dtx")
@RequestMapping("/trade/joyin")
@Api(tags = "场内交易-委托操作接口")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderManagement orderManagement;
    @Autowired
    private InstructionService instructionService;
    @Autowired
    private CommonCheckKit commonCheckKit;

//    /**
//     * 查询订单信息
//     *
//     * @param userId        用户Id
//     * @param instructionId 指令Id
//     * @param accountId     账户Id
//     * @return ResultBean
//     */
//    @RequestMapping(value = {"/order"}, method = RequestMethod.GET)
//    public Message<List<Order>> queryAll(@RequestParam(value = "userId") String userId,
//                               @RequestParam(value = "instructionId", required = false, defaultValue = "") String instructionId,
//                               @RequestParam(value = "accountId", required = false, defaultValue = "") String accountId) {
//        return new Message<List<Order>>(orderManagement.queryOrder(userId, instructionId, accountId));
//    }

    /**
     * 普通下单
     *
     * @param order 下单信息
     * @return Message
     */
    @ApiOperation(value = "普通下单（竞价下单）", notes = "普通下单（竞价下单）")
    @PostMapping(value = "/order")
    public Message<OrderPlaceBean> sendOrder(@RequestBody OrderPlaceBean order) {
        if (isSlave()) {
            return MasterUtil.postForObject(order);
        }

        /*ResultBean checkResult = commonCheckKit.check(order);
        if (ErrorCode.isFail(checkResult.getErrorCode())) {
            return new Message<>(checkResult);
        }*/

        Instruction instruction = instructionService.findById(order.getInstructionId());

        if (投管业务类型_质押券出入库.getValue().equals(instruction.getBusiTypeTg())) {
            order.setEntrustPrice(NumberUtil.div(order.getEntrustPrice(), 100));
        }

        String entrustBs = EntrustBsProp.getEntrustBs(instruction.getPs());
        logger.debug("普通下单（竞价下单）买卖方向（entrustBs）转换：{} ==> {}, ", order.getEntrustBs(), entrustBs);
        order.setEntrustBs(entrustBs);

        String entrustProp = EntrustPropUtils.getEntrustProp(instruction, order.getEntrustProp());
        logger.debug("普通下单（竞价下单）委托属性（entrustProp）转换：{} ==> {}, ", order.getEntrustProp(), entrustProp);
        order.setEntrustProp(entrustProp);

        return new Message<>(orderManagement.sendOrder(order.getUserId(), order));
    }

    private boolean isSlave() {
        return MasterUtil.isSlave();
    }

    /**
     * 综合业务下单
     *
     * @param order 综合订单信息
     * @return Message
     */
    @ApiOperation(value = "综合业务下单", notes = "综合业务下单")
    @PostMapping(value = "/order/integratedEntrust")
    public Message<IntegratedEntrust> sendIntegratedEntrust(@RequestBody @Validated IntegratedOrderPlaceBean order) {
        if (isSlave()) {
            return MasterUtil.postForObject(order);
        }

        /*ResultBean checkResult = commonCheckKit.check(order);
        if (ErrorCode.isFail(checkResult.getErrorCode())) {
            return new Message<>(checkResult);
        }*/

        Instruction instruction = instructionService.findById(order.getInstructionId());

        IntegratedEntrust integratedEntrust = new IntegratedEntrust();
        BeanUtils.copyProperties(order, integratedEntrust);
        if (DictCodeListExt3.投管业务类型_解除质押.getValue().equals(instruction.getBusiTypeTg()) && order.getEntrustVolume() == 0) {
            integratedEntrust.setEntrustVolume(instruction.getEntrustNum());
        }

        String entrustBs = EntrustPropUtils.getEntrustBs(instruction);
        logger.info("综合业务下单：买卖方向（entrustBs）转换：{} ==> {}", integratedEntrust.getEntrustBs(), entrustBs);
        integratedEntrust.setEntrustBs(entrustBs);

        String entrustProp = EntrustPropUtils.getEntrustProp(instruction, integratedEntrust.getEntrustProp());
        logger.info("综合业务下单：委托属性（entrustProp）转换：{} ==> {}", integratedEntrust.getEntrustProp(), entrustProp);
        integratedEntrust.setEntrustProp(entrustProp);

        return new Message<>(orderManagement.sendIntegratedEntrust(integratedEntrust.getUserId(), integratedEntrust));
    }

    /**
     * 委托撤单
     *
     * @param userId  用户Id
     * @param orderId 订单Id
     * @return Message
     */
    @ApiOperation(value = "委托撤单", notes = "委托撤单")
    @PostMapping(value = "/order/cancel")
    public Message<Boolean> cancelOrder(@RequestParam String userId, @RequestParam String orderId) {
        if (isSlave()) {
            return MasterUtil.postForObject(null);
        }
        return new Message<Boolean>(orderManagement.cancelOrder(userId, orderId));
    }

    @ApiOperation(value = "普通委托复核", notes = "普通委托复核", hidden = true)
    @PostMapping(value = "/order/review")
    public Message<ReviewDto> sendOrderWithReview(@RequestParam String userId, @RequestBody ReviewDto dto) {
        if (isSlave()) {
            return MasterUtil.postForObject(dto);
        }
        return new Message<>(orderManagement.sendOrderWithReview(userId, dto));
    }

    @ApiOperation(value = "综合业务委托复核", notes = "综合业务委托复核", hidden = true)
    @PostMapping(value = "/order/integratedEntrust/review")
    public Message<TrdExchangeDto> sendOrderWithReview(@RequestParam String userId, @RequestBody TrdExchangeDto dto) {
        if (isSlave()) {
            return MasterUtil.postForObject(dto);
        }
        return new Message<>(orderManagement.sendIntegratedEntrustWithReview(userId, dto));
    }

    /**
     * 算法单创建
     *
     * @param userId    用户id
     * @param algorithm 算法母单信息
     * @return ResultBean
     */
    @ApiOperation(value = "算法单创建", notes = "算法单创建", hidden = true)
    @PostMapping(value = "/order/algorithm")
    public Message<Algorithm> createAlgorithm(@RequestParam String userId, @RequestBody AlgorithmPlaceBean algorithm) {
        if (isSlave()) {
            return MasterUtil.postForObject(algorithm);
        }
        return new Message<>(orderManagement.createAlgorithm(userId, algorithm));
    }

    /**
     * 算法单取消
     * 取消之后停止算法执行，未完成的委托需要单独撤单
     *
     * @param algorithmId 算法母单id
     * @return ResultBean
     */
    @ApiOperation(value = "算法单取消", notes = "算法单取消", hidden = true)
    @PostMapping(value = "/order/algorithm/cancel")
    public Message<Algorithm> cancelAlgorithm(@RequestParam String algorithmId) {
        if (isSlave()) {
            return MasterUtil.postForObject(null);
        }
        return new Message<>(orderManagement.cancelAlgorithm(algorithmId));
    }

//    @RequestMapping(value = "/order/cancel/revoke", method = RequestMethod.POST)
//    public ResultBean cancelOrderWithRevoke( String userId,
//                                             @RequestBody RevokeDto dto) {
//        return orderManagement.cancelOrderWithRevoke(userId, dto);
//    }

}
