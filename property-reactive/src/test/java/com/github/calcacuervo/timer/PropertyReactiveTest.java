package com.github.calcacuervo.timer;

import junit.framework.Assert;

import org.junit.Test;
import org.kie.api.runtime.KieSession;

import com.github.calcacuervo.common.TestResources;
import com.github.calcacuervo.common.TestRuleUtils;
import com.github.calcacuervo.models.Customer;
import com.github.calcacuervo.models.SubscriptionType;

/**
 * This test aims to show the usage of the property reactive functionality in
 * drools. The rule com/github/calcacuervo/reactive/subscriptions.drl has rules
 * which will listen only for changes in specific properties of the Customer
 * fact.
 * 
 * @author calcacuervo
 *
 */
public class PropertyReactiveTest {

	@Test
	public void customerWith4YearsAge_fireRules_shouldSetGoldSubscription() {
		// first, we need to create the Kie Session.
		KieSession ksession = new TestRuleUtils().new KieSessionBuilder()
				.withResources(TestResources.SUBSCRIPTIONS.getResource())
				.build();

		// we will insert a customer which has age = 4
		Customer customer = new Customer("Demian");
		customer.setAgeAsCustomer(4);
		ksession.insert(customer);
		ksession.fireAllRules();

		// the customer must be gold. If it is not property reactive, it will
		// start looping.
		Assert.assertEquals(SubscriptionType.GOLD,
				customer.getSubscriptionType());

		// dispose the ksession.
		ksession.dispose();
	}

	@Test
	public void customerWith10YearsAge_fireRules_shouldSetGoldSubscription() {
		// first, we need to create the Kie Session.
		KieSession ksession = new TestRuleUtils().new KieSessionBuilder()
				.withResources(TestResources.SUBSCRIPTIONS.getResource())
				.build();

		// we will insert a customer which has age = 10
		Customer customer = new Customer("Demian");
		customer.setAgeAsCustomer(10);
		ksession.insert(customer);
		ksession.fireAllRules();

		// the customer must be platinium. If it is not property reactive, it
		// will start looping.
		Assert.assertEquals(SubscriptionType.PLATINUM,
				customer.getSubscriptionType());

		// dispose the ksession.
		ksession.dispose();
	}

}
