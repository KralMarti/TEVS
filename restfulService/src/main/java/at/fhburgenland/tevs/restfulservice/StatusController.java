package at.fhburgenland.tevs.restfulservice;

import at.fhburgenland.tevs.restfulservice.models.RMQStatus;
import at.fhburgenland.tevs.restfulservice.models.RequestType;
import at.fhburgenland.tevs.restfulservice.models.Status;
import at.fhburgenland.tevs.restfulservice.rmq.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;


@RestController
@RequestMapping(value = "/status")
@CrossOrigin
public class StatusController {
    public static Map<String, Status> statusMap = new HashMap<>();
    private final RabbitTemplate rabbitTemplate;
    private final FanoutExchange fanoutExchange;

    public StatusController(RabbitTemplate rabbitTemplate, FanoutExchange fanoutExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.fanoutExchange = fanoutExchange;
    }

    @GetMapping
    public ResponseEntity<Map<String, Status>> getStatus() {
        return ResponseEntity.ok(statusMap);
    }


    @PostMapping
    public ResponseEntity postStatus(@RequestBody Status status) {
        try {
            status.setTimeStamp(new Date());
            RMQStatus rmqStatus = new RMQStatus(status, RequestType.POST);
            FanoutExchange.publishMessage(rmqStatus);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("Status erfolgreich gesendet!");
    }

    @PutMapping
    public ResponseEntity putStatus(@RequestBody Status status) {
        try {
            status.setTimeStamp(new Date());
            RMQStatus rmqStatus = new RMQStatus(status, RequestType.PUT);
            FanoutExchange.publishMessage(rmqStatus);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("Status erfolgreich gesendet!");
    }

    @DeleteMapping("{username}")
    ResponseEntity deleteStatus(@PathVariable String username) {
        Status status = statusMap.get(username);
        if (status != null) {
            try {
                RMQStatus rmqStatus = new RMQStatus(status, RequestType.DELETE);
                FanoutExchange.publishMessage(rmqStatus);
                return ResponseEntity.ok("Status erfolgreich gelöscht!");
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().body("Es ist ein Fehler beim Löschen des Status aufgetreten!");
            }
        } else {
            return ResponseEntity.badRequest().body("Es existiert kein Status für diesen Usernamen");
        }
    }

}
