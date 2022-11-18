package nh.demo.graphql.orderservice.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

  private static final Logger log = LoggerFactory.getLogger(OrderService.class);

  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;

  public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository) {
    this.orderRepository = orderRepository;
    this.customerRepository = customerRepository;
  }

  public record NewOrderItem(
    String product, Integer quantity, BigDecimal price
  ) {  }

  @Transactional
  public Order createOrder(Integer customerId, Integer discount, List<NewOrderItem> orderItems) {
    log.info("Create new order for customer {} with discount {} and first order item {}",
      customerId, discount, orderItems);
    var customer = customerRepository.findById(customerId).orElseThrow();
    var newOrder = new Order(customer, discount);

    orderItems.forEach(item -> newOrder.addOrderItem(
      item.product(), item.quantity(), item.price())
    );

    return orderRepository.save(newOrder);
  }

  @Transactional
  public OrderItem createOrderItem(Integer orderId, String product, Integer quantity, BigDecimal price) {
    var order = orderRepository.findById(orderId).orElseThrow();

    var newOrderItem = order.addOrderItem(product, quantity, price);
    return newOrderItem;
  }
}
