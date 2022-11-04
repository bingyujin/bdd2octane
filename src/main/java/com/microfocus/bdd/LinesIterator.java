/*
 * © Copyright [2021] Micro Focus or one of its affiliates.
 * Licensed under Apache License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.microfocus.bdd;

import java.util.Iterator;

class LinesIterator implements Iterator<String> {
    private String string;

    private int index = 0;

    public LinesIterator(String string) {
        this.string = string;
    }

    @Override
    public boolean hasNext() {
        return index < string.length();
    }

    @Override
    public String next() {
        int endIndexOfLine = string.indexOf('\n', index);
        String nextLine = string.substring(index, endIndexOfLine).trim();
        index = endIndexOfLine + 1;
        return nextLine;
    }
}
