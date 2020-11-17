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

package rapaio.graphics.plot.artist;

import rapaio.graphics.Plotter;
import rapaio.graphics.opt.GOption;
import rapaio.graphics.opt.GOptionColor;
import rapaio.graphics.plot.Artist;
import rapaio.graphics.plot.Axis;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Artist which draws a line of the form y = f(x) = a*x + b
 * There is a generic form of the line by calling {@link #ABLine(double, double, GOption...)}.
 * <p>
 * Also there is a simpler form for drawing horizontal or vertical lines.
 * The simpler form is called by using {@link #ABLine(boolean, double, GOption...)},
 * with the boolean parameter specifying if the line is horizontal or vertical.
 * <p>
 * Also there are two dedicated shortcuts in plotter {@link Plotter#hLine(double, GOption...)}
 * and {@link Plotter#vLine(double, GOption...)} which can be used to have the shortest code.
 * <p>
 * The default color is {@link Color#LIGHT_GRAY}, making this useful for drawing guides.
 *
 * @author <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a>
 */
public class ABLine extends Artist {

    private static final long serialVersionUID = 8980967297314815554L;
    private final double a;
    private final double b;
    private final boolean h;
    private final boolean v;

    public ABLine(boolean horiz, double a, GOption<?>... opts) {
        this.a = a;
        this.b = a;
        this.h = horiz;
        this.v = !horiz;
        this.options.setColor(new GOptionColor(new Color[]{Color.LIGHT_GRAY}));
        this.options.bind(opts);
    }

    public ABLine(double a, double b, GOption<?>... opts) {
        this.a = a;
        this.b = b;
        this.h = false;
        this.v = false;
        this.options.setColor(new GOptionColor(new Color[]{Color.LIGHT_GRAY}));
        this.options.bind(opts);
    }

    @Override
    public Axis newXAxis() {
        return Axis.numeric(plot);
    }

    @Override
    public Axis newYAxis() {
        return Axis.numeric(plot);
    }

    @Override
    public void updateDataRange() {
        if (h) {
            union(Double.NaN, a);
        }
        if (v) {
            union(a, Double.NaN);
        }
    }

    @Override
    public void paint(Graphics2D g2d) {
        g2d.setColor(options.getColor(0));

        double x1, x2, y1, y2;
        if (!h && !v) {
            double xx = plot.xAxis().min();
            double yy = a * xx + b;
            if (contains(xx, yy)) {
                x1 = xScale(xx);
                y1 = yScale(yy);
            } else {
                y1 = yScale(plot.xAxis().min());
                x1 = xScale((plot.yAxis().min() - b) / a);
            }

            xx = plot.xAxis().max();
            yy = a * xx + b;
            if (contains(xx, yy)) {
                x2 = xScale(xx);
                y2 = yScale(yy);
            } else {
                y2 = yScale(plot.yAxis().max());
                x2 = xScale((plot.yAxis().max() - b) / a);
            }
        } else {
            if (h) {
                x1 = xScale(plot.xAxis().min());
                y1 = yScale(a);
                x2 = xScale(plot.xAxis().max());
                y2 = yScale(a);
            } else {
                x1 = xScale(a);
                y1 = yScale(plot.yAxis().min());
                x2 = xScale(a);
                y2 = yScale(plot.yAxis().max());
            }
        }
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(options.getLwd()));
        g2d.draw(new Line2D.Double(x1, y1, x2, y2));
        g2d.setStroke(oldStroke);
    }
}
