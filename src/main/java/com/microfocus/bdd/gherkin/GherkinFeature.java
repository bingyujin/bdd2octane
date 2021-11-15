/*
 * © Copyright [2021] Micro Focus or one of its affiliates.
 * Licensed under Apache License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.microfocus.bdd.gherkin;

import com.microfocus.bdd.api.OctaneScenario;
import io.cucumber.gherkin.GherkinDialect;
import io.cucumber.gherkin.GherkinDialectProvider;
import io.cucumber.messages.types.Background;
import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.FeatureChild;
import io.cucumber.messages.types.Scenario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Describes a GHERKIN Test feature
 */
public class GherkinFeature {

    private final List<GherkinScenario> scenarios = new ArrayList<>();
    private Background background;
    private GherkinDialect dialect;
    private Feature feature;

    public GherkinFeature(Feature feature) {
        this.feature = feature;
        this.dialect = new GherkinDialectProvider().getDialect(feature.getLanguage(), null);
        initialize(feature);
    }

    private void initialize(Feature feature) {
        List<FeatureChild> children = feature.getChildren();
        if (!children.isEmpty()) {
            children.forEach(child -> {
                if (GherkinMultiLingualService.hasBackground(child)) {
                    background = child.getBackground();
                }
                if (GherkinMultiLingualService.hasScenario(child)) {
                    Scenario scenario = child.getScenario();
                    if (GherkinMultiLingualService.isOutlineScenario(scenario)) {
                        scenarios.add(new GherkinScenarioOutline(background, scenario, dialect));
                    } else {
                        scenarios.add(new GherkinScenario(background, scenario, dialect));
                    }
                }
            });
        }
    }

    public String getDescription() {
        return feature.getDescription() != null ? feature.getDescription() : "";
    }

    public List<GherkinScenario> getScenarios() {
        return scenarios;
    }


    public List<OctaneScenario> createOctaneScenarios(GherkinScenario scenario) {
        if (scenario == null) {
            return Collections.emptyList();
        }
        return scenario.createOctaneScenarios();
    }

    public String getName() {
        return feature.getName();
    }
}
