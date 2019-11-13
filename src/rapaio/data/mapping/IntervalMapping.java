/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
 *    Copyright 2014 Aurelian Tutuianu
 *    Copyright 2015 Aurelian Tutuianu
 *    Copyright 2016 Aurelian Tutuianu
 *    Copyright 2017 Aurelian Tutuianu
 *    Copyright 2018 Aurelian Tutuianu
 *    Copyright 2019 Aurelian Tutuianu
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
 *
 */

package rapaio.data.mapping;

import rapaio.data.Mapping;
import rapaio.util.collection.IntArrays;
import rapaio.util.collection.IntIterator;

import java.util.NoSuchElementException;
import java.util.stream.IntStream;

/**
 * Created by <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a> on 6/27/18.
 */
public final class IntervalMapping implements Mapping {

    private static final long serialVersionUID = -7421133121383028265L;

    private final int start;
    private final int end;
    private boolean onList = false;
    private ArrayMapping listMapping;

    public IntervalMapping(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public int size() {
        if (onList)
            return listMapping.size();
        return end - start;
    }

    @Override
    public int get(int pos) {
        if (onList)
            return listMapping.get(pos);
        return pos + start;
    }

    @Override
    public void add(int row) {
        if (!onList) {
            onList = true;
            listMapping = new ArrayMapping(start, end);
        }
        listMapping.add(row);
    }

    @Override
    public void addAll(IntIterator rows) {
        if (!onList) {
            onList = true;
            listMapping = new ArrayMapping(start, end);
        }
        listMapping.addAll(rows);
    }

    @Override
    public void remove(int pos) {
        if (!onList) {
            onList = true;
            listMapping = new ArrayMapping(start, end);
        }
        listMapping.remove(pos);
    }

    @Override
    public void removeAll(IntIterator positions) {
        if (!onList) {
            onList = true;
            listMapping = new ArrayMapping(start, end);
        }
        listMapping.removeAll(positions);
    }

    @Override
    public void clear() {
        if (!onList) {
            onList = true;
            listMapping = new ArrayMapping(0, 0);
        }
        listMapping.clear();
    }

    @Override
    public IntIterator iterator() {
        return onList ? listMapping.iterator() : new MyIntListIterator(start, end);
    }

    @Override
    public IntIterator iterator(int start, int end) {
        return onList ? listMapping.iterator(start, end) : new MyIntListIterator(this.start + start, this.start + end);
    }

    @Override
    public int[] elements() {
        return IntArrays.newSeq(start, end);
    }

    @Override
    public void shuffle() {
        onList = true;
        listMapping = new ArrayMapping(start, end);
        listMapping.shuffle();
    }

    @Override
    public IntStream stream() {
        return onList ? listMapping.stream() : IntStream.range(start, end);
    }
}

class MyIntListIterator implements IntIterator {
    private final int start;
    private final int end;
    private int s;

    public MyIntListIterator(int start, int end) {
        this.start = start;
        this.end = end;
        s = start;
    }

    @Override
    public boolean hasNext() {
        return s < end;
    }

    @Override
    public int nextInt() {
        if (s >= end) {
            throw new NoSuchElementException();
        }
        return s++;
    }
}