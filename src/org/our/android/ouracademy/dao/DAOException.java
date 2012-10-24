package org.our.android.ouracademy.dao;

public class DAOException extends Exception {
	private String message;
	public DAOException() {
		super();
	}

	public DAOException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
