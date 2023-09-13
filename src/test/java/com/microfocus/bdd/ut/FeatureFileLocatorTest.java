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

import com.microfocus.bdd.FeatureFileLocator;
import com.microfocus.bdd.FeatureFileMeta;
import com.microfocus.bdd.gherkin.GherkinMultiLingualService;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeatureFileLocatorTest {
    @Test
    public void findFeatureFileTest() throws IOException {
        String featureName = "Some terse yet descriptive text of what is desired like bla bla";
        String featureName01 = "Some terse yet descriptive text of what is desired: like bla bla01";
        String featureName02 = "Some terse yet descriptive text of what is desired like bla bla02";
        String featureName03 = "Some terse yet descriptive text of what is desired like bla bla tag in comment";
        String featureNameInCN = "一个功能";
        String featureNameInDE = "Funktion";
        String featureNameInDELanguageTag01 = "Funktion01";
        String featureNameInDELanguageTag02 = "Funktion02";
        FeatureFileLocator featureFileLocator = new FeatureFileLocator(Arrays.asList(
                "src/test/resources/features/robustgherkin.feature",
                "src/test/resources/features/robustgherkin_language_tag_in_comment.feature",
                "src/test/resources/features/robustgherkin_cn.feature",
                "src/test/resources/features/robustgherkin_de.feature",
                "src/test/resources/features/robustgherkin_de_language_tag_01.feature",
                "src/test/resources/features/robustgherkin_de_language_tag_02.feature",
                "src/test/resources/features/robustgherkin01.feature",
                "src/test/resources/features/robustgherkin02.feature"));
        Assert.assertEquals("find robustgherkin.feature by name: " + featureName, "src/test/resources/features/robustgherkin.feature",
                featureFileLocator.getFeatureFileMeta(featureName).getFeatureFile());
        Assert.assertEquals("find robustgherkin.feature language is " + GherkinMultiLingualService.DEFAULT_LANGUAGE, GherkinMultiLingualService.DEFAULT_LANGUAGE,
                featureFileLocator.getFeatureFileMeta(featureName).getLanguage());
        Assert.assertEquals("find robustgherkin01.feature by name: " + featureName01, "src/test/resources/features/robustgherkin01.feature",
                featureFileLocator.getFeatureFileMeta(featureName01).getFeatureFile());
        Assert.assertEquals("find robustgherkin02.feature by name: " + featureName02, "src/test/resources/features/robustgherkin02.feature",
                featureFileLocator.getFeatureFileMeta(featureName02).getFeatureFile());
        Assert.assertEquals("find robustgherkin_cn.feature by name: " + featureNameInCN, "src/test/resources/features/robustgherkin_cn.feature",
                featureFileLocator.getFeatureFileMeta(featureNameInCN).getFeatureFile());
        Assert.assertEquals("find robustgherkin.feature language is " + GherkinMultiLingualService.CN_LANGUAGE, GherkinMultiLingualService.CN_LANGUAGE,
                featureFileLocator.getFeatureFileMeta(featureNameInCN).getLanguage());
        Assert.assertEquals("find robustgherkin_de.feature by name: " + featureNameInDE, "src/test/resources/features/robustgherkin_de.feature",
                featureFileLocator.getFeatureFileMeta(featureNameInDE).getFeatureFile());
        Assert.assertEquals("find robustgherkin.feature language is " + GherkinMultiLingualService.DE_LANGUAGE, GherkinMultiLingualService.DE_LANGUAGE,
                featureFileLocator.getFeatureFileMeta(featureNameInDE).getLanguage());
        Assert.assertEquals("find robustgherkin.feature language is " + GherkinMultiLingualService.DE_LANGUAGE, GherkinMultiLingualService.DE_LANGUAGE,
                featureFileLocator.getFeatureFileMeta(featureNameInDELanguageTag01).getLanguage());
        Assert.assertEquals("find robustgherkin.feature language is " + GherkinMultiLingualService.DE_LANGUAGE, GherkinMultiLingualService.DE_LANGUAGE,
                featureFileLocator.getFeatureFileMeta(featureNameInDELanguageTag02).getLanguage());
        Assert.assertEquals("find robustgherkin.feature language is " + GherkinMultiLingualService.DEFAULT_LANGUAGE, GherkinMultiLingualService.DEFAULT_LANGUAGE,
                featureFileLocator.getFeatureFileMeta(featureName03).getLanguage());
    }

    @Test
    public void getFeatureFileMetaTest() throws IOException {
        List<String> featureFilePath = new ArrayList<>(Arrays.asList("src/test/resources/features/robustgherkin.feature", "src/test/resources/features/robustgherkin_cn.feature",
                "src/test/resources/features/robustgherkin01.feature", "src/test/resources/features/robustgherkin02.feature"));
        FeatureFileLocator featureFileLocator = new FeatureFileLocator(featureFilePath);
        Assert.assertTrue("can not find feature file",
                featureFileLocator.getFeatureFileMeta("I am fake") == null);
        Assert.assertEquals("find language: " + GherkinMultiLingualService.DEFAULT_LANGUAGE, GherkinMultiLingualService.DEFAULT_LANGUAGE,
                featureFileLocator.getFeatureFileMeta("Some terse yet descriptive text of what is desired like bla bla").getLanguage());
        Assert.assertEquals("find language: " + GherkinMultiLingualService.CN_LANGUAGE, GherkinMultiLingualService.CN_LANGUAGE,
                featureFileLocator.getFeatureFileMeta("一个功能").getLanguage());
    }

    @Test
    public void testMapContainsFeatureNameWithFeatureFileMeta() throws IOException {
        List<String> featureFilePath = new ArrayList<>(Arrays.asList("src/test/resources/features/robustgherkin_cn.feature", "src/test/resources/features/robustgherkin.feature",
                "src/test/resources/features/robustgherkin01.feature", "src/test/resources/features/robustgherkin02.feature"));
        FeatureFileLocator featureFileLocator = new FeatureFileLocator(featureFilePath);
        FeatureFileMeta meta1 = featureFileLocator.getFeatureFileMeta("Some terse yet descriptive text of what is desired like bla bla");
        FeatureFileMeta meta2 = featureFileLocator.getFeatureFileMeta("Some terse yet descriptive text of what is desired like bla bla");
        Assert.assertTrue("Feature file meta is cached",
                meta1 == meta2);
        meta1 = featureFileLocator.getFeatureFileMeta("一个功能");
        meta2 = featureFileLocator.getFeatureFileMeta("一个功能");
        Assert.assertTrue("Feature file meta is cached",
                meta1 == meta2);
    }

    @Test
    public void testGetFeature() {
        List<String> featureFilePath = new ArrayList<>(Arrays.asList(
                "src/test/resources/features/robustgherkin_cn.feature",
                "src/test/resources/features/feature_name_is_empty.feature"
        ));
        FeatureFileLocator locator = new FeatureFileLocator(featureFilePath);
        String scenarioName = "Some another scenario 2 (empty feature name)";
        String cnScenario = "一个场景";
        Assert.assertFalse("scenario is not in cache",
                locator.getFeatureFileMetaByScenarioNameFromCache(scenarioName).isPresent());

        locator.getOctaneFeatureByScenarioNameFromFile(scenarioName);
        Assert.assertTrue("scenario is in cache",
                locator.getFeatureFileMetaByScenarioNameFromCache(scenarioName).isPresent());
        Assert.assertFalse("cn feature is NOT in cache as only unnamed feature is cached with scenario name",
                locator.getFeatureFileMetaByScenarioNameFromCache(cnScenario).isPresent());

    }
}
