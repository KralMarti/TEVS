package at.fhburgenland.tevs.restfulservice.rmq;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class StatusReceiver {

    private FanoutExchange fanoutExchange;

    public StatusReceiver(FanoutExchange fanoutExchange) {
        this.fanoutExchange = fanoutExchange;
    }

    @PostConstruct
    private void init() {
        try {
            FanoutExchange.declareQueues();
            FanoutExchange.declareExchange();
            FanoutExchange.declareBindings();
            FanoutExchange.subscribeMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

}
