package com.itu4061.exception ;

public class InnerRequestException extends FrameworkException4061{

	public InnerRequestException() {
		super();
	}

	public InnerRequestException(String message) {
		super(message);
	}

	public InnerRequestException(String message, Throwable cause) {
		super(message, cause);
	}

}