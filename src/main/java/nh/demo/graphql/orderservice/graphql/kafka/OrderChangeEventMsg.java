package nh.demo.graphql.orderservice.graphql.kafka;

public record OrderChangeEventMsg(int id, String __op) {
  boolean isCreateEvent() {
    return "c".equals(this.__op);
  }
}
