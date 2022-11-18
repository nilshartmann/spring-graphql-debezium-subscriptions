#! /bin/bash

docker-compose exec -it kafka /bin/sh \
  /kafka/bin/kafka-topics.sh --list \
    --bootstrap-server kafka:29092
