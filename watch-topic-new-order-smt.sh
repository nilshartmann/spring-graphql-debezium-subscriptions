#! /bin/bash

TOPICNAME="new_order"

docker-compose exec -it kafka /bin/sh \
  /kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server kafka:29092 \
    --property print.key=true \
    --topic "$TOPICNAME"
