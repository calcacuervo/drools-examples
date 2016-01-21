package com.github.calcacuervo.common;

public enum TestResources {
	SUBSCRIPTIONS("com/github/calcacuervo/reactive/subscriptions.drl");

	private String resource;

	private TestResources(String resource) {
		this.resource = resource;
	}

	public String getResource() {
		return resource;
	}
}
