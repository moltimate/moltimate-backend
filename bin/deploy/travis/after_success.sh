#!/usr/bin/env bash

if [ "${TRAVIS_REPO_SLUG}" == "${GIT_ORGANIZATION_NAME}/${GIT_REPO_NAME}" ] # \
#    && [ "push" == "$TRAVIS_EVENT_TYPE" ] \
#    && [ "master" == "$TRAVIS_BRANCH" ]
then
    gcloud beta app deploy src/main/webapp/WEB-INF/appengine-web.xml
#    mvn appengine:deploy -Dmaven.test.skip=true
fi
