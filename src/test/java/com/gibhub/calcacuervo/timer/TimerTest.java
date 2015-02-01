package com.gibhub.calcacuervo.timer;

import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.drools.core.time.SessionPseudoClock;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;

import com.gibhub.calcacuervo.common.TestResources;
import com.gibhub.calcacuervo.common.TestRuleUtils;

public class TimerTest {

	@Test
	public void test_send_reminder_interval() {
		// first, we need to create the Kie Session.
		KieSession ksession = new TestRuleUtils().new KieSessionBuilder()
				.withResources(TestResources.REMINDER_WITH_INTERVAL.getResource())
				.withKieSessionOption(ClockTypeOption.get("pseudo")).build();

		// now, create a customer who did the payment of the subscription.
		Customer customer = new Customer("Demian");
		FactHandle customerFactHandle = ksession.insert(customer);
		ksession.fireAllRules();
		Assert.assertEquals(0, getAmountOfReminders(ksession));

		// until now, no reminder has been sent.
		// but now, we set the customer as not have done the latest payment.
		customer.setSuscriptionPaymentDone(false);

		// we have to update the ksession.
		ksession.update(customerFactHandle, customer);
		ksession.fireAllRules();
		// now, we may have sent 1 reminder.
		Assert.assertEquals(1, getAmountOfReminders(ksession));

		// 15 minutes passes, a new reminder should have been sent.
		SessionPseudoClock clock = ksession.getSessionClock();
		clock.advanceTime(15, TimeUnit.MINUTES);
		ksession.fireAllRules();
		// now, we may have sent 2 reminders.
		Assert.assertEquals(2, getAmountOfReminders(ksession));

		// now, the user have done the payment.
		customer.setSuscriptionPaymentDone(true);
		ksession.update(customerFactHandle, customer);

		// so, after 15 mins
		clock.advanceTime(15, TimeUnit.MINUTES);
		ksession.fireAllRules();

		// no new reminders have been sent.
		// now, we may have sent 2 reminders.
		Assert.assertEquals(2, getAmountOfReminders(ksession));
	}

	@Test
	public void test_send_reminder_cron() {
		// first, we need to create the Kie Session.
		KieSession ksession = new TestRuleUtils().new KieSessionBuilder()
				.withResources(TestResources.REMINDER_WITH_CRON.getResource())
				.withKieSessionOption(ClockTypeOption.get("pseudo")).build();

		// now, create a customer who did the payment of the subscription.
		Customer customer = new Customer("Demian");
		FactHandle customerFactHandle = ksession.insert(customer);
		ksession.fireAllRules();
		Assert.assertEquals(0, getAmountOfReminders(ksession));

		// until now, no reminder has been sent.
		// but now, we set the customer as not have done the latest payment.
		customer.setSuscriptionPaymentDone(false);

		// we have to update the ksession.
		ksession.update(customerFactHandle, customer);
		ksession.fireAllRules();

		// now, we may have sent no reminder, as we do at the begining of the
		// day.
		Assert.assertEquals(0, getAmountOfReminders(ksession));

		// 1 day passes, a reminder should have been sent.
		SessionPseudoClock clock = ksession.getSessionClock();
		clock.advanceTime(1, TimeUnit.DAYS);
		ksession.fireAllRules();
		// now, we may have sent 1 reminder.
		Assert.assertEquals(1, getAmountOfReminders(ksession));

		// another day passes, a reminder should have been sent.
		clock.advanceTime(1, TimeUnit.DAYS);
		ksession.fireAllRules();
		// now, we may have sent 2 reminders.
		Assert.assertEquals(2, getAmountOfReminders(ksession));

		// now, the user have done the payment.
		customer.setSuscriptionPaymentDone(true);
		ksession.update(customerFactHandle, customer);

		// so, after another day
		clock.advanceTime(1, TimeUnit.DAYS);
		ksession.fireAllRules();

		// no new reminders have been sent.
		// now, we may have sent 2 reminders.
		Assert.assertEquals(2, getAmountOfReminders(ksession));
	}
	
	@Test
	public void test_send_reminder_expression() {
		// first, we need to create the Kie Session.
		KieSession ksession = new TestRuleUtils().new KieSessionBuilder()
				.withResources(TestResources.REMINDER_WITH_EXPRESSION.getResource())
				.withKieSessionOption(ClockTypeOption.get("pseudo")).build();
	
		ksession.insert(new TimerSettings("30s", 60000));
		
		// now, create a customer who did the payment of the subscription.
		Customer customer = new Customer("Demian");
		FactHandle customerFactHandle = ksession.insert(customer);
		ksession.fireAllRules();
		Assert.assertEquals(0, getAmountOfReminders(ksession));

		// until now, no reminder has been sent.
		// but now, we set the customer as not have done the latest payment.
		customer.setSuscriptionPaymentDone(false);

		// we have to update the ksession.
		ksession.update(customerFactHandle, customer);
		ksession.fireAllRules();
		// now, we may have sent 0 reminder.
		Assert.assertEquals(0, getAmountOfReminders(ksession));

		// 31 second passes, a new reminder should have been sent.
		SessionPseudoClock clock = ksession.getSessionClock();
		clock.advanceTime(31, TimeUnit.SECONDS);
		ksession.fireAllRules();
		// now, we may have sent 1 reminders.
		Assert.assertEquals(1, getAmountOfReminders(ksession));

		clock.advanceTime(61, TimeUnit.SECONDS);
		ksession.fireAllRules();
		// now, we may have sent 2 reminders.
		Assert.assertEquals(2, getAmountOfReminders(ksession));

		// now, the user have done the payment.
		customer.setSuscriptionPaymentDone(true);
		ksession.update(customerFactHandle, customer);

		// so, after 15 mins
		clock.advanceTime(15, TimeUnit.MINUTES);
		ksession.fireAllRules();

		// no new reminders have been sent.
		// now, we may have sent 2 reminders.
		Assert.assertEquals(2, getAmountOfReminders(ksession));
	}
	

	private int getAmountOfReminders(KieSession ksession) {
		QueryResults results = ksession.getQueryResults("get reminders");
		return results.size();
	}
}
