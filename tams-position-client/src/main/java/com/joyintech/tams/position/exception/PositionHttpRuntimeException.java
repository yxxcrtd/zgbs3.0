package com.joyintech.tams.position.exception;

public class PositionHttpRuntimeException extends RuntimeException {


	private int exceptionType = 0;

	public int getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(int exceptionType) {
		this.exceptionType = exceptionType;
	}

	public PositionHttpRuntimeException() {
	}

	public PositionHttpRuntimeException(String arg0) {
		super(arg0);
	}

	public PositionHttpRuntimeException(Throwable arg0) {
		super(arg0);
	}

	public PositionHttpRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
