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
import io.cucumber.messages.types.TableCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class GherkinScenarioOutline extends GherkinScenario {

    private List<Map<String, String>> examplesRowsValues = new ArrayList<>();

    public GherkinScenarioOutline(Background background, Scenario scenario, GherkinDialect dialect) {
        super(background, scenario, dialect);
        initialize(scenario);
    }

    private void initialize(Scenario scenarioOutline) {
        scenarioOutline.getExamples().forEach(example -> {
            List<TableCell> headers = example.getTableHeader().getCells();
            example.getTableBody().forEach(row -> {
                Map<String, String> values = new HashMap<>();
                List<TableCell> cells = row.getCells();
                for (int i = 0; i < headers.size(); i++) {
                    values.put(headers.get(i).getValue(), cells.get(i).getValue());
                }
                examplesRowsValues.add(values);
            });
        });
    }

    @Override
    public List<OctaneScenario> createOctaneScenarios() {
        int scenarioMinorIndex = 0;
        List<OctaneScenario> octaneScenarios = new ArrayList<>();

        for (Map<String, String> rowParametersValues : examplesRowsValues) {
            scenarioMinorIndex++;
            OctaneScenario octaneScenario = createOctaneScenarioAndSteps(scenarioMinorIndex, rowParametersValues);
            octaneScenarios.add(octaneScenario);
        }
        return octaneScenarios;
    }

    private OctaneScenario createOctaneScenarioAndSteps(int index, Map<String, String> rowParametersValues) {
        OctaneScenario scenario = createOctaneScenario(index, rowParametersValues);
        scenario.setSteps(createSteps(rowParametersValues));
        return scenario;
    }


    private OctaneScenario createOctaneScenario(int index, Map<String, String> rowParametersValues) {
        OctaneScenario scenario = createOctaneScenario();
        scenario.setOutlineIndex(index);
        String name = getName();
        for (Map.Entry<String, String> entry : rowParametersValues.entrySet()) {
            name = replaceParameter(name, entry);
        }
        scenario.setOutlineName(name);
        scenario.setName(getName());
        return scenario;
    }

    private List<OctaneStep> createSteps(Map<String, String> rowParametersValues) {
        return stepsDescriptions.stream().map(step -> createOctaneStep(step, rowParametersValues)).collect(Collectors.toList());
    }

    protected OctaneStep createOctaneStep(GherkinStep step, Map<String, String> nameValueMap) {
        String stepName = step.getName();
        for (Map.Entry<String, String> entry : nameValueMap.entrySet()) {
            stepName = replaceParameter(stepName, entry);
        }
        return new OctaneStep(step.getKeyword(), stepName, step.getStepTypeDefinition(),
                step.getDocString(), step.getFlatDataTable());
    }

    private String replaceParameter(String text, Map.Entry<String, String> parameterEntry) {
        return text.replaceAll(
                "<" + parameterEntry.getKey() + ">", Matcher.quoteReplacement(parameterEntry.getValue()));
    }
}
