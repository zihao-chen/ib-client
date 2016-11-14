package guice;

import com.google.common.collect.Maps;
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
    public Map<String, String> provideShareMapping(@Named("mapping") String path) {
        final Map<String, String> ahMap = Maps.newHashMap();

        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while ((line = br.readLine()) != null) {
                final String[] symbols = line.split(",");
                ahMap.put(symbols[2], symbols[1]);
            }
        } catch (IOException e) {
            LOGGER.error("无法读取A股H股对照", e);
        }
        return ahMap;
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
                                    @Named("setp") double setp,
                                    @Named("min.margin") double margin,
                                    Map<String, String> ahMap) {
        switch (name.toLowerCase()) {
            case "wx":
                final WX strategy = new WX();
                strategy.setALaw(aLaw);
                strategy.setHLaw(hLaw);
                strategy.setSetp(setp);
                strategy.setMinMargin(margin);
                strategy.setAhMap(ahMap);
                return strategy;
            default:
                LOGGER.warn("Invalid strategy name provided, use default one");
                return new Simple();
        }
    }
}
