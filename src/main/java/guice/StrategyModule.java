package guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import strategy.Simple;
import strategy.Strategy;
import strategy.WX;

/**
 * @author Zihao Chen
 */
public class StrategyModule extends AbstractModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(StrategyModule.class);

    @Override
    protected void configure() {
        
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
