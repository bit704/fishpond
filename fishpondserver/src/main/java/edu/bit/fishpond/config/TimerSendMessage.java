package edu.bit.fishpond.config;

import edu.bit.fishpond.server.WebSocketServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class TimerSendMessage {

    @Scheduled(fixedRate = 50 * 1000)
    private void configureTasks() {
        WebSocketServer.sendMessageToAll();
    }
}
