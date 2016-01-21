package com.github.calcacuervo.models;

import org.apache.commons.lang3.Validate;

/**
 * Settings to define a timer.
 * @author calcacuervo
 *
 */
public class TimerSettings {

	private String delay;
	
	private long period;

	public TimerSettings(String delay, long period) {
		Validate.notEmpty(delay);
		this.delay = delay;
		this.period = period;
	}
	
	public String getDelay() {
		return delay;
	}
	
	public long getPeriod() {
		return period;
	}
}
