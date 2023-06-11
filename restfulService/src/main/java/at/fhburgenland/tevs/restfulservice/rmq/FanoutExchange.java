package at.fhburgenland.tevs.restfulservice.rmq;

import at.fhburgenland.tevs.restfulservice.StatusController;
import at.fhburgenland.tevs.restfulservice.models.RMQStatus;
import at.fhburgenland.tevs.restfulservice.models.RequestType;
import at.fhburgenland.tevs.restfulservice.models.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Component
public class FanoutExchange {

    private static final String QUEUE_NAME = "q.state" + UUID.randomUUID();

    public static void declareExchange() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();
        //Declare my-fanout-exchange
        channel.exchangeDeclare("exchange.status", BuiltinExchangeType.FANOUT, true);
        channel.close();
    }

    public static void declareQueues() throws IOException, TimeoutException {
        //Create a channel - do no't share the Channel instance
        Channel channel = ConnectionManager.getConnection().createChannel();
        //Create the Queues
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.close();
    }

    public static void declareBindings() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();
        //Create bindings - (queue, exchange, routingKey) - routingKey != null
        channel.queueBind(QUEUE_NAME, "exchange.status", "");
        channel.close();
    }

    public static void subscribeMessage() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();
        channel.basicConsume(QUEUE_NAME, true, ((consumerTag, message) -> {
            handleRequest(new String(message.getBody()));
            System.out.println(consumerTag);
            System.out.println(QUEUE_NAME + ": " + new String(message.getBody()));
        }), consumerTag -> {
            System.out.println(consumerTag);
        });
    }

    public static void publishMessage(RMQStatus status) throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(status);
        channel.basicPublish("exchange.status", "", null, message.getBytes());
        channel.close();
    }


    private static void handleRequest(String jsonRMQStatus) {
        RMQStatus rmqStatus = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            rmqStatus = objectMapper.readValue(jsonRMQStatus, RMQStatus.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (rmqStatus != null) {
            switch (rmqStatus.getRequestType()) {
                case POST -> {
                    if (rmqStatus.getStartupStatusMap() != null) {
                        if(StatusController.statusMap == null || StatusController.statusMap.isEmpty()) {
                            StatusController.statusMap = rmqStatus.getStartupStatusMap();
                        }
                    } else {
                        StatusController.statusMap.put(rmqStatus.getStatus().getUsername(), rmqStatus.getStatus());
                    }
                }
                case PUT -> StatusController.statusMap.put(rmqStatus.getStatus().getUsername(), rmqStatus.getStatus());
                case DELETE ->  {
                    if (StatusController.statusMap.containsKey(rmqStatus.getStatus().getUsername())) {
                        StatusController.statusMap.remove(rmqStatus.getStatus().getUsername());
                    }
                }
                case STARTUP_SYNC -> {
                    if (StatusController.statusMap != null && !StatusController.statusMap.isEmpty()) {
                        RMQStatus rmqStatusSync = new RMQStatus(null, RequestType.POST, StatusController.statusMap);
                        try {
                            publishMessage(rmqStatusSync);
                        } catch (IOException | TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


}
