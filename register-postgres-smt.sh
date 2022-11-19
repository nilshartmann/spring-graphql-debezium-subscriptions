#! /bin/bash

curl -i -X POST -H "Accept:application/json" \
  -H  "Content-Type:application/json" http://localhost:30083/connectors/ \
  -d @postgres-connector-smt.json