#! /bin/bash

curl -i -X DELETE -H "Accept:application/json" \
  http://localhost:30083/connectors/$1
