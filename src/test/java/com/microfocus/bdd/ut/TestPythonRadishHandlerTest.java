 /**
 *
 * Copyright 2021-2023 Open Text
 *
 * The only warranties for products and services of Open Text and
 * its affiliates and licensors (“Open Text”) are as may be set forth
 * in the express warranty statements accompanying such products and services.
 * Nothing herein should be construed as constituting an additional warranty.
 * Open Text shall not be liable for technical or editorial errors or
 * omissions contained herein. The information contained herein is subject
 * to change without notice.
 *
 * Except as specifically indicated otherwise, this document contains
 * confidential information and a valid license is required for possession,
 * use or copying. If this work is provided to the U.S. Government,
 * consistent with FAR 12.211 and 12.212, Commercial Computer Software,
 * Computer Software Documentation, and Technical Data for Commercial Items are
 * licensed to the U.S. Government under vendor's standard commercial license.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
