/*
 * © Copyright [2021] Micro Focus or one of its affiliates.
 * Licensed under Apache License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.microfocus.bdd;

import com.microfocus.bdd.util.FileUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;

@Mojo(name = "run", requiresProject = false)
public class MavenPlugin extends AbstractMojo {
    @Parameter(property = "reportFiles")
    String reportFilesPath;
    @Parameter(property = "featureFiles")
    String featureFilesPath;
    @Parameter(property = "framework")
    String framework;
    @Parameter(property = "resultFile")
    String resultFilePath;

    @Override
    public void execute() throws ParameterMissingException {
        validateParameter(reportFilesPath, "-DreportFiles");
        validateParameter(featureFilesPath, "-DfeatureFiles");
        validateParameter(framework, "-Dframework");
        validResultFilePathParameter(resultFilePath, "-DresultFile");
        List<String> reportFiles = FilesLocator.getReportFiles(reportFilesPath);
        FileUtil.printFiles(reportFiles, "xml", reportFilesPath);
        List<String> featureFiles = FilesLocator.getFeatureFiles(featureFilesPath);
        FileUtil.printFiles(featureFiles, "feature", featureFilesPath);
        try {
            new Bdd2Octane(reportFiles, featureFiles, resultFilePath, framework).run();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void validateParameter(String paramValue, String paraName) throws ParameterMissingException {
        if (paramValue == null) {
            throw new ParameterMissingException("Parameter " + paraName + " is missing.");
        }
    }

    private static void validResultFilePathParameter(String paramValue, String paramName) throws ParameterMissingException {
        if (paramValue != null && !FileUtil.isPathValid(paramValue)) {
            System.err.println("Parameter " + paramName + " is invalid");
            throw new ParameterMissingException("Parameter " + paramName + " is invalid.");
        }
    }

    public static class ParameterMissingException extends MojoExecutionException {
        private static final String parameterErrorIndicate = "Required parameters:\n" +
                "-DreportFiles=<pattern>\n" +
                "-DfeatureFiles=<pattern>\n" +
                "-Dframework=<bdd framework>\n" +
                "All optional parameters:\n" +
                "-DresultFile=<pattern>\n";

        public ParameterMissingException(String message) {
            super(message + '\n' + parameterErrorIndicate);
        }
    }

}
