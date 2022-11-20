package nh.demo.graphql.orderservice.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Profile("with-order-producer")
@Component
public class OrderProducer {
  private static final Logger log = LoggerFactory.getLogger(OrderProducer.class);
  private final OrderService orderService;

  public OrderProducer(OrderService orderService) {
    this.orderService = orderService;
  }

  @Scheduled(fixedDelay = 5000)
  public void addOrder() {
    final var now = LocalDateTime.now();
    final var dummyPrefix = String.format("%02d:%02d:%02d", now.getHour(), now.getMinute(), now.getSecond());
    var items = List.of(
      new OrderService.NewOrderItem("p-1" + dummyPrefix, 1, new BigDecimal("1.0")),
      new OrderService.NewOrderItem("p-2" + dummyPrefix, 2, new BigDecimal("1.1")),
      new OrderService.NewOrderItem("p-3" + dummyPrefix, 3, new BigDecimal("1.2"))
    );
    orderService.createOrder(1, 1, items);
  }


}
