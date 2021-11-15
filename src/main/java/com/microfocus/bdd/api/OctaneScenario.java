/*
 * © Copyright [2021] Micro Focus or one of its affiliates.
 * Licensed under Apache License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.microfocus.bdd.api;

import com.microfocus.bdd.gherkin.GherkinMultiLingualService;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

public class OctaneScenario {
    // scenario name contains <> like feeding a cow <name> yum yum yum for outline scenario
    private String name;
    // scenario name filled with example values like feeding a cow cow1 yum yum yum
    private String outlineName;
    private int outlineIndex;
    private List<OctaneStep> octaneSteps;
    private String scenarioType;
    private boolean merged = false;

    public OctaneScenario() {
    }

    public String getOutlineName() {
        return outlineName;
    }

    public void setOutlineName(String outlineName) {
        this.outlineName = outlineName;
    }

    public String getScenarioType() {
        return scenarioType;
    }

    public void setScenarioType(String scenarioType) {
        this.scenarioType = scenarioType;
    }

    public void markMerged() {
        this.merged = true;
    }

    public boolean isMerged() {
        return merged;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OctaneStep> getSteps() {
        return octaneSteps;
    }

    public void setSteps(List<OctaneStep> octaneSteps) {
        this.octaneSteps = octaneSteps;
    }

    public int getOutlineIndex() {
        return outlineIndex;
    }

    public void setOutlineIndex(int outlineIndex) {
        this.outlineIndex = outlineIndex;
    }

    public void addStep(OctaneStep octaneStep) {
        octaneSteps.add(octaneStep);
    }

    public void writeToXml(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("scenario");
        writer.writeAttribute("name", name);
        if (scenarioType.equals(GherkinMultiLingualService.SCENARIO_OUTLINE)) {
            writer.writeAttribute("outlineIndex", String.valueOf(outlineIndex));
        }
        writer.writeCharacters("\n");
        writer.writeStartElement("steps");
        writer.writeCharacters("\n");
        for (OctaneStep octaneStep : octaneSteps) {
            octaneStep.writeToXml(writer);
        }
        writer.writeEndElement();
        writer.writeCharacters("\n");
        writer.writeEndElement();
    }
}
