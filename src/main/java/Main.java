import com.ib.controller.ApiController;
import connection.ConnectionHandler;
import strategy.Simple;
import strategy.Strategy;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Zihao Chen
 */

public class Main {

    //TODO: move into configuration
    public static final String LOCAL_HOST = "localhost";
    public static final int PORT = 4001; //7777;
    public static final String PREF_ACCOUNT = "";
    public static final Strategy strategy = new Simple();

    private final static ThreadFactory THREAD_FACTORY = r -> {
        final Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    };

    private static final ScheduledExecutorService SERVICE = Executors.newSingleThreadScheduledExecutor(THREAD_FACTORY);

    public static void main(String[] args) {
        //connect to TWS
        final ConnectionHandler connectionHandler = new ConnectionHandler();
        final ApiController controller = connectionHandler.getController();
        controller.connect(LOCAL_HOST, PORT, 0, "");

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(5));
        } catch (Exception e) {
            //TODO
        }

        final List<String> accounts = connectionHandler.getAccounts();
        final String tradingAccount;
        if (accounts.contains(PREF_ACCOUNT)) {
            tradingAccount = PREF_ACCOUNT;
        } else {
            tradingAccount = accounts.get(0);
        }

        SERVICE.scheduleAtFixedRate(() -> strategy.apply(controller, tradingAccount), 0, 30, TimeUnit.SECONDS);

        /*
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                controller.disconnect();
            }
        });
        */

        //TODO: clean this rubbish...
        while (true) {
            try {
                Thread.sleep(TimeUnit.MINUTES.toMillis(5));
            } catch (Exception e) {
                //TODO
            }
        }
    }
}
