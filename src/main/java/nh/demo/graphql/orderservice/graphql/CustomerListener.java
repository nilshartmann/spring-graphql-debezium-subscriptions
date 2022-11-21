package nh.demo.graphql.orderservice.graphql;

import nh.demo.graphql.orderservice.graphql.kafka.CustomerChangeEventMsg;
import reactor.core.publisher.Flux;

public interface CustomerListener {
  Flux<CustomerChangeEventMsg> getPublisher();
}
