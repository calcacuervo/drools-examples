package com.gibhub.calcacuervo.flights;

public class Flight {

	private String from;
	private String to;
	
	public Flight(String from,String to) {
		this.from=from;
		this.to=to;
	}
	
	public String getFrom() {
		return from;
	}
	
	public String getTo() {
		return to;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	
	public void setTo(String to) {
		this.to = to;
	}
	
	@Override
	public String toString() {		
		return "Flight founded from " + getFrom() + " to " + getTo();
	}
}
