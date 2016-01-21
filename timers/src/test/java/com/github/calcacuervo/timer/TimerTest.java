package com.github.calcacuervo.timer;

import java.text.ParseException;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.drools.core.time.SessionPseudoClock;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.Calendar;

import com.github.calcacuervo.common.TestResources;
import com.github.calcacuervo.common.TestRuleUtils;
import com.github.calcacuervo.models.Customer;
import com.github.calcacuervo.models.TimerSettings;

public class TimerTest {

	@Test
	public void testSendReminderWithIntervalTimer_shouldSendReminder() {
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

		// dispose the ksession.
		ksession.dispose();
	}

	@Test
	public void testSendReminderWithCronTimer_shouldSendReminder() {
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
	public void testSendReminderWithQuartzCalendar_shouldSendReminder() throws ParseException {
		// first, we need to create the Kie Session.
		KieSession ksession = new TestRuleUtils().new KieSessionBuilder()
				.withResources(TestResources.REMINDER_WITH_CALENDAR.getResource())
				.withKieSessionOption(ClockTypeOption.get("pseudo")).build();
		TimeZone tzone = TimeZone.getTimeZone("GMT");
		TimeZone.setDefault(tzone);
		final org.quartz.impl.calendar.DailyCalendar businessHours = new org.quartz.impl.calendar.DailyCalendar(8, 0,
				0, 0, 16, 0, 0, 0);
		businessHours.setInvertTimeRange(true);
		Calendar adapted = new Calendar() {

			public boolean isTimeIncluded(long timestamp) {
				return businessHours.isTimeIncluded(timestamp);
			}
		};
		ksession.getCalendars().set("cron", adapted);
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

		// 9 hours passes, a reminder be sent, as we are in GMT 9AM (pseudo
		// clock starts at time 0!)
		SessionPseudoClock clock = ksession.getSessionClock();
		clock.advanceTime(9, TimeUnit.HOURS);
		ksession.update(customerFactHandle, customer);
		ksession.fireAllRules();
		// now, we may have sent 1 reminder.
		Assert.assertEquals(1, getAmountOfReminders(ksession));

		// another day passes, a reminder should have been sent.
		clock.advanceTime(1, TimeUnit.DAYS);
		ksession.update(customerFactHandle, customer);
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
	public void testSendReminderWithQuartzCalendarAndTimer_shouldSendReminder() throws ParseException {
		// first, we need to create the Kie Session.
		KieSession ksession = new TestRuleUtils().new KieSessionBuilder()
				.withResources(TestResources.REMINDER_WITH_CALENDAR_AND_TIMER.getResource())
				.withKieSessionOption(ClockTypeOption.get("pseudo")).build();
		TimeZone tzone = TimeZone.getTimeZone("GMT");
		TimeZone.setDefault(tzone);
		final org.quartz.impl.calendar.DailyCalendar businessHours = new org.quartz.impl.calendar.DailyCalendar(8, 0,
				0, 0, 16, 0, 0, 0);
		businessHours.setInvertTimeRange(true);
		Calendar adapted = new Calendar() {

			public boolean isTimeIncluded(long timestamp) {
				return businessHours.isTimeIncluded(timestamp);
			}
		};
		ksession.getCalendars().set("cron", adapted);
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

		// 9 hours passes, a reminder be sent, as we are in GMT 9AM (pseudo
		// clock starts at time 0!)
		SessionPseudoClock clock = ksession.getSessionClock();
		clock.advanceTime(9, TimeUnit.HOURS);
		ksession.update(customerFactHandle, customer);
		ksession.fireAllRules();
		// now, we may have sent 1 reminder.
		Assert.assertEquals(1, getAmountOfReminders(ksession));

		// now, it should be repeated in 15 minutes!
		clock.advanceTime(15, TimeUnit.MINUTES);
		ksession.update(customerFactHandle, customer);
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
	public void testSendReminderWithExpresionTimer_shouldSendReminder() {
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

		// after the first delay, advance 61 seconds..
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

	// Now, some examples with fire until halt!
	@Test
	public void testSendReminderWithCron_withFireUntilHalt_shouldSendReminder() {
		// first, we need to create the Kie Session.
		KieSession ksession = new TestRuleUtils().new KieSessionBuilder()
				.withResources(TestResources.REMINDER_WITH_CRON.getResource())
				.withKieSessionOption(ClockTypeOption.get("pseudo")).withFireUntilHalt(true).build();

		// now, create a customer who did the payment of the subscription.
		Customer customer = new Customer("Demian");
		FactHandle customerFactHandle = ksession.insert(customer);
		Assert.assertEquals(0, getAmountOfReminders(ksession));
		
		// until now, no reminder has been sent.
		// but now, we set the customer as not have done the latest payment.
		customer.setSuscriptionPaymentDone(false);

		// we have to update the ksession.
		ksession.update(customerFactHandle, customer);
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

		// dispose the session.
		ksession.dispose();
	}

	private int getAmountOfReminders(KieSession ksession) {
		QueryResults results = ksession.getQueryResults("get reminders");
		return results.size();
	}
}
