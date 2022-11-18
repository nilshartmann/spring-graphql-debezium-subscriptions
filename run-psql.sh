#! /bin/bash

docker-compose exec -it order-service-db \
  psql -U klaus order_service_db


# INSERT INTO customers (name, address) VALUES('Heinz', 'Somewhere 123');