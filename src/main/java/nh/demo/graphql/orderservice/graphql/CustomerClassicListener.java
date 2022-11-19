package nh.demo.graphql.orderservice.graphql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@Profile("!kafka-reactive")
public class CustomerClassicListener implements CustomerListener {

  private static final Logger log = LoggerFactory.getLogger(CustomerClassicListener.class);

  private final Sinks.Many<CustomerChangeEventMsg> sink;

  public CustomerClassicListener() {
    this.sink = Sinks.many().multicast().directBestEffort();

    log.info("*** Using CLASSIC Kafka Listener");
  }

  @KafkaListener(topics = "${order-service.topics.customer}",
    containerFactory = "kafkaJsonListenerContainerFactory")
  public void newCustomerEventReceived(@Payload CustomerChangeEventMsg msg) {
    if (msg.isCreateEvent()) {
      this.sink.emitNext(msg,
        (signalType, emitResult) -> (emitResult == Sinks.EmitResult.FAIL_NON_SERIALIZED));
    }
  }

  @Override
  public Flux<CustomerChangeEventMsg> getPublisher() {
    return this.sink.asFlux();
  }
}
