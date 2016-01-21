package com.github.calcacuervo.models;

import org.apache.commons.lang3.Validate;

/**
 * This is a reminder to be sent to customers.
 * @author calcacuervo
 *
 */
public class Reminder {

	private String message;

	/**
	 * Creates a new reminder.
	 * @param message The message to send. It cannot be null.
	 */
	public Reminder(String message) {
		Validate.notEmpty(message);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
