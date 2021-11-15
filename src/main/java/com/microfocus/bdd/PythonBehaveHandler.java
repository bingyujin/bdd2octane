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

import java.util.Optional;

public class PythonBehaveHandler implements BddFrameworkHandler {
    private Element element;
    private String errorMessage;
    private Optional<String> featureFilePath = Optional.empty();
    private Optional<String> failedStepName = Optional.empty();
    private boolean isSkipped = false;

    @Override
    public String getName() {
        return "python-behave";
    }

    @Override
    public void setElement(Element element) {
        this.element = element;

        if (!element.getChildren().isEmpty()) {
            Element child = element.getChildren().get(0);
            if (child.getName().equals("failure") || child.getName().equals("error")) {
                errorMessage = child.getText();
                featureFilePath = findFeatureFile(errorMessage);
                failedStepName = findFailedStepName(errorMessage);
            } else if (child.getName().equals("skipped")) {
                isSkipped = true;
            }
        }
    }

    private Optional<String> findFeatureFile(String wholeMessage) {
        String[] lines = wholeMessage.split("\n");
        for (String line : lines) {
            if (line.trim().startsWith("Location")) {
                return Optional.of(line.split(":")[1].trim());
            }
        }
        return Optional.empty();
    }

    private Optional<String> findFailedStepName(String wholeMessage) {
        String[] lines = wholeMessage.split("\n");
        for (String line : lines) {
            if (line.trim().startsWith("Failing step:")) {
                return Optional.of(line.split("\\s\\.\\.\\.\\s|Failing step:\\s")[1].trim());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getFeatureName() {
        String featureNameWithFileName = element.getAttribute("classname");
        return Optional.of(featureNameWithFileName.substring(featureNameWithFileName.indexOf(".") + 1));
    }

    @Override
    public Optional<String> getFeatureFile() {
        return featureFilePath;
    }

    @Override
    public String getScenarioName(OctaneFeature feature) {
        String sceName = element.getAttribute("name");
        if (!feature.getScenarios(sceName).isEmpty()) {
            return sceName;
        }
        for (OctaneScenario sce : feature.getScenarios()) {
            if (sceName.startsWith(sce.getOutlineName())) {
                return (sce.getName());
            }
        }
        return null;
    }

    @Override
    public void fillStep(OctaneStep octaneStep) {
        if (isSkipped) {
            octaneStep.setStatus(Status.SKIPPED);
            return;
        }
        if (failedStepName.isPresent()) {
            String stepName = octaneStep.getCompleteStepName();
            if (stepName.equals(failedStepName.get())) {
                octaneStep.setStatus(Status.FAILED);
                octaneStep.setErrorMessage(errorMessage);
            } else {
                octaneStep.setStatus(Status.PASSED);
            }
        } else {
            octaneStep.setStatus(Status.PASSED);
        }
    }

    @Override
    public String getTestCaseElementName() {
        return "testcase";
    }
}
