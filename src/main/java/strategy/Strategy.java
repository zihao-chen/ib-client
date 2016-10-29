package strategy;

import com.ib.controller.ApiController;

/**
 * @author Zihao Chen
 */
@FunctionalInterface
public interface Strategy {
    void apply(ApiController controller, String account);
}
