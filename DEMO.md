# ...

- watch-topic-customer.sh
  - AddCustomer Mutation
  - CDC Event kommt
  - Zeigen: Insbesondere Payload
  - SQL ausführen, Customer anlegen
    -  INSERT INTO customers (name, address) VALUES('Heinz', 'Somewhere 123') RETURNING id;
  - CDC Event kommt
  - Exemplarisch:
    - UPDATE customers SET name = 'Susi' WHERE id = 1000;