#! /bin/bash

export DEBEZIUM_VERSION=1.9

DOCKER_KAFKA_HOST=""

for i in $(ifconfig -l); do
  ADDR=$(ipconfig getifaddr $i)
  if [ ! -z "$ADDR" ]; then
    DOCKER_KAFKA_HOST=$ADDR;
    break
  fi;
done;

#export DOCKER_KAFKA_HOST=$(ifconfig -l | xargs -n1 ipconfig getifaddr)

echo DOCKER_KAFKA_HOST: $DOCKER_KAFKA_HOST
export DOCKER_KAFKA_HOST=$DOCKER_KAFKA_HOST

docker-compose -f docker-compose.yml $1 $2 $3 $4

if [ "$1" == "up" ]; then
  docker-compose -f docker-compose.yml logs -f
fi;