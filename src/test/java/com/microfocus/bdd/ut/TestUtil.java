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
import com.microfocus.bdd.FeatureFileMeta;
import com.microfocus.bdd.JunitReportReader;
import com.microfocus.bdd.gherkin.GherkinMultiLingualService;
import com.microfocus.bdd.util.GherkinDocumentUtil;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;

public class TestUtil {

    public static Element getXmlElement(String reportFile, String elementName, int num) {
        Iterator<Element> it = null;
        try {
            it = (new JunitReportReader(new FileInputStream(reportFile), elementName)).iterator();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(num-->1) {
            it.next();
        }
        return it.next();
    }

    public static OctaneFeature parseFeatureFile(String featureFile) {
        FeatureFileMeta featureFileMeta = new FeatureFileMeta(featureFile, GherkinMultiLingualService.DEFAULT_LANGUAGE);
        return GherkinDocumentUtil.generateSkeletonFeature(featureFileMeta);
    }
}
