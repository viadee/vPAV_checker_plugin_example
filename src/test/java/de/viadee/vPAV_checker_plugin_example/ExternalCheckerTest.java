package de.viadee.vPAV_checker_plugin_example;

/**
 * BSD 3-Clause License
 *
 * Copyright © 2019, viadee Unternehmensberatung AG
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import de.viadee.bpm.vPAV.BpmnScanner;
import de.viadee.bpm.vPAV.RuntimeConfig;
import de.viadee.bpm.vPAV.config.model.ElementConvention;
import de.viadee.bpm.vPAV.config.model.Rule;
import de.viadee.bpm.vPAV.config.model.RuleSet;
import de.viadee.bpm.vPAV.config.model.Setting;
import de.viadee.bpm.vPAV.processing.checker.AbstractElementChecker;
import de.viadee.bpm.vPAV.processing.checker.CheckerFactory;
import de.viadee.bpm.vPAV.processing.code.flow.BpmnElement;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ExternalCheckerTest {

	private static final String BASE_PATH = "src/test/resources/";

	private static ClassLoader cl;

	private static Map<String, Map<String, Rule>> rules = new HashMap<>();

	private static RuleSet ruleSet;

	@BeforeClass
	public static void setup() throws MalformedURLException {
		final File file = new File(".");
		final String currentPath = file.toURI().toURL().toString();
		final URL classUrl = new URL(currentPath + "src/test/java");
		final URL[] classUrls = { classUrl };
		cl = new URLClassLoader(classUrls);
		RuntimeConfig.getInstance().setClassLoader(cl);
		RuntimeConfig.getInstance().setTest(true);
		RuntimeConfig.getInstance().getResource("en_US");
		createRule();
	}

	/**
	 * Case: Correct BoundaryErrorEvent with corresponding ErrorCodes
	 *
	 */
	@Test
	public void testExternalChecker() {

		final String PATH = BASE_PATH + "ExternalChecker.bpmn";

		CheckerFactory checkerFactory = new CheckerFactory();

		Collection[] cElChecker = checkerFactory.createCheckerInstances(ruleSet, null,
				new BpmnScanner(PATH), null, new ArrayList<>(), new ArrayList<>(), new HashMap<>());

		// parse bpmn model
		final Collection<CheckerIssue> issues = new ArrayList<>();

		// parse bpmn model
		final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

		final Collection<BaseElement> baseElements = modelInstance.getModelElementsByType(BaseElement.class);

		for (BaseElement baseElement : baseElements) {
			final BpmnElement element = new BpmnElement(PATH, baseElement, null, null);

			for (int i = 0; i < cElChecker.length; i++) {
				for (int j = 0; j < cElChecker[i].size(); j++) {
					issues.addAll(((AbstractElementChecker) ((ArrayList) cElChecker[i]).get(j)).check(element));
				}
			}
		}

		Assert.assertEquals("Incorrect model should generate an issue", 1, issues.size());
	}

	private static void createRule() {

		final Map<String, Setting> settings = new HashMap<>();
		final Setting setting = new Setting("external_Location", null, null, null, true,
				"de.viadee.vPAV_checker_plugin_example");

		settings.put("external_Location", setting);

		final Collection<ElementConvention> elementConventions = new ArrayList<>();

		final ElementConvention elementConvention = new ElementConvention("convention", null,
				"Starts with a capital letter followed by letters, hyphens or spaces.", "[A-ZÄÖÜ][a-zäöü\\-\\s]+");

		elementConventions.add(elementConvention);

		final Rule rule = new Rule("TaskNamingConventionCheckerExtern", true, null, settings, elementConventions, null);
		HashMap<String, Rule> ruleMap = new HashMap<>();
		ruleMap.put("TaskNamingConventionCheckerExtern", rule);

		rules.put("TaskNamingConventionCheckerExtern", ruleMap);

		ruleSet = new RuleSet(rules, new HashMap<>(), false);
	}

}
