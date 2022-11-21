package nh.demo.graphql.orderservice.graphql.kafka;

import nh.demo.graphql.orderservice.graphql.CustomerListener;
import nh.demo.graphql.orderservice.graphql.OrderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

@Component
@Profile("kafka-reactive")
public class OrdersReactiveListener implements OrderListener {
  private static final Logger log = LoggerFactory.getLogger(OrdersReactiveListener.class);

  private final KafkaReceiver<String, OrderChangeEventMsg> orderReceiver;
  private final Sinks.Many<OrderChangeEventMsg> sink;

  public OrdersReactiveListener(
    @Qualifier("kafkaOrderReceiver")
    KafkaReceiver<String, OrderChangeEventMsg> orderReceiver) {
    this.orderReceiver = orderReceiver;
    this.sink = Sinks.many().multicast().directBestEffort();

    log.info("*** Using REACTIVE Kafka Listener for Order Events");
  }

  @Override
  public Flux<OrderChangeEventMsg> getPublisher() {
    log.info(">>>>>>>>>>>>>> getPublisher {}", sink.currentSubscriberCount());

    return this.sink.asFlux().mergeWith(getSimpleFlux());
  }

  private Flux<OrderChangeEventMsg> getSimpleFlux() {
    Flux<ReceiverRecord<String, OrderChangeEventMsg>> kafkaFlux = orderReceiver.receive();
    return kafkaFlux.log().doOnNext(r -> {
        log.info(">>>>>>>>>>>>>> ORDER DO ON NEXT {}", r);
        log.info(">>>>>>>>>>>>>> ORDER SUBSCRIBER COUNT {}", sink.currentSubscriberCount());
        r.receiverOffset().acknowledge();
      })
      .map(ReceiverRecord::value)
      .doOnNext(m -> {
        sink.emitNext(m,
          (signalType, emitResult) -> (emitResult == Sinks.EmitResult.FAIL_NON_SERIALIZED));
      });
  }
}
