/*
 * © Copyright [2021] Micro Focus or one of its affiliates.
 * Licensed under Apache License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.microfocus.bdd;

import com.microfocus.bdd.api.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonRadishHandler implements BddFrameworkHandler {
    private static final Pattern scenarioOutLineSuffixPattern = Pattern.compile("\\s-\\srow\\s\\d+"); // pattern "- row 0" or "- row 1"

    private Element element;
    private List<String> errorSteps; //report may contains only 1 error step
    private Boolean isSkippedElement;
    private String errorMessage;

    @Override
    public String getName() {
        return "python-radish";
    }

    @Override
    public void setElement(Element element) {
        this.element = element;
        // add logic for failed message check
        errorSteps = new ArrayList<>();
        isSkippedElement = false;
        if(!element.getChildren().isEmpty()) {
            for(Element stepElement: element.getChildren()) {
                if(stepElement.getName().equals("failure")) {
                    errorSteps.add(stepElement.getAttribute("message"));
                    errorMessage = stepElement.getText();
                }else if(stepElement.getName().equals("skipped")){
                    isSkippedElement = true;
                }
            }
        }
    }

    @Override
    public Optional<String> getFeatureName() {
        return Optional.of(element.getAttribute("classname"));
    }

    @Override
    public Optional<String> getFeatureFile() {
        return Optional.empty();
    }

    @Override
    public String getScenarioName(OctaneFeature feature) {
        //two cases: with outline/without outline
        String elementScenarioName = element.getAttribute("name");
        Matcher scenarioOutLineSuffixMatcher = scenarioOutLineSuffixPattern.matcher(elementScenarioName);
        if (scenarioOutLineSuffixMatcher.find()) {
            //if it contains outline
            //feature.gherkinDocument.feature.children
            String elementScenarioRow = scenarioOutLineSuffixMatcher.group();
            //remove - row 0
            String elementScenarioNameWithoutRow = elementScenarioName.replace(elementScenarioRow, "");
            return elementScenarioNameWithoutRow;
        }
        return elementScenarioName;
    }

    @Override
    public void fillStep(OctaneStep octaneStep) {
        //set each step passed/failed/skipped
        if(isSkippedElement) {
            octaneStep.setStatus(Status.SKIPPED);
            return;
        }

        String stepName = octaneStep.getCompleteStepName();
        if (errorSteps.contains(stepName)) {
            octaneStep.setStatus(Status.FAILED);
            octaneStep.setErrorMessage(errorMessage);
            return;
        }

        octaneStep.setStatus(Status.PASSED);
    }

    @Override
    public String getTestCaseElementName() {
        return "testcase";
    }
}
