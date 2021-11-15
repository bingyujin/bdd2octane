/*
 * © Copyright [2021] Micro Focus or one of its affiliates.
 * Licensed under Apache License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.microfocus.bdd;

import com.google.common.base.Strings;
import com.microfocus.bdd.api.*;
import com.microfocus.bdd.util.FileUtil;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;


public class Bdd2Octane {

    private Class<? extends BddFrameworkHandler> handlerClass;
    private String testCaseElementName;
    private List<String> reportFiles;
    private List<String> featureFiles;
    private String framework;
    private String outPutFilePath;
    private OctaneFeatureLocator octaneFeatureLocator;
    private String toolVersion;


    public Bdd2Octane(List<String> reportFiles, List<String> featureFiles, String resultFilePath, String framework) {
        this.reportFiles = reportFiles;
        this.featureFiles = featureFiles;
        this.framework = framework;
        this.outPutFilePath = FileUtil.getResultFilePath(resultFilePath, framework);
        initializeHandler();
        octaneFeatureLocator = new OctaneFeatureLocator(featureFiles);
        toolVersion = getToolVersion();
    }

    public void run() throws IOException, XMLStreamException, InstantiationException, IllegalAccessException {

        XMLOutputFactory xmlOutFact = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = xmlOutFact.createXMLStreamWriter(new FileOutputStream(outPutFilePath), StandardCharsets.UTF_8.name());
        writeDocumentHeader(writer);

        OctaneFeature previousFeature = null;
        int count = 0, skipped = 0;
        for (String file : reportFiles) {
            for (Element testCaseElement : getTestCaseElements(file)) {
                BddFrameworkHandler bddFrameworkHandler = handlerClass.newInstance();
                bddFrameworkHandler.setElement(testCaseElement);

                //1. get feature name or feature file, parse it into Gherkin Document and expand scenarios.
                Optional<String> featureFilePath = bddFrameworkHandler.getFeatureFile();
                OctaneFeature octaneFeature;

                if (featureFilePath.isPresent()) {
                    String featureFile = featureFilePath.get();
                    Optional<String> canonicalizeFilePath = canonicalizeFilePath(featureFile);
                    if (canonicalizeFilePath.isPresent()) {
                        octaneFeature = octaneFeatureLocator.getOctaneFeatureByPath(canonicalizeFilePath.get());
                    } else {
                        System.err.println("cannot locate a feature file by path:  " + featureFile + " skipping...");
                        skipped ++;
                        continue;
                    }
                } else {
                    Optional<String> featureNameOpt = bddFrameworkHandler.getFeatureName();
                    if (featureNameOpt.isPresent() && !featureNameOpt.get().isEmpty()) {
                        String featureName = featureNameOpt.get();
                        Optional<OctaneFeature> octaneFeatureOpt = octaneFeatureLocator.getOctaneFeatureByName(featureName);
                        if (!octaneFeatureOpt.isPresent()) {
                            System.err.println("Cannot locate a feature file for: " + featureName + " skipping ...");
                            skipped ++;
                            continue;
                        }
                        octaneFeature = octaneFeatureOpt.get();
                    } else {
                        System.err.println("Feature name is empty in element:\n" + testCaseElement + " try searching by scenario name");
                        String scenarioName = testCaseElement.getAttribute("name");
                        Optional<OctaneFeature> feature = octaneFeatureLocator.getFeatureByScenarioName(scenarioName);
                        if (feature.isPresent()) {
                            octaneFeature = feature.get();
                        } else {
                            System.err.println("Cannot locate a feature file by scenario name for: " + scenarioName + ", skipping...");
                            skipped ++;
                            continue;
                        }
                    }
                }

                if (previousFeature != null) {
                    if (previousFeature != octaneFeature) {
                        writeFeatureToXML(writer, previousFeature);
                        previousFeature = null;
                    }
                } else {
                    previousFeature = octaneFeature;
                }

                //2. get scenarioName
                String scenarioName = bddFrameworkHandler.getScenarioName(octaneFeature);
                if (Strings.isNullOrEmpty(scenarioName)) {
                    System.err.println(bddFrameworkHandler.getClass().getSimpleName()
                            + " cannot extract a scenario out of XML " + file + ":" + testCaseElement.getLineNum() + "\n"
                            + testCaseElement
                            + "\n skipping ...");
                    skipped ++;
                    continue;
                }

                //3. Merge steps into a scenario.
                Optional<OctaneScenario> scenario = octaneFeature.findScenarioAndUpdateOutlineIndex(scenarioName);
                if (!scenario.isPresent()) {
                    System.err.println("cannot find scenario: " + scenarioName + " from feature");
                    skipped ++;
                    continue;
                }
                mergeScenario(bddFrameworkHandler, scenario.get());
                count++;
            }
            if (previousFeature != null) {
                writeFeatureToXML(writer, previousFeature);
                previousFeature = null;
            }
        }
        writeDocumentEnd(writer);
        System.out.println(String.format("%d test case(s) processed", count));
        System.out.println(String.format("%d test case(s) skipped", skipped));
    }

    private void writeFeatureToXML(XMLStreamWriter writer, OctaneFeature octaneFeature) throws XMLStreamException, IOException {
        octaneFeature.writeToXml(writer);
        octaneFeatureLocator.remove(octaneFeature);
    }

    private void mergeScenario(BddFrameworkHandler bddFrameworkHandler, OctaneScenario scenario) {
        Iterator<OctaneStep> stepsIterator = scenario.getSteps().iterator();
        while (stepsIterator.hasNext()) {
            OctaneStep octaneStep = stepsIterator.next();
            bddFrameworkHandler.fillStep(octaneStep);
            if (octaneStep.getStatus() != Status.PASSED) {
                break;
            }
        }
        while (stepsIterator.hasNext()) {
            //todo, need to confirm whether to ignore steps or mark them as skipped.
            OctaneStep octaneStep = stepsIterator.next();
            octaneStep.setStatus(Status.SKIPPED);
        }
        scenario.markMerged();
    }

    private Optional<String> canonicalizeFilePath(String featureFile) {
        return featureFiles.stream().filter(f -> {
            if (featureFile.startsWith("file:///")) {
                try {
                    URI uri = new URI(featureFile);
                    return Paths.get(f).toUri().equals(uri);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            } else if (featureFile.startsWith("file:")) {
                return Paths.get(f).endsWith(featureFile.substring(5));
            } else if (featureFile.startsWith("classpath:")) {
                return Paths.get(f).endsWith(featureFile.substring(10));
            } else {
                return Paths.get(f).endsWith(featureFile);
            }
        }).findFirst();
    }

    private void writeDocumentEnd(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();//  </features>
        writer.writeCharacters("\n");
        writer.writeEndDocument();
        writer.close();
    }

    private void writeDocumentHeader(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartDocument();
        writer.writeCharacters("\n");
        writer.writeComment("Generated by " + toolVersion);
        writer.writeCharacters("\n");
        writer.writeStartElement("features");
        writer.writeAttribute("version", "1");
        writer.writeCharacters("\n");
    }

    private Iterable<Element> getTestCaseElements(String file) throws FileNotFoundException, XMLStreamException {
        return new JunitReportReader(new FileInputStream(file), testCaseElementName);
    }

    private void initializeHandler() {
        Set<Class<? extends BddFrameworkHandler>> implementations = App.getBddHandlerImplementations();
        if (implementations.isEmpty()) {
            throw new RuntimeException("No handler found");
        }
        try {
            for (Class<? extends BddFrameworkHandler> handlerClass : implementations) {
                BddFrameworkHandler handler = handlerClass.newInstance();
                if (handler.getName().equals(framework)) {
                    this.handlerClass = handlerClass;
                    this.testCaseElementName = handler.getTestCaseElementName();
                    return;
                }
            }
            System.err.println("Supported frameworks: ");
            System.err.print(App.getAllHandlerNames(implementations));
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }

        throw new RuntimeException("No handler found for: " + framework);
    }

    private String getToolVersion() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("build.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty("version");
        } catch (IOException ex) {
            System.err.println("Failed to get tool version");
        }
        return null;
    }
}
