/*
 * © Copyright [2021] Micro Focus or one of its affiliates.
 * Licensed under Apache License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.microfocus.bdd.it;

import org.apache.tools.ant.DirectoryScanner;
import org.custommonkey.xmlunit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@RunWith(Parameterized.class)
public abstract class Bdd2OctaneITCase {
    List<String> diffList = new ArrayList<>();

    @Parameterized.Parameter
    public Framework framework;

    @Parameterized.Parameter(1)
    public String reportFilesPath;

    @Parameterized.Parameter(2)
    public String featureFilesPath;

    @Parameterized.Parameter(3)
    public String resultFilesPath;

    @Parameterized.Parameter(4)
    public String standardResultPath;

    protected void validate(String outputResultPath, String standardResultPath) throws CompareXmlException, IOException, SAXException, ParserConfigurationException {
        File expectedFile = getStandardResult(standardResultPath);
        File actualFile = getOutputResult(outputResultPath);
        Diff diff = compareXmlFile(expectedFile, actualFile);
        if (diff.similar()) {
            System.out.println("-----------------------------Excellent! No difference found.-----------------------------");
            return;
        }
        validateCData(diff);
        validateNode(diff);
        if (diffList.size() > 0) {
            printDifferences();
        } else {
            System.out.println("-----------------------------Excellent! No difference found.(Ignore started time difference in <feature>. Ignore blank lines and extra spaces.)-----------------------------");
        }
    }

    private File getStandardResult(String standardResultPath) {
        File file = null;
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{standardResultPath});
        scanner.setBasedir(".");
        scanner.setCaseSensitive(false);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        for (String f : files) {
            if (f.contains(standardResultPath.substring(standardResultPath.lastIndexOf("/") + 1))) {
                file = new File(f);
                break;
            }
        }
        return file;
    }

    private File getOutputResult(String outputResultPath) {
        File file;
        if (outputResultPath != null) {
            file = new File(outputResultPath);
        } else {
            file = new File(framework.getValue() + "-result.xml");
        }
        return file;
    }

    private void validateNode(Diff diff) {
        DetailedDiff detailedDiff = new DetailedDiff(diff);
        List<?> nodeDifferences = detailedDiff.getAllDifferences();
        if (diffList.size() > 0) {
            diffList.add(0, "-----------------------------CData-different-----------------------------");
        }
        if (nodeDifferences.size() > 0) {
            diffList.add("-----------------------------Node-different-----------------------------");
            nodeDifferences.forEach(node -> diffList.add(node.toString()));
        }
    }

    private void compareText(String xpath, String expectedText, String actualText) throws IOException {
        LineNumberReader expectedReader = new LineNumberReader(new StringReader(expectedText));
        LineNumberReader actualReader = new LineNumberReader(new StringReader(actualText));
        String expectedLine;
        String actualLine;
        int lines = Math.max(countLines(expectedText), countLines(actualText));
        int expectedLineNumber = 0;
        int actualLineNumber = 0;
        while (Math.max(expectedLineNumber, actualLineNumber) < lines) {
            expectedLine = expectedReader.readLine();
            expectedLineNumber = expectedReader.getLineNumber();
            actualLine = actualReader.readLine();
            actualLineNumber = actualReader.getLineNumber();
            if (!(expectedLine == null)) {
                while (expectedLine.trim().equals("")) {
                    expectedLine = expectedReader.readLine();
                    expectedLineNumber = expectedReader.getLineNumber();
                }
            }
            if (!(actualLine == null)) {
                while (actualLine.trim().equals("")) {
                    actualLine = actualReader.readLine();
                    actualLineNumber = actualReader.getLineNumber();
                }
            }
            if (expectedLine == null) {
                diffList.add("[#cdata-section-different] " + "Expected line" + ": '" + expectedLine + "', but was line" + actualLineNumber + ": '" + actualLine + "', at " + xpath);
            } else if (actualLine == null) {
                diffList.add("[#cdata-section-different] " + "Expected line" + expectedLineNumber + ": '" + expectedLine + "', but was line" + ": '" + actualLine + "', at " + xpath);
            } else if (!(expectedLine.trim().equals(actualLine.trim()))) {
                diffList.add("[#cdata-section-different] " + "Expected line" + expectedLineNumber + ": '" + expectedLine + "', but was line" + actualLineNumber + ": '" + actualLine + "', at " + xpath);
            }
        }
    }

    private static int countLines(String str) {
        String[] lines = str.split("\r\n|\r|\n");
        return lines.length;
    }

    private Diff compareXmlFile(File expectedFile, File actualFile) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document expectedDoc = docBuilder.parse(expectedFile);
        Document actualDoc = docBuilder.parse(actualFile);
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
        return (new Diff(expectedDoc, actualDoc));
    }

    private void validateCData(Diff diff) {
        final HashSet<String> nodeDoesNotMatter = new HashSet<>();
        nodeDoesNotMatter.add("#cdata-section");
        nodeDoesNotMatter.add("started");
        nodeDoesNotMatter.add("path");
        diff.overrideDifferenceListener(new DifferenceListener() {
            @Override
            public int differenceFound(Difference difference) {
                String nodeName;
                if (difference.toString().contains("child node 'null'")) {
                    nodeName = difference.getTestNodeDetail().getNode().getNodeName();
                } else {
                    nodeName = difference.getControlNodeDetail().getNode().getNodeName();
                }
                if (nodeDoesNotMatter.contains(nodeName)) {
                    try {
                        String controlNodeTextContent = difference.getControlNodeDetail().getNode().getTextContent();
                        String testNodeTextContent = difference.getTestNodeDetail().getNode().getTextContent();
                        String controlNodeXpath = difference.getControlNodeDetail().getXpathLocation();
                        if (!nodeName.equals("started")) {
                            if (nodeName.equals("path")) {
                                String[] controlNodeTextContentArray = controlNodeTextContent.split("\\\\");
                                String[] testNodeTextContentArray = testNodeTextContent.split("/");
                                controlNodeTextContent = Arrays.toString(controlNodeTextContentArray);
                                testNodeTextContent = Arrays.toString(testNodeTextContentArray);
                            }
                            compareText(controlNodeXpath, controlNodeTextContent, testNodeTextContent);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                }
                return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
            }

            @Override
            public void skippedComparison(Node node, Node node1) {

            }
        });
    }

    private void printDifferences() throws CompareXmlException {
        diffList.forEach(System.err::println);
        throw new CompareXmlException("-----------------------------Differences found.-----------------------------");
    }

    enum Framework {
        CUCUMBER_JVM("cucumber-jvm"),
        CUCUMBER_JS("cucumber-js"),
        CUCUMBER_RUBY("cucumber-ruby"),
        PYTHON_RADISH("python-radish"),
        PYTHON_BEHAVE("python-behave"),
        PHP_BEHAT("php-behat");

        String value;

        Framework(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }
}

class CompareXmlException extends Exception {
    public CompareXmlException(String message) {
        super(message);
    }
}
