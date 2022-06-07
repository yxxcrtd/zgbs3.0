package com.joyintech.tams.exchange.service.trade.processor.check;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.validation.BeanValidationResult;
import com.joyintech.exchange.service.quote.service.QuoteService;
import com.joyintech.tams.exchange.common.BusinessResultException;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.datacommon.db.service.InstructionService;
import com.joyintech.tams.exchange.datacommon.db.service.InstrumentService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author swwan
 * @date 2022/4/13 - 16:03
 */
public abstract class BaseCheckKit {

    protected BeanValidationResult checkResult;
    private InstructionService instructionService;
    private InstrumentService instrumentService;
    private QuoteService quoteService;

    @Autowired
    public void setInstructionService(InstructionService instructionService) {
        this.instructionService = instructionService;
    }

    @Autowired
    public void setInstrumentService(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }

    @Autowired
    public void setQuoteService(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    protected BeanValidationResult getCheckResult() {
        return checkResult;
    }

    public ResultBean check(BaseOrderRequest order) {
        checkResult  = new BeanValidationResult(true);
        Instruction instruction = getInstruction(order);
        Instrument instrument = getInstrument(instruction);
        TickBean tickBean = getTickBean(instruction);
        return check(order, instruction, instrument, tickBean);
    }

    public abstract ResultBean check(BaseOrderRequest order, Instruction instruction, Instrument instrument, TickBean tickBean);

    protected Instruction getInstruction(BaseOrderRequest order) {
        Instruction instruction = instructionService.findById(order.getInstructionId());
        if (ObjectUtil.isNull(instruction)) {
            throw new BusinessResultException(ErrorCode.INSTRUCTIONS_NOT_EXIST);
        }
        return instruction;
    }

    protected Instrument getInstrument(Instruction instruction) {
        Instrument instrument = instrumentService.findById(instruction.getFinprodMarketId());
        if (ObjectUtil.isNull(instrument)) {
            throw new BusinessResultException(ErrorCode.DERIVATIVE_INSTRUMENT_ERROR);
        }
        return instrument;
    }

    protected TickBean getTickBean(Instruction instruction) {
        ResultBean tick = quoteService.getTick(instruction.getFinprodMarketId());
        if (ErrorCode.isFail(tick.getErrorCode())) {
            throw new BusinessResultException(ErrorCode.QUOTE_NOT_EXIT);
        }
        return (TickBean) tick.getData();
    }

    protected void addCheckResult(String propertyName, Object value, String message) {
        BeanValidationResult.ErrorMessage errorMessage = new BeanValidationResult.ErrorMessage();
        errorMessage.setPropertyName(propertyName);
        errorMessage.setValue(value);
        errorMessage.setMessage(message);

        checkResult.setSuccess(false);
        checkResult.addErrorMessage(errorMessage);
    }
}
