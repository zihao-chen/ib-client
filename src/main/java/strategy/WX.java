package strategy;

import com.ib.controller.ApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Zihao Chen
 */
public class WX extends AbstractStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(WX.class);

    public WX() {
        LOGGER.info("WX");
    }

    @Override
    public void apply(ApiController controller, String account) {
        //Do nothing
    }
}
