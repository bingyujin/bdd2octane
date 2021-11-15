/*
 * © Copyright [2021] Micro Focus or one of its affiliates.
 * Licensed under Apache License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.microfocus.bdd.ut;

import com.microfocus.bdd.CucumberRubyHandler;
import com.microfocus.bdd.api.Element;
import com.microfocus.bdd.JunitReportReader;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;

public class TestCucumberRubyHandler {
    @Test
    public void testRubyHandler() throws FileNotFoundException, XMLStreamException {
        CucumberRubyHandler handler = new CucumberRubyHandler();
        Iterator<Element> element = new JunitReportReader(new FileInputStream("src/test/resources/cucumber-ruby/TEST-features-gherkin_sample_all_passed.xml"),
                "testcase").iterator();
        handler.setElement(element.next());
        Assert.assertEquals("test case has correct feature name",
                "Some terse yet descriptive text of what is desired like bla bla 3 examples", handler.getFeatureName().get());
        Assert.assertEquals("test case has correct scenario name",
                "Some determinable business situation",
                handler.getScenarioName(null));
    }
}
