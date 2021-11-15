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
import com.microfocus.bdd.api.OctaneStep;
import io.cucumber.gherkin.GherkinDialect;
import io.cucumber.messages.types.Background;
import io.cucumber.messages.types.Scenario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GherkinScenario {

    private String flowContext;
    protected List<GherkinStep> stepsDescriptions = new ArrayList<>();
    protected GherkinDialect dialect;
    private Scenario scenario;

    public GherkinScenario(Background background, Scenario scenario, GherkinDialect dialect) {
        this.scenario = scenario;
        this.dialect = dialect;
        initialize(background, scenario);
    }

    private void initialize(Background background, Scenario scenario) {
        flowContext = "";
        if (background != null) {
            background.getSteps().forEach(step -> {
                updateFlowContext(step);
                stepsDescriptions.add(new GherkinStep(step, flowContext, dialect));
            });
        }
        flowContext = "";
        scenario.getSteps().forEach(step -> {
            updateFlowContext(step);
            stepsDescriptions.add(new GherkinStep(step, flowContext, dialect));
        });
    }

    public String getType() {
        return scenario.getKeyword().trim();
    }

    public String getDescription() {
        return scenario.getDescription() != null ? scenario.getDescription() : "";
    }

    private String updateFlowContext(io.cucumber.messages.types.Step step) {
        String keyword = step.getKeyword();

        if (GherkinMultiLingualService.isAndStep(dialect, keyword) || GherkinMultiLingualService.isButStep(dialect, keyword)) {
            if (flowContext.isEmpty()) {
                if (!keyword.equals(GherkinMultiLingualService.STAR)) {
                    throw new RuntimeException("when using BUT, AND flowContext should not be empty");
                } else {
                    flowContext = dialect.getGivenKeywords().stream().filter(kw -> !kw.equals(keyword)).collect(Collectors.toList()).get(0);
                }
            }
            return flowContext;

        }
        flowContext = keyword;

        return flowContext;
    }

    private OctaneScenario createOctaneScenarioAndSteps() {
        OctaneScenario scenario = createOctaneScenario();
        scenario.setSteps(createSteps());
        return scenario;
    }

    private List<OctaneStep> createSteps() {
        return stepsDescriptions.stream().map(step ->
                new OctaneStep(step.getKeyword(), step.getName(), step.getStepTypeDefinition(),
                        step.getDocString(), step.getFlatDataTable())
        ).collect(Collectors.toList());
    }

    public List<OctaneScenario> createOctaneScenarios() {
        return Collections.singletonList(createOctaneScenarioAndSteps());
    }

    protected OctaneScenario createOctaneScenario() {
        OctaneScenario octaneScenario = new OctaneScenario();
        String name = scenario.getName();
        octaneScenario.setName(name);
        octaneScenario.setOutlineName(name);
        octaneScenario.setScenarioType(getScenarioTypeDefinition());
        return octaneScenario;
    }

    protected String getScenarioTypeDefinition() {
        return GherkinMultiLingualService.getTypeDefinition(dialect, getType());
    }

    public String getName() {
        return scenario.getName();
    }
}
