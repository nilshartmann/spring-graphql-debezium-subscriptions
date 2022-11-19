package nh.demo.graphql.orderservice.graphql;

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
public class CustomerReactiveListener implements CustomerListener {
  private static final Logger log = LoggerFactory.getLogger(CustomerReactiveListener.class);

  private final KafkaReceiver<String, CustomerChangeEventMsg> customerReceiver;
  private final Sinks.Many<CustomerChangeEventMsg> sink;

  public CustomerReactiveListener(
    @Qualifier("kafkaCustomerReceiver")
    KafkaReceiver<String, CustomerChangeEventMsg> customerReceiver) {
    this.customerReceiver = customerReceiver;
    this.sink = Sinks.many().multicast().directBestEffort();

    log.info("*** Using REACTIVE Kafka Listener");
  }

  @Override
  public Flux<CustomerChangeEventMsg> getPublisher() {
    log.info(">>>>>>>>>>>>>> getPublisher {}", sink.currentSubscriberCount());

    return this.sink.asFlux().mergeWith(getSimpleFlux());
  }

  private Flux<CustomerChangeEventMsg> getSimpleFlux() {
    Flux<ReceiverRecord<String, CustomerChangeEventMsg>> kafkaFlux = customerReceiver.receive();
    return kafkaFlux.log().doOnNext(r -> {
        log.info(">>>>>>>>>>>>>> DO ON NEXT {}", r);
        log.info(">>>>>>>>>>>>>> SUBSCRIBER COUNT {}", sink.currentSubscriberCount());
        r.receiverOffset().acknowledge();
      })
      .map(ReceiverRecord::value)
      .doOnNext(m -> {
        sink.emitNext(m,
          (signalType, emitResult) -> (emitResult == Sinks.EmitResult.FAIL_NON_SERIALIZED));
      });
  }
}
