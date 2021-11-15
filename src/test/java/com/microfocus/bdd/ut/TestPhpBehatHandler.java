/*
 * © Copyright [2021] Micro Focus or one of its affiliates.
 * Licensed under Apache License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.microfocus.bdd.ut;

import com.microfocus.bdd.api.*;
import com.microfocus.bdd.PhpBehatHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestPhpBehatHandler {

    private OctaneFeature octaneFeature;

    @Before
    public void setup() {
        octaneFeature = TestUtil.parseFeatureFile("src/test/resources/features/StandardRobustGherkin.feature");
    }

    @Test
    public void testFailedScenario() {
        PhpBehatHandler handler = new PhpBehatHandler();
        Element element = TestUtil.getXmlElement("src/test/resources/php-behat/fail-assert-1.xml", "testcase", 1);
        handler.setElement(element);
        Assert.assertEquals("testcase status should be failed", "failed", element.getAttribute("status"));
        Assert.assertTrue("Failed step contains correct error message",
                element.getChild("failure").get().getAttribute("message").contains("This step failed so we skipped it"));
        Assert.assertEquals("handler can get back feature name",
                "Some terse yet descriptive text of what is desired like bla bla", handler.getFeatureName().get());
        Assert.assertEquals("handler can get back scenario name",
                "Some determinable business situation", handler.getScenarioName(octaneFeature));
    }

    @Test
    public void testPassedScenario() {
        PhpBehatHandler handler = new PhpBehatHandler();
        Element element = TestUtil.getXmlElement("src/test/resources/php-behat/all-pass-1.xml", "testcase", 1);
        handler.setElement(element);
        Assert.assertEquals("testcase status should be passed", "passed", element.getAttribute("status"));
        Assert.assertEquals("handler can get back feature name",
                "Some terse yet descriptive text of what is desired like bla bla", handler.getFeatureName().get());
        Assert.assertEquals("handler can get back scenario name",
                "Some determinable business situation", handler.getScenarioName(octaneFeature));
    }

    @Test
    public void testScenarioOutline() {
        PhpBehatHandler handler = new PhpBehatHandler();
        Element element = TestUtil.getXmlElement("src/test/resources/php-behat/all-pass-1.xml", "testcase", 3);
        handler.setElement(element);
        Assert.assertEquals("handler can get back feature name",
                "Some terse yet descriptive text of what is desired like bla bla", handler.getFeatureName().get());
        Assert.assertEquals("handler can get back scenario name",
                "feeding a cow <name> yum yum yum", handler.getScenarioName(octaneFeature));
    }
}
