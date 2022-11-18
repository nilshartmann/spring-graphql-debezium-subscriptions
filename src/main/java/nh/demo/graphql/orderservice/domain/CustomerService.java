package nh.demo.graphql.orderservice.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

  private final CustomerRepository customerRepository;

  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public Customer createCustomer(String name, String address) {
    Customer customer = new Customer(name, address);
    customerRepository.save(customer);
    return customer;
  }
}
