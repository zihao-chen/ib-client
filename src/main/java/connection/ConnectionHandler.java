package connection;

import com.google.common.collect.Lists;
import com.ib.controller.ApiConnection;
import com.ib.controller.ApiController;
import com.ib.controller.Formats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zihao Chen
 */
public class ConnectionHandler implements ApiController.IConnectionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionHandler.class);

    private final ApiConnection.ILogger inLogger = s -> LOGGER.debug("InLogger: {}", s);
    private final ApiConnection.ILogger outLogger = s -> LOGGER.debug("OutLogger: {}", s);
    private final ApiController controller = new ApiController(this, inLogger, outLogger);

    private final List<String> accounts = Lists.newArrayList();

    public ApiController getController() {
        return controller;
    }

    @Override
    public void connected() {
        show("Connected");

        controller.reqCurrentTime(time -> show("Server date/time is " + Formats.fmtDate(time * 1000)));

        /*
        controller.reqBulletins(true, (msgId, newsType, message, exchange) -> {
            final String str = String.format("Received bulletin:  type=%s  exchange=%s", newsType, exchange);
            show(str);
            show(message);
        });*/
    }

    public List<String> getAccounts() {
        return accounts;
    }

    @Override
    public void disconnected() {
        show("Disconnected");
        System.exit(0);
    }

    @Override
    public void accountList(ArrayList<String> accounts) {
        this.accounts.addAll(accounts);
        show(String.format("Received account list: %s", String.join(",", accounts)));
    }

    @Override
    public void error(Exception e) {
        LOGGER.error("Error: ", e);
    }

    @Override
    public void message(int id, int errorCode, String errorMsg) {
        show(id + ": " + errorCode + " - " + errorMsg);
    }

    @Override
    public void show(String s) {
        LOGGER.info(s);
    }
}
