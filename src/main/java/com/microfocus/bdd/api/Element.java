/*
 * © Copyright [2021] Micro Focus or one of its affiliates.
 * Licensed under Apache License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
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
