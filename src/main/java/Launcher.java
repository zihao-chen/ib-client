import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ib.controller.ApiController;
import guice.ControllerModule;
import strategy.Strategy;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Zihao Chen
 */
public class Launcher {

    private ScheduledExecutorService service;

    private ApiController controller;

    public void start() {
        service = Executors.newScheduledThreadPool(1);

        final Injector injector = Guice.createInjector(new ControllerModule());
        controller = injector.getInstance(ApiController.class);

        final Strategy strategy = injector.getInstance(Strategy.class);
        final String tradingAccount = injector.getInstance(String.class);
        final int interval = injector.getInstance(Integer.class);

        service.scheduleAtFixedRate(() -> strategy.apply(controller, tradingAccount), 0, interval, TimeUnit.SECONDS);
    }

    public void stop() {
        if (service != null) {
            service.shutdown();
        }

        if (controller != null) {
            controller.disconnect();
        }

    }
}
