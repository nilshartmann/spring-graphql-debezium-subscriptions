package nh.demo.graphql.orderservice.graphql;

import nh.demo.graphql.orderservice.domain.Order;
import nh.demo.graphql.orderservice.graphql.kafka.OrderChangeEventMsg;
import reactor.core.publisher.Flux;

public interface OrderListener {

  public Flux<OrderChangeEventMsg> getPublisher();

}
