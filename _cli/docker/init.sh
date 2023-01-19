#!/bin/bash
# Used to start Katalon and MySQL Docker containers.
# Usage init.sh $DOCKER_TAG

if [ -z "$1" ];
then
  export DOCKER_TAG=stable
else
  export DOCKER_TAG=$1
fi

export TARGET_KATALON_DIR=/katalon/katalon

docker -v
docker-compose -v

aws ecr get-login-password --region "$REGION" --profile $PROFILE | docker login --username AWS --password-stdin "$AWS_ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com" || exit 1

docker-compose up -d || exit 1

## Copy AWS Credentials over to Docker. I've ended up doing this instead of mounting the .aws volume for AWS CodeBuild compatibility. $AWS_CONFIG_FILE is set in the BuildSpecs. -AN
if [ "$(docker ps -q -f name=katalon)" ] && [ -z "$AWS_CONFIG_FILE" ];
then
  ## This is for local dev only.
  docker cp ~/.aws/config katalon:/root/.aws
  docker cp ~/.aws/credentials katalon:/root/.aws
else
  docker cp "$AWS_CONFIG_FILE" katalon:/root/.aws/
  docker cp "$AWS_CRED_FILE" katalon:/root/.aws/
fi

frames="/ | \ -"
count=1
threshold=200

while [ "$(docker inspect -f '{{.State.Health.Status}}' mysql_db )" != "healthy" ] && [ $count -le $threshold ];
do
  for frame in $frames;
  do
      printf "\r %s Starting MySQL..." "$frame"
      sleep 0.25
      count=$(( $count+1 ))
  done
done

if [ $count -ge $threshold ]; then
  printf "\n\r ERROR Starting MySQL... \n"
else
  printf "\n\r SUCCESS MySQL Started! \n"
  threshold=200
fi

docker exec mysql_db mysql insights --user=$MYSQL_DB_U --password=$MYSQL_DB_P -e 'SELECT COUNT(id) FROM vehicle;'
