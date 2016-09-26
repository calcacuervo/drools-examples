package com.github.calcacuervo;

import java.util.UUID;

import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.core.io.impl.ClassPathResource;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;

/**
 * Tests using KieHelper with concurrent access. This uses a custom kie helper
 * implementation which allows to pass different release ids.
 * 
 * @author calcacuervo
 *
 */
public class TestWithCustomKieHelper {

	/**
	 * In this test, one thread fails!
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void twoSessions() throws InterruptedException {
		TestRunner tr1 = new TestRunner("1", "one", "one.drl");
		TestRunner tr2 = new TestRunner("2", "two", "two.drl");

		Thread t1 = new Thread(tr1);
		Thread t2 = new Thread(tr2);
		t1.start();
		t2.start();
		while (!tr1.finished) {
			Thread.sleep(100);
		}
		Assert.assertEquals("", tr1.getError());
		while (!tr2.finished) {
			Thread.sleep(100);
		}
		Assert.assertEquals("", tr2.getError());
	}

	/**
	 * Session One separately works fine.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void sessionOne() throws InterruptedException {
		TestRunner tr = new TestRunner("1", "one", "one.drl");
		Thread t1 = new Thread(tr);
		t1.start();
		while (!tr.finished) {
			Thread.sleep(100);
		}
		Assert.assertEquals("", tr.getError());
	}

	/**
	 * Session One separately works fine.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void sessionTwo() throws InterruptedException {
		TestRunner tr = new TestRunner("2", "two", "two.drl");
		Thread t1 = new Thread(tr);
		t1.start();
		while (!tr.finished) {
			Thread.sleep(100);
		}
		Assert.assertEquals("", tr.getError());
	}

	public static class TestRunner implements Runnable {

		private String val;
		private String drl;
		private String expectedVal;
		private boolean finished;
		private String error = "";

		public TestRunner(String val, String expected, String drl) {
			this.val = val;
			this.expectedVal = expected;
			this.drl = drl;
			this.finished = false;
		}

		public boolean isFinished() {
			return finished;
		}

		public String getError() {
			return error;
		}

		@Override
		public void run() {
			KieHelper helper = new KieHelper();
			ClassPathResource r = new ClassPathResource(drl);
			this.getClass().getClassLoader();
			helper.addResource(r, ResourceType.DRL);
			KieSession session = helper.build(
					new ReleaseIdImpl("com.test:"
							+ val + ":1.0"))
					.newKieSession();
			Holder h = new Holder();
			h.origVal = val;
			session.insert(h);
			session.fireAllRules();
			finished = true;
			error = expectedVal.equals(h.sessionVal) ? ""
					: "Expected and session values are different. Expected: "
							+ this.expectedVal + " Got: " + h.sessionVal;
		}
	}

}
