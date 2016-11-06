package strategy;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ib.client.Contract;
import com.ib.client.OrderState;
import com.ib.client.OrderStatus;
import com.ib.client.Types;
import com.ib.controller.AccountSummaryTag;
import com.ib.controller.ApiController;
import com.ib.controller.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * @author Zihao Chen
 */
public abstract class AbstractStrategy implements Strategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStrategy.class);

    protected final Set<String> symbols = Sets.newHashSet();
    protected final Map<String, Position> contractDetails = Maps.newHashMap();

    protected boolean completed = false;
    protected double margin;

    public abstract void apply(ApiController controller, String account);

    protected Contract createContract(Contract originalContract) {
        final Contract contract = new Contract();

        contract.localSymbol(originalContract.symbol());
        contract.secType(Types.SecType.STK);
        contract.secIdType(originalContract.secIdType());
        contract.exchange("Smart");
        contract.currency(originalContract.currency());
        return contract;
    }

    protected class AccountHandler implements ApiController.IAccountHandler {

        @Override
        public void accountValue(String s, String s1, String s2, String s3) {
            //if (s1.toLowerCase().contains("margin")) {
            if (s1.equalsIgnoreCase("LookAheadMaintMarginReq")) {
                LOGGER.info("Account Value: {}, {}, {}, {}", s, s1, s2, s3);
                margin = Double.valueOf(s2);
            }
        }

        @Override
        public void accountTime(String s) {
            //Do Nothing
        }

        @Override
        public void accountDownloadEnd(String s) {
            completed = true;
        }

        @Override
        public void updatePortfolio(Position position) {
            final Contract contract = position.contract();
            final String symbol = contract.symbol();
            symbols.add(symbol);
            contractDetails.put(symbol, position);
            LOGGER.info("Contract {} {}: Position: {}, Market Price: {}, Market Value: {}, PnL(Unrealised): {}",
                    contract.localSymbol(), contract.exchange(), position.position(),
                    position.marketPrice(), position.marketValue(), position.unrealPnl());
        }

    }

    protected class AccountSummaryHandler implements ApiController.IAccountSummaryHandler {

        @Override
        public void accountSummary(String s, AccountSummaryTag tag, String s1, String s2) {
            LOGGER.info("Account: {}, {}, {} {}", s, tag, s1, s2);
        }

        @Override
        public void accountSummaryEnd() {
            completed = true;
        }

    }

    protected class PositionHandler implements ApiController.IPositionHandler {

        @Override
        public void position(String s, Contract contract, int i, double v) {
            LOGGER.info("{}. {}, {}. {}", s, contract, i, v);
        }

        @Override
        public void positionEnd() {
            completed = true;
        }

    }

    protected class OrderHandler implements ApiController.IOrderHandler {

        @Override
        public void orderState(OrderState orderState) {
            //TODO
            //LOGGER.info("orderState: {}", orderState);
        }

        @Override
        public void orderStatus(OrderStatus orderStatus, int i, int i1, double v, long l, int i2, double v1, int i3, String s) {
            //TODO
            //LOGGER.info("orderStatus: {}, {}, {}. {}, {}, {}, {}, {}", i, i1, v, l, i2, v1, i3, s);
        }

        @Override
        public void handle(int i, String s) {
            // LOGGER.info("handle: {}, {}", i, s);
        }

    }
}
