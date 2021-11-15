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
import com.microfocus.bdd.gherkin.GherkinMultiLingualService;
import com.microfocus.bdd.util.GherkinDocumentUtil;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;

public class TestPythonRadishHandlerTest {

    @Test
    public void testPythonRadishHandlerWithAllPass() throws FileNotFoundException, XMLStreamException {
        PythonRadishHandler pythonRadishHandler = new PythonRadishHandler();
        Iterator<Element> it = new JunitReportReader(
                new FileInputStream("src/test/resources/python-radish/python-radish-all-pass-1.xml"),
                "testcase").iterator();
        FeatureFileMeta featureFileMeta = new FeatureFileMeta("src/test/resources/features/robustgherkin.feature",
                GherkinMultiLingualService.DEFAULT_LANGUAGE);
        OctaneFeature octaneFeature = GherkinDocumentUtil.generateSkeletonFeature(featureFileMeta);

        Element element = it.next();
        pythonRadishHandler.setElement(element);
        Assert.assertEquals("test case has correct feature name", "Some terse yet descriptive text of what is desired like bla bla", pythonRadishHandler.getFeatureName().get());
        Assert.assertEquals("test case has correct scenario name", "Some determinable business situation", pythonRadishHandler.getScenarioName(octaneFeature));

        element = it.next();
        pythonRadishHandler.setElement(element);
        Assert.assertEquals("test case has correct scenario name", "Some another scenario 2", pythonRadishHandler.getScenarioName(octaneFeature));

        //test scenario name with outline
        element = it.next();
        pythonRadishHandler.setElement(element);
        Assert.assertEquals("test case has correct scenario name", "feeding a cow <name> yum yum yum", pythonRadishHandler.getScenarioName(octaneFeature));


        element = it.next();
        pythonRadishHandler.setElement(element);
        Assert.assertEquals("test case has correct scenario name", "feeding a cow <name> yum yum yum", pythonRadishHandler.getScenarioName(octaneFeature));

    }

    @Test
    public void testPythonRadishHandlerWithSameNameOutlineFail() throws FileNotFoundException, XMLStreamException {
        PythonRadishHandler pythonRadishHandler = new PythonRadishHandler();
        Iterator<Element> it = new JunitReportReader(
                new FileInputStream("src/test/resources/python-radish/python-radish-same-name-outline-fail.xml"),
                "testcase").iterator();
        FeatureFileMeta featureFileMeta = new FeatureFileMeta("src/test/resources/features/robustgherkin03.feature",
                GherkinMultiLingualService.DEFAULT_LANGUAGE);
        OctaneFeature octaneFeature = GherkinDocumentUtil.generateSkeletonFeature(featureFileMeta);

        Element element = it.next();
        pythonRadishHandler.setElement(element);
        Assert.assertEquals("test case has correct feature name", "Some terse yet descriptive text of what is desired like bla bla", pythonRadishHandler.getFeatureName().get());
        Assert.assertEquals("test case has correct scenario name", "Some determinable business situation", pythonRadishHandler.getScenarioName(octaneFeature));

        element = it.next();
        pythonRadishHandler.setElement(element);
        Assert.assertEquals("test case has correct scenario name", "Some another scenario 2", pythonRadishHandler.getScenarioName(octaneFeature));

        //test scenario name with outline
        element = it.next();
        pythonRadishHandler.setElement(element);
        Assert.assertEquals("test case has correct scenario name", "feeding a cow <name> yum yum yum", pythonRadishHandler.getScenarioName(octaneFeature));

        element = it.next();
        pythonRadishHandler.setElement(element);
        Assert.assertEquals("test case has correct scenario name", "feeding a cow <name> yum yum yum", pythonRadishHandler.getScenarioName(octaneFeature));


        element = it.next();
        pythonRadishHandler.setElement(element);
        Assert.assertEquals("test case has correct scenario name", "feeding a cow <name> yum yum yum", pythonRadishHandler.getScenarioName(octaneFeature));

    }
}
