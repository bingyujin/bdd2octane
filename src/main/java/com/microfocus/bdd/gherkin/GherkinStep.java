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
import io.cucumber.messages.types.DataTable;
import io.cucumber.messages.types.DocString;
import io.cucumber.messages.types.Step;
import io.cucumber.messages.types.TableCell;

import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GherkinStep  {
    private String flowContext;
    private GherkinDialect dialect;
    private Step step;


    public GherkinStep(Step step, String flowContext, GherkinDialect dialect) {
        this.step = step;
        this.flowContext = flowContext;
        this.dialect = dialect;
    }

    public String getName() {
        return step.getText();
    }

    public String getFlowContext() {
        return flowContext;
    }

    public String getKeyword() {
        return step.getKeyword().trim();
    }

    public Optional<String> getFlatDataTable() {
        List<String> rowLines = new ArrayList<>();
        DataTable dataTable = step.getDataTable();
        if (dataTable == null) {
            return Optional.empty();
        }
        dataTable.getRows().forEach(row -> {
            StringBuilder rowLine = new StringBuilder();
            List<TableCell> cells = row.getCells();
            for (int i = 0; i < cells.size(); i++) {
                rowLine.append(GherkinMultiLingualService.TABLE_INDENTATION + "|" + GherkinMultiLingualService.TABLE_INDENTATION)
                        .append(cells.get(i).getValue());
            }
            rowLine.append(GherkinMultiLingualService.TABLE_INDENTATION + "|");
            rowLines.add(rowLine.toString());
        });
        return Optional.of(String.join("\n", rowLines));
    }

    public String getStepTypeDefinition() {
        return GherkinMultiLingualService.getTypeDefinition(dialect, getFlowContext());
    }

    public Optional<String> getDocString() {
        DocString docString = step.getDocString();
        if (null == docString) {
            return Optional.empty();
        }
        return Optional.of(docString.getDelimiter() + '\n' + docString.getContent() + '\n' + docString.getDelimiter());
    }
}
