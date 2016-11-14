package strategy;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderType;
import com.ib.client.Types;
import com.ib.controller.ApiController;
import com.ib.controller.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author Zihao Chen
 */
public class Simple extends AbstractStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(Simple.class);

    private final ApiController.IAccountHandler accountHandler = new AccountHandler();
    private final ApiController.IAccountSummaryHandler accountSummaryHandler = new AccountSummaryHandler();
    private final ApiController.IPositionHandler positionHandler = new PositionHandler();

    public Simple() {
        LOGGER.info("A simple strategy");
    }

    @Override
    public void apply(ApiController controller, String account) {
        //final ApiConnection client = controller.client();

        /*
        controller.reqAccountSummary("All",
                new AccountSummaryTag[]{AccountType, TotalCashValue, InitMarginReq, MaintMarginReq, AvailableFunds,},
                accountSummaryHandler);
        */


        if (!completed) {
            controller.reqAccountUpdates(true, account, accountHandler);
            //controller.reqPositions(positionHandler);
        }

        while (!completed) {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            } catch (InterruptedException e) {
                LOGGER.error("Error: ", e);
            }
        }

        LOGGER.info("Prepare for order");

        symbols.forEach(s -> {
            final Position position = contractDetails.get(s);
            if (Math.abs(position.position()) < 300) {
                final Contract originalContract = position.contract();
                LOGGER.info("{}, with Unrealised PnL of {}", originalContract.symbol(), position.unrealPnl());
                final Contract contract = createContract(originalContract);

                controller.reqContractDetails(contract, incomingContracts -> {
                    if (incomingContracts.size() == 1) {
                        final Order order = new Order();
                        order.account(account);
                        order.orderType(OrderType.MKT);
                        order.totalQuantity(100);
                        order.action(position.position() > 0 ? Types.Action.BUY : Types.Action.SELL);
                        //LOGGER.info("{}", incomingContracts.get(0));
                        controller.placeOrModifyOrder(incomingContracts.get(0).contract(), order, new OrderHandler());
                    }
                });

            }
        });

        LOGGER.info("Complete order");

        //controller.cancelPositions(positionHandler);
        controller.reqAccountUpdates(false, account, accountHandler);
        completed = false;

        //TODO: contract and order
    }

}

