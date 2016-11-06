package guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.ib.controller.ApiController;
import connection.ConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import strategy.Simple;
import strategy.Strategy;
import strategy.WX;

import java.io.FileReader;
import java.util.List;
import java.util.Properties;

/**
 * @author Zihao Chen
 */
public class ControllerModule extends AbstractModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerModule.class);

    @Override
    protected void configure() {
        try {
            final String fileName = System.getProperty("config.properties");
            final Properties properties = new Properties();
            properties.load(new FileReader(fileName));
            Names.bindProperties(binder(), properties);
        } catch (Exception e) {
            LOGGER.error("Error when load config file", e);
        }
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

    @Provides
    public Strategy provideStrategy(@Named("strategy.name") String name,
                                    @Named("a.law") int aLaw,
                                    @Named("h.law") int hLaw,
                                    @Named("setp") double setp) {
        switch (name.toLowerCase()) {
            case "wx":
                final WX strategy = new WX();
                strategy.setALaw(aLaw);
                strategy.setHLaw(hLaw);
                strategy.setSetp(setp);
                return strategy;
            default:
                LOGGER.warn("Invalid strategy name provided, use default one");
                return new Simple();
        }
    }
}
