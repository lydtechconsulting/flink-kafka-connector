package demo.component;

import java.util.List;
import java.util.UUID;

import demo.event.DemoEvent;
import dev.lydtech.component.framework.client.kafka.KafkaClient;
import dev.lydtech.component.framework.extension.ComponentTestExtension;
import dev.lydtech.component.framework.mapper.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@Slf4j
@ExtendWith(ComponentTestExtension.class)
public class EndToEndCT {

    private static final String GROUP_ID = "EndToEndCT";

    private Consumer consumer;

    @BeforeEach
    public void setup() {
        consumer = KafkaClient.getInstance().initConsumer(GROUP_ID, "demo-outbound", 3L);
    }

    @AfterEach
    public void tearDown() {
        consumer.close();
    }

    /**
     * Send in multiple events and ensure an outbound event is emitted for each that has been processed and transformed
     * by the Flink application.
     */
    @Test
    public void testFlow() throws Exception {
        int totalMessages = 10;
        for (int i=0; i<totalMessages; i++) {
            String key = UUID.randomUUID().toString();
            KafkaClient.getInstance().sendMessage("demo-inbound", key, JsonMapper.writeToJson(DemoEvent.builder().name("John Smith").build()));
        }
        List<ConsumerRecord<String, String>> outboundEvents = KafkaClient.getInstance().consumeAndAssert("testFlow", consumer, totalMessages, 2);
        outboundEvents.stream().forEach(outboundEvent -> assertThat(outboundEvent.value(), containsString("JOHN SMITH")));
    }
}
