package com.gibhub.calcacuervo.timer;

public class TimerSettings {

	private String delay;
	
	private long period;

	public TimerSettings(String delay, long period) {
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
