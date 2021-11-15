/*
 * © Copyright [2021] Micro Focus or one of its affiliates.
 * Licensed under Apache License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.microfocus.bdd.api;

import java.util.Optional;

public interface BddFrameworkHandler {

    /**
     *
     * @return the name of the BDD framework
     */
    String getName();

    /**
     *
     * @param element, the XML element that represents a scenario, the implementation can take this element,
     *                 store it for later usage or parse it immediately.
     */
    void setElement(Element element);

    /**
     *  This method is supposed to extract feature name from XML element
     * @return the feature name optionally. If it is empty, this XML element is skipped and an error message is printed.
     */
    Optional<String> getFeatureName();

    /**
     *  This method is supposed to extract feature file name from XML element. It is treated a shortcut to location the
     *   feature file of the scenario. If it is empty, all the feature files could be scanned in order to locate the
     *   corresponding feature file.
     * @return Optional of feature file (including path)
     */
    Optional<String> getFeatureFile();

    /**
     *  This method is to get the scenario name out of the OctaneFeature structure. If the scenario is an outlined one,
     *  the name must contains parameter name inside a pair of brackets.
     * @param feature, the OctaneFeature
     * @return the scenario name
     */
    String getScenarioName(OctaneFeature feature);

    /**
     *  This method is invoked to get back the status of the OctaneStep, the implementation should set correct Status
     *  based on the XML element, and in case of failure, set the errorMessage as well.
     *  If the duration is available in XML element, the implementation also should set it by invoking setDuration.
     *
     * @param octaneStep
     */
    void fillStep(OctaneStep octaneStep);

    /**
     * @return the XML element name of a scenario, most of bdd frameworks use "testcase"
     * cucumber-js uses "testsuite"
     */
    String getTestCaseElementName();

}
