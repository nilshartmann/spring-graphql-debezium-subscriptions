package nh.demo.graphql.orderservice.graphql;

import graphql.schema.DataFetchingFieldSelectionSet;
import nh.demo.graphql.orderservice.domain.CustomerRepository;
import nh.demo.graphql.orderservice.domain.Order;
import nh.demo.graphql.orderservice.domain.OrderRepository;
import nh.demo.graphql.orderservice.domain.OrderService;
import nh.demo.graphql.orderservice.graphql.kafka.OrderChangeEventMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class OrderGraphQlController {

  private static final Logger log = LoggerFactory.getLogger(OrderGraphQlController.class);

  private final CustomerRepository customerRepository;
  private final OrderRepository orderRepository;
  private final OrderService orderService;
  private final OrderListener orderListener;
  private final OrderLoader orderLoader;

  public OrderGraphQlController(CustomerRepository customerRepository, OrderRepository orderRepository, OrderService orderService, OrderListener orderListener, OrderLoader orderLoader) {
    this.customerRepository = customerRepository;
    this.orderRepository = orderRepository;
    this.orderService = orderService;
    this.orderListener = orderListener;
    this.orderLoader = orderLoader;
  }

  @QueryMapping
  public List<Order> orders() {
    return orderRepository.findAll();
  }

  @QueryMapping
  public Optional<Order> order(@Argument Integer id) {
    return orderRepository.findById(id);
  }

  record NewOrderItemInput(
    String product, int quantity, BigDecimal price
  ) {
  }


  record AddOrderInput(Integer customerId,
                       Integer discount,
                       List<NewOrderItemInput> orderItems) {
  }

  interface AddOrderResult {
  }

  record AddOrderSuccessResult(Order newOrder) implements AddOrderResult {
  }

  record AddOrderFailedResult(String errorMessage) implements AddOrderResult {
  }

  @MutationMapping
  public AddOrderResult addOrder(@Argument AddOrderInput input) {
    try {
      var newOrder = orderService.createOrder(
        input.customerId(),
        input.discount(),
        input.orderItems().
          stream().map(oi -> new OrderService.NewOrderItem(
            oi.product(), oi.quantity(), oi.price()
          )).toList()
      );

      return new AddOrderSuccessResult(newOrder);
    } catch (Exception ex) {
      log.error("Could not create order from input {}: {}", input, ex);
      return new AddOrderFailedResult("Could not create order.");
    }
  }

  record NewOrderEvent(Order newOrder) {
  }

  @SubscriptionMapping
  public Flux<NewOrderEvent> onNewOrder(DataFetchingFieldSelectionSet dataFetchingFieldSelectionSet) {
    return orderListener.getPublisher()
      .map(OrderChangeEventMsg::id)
      .publishOn(Schedulers.boundedElastic())
      .map(id -> orderLoader.loadOrder(id, dataFetchingFieldSelectionSet))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .map(NewOrderEvent::new);
  }



}
