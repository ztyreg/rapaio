package rapaio.data.format;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Created by <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a> on 10/30/19.
 */
public interface InstantFormatter {

    String format(Instant value);

    InstantFormatter ISO = DateTimeFormatter.ISO_INSTANT::format;
}
