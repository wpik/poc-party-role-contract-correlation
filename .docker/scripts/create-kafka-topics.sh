#!/bin/bash

KAFKA_CREATE_TOPICS=$(echo $KAFKA_CREATE_TOPICS | sed 's/,/ /g')

for topic in $KAFKA_CREATE_TOPICS; do
  echo "Creating topic $topic"
  TOPIC_NAME=$(echo $topic | cut -d : -f1)
  TOPIC_PARTITION=$(echo $topic | cut -d : -f2)
  TOPIC_REPL_FACTOR=$(echo $topic | cut -d : -f3)
  kafka-topics --delete --if-exists --topic $TOPIC_NAME --zookeeper $KAFKA_ZOOKEEPER_CONNECT
  kafka-topics --create --topic $TOPIC_NAME --partitions $TOPIC_PARTITION --replication-factor $TOPIC_REPL_FACTOR --if-not-exists --zookeeper $KAFKA_ZOOKEEPER_CONNECT
done

echo "Topics created"
