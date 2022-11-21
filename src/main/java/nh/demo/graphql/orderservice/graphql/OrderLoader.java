package nh.demo.graphql.orderservice.graphql;

import graphql.schema.DataFetchingFieldSelectionSet;
import nh.demo.graphql.orderservice.domain.Order;
import nh.demo.graphql.orderservice.domain.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class OrderLoader {

  private final OrderRepository orderRepository;

  public OrderLoader(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  private static final Logger log = LoggerFactory.getLogger( OrderLoader.class );

  @Transactional
  public Optional<Order> loadOrder(Integer id, DataFetchingFieldSelectionSet dataFetchingFieldSelectionSet) {
    // very ugly hack to avoid lazy initialization problems within
    // reactive/flux code when loading orders for subscription
    // better way would be to avoid JPA in this context at all
    return orderRepository.findById(id)
      .map((order) -> {
        if (dataFetchingFieldSelectionSet.contains("**/orderItems")) {
          log.info("Loading order items");
          order.getOrderItems();
        }
        if (dataFetchingFieldSelectionSet.contains("**/customer")) {
          log.info("Loading customer");
          order.getCustomer().getName();
        }
        return order;
      });
  }
}