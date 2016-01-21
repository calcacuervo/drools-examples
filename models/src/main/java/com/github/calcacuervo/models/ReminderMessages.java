package com.github.calcacuervo.models;

/**
 * Different messages that can be sent in reminders.
 * @author calcacuervo
 *
 */
public enum ReminderMessages {
	PAYMENT_NOT_DONE("Payment has not been done");

	private String message;

	private ReminderMessages(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
