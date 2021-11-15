/*
 * © Copyright [2021] Micro Focus or one of its affiliates.
 * Licensed under Apache License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.microfocus.bdd.ut;

import com.microfocus.bdd.*;
import com.microfocus.bdd.api.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class TestCucumberJsHandler {
    private OctaneFeature octaneFeature;

    @Before
    public void setup() {
        octaneFeature = TestUtil.parseFeatureFile("src/test/resources/features/robustgherkin.feature");
    }

    @Test
    public void testFailedScenario() {
        CucumberJsHandler handler = new CucumberJsHandler();
        Element element = TestUtil.getXmlElement("src/test/resources/cucumber-js/cucumber-junit-test-report.xml", "testsuite", 2);
        handler.setElement(element);
        Assert.assertEquals("handler can get back feature's partial path",
                "features/robustgherkin.feature",
                handler.getFeatureFile().get());
        Assert.assertEquals("handler can get back scenario name",
                "Some another scenario 2", handler.getScenarioName(octaneFeature));
        Assert.assertEquals("handler can get back file path",
                "features/robustgherkin.feature",
                handler.getFeatureFile().get());
        String errorMessage = element.getChildren().get(10).getChildren().get(0).getText();
        Assert.assertTrue("handler can get correct error message for failed step", errorMessage.contains("Expected values to be strictly equal"));
    }

    @Test
    public void testPassedScenario(){
        CucumberJsHandler handler = new CucumberJsHandler();
        Element element = TestUtil.getXmlElement("src/test/resources/cucumber-js/all-passed-junit-test-report.xml", "testsuite", 1);
        handler.setElement(element);
        Assert.assertEquals("handler can get back feature's partial path",
                "features/robustgherkin.feature",
                handler.getFeatureFile().get());
        Assert.assertEquals("handler can get back scenario name",
                "Some determinable business situation", handler.getScenarioName(octaneFeature));
        List<Element> childrenElement = element.getChildren().stream().filter(child -> child.getName().equals("testcase")).collect(Collectors.toList());
        childrenElement.forEach(child -> {
            Assert.assertTrue("Each step should not have children",child.getChildren().isEmpty());
        });

    }

    @Test
    public void testOutlineScenario() {
        CucumberJsHandler handler = new CucumberJsHandler();
        Element element = TestUtil.getXmlElement("src/test/resources/cucumber-js/cucumber-junit-test-report.xml", "testsuite", 3);
        handler.setElement(element);

        Assert.assertEquals("handler can get back feature's partial path",
                "features/robustgherkin.feature",
                handler.getFeatureFile().get());
        String scenarioName = handler.getScenarioName(octaneFeature);
        Assert.assertEquals("handler can get back scenario name",
                "feeding a cow <name> yum yum yum", scenarioName);
    }

    @Test
    public void testTwoOutlineScenarioWithSameName() {
        Element element = TestUtil.getXmlElement(
                "src/test/resources/cucumber-js/cucumber-junit-test-with-examples-has-same-name.xml",
                "testsuite",
                4);

        CucumberJsHandler handler = new CucumberJsHandler();
        handler.setElement(element);
        Assert.assertEquals("handler can get back feature's partial path",
                "features/robustgherkin03.feature",
                handler.getFeatureFile().get());
        String scenarioName = handler.getScenarioName(
                TestUtil.parseFeatureFile("src/test/resources/features/robustgherkin03.feature"));
        Assert.assertEquals("handler can get back scenario name",
                "feeding a cow <name> yum yum yum", scenarioName);
    }
}
