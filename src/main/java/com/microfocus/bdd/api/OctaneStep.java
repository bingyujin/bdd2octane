/*
 * © Copyright [2021] Micro Focus or one of its affiliates.
 * Licensed under Apache License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.microfocus.bdd.api;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Objects;
import java.util.Optional;

public class OctaneStep {

    // step name does not contain keywords like Given, Then, *
    private final String name;
    // keyword is a localized keyword
    private final String keyword;
    // step type is Given, When, Then, But (* is translated as well)
    private final String stepType;
    private Status status;
    private Long duration = 0l;
    private String errorMessage = null;
    private final Optional<String> docString;
    private final Optional<String> dataTable;

    public OctaneStep(String keyword, String name, String stepType, Optional<String> docString, Optional<String> dataTable) {
        this.name = name;
        this.keyword = keyword;
        this.stepType = stepType;
        this.docString = docString;
        this.dataTable = dataTable;
    }

    public String getStepType() {
        return stepType;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public void writeToXml(XMLStreamWriter writer) throws XMLStreamException {
        StringBuilder stepName = new StringBuilder(getCompleteStepName());
        docString.ifPresent(ds-> {
            stepName.append('\n');
            stepName.append(ds);
        });
        dataTable.ifPresent(dt -> {
            stepName.append('\n');
            stepName.append(dt);
        });

        writer.writeStartElement("step");
        writer.writeAttribute("name", stepName.toString());
        writer.writeAttribute("duration", Long.toString(duration));
        writer.writeAttribute("status", status == null ? "undefined" : status.toString());

        if (status == Status.FAILED) {
            writer.writeCharacters("\n");
            writer.writeStartElement("error_message");
            writer.writeCData(errorMessage);
            writer.writeEndElement();//error_message
            writer.writeCharacters("\n");
        }
        writer.writeEndElement();
        writer.writeCharacters("\n");
    }

    public String getCompleteStepName() {
        return keyword + " " + name;
    }
}
