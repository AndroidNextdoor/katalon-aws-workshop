#!/bin/bash

REPO_URL="https://$GITHUB_TOKEN@github.com/$GITHUB_REPO.git"

if [ -z "$IMAGE_TAG" ]
then
  IMAGE_TAG=$(date +%m-%d-%Y-%H-%M-%S)
fi

if [ -z "$DOCKER_TAG" ]
then
  DOCKER_TAG=latest
fi

cd ../../

echo "Image Tag: $IMAGE_TAG"

git clone --quiet "$REPO_URL"

cd $REPO_NAME || exit 1

git config --add remote.origin.fetch +refs/heads/*:refs/remotes/origin/* || exit 1
git fetch --all || exit 1
git checkout main || exit
git pull

cd .. || exit 1

aws ecr --profile sandbox get-login-password --region "$REGION" | docker login --username AWS --password-stdin "$AWS_ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com" || exit 1

docker build -t "$AWS_ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/$ORG/$ECR:$DOCKER_TAG" . || exit 1
docker tag "$AWS_ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/$ORG/$ECR:$DOCKER_TAG" "$AWS_ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/$ORG/$ECR:$IMAGE_TAG"

docker push "$AWS_ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/$ORG/$ECR:$DOCKER_TAG"
docker push "$AWS_ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/$ORG/$ECR:$IMAGE_TAG"

rm -rf $REPO_NAME
