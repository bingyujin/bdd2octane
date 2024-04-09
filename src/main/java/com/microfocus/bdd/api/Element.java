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
package com.microfocus.bdd.api;

import java.util.*;

public class Element {
    String name;
    Properties attributes = new Properties();
    List<Element> children = new ArrayList<>();
    String text = "";

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    int lineNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Properties getAttributes() {
        return attributes;
    }

    public void setAttributes(Properties attributes) {
        this.attributes = attributes;
    }

    public List<Element> getChildren() {
        return children;
    }

    public void appendChild(Element child) {
        this.children.add(child);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAttribute(String name) {
        return attributes.getProperty(name);
    }

    public Optional<Element> getChild(String name) {
        return children.stream().filter(c->c.getName().equals(name)).findFirst();
    }

    @Override
    public String toString() {
        return String.format("<%s %s>...</%s>", name, attributesString(), name);
    }

    private String attributesString() {
        StringBuilder sb = new StringBuilder();
        attributes.stringPropertyNames().forEach(name->{
            sb.append(name);
            sb.append('=');
            sb.append('\"');
            sb.append(attributes.getProperty(name));
            sb.append("\" ");
        });
        return sb.toString();
    }
}
