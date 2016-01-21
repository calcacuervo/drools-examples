package com.github.calcacuervo.models;

import org.apache.commons.lang3.Validate;

/**
 * Represents a customer.
 * @author calcacuervo
 *
 */
public class Customer {

	private String customerName;
	
	/**
	 * Indicates whether the customer has made the payment for his subscription.
	 */
	private boolean suscriptionPaymentDone;

	/**
	 * Creates a new customer.
	 * @param customerName It cannot be null.
	 */
	public Customer(String customerName) {
		Validate.notEmpty(customerName);
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
