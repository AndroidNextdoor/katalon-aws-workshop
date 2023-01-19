#!/bin/bash

if [ "$(docker ps -q -f name=mysql_db)" ]; then
  export TARGET_KATALON_DIR=/katalon/katalon

  TIMESTAMP=$(date +%m-%d-%Y-%H-%M-%S)

  cd ../../

  docker exec mysql_db mysqldump --user=$MYSQL_DB_U --password=$MYSQL_DB_P insights > "mysql/dump/insights_$TIMESTAMP.sql" --no-tablespaces

  rm -rf mysql/data

  if [ -z "$1" ];
  then
    export DOCKER_TAG=latest
  else
    export DOCKER_TAG=$1
  fi

  docker-compose down -v --rmi all --remove-orphans
fi