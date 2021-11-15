/*
 * © Copyright [2021] Micro Focus or one of its affiliates.
 * Licensed under Apache License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.microfocus.bdd.api;

public enum Status {
    PASSED("passed"),
    FAILED("failed"),
    SKIPPED("skipped");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    @Override
    public String toString(){
        return status;
    }
}
