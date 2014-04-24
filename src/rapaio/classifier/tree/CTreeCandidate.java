/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package rapaio.classifier.tree;

import rapaio.data.stream.FSpot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Models a splitting rule candidate.
 *
 * @author <a href="mailto:padreati@yahoo.com>Aurelian Tutuianu</a>
 */
public class CTreeCandidate implements Comparable<CTreeCandidate> {

    private final double score;
    private final int sign;
    private List<String> groupNames = new ArrayList<>();
    private List<Predicate<FSpot>> groupPredicates = new ArrayList<>();

    public CTreeCandidate(double score, int sign) {
        this.score = score;
        this.sign = sign;
    }

    public void addGroup(String name, Predicate<FSpot> predicate) {
        if (groupNames.contains(name)) {
            throw new IllegalArgumentException("group name already defined");
        }
        groupNames.add(name);
        groupPredicates.add(predicate);
    }

    public List<String> getGroupNames() {
        return groupNames;
    }

    public List<Predicate<FSpot>> getGroupPredicates() {
        return groupPredicates;
    }

    @Override
    public int compareTo(CTreeCandidate o) {
        return new Double(score).compareTo(o.score) * sign;
    }
}