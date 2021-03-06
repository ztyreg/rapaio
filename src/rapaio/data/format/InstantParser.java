package rapaio.data.format;

import java.time.Instant;

/**
 * Created by <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a> on 10/30/19.
 */
public interface InstantParser {

    Instant parse(String input);

    InstantParser ISO = Instant::parse;
}
