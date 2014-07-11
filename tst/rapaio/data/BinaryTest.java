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

package rapaio.data;

import static org.junit.Assert.*;

import org.junit.Test;
import rapaio.core.stat.Mean;
import rapaio.data.stream.VSpot;
import rapaio.ws.Summary;

/**
 * @author <a href="mailto:padreati@yahoo.com>Aurelian Tutuianu</a>
 */
public class BinaryTest {

    @Test
    public void testEmpty() {
        Binary b = Binary.newEmpty();
        b.addBinary(true);
        b.addBinary(true);
        b.addBinary(false);
        b.addMissing();
        b.addMissing();
        b.addBinary(true);

        assertEquals(1, b.stream().complete().filter(s -> !s.binary()).count());
        assertEquals(3, b.stream().complete().filter(VSpot::binary).count());
        assertEquals(2, b.stream().incomplete().count());

        assertEquals(10, Binary.newEmpty(10).stream().incomplete().count());
        assertEquals(0, Binary.newEmpty().stream().incomplete().count());
    }

    @Test
    public void testFill() {
        Binary b = Binary.newFill(10, false);

        assertEquals(0, b.stream().incomplete().count());
        assertEquals(10, b.stream().complete().filter(s -> !s.binary()).count());
        assertEquals(0, b.stream().complete().filter(VSpot::binary).count());

        b = Binary.newFill(10, true);

        assertEquals(0, b.stream().incomplete().count());
        assertEquals(0, b.stream().complete().filter(s -> !s.binary()).count());
        assertEquals(10, b.stream().complete().filter(VSpot::binary).count());
    }

    @Test
    public void testNumericStats() {
        Binary b = Binary.newCopyOf(1, 1, 0, 0, 1, 0, 1, 1);
        Summary.summary(b);
        assertEquals(0.625, new Mean(b).value(), 10e-10);
    }

    @Test
    public void testMissingValues() {
        Binary bin = Binary.newCopyOf(1, 0, 1, 0, -1, -1, 1, 0);
        assertEquals(8, bin.rowCount());
        assertEquals(true, bin.missing(4));
        assertEquals(false, bin.missing(7));

        bin = Binary.newEmpty();
        bin.addMissing();
        bin.addBinary(true);
        bin.setMissing(1);

        assertEquals(2, bin.rowCount());
        assertEquals(true, bin.missing(0));
        assertEquals(true, bin.missing(1));
    }

    @Test
    public void testBuilders() {
        Binary bin = Binary.newCopyOf(true, true, false, false);
        assertEquals(4, bin.rowCount());
        assertEquals(true, bin.binary(0));
        assertEquals(false, bin.binary(3));

        assertEquals(false, bin.isMapped());
        assertEquals(bin, bin.source());
        assertEquals(4, bin.mapping().size());
    }

    @Test
    public void testOther() {
        Binary bin = Binary.newEmpty();
        bin.addValue(1);
        bin.setValue(0, 0);
        bin.addIndex(1);
        bin.setIndex(1, 0);

        assertEquals(0, bin.value(0), 10e-10);
        assertEquals(0, bin.index(1));

        Binary copy = bin.solidCopy();
        assertEquals(false, copy.binary(0));
        assertEquals(false, copy.binary(1));
        assertEquals(2, copy.rowCount());

        copy.clear();
        assertEquals(0, copy.rowCount());
    }
}
