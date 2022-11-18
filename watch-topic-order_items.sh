#! /bin/bash

TOPICNAME="order_app.public.order_items"

docker-compose exec -it kafka /bin/sh \
  /kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server kafka:29092 \
    --property print.key=false \
    --topic "$TOPICNAME"
    # --from-beginning

 # ./watch-topic.sh|jq
#         exec $KAFKA_HOME/bin/kafka-console-consumer.sh
#         --bootstrap-server $KAFKA_BROKER --property print.key=$PRINT_KEY
#         --property fetch.min.bytes=$FETCH_MIN_BYTES --topic "$TOPICNAME" $FROM_BEGINNING $@