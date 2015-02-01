package com.gibhub.calcacuervo.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.KieSessionOption;
import org.kie.internal.io.ResourceFactory;

public class TestRuleUtils {

	public class KieSessionBuilder {
		private List<String> resources;
		private List<KieBaseOption> kbopts = new ArrayList<KieBaseOption>();
		private List<KieSessionOption> ksopts = new ArrayList<KieSessionOption>();

		public KieSessionBuilder withResources(String... resources) {
			this.resources = Arrays.asList(resources);
			return this;
		}

		public KieSessionBuilder withKieBaseOption(KieBaseOption o) {
			kbopts.add(o);
			return this;
		}

		public KieSessionBuilder withKieSessionOption(KieSessionOption o) {
			ksopts.add(o);
			return this;
		}

		public KieSession build() {
			return TestRuleUtils.createKieSession(this.resources, kbopts,
					ksopts);
		}
	}

	private static KieSession createKieSession(List<String> resources,
			List<KieBaseOption> options, List<KieSessionOption> ksessionOptions) {
		KieServices ks = KieServices.Factory.get();
		KieFileSystem kfs = ks.newKieFileSystem();
		for (String path : resources) {
			kfs.write(ResourceFactory.newClassPathResource(path));
		}
		KieBuilder kbuilder = ks.newKieBuilder(kfs);
		kbuilder.buildAll();
		if (kbuilder.getResults().hasMessages(Level.ERROR)) {
			for (Message msg : kbuilder.getResults().getMessages()) {
				System.out.print(msg);
			}
			throw new RuntimeException(
					"There where some errors compiling kbase.");
		}
		ReleaseId rel = kbuilder.getKieModule().getReleaseId();
		KieContainer kcontainer = ks.newKieContainer(rel);
		KieBaseConfiguration kbc = ks.newKieBaseConfiguration();
		for (KieBaseOption kieBaseOption : options) {
			kbc.setOption(kieBaseOption);
		}
		KieBase kbase = kcontainer.newKieBase(kbc);
		KieSessionConfiguration ksc = ks.newKieSessionConfiguration();
		for (KieSessionOption opt : ksessionOptions) {
			ksc.setOption(opt);
		}
		return kbase.newKieSession(ksc, null);
	}

}
