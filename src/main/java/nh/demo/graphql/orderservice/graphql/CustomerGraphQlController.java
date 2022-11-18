package nh.demo.graphql.orderservice.graphql;

import nh.demo.graphql.orderservice.domain.Customer;
import nh.demo.graphql.orderservice.domain.CustomerRepository;
import nh.demo.graphql.orderservice.domain.CustomerService;
import nh.demo.graphql.orderservice.domain.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class CustomerGraphQlController {

  private static final Logger log = LoggerFactory.getLogger(CustomerGraphQlController.class);

  private final CustomerRepository customerRepository;
  private final CustomerService customerService;

  public CustomerGraphQlController(CustomerRepository customerRepository, CustomerService customerService) {
    this.customerRepository = customerRepository;
    this.customerService = customerService;
  }


  @QueryMapping
  public List<Customer> customers() {
    return customerRepository.findAll();
  }

  interface AddCustomerResult {
  }

  record AddCustomerInput(String name, String address) {
  }

  record AddCustomerSuccessResult(Customer newCustomer) implements AddCustomerResult {
  }

  record AddCustomerFailedResult(String errorMessage) implements AddCustomerResult {
  }

  @MutationMapping
  public AddCustomerResult addCustomer(@Argument AddCustomerInput input) {
    try {
      var newCustomer = customerService.createCustomer(
        input.name(), input.address()
      );

      return new AddCustomerSuccessResult(newCustomer);
    } catch (Exception ex) {
      log.error("Could not create customer from input {}: {}", input, ex);
      return new AddCustomerFailedResult("Could not create order.");
    }
  }
}
