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

public class PhpBehatHandler implements BddFrameworkHandler {

    private Element element;
    private String status;
    private String errorMessage;

    @Override
    public String getName() {
        return "php-behat";
    }

    @Override
    public void setElement(Element element) {
        this.element = element;
        this.status = element.getAttribute("status");
        if (status.equalsIgnoreCase(Status.FAILED.toString())) {
            this.errorMessage = element.getChild("failure").get().getAttribute("message");
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
        String elementName = element.getAttribute("name");
        if (!feature.getScenarios(elementName).isEmpty()) {
            return elementName;
        }
        for (OctaneScenario scenario : feature.getScenarios()) {
            if (elementName.startsWith(scenario.getName())) {
                return scenario.getName();
            }
        }
        return null;
    }


    @Override
    public void fillStep(OctaneStep octaneStep) {
        if (status.equalsIgnoreCase(Status.PASSED.toString())) {
            octaneStep.setStatus(Status.PASSED);
        } else if (status.equalsIgnoreCase(Status.FAILED.toString())) {
            if (errorMessage.contains(octaneStep.getName())) {
                octaneStep.setStatus(Status.FAILED);
                octaneStep.setErrorMessage(errorMessage);
            } else {
                octaneStep.setStatus(Status.PASSED);
            }
        } else {
            octaneStep.setStatus(Status.SKIPPED);
        }
    }

    @Override
    public String getTestCaseElementName() {
        return "testcase";
    }
}
