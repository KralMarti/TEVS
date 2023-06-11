package at.fhburgenland.tevs.restfulservice.rmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class ConnectionManager {

    private static Connection connection;

    @Value("${spring.rabbitmq.username}")
    private String rmqUser;

    @Value("${spring.rabbitmq.password}")
    private String rmqPassword;

    @Value("${spring.rabbitmq.host}")
    private String rmqHost;

    @Value("${spring.rabbitmq.port}")
    private String rmqPort;

    public static Connection getConnection() {
        return connection;
    }

    @PostConstruct
    private void init() {
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(rmqHost);
            connectionFactory.setPort(Integer.parseInt(rmqPort));
            connectionFactory.setUsername(rmqUser);
            connectionFactory.setPassword(rmqPassword);
            connection = connectionFactory.newConnection();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

}