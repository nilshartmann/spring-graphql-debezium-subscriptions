package nh.demo.graphql.orderservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.converter.JsonMessageConverter;

@Configuration
@Profile("!kafka-reactive")
public class KafkaClassicConfig {

  @Bean
  public KafkaListenerContainerFactory<?> kafkaJsonListenerContainerFactory(ConsumerFactory<String, String> f) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
      new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(f);
    factory.setMessageConverter(new JsonMessageConverter());
    return factory;
  }

}
