package com.gibhub.calcacuervo.common;

public enum TestResources {
	REMINDER_WITH_INTERVAL("com/github/calcacuervo/timer/reminder_with_interval.drl"),
	REMINDER_WITH_EXPRESSION("com/github/calcacuervo/timer/reminder_with_expression.drl"),
	REMINDER_WITH_CALENDAR("com/github/calcacuervo/timer/reminder_with_calendar.drl"),
	REMINDER_WITH_CALENDAR_AND_TIMER("com/github/calcacuervo/timer/reminder_with_calendar_and_timer.drl"),
	REMINDER_WITH_CRON("com/github/calcacuervo/timer/reminder_with_cron.drl");

	private String resource;

	private TestResources(String resource) {
		this.resource = resource;
	}

	public String getResource() {
		return resource;
	}
}
