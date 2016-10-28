package strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Zihao Chen
 */
public class Simple {
    private static final Logger LOGGER = LoggerFactory.getLogger(Simple.class);

    public Simple() {
        LOGGER.info("A simple strategy");
    }
}
