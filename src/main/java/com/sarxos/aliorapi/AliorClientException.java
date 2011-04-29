package com.sarxos.aliorapi;

public class AliorClientException extends Exception {

	private static final long serialVersionUID = 1L;

	public AliorClientException() {
		super();
	}

	public AliorClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public AliorClientException(String message) {
		super(message);
	}

	public AliorClientException(Throwable cause) {
		super(cause);
	}
}
