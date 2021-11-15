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

class LinesBottomUpIterator implements Iterator<String> {
    private String string;
    private int lastIndex;

    public LinesBottomUpIterator(String string) {
        this.string = string;
        lastIndex = string.length();
    }

    @Override
    public boolean hasNext() {
        return lastIndex != -1;
    }

    @Override
    public String next() {
        int index = string.lastIndexOf('\n', lastIndex - 1);
        String lastLine = string.substring(index + 1, lastIndex).trim();
        lastIndex = index;
        return lastLine;
    }
}
