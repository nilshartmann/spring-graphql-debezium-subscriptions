package nh.demo.graphql.orderservice.graphql;

import reactor.core.publisher.Flux;

public interface CustomerListener {
  Flux<CustomerChangeEventMsg> getPublisher();
}
