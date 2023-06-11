package at.fhburgenland.tevs.restfulservice;

import at.fhburgenland.tevs.restfulservice.models.RMQStatus;
import at.fhburgenland.tevs.restfulservice.models.RequestType;
import at.fhburgenland.tevs.restfulservice.models.Status;
import at.fhburgenland.tevs.restfulservice.rmq.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;


@RestController
@RequestMapping(value = "/status")
@CrossOrigin
public class StatusController {
    private final RabbitTemplate rabbitTemplate;
    private FanoutExchange fanoutExchange;

    public static Map<String, Status> statusMap = new HashMap<>();

    public StatusController(RabbitTemplate rabbitTemplate, FanoutExchange fanoutExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.fanoutExchange = fanoutExchange;
    }

    @PostMapping
    public ResponseEntity postStatus (@RequestBody Status status) {
        System.out.println(status.getUsername());
        System.out.println(status.getStatusText());
        try {
            RMQStatus rmqStatus = new RMQStatus(status, RequestType.POST);
            fanoutExchange.publishMessage(rmqStatus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("Status erfolgreich gesendet!");
    }

    @PutMapping
    public ResponseEntity putStatus (@RequestBody Status status) {
        System.out.println(status.getUsername());
        System.out.println(status.getStatusText());
        try {
            RMQStatus rmqStatus = new RMQStatus(status, RequestType.PUT);
            fanoutExchange.publishMessage(rmqStatus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("Status erfolgreich gesendet!");
    }

    @GetMapping
    public ResponseEntity<Map<String, Status>> getStatus() {
        return ResponseEntity.ok(statusMap);
    }

}
