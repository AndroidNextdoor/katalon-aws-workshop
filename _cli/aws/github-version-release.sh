#!/bin/bash

## Branch protection limits this file from being ran unless designated as an authorized user

## Running locally without any arguments will create a patch level increment 1.0.0 -> 1.0.1
## Patch Usage: scripts/aws/github-merge-script.sh
## Running in CI/CD codepipeline creates a minor level increment 1.0.1 -> 1.1.0
## Running locally with the `major` argument will create a major release level increment 1.1.1 -> 2.0.0
## Major Usage: scripts/aws/github-merge-script.sh major

if [ -z "$PROJECT" ];then
  PROJECT=katalon-aws-workshop
fi

if [ "$CODEBUILD_INITIATOR" != "codepipeline" ];
then
  if [ -z "$1" ]; then
    VERSION="patch"
  else
    VERSION="$1"
  fi
  SHA=$(git rev-parse HEAD)
  USERNAME=$(git config user.name)
  EMAIL=$(git config user.email)
  CODEBUILD_RESOLVED_SOURCE_VERSION=$SHA
  CODEBUILD_PUBLIC_BUILD_URL="https://localhost.patch"
  CONTEXT=$USERNAME/local/patch
else
  echo "BaseRef: $CODEBUILD_WEBHOOK_BASE_REF"
  echo "HeadRef: $CODEBUILD_WEBHOOK_HEAD_REF"
  echo "Version: $CODEBUILD_SOURCE_VERSION"
  echo "Commit: $CODEBUILD_WEBHOOK_MERGE_COMMIT"
  echo "CodeBuild ARN: $CODEBUILD_BUILD_ARN"
  echo "Log Path: $CODEBUILD_LOG_PATH"
  echo "SHA: $CODEBUILD_RESOLVED_SOURCE_VERSION"
  echo "Versioning triggered by $CODEBUILD_INITIATOR"
  CONTEXT=$CODEBUILD_INITIATOR
fi

REPO_URL="https://$GITHUB_TOKEN@github.com/$GITHUB_REPO.git"
REPO_API_URL="https://$GITHUB_TOKEN@api.github.com/repos/$GITHUB_REPO"
TEMP_FOLDER="$(mktemp -d)"

git clone --quiet "$REPO_URL" "$TEMP_FOLDER"
cd "$TEMP_FOLDER" || exit 1

if [ -z "$CODEBUILD_INITIATOR" ]; then
  CODEBUILD_INITIATOR=local
else
  git config --add remote.origin.fetch +refs/heads/*:refs/remotes/origin/* || exit 1
  git config --global user.email "$SERVICE_ACCT_EMAIL" || exit 1
  git config --global user.name "$SERVICE_ACCT" || exit 1
fi

git fetch --all || exit 1
git checkout main || exit 1

if [ -z "$VERSION" ];
then
  VERSION="minor"
elif [ "$1" -ne 0 ];
then
  VERSION="$1"
fi

#get highest tag number, and add 1.0.0 if doesn't exist
CURRENT_VERSION=$(git describe --abbrev=0 --tags 2>/dev/null)

if [[ $CURRENT_VERSION == '' ]];
then
  CURRENT_VERSION='1.0.0'
fi
echo "Current Version: $CURRENT_VERSION"

#replace . with space so can split into an array
CURRENT_VERSION_PARTS=(${CURRENT_VERSION//./ })

#get number parts
VNUM1=${CURRENT_VERSION_PARTS[0]}
VNUM2=${CURRENT_VERSION_PARTS[1]}
VNUM3=${CURRENT_VERSION_PARTS[2]}

if [[ "$VERSION" == "major" ]];
then
  VNUM1=$((VNUM1+1))
  VNUM2=0
  VNUM3=0
elif [[ "$VERSION" == "minor" ]];
then
  VNUM2=$((VNUM2+1))
  VNUM3=0
elif [[ "$VERSION" == "patch" ]];
then
  VNUM3=$((VNUM3+1))
else
  echo "Incorrect version type specified, try: [major, minor, patch]"
  exit 1
fi

#create new tag
NEW_TAG="$VNUM1.$VNUM2.$VNUM3"

#get current hash and see if it already has a tag
GIT_COMMIT=$(git rev-parse HEAD)
NEEDS_TAG=$(git describe --contains $GIT_COMMIT 2>/dev/null)

#only tag if no tag already
#to publish, need to be logged in to npm, and with clean working directory: `npm login; git stash`
if [ -z "$NEEDS_TAG" ]; then

  echo "($VERSION) updating $CURRENT_VERSION to $NEW_TAG"
  echo "CodeBuild Initiator: $CODEBUILD_INITIATOR"
  git tag -a $NEW_TAG -m "Release version $NEW_TAG"
  echo "Setting Release version $NEW_TAG to SUCCESS"
  # https://docs.github.com/en/rest/commits/statuses
  curl \
     -X POST \
     -H "Accept: application/vnd.github.v3+json" \
     "$REPO_API_URL/statuses/$CODEBUILD_RESOLVED_SOURCE_VERSION" \
     -d "{\"state\":\"success\",\"target_url\":\"$CODEBUILD_PUBLIC_BUILD_URL\",\"description\":\"$PROJECT Release $NEW_TAG\",\"context\":\"$CONTEXT\"}"

  git push "$REPO_URL" --tags || exit 1

else
  echo "Already a tag on this commit"
fi

cd ..
rm -rf "$TEMP_FOLDER"
