package com.joyintech.tams.exchange.common;

import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.domain.Message;

import java.util.Objects;

public class BusinessResultException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private ErrorCode errorCode;

    private String message;

    private Message<Object> resultMessage;

    public BusinessResultException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public BusinessResultException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public BusinessResultException(Message<Object> resultMessage) {
        this.resultMessage = resultMessage;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public Message<Object> getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(Message<Object> resultMessage) {
        this.resultMessage = resultMessage;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static BusinessResultException create(ErrorCode errorCode) {
        return create(errorCode, null);
    }

    public static BusinessResultException create(ErrorCode errorCode, String message) {
        return new BusinessResultException(errorCode, message);
    }

    public Message<Object> toMessage() {
        if (Objects.nonNull(resultMessage)) {
            return resultMessage;
        }

        return new Message<Object>(errorCode.value() + "", errorCode.getDescription());
    }
}
