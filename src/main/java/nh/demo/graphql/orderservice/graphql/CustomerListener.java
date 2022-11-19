package nh.demo.graphql.orderservice.graphql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

@Component
public class CustomerListener {

  private static final Logger log = LoggerFactory.getLogger( CustomerListener.class );

  private final Sinks.Many<NewCustomerMsg> sink;

  public CustomerListener() {
    this.sink = Sinks.many()
      .replay().latest();
  }

  public record NewCustomerMsg(long id, String name, String address, String __op) {}

  @KafkaListener(topics = "order_app_smt.public.customers",
  containerFactory = "kafkaJsonListenerContainerFactory")
  public void newCustomerEventReceived(@Payload NewCustomerMsg o) {
  log.info("subscribers {}", this.sink.currentSubscriberCount());
    log.info("RECEIVED {}", o);
    if ("c".equals(o.__op)) {
      this.sink.emitNext(o, Sinks.EmitFailureHandler.FAIL_FAST);
    }
  }


  public Flux<NewCustomerMsg> getPublisher() {
    return this.sink.
      asFlux();
  }

}
