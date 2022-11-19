package nh.demo.graphql.orderservice.graphql;

public record CustomerChangeEventMsg(long id, String name, String address, String __op) {
  boolean isCreateEvent() {
    return "c".equals(this.__op);
  }
}
