# Example: Using Kafka and Debezium for GraphQL Subscriptions

This example uses [Debezium](https://debezium.io/) to create CDC events from a PostgreSQL database.
The events are published to [Apache Kafka](https://kafka.apache.org/) Topics. The Spring Boot application uses
to [Reactor Kafka Client](https://projectreactor.io/docs/kafka/1.3.14/reference/index.html) to subscribe to the events and "forwards" them to
GraphQL subscription clients using [Spring for GraphQL](https://spring.io/projects/spring-graphql).

## Running the example

1. *Start docker-compose* 

This will start Zookeeper, Kafka, Kafka Connect and Postgres.

* Please make sure following ports are locally available:
  * Zookeeper: 30218, 30288, 30388
  * Kafka: 9092, 29092
  * Postgres: 30432
  * Kafka Connect: 30083
  
* Note: the content of the database is stored in `docker/db-data`. If you re-start the database and you want to have a clean database, simply remove that directory.

```bash
docker-compose up -d
```

2. **Register Debezium Postgres Connectors**

There are two configured connectors: one that publishes  Debezium "full" Change Events including
schema information and one that only publishes the payload. The payload is the actual data that
has been changed.
- The former is used for demo purposes only
- The latter is used in the application

While you can register both connectors, please make sure that you at least register the second connector when you want to run the spring boot application.

```bash
# For demo only:
./register-postgres-connector.sh

# For usage in Spring Boot application:
./register-postgres-connector-smt.sh
```

3. **Verify connectors have been registered**

This should list all connectors you have registered in previous step:

```bash
./list-connectors.sh
```

4. **Watch Kafka Topic on command line**

To make sure, everything works and changes in the DB are published to Kafka,
you can use the `watch-XXX` scripts.

For example, to watch the "full" change events that are published for the `customers` table,
you can run:

```bash
./watch-topic-customer-smt.sh
```

5. **Make changes to the database**

For testing purposes, you can insert new customers manually into the database. Therefore,
run `psql` and execute some INSERT statement:

```bash
./run-psql.sh
```

...then in psql:
```sql
INSERT INTO customers (name, address) VALUES('Heinz', 'Somewhere 123') RETURNING id;
```

When you're watching the topics (as described in the previous step), you should see change events.

6. **Start Spring Boot application**

Use maven to start the Spring Boot application.

* Please make sure, that port 30080 is locally available.
* Activate the `kafka-reactive` profile to use the Reactor Kafka API. Otherwise the "classic" Spring Kafka API is used.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=kafka-reactive
```

7. **Open GraphiQL**

When the spring boot application runs, you can open GraphiQL at http://localhost:30080

Using GraphiQL you can add new Customers and new Orders, query for Customers and Orders and starts subscriptions for new Customers.
Please see example `.graphql`-files in the `queries` folder.

You could for example run a subscription for the new customers, as seen below. Whenever you insert a new customer (using a GraphQL mutation or directly with an INSERT using psql), a new event is  published through the GraphQL subscription. (DB -> Kafka Connect/Debezium -> Kafka Topic -> Spring Boot App/Reactive Kafka Client -> GraphQL Subscription)

```graphql
subscription {
  onNewCustomer {
    customer {
      id
      name
      address
    }
  }
}
```

# Contributing

If you like to help and contribute you're more than welcome! Please open or a pull request here in this repo.

For questions and comments, do not hesitate to contact me
  * [Nils Hartmann](https://nilshartmann.net)
  * [Twitter](https://twitter.com/nilshartmann)
  * [Mastodon](https://norden.social/@nilshartmann)
