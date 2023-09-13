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
package com.microfocus.bdd.util;

import com.google.common.collect.LinkedListMultimap;
import com.microfocus.bdd.FeatureFileMeta;
import com.microfocus.bdd.api.*;
import com.microfocus.bdd.gherkin.GherkinFeature;
import com.microfocus.bdd.gherkin.GherkinScenario;
import io.cucumber.gherkin.GherkinDocumentBuilder;
import io.cucumber.gherkin.Parser;
import io.cucumber.gherkin.ParserException;
import io.cucumber.gherkin.TokenMatcher;
import io.cucumber.messages.IdGenerator;
import io.cucumber.messages.types.GherkinDocument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GherkinDocumentUtil {
    public static OctaneFeature generateSkeletonFeature(FeatureFileMeta featureFileMeta) {
        List<OctaneScenario> octaneScenarios = new ArrayList<>();
        LinkedListMultimap<String, OctaneScenario> scenarioName2Scenario = LinkedListMultimap.create();
        GherkinDocument gherkinDocument = getDocument(featureFileMeta);
        Optional<GherkinFeature> featureOpt = getFeature(gherkinDocument);
        GherkinFeature gherkinTestFeature = featureOpt.get();

        OctaneFeature octaneFeature = new OctaneFeature();
        octaneFeature.setName(gherkinTestFeature.getName());
        for (GherkinScenario gherkinTestScenario : gherkinTestFeature.getScenarios()) {
            octaneScenarios.addAll(gherkinTestFeature.createOctaneScenarios(gherkinTestScenario));
        }
        for (OctaneScenario octaneScenario : octaneScenarios) {
            scenarioName2Scenario.put(octaneScenario.getName(), octaneScenario);
        }
        octaneFeature.setScenarios(scenarioName2Scenario);
        octaneFeature.setGherkinDocument(gherkinDocument);
        octaneFeature.setFeatureFile(featureFileMeta.getFeatureFile());
        return octaneFeature;
    }

    private static GherkinDocument getDocument(FeatureFileMeta featureFileMeta) {
        try {
            return parse(String.join("\n", Files.readAllLines(Paths.get(featureFileMeta.getFeatureFile()))), featureFileMeta.getLanguage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static GherkinDocument parse(String script, String language) throws ParserException {
        TokenMatcher matcher = new TokenMatcher(language);
        Parser<GherkinDocument> parser = new Parser<>(new GherkinDocumentBuilder(new IdGenerator.Incrementing()));
        return parser.parse(script, matcher);
    }

    private static Optional<GherkinFeature> getFeature(GherkinDocument gherkinDocument) {
        GherkinFeature feature = new GherkinFeature(gherkinDocument.getFeature());

        return Optional.of(feature);
    }
}
