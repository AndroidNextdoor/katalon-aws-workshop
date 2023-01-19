#!/bin/bash
# Pre-requisite: Use scripts/docker/init.sh to start docker containers.
# When done testing, use scripts/docker/down.sh to stop the containers.
# This creates a mysql dump file of the test results in mysql/dump.

# `Profile` and `Browser` are set in the collection.

# USAGE: ./KatalonCollection $PROJECT $TEST_PATH $URL $RETRY_COUNT $SOURCE

CONTAINER=<YOUR KATALON DOCKER CONTAINER NAME>
TARGET_KATALON_DIR=<YOUR DOCKER WORK DIR>
ORG_ID=<YOUR_ORG_ID>
KATALON_API_KEY=<YOUR_KATALON_API_KEY>
TESTOPS_ID=<YOUR_TESTOPS_ID>

if [ -z "$1" ]; then
  PROJECT=katalon-aws-workshop
else
  PROJECT=$1
fi

if [ -z "$2" ]; then
  TEST_PATH="_Collections/_DEV"
else
  TEST_PATH=$2
fi

if [ -z "$3" ]; then
  URL="https://reddit.com"
else
  URL=$3
fi

if [ -z "$4" ]; then
  RETRY_COUNT=1
else
  RETRY_COUNT=$4
fi

if [ -z "$5" ]; then
  SOURCE="Local Testing"
else
  SOURCE=$5
fi

if [ -z "$6" ]; then
  MAX_FAILED=5
else
  MAX_FAILED=$6
fi

COMMAND="unset DISPLAY && katalonc -g_Source='${SOURCE}' -g_Url='${URL}' -statusDelay=15 --licenseRelease=true -testOpsProjectId=${TESTOPS_ID} -retry=${RETRY_COUNT} -maxFailedTests=${MAX_FAILED} -testSuiteCollectionPath='Test Suites/$TEST_PATH' -projectPath='$TARGET_KATALON_DIR$PROJECT/$PROJECT.prj' -apiKey=${KATALON_API_KEY} -orgID=${ORG_ID} --config -webui.autoUpdateDrivers=true --info -buildLabel='${PROJECT} - ${TEST_PATH}'"

echo "${COMMAND}"

docker exec "${CONTAINER}" sh -c "${COMMAND}"