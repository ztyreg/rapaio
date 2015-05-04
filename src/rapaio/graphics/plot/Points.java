/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
 *    Copyright 2014 Aurelian Tutuianu
 *    Copyright 2015 Aurelian Tutuianu
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

package rapaio.graphics.plot;

import rapaio.core.RandomSource;
import rapaio.data.Var;
import rapaio.graphics.base.Range;
import rapaio.graphics.opt.GOpt;
import rapaio.graphics.opt.PchPalette;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author tutuianu
 */
@Deprecated
public class Points extends PlotComponent {

    private static final long serialVersionUID = -4766079423843859315L;

    private final Var x;
    private final Var y;

    public Points(Var x, Var y, GOpt... opts) {
        this.x = x;
        this.y = y;
        this.options.apply(opts);
    }

    @Override
    public void initialize(Plot parent) {
        super.initialize(parent);
        parent.xLab(x.name());
        parent.yLab(y.name());
    }

    @Override
    public Range buildRange() {
        if (x.rowCount() == 0) {
            return null;
        }
        Range range = new Range();
        for (int i = 0; i < Math.min(x.rowCount(), y.rowCount()); i++) {
            if (x.missing(i) || y.missing(i)) {
                continue;
            }
            range.union(x.value(i), y.value(i));
        }
        return range;
    }

    @Override
    public void paint(Graphics2D g2d) {

        int len = Math.min(x.rowCount(), y.rowCount());
        List<Integer> pos = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            pos.add(i);
        }
        Collections.shuffle(pos, RandomSource.getRandom());
        for (int j = 0; j < len; j++) {
            int i = pos.get(j);
            if (x.missing(i) || y.missing(i)) {
                continue;
            }
            g2d.setColor(options.getColor(i));
            g2d.setStroke(new BasicStroke(options.getLwd()));
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, options.getAlpha()));
            PchPalette.STANDARD.draw(g2d,
                    parent.xScale(x.value(i)),
                    parent.yScale(y.value(i)),
                    options.getSize(i), options.getPch(i));
        }
    }
}
