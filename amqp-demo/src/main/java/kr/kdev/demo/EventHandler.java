package kr.kdev.demo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class EventHandler {
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = "stream.event"),
                    exchange = @Exchange(value = "basic-event"),
                    key = "event.publish")
    })
    public void consume(Message message) {
        log.info("{}", new String(message.getBody(), StandardCharsets.UTF_8));
    }

    public void publish(String payload) {
        Message message = MessageBuilder.withBody(payload.getBytes(StandardCharsets.UTF_8)).build();
        rabbitTemplate.convertAndSend("basic-event", "event.publish", message);
    }

    @Scheduled(cron = "* * * * * ?")
    public void run() {
        publish("%s::%s".formatted(
                UUID.randomUUID().toString(),
                RandomStringUtils.random(8, true, false)));
    }
}
