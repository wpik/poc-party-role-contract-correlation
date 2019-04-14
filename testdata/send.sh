#!/bin/bash

ENTITY=$1
FILE=$2

curl -v -H "Content-Type: application/json" -d@$FILE localhost:8080/$ENTITY
