package com.joyintech.tams.exchange.common.exception;


import com.joyintech.tams.business.common.pojo.Message;

/**
 * 场内交易头寸异常
 *
 * @author swwan
 * @date 2022/5/6 - 16:23
 */
@SuppressWarnings("rawtypes")
public class PositionExchangeException extends RuntimeException {

    private static final long serialVersionUID = -7792778645021847199L;
    private Message exceptionMessage;

    private String message;

    public PositionExchangeException() {
    }

    public PositionExchangeException(Message exceptionMessage, String message) {
        this.exceptionMessage = exceptionMessage;
        this.message = message;
    }

    public Message getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(Message exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static PositionExchangeException create(Message exceptionMessage) {
        return new PositionExchangeException(exceptionMessage, exceptionMessage.getMessage());
    }
}