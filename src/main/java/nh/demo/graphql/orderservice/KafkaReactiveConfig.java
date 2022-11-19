package nh.demo.graphql.orderservice;

import nh.demo.graphql.orderservice.graphql.CustomerChangeEventMsg;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverPartition;

import java.util.Collections;
import java.util.Map;

@Configuration
@Profile("kafka-reactive")
public class KafkaReactiveConfig {

  @Bean("kafkaCustomerReceiver")
  public KafkaReceiver<String, CustomerChangeEventMsg> kafkaCustomerReceiver(KafkaProperties properties,
                                                                             @Value("${order-service.topics.customer}") String topic
                                     ) {
    Map<String, Object> configProperties = properties.buildConsumerProperties();
//    configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
//    configProperties.put("spring.json.use.type.headers", "false");
//    configProperties.put("spring.json.value.default.type", CustomerChangeEventMsg.class.getName());
    ReceiverOptions<String, CustomerChangeEventMsg> receiverOptions =
      ReceiverOptions.<String, CustomerChangeEventMsg>create(configProperties)
        .withValueDeserializer(new JsonDeserializer<>(CustomerChangeEventMsg.class))
        // we're not interessted in Kafka Messages that have been added to the topic
        // while the server is not running
        // if a (GraphQL) Client subscribes, it only should receive UPCOMING events
        .addAssignListener(partitions -> partitions.forEach(ReceiverPartition::seekToEnd))
        .subscription(Collections.singleton(topic));

    KafkaReceiver<String, CustomerChangeEventMsg> receiver = KafkaReceiver.create(receiverOptions);
    return receiver;
  }
}
