/*
 * © Copyright [2021] Micro Focus or one of its affiliates.
 * Licensed under Apache License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.microfocus.bdd.ut;

import com.microfocus.bdd.FilesLocator;
import org.junit.Assert;
import org.junit.Test;
import java.io.File;
import java.util.List;

public class FilesLocatorTest {

    @Test(expected = RuntimeException.class)
    public void validateRelativePathTest() {
        final String absolutePath = new File("").getAbsolutePath();
        FilesLocator.getFeatureFiles(absolutePath);
    }

    @Test(expected = RuntimeException.class)
    public void validateFileExistenceFailTest() {
        final String path = "src/test";
        FilesLocator.getFeatureFiles(path);
    }

    @Test
    public void validateFileExistencePassTest() {
        final String path = "src/test/resources/standardOctaneGherkinReport/";
        List<String> reports = FilesLocator.getReportFiles(path);
        Assert.assertEquals(4, reports.size());
    }

    @Test(expected = RuntimeException.class)
    public void matchPatternFailTest() {
        final String pattern = "**/*dfgui*kl?sha?bi?ertu*.xml";
        FilesLocator.getReportFiles(pattern);
    }

    @Test
    public void matchPatternPassTest() {
        final String pattern = "src/**/*junit*.xml";
        List<String> reports = FilesLocator.getReportFiles(pattern);
        Assert.assertEquals(16, reports.size());
    }
}
