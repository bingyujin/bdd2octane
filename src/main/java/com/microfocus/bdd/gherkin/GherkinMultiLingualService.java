/*
 * © Copyright [2021] Micro Focus or one of its affiliates.
 * Licensed under Apache License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.microfocus.bdd.gherkin;

import io.cucumber.gherkin.GherkinDialect;
import io.cucumber.messages.types.FeatureChild;
import io.cucumber.messages.types.Scenario;
import io.cucumber.messages.types.Tag;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GherkinMultiLingualService {
    public static final String FEATURE = "Feature";
    public static final String SCENARIO = "Scenario";
    public static final String SCENARIO_OUTLINE = "Scenario Outline";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String CN_LANGUAGE = "zh-CN";
    public static final String DE_LANGUAGE = "de";
    static final String GIVEN = "Given";
    static final String WHEN = "When";
    static final String THEN = "Then";
    static final String STAR = "* ";

    public static final String TABLE_INDENTATION = " ";
    public static final String DESCRIPTION_TYPE_PROP = "type";
    public static final String DESCRIPTION_NAME_PROP = "name";
    public static final String DESCRIPTION_INDEX_PROP = "index";
    public static final String DESCRIPTION_INDEX_MAJOR_PROP = "major";
    public static final String DESCRIPTION_INDEX_MINOR_PROP = "minor";
    public static final String DESCRIPTION_CONTENT_PROP = "description";

    public static boolean hasBackground(FeatureChild child) {
        return child.getBackground() != null;
    }

    public static boolean hasScenario(FeatureChild child) {
        return child.getScenario() != null;
    }

    public static boolean isOutlineScenario(Scenario scenario) {
        return !scenario.getExamples().isEmpty();
    }

    public static boolean isAndStep(GherkinDialect gherkinDialect, String keyword) {
        return gherkinDialect.getAndKeywords().contains(keyword);
    }

    public static boolean isButStep(GherkinDialect gherkinDialect, String keyword) {
        return gherkinDialect.getButKeywords().contains(keyword);
    }

    public static boolean isThenStep(GherkinDialect gherkinDialect, String keyword) {
        return gherkinDialect.getThenKeywords().contains(keyword);
    }

    public static Set<String> getTagNames(List<Tag> tags) {
        return tags.stream().map(tag -> tag.getName().replaceFirst("@", "")).collect(Collectors.toSet());
    }

    public static String getTypeDefinition(GherkinDialect gherkinDialect, String keyword) {
        if (gherkinDialect.getFeatureKeywords().contains(keyword)) {
            return GherkinMultiLingualService.FEATURE;
        }

        if (gherkinDialect.getScenarioKeywords().contains(keyword)) {
            return GherkinMultiLingualService.SCENARIO;
        }

        if (gherkinDialect.getScenarioOutlineKeywords().contains(keyword)) {
            return GherkinMultiLingualService.SCENARIO_OUTLINE;
        }

        if (gherkinDialect.getGivenKeywords().contains(keyword)) {
            return GherkinMultiLingualService.GIVEN;
        }

        if (gherkinDialect.getWhenKeywords().contains(keyword)) {
            return GherkinMultiLingualService.WHEN;
        }

        if (gherkinDialect.getThenKeywords().contains(keyword)) {
            return GherkinMultiLingualService.THEN;
        }

        throw new RuntimeException("Gherkin keyword type " + keyword + " not recognized here, dialect " + gherkinDialect.getName());
    }
}
