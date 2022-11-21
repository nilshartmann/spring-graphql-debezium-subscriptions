package nh.demo.graphql.orderservice.graphql.kafka;

import nh.demo.graphql.orderservice.domain.Order;
import nh.demo.graphql.orderservice.graphql.CustomerListener;
import nh.demo.graphql.orderservice.graphql.OrderListener;
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
public class OrdersClassicListener implements OrderListener {

  private static final Logger log = LoggerFactory.getLogger(OrdersClassicListener.class);

  private final Sinks.Many<OrderChangeEventMsg> sink;

  public OrdersClassicListener() {
    this.sink = Sinks.many().multicast().directBestEffort();

    log.info("*** Using CLASSIC Kafka Listener for Orders");
  }

  @KafkaListener(topics = "${order-service.topics.orders}",
    containerFactory = "kafkaJsonListenerContainerFactory")
  public void newCustomerEventReceived(@Payload OrderChangeEventMsg msg) {
    if (msg.isCreateEvent()) {
      this.sink.emitNext(msg,
        (signalType, emitResult) -> (emitResult == Sinks.EmitResult.FAIL_NON_SERIALIZED));
    }
  }

  @Override
  public Flux<OrderChangeEventMsg> getPublisher() {
    return this.sink.asFlux();
  }
}
