/*
 * © Copyright [2021] Micro Focus or one of its affiliates.
 * Licensed under Apache License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.microfocus.bdd;

import com.microfocus.bdd.api.Element;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class JunitReportReader implements Iterable<Element>{

    private XMLEventReader reader;
    private String testcaseElementName;
    private final ElementIterator iterator;

    public JunitReportReader(InputStream inputStream, String testcaseElementName) throws XMLStreamException {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        reader = xmlInputFactory.createXMLEventReader(inputStream, StandardCharsets.UTF_8.name());
        this.testcaseElementName = testcaseElementName;
        iterator = new ElementIterator();
    }

    @Override
    public Iterator<Element> iterator() {
        return iterator;
    }

    private class ElementIterator implements Iterator<Element> {

        private Element currentElement = null;

        public ElementIterator() throws XMLStreamException {
            currentElement = getElement(findNextStartElementEvent());
        }

        @Override
        public boolean hasNext() {
            return currentElement != null;
        }

        @Override
        public Element next() {
            Element oldElement = currentElement;
            try {
                StartElement nextStartElementEvent = findNextStartElementEvent();
                if (null == nextStartElementEvent) {
                    currentElement = null;
                } else {
                    currentElement = getElement(nextStartElementEvent);
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
            return oldElement;
        }

        private Element getElement(StartElement startElementEvent) {
            Element element = new Element();
            try {
                String elementName = getElementName(startElementEvent);
                element.setName(elementName);
                element.setLineNum(startElementEvent.getLocation().getLineNumber());
                extractAttributes(startElementEvent, element);


                while (reader.hasNext()) {
                    XMLEvent event = reader.nextEvent();
                    if (event.isCharacters()) {
                        Characters cev = (Characters) event;
                        if (!cev.isWhiteSpace()) {
                            element.setText(element.getText() + cev.getData());
                        }
                    } else if (event.isStartElement()) {
                        StartElement startEvent = (StartElement) event;
                        element.appendChild(getElement(startEvent));
                    } else if (event.isEndElement()) {
                        if (getElementName((EndElement) event).equals(elementName)) {
                            return element;
                        }
                    }
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
            return element;
        }

        private StartElement findNextStartElementEvent() throws XMLStreamException {
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    String name = getElementName((StartElement) event);
                    if (testcaseElementName.equals(name)) {
                        return (StartElement) event;
                    };
                }
            }
            return null;
        }

        private String getElementName(EndElement event) {
            return event.getName().getLocalPart();
        }

        private String getElementName(StartElement event) {
            return event.getName().getLocalPart();
        }

        private void extractAttributes(StartElement startElement, Element element) {
            startElement.getAttributes().forEachRemaining(attr -> {
                Attribute attribute = (Attribute) attr;
                element.getAttributes().put(attribute.getName().getLocalPart(), attribute.getValue());
            });
        }
    }
}
