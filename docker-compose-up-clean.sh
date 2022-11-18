#! /bin/bash

rm -rf docker/db-data

docker-compose up -d
docker-compose logs -f
