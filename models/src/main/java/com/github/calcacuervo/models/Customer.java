package com.github.calcacuervo.models;

import org.apache.commons.lang3.Validate;
import org.kie.api.definition.type.PropertyReactive;

/**
 * Represents a customer.
 * @author calcacuervo
 *
 */
@PropertyReactive
public class Customer {

	private String customerName;
	
	private int ageAsCustomer;
	
	private SubscriptionType subscriptionType;
	
	
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
		this.ageAsCustomer = 0;
		this.subscriptionType = SubscriptionType.BASIC;
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
	
	public int getAgeAsCustomer() {
		return ageAsCustomer;
	}
	
	public void setAgeAsCustomer(int ageAsCustomer) {
		this.ageAsCustomer = ageAsCustomer;
	}
	
	public SubscriptionType getSubscriptionType() {
		return subscriptionType;
	}
	
	public void setSubscriptionType(SubscriptionType subscriptionType) {
		this.subscriptionType = subscriptionType;
	}
}
