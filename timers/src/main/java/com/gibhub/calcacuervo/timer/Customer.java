package com.gibhub.calcacuervo.timer;

public class Customer {

	private String customerName;
	
	private boolean suscriptionPaymentDone;

	public Customer(String customerName) {
		this.customerName = customerName;
		suscriptionPaymentDone  = true;
	}
	
	public void setSuscriptionPaymentDone(boolean suscriptionPaymentDone) {
		this.suscriptionPaymentDone = suscriptionPaymentDone;
	}
	
	public String getCustomerName() {
		return customerName;
	}
	
	public boolean isSuscriptionPaymentDone() {
		return suscriptionPaymentDone;
	}
}
