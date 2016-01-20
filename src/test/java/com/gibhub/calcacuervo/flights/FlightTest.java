package com.gibhub.calcacuervo.flights;


import junit.framework.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import com.gibhub.calcacuervo.common.TestResources;
import com.gibhub.calcacuervo.common.TestRuleUtils;

public class FlightTest {

	@Test
	public void test_send_reminder_interval() {
		// first, we need to create the Kie Session.
		KieSession ksession = new TestRuleUtils().new KieSessionBuilder()
				.withResources(TestResources.FLIGHT_SIMPLE_QUERY.getResource()).build();

		// now, create a flight to Argentina
		Flight flight = new Flight("Madrid","Argentina");
		FactHandle customerFactHandle = ksession.insert(flight);
		Flight flight2 = new Flight("Londres","Argentina");
		customerFactHandle = ksession.insert(flight2);
		Flight flight3 = new Flight("Londres","Madrid");
		customerFactHandle = ksession.insert(flight3);
		ksession.fireAllRules();
		Assert.assertEquals(0, getFlights(ksession));

			}

	
	private int getFlights(KieSession ksession) {
		QueryResults results = ksession.getQueryResults("Fights To Argentina");
		return results.size();
	}
}
