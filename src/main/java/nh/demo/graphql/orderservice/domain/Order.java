package nh.demo.graphql.orderservice.domain;


import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name="orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="id")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "order_id")
  private List<OrderItem> orderItems = new LinkedList<>();

  private Integer discount;

  public Order(Customer customer, Integer discount) {
    this.customer = customer;
    this.discount = discount;
  }

  protected Order() {}

  public Integer getId() {
    return id;
  }

  public Customer getCustomer() {
    return customer;
  }

  public Iterable<OrderItem> getOrderItems() {
    return Collections.unmodifiableList(orderItems);
  }

  public Integer getDiscount() {
    return discount;
  }

  public OrderItem addOrderItem(String product, int quantity, BigDecimal price) {
    OrderItem orderItem = new OrderItem(
      this, product, quantity, price
    );
    this.orderItems.add(orderItem);

    return orderItem;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Order order = (Order) o;

    return Objects.equals(id, order.id);
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}
