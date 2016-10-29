package strategy;

import com.ib.client.Contract;
import com.ib.controller.AccountSummaryTag;
import com.ib.controller.ApiController;
import com.ib.controller.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static com.ib.controller.AccountSummaryTag.*;

/**
 * @author Zihao Chen
 */
public class Simple implements Strategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(Simple.class);

    private final ApiController.IAccountHandler accountHandler = new AccountHandler();
    private final ApiController.IAccountSummaryHandler accountSummaryHandler = new AccountSummaryHandler();
    private final ApiController.IPositionHandler positionHandler = new PositionHandler();

    private boolean completed = false;

    public Simple() {
        LOGGER.info("A simple strategy");
    }

    @Override
    public void apply(ApiController controller, String account) {
        //final ApiConnection client = controller.client();

        controller.reqAccountSummary("All",
                new AccountSummaryTag[]{AccountType, TotalCashValue, InitMarginReq, MaintMarginReq, AvailableFunds,},
                accountSummaryHandler);

        controller.reqPositions(positionHandler);

        controller.reqAccountUpdates(true, account, accountHandler);

        LOGGER.info("Do Something at {}: {}", account, Calendar.getInstance().getTime());

        while (completed) {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            } catch (InterruptedException e) {
                LOGGER.error("Error: ", e);
            }
        }

        controller.cancelAccountSummary(accountSummaryHandler);
        controller.cancelPositions(positionHandler);

        controller.reqAccountUpdates(false, account, accountHandler);

        //TODO: contract and order
    }

    private class AccountHandler implements ApiController.IAccountHandler {
        @Override
        public void accountValue(String s, String s1, String s2, String s3) {
            LOGGER.info("Account Value: {}, {}, {}, {}", s, s1, s2, s3);
        }

        @Override
        public void accountTime(String s) {
            //LOGGER.info("Account Time: {}", s);
        }

        @Override
        public void accountDownloadEnd(String s) {

        }

        @Override
        public void updatePortfolio(Position position) {
            LOGGER.info("Contract {}\nPosition: {}, Market Price: {}, Market Value: {}, PnL(Unrealised): {}",
                    position.contract(), position.position(), position.marketPrice(), position.marketValue(), position.unrealPnl());
        }
    }

    private class AccountSummaryHandler implements ApiController.IAccountSummaryHandler {
        @Override
        public void accountSummary(String s, AccountSummaryTag tag, String s1, String s2) {
            LOGGER.info("Account: {}, {}, {} {}", s, tag, s1, s2);
        }

        @Override
        public void accountSummaryEnd() {

        }
    }

    private class PositionHandler implements ApiController.IPositionHandler {
        @Override
        public void position(String s, Contract contract, int i, double v) {
            LOGGER.info("{}. {}, {}. {}", s, contract, i, v);
        }

        @Override
        public void positionEnd() {
            completed = true;
        }
    }
}

