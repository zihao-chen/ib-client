package guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.ib.controller.ApiController;
import connection.ConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Zihao Chen
 */
public class ControllerModule extends AbstractModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerModule.class);

    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    public ConnectionHandler provideConnectionHandler() {
        return new ConnectionHandler();
    }

    @Provides
    public ApiController provideApiController(ConnectionHandler connectionHandler,
                                              @Named("host.name") String hostName, @Named("port") int port) {
        final ApiController controller = connectionHandler.getController();
        controller.connect(hostName, port, 0, "");
        return controller;
    }

    @Provides
    public Integer provideInterval(@Named("interval") int interval) {
        return interval;
    }

    @Provides
    public String provideAccount(ConnectionHandler connectionHandler, @Named("account") String account) {
        final List<String> accounts = connectionHandler.getAccounts();
        final String tradingAccount;
        if (accounts.contains(account)) {
            tradingAccount = account;
        } else {
            tradingAccount = accounts.get(0);
        }
        return tradingAccount;
    }
}
